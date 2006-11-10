package cytoscape.data.readers;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

import cytoscape.data.CyAttributes;


/**
 * Reader for Network file in Excel (.xls) format.<br>
 * 
 * <p>
 * Currently supports only one sheet.
 * </p>
 * 
 * @since Cytoscape 2.4
 * @version 0.6
 * @author Keiichiro Ono
 */
public class ExcelNetworkSheetReader extends NetworkTableReader {

	private final HSSFSheet sheet;

	public ExcelNetworkSheetReader(String networkName, HSSFSheet sheet,
			NetworkTableMappingParameters nmp) {
		super(networkName, null, nmp);

		this.sheet = sheet;
	}

	@Override
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

		String[] cells = new String[nmp.getColumnCount()];
		HSSFCell cell = null;

		for (short i = 0; i < nmp.getColumnCount(); i++) {

			cell = row.getCell(i);
			if (cell == null) {
				cells[i] = null;
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				cells[i] = cell.getStringCellValue();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				if (nmp.getAttributeTypes()[i] == CyAttributes.TYPE_INTEGER) {
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
