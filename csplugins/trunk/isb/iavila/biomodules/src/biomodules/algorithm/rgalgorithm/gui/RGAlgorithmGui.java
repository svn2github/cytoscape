/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/

/**
 * A graphical user interface for the algorithm implemented in <code>RGAlgorithm</code>.
 *
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */
package biomodules.algorithm.rgalgorithm.gui;

import biomodules.algorithm.rgalgorithm.*;
import biomodules.view.ViewUtils;
import biomodules.action.*;
import common.algorithms.hierarchicalClustering.*;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;
import cytoscape.util.SwingWorker;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import utils.*;
import giny.model.*;
import metaNodeViewer.model.MetaNodeFactory;
import metaNodeViewer.data.IntraDegreeComparator;
import cern.colt.list.IntArrayList;
import annotations.ui.*;
import cytoscape.data.servers.*;
import filter.cytoscape.CsFilter;
import filter.model.*;

public class RGAlgorithmGui extends JDialog {
  
  protected final static String BIOMODS_PLOT = "Tree node index vs. Num biomods";
  protected final static String DISTANCES_PLOT = "Tree node index vs. Join distance";
  protected final static int BOUNDS_ERROR = -1;
  protected final static int NOT_A_NUM_ERROR = -2;
  protected final static int UNKNOWN_BOUNDS = -3;
  protected final static boolean DEFAULT_VIEW_DATA = false;
  protected final static int APSP_TABLE = 0;
  protected final static int MD_TABLE = 1;	
  protected final static Filter dummyFilter = new DummyFilter();
  
  protected RGAlgorithmData algorithmData;
  protected JTextField minSizeField;
  protected JPanel plotPanel;
  protected HCPlot numBiomodulesPlot;
  protected HCPlot distancesPlot;
  protected JLabel yLabel;
  protected JLabel yField;
  protected JTextField xField;
  protected PlotListener numBiomodulesPlotListener;
  protected PlotListener distancesPlotListener;
  protected JRadioButton abstractRbutton;
  protected JRadioButton viewDataRadioButton;
  protected DataTable apspTable;
  protected DataTable mdTable;
  protected DataTable biomodulesTable;
  protected DisplayTableAction displayApspAction, displayMDAction;
  protected DisplayBiomodulesAction displayBiomodulesAction;
  protected ModuleAnnotationsDialog moduleAnnotsDialog;
  protected JComboBox nodesFiltersBox;
  protected JComboBox edgesFiltersBox;

  /**
   * Constructor, calls <code>create()</code>.
   *
   * @param network the <code>CyNetwork</code> for which this dialog displays options.
   */
  public RGAlgorithmGui (CyNetwork network){
    super();
    setTitle("Biomodules: " + network.getTitle());
    this.algorithmData = RGAlgorithm.getClientData(network);
    create();
  }//RGAlgorithmGui
  
  /**
   * @return a Filter object that the user selected from the filters JComboBox, can be null
   */
  public Filter getSelectedNodesFilter (){
    Filter filter = (Filter)this.nodesFiltersBox.getSelectedItem();
    return filter;
  }//getSelectedNodesFilter
  
  /**
   * @return a Filter object that the user selected from the filters JComboBox, can be null
   */
  public Filter getSelectedEdgesFilter (){
    Filter filter = (Filter)this.edgesFiltersBox.getSelectedItem();
    return filter;
  }//getSelectedEdgesFilter
  
  /**
   * Creates the dialog.
   */
  protected void create (){
    
    Container mainPanel = getContentPane();
    
    JTabbedPane tabbedPane = new JTabbedPane();
    
    JPanel paramsPanel = createParamsPanel();
    tabbedPane.add("Parameters", paramsPanel);
    
    JPanel visPanel = createVisualizationPanel();
    tabbedPane.add("Visualization", visPanel);
    
    JPanel dataPanel = createDataPanel();
    tabbedPane.add("Data", dataPanel);

    JPanel buttonsPanel = createButtonsPanel();

    mainPanel.add(tabbedPane,BorderLayout.CENTER);
    mainPanel.add(buttonsPanel,BorderLayout.PAGE_END);
    
  }//create

