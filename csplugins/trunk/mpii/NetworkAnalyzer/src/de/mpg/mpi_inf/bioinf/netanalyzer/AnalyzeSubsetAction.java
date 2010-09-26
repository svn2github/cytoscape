package de.mpg.mpi_inf.bioinf.netanalyzer;

import giny.model.Node;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cytoscape.Cytoscape;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import de.mpg.mpi_inf.bioinf.netanalyzer.data.Messages;
import de.mpg.mpi_inf.bioinf.netanalyzer.ui.Utils;
import ding.view.DNodeView;

/**
 * Action handler for the menu item &quot;Analyze Subset of Nodes&quot;.
 * 
 * @author Yassen Assenov
 * @author Nadezhda Doncheva
 */
public class AnalyzeSubsetAction extends NetAnalyzerAction {

	/**
	 * Initializes a new instance of <code>NetSubsetAction</code>.
	 */
	public AnalyzeSubsetAction() {
		super(Messages.AC_ANALYZE_SUBSET);
		selected = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (selectNetwork()) {
				final AnalysisExecutor exec = AnalyzeNetworkAction.initAnalysisExecuter(network, null);
				if (exec != null) {
					exec.start();
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
	 * @see de.mpg.mpi_inf.bioinf.netanalyzer.NetAnalyzerAction#selectNetwork()
	 */
	@Override
	protected boolean selectNetwork() {
		if (super.selectNetwork()) {
			final CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());
			if (view == null) {
				Utils.showErrorBox(Messages.DT_WRONGDATA, Messages.SM_CREATEVIEW);
				return false;
			}
			// TODO: [Cytoscape 2.8] Check if the returned list is parameterized
			final List<?> nodes = view.getSelectedNodes();
			if (nodes.isEmpty()) {
				Utils.showErrorBox(Messages.DT_WRONGDATA, Messages.SM_SELECTNODES);
				return false;
			}
			selected = new HashSet<Node>();
			for (final Object node : nodes) {
				selected.add(((DNodeView) node).getNode());
			}
			return true;
		}
		return false;
	}

	/**
	 * Set of nodes in the networks that were selected before the user clicked on the item.
	 */
	protected Set<Node> selected;

	/**
	 * Unique ID for this version of this class. It is used in serialization.
	 */
	private static final long serialVersionUID = 4670655302334870699L;
}
