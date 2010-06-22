/*
 * GoBin.java
 *
 * Created on July 28, 2006, 2:32 PM
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

package BiNGO.GOlorize;

import BiNGO.SettingsPanel;
import giny.model.GraphPerspectiveChangeEvent;
import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;

import cytoscape.data.annotation.Ontology;
import cytoscape.data.annotation.Annotation;
import cytoscape.Cytoscape ;
import cytoscape.view.CytoscapeDesktop ;
import javax.swing.* ;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.*;
import cytoscape.view.CyNetworkView;
import cytoscape.CyNetwork;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
/**
 *
 * @author ogarcia
 */
public class GoBin extends javax.swing.JFrame{
    

        private final JTabbedPane jTabbedPane;
        private JComboBox genesLinked;
        private JComboBox genesLinkedView;
        private boolean bingoLaunched=false;
        private int resultPanelCount =0;
        //boolean layoutPanel = false;
        

        
        
        private HashMap network_Options=new HashMap();
        
        private HashMap goTerm_Annotation=new HashMap();

        private HashMap goColor=new HashMap();
        /*private Color[] automaticColorArray={Color.BLUE,Color.RED,Color.GREEN,Color.YELLOW,Color.WHITE,Color.GRAY,
                                Color.MAGENTA,Color.ORANGE,Color.DARK_GRAY,Color.CYAN,Color.PINK,Color.LIGHT_GRAY};*/
        private int[][] automaticColorArray={{0,0,255},{255,102,102},{153,255,153},{255,255,0},{204,0,204},{153,153,153},
        {255,153,51},{0,255,204},{102,102,0},{0,153,0},{51,51,51},{153,153,255},{255,0,0},{102,0,153},
        {255,204,255},{255,255,153},{0,102,102},{255,255,255},{255,102,255},{0,0,0}};
        private boolean []automaticColorUsed= new boolean[getAutomaticColorArray().length];
        private HashMap termAutomaticlyColored=new HashMap();
        
        private Color layoutColor = new java.awt.Color(51,255,51);
        private Color noLayoutColor = Color.WHITE;
        
        //private JFrame bingoWindow = new JFrame("BiNGO Settings");
        private SettingsPanel settingsPanel;
        private CyNetworkView networkView ;
    
    public GoBin(BiNGO.SettingsPanel settingsPanel, CyNetworkView networkView){
        
        this.setTitle("BiNGO output");
        this.settingsPanel = settingsPanel;
        this.networkView = networkView;
        this.jTabbedPane=new javax.swing.JTabbedPane();
        initComponents();
       // startBiNGO();  
        
       /* 
        for (int i=0;i<getAutomaticColorUsed().length;i++)
            getAutomaticColorUsed()[i]=false;
        
        GoBin cl2 = new GoBin(this);
        //Cytoscape.getDesktop().addPropertyChangeListener(cl2);
        
        //cytoscape.Cytoscape.getSwingPropertyChangeSupport().
        //        addPropertyChangeListener(cl2);
        Cytoscape.getDesktop().getSwingPropertyChangeSupport().
        addPropertyChangeListener(cl2);
        
        
        
        addLayoutPanel();*/
    }
    
    
    
