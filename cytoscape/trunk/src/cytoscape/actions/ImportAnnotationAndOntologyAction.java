package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.data.servers.ui.ImportTextTableDialog;
import cytoscape.util.CytoscapeAction;

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
