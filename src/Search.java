import java.util.ArrayList;

/**
 * This class is for the search functions that the server uses.
 */
public class Search
{
	//The department numbers used by EAMS
	//Values redacted for company privacy
	public static final String API_MF = "";
	public static final String OSP1 = "";
	public static final String OSP3 = "";
	public static final String OSP4 = "";
	public static final String PRODUCTION_SERVICES = "";
	public static final String NPTL = "";
	public static final String KTL = "";
	public static final String API_MF_AND_OSP4 = ""; 
	
	/**
	 * This method searches through the passed in array of EAMSObjects for objects that
	 * have the same position as the search term and returns an array of those EAMSObjects
	 * 
	 * 
	 * @param array is the ArrayList of EAMSObjects that will be searched through
	 * @param pos is the position that the program will search for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchPosition(ArrayList<EAMSObject> array, String pos)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		String[] strings;
		boolean isMatch;
		
		//Changing the search term so as to get a more accurate result
		pos = pos.replace('-', ' ');//Hyphens replaced with spaces so it can be split
		strings = pos.toLowerCase().split("[ ]");//Splitting the search term by spaces so it's easier to search through
		
		for(EAMSObject obj : array)
		{
			String position = obj.getPosition().toLowerCase();//Making it case-insensitive
			isMatch = true;//Assume it's a match
			
			//If the position of the object doesn't match the words in the search term it's flagged as a non-match
			for(String string : strings)
				if(position.indexOf(string) == -1)
					isMatch = false;
			
			//Only adds the current object in the loop to the return array if it's a match
			if(isMatch)
				returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	/**
	 * This method searches through the passed in EAMSObjects and adds them to an ArrayList
	 * that will be returned when finished
	 * 
	 * @param array is the ArrayList of EAMSObjects that will be searched through
	 * @param man is the manufacturer that the program will search for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchManufacturer(ArrayList<EAMSObject> array, String man)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		man = man.toLowerCase();//Making it case-insensitive
		
		for(EAMSObject obj : array)
			if(obj.getManufacturer().toLowerCase().indexOf(man) != -1)//Checking if the search term matches the object
				returnArray.add(obj);//Adding matches to the array that will be returned
		
		return returnArray;
	}
	
	/**
	 * This method searches through the passed in array of EAMSObjects for the passed in search term
	 * and returns an ArrayList of matching objects
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param cat is the search term
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchCategory(ArrayList<EAMSObject> array, String cat)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		cat = cat.toLowerCase();//Making the search term case-insensitive
		
		for(EAMSObject obj : array)
			if(obj.getCategory().toLowerCase().indexOf(cat) != -1)//Checking if the search term matches the object
				returnArray.add(obj);
		
		return returnArray;
	}
	
	/**
	 * This method searches through the passed in array for the EAMS tag that has been passed in.
	 * The search is case-sensitive as it should only be numbers that are being searched.
	 * <br>
	 * Even though the tag number is unique, it still searches the entire array. This is for
	 * partially-entered values also being able to be used.
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param tag is the tag to search for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchTag(ArrayList<EAMSObject> array, String tag)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		//Checking if any of the objects have a matching tag number
		for(EAMSObject obj : array)
			if(obj.getVariable2().indexOf(tag) != -1)
				returnArray.add(obj);
		
		return returnArray;
	}
	
	/**
	 * Searches the array of EAMSObjects for objects that have matching cab numbers as 
	 * the one that has been passed in.
	 * <br>
	 * It strips "cp" and "cp-" from the search term as some of the EAMSObjects have the
	 * same cab number under cpXXXX and cp-XXXX, thus providing more accurate results 
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param cab is the cab to search for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchCab(ArrayList<EAMSObject> array, String cab)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		cab = cab.toLowerCase();//Making it case-insensitive
		
		//Stripping the search term of "cp" and "cp-" for better results
		if(cab.indexOf("cp-") != -1)
			cab = cab.replace("cp-", "");
		else if(cab.indexOf("cp") != -1)
			cab = cab.replace("cp", "");
		
		//Checking the array for matching EAMSObjects
		for(EAMSObject obj : array)
			if(obj.getVariable4().indexOf(cab) != -1)
				returnArray.add(obj);
		
		
		return returnArray;
	}
	
	/**
	 * This method searches the array of EAMSObjects for entries that match the search term.
	 * <br>
	 * Any spaces in the model name are removed as some entries have the same model under with
	 * and without the space in the name. E.G. The SR 511/SR511 controller made by ABB
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param modelName is the model name that will be searched for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchModel(ArrayList<EAMSObject> array, String modelName)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		//Removing the spaces from the model name and making it case-insensitive
		modelName = modelName.replace(" ", "").toLowerCase();
		
		for(EAMSObject obj : array)
		{
			//Removing the spaces from the object to be searched as well
			String model = obj.getModel().replace(" ", "").toLowerCase();
			if(model.indexOf(modelName) != -1)
				returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	/**
	 * This method searches every column of the given array of EAMSObjects
	 * for the search terms that have been passed in
	 * <br>
	 * It has not been implemented on the user-end and has not been properly tested
	 * @param array The ArrayList of EAMSObjects that will be searched
	 * @param searchTerms The terms that should be searched for
	 * @return Returns an ArrayList of EAMSObjects that match the search terms
	 */
	public static ArrayList<EAMSObject> searchAll(ArrayList<EAMSObject> array, String searchTerms)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		String[] strings = searchTerms.toLowerCase().split("[ ]");
		boolean hasString = false;
		
