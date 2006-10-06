package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.data.servers.ui.ImportAnnotationDialog;
import cytoscape.util.CytoscapeAction;

public class ImportAnnotationAction extends CytoscapeAction {

	public ImportAnnotationAction() {
		super("Annotation...");
		setPreferredMenu("File.Import.Ontology");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ImportAnnotationDialog iad = new ImportAnnotationDialog(
				Cytoscape.getDesktop(), true);
		iad.pack();
		iad.setLocationRelativeTo(Cytoscape.getDesktop());
		iad.setVisible(true);
	}
}
