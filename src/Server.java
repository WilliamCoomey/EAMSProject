import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("unchecked")
/**
 * This is the main class where the program will start.
 * It deals with setting up the server and serving the web page
 * to the user 
 */
public class Server
{
	private static ArrayList<EAMSObject> array;
	private static final Scanner kb = new Scanner(System.in);
	private static boolean running = true;
	private static final int MAX_PORT_VALUE = 65535, MIN_PORT_VALUE = 1;
	
	public static void main(String[] args)
	{
		int portNumber = 80;
		boolean hasLoadedArray = false;//used to ensure an array is loaded before saving the full table file
		
		//Checking if an argument is there before trying to use it
		if(args.length > 0)
		{
			try //In a try-catch because it's easier than having to check for everything that could be entered
			{
				portNumber = Integer.parseInt(args[0]);
				if(portNumber <= MIN_PORT_VALUE || portNumber >= MAX_PORT_VALUE)
				{
					System.err.println("ERROR: The port number must be greater than 1 and below 65535");
					portNumber = 8080;
				}
				
			}
			catch(NumberFormatException e)
			{
				System.err.println("ERROR: Port number must be a number");
			}
		}
		
		//Starting the server and creating the contexts
		HttpServer server = startServer(portNumber);
		server.createContext("/", new RootHandler());
		server.createContext("/bootstrap.css", new BootstrapHandler());
		server.createContext("/bootstrap.js", new JavascriptHandler());
		server.createContext("/jQuery.js", new JQueryHandler());
		server.createContext("/popper.js", new PopperHandler());
		server.createContext("/filter", new FilterHandler());
		server.createContext("/table", new TableHandler());
		server.createContext("/plants", new PlantsHandler());
		server.createContext("/search", new SearchHandler());
		server.createContext("/cab", new CabHandler());
		server.start();
		
		try
		{
			//Populating the EAMSObject array by deserializing the serialized file
			//Deserializing is faster than using Apache poi library, so checking that first
			System.out.println("Trying to load EAMS.ser");
			array = (ArrayList<EAMSObject>) FileManager.DeserializeObject("EAMS.ser");
			hasLoadedArray = true;
		}
		catch(Exception e1)
		{
			//Trying to read the excel file if there's no serialized file
			System.out.println("Unable to load serialized file, trying to load EAMS.xlsx");
			try
			{
				array = (ArrayList<EAMSObject>) ExcelReader.getStrippedArray("EAMS.xlsx");
				hasLoadedArray = true;
			}
			catch(Exception e2)
			{
				System.out.println("Unable to load any EAMS file");
			}
		}
		
		//Saving the full table so it doesn't have to be generated each time
		if(hasLoadedArray)
			if(!(array.size() < 1))
				FileManager.outputFile("html/strippedFile2.html", HTMLBuilder.getHTML(array));
		
		
		//Main loop
		while(running)
			runCommand();
		
		server.stop(0);
	}
	
	/**
	 * This method spawns a GUI that will let the user save
	 * the currently-loaded array of EAMS objects to either
	 * an excel or serialized file.
	 * <br>
	 * If a serialized file of the current array of EAMSObjects 
	 * is saved as EAMS.ser in the directory of the program,
	 * it will dramatically increase the start time of the server.
	 * @param line Line is the command the user entered to start
	 * the saveCommand() method. Was used before the switch to a GUI
	 * saving interface and is still there for fear that something will break
	 */
	private static void saveCommand(String line)
	{
		FileManager fileSaver = new FileManager();
		File fileToSave = fileSaver.getFile(true); //This spawns the FileManager GUI, argument is to make it save a file, not load
		String fileName;
		boolean overrideFile = false;
		
		if(fileToSave == null)
			return;
		
		//Changing file path to string so it can be to appended if needs be
		fileName = fileToSave.getAbsolutePath();
		
		//Ensuring the user wants to override the file if it exists
		if(fileToSave.exists())
		{
			String input;
			
			System.out.print("Are you sure you want to override the file \""+fileToSave.getName()+"\"?(Y/N): ");
			input = kb.nextLine();
			
			//Checking if the user has actually entered something
			if(input.length() < 1)
			{
				System.out.println("No answer selected, returning");
				return;
			}
			
			if(input.charAt(0) == 'n' || input.charAt(0) == 'N')
				return;
			else if(input.charAt(0) == 'y' || input.charAt(0) == 'Y')
				overrideFile = true;
			else
				System.out.println("No valid answer selected, returning");
			
			if(overrideFile != true)
				return;
		}
		
		//Checking if the user chose to save it as serialized
		if(fileSaver.getIsSerialized())
		{
			//Checking if the file already has the extension and if it doesn't, it's added
			if(!fileName.endsWith(".ser"))
				fileName += ".ser";
			
			FileManager.SerializeObject(fileName, array);
			
		}
		else if(!fileSaver.getIsSerialized())
		{
			if(!fileName.endsWith(".xlsx"))
				fileName += ".xlsx";
			//Creates an autosized excel sheet of the main array of EAMS objects
			//ExcelWeriter.writeArrayNoAutoSize is much faster, but not as useful
			ExcelWriter.createEAMSSheet(fileName, array);
		}
	}
	
