package cytoscape.plugin.cheminfo;

import java.util.ArrayList;
import java.util.List;

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
    
    public ChemTableModel() {
        super();
    }
    
    public ChemTableModel(List records, List colNames) {
        super();
        this.records = records;
        this.colNames = colNames;
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
}
