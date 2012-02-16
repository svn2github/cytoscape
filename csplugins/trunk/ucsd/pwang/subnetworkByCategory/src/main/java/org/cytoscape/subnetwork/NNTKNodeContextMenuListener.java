
package org.cytoscape.subnetwork; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import giny.model.Edge;
import giny.model.Node;
import giny.view.EdgeView;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CyNode;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.NodeContextMenuListener;

import java.util.*;

import java.io.*;

public class NNTKNodeContextMenuListener implements NodeContextMenuListener
{
	 private final CyNetworkView view;
	 
	 public NNTKNodeContextMenuListener(CyNetworkView view)
     {
             this.view = view;
     }

     public void addNodeContextMenuItems(NodeView nv, JPopupMenu menu)
     {

	 	GraphView gv = nv.getGraphView();

         if (menu == null)
             return;

	     final JMenu nntkMenu = new JMenu("Nested Network Toolkit");
	
	     boolean selectedHasNested = false;
	     
	     for (Object n : gv.getSelectedNodes())
	    	 if (((ding.view.DNodeView)n).getNode().getNestedNetwork()!=null)
	    	 {
	    		 selectedHasNested = true;
	    		 break;
	    	 }
	     
	     boolean isOverviewNetwork = view.getNetwork().getTitle().startsWith(SubnetworkByCategoryPlugin.overviewTitle);
	     
	     //ITEM1
	     if (selectedHasNested && isOverviewNetwork)
	     {
	         JMenuItem item = new JMenuItem();
	         item.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 DetailedNetworkCreator.createDetailedView(view);
	             }
	         });
		     item.setText("Create Detailed View");
		
		     nntkMenu.add(item);
	     }
	    
		
	     //MENU
	     if (nntkMenu.getItemCount()>0) 
		 	menu.add(nntkMenu);
     }
}
