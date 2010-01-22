package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.xml.bind.JAXBException;

import cytoscape.Cytoscape;
import cytoscape.dialogs.preferences.JVMMemorySettingsDialog;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;


public class JVMMemorySettingsAction extends CytoscapeAction {
	public JVMMemorySettingsAction() {
		super("JVM Memory Settings...");

		CyLogger.getLogger(JVMMemorySettingsAction.class).info("JVMMemorySettingsAction()...");
		setPreferredMenu("Edit.Preferences");
	}


	public void actionPerformed(ActionEvent e) {
		JVMMemorySettingsDialog dialog;

		try {
			dialog = new JVMMemorySettingsDialog((JFrame) Cytoscape.getDesktop());
			dialog.setVisible(true);
		} catch (final JAXBException e1) {
			CyLogger.getLogger(JVMMemorySettingsAction.class).error(null, e1);
		} catch (final IOException e1) {
			CyLogger.getLogger(JVMMemorySettingsAction.class).error(null, e1);
		}
	}
}
