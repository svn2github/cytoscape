package bingo.internal;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class which creates the new CyNetwork and CyNetworkView of the 
 * * overrepresented GO graph with accompanying visual style and attributes.     
 **/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JFrame;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyRow;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import bingo.internal.ontology.Ontology;
import bingo.internal.ui.ColorPanel;

/**
 * *****************************************************************
 * DisplaybingoWindow.java ----------------------- Steven Maere & Karel Heymans
 * (c) March 2005
 * <p/>
 * Class which creates the new CyNetwork and CyNetworkView of the
 * overrepresented GO graph.
 * <p/>
 * ******************************************************************
 */

public class DisplayBiNGOWindow {

	private static final String[] NODE_COL_NAMES = { "pValue", "adjustedPValue", "xx", "X", "nn", "N", "description",
			"nodeFillColor", "nodeSize", "nodeType", "nodeFontSize" };

	/**
	 * final defaultsize for the size of the nodes.
	 */
	private static final Double DEFAULT_SIZE = 1d;
	/**
	 * final sizefactor for the size of the nodes.
	 */
	private static final int MAX_SIZE = 50;
	/**
	 * final String for default name sif-file.
	 */
	private static final String SIFFILENAME = "bingo.sif";
	/**
	 * constant string for the checking of numbers of categories, all
	 * categories.
	 */
	private static final String CATEGORY_ALL = BingoAlgorithm.CATEGORY;
	/**
	 * constant string for the checking of numbers of categories, before
	 * correction.
	 */
	private static final String CATEGORY_BEFORE_CORRECTION = BingoAlgorithm.CATEGORY_BEFORE_CORRECTION;

	/**
	 * constant string for the checking of numbers of categories, after
	 * correction.
	 */
	private static final String CATEGORY_CORRECTION = BingoAlgorithm.CATEGORY_CORRECTION;
	/**
	 * scale for BigDecimal
	 */
	private static final int SCALE_RESULT = 100;

	/**
	 * hasmap with key termID and value pvalue.
	 */
	private Map<Integer, String> testMap;

	/**
	 * hasmap with key termID and value corrected pvalue.
	 */
	private Map correctionMap;

	/**
	 * hashmap with key termID and value x.
	 */
	private Map mapSmallX;

	/**
	 * hashmap with key termID and value n.
	 */
	private Map mapSmallN;
	/**
	 * hashmap with key termID and value X.
	 */
	private Map mapBigX;
	/**
	 * hashmap with key termID and value N.
	 */
	private Map mapBigN;
	/**
	 * String with significance level.
	 */
	private String alpha;
	/**
	 * String with cluster name.
	 */
	private String clusterName;
	/**
	 * string with number of categories in the graph.
	 */
	private String categoriesString;
	/**
	 * the ontology.
	 */
	private Ontology ontology;

	private final CySwingAppAdapter adapter;

	/**
	 * Constructor for an overrepresentation visualization with correction.
	 * 
	 * @param testMap
	 *            HashMap with key: termID and value: pvalue.
	 * @param correctionMap
	 *            HashMap with key: termID and value: corrected pvalue.
	 * @param mapSmallX
	 *            HashMap with key: termID and value: #genes in selection with
	 *            GO termID.
	 * @param mapSmallN
	 *            HashMap with key: termID and value: #genes in reference with
	 *            GO termID.
	 * @param bigX
	 *            int with value of X (# of selected genes).
	 * @param bigN
	 *            int with value of N (total # genes in reference).
	 * @param alpha
	 *            String with value for significance level.
	 * @param ontology
	 *            the selected ontology.
	 * @param categoriesString
	 *            String with option what categories should be displayed.
	 */
	public DisplayBiNGOWindow(Map<Integer, String> testMap, Map correctionMap, Map mapSmallX, Map mapSmallN,
			Map mapBigX, Map mapBigN, String alpha, Ontology ontology, String clusterName, String categoriesString,
			final CySwingAppAdapter adapter) {

		if (adapter == null)
			throw new NullPointerException("Plugin Adapter is null.");

		this.adapter = adapter;
		this.testMap = testMap;
		this.correctionMap = correctionMap;
		this.mapSmallX = mapSmallX;
		this.mapSmallN = mapSmallN;
		this.mapBigX = mapBigX;
		this.mapBigN = mapBigN;
		this.alpha = alpha;
		this.ontology = ontology;
		this.clusterName = clusterName;
		this.categoriesString = categoriesString;
	}

