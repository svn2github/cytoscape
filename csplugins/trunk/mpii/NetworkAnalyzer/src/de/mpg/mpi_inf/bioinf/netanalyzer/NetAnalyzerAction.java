package de.mpg.mpi_inf.bioinf.netanalyzer;

import java.util.List;
import java.util.Set;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;

/**
 * Base class for all NetworkAnalyzer actions which operate on a single network.
 * 
 * @author Yassen Assenov
 */
public abstract class NetAnalyzerAction extends CytoscapeAction {

	/**
	 * Constructs an action with the given name.
	 * 
	 * @param aName
	 *            Name of the action as it will appear in a menu.
	 */
	protected NetAnalyzerAction(String aName) {
		super(aName);
		network = null;
	}

	/**
	 * Finds the network of interest to the user.
	 * <p>
	 * In case a network has been identified, the value of the field {@link #network} is updated, otherwise
	 * the value of <code>network</code> is set to the empty network or <code>null</code>. There are
	 * three possible reasons for the inability to choose a network - (1) no network is loaded; (2) there are
	 * two or more networks loaded and none selected; and (3) there is more than one network selected. For
	 * each of the two cases above, the method displays an appropriate message dialog before exiting and
	 * returning <code>false</code>.
	 * </p>
	 * 
	 * @return <code>true</code> if a network targeting analysis has been identified, <code>false</code>
	 *         otherwise.
	 */
	@SuppressWarnings("fallthrough")
	protected boolean selectNetwork() {
		network = null;
		String error = null;
		final Set<CyNetwork> networksSet = Cytoscape.getNetworkSet();
		switch (networksSet.size()) {
			case 0: // no network is loaded
				error = Messages.SM_LOADNET;
				break;
			case 1: // single network is available
				network = networksSet.iterator().next();
				break;
			default:
				final List<CyNetwork> networks = Cytoscape.getSelectedNetworks();
				switch (networks.size()) {
					case 1:
						network = networks.get(0);
						if (network != null && network != Cytoscape.getNullNetwork()) {
							// single network is selected
							break;
						}
					case 0: // no network is selected
						error = Messages.SM_SELECTNET;
						break;
					default: // multiple networks are selected
						error = Messages.SM_SELECTONENET;
				}
		}
		if (error != null) {
			Utils.showErrorBox(Messages.DT_WRONGDATA, error);
			return false;
		}
		return true;
	}

	/**
	 * Target network for the action.
	 */
	protected CyNetwork network;

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = -6263068520728141892L;
}