  /**
   * Creates and returns a panel for inputing parameters to the
   * <code>RGAlgorithm</code>.
   * 
   * @return a <code>JPanel</code>
   */
  protected JPanel createParamsPanel (){
    
    JPanel paramsPanel = new JPanel();
    paramsPanel.setLayout(new BoxLayout(paramsPanel, BoxLayout.Y_AXIS));
    
    // ------- Min number of members -------- //
    // TODO: Option for min number of proteins
    JPanel sizePanel = new JPanel();
    sizePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    JLabel minSizeLabel = new JLabel("Minimum biomodule size:");
    this.minSizeField = new JTextField(
                            Integer.toString(this.algorithmData.getMinNumMembers()),
                            3);
    minSizeField.addActionListener(
                                   new AbstractAction (){
                                     public void actionPerformed (ActionEvent e){
                                       if(readInput(minSizeField) < 0){
                                         showErrorMessageDialog("Enter a positive integer.");
                                       }
                                     }
                                   }
                                   );
    
    JLabel explanation = new JLabel("<html>If molecule-type available, biomodule size<br>is number of <b>proteins</b> within the biomodule.</html>");
    JPanel expPanel = new JPanel();
    expPanel.add(explanation);
    
    sizePanel.add(minSizeLabel);
    sizePanel.add(Box.createHorizontalStrut(3));
    sizePanel.add(minSizeField);
    paramsPanel.add(expPanel);
    paramsPanel.add(sizePanel);
    
    
    // --------- Filters -------- //
    
    JPanel filtersPanel = new JPanel();
    filtersPanel.setLayout( new BoxLayout(filtersPanel, BoxLayout.X_AXIS) );
    
    JPanel labelsPanel = new JPanel();
    GridLayout gl = new GridLayout(2,0);
    labelsPanel.setLayout(gl);
  	
    JLabel nodeFiltersLabel = new JLabel("Node Filter:");
    JLabel edgeFiltersLabel = new JLabel("Edge Filter:");
    labelsPanel.add(nodeFiltersLabel);
    labelsPanel.add(edgeFiltersLabel);

    JPanel boxesPanel = new JPanel();
    GridLayout gl2 = new GridLayout(2,0);
    boxesPanel.setLayout(gl2);
        
    FilterManager defaultManager = FilterManager.defaultManager();
    defaultManager.addFilter(dummyFilter);
    
    this.nodesFiltersBox = new JComboBox(defaultManager.getComboBoxModel());
    
    Filter nFilter = this.algorithmData.getNodeFilter();
    
    if(nFilter != null){
      this.nodesFiltersBox.setSelectedItem(nFilter);
    }else{
      this.nodesFiltersBox.setSelectedItem(dummyFilter);
    }
    
    this.edgesFiltersBox = new JComboBox(defaultManager.getComboBoxModel());
        
    Filter eFilter = this.algorithmData.getEdgeFilter();
    
    if(eFilter != null){
      this.edgesFiltersBox.setSelectedItem(eFilter);
    }else{
      this.edgesFiltersBox.setSelectedItem(dummyFilter);
    }
    
    boxesPanel.add(this.nodesFiltersBox);
    boxesPanel.add(this.edgesFiltersBox);

    filtersPanel.add(labelsPanel);
    filtersPanel.add(boxesPanel);
        
    //------------------------------- OLD CODE -------------------------------//
    //filtersButton.addActionListener(
    //	new AbstractAction() {
    //    public void actionPerformed(ActionEvent e) {
    //      if(fqiltersDialog == null){
    //        filtersDialog = new CsFilter(Cytoscape.getDesktop());
    //      }
    //      filtersDialog.show();
    //}
    // TEST: Print All Threads !
    //int numThreads = Thread.activeCount();
    //System.out.println("num threads = " + numThreads);
    //Thread[] threads = new Thread[numThreads];
    //int returnedThreads = Thread.enumerate(threads);
    //for (int i = 0; i < threads.length; i++) {
    //if(threads[i] != null){
    //	System.out.println(threads[i] + " is alive = " + threads[i].isAlive());
    //}
    //}//for i
    //    }//actionPerformed
    //  }//AbstractAction
    //  );
    //filtersPanel.add(filtersButton);
    // ---------------------------- END OF OLD CODE ----------------------------//
    paramsPanel.add(filtersPanel);
    // --------- Plots ---------- //
    JPanel optionsPanel = new JPanel();
    optionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    JLabel plotOpsLabel = new JLabel("Plot:");
    String [] plotOps = {BIOMODS_PLOT,DISTANCES_PLOT};
    JComboBox plotsOptions = new JComboBox(plotOps);
    plotsOptions.addActionListener(new PlotsOptionsListener());
    optionsPanel.add(plotOpsLabel);
    optionsPanel.add(Box.createHorizontalStrut(3));
    optionsPanel.add(plotsOptions);
    
    paramsPanel.add(optionsPanel);

    this.plotPanel = new JPanel();
    this.plotPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    this.numBiomodulesPlot = createNumBiomodulesPlot();
    this.numBiomodulesPlotListener = new PlotListener();
    this.numBiomodulesPlot.addJoinBarListener(this.numBiomodulesPlotListener);
    this.distancesPlot = createDistancesPlot();
    this.distancesPlotListener = new PlotListener();
    this.distancesPlot.addJoinBarListener(this.distancesPlotListener);
    this.plotPanel.add(this.numBiomodulesPlot.getContentPane());
    
    paramsPanel.add(this.plotPanel);
    
    JPanel xyPanel = new JPanel();
    xyPanel.setLayout(new BoxLayout(xyPanel, BoxLayout.Y_AXIS));
    JLabel treeNodeLabel = new JLabel("Tree node index:");
    int selectedJoinNumber = this.algorithmData.getCutJoinNumber();
    this.xField = new JTextField(Integer.toString(selectedJoinNumber),4);
    this.xField.addActionListener(new XFieldListener());
    JPanel xPanel = new JPanel();
    xPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    xPanel.add(treeNodeLabel);
    xPanel.add(Box.createHorizontalStrut(3));
    xPanel.add(this.xField);
    xyPanel.add(xPanel);
    
    this.yLabel = new JLabel("Num biomodules:");
    this.yField = new JLabel("N/A");
    JPanel yPanel = new JPanel();
    yPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    yPanel.add(this.yLabel);
    yPanel.add(Box.createHorizontalStrut(3));
    yPanel.add(this.yField);
    xyPanel.add(yPanel);
    
    paramsPanel.add(xyPanel);
    
    return paramsPanel;
  }//createParamsPanel