	/**
	 * Method that builds up the new CyNetwork and shows it to the user.
	 */
	public void makeWindow() {
		// Create ontology DAG as CyNetwork
		final CyNetwork network = buildNetwork();
		adapter.getCyNetworkManager().addNetwork(network);

		buildNodeAttributes(network);
		buildEdgeAttributes(network);

		// Create View
		final TaskManager tm = adapter.getTaskManager();
		tm.execute(new GenericTaskFactory(new CreateViewTask(network)).createTaskIterator());

		// add color scale panel
		JFrame window = new JFrame(clusterName + " Color Scale");
		String alpha1 = SignificantFigures.sci_format(alpha, 3);
		String tmp = (new BigDecimal(alpha)).divide(new BigDecimal("100000"), SCALE_RESULT, BigDecimal.ROUND_HALF_UP)
				.toString();
		String alpha2 = SignificantFigures.sci_format(tmp, 3);
		ColorPanel colPanel = new ColorPanel(alpha1, alpha2, new Color(255, 255, 0), new Color(255, 127, 0));
		window.getContentPane().add(colPanel);
		window.getContentPane().setBackground(Color.WHITE);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		window.pack();
		// for bottom right position of the color scale panel.
		window.setLocation(screenSize.width - window.getWidth() - 10, screenSize.height - window.getHeight() - 30);
		window.setVisible(true);
		window.setResizable(false);
	}

	private final class CreateViewTask extends AbstractTask {
		private final CyNetwork network;

		CreateViewTask(final CyNetwork network) {
			this.network = network;
		}

		@Override
		public void run(TaskMonitor taskMonitor) throws Exception {
			taskMonitor.setTitle("Creating view for Ontology DAG");
			taskMonitor.setStatusMessage("Creating view...");
			taskMonitor.setProgress(-1.0);

			final CyNetworkViewFactory viewFactory = adapter.getCyNetworkViewFactory();
			final CyNetworkViewManager networkViewManager = adapter.getCyNetworkViewManager();

			final CyNetworkView view = viewFactory.createNetworkView(network);
			networkViewManager.addNetworkView(view);

			// Apply layout only when it is necessary.
			final CyLayoutAlgorithm layout = adapter.getCyLayoutAlgorithmManager().getLayout("force-directed");
			//layout.setNetworkView(view);
			
			final Object context = layout.getDefaultLayoutContext();
			adapter.getTaskManager().execute(layout.createTaskIterator(view, context, CyLayoutAlgorithm.ALL_NODE_VIEWS,""));
			
			TheVisualStyle vs = new TheVisualStyle(adapter, clusterName, Double.parseDouble(alpha));
			final VisualMappingManager vmm = adapter.getVisualMappingManager();
			final VisualStyle newStyle = vs.createVisualStyle(view.getModel());
			vmm.addVisualStyle(newStyle);
			vmm.setVisualStyle(vs.createVisualStyle(view.getModel()), view);
			newStyle.apply(view);
			
			taskMonitor.setProgress(1.0);
			taskMonitor.setStatusMessage("Network view successfully create for:  "
					+ network.getDefaultNetworkTable().getRow(network.getSUID()));
		}
	}


