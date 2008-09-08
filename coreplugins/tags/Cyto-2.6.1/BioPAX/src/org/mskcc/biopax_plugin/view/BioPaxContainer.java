// $Id: BioPaxContainer.java,v 1.7 2006/06/15 22:06:02 grossb Exp $
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

import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;
import org.mskcc.biopax_plugin.mapping.MapBioPaxToCytoscape;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.CyAttributes;


/**
 * Container for all BioPax UI Components.
 * <p/>
 * Currently includes:
 * <UL>
 * <LI>BioPaxDetailsPanel
 * <LI>BioPaxLegendPanel
 * <LI>AboutPanel
 * </UL>
 *
 * @author Ethan Cerami
 */
public class BioPaxContainer extends JPanel {
	/**
	 * CytoPanel Location of this Panel
	 */
	public static final int CYTO_PANEL_LOCATION = SwingConstants.EAST;
	private BioPaxDetailsPanel bpDetailsPanel;
	private NetworkListener networkListener;
	private static BioPaxContainer bioPaxContainer;
    private JEditorPane label;
    private JPanel cards;

    private final static String DETAILS_CARD = "DETAILS";
    private final static String LEGEND_BIOPAX_CARD = "LEGEND_BIOPAX";
    private final static String LEGEND_BINARY_CARD = "LEGEND_BINARY";

    /**
	 * Private Constructor.
	 */
	private BioPaxContainer() {
        cards = new JPanel(new CardLayout());
        bpDetailsPanel = new BioPaxDetailsPanel();
        LegendPanel bioPaxLegendPanel = new LegendPanel(LegendPanel.BIOPAX_LEGEND);
        LegendPanel binaryLegendPanel = new LegendPanel(LegendPanel.BINARY_LEGEND);

        cards.add (bpDetailsPanel, DETAILS_CARD);
        cards.add (bioPaxLegendPanel, LEGEND_BIOPAX_CARD);
        cards.add (binaryLegendPanel, LEGEND_BINARY_CARD);
        
        this.setLayout(new BorderLayout());
		this.add(cards, BorderLayout.CENTER);

        label = new JEditorPane ("text/html", "<a href='LEGEND'>Visual Legend</a>");
        label.setEditable(false);
        label.setOpaque(false);
        label.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        label.addHyperlinkListener(new HyperlinkListener() {

            public void hyperlinkUpdate(HyperlinkEvent hyperlinkEvent) {
                if (hyperlinkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String name = hyperlinkEvent.getDescription();
                    if (name.equalsIgnoreCase("LEGEND")) {
                        showLegend();
                    } else {
                        showDetails();
                    }
                }
            }
        });

        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        Font font = label.getFont();
        Font newFont = new Font (font.getFamily(), font.getStyle(), font.getSize()-2);
        label.setFont(newFont);
        label.setBorder(new EmptyBorder(5,3,3,3));
        this.add(label, BorderLayout.SOUTH);
        this.networkListener = new NetworkListener(bpDetailsPanel);
	}

    /**
     * Show Details Panel.
     */
    public void showDetails() {
        CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, DETAILS_CARD);
        label.setText("<a href='LEGEND'>Visual Legend</a>");
    }

    /**
     * Show Legend Panel.
     */
    public void showLegend() {
        CardLayout cl = (CardLayout)(cards.getLayout());
        CyNetwork network = Cytoscape.getCurrentNetwork();
        CyAttributes networkAttributes = Cytoscape.getNetworkAttributes();
        Boolean isBioPaxNetwork = networkAttributes.getBooleanAttribute(network.getIdentifier(),
                MapBioPaxToCytoscape.BIOPAX_NETWORK);
        if (isBioPaxNetwork != null) {
            cl.show(cards, LEGEND_BIOPAX_CARD);
        } else {
            cl.show(cards, LEGEND_BINARY_CARD);
        }
        label.setText("<a href='DETAILS'>View Details</a>");
    }

    /**
	 * Gets Instance of Singleton.
	 *
	 * @return BioPaxContainer Object.
	 */
	public static BioPaxContainer getInstance() {
		if (bioPaxContainer == null) {
			bioPaxContainer = new BioPaxContainer();
		}

		return bioPaxContainer;
	}

	/**
	 * Gets the Embedded BioPax Details Panel.
	 *
	 * @return BioPaxDetailsPanel Object.
	 */
	public BioPaxDetailsPanel getBioPaxDetailsPanel() {
		return bpDetailsPanel;
	}

	/**
	 * Gets the Network Listener Object.
	 *
	 * @return Network Listener Object.
	 */
	public NetworkListener getNetworkListener() {
		return networkListener;
	}
}
