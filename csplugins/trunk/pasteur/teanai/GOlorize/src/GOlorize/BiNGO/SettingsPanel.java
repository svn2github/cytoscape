package GOlorize.BiNGO;

/* * Copyright (c) 2005 Flanders Interuniversitary Institute for Biotechnology (VIB)
 * *
 * * Authors : Steven Maere, Karel Heymans
 * *
 * * This program is free software; you can redistribute it and/or modify
 * * it under the terms of the GNU General Public License as published by
 * * the Free Software Foundation; either version 2 of the License, or
 * * (at your option) any later version.
 * *
 * * This program is distributed in the hope that it will be useful,
 * * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * * The software and documentation provided hereunder is on an "as is" basis,
 * * and the Flanders Interuniversitary Institute for Biotechnology
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Flanders Interuniversitary Institute for Biotechnology
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * the Flanders Interuniversitary Institute for Biotechnology
 * * has been advised of the possibility of such damage. See the
 * * GNU General Public License for more details.
 * *
 * * You should have received a copy of the GNU General Public License
 * * along with this program; if not, write to the Free Software
 * * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * *
 * * Authors: Steven Maere, Karel Heymans
 * * Date: Mar.25.2005
 * * Description: Class that extends JPanel and takes care of the GUI-objects
 * * needed for the settings panel.
 **/

/** Modified by Olivier Garcia (23/10/2006) :
 *  Changes :   Constructor that allows interactions with GOlorize (parameter GoBin).
 *              Detection of loaded BiodataServer (usage of ZChooseBioDataServerAnnotation), and 
 *              providing of these datas.
 *              
 *
 *
 *
 *
 */

import GOlorize.GoBin;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.*;
import cytoscape.Cytoscape;

/******************************************************************
 * SettingsPanel.java:       Steven Maere & Karel Heymans (c) March 2005
 * -------------------
 *
 * Class that extends JPanel and takes care of the GUI-objects
 * needed for the settings panel.
 ******************************************************************/



/**
 * Class that extends JPanel and takes care of the GUI-objects
 * needed for a userfriendly settings panel with the options of using graph or text input,
 * choosing a test set name, a test, a correction, an annotation file,
 * an ontology file, a significance level, the GO categories shown in the
 * graph, reference set options and the option
 * of saving a data-file.
 */
public class SettingsPanel extends JPanel{
    
    
    /*--------------------------------------------------------------
      FIELDS.
      --------------------------------------------------------------*/
    
    /** the height of the panel*/
    private final int DIM_HEIGHT = 600 ;
    /** the width of the panel*/
    private final int DIM_WIDTH = 550 ;
    /** the BiNGO directory path*/
    private String bingoDir ;
    /** array of strings with the options for the testBox.*/
    private final String [] testsArray = {"---",
    "Hypergeometric test",
    "Binomial test"};
    /** array of strings with the options for the correctionBox.*/
    private final String [] correctionArray = {"---",
    "Benjamini & Hochberg False Discovery Rate (FDR) correction",
    "Bonferroni Family-Wise Error Rate (FWER) correction"};
    /** array of strings with the options categories to be shown in graph.*/
    private final String [] categoriesArray = {"---",
    "All categories",
    "Overrepresented categories before correction",
    "Overrepresented categories after correction"};
    /** array of strings with the options against what cluster should be tested.*/
    private final String [] clusterVsArray = {"---",
    "Test cluster versus complete annotation",
    "Test cluster versus network"};
    
