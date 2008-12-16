/*
 File: CyNetworkManager.java

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

package cytoscape;

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


/**
 * Basic access to networks and view in an instance of Cytoscape. 
 */
public interface CyNetworkManager {

	public CyNetwork getCurrentNetwork();
	public void setCurrentNetwork(final long network_id);

	public GraphView getCurrentNetworkView();
	public void setCurrentNetworkView(final long view_id);

	public Set<CyNetwork> getNetworkSet();
	public Set<GraphView> getNetworkViewSet();

	public CyNetwork getNetwork(long id);
	public GraphView getNetworkView(long network_id);

	public boolean networkExists(long network_id);
	public boolean viewExists(long network_id);

	public List<CyNetwork> getSelectedNetworks();
    public List<GraphView> getSelectedNetworkViews();

    public void setSelectedNetworkViews(final List<Long> viewIDs);
	public void setSelectedNetworks(final List<Long> ids);

	public void destroyNetwork(CyNetwork network);
	public void destroyNetworkView(GraphView view);

	public void addNetwork(CyNetwork network, GraphView view, CyLayouts cyLayouts);
}
