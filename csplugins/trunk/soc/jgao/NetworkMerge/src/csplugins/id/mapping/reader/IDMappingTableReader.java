/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping.reader;

import java.util.List;

import java.io.IOException;

import java.net.URL;

/**
 *
 * @author gjj
 */
public class IDMappingTableReader implements TextTableReader {
        protected final URL sourceURL;

        public IDMappingTableReader(final URL sourceURL) {
                this.sourceURL = sourceURL;
        }

        public void readTable() throws IOException {
                throw new IOException();
        }

	public List getColumnNames() {
                return null;
        }

	/**
	 * Report the result of import as a string.
	 * @return Description of
	 */
	public String getReport() {
                return null;
        }
}
