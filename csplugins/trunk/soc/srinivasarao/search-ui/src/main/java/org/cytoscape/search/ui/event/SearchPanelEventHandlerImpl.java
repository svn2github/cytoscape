package org.cytoscape.search.ui.event;

import org.cytoscape.search.EnhancedSearch;
import org.cytoscape.search.ui.RootPanel;
import org.cytoscape.search.ui.SearchPanelFactory;
import org.cytoscape.session.CyNetworkManager;
import org.cytoscape.session.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.session.events.NetworkAddedEvent;
import org.cytoscape.session.events.NetworkAddedListener;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedEvent;
import org.cytoscape.session.events.NetworkViewAboutToBeDestroyedListener;
import org.cytoscape.session.events.NetworkViewAddedEvent;
import org.cytoscape.session.events.NetworkViewAddedListener;
import org.cytoscape.session.events.SetCurrentNetworkEvent;
import org.cytoscape.session.events.SetCurrentNetworkListener;
import org.cytoscape.session.events.SetCurrentNetworkViewEvent;
import org.cytoscape.session.events.SetCurrentNetworkViewListener;

public class SearchPanelEventHandlerImpl implements NetworkAddedListener,
		NetworkViewAddedListener, SetCurrentNetworkListener,
		SetCurrentNetworkViewListener, NetworkAboutToBeDestroyedListener,
		NetworkViewAboutToBeDestroyedListener {

	private CyNetworkManager netmgr;
	private EnhancedSearch es;

	public SearchPanelEventHandlerImpl(CyNetworkManager nm, EnhancedSearch es) {
		this.netmgr = nm;
		this.es = es;
	}

	@Override
	public void handleEvent(NetworkAddedEvent event) {
		// TODO Auto-generated method stub
		if (es.getNetworkIndexStatus(event.getNetwork()) != EnhancedSearch.INDEX_SET
				&& es.getNetworkIndexStatus(event.getNetwork()) != EnhancedSearch.REINDEX)
			es.addNetwork(event.getNetwork());
	}

	@Override
	public void handleEvent(NetworkViewAddedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent event) {
		RootPanel attrPanel = SearchPanelFactory.getGlobalInstance(netmgr)
				.getattrPanel();
		attrPanel.clearAll();
		SearchPanelFactory.getGlobalInstance(netmgr).initattrPanel();
		if (netmgr.getCurrentNetwork() != null) {
			SearchPanelFactory.getGlobalInstance(netmgr).getmainPanel()
					.enableSearch();
		}
	}

	@Override
	public void handleEvent(SetCurrentNetworkViewEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(NetworkAboutToBeDestroyedEvent event) {
		// TODO Auto-generated method stub
		es.removeNetworkIndex(event.getNetwork());
		SearchPanelFactory.getGlobalInstance(netmgr).getmainPanel().setEnabled(
				false);

	}

	@Override
	public void handleEvent(NetworkViewAboutToBeDestroyedEvent event) {
		// TODO Auto-generated method stub
		if (event.getNetworkView().equals(netmgr.getCurrentNetworkView()))
			SearchPanelFactory.getGlobalInstance(netmgr).getmainPanel()
					.setEnabled(false);
	}
}
