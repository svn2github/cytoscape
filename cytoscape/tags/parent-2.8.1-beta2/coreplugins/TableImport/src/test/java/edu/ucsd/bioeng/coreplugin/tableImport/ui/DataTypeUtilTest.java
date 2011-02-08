
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

package edu.ucsd.bioeng.coreplugin.tableImport.ui;

import junit.framework.*;
import javax.swing.table.TableModel;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.util.HashMap;
import cytoscape.data.CyAttributes;

public class DataTypeUtilTest extends TestCase {

	TableModel tableModel;
	Map<String,Byte[]> dataTypeMap;

	protected void setUp() throws Exception {
		super.setUp();

		Object[] columnNames = 
		        {"allFalse","mostlyFalse","mostlyTrue","integer","double","string"}; 
		Object[][] data = {
		       { "false",  "false",      "false",      "1",     "1.0",   "homer"    },
		       { "false",  "true",       "true",       "5",     "4.4",   "marge"    },
		       { "false",  "false",      "true",       "18",    "3.14",  "bart"     },
		       { "false",  "false",      "true",       "123",   "123",   "lisa"     },
		       { "false",  "true",       "true",       "7",     "5",     "maggie"   },
		       { "false",  "false",      "false",      "9",     "0.007", "smithers" } 
			};

		tableModel = new DefaultTableModel(data, columnNames);
		dataTypeMap = new HashMap<String,Byte[]>();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGuessTypes() throws Exception {
		DataTypeUtil.guessTypes(tableModel,"test1",dataTypeMap);
		Byte[] res = dataTypeMap.get("test1");

		// CyAttributes.TYPE_BOOLEAN = 1
		// CyAttributes.TYPE_FLOATING = 2
		// CyAttributes.TYPE_INTEGER = 3
		// CyAttributes.TYPE_STRING = 4
		assertEquals( CyAttributes.TYPE_BOOLEAN,  res[0].byteValue() ); // allFalse
		assertEquals( CyAttributes.TYPE_BOOLEAN,  res[1].byteValue() ); // mostlyFalse
		assertEquals( CyAttributes.TYPE_BOOLEAN,  res[2].byteValue() ); // mostlyTrue
		assertEquals( CyAttributes.TYPE_INTEGER,  res[3].byteValue() ); // integer
		assertEquals( CyAttributes.TYPE_FLOATING, res[4].byteValue() ); // double
		assertEquals( CyAttributes.TYPE_STRING,   res[5].byteValue() ); // string
	}
}
