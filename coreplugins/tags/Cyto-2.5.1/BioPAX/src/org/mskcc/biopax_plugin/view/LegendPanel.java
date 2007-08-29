// $Id: LegendPanel.java,v 1.6 2006/06/15 22:06:02 grossb Exp $
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

import org.mskcc.biopax_plugin.util.net.WebFileConnect;

import java.awt.*;

import java.io.IOException;

import java.net.URL;

import javax.swing.*;


/**
 * Displays the Default Visual Style Legend for the BioPAX Mapper.
 *
 * @author Ethan Cerami
 */
public class LegendPanel extends JPanel {
	/**
	 * Constructor.
	 *
	 * @param bgColor Background Color.
	 */
	public LegendPanel(Color bgColor) {
		this.setBackground(bgColor);
		this.setLayout(new BorderLayout());

		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setBackground(BioPaxDetailsPanel.BG_COLOR);

		URL legendUrl = LegendPanel.class.getResource("resources/legend.html");
		StringBuffer temp = new StringBuffer();
		temp.append("<HTML><BODY>");

		try {
			String legendHtml = WebFileConnect.retrieveDocument(legendUrl.toString());
			temp.append(legendHtml);
		} catch (IOException e) {
			temp.append("Could not load legend...");
		}

		temp.append("</BODY></HTML>");
		textPane.setText(temp.toString());

		JScrollPane scrollPane = new JScrollPane(textPane);
		this.add(scrollPane, BorderLayout.CENTER);
	}
}
