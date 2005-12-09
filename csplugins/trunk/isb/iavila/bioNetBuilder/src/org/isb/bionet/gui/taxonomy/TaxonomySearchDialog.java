
package org.isb.bionet.gui.taxonomy;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import org.isb.bionet.datasource.synonyms.SynonymsClient;
import org.isb.bionet.datasource.synonyms.SynonymsSource;

public class TaxonomySearchDialog extends JFrame {
    
    protected SynonymsClient synonymsClient;
    protected JTextField speciesInput, geneInput;
    protected JList speciesList, genesList;
    
    /**
     * Creates the dialog
     * 
     * @param synonyms_client the client for the synonyms database from which to obtain
     * taxonomy information
     */
    public TaxonomySearchDialog (SynonymsClient synonyms_client){
        super("NCBI Taxonomy");
        this.synonymsClient = synonyms_client;
        create();
    }
    
    
    /**
     * Creates the dialog
     */
    protected void create (){
        
        // First, create all the GUI stuff
        
        JPanel mainFrame = new JPanel();
        mainFrame.setLayout(new BorderLayout());
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.X_AXIS));
        
        // LEFT SIDE, SPECIES PATTERN MATCHING
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        
        // the text field to enter species
        JPanel spLabelPanel = new JPanel();
        JLabel spLabel = new JLabel("Species that contain:");
        spLabelPanel.add(spLabel);
        speciesInput = new JTextField(10);
        spLabelPanel.add(Box.createHorizontalStrut(5));
        spLabelPanel.add(speciesInput);
        JButton searchSpButton = new JButton("Search");
        spLabelPanel.add(Box.createHorizontalStrut(5));
        spLabelPanel.add(searchSpButton);
        
        leftPanel.add(spLabelPanel, BorderLayout.NORTH);
        
        // results of search display
        speciesList = new JList(); 
        speciesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        speciesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        speciesList.setVisibleRowCount(-1);
        JScrollPane listScroller1 = new JScrollPane(speciesList);
        listScroller1.setPreferredSize(new Dimension(80, 160));
        
        leftPanel.add(listScroller1, BorderLayout.CENTER);
        
        centerPanel.add(leftPanel);
        
        // RIGHT SIDE, GENE PATTERN MATCHING
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        
        JPanel gLabelPanel = new JPanel();
        JLabel geneLabel = new JLabel("Genes that contain:");
        gLabelPanel.add(geneLabel);
        geneInput = new JTextField(10);
        gLabelPanel.add(Box.createHorizontalStrut(5));
        gLabelPanel.add(geneInput);
        JButton searchGenesButton = new JButton("Search");
        gLabelPanel.add(Box.createHorizontalStrut(5));
        gLabelPanel.add(searchGenesButton);
        rightPanel.add(gLabelPanel, BorderLayout.NORTH);
        
        // results of search display
        genesList = new JList(); 
        genesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        genesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        genesList.setVisibleRowCount(-1);
        JScrollPane listScroller2 = new JScrollPane(genesList);
        listScroller2.setPreferredSize(new Dimension(80, 160));
        
        rightPanel.add(listScroller2, BorderLayout.CENTER);
        centerPanel.add(rightPanel);
        
        mainFrame.add(centerPanel, BorderLayout.CENTER);
        
        // BUTTONS
        JPanel buttonsPanel = new JPanel();
        JButton okButton = new JButton("OK");
        JButton helpButton = new JButton("Help");
        JButton cancelButton = new JButton("Cancel");
        buttonsPanel.add(okButton);
        buttonsPanel.add(helpButton);
        buttonsPanel.add(cancelButton);
        
        mainFrame.add(buttonsPanel, BorderLayout.SOUTH);
        
        this.setContentPane(mainFrame);
        
        // Now, add actions to buttons
        
        searchSpButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent e){
                        searchForSpecies();
                    }//actionPerformed
                    
                }//AbstractAction
        );
        
        searchGenesButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent e){
                        searchForGenes();
                    }//actionPerformed
                    
                }//AbstractAction
        );
        
        cancelButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed(ActionEvent e){
                        TaxonomySearchDialog.this.dispose();
                    }//actionPerformed
                }//AbstractAction
        );
        
        okButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed(ActionEvent e){
                        // For now!
                        TaxonomySearchDialog.this.dispose();
                    }//actionPerformed
                }//AbstractAction
        );
        
        helpButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed(ActionEvent e){
                        JOptionPane.showMessageDialog(TaxonomySearchDialog.this,"HELP GOES HERE","Help", JOptionPane.INFORMATION_MESSAGE);                        
                    }//actionPerformed
                }//AbstractAction
        );
        
        
        
    }
    
    
    protected void searchForSpecies (){
        String speciesString = this.speciesInput.getText();
        if(speciesString == null || speciesString.length() == 0) return;
        
        speciesString.trim();
        
        Vector species = null;
        try{
            species = this.synonymsClient.getSpeciesLike(speciesString);
        }catch (Exception e){
            e.printStackTrace();
            return;
        }
        
        if(species == null) return;
        
        // For now:
        this.speciesList.removeAll();
        this.speciesList.setListData(species);       
    }
    
    protected void searchForGenes (){
        String genesString = this.geneInput.getText();
        if(genesString == null ||  genesString.length() == 0) return;
        
        genesString.trim();
        
        Object[] species = this.speciesList.getSelectedValues();
        if(species == null || species.length == 0) return;
       
        Hashtable matches = new Hashtable();
        for(int i = 0; i < species.length; i++){
            Hashtable sp = (Hashtable)species[i];
            String taxID = (String)sp.get(SynonymsSource.TAXID);
            try{
                matches.putAll(this.synonymsClient.getGenesLike(taxID,genesString));
            }catch(Exception e){ e.printStackTrace(); continue;}
        }//for i
       
        Vector entries = new Vector();
        entries.addAll(matches.entrySet());
        this.genesList.removeAll();
        this.genesList.setListData(entries);
        
    }
    
}