    private void initComponents() { 
                
        //JPanel northPanel = new JPanel();      
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize(); 

        getJTabbedPane().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        //jTabbedPane.addTab("Start",startPanel);


        //pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        /*JButton launchBiNGOButton = new JButton("Start BiNGO");
        northPanel.add(launchBiNGOButton);
        launchBiNGOButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ev){
                            startBiNGO();
                        }
        });*/
        
        /*genesLinked = new JComboBox();
        getGenesLinked().addItem("All nodes in view");
        getGenesLinked().addItem("Selected nodes only");
        getGenesLinked().setSelectedItem("All current view nodes");
        northPanel.add(new JLabel("Apply coloring"));
        northPanel.add(getGenesLinked());
        
        genesLinkedView = new JComboBox(); 
        getGenesLinkedView().addItem("No coloring");
        getGenesLinkedView().addItem("Small pie size");
        getGenesLinkedView().addItem("Default pie size");
        getGenesLinkedView().addItem("Large pie size");
        getGenesLinkedView().setSelectedItem("Default pie size");
        northPanel.add(new JLabel("Coloring effect"));
        northPanel.add(getGenesLinkedView());
        getGenesLinkedView().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                Component comp = getJTabbedPane().getSelectedComponent();
                if (comp instanceof ResultPanel)
                    ((ResultPanel)comp).getDisplayPieChart().doIt(((ResultPanel)comp).displayPieChartListener.APPLY_CHANGE,
                            true, networkView);
                else if (comp instanceof StartPanel){
                    Component comp2 = getStartPanel().jTabbedPane.getSelectedComponent();
                    if (comp2 instanceof StartPanelPanel)
                        ((StartPanelPanel)comp2).getDisplayPieChart().doIt(((StartPanelPanel)comp2).getDisplayPieChart().
                                APPLY_CHANGE,true,networkView);
                    else {
                        ResultAndStartPanel all = getStartPanel().tabAll; 
                        all.getDisplayPieChart().doIt(all.getDisplayPieChart().
                                    APPLY_CHANGE,true,networkView);
                    }
                }
                else {
                    ResultAndStartPanel all = getStartPanel().tabAll; 
                    all.getDisplayPieChart().doIt(all.getDisplayPieChart().
                                APPLY_CHANGE,true,networkView);
                }
                
            }
        });
        
        getContentPane().add(northPanel,java.awt.BorderLayout.NORTH);*/
        
        getContentPane().add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
        //jTabbedPane.setAlignmentY(jTabbedPane.CENTER_ALIGNMENT);
        //jTabbedPane.setAlignmentX(jTabbedPane.CENTER_ALIGNMENT);
        //jTabbedPane.addTab("Start",startPanel);
        //startPanel.validate();

        //getContentPane().add(new JLabel("ouahhhhh"),java.awt.BorderLayout.SOUTH);


        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //this.startPanel=new StartPanel(this);//startPanel.validate();this.validate();
        //pack();  
        //getJTabbedPane().addTab("Selected", getStartPanel());//pack();   
        
        //startPanel.validate();
        //this.validate();
        //startPanel.initComponents();
        //startPanel.tabAll.validate();
        //startPanel.tabBioProcess.validate();
        //startPanel.tabCellComponant.validate();
        //startPanel.tabMolFunction.validate();
        //startPanel.tabOther.validate();
        
        pack();   



        //this.setLocation(25,screenSize.height-450);
        //this.setSize(screenSize.width-50,400);
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e){
               /* if (getBingoWindow()!=null)
                    getBingoWindow().dispose();*/
               /* if (getResultPanelCount()!=0){
                    GoBin goBin=(GoBin)e.getComponent();
                    goBin.getResultPanelAt(1).displayPieChartListener.resetAll(goBin);

                }*/
            }
        });
        
        this.setLocation(25,screenSize.height-450);
        this.setSize(screenSize.width-50,400);

        setVisible(true);
        setResizable(true);
        this.validate();
        
        /*getStartPanel().jTabbedPane.setAlignmentY(getStartPanel().jTabbedPane.CENTER_ALIGNMENT);
        getStartPanel().jTabbedPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getStartPanel().validate();*/
        
        
        
       
        
        
        this.repaint();