	/**
	 * Method that builds up the new network.
	 * 
	 * @return CyNetwork the network that was built.
	 */
	public CyNetwork buildNetwork() {
		final Set<Integer> set;

		if (testMap != null)
			set = new HashSet<Integer>(testMap.keySet());
		else
			set = new HashSet<Integer>(mapSmallX.keySet());

		// put the edges in a set of Strings
		final Set<String> sifSet = new HashSet<String>();

		// some GO labels might have multiple termIds, which need to be
		// canonicalized.
		// (for safety, has also been taken care of in
		// bingoOntologyFlatFilereader)
		Map nameMap = new HashMap();

		final CyNetworkFactory networkFactory = adapter.getCyNetworkFactory();
		final CyNetwork network = networkFactory.createNetwork();
		//network.getCyRow().set(CyNetwork.NAME, clusterName);
		network.getDefaultNetworkTable().getRow(network.getSUID()).set(CyNetwork.NAME, clusterName);
		// CyNetwork network = Cytoscape.createNetwork(clusterName);

		for (final Integer termID : set) {
			// final int termID = Integer.parseInt(value);
			// ifs for determining GO coverage of graphs.
			if (categoriesString.equals(CATEGORY_ALL)
					|| (categoriesString.equals(CATEGORY_BEFORE_CORRECTION) && new BigDecimal(testMap.get(
							new Integer(termID)).toString()).compareTo(new BigDecimal(alpha)) < 0)
					|| (categoriesString.equals(CATEGORY_CORRECTION) && new BigDecimal(correctionMap.get(termID + "")
							.toString()).compareTo(new BigDecimal(alpha)) < 0)) {

				int[][] paths = ontology.getAllHierarchyPaths(termID);

				int previousNode;
				for (int i = 0; i < paths.length; i++) {
					previousNode = paths[i][0];
					// for singleton nodes
					if ((paths.length == 1) && (paths[i].length == 1)) {
						sifSet.add(previousNode + "\n");
					}
					// first substring added to any map value will be null
					nameMap.put(ontology.getTerm(previousNode).getName(),
							nameMap.get(ontology.getTerm(previousNode).getName()) + " " + previousNode);
					for (int j = 1; j < paths[i].length; j++) {
						// first substring added to any map value will be null
						nameMap.put(ontology.getTerm(paths[i][j]).getName(),
								nameMap.get(ontology.getTerm(paths[i][j]).getName()) + " " + paths[i][j]);
						sifSet.add(previousNode + " pp " + paths[i][j] + "\n");
						previousNode = paths[i][j];
					}
				}
			}
		}

		// canonicalize nodes.
		Map termIdMap = makeTermIdMap(nameMap);

		// canonicalize edges and build network.
		Map<String, CyNode> nodeMap = new HashMap<String, CyNode>();
		for (final String writeString : sifSet) {
			final StringTokenizer st = new StringTokenizer(writeString);
			final String firstTermId = termIdMap.get(st.nextToken()).toString();
			CyNode node1 = nodeMap.get(firstTermId);
			if (node1 == null) {
				node1 = network.addNode();
				//node1.getCyRow().set(CyNetwork.NAME, firstTermId);
				network.getDefaultNodeTable().getRow(node1.getSUID()).set(CyNetwork.NAME, firstTermId);
				
				nodeMap.put(firstTermId, node1);
			}

			if (st.hasMoreTokens()) {
				st.nextToken(); // Skip
				final String secondTermId = termIdMap.get(st.nextToken()).toString();

				CyNode node2 = nodeMap.get(secondTermId);
				if (node2 == null) {
					node2 = network.addNode();
					//node2.getCyRow().set(CyNetwork.NAME, secondTermId);
					network.getDefaultNodeTable().getRow(node2.getSUID()).set(CyNetwork.NAME, secondTermId);
					
					nodeMap.put(secondTermId, node2);
				}

				final CyEdge edge = network.addEdge(node1, node2, true);
				//edge.getCyRow().set(CyEdge.INTERACTION, "pp");
				network.getDefaultEdgeTable().getRow(edge.getSUID()).set(CyEdge.INTERACTION, "pp");
				//edge.getCyRow().set(CyNetwork.NAME, firstTermId + " (pp) " + secondTermId);
				network.getDefaultEdgeTable().getRow(edge.getSUID()).set(CyNetwork.NAME, firstTermId + " (pp) " + secondTermId);
			}
		}
		nodeMap.clear();
		nodeMap = null;
		return network;
	}

	/**
	 * method that makes a termid map with as key a termid and as value the
	 * termid which it is equal to. e.g. 8125 = 8125 654 = 8125 6546 = 8125 this
	 * allows to map every termid that is the same to a unique termid.
	 * 
	 * @param nameMap
	 *            key: termid, value: string with termids
	 * @return HashMap key: termid, value: termid
	 */

	public Map makeTermIdMap(Map nameMap) {
		final Set<String> set = new HashSet<String>(nameMap.keySet());
		final Map resultMap = new HashMap();

		for (final String val : set) {
			// first substring null deleted at beginning of every string
			String valueIDs = nameMap.get(val).toString().substring(5);
			StringTokenizer st = new StringTokenizer(valueIDs);
			String termID = st.nextToken();
			resultMap.put(termID, termID);
			while (st.hasMoreTokens()) {
				resultMap.put(st.nextToken(), termID);
			}
		}
		return resultMap;
	}

