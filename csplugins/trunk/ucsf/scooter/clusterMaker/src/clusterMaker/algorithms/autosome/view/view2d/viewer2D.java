/*
 * clustView.java
 *
 * Created on June 10, 2008, 11:50 AM
 */

package clusterMaker.algorithms.autosome.view.view2d;


import java.util.*;
import javax.swing.tree.*;
import java.io.*;
import clusterMaker.algorithms.autosome.launch.*;
import clusterMaker.algorithms.autosome.cluststruct.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.Timer;
import java.text.*;
/**
 *
 * @author  Aaron
 */
public class viewer2D extends javax.swing.JFrame {
    
    private DefaultMutableTreeNode root;
    private java.awt.image.BufferedImage B;
    private Text_JPanel TP;
    private int clustIndex;
    private static int confThreshold = 0;
    private String highlight = null;
    private int highlightCluster = -1;
    public File inputFile;
    public boolean readColumns = false;
    private int originalWidth = 0;

    private final String autosomeBuild = "1.1";
    private final String guiBuild = "09-09-10";


    public Settings s;
    private cluster[] c;
 
    
    public viewer2D(){
        s = new Settings();
        init();
        this.initComponents();
        jslider1statechange();
       
        setBuild();
        monitorRAM();
        jComboBox6.setSelectedIndex(1);
        this.setLocation(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width/2-this.getWidth()/2,
                         java.awt.Toolkit.getDefaultToolkit().getScreenSize().height/2-this.getHeight()/2);
        this.jMenu4.setEnabled(false);
        this.jMenu16.setEnabled(false);
        originalWidth=this.getWidth();
        this.setSize(this.getWidth(),this.getHeight()-25);
        /*try{
         setIconImage(new javax.swing.ImageIcon(getClass().getResource("/imgs/icon.png")).getImage());
        }catch(Exception err){System.err.println(err);};*/
    }
    
    /** Creates new form clustView */
    public viewer2D(cluster[] c, Settings s) {
        this.s = s;
        this.c = c;
        init();
        initTree(c);
        initComponents();
        jslider1statechange();       
        setBuild();
        monitorRAM();
        jComboBox6.setSelectedIndex(1);
        
        
        if(!s.benchmark){
            String[] ids = new String[s.input.length];
            for(int i = 0; i < s.input.length; i++)
                ids[i] = s.input[i].getIdentity();
            Arrays.sort(ids);
            jComboBox3.removeAllItems();
            for(int i = 0; i < ids.length; i++){
                if(ids[i].length()>0) jComboBox3.addItem(ids[i]);
            }
        }
        
        jTabbedPane1.setSelectedIndex(1);

        initTreeSelection();
       

    }
    

    private void setBuild(){
        jLabel22.setText("AutoSOME build: "+autosomeBuild);
        jLabel27.setText("GUI 2.0 build: "+guiBuild);
        this.setTitle("AutoSOME GUI 2.0 (build "+guiBuild+")");
    }

    public void setGUITitle(String s){
        this.setTitle("AutoSOME GUI 2.0 (build "+guiBuild+") :: "+s);
    }

