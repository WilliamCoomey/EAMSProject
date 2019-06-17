import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class is for general file operations.
 */
public class FileManager
{
	/*
	 * The is a bug with JFileChooser where it opens behind all windows due to having a null parent
	 * The JFrame below makes itself appear on top of all windows and that in turn makes
	 * The JFileChooser appear on top of the windows
	 */
	private JFrame frame;
	private boolean isSerializedFile = true;
	
	/**
	 * The constructor for the class. Only used when a FileChooser is needed.
	 */
	public FileManager()
	{
		try
		{	//Setting the UI to look like Windows
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e)
		{
			System.err.println("ERROR: Error setting UI to windows. Reverting back.");//If it can't set it to Windows, it just uses default
		}
		
		frame = new JFrame();
		frame.setVisible(true);
		BringToFront();
	}
	
	/**
	 * Method for setting the isSerializedFile boolean. Used by the PropertyChangeListener
	 * @param isSerialized A boolean value to change isSerializedFile to
	 */
	public void isSerializedFile(boolean isSerialized)
	{
		this.isSerializedFile = isSerialized;
	}
	
	/**
	 * Method for returning the isSerializedFile boolean. This is used to know
	 * if the user wants to save the array as a serialized file or an Excel spread sheet
	 * @return Returns the isSerializedFile variable
	 */
	public boolean getIsSerialized()
	{
		return this.isSerializedFile;
	}
	
	/**
	 * This listener class used to check what save format the user has selected
	 * by changing the isSerializedFile boolean
	 */
	private class Listener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			Object obj = event.getSource();
			JFileChooser fileChooser;
			
			if(!(obj instanceof JFileChooser))
			{
				System.out.println("not an instance");
				return;
			}
			
			fileChooser = (JFileChooser) obj;

