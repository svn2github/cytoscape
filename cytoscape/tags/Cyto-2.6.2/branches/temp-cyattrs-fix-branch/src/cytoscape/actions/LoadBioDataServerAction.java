// $Revision$
// $Date$
// $Author$

package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.util.swing.BioDataServerWizard;

/**
 * Action allows the loading of a BioDataServer from the gui.
 * 
 * added by dramage 2002-08-20
 * 
 * Mod by kono 2005-09-16
 * Added new biodataserver wizard
 */
public class LoadBioDataServerAction extends CytoscapeAction {

	static final int SUCCESS = 0;
	
	BioDataServerWizard wiz;
	
	public LoadBioDataServerAction() {
		super("Gene Ontology Server...");
		setPreferredMenu("File.Load");
	}

	/*
	 *  (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		// Get the file name
		final String name;
		int wizardResult;

		// Create the wizard to choose biodataserver
		wiz = new BioDataServerWizard();
		wizardResult = wiz.show();
		if( wizardResult == SUCCESS ) {
			System.out.println( "Succesfully loaded Data Server.");
		}
	}
}
