/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A class with class members and methods that facilitate viewing biomodules.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 */
package biomodules.view;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.visual.*;
import metaNodeViewer.MetaNodeUtils;
import metaNodeViewer.model.*;
import metaNodeViewer.data.*;
import metaNodeViewer.view.VisualStyleFactory;
import java.util.*;

public class ViewUtils {

	public static final MetaNodeAttributesHandler attributesHandler = new AbstractMetaNodeAttsHandler();

	public static final AbstractMetaNodeModeler abstractModeler = MetaNodeModelerFactory
			.getCytoscapeAbstractMetaNodeModeler();

	/**
	 * @return an array of RootGraph indices for the newly created meta-nodes,
	 *         null if something went wrong (null arguments for example) The
	 *         order of the indices in the array corresponds to the order of the
	 *         biomodules in the given CyNode[][]. For example, meta node with
	 *         index 'i' in the returned array is the parent of nodes
	 *         biomodules[i].
	 */
	public static ArrayList abstractBiomodules(CyNetwork network,
			CyNode[][] biomodules) {
	    
        ArrayList metaNodes = new ArrayList();
        for(int i = 0; i < biomodules.length; i++){
            ArrayList nodes = new ArrayList();
            for(int j = 0; j < biomodules[i].length; j++){nodes.add(biomodules[i][j]);}
            CyNetwork subnet = Cytoscape.getRootGraph().createNetwork(nodes,new ArrayList());
            CyNode mnode = MetaNodeUtils.createMetaNode(network,subnet,ViewUtils.attributesHandler);
            MetaNodeUtils.collapseMetaNode(network,mnode,true);
            metaNodes.add(mnode);
        }
        
        // Apply vizmapper
		CytoscapeDesktop cyDesktop = Cytoscape.getDesktop();
		VisualMappingManager vizmapper = cyDesktop.getVizMapManager();
		VisualStyle abstractMetaNodeVS = vizmapper.getCalculatorCatalog()
				.getVisualStyle(VisualStyleFactory.ABSTRACT_METANODE_VS);
		if (abstractMetaNodeVS == null) {
			abstractMetaNodeVS = VisualStyleFactory
					.createAbstractMetaNodeVisualStyle(network);
		}
		String netID = network.getIdentifier();
		CyNetworkView netView = Cytoscape.getNetworkView(netID);
		if (netView != null) {
			netView.applyVizmapper(abstractMetaNodeVS);
		}
		return metaNodes;
	}// abstractBiomodules

	/**
	 * Removes the given list of meta-nodes from the network and restores their
	 * children.
	 * 
	 * @param network
	 *            the <code>CyNetwork</code> from which meta-nodes will be
	 *            removed
	 * @param nodes
	 *            the meta-nodes to be removed
	 * @param recursive
	 *            if there are > 1 levels of meta-node hierarchy, whether or not
	 *            to remove all the levels (if it is known that there is only 1
	 *            level, setting this to false significantly improves
	 *            performance)
	 */
	public static void removeMetaNodes(CyNetwork network,
			ArrayList nodes, boolean recursive) {
		
        Iterator it = nodes.iterator();
        while(it.hasNext()){
            CyNode mnode = (CyNode)it.next();
            MetaNodeUtils.removeMetaNode(network,mnode,recursive);
        }
        
	}// removeMetaNodes

}// ViewUtils
