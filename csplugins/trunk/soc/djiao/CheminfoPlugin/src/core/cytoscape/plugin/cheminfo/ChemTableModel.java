/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;


/**
 * This class is the model for rendering a table 
 * 
 * @author <a href="mailto:djiao@indiana.edu">David Jiao</a>
 * @version $Revision: $ $Date: $
 */
public class ChemTableModel extends AbstractTableModel {
  
    protected List records;
    protected List colNames;
    
    protected Map<String, List> recordHash;
    
    public ChemTableModel() {
        super();
    }
    
    public ChemTableModel(List records, List colNames) {
        super();
        this.records = records;
        this.colNames = colNames;
        this.recordHash = new HashMap<String, List>();
        for (Object object : records) {
			List record = (List)object;
			recordHash.put((String)record.get(0), record);
		}
    }
    
    public String getColumnName(int columnIndex) {
        if (columnIndex < colNames.size()) {
            return (String)colNames.get(columnIndex);
        } else {
            return "";
        }
    }
    
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0 || columnIndex == 1) {
            return String.class;
        } else {
            return Double.class;
        }
    }

    /**
     * @return  Returns the records.
     * @uml.property  name="records"
     */
    public List getRecords() {
        return records;
    }

    /**
     * @param records  The records to set.
     * @uml.property  name="records"
     */
    public void setRecords(List records) {
        this.records = records;
    }

    public int getRowCount() {
        return records.size();
    }

    public int getColumnCount() {
        if (records.size() > 0) {
            return ((List)records.get(0)).size();
        } else {
            return 0;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < records.size()) {
            List record = (List)records.get(rowIndex);
            if (columnIndex < record.size())
                return record.get(columnIndex);
            else 
                return null;
        } else {
            return null;
        }
    }
    
    
    public List getValuesAt(int[] rows) {
        List values = new ArrayList();
        for (int i = 0; i < rows.length; i++) {
            values.add(getRecords().get(rows[i]));
        }
        return values;
    }
    
    public void addAll(List records) {
    	synchronized (records) {
        	this.records.addAll(records);
        	for (Object object : records) {
				List record = (List)object;
				recordHash.put((String)record.get(0), record);
			}
		}
    }
    
    public void removeAll(List records) {
    	synchronized (records) {
    		for (Object object : records) {
				List record = (List)object;
				this.records.remove(recordHash.get(record.get(0)));
				recordHash.remove(record.get(0));
			}
		}
    }
    
    public List removeOthers(List records) {
		List removed = new ArrayList();
    	synchronized (records) {
    		for (Map.Entry<String, List> object : recordHash.entrySet()) {
				if (!records.contains(object.getKey())) {
					this.records.remove(object.getValue());
					removed.add(object.getValue());
					recordHash.remove(object.getKey());
				}
			}
    	}
    	return removed;
    }
}
