package csplugins.mcode;

import cytoscape.util.GinyFactory;
import cytoscape.data.CyNetwork;
import cytoscape.actions.GinyUtils;
import cytoscape.GraphObjAttributes;
import cytoscape.view.CyWindow;
import giny.model.GraphPerspective;
import giny.model.Node;
import giny.util.SpringEmbeddedLayouter;
import giny.view.NodeView;
import giny.view.EdgeView;
import giny.view.GraphView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

import phoebe.PGraphView;

/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Gary Bader
 ** Authors: Gary Bader, Ethan Cerami, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **
 ** User: Gary Bader
 ** Date: Jan 30, 2004
 ** Time: 11:45:02 AM
 ** Description
 **/

//Reports the results of MCODE complex finding
public class MCODEResultsDialog extends JDialog {
	protected Frame mainFrame;
	protected JTable table;
	protected MCODEResultsDialog popupTable;
	protected MCODEResultsDialog.MCODEResultsTableModel model;
	//table size parameters
	protected final int defaultColumnWidth = 100;
	protected final int defaultRowHeight = 200;
	protected int preferredTableWidth = defaultColumnWidth; // incremented below
	//User preference
	protected boolean openInNewWindow=true;
	//Actual complex data
	protected GraphPerspective[] gpComplexArray;    //The list of complexes, sorted when !null
	//These are here so we can select complexes in the main window and open them in new windows
	protected GraphView graphView;
	protected GraphPerspective gpInputGraph;
	protected CyWindow cyWindow;

	public MCODEResultsDialog(Frame parentFrame, CyWindow cyWindow, ArrayList complexes) {
		super(parentFrame, "MCODE Results", false);
		mainFrame = parentFrame;
		popupTable = this;
		this.graphView = cyWindow.getView();
		this.gpInputGraph = cyWindow.getNetwork().getGraphPerspective();
		this.cyWindow = cyWindow;

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		model = new MCODEResultsDialog.MCODEResultsTableModel(gpInputGraph, complexes);
		table = new JTable(model) {
			//Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);
				int realColumnIndex = convertColumnIndexToModel(colIndex);

				if (realColumnIndex == 2) { //Size column
					String value = (String) getValueAt(rowIndex, colIndex);
					String nodes=null;
					String edges=null;
					int i=0;
					for(StringTokenizer st = new StringTokenizer(value, ","); st.hasMoreTokens(); i++) {
						String s = st.nextToken();
						//string is assumed to only have two tokens, since we build it in MCODEResultsTableModel
						if(i==0) {
							nodes = s;
						}
						else if (i==1) {
							edges = s;
						}
					}
					tip = "Complex has " + nodes + " nodes and " + edges + " edges.";
				}
				return tip;
			}
		};
		table.setPreferredScrollableViewportSize(new Dimension(preferredTableWidth, 100));
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//Ask to be notified of selection changes.
		ListSelectionModel rowSM = table.getSelectionModel();
		rowSM.addListSelectionListener(new TableRowSelectionHandler());

		table.setRowHeight(defaultRowHeight);

