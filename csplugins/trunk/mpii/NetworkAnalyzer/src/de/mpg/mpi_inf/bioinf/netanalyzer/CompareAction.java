package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.util.CytoscapeAction;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.CompareDialog;

/**
 * Action handler for the menu item &quot;Compare Two Networks&quot;.
 * 
 * @author Yassen Assenov
 */
public class CompareAction extends CytoscapeAction {

	/**
	 * Initializes a new instance of <code>GOPTRunAlgorithm</code>.
	 */
	public CompareAction() {
		super(Messages.AC_COMPARE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		try {
			CompareDialog d = new CompareDialog(Cytoscape.getDesktop());
			d.setVisible(true);
		} catch (InnerException ex) {
			// NetworkAnalyzer internal error
			CyLogger.getLogger().error(Messages.SM_LOGERROR, ex);
		}
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -8249265620304925132L;
}
