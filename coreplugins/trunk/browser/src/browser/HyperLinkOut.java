
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

package browser;

import cytoscape.Cytoscape;

import cytoscape.data.ontology.DBCrossReferences;

import cytoscape.util.OpenBrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;


/**
 *
 */
public class HyperLinkOut extends JMenu {
	private static final String TITLE = "Search";
	private String value;
	private DBCrossReferences xref;
	private Map<String, List> structure;

	/**
	 * Creates a new HyperLinkOut object.
	 *
	 * @param value  DOCUMENT ME!
	 */
	public HyperLinkOut(String value) {
		this(value, null);
	}

	/**
	 * Creates a new HyperLinkOut object.
	 *
	 * @param value  DOCUMENT ME!
	 * @param menuStructure  DOCUMENT ME!
	 */
	public HyperLinkOut(String value, Map<String, List> menuStructure) {
		this.value = value;
		this.structure = menuStructure;

		if (structure == null) {
			structure = getDefaultMenu();
		}

		if (Cytoscape.getOntologyServer() != null) {
			xref = Cytoscape.getOntologyServer().getCrossReferences();
			setText("Search " + value + " on the web...");
			buildLinks();
		}
	}

	private Map<String, List> getDefaultMenu() {
		Map<String, List> def = new HashMap<String, List>();

		List<String> se = new ArrayList<String>();
		se.add("Google");
		se.add("Ask");
		def.put("Search Engines", se);

		List<String> bio = new ArrayList<String>();
		bio.add("SGD");
		bio.add("GO");
		bio.add("MGD");
		bio.add("Reactome");
		def.put("Biological Databases", bio);

		return def;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void search() {
	}

	private void buildLinks() {
		Set<String> dbNames = xref.getDBNames();
		JMenuItem dbLink;
		String fullName;

		JMenuItem directLink = new JMenuItem("Open as URL");
		directLink.setToolTipText(value);
		directLink.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					OpenBrowser.openURL(value);
				}
			});
		this.add(directLink);
		
		JMenu cat;

		for (String category : structure.keySet()) {
			cat = new JMenu(category);

			List<String> children = structure.get(category);

			for (String name : children) {
				fullName = xref.getDBReference(name).getFullName();
				dbLink = new JMenuItem(name);
				dbLink.setToolTipText(fullName);
				dbLink.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							openBrowser(e.getActionCommand());
						}
					});
				cat.add(dbLink);
			}

			this.add(cat);
		}
	}

	private void openBrowser(String dbName) {
		try {
			OpenBrowser.openURL(xref.getDBReference(dbName).getQueryURL(value).toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
