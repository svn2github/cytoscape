package edu.ucsd.bioeng.coreplugin.tableImport.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import edu.ucsd.bioeng.coreplugin.tableImport.ui.ImportTextTableDialog;

/**
 * Display Ontology and Annotation import dialog.<br>
 * 
 * @since Cytoscape 2.4
 * @version 1.0
 * @author kono
 *
 */
public class ImportAnnotationAndOntologyAction extends CytoscapeAction {

	public ImportAnnotationAndOntologyAction() {
		super("Ontology and Annotation...");
		setPreferredMenu("File.Import");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ImportTextTableDialog iad = new ImportTextTableDialog(Cytoscape
				.getDesktop(), true,
				ImportTextTableDialog.ONTOLOGY_AND_ANNOTATION_IMPORT);
		iad.pack();
		iad.setLocationRelativeTo(Cytoscape.getDesktop());
		iad.setVisible(true);
	}
}
