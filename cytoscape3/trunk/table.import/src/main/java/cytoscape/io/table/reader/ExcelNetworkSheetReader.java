
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.io.table.reader;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.cytoscape.model.CyRow;

import java.io.IOException;


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

	/*
	 * Reader will read entries from this line.
	 */
	/**
	 * Creates a new ExcelNetworkSheetReader object.
	 *
	 * @param networkName  DOCUMENT ME!
	 * @param sheet  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 */
	public ExcelNetworkSheetReader(String networkName, HSSFSheet sheet,
	                               NetworkTableMappingParameters nmp) {
		this(networkName, sheet, nmp, 0);
	}

	/**
	 * Creates a new ExcelNetworkSheetReader object.
	 *
	 * @param networkName  DOCUMENT ME!
	 * @param sheet  DOCUMENT ME!
	 * @param nmp  DOCUMENT ME!
	 * @param startLineNumber  DOCUMENT ME!
	 */
	public ExcelNetworkSheetReader(String networkName, HSSFSheet sheet,
	                               NetworkTableMappingParameters nmp, final int startLineNumber) {
		this(networkName, sheet, nmp, startLineNumber, true);
	}
	public ExcelNetworkSheetReader(String networkName, HSSFSheet sheet,
			NetworkTableMappingParameters nmp, final int startLineNumber, final boolean directed_edges) {
		super(networkName, null, nmp, startLineNumber, null, directed_edges);
		this.sheet = sheet;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	@Override
	public void readTable() throws IOException {
		HSSFRow row;
		int rowCount = startLineNumber;
		String[] cellsInOneRow;

		while ((row = sheet.getRow(rowCount)) != null) {
			cellsInOneRow = createElementStringArray(row);
			try {
				parser.parseEntry(cellsInOneRow);
			} catch (Exception e) {
				System.out.println("Couldn't parse row: " + rowCount);
			}
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
				cells[i] = cell.getRichStringCellValue().getString();
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
