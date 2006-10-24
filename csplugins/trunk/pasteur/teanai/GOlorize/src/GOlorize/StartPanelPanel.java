/*
 * StartPanelPanel.java
 *
 * Created on August 4, 2006, 7:16 PM
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
 * Authors: Olivier Garcia.
 */

package GOlorize;

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
import java.util.*;
/**
 *
 * @author ogarcia
 */
public class StartPanelPanel extends javax.swing.JPanel implements ResultAndStartPanel {
    static String ALLTYPE="All";
    static String BPTYPE="Biological Process";
    static String CCTYPE="Cellular Component";
    static String MFTYPE="Molecular Function";
    static String OTHERTYPE="Other";
            
    private int SELECT_COLUMN = 0;
    private int GO_TERM_COLUMN=1;
    private int LAYOUT_COLUMN=4;
    private int REMOVE_COLUMN=5;
    private int DESCRIPTION_COLUMN=2;
    private final String NO_ANNOTATION="no annotation available";
    private final String NO_ONTOLOGY="no ontology available";
    
    
    JTable jTable1 = new JTable();
    final GoBin goBin;
    private Annotation annotation;
    private Ontology ontology;
    
    private HashMap term_Annotation= new HashMap();
    private HashMap available_Annotations=new HashMap();
    
    private JComboBox annotationComboBox;
    private JComboBox ontologyComboBox;
    //private boolean empty=true;
    private ArrayList dataAL = new ArrayList();
    private StartTableModel tableModel;
    private String type;
    private JCheckBox autoColor;
    DisplayPieChart2 displayPieChartListener;
    
