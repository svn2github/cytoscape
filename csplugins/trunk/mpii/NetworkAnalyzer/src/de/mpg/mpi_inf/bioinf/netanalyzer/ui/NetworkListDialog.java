package de.mpg.mpi_inf.bioinf.netanalyzer.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.event.ListSelectionListener;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

/**
 * Base class for dialogs which display a list of all loaded networks in Cytoscape.
 * <p>
 * This type of dialog is always created modal in order to prevent network modification (e.g.
 * deletion of network from Cytoscape) while the dialog is visible.
 * </p>
 * 
 * @author Yassen Assenov
 */
abstract class NetworkListDialog extends JDialog implements ListSelectionListener {

	/**
	 * Initializes the fields of <code>NetworkListDialog</code>.
	 * 
	 * @param aOwner The <code>Frame</code> from which this dialog is displayed.
	 * @param aTitle Title of the dialog.
	 */
	protected NetworkListDialog(Frame aOwner, String aTitle) {
		super(aOwner, aTitle, true);
		initNetworkList();
	}

	/**
	 * Initializes the fields of <code>NetworkListDialog</code>.
	 * 
	 * @param aOwner The <code>Dialog</code> from which this dialog is displayed.
	 * @param aTitle Title of the dialog.
	 */
	protected NetworkListDialog(Dialog aOwner, String aTitle) {
		super(aOwner, aTitle, true);
		initNetworkList();
	}

	/**
	 * Checks if a network name in the list of networks is selected.
	 * 
	 * @return <code>true</code> if at least one of the listed network names is selected; <code>false</code> otherwise.
	 */
	protected boolean isNetNameSelected() {
		int[] indices = listNetNames.getSelectedIndices();
		return indices.length > 0;
	}

	/**
	 * List of all available networks.
	 */
	protected List<CyNetwork> networks;

	/**
	 * List control that contains the names of the available networks.
	 * 
	 * @see #networks
	 */
	protected JList listNetNames;

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 5706001778102104118L;

	/**
	 * Initializes the network list and list control containing network names.
	 */
	private void initNetworkList() {
		final Set<CyNetwork> networkSet = Cytoscape.getNetworkSet();
		final int netCount = networkSet.size();
		networks = new ArrayList<CyNetwork>(netCount);
		String[] netTitles = new String[netCount];
		int i = 0;
		for (final CyNetwork network : networkSet) {
			networks.add(network);
			netTitles[i++] = network.getTitle();
		}
		listNetNames = new JList(netTitles);
		listNetNames.addListSelectionListener(this);
		if (netCount < 8) {
			listNetNames.setVisibleRowCount(netCount);
		}
	}
}
