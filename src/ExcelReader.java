import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

/**
 * This class is responsible for reading an Excel file of the exported
 * EAMS asset database and creating an ArrayList of EAMSObjects to be used by the server
 */
public class ExcelReader
{
	/**
	 * Reads an Excel file of EAMSObjects and removes the header, if there is one.
	 * @param filename The name of the Excel file to read
	 * @return An ArrayList of EAMSObjects with the header removed, if it has one
	 */
	public static ArrayList<EAMSObject> getStrippedArray(String filename)
	{
		ArrayList<EAMSObject> array = getArray(filename);
		array = stripHeader(array);
		
		return array;
	}
	
	/**
	 * Method used to remove the header of the Excel sheet so it is only EAMS assets that
	 * are in the ArrayList
	 * @param array An ArrayList of EAMSObjects
	 * @return An ArrayList of EAMSObjects with the header removed, if it has one
	 */
	public static ArrayList<EAMSObject> stripHeader(ArrayList<EAMSObject> array)
	{
		if(array == null)
			return new ArrayList<EAMSObject>();
		
		boolean isHeader = true;
		String[] valuesToCheck;
		String[] values = {"Position", "Description", "Organization", "Department(*)", "Class", "Category", "Manufacturer", "Model", "Location(*)", "Variable 2", "Variable 4"};
		EAMSObject objToCheck = array.get(0);
		
		valuesToCheck = objToCheck.getObjectValues();
		
		//Looping through the values array to check if they are header values
		for(int i=0;i<values.length;i++)
		{
			if(values[i].equals(valuesToCheck[i]))//Restarts the loop if the value is a header value
				continue;
			
			isHeader = false;//Sets flag to false if the object doesn't have the header values
		}
		
		//Removing the header if it is a header
		if(isHeader)
			array.remove(objToCheck);
		
		
		return array;
	}
	
	/**
	 * Reads an Excel sheet and populates an ArrayList of EAMSObjects from it.
	 * Will only work if the sheet is in the same format as an asset export of the EAMS database
	 * @param filename The name of the Excel sheet to read in
	 * @return An ArrayList of EAMSObjects read in from the Excel sheet
	 */
	public static ArrayList<EAMSObject> getArray(String filename)
	{
		ArrayList<EAMSObject> arrayList = new ArrayList<EAMSObject>();//ArrayList to store the EAMS objects
		Workbook workbook = null;
		try
		{	//Opens the excel workbook at filename
			workbook = WorkbookFactory.create(new File(filename));
		}
		catch(EncryptedDocumentException e)
		{
			System.out.println("\nERROR: EncryptedDocumentException thrown\n");
			return null;
		}
		catch(InvalidFormatException e)
		{
			System.out.println("\nERROR: Invalid format\n");
			return null;
		}
		catch(IOException e)
		{
			System.out.println("\nERROR: There was an error reading the file");
			System.out.println("Ensure the document isn't open in another program and try again\n");
			return null;
		}
		Sheet sheet = workbook.getSheetAt(0);//EAMS only outputs one sheet per workbook, no need to look for more
		
		//Populating the ArrayList with EAMS Objects
		for(Row row : sheet)
		{
			//Making a String array the length of the row
			String[] values = new String[row.getLastCellNum()];
			//Looping through the Cells in the row and adding the String value to the String array
			for(int i=0;i<row.getLastCellNum();i++)
			{
				try
				{
					if(row.getCell(i) != null)
						values[i] = row.getCell(i).getStringCellValue();
					else
						values[i] = "";
				}
				catch(NullPointerException e)//A NPE will be thrown if the sheet has an empty space
				{
					values[i] = "";
					continue;
				}
			}
			//Creating the EAMSObjects by passing the array of string values and then adding object to arrayList
			arrayList.add(new EAMSObject(values));
		}
		try
		{
			workbook.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		//Returning the populated arrayList of EAMS objects
		return arrayList;
	}

}
