
package org.isb.bionet.gui.wizard;

import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import org.isb.bionet.datasource.interactions.*;
import cytoscape.*;
/**
 * 
 * @author iavila
 * TODO: Add actions to buttons
 */
public class NetworkBuilderWizard {
    
    protected InteractionDataClient interactionsClient;
    protected List dialogs;
    protected SpeciesPanel speciesPanel;
    protected EdgeSourcesPanel edgeSourcesPanel;
    protected NetworkSettingsPanel networkPanel;
    protected int currentStep;
    protected boolean onLastStep = false;
    
    protected AbstractAction DEFAULT_BACK_ACTION = new AbstractAction(){
            
            public void actionPerformed (ActionEvent e){
                displayStep(currentStep-1);
            }
    };
    
    protected AbstractAction DEFAULT_NEXT_ACTION = new AbstractAction(){
                
                public void actionPerformed (ActionEvent e){
                    displayStep(currentStep+1);
                }
        
    };
    
    /**
     * 
     * @param interactions_client
     */
    public NetworkBuilderWizard (InteractionDataClient interactions_client){
        this.interactionsClient = interactions_client;
        createDialogs();
    }//constructor
    
    /**
     * Starts the wizard.
     */
    public void startWizard (){
        displayStep(0);
    }//startWizard
    
    /**
     * Displays the dialog at position step in this.dialogs
     * @param step
     */
    protected void displayStep (int step){      
        JDialog prevDialog = (JDialog)this.dialogs.get(this.currentStep);
        this.currentStep = step;
        JDialog dialog = (JDialog)this.dialogs.get(this.currentStep);
        dialog.setLocationRelativeTo(prevDialog);
        if(prevDialog.isVisible()){
            prevDialog.setVisible(false);
        }
        dialog.setVisible(true);
    }//dsiplayStep
    
    /**
     * Creates all the dialogs
     */
    public void createDialogs (){
        this.dialogs = new ArrayList();
        
        this.currentStep = -1;
        
        // Create the dialog for selecting species
        this.currentStep++;
        JDialog speciesDialog = createSpeciesDialog();
        this.dialogs.add(this.currentStep, speciesDialog);
        
        // Create the dialog to select nodes
        this.currentStep++;
        JDialog nodesDialog = createNodeSourcesDialog();
        this.dialogs.add(this.currentStep, nodesDialog);
        
        // Create the dialog to select edges
        this.currentStep++;
        JDialog edgesDialog = createEdgeSourcesDialog();
        this.dialogs.add(this.currentStep, edgesDialog);
        
        // Create the dialog for network settings
        this.currentStep++;
        this.onLastStep = true;
        JDialog netDialog = createNetworkSettingsDialog();
        this.dialogs.add(this.currentStep,netDialog);
    }//start
    
    
    /**
     * Creates a dialog with a BorderLayout, the SOUTH portion of the dialog contains wizard buttons (back, next, cancel)
     * @return a JDialog
     */
    protected JDialog createWizardDialog (AbstractAction backAction, AbstractAction nextAction){
        
        JDialog dialog = new JDialog(Cytoscape.getDesktop());
        dialog.setTitle("BioNetwork Builder");
        dialog.setSize(400, 500);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        
        JPanel buttons = createWizardButtons(backAction, nextAction);
        
        panel.add(buttons, BorderLayout.SOUTH);
        
        dialog.setContentPane(panel);
        
        return dialog;
        
    }//createWizardDialog
    
    /**
     * Creates Back, Next, and Cancel buttons
     * @return
     */
    protected JPanel createWizardButtons (AbstractAction backAction, AbstractAction nextAction){
        
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        JButton back = new JButton("< Back");
        if(backAction != null){
            back.addActionListener(backAction);
        }else{
            back.setEnabled(false);
        }
 
        JButton next = new JButton("Next >");
        next.addActionListener(nextAction);
        
        if(this.onLastStep){
            next.setText("Finish");
        }
        
        
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new AbstractAction(){
            public void actionPerformed (ActionEvent event){
                JDialog dialog = (JDialog)dialogs.get(currentStep);
                dialog.dispose();
            }
        });
        
        panel.add(back);
        panel.add(next);
        panel.add(cancel);
        
