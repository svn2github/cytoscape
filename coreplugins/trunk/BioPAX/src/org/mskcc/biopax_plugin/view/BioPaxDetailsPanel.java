// $Id: BioPaxDetailsPanel.java,v 1.24 2006/10/09 20:48:04 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.view;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import org.mskcc.biopax_plugin.action.LaunchExternalBrowser;
import org.mskcc.biopax_plugin.mapping.MapNodeAttributes;
import org.mskcc.biopax_plugin.style.BioPaxVisualStyleUtil;
import org.mskcc.biopax_plugin.util.biopax.BioPaxPlainEnglish;

import java.awt.*;

import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.ParagraphView;


/**
 * BioPAX Details Panel.
 *
 * @author Ethan Cerami.
 */
public class BioPaxDetailsPanel extends JPanel {
	/**
	 * Background Color.
	 */
	public static final Color BG_COLOR = new Color(236, 233, 216);

	/**
	 * Foreground Color.
	 */
	public static final Color FG_COLOR = new Color(75, 75, 75);
	private JScrollPane scrollPane;
	private JTextPane textPane;
	private Font defaultFont;
	private CyAttributes nodeAttributes;

	/**
	 * Constructor.
	 */
	public BioPaxDetailsPanel() {
		JLabel label = new JLabel();
		defaultFont = label.getFont();
		textPane = new JTextPane();

		//  Set Editor Kit that is capable of handling long words
		MyEditorKit kit = new MyEditorKit();
		textPane.setEditorKit(kit);

		textPane.setBackground(BG_COLOR);
		textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new LaunchExternalBrowser(this));
		resetText();

		scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBackground(BG_COLOR);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(300, 300));
		this.setMaximumSize(new Dimension(300, 300));

		// get a ref to node attributes
		nodeAttributes = Cytoscape.getNodeAttributes();
	}

	/**
	 * Resets the Text to "Select a node to view details...";
	 */
	public void resetText() {
		StringBuffer temp = new StringBuffer();
		temp.append("<HTML><BODY>");
		temp.append("<FONT FACE=\"" + defaultFont.getFontName() + "\">");
		temp.append("Select a node to view details...");
		temp.append("</FONT>");
		temp.append("</BODY></HTML>");
		textPane.setText(temp.toString());
	}

	/**
	 * Resets the Text to the specified Text String.
	 *
	 * @param text Text String.
	 */
	public void resetText(String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("<HTML><BODY>");
		temp.append("<FONT FACE=\"" + defaultFont.getFontName() + "\">");
		temp.append(text);
		temp.append("</FONT>");
		temp.append("</BODY></HTML>");
		textPane.setText(temp.toString());
	}

	/**
	 * Shows details about the BioPAX Entity with the specified RDF ID.
	 *
	 * @param nodeID RDF ID String.
	 */
	public void showDetails(String nodeID) {
		String stringRef;

		StringBuffer buf = new StringBuffer("<HTML><BODY BGCOLOR=LIGHTGRAY>");
		buf.append("<TABLE WIDTH=100% CELLPADDING=5 CELLSPACING=5>" + "<TR BGCOLOR='ECE9D8'><TD>");

		// pathway membership
		addPathwayMembership(nodeID, buf);

		// type
		addType(nodeID, buf);

		// cellular location
		addAttributeList(nodeID, MapNodeAttributes.BIOPAX_CELLULAR_LOCATIONS,
		                 "Cellular Location(s)", buf);

		// node label
		stringRef = nodeAttributes.getStringAttribute(nodeID,
		                                              BioPaxVisualStyleUtil.BIOPAX_NODE_LABEL);
		addField("Label", stringRef, buf);

		// name, shortname
		stringRef = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_NAME);

		String shortName = nodeAttributes.getStringAttribute(nodeID,
		                                                     MapNodeAttributes.BIOPAX_SHORT_NAME);

		if ((shortName != null) && (shortName.length() > 0) && !stringRef.equals(shortName)) {
			addField("Short Name", shortName, buf);
			addField("Name", stringRef, buf);
		} else {
			addField("Name", stringRef, buf);
		}

		// chemical modification
		addAttributeList(nodeID, MapNodeAttributes.BIOPAX_CHEMICAL_MODIFICATIONS_LIST,
		                 "Chemical Modifications", buf);

		// synonyms
		addAttributeList(nodeID, MapNodeAttributes.BIOPAX_SYNONYMS, "Synonyms", buf);

		// organism
		stringRef = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_ORGANISM_NAME);
		addField("Organism", stringRef, buf);

		// comment
		stringRef = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_COMMENT);
		addField("Comment", stringRef, buf);

		// publication references
		addPublicationXRefs(nodeID, buf);

		// unification references
		addUnificationReferences(nodeID, buf);

		// relationship references
		addRelationshipReferences(nodeID, buf);

		// availability
		stringRef = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_AVAILABILITY);
		addField("Availability", stringRef, buf);

		// data source
		addDataSource(nodeID, buf);

		// ihop links
		addIHOPLinks(nodeID, buf);

		// identifier
		//stringRef = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_RDF_ID);
		//addField("Identifier", stringRef, buf);

		// close up the table
		buf.append("</TD></TR></TABLE>");
		buf.append("</BODY></HTML>");
		textPane.setText(buf.toString());
		textPane.setCaretPosition(0);

		//  If the containing parent is a BioPaxDetailsWindow, show it.
		//  This only applies in Cytoscape 2.1 and local testing
		Container parent = this.getTopLevelAncestor();

		if (parent instanceof BioPaxWindow) {
			JFrame parentFrame = (BioPaxWindow) parent;
			parentFrame.setVisible(true);
		}
	}

	private void addPathwayMembership(String nodeID, StringBuffer buf) {
		String pathwayMembership = nodeAttributes.getStringAttribute(nodeID,
		                                                             MapNodeAttributes.BIOPAX_PATHWAY_NAME);

		if (pathwayMembership != null) {
			appendHeader("Pathway", buf);
			appendData(pathwayMembership, buf, true);
		}
	}

	private void addType(String nodeID, StringBuffer buf) {
		String type = nodeAttributes.getStringAttribute(nodeID, MapNodeAttributes.BIOPAX_ENTITY_TYPE);
		addField("Type", type, buf);
	}

	private void addDataSource(String nodeID, StringBuffer buf) {
		String dataSources = nodeAttributes.getStringAttribute(nodeID,
		                                                       MapNodeAttributes.BIOPAX_DATA_SOURCES);

		if (dataSources != null) {
			appendHeader("Data Sources", buf);
			appendData(dataSources, buf, true);
		}
	}

	private void addPublicationXRefs(String nodeID, StringBuffer buf) {
		String xrefs = nodeAttributes.getStringAttribute(nodeID,
		                                                 MapNodeAttributes.BIOPAX_PUBLICATION_REFERENCES);

		if (xrefs != null) {
			appendHeader("Publication References", buf);
			appendData(xrefs, buf, true);
		}
	}

	private void addUnificationReferences(String nodeID, StringBuffer buf) {
		String xrefs = nodeAttributes.getStringAttribute(nodeID,
		                                                 MapNodeAttributes.BIOPAX_UNIFICATION_REFERENCES);

		if (xrefs != null) {
			appendHeader("Unification References", buf);
			appendData(xrefs, buf, true);
		}
	}

	private void addRelationshipReferences(String nodeID, StringBuffer buf) {
		String xrefs = nodeAttributes.getStringAttribute(nodeID,
		                                                 MapNodeAttributes.BIOPAX_RELATIONSHIP_REFERENCES);

		if (xrefs != null) {
			appendHeader("Relationship References", buf);
			appendData(xrefs, buf, true);
		}
	}

	private void addAttributeList(String nodeID, String attribute, String label, StringBuffer buf) {
		String displayString = null;
		List list = nodeAttributes.getAttributeList(nodeID, attribute);

		if (list != null) {
			for (int lc = 0; lc < list.size(); lc++) {
				String listItem = (String) list.get(lc);

				if ((listItem != null) && (listItem.length() > 0)) {
					displayString = (displayString == null) ? "" : displayString;

					String plainEnglish = BioPaxPlainEnglish.getTypeInPlainEnglish(listItem);
					displayString += ("- " + ((plainEnglish != null) ? plainEnglish : listItem));

					if (lc < (list.size() - 1)) {
						displayString += "<BR>";
					}
				}
			}
		}

		// do we have a string to display ?
		if (displayString != null) {
			appendHeader(label, buf);
			appendData(displayString, buf, true);
		}
	}

	private void addField(String header, String value, StringBuffer buf) {
		if (value != null) {
			appendHeader(header, buf);
			appendData(value, buf, true);
		}
	}

	private void appendHeader(String header, StringBuffer buf) {
		buf.append("<FONT COLOR=4B4B4B FACE=\"" + defaultFont.getFontName() + "\">");
		buf.append(header);
		buf.append("</FONT>");
	}

	private void appendData(String data, StringBuffer buf, boolean appendBr) {
		buf.append("<TABLE WIDTH=95% CELLSPACING=3 CELLPADDING=3>");
		buf.append("<TR BGCOLOR=#FFFFFF><TD>");
		buf.append("<FONT FACE=\"" + defaultFont.getFontName() + "\">");
		buf.append(data);
		buf.append("</FONT>");
		buf.append("</TD></TR></TABLE>");

		if (appendBr) {
			buf.append("<BR>");
		}
	}

	private void addIHOPLinks(String nodeID, StringBuffer buf) {
		String ihopLinks = nodeAttributes.getStringAttribute(nodeID,
		                                                     MapNodeAttributes.BIOPAX_IHOP_LINKS);

		if (ihopLinks != null) {
			appendData(ihopLinks, buf, true);
		}
	}
}


