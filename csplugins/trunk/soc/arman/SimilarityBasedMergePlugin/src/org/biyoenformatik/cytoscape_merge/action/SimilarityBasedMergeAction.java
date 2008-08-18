/* Copyright 2008 - The Cytoscape Consortium (www.cytoscape_merge.org)
 *
 * The Cytoscape Consortium is:
 * - Institute for Systems Biology
 * - University of California San Diego
 * - Memorial Sloan-Kettering Cancer Center
 * - Institut Pasteur
 * - Agilent Technologies
 *
 * Authors: B. Arman Aksoy, Thomas Kelder, Emek Demir
 *
 * This file is part of SimilarityBasedMergePlugin.
 *
 *  SimilarityBasedMergePlugin is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PaxtoolsPlugin is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this project.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.biyoenformatik.cytoscape_merge.action;

import cytoscape.util.CytoscapeAction;

import javax.swing.event.MenuEvent;
import java.awt.event.ActionEvent;

import org.biyoenformatik.cytoscape_merge.ui.SimilarityBasedMergeDialog;

public class SimilarityBasedMergeAction extends CytoscapeAction {
    public SimilarityBasedMergeAction() {
		super("Merge Similar Networks Components");
		setPreferredMenu("Plugins");
	}

    public void actionPerformed(ActionEvent event) {
        SimilarityBasedMergeDialog dialog = new SimilarityBasedMergeDialog();
        dialog.pack();
        dialog.setVisible(true);
    }

    public void menuSelected(MenuEvent e) {
        setEnabled(true);
    }
}
