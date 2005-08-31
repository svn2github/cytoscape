/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.isb.bionet.datasource.interactions.ProlinksInteractionsSource;
import org.isb.bionet.gui.*;
import java.util.*;

/**
 * 
 * @author iavila
 * 
 */
public class EdgeSourcesPanel extends JPanel {

    /**
     * The fully specified edge source class to its human friendly name for
     * buttons
     */
    protected Map sourceToName;

    /**
     * If a data source has been selected, it's fully specified calss will be in
     * this Map with a Vector of species as a value
     */
    protected Map sourceToSpecies;

    /**
     * The list of buttons
     */
    protected List buttons;
    
    /**
     * 
     * @param edgeSourceToName
     *            the fully specified edge source class to its human friendly
     *            name for buttons
     * @param souceToSelectedSpecies
     *            if a data source has been selected, it's fully specified calss
     *            will be in this Map with a Vector of species as a value
     */
    public EdgeSourcesPanel(Map edgeSourceToName, Map sourceToSelectedSpecies) {
        this.sourceToName = edgeSourceToName;
        this.sourceToSpecies = sourceToSelectedSpecies;
        create();
    }
    
    /**
     * 
     * @param buttonName the human friendly name of the button to enable
     * @param enabled true or false
     */
    public void setSourceButtonEnabled (String buttonName, boolean enabled){
        Iterator it = this.buttons.iterator();
        while(it.hasNext()){
            JButton button = (JButton)it.next();
            String actualName = button.getText();
            if(actualName.startsWith(buttonName)){
                button.setEnabled(enabled);
                return;
            }
        }//while it.hasNext
    }

    protected void create() {

        // Create buttons and select them if the user has selected them as data sources
        this.buttons = new ArrayList();
        Iterator it = this.sourceToName.keySet().iterator();
        while(it.hasNext()){
            String sourceClass = (String)it.next();
            String buttonName = (String)this.sourceToName.get(sourceClass);
            boolean enabled = this.sourceToSpecies.containsKey(sourceClass);
            JButton button = new JButton(buttonName + "...");
            if(buttonName.equals(ProlinksInteractionsSource.NAME)){
                button.addActionListener(new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        ProlinksGui pDialog  = new ProlinksGui();
                        pDialog.pack();
                        pDialog.setLocationRelativeTo(EdgeSourcesPanel.this);
                        pDialog.setVisible(true);
                    }//actionPerformed
                });//AbstractAction
            }
            button.setEnabled(enabled);
            this.buttons.add(button);
        }//while it
        
        JButton netsButton = new JButton("Loaded Networks...");
        this.buttons.add(netsButton);
        
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.ipadx = 5;
        JLabel sourceLabel = new JLabel("Edge Source");
        gridbag.setConstraints(sourceLabel, c);
        add(sourceLabel);

        c.gridwidth = GridBagConstraints.REMAINDER; // end row

        JLabel stats = new JLabel("Num Edges");
        gridbag.setConstraints(stats, c);
        add(stats);
        
        c.fill = GridBagConstraints.HORIZONTAL;
        it = this.buttons.iterator();
        while(it.hasNext()){
            c.gridwidth = 1; // reset to the default
            JButton button = (JButton)it.next();
            gridbag.setConstraints(button,c);
            add(button);
            c.gridwidth = GridBagConstraints.REMAINDER;
            JTextField edgesNum = new JTextField(4);
            edgesNum.setEditable(false);
            gridbag.setConstraints(edgesNum, c);
            add(edgesNum);
        }//while it buttons
        
    }//create 
}