/**
 * Editor Kit which is capable of handling long words.
 * <p/>
 * Here is a description of the problem:
 * By default, JTextPane uses an InlineView. It was designed to avoid
 * wrapping.  Text can't be broken if it doesn't contain spaces.
 * <p/>
 * This is a real problem with the BioPaxDetailsPanel, as BioPax Unique
 * Identifiers can get really long, and this prevents the user from
 * resizing the CytoPanel to any arbitrary size.
 * <p/>
 * The solution below comes from:
 * http://joust.kano.net/weblog/archives/000074.html
 * <p/>
 * (The following code is released in the public domain.)
 *
 * @author Joust Team.
 */
class MyEditorKit extends HTMLEditorKit {
	/**
	 * Gets the ViewFactor Object.
	 *
	 * @return View Factor Object.
	 */
	public ViewFactory getViewFactory() {
		return new MyViewFactory(super.getViewFactory());
	}

	/**
	 * Word Splitting Paragraph View.
	 */
	private static class WordSplittingParagraphView extends ParagraphView {
		public WordSplittingParagraphView(javax.swing.text.Element elem) {
			super(elem);
		}

		protected SizeRequirements calculateMinorAxisRequirements(int axis, SizeRequirements r) {
			SizeRequirements sup = super.calculateMinorAxisRequirements(axis, r);
			sup.minimum = 1;

			return sup;
		}
	}

	/**
	 * View Factory.
	 */
	private static class MyViewFactory implements ViewFactory {
		private final ViewFactory parent;

		/**
		 * Constructor.
		 *
		 * @param parent ViewFactory Object.
		 */
		public MyViewFactory(ViewFactory parent) {
			this.parent = parent;
		}

		/**
		 * Creates a Text Element View.
		 *
		 * @param elem Element Object.
		 * @return View Object.
		 */
		public View create(javax.swing.text.Element elem) {
			AttributeSet attr = elem.getAttributes();
			Object name = attr.getAttribute(StyleConstants.NameAttribute);

			if ((name == HTML.Tag.P) || (name == HTML.Tag.IMPLIED)) {
				return new WordSplittingParagraphView(elem);
			}

			return parent.create(elem);
		}
	}
}