        return panel;
    }//createWizardButtons
    
    
    protected JPanel createExplanationPanel (String explanation){
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        panel.setBackground(Color.WHITE);
        JLabel label = new JLabel(explanation);
        panel.add(label);
        return panel;
    }
    
    
    protected JDialog createSpeciesDialog (){
        
        AbstractAction back, next;
        if(this.currentStep == 0){
            back = null;
        }else{
            back = DEFAULT_BACK_ACTION;
        }
        
        next = new AbstractAction (){
            public void actionPerformed (ActionEvent event){
                Map table = (Map)speciesPanel.getSourcesSelectedSpecies();
                if(table.size() == 0){
                    JOptionPane.showMessageDialog(speciesPanel,"Please select a species", "Error", JOptionPane.ERROR_MESSAGE);
                }else{
//                  enable edge sources in the edges dialog
                    if(edgeSourcesPanel == null)
                        return;
                    Map sourceToName = (Map)speciesPanel.getSourcesNames();
                    Iterator it = table.keySet().iterator();
                    while(it.hasNext()){
                        String name = (String)sourceToName.get(it.next());
                        System.out.println("------------- " + name);
                        edgeSourcesPanel.setSourceButtonEnabled(name, true);
                    }//while it
                    DEFAULT_NEXT_ACTION.actionPerformed(event);
                }//else
            }//actionPerformed
        };
        
        
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel("<html><br>Select a species for your biological network from your<br>desired data sources.<br></html>");
        dialog.getContentPane().add(explanation, BorderLayout.NORTH);
        
        try{
            Hashtable sourceToSp = this.interactionsClient.getSupportedSpeciesForEachSource();
            Hashtable  sourceToName = this.interactionsClient.getSourcesNames();
            this.speciesPanel = new SpeciesPanel(sourceToSp, sourceToName);
        }catch (Exception e){
            e.printStackTrace();
            Hashtable emptyTable = new Hashtable();
            this.speciesPanel = new SpeciesPanel(emptyTable, emptyTable);
        }
       
        dialog.getContentPane().add(this.speciesPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }//createSpeciesDialog
    
    
    protected JDialog createNodeSourcesDialog (){
        
        AbstractAction back, next;
        back = DEFAULT_BACK_ACTION;
        next = DEFAULT_NEXT_ACTION;
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel("<html><br>Select the sources for the nodes in your biological network.<br>"+
                    "Advanced settings for some sources are available if you"+
                    "<br>press the source's corresponding button.<br><br>"+
                    "If you don't select any node sources, then nodes will be"+
                    "<br>created automatically when edges are created (next step).<br>" +
                    "</html>"); 
        
        dialog.getContentPane().add(explanation,BorderLayout.NORTH);
        
        JPanel nodeSourcesPanel = new NodeSourcesPanel();
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(nodeSourcesPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }
    
    protected JDialog createEdgeSourcesDialog (){
        
        AbstractAction back, next;
        back = DEFAULT_BACK_ACTION;
        next = DEFAULT_NEXT_ACTION;
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel(
                "<html><br>The edge sources that you selected when specifying species<br>"+
                "are available here.<br>"+
                "You can set their parameters by pressing on their corresponding<br>buttons.<br></htlm>"
        ); 
        
        dialog.getContentPane().add(explanation,BorderLayout.NORTH);
        
        Map sourcesToNames;
        Map sourcesToSpecies;
        if(this.speciesPanel != null){
            sourcesToSpecies = this.speciesPanel.getSourcesSelectedSpecies();
            sourcesToNames = this.speciesPanel.getSourcesNames();
        }else{
            try{
                sourcesToSpecies = new Hashtable();
                sourcesToNames = this.interactionsClient.getSourcesNames();
            }catch(Exception ex){
                ex.printStackTrace();
                sourcesToNames = new Hashtable();
                sourcesToSpecies = sourcesToNames;
            }
        }
        this.edgeSourcesPanel = new EdgeSourcesPanel(sourcesToNames, sourcesToSpecies);
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(this.edgeSourcesPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
        
    }
    
    protected JDialog createNetworkSettingsDialog (){
        AbstractAction back, next;
        back = DEFAULT_BACK_ACTION;
        next = DEFAULT_NEXT_ACTION; // for now
        
        JDialog dialog = createWizardDialog(back, next);
        
        JPanel explanation = createExplanationPanel(
                "<html><br>Set parameters for your new biological network.<br></html>"
        );
        
        dialog.getContentPane().add(explanation, BorderLayout.NORTH);
        
        this.networkPanel = new NetworkSettingsPanel();
        
        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bigPanel.add(this.networkPanel);
       
        dialog.getContentPane().add(bigPanel, BorderLayout.CENTER);
        
        return dialog;
    }
    
}//NetworkBuilderWizard