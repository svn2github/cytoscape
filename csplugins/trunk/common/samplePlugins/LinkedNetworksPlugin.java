//---------------------------------------------------------------------------
//  $Revision:  
//  $Date$
//  $Author$
//---------------------------------------------------------------------------
package csplugins.common.samplePlugins;

import java.util.*;
import java.io.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;

/**
 * This plugin allows the user to select a set of existing network objects
 * that should be synchronized with respect to flagging. When a node
 * or edge is flagged or unflagged in one of the networks, this state
 * is propagated to all of the other linked networks.
 */
public class LinkedNetworksPlugin extends CytoscapePlugin {
    
    NetworkLinker linker = new NetworkLinker();
    
    public LinkedNetworksPlugin() {
        LinkedNetworksAction action = new LinkedNetworksAction();
        action.setPreferredMenu("Plugins");
        Cytoscape.getDesktop().getCyMenus().addAction(action);
    }
    
    /**
     * Sets the set of networks to link. The argument is a Collection of
     * names of networks, and should be non-null and contain at least
     * two entries. This method does nothing if the argument is null.
     */
    public void setLinkedNetworksByName(Collection networksByName) {
        if (networksByName == null || networksByName.size() < 2) {return;}
        Set networks = new HashSet();
        for (Iterator iter = networksByName.iterator(); iter.hasNext(); ) {
            String name = (String)iter.next();
            CyNetwork network = Cytoscape.getNetwork(name);
            if (network == null) {continue;}
            networks.add(network);
        }
        linker.setNetworks(networks);
    }
    
    public class LinkedNetworksAction extends CytoscapeAction {
        
        public LinkedNetworksAction() {super("Link Networks");}
        
        /**
         * This method gets run when the user selects the associated menu option.
         */
        public void actionPerformed(ActionEvent ae) {
            showNetworkSelectionUI();
        }
    }
    
    /**
     * Creates a simple UI allowing the user to select from the current list
     * of Cytoscape networks. This set will be installed as the group of
     * networks to link together.
     */
    public void showNetworkSelectionUI() {
            String titleString = "LinkNetworks";
            JDialog dialog = new JDialog(Cytoscape.getDesktop(), titleString, true);

            Set allNetworks = Cytoscape.getNetworkSet();
            Vector tempVector = new Vector(allNetworks);
            JList listUI = new JList(tempVector);
            listUI.setVisibleRowCount(10);
            String lineSep = System.getProperty("line.separator");
            String headString = "Please select the networks to link."
                + lineSep + "Any selection will be selected in all linked networks.";
            JLabel label = new JLabel(headString);
            JButton OKButton = new JButton("OK");
            OKButton.addActionListener( new OKButtonListener(dialog, listUI) );
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener( new CancelButtonListener(dialog) );
            
            JPanel topPanel = new JPanel();
            topPanel.setLayout( new GridLayout(2,1) );
            topPanel.add(label);
            topPanel.add(listUI);
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(OKButton);
            buttonPanel.add(cancelButton);
            
            JPanel contentPane = new JPanel();
            contentPane.setLayout( new GridLayout(2,1) );
            contentPane.add(topPanel);
            contentPane.add(buttonPanel);

            dialog.setContentPane(contentPane);
            dialog.pack();
            dialog.show();
    }
    
    /**
     * Responds when the user hits the "OK" button in the UI. Gets the
     * currently selected networks and passes them to setLinkedNetworksByName.
     */
    public class OKButtonListener implements ActionListener {
        JDialog dialog;
        JList listUI;
        public OKButtonListener(JDialog dialog, JList listUI) {
            this.dialog = dialog;
            this.listUI = listUI;
        }
        public void actionPerformed(ActionEvent ae) {
            Object[] selectedValues = listUI.getSelectedValues();
            setLinkedNetworksByName(Arrays.asList(selectedValues));
            dialog.dispose();
        }
    }
    
    /**
     * Responds when the user hits the "Cancel" button in the UI. Disposes
     * of the UI without changing the current set of linked networks.
     */
    public class CancelButtonListener implements ActionListener {
        JDialog dialog;
        public CancelButtonListener(JDialog dialog) {this.dialog = dialog;}
        public void actionPerformed(ActionEvent ae) {dialog.dispose();}
    }
}

            
            
