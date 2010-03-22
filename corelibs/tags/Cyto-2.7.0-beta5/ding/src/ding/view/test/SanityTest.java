
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package ding.view.test;

import cytoscape.render.stateful.GraphLOD;

import ding.view.BirdsEyeView;
import ding.view.DGraphView;

import fing.model.FingRootGraphFactory;

import giny.model.GraphPerspective;
import giny.model.RootGraph;

import giny.view.Bend;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.GraphViewChangeEvent;
import giny.view.GraphViewChangeListener;
import giny.view.NodeView;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;


/**
 * DOCUMENT ME!
 *
 * @author $author$
 */
public class SanityTest {
	/**
	 * DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 *
	 * @throws Exception DOCUMENT ME!
	 */
	public static void main(String[] args) throws Exception {
		final RootGraph root = FingRootGraphFactory.instantiateRootGraph();
		final int node1 = root.createNode();
		final int node2 = root.createNode();
		final int node3 = root.createNode();
		final int node4 = root.createNode();
		final int edge1 = root.createEdge(node1, node2);
		final int edge2 = root.createEdge(node2, node3);
		final int edge3 = root.createEdge(node3, node1);
		final int edge4 = root.createEdge(node4, node1);
		final int edge5 = root.createEdge(node4, node2);
		final GraphPerspective persp = root.createGraphPerspective((int[]) null, (int[]) null);
		final DGraphView view = new DGraphView(persp);

		for (int i = 0; i < 5; i++) {
			if (i != 2)
				view.addGraphViewChangeListener(new GraphViewChangeListener() {
						public void graphViewChanged(GraphViewChangeEvent evt) {
						}
					});

			else
				view.addGraphViewChangeListener(new GraphViewChangeListener() {
						public void graphViewChanged(GraphViewChangeEvent evt) {
							if (evt.isNodesSelectedType()) {
								final int[] selectedNodes = evt.getSelectedNodeIndices();
								System.out.print("selected nodes: ");

								for (int i = 0; i < selectedNodes.length; i++)
									System.out.print(selectedNodes[i] + "  ");

								System.out.println();
							}
						}
					});

		}

		EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					Frame f = new Frame() {
						public void update(Graphics g) {
							paint(g);
						}
					};

					f.add(view.getComponent());

					for (int i = 0; i < 10; i++) {
						f.setVisible(true);
						f.setSize(new Dimension(400, 300));
					}

					f.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								System.exit(0);
							}
						});
				}
			});

		final NodeView nv1 = view.addNodeView(node1);
		final NodeView nv2 = view.addNodeView(node2);
		final NodeView nv3 = view.addNodeView(node3);
		final NodeView nv4 = view.addNodeView(node4);
		final EdgeView ev1 = view.addEdgeView(edge1);
		final EdgeView ev2 = view.addEdgeView(edge2);
		final EdgeView ev3 = view.addEdgeView(edge3);
		final EdgeView ev4 = view.addEdgeView(edge4);
		final EdgeView ev5 = view.addEdgeView(edge5);
		final Bend bend5 = ev5.getBend();
		bend5.addHandle(new Point2D.Float(0.0f, 0.0f));
		nv1.setOffset(0.0d, 50.0d);
		nv2.setOffset(-30.0d, -10.0d);
		nv3.setOffset(30.0d, 10.0d);
		nv4.setOffset(-60.0d, 50.0d);
		nv1.setBorderWidth(1.0f);
		nv2.setBorderWidth(1.0f);
		nv3.setBorderWidth(1.0f);
		nv4.setBorderWidth(1.0f);
		ev1.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
		ev2.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
		ev3.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
		ev4.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
		ev5.setTargetEdgeEnd(EdgeView.BLACK_DELTA);
		view.fitContent();
		view.setGraphLOD(new GraphLOD() {
				public boolean detail(int nodes, int edges) {
					return nodes < 4;
				}
			});
		view.updateView();
		EventQueue.invokeAndWait(new Runnable() {
				public void run() {
					Frame f = new Frame() {
						public void update(Graphics g) {
							paint(g);
						}
					};

					f.add(new BirdsEyeView(view));

					for (int i = 0; i < 10; i++) {
						f.setVisible(true);
						f.setSize(new Dimension(150, 100));
					}

					f.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent e) {
								System.exit(0);
							}
						});
				}
			});
	}
}