	/**
	 * This method spawns a GUI that will let the user load a serialized EAMSObject
	 * array, or an Excel spreadsheet of EAMS assets.
	 * <br>
	 * They will only be able to load .ser and .xlsx files.
	 * @param line Line is the command the user entered to start
	 * the loadCommand() method. Was used before the switch to a GUI
	 * saving interface and is still there for fear that something will break
	 */
	private static void loadCommand(String line)
	{
		FileManager fileLoader = new FileManager();
		File fileToLoad = fileLoader.getFile(false);//Boolean parameter is for choosing open or save dialog
		String fileName;
		ArrayList<EAMSObject> tempArray = null;//Using a temp array in case the file is empty or returns null
		
		if(fileToLoad == null)
			return;
		
		fileName = fileToLoad.getName();
		
		
		if(fileName.toLowerCase().endsWith(".ser"))
		{
			try
			{
				//Using FileManager to deserialize the array
				//It's casted and not checked, hence the suppressed warning at the top
				tempArray = (ArrayList<EAMSObject>) FileManager.DeserializeObject(fileToLoad.getAbsolutePath());
			}
			catch(IOException e)
			{
				System.err.println("ERROR: There was an error loading the file");
			}
		}
		else if(fileName.endsWith(".xlsx"))
		{
			tempArray = ExcelReader.getStrippedArray(fileToLoad.getAbsolutePath());//Stripped array gets rid of the header of the file if it's there
		}
		else
			System.err.println("\nERROR: Invalid file type selected\nLoad a .ser or .xlsx file");
		
		if(tempArray == null || tempArray.size() < 1)
			return;
		
		array = tempArray;
		FileManager.outputFile("html/strippedFile2.html", HTMLBuilder.getHTML(array));
	}

	/**
	 * This method is called when the user enters the "quit" command.
	 * It asks them if they are sure they want to stop the server,
	 * which should reduce accidental termination of the server
	 */
	private static void quitCommand()
	{
		String line;
		
		System.out.print("Are you sure you want to terminate the server?(Y/N): ");
		line = kb.nextLine();
		
		if(line.length() < 1)
		{
			quitCommand();//Recursively calling quitCommand() until a valid answer is given
			return;
		}
		
		if(line.charAt(0) == 'n' || line.charAt(0) == 'N')
			return;
		else if(line.charAt(0) == 'y' || line.charAt(0) == 'Y')
			running = false;
		else
			quitCommand();//Making the user enter a valid value
	}
	
