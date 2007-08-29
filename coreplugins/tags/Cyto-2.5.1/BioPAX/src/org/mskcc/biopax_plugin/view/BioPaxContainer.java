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

import org.mskcc.biopax_plugin.plugin.BioPaxPlugIn;
import org.mskcc.biopax_plugin.util.cytoscape.NetworkListener;

import java.awt.*;

import java.net.URL;

import javax.swing.*;


/**
 * Container for all BioPax UI Components.
 * <p/>
 * Currently includes:
 * <UL>
 * <LI>BioPaxDetailsPanel
 * <LI>LegendPanel
 * <LI>AboutPanel
 * </UL>
 *
 * @author Ethan Cerami
 */
public class BioPaxContainer extends JPanel {
	/**
	 * CytoPanel Location of this Panel
	 */
	public static final int CYTO_PANEL_LOCATION = SwingConstants.SOUTH;
	private JTabbedPane tabbedPane;
	private BioPaxDetailsPanel bpDetailsPanel;
	private NetworkListener networkListener;
	private static BioPaxContainer bioPaxContainer;

	/**
	 * Private Constructor.
	 */
	private BioPaxContainer() {
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);

		URL url1 = BioPaxDetailsPanel.class.getResource("resources/glasses.gif");
		Icon icon1 = new ImageIcon(url1);
		URL url2 = BioPaxDetailsPanel.class.getResource("resources/legend.gif");
		Icon icon2 = new ImageIcon(url2);
		URL url3 = BioPaxDetailsPanel.class.getResource("resources/info.gif");
		Icon icon3 = new ImageIcon(url3);

		bpDetailsPanel = new BioPaxDetailsPanel();
		tabbedPane.addTab("Node Details", icon1, bpDetailsPanel,
		                  "Select a node to view complete node details");
		tabbedPane.addTab("Visual Legend", icon2, new LegendPanel(BioPaxDetailsPanel.BG_COLOR),
		                  "View Visual Legend");
		tabbedPane.addTab("About", icon3,
		                  new AboutPanel("BioPAX Extension", BioPaxPlugIn.VERSION_MAJOR_NUM,
		                                 BioPaxPlugIn.VERSION_MINOR_NUM, BioPaxDetailsPanel.BG_COLOR),
		                  "About the BioPAX Extension");
		this.setLayout(new BorderLayout());
		this.add(tabbedPane);
		this.networkListener = new NetworkListener(bpDetailsPanel);
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
