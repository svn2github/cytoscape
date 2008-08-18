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
