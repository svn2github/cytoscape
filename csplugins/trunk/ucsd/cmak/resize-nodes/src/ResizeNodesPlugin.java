package ucsd.cmak.ResizeNodes;

import java.util.Iterator;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JMenu;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.CytoscapeObj;
import cytoscape.CyNode;

import cytoscape.giny.CytoscapeRootGraph;
import cytoscape.plugin.CytoscapePlugin;

/**
 **
 ** A plugin to adjust the size of nodes based on a node attribute.
 ** Add new JMenu items if you want to use different node attrs.
 ** Eventually, we may want to select the attribute to use in the UI.
 ** <p>
 ** The value of the node attribute is split using the "\n" [newline]
 ** character into an array of values.  The longest value is used
 ** to set the node width.  The number of values is used to set the
 ** node height.
 ** <p>
 ** <b>Usage</b><p>
 ** Running this plugin creates 2 nodes attributes: NodeHeight and NodeWidth.
 ** Use a PassthroughMapper to map these to the NodeSize visual property.
 ** <p>
 ** NOTE: The NodeHeight attribute is only created if there is more than
 ** 1 attribute value for a node.  The NodeWidth is only created if the
 ** longest name is contains more than 3 characters.
 ** <p>
 ** Plugin code-structure was borrowed from MergeEquivalentNodes.
 **
 ** @author cmak, April 13, 2005
 */
public class ResizeNodesPlugin extends CytoscapePlugin {
    
    CyNetwork network;
    CytoscapeObj cyObj;
    boolean DEBUG=false;

    /**
     * This constructor saves the cyWindow argument (the window to which this
     * plugin is attached) and adds an item to the operations menu.
     */
    public ResizeNodesPlugin() {
	this.cyObj = Cytoscape.getCytoscapeObj();
	JMenu resizeMenu = new JMenu("Resize Nodes");
	resizeMenu.add( new ResizeSelectedAction("NodeName") );
	resizeMenu.add( new ResizeAllAction("NodeName") );
	resizeMenu.add( new ResizeSelectedAction("canonicalName") );
	resizeMenu.add( new ResizeAllAction("canonicalName") );
        resizeMenu.add( new ResizeSelectedAction("commonName") );
	resizeMenu.add( new ResizeAllAction("commonName") );
        
	Cytoscape.getDesktop().getCyMenus().getOperationsMenu().
	    add( resizeMenu );
    }
    

    /**
     * This class gets attached to the menu item.
     */
    public class ResizeAllAction extends AbstractAction {

        private String attr;
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ResizeAllAction(String attrToUse)
        {
            super("Resize all nodes [" + attrToUse + "]");
            attr = attrToUse;
        }
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Resize all nodes using [" + attr + "] attribute");
            return sb.toString();
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
	    network = Cytoscape.getCurrentNetwork();
            if (network == null) {return;}
	    Thread t = new ResizeAllNodesThread(attr);
	    t.start();
	}
    }

    /**
     * This class gets attached to the menu item.
     */
    public class ResizeSelectedAction extends AbstractAction {

        private String attr;
        
        /**
         * The constructor sets the text that should appear on the menu item.
         */
        public ResizeSelectedAction(String attrToUse)
        {
            super("Resize selected nodes [" + attrToUse + "]");
            attr = attrToUse;
        }
        
        /**
         * Gives a description of this plugin.
         */
        public String describe() {
            StringBuffer sb = new StringBuffer();
            sb.append("Resize selected nodes using [" + attr + "] attribute");
            return sb.toString();
        }
        
        /**
         * This method is called when the user selects the menu item.
         */
        public void actionPerformed(ActionEvent ae) {
	    network = Cytoscape.getCurrentNetwork();
            if (network == null) {return;}
	    Thread t = new ResizeSelectedNodesThread(attr);
	    t.start();
	}
    }

    class ResizeSelectedNodesThread extends Thread{
	// just like ResizeAllNodesThread below but no 
	// need to compute the nodes to collapse

        private String attr;
        
        ResizeSelectedNodesThread(String attributeToUse)
        {
            attr = attributeToUse;
        }
        
	public void run(){
	    System.err.println("Starting ResizeSelectedNodes plugin: " + attr);

            CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
	    
	    // get selected nodes
	    int [] flaggedNodesGP = network.getFlaggedNodeIndicesArray();

	    for (int i=0; i<flaggedNodesGP.length; i++)
            {
                int nodeRG = network.getRootGraphNodeIndex(flaggedNodesGP[i]);
                adjustNodeSize(attr, nodeRG, rootGraph);
            }

            System.err.println("Finished ResizeSelectedNodes: " + attr );
	}
    }

    class ResizeAllNodesThread extends Thread{

        private String attr;
        
        ResizeAllNodesThread(String attributeToUse)
        {
            attr = attributeToUse;
        }

        
	public void run(){
	    System.err.println("Starting ResizeAllNodes plugin:" + attr);

	    CytoscapeRootGraph rootGraph = Cytoscape.getRootGraph();
	    
            // adjust node sizes
            for (Iterator nIt=network.nodesIterator(); nIt.hasNext();) {
		int thisNodeGP = network.getIndex((CyNode)nIt.next());
		int thisNodeRG = network.getRootGraphNodeIndex(thisNodeGP);
		
                adjustNodeSize(attr, thisNodeRG, rootGraph);
            }

            System.err.println("Finished ResizeAllNodes: " + attr);
	}
        
    } // end mergeEquivalentNodesThread


    /**
     * Adjust the size of the node based on the "NodeName" attribute
     * (if present), otherwise use the "canonicalName" attribute.
     *
     * NOTE: the size multipliers (eg 7, 16, 20) are probably dependent
     * of the font size used.  These multipliers were determined by
     * trial and error.
     *
     * @param attr the node attribute to use to resize the node
     * @param nodeIndex root graph index of a node
     *
     * @author cmak
     */
    private void adjustNodeSize(String attr,
                                int nodeIndex,
                                CytoscapeRootGraph rootGraph)
    {
        giny.model.Node node = rootGraph.getNode(nodeIndex);
        Object val = network.getNodeAttributeValue(node, attr);
        
        if(val != null)
        {
            String name = String.valueOf(val);
            String[] nameArray = name.split("\n");
            String longest = findLongestName(nameArray);

            if(DEBUG)
                System.err.println("longest [" + attr + "] = " + longest);
            
            // If the longest name has fewer than 6 chars,
            // the node will be the default width.
            if(longest.length() > 5)
            {
                network.setNodeAttributeValue(node, "NodeWidth",
                                              new Double(7 * longest.length()));
            }
            else if(longest.length() > 3)
            {
                network.setNodeAttributeValue(node, "NodeWidth",
                                              new Double(10 * longest.length()));
            }
            
            if(nameArray.length > 5)
            {
                network.setNodeAttributeValue(node, "NodeHeight",
                                              new Double(16 * nameArray.length));
            }
            // If there is only 1 name, the node will be the default height
            else if(nameArray.length > 1)
            {
                network.setNodeAttributeValue(node, "NodeHeight",
                                              new Double(20 * nameArray.length));
            }
        }
    }


    private String findLongestName(String[] values)
    {
        int max = 0;
        String longest = "";
        for (int x=0; x < values.length; x++) {
	    String s = values[x];

            if(s.length() > max)
            {
                longest = s;
                max = s.length();
            }
        }

        return longest;
    }
}


