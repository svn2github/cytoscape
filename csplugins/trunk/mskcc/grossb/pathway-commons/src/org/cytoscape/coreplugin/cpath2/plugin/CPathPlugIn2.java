// $Id: CPathPlugIn2.java,v 1.8 2007/04/27 19:18:48 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
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
package org.cytoscape.coreplugin.cpath2.plugin;

// imports

import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginProperties;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import org.cytoscape.coreplugin.cpath2.http.HTTPServer;
import org.cytoscape.coreplugin.cpath2.mapping.MapCPathToCytoscape;
import org.cytoscape.coreplugin.cpath2.util.NetworkListener;
import org.cytoscape.coreplugin.cpath2.web_service.CPathWebService;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.view.cPathSearchPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.io.IOException;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * The cPath plugin class.  It gets called by Cytoscape's plugin manager
 * to install inself.  The main job of this guy is to instantiate our http server.
 *
 * @author Benjamin Gross.
 */
public class CPathPlugIn2 extends CytoscapePlugin {

    /**
     * Constructor.
     */
    public CPathPlugIn2() throws IOException {

        String debugProperty = System.getProperty("DEBUG");
        Boolean debug = (debugProperty != null && debugProperty.length() > 0) &&
                new Boolean(debugProperty.toLowerCase());
        initProperties();

        // to catch network creation events - to setup context menu
        NetworkListener networkListener = new NetworkListener();

        // create our http server and start its thread
        new HTTPServer(HTTPServer.DEFAULT_PORT,
                new MapCPathToCytoscape(networkListener), debug).start();

        CytoscapeDesktop desktop = Cytoscape.getDesktop();
        final CytoPanel cytoPanelWest = desktop.getCytoPanel(SwingConstants.EAST);

        CPathWebService webApi = CPathWebService.getInstance();

        final cPathSearchPanel pcPanel = new cPathSearchPanel(webApi);

        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Search", pcPanel);

        JPanel configPanel = createConfigPanel();
        tabbedPane.add("Options", configPanel);


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                cytoPanelWest.add(CPathProperties.getInstance().getCPathServerName(), tabbedPane);
                cytoPanelWest.setState(CytoPanelState.DOCK);
            }
        });
    }

    private void initProperties() throws IOException {
        PluginProperties pluginProperties = new PluginProperties(this);
        CPathProperties cpathProperties = CPathProperties.getInstance();
        cpathProperties.initProperties(pluginProperties);
    }

    private JPanel createConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setBorder(new TitledBorder("Retrieval Options"));
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        final JRadioButton button1 = new JRadioButton("Full Model");
        button1.setSelected(true);
        final JRadioButton button2 = new JRadioButton("Reduced Binary Model");
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);
        configPanel.add(button1);
        configPanel.add(button2);

        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPathProperties config = CPathProperties.getInstance();
                config.setDownloadMode(CPathProperties.DOWNLOAD_FULL_BIOPAX);
            }
        });
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                CPathProperties config = CPathProperties.getInstance();
                config.setDownloadMode(CPathProperties.DOWNLOAD_REDUCED_BINARY_SIF);
            }
        });
        return configPanel;
    }
}
