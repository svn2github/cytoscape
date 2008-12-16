/*
 File: NetworkManager.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */

package cytoscape.internal;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyDataTable;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.layout.CyLayoutAlgorithm;
import org.cytoscape.layout.CyLayouts;
import org.cytoscape.view.GraphView;

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import cytoscape.CyNetworkManager;


public class NetworkManager implements CyNetworkManager {

	private final Map<Long,GraphView> networkViewMap;
	private final Map<Long,CyNetwork> networkMap;

	private final List<GraphView> selectedNetworkViews; 
	private final List<CyNetwork> selectedNetworks;

	private final CyEventHelper eh;

	private CyNetwork currentNetwork;
	private GraphView currentNetworkView;

	NetworkManager(final CyEventHelper eh) {
		networkMap = new HashMap<Long,CyNetwork>();
		networkViewMap = new HashMap<Long,GraphView>();
		selectedNetworkViews = new LinkedList<GraphView>();
		selectedNetworks = new LinkedList<CyNetwork>();
		currentNetwork = null;
		currentNetworkView = null;
		this.eh = eh;
	}

	public CyNetwork getCurrentNetwork() {
		return currentNetwork;
	}

	public void setCurrentNetwork(final long networkId) {
		if ( !networkMap.containsKey( networkId ) ) 
			throw new IllegalArgumentException("network is not recognized by this NetworkManager");

		currentNetwork = networkMap.get(networkId);

        // reset selected networks
        selectedNetworks.clear();
        selectedNetworks.add(currentNetwork);

//		eh.fireSynchronousEvent( new SetCurrentNetworkEvent(net), SetCurrentNetworkListener.class );
	}

	public Set<CyNetwork> getNetworkSet() {
		return new HashSet<CyNetwork>(networkMap.values());
	}

	public Set<GraphView> getNetworkViewSet() {
		return new HashSet<GraphView>(networkViewMap.values());
	}

	public CyNetwork getNetwork(long id) {
		return networkMap.get(id);
	}

	public GraphView getNetworkView(long network_id) {
		return networkViewMap.get(network_id);
	}

	public boolean networkExists(long network_id) {
		return networkMap.containsKey(network_id);
	}

	public boolean viewExists(long network_id) {
		return networkViewMap.containsKey(network_id);
	}

	public GraphView getCurrentNetworkView() {
		return currentNetworkView;
	}

	public void setCurrentNetworkView(final long viewId) {
		if ( !networkMap.containsKey( viewId ) ||
		     !networkViewMap.containsKey( viewId ) )
			throw new IllegalArgumentException("network view is not recognized by this NetworkManager");

		currentNetworkView = networkViewMap.get(viewId);

        // reset selected network views
        selectedNetworkViews.clear();
        selectedNetworkViews.add(currentNetworkView);

//		eh.fireSynchronousEvent( new SetCurrentNetworkViewEvent(view), SetCurrentNetworkViewListener.class );
	}

    public List<GraphView> getSelectedNetworkViews() {
        return new ArrayList<GraphView>(selectedNetworkViews);
    }

    public void setSelectedNetworkViews(final List<Long> viewIDs) {

        if (viewIDs == null)
            return;

        selectedNetworkViews.clear();

        for (Long id : viewIDs) {
            GraphView nview = networkViewMap.get(id);

            if (nview != null) {
                selectedNetworkViews.add(nview);
            }
        }

        GraphView cv = getCurrentNetworkView();

        if (!selectedNetworkViews.contains(cv)) {
            selectedNetworkViews.add(cv);
        }

//		eh.fireSynchronousEvent( new SetSelectedNetworkViewsEvent(view), SetSelectedNetworkViewsListener.class );
    }

	public List<CyNetwork> getSelectedNetworks() {
        return new ArrayList<CyNetwork>(selectedNetworks);
	}

	public void setSelectedNetworks(final List<Long> ids) {
        selectedNetworks.clear();

        if (ids == null)
            return;

        for (Long id : ids) {
            CyNetwork n = networkMap.get(id);

            if (n != null) {
                selectedNetworks.add(n);
            }
        }

        CyNetwork cn = currentNetwork; 

        if (!selectedNetworks.contains(cn)) {
            selectedNetworks.add(cn);
        }

//		eh.fireSynchronousEvent( new SetSelectedNetworksEvent(view), SetSelectedNetworksListener.class );
	}


	// TODO
	// Does this need to distinguish between root networks and subnetworks?
	public void destroyNetwork(CyNetwork network) {
		if ( network == null )
			throw new NullPointerException("network is null");

        final Long networkId = network.getSUID();

		if ( !networkMap.containsKey( networkId ) ) 
			throw new IllegalArgumentException("network is not recognized by this NetworkManager");

//		eh.fireSynchronousEvent( new NetworkAboutToBeDestroyedEvent(network), 
//		                         NetworkAboutToBeDestroyedListener.class );

        selectedNetworks.remove(network);

		for ( CyNode n : network.getNodeList() )
			n.attrs().set("selected",false);
		for ( CyEdge e : network.getEdgeList() )
			e.attrs().set("selected",false);

        networkMap.remove(networkId);

        if (network == currentNetwork) {
            if (networkMap.size() <= 0) {
                currentNetwork = null;
            } else {
                // randomly pick a network to become the current network
                for (CyNetwork net : networkMap.values()) {
                    currentNetwork = net;
                    break;
                }
            }
        }

        if (viewExists(networkId))
            destroyNetworkView(networkViewMap.get(networkId));

        network = null;

		// lets everyone know that *A* network is gone
//		eh.fireSynchronousEvent( new NetworkDestroyedEvent(), 
//		                         NetworkDestroyedListener.class );
    }

	public void destroyNetworkView(GraphView view) {

		if ( view == null )
			throw new NullPointerException("view is null");

		final Long viewID = view.getNetwork().getSUID(); 

		if ( !networkMap.containsKey( viewID ) ||
		     !networkViewMap.containsKey( viewID ) )
			throw new IllegalArgumentException("network view is not recognized by this NetworkManager");

//		eh.fireSynchronousEvent( new NetworkViewAboutToBeDestroyedEvent(network), 
//		                         NetworkViewAboutToBeDestroyedListener.class );

		selectedNetworkViews.remove(view);

		if (view.equals(currentNetworkView)) {
			if (networkViewMap.size() <= 0)
				currentNetworkView = null;
			else {
				// depending on which randomly chosen currentNetwork we get, 
				// we may or may not have a view for it.
				GraphView newCurr = networkViewMap.get(currentNetwork.getSUID());

				if (newCurr != null)
					currentNetworkView = newCurr; 
				else
					currentNetworkView = null;
			}
		}

		networkViewMap.remove(viewID);
		view = null;

//		eh.fireSynchronousEvent( new NetworkViewDestroyedEvent(), 
//		                         NetworkViewDestroyedListener.class );

	}


	public void addNetwork(CyNetwork network, GraphView view, CyLayouts cyLayouts) {
		if ( network == null )
			throw new NullPointerException("network is null");

		networkMap.put(network.getSUID(), network);

		if ( view != null ) {
			networkViewMap.put(network.getSUID(), view);

			setCurrentNetworkView( network.getSUID() );

			if ( cyLayouts != null )
				cyLayouts.getDefaultLayout().doLayout(view);

			view.fitContent();
		}

//		eh.fireSynchronousEvent( new NetworkAddedEvent(network), 
//		                         NetworkAddedListener.class );

//		eh.fireSynchronousEvent( new NetworkViewAddedEvent(view), 
//		                         NetworkViewAddedListener.class );
	}
}
