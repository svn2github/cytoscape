/**
 * 
 */
package modulefinder;

import java.awt.event.*;
import javax.swing.*;

import javax.swing.*;
import java.util.*;
import java.io.*;

import junit.framework.*;   

import cytoscape.plugin.*;
import cytoscape.*;

/*import cytoscape.view.*;
import cytoscape.*;
import cytoscape.data.*;
import cytoscape.data.servers.*;
import cytoscape.data.readers.*;
import cytoscape.plugin.*;
import csplugins.jActiveModules.data.*;
import csplugins.jActiveModules.dialogs.*;
*/

/**
 * @author alee
 * Now, this is just for the interface with cytoscape, later it will be filled with UI code
 */

public class ModuleFinderUI extends CytoscapePlugin {

	/**
	 * @param args
	 */
	public ModuleFinder moduleFinder = null;
	public ModuleFinderParams mfParams = null;
	
	public ModuleFinderUI() {
		System.out.println("Starting ModuleFinder plugin!\n");

		// adding menu for modulefinder plugin
		JMenu topMenu = new JMenu("ModuleFinder");
	    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(topMenu);

		mfParams = new ModuleFinderParams();

		moduleFinder = new ModuleFinder(Cytoscape.getCurrentNetwork(), mfParams);
		Thread t = new Thread(moduleFinder);
		t.start();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