    private void monitorRAM(){





         Action updateTimeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    long freeMemory = Runtime.getRuntime().freeMemory();
                    long totalMemory = Runtime.getRuntime().totalMemory();
                    long maxMemory = Runtime.getRuntime().maxMemory();
                    long memoryUsed = totalMemory-freeMemory;

                    jLabel32.setText((memoryUsed/(1024*1024))+" of "+(maxMemory/(1024*1024))+" MB used");
                    //jProgressBar2.setValue((int)(100*((memoryUsed/(1024*1024))/(maxMemory/(1024*1024)))));
                }
            };

            memory = new Timer(1000, updateTimeAction);
            memory.start();
    }

    
    public void init(){

        pic_Drawer pic = new pic_Drawer(s);
        pic.CreateTRImage(new String[]{""}, false);
        B = pic.rendImage;
        TP = new Text_JPanel(pic);

        Format.setMinimumFractionDigits(2);
        Format2.setMinimumFractionDigits(3);
    }
 
    
     private void initTree(cluster[] clusters){
        root = new DefaultMutableTreeNode("Cluster Output");
        DefaultMutableTreeNode[] clusts = new DefaultMutableTreeNode[clusters.length];

        clustIndex = 1;

        int singleIndex=1;

        Arrays.sort(clusters);

        for(int i = 0; i < clusters.length; i++){
            //System.out.println("cluster "+i);
            if(clusters[i].ids.size() < 1) continue;
            
            int size = clusters[i].ids.size();

            //adjust size for cluster members below confidence threshold
            if(s.confidence){
                for(int k = 0; k < clusters[i].ids.size(); k++){
                    int id = Integer.valueOf(clusters[i].ids.get(k).toString());
                    if(s.input[id].getConf() < confThreshold) size--;
                }
            }
            
            DefaultMutableTreeNode[] members = new DefaultMutableTreeNode[size];
            clusts[i] = (c[i].ids.size()>1) ? new DefaultMutableTreeNode("cluster "+(i+1)+" ("+size+")") :
                                   new DefaultMutableTreeNode("singleton "+(singleIndex++));

            if((c[i].ids.size()>1)) clustIndex++;

            for(int h = 0; h < members.length; h++){
                int id = Integer.valueOf(clusters[i].ids.get(h).toString());

                s.input[id].setClustID(i+1);

                members[h] = new DefaultMutableTreeNode(s.input[id]);
                if(!s.confidence) clusts[i].add(members[h]);
                if(s.confidence && (s.input[id].getConf() >= confThreshold)) clusts[i].add(members[h]);
                
            }
            
            if(clusts[i].getChildCount() > 0) root.add(clusts[i]);
        }
   
    }
    
    
   

    
    
    private void initTreeSelection(){
      jTree1.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
        public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
             // System.out.println(evt.getButton());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                       jTree1.getLastSelectedPathComponent();

            if (node == null) return;	
 
            if(node.isRoot()) return;
            
            
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
 
            TreePath[] paths = jTree1.getSelectionPaths();
            
            if(paths == null) return;
            
            TreeNode parent = (TreeNode) paths[0].getParentPath().getLastPathComponent();
            
            if(model.getRoot() != parent) return;
            
           // if(evt.getButton() == evt.BUTTON1) display((TreeNode)paths[0].getLastPathComponent());
           // else 
             display(paths);
	
          }
        });
 
    }
    
   
    private void display(TreePath[] paths){
        multipleClusters=false;
        ArrayList data = new ArrayList();
        for(int i = 0; i < paths.length; i++){
            MutableTreeNode node = (MutableTreeNode)paths[i].getLastPathComponent();
            if(i>0) {
                data.add(new dataItem(new float[1], "spacer"));
                multipleClusters=true;
            }
            for(int j = 0; j < node.getChildCount(); j++){
                data.add(((dataItem)((DefaultMutableTreeNode)node.getChildAt(j)).getUserObject()));
            }
        }
        dataItem[] d = new dataItem[data.size()];

        for(int j = 0; j < data.size(); j++){
            d[j] = (dataItem)data.get(j);
        }
        
        add(d);
    }
    
     private void display(TreeNode node){
        
        if(node.getParent() != root) return; 
         
        dataItem[] d = new dataItem[node.getChildCount()];

           
        for(int j = 0; j < node.getChildCount(); j++){
                d[j] = (dataItem) ((DefaultMutableTreeNode)node.getChildAt(j)).getUserObject();

         }
            
        add(d);
    }
    
   public void add(dataItem[] d){
        if(d==null) return;
        jTable2.setValueAt(d.length, 0,1);
        jTable2.setValueAt(d[0].getValues().length, 1,1);

        currMin = Float.MAX_VALUE;
        currMax = Float.MIN_VALUE;
            for(int i = 0; i < d.length; i++){
            for(int j = 0; j < d[i].getValues().length; j++){
                if(currMin > d[i].getValues()[j]) currMin = d[i].getValues()[j];
                if(currMax < d[i].getValues()[j]) currMax = d[i].getValues()[j];
            }
        }
        currMin = ((Math.abs(currMin)>0) ? Float.valueOf(Format.format(currMin)) : currMin);
        currMax = ((Math.abs(currMax)>0) ? Float.valueOf(Format.format(currMax)) : currMax);

        jTable2.setValueAt(currMax, 2,1);
        jTable2.setValueAt(currMin, 3,1);

       if(highlightCluster != -1){
           int[] rows = jTree1.getSelectionRows();
           if(rows!=null){
               if(highlightCluster!=rows[0]) highlight = null;
           }
       }else highlight = null;
       
        currSelection = d;
       
        
        if(renderImg!=null) {
            cv.kill();
            if(!scroll) renderImg.stop();
        }
        renderImg = new Thread(cv=new changeView(d, scroll));
        renderImg.start();
        if(renderImgActiveCount==0) renderImgActiveCount=renderImg.activeCount();
    }
    
   
  
   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jFrame1 = new javax.swing.JFrame();
        javax.swing.ImageIcon icon = new javax.swing.ImageIcon(getClass().getResource("/imgs/icon.jpg"));
        jFrame1.setIconImage(icon.getImage());
        jFrame1.setLocationRelativeTo(null);
        jLabel21 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jLabel24 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jPanel17 = new javax.swing.JPanel();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jSlider3 = new javax.swing.JSlider();
        jCheckBox14 = new javax.swing.JCheckBox();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jSeparator5 = new javax.swing.JSeparator();
        jSeparator6 = new javax.swing.JSeparator();
        jLabel42 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jTextField18 = new javax.swing.JTextField();
        jCheckBox25 = new javax.swing.JCheckBox();
        jCheckBox26 = new javax.swing.JCheckBox();
        jCheckBox27 = new javax.swing.JCheckBox();
        jCheckBox28 = new javax.swing.JCheckBox();
        jCheckBox29 = new javax.swing.JCheckBox();
        jSeparator15 = new javax.swing.JSeparator();
        jSeparator16 = new javax.swing.JSeparator();
        jComboBox13 = new javax.swing.JComboBox();
        jComboBox16 = new javax.swing.JComboBox();
        jCheckBox24 = new javax.swing.JCheckBox();
        jPanel28 = new javax.swing.JPanel();
        jCheckBox9 = new javax.swing.JCheckBox();
        jCheckBox13 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox18 = new javax.swing.JCheckBox();
        jCheckBox23 = new javax.swing.JCheckBox();
        jCheckBox17 = new javax.swing.JCheckBox();
        jCheckBox30 = new javax.swing.JCheckBox();
        jFrame2 = new javax.swing.JFrame();
        jFrame2.setIconImage(icon.getImage());
        jLabel4 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jButton7 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jFrame3 = new javax.swing.JFrame();
        jFrame3.setIconImage(icon.getImage());
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jButton23 = new javax.swing.JButton();
        jLabel36 = new javax.swing.JLabel();
        jButton21 = new javax.swing.JButton();
        jButton22 = new javax.swing.JButton();
        jLabel35 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jButton24 = new javax.swing.JButton();
        jLabel33 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jButton25 = new javax.swing.JButton();
        jButton26 = new javax.swing.JButton();
        jLabel38 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jFrame4 = new javax.swing.JFrame();
        jFrame4.setIconImage(icon.getImage());
        jLabel39 = new javax.swing.JLabel();
        jComboBox9 = new javax.swing.JComboBox();
        jComboBox10 = new javax.swing.JComboBox();
        jLabel40 = new javax.swing.JLabel();
        jButton27 = new javax.swing.JButton();
        jFrame5 = new javax.swing.JFrame();
        jLabel45 = new javax.swing.JLabel();
        jComboBox17 = new javax.swing.JComboBox();
        jLabel52 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jComboBox18 = new javax.swing.JComboBox();
        jLabel54 = new javax.swing.JLabel();
        jComboBox19 = new javax.swing.JComboBox();
        jLabel55 = new javax.swing.JLabel();
        jComboBox20 = new javax.swing.JComboBox();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jComboBox21 = new javax.swing.JComboBox();
        jPanel23 = new javax.swing.JPanel();
        jButton28 = new javax.swing.JButton();
        jLabel59 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel24 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jCheckBox6 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jLabel16 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel8 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel6.setVisible(false);
        jTextField10 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jSeparator7 = new javax.swing.JSeparator();
        jSeparator8 = new javax.swing.JSeparator();
        jComboBox7 = new javax.swing.JComboBox();
        jComboBox8 = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();

        jPanel5.setVisible(false);
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        javax.swing.SpinnerModel model1 = new javax.swing.SpinnerNumberModel(Runtime.getRuntime().availableProcessors(), 1, Runtime.getRuntime().availableProcessors(), 1);
        jSpinner1 = new javax.swing.JSpinner(model1);
        jLabel28 = new javax.swing.JLabel();
        jComboBox6 = new javax.swing.JComboBox();
        jLabel48 = new javax.swing.JLabel();
        jComboBox11 = new javax.swing.JComboBox();
        jPanel12 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JSeparator();
        jButton16 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jLabel20 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox();
        jCheckBox16 = new javax.swing.JCheckBox();
        jLabel46 = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox();
        jPanel18 = new javax.swing.JPanel();
        jCheckBox15 = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        jComboBox5 = new javax.swing.JComboBox();
        jButton20 = new javax.swing.JButton();
        jLabel31 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        jPanel19 = new javax.swing.JPanel();
        jCheckBox11 = new javax.swing.JCheckBox();
        jPanel25 = new javax.swing.JPanel();
        jPanel25.setVisible(false);
        jLabel47 = new javax.swing.JLabel();
        jSeparator11 = new javax.swing.JSeparator();
        jLabel49 = new javax.swing.JLabel();
        jSeparator12 = new javax.swing.JSeparator();
        jSeparator12.setVisible(false);
        jPanel26 = new javax.swing.JPanel();

        jPanel26.setVisible(false);
        jLabel50 = new javax.swing.JLabel();
        jLabel51 = new javax.swing.JLabel();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jComboBox12 = new javax.swing.JComboBox();
        jPanel27 = new javax.swing.JPanel();
        jPanel27.setVisible(false);
        jTextField17 = new javax.swing.JTextField();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox19 = new javax.swing.JCheckBox();
        jCheckBox20 = new javax.swing.JCheckBox();
        jCheckBox21 = new javax.swing.JCheckBox();
        jCheckBox22 = new javax.swing.JCheckBox();
        jSeparator13 = new javax.swing.JSeparator();
        jSeparator14 = new javax.swing.JSeparator();
        jComboBox14 = new javax.swing.JComboBox();
        jComboBox15 = new javax.swing.JComboBox();
        jPanel11 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton11 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jButton12 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree(root);
        AdjustmentListener listener = new MyAdjustmentListener();
        jScrollPane2 = new javax.swing.JScrollPane(TP);
        jScrollPane2.getHorizontalScrollBar().addAdjustmentListener(listener);
        jScrollPane2.getVerticalScrollBar().addAdjustmentListener(listener);  
        jButton19 = new javax.swing.JButton();
        jLabel58 = new javax.swing.JLabel();
        jLabel58.setVisible(false);
        jTextField19 = new javax.swing.JTextField();
        jTextField19.setVisible(false);
        jComboBox1 = new javax.swing.JComboBox();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel8 = new javax.swing.JLabel();
        jButton8 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jLabel32 = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jMenuBar4 = new javax.swing.JMenuBar();
        jMenu10 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();
        jMenuItem10.setVisible(false);
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem24 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem30 = new javax.swing.JMenuItem();
        jMenuItem30.setVisible(false);
        jMenu4 = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();
        jMenu12 = new javax.swing.JMenu();
        jMenuItem20 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem22 = new javax.swing.JMenuItem();
        jMenuItem23 = new javax.swing.JMenuItem();
        jMenu13 = new javax.swing.JMenu();
        jMenuItem28 = new javax.swing.JMenuItem();
        jMenuItem21 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem4 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem7 = new javax.swing.JCheckBoxMenuItem();
        jMenu15 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jCheckBoxMenuItem8 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem9 = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItem9.setVisible(false);
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenu14 = new javax.swing.JMenu();
        jMenu14.setVisible(false);
        jMenuItem25 = new javax.swing.JMenuItem();
        jMenuItem25.setVisible(false);
        jMenuItem27 = new javax.swing.JMenuItem();
        jMenuItem27.setVisible(false);
        jMenu16 = new javax.swing.JMenu();
        jMenuItem29 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem11.setVisible(false);
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();

        jFrame1.setTitle("Image Settings");
        jFrame1.setAlwaysOnTop(true);
        jFrame1.getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("Contrast:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(1, 7, 0, 4);
        jFrame1.getContentPane().add(jLabel21, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("Adjust Height");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jFrame1.getContentPane().add(jLabel1, gridBagConstraints);

        jSlider1.setMinorTickSpacing(1);
        jSlider1.setValue(71);
        jSlider1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jSlider1MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider1MouseReleased(evt);
            }
        });
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        jFrame1.getContentPane().add(jSlider1, gridBagConstraints);

        jSlider2.setMinorTickSpacing(1);
        jSlider2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider2MouseReleased(evt);
            }
        });
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        jFrame1.getContentPane().add(jSlider2, gridBagConstraints);

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel24.setForeground(new java.awt.Color(51, 51, 51));
        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText("Zoom Factor");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 0);
        jFrame1.getContentPane().add(jLabel24, gridBagConstraints);

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("10");
        jTextField1.setMinimumSize(new java.awt.Dimension(20, 20));
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField1MousePressed(evt);
            }
        });
        jTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField1FocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 28;
        gridBagConstraints.insets = new java.awt.Insets(17, 5, 5, 19);
        jFrame1.getContentPane().add(jTextField1, gridBagConstraints);

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField11.setText(".500");
        jTextField11.setMinimumSize(new java.awt.Dimension(20, 20));
        jTextField11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField11MousePressed(evt);
            }
        });
        jTextField11.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField11FocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 21;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 4, 5, 20);
        jFrame1.getContentPane().add(jTextField11, gridBagConstraints);

        jTextField12.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField12.setText("1");
        jTextField12.setMinimumSize(new java.awt.Dimension(20, 20));
        jTextField12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTextField12MousePressed(evt);
            }
        });
        jTextField12.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jTextField12FocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 31;
        gridBagConstraints.insets = new java.awt.Insets(12, 10, 5, 25);
        jFrame1.getContentPane().add(jTextField12, gridBagConstraints);

        jPanel17.setLayout(new java.awt.GridBagLayout());

        jButton17.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton17.setText("Save");
        jButton17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton17MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(6, 25, 16, 25);
        jPanel17.add(jButton17, gridBagConstraints);

        jButton18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton18.setText("Close");
        jButton18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton18MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 16, 10);
        jPanel17.add(jButton18, gridBagConstraints);

        jButton6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton6.setText("Update");
        jButton6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton6MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(6, 10, 16, 0);
        jPanel17.add(jButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 19;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jFrame1.getContentPane().add(jPanel17, gridBagConstraints);

        jSlider3.setMinimum(1);
        jSlider3.setMinorTickSpacing(1);
        jSlider3.setValue(10);
        jSlider3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider3MouseReleased(evt);
            }
        });
        jSlider3.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider3StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 16);
        jFrame1.getContentPane().add(jSlider3, gridBagConstraints);

        jCheckBox14.setText("Manually adjust range for contrast");
        jCheckBox14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox14ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(17, 31, 0, 17);
        jFrame1.getContentPane().add(jCheckBox14, gridBagConstraints);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("Minimum:");
        jLabel25.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 15, 7, 0);
        jFrame1.getContentPane().add(jLabel25, gridBagConstraints);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("Maximum:");
        jLabel26.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        jFrame1.getContentPane().add(jLabel26, gridBagConstraints);

        jTextField13.setText("0");
        jTextField13.setEnabled(false);
        jTextField13.setMaximumSize(new java.awt.Dimension(20, 2147483647));
        jTextField13.setMinimumSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        jFrame1.getContentPane().add(jTextField13, gridBagConstraints);

        jTextField14.setText("0");
        jTextField14.setEnabled(false);
        jTextField14.setMaximumSize(new java.awt.Dimension(20, 2147483647));
        jTextField14.setMinimumSize(new java.awt.Dimension(20, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 40;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 10);
        jFrame1.getContentPane().add(jTextField14, gridBagConstraints);

        jSeparator5.setFocusable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(19, 11, 0, 11);
        jFrame1.getContentPane().add(jSeparator5, gridBagConstraints);

        jSeparator6.setFocusable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 18;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 10, 11);
        jFrame1.getContentPane().add(jSeparator6, gridBagConstraints);

        jLabel42.setFont(new java.awt.Font("Tahoma", 1, 12));
        jLabel42.setText("Dimensions:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        jFrame1.getContentPane().add(jLabel42, gridBagConstraints);

        jLabel44.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel44.setText("Heat Map");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 2);
        jFrame1.getContentPane().add(jLabel44, gridBagConstraints);

        jSeparator10.setFocusable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 11, 10, 11);
        jFrame1.getContentPane().add(jSeparator10, gridBagConstraints);

        jTabbedPane2.setForeground(new java.awt.Color(0, 102, 255));
        jTabbedPane2.setFont(new java.awt.Font("Tahoma", 1, 12));

        jPanel30.setLayout(new java.awt.GridBagLayout());

        jTextField18.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField18.setText("99");
        jTextField18.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 23;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 7, 40);
        jPanel30.add(jTextField18, gridBagConstraints);

        jCheckBox25.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox25.setText("1. Log2 Scaling");
        jCheckBox25.setToolTipText("Take logarithm base 2 of every value");
        jCheckBox25.setEnabled(false);
        jCheckBox25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox25MouseReleased(evt);
            }
        });
        jCheckBox25.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox25ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 5, 22);
        jPanel30.add(jCheckBox25, gridBagConstraints);

        jCheckBox26.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox26.setText("2. Range [0,x]   x =");
        jCheckBox26.setToolTipText("Normalize every column into range [0,x]");
        jCheckBox26.setEnabled(false);
        jCheckBox26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox26MouseReleased(evt);
            }
        });
        jCheckBox26.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox26ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 0);
        jPanel30.add(jCheckBox26, gridBagConstraints);

        jCheckBox27.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox27.setText("2. Unit Variance");
        jCheckBox27.setToolTipText("Normalize every column to zero mean and standard deviation of one");
        jCheckBox27.setEnabled(false);
        jCheckBox27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox27MouseReleased(evt);
            }
        });
        jCheckBox27.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox27ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 2, 22);
        jPanel30.add(jCheckBox27, gridBagConstraints);

        jCheckBox28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox28.setText("3. Median Center");
        jCheckBox28.setToolTipText("Subtract median of row/column from every value in row/column");
        jCheckBox28.setEnabled(false);
        jCheckBox28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox28MouseReleased(evt);
            }
        });
        jCheckBox28.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox28ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel30.add(jCheckBox28, gridBagConstraints);

        jCheckBox29.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox29.setText("4. Sum Squares = 1 ");
        jCheckBox29.setToolTipText("Normalize every row/column such that the sum of each value squared equals one");
        jCheckBox29.setEnabled(false);
        jCheckBox29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox29MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 11, 0);
        jPanel30.add(jCheckBox29, gridBagConstraints);

        jSeparator15.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator15.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 13, 0, 11);
        jPanel30.add(jSeparator15, gridBagConstraints);

        jSeparator16.setForeground(new java.awt.Color(102, 102, 102));
        jSeparator16.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 13, 1, 11);
        jPanel30.add(jSeparator16, gridBagConstraints);

        jComboBox13.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        jComboBox13.setSelectedIndex(2);
        jComboBox13.setEnabled(false);
        jComboBox13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox13ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 9);
        jPanel30.add(jComboBox13, gridBagConstraints);

        jComboBox16.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        jComboBox16.setEnabled(false);
        jComboBox16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox16ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 9);
        jPanel30.add(jComboBox16, gridBagConstraints);

        jCheckBox24.setFont(new java.awt.Font("Tahoma", 1, 14));
        jCheckBox24.setText("Display Original Data");
        jCheckBox24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox24MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 7;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 7, 22);
        jPanel30.add(jCheckBox24, gridBagConstraints);

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(37, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Normalization", jPanel29);

        jPanel28.setLayout(new java.awt.GridBagLayout());

        jCheckBox9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox9.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox9.setText("Black Background");
        jCheckBox9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox9MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 68;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox9, gridBagConstraints);

        jCheckBox13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox13.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox13.setText("Hide Heat Map Confidence Bar");
        jCheckBox13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jCheckBox13MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox13MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox13, gridBagConstraints);

        jCheckBox12.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox12.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox12.setText("Hide Heat Map Row Labels");
        jCheckBox12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox12MouseReleased(evt);
            }
        });
        jCheckBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox12ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 24;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox12, gridBagConstraints);

        jCheckBox10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox10.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox10.setText("Hide Heat Map Column Labels");
        jCheckBox10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox10MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox10, gridBagConstraints);

        jCheckBox18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox18.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox18.setText("Hide Cluster Row Separators");
        jCheckBox18.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox18MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 14;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox18, gridBagConstraints);

        jCheckBox23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox23.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox23.setText("Hide Cluster Column Separators");
        jCheckBox23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jCheckBox23MousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox23MouseReleased(evt);
            }
        });
        jCheckBox23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox23ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox23, gridBagConstraints);

        jCheckBox17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox17.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox17.setText("Hide Heat Map Color Bar");
        jCheckBox17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox17MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 5, 70);
        jPanel28.add(jCheckBox17, gridBagConstraints);

        jCheckBox30.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox30.setForeground(new java.awt.Color(51, 51, 51));
        jCheckBox30.setText("Sort by Decreasing Variance");
        jCheckBox30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jCheckBox30MouseReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 70, 0, 70);
        jPanel28.add(jCheckBox30, gridBagConstraints);

        jTabbedPane2.addTab("Display Options", jPanel28);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jFrame1.getContentPane().add(jTabbedPane2, gridBagConstraints);

        jFrame2.setTitle("Search");
        jFrame2.getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Choose Identifier");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        jFrame2.getContentPane().add(jLabel4, gridBagConstraints);

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Null" }));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 15, 0, 10);
        jFrame2.getContentPane().add(jComboBox3, gridBagConstraints);

        jLabel5.setText("OR");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jFrame2.getContentPane().add(jLabel5, gridBagConstraints);

        jLabel6.setText("Type Identifier");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jFrame2.getContentPane().add(jLabel6, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 49;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jFrame2.getContentPane().add(jTextField3, gridBagConstraints);

        jButton7.setText("Submit");
        jButton7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton7MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(19, 0, 10, 0);
        jFrame2.getContentPane().add(jButton7, gridBagConstraints);

        jLabel2.setText("jLabel2");

        jFrame3.setTitle("Version and Online Help");
        jFrame3.setResizable(false);

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel22.setText("AutoSOME build: 1.0");

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel23.setText("http://jimcooperlab.mcdb.ucsb.edu/autosome");
        jLabel23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel23MousePressed(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel27.setText("GUI build: 10-16-09");

        jPanel22.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(51, 153, 255)));

        jButton23.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton23.setText("Go");
        jButton23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton23MousePressed(evt);
            }
        });

        jLabel36.setText("Terms of Use");

        jButton21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton21.setText("Go");
        jButton21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton21MousePressed(evt);
            }
        });

        jButton22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton22.setText("Go");
        jButton22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton22MousePressed(evt);
            }
        });

        jLabel35.setText("Manual");

        jLabel34.setText("Tutorial");

        jButton24.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton24.setText("Go");
        jButton24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton24MousePressed(evt);
            }
        });

        jLabel33.setText("AutoSOME Website");

        jLabel37.setText("Fuzzy Cluster Networks");

        jButton25.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton25.setText("Go");
        jButton25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton25MousePressed(evt);
            }
        });

        jButton26.setFont(new java.awt.Font("Tahoma", 1, 11));
        jButton26.setText("Go");
        jButton26.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton26MousePressed(evt);
            }
        });

        jLabel38.setText("Example Datasets");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33)
                            .addComponent(jLabel34)
                            .addComponent(jLabel37))
                        .addGap(69, 69, 69)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton22)
                            .addComponent(jButton23)
                            .addComponent(jButton25)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel22Layout.createSequentialGroup()
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton26)
                            .addComponent(jButton21))))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton22)
                    .addComponent(jLabel33))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton23)
                    .addComponent(jLabel34))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton25)
                    .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton26)
                    .addComponent(jLabel38))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(jButton21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton24)
                    .addComponent(jLabel36))
                .addContainerGap())
        );

        jLabel41.setText("If you use AutoSOME for your research, please cite:");

        jLabel43.setText("Newman and Cooper, BMC Bioinformatics 2010, 11:117");

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                    .addComponent(jLabel41)
                    .addComponent(jLabel43))
                .addContainerGap())
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel41)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addContainerGap())
        );

        jFrame4.setTitle("Missing Values");
        jFrame4.getContentPane().setLayout(new java.awt.GridBagLayout());

        jLabel39.setText("Fill Missing Values With:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 12, 0, 7);
        jFrame4.getContentPane().add(jLabel39, gridBagConstraints);

        jComboBox9.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Means", "Medians" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jFrame4.getContentPane().add(jComboBox9, gridBagConstraints);

        jComboBox10.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 14);
        jFrame4.getContentPane().add(jComboBox10, gridBagConstraints);

        jLabel40.setText("Of");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(18, 7, 7, 7);
        jFrame4.getContentPane().add(jLabel40, gridBagConstraints);

        jButton27.setText("OK");
        jButton27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton27MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 15, 27);
        jFrame4.getContentPane().add(jButton27, gridBagConstraints);

        jFrame5.setTitle("Quick Settings Helper");

        jLabel45.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel45.setForeground(new java.awt.Color(51, 51, 51));
        jLabel45.setText("1) Are you clustering a microarray data set?");

        jComboBox17.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "yes", "no" }));
        jComboBox17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox17ActionPerformed(evt);
            }
        });

        jLabel52.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel52.setForeground(new java.awt.Color(51, 51, 51));
        jLabel52.setText("4) Would you like to cluster genes, arrays, or both?");

        jScrollPane6.setBackground(new java.awt.Color(204, 204, 204));

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(4);
        jScrollPane6.setViewportView(jTextArea1);

        jComboBox18.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "genes", "arrays", "both" }));

        jLabel54.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel54.setForeground(new java.awt.Color(51, 51, 51));
        jLabel54.setText("5) Is this a first-pass run or a final clustering?");

        jComboBox19.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "first-pass", "first-pass (very fast)", "final result" }));

        jLabel55.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel55.setForeground(new java.awt.Color(51, 51, 51));
        jLabel55.setText("2) Are your data already log2 scaled?");

        jComboBox20.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "no", "yes" }));

        jLabel56.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel56.setForeground(new java.awt.Color(102, 102, 102));
        jLabel56.setText("(or comparably large and noisy data set?)");

        jLabel57.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel57.setForeground(new java.awt.Color(51, 51, 51));
        jLabel57.setText("3) Is your data set filtered (e.g. by fold change)?");

        jComboBox21.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "yes", "no" }));

        jPanel23.setLayout(new java.awt.GridBagLayout());

        jButton28.setFont(new java.awt.Font("Tahoma", 1, 12));
        jButton28.setForeground(new java.awt.Color(0, 102, 255));
        jButton28.setText("Set Parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        jPanel23.add(jButton28, gridBagConstraints);

        javax.swing.GroupLayout jFrame5Layout = new javax.swing.GroupLayout(jFrame5.getContentPane());
        jFrame5.getContentPane().setLayout(jFrame5Layout);
        jFrame5Layout.setHorizontalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame5Layout.createSequentialGroup()
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFrame5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFrame5Layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jLabel56))
                            .addGroup(jFrame5Layout.createSequentialGroup()
                                .addComponent(jLabel45, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                                .addGap(39, 39, 39)
                                .addComponent(jComboBox17, 0, 89, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFrame5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jFrame5Layout.createSequentialGroup()
                                .addComponent(jLabel52)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(jFrame5Layout.createSequentialGroup()
                                .addComponent(jLabel55, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                                .addGap(7, 7, 7))
                            .addComponent(jLabel57))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox18, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox20, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jFrame5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
                            .addGroup(jFrame5Layout.createSequentialGroup()
                                .addComponent(jLabel54)
                                .addGap(18, 18, 18)
                                .addComponent(jComboBox19, 0, 109, Short.MAX_VALUE))))
                    .addGroup(jFrame5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jFrame5Layout.setVerticalGroup(
            jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFrame5Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel45)
                    .addComponent(jComboBox17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel56)
                .addGap(28, 28, 28)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(jComboBox20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel52)
                    .addComponent(jComboBox18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jFrame5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel54)
                    .addComponent(jComboBox19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel59.setText("jLabel59");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("AutoSOME GUI (build 10-16-09)");
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jTabbedPane1.setFont(new java.awt.Font("Tahoma", 1, 12));
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jScrollPane5.setBorder(null);

        jPanel2.setFont(new java.awt.Font("Tahoma", 1, 11));

        jPanel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel7.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(jTextField8, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel7.add(jTextField9, gridBagConstraints);

        jButton9.setText("...");
        jButton9.setToolTipText("Launch file browser");
        jButton9.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton9MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        jPanel7.add(jButton9, gridBagConstraints);

        jButton10.setText("...");
        jButton10.setToolTipText("Launch file browser");
        jButton10.setMargin(new java.awt.Insets(2, 2, 2, 2));
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton10MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        jPanel7.add(jButton10, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel14.setText(" Input File");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        jPanel7.add(jLabel14, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel15.setText("Output Directory");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        jPanel7.add(jLabel15, gridBagConstraints);

        jCheckBox6.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox6.setSelected(true);
        jCheckBox6.setText("Autodetect Column Labels");
        jCheckBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jCheckBox6, gridBagConstraints);

        jCheckBox7.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox7.setText("My Data Has Column Labels");
        jCheckBox7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox7ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel7.add(jCheckBox7, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel16.setForeground(new java.awt.Color(21, 113, 204));
        jLabel16.setText("Input and Output");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 2);
        jPanel7.add(jLabel16, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        jPanel7.add(jSeparator1, gridBagConstraints);

        jPanel6.setLayout(new java.awt.GridBagLayout());

        jTextField10.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField10.setText("99");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 23;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 7, 68);
        jPanel6.add(jTextField10, gridBagConstraints);

        jCheckBox1.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox1.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox1.setText("1. Log2 Scaling");
        jCheckBox1.setToolTipText("Take logarithm base 2 of every value");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 5, 50);
        jPanel6.add(jCheckBox1, gridBagConstraints);

        jCheckBox2.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox2.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox2.setText("2. Range [0,x]   x =");
        jCheckBox2.setToolTipText("Normalize every column into range [0,x]");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 0);
        jPanel6.add(jCheckBox2, gridBagConstraints);

        jCheckBox3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox3.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox3.setText("2. Unit Variance");
        jCheckBox3.setToolTipText("Normalize every column to zero mean and standard deviation of one");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 2, 50);
        jPanel6.add(jCheckBox3, gridBagConstraints);

        jCheckBox4.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox4.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox4.setText("3. Median Center");
        jCheckBox4.setToolTipText("Subtract median of row/column from every value in row/column");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel6.add(jCheckBox4, gridBagConstraints);

        jCheckBox5.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox5.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox5.setText("4. Sum Squares = 1 ");
        jCheckBox5.setToolTipText("Normalize every row/column such that the sum of each value squared equals one");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 11, 0);
        jPanel6.add(jCheckBox5, gridBagConstraints);

        jSeparator7.setForeground(new java.awt.Color(102, 102, 102));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 13, 0, 39);
        jPanel6.add(jSeparator7, gridBagConstraints);

        jSeparator8.setForeground(new java.awt.Color(102, 102, 102));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 13, 1, 39);
        jPanel6.add(jSeparator8, gridBagConstraints);

        jComboBox7.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        jComboBox7.setSelectedIndex(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 37);
        jPanel6.add(jComboBox7, gridBagConstraints);

        jComboBox8.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 37);
        jPanel6.add(jComboBox8, gridBagConstraints);

        jPanel5.setLayout(new java.awt.GridBagLayout());

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel9.setForeground(new java.awt.Color(70, 70, 70));
        jLabel9.setText("No. Ensemble Runs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 29;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 49, 0, 0);
        jPanel5.add(jLabel9, gridBagConstraints);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel10.setForeground(new java.awt.Color(70, 70, 70));
        jLabel10.setText("P-value Threshold");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 49, 0, 0);
        jPanel5.add(jLabel10, gridBagConstraints);

        jLabel13.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel13.setForeground(new java.awt.Color(70, 70, 70));
        jLabel13.setText("No. CPUs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 49, 5, 0);
        jPanel5.add(jLabel13, gridBagConstraints);

        jTextField4.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField4.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 16, 5, 27);
        jPanel5.add(jTextField4, gridBagConstraints);

        jTextField5.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField5.setText("0.1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 16, 5, 27);
        jPanel5.add(jTextField5, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 16, 10, 27);
        jPanel5.add(jSpinner1, gridBagConstraints);

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel28.setForeground(new java.awt.Color(70, 70, 70));
        jLabel28.setText("Running Mode");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 49, 0, 0);
        jPanel5.add(jLabel28, gridBagConstraints);

        jComboBox6.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Precision", "Normal", "Speed" }));
        jComboBox6.setToolTipText("Speed/Normal/Precision=250/500/1000 SOM iterations and 16x16/32x32/64x64 cartogram resolution");
        jComboBox6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox6ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 32, 6, 26);
        jPanel5.add(jComboBox6, gridBagConstraints);

        jLabel48.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel48.setForeground(new java.awt.Color(70, 70, 70));
        jLabel48.setText("Cluster Analysis");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 49, 2, 0);
        jPanel5.add(jLabel48, gridBagConstraints);

        jComboBox11.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        jComboBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox11ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(8, 32, 2, 26);
        jPanel5.add(jComboBox11, gridBagConstraints);

        jPanel12.setLayout(new java.awt.GridBagLayout());

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel19.setForeground(new java.awt.Color(21, 113, 204));
        jLabel19.setText("Advanced Fields");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(7, 35, 0, 4);
        jPanel12.add(jLabel19, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 5, 0);
        jPanel12.add(jSeparator3, gridBagConstraints);

        jButton16.setText("Show");
        jButton16.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton16MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(7, 3, 0, 24);
        jPanel12.add(jButton16, gridBagConstraints);

        jPanel13.setVisible(false);
        jPanel13.setLayout(new java.awt.GridBagLayout());

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Fuzzy Cluster Networks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11), new java.awt.Color(102, 102, 255))); // NOI18N
        jPanel14.setLayout(new java.awt.GridBagLayout());

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel20.setText("Distance Metric");
        jLabel20.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 4, 0, 6);
        jPanel14.add(jLabel20, gridBagConstraints);

        jComboBox4.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Euclidean Distance", "Uncentered Correlation", "Pearson's Correlation" }));
        jComboBox4.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 13, 0);
        jPanel14.add(jComboBox4, gridBagConstraints);

        jCheckBox16.setText("Unit Variance Normalize");
        jCheckBox16.setToolTipText("Perform unit variance normalization of distance matrix columns");
        jCheckBox16.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel14.add(jCheckBox16, gridBagConstraints);

        jLabel46.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Column Clustering");
        jLabel46.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 9, 0);
        jPanel14.add(jLabel46, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(19, 41, 0, 4);
        jPanel13.add(jPanel14, gridBagConstraints);

        jPanel20.setVisible(false);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Cartogram", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel15.setLayout(new java.awt.GridBagLayout());

        jLabel30.setText("Resolution:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 9);
        jPanel15.add(jLabel30, gridBagConstraints);

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "64x64", "32x32", "16x16", "128x128", "256x256" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 4, 0);
        jPanel15.add(jComboBox2, gridBagConstraints);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "SOM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel18.setMaximumSize(new java.awt.Dimension(300, 130));
        jPanel18.setLayout(new java.awt.GridBagLayout());

        jCheckBox15.setText("Use Square Topology (Circular=Default)");
        jCheckBox15.setToolTipText("Choose SOM node topology");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 7, 4);
        jPanel18.add(jCheckBox15, gridBagConstraints);

        jLabel11.setText("Maximum Grid Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        jPanel18.add(jLabel11, gridBagConstraints);

        jTextField7.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField7.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.insets = new java.awt.Insets(6, 17, 5, 20);
        jPanel18.add(jTextField7, gridBagConstraints);

        jLabel12.setText("Minimum Grid Length");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 41);
        jPanel18.add(jLabel12, gridBagConstraints);

        jTextField6.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField6.setText("30");
        jTextField6.setToolTipText("Decreasing this quantity will decrease cluster resolving ability but also increase AutoSOME speed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.insets = new java.awt.Insets(0, 17, 0, 20);
        jPanel18.add(jTextField6, gridBagConstraints);

        jLabel29.setText("Training Iterations");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        jPanel18.add(jLabel29, gridBagConstraints);

        jComboBox5.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1000", "500", "250" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 9, 0, 19);
        jPanel18.add(jComboBox5, gridBagConstraints);

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel20Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
                    .addComponent(jPanel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        jPanel13.add(jPanel20, gridBagConstraints);

        jButton20.setText("Show");
        jButton20.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton20MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 24);
        jPanel13.add(jButton20, gridBagConstraints);

        jLabel31.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel31.setForeground(new java.awt.Color(102, 102, 255));
        jLabel31.setText("Algorithm Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 53, 0, 0);
        jPanel13.add(jLabel31, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(10, 6, 10, 6);
        jPanel13.add(jSeparator9, gridBagConstraints);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Memory", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel19.setLayout(new java.awt.GridBagLayout());

        jCheckBox11.setText("Write Ensemble Runs to Disk");
        jCheckBox11.setToolTipText("Store intermediate ensemble runs in temporary folder to decrease memory consumption");
        jCheckBox11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox11ActionPerformed(evt);
            }
        });
        jPanel19.add(jCheckBox11, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 41, 10, 4);
        jPanel13.add(jPanel19, gridBagConstraints);

        jPanel25.setLayout(new java.awt.GridBagLayout());

        jLabel47.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel47.setForeground(new java.awt.Color(21, 113, 204));
        jLabel47.setText("Basic Fields (Columns)");
        jLabel47.setToolTipText("Core AutoSOME parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        jPanel25.add(jLabel47, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 103;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 0);
        jPanel25.add(jSeparator11, gridBagConstraints);

        jLabel49.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel49.setForeground(new java.awt.Color(21, 113, 204));
        jLabel49.setText("Input Adjustment (Columns)");
        jLabel49.setToolTipText("Scale/normalize your input for improved clustering (operations are executed top to bottom)");
        jLabel49.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel25.add(jLabel49, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 39;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel25.add(jSeparator12, gridBagConstraints);

        jPanel26.setForeground(new java.awt.Color(70, 70, 70));
        jPanel26.setLayout(new java.awt.GridBagLayout());

        jLabel50.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel50.setForeground(new java.awt.Color(70, 70, 70));
        jLabel50.setText("No. Ensemble Runs");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 34;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(1, 49, 0, 0);
        jPanel26.add(jLabel50, gridBagConstraints);

        jLabel51.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel51.setForeground(new java.awt.Color(70, 70, 70));
        jLabel51.setText("P-value Threshold");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 49, 5, 0);
        jPanel26.add(jLabel51, gridBagConstraints);

        jTextField15.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField15.setText("50");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 12, 5, 43);
        jPanel26.add(jTextField15, gridBagConstraints);

        jTextField16.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField16.setText("0.1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 12, 10, 43);
        jPanel26.add(jTextField16, gridBagConstraints);

        jLabel53.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel53.setForeground(new java.awt.Color(70, 70, 70));
        jLabel53.setText("Running Mode");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.insets = new java.awt.Insets(4, 49, 0, 1);
        jPanel26.add(jLabel53, gridBagConstraints);

        jComboBox12.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Precision", "Normal", "Speed" }));
        jComboBox12.setSelectedIndex(1);
        jComboBox12.setToolTipText("Speed/Normal/Precision=250/500/1000 SOM iterations and 16x16/32x32/64x64 cartogram resolution");
        jComboBox12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox12ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(6, 31, 6, 42);
        jPanel26.add(jComboBox12, gridBagConstraints);

        jPanel27.setLayout(new java.awt.GridBagLayout());

        jTextField17.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField17.setText("99");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 23;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 7, 68);
        jPanel27.add(jTextField17, gridBagConstraints);

        jCheckBox8.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox8.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox8.setText("1. Log2 Scaling");
        jCheckBox8.setToolTipText("Take logarithm base 2 of every value");
        jCheckBox8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox8ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 5, 50);
        jPanel27.add(jCheckBox8, gridBagConstraints);

        jCheckBox19.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox19.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox19.setText("2. Range [0,x]   x =");
        jCheckBox19.setToolTipText("Normalize every column into range [0,x]");
        jCheckBox19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox19ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 3, 0);
        jPanel27.add(jCheckBox19, gridBagConstraints);

        jCheckBox20.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox20.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox20.setText("2. Unit Variance");
        jCheckBox20.setToolTipText("Normalize every column to zero mean and standard deviation of one");
        jCheckBox20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox20ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(1, 10, 2, 50);
        jPanel27.add(jCheckBox20, gridBagConstraints);

        jCheckBox21.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox21.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox21.setText("3. Median Center");
        jCheckBox21.setToolTipText("Subtract median of row/column from every value in row/column");
        jCheckBox21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox21ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel27.add(jCheckBox21, gridBagConstraints);

        jCheckBox22.setFont(new java.awt.Font("Tahoma", 1, 11));
        jCheckBox22.setForeground(new java.awt.Color(70, 70, 70));
        jCheckBox22.setText("4. Sum Squares = 1 ");
        jCheckBox22.setToolTipText("Normalize every row/column such that the sum of each value squared equals one");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 11, 0);
        jPanel27.add(jCheckBox22, gridBagConstraints);

        jSeparator13.setForeground(new java.awt.Color(102, 102, 102));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 13, 0, 39);
        jPanel27.add(jSeparator13, gridBagConstraints);

        jSeparator14.setForeground(new java.awt.Color(102, 102, 102));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 13, 1, 39);
        jPanel27.add(jSeparator14, gridBagConstraints);

        jComboBox14.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        jComboBox14.setSelectedIndex(2);
        jComboBox7.setSelectedIndex(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 10, 37);
        jPanel27.add(jComboBox14, gridBagConstraints);

        jComboBox15.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Rows", "Columns", "Both" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 37);
        jPanel27.add(jComboBox15, gridBagConstraints);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                        .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel11.setLayout(new java.awt.GridBagLayout());

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel17.setForeground(new java.awt.Color(21, 113, 204));
        jLabel17.setText("Basic Fields");
        jLabel17.setToolTipText("Core AutoSOME parameters");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 35, 0, 0);
        jPanel11.add(jLabel17, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 0, 4);
        jPanel11.add(jSeparator2, gridBagConstraints);

        jButton11.setText("Show");
        jButton11.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton11MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel11.add(jButton11, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel18.setForeground(new java.awt.Color(21, 113, 204));
        jLabel18.setText("Input Adjustment");
        jLabel18.setToolTipText("Scale/normalize your input for improved clustering (operations are executed top to bottom)");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 70, 0, 0);
        jPanel11.add(jLabel18, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 18;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 4.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        jPanel11.add(jSeparator4, gridBagConstraints);

        jButton12.setText("Show");
        jButton12.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton12MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 23);
        jPanel11.add(jButton12, gridBagConstraints);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 592, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jScrollPane5.setViewportView(jPanel2);

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel24Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Input", jPanel24);

        jButton3.setText("Split");
        jButton3.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton3MousePressed(evt);
            }
        });

        jButton5.setText("Merge");
        jButton5.setMargin(new java.awt.Insets(2, 5, 2, 5));
        jButton5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton5MousePressed(evt);
            }
        });

        jButton2.setText("Update");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton2MousePressed(evt);
            }
        });

        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText("0");

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel3.setForeground(new java.awt.Color(21, 113, 204));
        jLabel3.setText("Confidence:");

        jSplitPane1.setDividerLocation(170);

        jTree1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTree1MousePressed(evt);
            }
        });
        jTree1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTree1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTree1);

        jSplitPane1.setLeftComponent(jScrollPane1);

        jScrollPane2.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                jScrollPane2MouseWheelMoved(evt);
            }
        });
        jScrollPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jScrollPane2MousePressed(evt);
            }
        });
        jScrollPane2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jScrollPane2KeyPressed(evt);
            }
        });
        jSplitPane1.setRightComponent(jScrollPane2);

        jButton19.setText("Reset");
        jButton19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton19MousePressed(evt);
            }
        });

        jLabel58.setFont(new java.awt.Font("Tahoma", 1, 11));
        jLabel58.setForeground(new java.awt.Color(21, 113, 204));
        jLabel58.setText("P-value:");

        jTextField19.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField19.setText("0.1");
        jTextField19.setMinimumSize(new java.awt.Dimension(100, 20));
        jTextField19.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField19KeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField19KeyTyped(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "rows", "columns" }));
        jComboBox1.setVisible(false);
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 115, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel58)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton2)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel58)
                        .addComponent(jLabel3)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton3)
                        .addComponent(jButton5)
                        .addComponent(jButton19)))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Output", jPanel1);

        jPanel9.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Run Progress", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel4.setMinimumSize(new java.awt.Dimension(178, 150));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText(" ");
        jLabel7.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        jPanel4.add(jLabel7, gridBagConstraints);

        jProgressBar1.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 10, 3, 10);
        jPanel4.add(jProgressBar1, gridBagConstraints);

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18));
        jLabel8.setForeground(new java.awt.Color(51, 153, 255));
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("00:00:00");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 5.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel4.add(jLabel8, gridBagConstraints);

        jButton8.setText("Cancel");
        jButton8.setEnabled(false);
        jButton8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton8MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(17, 0, 13, 0);
        jPanel4.add(jButton8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(jPanel4, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "AutoSOME Steps", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jButton13.setFont(new java.awt.Font("Tahoma", 1, 13));
        jButton13.setForeground(new java.awt.Color(230, 0, 0));
        jButton13.setText("1) INPUT   ");
        jButton13.setToolTipText("go to Input window and launch file browser");
        jButton13.setFocusable(false);
        jButton13.setMaximumSize(new java.awt.Dimension(79, 25));
        jButton13.setMinimumSize(new java.awt.Dimension(79, 25));
        jButton13.setPreferredSize(new java.awt.Dimension(79, 25));
        jButton13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton13MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton13MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton13MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.weightx = 0.5;
        jPanel3.add(jButton13, gridBagConstraints);

        jButton14.setFont(new java.awt.Font("Tahoma", 1, 13));
        jButton14.setForeground(new java.awt.Color(0, 160, 0));
        jButton14.setText("2) RUN     ");
        jButton14.setToolTipText("Invoke AutoSOME");
        jButton14.setFocusable(false);
        jButton14.setMaximumSize(new java.awt.Dimension(79, 25));
        jButton14.setMinimumSize(new java.awt.Dimension(79, 25));
        jButton14.setPreferredSize(new java.awt.Dimension(79, 25));
        jButton14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton14MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton14MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton14MousePressed(evt);
            }
        });
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel3.add(jButton14, gridBagConstraints);

        jButton15.setFont(new java.awt.Font("Tahoma", 1, 13));
        jButton15.setForeground(new java.awt.Color(51, 153, 255));
        jButton15.setText("3) OUTPUT");
        jButton15.setToolTipText("Go to output window");
        jButton15.setFocusable(false);
        jButton15.setMaximumSize(new java.awt.Dimension(79, 25));
        jButton15.setMinimumSize(new java.awt.Dimension(79, 25));
        jButton15.setPreferredSize(new java.awt.Dimension(79, 25));
        jButton15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jButton15MouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jButton15MouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jButton15MousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 30;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.5;
        jPanel3.add(jButton15, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel9.add(jPanel3, gridBagConstraints);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Input Data", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"Rows", null},
                {"Columns", null},
                {"Maximum", null},
                {"Minimum", null}
            },
            new String [] {
                "Property", "Value"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setAutoscrolls(false);
        jTable2.setFocusable(false);
        jTable2.setGridColor(new java.awt.Color(204, 204, 204));
        jTable2.setRowMargin(3);
        jTable2.setRowSelectionAllowed(false);
        jTable2.setUpdateSelectionOnSort(false);
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable2MousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel9.add(jPanel16, gridBagConstraints);

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel32.setForeground(new java.awt.Color(102, 102, 102));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Memory Used");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        jPanel9.add(jLabel32, gridBagConstraints);

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Output Files", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "File", "Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setToolTipText("Click on output file to launch in browser");
        jTable1.setAutoscrolls(false);
        jTable1.setFocusable(false);
        jTable1.setRequestFocusEnabled(false);
        jTable1.setRowSelectionAllowed(false);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jTable1MousePressed(evt);
            }
        });
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 83;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 0);
        jPanel9.add(jPanel21, gridBagConstraints);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 710, Short.MAX_VALUE))
                .addContainerGap())
        );

        jMenu10.setText("File");

        jMenuItem1.setText("Open AutoSOME results");
        jMenuItem1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem1MousePressed(evt);
            }
        });
        jMenu10.add(jMenuItem1);

        jMenuItem10.setText("Filter input data");
        jMenuItem10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem10MousePressed(evt);
            }
        });
        jMenu10.add(jMenuItem10);

        jMenuItem7.setText("Missing values");
        jMenuItem7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem7MousePressed(evt);
            }
        });
        jMenu10.add(jMenuItem7);

        jMenu2.setText("Export");

        jMenuItem24.setText("save image");
        jMenuItem24.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem5MousePressed(evt);
            }
        });
        jMenu2.add(jMenuItem24);

        jMenu3.setText("save table");

        jMenuItem5.setText("with original data");
        jMenuItem5.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem5MousePressed2(evt);
            }
        });
        jMenu3.add(jMenuItem5);

        jMenuItem9.setText("current normalization");
        jMenuItem9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem9MousePressed(evt);
            }
        });
        jMenu3.add(jMenuItem9);

        jMenu2.add(jMenu3);

        jMenu10.add(jMenu2);

        jMenuItem6.setText("Reset");
        jMenuItem6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem6MousePressed(evt);
            }
        });
        jMenu10.add(jMenuItem6);

        jMenuItem30.setText("Print Cluster Means");
        jMenuItem30.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem30MousePressed(evt);
            }
        });
        jMenu10.add(jMenuItem30);

        jMenuBar4.add(jMenu10);

        jMenu4.setText("View");

        jMenuItem19.setText("raw data");
        jMenuItem19.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem19MousePressed(evt);
            }
        });
        jMenu4.add(jMenuItem19);

        jMenu12.setText("heat map");

        jMenuItem20.setText("green red");
        jMenuItem20.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem20MousePressed(evt);
            }
        });
        jMenu12.add(jMenuItem20);

        jMenuItem3.setText("rainbow");
        jMenuItem3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem3MousePressed(evt);
            }
        });
        jMenu12.add(jMenuItem3);

        jMenuItem22.setText("gray scale");
        jMenuItem22.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem22MousePressed(evt);
            }
        });
        jMenu12.add(jMenuItem22);

        jMenuItem23.setText("blue white");
        jMenuItem23.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem23MousePressed(evt);
            }
        });
        jMenu12.add(jMenuItem23);

        jMenu4.add(jMenu12);

        jMenu13.setText("signal plot");

        jMenuItem28.setText("rainbow");
        jMenuItem28.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem28MousePressed(evt);
            }
        });
        jMenu13.add(jMenuItem28);

        jMenuItem21.setText("red");
        jMenuItem21.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem21MousePressed(evt);
            }
        });
        jMenu13.add(jMenuItem21);

        jCheckBoxMenuItem4.setSelected(true);
        jCheckBoxMenuItem4.setText("scale bar");
        jCheckBoxMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem4ActionPerformed(evt);
            }
        });
        jMenu13.add(jCheckBoxMenuItem4);

        jCheckBoxMenuItem7.setText("mean signal");
        jCheckBoxMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem7ActionPerformed(evt);
            }
        });
        jMenu13.add(jCheckBoxMenuItem7);

        jMenu4.add(jMenu13);

        jMenu15.setText("settings");

        jMenuItem2.setFont(new java.awt.Font("Segoe UI", 1, 12));
        jMenuItem2.setForeground(new java.awt.Color(0, 153, 255));
        jMenuItem2.setText("image settings");
        jMenuItem2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem2MousePressed(evt);
            }
        });
        jMenu15.add(jMenuItem2);

        jCheckBoxMenuItem8.setSelected(true);
        jCheckBoxMenuItem8.setText("scale using entire dataset");
        jCheckBoxMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem8ActionPerformed(evt);
            }
        });
        jMenu15.add(jCheckBoxMenuItem8);

        jCheckBoxMenuItem9.setText("order columns");
        jCheckBoxMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItem9ActionPerformed(evt);
            }
        });
        jMenu15.add(jCheckBoxMenuItem9);

        jMenu4.add(jMenu15);

        jMenuItem8.setText("fit to screen");
        jMenuItem8.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem8MousePressed(evt);
            }
        });
        jMenu4.add(jMenuItem8);

        jMenu14.setText("motifLogo");

        jMenuItem25.setText("entropy");
        jMenuItem25.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem25MousePressed(evt);
            }
        });
        jMenu14.add(jMenuItem25);

        jMenuItem27.setText("frequency");
        jMenuItem27.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem27MousePressed(evt);
            }
        });
        jMenu14.add(jMenuItem27);

        jMenu4.add(jMenu14);

        jMenuBar4.add(jMenu4);

        jMenu16.setText("Search");

        jMenuItem29.setText("Find");
        jMenuItem29.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem29MousePressed(evt);
            }
        });
        jMenu16.add(jMenuItem29);

        jMenuBar4.add(jMenu16);

        jMenu1.setText("Help");

        jMenuItem11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem11MousePressed(evt);
            }
        });
        jMenu1.add(jMenuItem11);

        jMenuItem12.setText("About");
        jMenuItem12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem12MousePressed(evt);
            }
        });
        jMenu1.add(jMenuItem12);

        jMenuItem4.setText("Version and online help");
        jMenuItem4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jMenuItem4MousePressed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuBar4.add(jMenu1);

        setJMenuBar(jMenuBar4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTree1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTree1KeyPressed
        
        if(evt.getKeyCode() == evt.VK_DELETE){
            delete();
        }
        if(evt.getKeyCode() == evt.VK_S){
            split();
        }
        if(evt.getKeyCode() == evt.VK_M){
            merge();
        }
        if(choice==5 || choice==7){
        if(evt.getKeyCode() == evt.VK_NUMPAD2){
            barNum--;
            if(barNum<2) barNum=2;
            add(currSelection);
        }
        if(evt.getKeyCode() == evt.VK_NUMPAD8){
            barNum++;
            //if(barNum<2) barNum=2;
            add(currSelection);
        }
        if(evt.getKeyCode() == evt.VK_NUMPAD4){
            numWidth--;
            if(numWidth<1) numWidth=1;
            add(currSelection);
        }
        if(evt.getKeyCode() == evt.VK_NUMPAD6){
            numWidth++;
            //if(numWidth<1) barNum=1;
            add(currSelection);
        }      
        }
        if(evt.getKeyCode()==evt.VK_F) {
            fittoscreen();
        }
    }//GEN-LAST:event_jTree1KeyPressed

    private void jMenuItem1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem1MousePressed
        openAutoSOMEresults=true;
        clusterMaker.algorithms.autosome.fileio.File_Open.start(this);
    }//GEN-LAST:event_jMenuItem1MousePressed

    private void jMenuItem2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem2MousePressed
        jFrame1.pack();
        jFrame1.setLocation(0,0);
        jTextField1.setText(String.valueOf(scale));
        jSlider3.setValue(scale);
        jTextField13.setText(Format2.format(Double.valueOf(jTextField13.getText())));
        jTextField14.setText(Format2.format(Double.valueOf(jTextField14.getText())));
        jFrame1.setVisible(true);
    }//GEN-LAST:event_jMenuItem2MousePressed

    private void jMenuItem3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem3MousePressed
        pic_Drawer.rainbow = true;
        choice = 2;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem3MousePressed

    private void jMenuItem5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem5MousePressed
        clusterMaker.algorithms.autosome.fileio.File_Write.graphics = true;
        clusterMaker.algorithms.autosome.fileio.File_Write.b = B;
        if(currSelection==null){
            JOptionPane.showMessageDialog(this,
                            "Select cluster(s) before writing to file.",
                            "Output Error",
                            JOptionPane.ERROR_MESSAGE);
            return;
        }
       // clusterMaker.algorithms.autosome.fileio.File_Write.start("", this);
    }//GEN-LAST:event_jMenuItem5MousePressed

    private void jMenuItem4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem4MousePressed
        jFrame3.pack();
        jFrame3.setLocationRelativeTo(this);
        jFrame3.setVisible(true);

    }//GEN-LAST:event_jMenuItem4MousePressed

    private void jButton3MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MousePressed
        split();
    }//GEN-LAST:event_jButton3MousePressed

    private void jButton5MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton5MousePressed
       merge();
    }//GEN-LAST:event_jButton5MousePressed

    private void jTree1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTree1MousePressed
          
           
    }//GEN-LAST:event_jTree1MousePressed

    private void jMenuItem6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem6MousePressed
        reset();
        jTextField8.setText("");
        jTextField9.setText("");
        jTextField4.setText("50");
        jTextField5.setText("0.1");
        jTextField6.setText("30");
        jTextField7.setText("5");
        jTextField2.setText("0");
        jTextField11.setText("0.5");
        jTextField12.setText("1");
        jTextField1.setText("10");
        jTextField3.setText("");
        jTextField10.setText("99");
        jComboBox11.setSelectedIndex(0);
        jSpinner1.setValue(Runtime.getRuntime().availableProcessors());
        jComboBox6.setSelectedIndex(1);
        jComboBox7.setSelectedIndex(0);
        jComboBox8.setSelectedIndex(0);
        jComboBox4.setSelectedIndex(0);
        jComboBox5.setSelectedIndex(1);
        jComboBox2.setSelectedIndex(1);
        jCheckBox1.setSelected(false);
        jCheckBox2.setSelected(false);
        jCheckBox3.setSelected(false);
        jCheckBox4.setSelected(false);
        jCheckBox5.setSelected(false);
        jComboBox11.setSelectedIndex(0);
        jCheckBox16.setSelected(false);
        jCheckBox11.setSelected(false);
        jCheckBox15.setSelected(false);
        jCheckBox6.setSelected(true);
        jCheckBox7.setSelected(false);
        jCheckBox9.setSelected(false);
        jCheckBox10.setSelected(false);
        jCheckBox12.setSelected(false);
        jCheckBox13.setSelected(false);
        jCheckBox16.setEnabled(false);
        jCheckBox23.setSelected(false);
        jCheckBox18.setSelected(false);
        jCheckBox30.setEnabled(false);
        setGUITitle("");
           jTable1.getModel().setValueAt("", 0, 0);
           jTable1.getModel().setValueAt("", 0, 1);
           jTable1.getModel().setValueAt("", 1, 0);
           jTable1.getModel().setValueAt("", 1, 1);
           jTable1.getModel().setValueAt("", 2, 0);
           jTable1.getModel().setValueAt("", 2, 1);

           jTable1.getModel().setValueAt("", 3, 0);
           jTable1.getModel().setValueAt("", 3, 1);
           jTable1.getModel().setValueAt("", 4, 0);
           jTable1.getModel().setValueAt("", 4, 1);

           
           jTable2.getModel().setValueAt("", 0, 1);
          
           jTable2.getModel().setValueAt("", 1, 1);
           
           jTable2.getModel().setValueAt("", 2, 1);

          
           jTable2.getModel().setValueAt("", 3, 1);

            root=null;
            javax.swing.JTree jt = new javax.swing.JTree(root);
            jTree1.setModel(jt.getModel());
            initTreeSelection();
           

            jCheckBox24.setSelected(false);
            jCheckBox25.setSelected(false);
            jCheckBox26.setSelected(false);
            jCheckBox27.setSelected(false);
            jCheckBox28.setSelected(false);
            jCheckBox29.setSelected(false);
            jTabbedPane2.setSelectedIndex(0);
            jTextField18.setText("99");
            jCheckBox25.setEnabled(false);
            jCheckBox26.setEnabled(false);
            jCheckBox27.setEnabled(false);
            jCheckBox28.setEnabled(false);
            jCheckBox29.setEnabled(false);
            jTextField18.setEnabled(false);

           jComboBox3.removeAllItems();
           jComboBox3.addItem("Null");

           jTabbedPane1.setSelectedIndex(0);
           jSlider1.setValue(71);
           jslider1statechange();
           jSlider2.setValue(50);
           jSlider3.setValue(10);

            jLabel20.setEnabled(false);
            jComboBox4.setEnabled(false);

            jMenu4.setEnabled(false);
            jMenu16.setEnabled(false);

            jPanel13.setVisible(false);
            jPanel5.setVisible(false);
            jPanel6.setVisible(false);
            jPanel20.setVisible(false);

            jButton11.setText("Show");
            jButton12.setText("Show");
            jButton16.setText("Show");
            jButton20.setText("Show");

            jTable1.getModel().setValueAt("", 0, 0);
            jTable1.getModel().setValueAt("", 0, 1);
            jTable1.getModel().setValueAt("", 1, 0);
            jTable1.getModel().setValueAt("", 1, 1);
            jTable1.getModel().setValueAt("", 2, 0);
            jTable1.getModel().setValueAt("", 2, 1);

            jTable1.getModel().setValueAt("", 3, 0);
            jTable1.getModel().setValueAt("", 3, 1);
            jTable1.getModel().setValueAt("", 4, 0);
            jTable1.getModel().setValueAt("", 4, 1);

            jTable2.setValueAt("", 0,1);
            jTable2.setValueAt("" , 1,1);
            jTable2.setValueAt("" , 2,1);
            jTable2.setValueAt("", 3,1);

            jScrollPane2.getViewport().removeAll();
    }//GEN-LAST:event_jMenuItem6MousePressed

    private void jMenuItem19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem19MousePressed
        choice = 1;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem19MousePressed

    private void jMenuItem20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem20MousePressed
        choice = 2;
        pic_Drawer.rainbow = false;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem20MousePressed

    private void jMenuItem22MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem22MousePressed
       choice = 3;
       pic_Drawer.rainbow = false;
       add(currSelection);
    }//GEN-LAST:event_jMenuItem22MousePressed

    private void jMenuItem23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem23MousePressed
        choice = 4;
        pic_Drawer.rainbow = false;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem23MousePressed

    private void jMenuItem21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem21MousePressed
        choice = 5;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem21MousePressed

    private void jScrollPane2MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_jScrollPane2MouseWheelMoved
        if(choice == 1) return;
        scale -= evt.getWheelRotation();
        jTextField1.setText(String.valueOf(scale));
        jSlider3.setValue(scale);
        if(scale < 1) scale = 1;
        jslider1statechange();
        scroll=true;
        zoom.cancel();
        zoom = new java.util.Timer();
        zoom.schedule(RenderHMTimerTask(),50);
        scroll=false;
    }//GEN-LAST:event_jScrollPane2MouseWheelMoved

    private void jMenuItem27MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem27MousePressed
        choice = 6;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem27MousePressed

    private void jMenuItem28MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem28MousePressed
        choice = 7;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem28MousePressed

    private void jMenuItem25MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem25MousePressed
         choice = 8;
        add(currSelection);
    }//GEN-LAST:event_jMenuItem25MousePressed

    private void jButton2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MousePressed
       // if(!runningUpdatePvalue && (!jTextField19.getText().equals(jTextField5.getText()) || !jTextField5.getText().equals(String.valueOf(s.mst_pval)))) updatePValue();
        if(root!=null && confThreshold!=Integer.valueOf(jTextField2.getText())) updateConfidence();
    }//GEN-LAST:event_jButton2MousePressed

    private void jCheckBoxMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem4ActionPerformed
        add(currSelection);
    }//GEN-LAST:event_jCheckBoxMenuItem4ActionPerformed

    private void jScrollPane2KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jScrollPane2KeyPressed
      
    }//GEN-LAST:event_jScrollPane2KeyPressed

    private void jCheckBoxMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem7ActionPerformed
        add(currSelection);
    }//GEN-LAST:event_jCheckBoxMenuItem7ActionPerformed

    private void jCheckBoxMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem8ActionPerformed
        add(currSelection);
    }//GEN-LAST:event_jCheckBoxMenuItem8ActionPerformed

    private void jCheckBoxMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItem9ActionPerformed
         add(currSelection);
    }//GEN-LAST:event_jCheckBoxMenuItem9ActionPerformed

    private void jMenuItem29MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem29MousePressed
        jFrame2.pack();
        jFrame2.setVisible(true);
        jFrame2.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem29MousePressed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jButton7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton7MousePressed
        String search = jComboBox3.getSelectedItem().toString();
        if(!jTextField3.getText().equals("")) search = jTextField3.getText();
        if(!walk((DefaultTreeModel)jTree1.getModel(),root, search, 1, true))
            walk((DefaultTreeModel)jTree1.getModel(),root, search, 1, false);
        jFrame2.setVisible(false);
    }//GEN-LAST:event_jButton7MousePressed

    private void jMenuItem30MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem30MousePressed
        for(int i = 0; i < c.length; i++){
            float[] center = new float[s.input[0].getValues().length];
            System.out.print((i+1)+"\t");
            for(int j = 0; j < c[i].ids.size(); j++){
                int ID = Integer.valueOf(c[i].ids.get(j).toString());
                for(int k = 0; k < s.input[ID].getValues().length; k++){
                    center[k] += s.input[ID].getValues()[k];
                }                
            }
            for(int k = 0; k < center.length; k++){
                    center[k] /= c[i].ids.size();
                    if(k < center.length-1) System.out.print(center[k]+"\t");
                    else System.out.print(center[k]+"\n");
            }
        }
    }//GEN-LAST:event_jMenuItem30MousePressed

    private void jButton11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MousePressed
        if(jButton11.getText().equals("Show")){
            jButton11.setText("Hide");
            jPanel5.setVisible(true);
            if(jComboBox11.getSelectedIndex()==2) {
                jPanel26.setVisible(true);
                jLabel47.setVisible(true);
                jSeparator11.setVisible(true);
                jLabel17.setText("Basic Fields (Rows)");
            }

        }else{
            jPanel5.setVisible(false);
            jButton11.setText("Show");
            if(jComboBox11.getSelectedIndex()==2) {
                jPanel26.setVisible(false);
                jLabel47.setVisible(false);
                 jLabel17.setText("Basic Fields");
                if(jButton12.getText().equals("Show"))jSeparator11.setVisible(false);

            }
        }
    }//GEN-LAST:event_jButton11MousePressed

    private void jButton12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton12MousePressed
        if(jButton12.getText().equals("Show")){
            jButton12.setText("Hide");
            jPanel6.setVisible(true);
            if(jPanel25.isVisible()) {
                jPanel27.setVisible(true);
                jLabel49.setVisible(true);
                jSeparator12.setVisible(true);
                jLabel18.setText("Input Adjustment (Rows)");
                if(jButton11.getText().equals("Show")) jSeparator11.setVisible(true);
              //  jLabel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
              //  jLabel49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
            }
        }else{
            jPanel6.setVisible(false);
            jPanel27.setVisible(false);
            jLabel49.setVisible(false);
            jSeparator12.setVisible(false);
            jButton12.setText("Show");
            jLabel18.setText("Input Adjustment");
            jLabel18.setBorder(null);
            jLabel49.setBorder(null);
            if(jButton11.getText().equals("Show")) jSeparator11.setVisible(false);
        }
    }//GEN-LAST:event_jButton12MousePressed

    private void jButton9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MousePressed
        openAutoSOMEresults=false;
        clusterMaker.algorithms.autosome.fileio.File_Open.start(this);
    }//GEN-LAST:event_jButton9MousePressed

    private void jButton10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MousePressed
        updateOutputDirectory = true;
        clusterMaker.algorithms.autosome.fileio.File_Open.start(this);
        
    }//GEN-LAST:event_jButton10MousePressed

    private void jButton8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton8MousePressed
        runAut.killAut();
        runAutoSOMEthread.stop();
        reset();
        jLabel8.setText("00:00:00");
    }//GEN-LAST:event_jButton8MousePressed

    private void jButton16MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton16MousePressed
        if(jButton16.getText().equals("Show")){
            jButton16.setText("Hide");
            jPanel13.setVisible(true);
        }else{
            jPanel13.setVisible(false);
            jButton16.setText("Show");
        }
    }//GEN-LAST:event_jButton16MousePressed

    private void jCheckBox7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox7ActionPerformed
        if(jCheckBox7.isSelected()) jCheckBox6.setSelected(false);
    }//GEN-LAST:event_jCheckBox7ActionPerformed

    private void jCheckBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox6ActionPerformed
       if(jCheckBox6.isSelected()) jCheckBox7.setSelected(false);
    }//GEN-LAST:event_jCheckBox6ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton13MousePressed
        if(jTabbedPane1.getSelectedIndex()==1) {
            jTabbedPane1.setSelectedIndex(0);
            if(inputStats[0]!=0){
                jTable2.setValueAt((int)inputStats[0], 0,1);
                jTable2.setValueAt((int)inputStats[1] , 1,1);
                jTable2.setValueAt(Format.format(inputStats[2]), 2,1);
                jTable2.setValueAt(Format.format(inputStats[3]), 3,1);
                jTable2.doLayout();
            }
        }
        else {
            openAutoSOMEresults=false;
            //fileio.File_Open.start(this);
        }

    }//GEN-LAST:event_jButton13MousePressed

    private void jButton15MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton15MousePressed
         jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_jButton15MousePressed

    private void jButton14MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton14MousePressed
        if(runAutoSOMEthread!=null) if(runAutoSOMEthread.isAlive()) return;
        runAutoSOMEthread = new Thread(runAut = new RunAutoSOME(this));
        runAutoSOMEthread.start();
        jButton14.setSelected(true);
        jButton14.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_jButton14MousePressed

    private void jButton6MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton6MousePressed
        updateImageSettings();
        pic_Drawer.pixelY = Double.valueOf(jTextField11.getText());
        pic_Drawer.userScale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.colorScaleBar=!jCheckBox17.isSelected();
        scale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.useUserScale=true;
        add(currSelection);
        pic_Drawer.useUserScale=false;
    }//GEN-LAST:event_jButton6MousePressed

    private void jButton18MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton18MousePressed
        jFrame1.setVisible(false);
    }//GEN-LAST:event_jButton18MousePressed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged

        jslider1statechange();
    
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        jTextField12.setText(String.valueOf((((double)jSlider2.getValue()*2)/100)));
    }//GEN-LAST:event_jSlider2StateChanged

    private void jSlider3StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider3StateChanged
        jTextField1.setText(String.valueOf(jSlider3.getValue()));
        jslider1statechange();
    }//GEN-LAST:event_jSlider3StateChanged

    private void jButton17MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton17MousePressed
       //fileio.File_Write.graphics = true;
       // fileio.File_Write.b = B;
       // fileio.File_Write.start("", this);
    }//GEN-LAST:event_jButton17MousePressed

    private void jTextField11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField11MousePressed
       
    }//GEN-LAST:event_jTextField11MousePressed

    private void jTextField12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField12MousePressed
      
    }//GEN-LAST:event_jTextField12MousePressed

    private void jTextField1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MousePressed
      
    }//GEN-LAST:event_jTextField1MousePressed

    private void jTextField11FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField11FocusLost
      jSlider1.setValue(((int)(100*Math.sqrt(Double.valueOf(jTextField11.getText())))));
    }//GEN-LAST:event_jTextField11FocusLost

    private void jTextField12FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField12FocusLost
       jSlider2.setValue((int)(100*(Double.valueOf(jTextField12.getText())/2)));
    }//GEN-LAST:event_jTextField12FocusLost

    private void jTextField1FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextField1FocusLost
        jSlider3.setValue(Integer.valueOf(jTextField1.getText()));
        jslider1statechange();
    }//GEN-LAST:event_jTextField1FocusLost

    private void jCheckBox13MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox13MousePressed
        
    }//GEN-LAST:event_jCheckBox13MousePressed

    private void jButton19MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton19MousePressed
        jTextField2.setText("0");
        updateConfidence();
    }//GEN-LAST:event_jButton19MousePressed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(s.input==null) return;
        if(jTabbedPane1.getSelectedIndex()==0){
           if(inputStats[0]!=0){
                jTable2.setValueAt((int)inputStats[0], 0,1);
                jTable2.setValueAt((int)inputStats[1] , 1,1);
                jTable2.setValueAt(Format.format(inputStats[2]), 2,1);
                jTable2.setValueAt(Format.format(inputStats[3]), 3,1);
                jTable2.doLayout();
            }
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jCheckBox14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox14ActionPerformed
        if(jCheckBox14.isSelected()){
            
            jTextField14.setEnabled(true);
            jTextField13.setEnabled(true);
            jLabel25.setEnabled(true);
            jLabel26.setEnabled(true);
            jSlider2.setEnabled(false);
            jTextField12.setEnabled(false);
            jTextField13.setText(Format2.format(s.inputMin));
            jTextField14.setText(Format2.format(s.inputMax));
        }else{
         
            jTextField14.setEnabled(false);
            jTextField13.setEnabled(false);
            jLabel25.setEnabled(false);
            jLabel26.setEnabled(false);
            jSlider2.setEnabled(true);
            jTextField12.setEnabled(true);
        
            jTextField13.setText(Format2.format(s.inputMin));
            jTextField14.setText(Format2.format(s.inputMax));
        }
    }//GEN-LAST:event_jCheckBox14ActionPerformed

    private void jMenuItem5MousePressed1(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem5MousePressed1
       saveTable(true);
    }//GEN-LAST:event_jMenuItem5MousePressed1

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        if(jCheckBox3.isSelected()) jCheckBox2.setSelected(false);
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
         if(jCheckBox2.isSelected()) jCheckBox3.setSelected(false);
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jButton20MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton20MousePressed
 if(jButton20.getText().equals("Show")){
            jButton20.setText("Hide");
            jPanel20.setVisible(true);
        }else{
            jPanel20.setVisible(false);
            jButton20.setText("Show");
        }
    }//GEN-LAST:event_jButton20MousePressed

    private void jComboBox6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox6ActionPerformed
        switch(jComboBox6.getSelectedIndex()){
            case 0:
                jComboBox5.setSelectedIndex(0);
                jComboBox2.setSelectedIndex(0);
            break;
            case 1:
                jComboBox5.setSelectedIndex(1);
                jComboBox2.setSelectedIndex(1);
            break;
            case 2:
                jComboBox5.setSelectedIndex(2);
                jComboBox2.setSelectedIndex(2);
            break;
        }
    }//GEN-LAST:event_jComboBox6ActionPerformed

    private void jButton13MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton13MouseEntered
        jButton13.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_jButton13MouseEntered

    private void jButton14MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton14MouseEntered
        jButton14.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_jButton14MouseEntered

    private void jButton15MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton15MouseEntered
        jButton15.setForeground(new Color(0,0,0));
    }//GEN-LAST:event_jButton15MouseEntered

    private void jButton13MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton13MouseExited
        jButton13.setForeground(new Color(230,0,0));
    }//GEN-LAST:event_jButton13MouseExited

    private void jButton14MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton14MouseExited
  
        if(runAutoSOMEthread!=null){ if(!runAutoSOMEthread.isAlive())jButton14.setForeground(new Color(0,140,0));}
        else jButton14.setForeground(new Color(0,140,0));
    }//GEN-LAST:event_jButton14MouseExited

    private void jButton15MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton15MouseExited
       jButton15.setForeground(new Color(0,153,255));
    }//GEN-LAST:event_jButton15MouseExited

    private void jLabel23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel23MousePressed
       
    }//GEN-LAST:event_jLabel23MousePressed

    private void jButton22MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton22MousePressed
        try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton22MousePressed

    private void jButton23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton23MousePressed
     try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/gettingStarted.jsp"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton23MousePressed

    private void jButton21MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton21MousePressed
       try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/files/AutoSOME_Manual.pdf"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton21MousePressed

    private void jButton24MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton24MousePressed
          try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/terms.jsp"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton24MousePressed

    private void jButton25MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton25MousePressed
           try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/fuzzyClusters.jsp"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton25MousePressed

    private void jButton26MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton26MousePressed
              try{
           Desktop.getDesktop().browse(new java.net.URI("http://jimcooperlab.mcdb.ucsb.edu/autosome/datasets.jsp"));
       }catch(IOException err){
           System.err.println(err);
       }catch(java.net.URISyntaxException err){
           System.err.println(err);
       }
    }//GEN-LAST:event_jButton26MousePressed

    private void jTable1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MousePressed
       
        if(jTable1.getModel().getValueAt(1, 1)==null) { jTable1.clearSelection();return;}
        if(jTable1.getModel().getValueAt(1, 1).toString().length()==0) { jTable1.clearSelection();return;}
        try{
            switch(jTable1.getSelectedRow()){
                case 0:
                    Desktop.getDesktop().open(new File(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+s.add+"_Summary.html"));
                break;
                case 1:
                    Desktop.getDesktop().open(new File(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+s.add+".html"));
                break;
                case 2:
                    Desktop.getDesktop().open(new File(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+s.add+".txt"));
                break;
                case 3:
                   if(jTable1.getModel().getValueAt(3, 1)==null) break;
                   if(jTable1.getModel().getValueAt(3, 1).equals("")) break;
                   Desktop.getDesktop().open(new File(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+s.add+"_Edges.txt"));
                break;
                case 4:
                   if(jTable1.getModel().getValueAt(4, 1)==null) break;
                   if(jTable1.getModel().getValueAt(4, 1).equals("")) break;
                   Desktop.getDesktop().open(new File(s.outputDirectory+s.getFolderDivider()+"AutoSOME_"+s.add+"_Nodes.txt"));
                break;

            }
        }catch(IOException err){
           System.err.println(err);

         }
        jTable1.clearSelection();
       

    }//GEN-LAST:event_jTable1MousePressed

    private void jTable2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MousePressed
        jTable2.clearSelection();
    }//GEN-LAST:event_jTable2MousePressed

    private void jButton27MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton27MousePressed

        jFrame4.setVisible(false);
    }//GEN-LAST:event_jButton27MousePressed

    private void jMenuItem7MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem7MousePressed
        jFrame4.pack();
        jFrame4.setLocationRelativeTo(this);
        jFrame4.setVisible(true);
    }//GEN-LAST:event_jMenuItem7MousePressed

    private void jCheckBox13MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox13MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jCheckBox13MouseReleased

    private void jCheckBox9MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox9MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jCheckBox9MouseReleased

    private void jCheckBox12MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox12MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jCheckBox12MouseReleased

    private void jCheckBox10MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox10MouseReleased
         add(currSelection);
    }//GEN-LAST:event_jCheckBox10MouseReleased

    private void jCheckBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox12ActionPerformed

    private void jCheckBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox11ActionPerformed
        if(jCheckBox11.isSelected()){

            JOptionPane.showMessageDialog(this,
                    "A temporary folder will be created in the output directory.\nThis option will take longer (during Find Cluster No. and Ensemble Merging steps) due to reading and writing to disk.",
                    "Write Ensemble Runs to Disk",
                    JOptionPane.INFORMATION_MESSAGE);

            System.out.println("A temporary folder will be created in the output directory.\nThis option will take longer (during Find Cluster No. and Ensemble Merging steps) due to reading and writing to disk.");
        }
    }//GEN-LAST:event_jCheckBox11ActionPerformed

    private void jCheckBox18MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox18MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jCheckBox18MouseReleased

    private void jMenuItem8MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem8MousePressed
        fittoscreen();

    }//GEN-LAST:event_jMenuItem8MousePressed

    private void jComboBox11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox11ActionPerformed
        if(jComboBox11.getSelectedIndex()>0){
            jLabel20.setEnabled(true);
            jLabel46.setEnabled(true);
            jComboBox4.setEnabled(true);
            jCheckBox16.setEnabled(true);
            if(jComboBox11.getSelectedIndex()==2) {
               // jComboBox1.setVisible(true);
                jPanel25.setVisible(true);
                jPanel26.setVisible(true);
               // jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
               // jLabel47.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
                if(jPanel6.isVisible()) {
                    jPanel27.setVisible(true);                   
                }
                jLabel17.setText("Basic Fields (Rows)");
                if(jPanel6.isVisible()) {
                    jLabel18.setText("Input Adjustment (Rows)");
                  //  jLabel18.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
                  //  jLabel49.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 153, 51)));
                    jLabel49.setVisible(true);
                    jSeparator12.setVisible(true);
                }
                
            }
            else {
                jComboBox1.setVisible(false);
                jPanel25.setVisible(false);
                jPanel26.setVisible(false);
                jPanel27.setVisible(false);
                jLabel17.setText("Basic Fields");
                jLabel18.setText("Input Adjustment");
                jLabel49.setVisible(false);
                jSeparator12.setVisible(false);
                jLabel17.setBorder(null);
                jLabel47.setBorder(null);
                jLabel18.setBorder(null);
                jLabel49.setBorder(null);
            }
        }else{
            jComboBox1.setVisible(false);
            jLabel17.setBorder(null);
            jLabel47.setBorder(null);
            jLabel18.setBorder(null);
            jLabel49.setBorder(null);
            jLabel20.setEnabled(false);
            jLabel46.setEnabled(false);
            jComboBox4.setEnabled(false);
            jCheckBox16.setEnabled(false);
            jPanel25.setVisible(false);
            jPanel26.setVisible(false);
            jPanel27.setVisible(false);
            jLabel17.setText("Basic Fields");
            jLabel18.setText("Input Adjustment");
            jLabel49.setVisible(false);
            jSeparator12.setVisible(false);
        }
    }//GEN-LAST:event_jComboBox11ActionPerformed

    private void jComboBox12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox12ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox12ActionPerformed

    private void jCheckBox8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox8ActionPerformed

    private void jCheckBox19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox19ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox19ActionPerformed

    private void jCheckBox20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox20ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox20ActionPerformed

    private void jCheckBox21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox21ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox21ActionPerformed

    private void jMenuItem10MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem10MousePressed
        try{
            if(s.input==null){
                JOptionPane.showMessageDialog(this,
                            "Error reading input file.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
            }else{
          
            }
        }catch(Exception err){
            
            JOptionPane.showMessageDialog(this,
                            "Error reading input file.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem10MousePressed

    private void jMenuItem9MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem9MousePressed
             saveTable(false);        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem9MousePressed

    private void jCheckBox23MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox23MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox23MousePressed

    private void jCheckBox23MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox23MouseReleased
       add(currSelection);
    }//GEN-LAST:event_jCheckBox23MouseReleased

    private void jCheckBox23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox23ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox23ActionPerformed

    private void jCheckBox17MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox17MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jCheckBox17MouseReleased

    private void jCheckBox25ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox25ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox25ActionPerformed

    private void jCheckBox27ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox27ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox27ActionPerformed

    private void jCheckBox28ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox28ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox28ActionPerformed

    private void jCheckBox24MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox24MouseReleased
        if(jCheckBox24.isSelected()){
            jCheckBox25.setEnabled(true);
            jCheckBox26.setEnabled(true);
            jSeparator15.setEnabled(true);
            jSeparator16.setEnabled(true);
            jCheckBox27.setEnabled(true);
            jCheckBox28.setEnabled(true);
            jCheckBox29.setEnabled(true);
            jTextField18.setEnabled(true);
            jComboBox16.setEnabled(true);
            jComboBox13.setEnabled(true);
            s.logNorm=jCheckBox25.isSelected();
            s.unitVar=jCheckBox27.isSelected();
            if(jCheckBox26.isSelected()) s.scale=Integer.valueOf(jTextField18.getText());
            else s.scale=0;
            if(jCheckBox28.isSelected()) {
            if(jComboBox16.getSelectedIndex()==0) {s.medCenter = true; s.medCenterCol = false;}
            if(jComboBox16.getSelectedIndex()==1) {s.medCenterCol = true; s.medCenter = false;}
            if(jComboBox16.getSelectedIndex()==2) {s.medCenter = true;s.medCenterCol = true;}
        }else{
            s.medCenter = false;
            s.medCenterCol=false;
        }
        if(jCheckBox29.isSelected()) {
            if(jComboBox13.getSelectedIndex()==0) {s.sumSqrRows = true; s.sumSqrCol = false;}
            if(jComboBox13.getSelectedIndex()==1) {s.sumSqrCol = true; s.sumSqrRows = false;}
            if(jComboBox13.getSelectedIndex()==2) {s.sumSqrRows = true;s.sumSqrCol = true;}
        }else{
            s.sumSqrRows = false;
            s.sumSqrCol = false;
        }

            saveNorm = new Settings();
            saveNorm.logNorm=s.logNorm;
            saveNorm.unitVar=s.unitVar;
            saveNorm.medCenter=s.medCenter;
            saveNorm.medCenterCol=s.medCenterCol;
            saveNorm.scale=s.scale;
            saveNorm.sumSqrCol=s.sumSqrCol;
            saveNorm.sumSqrRows=s.sumSqrRows;


            for(int i = 0; i < s.input.length; i++){
                if(s.input[i].getNormValues()!=null) break;
                s.input[i].initNormValue(s.input[i].getValues().length);
               // System.out.println("null");
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setnormValue(j,s.input[i].getValues()[j]);
                }
            }

            for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                  // if(i<10) System.out.println(s.input[i].getOriginalValues()[j]+" "+s.input[i].getValues()[j]);
                    s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
                }
            }
            doNormalization(new Run());
       
        }else{
            resetOrigDataPanel();
            for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setValue(j,s.input[i].getNormValues()[j]);
                }
            }
            s.logNorm=saveNorm.logNorm;
            s.unitVar=saveNorm.unitVar;
            s.medCenter=saveNorm.medCenter;
            s.medCenterCol=saveNorm.medCenterCol;
            s.scale=saveNorm.scale;
            s.sumSqrCol=saveNorm.sumSqrCol;
            s.sumSqrRows=saveNorm.sumSqrRows;
        }
        s.setInputMinMax();
        add(currSelection);
    }//GEN-LAST:event_jCheckBox24MouseReleased

    private void jCheckBox25MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox25MouseReleased
        if(!jCheckBox25.isEnabled()) return;

        s.logNorm=jCheckBox25.isSelected();

         for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
                }
            }

        doNormalization(new Run());
        s.setInputMinMax();
        add(currSelection);
    }//GEN-LAST:event_jCheckBox25MouseReleased

    private void jCheckBox27MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox27MouseReleased
        if(!jCheckBox27.isEnabled()) return;
        s.unitVar=jCheckBox27.isSelected();
        s.scale=0;
        if(jCheckBox27.isSelected()) jCheckBox26.setSelected(false);
         for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
                }
            }

        doNormalization(new Run());
        s.setInputMinMax();
        add(currSelection);
    }//GEN-LAST:event_jCheckBox27MouseReleased

    private void jCheckBox28MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox28MouseReleased
         if(!jCheckBox28.isEnabled()) return;
         changeMedCenter();
    }//GEN-LAST:event_jCheckBox28MouseReleased

    private void jCheckBox29MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox29MouseReleased
         if(!jCheckBox29.isEnabled()) return;
        changeSumSqr();
    }//GEN-LAST:event_jCheckBox29MouseReleased

    private void jComboBox16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox16ActionPerformed
        if(flag) return;
        changeMedCenter();
    }//GEN-LAST:event_jComboBox16ActionPerformed

    private void jComboBox13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox13ActionPerformed
        if(flag) return;
        changeSumSqr();
    }//GEN-LAST:event_jComboBox13ActionPerformed

    private void jCheckBox30MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox30MouseReleased
        int[] sel = jTree1.getSelectionRows();
        sortCluster sc = new sortCluster();
        if(jCheckBox30.isSelected()){
            jCheckBox13.setSelected(true);
            for(int i = 0; i < c.length; i++){
                sc.sortVar(c[i], s);
            }
        }else{
            jCheckBox13.setSelected(false);
            for(int i = 0; i < c.length; i++){
                sc.sortConf2(c[i], s);
            }
        }
        //updateImageSettings();
        initTree(c);
        javax.swing.JTree jt = new javax.swing.JTree(root);
        jTree1.setModel(jt.getModel());
        jTree1.setSelectionRows(sel);

    }//GEN-LAST:event_jCheckBox30MouseReleased

    private void jSlider1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MouseReleased
        updateImageSettings();
        pic_Drawer.pixelY = Double.valueOf(jTextField11.getText());
        pic_Drawer.userScale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.colorScaleBar=!jCheckBox17.isSelected();
        scale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.useUserScale=true;
        add(currSelection);
        pic_Drawer.useUserScale=false;
        slide1=false;
    }//GEN-LAST:event_jSlider1MouseReleased

    private void jSlider3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider3MouseReleased
        updateImageSettings();
        pic_Drawer.pixelY = Double.valueOf(jTextField11.getText());
        pic_Drawer.userScale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.colorScaleBar=!jCheckBox17.isSelected();
        scale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.useUserScale=true;
        add(currSelection);
        pic_Drawer.useUserScale=false;
    }//GEN-LAST:event_jSlider3MouseReleased

    private void jSlider2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider2MouseReleased
        add(currSelection);
    }//GEN-LAST:event_jSlider2MouseReleased

    private void jMenuItem11MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem11MousePressed
        jFrame5.pack();
        jFrame5.setLocationRelativeTo(this);
        jFrame5.setVisible(true);
    }//GEN-LAST:event_jMenuItem11MousePressed

    private void jComboBox17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox17ActionPerformed

    private void jMenuItem5MousePressed2(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem5MousePressed2
        saveTable(true);
    }//GEN-LAST:event_jMenuItem5MousePressed2

    private void jSlider1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider1MousePressed
        slide1=true;
    }//GEN-LAST:event_jSlider1MousePressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
  
    }//GEN-LAST:event_formKeyPressed

    private void jScrollPane2MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane2MousePressed
        if(evt.getButton()==evt.BUTTON3) fittoscreen();
    }//GEN-LAST:event_jScrollPane2MousePressed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        jTextField19.setText(pvals[jComboBox1.getSelectedIndex()]);
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jTextField19KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField19KeyTyped
      
    }//GEN-LAST:event_jTextField19KeyTyped

    private void jTextField19KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField19KeyReleased
        pvals[jComboBox1.getSelectedIndex()] = jTextField19.getText();
    }//GEN-LAST:event_jTextField19KeyReleased

    private void jMenuItem12MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenuItem12MousePressed
        new imgs.autosomeBanner().start();
    }//GEN-LAST:event_jMenuItem12MousePressed

    private void jCheckBox26ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox26ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_jCheckBox26ActionPerformed

    private void jCheckBox26MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jCheckBox26MouseReleased
        if(!jCheckBox26.isEnabled()) return;
        if(jCheckBox26.isSelected()) s.scale=Integer.valueOf(jTextField18.getText());// TODO add
        else s.scale=0;
        s.unitVar=false;
        s.logNorm=false;
        jCheckBox25.setSelected(false);
        if(jCheckBox26.isSelected()) jCheckBox27.setSelected(false);
        for(int i = 0; i < s.input.length; i++){
            for(int j = 0; j < s.input[i].getValues().length; j++){
                s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
            }
        }

        doNormalization(new Run());
        s.setInputMinMax();
        add(currSelection);
}//GEN-LAST:event_jCheckBox26MouseReleased


    private void updatePValue(){
         System.gc();
         runningUpdatePvalue=true;
         boolean cols = false;
         if(jComboBox1.getSelectedIndex()==2) cols=true;
         int[] paths = jTree1.getSelectionRows();

      
            clusterRuns = new ArrayList();
            threadSafeList = Collections.synchronizedCollection( clusterRuns );
            s.htmlOut=false;
            s.textOut=false;

            jLabel8.setText("00:00:00");
            final long t1 = System.currentTimeMillis();
            Action updateTimeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    jLabel8.setText(calcHMS((System.currentTimeMillis()-t1)/1000));
                }
            };

            timer = new Timer(1000, updateTimeAction);
            timer.start();
            jButton8.setEnabled(true);
            jLabel7.setEnabled(true);
            jLabel7.setText("Updating");
            jProgressBar1.setEnabled(true);
            jProgressBar1.setIndeterminate(true);
           
            updatePValueCounter=0;
       
            Thread[] threads = new Thread[s.threads];
            System.out.println("**"+threads.length);
            for(int q = 0; q < threads.length; q++){

                threads[q] = new Thread(new runUpdatePValue(paths,this, q*((jComboBox1.getSelectedIndex()!=1) ? storeMapping.size() : storeMappingB.size())/(threads.length), (q+1)*((jComboBox1.getSelectedIndex()!=1) ? storeMapping.size() : storeMappingB.size())/(threads.length), (jComboBox1.getSelectedIndex()!=1) ? false : true));
                threads[q].start();
            }


        

    }
    
    private class runUpdatePValue implements Runnable{
        int start, end;
        boolean cols;
        viewer2D v2d;
        int[] paths;
        public runUpdatePValue(int[] paths, viewer2D v2d, int start, int end, boolean cols){
            this.start = start;
            this.end = end;
            this.v2d=v2d;
            this.cols=cols;
            this.paths=paths;
        }
        public void run(){
   
            for(;start<end;start++){
                    updatePValueCounter++;
               // try{
                    s.mst_pval=Double.valueOf(jTextField19.getText());
                    Run r = new Run();
                    Object[] results = (cols || jComboBox1.getSelectedIndex()==1) ? (Object[])storeMappingB.get(start) : (Object[])storeMapping.get(start);
                    float[][] coors = (float[][]) results[0];
                    ArrayList dataLabels = (ArrayList) results[1];
                    //clustering
                    clusterRun cr = r.doClustering(s, coors, dataLabels);
                    cr.DEC = null;
                    threadSafeList.add(cr);
                    System.out.println(start+" "+end+" "+updatePValueCounter+" "+clusterRuns.size()+" "+storeMapping.size());
               // }catch(Exception err){System.err.println("*"+err);runningUpdatePvalue=false;};
             }
             if(threadSafeList.size() == ((cols) ? storeMappingB.size() : storeMapping.size())){
               //  try{
                    ArrayList ct = new ArrayList();
                    Object[] o = threadSafeList.toArray();
                    for(int i = 0; i < threadSafeList.size(); i++) ct.add(o[i]);
                    clusterRun cr = new Run().runNewPValue(ct, s, v2d);
                    if(jComboBox11.getSelectedIndex()==2 && (cols || jComboBox1.getSelectedIndex()>0)) repartitionColumns(cr);
                    if(jComboBox11.getSelectedIndex()< 2 || jComboBox1.getSelectedIndex()==0 || (jComboBox1.getSelectedIndex()==2 && cols)) c = cr.c;
                  
                        jScrollPane2.getViewport().removeAll();
                        initTree(c);
                        javax.swing.JTree jt = new javax.swing.JTree(root);
                        jTree1.setModel(jt.getModel());
                        initTreeSelection();
                       
                        jProgressBar1.setIndeterminate(false);
                        reset();
                        //System.out.println(paths.length);
                        jTree1.setSelectionRows(paths);
                        add(currSelection);
                        runningUpdatePvalue=false;
               //  }catch(Exception err){System.err.println("*"+err);runningUpdatePvalue=false;}
             }
        }
    }


    private void fittoscreen(){
        scale = Math.max(1, (jScrollPane2.getViewport().getWidth()-pic_Drawer.extraWidth)/((s.input[0].getValues().length)+1));
        jTextField1.setText(String.valueOf(scale));
        double vertical = Math.min(1,((double)(jScrollPane2.getViewport().getHeight()-pic_Drawer.extraHeight)/currSelection.length)/scale);
        if(vertical<.001) vertical=.001;
        jTextField11.setText(String.valueOf(vertical));
        //System.out.println(vertical);
        //jTextField11.setText(String.valueOf(Math.min(1,((double)(jScrollPane2.getViewport().getHeight()-pic_Drawer.extraHeight)/currSelection.length)/scale)));
        updateImageSettings();
        pic_Drawer.pixelY = Double.valueOf(jTextField11.getText());
        pic_Drawer.userScale = Integer.valueOf(jTextField1.getText());
        pic_Drawer.colorScaleBar=!jCheckBox17.isSelected();
        pic_Drawer.useUserScale=true;
        add(currSelection);
        pic_Drawer.useUserScale=false;
        jSlider1.setValue(((int)(100*Math.sqrt(Double.valueOf(jTextField11.getText())))));
        jSlider3.setValue(Integer.valueOf(jTextField1.getText()));
    }


    private void resetOrigDataPanel(){
        flag=true;
        jCheckBox25.setEnabled(false);
        jCheckBox26.setEnabled(false);
        jSeparator15.setEnabled(false);
        jSeparator16.setEnabled(false);
        jCheckBox27.setEnabled(false);
        jCheckBox28.setEnabled(false);
        jCheckBox29.setEnabled(false);
        jTextField18.setEnabled(false);
        jComboBox16.setEnabled(false);
        jComboBox16.setSelectedIndex(0);
        jComboBox13.setEnabled(false);
        jComboBox13.setSelectedIndex(2);
        jCheckBox30.setSelected(false);
        flag=false;
    }


    private void changeMedCenter(){
        if(s.input==null) return;
        if(jCheckBox28.isSelected()) {
            if(jComboBox16.getSelectedIndex()==0) {s.medCenter = true; s.medCenterCol = false;}
            if(jComboBox16.getSelectedIndex()==1) {s.medCenterCol = true; s.medCenter = false;}
            if(jComboBox16.getSelectedIndex()==2) {s.medCenter = true;s.medCenterCol = true;}
        }else{
            s.medCenter = false;
            s.medCenterCol=false;
        }
          for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
                }
            }

          doNormalization(new Run());
          s.setInputMinMax();
        add(currSelection);
    }


    private void changeSumSqr(){
        if(s.input==null) return;
        if(jCheckBox29.isSelected()) {
            if(jComboBox13.getSelectedIndex()==0) {s.sumSqrRows = true; s.sumSqrCol = false;}
            if(jComboBox13.getSelectedIndex()==1) {s.sumSqrCol = true; s.sumSqrRows = false;}
            if(jComboBox13.getSelectedIndex()==2) {s.sumSqrRows = true;s.sumSqrCol = true;}
        }else{
            s.sumSqrRows = false;
            s.sumSqrCol = false;
        }

          for(int i = 0; i < s.input.length; i++){
                for(int j = 0; j < s.input[i].getValues().length; j++){
                    s.input[i].setValue(j,s.input[i].getOriginalValues()[j]);
                }
            }
          doNormalization(new Run());
          s.setInputMinMax();
        add(currSelection);
    }

    private void updateImageSettings(){

        pic_Drawer.contrast = Double.valueOf(jTextField12.getText());
        pic_Drawer.black = jCheckBox9.isSelected();
        pic_Drawer.columnLabels = !jCheckBox10.isSelected();
        pic_Drawer.rowLabels = !jCheckBox12.isSelected();
        pic_Drawer.colorScaleBar=!jCheckBox17.isSelected();
        pic_Drawer.showClusterSeparators=!jCheckBox18.isSelected();
        pic_Drawer.showClusterColumnSeparators=!jCheckBox23.isSelected();
    }


    private void jslider1statechange(){
        if(slide1 && (Double.valueOf(jTextField11.getText())<=1 || jSlider1.getValue()<100)) jTextField11.setText(Format2.format(Math.pow((double)jSlider1.getValue()/100,2)));
        int pxls = (int)(Double.valueOf(Format2.format(Math.pow((double)jSlider1.getValue()/100,2)))*Integer.valueOf(jTextField1.getText()));
        String pixels = String.valueOf(pxls);
        if(pxls<1) pixels="1";
        if(Double.valueOf(jTextField11.getText())==.504) jTextField11.setText(".500");
        if(Double.valueOf(jTextField11.getText())==1) jTextField11.setText("1.00");
        if(Double.valueOf(jTextField11.getText())==0) jTextField11.setText(".001");
    }

    
    protected boolean walk(TreeModel model, Object o, String query, int level, boolean exact){
        int cc;
        cc = model.getChildCount(o);
        for( int i=0; i < cc; i++) {
            Object child = model.getChild(o, i );
            if (model.isLeaf(child)){
                     String identifier = new StringTokenizer(child.toString(),",").nextToken();
                     if((identifier.equals(query) && exact) || (!exact && identifier.contains(query))){
                         choice = 9;                        
                         highlightCluster = level;
                         jTree1.setSelectionRow(level);
                         highlight = query;
                         add(currSelection);
                         return true;
                     }
            }
            else {
                if(walk(model,child, query, level++, exact)) break;
                }
            }
        return false;
        }

    private void updateConfidence(){
        confThreshold = Integer.valueOf(jTextField2.getText());
        initTree(c);
        javax.swing.JTree jt = new javax.swing.JTree(root);
        jTree1.setModel(jt.getModel());
       
    }


  
   
    
  
    
    
    private void delete(){
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
 
            TreePath[] paths = jTree1.getSelectionPaths();
            
            TreeNode parent = (TreeNode) paths[0].getParentPath().getLastPathComponent();
            
            for(int i = 0; i < paths.length; i++){
                MutableTreeNode mNode = (MutableTreeNode)paths[i].getLastPathComponent();
                model.removeNodeFromParent(mNode);
            }
            if(model.getRoot() != parent) display(parent);
           
    }
    
     private void split(){
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
 
            TreePath[] paths = jTree1.getSelectionPaths();
            
            TreeNode parent = (TreeNode) paths[0].getParentPath().getLastPathComponent();
            
            if(model.getRoot() == parent) return;
            
            DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[paths.length];
            DefaultMutableTreeNode clust = new DefaultMutableTreeNode("cluster "+(clustIndex++)+" ("+nodes.length+")");
           
            for(int i = 0; i < paths.length; i++){
                MutableTreeNode mNode = (MutableTreeNode)paths[i].getLastPathComponent();
                model.removeNodeFromParent(mNode);
                ((dataItem)((DefaultMutableTreeNode) mNode).getUserObject()).setClustID(clustIndex-1);
                clust.add(mNode);
            }
            
            root.add(clust);
            
            String label = ((DefaultMutableTreeNode)parent).getUserObject().toString();
            StringTokenizer st = new StringTokenizer(label, "(");
            label = st.nextToken();
            ((DefaultMutableTreeNode)parent).setUserObject(label+"("+((DefaultMutableTreeNode)parent).getChildCount()+")");
        
            model.nodeStructureChanged(root);
                        
            display(parent);
            
    }
     
      private void merge(){
            DefaultTreeModel model = (DefaultTreeModel)jTree1.getModel();
 
            TreePath[] paths = jTree1.getSelectionPaths();
            
            TreeNode parent = (TreeNode) paths[0].getParentPath().getLastPathComponent();
            
            if(model.getRoot() != parent || paths.length == 1) return;
         
            DefaultMutableTreeNode mNode = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
            String label = ((DefaultMutableTreeNode)mNode).getUserObject().toString();
           // System.out.println(mNode.getChildCount());
            StringTokenizer st = new StringTokenizer(label);
            st.nextToken();
            int clustID = Integer.valueOf(st.nextToken());
            for(int i = 1; i  < paths.length; i++){
                DefaultMutableTreeNode dNode = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
                 for(int j = 0; j < dNode.getChildCount(); j++){
                    ((dataItem)((DefaultMutableTreeNode)dNode.getChildAt(j)).getUserObject()).setClustID(clustID);
                    mNode.add((DefaultMutableTreeNode)dNode.getChildAt(j));
                     //System.out.println("*"+mNode.getChildCount()+" "+dNode.getChildCount());
                     j--;
                
                }                    
           
                model.removeNodeFromParent(dNode);
                //System.out.println(dNode.getChildCount());
               
            }
            
           //System.out.println(mNode.getChildCount());
         
            st = new StringTokenizer(label, "(");
            label = st.nextToken();
            mNode.setUserObject(label+"("+mNode.getChildCount()+")");
            //System.out.println(mNode.getUserObject().toString());
           // model.nodeStructureChanged(root);
                        
            //display(mNode);
           
    }
     
     
     
    
     
     

 
   private void saveTable(boolean original){
       TreePath[] paths = jTree1.getSelectionPaths();

        if(paths == null) {
            JOptionPane.showMessageDialog(this,
                            "Select cluster(s) before writing to file.",
                            "Output Error",
                            JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuffer sb = new StringBuffer();
        if(s.readColumns || s.columnHeaders != null){
             sb.append("#\t");

                if(!s.distMatrix && original){
                        if(s.logNorm) sb.append("l\t");
                        if(s.unitVar) sb.append("u\t");
                        if(s.scale>0) sb.append("s"+s.scale+"\t");
                        if(s.medCenter) sb.append("m\t");
                        if(s.medCenterCol) sb.append("M\t");
                        if(s.sumSqrRows) sb.append("q\t");
                        if(s.sumSqrCol) sb.append("Q\t");
                }
                        sb.append("\nCLUST\tCONF\t");
             for(int k = s.startData-1; k < s.columnHeaders.length; k++){
                             if(s.PCLformat && k==s.startData-1) sb.append(s.columnHeaders[1]+"\t");
                             else sb.append(s.columnHeaders[k]+"\t");
             }
             sb.append("\n");
         }
         for(int i = 0; i < paths.length; i++){
            MutableTreeNode node = (MutableTreeNode)paths[i].getLastPathComponent();

            String[] tokens = node.toString().split(" ");


            for(int j = 0; j < node.getChildCount(); j++){
                dataItem d = (((dataItem)((DefaultMutableTreeNode)node.getChildAt(j)).getUserObject()));
                sb.append(tokens[1]+"\t"+d.getConf()+"\t"+((original) ? d.toDescString() : d.toDescNormString())+"\n");
            }
        }

      
   }
       
   

   
   
   
   public void invokeClusterViewer_From_trIDFile(File results, Settings set){
       new Thread(new openFile(results,set,this)).start();
   }

   private class openFile implements Runnable{
       File results;
       Settings set;
       viewer2D v2d;
       public openFile(File results, Settings set, viewer2D v2d){
           this.results=results;
           this.set=set;
           this.v2d=v2d;
       }
       public void run(){
           s=set;
       jCheckBox24.setSelected(false);
       resetOrigDataPanel();
       try{
            s.columnHeaders = null;
            //getParameters(s);
            s.inputFile=results.toString();
            new clusterMaker.algorithms.autosome.fileio.File_Open(true);

            BufferedReader bf = new BufferedReader(new FileReader(results));
            String line = new String();
            ArrayList allTRs = new ArrayList();
            ArrayList ids = new ArrayList();
            ArrayList trs = new ArrayList();
            ArrayList allIds = new ArrayList();
            ArrayList allValues = new ArrayList();
            ArrayList allIdentifiers = new ArrayList();
            ArrayList conf = new ArrayList();
            ArrayList allConf = new ArrayList();
            clusterMaker.algorithms.autosome.launch.Run run = new clusterMaker.algorithms.autosome.launch.Run();
            String lastCluster = new String();
            int count = -1;
            int lineCount = 0;
            while((line = bf.readLine())!=null){
                if(line.length() != 0){
                    count++;
                    lineCount++;
                    int tIndex = 0;
                    String[] tokens = line.split("\t");

                    if(tokens[0].length()==line.length()) tokens = line.split(" ");
                    if(tokens[0].length()==line.length()) tokens = line.split(",");
                    if(lineCount%1000==0) {
                        clusterMaker.algorithms.autosome.fileio.File_Open.jLabel15.setText(String.valueOf(lineCount));
                    }

                    ///////////////

                    //column headers
                    //System.out.println(line+" *"+tokens[0]+"* "+tokens[0].equals("#")+" "+lineCount);
                    if(lineCount==1 && (tokens[0].equals("#"))){
                        if(tokens[0].equals("#")){
                            readNorm(tokens);
                            line = bf.readLine();
                            tokens = line.split("\t");
                            if(tokens[0].length()==line.length()) tokens = line.split(" ");
                            if(tokens[0].length()==line.length()) tokens = line.split(",");
                            //column clusters
                            if(!tokens[0].equals("CLUST")){
                                if(tokens.length < 2) tokens = line.split(" ");
                                s.columnClusters = new int[tokens.length-2];
                                for(int k = 3; k < s.columnClusters.length+2; k++){
                                        s.columnClusters[k-3] = Integer.valueOf(tokens[k]);
                                        //System.out.println(s.columnClusters[k-3]);
                                }
                                line = bf.readLine();
                            }
                            tokens = line.split("\t");
                            if(tokens.length < 2) tokens = line.split(" ");
                        }
                        s.columnHeaders = new String[tokens.length-2];
                        for(int k = 2; k < s.columnHeaders.length+2; k++){
                            s.columnHeaders[k-2] = tokens[k];
                        }
                        s.readColumns=true;
                        count = -1;
                        continue;
                    }
                    ///////////


                    String clustId = tokens[tIndex++];
                    if(!clustId.equals(lastCluster) && lastCluster.length()>0){
                        allTRs.add(trs);
                        allIds.add(ids);
                        if(s.confidence) allConf.add(conf);
                        ids = new ArrayList();
                        trs = new ArrayList();
                        conf = new ArrayList();
                    }

                    ids.add(count);
                    StringBuffer values = new StringBuffer();
                    //
                    if(s.confidence){
                        conf.add(tokens[tIndex++]);
                    }
                    if(s.startData > 1){
                        tIndex += (s.startData+1);
                    }
                    ArrayList val = new ArrayList();
                    boolean first = true;
                    for(; tIndex < tokens.length; tIndex++) {
                        String v = tokens[tIndex];
                        if(!first) val.add(v);
                        else allIdentifiers.add(v);
                        values.append(v+",");
                        first = false;
                    }
                    float[] f = new float[val.size()];

                        for(int i = 0; i < f.length; i++) {
                            f[i] = Float.valueOf(val.get(i).toString());
                        }


                    allValues.add(f);
                    String label = values.substring(0,values.length()-1);

                    trs.add(label);

                    lastCluster = clustId;
                }
            }
            if(ids.size() > 0){
                        allTRs.add(trs);
                        allIds.add(ids);
                        if(s.confidence) allConf.add(conf);
                        ids = new ArrayList();
                        trs = new ArrayList();
                        conf = new ArrayList();
             }

            c = new cluster[allTRs.size()];
            for(int i = 0; i < c.length; i++)
            {
                c[i] = new cluster();
                c[i].ids = (ArrayList) allIds.get(i);
                c[i].labels = (ArrayList) allTRs.get(i);
                if(s.confidence) c[i].confidence = (ArrayList) allConf.get(i);
            }

            s.input = new dataItem[count+1];
            for(int j = 0; j < s.input.length; j++)
            {
                s.input[j] = new dataItem((float[])allValues.get(j), allIdentifiers.get(j).toString());
            }

            if(s.confidence){
               for(int i = 0; i < c.length; i++)
               {
                for(int j = 0; j < c[i].ids.size(); j++){
                    int id = Integer.valueOf(c[i].ids.get(j).toString());
                    s.input[id].setConf(Integer.valueOf(c[i].confidence.get(j).toString()));
                }
               }
            }

            //doNormalization(run);

            s.setInputMinMax();
            initTree(c);
            String[] IDs = new String[s.input.length];
            for(int i = 0; i < s.input.length; i++)
                IDs[i] = s.input[i].getIdentity();
            Arrays.sort(IDs);
            for(int i = 0; i < IDs.length; i++){
                if(IDs[i].length()>0) jComboBox3.addItem(IDs[i]);
            }
            jMenu4.setEnabled(true);
            jMenu16.setEnabled(true);
            javax.swing.JTree jt = new javax.swing.JTree(root);
            jTree1.setModel(jt.getModel());
            initTreeSelection();

            jTabbedPane1.setSelectedIndex(1);
            if(s.input.length<=200) {pic_Drawer.pixelY=1;
                jSlider1.setValue(100);
                jTextField11.setText("1.0");
            }else{
                pic_Drawer.pixelY=.5;
                jSlider1.setValue(71);
                jTextField11.setText("0.5");
            }
            setGUITitle(s.getName());
            clusterMaker.algorithms.autosome.fileio.File_Open.ofile.kill();
            scale = Math.max(1, jScrollPane2.getWidth()/(5+s.input[0].getValues().length));
            v2d.setSize(Math.max(originalWidth+20, v2d.getWidth()), v2d.getHeight());
        }catch(Exception err){System.err.println("Error opening AutoSOME cluster results.");
        JOptionPane.showMessageDialog(v2d,
                            "Error opening AutoSOME cluster results.",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
        clusterMaker.algorithms.autosome.fileio.File_Open.ofile.kill();}
       }
   }
   

   private void doNormalization(Run run){
        if((s.unitVar || s.scale>0 || s.logNorm)) {
                if(s.unitVar && s.logNorm){
                    s.unitVar=false;
                    s.input = run.norm(s); //normalize input
                    s.unitVar=true; s.logNorm=false;
                    s.input = run.norm(s);
                    s.logNorm=true;
                    if((s.medCenter||s.medCenterCol)) s.input = run.medCenter(s);
                    if( (s.sumSqrRows||s.sumSqrCol)) s.input = run.getSumSqr(s);
                
            }else {
                    if(s.logNorm){
                        s.input = run.norm(s);
                        if((s.medCenter||s.medCenterCol)) s.input = run.medCenter(s);
                        if( (s.sumSqrRows||s.sumSqrCol)) s.input = run.getSumSqr(s);
                    }else{
                        s.input = run.norm(s);
                        if((s.medCenter||s.medCenterCol)) s.input = run.medCenter(s);
                        if( (s.sumSqrRows||s.sumSqrCol)) {s.input = run.getSumSqr(s);}

                    }
            }
        }else{
                if((s.medCenter||s.medCenterCol)) s.input = run.medCenter(s);
                if((s.sumSqrRows||s.sumSqrCol)) s.input = run.getSumSqr(s);
        }
   }



   private class RunAutoSOME implements Runnable{
       viewer2D v2d;
       Run r;

       public RunAutoSOME(viewer2D v2d){
           this.v2d = v2d;
       }
       public void run(){
            HashMap kept = s.kept;
          //  dataItem[] temp = s.input;
          //  String[] coltemp = s.columnHeaders;
         //   boolean rctemp = (coltemp!=null) ? ((coltemp.length>0) ? true : false) : false;
            s = new Settings();
            v2d.s.input=null;
           // s.input=temp;
           // s.columnHeaders=coltemp;
            s.kept=kept;
           // s.readColumns=rctemp;*/


            jLabel8.setText("00:00:00");
            final long t1 = System.currentTimeMillis();
            Action updateTimeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    jLabel8.setText(calcHMS((System.currentTimeMillis()-t1)/1000));
                }
            };

            timer = new Timer(1000, updateTimeAction);
            timer.start();
            jButton8.setEnabled(true);
            jLabel7.setEnabled(true);
            jLabel7.setText("Clustering");
            v2d.jProgressBar1.setEnabled(true);




            //run AutoSOME
            r = new Run();
            getParameters(s);
            s.inputFile = jTextField8.getText();
            s.outputDirectory = jTextField9.getText();
            if(!s.readColumns) s.readColumns = jCheckBox7.isSelected();
            if(jComboBox11.getSelectedIndex()==2 || jComboBox11.getSelectedIndex()==0) s.printRowsCols=1;
            else if(jComboBox11.getSelectedIndex()==1) s.printRowsCols=2;

            s.input=new dataItem[1];
            System.gc();

            clusterRun cr = r.run(s, true, v2d);
            if(cr==null || s.input==null) {reset(); jLabel8.setText("00:00:00"); return;}
            c = cr.c;
            
            storeMapping = r.getMappingArrayList();
            pvals[0] = jTextField5.getText();
            pvals[1] = jTextField16.getText();
            pvals[2] = "0.1";

            if(jComboBox11.getSelectedIndex()==2){
               clusterColumns(v2d);
               s.add=s.getName()+"_rows_columns";
               s.printRowsCols=3;
               new clusterMaker.algorithms.autosome.fileio.printClusters().printClusters(s.add, cr.c, s);
            }
            //display running time

            jScrollPane2.getViewport().removeAll();

            jTextField13.setText(Format2.format(v2d.s.inputMin));
            jTextField14.setText(Format2.format(v2d.s.inputMax));

            v2d.jMenu4.setEnabled(true);
            v2d.jMenu16.setEnabled(true);
            v2d.initTree(c);
            String[] ids = new String[s.input.length];
            for(int i = 0; i < s.input.length; i++)
                ids[i] = s.input[i].getIdentity();
            Arrays.sort(ids);
            jComboBox3.removeAllItems();
            for(int i = 0; i < ids.length; i++){
                if(ids[i].length()>0) jComboBox3.addItem(ids[i]);
            }
            javax.swing.JTree jt = new javax.swing.JTree(root);
            v2d.jTree1.setModel(jt.getModel());
            initTreeSelection();

     

            reset();
            jTabbedPane1.setSelectedIndex(1);
            scale = Math.max(1, jScrollPane2.getWidth()/(5+s.input[0].getValues().length));
             if(s.input.length<=200) {pic_Drawer.pixelY=1;
                jSlider1.setValue(100);
                jTextField11.setText("1.0");
            }else{
                pic_Drawer.pixelY=.5;
                jSlider1.setValue(71);
                jTextField11.setText("0.5");
            }



            jPanel1.repaint();
            jScrollPane2.repaint();
            jTable1.getModel().setValueAt("Summary", 0, 0);
            jTable1.getModel().setValueAt("html", 0, 1);
            jTable1.getModel().setValueAt("Clusters", 1, 0);
            jTable1.getModel().setValueAt("html", 1, 1);
            jTable1.getModel().setValueAt("Clusters", 2, 0);
            jTable1.getModel().setValueAt("text", 2, 1);
            if(jComboBox11.getSelectedIndex()==1){
                jTable1.getModel().setValueAt("Edges", 3, 0);
                jTable1.getModel().setValueAt("text", 3, 1);
                jTable1.getModel().setValueAt("Nodes", 4, 0);
                jTable1.getModel().setValueAt("text", 4, 1);
            }else{
                jTable1.getModel().setValueAt("", 3, 0);
                jTable1.getModel().setValueAt("", 3, 1);
                jTable1.getModel().setValueAt("", 4, 0);
                jTable1.getModel().setValueAt("", 4, 1);
            }
            jTable1.doLayout();
            if(jComboBox11.getSelectedIndex()==1){
                if(jSlider1.getModel().getValue()==71) {
                    jSlider1.setValue(100);
                    jTextField11.setText("1.000");
                }
            }else{
                if(jSlider1.getModel().getValue()==100) {
                    jSlider1.setValue(71);
                    jTextField11.setText(".500");
                }
            }
            jCheckBox24.setSelected(false);
            resetOrigDataPanel();
            jTextField19.setText(jTextField5.getText());
           // pic_Drawer.pixelY = Double.valueOf(jTextField11.getText());
       }

       public void killAut() {r.kill();}
   }

 private void getParameters(Settings s){
     try{
        s.ensemble_runs = Integer.valueOf(jTextField4.getText());
        s.mst_pval = Double.valueOf(jTextField5.getText());
        s.som_maxGrid = Integer.valueOf(jTextField6.getText());
        s.som_minGrid = Integer.valueOf(jTextField7.getText());
        s.threads = Integer.valueOf(jSpinner1.getValue().toString());
            s.GEOformat=GEOformat;
            s.PCLformat=PCLformat;
        if(jCheckBox1.isSelected()) s.logNorm = true;
        else s.logNorm=false;
        if(jCheckBox3.isSelected()) s.unitVar = true;
        else s.unitVar = false;
        if(jCheckBox2.isSelected()) s.scale = Integer.valueOf(jTextField10.getText());
        if(jCheckBox4.isSelected()) {
            if(jComboBox8.getSelectedIndex()==0) {s.medCenter = true; s.medCenterCol = false;}
            if(jComboBox8.getSelectedIndex()==1) {s.medCenterCol = true; s.medCenter = false;}
            if(jComboBox8.getSelectedIndex()==2) {s.medCenter = true;s.medCenterCol = true;}
        }else{
            s.medCenter = false;
            s.medCenterCol=false;
        }
        if(jCheckBox5.isSelected()) {
            if(jComboBox7.getSelectedIndex()==0) {s.sumSqrRows = true; s.sumSqrCol = false;}
            if(jComboBox7.getSelectedIndex()==1) {s.sumSqrCol = true; s.sumSqrRows = false;}
            if(jComboBox7.getSelectedIndex()==2) {s.sumSqrRows = true;s.sumSqrCol = true;}
        }else{
            s.sumSqrRows = false;
            s.sumSqrCol = false;
        }
        
        if(jComboBox11.getSelectedIndex()>0) {
            if(jComboBox4.getSelectedItem().toString().equals("Euclidean Distance")){
                
                s.dmDist = 1; 
            }
             if(jComboBox4.getSelectedItem().toString().equals("Pearson's Correlation")){
                s.dmDist = 2;
            }
             if(jComboBox4.getSelectedItem().toString().equals("Uncentered Correlation")){
                s.dmDist = 3;
            }
        }
        if(jComboBox11.getSelectedIndex()==1) {
           s.distMatrix = true;
           s.printConsMatrix=true;
        }else  s.distMatrix=false;
        switch(jComboBox2.getSelectedIndex()){
            case 0:
                 s.som_iters = 1000;
            break;
            case 1:
                 s.som_iters = 500;
            break;
            case 2:
                 s.som_iters = 250;
            break;
        }
        switch(jComboBox2.getSelectedIndex())
        {
            case 0:
                s.de_resolution=64;
            break;
            case 1:
                s.de_resolution=32;
            break;
            case 2:
                s.de_resolution=16;
            break;
            case 3:
                s.de_resolution=128;
            break;
            case 4:
                s.de_resolution=256;
            break;
            
        }
        s.som_circle = !jCheckBox15.isSelected();
        if(jCheckBox11.isSelected()) s.writeTemp = true;
         else s.writeTemp = false;
        if(jCheckBox16.isSelected()) s.unitVarAfterDM=true;
         else s.unitVarAfterDM=false;

         if(jComboBox9.getSelectedIndex()==1) s.mvMedian=true;
        else s.mvMedian=false;
        if(jComboBox10.getSelectedIndex()==1) s.mvCol=true;
        else s.mvCol=false;

     }catch(Exception err) {
         JOptionPane.showMessageDialog(this,
                            "Parameter Error. Make sure all fields contain appropriate entries.",
                            "Parameter Error",
                            JOptionPane.ERROR_MESSAGE);
     }
 }

 private void getParametersColumns(Settings s){
     try{
        s.ensemble_runs = Integer.valueOf(jTextField15.getText());
        s.mst_pval = Double.valueOf(jTextField16.getText());

        if(jCheckBox8.isSelected()) s.logNorm = true;
        else s.logNorm=false;
        if(jCheckBox20.isSelected()) s.unitVar = true;
        else s.unitVar = false;
        if(jCheckBox19.isSelected()) s.scale = Integer.valueOf(jTextField17.getText());
        if(jCheckBox21.isSelected()) {
            if(jComboBox15.getSelectedIndex()==0) {s.medCenter = true; s.medCenterCol = false;}
            if(jComboBox15.getSelectedIndex()==1) {s.medCenterCol = true; s.medCenter = false;}
            if(jComboBox15.getSelectedIndex()==2) {s.medCenter = true;s.medCenterCol = true;}
        }else{
            s.medCenter = false;
            s.medCenterCol=false;
        }
        if(jCheckBox22.isSelected()) {
            if(jComboBox14.getSelectedIndex()==0) {s.sumSqrRows = true; s.sumSqrCol = false;}
            if(jComboBox14.getSelectedIndex()==1) {s.sumSqrCol = true; s.sumSqrRows = false;}
            if(jComboBox14.getSelectedIndex()==2) {s.sumSqrRows = true;s.sumSqrCol = true;}
        }else{
            s.sumSqrRows = false;
            s.sumSqrCol = false;
        }


     }catch(Exception err) {
         JOptionPane.showMessageDialog(this,
                            "Parameter Error. Make sure all fields contain appropriate entries.",
                            "Parameter Error",
                            JOptionPane.ERROR_MESSAGE);
     }
 }


 private void clusterColumns(viewer2D v2d){
                Settings sc = new Settings();
                getParameters(sc);
                getParametersColumns(sc);
                sc.inputFile = jTextField8.getText();
                sc.outputDirectory = jTextField9.getText();
                sc.readColumns = jCheckBox7.isSelected();
                sc.kept=s.kept;
                v2d.jProgressBar1.setIndeterminate(false);
                v2d.jProgressBar1.setEnabled(false);
                v2d.jProgressBar1.setValue(0);
                jLabel7.setEnabled(true);
                jLabel7.setText("Clustering");
                v2d.jProgressBar1.setEnabled(true);
                sc.distMatrix=true;
                sc.printConsMatrix=true;
                Run r = new Run();
                sc.printRowsCols=2;
                clusterRun cols = r.run(sc,true,v2d);
                storeMappingB = r.getMappingArrayList();
                repartitionColumns(cols);
 }

 private void repartitionColumns(clusterRun cols){
               /* sortBySize[] sbs = new sortBySize[cols.c.length];
                for(int i = 0; i < cols.c.length; i++){
                    float[] f = new float[s.input[0].getValues().length];
                    for(int j = 0; j < cols.c[i].ids.size(); j++ ){
                        int id = Integer.valueOf(cols.c[i].ids.get(j).toString());
                        float[] f2 = s.input[id].getValues();
                        for(int k = 0; k < f2.length; k++) f[k] += f2[k];
                    }
                    for(int k = 0; k < f.length; k++) f[k]/=cols.c[i].ids.size();
                    if(i==0) anchor=f;
                    sbs[i] = new sortBySize(f, cols.c[i]);
                }
                Arrays.sort(sbs);
                for(int i = 0; i < cols.c.length; i++) cols.c[i] = sbs[i].c;*/


                s.columnClusters = new int[s.columnHeaders.length];
                s.oldOrder = new int[s.input[0].getValues().length];
                dataItem[] temp = new dataItem[s.input.length];
                String[] colHeaders = new String[s.columnHeaders.length];
                colHeaders[0] = "ID";
                for(int i = 0; i < temp.length; i++) temp[i] = new dataItem(new float[s.input[i].getValues().length], s.input[i].getIdentity());
                for(int i = 0, itor=0; i < cols.c.length; i++){
                    for(int j = 0; j < cols.c[i].ids.size(); j++, itor++){
                        //System.out.println("cluster "+i+" "+cols.c[i].ids.get(j).toString());
                        s.columnClusters[itor] = i;
                        s.oldOrder[itor] = Integer.valueOf(cols.c[i].ids.get(j).toString());
                        //System.out.println(itor+" "+s.oldOrder[itor]);
                        //System.out.println(s.columnClusters[itor]+" "+itor+" "+i+" "+s.columnClusters.length);
                        colHeaders[itor+s.startData] = s.columnHeaders[Integer.valueOf(cols.c[i].ids.get(j).toString())+s.startData];
                       // System.out.println(i+" "+Integer.valueOf(cols.c[i].ids.get(j).toString())+" "+s.columnClusters[Integer.valueOf(cols.c[i].ids.get(j).toString())]);
                        for(int k = 0; k < temp.length; k++) {
                            temp[k].setValue(itor, s.input[k].getValues()[Integer.valueOf(cols.c[i].ids.get(j).toString())]);
                            temp[k].setOriginalValue(itor, s.input[k].getOriginalValues()[Integer.valueOf(cols.c[i].ids.get(j).toString())]);
                        }
                    }
                }
                for(int i = 0; i < s.input.length; i++){
                    for(int j = 0; j < s.input[i].getValues().length; j++){
                        s.input[i].setOriginalValue(j, temp[i].getOriginalValues()[j]);
                        s.input[i].setValue(j, temp[i].getValues()[j]);
                    }
                }
                s.columnHeaders = colHeaders;
                temp = null;
 }


private String calcHMS(long timeInSeconds) {
      long hours, minutes, seconds;
      hours = timeInSeconds / 3600;
      String h = String.valueOf(hours);
      timeInSeconds = timeInSeconds - (hours * 3600);
      minutes = timeInSeconds / 60;
      String m = String.valueOf(minutes);
      timeInSeconds = timeInSeconds - (minutes * 60);
      seconds = timeInSeconds;
      String s = String.valueOf(seconds);
      return(addZero(h) + ":" + addZero(m) + ":" + addZero(s));
   }

private String addZero(String t){
    if(t.length()==0) return "00";
    if(t.length()==1) return "0"+t;
    return t;
}


private void reset(){
    jLabel7.setEnabled(false);
    jProgressBar1.setIndeterminate(false);
    jProgressBar1.setEnabled(false);
    jProgressBar1.setValue(0);
    jButton8.setEnabled(false);
    if(timer!=null) timer.stop();
    jLabel7.setText(" ");
    jButton14.setSelected(false);
    jButton14.setForeground(new Color(0,140,0));
    jCheckBox30.setSelected(false);
}


private class changeView implements Runnable{
    dataItem[] d;
    boolean finish = false;
    boolean scroll=false;
    public changeView(dataItem[] d, boolean scroll){
        this.d = d;
        this.scroll=scroll;
    }
    public void run(){

        updateImageSettings();


        if(!s.readColumns) pic_Drawer.colorScaleBar=false;

       // try{
        //System.out.println(choice+" "+renderImgActiveCount+" "+renderImg.activeCount()+" "+firstTime);
        boolean memFlag=false;
        if(renderImg.activeCount()>renderImgActiveCount && d.length>=5000){
            memFlag=true;
        }
        
         
        
         javax.swing.JTable jtab = new javax.swing.JTable(d.length+1,2);

         //System.out.println("before "+choice+" "+debugItor);
        //while(!finish){
        pic_Drawer pic = new pic_Drawer(s);

        pic.RGB = null;

        pic.setmaxSize(jScrollPane2.getWidth()-20, jScrollPane2.getHeight()-20);

       
         if(!memFlag){
         switch(choice){
            case 1:

                ArrayList temp = new ArrayList();
                for(int i = 0; i < d.length; i++) {
                    String[] dat = new String[(multipleClusters) ? 3 : 2];
                    if(d[i].getValues().length==1 && d[i].getIdentity().equals("spacer")) continue;
                    if(multipleClusters){
                        dat[0] = String.valueOf(d[i].getClustID());
                        dat[1] = d[i].getIdentity();
                        dat[2] = String.valueOf(d[i].getConf());
                    }
                    else{
                        dat[0] = d[i].getIdentity();
                        dat[1] = String.valueOf(d[i].getConf());
                    }
                    temp.add(dat);
                }
                String[][] tabledata = new String[temp.size()][(multipleClusters) ? 3 : 2];
                for(int i = 0;i<tabledata.length; i++) tabledata[i]=(String[])temp.get(i);

                jtab = new javax.swing.JTable(tabledata, (multipleClusters) ? new Object[]{"Cluster","Name", "Confidence"} : new Object[]{"Name", "Confidence"});
                jtab.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
                jtab.setCellSelectionEnabled(true);
                jtab.setAutoResizeMode(jtab.AUTO_RESIZE_ALL_COLUMNS);

                break;
            case 2:               
               pic.createHeatMap(d, choice, scale, !jCheckBox13.isSelected(), true,jCheckBoxMenuItem8.isSelected(),jCheckBoxMenuItem9.isSelected(), null, jCheckBox14.isSelected(), Float.valueOf(jTextField13.getText()), Float.valueOf(jTextField14.getText()));
               break;
            case 3:
               pic.createHeatMap(d, choice, scale, !jCheckBox13.isSelected(), true,jCheckBoxMenuItem8.isSelected(),jCheckBoxMenuItem9.isSelected(), null, jCheckBox14.isSelected(), Float.valueOf(jTextField13.getText()), Float.valueOf(jTextField14.getText()));
               break;
            case 4:
               pic.createHeatMap(d, choice, scale, !jCheckBox13.isSelected(), true,jCheckBoxMenuItem8.isSelected(),jCheckBoxMenuItem9.isSelected(), null, jCheckBox14.isSelected(), Float.valueOf(jTextField13.getText()), Float.valueOf(jTextField14.getText()));
               break;
            case 5:
               pic.createScatterPlot(d, scale, true, jCheckBoxMenuItem4.isSelected(), barNum, numWidth,jCheckBoxMenuItem7.isSelected(),jCheckBoxMenuItem8.isSelected());
               break;
            case 6:
               // pic.createMotifLogo(d, scale, false);
                break;
            case 7:
                pic.createScatterPlot(d, scale, false, jCheckBoxMenuItem4.isSelected(), barNum, numWidth,jCheckBoxMenuItem7.isSelected(),jCheckBoxMenuItem8.isSelected());
                break;
             case 8:
               // pic.createMotifLogo(d, scale, true);
                break;
              case 9:
               pic.createHeatMap(d, choice, scale, !jCheckBox13.isSelected(), true,jCheckBoxMenuItem8.isSelected(),jCheckBoxMenuItem9.isSelected(), highlight, jCheckBox14.isSelected(), Float.valueOf(jTextField13.getText()), Float.valueOf(jTextField14.getText()));
               break;
         }
        
         
        }else{
             //pic.CreateTRImage(new String[]{"Rendering..."}, false);
        }

        if(choice!=1) {
            firstTime=false;
            renderImgActiveCount=renderImg.activeCount();
            if(!memFlag){
                TP.update(pic);
                TP.repaint();
            }
        }


        jScrollPane2.repaint();

        if(!memFlag){
            if(choice!=1) jScrollPane2.setViewportView(TP);
            else jScrollPane2.setViewportView(jtab);
        }//else jScrollPane2.setViewportView(jPanel23);
        //System.out.println("after "+choice+" "+debugItor);
        jScrollPane2.repaint();
        B = pic.rendImage;
       // finish=true;
       // }
      //  }catch(Exception err){};
    }

    public void kill(){finish=true; jLabel8.setText("00:00:00");}

}



private void readNorm(String[] tokens){
    if(tokens.length==1)return;
    for(int j = 1; j < tokens.length; j++){
        switch(tokens[j].charAt(0)){
            case 'l':
                s.logNorm=true;
              //  jCheckBox1.setSelected(true);
            break;
            case 'u':
                s.unitVar=true;
               // jCheckBox3.setSelected(true);
            break;
            case 's':
                s.scale=Integer.valueOf(tokens[j].substring(1));
              //  jCheckBox2.setSelected(true);
              //  jTextField10.setText(String.valueOf(s.scale));
            break;
            case 'm':
                s.medCenter=true;
              //  jCheckBox4.setSelected(true);
              //  jComboBox8.setSelectedIndex(0);
            break;
            case 'M':
                s.medCenterCol=true;
               // jCheckBox4.setSelected(true);
              //  jComboBox8.setSelectedIndex(1);
            break;
            case 'q':
                s.sumSqrRows=true;
               // jCheckBox5.setSelected(true);
               // jComboBox7.setSelectedIndex(0);
            break;
            case 'Q':
                s.sumSqrCol=true;
               // jCheckBox5.setSelected(true);
               // jComboBox7.setSelectedIndex(0);
            break;
        }
    }
   // if(s.medCenter && s.medCenterCol) jComboBox8.setSelectedIndex(2);
   //if(s.sumSqrRows && s.sumSqrCol) jComboBox8.setSelectedIndex(2);
}

class MyAdjustmentListener implements AdjustmentListener {

     public void adjustmentValueChanged(AdjustmentEvent evt) {
         if(choice==1) return;
         Adjustable source = evt.getAdjustable();
         if (evt.getValueIsAdjusting()) {
               //TP.setBorder(jScrollPane2.getHorizontalScrollBar().getValue(), jScrollPane2.getVerticalScrollBar().getValue(), jScrollPane2.getViewport().getWidth(), jScrollPane2.getViewport().getHeight());

               //jScrollPane2.setViewportView(TP);
               jScrollPane2.repaint();
         }

     }

 }

  private TimerTask RenderHMTimerTask(){
        TimerTask tt = new TimerTask(){
            public void run(){
                add(currSelection);
            }
        };
        return tt;
    }



   public void killDisplay(){
       jScrollPane2.getViewport().removeAll();
            c=null;
            root=null;
            javax.swing.JTree jt = new javax.swing.JTree(root);
            jTree1.setModel(jt.getModel());
   }



    float[] anchor;
    private int choice = 1;
    private int scale = 10;
    private dataItem[] currSelection;
    float currMin = 0;
    float currMax = 0;
    private int barNum = 5;
    private int numWidth = 5;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    public javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton22;
    private javax.swing.JButton jButton23;
    private javax.swing.JButton jButton24;
    private javax.swing.JButton jButton25;
    private javax.swing.JButton jButton26;
    private javax.swing.JButton jButton27;
    private javax.swing.JButton jButton28;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private static javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBox13;
    private javax.swing.JCheckBox jCheckBox14;
    private javax.swing.JCheckBox jCheckBox15;
    private javax.swing.JCheckBox jCheckBox16;
    private javax.swing.JCheckBox jCheckBox17;
    private javax.swing.JCheckBox jCheckBox18;
    private static javax.swing.JCheckBox jCheckBox19;
    private static javax.swing.JCheckBox jCheckBox2;
    private static javax.swing.JCheckBox jCheckBox20;
    private static javax.swing.JCheckBox jCheckBox21;
    private static javax.swing.JCheckBox jCheckBox22;
    private javax.swing.JCheckBox jCheckBox23;
    private javax.swing.JCheckBox jCheckBox24;
    private static javax.swing.JCheckBox jCheckBox25;
    private static javax.swing.JCheckBox jCheckBox26;
    private static javax.swing.JCheckBox jCheckBox27;
    private static javax.swing.JCheckBox jCheckBox28;
    private static javax.swing.JCheckBox jCheckBox29;
    private static javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox30;
    private static javax.swing.JCheckBox jCheckBox4;
    private static javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    public javax.swing.JCheckBox jCheckBox7;
    private static javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JCheckBox jCheckBox9;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem4;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem7;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem8;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem9;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JComboBox jComboBox10;
    private javax.swing.JComboBox jComboBox11;
    private javax.swing.JComboBox jComboBox12;
    private javax.swing.JComboBox jComboBox13;
    private javax.swing.JComboBox jComboBox14;
    private javax.swing.JComboBox jComboBox15;
    private javax.swing.JComboBox jComboBox16;
    private javax.swing.JComboBox jComboBox17;
    private javax.swing.JComboBox jComboBox18;
    private javax.swing.JComboBox jComboBox19;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JComboBox jComboBox20;
    private javax.swing.JComboBox jComboBox21;
    private javax.swing.JComboBox jComboBox3;
    private javax.swing.JComboBox jComboBox4;
    private javax.swing.JComboBox jComboBox5;
    private javax.swing.JComboBox jComboBox6;
    private javax.swing.JComboBox jComboBox7;
    private javax.swing.JComboBox jComboBox8;
    private javax.swing.JComboBox jComboBox9;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JFrame jFrame4;
    private javax.swing.JFrame jFrame5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    public javax.swing.JLabel jLabel7;
    public javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu10;
    private javax.swing.JMenu jMenu12;
    private javax.swing.JMenu jMenu13;
    private javax.swing.JMenu jMenu14;
    private javax.swing.JMenu jMenu15;
    private javax.swing.JMenu jMenu16;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar4;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem20;
    private javax.swing.JMenuItem jMenuItem21;
    private javax.swing.JMenuItem jMenuItem22;
    private javax.swing.JMenuItem jMenuItem23;
    private javax.swing.JMenuItem jMenuItem24;
    private javax.swing.JMenuItem jMenuItem25;
    private javax.swing.JMenuItem jMenuItem27;
    private javax.swing.JMenuItem jMenuItem28;
    private javax.swing.JMenuItem jMenuItem29;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem30;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    public javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JSlider jSlider3;
    private static javax.swing.JSpinner jSpinner1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    public javax.swing.JTable jTable1;
    public javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    public static javax.swing.JTextField jTextField1;
    private static javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    public javax.swing.JTextField jTextField13;
    public javax.swing.JTextField jTextField14;
    private static javax.swing.JTextField jTextField15;
    private static javax.swing.JTextField jTextField16;
    private static javax.swing.JTextField jTextField17;
    private static javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private static javax.swing.JTextField jTextField4;
    private static javax.swing.JTextField jTextField5;
    public static javax.swing.JTextField jTextField6;
    private static javax.swing.JTextField jTextField7;
    public javax.swing.JTextField jTextField8;
    public javax.swing.JTextField jTextField9;
    private javax.swing.JTree jTree1;
    // End of variables declaration//GEN-END:variables
    public boolean updateOutputDirectory = false;
    public boolean openAutoSOMEresults = false;
    private boolean firstTime = true;
    private RunAutoSOME runAut;
    private DecimalFormat Format = new DecimalFormat("#");
    private DecimalFormat Format2 = new DecimalFormat("#");
    private Thread runAutoSOMEthread, renderImg, rendFlag;
    private boolean scroll=false;
    private int renderImgActiveCount=0;
    private java.util.Timer zoom = new java.util.Timer();
    private Timer timer, memory;
    private changeView cv;
    private int currComboBox1width=0;
    private Settings saveNorm;
    public double[] inputStats = new double[4]; //for current input file: [0]=# rows; [1]=# columns; [2]=maximum value; [3]=minimum value
    private int debugItor=0;
    private boolean multipleClusters=false;
    private boolean flag=false;
    public boolean GEOformat=false;
    public boolean PCLformat=false;
    private boolean slide1=false;
    private ArrayList storeMapping = new ArrayList();
    private ArrayList storeMappingB = new ArrayList();
    private ArrayList clusterRuns = new ArrayList();
    Collection threadSafeList;
    String[] pvals = new String[3];
    boolean runningUpdatePvalue=false;
    int updatePValueCounter=0;
}