    /** Help button ; opens BiNGO website*/
    private JButton helpButton ;
    /** Panel for text or graph input*/
    private TextOrGraphPanel textOrGraphPanel ;
    /** Panel for overrepresentation/underrepresentation .*/
    private OverUnderPanel overUnderPanel ;
    /** JComboBox with the possible tests.*/
    private VizPanel vizPanel ;
    /** JComboBox with the possible tests.*/
    private JComboBox testBox;
    /** JComboBox with the possible corrections.*/
    private JComboBox correctionBox;
    /** JComboBox with the possible number of categories in the graph.*/
    private JComboBox categoriesBox;
    /** JComboBox with the options against what cluster should be tested.*/
    private JComboBox clusterVsBox;
    /** JTextField for input of the desired significance level.*/
    private JTextField alphaField;
    /** JLabel nameLabel*/
    private JLabel nameLabel;
    /** JLabel overUnderLabel*/
    private JLabel overUnderLabel;
    /** JLabel testLabel*/
    private JLabel testLabel;
    /** JLabel correctionLabel*/
    private JLabel correctionLabel;
    /** JLabel alphaLabel*/
    private JLabel alphaLabel;
    /** JLabel ontologyLabel*/
    private JLabel ontologyLabel;
    /** JLabel annotationLabel*/
    private JLabel annotationLabel;
    /** JLabel categoriesLabel*/
    private JLabel categoriesLabel;
    /** JLabel clusterVsLabel*/
    private JLabel clusterVsLabel;
    /** JButton the bingo button*/
    private JButton bingoButton;
    /** SettingsOpenPanel for choosing the annotation file*/
    private ChooseAnnotationPanel annotationPanel;
    /** TypeOfIdentifierPanel for choosing the type of geneID's used*/
    private TypeOfIdentifierPanel typeOfIdentifierPanel;
    /** ChooseOntologyPanel for choosing the ontology file*/
    private ChooseOntologyPanel ontologyPanel;
    /** text field for naming test cluster*/
    private JTextField nameField;
    /** SettingsSavePanel for option of saving data-file*/
    private SettingsSavePanel dataPanel;
    
    
    
    
    
    private ZChooseBioDataServerAnnotation bdsAnnot;
    private GoBin goBin=null;
    /*-----------------------------------------------------------------
      CONSTRUCTOR.
      -----------------------------------------------------------------*/
    
    /**
     * This constructor creates the panel with its swing-components.
     */
    public SettingsPanel(String bingoDir) {
        super();
        this.bingoDir = bingoDir ;
        
        //create the JComponents.
        makeJComponents();
        
        setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        setOpaque(false);
        //create border.
        setBorder(BorderFactory.createTitledBorder
                (BorderFactory.createLineBorder(Color.black),
                "BiNGO settings",
                0,
                0,
                new Font("BiNGO settings",Font.BOLD,16),
                Color.black));
        
        // Layout with GridBagLayout.
        GridBagLayout gridbag = new GridBagLayout() ;
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
        
        c.weighty = 1 ;
        c.weightx = 1 ;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel dummyPanel = new JPanel() ;
        dummyPanel.setOpaque(false);
        dummyPanel.setPreferredSize(new Dimension(50,20)) ;
        
        JButton infoButton = new JButton("Info") ;
        c.gridx = 0 ;
        gridbag.setConstraints(dummyPanel, c);
        add(dummyPanel) ;
        
        c.gridx = 1 ;
        c.fill = GridBagConstraints.NONE ;
        c.anchor = GridBagConstraints.EAST ;
        gridbag.setConstraints(helpButton, c);
        add(helpButton) ;
        
        JPanel dummyPanel2 = new JPanel() ;
        dummyPanel2.setOpaque(false);
        dummyPanel2.setPreferredSize(new Dimension(50,20)) ;
        c.gridx = 2 ;
        c.fill = GridBagConstraints.HORIZONTAL ;
        c.anchor = GridBagConstraints.CENTER ;
        c.gridwidth = GridBagConstraints.REMAINDER ;
        gridbag.setConstraints(dummyPanel2, c);
        add(dummyPanel2) ;
        
        c.weighty = 1 ;
        c.weightx = 100 ;
        c.gridx = 1 ;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        gridbag.setConstraints(nameLabel, c);
        add(nameLabel) ;
        
        gridbag.setConstraints(nameField, c);
        add(nameField) ;
        
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1 ;
        c.weighty = 10 ;
        gridbag.setConstraints(textOrGraphPanel, c);
        add(textOrGraphPanel) ;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.weighty = 1 ;
        
        gridbag.setConstraints(overUnderLabel, c);
        add(overUnderLabel) ;
        
        gridbag.setConstraints(overUnderPanel, c);
        add(overUnderPanel) ;
        
        gridbag.setConstraints(vizPanel, c);
        add(vizPanel) ;
        
        gridbag.setConstraints(testLabel, c);
        add(testLabel);
        
        gridbag.setConstraints(testBox, c);
        add(testBox);
        
        gridbag.setConstraints(correctionLabel, c);
        add(correctionLabel);
        
        gridbag.setConstraints(correctionBox, c);
        add(correctionBox);
        
        gridbag.setConstraints(alphaLabel, c);
        add(alphaLabel);
        
        gridbag.setConstraints(alphaField, c);
        add(alphaField);
        
        gridbag.setConstraints(categoriesLabel, c);
        add(categoriesLabel);
        
        gridbag.setConstraints(categoriesBox, c);
        add(categoriesBox);
        
        gridbag.setConstraints(clusterVsLabel, c);
        add(clusterVsLabel);
        
        gridbag.setConstraints(clusterVsBox, c);
        add(clusterVsBox);
        
        
        
        
        if (bdsAnnot.isThereAnnotInMemorie()){
            JLabel jl = new JLabel("Use Annotation/Ontology of BioDataServer in Memory :");
            gridbag.setConstraints(jl, c);
            add(jl);
            
            gridbag.setConstraints(bdsAnnot, c);
            add(bdsAnnot);
        }
        
        
        
        
        
        gridbag.setConstraints(ontologyLabel, c);
        add(ontologyLabel);
        
        gridbag.setConstraints(ontologyPanel, c);
        add(ontologyPanel);
        
        gridbag.setConstraints(annotationLabel, c);
        add(annotationLabel);
        
        gridbag.setConstraints(annotationPanel, c);
        add(annotationPanel);
        
        gridbag.setConstraints(dataPanel, c);
        add(dataPanel);
        
        gridbag.setConstraints(bingoButton, c);
        add(bingoButton);
        
        
        validate() ;
    }
    
