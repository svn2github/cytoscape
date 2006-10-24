/*
 * LayoutPanel.java
 *
 * Created on August 11, 2006, 2:29 PM
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


import java.util.*;
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


/**
 *
 * @author ogarcia
 */



public class LayoutPanel implements ResultAndStartPanel{
    
    final Settings setting = new Settings();
    private int SELECT_COLUMN = 0;
    private int GO_TERM_COLUMN=1;
    private int ANNOTATION_COLUMN=3;
    private int REMOVE_COLUMN=5;
    private int DESCRIPTION_COLUMN=2;
    private int METHOD_COLUMN=4;
    
    private JFrame advSettings;
    
    private final String NO_ANNOTATION="no annotation available";
    private final String NO_ONTOLOGY="no ontology available";
    protected final String METHOD_NORMAL="Movable Node";
    protected final String METHOD_STRANGE="Fixed Node";
    protected final String METHOD_SUPERSTRANGE="Strange Node + Strange Tensio";
    
    protected final String METHOD1="method 1";
    protected final String METHOD2="method 2";
    protected final String METHOD3="method 3";
    
    protected final String LAYOUT1="layout1";
    protected final String LAYOUT2="layout2";
    protected final String LAYOUT3="layout3";
    
    
    
    GoBin goBin;
    private HashMap term_Annotation= new HashMap();
    ArrayList termsList;
    private ArrayList dataAL = new ArrayList();
    JPanel jPanelDeBase;
    JTable jTable1;
    LayoutTableModel tableModel;
    StartPanelPanel tabAll;
    
    HashMap goNodesNormalPosition=new HashMap();
    HashMap goNodesStrangePosition=new HashMap();
    
    //JCheckBox useGoBin;
    //JCheckBox minimizeEdge;
    JCheckBox circularize;
    
    JTextField goStrength1;
    JTextField kFactorAnnotationNodeRound2;
    JTextField vectorFactorForStrangeThings;
    JTextField attractionForce;
    
    final String default1stPassIteration = "200";
    final String default2ndPassIteration = "400";
    
    final JLabel iterations1TF=new JLabel(default1stPassIteration);
    final JLabel iterations2TF=new JLabel(default2ndPassIteration);
    final JSlider slideAttraction=new JSlider(JSlider.HORIZONTAL,1,100,100);
    
    JComboBox style;
    JComboBox layoutMethod;
    JComboBox randomize;
    
    /** Creates a new instance of LayoutPanel */
    public LayoutPanel(GoBin goBin) {
        this.goBin=goBin;
        
        StartPanel sp = goBin.getStartPanel();
        tabAll = sp.tabAll;
        //initAdvancedSettings();
        
    }
    public void initComponents(){
        
        //initAdvancedSettings();
        jPanelDeBase = new JPanel();
        jPanelDeBase.setLayout(new BorderLayout());
        
        JPanel jPanelDuHaut = new JPanel();
        jPanelDeBase.add(jPanelDuHaut, BorderLayout.NORTH);
        
        
        JPanel jPanelDuBas = new JPanel();
        jPanelDeBase.add(jPanelDuBas, BorderLayout.SOUTH);
        GridBagLayout gridbag = new GridBagLayout() ;		
	GridBagConstraints c = new GridBagConstraints();
        jPanelDuBas.setLayout(gridbag);
        
        String[] columnNames = {" ","Term","Description","Annotation","Group Separation","X"};
        tableModel = new LayoutTableModel(columnNames,dataAL);
        
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
                    tcModel.getColumn(i).setPreferredWidth((screenSize.width-15-50-100-15-200)/2);
                 if (columnNames[i].equals("Annotation"))
                    tcModel.getColumn(i).setPreferredWidth((screenSize.width-15-50-100-15-200)/2);
                 if (columnNames[i].equals("Method"))
                    tcModel.getColumn(i).setPreferredWidth(200);
                 if (columnNames[i].equals("X")){
                    tcModel.getColumn(i).setPreferredWidth(10);
                    tcModel.getColumn(i).setMaxWidth(10);
                    tcModel.getColumn(i).setResizable(false);
                 }
        }
        
        TableColumn coloredColumn = jTable1.getColumnModel().getColumn(this.getDescriptionColumn());
        coloredColumn.setCellRenderer(new ColorRenderer(false,this.getGoBin().getGoColor(), this));
        
