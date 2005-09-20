
package org.isb.bionet.gui.wizard;

import javax.swing.*;

public class NetworkSettingsPanel extends JPanel{
    
    protected JTextField nameField;
    protected JRadioButton createView;
    protected JRadioButton createRosettaUrl;
    
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
     * @return whether or not to create a Rosetta Benchmark URL for node attributes
     */
    public boolean createRosettaURLAttribute (){
        return this.createRosettaUrl.isSelected();
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
        
        JPanel attsPanel = new JPanel();
        this.createRosettaUrl = new JRadioButton("<html>Create Rosetta Benchmark URL node attribute<br>(currently, only for yeast)</html>");
        this.createRosettaUrl.setSelected(true);
        attsPanel.add(createRosettaUrl);
        
        add(viewPanel);
        add(attsPanel);
    }
    
}