/*
 * StartPanel.java
 *
 * Created on July 31, 2006, 2:34 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package GOlorize;

import javax.swing.*;
import java.awt.event.*;


/**
 *
 * @author ogarcia
 */
public class StartPanel extends javax.swing.JPanel{
    
    /** Creates a new instance of StartPanel */
    GoBin goBin;
    JPanel jPanelDeBase;
    final JTabbedPane jTabbedPane;
    StartPanelPanel tabAll;
    StartPanelPanel tabMolFunction;
    StartPanelPanel tabBioProcess;
    StartPanelPanel tabCellComponant;   
    StartPanelPanel tabOther;
    
    public StartPanel(GoBin goB) {
        this.jTabbedPane=new javax.swing.JTabbedPane();
        
        this.goBin=goB;     
        initComponents();
        
        
        
        
    }
    
     void initComponents(){
        this.setLayout(new java.awt.BorderLayout());
        //jPanelDeBase= new JPanel();
        
        
        //this.add(jPanelDeBase,java.awt.BorderLayout.NORTH);
        //jPanelDeBase.setLayout(new java.awt.BorderLayout());
        
         JPanel northPanel = new JPanel();
         //northPanel.add(new JLabel(" on me vois ou pas ?"));
        
   //     //jPanelDeBase.add(new JLabel(" on me vois ou pas ?"),java.awt.BorderLayout.SOUTH);
        
        //jPanelDeBase.add(jTabbedPane,java.awt.BorderLayout.CENTER);
        
        jTabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        
        tabAll = new StartPanelPanel(goBin,StartPanelPanel.ALLTYPE);
        jTabbedPane.addTab("       All        ",tabAll);
        
        
        tabBioProcess = new StartPanelPanel(goBin,StartPanelPanel.BPTYPE);
        jTabbedPane.addTab("Biological Process",tabBioProcess);
        
        
        tabCellComponant = new StartPanelPanel(goBin,StartPanelPanel.CCTYPE);
        jTabbedPane.addTab("Cellular Component",tabCellComponant);
        
        
        tabMolFunction = new StartPanelPanel(goBin,StartPanelPanel.MFTYPE);
        jTabbedPane.addTab("Molecular Function",tabMolFunction);     
        
        
        tabOther = new StartPanelPanel(goBin,StartPanelPanel.OTHERTYPE);
        jTabbedPane.addTab("       Other      ",tabOther);         
        jTabbedPane.setSelectedIndex(0);
        
        
        
        this.add(northPanel,java.awt.BorderLayout.NORTH);
        this.add(jTabbedPane,java.awt.BorderLayout.CENTER);
        
        
        //this.add(new JLabel("ouahhhhh"),java.awt.BorderLayout.SOUTH);
        //jTabbedPane.validate();
        //jPanelDeBase.validate();
        //this.validate();
        //jPanelDeBase.add(jTabbedPane,java.awt.BorderLayout.CENTER);
                  
                    
                    
                    
             
        
    }
    
}
