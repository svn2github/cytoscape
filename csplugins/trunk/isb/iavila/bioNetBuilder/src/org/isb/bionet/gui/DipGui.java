package org.isb.bionet.gui;

import java.util.*;

import org.isb.bionet.datasource.interactions.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

/**
 * A modal dialog that displays DIP parameters
 * 
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class DipGui extends JDialog implements InteractionsSourceGui{
    
    /**
     * The title of this JFrame
     */
    public static final String TITLE = "DIP Settings";
    
    /**
     * 
     *
     */
    public DipGui (){
        create();
    }
    
    /**
     * 
     *
     */
    protected void create (){
        JLabel label = new JLabel("Send suggestions to iavila@systemsbiology.org");
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
        getContentPane().add(label);
        JButton ok = new JButton("OK");
        getContentPane().add(ok);
        
        ok.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent e){
                        DipGui.this.dispose();
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
    