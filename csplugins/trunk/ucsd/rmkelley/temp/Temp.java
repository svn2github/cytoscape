package ucsd.rmkelley.Temp;
import java.util.*;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;


/**
 * This is a sample Cytoscape plugin using Giny graph structures. For each
 * currently selected node in the graph view, the action method of this plugin
 * additionally selects the neighbors of that node if their canonical name ends
 * with the same letter. (For yeast genes, whose names are of the form 'YOR167C',
 * this selects genes that are on the same DNA strand). This operation was
 * chosen to be illustrative, not necessarily useful.
 *
 * Note that selection is a property of the view of the graph, while neighbors
 * are a property of the graph itself. Thus this plugin must access both the
 * graph and its view.
 */
public class Temp extends CytoscapePlugin{
  /**
   * This constructor saves the cyWindow argument (the window to which this
   * plugin is attached) and adds an item to the operations menu.
   */
		public Temp(){
    Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add( new TestAction() );
  }
    
   

  public class TestAction extends AbstractAction{
    
    public TestAction() {super("Test Action");}
    
    /**
     * This method is called when the user selects the menu item.
     */
    public void actionPerformed(ActionEvent ae) {
								//System.out.println(Cytoscape.getCurrentNetwork()); 
								//System.out.println(Cytoscape.getCurrentNetworkView());	
								CyNetwork myNetwork = Cytoscape.createNetwork("Test network");
								myNetwork.addNode(Cytoscape.getCyNode("Node One",true));
								CyNetworkView myView = Cytoscape.createNetworkView(myNetwork);
								myNetwork.addNode(Cytoscape.getCyNode("Node Two",true));
								//myView.bakeMePie("apple");	
				}
  }
}

