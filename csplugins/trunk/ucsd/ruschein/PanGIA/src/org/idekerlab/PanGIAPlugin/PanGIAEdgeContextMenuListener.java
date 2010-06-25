package org.idekerlab.PanGIAPlugin;

import giny.view.EdgeView;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

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

         final JMenu pangiaMenu = new JMenu("PanGIA");

         JMenuItem item = new JMenuItem();
         item.setText("Yeah! You got an edge!");

         pangiaMenu.add(item);

         menu.add(pangiaMenu);
	}
}
