// $Id: AboutPanel.java,v 1.7 2006/06/15 22:06:02 grossb Exp $
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

import cytoscape.CytoscapeInit;
import org.mskcc.biopax_plugin.plugin.BioPaxPlugIn;

import javax.swing.*;
import java.awt.*;
import java.util.Properties;

/**
 * Displays information "About this PlugIn...".
 *
 * @author Ethan Cerami
 */
public class AboutPanel extends JPanel {

    /**
     * Constructor.
     *
     * @param title        PlugIn Title.
     * @param majorVersion PlugIn Major Version.
     * @param minorVersion PlugIn Minor Version.
     * @param bgColor      Background Color.
     */
    public AboutPanel(String title, int majorVersion, int minorVersion,
            Color bgColor) {
        this.setBackground(bgColor);
        this.setLayout(new BorderLayout());
        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setContentType("text/html");
        textPane.setBackground(BioPaxDetailsPanel.BG_COLOR);

        StringBuffer temp = new StringBuffer();
        temp.append("<HTML><BODY>");
        temp.append("<TABLE WIDTH=100% CELLPADDING=5 CELLSPACING=5>"
                + "<TR BGCOLOR='ECE9D8'><TD>");
        temp.append("<FONT FACE=\"ARIAL\">");
        temp.append(title + ", Version:  "
                + majorVersion + ". " + minorVersion);
        temp.append("<P>");

        Properties cytoProps = CytoscapeInit.getProperties();
        if (cytoProps != null) {
            String proxyHost = (String) cytoProps.get
                    (BioPaxPlugIn.PROXY_HOST_PROPERTY);
            String proxyPort = (String) cytoProps.get
                    (BioPaxPlugIn.PROXY_PORT_PROPERTY);
            if (proxyHost != null && proxyPort != null) {
                temp.append("HTTP Proxy Support Enabled:  ");
                temp.append(proxyHost + " [Port:  " + proxyPort + "]");
            } else {
                temp.append("HTTP Proxy Support:  Not Enabled.");
            }
            temp.append("<P>");
        }
        temp.append("Extension released by:  Sander Group, "
                + "<A HREF=\"http://www.cbio.mskcc.org/\">"
                + "Computational Biology Center</A>, "
                + "<A HREF=\"http://www.mskcc.org\">Memorial Sloan-Kettering "
                + "Cancer Center</A>.");
        temp.append("<P>For any questions concerning this extension, please "
                + "contact:<P>Gary Bader:  baderg AT mskcc.org"
                + "<BR>Ethan Cerami:  cerami AT cbio.mskcc.org"
                + "<BR>Benjamin Gross:  grossb AT cbio.mskcc.org"
                + "<P>License:  "
                + "This software is made available under the "
                + "<A HREF=\"http://www.gnu.org/licenses/lgpl.html\">LGPL "
                + "(Lesser General Public License)</A>.");
        temp.append("</FONT>");
        temp.append("</TD></TR></TABLE>");
        temp.append("</BODY></HTML>");
        textPane.setText(temp.toString());
        textPane.setText(temp.toString());
        JScrollPane scrollPane = new JScrollPane(textPane);
        this.add(scrollPane, BorderLayout.CENTER);
    }
}
