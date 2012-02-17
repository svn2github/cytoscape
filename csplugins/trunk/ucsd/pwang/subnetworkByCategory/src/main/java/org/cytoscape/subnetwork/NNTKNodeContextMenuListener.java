
package org.cytoscape.subnetwork; 

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import giny.view.GraphView;
import giny.view.NodeView;
import cytoscape.view.CyNetworkView;
import ding.view.NodeContextMenuListener;
import javax.swing .MenuElement;


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

         JMenu nntkMenu = null;
         
         // check if "Nested Network" already existed
         MenuElement[] mElements = menu.getSubElements();
         for (int i=0; i< mElements.length; i++){
        	 if (mElements[i] instanceof JMenu){
        		 JMenu m= (JMenu)mElements[i];
        		 if (m.getText().equalsIgnoreCase("Nested Network")){
        			 nntkMenu = m;
        			 break;
        		 }
        	 }
         }
         
         if (nntkMenu == null){
    	     nntkMenu = new JMenu("Nested Network");
         }
         
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
