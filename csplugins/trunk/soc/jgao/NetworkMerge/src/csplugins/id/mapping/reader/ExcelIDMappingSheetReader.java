/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping.reader;

import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;

/**
 *
 * @author gjj
 */
public class ExcelIDMappingSheetReader extends IDMappingTableReader {
        private final HSSFSheet sheet;

        public ExcelIDMappingSheetReader(final HSSFSheet sheet) {
                super(null);
                this.sheet = sheet;
        }

        public void readTable() throws IOException {
                throw new IOException();
        }
}