    public SettingsPanel(String bingoDir,GOlorize.GoBin goB) {
        
        super();
        
        goBin = goB;
        goBin.setBingoLaunched(true);
        this.bingoDir = bingoDir ;
        
        //create the JComponents.
        makeJComponents();
        
        setPreferredSize(new Dimension(DIM_WIDTH, DIM_HEIGHT));
        setOpaque(false);
        //create border.
        setBorder(BorderFactory.createTitledBorder
                (BorderFactory.createLineBorder(Color.black),
                "BiNGO settings",
                0,
                0,
                new Font("BiNGO settings",Font.BOLD,16),
                Color.black));
        
        // Layout with GridBagLayout.
        GridBagLayout gridbag = new GridBagLayout() ;
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
        
        c.weighty = 1 ;
        c.weightx = 1 ;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        JPanel dummyPanel = new JPanel() ;
        dummyPanel.setOpaque(false);
        dummyPanel.setPreferredSize(new Dimension(50,20)) ;
        
        JButton infoButton = new JButton("Info") ;
        c.gridx = 0 ;
        gridbag.setConstraints(dummyPanel, c);
        add(dummyPanel) ;
        
        c.gridx = 1 ;
        c.fill = GridBagConstraints.NONE ;
        c.anchor = GridBagConstraints.EAST ;
        gridbag.setConstraints(helpButton, c);
        add(helpButton) ;
        
        JPanel dummyPanel2 = new JPanel() ;
        dummyPanel2.setOpaque(false);
        dummyPanel2.setPreferredSize(new Dimension(50,20)) ;
        c.gridx = 2 ;
        c.fill = GridBagConstraints.HORIZONTAL ;
        c.anchor = GridBagConstraints.CENTER ;
        c.gridwidth = GridBagConstraints.REMAINDER ;
        gridbag.setConstraints(dummyPanel2, c);
        add(dummyPanel2) ;
        
        c.weighty = 1 ;
        c.weightx = 100 ;
        c.gridx = 1 ;
        c.gridwidth = GridBagConstraints.RELATIVE;
        c.fill = GridBagConstraints.HORIZONTAL;
        
        gridbag.setConstraints(nameLabel, c);
        add(nameLabel) ;
        
        gridbag.setConstraints(nameField, c);
        add(nameField) ;
        
        c.fill = GridBagConstraints.BOTH;
        c.gridheight = 1 ;
        c.weighty = 10 ;
        gridbag.setConstraints(textOrGraphPanel, c);
        add(textOrGraphPanel) ;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridheight = 1;
        c.weighty = 1 ;
        
        gridbag.setConstraints(overUnderLabel, c);
        add(overUnderLabel) ;
        
        gridbag.setConstraints(overUnderPanel, c);
        add(overUnderPanel) ;
        
        gridbag.setConstraints(vizPanel, c);
        add(vizPanel) ;
        
        gridbag.setConstraints(testLabel, c);
        add(testLabel);
        
        gridbag.setConstraints(testBox, c);
        add(testBox);
        
        gridbag.setConstraints(correctionLabel, c);
        add(correctionLabel);
        
        gridbag.setConstraints(correctionBox, c);
        add(correctionBox);
        
        gridbag.setConstraints(alphaLabel, c);
        add(alphaLabel);
        
        gridbag.setConstraints(alphaField, c);
        add(alphaField);
        
        gridbag.setConstraints(categoriesLabel, c);
        add(categoriesLabel);
        
        gridbag.setConstraints(categoriesBox, c);
        add(categoriesBox);
        
        gridbag.setConstraints(clusterVsLabel, c);
        add(clusterVsLabel);
        
        gridbag.setConstraints(clusterVsBox, c);
        add(clusterVsBox);
        
        
        
        
        if (bdsAnnot.isThereAnnotInMemorie()){
            JLabel jl = new JLabel("Use Annotation/Ontology of BioDataServer in Memory :");
            gridbag.setConstraints(jl, c);
            add(jl);
            
            gridbag.setConstraints(bdsAnnot, c);
            add(bdsAnnot);
        }
        
        
        
        
        
        gridbag.setConstraints(ontologyLabel, c);
        add(ontologyLabel);
        
        gridbag.setConstraints(ontologyPanel, c);
        add(ontologyPanel);
        
        gridbag.setConstraints(annotationLabel, c);
        add(annotationLabel);
        
        gridbag.setConstraints(annotationPanel, c);
        add(annotationPanel);
        
        gridbag.setConstraints(dataPanel, c);
        add(dataPanel);
        
        gridbag.setConstraints(bingoButton, c);
        add(bingoButton);
        
        
        validate() ;
    }
    
    
    