  /**
   * Creates and returns a <code>HCPlot</code> for number of biomodules at each join in the
   * hierarchical-tree contained in <code>this.algorithmData</code>. 
   */
  protected HCPlot createNumBiomodulesPlot (){
    HierarchicalClustering hClustering = this.algorithmData.getHierarchicalClustering();
    
    double [] numBiomodulesAtJoin;
    double minNumBiomods = 0;
    double maxNumBiomods = 0;
    int cutJoin = 0;
    int [] numClustersPerJoin = null;
    
    if(hClustering != null){
      numClustersPerJoin = hClustering.getNumClustersAtIterations();
    }
      
    if(numClustersPerJoin == null || numClustersPerJoin.length == 0){
      int numIterations = this.algorithmData.getNetwork().getNodeCount() - 1;
      if(numIterations <= 0){
        // HCPlot crashes if this is 0, so make it 1
        numIterations = 1;
      }
      numBiomodulesAtJoin = new double[numIterations];
      Arrays.fill(numBiomodulesAtJoin,0);
    }else{
      numBiomodulesAtJoin = new double[numClustersPerJoin.length];
      for(int i = 0; i < numClustersPerJoin.length; i++){
        numBiomodulesAtJoin[i] = (double)numClustersPerJoin[i];
      }//for i
      minNumBiomods = (double)hClustering.getMinNumClusters();
      maxNumBiomods = (double)hClustering.getMaxNumClusters();
      cutJoin = this.algorithmData.getCutJoinNumber();
    }
    
    return new HCPlot( 
                      numBiomodulesAtJoin,
                      minNumBiomods,
                      maxNumBiomods,
                      cutJoin,
                      "Tree_node_index",
                      "Num_Biomods"
                      );
  }//createNumBiomodulesPlot

  /**
   * Creates and returns a <code>HCPlot</code> for distances between children of
   * hierarchical-tree nodes in <code>this.algorithmData</code>
   */
  protected HCPlot createDistancesPlot (){
    HierarchicalClustering hClustering = this.algorithmData.getHierarchicalClustering();
    
    double [] distancesAtJoin = null;
    // The smallest distance with which two nodes in the hierarchical-tree were joined
    double minJoiningValue = 0;
    // The largest one
    double maxJoiningValue = 0;
    int cutJoin = 0;
      
    if(hClustering != null){
      distancesAtJoin = hClustering.getJoinDistances();
    }
      
    if(distancesAtJoin == null || distancesAtJoin.length == 0){
      int numIterations = this.algorithmData.getNetwork().getNodeCount() - 1;
      if(numIterations <= 0){
        // HCPlot crashes if this is 0, so make it 1
        numIterations = 1;
      }
      distancesAtJoin = new double[numIterations];
      Arrays.fill(distancesAtJoin,0);
    }else{
      minJoiningValue = hClustering.getMinimumRowJoiningValue();
      maxJoiningValue = hClustering.getMaximumRowJoiningValue();
      cutJoin = this.algorithmData.getCutJoinNumber();
    }
    
    return new HCPlot( 
                      distancesAtJoin,
                      minJoiningValue,
                      maxJoiningValue,
                      cutJoin,
                      "Tree_node_index",
                      "Join_Distance"
                      );
  }//createDistancesPlot

  /**
   * Creates and returns a panel with settings for visualizing biomodules.
   *
   * @return a <code>JPanel</code>
   */
  protected JPanel createVisualizationPanel (){
    JPanel visPanel = new JPanel();
    visPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
    this.abstractRbutton = new JRadioButton("Abstract Biomodules");
    String netID = this.algorithmData.getNetwork().getIdentifier();
    CyNetworkView netView = Cytoscape.getNetworkView(netID);
    if(netView != null){
      this.abstractRbutton.setSelected(true);
    }
    visPanel.add(this.abstractRbutton);
    return visPanel;
  }//createVisualizationPanel

