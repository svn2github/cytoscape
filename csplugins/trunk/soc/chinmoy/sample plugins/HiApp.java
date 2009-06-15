package testplugin;

import javax.swing.JOptionPane;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;


public class HiApp extends CytoscapePlugin{
	
	public HiApp(){
		   String message = "Hello World!Hello World!Hello World!Hello World!Hello World!Hello World!Hello World!Hello World!Hello World!";
	        //System.out.println(message);
	        JOptionPane.showMessageDialog(Cytoscape.getDesktop(), message);		
	}
	}
