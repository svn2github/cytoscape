package org.isb.bionet.gui.taxonomy;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;

import org.isb.bionet.datasource.synonyms.*;

/**
 * This panel contains a text field where users enter their searched for species, and a JList that displays NCBI species that contain
 * the entered String.
 * 
 * @author Iliana Avila-Campillo
 * @since 2.3
 *
 */
public class TaxonomyPanel extends JPanel{
    
    /**
     * A SynonymsClient that communicated with a remote synonyms database
     */
    protected SynonymsClient synonymsClient;
    /**
     * The field where users enter a search String for a species
     */
    protected JTextField speciesInput;
    /**
     * A list of matching species to the user's input
     */
    protected JList speciesList;
    
    
    /**
     * 
     * @param syn_client a SynonymsClient that contains taxonomy tables
     */
    public TaxonomyPanel (SynonymsClient syn_client){
        this.synonymsClient = syn_client;
        create();
    }
    
    /**
     * Gets a vector of Strings parsable as integers that represent NCBI taxids of the selected
     * species in the panel's <code>JList</code>
     * 
     * @return a Vector of Strings parsable as integers that represent NCBI taxids
     */
    public Vector getSelectedSpeciesTaxids (){
        Object[] species = this.speciesList.getSelectedValues();
        if(species == null || species.length == 0) return new Vector();
       
        Vector taxids = new Vector();
        for(int i = 0; i < species.length; i++){
            Hashtable sp = (Hashtable)species[i];
            String taxID = (String)sp.get(SynonymsSource.TAXID);
            taxids.add(taxID);
        }//for i
        return taxids;
    }
   
    /**
     * Creates the panel
     *
     */
    protected void create (){
        
        setLayout(new BorderLayout());
        
        // the text field to enter species
        JPanel spLabelPanel = new JPanel();
        JLabel spLabel = new JLabel("Species that contain:");
        spLabelPanel.add(spLabel);
        this.speciesInput = new JTextField(10);
        spLabelPanel.add(Box.createHorizontalStrut(5));
        spLabelPanel.add(this.speciesInput);
        JButton searchSpButton = new JButton("Search");
        spLabelPanel.add(Box.createHorizontalStrut(5));
        spLabelPanel.add(searchSpButton);
        
        add(spLabelPanel, BorderLayout.NORTH);
        
        // results of search display
        speciesList = new JList(); 
        speciesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        speciesList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        speciesList.setVisibleRowCount(-1);
        JScrollPane listScroller1 = new JScrollPane(speciesList);
        listScroller1.setPreferredSize(new Dimension(80, 160));
        
        add(listScroller1, BorderLayout.CENTER);
        
        // Add action to the search button
        searchSpButton.addActionListener(
                new AbstractAction (){
                    
                    public void actionPerformed (ActionEvent e){
                        searchForSpecies();
                    }//actionPerformed
                    
                }//AbstractAction
        );
        
           
    }
    
    /**
     * Searches for species that match the String entered in <code>speciesInput</code> and adds them
     * to <code>this.speciesList JList</code>
     *
     */
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
    
}