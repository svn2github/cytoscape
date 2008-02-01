/*
  File: AnimatedLayoutAction.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.actions;

import cytoscape.dialogs.GraphObjectSelection;

//import giny.util.*;
import cytoscape.ding.DingNetworkView;

import cytoscape.view.CyNetworkView;

import giny.model.*;

import giny.view.*;

import phoebe.*;

import phoebe.event.*;

import phoebe.util.*;

import java.awt.event.ActionEvent;

import java.util.*;

import javax.swing.*;


/**
 * @deprecated Not apparently used. Shout if you are.  Will be
 * removed 5/2008.
 */
@Deprecated
public class AnimatedLayoutAction extends AbstractAction {
	CyNetworkView networkView;
	boolean bool = false;

	/**
	 * Creates a new AnimatedLayoutAction object.
	 *
	 * @param networkView  DOCUMENT ME!
	 */
	public AnimatedLayoutAction(CyNetworkView networkView) {
		super("Animate Layout");
		this.networkView = networkView;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		JDialog dialog = new JDialog();
		JPanel main = new JPanel();

		main.add(new JButton(new AbstractAction("3D") {
				public void actionPerformed(ActionEvent e) {
					// Do this in the GUI Event Dispatch thread...
					SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								//                   PGrap*View gv = ( PGrap*View )networkView.getView();
								//                   ISOM3DLayout isom = new ISOM3DLayout( gv );
								//                   isom.doLayout();
							}
						});
				}
			}));

		main.add(new JButton(new AbstractAction("Z Axis") {
				public void actionPerformed(ActionEvent e) {
					// Do this in the GUI Event Dispatch thread...
					SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								double maxZ = Double.MAX_VALUE;
								double minZ = Double.MAX_VALUE;

								DingNetworkView gv = (DingNetworkView) networkView
								                                                                               .getView();
								Iterator nvi = gv.getNodeViewsIterator();

								while (nvi.hasNext()) {
									NodeView nv = (NodeView) nvi.next();

									// System.out.print( "Index: "+nv.getRootGraphIndex() );
									//                     System.out.print( "Index: "+nv.getRootGraphIndex() );
									//                     System.out.print( " X: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_X_POSITION ) );
									//                     System.out.print( " Y: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Y_POSITION ) );
									//                     System.out.println( " Z: "+gv.getNodeDoubleProperty( nv.getRootGraphIndex(), GraphView.NODE_Z_POSITION ) );
									if (maxZ == Double.MAX_VALUE) {
										maxZ = gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
										                                GraphView.NODE_Z_POSITION);
										minZ = gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
										                                GraphView.NODE_Z_POSITION);
									}

									if (maxZ < gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
									                                    GraphView.NODE_Z_POSITION)) {
										maxZ = gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
										                                GraphView.NODE_Z_POSITION);
									}

									if (minZ > gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
									                                    GraphView.NODE_Z_POSITION)) {
										minZ = gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
										                                GraphView.NODE_Z_POSITION);
									}
								}

								//                   System.out.println( "Z-RAnge: "+minZ+ " to "+maxZ );
								nvi = gv.getNodeViewsIterator();

								while (nvi.hasNext()) {
									NodeView nv = (NodeView) nvi.next();

									double scale = (gv.getNodeDoubleProperty(nv.getRootGraphIndex(),
									                                         GraphView.NODE_Z_POSITION)
									               - minZ) / (maxZ - minZ);

									nv.setWidth(scale * 100);
									nv.setHeight(scale * 100);
									gv.setNodeDoubleProperty(nv.getRootGraphIndex(),
									                         GraphView.NODE_Z_POSITION, scale * 100);
								}

								nvi = gv.getNodeViewsIterator();

								while (nvi.hasNext()) {
									NodeView nv = (NodeView) nvi.next();
									gv.addNodeView("phoebe.util.P3DNode", nv.getRootGraphIndex());
								}
							}
						});
				}
			}));

		dialog.getContentPane().add(main);
		dialog.pack();
		dialog.setVisible(true);
	}
}
