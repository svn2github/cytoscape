/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping.reader;

import java.util.List;
import java.io.IOException;

/**
 *
 * @author gjj
 */
public interface TextTableReader {

        public void readTable() throws IOException;

	public List getColumnNames();

	/**
	 * Report the result of import as a string.
	 * @return Description of
	 */
	public String getReport();
}
