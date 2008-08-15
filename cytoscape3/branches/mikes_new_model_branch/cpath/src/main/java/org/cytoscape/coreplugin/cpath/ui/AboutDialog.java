/*
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
package org.cytoscape.coreplugin.cpath.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Displays Information About the cPath PlugIn.
 *
 * @author Ethan Cerami.
 */
public class AboutDialog extends MouseAdapter implements ActionListener {
	/**
	 * Current Version Number.
	 */
	public static final String VERSION = "Beta 3";

	/**
	 * Current Release Date.
	 */
	public static final String RELEASE_DATE = "June, 2006";
	private JFrame parent;
	private JDialog dialog;
	private static final int WIDTH = 400;
	private static final int HEIGHT = 450;

	/**
	 * Constructor.
	 *
	 * @param parent Parent Frame.
	 */
	public AboutDialog(JFrame parent) {
		this.parent = parent;
	}

	/**
	 * User has requested that we shows the About Dialog.
	 *
	 * @param event ActionEvent Object.
	 */
	public void actionPerformed(ActionEvent event) {
		dialog = new JDialog(parent, "About the cPath PlugIn", true);

		Container contentPane = dialog.getContentPane();
		contentPane.setLayout(new BorderLayout());

		JEditorPane htmlPane = new JEditorPane();
		htmlPane.addMouseListener(this);

		EmptyBorder border = new EmptyBorder(5, 5, 5, 5);
		htmlPane.setBorder(border);
		htmlPane.setContentType("text/html");
		htmlPane.setEditable(false);
		htmlPane.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		String html = this.getAboutHtml();
		htmlPane.setText(html);

		JScrollPane scrollPane = new JScrollPane(htmlPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		Point point = parent.getLocation();
		Dimension size = parent.getSize();
		int x = (int) ((point.getX() + (size.getWidth() / 2)) - (WIDTH / 2));
		int y = (int) ((point.getY() + (size.getHeight() / 2)) - (HEIGHT / 2));
		dialog.setLocation(x, y);
		dialog.pack();
		dialog.setVisible(true);
	}

	private String getAboutHtml() {
		StringBuffer html = new StringBuffer();
		html.append("<FONT FACE=ARIAL>");
		html.append("<IMG SRC='http://www.mskcc.org/mskcc_resources/" + "images/logos/336699.gif'>");
		html.append("<BR>cPath PlugIn Version:  " + VERSION);
		html.append("<BR>Release Date:  " + RELEASE_DATE);

		//        PropertyManager pManager = PropertyManager.getInstance();
		//        String url = pManager.getProperty(PropertyManager.CPATH_READ_LOCATION);
		//        html.append("<P>Your PlugIn is currently configured to retrieve "
		//                + "data from:  " + url);
		html.append("<P>The Cytoscape cPath Plugin is maintained by ");
		html.append("the Sander group at the Computational Biology Center ");
		html.append("of Memorial Sloan-Kettering Cancer Center.");
		html.append("<P>");
		html.append("cPath is available at:  " + "http://cbio.mskcc.org/cpath.");
		html.append("<P>");
		html.append("For scientific questions regarding cPath or the cPath"
		            + " PlugIn, please contact Gary Bader:  &lt;" + "bader@cbio.mskcc.org&gt;.");
		html.append("<P>");
		html.append("For technical / programming questions regarding cPath or"
		            + " the cPath PlugIn, please contact Ethan Cerami:  "
		            + " &lt;cerami@cbio.mskcc.org&gt;");
		html.append("</FONT>");
		html.append("<P><HR><FONT FACE=ARIAL SIZE=-1>");
		html.append("Copyright (C) 2004 Memorial Sloan-Kettering Cancer Center.");
		html.append("</FONT>");

		return html.toString();
	}

	/**
	 * Mouse Clicked.  Dialog if Disposed.
	 *
	 * @param e MouseEvent.
	 */
	public void mouseClicked(MouseEvent e) {
		if (dialog != null) {
			dialog.dispose();
		}
	}
}
