/**
* Copyright (C) Gerardo Huck, 2010
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published 
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*  
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
*  
* You should have received a copy of the GNU Lesser General Public License
* along with this program.  If not, see <http://www.gnu.org/licenses/>.
*
*/

package cytoscape.plugins;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import giny.view.NodeView;
import giny.view.Label;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/** ---------------------------LabelLayoutPlugin-----------------------------
 * Takes the current network and reorganizes it so that the new network is more
 * readable.  This will be done through the repositioning of network labels,
 * and subtle repositioning of nodes.
 * @author Gerardo Huck
 *
 */
public class IgraphPlugin extends CytoscapePlugin {
	
    /**
     * Constructor which adds this layout to Cytoscape Layouts.  This in turn
     * adds it to the Cytoscape menus as well.
     */
    public IgraphPlugin() {
	MyPluginMenuAction menuAction = new MyPluginMenuAction(this);
	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menuAction);

 	// Check whether the static library is already extracted in the plugins folder, if not, extract it
	File dynamicLib = new File ("./plugins/libigraph.dylib");
	if (!dynamicLib.exists() || isOldVersion()){
	    // Extract it
	    extractLib();
	}
	
    }

    private boolean isOldVersion(){
	return true;
    }

    private void extractLib() {
	String message;

	try {
	    String home = getClass().getProtectionDomain().
		getCodeSource().getLocation().toString().
		substring(6);
	    JarFile jar = new JarFile("./plugins/igraphPlugin.jar");
	    ZipEntry entry = jar.getEntry("libigraph.dylib");
	    File efile = new File("./plugins/", entry.getName());
	    
	    InputStream in = 
		new BufferedInputStream(jar.getInputStream(entry));
	    OutputStream out = 
		new BufferedOutputStream(new FileOutputStream(efile));
	    byte[] buffer = new byte[2048];
	    for (;;)  {
		int nBytes = in.read(buffer);
		if (nBytes <= 0) break;
		out.write(buffer, 0, nBytes);
	    }
	    out.flush();
	    out.close();
	    in.close();

	    message = "Igraph library extracted!"; 

	}
	catch (Exception e) {
	    e.printStackTrace();
	    message = "Igraph Plugin: Error While extracting libraty from jar file:\n" + e.getMessage(); 
	}

	//Show message
	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);	   

	return;
    }
        
    public class MyPluginMenuAction extends CytoscapeAction {

	public MyPluginMenuAction(IgraphPlugin myPlugin) {
	    super("Hello World");
	    setPreferredMenu("Plugins");
	}

	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"Hello World is selected!");       
	}
    }       	
}