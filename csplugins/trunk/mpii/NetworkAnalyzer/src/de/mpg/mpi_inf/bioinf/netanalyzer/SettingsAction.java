package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.PluginSettingsDialog;

/**
 * Action handler for the menu item &quot;NetworkAnalyzer Settings&quot;.
 * 
 * @author Yassen Assenov
 */
public class SettingsAction extends CytoscapeAction {

	/**
	 * Initializes a new instance of <code>SettingsAction</code>.
	 */
	public SettingsAction() {
		super(Messages.AC_SETTINGS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// Display settings dialog
			PluginSettingsDialog d = new PluginSettingsDialog(Cytoscape.getDesktop());
			d.setVisible(true);
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 7321507757114057304L;
}
