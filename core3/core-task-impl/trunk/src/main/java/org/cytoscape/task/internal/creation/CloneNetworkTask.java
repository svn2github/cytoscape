/*
 File: CloneNetworkTask.java

 Copyright (c) 2006, 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package org.cytoscape.task.internal.creation;


import java.util.HashMap;
import java.util.Map;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.session.CyApplicationManager;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.VisualProperty;
import org.cytoscape.view.presentation.RenderingEngine;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;


public class CloneNetworkTask extends AbstractCreationTask {
    private Map<CyNode,CyNode> origNewNodeMap;
    private Map<CyEdge,CyEdge> origNewEdgeMap;
    private final VisualMappingManager vmm;
    private final CyNetworkFactory netFactory;
    private final CyNetworkViewFactory netViewFactory;
    private final RenderingEngine<CyNetwork> re;
    private final CyNetworkNaming naming;

    public CloneNetworkTask(final CyNetwork net, final CyNetworkManager netmgr, 
    		                final CyNetworkViewManager networkViewManager, final VisualMappingManager vmm, 
    		                final CyNetworkFactory netFactory, final CyNetworkViewFactory netViewFactory, 
    		                final CyApplicationManager appMgr, final CyNetworkNaming naming) {
        super(net, netmgr, networkViewManager);
        this.vmm = vmm;
        this.netFactory = netFactory;
        this.netViewFactory = netViewFactory;
        this.re = appMgr.getCurrentRenderingEngine();
        this.naming = naming;
    }

    public void run(TaskMonitor tm) {
    	System.out.println("start cloning network");
        CyNetwork newNet = cloneNetwork(net);
        System.out.println("----- cloning topology");
        CyNetworkView origView = networkViewManager.getNetworkView(net.getSUID());
        networkManager.addNetwork(newNet);
        if ( origView != null ) {
        	System.out.println("----- cloning visualization");
            CyNetworkView newView = cloneNetworkView(origView,newNet);
            vmm.setVisualStyle(vmm.getVisualStyle(origView), newView );
            networkViewManager.addNetworkView(newView);
            newView.updateView();
        }
        System.out.println("finished cloning network");
    }

    private CyNetworkView cloneNetworkView(CyNetworkView origView, CyNetwork newNet) {
        CyNetworkView newView = netViewFactory.getNetworkView(newNet);

        // copy node view visual properties
        for ( View<CyNode> origNodeView : origView.getNodeViews() ) {
            View<CyNode> newNodeView = newView.getNodeView( origNewNodeMap.get( origNodeView.getModel() ) );
            for ( VisualProperty<?> vp : re.getVisualLexicon().getAllVisualProperties() ) {
                newNodeView.setVisualProperty(vp, origNodeView.getVisualProperty(vp));
                if (origNodeView.isValueLocked(vp) )
                    newNodeView.setLockedValue(vp, origNodeView.getVisualProperty(vp));
            }
        }

        // copy edge view visual properties
        for ( View<CyEdge> origEdgeView : origView.getEdgeViews() ) {
            View<CyEdge> newEdgeView = newView.getEdgeView( origNewEdgeMap.get( origEdgeView.getModel() ) );
            for ( VisualProperty<?> vp : re.getVisualLexicon().getAllVisualProperties() ) {
                newEdgeView.setVisualProperty(vp, origEdgeView.getVisualProperty(vp));
                if (origEdgeView.isValueLocked(vp) )
                    newEdgeView.setLockedValue(vp, origEdgeView.getVisualProperty(vp));
            }
        }
        
        return newView;
    }

    private CyNetwork cloneNetwork(CyNetwork origNet) {
    	throw new RuntimeException("uuuuhhh");
    	System.out.println("enter clone network");

        final CyNetwork newNet = netFactory.getInstance();

        System.out.println("cloning columns");
        // copy default columns
        cloneColumns( origNet.getDefaultNodeTable(), newNet.getDefaultNodeTable() );
        cloneColumns( origNet.getDefaultEdgeTable(), newNet.getDefaultEdgeTable() );
        cloneColumns( origNet.getDefaultNetworkTable(), newNet.getDefaultNetworkTable() );

        System.out.println("cloning nodes");
        cloneNodes( origNet, newNet );
        System.out.println("cloning edges");
        cloneEdges( origNet, newNet );
        
        System.out.println("setting names");
        newNet.set(CyTableEntry.NAME,naming.getSuggestedNetworkTitle(origNet.getCyRow().get(CyTableEntry.NAME, String.class)));

        return newNet;
    }

    private void cloneNodes( CyNetwork origNet, CyNetwork newNet ) {
        origNewNodeMap = new HashMap<CyNode,CyNode>();
        for ( final CyNode origNode : origNet.getNodeList() ) {
            final CyNode newNode = newNet.addNode();
            origNewNodeMap.put( origNode, newNode );
            cloneRow( origNode.getCyRow(), newNode.getCyRow() );
        }
    }

    private void cloneEdges( CyNetwork origNet, CyNetwork newNet ) {
        origNewEdgeMap = new HashMap<CyEdge,CyEdge>();
        for ( final CyEdge origEdge : origNet.getEdgeList() ) {
            final CyNode newSource = origNewNodeMap.get( origEdge.getSource() );
            final CyNode newTarget = origNewNodeMap.get( origEdge.getTarget() );
            final boolean newDirected = origEdge.isDirected();
            final CyEdge newEdge = newNet.addEdge( newSource, newTarget, newDirected );
            origNewEdgeMap.put( origEdge, newEdge );
            cloneRow( origEdge.getCyRow(), newEdge.getCyRow() );
        }
    }

    private void cloneColumns(CyTable from, CyTable to) {
        for ( final Map.Entry<String,Class<?>> fromEntry : from.getColumnTypeMap().entrySet() ) {
            final Class<?> toType = to.getType( fromEntry.getKey() );
            if ( toType == fromEntry.getValue() ) {
                continue;
            } else if ( toType == null ) {
                to.createColumn( fromEntry.getKey(), fromEntry.getValue() );
            } else {
                throw new IllegalArgumentException("column of same name: " + fromEntry.getKey() +
                                                   "but types don't match (orig): " +
                                                   fromEntry.getValue() + " (new): " + toType);
            }
        }
    }

    private void cloneRow(CyRow from, CyRow to) {
        for ( final String column : from.getDataTable().getColumnTypeMap().keySet() )
            to.set(column, from.getRaw(column) );
    }

}