    /** Creates a new instance of StartPanelPanel */
    public StartPanelPanel(GoBin goB,String type) {
        this.goBin = goB;
        this.type=type;
        initComponents ();
        
    }
     void initComponents (){
        this.setLayout(new BorderLayout());
        JPanel jPanelDeBase = new JPanel();
        jPanelDeBase.setLayout(new BorderLayout());
        JPanel jPanelDuHaut = new JPanel();
        
        
        annotationComboBox= new JComboBox();
        ontologyComboBox= new JComboBox();
        annotationComboBox.addItem(NO_ANNOTATION);
        ontologyComboBox.addItem(NO_ONTOLOGY);
        
        //autoColor = new JCheckBox();
        //jPanelDuHaut.add(autoColor);
        
        //jPanelDuHaut.add(new JLabel("default annotation"));
        jPanelDuHaut.add(annotationComboBox);
        
        //jPanelDuHaut.add(ontologyComboBox);
        
        JButton newTerm = new JButton("Add GO category");
        jPanelDuHaut.add(newTerm);
        newTerm.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent ev){
                chooseNewTerm();
            }
        });
        
        jPanelDeBase.add(jPanelDuHaut,java.awt.BorderLayout.NORTH);
        
        String[] columnNames = {" ","Term","Description","Annotation","Layout","X"};
        
        tableModel=new StartTableModel(columnNames,dataAL);
        jTable1 = new JTable(tableModel);
        
        
        JScrollPane jScrollPane = new javax.swing.JScrollPane(jTable1);
        
        
        
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize(); 
        TableColumnModel tcModel = jTable1.getColumnModel();
                for (int i=0;i<columnNames.length;i++){
                        if (columnNames[i].equals(" ")){
                            tcModel.getColumn(i).setPreferredWidth(15);
                            tcModel.getColumn(i).setMaxWidth(15);
                            tcModel.getColumn(i).setResizable(false);
                        }
                        if (columnNames[i].equals("Term"))
                            tcModel.getColumn(i).setPreferredWidth(50);
                         if (columnNames[i].equals("Description"))
                            tcModel.getColumn(i).setPreferredWidth((screenSize.width-15-50-100-10-15)/2);
                         if (columnNames[i].equals("Annotation"))
                            tcModel.getColumn(i).setPreferredWidth((screenSize.width-15-50-100-10-15)/2);
                         if (columnNames[i].equals("X")){
                            tcModel.getColumn(i).setPreferredWidth(10);
                            tcModel.getColumn(i).setMaxWidth(10);
                            tcModel.getColumn(i).setResizable(false);
                         }
                         if (columnNames[i].equals("Layout")){
                            tcModel.getColumn(i).setPreferredWidth(40);
                            tcModel.getColumn(i).setResizable(false);
                         }
                         
                }
        
        //jPanelDeBase.add(jScrollPane,java.awt.BorderLayout.CENTER);
        
        
        TableColumn coloredColumn = jTable1.getColumnModel().getColumn(this.getDescriptionColumn());
        coloredColumn.setCellRenderer(new ColorRenderer(false,this.getGoBin().getGoColor(), this));
        
        TableColumn LayoutColumn = jTable1.getColumnModel().getColumn(this.getLayoutColumn());
        LayoutColumn.setCellRenderer(new ColorRendererForStartLayout(false,this));
        
        TableColumn RemoveColumn = jTable1.getColumnModel().getColumn(this.getRemoveColumn());
        RemoveColumn.setCellRenderer(new ColorRendererForRemoveStart(false,this));
        
        jTable1.addMouseListener(new MouseStartPanelHandler(this));
        jTable1.addMouseMotionListener(new MouseMotionStartPanelHandler(this));
        
        jPanelDeBase.add(jScrollPane);
        //final ResultTableSortFilterModel sorter = new ResultTableSortFilterModel(jTable1.getModel());/////////////////////////
        //jTable1 = new JTable(sorter);
        
        
        displayPieChartListener = new DisplayPieChart2(this, this.goBin.getNetwork_Options());
                
        JPanel jPanelApplyButtons = new JPanel();
        GridBagLayout blayout = new GridBagLayout();
        GridBagConstraints constr = new GridBagConstraints();
        jPanelApplyButtons.setLayout(blayout);
        constr.weightx = 0;
        
        
        //jPanelApplyButtons.setLayout(new BorderLayout());
        
        JButton selectAllButton = new JButton();
        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e){
                            selectAll();
                        }
        });
        jPanelApplyButtons.add(selectAllButton,constr);
        constr.weightx = 100;
        constr.anchor = constr.WEST;
        
        JButton unSelectAllButton = new JButton();
        unSelectAllButton.setText("Unselect All");
        unSelectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e){
                            unselectAll();
                        }
        });
        jPanelApplyButtons.add(unSelectAllButton,constr);
        
        //JButton pourjoliesse =new JButton();
        //pourjoliesse.setPreferredSize(new Dimension(500,JButton.HEIGHT));
        //jPanelApplyButtons.add(pourjoliesse);
        
        JButton jButton1 = new JButton("Validate");
        jButton1.setText("Validate");
        jButton1.addActionListener(displayPieChartListener); ////// 1 
        constr.weightx=0;
        jPanelApplyButtons.add(jButton1,constr);
              
        
        JButton automaticColorsButton = new JButton();
        automaticColorsButton.setText("Auto-Colors");
        automaticColorsButton.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e){
                            StartPanelPanel all = goBin.getStartPanel().tabAll;
                            //goBin.getStartPanel().tabAll.getS
                            goBin.getGoColor().clear();
                            goBin.synchroColor();
                            goBin.resetAutomaticColor();
                            for (int i=0;i<getRowCount();i++){
                                String term = getTerm(i);
                                if (isSelected(i)){
                                    if (!goBin.isAutomaticlyColored(term) ){
                                        goBin.getGoColor().put(term,goBin.getNextAutomaticColor(term));
                                        ((StartTableModel)jTable1.getModel()).fireColor();
                                        //System.out.println("oui");
                                    }
                                    
                                }
                                //else {
                                //    goBin.goColor.remove(term);
                                //    goBin.freeAutomaticColor(term);
                                //}
                            }
                            
                            displayPieChartListener.doIt(displayPieChartListener.APPLY_CHANGE,true,Cytoscape.getCurrentNetworkView());
                            goBin.synchroColor();
                        }
                    });
        
        jPanelApplyButtons.add(automaticColorsButton,constr);
        
        
        JButton jButton3 = new JButton();
        jButton3.setText("Reset");
        jButton3.addActionListener(displayPieChartListener);
        jPanelApplyButtons.add(jButton3,constr);

        
        constr.weightx=100;
        constr.anchor=constr.EAST;
        JButton jButton4 = new JButton("Select nodes");
        jButton4.addActionListener(new ZSelectNodes((ResultAndStartPanel)this));
        jPanelApplyButtons.add(jButton4,constr);
        
        
        
        
        
        jPanelDeBase.add(jPanelApplyButtons,java.awt.BorderLayout.SOUTH);
        this.add(jPanelDeBase,java.awt.BorderLayout.CENTER);
        
        
        //goBin.repaint();
    }
    public DisplayPieChart2 getDisplayPieChart(){
        return this.displayPieChartListener;
    }
    boolean isAutoColor(){
        return this.autoColor.isSelected();
    }
    void setAutoColor(boolean b){
        autoColor.setSelected(b);
    }
     
    public int getRowCount(){
        return this.jTable1.getRowCount();
    }
    public String getTerm(int i){
        return ((String)this.jTable1.getValueAt(i,this.getGoTermColumn()));
    }
    public HashMap getTerm_Annotation(){
        return term_Annotation;
    }
     
    public String getTypeAnnotation(){
        return type;
    }
    public GoBin getGoBin(){
        return goBin;
    }
    public Annotation getAnnotation(){
        return annotation;
    }
    public void setAnnotation(Annotation annotation){
        this.annotation=annotation;
    }
    public Annotation getAnnotation(String term){
        return (Annotation)this.term_Annotation.get(term);
    }
    public Ontology getOntology(){
        return annotation.getOntology();
    }
    public Ontology getOntology(String term){
        return (Ontology)((Annotation)this.term_Annotation.get(term)).getOntology();
    }
    public int getSelectColumn(){
        return this.SELECT_COLUMN;
    }
    public int getGoTermColumn(){
        return this.GO_TERM_COLUMN;
    }    
    public int getDescriptionColumn(){
        return this.DESCRIPTION_COLUMN;
    }
    public int getLayoutColumn(){
        return this.LAYOUT_COLUMN;
    }
    public int getRemoveColumn(){
        return this.REMOVE_COLUMN;
    }
    
    public JTable getJTable(){
        return this.jTable1;
    }
    public int getTermIndex(String term){
        int index =-1;
        for (int i =0; i<jTable1.getRowCount();i++){
            if (((String)jTable1.getValueAt(i,getGoTermColumn())).equals(term)){
                index=i;
                break;
            }
                
        }
        
        return index;
    }
    public void unselectAll(){
        for (int i =0; i<jTable1.getRowCount();i++){
            jTable1.setValueAt(new Boolean(false),i,this.getSelectColumn());
        }
    }
    public void selectAll(){
        for (int i =0; i<jTable1.getRowCount();i++){
            jTable1.setValueAt(new Boolean(true),i,this.getSelectColumn());
        }
    }
    
    public boolean isSelected(String term){
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,this.getSelectColumn())).booleanValue() &&
                   ((String)jTable1.getValueAt(i,this.getGoTermColumn())) == term)
                return true;
            
        }
        return false;
    }
    public boolean isSelected(int i){
        return ((Boolean)this.jTable1.getValueAt(i,this.getSelectColumn())).booleanValue();
            
    }
    public boolean unselect(String term){
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,this.getSelectColumn())).booleanValue() &&
                   ((String)jTable1.getValueAt(i,this.getGoTermColumn())) == term)
                jTable1.setValueAt(new Boolean(true),i,this.getSelectColumn());
                return true;
            
        }
        return false;
    }
    public boolean select(String term){
        for (int i=0;i<jTable1.getRowCount();i++){
            if ( !((Boolean)jTable1.getValueAt(i,this.getSelectColumn())).booleanValue() &&
                   ((String)jTable1.getValueAt(i,this.getGoTermColumn())) == term)
                jTable1.setValueAt(new Boolean(false),i,this.getSelectColumn());
                return true;
            
        }
        return false;
    }
    public void addLine(String term,Annotation annotation,Ontology ontology){
        //dataAL
        if (term_Annotation.containsKey(term)){
            System.out.println("yen a deja du "+term+" et c avec "+(String)this.annotationToString((Annotation)this.term_Annotation.get(term)));
            return;
        }
        if (   this.type.equals(this.ALLTYPE) || annotation.getType().matches(".*"+this.type) || 
                (    (!annotation.getType().matches(".*"+this.BPTYPE) && !annotation.getType().matches(".*"+this.CCTYPE) 
                          && !annotation.getType().matches(".*"+this.MFTYPE))&& 
                     (!annotation.getCurator().matches("^[gG][oO]") && this.type.equals(this.OTHERTYPE) )
                )
            ){
            term_Annotation.put(term,annotation);
            
            
            final String terme = term;
            Object [] line= new Object[6];
            
            line[this.getSelectColumn()]=new Boolean(true);
            line [this.getGoTermColumn()]=term;
            

            line[this.getDescriptionColumn()]=new JLabel(ontology.getTerm(Integer.parseInt(term)).getName());
            ((JLabel)line[getDescriptionColumn()]).setBackground((Color)goBin.getGoColor().get(term));

            line[3]=annotation.getCurator()+" "+annotation.getType();


            //JButton removeB=new JButton("remove");
            line[this.getRemoveColumn()]=new JLabel("X");
            

            //JButton layoutB=new JButton("layout");
            line[this.getLayoutColumn()]=new JLabel(" ");
            ((JLabel)line[this.getLayoutColumn()]).setBackground(Color.WHITE);
            
            tableModel.addLine(line);//System.out.println(term);
            
            
            goBin.getLayoutPanel().addLine(term,getAnnotation(term),getAnnotation(term).getOntology());
            
            
            
            
            this.upDateAnnotationComboBox();
            
        }
    }
    public void removeLine(String term){
        //term_Annotation.remove(term);
        
        
        
        
        
        tableModel.removeLine(term);
        removeTermOfAllCyNetworkView(term);
        
        
        
        goBin.synchroSelections();
        
        this.term_Annotation.remove(term);
        upDateAnnotationComboBox();
        
        //goBin.goTerm_Annotation.remove(term);
        
        
    }
    
    void removeTermOfAllCyNetworkView(String term){
        
        Iterator it = goBin.getNetwork_Options().keySet().iterator();
        
        
        
        
        it = goBin.getNetwork_Options().keySet().iterator();
        while (it.hasNext()){
            cytoscape.view.CyNetworkView cnv = (cytoscape.view.CyNetworkView)it.next();
            PieChartInfo pci = (PieChartInfo)goBin.getNetwork_Options().get(cnv);
            //pci.goToDisplay.remove(term);
            if (pci.goToDisplay.contains(term)){
                this.displayPieChartListener.termToRemove=term;
                this.displayPieChartListener.doIt(displayPieChartListener.APPLY_CHANGE,true,cnv);
            }
            
        }
        
        
        
    }
    
    public void upDateAnnotationComboBox() {
        this.annotationComboBox.removeAllItems();
        Annotation ann;
        HashSet fait=new HashSet();
        available_Annotations.clear();
        //Iterator it = this.term_Annotation.keySet().iterator();
        //if ()
        for (int i =2;i<this.goBin.getResultPanelCount()+2;i++){
            ann = ((ResultPanel)goBin.getResultTabAt(i)).getAnnotation();
            if (   this.type.equals(this.ALLTYPE) || ann.getType().matches(".*"+this.type) || 
                (    (!ann.getType().matches(".*"+this.BPTYPE) && !ann.getType().matches(".*"+this.CCTYPE) 
                          && !ann.getType().matches(".*"+this.MFTYPE))&& 
                     (!ann.getCurator().matches("^[gG][oO]") && this.type.equals(this.OTHERTYPE) )
                )
            ){
                fait.add(annotationToString(ann));
                this.available_Annotations.put(annotationToString(ann),ann);
            }
        }
        
        Iterator it = this.term_Annotation.values().iterator();
        while (it.hasNext()){
            ann = (Annotation)it.next();
            fait.add(annotationToString(ann));
            this.available_Annotations.put(annotationToString(ann),ann);
        }
        it = fait.iterator();
        if (!it.hasNext())
            annotationComboBox.addItem(this.NO_ANNOTATION);
        while (it.hasNext()){
            
            String kkc = (String)it.next();
            annotationComboBox.addItem(kkc);
        }
        
    }
    
    
    
    private void chooseNewTerm(){
        //JFrame frame = new JFrame("Choose a term");
        //frame.getContentPane().setLayout(new BorderLayout());
        if (this.NO_ANNOTATION.equals((String)annotationComboBox.getSelectedItem())){
            JOptionPane.showMessageDialog(this,this.NO_ANNOTATION);
            return;
        }
        
        String term = JOptionPane.showInputDialog(this,"Add a term ID");
        int termInt;
        boolean erreur = false;
        try {
            termInt = Integer.parseInt(term);
        }
        catch (Exception e){
            if (term==null){
                return;
            }
            erreur = true;
            JOptionPane.showMessageDialog(this,"bad value : "+term);
            return;
        }
        
        String annName =  (String)this.annotationComboBox.getSelectedItem();
        Annotation ann =  (Annotation)available_Annotations.get(annName);
        Ontology ont= ann.getOntology();
        cytoscape.data.annotation.OntologyTerm ontTerm = ont.getTerm(termInt);
        if (ontTerm==null){
            JOptionPane.showMessageDialog(this,annName+"\ndoes not contains the term ID "+term);
            return;
        }
        if (JOptionPane.showConfirmDialog(this,"do you want to associate\n\""+ontTerm.toString()+"\"\nwith this annotation :\n"+ann.getType())==JOptionPane.YES_OPTION){
            this.addLine(term,ann,ann.getOntology());
        }
    }
    
    private String annotationToString(Annotation ann){
        return ann.getCurator()+","+ann.getType()+","+ann.getSpecies()+"/"+ann.getOntologyType();
    }
    
}
