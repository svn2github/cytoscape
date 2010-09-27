/*
 * Copyright (c) 2006, 2007, 2008, 2010, Max Planck Institute for Informatics, Saarbruecken, Germany.
 *
 * This file is part of NetworkAnalyzer.
 * 
 * NetworkAnalyzer is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * 
 * NetworkAnalyzer is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with NetworkAnalyzer. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.Frame;
import java.awt.event.ActionEvent;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.sconnect.HelpConnector;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.NetModificationDialog;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;

/**
 * Action handler for the menu item &quot;Remove Self-loops&quot;.
 * 
 * @author Yassen Assenov
 * @author Sven-Eric Schelhorn
 */
public class RemoveSelfLoopsAction extends NetAnalyzerAction {

	/**
	 * Initializes a new instance of <code>RemoveSelfLoopsAction</code>.
	 */
	public RemoveSelfLoopsAction() {
		super(Messages.AC_REMSELFLOOPS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (!selectNetwork()) {
				return;
			}

			final Frame desktop = Cytoscape.getDesktop();
			final String helpURL = HelpConnector.getRemSelfloopsURL();
			final NetModificationDialog d = new NetModificationDialog(desktop, Messages.DT_REMSELFLOOPS,
					Messages.DI_REMOVESL, helpURL);
			d.setVisible(true);

			// Remove the self-loops from all networks selected by the user
			final CyNetwork[] networks = d.getSelectedNetworks();
			if (networks != null) {
				final int size = networks.length;
				int[] removedLoops = new int[size];
				String[] networkNames = new String[size];
				for (int i = 0; i < size; ++i) {
					final CyNetwork currentNet = networks[i];
					networkNames[i] = currentNet.getTitle();
					removedLoops[i] = CyNetworkUtils.removeSelfLoops(currentNet);
				}

				final String r = Messages.constructReport(removedLoops, Messages.SM_REMSELFLOOPS,
						networkNames);
				Utils.showInfoBox(desktop, Messages.DT_REMSELFLOOPS, r);
			}
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -7465036491341908005L;
}
