/**  Copyright (c) 2005 Institute for Systems Biology
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
 * Determine and analyze biological networks from the observation of phenotypes 
 * under defined environmental and/or genetic perturbations.
 *
 * @author Vesteinn Thorsson
 * @author Greg Carter
 * @author Alex Rives
 * @author Paul Shannon
 * @author Iliana Avila-Campillo
 */
package phenotypeGenetics;
//TODO:  IndeterminateProgressBar
//----------------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import junit.framework.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.data.servers.*;
import cytoscape.util.*;
import cytoscape.data.Semantics;
import cytoscape.view.*;
import cern.colt.list.IntArrayList;
//----------------------------------------------------------------------------------------
public class PhenotypeGenetics extends CytoscapePlugin {

  Project project;
  PhenoEnvironment[] phenoEnvironments ;
  String[] phenoEnviroNames ; 
  Project[] projectByPhenoEnvironment ;
  
  //----------------------------------------------------------------------------------------
  public PhenotypeGenetics (){
    
    boolean pluginLoad = pluginLoadFlag(); //confirm that plug-in is to be loaded
    
    if ( pluginLoad ){
      System.out.println("pluginLoad = true");
      
      IndeterminateProgressBar pbar = 
        new IndeterminateProgressBar(Cytoscape.getDesktop(),
                                     "Progress", 
                                     "Loading data...");
      pbar.pack();
      pbar.setLocationRelativeTo(Cytoscape.getDesktop());
      pbar.setVisible(true);
      
      readProject(); // Reads project
      
      // CHECK 1:
      // we should have a bunch of unconnected nodes at this point
      int numNodes = Cytoscape.getCurrentNetwork().getNodeCount();
      int numEdges = Cytoscape.getCurrentNetwork().getEdgeCount();
      System.out.println("After calling readProject:");
      System.out.println("num nodes = " + numNodes);
      System.out.println("num edges = " + numEdges);
      
      phenoEnvironments = project.getPhenoEnvironments();
      phenoEnviroNames = project.getPhenoEnviroArray(phenoEnvironments);
      projectByPhenoEnvironment = project.separatePhenoEnvironments(phenoEnvironments);
    
      pbar.setVisible(false);
      pbar.setLabelText("Identifying genetic interactions...");
      pbar.pack();
      pbar.setVisible(true);
      
      // Added by Iliana: automatically identify genetic interactions
      HashMap phenoEnvToBoolean = new HashMap();
      for(int i = 0; i < phenoEnviroNames.length; i++){
        phenoEnvToBoolean.put(phenoEnviroNames[i], Boolean.TRUE);
      }//for i
      doIdentifyGeneticInteraction(phenoEnvToBoolean);
      
      Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new FilterDialogAction());
      
      // Added by Iliana: "About Phenotype Genetics"
      JMenuItem aboutItem = new JMenuItem("About Phenotype Genetics...");
      aboutItem.addActionListener(new AboutListener());
      Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(aboutItem);
      
      pbar.setVisible(false);
      pbar.dispose();
      
    }
  }
  //------------------------------------------------------------------------------
  class AboutListener extends AbstractAction {
    
    AboutListener (){}
    
    public void actionPerformed (ActionEvent event){
      final String nl = System.getProperty("line.separator");
      final String info =
        "Version: 1.0"+ nl +
        "Authors:"+nl+
        " Vesteinn Thorsson"+ nl +
        " Greg Carter" + nl +
        " Paul Shannon"+ nl +
        " Alex Rives" + nl +
        " Iliana Avila" + nl +
        "Organization: Institute for Systems Biology" + nl +
        "Contact: vthorsson@systemsbiology.org"+nl+
        "License: GNU General Public License";
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    info,
                                    "About Phenotype Genetics",
                                    JOptionPane.PLAIN_MESSAGE);
    }//actionPerformed
    
  }//AboutListener
  
  class FilterDialogAction extends AbstractAction {

    FilterDialogAction () {super ("Genetic Analysis...");}
    
    public void actionPerformed (ActionEvent e) {
      // Iliana: change this to my own simpler dialog
      //JDialog dialog = new PGFilterDialog (PhenotypeGenetics.this, phenoEnviroNames);
      JDialog dialog = new PGDialog(PhenotypeGenetics.this);
      dialog.pack ();
      dialog.setLocationRelativeTo(Cytoscape.getDesktop());
      dialog.setVisible(true);
    }
  }
  
  //------------------------------------------------------------------------------
  public void doUpdateNodes () {
    System.out.println("--------------Updating Nodes ---------------");
    // Update with genes found in Project but not in the network
    nodeUpdate();
    Cytoscape.getCurrentNetworkView().redrawGraph(true,true);// layout, apply vizmaps
    System.out.println("--------------Done ---------------");
  }
  
  //TODO: Code GeneticClassVisualizer
  public void doVisualize (GeneticInteraction [] interactions) {
    System.out.println("----Visualizing Genetic Interactions----");
    GeneticClassVisualizer v = new GeneticClassVisualizer(Cytoscape.getCurrentNetwork());
    v.visualize(interactions);
    System.out.println("-----------------Done ------------------");
    Cytoscape.getCurrentNetworkView().redrawGraph(false,true);//layout, apply vizmaps
  }
  
  public void doPhenoRelationDist () {
    System.out.println("--------------Distribution Interaction Types ---------------");
    //InteractionDistributionAnalyzer analyzer = 
    // new InteractionDistributionAnalyzer(cytoscapeWindow);
    //analyzer.attributeDistribution("phenoRelation");
    System.out.println("NOT IMPLEMENTED FOR C2.0 !!!!!!!!!!!!!!!!!!!!");
    System.out.println("--------------Done ---------------");
  }
  public void doTypeDist () {
    System.out.println("--------------Distribution Interaction Types ---------------");
    System.out.println("NOT IMPLEMENTED FOR C2.0 !!!!!!!!!!!!!!!!!!!!!");
    //InteractionDistributionAnalyzer analyzer = 
    // new InteractionDistributionAnalyzer(cytoscapeWindow);
    //analyzer.attributeDistribution("geneticInteractionClass");
    System.out.println("--------------Done ---------------");
  }

  public void doNodeInteractionOverlap() {
    System.out.println("------------- Nearest-Neighbor Overlaps ---------------");
    System.out.println("NOT IMPLEMENTED FOR C2.0 !!!!!!!!!!!!!!!!!!!!!!!!!!!");
    //NNOverlapCalculator overlap = new NNOverlapCalculator( this.cytoscapeWindow );
    //NNOverlapDialog overlapDialog = new NNOverlapDialog( overlap );
    //overlapDialog.pack();
    //overlapDialog.setLocationRelativeTo( PhenotypeGenetics.this.cytoscapeWindow );
    //overlapDialog.setVisible(true);
    System.out.println("--------------Done ---------------");
  }
 
  public void doMutualInfo () {
    System.out.println("------------- Mutual Information ---------------");
    MutualInfoCalculator info = new MutualInfoCalculator(Cytoscape.getCurrentNetwork());
    MutualInfoDialog infoDialog = new MutualInfoDialog(info);
    infoDialog.pack();
    infoDialog.setLocationRelativeTo(Cytoscape.getDesktop());
    infoDialog.setVisible(true);
    System.out.println("--------------Done ---------------");
  }
  
  public void doStatementMaking () {
    System.out.println("------------- Biological Statements ---------------");
    StatementCalculator stater = new StatementCalculator("geneticInteractionClass");
    BioDataServer bioDataServer = Cytoscape.getCytoscapeObj().getBioDataServer();
    if(bioDataServer == null){
      System.out.println("The bioDataServer is null.");
    }

    StatementDialog dialog = new StatementDialog(stater, bioDataServer);
    dialog.pack();
    dialog.setLocationRelativeTo(Cytoscape.getDesktop());
    dialog.setVisible(true);
    System.out.println("--------------Done ---------------");
  }
  
  public void doNodeDist () {
    System.out.println("------------- Nodal Interactions by Types ---------------");
    NodalDistributionAnalyzer nodeAnalyzer = new NodalDistributionAnalyzer();
    nodeAnalyzer.attributeDistribution(Cytoscape.getCurrentNetwork());
    System.out.println("--------------Done ---------------");
  }

  public void doBiomoduleDist () {
    System.out.println("--------------Distribution Interaction Types ---------------");
    System.out.println("NOT IMPLEMENTED FOR C2.0 !!!!!!!!!!!!!!!!!!!!!!!!!1");
    //InteractionDistributionAnalyzer analyzer = 
    // new InteractionDistributionAnalyzer(cytoscapeWindow);
    //analyzer.biomoduleDistribution();
    System.out.println("--------------Done ---------------");
  }
  
  public void doPRTree () {
    System.out.println("PhenotypeGenetics.doPRTree() is not implemented !!!!!!!!!");
    //ClassTree c = new ClassTree(this.cytoscapeWindow);
    //System.out.println("The button action is complete calling create sets");
    //c.createUniqueSets();
    //cytoscapeWindow.redrawGraph(true);
  }
  
  public void doClassTree () {
    System.out.println("PhenotypeGenetics.doClassTree() is not implemented !!!!!!!");
    //ClassTree c = new ClassTree(this.cytoscapeWindow);
    //System.out.println("The button action is complete calling create sets");
    //c.createUniqueSets();
    //c.classify();
    //cytoscapeWindow.redrawGraph(true);
  }
  
  public void doApspWindow () {
    System.out.println("PhenotypeGenetics.doApspWindow() is not implemented !!!!!!!");
    //ApspAnalyzer a = new ApspAnalyzer(this.cytoscapeWindow);
  }
  
  public void doNetComparator() {
    System.out.println("PhenotypeGenetics.doNetComparator() is not implemented !!!!!!");
    // NetComparator c = new NetComparator(this.cytoscapeWindow);
  }
  
  //------------------------------------------------------------------------------
  /*
   * Tag nodes by their overall effect on phenotypes
   */
  public void doSinglePerturbation (HashMap bundleButtonsState) throws Exception
  {

    System.out.println("PhenotypeGenetics.doSinglePerturbation() "+
                       "is not implemented yet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

    // Confirm that only one box has been checked
    //int trueCount = 0;
    //int index = -1  ;
    //for (int j = 0 ; j < phenoEnvironments.length ; j++ ){
    //if ( ((Boolean)bundleButtonsState.get(phenoEnviroNames[j])).booleanValue() ){
    //  trueCount++ ;
    //  index=j;
    //}
    //}
    //if ( trueCount == 1 ){
    //SingleMutantEvaluator calculator = 
    //new SingleMutantEvaluator ( projectByPhenoEnvironment[index], cytoscapeWindow);
    //calculator.calculate ();
    // cytoscapeWindow.redrawGraph (false);
    //} else if ( trueCount == 0 ){
    //GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes ();
    //nodeAttributes.deleteAttribute("interaction");
    //} else {
    //System.out.println("This function does not accept more than one checked box.");
    //}
  }
  //------------------------------------------------------------------------------
  /*
   * Compare effect of single mutant to those associated with current node attributes
   */
  public void doSinglePerturbationComparison (HashMap bundleButtonsState) throws Exception
  {
    System.out.println("PhenotypeGenetics.doSinglePerturbationComparison() " +
                       "is not implemented yet !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    // Confirm that only one box has been checked
    //int trueCount = 0;
    //int index=-1  ; 
    //for ( int j=0 ; j<phenoEnvironments.length ; j++ ){
    //if ( ((Boolean) bundleButtonsState.get(phenoEnviroNames[j])).booleanValue() ){ 
    //  trueCount++ ;
    //  index=j; 
    //}
    //}
    //if ( trueCount == 1 ){
    //SingleMutantEvaluator calculator = 
    //   new SingleMutantEvaluator ( projectByPhenoEnvironment[index], cytoscapeWindow);
    //calculator.compare ();
    //} else if ( trueCount == 0 ){
    //GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes ();
    //nodeAttributes.deleteAttribute("interaction"); 
    //} else { 	
    //System.out.println("This function does not accept more than one checked box.");
    //}
  }
  //------------------------------------------------------------------------------
  public void doRemoveNodeAttribute (){
    System.out.println("PhenotypeGenetics.doRemoveNodeAttribute() "
                       +"is not implemented yet!!!!!!!!!!!!!!!!!!!");
    //GraphObjAttributes nodeAttributes = cytoscapeWindow.getNodeAttributes() ; 
    //nodeAttributes.deleteAttribute("interaction");
    //cytoscapeWindow.redrawGraph (false);
  }
  //------------------------------------------------------------------------------
  public void doIdentifyGeneticInteraction(HashMap bundleButtonsState)
  {
    System.out.println("--------------Determining genetic interactions------------------");
    ArrayList allInteractions = new ArrayList();
    
    for (int j = 0 ; j < phenoEnvironments.length ;j++){ 
      Boolean desired = (Boolean) bundleButtonsState.get(phenoEnviroNames[j]) ; 
      if ( desired.booleanValue() ){
        System.out.println("Considering environment " + phenoEnvironments[j]);
        GeneticInteractionCalculator calculator = 
          new GeneticInteractionCalculator(phenoEnvironments[j], 
                                           projectByPhenoEnvironment[j], 
                                           Cytoscape.getCurrentNetwork());
        try {
          GeneticInteraction [] interactions = calculator.calculate();
          for(int i = 0; i < interactions.length; i++){
            allInteractions.add(interactions[i]);
          }
        } catch (Exception e) {
          System.err.println ("--- Error in genetic interaction calculation---");
          e.printStackTrace ();
          
        }
      }
    }
    
    // Visualize the resulting interactions. Note that this will reset arrow
    // directions. If your class sets the arrow directions correctly, you should
    // not call this function.
    GeneticInteraction [] interactionsArray =
      (GeneticInteraction[])allInteractions.toArray(
                                              new GeneticInteraction[allInteractions.size()]
                                              );
    doVisualize(interactionsArray);
  }

  //------------------------------------------------------------------------------
  public void doInterpretGeneticInteraction(HashMap bundleButtonsState)
  {
    System.out.println("--------------Determining genetic interactions------------------");
    for (int j = 0; j < phenoEnvironments.length; j++){ 
      Boolean desired = (Boolean) bundleButtonsState.get(phenoEnviroNames[j]) ; 
      if ( desired.booleanValue() ){
        System.out.println("Considering environment "+phenoEnvironments[j] );
        GeneticInteractionCalculator calculator = 
          new GeneticInteractionCalculator (phenoEnvironments[j], 
                                            projectByPhenoEnvironment[j], 
                                            Cytoscape.getCurrentNetwork());
	   
        try {
          calculator.interpret();
        } catch (Exception e) {
          System.err.println ("--- Error in genetic interaction calculation---");
          e.printStackTrace ();
        }
      }
    }
    Cytoscape.getCurrentNetworkView().redrawGraph(false,true);
  }
  //------------------------------------------------------------------------------
  /*
   *  Clear edges (and attributes)
   */
  public void clear (HashMap bundleButtonsState)
  {
    CyNetwork net = Cytoscape.getCurrentNetwork();
    Iterator edgeIt = net.edgesIterator();
    ArrayList edgeList = new ArrayList();
    while(edgeIt.hasNext()){
      edgeList.add(edgeIt.next());
    }
    CyEdge [] edges = (CyEdge[])edgeList.toArray(new CyEdge[edgeList.size()]);
    
    System.out.println("--------------Removing edges and attributes------------------");
    
    // For each environment, walk through edges and collect the ones which need to be removed
    String removalTag = GeneticInteraction.ATTRIBUTE_PHENO_ENVIRONMENT_STRING;
    
    Vector edgesToRemove = new Vector () ;
    for (int j = 0; j < phenoEnvironments.length; j++){ 
      Boolean desired = (Boolean) bundleButtonsState.get(phenoEnviroNames[j]) ; 
      if (desired.booleanValue()){
        System.out.println("Considering environment "+phenoEnviroNames[j] );
        String attributeValue = phenoEnviroNames[j];

        for (int i=0;i < edges.length ;i++){
          CyEdge edge = edges[i];
          String edgeName = 
            (String)Cytoscape.getEdgeAttributeValue(edge, Semantics.CANONICAL_NAME);
          String edgeValue = (String)Cytoscape.getEdgeAttributeValue(edge, removalTag);
          if ( edgeValue != null ){ 
            if (edgeValue.equals(attributeValue)){
              edgesToRemove.add(edge);
            }
          }						       
        }//for each edge
      }// if desired.booleanValue()
    }// for each phenoEnvironment
    CyEdge [] edgesToRemoveArray = (CyEdge[])edgesToRemove.toArray (new CyEdge[0]); 
    // Remove edges, NameMappings, and Attributes
    for(int i = 0; i < edgesToRemoveArray.length; i++){
      CyEdge edge = edgesToRemoveArray[i];
      String edgeName = 
        (String)Cytoscape.getEdgeAttributeValue(edge, Semantics.CANONICAL_NAME);

      // NOT SURE HOW TO REMOVE ATTRIBUTES IN C2.0
      // Remove all attributes for that edge
      //HashMap atts = edgeAttributes.getAttributes(edgeName);
      //Set sset = atts.keySet(); 
      //String [] names = (String []) sset.toArray( new String [0]); 
      //for ( int k=0 ; k<names.length ; k++ ) {
      //System.out.println("Deleting "+names[k]+ " from " + edgeName );
      //edgeAttributes.deleteAttribute( names[k], edgeName );
      //}
      // Remove NameMapping
      //edgeAttributes.removeNameMapping( edgeName );
      
      // Hide the edge from the CyNetwork, remember that it is still in the
      // RootGraph though
      net.hideEdge(edge);
    }

    // Delete any edge attribute that has become empty ( no Name value pairs attached to it)
    //String [] remainingAttributes = edgeAttributes.getAttributeNames()  ;
    //for ( int i=0 ; i<remainingAttributes.length ; i++ ){
    //String attName = remainingAttributes[i]; 
    //if ( edgeAttributes.getAttribute(attName).isEmpty() ){
    //System.out.println("time to go, for "+attName );
    //  edgeAttributes.deleteAttribute(attName);
    //}
    //}

  }
  //------------------------------------------------------------------------------
  public void simplify ()
  {
    System.out.println("PhenotypeGenetics.simplify() not implemented yet !!!!!!!!!!");
    //NetworkReducer networkReducer = new NetworkReducer (cytoscapeWindow);
    //networkReducer.calculate ();
    //cytoscapeWindow.redrawGraph (false);
  } // simplify
  //----------------------------------------------------------------------------------------
  private String[] getProjectFilename (String[] args) {
    ArrayList strs = new ArrayList();
    int len = 0;
    int i;
    for(i=0;i<args.length;i++){
      if (args[i].equals ("--PGproject")) {
        if (i+1 > args.length) {
          //throw new IllegalArgumentException("error!  no --PGproject value");
        } else {
          strs.add(args[i+1]);
          len++;
        }
      }
    }
    if (len > 0) {
      String[] ret = new String[len];
      for(i=0;i<len;i++) {
        ret[i] = (String)strs.get(i);
      }
      return ret;
    } else {
      //throw new IllegalArgumentException("error!  no --PGproject switch");
    }
    return null;
  }
  
  //------------------------------------------------------------------------------------
  protected void readProject () {
    this.project = new Project();
    System.out.println("---------------Reading data from XML file----------");
    String[] projectXmlFiles;
    try {
      projectXmlFiles = 
        getProjectFilename(Cytoscape.getCytoscapeObj().getConfiguration().getArgs());
    }catch (IllegalArgumentException e) {
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),e.getMessage());
      return;
    }
    try {
      ProjectXmlReader reader;
      Project newProject;
      for (int i=0;i<projectXmlFiles.length;i++) {
        reader = new ProjectXmlReader(projectXmlFiles[i]);
        reader.read();
        newProject = reader.getProject();
        this.project.concatenate(newProject);
      }
    }catch (Exception e) {
      System.err.println ("--- Error reading xml file");
      e.printStackTrace();
    }
    nodeUpdate();
    // redraw graph including layout
    Cytoscape.getCurrentNetworkView().redrawGraph(true,true); 

    System.out.println("---------------Finished reading data from XML file----------");
    //return returnProject;
  }
  //---------------------------------------------------------------------------------
  /**
   * Update current set of nodes based on those found in the Project
   */
  protected void nodeUpdate ()
  {
    System.out.println("--------------- PhenotypeGenetics.nodeUpdate () ------------------");
    
    // If there is no network loaded, its identifier will be '0'
    String currentNetID = Cytoscape.getCurrentNetwork().getIdentifier();
    boolean createNewNetwork = false;
    if(currentNetID.equals("0")){
      System.out.println("Will create a new network for this project.");
      createNewNetwork = true;
    }
    
    // Get the Set of genes in the project
    HashSet genesInProject = project.getGenes();
    
    // if we are not creating a new network, then reset the current one
    if(!createNewNetwork){
      // Get graph and node attributes
      CyNetwork net = Cytoscape.getCurrentNetwork();
      Iterator nodesIt = net.nodesIterator();
      ArrayList nodeList = new ArrayList();
      while(nodesIt.hasNext()){
        nodeList.add(nodesIt.next());
      }
      CyNode [] nodes = (CyNode[])nodeList.toArray(new CyNode[nodeList.size()]);
      System.out.println("Number of nodes before creating new ones = " + nodes.length);
          
      // Remove existing nodes from genesInProject
      for(int i = 0; i < nodes.length; i++ ){
        String enode = 
          (String)Cytoscape.getNodeAttributeValue(nodes[i],Semantics.CANONICAL_NAME);
        genesInProject.remove(enode);
      }
    }// if !createNewNetwork

    // Create new nodes for genesInProject in the RootGraph
    // species is not yet handled correctly
    // First need (public) method to get "current" species 
    // that cytoscapeWindow should know about from -s command line flag
    String [] genes = (String [])genesInProject.toArray(new String[0]);
    System.out.println("Number of nodes in project to be created = " + genes.length);
    int nodeCounter = 0;
    IntArrayList nodeIndices = new IntArrayList();
    for (int i = 0; i < genesInProject.size(); i++){
      //System.out.println("Creating "+genes[i]);
      CyNode newNode = Cytoscape.getCyNode(genes[i],true);
      nodeIndices.add(newNode.getRootGraphIndex());
    }
    nodeIndices.trimToSize();
    
    if(createNewNetwork){
      // create a new network and a view for it
      String title = this.project.getName(); 
      CyNetwork newNetwork = 
        Cytoscape.createNetwork(nodeIndices.elements(), new int[0], title);
      CyNetworkView newNetworkView = Cytoscape.createNetworkView(newNetwork);
    }else{
      // restore the nodes we created in the current network
      CyNetwork currentNet = Cytoscape.getCurrentNetwork();
      int [] nodes = nodeIndices.elements();
      for(int n = 0; n < nodes.length; n++){
        currentNet.restoreNode(nodes[n]);
      }
    }
    
  }

  //------------------------------------------------------------------------------------
  /**
   * This is code for a workaround solution. Should be disposed of when possible.
   * The command line switch --PGnoPluginLoading sets value to false
   * causing the constructor to return empty
   */
  private boolean pluginLoadFlag () {
 
    String [] arrgs = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
      //CytoscapeConfig.getArgs(); 
    ArrayList strs = new ArrayList();
    int len = 0;
    int i;
    //boolean returnVal = true;
    boolean returnVal = false; 
    for (i=0;i<arrgs.length;i++) {
      //System.out.println("-----" + arrgs[i] + "-----");
      //if (arrgs[i].equals ("--PGnoPluginLoading")){
      //returnVal = false; 
      //}
      if(arrgs[i].equals("--PGproject")){
        return true;
      }
    }
    System.out.println("NOT LOADING PhenotypeGenetics (no --PGproject argument)!!!");
    return returnVal; 
  }// pluginLoadFlag
  
} // class PhenotypeGenetics