		JScrollPane scrollPane = new JScrollPane(table);
		panel.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new MCODEResultsDialog.OKAction(this));
		buttonPanel.add(okButton, BorderLayout.CENTER);
		panel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(panel);
	}

	class MCODEResultsTableModel extends AbstractTableModel {

		//Create column headings
		String[] columnNames = {"Rank", "Score", "Size", "Names", "Complex"};
		int[] columnWidths = {40};
		Object[][] data;    //the actual table data

		public MCODEResultsTableModel(GraphPerspective gpInputGraph, ArrayList complexes) {
			int[] complexArray;
			GraphPerspective gpComplex;

			//get GraphPerspectives for all complexes, score and rank them
			//convert the ArrayList to an array of GraphPerspectives and sort it by score using a custom Comparator
			gpComplexArray = new GraphPerspective[complexes.size()];
			ArrayList complex;
			for (int i = 0; i<complexes.size(); i++) {
				complex = (ArrayList) complexes.get(i);
				complexArray = convertIntArrayList2array(complex);
				gpComplex = gpInputGraph.createGraphPerspective(complexArray);
				gpComplexArray[i] = gpComplex;
			}
            Arrays.sort(gpComplexArray, new Comparator() {
	            //sorting GraphPerpectives by decreasing score
				public int compare(Object o1, Object o2) {
					double d1 = MCODE.getInstance().alg.scoreComplex((GraphPerspective) o1);
		            double d2 = MCODE.getInstance().alg.scoreComplex((GraphPerspective) o2);
					if (d1 == d2) {
						return 0;
					} else if (d1 < d2) {
						return 1;
					} else {
						return -1;
					}
				}
			});

			//copy information into the data array that represents the table
			data = new Object[gpComplexArray.length][columnNames.length];
			PGraphView view;
			SpringEmbeddedLayouter lay;
			Image image;
			for (int i=0; i<gpComplexArray.length; i++) {
				gpComplex = gpComplexArray[i];
				data[i][0] = new Integer(i+1); //rank
				data[i][1] = new Double(MCODE.getInstance().alg.scoreComplex(gpComplex));
				//complex size - format: (# prot, # intx)
				data[i][2] = new String(gpComplex.getNodeCount()+","+gpComplex.getEdgeCount());
				//create a string of node names - this can be long
				data[i][3] = getNodeNameList(gpComplex);
				//create an image for each complex - make it a nice layout of the complex
				view = (PGraphView) GinyFactory.createGraphView(gpComplex);
				for (Iterator in = view.getNodeViewsIterator(); in.hasNext(); ) {
					NodeView nv = (NodeView) in.next();
					String label = nv.getNode().getIdentifier();
					nv.setLabel(label);
					nv.setShape(NodeView.ELLIPSE);
					nv.setUnselectedPaint(Color.red);
					nv.setBorderPaint(Color.black);
				}
				for (Iterator ie = view.getEdgeViewsIterator(); ie.hasNext();) {
					EdgeView ev = (EdgeView) ie.next();
					ev.setUnselectedPaint(Color.blue);
					ev.setTargetEdgeEnd(EdgeView.ARROW_END);
					ev.setTargetEdgeEndPaint(Color.CYAN);
					ev.setSourceEdgeEndPaint(Color.CYAN);
					ev.setStroke(new BasicStroke(5f));
				}
				lay = new SpringEmbeddedLayouter(view);
				lay.doLayout();
				image = view.getCanvas().getLayer().toImage();
				BufferedImage bi = new BufferedImage(defaultRowHeight, defaultRowHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = bi.createGraphics();
				double largestSide = view.getCanvas().getLayer().getFullBounds().width;
				if(view.getCanvas().getLayer().getFullBounds().height>largestSide) {
					largestSide= view.getCanvas().getLayer().getFullBounds().height;
				}
				double scaleFactor = (double) defaultRowHeight / largestSide;
				g2d.drawImage(image,AffineTransform.getScaleInstance(scaleFactor, scaleFactor),view.getComponent());
				data[i][4] = new ImageIcon(bi);
			}
		}

		//Utility method - convert ArrayList to int[]
		private int[] convertIntArrayList2array(ArrayList alInput) {
			int[] outputNodeIndices = new int[alInput.size()];
			int j = 0;
			for (Iterator i = alInput.iterator(); i.hasNext(); j++) {
				outputNodeIndices[j] = ((Integer) i.next()).intValue();
			}
			return (outputNodeIndices);
		}

		private String getNodeNameList(GraphPerspective gpInput) {
			Iterator i = gpInput.nodesIterator();
			StringBuffer sbNodeNames= new StringBuffer();
            while (i.hasNext()) {
	            Node node = (Node) i.next();
	            sbNodeNames.append(node.getIdentifier());
	            if(i.hasNext()) {
	                sbNodeNames.append(",");
	            }
            }
			return(sbNodeNames.toString());
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public int getPreferredColumnWidth(int col) {
			return 0;
		}
	}

	public class OKAction extends AbstractAction {
		private JDialog dialog;

		OKAction(JDialog popup) {
			super("");
			this.dialog = popup;
		}

		public void actionPerformed(ActionEvent e) {
			dialog.dispose();
		}
	}

	//Selects nodes in graph when a row is selected
	public class TableRowSelectionHandler implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			//Ignore extra messages.
			if (e.getValueIsAdjusting()) return;
			ListSelectionModel lsm = (ListSelectionModel) e.getSource();
			GraphPerspective gpComplex;
			if (!lsm.isSelectionEmpty()) {
				//start with no selected nodes
				GinyUtils.deselectAllNodes(graphView);
				int selectedRow = lsm.getMinSelectionIndex();
				gpComplex = gpComplexArray[selectedRow];
				//go through graph and select nodes in the complex
				List nodeList = gpComplex.nodesList();
				NodeView nv;
				for (int i = 0; i < nodeList.size(); i++) {
					Node n = (Node) nodeList.get(i);
					if(gpInputGraph.containsNode(n)) {
						nv = graphView.getNodeView(n);
						nv.setSelected(true);
					}
				}
				if(openInNewWindow) {
					//this code copied from Cytoscape NewWindowSelectedNodesEdgesAction.java on Feb.4.2004
					//save the vizmapper catalog
					cyWindow.getCytoscapeObj().saveCalculatorCatalog();
					CyNetwork oldNetwork = cyWindow.getNetwork();
					GraphView view = cyWindow.getView();
					int[] nodes = view.getSelectedNodeIndices();
					GraphObjAttributes newNodeAttributes = oldNetwork.getNodeAttributes();
					GraphObjAttributes newEdgeAttributes = oldNetwork.getEdgeAttributes();

					CyNetwork newNetwork = new CyNetwork(gpComplex, newNodeAttributes,
					        newEdgeAttributes, oldNetwork.getExpressionData());
					newNetwork.setNeedsLayout(true);

					String title = "Complex "+ (selectedRow+1);
					try {
						//this call creates a WindowOpened event, which is caught by
						//cytoscape.java, enabling that class to manage the set of windows
						//and quit when the last window is closed
						CyWindow newWindow = new CyWindow(cyWindow.getCytoscapeObj(),
						        newNetwork, title);
						newWindow.showWindow();
					} catch (Exception e2) {
						System.err.println("Exception when creating new window");
						e2.printStackTrace();
					}
				}
			}
		}
	}
}
