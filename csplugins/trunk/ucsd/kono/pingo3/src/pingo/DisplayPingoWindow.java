package pingo;

/**
 * * Copyright (c) 2010 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere
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
 * * Authors: Steven Maere
 * * Date: Jul.27.2010
 * * Description: PiNGO is a Cytoscape plugin that leverages functional enrichment
 * * analysis to discover lead genes from biological networks.          
 **/

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.plugin.CyPluginAdapter;
import org.cytoscape.view.layout.CyLayoutAlgorithm;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.TaskMonitor;

import BiNGO.GenericTaskFactory;

/**
 * *****************************************************************
 * DisplayPiNGOWindow.java
 * @author Steven Maere (c) 2010
 * 
 * <p/>
 * Class which creates the new CyNetwork and CyNetworkView of the
 * overrepresented GO graph.
 * <p/>
 * ******************************************************************
 */

public class DisplayPingoWindow {

	private static final String[] NODE_COL_NAMES = { "goCat_", "pval_", "x_", "X_", "n_", "N_", "description_",
		"nodeFillColor_", "nodeSize_", "nodeType_", "nodeFontSize_" };
	
	private ModuleNetwork M;
	private Map<Gene, Map<Gene, Double>> G;
	/**
	 * hasmap with key termID and value pvalue.
	 */
	private Map<PingoAnalysis.TestInstance, Double> pvals;
	/**
	 * hashmap with key termID and value x.
	 */
	private Map<PingoAnalysis.TestInstance, Integer> smallX;
	/**
	 * hashmap with key termID and value n.
	 */
	private Map<PingoAnalysis.TestInstance, Integer> smallN;
	/**
	 * hashmap with key termID and value X.
	 */
	private Map<PingoAnalysis.TestInstance, Integer> bigX;
	/**
	 * hashmap with key termID and value N.
	 */
	private Map<PingoAnalysis.TestInstance, Integer> bigN;
	/**
	 * String with significance level.
	 */
	private String alpha;
	/**
	 * String with analysis name.
	 */
	private String pingoName;
	/**
	 * final defaultsize for the size of the nodes.
	 */
	private final Double DEFAULT_SIZE = 1d;
	/**
	 * scale for BigDecimal
	 */
	private static final int SCALE_RESULT = 100;

	private final CyPluginAdapter adapter;

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
	public DisplayPingoWindow(ModuleNetwork M, Map<Gene, Map<Gene, Double>> G,
			Map<PingoAnalysis.TestInstance, Double> pvals, Map<PingoAnalysis.TestInstance, Integer> smallX,
			Map<PingoAnalysis.TestInstance, Integer> smallN, Map<PingoAnalysis.TestInstance, Integer> bigX,
			Map<PingoAnalysis.TestInstance, Integer> bigN, String alpha, String pingoName, final CyPluginAdapter adapter) {

		this.adapter = adapter;
		this.M = M;
		this.G = G;
		this.pvals = pvals;
		this.smallX = smallX;
		this.smallN = smallN;
		this.bigX = bigX;
		this.bigN = bigN;
		this.alpha = alpha;
		this.pingoName = pingoName;
	}


