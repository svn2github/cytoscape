package cytoscape.editor.impl;

import cytoscape.Cytoscape;
import cytoscape.data.Semantics;
import cytoscape.editor.CytoscapeEditor;
import cytoscape.editor.CytoscapeEditorManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.view.NodeView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * add Nodes and Edges to the network based upon a line of SIF typed in by the
 * user
 * 
 * @author ajk
 * 
 */
public class SIF_Interpreter {

	public static void interpret(String input, Point p, CytoscapeEditor editor) {
		String[] terms = input.split(" ");
		NodeView nv1, nv2;
		if (terms != null) {
			if (terms.length > 0) {
				String name1 = terms[0].trim();
				if (!name1.equals(null)) {
					// first see if we already have a node
					CyNode node1 = Cytoscape.getCyNode(terms[0], false);
					if (node1 == null) {

						node1 = Cytoscape.getCyNode(terms[0], true);
						Cytoscape.getCurrentNetwork().restoreNode(node1);
						nv1 = Cytoscape.getCurrentNetworkView().getNodeView(
								node1);
						CytoscapeEditorManager.log("Node 1 = " + node1);
						CytoscapeEditorManager.log("NodeView 1 = " + nv1);
						double[] nextLocn = new double[2];
						nextLocn[0] = p.getX();
						nextLocn[1] = p.getY();
						Cytoscape.getCurrentNetworkView().xformComponentToNodeCoords(nextLocn);
						nv1.setOffset(nextLocn[0], nextLocn[1]);
					} else {
						nv1 = Cytoscape.getCurrentNetworkView().getNodeView(
								node1);
					}
					double spacing = 3.0 * Cytoscape.getCurrentNetworkView()
							.getNodeView(node1).getWidth();
					if (terms.length == 3) // simple case of 'A interaction B'
					{
						CyNode node2 = Cytoscape.getCyNode(terms[2], false);
						if (node2 == null) {
							node2 = Cytoscape.getCyNode(terms[2], true);
							Cytoscape.getCurrentNetwork().restoreNode(node2);
							nv2 = Cytoscape.getCurrentNetworkView()
									.getNodeView(node2);
							nv2.setOffset(nv1.getXPosition() + spacing, nv1
									.getYPosition());
						}
						// CytoscapeEditorManager.log("Node 2 = " + node2);
						// CytoscapeEditorManager.log("NodeView 2 = " + nv2);
						CyEdge edge = Cytoscape.getCyEdge(node1, node2,
								Semantics.INTERACTION, terms[1], true, true);
						Cytoscape.getCurrentNetwork().restoreEdge(edge);

					} else if (terms.length > 3) {
						// process multiple targets and one source
						// MLC 07/03/07:
						// List nodeViews = new ArrayList();
//						 MLC 07/03/07:
						List<NodeView> nodeViews = new ArrayList<NodeView>();
						String interactionType = terms[1];
						for (int i = 2; i < terms.length; i++)
						{
							CyNode node2 = Cytoscape.getCyNode(terms[i], false);
							if (node2 == null) {
								node2 = Cytoscape.getCyNode(terms[i], true);
								
								Cytoscape.getCurrentNetwork().restoreNode(node2);
								nv2 = Cytoscape.getCurrentNetworkView()
										.getNodeView(node2);
								nodeViews.add(nv2);
							}
							CyEdge edge = Cytoscape.getCyEdge(node1, node2,
									Semantics.INTERACTION, interactionType, true, true);
							Cytoscape.getCurrentNetwork().restoreEdge(edge);
						}
						doCircleLayout(nodeViews, nv1);
					}
				}
			}
			Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());
		}

	}

	public static void processInput(Point p, CytoscapeEditor editor) {
		String input = JOptionPane.showInputDialog(Cytoscape.getCurrentNetworkView().getComponent(),
				"Type in a nodes/edges expression in SIF format"
						+ ", e.g. A inhibit B");
		if (input != null) {
			interpret(input, p, editor);
		}
	}

	public static void doCircleLayout(List nodeViews, NodeView nv1) {

		NodeView nv;

		// // Compute Radius

		int r = (int) Math.max((nodeViews.size() * nv1.getWidth()) / Math.PI,
				30);

		// Compute angle step
		double phi = (2 * Math.PI) / nodeViews.size();

		// Arrange vertices in a circle
		for (int i = 0; i < nodeViews.size(); i++) {

			nv = (NodeView) nodeViews.get(i);
			nv.setOffset(
					nv1.getXPosition() + (int) (r * Math.sin(i * phi)), 
					nv1.getYPosition() + (int) (r * Math.cos(i * phi)));
		}

		Cytoscape.redrawGraph(Cytoscape.getCurrentNetworkView());

	}

}
