/**
 *
 */
package org.isb.bionet.gui.wizard;

import java.awt.event.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

import java.io.File;
import java.util.Vector;

import org.isb.bionet.gui.*;

import utils.MyUtils;
import cytoscape.*;

public class NodeSourcesPanel extends JPanel {

    protected File myListFile;
    protected Vector myListNodes;
    protected CyNetworksDialog netsDialog;
    protected JTextField listNodes, annotsNodes, netsNodes; 
    /**
     *  Creates a panel with node sources
     */
    public NodeSourcesPanel (){
        create();
    }
    
    /**
     * @return if a file has been selected, it returns it, returns null otherwise
     */
    public File getMyListFile (){
        return this.myListFile;
    }
    
    /**
     * 
     * @return the CyNetworks to be used as sources for nodes
     */
    public CyNetwork [] getSelectedNetworks (){
        if(this.netsDialog != null)
            return this.netsDialog.getSelectedNetworks();
        return new CyNetwork[0];
    }
    
    /**
     * 
     * @return a Vector with the node names in "myList" file
     */
    public Vector getNodesFromMyList (){
        return this.myListNodes;
    }
    
    /**
     * Creates the panel
     */
    protected void create() {
        
        final JButton annotsButton = new JButton("Nodes with selected annotations...");
        annotsButton.setEnabled(false);
        annotsButton.addActionListener(new AbstractAction(){
            
            public void actionPerformed (ActionEvent event){
                JOptionPane.showMessageDialog(NodeSourcesPanel.this, "Not implemented yet!", "Oops!", JOptionPane.ERROR_MESSAGE);
            }//actionPerformed
            
        });
        final JButton listButton = new JButton("Nodes from my list...");
        final JFileChooser fileChooser = new JFileChooser();
        listButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent event){
                        int returnVal = fileChooser.showOpenDialog(NodeSourcesPanel.this);
                        if(returnVal == JFileChooser.APPROVE_OPTION) {
                            myListFile = fileChooser.getSelectedFile();
                            try{
                                myListNodes = MyUtils.ReadFileLines(myListFile.getAbsolutePath());
                                int numRead = myListNodes.size();
                                listNodes.setText(Integer.toString(numRead));
                            }catch (Exception ex){
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(NodeSourcesPanel.this, "Could not read nodes in file " + myListFile.getName() + "!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }// APPROVE_OPTION
                    }//actionPerformed
                    
                }//AbstractAction
        );
        listButton.setEnabled(false);
        final JButton netsButton  =  new JButton("Nodes from loaded networks...");
        netsButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed (ActionEvent event){
                        // Make netsDialog modal
                        if(netsDialog == null){
                            netsDialog = new CyNetworksDialog();
                        }
                        netsDialog.update();
                        netsDialog.setLocationRelativeTo(NodeSourcesPanel.this);
                        netsDialog.pack();
                        netsDialog.setVisible(true);
                        // netsDialog is modal
                        CyNetwork [] nets = netsDialog.getSelectedNetworks();
                        int numNodes = 0;
                        for(int i = 0; i < nets.length; i++){
                            numNodes += nets[i].getNodeCount();
                        }//for i
                        netsNodes.setText(Integer.toString(numNodes));
                    }//actionPerformed
                }//AbstractAction
        );
        netsButton.setEnabled(false);
        
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagLayout gridbag = new GridBagLayout();
        this.setLayout(gridbag);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.ipadx = 5;
        Component emptyBox = Box.createHorizontalGlue();
        gridbag.setConstraints(emptyBox, c);
        this.add(emptyBox);

        JLabel use = new JLabel("Node Source");
        gridbag.setConstraints(use, c);
        this.add(use);

        c.gridwidth = GridBagConstraints.REMAINDER; // end row

        JLabel stats = new JLabel("Num Nodes");
        gridbag.setConstraints(stats, c);
        this.add(stats);

        c.gridwidth = 1; // reset to the default

        JCheckBox useAnnotations = new JCheckBox();
        useAnnotations.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        annotsButton.setEnabled(source.isSelected());
                    }
                }
        );
        useAnnotations.setSelected(false);
        gridbag.setConstraints(useAnnotations, c);
        this.add(useAnnotations);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(annotsButton, c);
        this.add(annotsButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.annotsNodes = new JTextField(4);
        this.annotsNodes.setText("0");
        this.annotsNodes.setEditable(false);
        gridbag.setConstraints(this.annotsNodes, c);
        this.add(this.annotsNodes);

        c.gridwidth = 1;

        c.fill = GridBagConstraints.NONE;
        JCheckBox useList = new JCheckBox();
        useList.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        listButton.setEnabled(source.isSelected());
                    }
                }
        );
        useList.setSelected(false);
        gridbag.setConstraints(useList, c);
        this.add(useList);

        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        gridbag.setConstraints(listButton, c);
        this.add(listButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.listNodes = new JTextField(4);
        this.listNodes.setEditable(false);
        this.listNodes.setText("0");
        gridbag.setConstraints(this.listNodes, c);
        this.add(this.listNodes);

        c.gridwidth = 1;

        c.fill = GridBagConstraints.NONE;
        JCheckBox useNets = new JCheckBox();
        useNets.addActionListener(
                new AbstractAction(){
                    public void actionPerformed(ActionEvent event){
                        JCheckBox source = (JCheckBox)event.getSource();
                        netsButton.setEnabled(source.isSelected());
                    }
                }
        );
        useNets.setSelected(false);
        gridbag.setConstraints(useNets, c);
        this.add(useNets);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = GridBagConstraints.RELATIVE;
        gridbag.setConstraints(netsButton, c);
        this.add(netsButton);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.netsNodes = new JTextField(4);
        this.netsNodes.setEditable(false);
        this.netsNodes.setText("0");
        gridbag.setConstraints(this.netsNodes, c);
        this.add(this.netsNodes);
    }
}