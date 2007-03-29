/*
  File: PrintAction.java

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

//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.ding.DingNetworkView;
import cytoscape.ding.DingNetworkView;

import cytoscape.giny.*;

import cytoscape.util.*;
import cytoscape.util.CytoscapeAction;

import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;

import ding.view.DGraphView;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;

import javax.swing.event.MenuEvent;


/**
 *
 */
public class PrintAction extends CytoscapeAction {
	/**
	 *
	 */
	public final static String MENU_LABEL = "Print...";

	/**
	 * Creates a new PrintAction object.
	 */
	public PrintAction() {
		super(MENU_LABEL);
		setPreferredMenu("File");
		setAcceleratorCombo(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		CyNetworkView curr = Cytoscape.getCurrentNetworkView();
		InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager()
		                                      .getInternalFrameComponent(curr);
		PrinterJob printJob = PrinterJob.getPrinterJob();

		// Export text as shape/font based on user's setting
		DGraphView theViewToPrint = (DingNetworkView) Cytoscape.getCurrentNetworkView();
		boolean exportTextAsShape = new Boolean(CytoscapeInit.getProperties()
		                                                     .getProperty("exportTextAsShape"))
		                                                                                                .booleanValue();
		theViewToPrint.setPrintingTextAsShape(exportTextAsShape);

		printJob.setPrintable(ifc);

		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
	} // actionPerformed

	public void menuSelected(MenuEvent e) {
		enableForNetworkAndView();
	}
}