    /*----------------------------------------------------------------
      PAINTCOMPONENT.
      ----------------------------------------------------------------*/
    
    /**
     * Paintcomponent method that paints the panel.
     *
     * @param g Graphics-object.
     */
    public void paintComponent(Graphics g){
        super.paintComponent(g);
    }
    
    
    
    
    
    /*----------------------------------------------------------------
      METHODS.
      ----------------------------------------------------------------*/
    /**
     * Method that makes the necessary JComponents for the SettingsPanel.
     * The used JComponents are: a textfield for the cluster name,
     * a TextOrGraphPanel for choosing between text and graph input,
     * two comboboxes for the distributions and
     * the corrections, a textfield for the alpha, two comboboxes for the
     * option against what cluster should be tested and one for the option
     * how many categories must be displayed int the graph, two SettingsOpenPanels
     * for choosing the annotation and ontology file, a SettingsSavePanel
     * for the data-file and a button to start
     * the calculations.
     */
    public void makeJComponents(){
        
        helpButton = new JButton("Help");
        helpButton.setMnemonic(KeyEvent.VK_H);
        helpButton.addActionListener(new HelpButtonActionListener(this));
        
        // JComboboxes.
        testBox = new JComboBox(testsArray);
        testBox.setSelectedItem("Hypergeometric test") ;
        correctionBox = new JComboBox(correctionArray);
        correctionBox.setSelectedItem("Benjamini & Hochberg False Discovery Rate (FDR) correction") ;
        categoriesBox = new JComboBox(categoriesArray);
        categoriesBox.setSelectedItem("Overrepresented categories after correction") ;
        clusterVsBox = new JComboBox(clusterVsArray);
        clusterVsBox.setSelectedItem("Test cluster versus complete annotation") ;
        
        //JTextField.
        alphaField = new JTextField("0.2");
        nameField = new JTextField();
        
        //OverUnderPanel
        overUnderPanel = new OverUnderPanel() ;
        
        //OverUnderPanel
        vizPanel = new VizPanel() ;
        
        //TextOrGraphPanel
        textOrGraphPanel = new TextOrGraphPanel() ;
        
        
        // JLabels.
        overUnderLabel = new JLabel("Do you want to assess over- or underrepresentation :");
        nameLabel = new JLabel("Cluster name :");
        testLabel = new JLabel("Select a statistical test :");
        correctionLabel = new JLabel("Select a multiple testing correction :");
        alphaLabel = new JLabel("Choose a significance level :");
        categoriesLabel = new JLabel("Select the categories to be visualized :");
        clusterVsLabel = new JLabel("Select reference set :");
        testLabel.setForeground(Color.black);
        correctionLabel.setForeground(Color.black);
        alphaLabel.setForeground(Color.black);
        categoriesLabel.setForeground(Color.black);
        clusterVsLabel.setForeground(Color.black);
        
        // annotationPanel.
        annotationLabel = new JLabel("Select organism/annotation and gene identifier:");
        annotationPanel = new ChooseAnnotationPanel(this,bingoDir);
        
        //TypeOfIdentifierPanel
        typeOfIdentifierPanel = annotationPanel.getTypeOfIdentifierPanel() ;
        
        // ontologyPanel.
        ontologyLabel = new JLabel("Select ontology :");
        ontologyPanel = new ChooseOntologyPanel(this,bingoDir);
        
        bdsAnnot = new ZChooseBioDataServerAnnotation();
        
        // Creating SettingsSavePanels.
        dataPanel = new SettingsSavePanel("Data",this,bingoDir);
        
        // the BiNGO-button to start the calculations.
        bingoButton = new JButton("Start BiNGO");
        bingoButton.setMnemonic(KeyEvent.VK_B);
        bingoButton.addActionListener(new SettingsPanelActionListener(this,goBin));
        
    }
    
    
    
    
    
