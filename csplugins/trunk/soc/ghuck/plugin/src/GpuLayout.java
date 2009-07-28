/**************************************************************************************
Copyright (C) Gerardo Huck, 2009


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
package GpuLayout;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;
import cytoscape.CyNode;

import giny.model.GraphPerspective;
import giny.model.Node;
import java.io.*;
import java.util.jar.*;
import java.util.zip.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;
import java.lang.reflect.Field;
import GpuLayout.*; 





/**
 * This plugin provides a GPU assited graph layout utility by calling CUDA C++ code
 */
public class GpuLayout extends CytoscapePlugin {

	
    /**
     * Adds a menu entry and creates an instance of ForceDirected
     */
    public GpuLayout() {	

	// Check whether the static library is already extracted in the plugins folder, if not, extract it
	File staticLib = new File ("./plugins/libGpuLayout.so");
	if (!staticLib.exists()){
	    // Extract it
	    try {
		String home = getClass().getProtectionDomain().
                    getCodeSource().getLocation().toString().
                    substring(6);
		JarFile jar = new JarFile("./plugins/GpuLayout.jar");
		ZipEntry entry = jar.getEntry("libGpuLayout.so");
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
	    }
	    catch (Exception e) {
		e.printStackTrace();
		String message2 = "GpuLayout Plugin: Error While extracting Static libraty from jar file:\n" + e.getMessage(); 
		JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message2);
		return;
	    }
	   
	    String message = "First time use of GpuLayout Plugin\nExtracted required library to plugins folder"; 
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	}

	
	// Add Layout to menu
	CyLayouts.addLayout(new ForceDirected(), "GPU Assisted Layout");
	

    }
    

}








