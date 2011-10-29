package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.cytoscape.view.model.View;
import org.cytoscape.model.CyEdge;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.view.model.CyNetworkView;
//import ding.view.EdgeContextMenuListener;

public class PanGIAEdgeContextMenuListener implements EdgeContextMenuListener 
{
	 private final CyNetworkView view;
	 private CyTable edgeAttr = view.getModel().getDefaultEdgeTable();


     public PanGIAEdgeContextMenuListener(CyNetworkView view)
     {
         this.view = view;
     }

     public void addEdgeContextMenuItems(View<CyEdge> ev, JPopupMenu menu)
     {
    	 PanGIANodeContextMenuListener.addContextMenuItems(view, ev.getGraphView(), menu);
    	 
    	 /*
         if (menu == null)
                 return;

         boolean selectedHasNested = false;
         
         for (Object n : ev.getGraphView().getSelectedNodes())
        	 if (((ding.view.DNodeView)n).getNode().getNestedNetwork()!=null)
        	 {
        		 selectedHasNested = true;
        		 break;
        	 }
        
         
         boolean isOverviewNetwork = PanGIAPlugin.output.isAvailable() && view.getNetwork().getIdentifier().equals(PanGIAPlugin.output.getOverviewNetwork().getIdentifier());
         
         
         if (selectedHasNested && isOverviewNetwork)
         {
        	 final JMenu pangiaMenu = new JMenu("PanGIA");
         
	         
	
	         JMenuItem item = new JMenuItem();
	         item.setText("Create Detailed View");
	         item.addActionListener(new ActionListener()
	         {
	             public void actionPerformed(ActionEvent e) {
	            	 DetailedNetworkCreator.createDetailedView(view);
	             }
	         });
	
	         pangiaMenu.add(item);
	         
	         
	         
	         menu.add(pangiaMenu);
         }
         */
         
	}
}
