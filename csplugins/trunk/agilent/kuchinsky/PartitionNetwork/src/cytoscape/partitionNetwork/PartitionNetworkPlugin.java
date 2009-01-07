package cytoscape.partitionNetwork;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;
import cytoscape.plugin.CytoscapePlugin;
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
 *   6. see CyNetworkNaming.getSuggestedSubnetworkTitle(current_network) for how to name the subnets
 * 
 */
public class PartitionNetworkPlugin extends CytoscapePlugin {

		private ArrayList<Object> nodeAttributeValues = setupNodeAttributeValues();
		private static final String attributeName = "annotation.GO BIOLOGICAL_PROCESS";
		private HashMap<Object, List<Node>> attributeValueNodeMap = new HashMap<Object, List<Node>>();
		private List<CyNetworkView> views = new ArrayList();
		

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
			list.add("cell death");
			list.add("cytoskeleton organization and biogenesis");
			list.add("protein metabolism");
			list.add("DNA metabolic process");
			list.add("RNA metabolic process");
			list.add("carbohydrate metabolic process");
			list.add("transcription");
			list.add("metabolism");
			list.add("stress response");
			list.add("transport");
			list.add("developmental processes");
			list.add("signal transduction");
			list.add("response to stress");
			list.add("response to biotic stimulus");
			list.add("protein transport");
			list.add("lipid metabolic process");
			list.add("ion transport");
			list.add("embryonic development");
			list.add("cell growth");
			list.add("cell differentiation");
	 
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
				populateNodes(attributeName);
		      
		      Set<Object> attributeValues = attributeValueNodeMap.keySet();
//		      System.out.println ("building subnets for attribute key set: " + attributeValues);
		      CyNetwork net = Cytoscape.getCurrentNetwork();
		      for (Object val : attributeValues)
		      {
//		    	  System.out.println("building subnet for attribute value: " + val.toString());
		    	  buildSubNetwork(net, val.toString());
		      }
		      
		      tileNetworkViews(); // tile and fit content in each view
		      
			}

	
			public void populateNodes (String attributeName) {
				
					
				CyAttributes attribs = Cytoscape.getNodeAttributes();
				Iterator<Node> it = Cytoscape.getCurrentNetwork().nodesIterator();
				List<Node> selectedNodes;
	
				while (it.hasNext()) {
	
					Node node = it.next();
					String val = null;
					String terms[] = new String[1];
					// add support for parsing List type attributes
					if (attribs.getType(attributeName) == CyAttributes.TYPE_SIMPLE_LIST) {
						List valList = attribs.getListAttribute(node.getIdentifier(),
								attributeName);
//						System.out.println ("Got values for node: " + node + " = " + valList);
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
//							System.out.println ("checking node value " + val + " against " + o.toString());
							if (val.indexOf(o.toString()) >= 0) {
								selectedNodes = attributeValueNodeMap.get(o);
								if (selectedNodes == null)
								{
									selectedNodes = new ArrayList<Node>();
									selectedNodes.add(node);
									attributeValueNodeMap.put(o.toString(), selectedNodes);
								}
								else if (!selectedNodes.contains(node))
								{
									selectedNodes.add(node);
									attributeValueNodeMap.put(o.toString(), selectedNodes);
								}
//								System.out.println ("selected nodes for value: " + o.toString() + " = " + 
//										selectedNodes);
							}
						}
					} 
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
			public void buildSubNetwork (CyNetwork current_network, String attributeValue) {
	
				CyNetworkView current_network_view = null;

				if (Cytoscape.viewExists(current_network.getIdentifier())) {
					current_network_view = Cytoscape.getNetworkView(current_network.getIdentifier());
				} // end of if ()

				List nodes = attributeValueNodeMap.get(attributeValue);
//				System.out.println("Got nodes for attributeValue: " + attributeValue + " = " + nodes);
				if (nodes == null)
				{
					return;
				}

				CyNetwork new_network = Cytoscape.createNetwork(nodes,
				                                                current_network.getConnectingEdges(new ArrayList(nodes)),
				                                     //           CyNetworkNaming.getSuggestedSubnetworkTitle(current_network),
				                                                attributeValue, // for network title
				                                                current_network);

				CyNetworkView new_view = Cytoscape.getNetworkView(new_network.getIdentifier());

				if (new_view == Cytoscape.getNullNetworkView()) {
					return;
				}
				
				views.add(new_view);

		        String vsName = "default";
		        
		        
		        
		        // apply layout
				if (current_network_view != Cytoscape.getNullNetworkView()) {
					Iterator i = new_network.nodesIterator();

//					while (i.hasNext()) {
//						Node node = (Node) i.next();
//						new_view.getNodeView(node)
//						        .setOffset(current_network_view.getNodeView(node).getXPosition(),
//						                   current_network_view.getNodeView(node).getYPosition());
//					}
//
//					new_view.fitContent();
					
//					CyLayoutAlgorithm layout = CyLayouts.getLayout("force-directed");
					CyLayoutAlgorithm layout = CyLayouts.getLayout("cellular-layout");
					layout.doLayout(new_view);

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
						      // finally loop through the network views and fitContent
						      for (CyNetworkView view : views)
						      {
						    	  Cytoscape.setCurrentNetworkView(view.getIdentifier());
						    	  view.fitContent();
						      }
						      
						}
					});
			}
			
	
			
		}
				
				
}