	/**
	 * This method asks the user for a command and executes it
	 */
	private static void runCommand()
	{
		String line;
		String[] words = null;
		
		//Getting input from the user
		System.out.print("\nEnter Command: ");
		line = kb.nextLine();

		//Checking the input is not empty
		if(line.length() < 1)
		{
			printHelp();
			return;
		}
		
		//Separating the words of the command by spaces using the regular expression [ ]+
		words = line.split("[ ]+");
		
		if(words[0].equalsIgnoreCase("help") || words[0].equalsIgnoreCase("?") || words[0].equalsIgnoreCase("h"))
		{
			printHelp();
		}
		else if(words[0].equalsIgnoreCase("quit") || words[0].equalsIgnoreCase("q") || words[0].equalsIgnoreCase("exit") || words[0].equalsIgnoreCase("stop"))
		{
			quitCommand();
			return;
		}
		else if(words[0].equalsIgnoreCase("load"))
		{
			loadCommand(line);
		}
		else if(words[0].equalsIgnoreCase("save"))
		{
			saveCommand(line);
		}
		else
			System.out.println("Invalid command. Type HELP for list of commands.");
	}

	
	/**
	 * Prints the available commands
	 */
	private static void printHelp()
	{
		System.out.println("\nAvaliable Commands:");
		System.out.println("===================");
		System.out.println("HELP \t Displays this help menu");
		System.out.println("QUIT \t Terminates the program");
		System.out.println("LOAD \t Loads a given EAMS file");
		System.out.println("SAVE \t Saves the loaded EAMS array to a file");
	}
	
	/**
	 * This is the method that filters the currently loaded array of 
	 * EAMS objects with the parameters that the user entered
	 * @param parameters The parameters that the user POSTed to the server
	 * @return The string of the HTML file that will be served to the user
	 */
	private static String filterArray(Map<String, Object> parameters)
	{
		ArrayList<EAMSObject> tempArray;
		String loadedTable = (String) parameters.remove("loadedTable");//Getting the table that the user has loaded so it can be filtered
		Map<String, Object> previousParameters = new HashMap<String, Object>();
		
		//Checking if the user actually has a table loaded
		if(loadedTable == null)
			return HTMLBuilder.getHTML(filterTable(array, parameters));
		
		try
		{
			//Parsing the query that the user previously made to have the table they currently have
			//The second argument will hold the parameters when it's done
			parseQuery(loadedTable, previousParameters);
		}
		catch(UnsupportedEncodingException e)
		{
			System.err.println("ERROR: The encoding of the request couldn't be parsed");
		}

		tempArray = searchTable(previousParameters);//Getting their current table again
		tempArray = filterTable(tempArray, parameters);//Filtering the table with the users actual request
		
		return HTMLBuilder.getHTML(tempArray);//Using HTMLBuilder to return a string of the filtered table
	}
	
	/**
	 * Method used by filterArray to filter a table based on the parameters of a request
	 * @param otherArray is the array to filter. Only called "otherArray" because "array" is static and would cause confusion
	 * @param parameters is the parameters to filter the table based on
	 * @return Returns an array of EAMS objects
	 */
	private static ArrayList<EAMSObject> filterTable(ArrayList<EAMSObject> otherArray, Map<String, Object> parameters)
	{
		Map<String, Integer> mapValues = EAMSObject.getMapValues();//Getting the map values statically from EAMSObject because it's easier and less messy
		String[] values = new String[mapValues.size()];//Creating the string array that will hold the parameters
		
		//Getting the values from the parameters using a common key between mapValues and parameters
		//A bit odd because it was done a different way at first and I didn't want to break the old code
		for(String key : parameters.keySet())
			values[mapValues.get(key)] = (String) parameters.get(key);
		
		ArrayList<EAMSObject> returnArray = Search.filterArray(otherArray, values);
		
		return returnArray;
	}
	
