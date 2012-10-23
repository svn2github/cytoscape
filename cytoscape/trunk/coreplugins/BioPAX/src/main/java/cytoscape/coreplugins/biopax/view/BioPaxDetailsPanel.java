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
package cytoscape.coreplugins.biopax.view;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;

import cytoscape.coreplugins.biopax.MapBioPaxToCytoscape;
import cytoscape.coreplugins.biopax.action.LaunchExternalBrowser;
import cytoscape.coreplugins.biopax.util.BioPaxUtil;
import cytoscape.data.CyAttributes;
import cytoscape.data.Semantics;
import cytoscape.logger.CyLogger;

import org.biopax.paxtools.model.level3.PhysicalEntity;

import java.awt.*;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.*;


/**
 * BioPAX Details Panel.
 *
 * @author Ethan Cerami.
 */
public class BioPaxDetailsPanel extends JPanel {
	public static final CyLogger log = CyLogger.getLogger(BioPaxDetailsPanel.class);
	
	/**
	 * Foreground Color.
	 */
	public static final Color FG_COLOR = new Color(75, 75, 75);
	private JScrollPane scrollPane;
	private JTextPane textPane;
	private CyAttributes nodeAttributes;

	/**
	 * Constructor.
	 */
	public BioPaxDetailsPanel() {
		textPane = new JTextPane();

		//  Set Editor Kit that is capable of handling long words
		MyEditorKit kit = new MyEditorKit();
		textPane.setEditorKit(kit);
        modifyStyleSheetForSingleDocument(textPane);

        textPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        textPane.setBorder(new EmptyBorder (5,5,5,5));
        textPane.setContentType("text/html");
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new LaunchExternalBrowser());
		resetText();

		scrollPane = new JScrollPane(textPane);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		this.setLayout(new BorderLayout());
		this.add(scrollPane, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(300, 300));
		this.setMaximumSize(new Dimension(300, 300));

