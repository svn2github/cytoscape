package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import cytoscape.Cytoscape;
import cytoscape.data.servers.ui.ImportTextTableDialog;
import cytoscape.util.CytoscapeAction;

/**
 * Display dialog for importing attribute text/Excel file.<br>
 * 
 * @since Cytoscape 2.4
 * @version 1.0
 * @author kono
 * 
 */
public class ImportAttributeTableAction extends CytoscapeAction {
	public ImportAttributeTableAction() {
		super("Attribute from Table (Text/MS Excel\u2122)...");
		setPreferredMenu("File.Import");
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		ImportTextTableDialog iad = new ImportTextTableDialog(Cytoscape
				.getDesktop(), true,
				ImportTextTableDialog.SIMPLE_ATTRIBUTE_IMPORT);
		iad.pack();
		iad.setLocationRelativeTo(Cytoscape.getDesktop());
		iad.setVisible(true);
	}
}
