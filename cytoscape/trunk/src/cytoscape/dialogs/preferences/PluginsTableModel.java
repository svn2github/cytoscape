package cytoscape.dialogs.preferences;

import cytoscape.*;

import java.net.URL;
import java.io.*;
import javax.swing.table.*;
import javax.swing.*;
import java.util.*;

public class PluginsTableModel extends AbstractTableModel {
    static int[] columnWidth = new int[] {400};
    static int[] alignment = new int[] {JLabel.LEFT};

    private Set pluginURLs = new HashSet();

    private Properties properties;
    Vector propertiesList = new Vector();
    static String[] columnHeader = new String[] {"Plugin Location"};
    private int rowNum = 0;

    public PluginsTableModel() {
        super();
	// get only one entry from properties: key=plugins
	properties = new Properties();
	if (CytoscapeInit.getProperties().get("plugins") != null) {
	    properties.put("plugins",
			CytoscapeInit.getProperties().get("plugins"));
	}
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
        if ( getProperty( "plugins" ) != null ) {
            String[] pargs = getProperty( "plugins", "" ).split(",");
            for ( int i = 0; i < pargs.length; i++ ) {
                String plugin = pargs[i];
                URL url; 
                try {
                    if ( plugin.startsWith( "http" ) ) {
                        plugin = plugin.replaceAll( "http:/" ,"http://" );
                        plugin = "jar:"+plugin+"!/";
                        url = new URL( plugin );
                    } else {
                        url = new URL( "file", "", plugin );
                    }
                    pluginURLs.add( url );
        		    addRow( new String[]{url.toString().substring(5)});
                } catch ( Exception ue ) {
                    System.err.println( "Jar: "+pargs[i]+ "was not a valid URL" );
                }
            }
        }
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

    public void addPlugin(String preferenceValue) {
        String tempPlugins = properties.getProperty("plugins");
        if (tempPlugins != null) {
//cull out duplicate entries, since single entries and directory adds
// can result in duplicates not specified strictly at UI level
            properties.setProperty("plugins",
		cullDuplicates(tempPlugins + "," + preferenceValue));
        } else {
            properties.setProperty("plugins", preferenceValue);
        }
    }

   /*
    * cull duplicate entries in comma-separated tokens in String
    */
    String cullDuplicates(String s) {
      StringTokenizer st = new StringTokenizer(s,",");
      HashSet hashSet = new HashSet();
      while (st.hasMoreTokens()) {
	hashSet.add(st.nextToken());
      }
      String newString = new String();
      Iterator it = hashSet.iterator();
      StringBuffer sb = new StringBuffer();
      while (it.hasNext()) {
	sb.append((String)it.next());
	sb.append(",");
      }
      sb.deleteCharAt(sb.length()-1);

      return sb.toString();
    }

    
    public void deletePlugins(String[] deletedPlugins) {
//MDA - deletion a little weird - multiple selection deletes not working
// right - restricting via setting Tables to use single selection
        String tempPlugins = properties.getProperty("plugins");
	for (int k = 0; k < deletedPlugins.length; k++) {
	  String value = new String(deletedPlugins[k]);
          if (value.startsWith("file:"))
            value = value.substring(5);
          if (tempPlugins != null) {
            String [] plugins = tempPlugins.split(",");
            String returnString = null;
            for (int i=0;i<plugins.length;i++) {
                if (value.compareTo(plugins[i])==0) {
                } else {
                    if (returnString == null) {
                        returnString = new String(plugins[i]);
                    } else {
                        returnString = new String(returnString+","+plugins[i]);
                    }
                }
            }
            if (returnString==null) {
                properties.remove("plugins");
            } else {
                properties.setProperty("plugins", returnString);
            }
          }
	}
	for (int k = 0; k < deletedPlugins.length; k++) {
          for (int i=0;i<propertiesList.size();i++) {
            String[] rowData = (String[])propertiesList.get(i);
            if (rowData[0].equals(deletedPlugins[k])) {
                propertiesList.remove(i);
            }
          }
	}
    }
    
    public void addRow(String[] row) {
        if (row.length < 0 || row.length > columnHeader.length) return;
        propertiesList.add(row);
        removeRedundancy();
        sort();
        rowNum++;
    }

    public void removeRedundancy() {
        Set s = new HashSet();

        for (int i=0;i<propertiesList.size();i++) {
            String[] row1 = (String[])propertiesList.get(i);
            s.add(row1);
        }
        propertiesList.clear();
        String[] element = null;
        String allPlugin = new String ("");        
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            element = (String[]) iter.next();
            if (allPlugin.length()>0) {
                allPlugin = allPlugin+","+element[0];
            } else {
                allPlugin = new String(element[0]);
            }
            propertiesList.add(element);
        }
        properties.setProperty("plugins", allPlugin);
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