		for(EAMSObject obj : array)
		{
			String[] values = obj.getObjectValues();
			
			for(String value : values)
			{
				if(!hasString)
				{
					for(String string : strings)
					{
						if(value.toLowerCase().indexOf(string) != -1)
						{
							returnArray.add(obj);
							break;
						}
					}
				}
			}
		}
		
		return returnArray;
	}
	
	/**
	 * This method searches the EAMSObjects descriptions for the searchTerms that have been passed in.
	 * <br>
	 * It splits the terms by spaces so as to make it independent of the positioning of the terms in the
	 * description, thus giving better results.
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param searchTerms are the search terms that will be searched for 
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchDescription(ArrayList<EAMSObject> array, String searchTerms)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		String[] strings = searchTerms.toLowerCase().split("[ ]");//Splitting the search terms by spaces
		boolean isMatch;
		
		for(EAMSObject obj : array)
		{
			//Making the search case-insensitive
			String description = obj.getDescription().toLowerCase();
			isMatch = true;
			
			//Checking all search terms to see if the object matches
			//If one search term is not there the object is considered a non-match
			for(String string : strings)
				if(description.indexOf(string) == -1)
					isMatch = false;
			
			//Adding matches to the return array
			if(isMatch)
				returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	/**
	 * This method searches through the array of EAMSObjects and returns a new array of objects
	 * that match the plant that has been searched for.
	 * <br>
	 * The method uses Strings that are declared as constant to check for the plant numbers
	 * instead of integers as it was easier to use strings instead of rewriting everything.
	 * <br>
	 * The number 6 is also used to represent both OSP4 and API-MF together.
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param plant is the number (in String form) representing the plant to search for
	 * @return returns an ArrayList of EAMSObjects
	 */
	public static ArrayList<EAMSObject> searchPlant(ArrayList<EAMSObject> array, String plant)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		//Checking if the user wants both API-MF and OSP4 search results
		if(!(plant.equals(API_MF_AND_OSP4)))
		{
			for(EAMSObject obj : array)
				if(obj.getDepartment().equalsIgnoreCase(plant))
					returnArray.add(obj);
		}
		else
		{
			for(EAMSObject obj : array)
				if(obj.getDepartment().equalsIgnoreCase(API_MF) || obj.getDepartment().equalsIgnoreCase(OSP4))
					returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	/**
	 * This method is the one used when filtering an array of objects instead of searching.
	 * <br>
	 * It takes in an array of string values that will be used as the filter when going through the objects.
	 * It is case-insensitive, but otherwise must have the exact filter value in the object to be considered
	 * a match.
	 * 
	 * @param array is the array of EAMS objects to be searched
	 * @param filterValues is the String array of filter values to check against the EAMSObjects
	 * @return returns an ArrayList of EAMSObjects 
	 */
	public static ArrayList<EAMSObject> filterArray(ArrayList<EAMSObject> array, String[] filterValues)
	{
		ArrayList<EAMSObject> returnArray = new ArrayList<EAMSObject>();
		
		for(EAMSObject obj : array)
		{
			//Getting a string array of the values of the EAMSObject to make it easier to filter
			String[] objValues = obj.getObjectValues();
			boolean hasString = true;
			
			for(int i=0;i<objValues.length;i++)
			{
				//Will only test the filter vale if it's not equal to null, thus allowing blank filter values
				if(filterValues[i] != null)
				{
					if(objValues[i].toUpperCase().indexOf(filterValues[i].toUpperCase()) == -1)
					{
						hasString = false;
					}
				}
			}
			if(hasString)
				returnArray.add(obj);
		}
		
		return returnArray;
	}
}