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
package org.cytoscape.coreplugin.cpath.action;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import org.cytoscape.coreplugin.cpath.model.OrganismOption;
import org.cytoscape.coreplugin.cpath.model.SearchBundleList;
import org.cytoscape.coreplugin.cpath.model.SearchRequest;
import org.cytoscape.coreplugin.cpath.task.QueryCPathTask;
import org.cytoscape.coreplugin.cpath.ui.Console;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;


/**
 * Executes cPath Searches.
 *
 * @author Ethan Cerami
 */
public class ExecuteQuery extends KeyAdapter implements ActionListener {
	private HashMap cyMap;
	private SearchRequest searchRequest;
	private JFrame parent;
	private QueryCPathTask task;
	private SearchBundleList searchBundleList;
	private JButton searchButton;
	private Console console;

	/**
	 * Constructor.
	 *
	 * @param cyMap        CyMap Object.
	 * @param request      SearchRequest Object.
	 * @param searchList   List of Searches.
	 * @param console      Console Panel.
	 * @param searchButton Search Button.
	 * @param parent       Parent Component.
	 */
	public ExecuteQuery(HashMap cyMap, SearchRequest request, SearchBundleList searchList,
	                    Console console, JButton searchButton, JFrame parent) {
		this.cyMap = cyMap;
		this.searchRequest = request;
		this.parent = parent;
		this.searchBundleList = searchList;
		this.searchButton = searchButton;
		this.console = console;
	}

	/**
	 * Execute cPath Query.
	 *
	 * @param e ActionEvent Object.
	 */
	public void actionPerformed(ActionEvent e) {
		executeQuery();
	}

	/**
	 * Listen to Key Press Events in Search Box.
	 *
	 * @param e Key Event.
	 */
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == 10) {
			JTextField textField = (JTextField) e.getSource();
			searchRequest.setQuery(textField.getText());
			executeQuery();
		}
	}

	/**
	 * Execute Query Against cPath.
	 */
	private void executeQuery() {
		if ((searchRequest.getQuery().length() == 0)
		    && searchRequest.getOrganism().equals(OrganismOption.ALL_ORGANISMS)) {
			JOptionPane.showMessageDialog(parent,
			                              "Please Specify a Keyword and/or an Organism, and "
			                              + "try again.", "cPath PlugIn",
			                              JOptionPane.INFORMATION_MESSAGE);
		} else {
			// Disable search button so that user cannot initiate
			// multiple concurrent searches.
			searchButton.setEnabled(false);
			console.clear();
			//  Instantiate QueryCPathTask
			//  Task runs in a new thread, so that GUI remains responsive.
			task = new QueryCPathTask(cyMap, searchRequest, searchBundleList, console);

			JTaskConfig config = new JTaskConfig();
			config.setAutoDispose(true);
			config.displayCancelButton(true);
			config.displayTimeElapsed(true);
			config.displayTimeRemaining(true);
			config.displayStatus(true);
			config.setOwner(parent);
			TaskManager.executeTask(task, config);
		}
	}
}
