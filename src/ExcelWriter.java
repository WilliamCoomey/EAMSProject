import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter
{
	/**
	 * This method creates an Excel file with the given filename and writes
	 * the assets of the given EAMSObject ArrayList to it
	 * @param filename The name of the file to save
	 * @param array The ArrayList of EAMSObjects to write to the file
	 */
	public static void createEAMSSheet(String filename, ArrayList<EAMSObject> array)
	{
		FileOutputStream output = null;
		try
		{
			output = new FileOutputStream(filename);
		}
		catch(FileNotFoundException e)
		{
			System.err.println("ERROR: The file couldn't be found");
		}
		//Creating the workbook and sheet that will be written to
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		
		addHeaders(workbook, sheet, EAMSObject.headings);
		writeArray(sheet, array);
		
		try
		{
			workbook.write(output);
			workbook.close();
			output.close();
		}
		catch(IOException e)
		{
			System.err.println("ERROR: There was an error writing the file");
		}
	}
	
	/**
	 * This method adds the the given headers to the Excel sheet
	 * @param workbook The workbook to write to
	 * @param sheet The sheet to write to
	 * @param headers A string array of the headers that will be written to the sheet
	 */
	public static void addHeaders(Workbook workbook,Sheet sheet, String[] headers)
	{
		int lastRowIndex = sheet.getLastRowNum();
		Row row = sheet.createRow(lastRowIndex);
		
		//Making the header bold
		CellStyle style = workbook.createCellStyle();
		Font boldFont = workbook.createFont();
		boldFont.setBold(true);
		style.setFont(boldFont);
		
		//Writing the header cells to the row
		for(int i=0;i<headers.length;i++)
		{
			Cell cell = row.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(style);
		}
	}
	
	/**
	 * Writes the values of the given EAMSObject ArrayList to an Excel sheet
	 * @param sheet The sheet that the ArrayList of EAMSObjects will be written to
	 * @param array The ArrayList of EAMSObjects to write tot the sheet
	 */
	public static void writeArray(Sheet sheet, ArrayList<EAMSObject> array)
	{
		int lastRowIndex = sheet.getLastRowNum();
		
		for(int i=0;i<array.size();i++)
		{
			Row row = sheet.createRow(++lastRowIndex);
			String[] values = array.get(i).getObjectValues();
			for(int j=0;j<values.length;j++)
			{
				Cell cell = row.createCell(j);
				cell.setCellValue(values[j]);
			}
		}
		
		//Auto-sizes the sheet to fit the array values - takes a long time with large arrays
		if(array.size() > 0)
			for(int i=0;i<sheet.getRow(--lastRowIndex).getLastCellNum();i++)
				sheet.autoSizeColumn(i);
	}
	
	/**
	 * Writes the values of the given EAMSObject ArrayList to an Excel sheet.
	 * <br>
	 * This method doesn't auto-size the the Excel sheet, which decreases the time it
	 * takes to write a long document
	 * @param sheet The sheet that the ArrayList of EAMSObjects will be written to
	 * @param array The ArrayList of EAMSObjects to write to the sheet 
	 */
	public static void writeArrayNoAutoSize(Sheet sheet, ArrayList<EAMSObject> array)
	{
		int lastRowIndex = sheet.getLastRowNum();
		
		for(int i=0;i<array.size();i++)
		{
			Row row = sheet.createRow(lastRowIndex++);
			String[] values = array.get(i).getObjectValues();
			for(int j=0;j<values.length;j++)
			{
				Cell cell = row.createCell(j);
				cell.setCellValue(values[j]);
			}
		}
	}
}