  /**
   * Creates and returns a panel for showing the data that the <code>RGAlgorithm</code>
   * produces.
   *
   * @return a <code>JPanel</code>
   */
  protected JPanel createDataPanel (){
  	JPanel dataPanel = new JPanel();
  	dataPanel.setLayout(new BorderLayout());
  	
    JPanel radioPanel = new JPanel();
    
  	this.viewDataRadioButton = 
      new JRadioButton("<html>Select this button <b>before</b> calculating<br>biomodules to view APSP and Manhattan data.</html>", 
                       this.algorithmData.getSaveIntermediaryData());
  	radioPanel.add(this.viewDataRadioButton);
    
    dataPanel.add(radioPanel, BorderLayout.NORTH);
  	
  	JPanel buttonsPanel = new JPanel();
  	GridLayout gl = new GridLayout(4,1);
  	gl.setVgap(5);
  	buttonsPanel.setLayout(gl);
  	
  	JButton apspButton = new JButton("Display All-Pairs-Shortest-Paths");
  	this.displayApspAction = new DisplayTableAction(APSP_TABLE);
  	apspButton.addActionListener(this.displayApspAction);
  	
  	JButton distButton = new JButton("Display Manhattan Distances");
  	this.displayMDAction = new DisplayTableAction(MD_TABLE);
  	distButton.addActionListener(this.displayMDAction);
  	
  	JButton biomodsButton = new JButton("Display Biomodules Table");
  	this.displayBiomodulesAction = new DisplayBiomodulesAction();
  	biomodsButton.addActionListener(this.displayBiomodulesAction);
  	
  	JButton annotsButton = new JButton("Find Overrepresented Annotations...");
  	AbstractAction [] annotsEdgesAction = {new DrawAnnotationEdgesAction(this.algorithmData.getNetwork()), new SaveAnnotationsToAttribute()};
  	this.moduleAnnotsDialog = new ModuleAnnotationsDialog();
  	this.moduleAnnotsDialog.setActionsForTable(annotsEdgesAction,null,null);
  	annotsButton.addActionListener(new AbstractAction(){
  		
  		public void actionPerformed (ActionEvent event){
  			
  			Map map = RGAlgorithmGui.this.algorithmData.getBiomodules(); 
  			if(map == null || map.size() == 0){
  				showErrorMessageDialog("There are no biomodules, please calculate them first.");
  				return;
  			}
  			BioDataServer server = Cytoscape.getBioDataServer();
  			if(server == null){
  				showErrorMessageDialog("There is no annotations server available.");
  				return;
  			}
  			Object [] keyIDs = map.keySet().toArray();
  			String [][] moduleMembers = new String[keyIDs.length][];
  			String [] moduleNames = new String[keyIDs.length];
  			CyNode [] metaNodes = new CyNode[keyIDs.length];
  			for(int i = 0; i < keyIDs.length; i++){
  				CyNode [] moduleNodes = (CyNode[]) map.get(keyIDs[i]);
  				moduleMembers[i] = new String[moduleNodes.length];
  				metaNodes[i] = Cytoscape.getCyNode((String)keyIDs[i]);
  				moduleNames[i] = (String)Cytoscape.getNodeAttributeValue(metaNodes[i],Semantics.COMMON_NAME);
  				for(int j = 0; j < moduleNodes.length; j++){
  					moduleMembers[i][j] = (String)Cytoscape.getNodeAttributeValue(moduleNodes[j],Semantics.CANONICAL_NAME);
  				}//for j
  			}//for i
  			
  			RGAlgorithmGui.this.moduleAnnotsDialog.setCalculatorParameters(metaNodes,moduleMembers,false);
  			RGAlgorithmGui.this.moduleAnnotsDialog.pack();
  			RGAlgorithmGui.this.moduleAnnotsDialog.setLocationRelativeTo(RGAlgorithmGui.this);
  			RGAlgorithmGui.this.moduleAnnotsDialog.setVisible(true);
  		}//actionPerformed
  	
  	});
  	
  	buttonsPanel.add(apspButton);
  	buttonsPanel.add(distButton);
  	buttonsPanel.add(biomodsButton);
  	buttonsPanel.add(annotsButton);
  	
  	dataPanel.add(buttonsPanel, BorderLayout.CENTER);
  	
    return dataPanel;
  }//createDataPanel

  /**
   * Creates and returns a panel with buttons (Calculate, Dismiss, etc).
   *
   * @return a <code>JPanel</code>
   */
  protected JPanel createButtonsPanel (){
    JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
    JButton calculateBiomodsButton = new JButton("Calculate");
    calculateBiomodsButton.addActionListener(
                                             new AbstractAction (){
                                               public void actionPerformed (ActionEvent e){
                                                 SwingWorker w = new SwingWorker(){
                                                     public Object construct (){
                                                       calculateBiomodules();
                                                       return null;
                                                     }
                                                   };
                                                 w.start();
                                               }//actionPerformed
                                             }//AbstractAction
      );
    buttonsPanel.add(calculateBiomodsButton);
    
    JButton closeWindowButton = new JButton("Dismiss");
    closeWindowButton.addActionListener(
                                        new AbstractAction (){
                                          public void actionPerformed (ActionEvent e){
                                            RGAlgorithmGui.this.dispose();
                                          }
                                        }
                                        );
    
    
    buttonsPanel.add(closeWindowButton);
    return buttonsPanel;
  }//createButtonsPanel

