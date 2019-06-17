import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"serial"})
/**
 * This class is used to represent an EAMS asset.
 */
public class EAMSObject implements Serializable
{
	private String position;
	private String description;
	private String organization;
	private String department;
	private String classVar;
	private String category;
	private String manufacturer;
	private String model;
	private String location;
	private String variable2;
	private String variable4;
	public static String[] headings = {"Position", "Description", "Organization", "Department(*)", "Class", "Category", "Manufacturer", "Model", "Location(*)", "Variable 2", "Variable 4"};
	public static final int POSITION = 0, DESCRIPTION = 1, ORGANIZATION = 2, DEPARTMENT = 3, CLASSVAR = 4, CATEGORY = 5, MANUFACTURER = 6;
	public static final int MODEL = 7, LOCATION = 8, VARIABLE2 = 9, VARIABLE4 = 10;
	
	/**
	 * Constructor for making an EAMSObject
	 * @param values A string of the values that will be assigned to the object
	 */
	public EAMSObject(String[] values)
	{
		if(values.length>11)
			return;
		else
		{
			position = values[0];
			description = values[1];
			organization = values[2];
			department = values[3];
			classVar = values[4];
			category = values[5];
			manufacturer = values[6];
			model = values[7];
			location = values[8];
			variable2 = values[9];
			variable4 = values[10];
		}
		
	}
	
	public EAMSObject(String position, String description, String organization, String department, 
					  String classVar, String category, String manufacurer, String location, String variable2, String variable4)
	{
		this.position = position;
		this.description = description;
		this.organization = organization;
		this.department = department;
		this.classVar = classVar;
		this.category = category;
		this.manufacturer = manufacurer;
		this.location = location;
		this.variable2 = variable2;
		this.variable4 = variable4;
	}
	
	/**
	 * Method used for getting a map of the index of the object values
	 * @return A map of the variable names and their index value
	 */
	public static Map<String, Integer> getMapValues()
	{
		Map<String, Integer> values = new HashMap<String, Integer>();
		
		values.put("position", 0);
		values.put("description", 1);
		values.put("organization", 2);
		values.put("department", 3);
		values.put("class", 4);
		values.put("category", 5);
		values.put("manufacturer", 6);
		values.put("model", 7);
		values.put("location", 8);
		values.put("variable2", 9);
		values.put("variable4", 10);
		
		return values;
	}
	
	/**
	 * Method for getting the names of the variables
	 * @return A string array of the names of the object's variables
	 */
	public String[] getObjectValues()
	{
		String[] values = new String[11];
		
		values[0] = position;
		values[1] = description;
		values[2] = organization;
		values[3] = department;
		values[4] = classVar;
		values[5] = category;
		values[6] = manufacturer;
		values[7] = model;
		values[8] = location;
		values[9] = variable2;
		values[10] = variable4;
		
		return values;
	}
	
	public String getPosition()
	{
		return position;
	}

	public void setPosition(String position)
	{
		this.position = position;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getOrganization()
	{
		return organization;
	}

	public void setOrganization(String organization)
	{
		this.organization = organization;
	}

	public String getDepartment()
	{
		return department;
	}

	public void setDepartment(String department)
	{
		this.department = department;
	}

	public String getClassVar()
	{
		return classVar;
	}

	public void setClassVar(String classVar)
	{
		this.classVar = classVar;
	}

	public String getCategory()
	{
		return category;
	}

	public void setCategory(String category)
	{
		this.category = category;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getLocation()
	{
		return location;
	}

	public void setLocation(String location)
	{
		this.location = location;
	}

	public String getVariable4()
	{
		return variable4;
	}

	public void setVariable4(String variable4)
	{
		this.variable4 = variable4;
	}

	public String getVariable2()
	{
		return variable2;
	}

	public void setVariable2(String variable2)
	{
		this.variable2 = variable2;
	}
	
	/**
	 * toString method that returns the description of the object
	 */
	public String toString()
	{
		return "Description: "+description;
	}
	
}














