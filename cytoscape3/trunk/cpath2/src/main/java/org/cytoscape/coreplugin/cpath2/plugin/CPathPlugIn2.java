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

import cytoscape.data.webservice.WebServiceClientManager;
import org.cytoscape.coreplugin.cpath2.http.HTTPServer;
import org.cytoscape.coreplugin.cpath2.mapping.MapCPathToCytoscape;
import org.cytoscape.coreplugin.cpath2.util.NetworkListener;
import org.cytoscape.coreplugin.cpath2.web_service.CPathProperties;
import org.cytoscape.coreplugin.cpath2.web_service.CytoscapeCPathWebService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The cPath plugin class.  It gets called by Cytoscape's plugin manager
 * to install inself.  The main job of this guy is to instantiate our http server.
 *
 * @author Benjamin Gross.
 */
public class CPathPlugIn2 implements BundleActivator {

    /**
     * Constructor.
     */
    public void start(BundleContext bc) {

        String debugProperty = System.getProperty("DEBUG");
        Boolean debug = (debugProperty != null && debugProperty.length() > 0) &&
                new Boolean(debugProperty.toLowerCase());

        CPathProperties cpathProperties = CPathProperties.getInstance();

        // to catch network creation events - to setup context menu
        NetworkListener networkListener = new NetworkListener();

        // create our http server and start its thread
        new HTTPServer(HTTPServer.DEFAULT_PORT,
                new MapCPathToCytoscape(networkListener), debug).start();

        //  Register Web Service
        WebServiceClientManager.registerClient(CytoscapeCPathWebService.getClient());
    }

    public void stop(BundleContext bc) {
	}

    public static JScrollPane createConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setBorder(new TitledBorder("Retrieval Options"));
        configPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        final JRadioButton button1 = new JRadioButton("Full Model");

        JTextArea textArea1 = new JTextArea();
        textArea1.setLineWrap(true);
        textArea1.setWrapStyleWord(true);
        textArea1.setEditable(false);
        textArea1.setOpaque(false);
        Font font = textArea1.getFont();
        Font smallerFont = new Font(font.getFamily(), font.getStyle(), font.getSize() - 2);
        textArea1.setFont(smallerFont);
        textArea1.setText("Retrieve the full model, as stored in the original BioPAX "
                + "representation.  In this representation, nodes within a network can "
                + "refer to physical entities and interactions.");
        textArea1.setBorder(new EmptyBorder(5, 20, 0, 0));

        JTextArea textArea2 = new JTextArea(3, 20);
        textArea2.setLineWrap(true);
        textArea2.setWrapStyleWord(true);
        textArea2.setEditable(false);
        textArea2.setOpaque(false);
        textArea2.setFont(smallerFont);
        textArea2.setText("Retrieve a simplified binary network, as inferred from the original "
                + "BioPAX representation.  In this representation, nodes within a network refer "
                + "to physical entities only, and edges refer to inferred interactions.");
        textArea2.setBorder(new EmptyBorder(5, 20, 0, 0));


        final JRadioButton button2 = new JRadioButton("Simplified Binary Model");
        button2.setSelected(true);
        ButtonGroup group = new ButtonGroup();
        group.add(button1);
        group.add(button2);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        configPanel.add(button2, c);

        c.gridy = 1;
        configPanel.add(textArea2, c);

        c.gridy = 2;
        configPanel.add(button1, c);

        c.gridy = 3;
        configPanel.add(textArea1, c);

        //  Add invisible filler to take up all remaining space
        c.gridy = 4;
        c.weighty = 1.0;
        JPanel panel = new JPanel();
        configPanel.add(panel, c);

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
        JScrollPane scrollPane = new JScrollPane(configPanel);
        return scrollPane;
    }
}
