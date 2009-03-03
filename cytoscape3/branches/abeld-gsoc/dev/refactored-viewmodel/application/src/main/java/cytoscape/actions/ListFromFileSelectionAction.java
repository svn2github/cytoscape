/*
  File: ListFromFileSelectionAction.java

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

// $Revision: 13022 $
// $Date: 2008-02-11 13:59:26 -0800 (Mon, 11 Feb 2008) $
// $Author: mes $
package cytoscape.actions;

import cytoscape.CyNetworkManager;
import cytoscape.data.Semantics;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 *
 */
public class ListFromFileSelectionAction extends CytoscapeAction {
	private final static long serialVersionUID = 1202339869837208L;

	private FileUtil fileUtil;
	/**
	 * Creates a new ListFromFileSelectionAction object.
	 */
	public ListFromFileSelectionAction(CyNetworkManager netmgr, FileUtil fileUtil) {
		super("From File...",netmgr);
		this.fileUtil = fileUtil;
		setPreferredMenu("Select.Nodes");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		boolean cancelSelectionAction = !selectFromFile();
		netmgr.getCurrentNetworkView().updateView();
	}

	private boolean selectFromFile() {
		// get the file name
		final String name;

		try {
			name = fileUtil.getFile("Load node selection file", FileUtil.LOAD).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return false;
		}

		CyNetwork network = netmgr.getCurrentNetwork();

		try {
			FileReader fin = new FileReader(name);
			BufferedReader bin = new BufferedReader(fin);
			List<String> fileNodes = new ArrayList<String>();
			String s;

			while ((s = bin.readLine()) != null) {
				String trimName = s.trim();

				if (trimName.length() > 0) {
					fileNodes.add(trimName);
				}
			}

			fin.close();

			// loop through all the node of the graph
			// selecting those in the file
			List<CyNode> nodeList = network.getNodeList();
			CyNode[] nodes = (CyNode[]) nodeList.toArray(new CyNode[0]);

			for (int i = 0; i < nodes.length; i++) {
				CyNode node = nodes[i];
				boolean select = false;
				List synonyms = Semantics.getAllSynonyms(node, network);

				for (Iterator synI = synonyms.iterator(); synI.hasNext();) {
					if (fileNodes.contains((String) synI.next())) {
						select = true;

						break;
					}
				}

				if (select) {
					//GraphView view = netmgr.getCurrentNetworkView();
					//NodeView nv = view.getNodeView(node.getRootGraphIndex());
					//nv.setSelected(true);
					node.attrs().set("selected",true);
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.toString(), "Error Reading \"" + name + "\"",
			                              JOptionPane.ERROR_MESSAGE);

			return false;
		}

		return true;
	}

    public void menuSelected(MenuEvent e) {
        enableForNetwork();
    }
}
