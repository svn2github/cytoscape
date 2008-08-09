/* File: ExcelIDMappingSheetReader.java

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

package csplugins.id.mapping.reader;

import csplugins.id.mapping.model.IDMappingData;
import csplugins.id.mapping.model.IDMappingDataImpl;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 * Read ID Mapping from MS Excel file
 * 
 */
public class ExcelIDMappingSheetReader extends IDMappingTableReader {
        private final HSSFSheet sheet;


        public ExcelIDMappingSheetReader(final HSSFSheet sheet) {
                super(null);
                this.sheet = sheet;
        }

        @Override
        public void readTable() throws IOException {
		idMappings = new IDMappingDataImpl();

                // add types
		HSSFRow row = sheet.getRow(0);
                if (row == null) {
                        System.err.println("Empty file");
                        return;
                }
                String[] types = createElementStringList(row);
                if (types==null || types.length==0) {
                        return;
                }


                Set<String> typeSet = new HashSet<String>();
                for (String type : types) {
                        if (type==null || type.length()==0) {
                                System.err.println("type is null or empty");
                                return;
                        }
                        typeSet.add(type);
                }

                idMappings.addIDTypes(typeSet);

                // read each ID mapping (line)
                int rowCount = 1;
                while ((row = sheet.getRow(rowCount))!=null) {
                        rowCount++;
                        String[] strs = createElementStringList(row);
                        if (strs.length>types.length) {
                                System.err.println("The number of ID is larger than the number of types at row "+rowCount);
                                continue;
                        }

                        this.addIDMapping(types,strs);
                }

        }

        /**
	 * For a given Excell row, convert the cells into String.
	 *
	 * @param row
	 * @return
	 */
	private String[] createElementStringList(final HSSFRow row) {
                List<String> cells = new Vector<String>();
                final String nullStr = "";

		Iterator<HSSFCell> itCell = row.cellIterator();
                while (itCell.hasNext()) {
                        HSSFCell cell = itCell.next();

			if (cell == null) {
				cells.add(nullStr);
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				cells.add(cell.getRichStringCellValue().getString());
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				cells.add(Double.toString(cell.getNumericCellValue()));
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
				cells.add(Boolean.toString(cell.getBooleanCellValue()));
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
				cells.add(nullStr);
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
				cells.add(nullStr);
				System.out.println("Error found when reading a cell!");
			}
		}

                final int n = cells.size();
                String[] return_this = new String[n];
                for (int i=0; i<n; i++) {
                        String cell = cells.get(i);
                        return_this[i] = nullStr.compareTo(cell)==0 ? null:cell;
                }

		return return_this;
	}
}
