package cytoscape.partitionNetwork;

import giny.model.Node;
import giny.view.NodeView;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CyNetworkNaming;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyDesktopManager;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.VisualStyle;


/**
 * PartitionNetworkPlugin
 * @author Allan Kuchinsky, Agilent Technologies
 *
 * Partitions a network into several subnetworks based upon attribute value of nodes
 * Notes:
 *   1. hard coded to use GO biological process, to be used in conjunction with Cellular Layout (aka BubbleRouter)
 *   2. work from a user-configured list of GO biological process values
 *   3. leverage from filter code to select according to attribute value (also look at BubbleRouter.populateNodeViews)
 *   4. leverage from code for Network -> New -> from selected Nodes, all edges to build subnet
 *   5. leverage from view code that lays out networks in a tiled manner
 * 
 */
public class PartitionNetworkPlugin extends CytoscapePlugin {

		private static MultiHashMap _attribute_map = Cytoscape.getEdgeAttributes().getMultiHashMap();
		
		private ArrayList<Object> nodeAttributeValues = setupNodeAttributeValues();

		

		public PartitionNetworkPlugin () {
			PartitionNetworkAction mainAction = new PartitionNetworkAction ();
			Cytoscape.getDesktop().getCyMenus().addAction(mainAction);

		}
		
		/**
		 * read in attribute values from a user configurable file
		 * Phase 1: hardcode a list of categories
		 * @return
		 */
		public ArrayList<Object> setupNodeAttributeValues()
		{
			ArrayList<Object> list = new ArrayList ();
			list.add("cell adhesion"); 
			list.add("cell-cell signaling"); 
			list.add("cell cycle");
			list.add("cell proliferation");
			list.add("death");
			list.add("cell organization and biogenesis");
			list.add("protein metabolism");
			list.add("DNA metabolism");
			list.add("RNA metabolism");
			list.add("transcription");
			list.add("metabolism");
			list.add("stress response");
			list.add("transport");
			list.add("developmental processes");
			list.add("signal transduction");
	 
			return list;
		}
		

		// ~ Inner Classes
		// //////////////////////////////////////////////////////////

		public class PartitionNetworkAction extends CytoscapeAction {


			public PartitionNetworkAction() {
				super("Partition Network by GO biological process ...");
				setPreferredMenu("Layout");

			}


			public void actionPerformed(ActionEvent e) {
		      
			      CyAttributes nodeAttrs = Cytoscape.getNodeAttributes();
			      CyAttributes edgeAttrs = Cytoscape.getEdgeAttributes();
			      CyNetwork net = Cytoscape.getCurrentNetwork();
			      
			      Node otherNode;
			      
			      // list of unique sources for each node
			      int nbrSources;
			      List<String> sources;
				
			      Iterator nodes = net.nodesIterator();
			      while (nodes.hasNext())
			      {
			    	  
			      }
			}

	
			public void populateNodes (String attributeName) {
				Comparator<Object> comparator = new Comparator<Object>() {
					public int compare(Object o1, Object o2) {
						return o1.toString().compareToIgnoreCase(o2.toString());
					}
				};
				SortedSet<Object> selectedNodes = new TreeSet<Object>(comparator);
				CyAttributes attribs = Cytoscape.getNodeAttributes();
				Iterator it = Cytoscape.getCurrentNetwork().nodesIterator();
				List<NodeView> nodeViews = new ArrayList<NodeView>();
				while (it.hasNext()) {
					Cytoscape.getCurrentNetwork().unselectAllNodes();
					Node node = (Node) it.next();
					nodeViews.add(Cytoscape.getCurrentNetworkView().getNodeView(node));
					String val = null;
					String terms[] = new String[1];
					// add support for parsing List type attributes
					if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
						List valList = attribs.getListAttribute(node.getIdentifier(),
								attributeName);
						// iterate through all elements in the list
						if (valList != null && valList.size() > 0) {
							terms = new String[valList.size()];
							for (int i = 0; i < valList.size(); i++) {
								Object o = valList.get(i);
								terms[i] = o.toString();
							}
						}
						val = join(terms);
					} else {
						String valCheck = attribs.getStringAttribute(node
								.getIdentifier(), attributeName);
						if (valCheck != null && !valCheck.equals("")) {
							val = valCheck;
						}
					}

					// loop through elements in array below and match
					if ((!(val == null) && (!val.equals("null")) && (val.length() > 0))) {
						for (Object o : nodeAttributeValues) {
							if (val.indexOf(o.toString()) >= 0) {
								selectedNodes.add(node);
							}

						}
					} else if (nodeAttributeValues.get(0).equals("unassigned")) {
						selectedNodes.add(node);
					}

				}

				Cytoscape.getCurrentNetwork().setSelectedNodeState(selectedNodes, true);
				System.out.println("Selected " + selectedNodes.size()
						+ " nodes for layout in "
						+ nodeAttributeValues.toString());

				// only run layout if some nodes are selected
				if (selectedNodes.size() > 0) {
				}
			}
			
			
			
			private String join(String values[]) {
				StringBuffer buf = new StringBuffer();
				for (int i = 0; i < values.length; i++) {
					buf.append(values[i]);
					if (i < values.length - 1) {
						buf.append(", ");
					}
				}
				return buf.toString();
			}
			
			/**
			 * build a subnetwork for selected nodes
			 * leverages from cytoscape.actions.NewWindowSelectedNodesOnlyAction
			 * @param current_network
			 */
			public void buildSubNetwork (CyNetwork current_network) {

				if ((current_network == null) || (current_network == Cytoscape.getNullNetwork()))
					return;

				CyNetworkView current_network_view = null;

				if (Cytoscape.viewExists(current_network.getIdentifier())) {
					current_network_view = Cytoscape.getNetworkView(current_network.getIdentifier());
				} // end of if ()

				Set nodes = current_network.getSelectedNodes();

				CyNetwork new_network = Cytoscape.createNetwork(nodes,
				                                                current_network.getConnectingEdges(new ArrayList(nodes)),
				                                                CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				                                                current_network);

				CyNetworkView new_view = Cytoscape.getNetworkView(new_network.getIdentifier());

				if (new_view == Cytoscape.getNullNetworkView()) {
					return;
				}

		        String vsName = "default";
		        
		        // keep the node positions
				if (current_network_view != Cytoscape.getNullNetworkView()) {
					Iterator i = new_network.nodesIterator();

					while (i.hasNext()) {
						Node node = (Node) i.next();
						new_view.getNodeView(node)
						        .setOffset(current_network_view.getNodeView(node).getXPosition(),
						                   current_network_view.getNodeView(node).getYPosition());
					}

					new_view.fitContent();

					// Set visual style
					VisualStyle newVS = current_network_view.getVisualStyle();

					if (newVS != null) {
		                vsName = newVS.getName();
					}
				}
		        Cytoscape.getVisualMappingManager().setVisualStyle(vsName);
			}

			
			/**
			 * layout the subnetwork views in a grid
			 */
			public void tileNetworkViews() {
				SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							CyDesktopManager.arrangeFrames(CyDesktopManager.Arrange.GRID);
						}
					});
			}
			
	
			
		}
				
				
}
