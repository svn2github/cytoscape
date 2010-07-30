/*
 File: OpenRecentAction.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.view;


import cytoscape.Cytoscape;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.logger.CyLogger;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import javax.swing.AbstractAction;


class OpenRecentAction extends AbstractAction {
	private URL sessionFileURL;
	private CyLogger logger;

	OpenRecentAction(final URL sessionFileURL) {
		super(sessionFileURL.toString());

		this.sessionFileURL = sessionFileURL;
		this.logger = CyLogger.getLogger(OpenRecentAction.class);
	}

	public void actionPerformed(final ActionEvent e) {
		try {
			final CytoscapeSessionReader reader = new CytoscapeSessionReader(sessionFileURL);
			if (reader == null) {
				logger.warn("Failed to load: " + sessionFileURL);
				return;
			}
			try {
				reader.read();
			} catch (final Exception ex1) {
				logger.warn("Failed to load: " + sessionFileURL);
				return;
			}

			final String sessionName = sessionFileURL.toString();
			Cytoscape.setCurrentSessionFileName(sessionName);
			Cytoscape.getDesktop().setTitle("Cytoscape Desktop (Session Name: " + sessionName + ")");

		} catch (final IOException ex2) {
			logger.warn("Failed to open session: " + sessionFileURL);
			System.err.println(ex2);
		}
	}
}
