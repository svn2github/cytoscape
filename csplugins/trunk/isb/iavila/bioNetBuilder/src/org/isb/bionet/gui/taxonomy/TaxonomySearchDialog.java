
package org.isb.bionet.gui.taxonomy;

import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import org.isb.bionet.datasource.synonyms.SynonymsClient;
import org.isb.bionet.datasource.synonyms.SynonymsSource;

public class TaxonomySearchDialog extends JDialog {
    
    protected SynonymsClient synonymsClient;
    protected JTextField geneInput;
    protected JList genesList;
    protected TaxonomyPanel taxonomyPanel;
    
    /**
     * Creates the dialog
     * 
     * @param synonyms_client the client for the synonyms database from which to obtain
     * taxonomy information
     */
    public TaxonomySearchDialog (SynonymsClient synonyms_client){
        super();
        setTitle("NCBI Taxonomy");
        setModal(true);
        this.synonymsClient = synonyms_client;
        create();
    }
    
    /**
     * 
     * @return the IDs of the selected entries on the right side of the dialog
     */
    public List getGeneIDs (){
        if(genesList.getModel().getSize() == 0) return new ArrayList();
        
        ArrayList idList = new ArrayList();
        for(int i = 0; i < genesList.getModel().getSize(); i++){
            if(genesList.isSelectedIndex(i)){
                Map.Entry entry = (Map.Entry)genesList.getModel().getElementAt(i);
                idList.add(entry.getKey());
            }
        }
        return idList;
    }
    
    /**
     * Gets the taxonomy panel that this dialog uses
     * @return a TaxonomyPanel that other GUIs can use
     */
    public TaxonomyPanel getTaxonomyPanel (){
        if(taxonomyPanel == null) this.taxonomyPanel = new TaxonomyPanel(this.synonymsClient);
        return this.taxonomyPanel;
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
        this.taxonomyPanel = new TaxonomyPanel(this.synonymsClient);
        leftPanel.add(this.taxonomyPanel, BorderLayout.CENTER);
        
        centerPanel.add(leftPanel);
        
        // RIGHT SIDE, GENE PATTERN MATCHING
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        
        JPanel gLabelPanel = new JPanel();
        JLabel geneLabel = new JLabel("Genes search string:");
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
        
        final String help = "<html>Enter partial or complete names of genes that you wish<br>"+
                                   "to find. You can enter a comma separated list.<br>"+
                                   "Example: NUC,CDC,PEX,MPT5</html>";
        helpButton.addActionListener(
                new AbstractAction (){
                    public void actionPerformed(ActionEvent e){
                        JOptionPane.showMessageDialog(TaxonomySearchDialog.this,help,"Help", JOptionPane.INFORMATION_MESSAGE);                        
                    }//actionPerformed
                }//AbstractAction
        );
        
        
        
    }
    
    protected void searchForGenes (){
        String genesString = this.geneInput.getText();
        if(genesString == null ||  genesString.length() == 0) return;
        
        genesString.trim();
        
        Vector taxids = this.taxonomyPanel.getSelectedSpeciesTaxids();
        
        if(taxids.size() == 0) return;
       
        Hashtable matches = new Hashtable();
        Iterator it = taxids.iterator();
        while(it.hasNext()){
            String taxid = (String)it.next();
            try{
                matches.putAll(this.synonymsClient.getGenesLike(taxid,genesString));
            }catch(Exception e){ e.printStackTrace(); continue;}
        }//for i
       
        Vector entries = new Vector();
        entries.addAll(matches.entrySet());
        this.genesList.removeAll();
        this.genesList.setListData(entries);
        
    }
    
}