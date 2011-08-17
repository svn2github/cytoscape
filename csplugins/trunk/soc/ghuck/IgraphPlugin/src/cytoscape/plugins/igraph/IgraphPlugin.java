/**************************************************************************************
Copyright (C) Gerardo Huck, 2011


This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

**************************************************************************************/

package cytoscape.plugins.igraph;

import cytoscape.plugins.igraph.layout.*;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import java.lang.reflect.Field;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.jna.*;
import java.net.URL;
import java.net.URLClassLoader;

/** ---------------------------IgraphPlugin-----------------------------
 * This plugin allows to call some of igraph functions from Cytoscape
 * @author Gerardo Huck
 *
 */
public class IgraphPlugin extends CytoscapePlugin {
	
    /**
     * 
     */
    public IgraphPlugin() {

	extractFileFromJar("libigraphWrapper.dylib");

 	// Make sure libraries are extracted in the plugins folder
	if (extractFileFromJar("jna.jar")) {
	    JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Igraph plugin succesfully installed!\nIt will be available the next time you run Cytoscape.");
	    return;
	}

	String userDir = System.getProperty("user.dir"); 
// 	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "user dir:"+ userDir);	
 	
	try {
	    NativeLibrary.addSearchPath("igraphWrapper", userDir + "/plugins");
	    //JOptionPane.showMessageDialog(Cytoscape.getDesktop(), IgraphInterface.nativeAdd(10, 20));	   
	    	    
	    // Add elements in menu toolbar
	    IsConnected isConnectedAction1 = new IsConnected(this, "All nodes", false);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) isConnectedAction1);
	    
	    IsConnected isConnectedAction2 = new IsConnected(this, "Selected Nodes", true);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) isConnectedAction2);

	    MinimumSpanningTree mst = new MinimumSpanningTree(this, "All nodes", false, false);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) mst);

	    MinimumSpanningTree mst2 = new MinimumSpanningTree(this, "Selected Nodes", true, false);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) mst2);

	    MinimumSpanningTree mst3 = new MinimumSpanningTree(this, "All nodes", false, true);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) mst3);

	    MinimumSpanningTree mst4 = new MinimumSpanningTree(this, "Selected Nodes", true, true);
	    Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) mst4);
	    
	    // Layouts
	    CyLayouts.addLayout(new CircleLayout(),                       "Igraph");
	    CyLayouts.addLayout(new StarLayout(),                         "Igraph");
	    CyLayouts.addLayout(new FruchtermanReingoldLayout(true),      "Igraph");
	    CyLayouts.addLayout(new FruchtermanReingoldGridLayout(true),  "Igraph");
	    CyLayouts.addLayout(new LGLLayout(),                          "Igraph");
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    String message = "Error while initializing Igraph Plugin:\n" + e.getMessage(); 
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	}       
    }

    private boolean isOldVersion(){
	return false;
    }

    /**
     * @return true if file was extracted, false otherwise
     */
    private boolean extractFileFromJar(String fileName) {
    // TODO: Make this cross-platform
	File file = new File ("./plugins/" + fileName);
	boolean ret = false;
	if (!file.exists() || isOldVersion()){
	    try {
		JarFile jar = new JarFile("./plugins/igraphPlugin.jar");
		ZipEntry entry = jar.getEntry(fileName);
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

		ret = true;
	    }
	    catch (Exception e) {
		e.printStackTrace();
		String message = "Igraph Plugin: Error While extracting file from igraph plugin jar :\n" + e.getMessage(); 
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
	    }

	    
	}
	return ret;
    } // extractFileFromJar
        
}