			if(fileChooser.getFileFilter().getDescription().contains(".xlsx"))
			{
				fileChooser.setSelectedFile(new File("output.xlsx"));
				isSerializedFile(false);
			}
			else if(fileChooser.getFileFilter().getDescription().contains(".ser"))
			{
				fileChooser.setSelectedFile(new File("output.ser"));
				isSerializedFile(true);
			}
		}
		
	}
	
	/**
	 * Method used to get the user to select a file.
	 * <br>
	 * A file manager object must be instantiated before calling this method
	 * @param isSave Boolean for changing the FileChooser to either save or choose a file
	 * @return The File that the user has selected
	 */
	public File getFile(boolean isSave)
	{
		int result;
		File returnFile;
		JFileChooser fileChooser = new JFileChooser();
		FileNameExtensionFilter filter;
		
		fileChooser.setCurrentDirectory(new File("./"));//Setting the directory that will be opened to the one in which the server is running
		fileChooser.addPropertyChangeListener(JFileChooser.FILE_FILTER_CHANGED_PROPERTY, new Listener());
		
		
		//Using a boolean to choose if an open or save dialog is opened
		if(isSave)
		{
			FileNameExtensionFilter excelFilter = new FileNameExtensionFilter("Save as Excel File (.xlsx)", "xlsx");
			FileNameExtensionFilter serFilter = new FileNameExtensionFilter("Save as a Serialized File (.ser)", "ser");
			fileChooser.addChoosableFileFilter(serFilter);
			fileChooser.addChoosableFileFilter(excelFilter);
			fileChooser.setAcceptAllFileFilterUsed(false);
			fileChooser.setSelectedFile(new File("output.ser"));
			result = fileChooser.showSaveDialog(null);//Will have to check if file exists before deleting
		}
		else
		{
			filter = new FileNameExtensionFilter("EAMs Excel or Serialized Files", "ser", "xlsx");//Making the extension filter for only excel and serialized files
			fileChooser.setFileFilter(filter);
			fileChooser.setAcceptAllFileFilterUsed(false);//Forcing the user to select a file from the filter
			result = fileChooser.showOpenDialog(null);
		}
		
		returnFile = fileChooser.getSelectedFile();//Getting the file from the file chooser
		
		//Disposing of the frame -> If frame isn't disposed it will keep the server running even after termination
		frame.setVisible(false);
		frame.dispose();
		
		
		if(result == JFileChooser.APPROVE_OPTION)//Checking if the user actually chose a file
			return returnFile;
		else
			System.out.println("No file selected");
		
		return null;//If no file is chosen, return null
	}
	
	/**
	 * Method to bring the FileChooser window to the front of the other
	 * opened windows. Required due to a bug in the swing framework.
	 */
	private void BringToFront()
	{
		frame.setExtendedState(JFrame.ICONIFIED);
		frame.setExtendedState(JFrame.NORMAL);
	}
	
	
	/**
	 * Method for outputting a byte array to a file
	 * @param filename The name of the file to output to
	 * @param fileContents The byte array to be saved to the file
	 */
	public static void outputFile(String filename, byte[] fileContents)
	{
		FileOutputStream output = null;
		try
		{
			output = new FileOutputStream(new File(filename));
			output.write(fileContents);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("\nERROR: Couldn't find file: "+filename);
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: There was an error writing the file");
		}
	}
	
	/**
	 * Method for outputting a string to a file
	 * @param filename The name of the file to save the string to
	 * @param fileContents The string that's to be save to the file
	 */
	public static void outputFile(String filename, String fileContents)
	{
		String[] lines = fileContents.split("\\r?\\n");//Splits the contents with the regular expression \r?\n
		PrintWriter output = null;
		try
		{
			output = new PrintWriter(filename);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("\nERROR: Could not find the file: "+filename);
		}

		for(String string : lines)
			output.println(string);
		
		output.close();
	}
	
	/**
	 * Method for reading in a file as a string
	 * @param file The file to read in
	 * @return The file as a string
	 */
	public static String readFile(File file)
	{
		String fileContents;
		FileInputStream input;
		byte[] bytes = {};
		
		try
		{
			input = new FileInputStream(file);
			bytes = input.readAllBytes();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("\nERROR: File could not be found");
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: There was an error reading the file");
		}
		
		fileContents = new String(bytes);
		return fileContents;
	}
	
	
	/**
	 * Method for reading in a file as a string
	 * @param filename The file to read
	 * @return The file as a string
	 */
	public static String readFile(String filename)
	{
		String fileContents;
		File file = new File(filename);
		FileInputStream input;
		byte[] bytes = {};
		
		try
		{
			input = new FileInputStream(file);
			bytes = input.readAllBytes();
		}
		catch(FileNotFoundException e)
		{
			System.err.println("\nERROR: File could not be found");
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: There was an error reading the file");
		}
		
		fileContents = new String(bytes);
		return fileContents;
	}
	
	/**
	 * Method for reading in a file as a byte array
	 * @param filename The file to read
	 * @return A byte array of the file
	 */
	public static byte[] readFileBytes(String filename)
	{
		byte[] bytes;
		File file = new File(filename);
		FileInputStream input = null;
		
		bytes = new byte[(int) file.length()];
		
		try
		{
			input = new FileInputStream(filename);
			bytes = input.readAllBytes();
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: Error reading file: "+filename);
		}
		
		return bytes;
	}
	
	/**
	 * Method for serializing a given object
	 * @param file The file to save the serialized object to
	 * @param object The object to serialize
	 */
	public static void SerializeObject(File file, Object object)
	{
		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;
		
		try
		{
			fileOutputStream = new FileOutputStream(file.getAbsolutePath());//File extension not added in, must be passed into method in filename
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			fileOutputStream.close();
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: There was an error serializing the file");
			return;
		}
		
		System.out.println("\nSerialized file saved in: "+file.getAbsolutePath());
	}

	/**
	 * Method for serializing a given object
	 * @param filename File to save the serialized object to
	 * @param object The object to serialize
	 */
	public static void SerializeObject(String filename, Object object)
	{
		FileOutputStream fileOutputStream;
		ObjectOutputStream objectOutputStream;
		
		try
		{
			fileOutputStream = new FileOutputStream(filename);//File extension not added in, must be passed into method in filename
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(object);
			objectOutputStream.close();
			fileOutputStream.close();
		}
		catch(IOException e)
		{
			System.err.println("\nERROR: There was an error serializing the file");
			return;
		}
		
		System.out.println("\nSerialized file saved in: "+filename);
	}
	
	/**
	 * Method for deserializing a saved serialized file
	 * @param filename The name of the file to deserialize
	 * @return The deserialized object
	 * @throws IOException An IOException will be thrown if there's an error reading the file
	 */
	public static Object DeserializeObject(String filename) throws IOException
	{
		//object is an arrayList that stores the deserialized EAMS objects
		Object object = null;
		FileInputStream fileIn;
		ObjectInputStream objectIn;
		
		try 
		{
			fileIn = new FileInputStream(filename);
			objectIn = new ObjectInputStream(fileIn);
			object = objectIn.readObject();//File contains only on serialized arrayList containing the EAMS objects
			objectIn.close();
			fileIn.close();
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("\nERROR: Class not found. Mismatch between the file and the class");
		}
		//Returns the ArrayList of EAMS objects
		return object;
	}

}








