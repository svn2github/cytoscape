/* File: IDMappingTableReader.java

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

import csplugins.id.mapping.model.*;

import cytoscape.util.URLUtil;

import java.util.List;
import java.util.Vector;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import java.net.URL;

/**
 * Read ID Mapping from table file
 * 
 */
public class IDMappingTableReader implements TextTableReader {
        protected final URL sourceURL;
        protected IDMappingData idMappings;

        private static final String typeSeparator = "[\t]";
        private static final String idSeparator = "[;,]";

        public IDMappingTableReader(final URL sourceURL) {
                this.sourceURL = sourceURL;
                idMappings = null;
        }

        @Override
        public void readTable() throws IOException {
                idMappings = new IDMappingDataImpl();

                InputStream is = URLUtil.getInputStream(sourceURL);
		final BufferedReader bufRd = new BufferedReader(new InputStreamReader(is));

                // add types
		String line = bufRd.readLine();
                if (line==null) {
                        System.err.println("Empty file");
                        return;
                }
                String[] types = line.split(typeSeparator);
                for (String type : types) {
                        if (type.length()==0) {//TODO: how to deal with consecutive separators
                                return;
                        }
                        idMappings.addIDType(type);
                }

                // read each ID mapping (line)
                int lineCount = 1;
                while ((line=bufRd.readLine())!=null) {
                        lineCount++;
                        String[] strs = line.split(typeSeparator);
                        if (strs.length>types.length) {
                                System.err.println("The number of ID is larger than the number of types at row "+lineCount);
                                continue;
                        }

                        this.addIDMapping(types,strs);
                }

                is.close();
                bufRd.close();

                //this.idMappings = idMappings;
        }

        protected void addIDMapping(final String[] types, final String[] strs) {
                Map<String,Set<String>> idMapping = new HashMap<String,Set<String>>();
                for (int i=0; i<strs.length; i++) {
                        String idstr = strs[i];
                        if (idstr==null||idstr.length()==0) {
                                continue;
                        }

                        Set<String> ids = new HashSet<String>();
                        String[] strids = idstr.split(idSeparator);
                        for (String id : strids) {
                                if (id!=null && id.length()!=0)
                                        ids.add(id);
                        }

                        idMapping.put(types[i], ids);
                }

                idMappings.addIDMapping(idMapping);
        }

        @Override
	public List getColumnNames() {
                if (idMappings==null) {
                        return null;
                }

                return new Vector(idMappings.getIDTypes());
        }

	/**
	 * Report the result of import as a string.
	 * @return Description of
	 */
        @Override
	public String getReport() {
                if (idMappings == null) {
                        return "No ID mapping is loaded\n";
                }

                //return idMappings.getIDMappingCount()+" ID mappings are loaded.\n";
                return "ID mapping from file loaded successfully";
        }

        /**
         *
         * @return ID Mappings
         */
        public IDMappingData getIDMappingList() {
                return idMappings;
        }
}
