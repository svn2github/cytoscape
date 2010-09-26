package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.io.SettingsSerializer;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.settings.PluginSettings;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.PlotParameterDialog;

/**
 * Action handler for the menu item &quot;Plot Parameters&quot;.
 * 
 * @author Nadezhda Doncheva
 */
public class PlotParameterAction extends NetAnalyzerAction implements AnalysisListener {

	/**
	 * Initializes a new instance of <code>PlotParameterAction</code>.
	 */
	public PlotParameterAction() {
		super(Messages.AC_PLOTPARAM);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			// Verify there is a network loaded in Cytoscape and selected
			if (!selectNetwork()) {
				return;
			}
			// Display a dialog for mapping the computed attributes to the network
			// only if NetworkAnalyzer has computed any parameters and
			// and theses are stored in the node/edge attributes
			nodeAttr = CyNetworkUtils.getComputedNodeAttributes(network);
			// edgeAttr = CyNetworkUtils.getComputedEdgeAttributes(network);
			final PluginSettings settings = SettingsSerializer.getPluginSettings();
			if ((nodeAttr[0].length > 1) || (nodeAttr[1].length > 0)) {
				openDialog();
			} else if (!settings.getUseNodeAttributes() && !settings.getUseEdgeAttributes()) {
				// Network does not contain computed parameters stored as attributes
				if (JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), Messages.SM_LOADPARAMETERS,
						Messages.DT_ANALYSISNEEDED, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					settings.setUseNodeAttributes(true);
					settings.setUseEdgeAttributes(true);
					runNetworkAnalyzer();
				}
			} else {
				// Network does not contain computed parameters stored as attributes
				if (JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), Messages.SM_RUNNETWORKANALYZER,
						Messages.DT_ANALYSISNEEDED, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
					runNetworkAnalyzer();
				}
			}
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.AnalysisListener#analysisCancelled()
	 */
	public void analysisCancelled() {
		// No specific action is required
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.AnalysisListener#analysisCompleted(de.mpg.mpi_inf.bioinf
	 * .netanalyzer.NetworkAnalyzer)
	 */
	public void analysisCompleted(NetworkAnalyzer analyzer) {
		openDialog();
	}

	/**
	 * Opens the &quot;Plot Computed Parameters&quot; dialog.
	 */
	private void openDialog() {
		nodeAttr = CyNetworkUtils.getComputedNodeAttributes(network);
		final PlotParameterDialog d = new PlotParameterDialog(Cytoscape.getDesktop(), network, nodeAttr);
		d.setVisible(true);
	}

	/**
	 * Runs the NetworkAnalyzer analysis on all nodes so that the network parameters are computed and can be
	 * visualized afterwards.
	 */
	private void runNetworkAnalyzer() {
		final AnalysisExecutor exec = AnalyzeNetworkAction.initAnalysisExecuter(network, null);
		if (exec != null) {
			exec.setShowDialog(false);
			exec.addAnalysisListener(this);
			exec.start();
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -7530206812954428688L;

	/**
	 * Array with node attributes to be plotted.
	 */
	private String[][] nodeAttr;

}