		// get a ref to node attributes
		nodeAttributes = Cytoscape.getNodeAttributes();

	}

    public static void modifyStyleSheetForSingleDocument(JTextPane textPane) {
        HTMLDocument htmlDoc = (HTMLDocument) textPane.getDocument();
        StyleSheet styleSheet = htmlDoc.getStyleSheet();
        styleSheet.addRule("h2 {color: #663333; font-size: 120%; font-weight: bold; "
                + "margin-bottom:3px}");
        styleSheet.addRule("h3 {color: #663333; font-size: 105%; font-weight: bold;"
                + "margin-bottom:7px}");
        styleSheet.addRule("ul { list-style-type: none; margin-left: 5px; "
                + "padding-left: 1em;	text-indent: -1em;}");
        styleSheet.addRule("h4 {color: #66333; font-weight: bold; margin-bottom:3px;}");
        styleSheet.addRule(".link {color:blue; text-decoration: underline;}");
        styleSheet.addRule(".description {font-size: 85%;}");
        styleSheet.addRule(".rule {font-size: 90%; font-weight:bold}");
        styleSheet.addRule(".excerpt {font-size: 90%;}");
    }

    /**
	 * Resets the Text to "Select a node to view details...";
	 */
	public void resetText() {
		StringBuffer temp = new StringBuffer();
		temp.append("<html><body>");
		temp.append("Select a node to view details...");
		temp.append("</body></html>");
		textPane.setText(temp.toString());
	}

	/**
	 * Resets the Text to the specified Text String.
	 *
	 * @param text Text String.
	 */
	public void resetText(String text) {
		StringBuffer temp = new StringBuffer();
		temp.append("<html><body>");
		temp.append(text);
		temp.append("</body></html>");
		textPane.setText(temp.toString());
	}

	/**
	 * Shows details about the BioPAX Entity with the specified RDF ID.
	 *
	 * @param nodeID RDF ID String.
	 */
	public void showDetails(String nodeID) {
        String stringRef;

		StringBuffer buf = new StringBuffer("<html><body>");

        // name, shortname
        stringRef = nodeAttributes.getStringAttribute(nodeID, Semantics.CANONICAL_NAME);

        String shortName = nodeAttributes.getStringAttribute(nodeID, "displayName");
        if ((shortName != null) && (shortName.length() > 0)) {
                buf.append("<h2>").append(shortName).append("</h2>");
        } else if (stringRef != null && stringRef.length() > 0) {
            buf.append("<h2>").append(stringRef).append("</h2>");
        }

        String type = nodeAttributes.getStringAttribute(nodeID, MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE);
        buf.append("<h3>").append(type).append("</h3>");

        // organism
        stringRef = null;
        stringRef = nodeAttributes.getStringAttribute(nodeID, "entityReference/organism/displayName");
        if(stringRef == null)
        	stringRef = nodeAttributes.getStringAttribute(nodeID, "entityReference/organism/standardName");
        if(stringRef == null)
        	stringRef = nodeAttributes.getStringAttribute(nodeID, "organism/displayName");
        if(stringRef == null)
        	stringRef = nodeAttributes.getStringAttribute(nodeID, "organism/standardName");
        if (stringRef != null) {
            buf.append("<h4>").append(stringRef).append("</h4>");
        }

        //  Add (optional) cPath Link
        addCPathLink(nodeID, buf);

        // synonyms
        addAttributeList(nodeID, "name;entityReference/name;memberPhysicalEntity/name;memberEntityReference/name", buf, "Synonyms:");
        
		// cellular location
        stringRef = null;
        stringRef = nodeAttributes.getStringAttribute(nodeID, "cellularLocation");
        if (stringRef != null) {
           buf.append("<h4>").append("Cellular Location: ").append(stringRef).append("</h4>");
        }		
//        addAttributeList(nodeID, MapBioPaxToCytoscape.BIOPAX_CELLULAR_LOCATIONS, buf, "Cellular Location: ");

		// chemical modification
		addAttributeList(nodeID, MapBioPaxToCytoscape.BIOPAX_CHEMICAL_MODIFICATIONS_LIST, buf, "Chemical Modifications:");

		// links
		addLinks(nodeID, buf);

		buf.append("</body></html>");
		textPane.setText(buf.toString());
		textPane.setCaretPosition(0);
    }

    private void addCPathLink(String nodeID, StringBuffer buf) {
        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        String serverName = networkAttributes.getStringAttribute(cyNetwork.getIdentifier(),
                "CPATH_SERVER_NAME");
        String serverDetailsUrl = networkAttributes.getStringAttribute(cyNetwork.getIdentifier(),
                "CPATH_SERVER_DETAILS_URL");
        if (serverName != null && serverDetailsUrl != null) {
            String type = nodeAttributes.getStringAttribute(nodeID,
                    MapBioPaxToCytoscape.BIOPAX_ENTITY_TYPE);
            if (BioPaxUtil.getSubclassNames(PhysicalEntity.class).contains(type)) {
                String url = serverDetailsUrl + nodeID;
                buf.append ("<h3><A href='" + url + "'>" + serverName + ": " + nodeID + "</A>");
            }
        }
    }


    private void addLinks(String nodeID, StringBuffer buf) {
		addAttributeList(nodeID, MapBioPaxToCytoscape.BIOPAX_UNIFICATION_REFERENCES 
				+ ";" + MapBioPaxToCytoscape.BIOPAX_RELATIONSHIP_REFERENCES, buf, "Links:");
		addAttributeList(nodeID, MapBioPaxToCytoscape.BIOPAX_PUBLICATION_REFERENCES, buf, "Publications:");
         
        addIHOPLinks(nodeID, buf);
	}

	private void addAttributeList(String nodeID, String attributes, StringBuffer buf, String label) 
	{
		StringBuilder displayString = new StringBuilder();

		Set<String> set = new TreeSet<String>();
		for (String attribute : attributes.split(";")) {
			@SuppressWarnings("unchecked")
			List<String> list = nodeAttributes.getListAttribute(nodeID,
					attribute);
			if (list != null)
				set.addAll(list);
		}
		
		for (String listItem : set) {
			if (listItem != null && listItem.length() > 0) {
				displayString.append("<li> - " + listItem);
				displayString.append("</li>");
			}
		}

		// do we have a string to display ?
		if (displayString.length() > 0) {
			if(label != null)
				appendHeader(label, buf);
			buf.append("<ul>");
			appendData(displayString.toString(), buf, false);
			buf.append("</ul>");
		}
	}

	private void appendHeader(String header, StringBuffer buf) {
		buf.append("<h4>");
		buf.append(header);
		buf.append("</h4>");
	}

	private void appendData(String data, StringBuffer buf, boolean appendBr) {
		buf.append(data);
		if (appendBr) {
			buf.append("<br/>");
		}
	}

	private void addIHOPLinks(String nodeID, StringBuffer buf) {
		String ihopLinks = nodeAttributes.getStringAttribute(nodeID,
                     MapBioPaxToCytoscape.BIOPAX_IHOP_LINKS);

		if (ihopLinks != null) {
			appendData(ihopLinks, buf, false);
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
