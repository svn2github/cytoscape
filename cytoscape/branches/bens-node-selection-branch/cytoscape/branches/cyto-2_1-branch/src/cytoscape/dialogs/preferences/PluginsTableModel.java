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

    private Properties properties;
    TreeSet pluginsSet = new TreeSet();
    static String[] columnHeader = new String[] {"Plugin Location"};

    boolean pluginsFromCommandLineLoadedAndSaved;

    public PluginsTableModel() {
        super();
//MDA
	pluginsFromCommandLineLoadedAndSaved = false;
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
	pluginsSet.clear();
        if ( getProperty( "plugins" ) != null ) {
            String[] pargs = getProperty( "plugins", "" ).split(",");
            for ( int i = 0; i < pargs.length; i++ ) {
                addPlugin(pargs[i]);
            }
        }
	// now (non-redundantly) include plugins specified on the command line
	if (!pluginsFromCommandLineLoadedAndSaved) {
	  Set plugins = CytoscapeInit.getPluginURLs();
	  Iterator iterator = plugins.iterator();
	  while (iterator.hasNext()) {
	    URL url = (URL) iterator.next();
            addPlugin(url);
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

    // Add plugin to plugins=... String in private Properties object for
    // later commit to CytoscapeInit's Properties object
    public void addPluginToPropertyString(String newPlugin) {
        String tempPlugins = properties.getProperty("plugins");
        if (tempPlugins != null) {
//cull out duplicate entries, since single entries and directory adds
// can result in duplicates not specified strictly at UI level
            properties.setProperty("plugins",
		cullDuplicates(tempPlugins + "," + newPlugin));
        } else {
            properties.setProperty("plugins", newPlugin);
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
	// deletion a little weird - multiple selection deletes not working
	// right - restricting via setting Tables to use single selection

	// remove deleted plugin from plugins=... property
        String tempPlugins = properties.getProperty("plugins");
	for (int k = 0; k < deletedPlugins.length; k++) {
	  String value = deletedPlugins[k];
          if (value.startsWith("file:"))
            value = value.substring(5);
          else if (value.startsWith("jar:"))
            value = value.substring(4);
          else if (value.startsWith("http\\:"))	// don't want the backslash
            value = value.substring(0,3) + value.substring(5);
						// NB: \\ is escaped "\"
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

	// and remove deleted plugins from TreeSet for model and table
          pluginsSet.remove(value);
	}
    }

    // add plugin to table and properties object
    // accept single plugin as String, or comma-seperated String of plugins
    public void addPlugin(String pluginString) {

	String[] plugin = pluginString.split(",");
	for ( int i = 0; i < plugin.length; i++ ) {

	  URL url; 
	  try {
	    if ( plugin[i].startsWith("http") ) {
            plugin[i] = "jar:"+plugin[i]+"!/";
            url = new URL( plugin[i] );
            } else if ( plugin[i].startsWith("file") ||
					plugin[i].startsWith("jar")) {
	    // do no massaging of string, just create URL
            url = new URL( plugin[i] );
            } else {
            url = new URL( "file", "", plugin[i] );
            }
            addPlugin(url);
	  } catch ( Exception ue ) {
            System.err.println("Error: cannot construct URL from: "+ plugin[i]);
          }
	}
    }
    
    public void addPlugin(URL u) {
        if (u!= null)  {
	    // get string info (no protocol) for insertion into table
	    // and plugins=... property
	    String path = u.getPath();
	    // strip off trailing "!/" for JAR URLs
	    if (path.endsWith("!/"))
		path = path.substring(0,path.length()-2);

            pluginsSet.add(path);	// add to TreeSet for model and table
	    addPluginToPropertyString(path); // also add to plugins=... string
					    //   in private properties object
	}
    }

    public String getColumnName(int col) { return columnHeader[col]; }
    
    public void save(Properties saveToProps) {
	// save local property values to passed-in Properties
	saveToProps.putAll(properties);
	// mark these plugins loaded from the command line as loaded
	// and saved, so no need to reparse, etc.
	pluginsFromCommandLineLoadedAndSaved = true;
    }

    public void restore(Properties restoreFromProps) {
	properties.clear();
	if (restoreFromProps.getProperty("plugins") != null) {
	    properties.put("plugins",restoreFromProps.getProperty("plugins"));
	    loadProperties();	// now get pluginsSet populated from properties
	}
    }

    public int getColumnCount() {
        return columnHeader.length;
    }

    public Object getValueAt(int row, int col) {
	Object retVal = new String("");
	int index=0;
	for (Iterator it=pluginsSet.iterator(); it.hasNext(); ) {
	    retVal = it.next();
	    if (index == row) 
		break;
	    index++;
	}
        return retVal;
    }

    public int getRowCount() {
        return pluginsSet.size();
    }

}