	/**
	 * Method that creates the node attributes (size, color, ...).
	 */
	public void buildNodeAttributes(CyNetwork network) {

		createNodeColumn(network);

		final List<CyNode> nodeList = network.getNodeList();
		for (final CyNode node : nodeList) {
			//String termID = node.getCyRow().get(CyNetwork.NAME, String.class);
			String termID =  network.getDefaultNodeTable().getRow(node.getSUID()).get(CyNetwork.NAME, String.class);
			String shape;
			String description;
			String pValue;
			String adj_pValue;
			String smallX;
			String smallN;
			String bigX;
			String bigN;
			Double color;
			Double size;
			shape = "ellipse";

			try {
				description = ontology.getTerm(Integer.parseInt(termID)).getName();
			} catch (Exception e) {
				description = "?";
			}

			try {
				if (testMap != null) {
					pValue = SignificantFigures.sci_format(testMap.get(new Integer(termID)).toString(), 5);
				} else {
					pValue = "N/A";
				}
			} catch (Exception e) {
				pValue = "N/A";
			}
			try {
				if (correctionMap != null) {
					adj_pValue = SignificantFigures.sci_format(correctionMap.get(termID).toString(), 5);
				} else {
					adj_pValue = "N/A";
				}
			} catch (Exception e) {
				adj_pValue = "N/A";
			}
			try {
				smallX = mapSmallX.get(new Integer(termID)).toString();
			} catch (Exception e) {
				smallX = "N/A";
			}
			try {
				smallN = mapSmallN.get(new Integer(termID)).toString();
			} catch (Exception e) {
				smallN = "N/A";
			}
			try {
				bigX = mapBigX.get(new Integer(termID)).toString();
			} catch (Exception e) {
				bigX = "N/A";
			}
			try {
				bigN = mapBigN.get(new Integer(termID)).toString();
			} catch (Exception e) {
				bigN = "N/A";
			}
			try {
				if (testMap == null) {
					color = new Double(0);
				} else if (correctionMap == null) {
					double a = -(Math.log((new BigDecimal(testMap.get(new Integer(termID)).toString())).doubleValue()) / Math
							.log(10));
					color = new Double(a);
				} else {
					double a = -(Math.log((new BigDecimal(correctionMap.get(termID).toString())).doubleValue()) / Math
							.log(10));
					color = new Double(a);
				}
			} catch (Exception e) {
				color = new Double(0);
			}
			try {
				double numberOfGenes = new Integer(mapSmallX.get(new Integer(termID)).toString()).doubleValue();
				numberOfGenes = Math.sqrt(numberOfGenes) * 2;
				size = new Double(numberOfGenes);
			} catch (Exception e) {
				size = DEFAULT_SIZE;
			}

			//node.getCyRow().set("pValue_" + clusterName, pValue);
			//node.getCyRow().set("adjustedPValue_" + clusterName, adj_pValue);
			//node.getCyRow().set("x_" + clusterName, smallX);
			//node.getCyRow().set("X_" + clusterName, bigX);
			//node.getCyRow().set("n_" + clusterName, smallN);
			//node.getCyRow().set("N_" + clusterName, bigN);
			//node.getCyRow().set("description_" + clusterName, description);
			//node.getCyRow().set("nodeFillColor_" + clusterName, color);
			//node.getCyRow().set("nodeSize_" + clusterName, size);
			//node.getCyRow().set("nodeType_" + clusterName, shape);
			//node.getCyRow().set("nodeFontSize_" + clusterName, 14);
			
			CyRow row = network.getDefaultNodeTable().getRow(node.getSUID()); 			
			row.set("pValue_" + clusterName, pValue);
			row.set("adjustedPValue_" + clusterName, adj_pValue);
			row.set("xx_" + clusterName, smallX);
			row.set("X_" + clusterName, bigX);
			row.set("nn_" + clusterName, smallN);
			row.set("N_" + clusterName, bigN);
			row.set("description_" + clusterName, description);
			row.set("nodeFillColor_" + clusterName, color);
			row.set("nodeSize_" + clusterName, size);
			row.set("nodeType_" + clusterName, shape);
			row.set("nodeFontSize_" + clusterName, 14);
			
		}
	}

	private void createNodeColumn(final CyNetwork network) {
		final CyTable nodeTable = network.getDefaultNodeTable();
		for (final String colName : NODE_COL_NAMES) {
			if (nodeTable.getColumn(colName) == null) {
				if (colName.equals("nodeFillColor") || colName.equals("nodeSize"))
					nodeTable.createColumn(colName + "_" + clusterName, Double.class, false);
				else if (colName.equals("nodeFontSize"))
					nodeTable.createColumn(colName + "_" + clusterName, Integer.class, false);
				else
					nodeTable.createColumn(colName + "_" + clusterName, String.class, false);
			}
		}
	}

	/**
	 * Method that creates the edge attributes (actually one attribute
	 * determines all edge properties, i.e. color, target arrow, line width...).
	 */
	public void buildEdgeAttributes(final CyNetwork network) {
		final CyTable edgeTable = network.getDefaultEdgeTable();
		edgeTable.createColumn("edgeType_" + clusterName, String.class, false);

		final List<CyEdge> edgeList = network.getEdgeList();
		for (final CyEdge edge : edgeList) {
			//edge.getCyRow().set("edgeType_" + clusterName, "black");
			network.getDefaultEdgeTable().getRow(edge.getSUID()).set("edgeType_" + clusterName, "black");
		}
	}
}