  /**
   * Calls <code>RGAlgorithm.calculateBiomodules()</code> and updates the plots.
   */
  protected void calculateBiomodules (){
    try{

      // Calculate the biomodules
    
      HierarchicalClustering hClustering = this.algorithmData.getHierarchicalClustering();
      CyNode [][] biomodules = null;
      
      // From the GUI, see if apsp and manhattan-distances need to be kept in memory
      this.algorithmData.setSaveIntermediaryData(this.viewDataRadioButton.isSelected());
      
      // Set any selected filters
      Filter nodeFilter = getSelectedNodesFilter();
      if(nodeFilter != null && nodeFilter != dummyFilter){
        this.algorithmData.setNodeFilter(nodeFilter);
      }
      Filter edgeFilter = getSelectedEdgesFilter();
      if(edgeFilter != null && edgeFilter != dummyFilter){
        this.algorithmData.setEdgeFilter(edgeFilter);
      }
      
      if(hClustering == null){
        biomodules = RGAlgorithm.calculateBiomodules(this.algorithmData.getNetwork());
        updatePlots();
      }else{
        biomodules = RGAlgorithm.createBiomodules(this.algorithmData);
      }
      
      // Visualize biomodules if necessary
      
      Map bioIdentifiersToMembers = new HashMap();
      CyNetwork net = this.algorithmData.getNetwork();
      
      String netID = net.getIdentifier();
      CyNetworkView netView = Cytoscape.getNetworkView(netID);
      
      if(this.abstractRbutton.isSelected() && netView != null){
        //System.out.println("this.abstractRbutton.isSelected () = true and netView exists so create meta-nodes");
        // Remove existent meta-nodes:
        IntArrayList metaNodes = (IntArrayList)net.getClientData(MetaNodeFactory.METANODES_IN_NETWORK);
        if(metaNodes != null){
          ViewUtils.removeMetaNodes(net,metaNodes.elements(),false);
        }
        // Create new meta-nodes
        int [] metaNodeRindices =  
          ViewUtils.abstractBiomodules(this.algorithmData.getNetwork(),biomodules);
        // Get the common names of the meta nodes
        int numUnknowns = 0;
        for(int i = 0; i < metaNodeRindices.length; i++){
          Node node = net.getNode(metaNodeRindices[i]);
          String canonical = (String)Cytoscape.getNodeAttributeValue(node,Semantics.CANONICAL_NAME);
          if(node == null || canonical == null){
            System.out.println("The node with index [" + metaNodeRindices[i] + "] is null");
            canonical = "unknown" + Integer.toString(numUnknowns);
            numUnknowns++;
          }
          bioIdentifiersToMembers.put(canonical,biomodules[i]);
        }//for i
        System.out.println("Done creating meta-nodes.");
      }else{
        //System.out.println("there is no view or the user does not want to abstract meta-nodes. creating names for biomodules...");
        // The Biomodules need to have an identifier, so lets make it the member with the highest intra-degree
        CyNetwork network = this.algorithmData.getNetwork();
        for(int i = 0; i < biomodules.length; i++){
          int [] memberRindices = new int[biomodules[i].length];
          for(int j = 0; j < biomodules[i].length; j++){
            memberRindices[j] = biomodules[i][j].getRootGraphIndex();
          }//for j
          SortedSet ss = IntraDegreeComparator.sortNodes(network, memberRindices);
          CyNode highestNode = (CyNode)ss.first();
    	    String alias = (String)network.getNodeAttributeValue(highestNode,Semantics.COMMON_NAME);
    	    if(alias == null){
    	      alias = (String)network.getNodeAttributeValue(highestNode,Semantics.CANONICAL_NAME);
    	    }
    	    bioIdentifiersToMembers.put(alias,biomodules[i]);
        }//for i
        //System.out.println("Done creating names for biomodules.");
      }
      
      
      this.algorithmData.setBiomodules(bioIdentifiersToMembers);
      this.displayApspAction.setUpdateNeeded(true);
      this.displayMDAction.setUpdateNeeded(true);
      this.displayBiomodulesAction.setUpdateNeeded(true);
    
    }catch (Exception exception){
      showErrorMessageDialog("<html>Error while calculating biomodules:<br>"+
                             exception.getMessage() + "</html>");
    }
    
    JOptionPane.showMessageDialog(this,
                                  "Done calculating Biomodules.",
                                  "Done!", 
                                  JOptionPane.INFORMATION_MESSAGE);
    
  }//calculateBiomodules

  /**
   * Updates the plots.
   */
  protected void updatePlots (){
    
  	String currentPlotID = getCurrentPlot();
  	
  	this.numBiomodulesPlot.removeJoinBarListener(this.numBiomodulesPlotListener);
    this.distancesPlot.removeJoinBarListener(this.distancesPlotListener);
  	this.plotPanel.removeAll();
  
    this.numBiomodulesPlot = createNumBiomodulesPlot();
    this.distancesPlot = createDistancesPlot();
    
    if(currentPlotID == null || currentPlotID.equals(BIOMODS_PLOT)){
      this.plotPanel.add(this.numBiomodulesPlot.getContentPane());
    }else if(currentPlotID.equals(DISTANCES_PLOT)){
      this.plotPanel.add(this.distancesPlot.getContentPane());
    }
    
    this.numBiomodulesPlot.addJoinBarListener(this.numBiomodulesPlotListener);
    this.distancesPlot.addJoinBarListener(this.distancesPlotListener);
    this.plotPanel.validate();
    updatePlotLabels();
  }//updatePlots

