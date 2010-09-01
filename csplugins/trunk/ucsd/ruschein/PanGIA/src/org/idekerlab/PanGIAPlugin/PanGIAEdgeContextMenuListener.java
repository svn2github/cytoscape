package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import giny.view.EdgeView;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.view.CyNetworkView;
import ding.view.EdgeContextMenuListener;

public class PanGIAEdgeContextMenuListener implements EdgeContextMenuListener
{
	 private final CyNetworkView view;
	 private CyAttributes edgeAttr = Cytoscape.getEdgeAttributes();


     public PanGIAEdgeContextMenuListener(CyNetworkView view)
     {
         this.view = view;
     }

     public void addEdgeContextMenuItems(EdgeView ev, JPopupMenu menu)
     {
         if (menu == null)
                 return;

         boolean selectedHasNested = false;
         
         for (Object n : ev.getGraphView().getSelectedNodes())
        	 if (((ding.view.DNodeView)n).getNode().getNestedNetwork()!=null)
        	 {
        		 selectedHasNested = true;
        		 break;
        	 }
        
         if (selectedHasNested && PanGIAPlugin.output.isAvailable())
         {
	         final JMenu pangiaMenu = new JMenu("PanGIA");
	
	         JMenuItem item = new JMenuItem();
	         item.setText("Create detailed view");
	         item.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 DetailedNetworkCreator.createDetailedView(view);
	             }
	         });
	
	         pangiaMenu.add(item);
	
	         menu.add(pangiaMenu);
         }
	}
}
