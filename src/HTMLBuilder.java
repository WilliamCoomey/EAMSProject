import java.util.ArrayList;

/**
 * This class is responsible for creating the HTML document that
 * will be served to the user
 */
public class HTMLBuilder
{
	/**
	 * Method that makes the HTML document based of the passed in ArrayList of
	 * EAMSObjects
	 * @param array The ArrayList of EAMSObjects to make the HTML document out of
	 * @return The HTML doument as a String
	 */
	public static String getHTML(ArrayList<EAMSObject> array)
	{
		String html = "";
		
		html += "<html>\n"+getHead()+getBody(array)+"\n</html>";
		
		return html;
	}

	/**
	 * Method for getting the head section of the HTML file.
	 * It reads the file located at html/htmlParts/head.txt, so any additional
	 * changes to the head can be made there.
	 * @return The string of the head portion of the HTML document
	 */
	public static String getHead()
	{
		return "<head id=\"head\">\n"+FileManager.readFile("html/htmlParts/head.txt")+"</head>\n";
	}
	
	/**
	 * Method for getting the filter section of the HTML file.
	 * It reads the file located at html/htmlParts/filter.txt, so any additional
	 * changes to the filter function can be made there.
	 * @return The string of the filter portion of the HTHL document
	 */
	public static String getFilter()
	{
		return FileManager.readFile("html/htmlParts/filter.txt");
	}
	
	/**
	 * Method for getting the search section of the HTML file.
	 * It reads the file located at html/htmlParts/search.txt, so any additional 
	 * changes to the search function can be made there.
	 * @return The string of the search portion of the HTML document
	 */
	public static String getSearch()
	{
		return FileManager.readFile("html/htmlParts/search.txt");
	}
	
	/**
	 * Method to get the body of the HTML document based of the ArrayList of EAMSObjects
	 * @param array The ArrayList of EAMSObjects to make the HTML body out of
	 * @return A string of the HTML body
	 */
	public static String getBody(ArrayList<EAMSObject> array)
	{
		String body = "";
		
		body = "<body>\n";
		body += "<div class=\"count\">Count = "+array.size()+"</div>\n";
		body += getFilter()+getSearch()+"\n"+getTable(array);
		body += "\n<button class=\"btn btn-block\" onclick=\"printTable()\">Print Table</button>\n</body>";
		
		return body;
	}
	
	/**
	 * Method for getting the full HTML table head
	 * @param array The ArrayList of the EAMSObjects to make the table from
	 * @return The string of the generated table
	 */
	public static String getTable(ArrayList<EAMSObject> array)
	{
		String table = "";
		
		table = "<table class =\"table table-bordered table-hover\" id=\"table\">\n"+getTableHead()+getTableBody(array)+"</table>";
		
		return table;
	}
	
	/**
	 * Method for getting the head of the HTML table
	 * @return The string of the table head
	 */
	public static String getTableHead()
	{
		String table = "";
		String headings = "";
		
		headings += "<tr>\n" + 
						"\t<th>Asset Name (Position)</th>\n" +
						"\t<th>Description</th>\n" + 
						"\t<th>Department</th>\n" +
						"\t<th>Category</th>\n" + 
						"\t<th>Manufacturer</th>\n" + 
						"\t<th>Model</th>\n" + 
						"\t<th>Tag Number (Variable 2)</th>\n" +
						"\t<th>Cabinet Number (Variable 4)</th>\n" +
						"\t<th>Class</th>\n" +
					"</tr>\n";
		
		table = "<thead class=\"thead-dark\">\n"+headings+"</thead>\n";
		
		return table;
	}
	
	/**
	 * Method used to make the body of the HTML table
	 * @param array The ArrayList of EAMSObjects to make the table out of
	 * @return The string of the table body
	 */
	public static String getTableBody(ArrayList<EAMSObject> array)
	{
		String table = "";
		String rows = "";
		
		//Getting the each of the table rows based off of the ArrayList
		for(EAMSObject obj : array)
			rows += getTableRow(obj);
		
		table += "<tbody>\n"+rows+"</tbody>\n";
		
		return table;
	}
	
	/**
	 * Method that uses the passed in EAMSObject to generate a row
	 * of the table.
	 * @param obj The EAMSObject to make the table row from
	 * @return String of the table row
	 */
	public static String getTableRow(EAMSObject obj)
	{
		String row = "";
		String table = "";
		String[] objectValues = obj.getObjectValues();
		
		//Looping through the values of the EAMSObject
		for(int i=0;i<objectValues.length;i++)
			if(i != 8 && i != 2 && i != 4)//Getting rid of the redundant heading "Location(*)"
				row += "<td>"+objectValues[i]+"</td>\n";//Surrounding the object values with table data html tags

		row += "<td>"+objectValues[4]+"</td>\n";
		
		table = "<tr>\n"+row+"</tr>\n";//Adding table row html tags and indenting it properly
		
		return table;
	}
	
	/**
	 * Method used to indent the lines of a String. Was only used for early
	 * development to make for easier debugging. Shouldn't be used in final program.
	 * @param string The string to indent
	 * @return The string after each line has been indented
	 */
	public static String indentString(String string)
	{
		//Splitting the string into its individual lines
		String[] lines = string.split("\\r?\\n");
		String formattedString = "";
		
		//Looping through the lines
		for(int i=0;i<lines.length;i++)
		{
			//Adding the tab to the lines
			lines[i] = "\t"+lines[i];
			formattedString += lines[i]+"\n";
		}
		
		return formattedString;
	}
}









