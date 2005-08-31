
package org.isb.bionet.gui.wizard;

import javax.swing.*;

public class NetworkSettingsPanel extends JPanel{
    
    protected JTextField nameField;
    protected JRadioButton createView;
    
    /**
     * Constructor, calls create()
     */
    public NetworkSettingsPanel (){
        create();
    }
    
    /**
     * @return whether or not to create a view for the network
     */
    public boolean createView (){
        return this.createView.isSelected();
    }
    
    /**
     * @return the name of the new network
     */
    public String getNetworkName (){
        String name = this.nameField.getText();
        return name;
    }
    
    /**
     *  Creates the panel
     */
    protected void create (){
        
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        
        JPanel namePanel = new JPanel();
        JLabel netName = new JLabel("Network Name:");
        this.nameField = new JTextField(20);
        namePanel.add(netName);
        namePanel.add(Box.createHorizontalStrut(5));
        namePanel.add(this.nameField);
        
        add(namePanel);
        
        JPanel viewPanel = new JPanel();
        this.createView = new JRadioButton("Create a Network View");
        createView.setSelected(true);
        viewPanel.add(this.createView);
        
        add(viewPanel);
    }
    
}