  /**
   * Updates the yLabel and the yField for the current plot.
   */
  public void updatePlotLabels (){
    
    String currentPlotID = getCurrentPlot();
    
    if(currentPlotID == null || currentPlotID.equals(BIOMODS_PLOT)){
      this.yLabel.setText("Num biomodules:");
    }else if(currentPlotID.equals(DISTANCES_PLOT)){
      this.yLabel.setText("Join distance:");
    }
    
    HierarchicalClustering hClustering = this.algorithmData.getHierarchicalClustering();
    if(hClustering == null){
      return;
    }
    
    int joinNumber = this.algorithmData.getCutJoinNumber();
    
    if(currentPlotID == null || currentPlotID.equals(BIOMODS_PLOT)){
    
      int [] numClustersPerJoin = hClustering.getNumClustersAtIterations();
      if(numClustersPerJoin == null || numClustersPerJoin.length == 0){
        return;
      }
      int numClusters = numClustersPerJoin[joinNumber];
      RGAlgorithmGui.this.yField.setText(Integer.toString(numClusters));
    
    }else if(currentPlotID.equals(DISTANCES_PLOT)){
      
      double [] distances = hClustering.getJoinDistances();
      if(distances == null || distances.length == 0){
        return;
      }
      double distance = distances[joinNumber];
      NumberFormat nf = NumberFormat.getInstance();
      nf.setMaximumFractionDigits(2);
      String text = nf.format(distance);
      RGAlgorithmGui.this.yField.setText(text);
    
    }
  
  }//updatePlotLabels

  /**
   * @return the currently displayed plot identifier (one of BIOMODS_PLOT or DISTANCES_PLOT),
   * or null if neither plot is displayed
   */
  public String getCurrentPlot (){
    Component pane = this.plotPanel.getComponent(0);
    if(pane == this.numBiomodulesPlot.getContentPane()){
      // The biomodules plot is already being displayed
      return BIOMODS_PLOT;
    }
    if(pane == this.distancesPlot.getContentPane()){
      return DISTANCES_PLOT;
    }
    System.err.println("getCurrentPlot() returning null");
    return null;
  }//getCurrentPlot

  /**
   * Pops-up an error dialog with the given message.
   *
   * @param message the message that will be displayed
   */
  protected void showErrorMessageDialog (String message){
    JOptionPane.showMessageDialog(this, 
                                  message, 
                                  "Error", 
                                  JOptionPane.ERROR_MESSAGE);
  }//showErrorMessageDialog

  /**
   * @return a negative number if there was an error:
   * <code>BOUNDS_ERROR</code> if the number entered violates its possible bounds
   * <code>NOT_A_NUM_ERROR</code> if the input is not numeric
   * <code>UNKNOWN_BOUNDS</code> can't asses whether the entered number meets bounds
   * or not (because the object to do this is null)
   * a positive number that represents the input otherwise.
   */
  protected int readInput (JTextField text_field){
    
    String text = text_field.getText();
    int input = -99;
    try{
      input = Integer.parseInt(text);
    }catch(NumberFormatException ex){
      return NOT_A_NUM_ERROR;
    }
    if(input < 0){
      return BOUNDS_ERROR;
    }
    if(text_field == this.minSizeField){
      return input;
    }else if(text_field == this.xField){
      HierarchicalClustering hClustering = this.algorithmData.getHierarchicalClustering();
      if(hClustering == null){
        return UNKNOWN_BOUNDS;
      }
      int numIntNodes = 
        hClustering.getNumInternalNodes(EisenClustering.ROWS);
      if(input < 0 || input > (numIntNodes - 1)){
        return BOUNDS_ERROR;
      }
    }
    return input;
  
  }//readInput

  //------------ Internal classes -----------//
  /**
   * Gets called when the user switches displayed plot using the combo-box.
   */
  protected class PlotsOptionsListener extends AbstractAction {
    PlotsOptionsListener (){
      super();
    }

    public void actionPerformed (ActionEvent event){
      Object source = event.getSource();
      if(!(source instanceof JComboBox)){
        return;
      }
      JComboBox comboBox = (JComboBox)source;
      String option = (String)comboBox.getSelectedItem();
      String currentPlotID = getCurrentPlot();
      if(currentPlotID == null){
        return;
      }
      
      if(option.equals(BIOMODS_PLOT) && !currentPlotID.equals(BIOMODS_PLOT)){
        
        // Show biomodules plot
        RGAlgorithmGui.this.plotPanel.removeAll();
        RGAlgorithmGui.this.plotPanel.add(
                              RGAlgorithmGui.this.numBiomodulesPlot.getContentPane());
        int joinNumber = RGAlgorithmGui.this.distancesPlot.getSelectedJoinNumber();
        if(joinNumber != RGAlgorithmGui.this.numBiomodulesPlot.getSelectedJoinNumber()){
          RGAlgorithmGui.this.numBiomodulesPlot.moveVerticalBarTo(joinNumber);
        }// if the selected join numbers are not the same
        RGAlgorithmGui.this.plotPanel.validate();
        updatePlotLabels();
        return;
      }

      if(option.equals(DISTANCES_PLOT) && !currentPlotID.equals(DISTANCES_PLOT)){
      
        // Show distances plot
        RGAlgorithmGui.this.plotPanel.removeAll();
        RGAlgorithmGui.this.plotPanel.add(RGAlgorithmGui.this.distancesPlot.getContentPane());
        int joinNumber = RGAlgorithmGui.this.numBiomodulesPlot.getSelectedJoinNumber();
        if(joinNumber != RGAlgorithmGui.this.distancesPlot.getSelectedJoinNumber()){
          RGAlgorithmGui.this.distancesPlot.moveVerticalBarTo(joinNumber);
        }// if the selected join numbers are not the same
        RGAlgorithmGui.this.plotPanel.validate();
        updatePlotLabels();
        return;
      }
      
    }//actionPerformed
  
  }//PlotsOptionsListener

