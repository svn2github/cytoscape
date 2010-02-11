/*
  Copyright (c) 2006, 2007, 2008 The Cytoscape Consortium (www.cytoscape.org)

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

package chemViz.menus;

import giny.model.GraphObject;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.util.TaskManager;

import chemViz.model.Compound;
import chemViz.ui.ChemInfoSettingsDialog;
import chemViz.tasks.TanimotoScorerTask;


/**
 * This plugin adds cheminformatics tools to Cytoscape.
 */
public class SimilarityMenu extends ChemVizAbstractMenu implements ActionListener {
	
	/**
 	 * This is the main constructor, which will be called by Cytoscape's Plugin Manager.
 	 * Add our listeners and create the main menu.
 	 */
	public SimilarityMenu(JMenu menu, Properties systemProps, ChemInfoSettingsDialog settingsDialog) {
		super(systemProps, settingsDialog);

		JMenu simMenu = new JMenu(systemProps
				.getProperty("chemViz.menu.similarity"));
		menu.add(simMenu);
		Set<GraphObject> selectedNodes = Cytoscape.getCurrentNetwork().getSelectedNodes();
		if (selectedNodes != null && selectedNodes.size() > 1) {
			JMenu tanMenu = new JMenu(systemProps.getProperty("chemViz.menu.similarity.tanimoto"));
			tanMenu.add(buildMenuItem("chemViz.menu.similarity.tanimoto.allNodes",
			                          "chemViz.menu.similarity.tanimoto.allNodes"));
			tanMenu.add(buildMenuItem("chemViz.menu.similarity.tanimoto.selectedNodes",
			                          "chemViz.menu.similarity.tanimoto.selectedNodes"));
			simMenu.add(tanMenu);
		} else {
			JMenuItem tanimoto = buildMenuItem("chemViz.menu.similarity.tanimoto",
				"chemViz.menu.similarity.tanimoto.allNodes");
			simMenu.add(tanimoto);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent evt) {
		String cmd = evt.getActionCommand();
		
		if (cmd.equals("chemViz.menu.similarity.tanimoto.selectedNodes")) {
			createScoreTable(Cytoscape.getCurrentNetwork().getSelectedNodes(), settingsDialog, false);
		} else if (cmd.equals("chemViz.menu.similarity.tanimoto.allNodes")) {
			createScoreTable((Collection<GraphObject>)Cytoscape.getCurrentNetwork().nodesList(), settingsDialog, true);
		}
	}
	
	/**
 	 * Calculate the tanimoto coefficients for each pair of compounds
 	 *
 	 * @param selection the nodes or edges we're going to pull the compounds from
 	 * @param dialog the settings dialog
 	 * @param newNetwork if 'true' a new network is created and 
 	 */
	private void createScoreTable(Collection<GraphObject>selection, ChemInfoSettingsDialog dialog, boolean newNetwork) {
		System.out.println("Creating score table");
		TanimotoScorerTask scorer = new TanimotoScorerTask(selection, dialog, newNetwork);
		TaskManager.executeTask(scorer, scorer.getDefaultTaskConfig());
	}

}
