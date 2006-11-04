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
public class ImportAttributeTableAction extends CytoscapeAction implements
		PropertyChangeListener {
	public ImportAttributeTableAction() {
		super("Attribute Table (Excel and Text)...");
		setPreferredMenu("File.Import");

		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(
				this);
		
		// By default, turn this menu off.
		super.setEnabled(false);
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

	/**
	 * Enable this menu item only when network exists in the memory.
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName().equals(Cytoscape.SESSION_LOADED) || e.getPropertyName().equals(Cytoscape.NETWORK_LOADED)
				|| e.getPropertyName().equals(Cytoscape.NETWORK_DESTROYED)) {
			if (Cytoscape.getNetworkSet().size() == 0) {
				super.setEnabled(false);
			} else {
				// Once network or session is loaded, turn it ON.
				super.setEnabled(true);
			}
		}

	}
}
