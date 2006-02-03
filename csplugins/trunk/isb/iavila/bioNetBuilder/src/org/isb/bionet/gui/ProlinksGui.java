
/**
 * ProlinksGui.java
 */

package org.isb.bionet.gui;

import java.util.*;

import org.isb.bionet.datasource.interactions.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * A modal dialog that displays Prolinks parameters.
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class ProlinksGui extends JDialog implements InteractionsSourceGui{
    
    /**
     * The title of this JFrame
     */
    public static final String TITLE = "Prolinks Settings";

    /**
     * The default p-value threshold
     */
    public static final String DEFAULT_PVAL = "0.05";
    protected Map interactionToCheckBox;
    protected JTextField pvalField;
    
    /**
     * Constructor, sets title to TITLE 
     * 
     * @param interactions_source
     */
    public ProlinksGui (){
        setTitle(TITLE);
        setModal(true);
        createGUI();
    }//ProlinksGui
    
    /**
     * Gets a Hashtable with (key, value) entries that a Prolinks interactions handler understands
     * @return a Hashtable
     * @see org.isb.bionet.datasource.interactions.ProlinksInteractionsSource
     */
    public Hashtable getArgsTable (){
        Vector interactionTypes = getSelectedInteractionTypes();
        double pvalTh = getPval(false);
        Hashtable args = new Hashtable();
        if(pvalTh != 1) args.put(ProlinksInteractionsSource.PVAL, new Double(pvalTh));
        if(interactionTypes.size() < 4) args.put(ProlinksInteractionsSource.INTERACTION_TYPE, interactionTypes);
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
     * @param show_error if the pvalue is incorrect, pop-up a JOptionDialog to inform the user
     * @return the pvalue, or a number > 1 if there was an error
     */
    public double getPval (boolean show_error){
        String text = this.pvalField.getText();
        boolean badInput = false;
        double pval = 2.0;
        try{
            pval = Double.parseDouble(text);
            if(pval < 0 || pval > 1)
                badInput = true;
        }catch(Exception e){
            badInput = true;
        }

        if(show_error && badInput)
            JOptionPane.showMessageDialog(this,"Please enter a correct p-value (number between 0 and 1).", "Incorrect Pvalue", JOptionPane.ERROR_MESSAGE);
        
        return pval;
    }//getPval
    
    /**
     * Creates the JFrame with Prolinks parameters
     */
    protected void createGUI (){
        JPanel interactions = createInteractionsPanel();
        JPanel pval = createPvalPanel();
        JPanel buttons = createButtonsPanel();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
        panel2.setBorder(BorderFactory.createEtchedBorder());
        
        panel2.add(interactions);
        panel2.add(pval);
        
        panel.add(panel2);
        panel.add(buttons);
        
        setContentPane(panel);
        
    }//createGUI
    
    /**
     * 
     * @return
     */
    protected JPanel createInteractionsPanel (){
        
        Hashtable interactionTypes = ProlinksInteractionsSource.INT_TYPES;
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
    
    
    /**
     * 
     * @return
     */
    protected JPanel createPvalPanel (){
        
        JPanel panel = new JPanel();
        
        JLabel label = new JLabel("Enter a p-value threshold:");
        panel.add(label);
        panel.add(Box.createHorizontalStrut(5));
        
        this.pvalField = new JTextField();
        this.pvalField.setColumns(5);
        this.pvalField.setText(DEFAULT_PVAL);
        
        panel.add(this.pvalField);
        
        return panel;
    }//createPvalPanel
    
    protected JPanel createButtonsPanel (){
        JButton OK = new JButton("OK");
        OK.addActionListener(new AbstractAction (){
           
            public void actionPerformed (ActionEvent e){
                ProlinksGui.this.getPval(true); // make sure the entered pval is correct
                ProlinksGui.this.dispose();
            }//actionPerformed
            
        });
        JPanel panel = new JPanel();
        panel.add(OK);
        return panel;
    }//createButtonsPanel
}//ProlinksGui