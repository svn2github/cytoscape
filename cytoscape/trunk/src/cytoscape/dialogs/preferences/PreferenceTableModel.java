package cytoscape.dialogs.preferences;

import cytoscape.*;

import java.net.URL;
import java.io.*;
import javax.swing.table.*;
import javax.swing.*;
import java.util.*;

public class PreferenceTableModel extends AbstractTableModel {
    static int[] columnWidth = new int[] {100, 400};
    static int[] alignment = new int[] {JLabel.LEFT, JLabel.LEFT};

    private Set pluginURLs = new HashSet();

    private Properties properties;
    Vector propertiesList = new Vector();
    static String[] columnHeader = new String[] {
        "Property Name", "Value"};
    private int rowNum = 0;

    public PreferenceTableModel() {
        super();
	// use clone of CytoscapeInit properties
        properties = (Properties)(CytoscapeInit.getProperties().clone());
	// remove "plugins" entry since that is in PluginsTableModel
	properties.remove("plugins");
        loadProperties();
    }
    
    public String abs2rel(String absS) {
        String mrudURI = new String((new File(
	    System.getProperty("user.dir"))).toURI().toString().substring(5));
        String absURI = absS;
        String prefix = new String();
        String returnRel = null;
            
        int check = 0;
        for (int i=1;i<mrudURI.length();i++) {
            if (absURI.startsWith(mrudURI.substring(0,i))) {
                check = i;
            } else {
                break;
            }
        }
            
        int lastpath = absURI.substring(0,check+1).lastIndexOf("/");
            
        if (lastpath == mrudURI.length()-1) {
            returnRel = absURI.substring(lastpath+1);
        } else { 
            returnRel = new String(mrudURI.substring(lastpath+1).toLowerCase().replaceAll("[a-z]+","..") + absURI.substring(lastpath+1));
        }
            
        return returnRel;
    }
    

    public void loadProperties() {
	    clearVector();
	    for (Enumeration names = properties.propertyNames();
					names.hasMoreElements();) {
		String name = (String)names.nextElement();
		addRow(new String[] {name,properties.getProperty(name)});
	    }
//	    addRow( new String[]{"mrud",properties.getProperty("mrud",
//		System.getProperty( "user.dir" ))});
	}

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key,value);
    }

    public void addRow(String[] row) {
        if (row.length < 0 || row.length > columnHeader.length) return;
        propertiesList.add(row);
        sort();
        rowNum++;
    }

    public String getColumnName(int col) { return columnHeader[col]; }
    
    public String getFieldName(int row) {
        String returnvalue = (String)getValueAt(row, 0);
        return returnvalue;
    }
    
    public void clearVector() {
        propertiesList.clear();
    }

    public void save(Properties updateProps) {
	// update local property values in passed-in Properties
	updateProps.putAll(properties);
    }

    public int getColumnCount() {
        return columnHeader.length;
    }

    public Object getValueAt(int row, int col) {
        String[] rowData = (String[])propertiesList.get(row);
        return rowData[col];
    }

    public int getRowCount() {
        return propertiesList.size();
    }

    public void sort() {
        Collections.sort(propertiesList, new StringComparator());
    }
}

class StringComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        int result = 0;

        String[] str1 = ((String[])o1);
        String[] str2 = ((String[])o2);
        
        for (int i=0;i<str1.length;i++) {
            result = str1[i].compareTo(str2[i]);

            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
    
