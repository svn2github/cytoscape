
package org.isb.bionet.gui;

import cytoscape.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;

public class CyNetworksDialog extends JDialog {
    
    
    protected JList netsList;
    
    public CyNetworksDialog (){
        setTitle("Cytoscape Networks");
        setModal(true);
        create();
    }
    
    /**
     * @return an array of selected CyNetworks
     */
    public CyNetwork [] getSelectedNetworks (){
        Object [] selectedItems = this.netsList.getSelectedValues();
        CyNetwork [] nets = new CyNetwork[selectedItems.length];
        for(int i = 0; i < selectedItems.length; i++){
            ListItem item = (ListItem)selectedItems[i];
            nets[i] = item.net;
        }//for i
        return nets;
    }
    
    /**
     * Updates the dialog by checking newly loaded networks in Cytoscape
     */
    public void update (){
        
        Set networks = Cytoscape.getNetworkSet();
        ListItem [] items = new ListItem[networks.size()];
        Iterator it = networks.iterator();
        int i = 0;
        while(it.hasNext()){
            CyNetwork cyNet = (CyNetwork)it.next();
            String netName = cyNet.getTitle();
            ListItem listItem = new ListItem(cyNet, netName);
            items[i] = listItem;
            i++;
        }//while it
        
        if(this.netsList == null){
            this.netsList = new JList();
            this.netsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            this.netsList.setLayoutOrientation(JList.VERTICAL);
            this.netsList.setVisibleRowCount(-1);
        }else{
            this.netsList.removeAll();
        }
        this.netsList.setListData(items);
        
    }
    
    protected void create (){
        update();
       
        JScrollPane listScroller = new JScrollPane(this.netsList);
        listScroller.setPreferredSize(new Dimension(80,80));
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        JPanel labelPanel = new JPanel();
        JLabel label = new JLabel("Select Cytoscape Networks:");
        labelPanel.add(label);
        
        mainPanel.add(labelPanel,BorderLayout.NORTH);
        mainPanel.add(listScroller, BorderLayout.CENTER);
        
        JPanel buttonsPanel = new JPanel();
        JButton OK = new JButton("OK");
        OK.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        CyNetworksDialog.this.dispose();
                    }//actionPerformed
                    
                }//AbstractAction
        );
        buttonsPanel.add(OK);
        
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    
    protected static final CyNetworksDialog DIALOG = new CyNetworksDialog();
    
    /**
     * Shows a CyNetworksDialog
     * @param relativeTo the Component relative to which position the dialog
     */
    public static void showDialog (Component relativeTo){
        DIALOG.update();
        DIALOG.pack();
        DIALOG.setLocationRelativeTo(relativeTo);
        DIALOG.setVisible(true);
    }
    
    /**
     * @return the selected networks in the static CyNetworksDialog that gets shown
     * when showDialog gets called
     */
    public static CyNetwork[] getSelectedCyNetworks (){
        return DIALOG.getSelectedNetworks();
    }
    
    // Internal class
    
    protected class ListItem {
        public CyNetwork net;
        public String netName;
        
        public ListItem (CyNetwork net, String netName){
            this.net = net;
            this.netName = netName;
        }
        
        public String toString (){
            return this.netName;
        }
    }//ListItem
    
}