        //TableColumn LayoutColumn = jTable1.getColumnModel().getColumn(this.getLayoutColumn());
        //LayoutColumn.setCellRenderer(new ColorRendererForStartLayout(false,this));
        
        TableColumn removeColumn = jTable1.getColumnModel().getColumn(this.getRemoveColumn());
        removeColumn.setCellRenderer(new ColorRendererForRemoveLayout(false,this));
        
        //TableColumn methodColumn = jTable1.getColumnModel().getColumn(this.getMethodColumn());
        //JComboBox cb = new JComboBox();
        //cb.addItem(this.METHOD_NORMAL);
        //cb.addItem(this.METHOD_STRANGE);
        //cb.addItem(this.METHOD_SUPERSTRANGE);
        //methodColumn.setCellEditor(new DefaultCellEditor(cb));
        
        
        jTable1.addMouseListener(new MouseLayoutPanelHandler(this));
        jTable1.addMouseMotionListener(new MouseMotionLayoutPanelHandler(this));
        
        
        
        c.weighty=1;
        c.weightx=1;
        c.gridheight=2;
        //c.fill=c.VERTICAL;
        JButton selectAllButton = new JButton();
        selectAllButton.setText("Select All");
        selectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e){
                            selectAll();
                        }
        });
        jPanelDuBas.add(selectAllButton,c);
        
        JButton unSelectAllButton = new JButton();
        unSelectAllButton.setText("Unselect All");
        unSelectAllButton.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e){
                            unselectAll();
                        }
        });
        c.weightx=100;
        c.anchor=c.WEST;
        jPanelDuBas.add(unSelectAllButton,c);
        
        
        //c.fill=c.HORIZONTAL;
        
        
        c.gridheight=2;
        c.weightx=1;
        c.anchor=c.CENTER;
        
        
        JPanel JPanelMilieu = new JPanel();
        GridBagLayout gridbagMilieu = new GridBagLayout() ;	
        GridBagConstraints cMilieu = new GridBagConstraints();
        
        JPanel jp1 = new JPanel();
        jp1.setLayout(new BorderLayout());
        jPanelDuBas.add(JPanelMilieu,c);
        JPanelMilieu.add(jp1,cMilieu);
        
        //////////////////////////////////////goStrength
        jp1.add(new JLabel("Intra-Group Attraction 2"),BorderLayout.WEST);
        //jPanelDuBas.add(goStrength1);
        
        this.kFactorAnnotationNodeRound2=new javax.swing.JTextField(5);
        this.kFactorAnnotationNodeRound2.setText("3");
        
        
        final JSlider slideGoStrength = new JSlider(JSlider.HORIZONTAL,1,100,30);
        ChangeListener cl = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if (!slideGoStrength.getValueIsAdjusting()){
                    double value = ((double)slideGoStrength.getValue())/10.0;
                
                    kFactorAnnotationNodeRound2.setText(Double.toString(value));
                    
                }
            }
        };
        slideGoStrength.addChangeListener(cl);
        
        jp1.add(this.kFactorAnnotationNodeRound2,BorderLayout.EAST);
        
        
        this.kFactorAnnotationNodeRound2.setText("3");
        kFactorAnnotationNodeRound2.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent f){
                
            }
            public void focusGained(FocusEvent f){
                kFactorAnnotationNodeRound2.setSelectionStart(0);
                kFactorAnnotationNodeRound2.setSelectionEnd(kFactorAnnotationNodeRound2.getText().length());
            }
            
        });
        kFactorAnnotationNodeRound2.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                double value;
                try {
                    value = Double.parseDouble(kFactorAnnotationNodeRound2.getText());
                }
                catch (Exception ex){
                    value = 1;
                }
                
                slideGoStrength.setValueIsAdjusting(true);
                if (value < 0.1 )
                    slideGoStrength.setValue(1);
                else if (value >10)
                    slideGoStrength.setValue(100);
                else 
                    slideGoStrength.setValue((int)(value*10));
                slideGoStrength.setValueIsAdjusting(false);
                
                kFactorAnnotationNodeRound2.setText(Double.toString((value)));
                //setting.kFactorAnnotationNodeRound2=Double.toString(value);
            }
        });
        
        
        
        
        JPanel jp2 = new JPanel();
        
        jp2.setLayout(new BorderLayout());
        JPanelMilieu.add(jp2,cMilieu);
        //c2.anchor=c2.EAST;
        /////////////////////////////////////////////////DISSOCIATION
        vectorFactorForStrangeThings=new javax.swing.JTextField(5);
        vectorFactorForStrangeThings.setText("10");
        jp2.add(new JLabel("Inter-Group Distance"),BorderLayout.WEST);
        //jPanelDuBas.add(vectorFactorForStrangeThings);
        final JSlider slideDissociation = new JSlider(JSlider.HORIZONTAL,0,100,10);
        ChangeListener clDissociation = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if (!slideDissociation.getValueIsAdjusting()){
                    double value = ((double)slideDissociation.getValue());
                
                    vectorFactorForStrangeThings.setText(Double.toString(value));
                    
                }
            }
        };
        slideDissociation.addChangeListener(clDissociation);
        
        
        //c2.anchor=c2.WEST;
        jp2.add(this.vectorFactorForStrangeThings,BorderLayout.EAST);
        jp2.validate();
        //this.vectorFactorForStrangeThings.setText("10");
        vectorFactorForStrangeThings.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent f){
                
            }
            public void focusGained(FocusEvent f){
                vectorFactorForStrangeThings.setSelectionStart(0);
                vectorFactorForStrangeThings.setSelectionEnd(vectorFactorForStrangeThings.getText().length());
            }
            
        });
        vectorFactorForStrangeThings.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                double value;
                try {
                    value = Double.parseDouble(vectorFactorForStrangeThings.getText());
                }
                catch (Exception ex){
                    value = 10;
                }
                
                slideDissociation.setValueIsAdjusting(true);
                if (value < 1 ){
                    slideDissociation.setValue(1);
                    vectorFactorForStrangeThings.setText("1");
                }
                else if (value >100){
                    slideDissociation.setValue(100);
                    vectorFactorForStrangeThings.setText("100");
                }
                else 
                    slideDissociation.setValue((int)(value));
                slideDissociation.setValueIsAdjusting(false);
                
                vectorFactorForStrangeThings.setText(Double.toString((value)));
                //setting.vectorFactorForStrangeThings=Double.toString(value);
            }
        });
        
        
        
        //cMilieu.gridwidth = cMilieu.REMAINDER;
        JPanel jp3 = new JPanel();
        
        jp3.setLayout(new BorderLayout());
        //JPanelMilieu.add(jp3,cMilieu);
        
        /////////////////////////////////////////////// ATTRACTION
        //jp3.add(new JLabel("Density"),BorderLayout.WEST);
        this.attractionForce = new JTextField(5);
        attractionForce.setText("0.1");
        //jPanelDuBas.add(attractionForce,c);
        
        //slideAttraction = ;
        ChangeListener clAttraction = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if (!slideAttraction.getValueIsAdjusting()){
                    double value = ((double)slideAttraction.getValue())/10.0;
                
                    attractionForce.setText(Double.toString(value));
                    
                }
            }
        };
        slideAttraction.addChangeListener(clAttraction);
        //jPanelDuBas.add(slideAttraction);
        //c.gridwidth=c.REMAINDER;
        
        
        //jp3.add(this.attractionForce,BorderLayout.EAST);
        //jp3.validate();
        this.attractionForce.setText("10");
        attractionForce.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent f){
                
            }
            public void focusGained(FocusEvent f){
                attractionForce.setSelectionStart(0);
                attractionForce.setSelectionEnd(attractionForce.getText().length());
            }
            
        });
        attractionForce.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                double value;
                try {
                    value = Double.parseDouble(attractionForce.getText());
                }
                catch (Exception ex){
                    value = 10;
                }
                
                slideAttraction.setValueIsAdjusting(true);
                if (value < 0.1 )
                    slideAttraction.setValue(1);
                else if (value >10)
                    slideAttraction.setValue(100);
                else 
                    slideAttraction.setValue((int)(value*10));
                slideAttraction.setValueIsAdjusting(false);
                
                attractionForce.setText(Double.toString((value)));
                //setting.attractionForce=Double.toString(value);
            }
        });
        c.weightx=100;
        c.gridheight=2;
        c.weighty=1;
        //c.fill=c.HORIZONTAL;
        //c.fill = c.VERTICAL;
        c.anchor=c.EAST;
        JButton advSet = new JButton("Advanced Settings");
        advSet.addActionListener(new ActionListener(){
            public void actionPerformed (ActionEvent ev){
                
                getAdvSettings().setVisible(true);
                getAdvSettings().toFront();
            }
        });
        jPanelDuBas.add(advSet,c);
        
        c.gridwidth=c.REMAINDER;
        c.weightx=0;
        JButton layoutButton=new JButton("Layout");
        jPanelDuBas.add(layoutButton,c);
        layoutButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                doLayout();
            }
        });
        
        //c.fill=  c.HORIZONTAL; 
        
        
        
        
        //slideGoStrength.get
        jp1.add(slideGoStrength,BorderLayout.SOUTH);
        jp1.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        jp2.add(slideDissociation,BorderLayout.SOUTH);
        jp2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        jp3.add(slideAttraction,BorderLayout.SOUTH);
        jp3.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        jPanelDeBase.add(jScrollPane,BorderLayout.CENTER);
        
        //initAdvancedSettings();
        
        
    }
    
    void initAdvancedSettings(){
        //advSettings.getContentPane().setLayout()
        
        this.setAdvSettings(new JFrame("Advanced Settings"));
        //goBin.add(advSettings);
        GridBagLayout gridbag = new GridBagLayout() ;		
	GridBagConstraints c = new GridBagConstraints();
        c.weighty = 1 ;
        c.weightx = 1 ;
        //c.fill = GridBagConstraints.NONE;
        c.fill = GridBagConstraints.HORIZONTAL;
        JPanel jPanelDuBas= new JPanel();
        this.getAdvSettings().getContentPane().setLayout(new BorderLayout());
        this.getAdvSettings().getContentPane().add(jPanelDuBas,BorderLayout.CENTER);
        jPanelDuBas.setLayout(gridbag);
        
        jPanelDuBas.add(new JLabel("Iterations in 1st phase"),c);
        c.gridwidth = c.RELATIVE;
        final JSlider slide = new JSlider(JSlider.HORIZONTAL,50,500,200);
        slide.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                iterations1TF.setText(Integer.toString(slide.getValue()));
                setting.iter1=Integer.toString(slide.getValue());
            }
        });
        jPanelDuBas.add(slide,c);
        c.gridwidth=c.REMAINDER;
        jPanelDuBas.add(iterations1TF,c);
        
        c.gridwidth=1;
        //c.fill = GridBagConstraints.NONE;
        c.fill = GridBagConstraints.HORIZONTAL;
        jPanelDuBas.add(new JLabel("Iterations in 2nd phase"),c);
        c.gridwidth = c.RELATIVE;
        final JSlider slide2 = new JSlider(JSlider.HORIZONTAL,50,500,400);
        slide2.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                iterations2TF.setText(Integer.toString(slide2.getValue()));
                setting.iter2=Integer.toString(slide2.getValue());
            }
        });
        jPanelDuBas.add(slide2,c);
        c.gridwidth=c.REMAINDER;
        jPanelDuBas.add(iterations2TF,c);
        
        
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        jPanelDuBas.add(new JLabel("Intra-Group Attraction 1"),c);
        c.gridwidth = c.RELATIVE;
        this.goStrength1=new javax.swing.JTextField(5);
        final JSlider slideGoStrength1stPass = new JSlider(JSlider.HORIZONTAL,1,100,50);
        ChangeListener cl = new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                if (!slideGoStrength1stPass.getValueIsAdjusting()){
                    double value = ((double)slideGoStrength1stPass.getValue())/10.0;
                
                    goStrength1.setText(Double.toString(value));
                    setting.goStrength1=Double.toString(value);
                }
            }
        };
        slideGoStrength1stPass.addChangeListener(cl);
        
        
        
        jPanelDuBas.add(slideGoStrength1stPass,c);
        c.gridwidth=c.REMAINDER;
        jPanelDuBas.add(this.goStrength1,c);
        
        
        this.goStrength1.setText("5");
        goStrength1.addFocusListener(new FocusListener(){
            public void focusLost(FocusEvent f){
                
            }
            public void focusGained(FocusEvent f){
                goStrength1.setSelectionStart(0);
                goStrength1.setSelectionEnd(goStrength1.getText().length());
            }
            
        });
        goStrength1.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                
                double value;
                try {
                    value = Double.parseDouble(goStrength1.getText());
                }
                catch (Exception ex){
                    value = 0.25;
                }
                
                slideGoStrength1stPass.setValueIsAdjusting(true);
                if (value < 0.1 )
                    slideGoStrength1stPass.setValue(1);
                else if (value >10)
                    slideGoStrength1stPass.setValue(100);
                else 
                    slideGoStrength1stPass.setValue((int)(value*10));
                slideGoStrength1stPass.setValueIsAdjusting(false);
                
                goStrength1.setText(Double.toString((value)));
                setting.goStrength1=Double.toString(value);
            }
        });
        
        
        
        
        //attractionforce et slideattraction sont definis et listenerise dans initComponents
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        jPanelDuBas.add(new JLabel("Density of nodes"),c);
        c.gridwidth = c.RELATIVE;
        jPanelDuBas.add(this.slideAttraction,c);
        c.gridwidth=c.REMAINDER;
        jPanelDuBas.add(this.attractionForce,c);
        
        
        
        /*
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=1;//c.anchor=c.EAST;
        //jPanelDuBas.add(new JLabel("use current visualisation"));
        useGoBin = new JCheckBox();
        //jPanelDuBas.add(useGoBin,c);
        c.gridwidth=c.REMAINDER;
        
        //jPanelDuBas.add(new JLabel("one edge per nodes couple"));
        minimizeEdge = new JCheckBox();
        //jPanelDuBas.add(minimizeEdge,c);
        
        this.advSettings.pack();
        advSettings.setVisible(false);
        
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        jPanelDuBas.add(new JLabel("Circularize"));
        //circularize = new JCheckBox();
        jPanelDuBas.add(circularize);
        circularize.setSelected(true);
        */
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=c.REMAINDER;
        
        style=new JComboBox();
        style.addItem(this.METHOD1);
        style.addItem(this.METHOD2);
        style.addItem(this.METHOD3);
        style.setSelectedItem(METHOD1);
        //jPanelDuBas.add(style,c);
        
        
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        jPanelDuBas.add(new JLabel("Initial node placement"));
        
        c.gridwidth=1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=c.REMAINDER;
        
        this.randomize=new JComboBox();
        randomize.addItem("Randomize");
        randomize.addItem("Circularize");
        randomize.setSelectedItem("Randomize");
        jPanelDuBas.add(randomize);
        
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth=1;
        
        layoutMethod = new JComboBox();
        layoutMethod.addItem(this.LAYOUT1);
        layoutMethod.addItem(this.LAYOUT2);
        layoutMethod.addItem(this.LAYOUT3);
        layoutMethod.setSelectedItem(LAYOUT1);
        //jPanelDuBas.add(layoutMethod,c);
        getAdvSettings().pack();
        
    }
    
    class Settings {
        String temp = "10";
        String iter1 = "200";
        String iter2 = "300";
        String goStrength1 = "0.25";
        public Settings(){
            
        }
        
    }
    
    void doLayout (){
        HashSet goNodesNormal=new HashSet();
        HashSet goNodesStrange=new HashSet();
        String term;
        
        for (int i=0;i<jTable1.getRowCount();i++){
            term = (String)jTable1.getValueAt(i,this.getGoTermColumn());
            Integer integerLine=new Integer(0);;
            if (this.isSelected(i)){
                /*if (jTable1.getValueAt(i,this.getMethodColumn()).equals(this.METHOD_NORMAL)){
                    goNodesNormal.add(term);
                }
                
                if (jTable1.getValueAt(i,this.getMethodColumn()).equals(this.METHOD_STRANGE))
                    goNodesStrange.add(term);
                */
                if (this.isLayoutSelected(i)){
                    goNodesStrange.add(term);
                    System.out.println("Strnage "+term);
                }
                else {
                    goNodesNormal.add(term);
                    System.out.println("normal "+term);
                }
            }
        }
        //if (((String)this.layoutMethod.getSelectedItem()).equals(this.LAYOUT1)){
            new FruchtermanTheEnd(this,goNodesNormal,goNodesStrange,Cytoscape.getCurrentNetworkView(),"","","",
                                "","");
           
        //}
        //else if (((String)this.layoutMethod.getSelectedItem()).equals(this.LAYOUT2)){
        //    new FruchtermanSpring(this,goNodesNormal,goNodesStrange,Cytoscape.getCurrentNetworkView(),"","","",
        //                        "","");
        //                        System.out.println(layoutMethod.getSelectedItem());
        //}
        //else if (((String)this.layoutMethod.getSelectedItem()).equals(this.LAYOUT3)){
        //    new FruchtermanSpring2(this,goNodesNormal,goNodesStrange,Cytoscape.getCurrentNetworkView(),"","","",
        //                        "","");
            
        //}
        //else 
        //    System.out.println(layoutMethod.getSelectedItem()+" crash");
            
            
        //new FruchtermanSpring2(this,goNodesNormal,goNodesStrange,Cytoscape.getCurrentNetworkView(),"","","",
        //                        "","");
        //new FruchtermanTheEnd(this,goNodesNormal,goNodesStrange,Cytoscape.getCurrentNetworkView(),"","","",
         //                       "","");
    }
    public DisplayPieChart2 getDisplayPieChart(){
        return null;
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
        return this.METHOD_COLUMN;
    }
    public int getRemoveColumn(){
        return this.REMOVE_COLUMN;
    }
    public int getAnnotationColumn(){
        return this.ANNOTATION_COLUMN;
    }
    public int getMethodColumn(){
        return this.METHOD_COLUMN;
    }
    public GoBin getGoBin(){
        return goBin;
    }
    public JTable getJTable(){
        return this.jTable1;
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
    public Annotation getAnnotation(String term){
        return (Annotation)goBin.getStartPanel().tabAll.getTerm_Annotation().get(term);
    }
    public Annotation getAnnotation(){
        JOptionPane.showMessageDialog(goBin,"This is a bug, there's no default annotation in \na layout panel");
        return null;
    }
    public Ontology getOntology(String term){
        return (Ontology)((Annotation)goBin.getStartPanel().tabAll.getTerm_Annotation().get(term)).getOntology();
    }
    public Ontology getOntology(){
        JOptionPane.showMessageDialog(goBin,"Another message for another bug : NO DEFAULT ONTOLOGY IN LAYOUT PANEL");
        return null;
    }
    public HashMap getTermAnnotation(){
        return this.term_Annotation;
    }
    
    
    public boolean isSelected(String term){
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,this.getSelectColumn())).booleanValue() &&
                   ((String)jTable1.getValueAt(i,this.getGoTermColumn())) == term)
                return true;
            
        }
        return false;
    }
    public boolean isSelected(int line){
        
        if (((Boolean)jTable1.getValueAt(line,this.getSelectColumn())).booleanValue() )
            return true;

        return false;
    }
    
    
    public boolean isSelected(String term,Integer lineNumberForReturn){
        for (int i=0;i<jTable1.getRowCount();i++){
            
            if (((String)jTable1.getValueAt(i,this.getGoTermColumn())) == term){
                lineNumberForReturn=new Integer(i);
                if (((Boolean)jTable1.getValueAt(i,this.getSelectColumn())).booleanValue()){
                    
                    return true;
                }
                else 
                    return false;
            }
            
        }
        return false;
    }
    
    public boolean isLayoutSelected(int i){
        if (((Boolean)jTable1.getValueAt(i,this.getLayoutColumn())).booleanValue() ){
                
                return true;
            }
        return false;
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
     public void addLine(String term,Annotation annotation,Ontology ontology){
        //dataAL

            

            //final String terme = term;
            if (this.getTermIndex(term) == -1){
                Object [] line= new Object[6];

                line[this.getSelectColumn()]=new Boolean(true);
                line [this.getGoTermColumn()]=term;
                //line[2]=ontology.getTerm(Integer.parseInt(term)).getName();

                line[this.getDescriptionColumn()]=new JLabel(ontology.getTerm(Integer.parseInt(term)).getName());
                ((JLabel)line[this.getDescriptionColumn()]).setBackground((Color)goBin.getGoColor().get(term));

                line[this.getAnnotationColumn()]=annotation.getCurator()+" "+annotation.getType();


                //JButton removeB=new JButton("remove");
                line[this.getRemoveColumn()]=new JLabel("X");


                //JButton layoutB=new JButton("layout");
                //////////line[this.getMethodColumn()]=new JLabel(" ");
                line[this.getMethodColumn()]=new Boolean(true);



                tableModel.addLine(line);//System.out.println(term);
                term_Annotation.put(term,annotation);
                //jTable1.setValueAt(this.METHOD_NORMAL,jTable1.getRowCount()-1,this.getMethodColumn());
            }
            
            
    }
    
    public void removeLine(String term){
        //term_Annotation.remove(term);
        
        tableModel.removeLine(term);
        term_Annotation.remove(term);
    }

    public JFrame getAdvSettings() {
        return advSettings;
    }

    public void setAdvSettings(JFrame advSettings) {
        this.advSettings = advSettings;
    }
    
    
    
    
    
    
}
