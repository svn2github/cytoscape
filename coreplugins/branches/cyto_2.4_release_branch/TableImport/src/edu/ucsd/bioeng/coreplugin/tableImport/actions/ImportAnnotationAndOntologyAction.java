package edu.ucsd.bioeng.coreplugin.tableImport.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.xml.bind.JAXBException;

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
		ImportTextTableDialog iad;
		try {
			iad = new ImportTextTableDialog(Cytoscape
					.getDesktop(), true,
					ImportTextTableDialog.ONTOLOGY_AND_ANNOTATION_IMPORT);
			iad.pack();
			iad.setLocationRelativeTo(Cytoscape.getDesktop());
			iad.setVisible(true);
		} catch (JAXBException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
