package csplugins.trp;


import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import giny.model.GraphPerspective;
import giny.model.Node;
import giny.view.GraphView;
import giny.view.NodeView;

import cytoscape.plugin.AbstractPlugin;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;

/**
 * This is a  Cytoscape plugin that is using Giny graph structures. 
 */
public class TRPplugin extends AbstractPlugin {
    
    CyWindow cyWindow;
    
    
    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public TRPplugin(CyWindow cyWindow) {
        this.cyWindow = cyWindow;
	JMenu pluginName = new JMenu( "Pathway Rendering Plugin" );
	pluginName.add( new TRPpluginAction() );
	pluginName.add( new PhosphorylatesAction() );
	pluginName.add( new ComplexAction() );
	
        cyWindow.getCyMenus().getOperationsMenu().add( pluginName) ;
    }
    
    /**
     * This class gets attached to the menu item.
     */
    public class TRPpluginAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public TRPpluginAction() {super("Apply whole TRP rendering");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            return sb.toString();
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
		//get a list of Phosphorylates  and Ubiquinates reactions(edges)
		performPhosphorylates();
		//get a list of Phosphorylates(auto) reactions(edges)
		performPhosAuto();
		//get a list of Formed reactions(edges)
		performFormed();
		//get a list of Dissociated reactions(edges)
		performDissociated();
		
		
            
        }
    }
    
    /**
     * This class gets attached to the menu item.
     */
    public class PhosphorylatesAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public PhosphorylatesAction() {super("Apply Phosphorylated and Umbiquinated rendering");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            return sb.toString();
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
		//get a list of Phosphorylates  and Ubiquinates reactions(edges)
		performPhosphorylates();
		//get a list of Phosphorylates(auto) reactions(edges)
		performPhosAuto();
		
        }
    }
    
     /**
     * This class gets attached to the menu item.
     */
    public class ComplexAction extends AbstractAction {
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ComplexAction() {super("Apply bio complex rendering");}
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            return sb.toString();
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
		performComplex();
		performFormed();
		performDissociated();
        }
    }
	
	void performPhosphorylates() {
		//iterate through the phosphorylated and ubiquinated reactions list
		
			//create Bp node, label
				//if ubiquinated add "Ub"
				//else add "P"
			//create reaction node, label
			
			//delete( or Hide?) old reaction edge
			
			//create new edges:
			//B-R (not directed)
			//R-Bp (directed)
			//A-R  (not directed)
			
			//layout the group
			
	}
	void performPhosAuto() {
		//iterate through the phosphorylated Auto reactions list
		
			//create Bp node, label
				//if ubiquinated add "Ub"
				//else add "P"
			//create middle bend on the edge
			
			//layout the group
	}
	
	void performFormed() {
		//iterate through the Formed reactions list
		
			//create groups with common target nodes
		//end iteration	
			//for each group:
				//create reaction node, label
			
				
				//delete( or Hide?) old reaction edges
				//remove from the hashmap all formed reactions for the target Nodes
			
				//create new edges:
				//From all sources to the reaction ( directed)
				//from reaction to the target (directed)
			
				//layout the complex and the group
			//end for	
	}
	
	void performDissociated() {
		//iterate through the dissociated reactions list
			//create groups with common source nodes
		//end iteration	
		//for each group	
			//create reaction node, label
			
			//delete( or Hide?) old reaction edges
			
			//create new edges:
			//From the source to the reaction ( directed)
			//from reaction to the targets (directed)
			
			//layout the complex and the group
		//end for	
	}
	
	void performComplex() {
	}
  
}//end of class


