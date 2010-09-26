package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.awt.event.ActionEvent;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.AboutDialog;

/**
 * Action handler for the menu item &quot;About NetworkAnalyzer&quot;.
 * 
 * @author Yassen Assenov
 */
public final class AboutAction extends CytoscapeAction {

	/**
	 * Initializes a new instance of <code>AboutAction</code>.
	 */
	public AboutAction() {
		super(Messages.AC_ABOUT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		AboutDialog d = new AboutDialog(Cytoscape.getDesktop());
		d.setLocationRelativeTo(Cytoscape.getDesktop());
		d.setVisible(true);
	}

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -6208980061184138790L;
}
