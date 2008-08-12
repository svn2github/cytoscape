/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.plugin.cheminfo.table;

import giny.view.NodeView;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.plugin.cheminfo.ChemInfoPlugin.AttriType;
import cytoscape.plugin.cheminfo.structure.StructureDepictor;
import cytoscape.view.CyNetworkView;

public abstract class ChemTable extends JTable implements ClipboardOwner, SelectEventListener {
	protected JPopupMenu popupMenu;

	protected String networkID;

	protected int xc;

	protected int yc;
	
	protected String attribute;
	protected AttriType attrType;

	public ChemTable(ChemTableModel model, String networkID, String attribute, AttriType attrType) {
		super(model);
		this.networkID = networkID;
		this.attribute = attribute;
		this.attrType = attrType;
		setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		setupPopup();
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				showPopup(e);
			}

			public void mousePressed(MouseEvent e) {
				showPopup(e);
			}

			private void showPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					// store the position where popup was called
					xc = e.getX();
					yc = e.getY();
					Point point = new Point(xc, yc);
					int rc = rowAtPoint(point);
					int cc = columnAtPoint(point);

					((JMenuItem) popupMenu.getComponent(1)).setEnabled(true);
					((JMenuItem) popupMenu.getComponent(0)).setEnabled(true);

					// show the popup
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	public void sortAllRowsBy(int colIndex, boolean ascending) {
		List data = ((ChemTableModel) getModel()).getRecords();
		Collections.sort(data, new ColumnSorter(colIndex, ascending));
		((ChemTableModel) getModel()).fireTableStructureChanged();
	}

	protected abstract void setupPopup();

	
	protected abstract void removeFromTable();

	protected void copySelected() {
		List values = ((ChemTableModel) getModel())
				.getValuesAt(getSelectedRows());
		Iterator it = values.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			List row = (List) it.next();
			Iterator lit = row.iterator();
			sb.append(lit.next());
			sb.append('\t');
			sb.append(lit.next());
			sb.append('\n');
		}
		StringSelection stringSelection = new StringSelection(sb.toString());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, this);
	}

	/**
	 * Empty implementation of the ClipboardOwner interface.
	 */
	public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
		// do nothing
	}
	
	public abstract void showTableDialog(String title);
	
	public String getNetworkID() {
		return networkID;
	}

	public void setNetworkID(String networkID) {
		this.networkID = networkID;
	}

	class ChemTableDialog extends JDialog {
		public void dispose() {
			Cytoscape.getNetwork(networkID).removeSelectEventListener(ChemTable.this);
			super.dispose();
		}
	}

}
