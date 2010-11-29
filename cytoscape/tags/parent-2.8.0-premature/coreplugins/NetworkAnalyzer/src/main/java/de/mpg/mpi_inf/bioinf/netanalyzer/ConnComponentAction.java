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

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfo;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.CCInfoInvComparator;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.ConnComponentsDialog;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;

/**
 * Action handler for the menu item &quot;Connected Components&quot;.
 * 
 * @author Yassen Assenov
 */
public class ConnComponentAction extends NetAnalyzerAction {

	/**
	 * Initializes a new instance of <code>ConnComponentAction</code>.
	 */
	public ConnComponentAction() {
		super(Messages.AC_CONNCOMP);
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

			ConnComponentAnalyzer an = new ConnComponentAnalyzer(network);

			Set<CCInfo> compsSet = an.findComponents();
			final int compsCount = compsSet.size();
			CCInfo[] comps = new CCInfo[compsCount];
			compsSet.toArray(comps);

			if (compsCount == 1) {
				final String msg = "<html><b>" + network.getTitle() + "</b>" + Messages.SM_CONNECTED;
				Utils.showInfoBox(Messages.DT_CONNCOMP, msg);
			} else {
				Arrays.sort(comps, new CCInfoInvComparator());
				ConnComponentsDialog d = new ConnComponentsDialog(Cytoscape.getDesktop(), network, comps);
				d.setVisible(true);
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
