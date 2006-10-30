package cytoscape.data.readers;

import java.io.IOException;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import cytoscape.data.CyAttributes;

/**
 * Reader for Excel attribute workbook.<br>
 * This class creates string array and pass it to the AttributeLineParser.<br>
 * 
 * <p>
 * This reader takes one sheet at a time.
 * </p>
 * 
 * @version 0.7
 * @since Cytoscape 2.4
 * @author kono
 * 
 */
public class ExcelAttributeSheetReader implements TextTableReader {

	private final HSSFSheet sheet;
	private final AttributeMappingParameters mapping;
	private final AttributeLineParser parser;

	public ExcelAttributeSheetReader(final HSSFSheet sheet,
			final AttributeMappingParameters mapping) {
		this.sheet = sheet;
		this.mapping = mapping;
		this.parser = new AttributeLineParser(mapping);
	}

	public List getColumnNames() {
		
		return null;
	}

	public void readTable() throws IOException {

		HSSFRow row;
		int rowCount = 0;
		String[] cellsInOneRow;

		while ((row = sheet.getRow(rowCount)) != null) {
			cellsInOneRow = createElementStringArray(row);
			parser.parseEntry(cellsInOneRow);
			rowCount++;
		}
	}

	/**
	 * For a given Excell row, convert the cells into String.
	 * 
	 * @param row
	 * @return
	 */
	private String[] createElementStringArray(HSSFRow row) {

		String[] cells = new String[mapping.getColumnCount()];
		HSSFCell cell = null;

		for (short i = 0; i < mapping.getColumnCount(); i++) {

			cell = row.getCell(i);
			if (cell == null) {
				cells[i] = null;
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				cells[i] = cell.getStringCellValue();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (mapping.getAttributeTypes()[i] == CyAttributes.TYPE_INTEGER) {
					Double dblValue = cell.getNumericCellValue();
					Integer intValue = dblValue.intValue();
					cells[i] = intValue.toString();
				} else {
					cells[i] = Double.toString(cell.getNumericCellValue());
				}
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				cells[i] = Boolean.toString(cell.getBooleanCellValue());
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
				cells[i] = null;
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
				cells[i] = null;
				System.out.println("Error found when reading a cell!");
			}
		}
		return cells;
	}
}