  /**
   * Listens to changes in the vertical bar of a HCPlot.
   */
  protected class PlotListener extends AbstractAction{
    PlotListener (){
      super();
    }
    
    public void actionPerformed (ActionEvent event){
      
      HCPlot sourcePlot = (HCPlot)event.getSource();
      int joinNumber = sourcePlot.getSelectedJoinNumber();
      HierarchicalClustering hClustering = 
        RGAlgorithmGui.this.algorithmData.getHierarchicalClustering();
      int numIntNodes;
      if(hClustering != null){
        numIntNodes = 
          hClustering.getNumInternalNodes(EisenClustering.ROWS);
      }else{
        return;
      }
            
      if(joinNumber < 0){
        joinNumber = 0;
      }else if(joinNumber > (numIntNodes - 1) ){
        joinNumber = numIntNodes - 1; // node indeces start at 0, not 1
      }
      RGAlgorithmGui.this.xField.setText(Integer.toString(joinNumber));
      if(hClustering == null){
        return;
      }
      RGAlgorithmGui.this.algorithmData.setCutJoinNumber(joinNumber);
      updatePlotLabels();
    
    }//actionperformed
  
  }//PlotListener
  
  /**
   * Listens to input to the xField.
   */
  protected class XFieldListener extends AbstractAction {
    public XFieldListener (){
      super("");
    }//XFieldListener
    
    public void actionPerformed (ActionEvent event){
      // The user hit the 'Enter' key for the xField (maybe entered new data)
      Object source = event.getSource();
      if(!(source instanceof JTextField)){
        System.err.println("ERROR: source is not a JTextField.");
        return;
      }
      int input = readInput((JTextField)source);
      if(input < 0){
        if(input == BOUNDS_ERROR){
          HierarchicalClustering hClustering = 
            RGAlgorithmGui.this.algorithmData.getHierarchicalClustering();
          if(hClustering != null){
            int numIntNodes = hClustering.getNumInternalNodes(EisenClustering.ROWS);
            showErrorMessageDialog("Enter a number between 0 and " + (numIntNodes -1) + ".");
          }
        }else if (input == UNKNOWN_BOUNDS){
          showErrorMessageDialog("Press \"Calculate\" button.");
        }else if (input == NOT_A_NUM_ERROR){
          showErrorMessageDialog("Please enter a number.");
        }
        return;
      }
      
      // The input is correct, move the vertical bar to the join-number
      RGAlgorithmGui.this.distancesPlot.removeJoinBarListener(
                                         RGAlgorithmGui.this.distancesPlotListener);
      RGAlgorithmGui.this.numBiomodulesPlot.removeJoinBarListener(
                                         RGAlgorithmGui.this.numBiomodulesPlotListener
                                         );
      RGAlgorithmGui.this.distancesPlot.moveVerticalBarTo(input);
      RGAlgorithmGui.this.numBiomodulesPlot.moveVerticalBarTo(input);
      RGAlgorithmGui.this.distancesPlot.addJoinBarListener(
                                     RGAlgorithmGui.this.distancesPlotListener);
      RGAlgorithmGui.this.numBiomodulesPlot.addJoinBarListener(
                                     RGAlgorithmGui.this.numBiomodulesPlotListener
                                     );
      
      RGAlgorithmGui.this.algorithmData.setCutJoinNumber(input);
      updatePlotLabels();
    }//actionPerformed
  
  }//XFieldListener
  
  protected class DisplayTableAction extends AbstractAction {
  	
  	protected int type;
  	protected boolean update;
  	
  	DisplayTableAction (int type){
  		this.type = type;
  	}
  	