	/**
	 * Method that builds up the new CyNetwork and shows it to the user.
	 */
	public void makeWindow() {
		
		System.out.println("MAKE Window called: ");

		
		final CyNetwork network = buildNetwork();
		buildNodeAttributes(network);
		buildEdgeAttributes(network);
		
		// Create View
		final TaskManager tm = adapter.getTaskManager();
		tm.execute(new GenericTaskFactory(new CreateViewTask(network)));
		
		// add color scale panel
		JFrame window = new JFrame(pingoName + " Color Scale");
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

			final CyNetworkView view = viewFactory.getNetworkView(network, false);
			networkViewManager.addNetworkView(view);

			// Apply layout only when it is necessary.
			final CyLayoutAlgorithm layout = adapter.getCyLayouts().getLayout("force-directed");
			layout.setNetworkView(view);
			insertTasksAfterCurrentTask(layout.getTaskIterator());
			
			final VisualStyleBuilder vs = new VisualStyleBuilder(adapter, pingoName, Double.parseDouble(alpha));
			final VisualMappingManager vmm = adapter.getVisualMappingManager();
			final VisualStyle newStyle = vs.createVisualStyle(view.getModel());
			vmm.addVisualStyle(newStyle);
			vmm.setVisualStyle(vs.createVisualStyle(view.getModel()), view);
			newStyle.apply(view);
			
			taskMonitor.setProgress(1.0);
			taskMonitor.setStatusMessage("Network view successfully create for:  "
					+ network.getCyRow().get(CyTableEntry.NAME, String.class));
		}
	}


	/**
	 * Method that builds up the new network.
	 * 
	 * @return CyNetwork the network that was built.
	 */

	public CyNetwork buildNetwork() {
		final CyNetworkFactory networkFactory = adapter.getCyNetworkFactory();
		final CyNetwork network = networkFactory.getInstance();
		network.getCyRow().set(CyTableEntry.NAME, pingoName);

		final Map<String, CyNode> nodes = new HashMap<String, CyNode>();
		for (final Gene g : G.keySet()) {
			final String nodeName1 = g.name;
			CyNode node1 = nodes.get(nodeName1);
			if(node1 == null) {
				node1 = network.addNode();
				node1.getCyRow().set(CyTableEntry.NAME, nodeName1);
				nodes.put(nodeName1, node1);
			}
			
			for (final Gene g2 : G.get(g).keySet()) {
				final String nodeName2 = g2.name;
				CyNode node2 = nodes.get(nodeName2);
				if(node2 == null) {
					node2 = network.addNode();
					node2.getCyRow().set(CyTableEntry.NAME, nodeName2);
					nodes.put(nodeName2, node2);
				}
				final CyEdge edge = network.addEdge(node1, node2, true);
				edge.getCyRow().set(CyTableEntry.NAME, nodeName1 + " (pp) " + nodeName2);
			}
		}
		return network;
	}


	/**
	 * Method that creates the node attributes (size, color, ...).
	 */
	public void buildNodeAttributes(CyNetwork network) {

		createNodeColumn(network);
		
		final List<CyNode> nodes = network.getNodeList();
		for(final CyNode node : nodes) {
			String id = node.getCyRow().get(CyTableEntry.NAME, String.class);
			String shape;
			String description;
			String goCat;
			String pval;
			String sX;
			String sN;
			String bX;
			String bN;
			// String color;
			Double color;
			Double size;
			shape = "ellipse";

			// find best TestInstance
			PingoAnalysis.TestInstance bestT = null;
			for (PingoAnalysis.TestInstance t : pvals.keySet()) {
				if (t.m.name.equals(id)) {
					if (bestT == null || pvals.get(t) < pvals.get(bestT)) {
						bestT = t;
					}
				}
			}

			try {
				description = M.geneMap.get(id).name;
			} catch (Exception e) {
				description = "";
			}

			try {
				goCat = bestT.o.getName();
			} catch (Exception e) {
				goCat = "";
			}

			try {
				if (pvals != null) {
					pval = SignificantFigures.sci_format(pvals.get(bestT).toString(), 5);
				} else {
					pval = "N/A";
				}
			} catch (Exception e) {
				pval = "N/A";
			}
			try {
				sX = smallX.get(bestT).toString();
			} catch (Exception e) {
				sX = "N/A";
			}
			try {
				sN = smallN.get(bestT).toString();
			} catch (Exception e) {
				sN = "N/A";
			}
			try {
				bX = bigX.get(bestT).toString();
			} catch (Exception e) {
				bX = "N/A";
			}
			try {
				bN = bigN.get(bestT).toString();
			} catch (Exception e) {
				bN = "N/A";
			}
			try {
				if (pvals == null) {
					color = 0.0;
				} else {
					color = -(Math.log(pvals.get(bestT)) / Math.log(10));
				}
			} catch (Exception e) {
				color = 0.0;
			}
			try {
				size = 2 * Math.sqrt(smallX.get(bestT));
			} catch (Exception e) {
				size = DEFAULT_SIZE;
			}

			node.getCyRow().set("goCat_" + pingoName, goCat);
			node.getCyRow().set("pval_" + pingoName, pval);
			node.getCyRow().set("x_" + pingoName, sX);
			node.getCyRow().set("X_" + pingoName, bX);
			node.getCyRow().set("n_" + pingoName, sN);
			node.getCyRow().set("N_" + pingoName, bN);
			node.getCyRow().set("description_" + pingoName, description);
			node.getCyRow().set("nodeFillColor_" + pingoName, color);
			node.getCyRow().set("nodeSize_" + pingoName, size);
			node.getCyRow().set("nodeType_" + pingoName, shape);
			node.getCyRow().set("nodeFontSize_" + pingoName, 14);
		}
	}
	
	private void createNodeColumn(final CyNetwork network) {
		final CyTable nodeTable = network.getDefaultNodeTable();
		for (final String colName : NODE_COL_NAMES) {
			if (nodeTable.getColumn(colName) == null) {
				if (colName.equals("nodeFillColor") || colName.equals("nodeSize"))
					nodeTable.createColumn(colName + "_" + pingoName, Double.class, false);
				else if (colName.equals("nodeFontSize"))
					nodeTable.createColumn(colName + "_" + pingoName, Integer.class, false);
				else
					nodeTable.createColumn(colName + "_" + pingoName, String.class, false);
			}
		}
	}

	/**
	 * Method that creates the edge attributes (actually one attribute
	 * determines all edge properties, i.e. color, target arrow, line width...).
	 */
	public void buildEdgeAttributes(CyNetwork network) {
		final CyTable edgeTable = network.getDefaultEdgeTable();
		edgeTable.createColumn("edgeType_" + pingoName, String.class, false);

		final List<CyEdge> edgeList = network.getEdgeList();
		for (final CyEdge edge : edgeList)
			edge.getCyRow().set("edgeType_" + pingoName, "black");
	}

}