//startPanel.validate();
        
                
                
      } 
    
    /*void startBiNGO(){
        
            setBingoLaunched(true);
            
            String tmp = System.getProperty("user.dir") ;
            String bingoDir = new File(tmp,"plugins").toString() ;
            SettingsPanel settingsPanel = new SettingsPanel(bingoDir,this);
            getBingoWindow().getContentPane().removeAll();
            getBingoWindow().getContentPane().add(settingsPanel);
            
           
            getBingoWindow().addWindowListener(new WindowAdapter(){
                public void windowClosed(WindowEvent e){
                    setBingoLaunched(false);
                }
            });
            
            
            
            getBingoWindow().pack();
            Dimension screenSize =
              Toolkit.getDefaultToolkit().getScreenSize();
            
            getBingoWindow().setLocation(screenSize.width/2 - (getBingoWindow().getWidth()/2),
                           screenSize.height/2 - (getBingoWindow().getHeight()/2));
            getBingoWindow().setVisible(true);
            getBingoWindow().setResizable(true);
            
        
        
    }*/
    
    public void createResultTab(HashMap testMap,
                                        HashMap correctionMap, 
                                        HashMap mapSmallX, 
                                        HashMap mapSmallN,
                                        HashMap mapBigX,
                                        HashMap mapBigN,
                                        String alphaString,
                                        Annotation annotation,
                                        HashMap alias,
                                        Ontology ontology,
                                        String annotationFile,
                                        String ontologyFile,
                                        String testString,
                                        String correctionString,
                                        String overUnderString,
                                        String dirName,
                                        String fileName,
                                        String clusterVsString,
                                        String catString,
                                        HashSet selectedCanonicalNameVector,
                                        CyNetwork currentNetwork,
                                        CyNetworkView currentNetworkview){
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();       
        ResultPanel result=new ResultPanel( testMap,correctionMap,mapSmallX, mapSmallN, mapBigX, mapBigN, alphaString,
                                         annotation, alias, ontology, annotationFile, ontologyFile, testString, correctionString,
                                         overUnderString, dirName,fileName, clusterVsString, catString,selectedCanonicalNameVector,
                                         currentNetwork, currentNetworkview, this);
        
        
        
        
        
        
        
        
        if (getResultPanelCount()!=0)
            result.setTabName(trouveBonNom(result.getTabName()));
         
        getJTabbedPane().addTab(result.getTabName(),result);

        getJTabbedPane().setSelectedIndex(getJTabbedPane().getTabCount()-1);
        
        
        
        result.validate();
        this.validate();
        
        
        resultPanelCount++;
        /*Iterator it = this.getStartPanelIterator();
        while (it.hasNext()){
            StartPanelPanel spp = (StartPanelPanel)it.next();
            spp.upDateAnnotationComboBox();
        }*/
        
    }
    
    
    
    
    private String trouveBonNom(String nom){
        String retour=nom;
        for (int i =1;i<getResultPanelCount()+1;i++){
            if (nom.equals(( (ResultPanel)getResultPanelAt(i) ).getTabName())){
                String aChanger = nom;
                
                if (aChanger.matches(".*[0123456789]$")){
                    String nombre2="";
                    String temp=aChanger;
                    while (temp.matches(".*[0123456789]$")){
                        nombre2 = temp.substring(temp.length()-1)+nombre2;
                        temp = temp.substring(0,temp.length()-1);
                    }
                    
                    int nombre=Integer.parseInt(nombre2)+1;
                    retour=temp+nombre;
                    retour=trouveBonNom(retour);
                }
                else {
                    retour=aChanger+"1";
                    retour=trouveBonNom(retour);
                }

            }
        }
        return retour;
    }
    
     
    
    
    
    void synchroColor(){
        JTable jtable;
        String goTerm;
        String goTerm2;
        Iterator it;
        ResultPanel result;
        for (int i =1;i<getResultPanelCount()+1;i++){
            result = (ResultPanel)getResultPanelAt(i);
            jtable=result.jTable1;
            
            
            
            
            
            for (int j=0;j<jtable.getRowCount();j++){
                goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
                it = getGoColor().keySet().iterator();
                while (it.hasNext()){
                    goTerm2 = (String)it.next();
                    if (goTerm.equals(goTerm2)){
                        
                        result.goColor.put(goTerm,(Color)this.getGoColor().get(goTerm2));
                        ((JLabel)jtable.getValueAt(j,ResultPanel.DESCRIPTION_COLUMN)).setBackground((Color)this.getGoColor().get(goTerm2));
                    }
                }
            }
            
        }
        
        
    }
    
    void synchroColor(ResultPanel result){
        JTable jtable;
        String goTerm;
        String goTerm2;
        Iterator it;
        
        jtable=result.jTable1;

        for (int j=0;j<jtable.getRowCount();j++){
            goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
            it = getGoColor().keySet().iterator();
            while (it.hasNext()){
                goTerm2 = (String)it.next();
                if (goTerm.equals(goTerm2)){

                    result.goColor.put(goTerm,(Color)this.getGoColor().get(goTerm2));
                    ((JLabel)jtable.getValueAt(j,ResultPanel.DESCRIPTION_COLUMN)).setBackground((Color)this.getGoColor().get(goTerm2));
                }
            }
        }
            
        
        
        
    }
    
    /*void synchroSelections(){
        ResultPanel result;
        JTable jtable;
        String goTerm;
        int temp;
        
        CyNetworkView currentNetworkView = Cytoscape.getCurrentNetworkView();
        Set goHashSet=new HashSet();//=((PieChartInfo)this.network_Options.get(currentNetworkView)).goToDisplay;
        PieChartInfo p = (PieChartInfo)this.getNetwork_Options().get(currentNetworkView);
        if (p!=null)
            goHashSet=p.goToDisplay;
        
        
        
        for (int i =1;i<getResultPanelCount()+1;i++){
            
            result = (ResultPanel)getResultPanelAt(i);
            jtable=result.jTable1;
            for (int j=0;j<jtable.getRowCount();j++){
                goTerm=(String)jtable.getValueAt(j,result.getGoTermColumn());
                if (goHashSet.contains(goTerm))
                    jtable.setValueAt(new Boolean(true),j,result.getSelectColumn());
                else
                    jtable.setValueAt(new Boolean(false),j,result.getSelectColumn());
               
            }
            
            //result.getGoBin().repaint();
        }
        
        Iterator spit = this.getStartPanelIterator();
        getStartPanel().tabAll.unselectAll();
        while (spit.hasNext()){
            ((StartPanelPanel)spit.next()).unselectAll();
        }
        
        Iterator it = goHashSet.iterator();
        
        
        while(it.hasNext()){

            goTerm=(String)it.next();
            spit = this.getStartPanelIterator();
            while (spit.hasNext()){
                StartPanelPanel tab=(StartPanelPanel)spit.next();
                temp = tab.getTermIndex(goTerm);

                if (temp==-1){
                    Annotation ann = (Annotation)(getGoTerm_Annotation().get(goTerm));
                    
                    if (ann!=null)
                        tab.addLine(goTerm,ann,ann.getOntology());

                }
                else {
                    tab.getJTable().setValueAt(new Boolean(true),temp,tab.getSelectColumn());
                    //startPanel.tabAll.getJTable().
                }
            }

        }
            
            
            
        repaint();
        
        
        
        //if (startPanel.tabAll.getTermIndex(""))
        
        
        
    }*/
    
    void synchroSelections(ResultPanel result){
        
        JTable jtable;
        String goTerm;
        
        CyNetworkView currentNetworkView = Cytoscape.getCurrentNetworkView();
        
        try {
            jtable=result.jTable1;
            for (int j=0;j<jtable.getRowCount();j++){
                
                goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
                jtable.setValueAt(new Boolean(false),j,result.SELECT_COLUMN);
            }    
        }
        catch (Exception e){
            //on s'en fout c'etait juste le premier tab a etre cree
        }
        
    }
        
    
    void removeTab(ResultPanel result){
        for (int i =1;i<getResultPanelCount()+1;i++){
            if ((ResultPanel)getResultPanelAt(i)==result){
                getJTabbedPane().removeTabAt(i-1);
                break;
            }
            
        }
        resultPanelCount--;
    }
    
    JTable getResultTableAt (int i){
        return ((ResultPanel) getJTabbedPane().getComponentAt(i-1)).jTable1;
    }
    
    ResultPanel getResultPanelAt (int i){
        return (ResultPanel)getJTabbedPane().getComponentAt(i-1);
    }
    
    ResultPanel getResultTabAt (int i){
        return (ResultPanel)getJTabbedPane().getComponentAt(i);
    }
    
    
    
    Color getNextAutomaticColor(String term){
        for (int i=0;i<this.getAutomaticColorUsed().length;i++){
            if (!getAutomaticColorUsed()[i]){
                this.getTermAutomaticlyColored().put(term,new Integer(i));
                getAutomaticColorUsed()[i]=true;
                return new Color(this.getAutomaticColorArray()[i][0],getAutomaticColorArray()[i][1],getAutomaticColorArray()[i][2]);
                
            }
        }
        return null;
    }
    
    void resetAutomaticColor(){
        for (int i=0;i<getAutomaticColorUsed().length;i++){
            getAutomaticColorUsed()[i]=false;
            
        }
        getTermAutomaticlyColored().clear();
    }
    
    boolean freeAutomaticColor(String term){
        if (getTermAutomaticlyColored().containsKey(term)){
            int numColor = ((Integer)getTermAutomaticlyColored().get(term)).intValue();
            getAutomaticColorUsed()[numColor]=false;
            return true;
        }
        return false;
    }
    
    boolean isAutomaticlyColored(String term){
        return (getTermAutomaticlyColored().containsKey(term));
    }    
    boolean freeAutomaticColor(){
        for (int i=0;i<this.getAutomaticColorUsed().length;i++){
            if (!getAutomaticColorUsed()[i]){
                return true;
            }
        }
        return false;
    }

    public HashMap getNetwork_Options() {
        return network_Options;
    }

    public void setNetwork_Options(HashMap network_Options) {
        this.network_Options = network_Options;
    }

    public JTabbedPane getJTabbedPane() {
        return jTabbedPane;
    }

    public JComboBox getGenesLinked() {
        return genesLinked;
    }

    public JComboBox getGenesLinkedView() {
        return genesLinkedView;
    }

    public int getResultPanelCount() {
        return resultPanelCount;
    }

    public HashMap getGoTerm_Annotation() {
        return goTerm_Annotation;
    }

    public HashMap getGoColor() {
        return goColor;
    }

    public void setGoColor(HashMap goColor) {
        this.goColor = goColor;
    }

    public int[][] getAutomaticColorArray() {
        return automaticColorArray;
    }

    public boolean[] getAutomaticColorUsed() {
        return automaticColorUsed;
    }

    public HashMap getTermAutomaticlyColored() {
        return termAutomaticlyColored;
    }

    public Color getLayoutColor() {
        return layoutColor;
    }

    public Color getNoLayoutColor() {
        return noLayoutColor;
    }

    public SettingsPanel getBingoWindow() {
        return settingsPanel;
    }

    public void setBingoLaunched(boolean bingoLaunched) {
        this.bingoLaunched = bingoLaunched;
    }
    
    
    
    
}



/*class GoBin implements PropertyChangeListener {
    GoBin goBin;
    public GoBin(GoBin goB){
        this.goBin=goB;
    }
   
  
   
   
    public void propertyChange(PropertyChangeEvent event) {
                //String net_id = (String)event.getNewValue();
                //CyNetwork network = Cytoscape.getNetwork(net_id);
        if (!event.getPropertyName().equals(Cytoscape.getDesktop().NETWORK_VIEW_DESTROYED)){
                if (goBin.getNetwork_Options().containsKey(Cytoscape.getCurrentNetworkView()))
                    goBin.synchroSelections();
                else
                    goBin.resetPieSelection();
                //System.out.println("truc");
        }
        else {
            CyNetworkView cv;
            
            Iterator it=goBin.getNetwork_Options().keySet().iterator();
            
            while (it.hasNext()){
                cv= (CyNetworkView)it.next();
                if (!Cytoscape.getNetworkViewMap().containsValue(cv)){
                    goBin.getNetwork_Options().remove(cv);
                    break;
                }
            }
            goBin.resetPieSelection();
            
            
        }
                
    }
    
}*/




















