package org.isb.bionet.gui;

import java.util.*;

import org.isb.bionet.datasource.interactions.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * A modal dialog that displays BIND parameters.
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class BindGui extends JDialog implements InteractionsSourceGui{
    
    /**
     * The title of this JFrame
     */
    public static final String TITLE = "BIND Settings";
    protected Map interactionToCheckBox;
    
    /**
     * Constructor, sets title to TITLE 
     * 
     * @param interactions_source
     */
    public BindGui (){
        setTitle(TITLE);
        setModal(true);
        createGUI();
    }//BindGui
    
    /**
     * Gets a Hashtable with (key, value) entries that a BIND interactions handler understands
     * @return a Hashtable
     * @see org.isb.bionet.datasource.interactions.BindInteractionsSource
     */
    public Hashtable getArgsTable (){
        Vector interactionTypes = getSelectedInteractionTypes();
        Hashtable args = new Hashtable();
        if(interactionTypes.size() < 3) args.put(BindInteractionsSource.INT_TYPES_ARG, interactionTypes);
        return args;
    }
    
    
    /**
     * @return a Vector of Strings that represent the selected types of interactions
     */
    public Vector getSelectedInteractionTypes (){
        Vector types = new Vector();
        if(this.interactionToCheckBox.isEmpty())
            return types;
        
        Iterator it = this.interactionToCheckBox.keySet().iterator();
        while(it.hasNext()){
            String key = (String)it.next();
            JCheckBox cb = (JCheckBox)this.interactionToCheckBox.get(key);
            if(cb.isSelected())
                types.add(key);
        }//while it
        
        return types;
    }//getSelectedInteractionsTypes
    
    
    /**
     * Creates the JFrame with Prolinks parameters
     */
    protected void createGUI (){
        JPanel interactions = createInteractionsPanel();
        JPanel buttons = createButtonsPanel();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setBorder(BorderFactory.createEtchedBorder());
        
        panel2.add(interactions);
   
        panel.add(panel2);
        panel.add(buttons);
        
        setContentPane(panel);
        
    }//createGUI
    
    /**
     * 
     * @return
     */
    protected JPanel createInteractionsPanel (){
        
        Hashtable interactionTypes = BindInteractionsSource.INT_TYPES;
        this.interactionToCheckBox = new HashMap();
        JPanel panel = new JPanel();
        int rows = interactionTypes.size()/2;
        if(interactionTypes.size() % 2 == 1){
            rows++;
        }
        int cols = 2;
        panel.setLayout(new GridLayout(rows,cols));
        
        Iterator it = interactionTypes.keySet().iterator();
        while(it.hasNext()){
            String type = (String)it.next();
            JCheckBox cb = new JCheckBox();
            cb.setText(type + " (" + interactionTypes.get(type) + ")");
            cb.setSelected(true);
            panel.add(cb);
            this.interactionToCheckBox.put(type, cb);
        }//while it
        
        JLabel label = new JLabel("Select desired interaction types:");
        JPanel labelPanel = new JPanel();
        labelPanel.add(label);
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.add(labelPanel);
        p.add(panel);
        return p;
    }//createInteractionsPanel
    
    protected JPanel createButtonsPanel (){
        JButton OK = new JButton("OK");
        OK.addActionListener(new AbstractAction (){
           
            public void actionPerformed (ActionEvent e){
                BindGui.this.dispose();
            }//actionPerformed
            
        });
        JPanel panel = new JPanel();
        panel.add(OK);
        return panel;
    }//createButtonsPanel
}//BindGui