	/**
	 * Method that calls the search functions on the array
	 * @param parameters The parameters that the user entered
	 * @return The tempArray after it has been searched through
	 */
	private static ArrayList<EAMSObject> searchTable(Map<String, Object> parameters)
	{
		//Making a clone of the main array to use for searching
		ArrayList<EAMSObject> tempArray = (ArrayList<EAMSObject>) array.clone();
		
		//Checking if the parameters have a value for each key and searching the array for it if it does
		if(parameters.get("position") != null)
			tempArray = Search.searchPosition(tempArray, (String) parameters.get("position"));
		if(parameters.get("description") != null)
			tempArray = Search.searchDescription(tempArray, (String) parameters.get("description"));
		if(parameters.get("department") != null)
			tempArray = Search.searchPlant(tempArray, (String) parameters.get("department"));
		if(parameters.get("category") != null)
			tempArray = Search.searchCategory(tempArray, (String) parameters.get("category"));
		if(parameters.get("manufacturer") != null)
			tempArray = Search.searchManufacturer(tempArray, (String) parameters.get("manufacturer"));
		if(parameters.get("model") != null)
			tempArray = Search.searchModel(tempArray, (String) parameters.get("model"));
		if(parameters.get("variable2") != null)
			tempArray = Search.searchTag(tempArray, (String) parameters.get("variable2"));
		if(parameters.get("variable4") != null)
			tempArray = Search.searchCab(tempArray, (String) parameters.get("variable4"));
		
		return tempArray;
	}

	/**
	 * Handler class for searching through the EAMS array by cab/panel
	 */
	private static class CabHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = t.getRequestURI();
			String query = requestedUri.getRawQuery();//The raw query is the GET request
			parseQuery(query, parameters);//Passing it off to be parsed
			
