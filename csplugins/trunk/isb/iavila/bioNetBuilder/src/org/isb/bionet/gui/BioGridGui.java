package org.isb.bionet.gui;

import java.util.*;
import javax.swing.*;
import java.awt.event.*;


/**
 * A modal dialog that displays BioGrid parameters
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class BioGridGui extends JDialog implements InteractionsSourceGui{
    
    /**
     * The title of this JFrame
     */
    public static final String TITLE = "BioGrid Settings";
    
    /**
     * 
     *
     */
    public BioGridGui (){
        create();
    }
    
    /**
     * Creates the BioGrid dialog
     * We need checkboxes for evidence types
     * For now, just do this
     */
    protected void create (){
        JLabel label = new JLabel("TODO: Add EVIDENCE checkboxes (for example, Two-hybrid, In Vivo, etc)");
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(label);
        JButton ok = new JButton("OK");
        getContentPane().add(ok);
        
        ok.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent e){
                        BioGridGui.this.dispose();
                    }
                }
        );
    }
    
    /**
     * @return and empty table for now
     */
    public Hashtable getArgsTable (){
        return new Hashtable();
    }
    
}
    