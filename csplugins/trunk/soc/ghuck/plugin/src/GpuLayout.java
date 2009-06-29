/**************************************************************************************
Copyright (C) Apeksha Godiyal, 2008
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


import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.layout.CyLayouts;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.AbstractLayout;
import cytoscape.layout.LayoutProperties;
import cytoscape.layout.Tunable;

import giny.model.GraphPerspective;
import giny.model.Node;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.Iterator;








/**
 * This plugin provides a GPU assited graph layout utility by calling CUDA C++ code
 */
public class GpuLayout extends CytoscapePlugin {
	
    private int coarseGraphSize         = 50;
    private int interpolationIterations = 50;
    private int levelConvergence        = 2;
    private int edgeLen                 = 5;
    private int initialNoIterations     = 300;

    private int groupcount = 2;

    private LayoutProperties layoutProperties;

	
    /**
     * 
     */
    public GpuLayout() {
	//  Show message on screen    
	String message = "GPU Graph Layout Loaded!\n" 
	    +  coarseGraphSize + "\n" 
	    + interpolationIterations + "\n"  
	    + levelConvergence + "\n"         
	    + edgeLen + "\n"                  
	    + initialNoIterations; 
	// Use the CytoscapeDesktop as parent for a Swing dialog
	JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	
	// Add Layout to menu
	CyLayouts.addLayout(new ForceDirected(), "GPU Assisted Layout");
    }
    


    class ForceDirected extends AbstractLayout{

	/**
	 * Creates a new layout object.
	 */
	public ForceDirected() {
	    super();
	    layoutProperties = new LayoutProperties(getName());
	    initialize_properties();
	}

	/**
	 * Adds tunable objects for adjusting plugin parameters
	 * Initializes default values for those parameters
	 */
	protected void initialize_properties() {
	    
	    // Add new properties to layout 
	    layoutProperties.add(new Tunable("coarseGraphSize", "Coarse Graph Size "               , Tunable.INTEGER, new Integer(50) ));
	    layoutProperties.add(new Tunable("interpolationIterations", "Interpolation Iterations ", Tunable.INTEGER, new Integer(50) ));
	    layoutProperties.add(new Tunable("levelConvergence", "Level Convergence "              , Tunable.INTEGER, new Integer(2)  ));
	    layoutProperties.add(new Tunable("edgeLen", "Ideal Edge Length "                       , Tunable.INTEGER, new Integer(5)  ));
	    layoutProperties.add(new Tunable("initialNoIterations", "Initial Number of Iterations ", Tunable.INTEGER, new Integer(300)));
	    
	    // Initialize layout properties
	    layoutProperties.initializeProperties();
	    
	    // Force the settings update
	    updateSettings(true);
	}

	/**
	 * getName is used to construct property strings
	 * for this layout.
	 */
	public  String getName() {
	    return "Force Directed GPU Layout";
	}
	
	/**
	 * toString is used to get the user-visible name
	 * of the layout
	 */
	public  String toString(){
	    return "Force Directed Layout";
	}

	/**
	 *  
	 */
	public void updateSettings() {
	    updateSettings(false);
	}
	
	/**
	 * 
	 */
	public void updateSettings(boolean force) {
	    layoutProperties.updateValues();
	    	    
	    // Get coarseGraphSize
	    Tunable t1 = layoutProperties.get("coarseGraphSize");
	    if ((t1 != null) && (t1.valueChanged() || force))
		coarseGraphSize = ((Integer) t1.getValue()).intValue();
	    
	    // Get interpolationIterations
	    Tunable t2 = layoutProperties.get("interpolationIterations");
	    if ((t2 != null) && (t2.valueChanged() || force))
		interpolationIterations = ((Integer) t2.getValue()).intValue();
	    
	    // Get levelConvergence
	    Tunable t3 = layoutProperties.get("levelConvergence");
	    if ((t3 != null) && (t3.valueChanged() || force))
		levelConvergence = ((Integer) t3.getValue()).intValue();
	    
	    // Get edgeLen
	    Tunable t4 = layoutProperties.get("edgeLen");
	    if ((t4 != null) && (t4.valueChanged() || force))
		edgeLen = ((Integer) t4.getValue()).intValue();
	    
	    // Get initialNoIterations
	    Tunable t5 = layoutProperties.get("initialNoIterations");
	    if ((t5 != null) && (t5.valueChanged() || force))
		initialNoIterations = ((Integer) t5.getValue()).intValue();
	    
	    //  Show message on screen    
	    String message = "Preferences updated\n" 
		+  coarseGraphSize + "\n" 
		+ interpolationIterations + "\n"  
		+ levelConvergence + "\n"         
		+ edgeLen + "\n"                  
		+ initialNoIterations; 

	    // Use the CytoscapeDesktop as parent for a Swing dialog
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	    
	}
	
	/**
	 * Get the settings panel for this layout
	 */
	public JPanel getSettingsPanel() {
	    JPanel panel = new JPanel(new GridLayout(0, 5));
	    panel.add(layoutProperties.getTunablePanel());
	    
	    return panel;
	}
	
	
	/**
	 *  
	 */
	public void revertSettings() {
	    layoutProperties.revertProperties();
	}
	
	public LayoutProperties getSettings() {
	    return layoutProperties;
	}
	

	/**
	 *  
	 */
	public void construct() {

	    taskMonitor.setStatus("Initializing");
	    initialize(); // Calls initialize_local	      
	    
	    //  Show message on screen    
	    String message = "Calculating Layout..."; 

	    // Use the CytoscapeDesktop as parent for a Swing dialog
	    JOptionPane.showMessageDialog( Cytoscape.getDesktop(), message);
	    
	    Iterator<Node> it = network.nodesIterator();
	    
	    while (it.hasNext()) {
		if (canceled)
		    return;
		
		double x = group_center_x[group_id] + (Math.random()-0.5)*group_width;
				
		Node node = (Node) it.next();
		
		//System.out.println(group_id);
		
		networkView.getNodeView(node).setXPosition(x);
			}
	}
	
	private double maxwidth = 5000.0;
	
    }
}