			//Checking if the user requested that a cab be searched for
			if(parameters.get("cab") != null)
			{
				String cabNumber = (String) parameters.get("cab");//Getting the cab number the user asked for
				String response = HTMLBuilder.getHTML(Search.searchCab(array, cabNumber));//Response is the string of the HTML page
				t.sendResponseHeaders(200, response.length());//Sending the response headers. 200 is for OK and also sending the response length
				OutputStream output = t.getResponseBody();//Getting the data stream to output to
				output.write(response.getBytes());//getting the bytes of the response and writing them to the output stream
				output.close();
			}
			else //Sending the user the cab search page if they haven't requested a cab
			{
				String response = FileManager.readFile("html/cabSearch.html");//Loading the HTML file since the user didn't request a cab
				t.sendResponseHeaders(200, response.length());
				OutputStream output = t.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		}
	}
	
	/**
	 * Handler class for searching
	 */
	private static class SearchHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			URI requestedUri = t.getRequestURI();
			String query = requestedUri.getRawQuery();
			parseQuery(query, parameters);
			
			if(parameters.keySet().size() > 0)//Checking if the user was after sending a search request
			{
				String response = HTMLBuilder.getHTML(searchTable(parameters));//Passing the parameters off to searchTable() and getting HTMLBuilder to make it
				t.sendResponseHeaders(200, response.length());
				OutputStream output = t.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
			else
			{
				String response = FileManager.readFile("html/search.html");
				t.sendResponseHeaders(200, response.length());
				OutputStream output = t.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		}
	}
	
	/**
	 * Handler for the plants context
	 * Doesn't work too well because the data that's in the EAMS database is inconsistent
	 */
	private static class PlantsHandler implements HttpHandler
	{

		public void handle(HttpExchange t) throws IOException
		{
			String response;
			URI uri = t.getRequestURI();
			String request = uri.toString();
			//Getting the plant they want instead of making a handler and context for each plant
			String plant = request.substring(request.lastIndexOf("/")); 
			
			//Checking which plant they requested and setting the response to that
			if(plant.equalsIgnoreCase("/api"))
				response = FileManager.readFile("html/API.html");
			else if(plant.equalsIgnoreCase("/osp1"))
				response = FileManager.readFile("html/OSP1.html");
			else if(plant.equalsIgnoreCase("/osp3"))
				response = FileManager.readFile("html/OSP3.html");
			else if(plant.equalsIgnoreCase("/osp4"))
				response = FileManager.readFile("html/OSP4.html");
			else if(plant.equalsIgnoreCase("/production"))
				response = FileManager.readFile("html/Production.html");
			else if(plant.equalsIgnoreCase("/nptl"))
				response = FileManager.readFile("html/NPTL.html");
			else if(plant.equalsIgnoreCase("/ktl"))
				response = FileManager.readFile("html/KTL.html");
			else
				response = FileManager.readFile("html/plants.html");
			
			
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
		
	}
	
	/**
	 * Handler class for filtering the array based on the user's parameters
	 */
	private static class FilterHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			Map<String, Object> parameters = new HashMap<String, Object>();
			InputStream in = t.getRequestBody();
			String input = new String(in.readAllBytes());//Getting a string form of the POSTed request
			parseQuery(input, parameters);//Parsing the request into a HashMap
			
			
			if(parameters.keySet().size() > 0 && input.length() > 0)
			{
				//Sending response
				String response = filterArray(parameters);
				t.sendResponseHeaders(200, response.length());//first argument is the HTTP Status code
				OutputStream output = t.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
			else
			{
				//The parameters should never be empty, but in case i
				String response = "<h1>ERROR: There was an error completing your request</h1>"
						+ "<p>Click <a href=\"/\">here</a> to return</p>";
				t.sendResponseHeaders(200, response.length());
				
				OutputStream output = t.getResponseBody();
				output.write(response.getBytes());
				output.close();
			}
		}
	}
	
	private static class TableHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("html/strippedFile2.html");
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}
	
	/**
	 * Handler class for serving the user the JavaScript portion of the bootstrap framework
	 */
	private static class JavascriptHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("resources/bootstrap.js");
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}
	
	/**
	 * Handler class for serving the user the Popper portion of the bootstrap framework
	 */
	private static class PopperHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("resources/popper.js");
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}
	
	/**
	 * Handler class for serving the user the JQuery portion of the bootstrap framework
	 */
	private static class JQueryHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("resources/jQuery.js");
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}
	
	/**
	 * Handler class for serving the user the CSS portion of the bootstrap framework
	 */
	private static class BootstrapHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("resources/bootstrap.css");
			t.sendResponseHeaders(200, response.length());
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}
	
	/**
	 * Handler class for the index of the site
	 */
	private static class RootHandler implements HttpHandler
	{
		public void handle(HttpExchange t) throws IOException
		{
			String response = FileManager.readFile("html/index.html");
			t.sendResponseHeaders(200, response.length());
			
			OutputStream output = t.getResponseBody();
			output.write(response.getBytes());
			output.close();
		}
	}

	/**
	 * Binds the HttpServer to a port and returns the server object
	 * @param port - default is 8080
	 * @return The server object after it was bound
	 */
	public static HttpServer startServer(int port)
	{
		HttpServer server = null;
		
		try
		{
			//Creating the server using the port supplied. Second argument is the request backlog allowed, zero sets it to the default
			server = HttpServer.create(new InetSocketAddress(port), 0);
			System.out.println("Server started at port: "+port);
		}
		catch(IOException e)
		{
			System.err.println("ERROR: Error starting server");
			System.err.println("Check if server is already running");
			System.exit(1);//Terminates the program if the server can't be started
		}
		
		return server;
	}
	
	/**
	 * Method that takes a string of a users HTTP query and then maps it to the map that was put in
	 * @param query - The request to be parsed
	 * @param parameters - The Map that the parsed query will be put in
	 * @throws UnsupportedEncodingException If the user tries to enter parameters that aren's supported
	 * an UnsupportedEncodingException will be thrown
	 */
	public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException
	{
		if(query != null)
		{
			String pairs[] = query.split("[&]");//Splitting up the query into pairs using [&] as HTTP requests work 
			for(String pair : pairs)
			{
				String param[] = pair.split("[=]");//Splitting the pairs into the keys and the values
				String key = null;
				String value = null;
				
				
				if(param.length > 0)
					key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));//Using URLDecoder to decode the request
				
				if(param.length > 1)
					value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
				
				//Checking if parameters already has a value for that key
				if(parameters.containsKey(key))
				{
					Object obj = parameters.get(key);
					if(obj instanceof List<?>)
					{
						//Adding the value to the list, since it's already created in the map
						List<String> values = (List<String>) obj;
						values.add(value);
					}
					else if(obj instanceof String)
					{
						//Creating a new ArrayList and added the values to it if a value for the key already exists
						List<String> values = new ArrayList<String>();
						values.add((String) obj);
						values.add(value);
						parameters.put(key, value);
					}
				}
				else
				{
					//Putting the key-value pair into the Map
					parameters.put(key, value);
				}
			}
		}
	}
}