  	public void actionPerformed (ActionEvent event){
  		if(this.type == APSP_TABLE){ 
  			if(RGAlgorithmGui.this.apspTable == null || this.update){
  				ArrayList orderedNodes = RGAlgorithmGui.this.algorithmData.getOrderedNodes();
          if(orderedNodes == null){
            JOptionPane.showMessageDialog(RGAlgorithmGui.this,
                                          "APSP not available.",
                                          "Oops!", 
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
  				String [] nodeNames = new String[orderedNodes.size()];
  				for(int i = 0; i < nodeNames.length; i++){
  					CyNode node = (CyNode)orderedNodes.get(i);
  					nodeNames[i] = 
              (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
  				}
  				int [][] apsp = RGAlgorithmGui.this.algorithmData.getAPSP();
  				if(apsp == null || apsp.length == 0){
            JOptionPane.showMessageDialog(RGAlgorithmGui.this,
                                          "APSP not available.",
                                          "Oops!", 
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
          RGAlgorithmGui.this.apspTable = 
  					new DataTable(nodeNames,
                          nodeNames,
                          apsp,
                         ("APSP: "+RGAlgorithmGui.this.algorithmData.getNetwork().getTitle()));
  			}//if apspTable = null
  			RGAlgorithmGui.this.apspTable.pack();
  			RGAlgorithmGui.this.apspTable.setLocationRelativeTo(RGAlgorithmGui.this);
  			RGAlgorithmGui.this.apspTable.setVisible(true);
  		}//apsp table
  		else if(this.type == MD_TABLE){
  			if(RGAlgorithmGui.this.mdTable == null || this.update){
  				ArrayList orderedNodes = RGAlgorithmGui.this.algorithmData.getOrderedNodes();
  				if(orderedNodes == null){
            JOptionPane.showMessageDialog(RGAlgorithmGui.this,
                                          "Manhattan distances not available.",
                                          "Oops!", 
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
          String [] nodeNames = new String[orderedNodes.size()];
  				for(int i = 0; i < nodeNames.length; i++){
  					CyNode node = (CyNode)orderedNodes.get(i);
  					nodeNames[i] = (String)Cytoscape.getNodeAttributeValue(node, Semantics.CANONICAL_NAME);
  				}
  				double [][] distances = RGAlgorithmGui.this.algorithmData.getManhattanDistances();
          if(distances == null || distances.length == 0){
            JOptionPane.showMessageDialog(RGAlgorithmGui.this,
                                          "Manhattan Distances not available.",
                                          "Oops!", 
                                          JOptionPane.INFORMATION_MESSAGE);
            return;
          }
  				RGAlgorithmGui.this.mdTable = 
  					new DataTable(nodeNames,
  								   nodeNames,
								   distances,
								   ("Manhattan Distances: " + 
								    RGAlgorithmGui.this.algorithmData.getNetwork().getTitle()));
  			}
  			this.update = false;
  			RGAlgorithmGui.this.mdTable.pack();
  			RGAlgorithmGui.this.mdTable.setLocationRelativeTo(RGAlgorithmGui.this);
  			RGAlgorithmGui.this.mdTable.setVisible(true);
  		}//manhattan  distances table
  		
  	}//actionPerformed
  	
  	/**
  	 * Whether or not the table should be updated next time it is displayed.
  	 *
  	 * @param update
  	 */
  	public void setUpdateNeeded (boolean update){
  		this.update = update;
  	}//setUpdateNeeded
  
  }//DisplayTableAction
  
  protected class DisplayBiomodulesAction extends AbstractAction {
  	
  	protected boolean update;
  	protected final String [] colNames = {"Biomodule", "Num Members", "Members (canonical names)", "Members (common names)"};
  	
  	DisplayBiomodulesAction (){
  		super("");
  	}//constructor
  	
  	public void actionPerformed (ActionEvent event){
  		if(RGAlgorithmGui.this.biomodulesTable == null || this.update){
  			Map biomodules = RGAlgorithmGui.this.algorithmData.getBiomodules();
  			String [][] data = new String[biomodules.size()][4];
  			Set entries = biomodules.entrySet();
  			Iterator it = entries.iterator();
  			int i = 0;
  			CyNetwork net = RGAlgorithmGui.this.algorithmData.getNetwork();
  			while(it.hasNext()){
  				Map.Entry entry = (Map.Entry)it.next();
  				String biomoduleName = (String)entry.getKey();
  				CyNode [] biomoduleMembers = (CyNode[])entry.getValue();
  				data[i][0] = biomoduleName;
  				data[i][1] = Integer.toString(biomoduleMembers.length);
  				String canonicals = "";
  				String commons = "";
  				for(int j = 0; j < biomoduleMembers.length; j++){
  					String can = (String)net.getNodeAttributeValue(biomoduleMembers[j],Semantics.CANONICAL_NAME);
  					String com = (String)net.getNodeAttributeValue(biomoduleMembers[j],Semantics.COMMON_NAME);
  					if(j == 0){
  						canonicals = can;
  						commons = com;
  					}else{
  						canonicals = canonicals + "," + can;
  	  					commons = commons + "," + com;
  					}
  				}//for j
  				data[i][2] = canonicals;
  				data[i][3] = commons;
  				i++;
  			}//while it.hasNext
  			RGAlgorithmGui.this.biomodulesTable = new DataTable(null, colNames, data, ("Biomodules Table: " + net.getTitle()));
  		}// if the table is null
  		this.update = false;
  		RGAlgorithmGui.this.biomodulesTable.pack();
  		RGAlgorithmGui.this.biomodulesTable.setLocationRelativeTo(RGAlgorithmGui.this);
  		RGAlgorithmGui.this.biomodulesTable.setVisible(true);
  	}//actionPerformed
  	
  	/**
  	 * Whether or not the table should be updated next time it is displayed.
  	 * 
  	 * @param update
  	 */
  	public void setUpdateNeeded (boolean update){
  		this.update = update;	
  	}//setUpdateNeeded
  }//DisplayBiomodulesAction


}//class RGAlgorithmGui

