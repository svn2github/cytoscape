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

import giny.view.NodeView;
import giny.view.Label;

import java.awt.event.ActionEvent;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import java.lang.reflect.Field;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.jna.*;

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

 	// Make sure libraries are extracted in the plugins folder
	checkLib("igraphWrapper");
	checkLib("igraph.0");

        String userDir = System.getProperty("user.dir"); 
// 	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "user dir:"+ userDir);	
 	NativeLibrary.addSearchPath("igraphWrapper", userDir + "/plugins");

// 	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), IgraphInterface.nativeAdd(10, 20));	   

	// Create Igraph object
	IgraphAPI igraph = new IgraphAPI();

	// Add elements in menu toolbar
	IgraphAPI.IsConnected isConnectedAction1 = igraph.new IsConnected(this, "All nodes", false);
	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) isConnectedAction1);

	IgraphAPI.IsConnected isConnectedAction2 = igraph.new IsConnected(this, "Selected Nodes", true);
	Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) isConnectedAction2);

	// Layouts
	CyLayouts.addLayout(new CircleLayout()             , "Igraph");
	CyLayouts.addLayout(new StarLayout()               , "Igraph");
	CyLayouts.addLayout(new FruchtermanReingoldLayout(), "Igraph");

		
    }

    private boolean isOldVersion(){
	return false;
    }

    private void checkLib(String lib) {
    // TODO: Make this cross-platform
	File dynamicLib = new File ("./plugins/lib" + lib + ".dylib");
	if (!dynamicLib.exists() || isOldVersion()){
	    String message;	    
	    try {
		String home = getClass().getProtectionDomain().
		    getCodeSource().getLocation().toString().
		    substring(6);
		JarFile jar = new JarFile("./plugins/igraphPlugin.jar");
		ZipEntry entry = jar.getEntry("lib" + lib + ".dylib");
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

		message = lib + " library extracted!"; 
	    }
	    catch (Exception e) {
		e.printStackTrace();
		message = "Igraph Plugin: Error While extracting library from jar file:\n" + e.getMessage(); 
	    }

	    //Show message
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);	   
	}
	return;
    } // checkLib


    protected boolean loadIgraph() {

	boolean res = true;

	// Reset the "sys_paths" field of the ClassLoader to null.
	Class clazz = ClassLoader.class;
	Field field;
	try {
	    field = clazz.getDeclaredField("sys_paths");
	    boolean accessible = field.isAccessible();
	    if (!accessible)
		field.setAccessible(true);
	    Object original = field.get(clazz);

	    // Get original PATH
	    String orig_path = System.getProperty("java.library.path");
	    
	    // Reset it to null so that whenever "System.loadLibrary" is called, it will be reconstructed with the changed value
	    field.set(clazz, null);
	    try {
		// Change the value and load the library.
		System.setProperty("java.library.path", "./plugins"  + ":" + orig_path);
		//		System.loadLibrary("igraph.0");
		System.loadLibrary("igraphWrapper");
	    }

	    catch (UnsatisfiedLinkError error) {
		String message = "Problem detected while loading library.\n"		    
		    + error.getMessage() 
		    + "\nPlease check your plugins folder.";
		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);
				
		res = false;
	    }		

	    finally {
		// Revert back the changes
		field.set(clazz, original);
		field.setAccessible(accessible);   
	    }
	}
	catch (Exception exception) {
	    res = false;
	}

	finally {
	    return res;
	}
    }
        
}