    /*----------------------------------------------------------------
      GETTERS.
      ----------------------------------------------------------------*/
    
    TextOrGraphPanel getTextOrGraphPanel(){return textOrGraphPanel;}
    OverUnderPanel getOverUnderPanel(){return overUnderPanel;}
    VizPanel getVizPanel(){return vizPanel;}
    /**
     * Get the testBox.
     *
     * @return JCombobox testBox.
     */
    
    JComboBox getTestBox(){ return testBox;}
    
    /**
     * Get the correctionBox.
     *
     * @return JCombobox correctionBox.
     */
    JComboBox getCorrectionBox(){ return correctionBox;}
    
    /**
     * Get the categoriesBox.
     *
     * @return JCombobox categoriesBox.
     */
    JComboBox getCategoriesBox(){ return categoriesBox;}
    
    /**
     * Get the clusterVsBox.
     *
     * @return JCombobox clusterVsBox.
     */
    JComboBox getClusterVsBox(){ return clusterVsBox;}
    
    /**
     * Get the alphaField.
     *
     * @return JTextField alphaField.
     */
    JTextField getAlphaField(){ return alphaField;}
    
    /**
     * Get the nameField.
     *
     * @return JTextField alphaField.
     */
    
    JTextField getNameField(){ return nameField;}
    /**
     * Get the typeOfIdentifierPanel.
     *
     * @return TypeOfIdentifierPanel typeOfIdentifierPanel.
     */
    TypeOfIdentifierPanel getTypeOfIdentifierPanel(){return typeOfIdentifierPanel ;}
    /**
     * Get the annotationPanel.
     *
     * @return SettingsOpenPanel annotationPanel.
     */
    ChooseAnnotationPanel getAnnotationPanel(){ return annotationPanel;}
    
    /**
     * Get the ontologyPanel.
     *
     * @return ChooseOntologyPanel ontologyPanel.
     */
    ChooseOntologyPanel getOntologyPanel(){ return ontologyPanel;}
    
    /**
     * Get the dataPanel.
     *
     * @return SettingsSavePanel dataPanel.
     */
    SettingsSavePanel getDataPanel(){ return dataPanel;}
    
    /** get the BiNGO directory path*/
    String getBingoDir(){return bingoDir ;}
    
    ZChooseBioDataServerAnnotation getBDSPanel(){return this.bdsAnnot;}
    
}

