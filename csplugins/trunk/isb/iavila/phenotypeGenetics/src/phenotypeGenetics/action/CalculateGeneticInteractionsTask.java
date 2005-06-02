/**
 * An action that reads a set of XML phenotype files and calculates genetic interactions
 * from these.
 *
 * @author Iliana Avila-Campillo
 * @version 2.0
 */

package phenotypeGenetics.action;

import phenotypeGenetics.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import cytoscape.*;
import cytoscape.util.*;
import cytoscape.view.*;
import cern.colt.list.IntArrayList;

public class CalculateGeneticInteractionsTask implements MonitoredTask{

  /**
   * An object that contains members variables to keep track of
   * task progress for progress bars
   */
  protected TaskProgress taskProgress;
  /**
   * The orignal Project that contains all of the phenotype data
   */
  protected Project originalProject;
  /**
   * Data in originalProject subdivided into Projects depending on
   * their phenotype environment
   */
  protected Project [] projectsByPhenoEnviro;
  /**
   * The PhenoEnvironments (iliana wrote: I don't understand PhenoEnvironment)
   */
  protected PhenoEnvironment [] phenoEnviros;
  /**
   * Whether or not a new graph should be created each time genetic
   * interactions are calculated
   */
  protected boolean createNewGraph;
  
  /**
   * Constructor
   * 
   * @param project the Project
   * @param create_new_graph  whether or not the graph should be a new one or if
   * it should be added to the currently displayed graph in Cytoscape
   */
  public CalculateGeneticInteractionsTask (Project project,
                                           boolean create_new_graph){
    this.taskProgress = new TaskProgress();
    // Set up phenotype environments
    this.originalProject = project;
    this.phenoEnviros = project.getPhenoEnvironments();
    this.projectsByPhenoEnviro = project.separatePhenoEnvironments(this.phenoEnviros);
    this.taskProgress.taskLength = 
      GeneticInteractionCalculator.getLengthOfTask(this.projectsByPhenoEnviro);
    this.createNewGraph = create_new_graph;
  }//CalculateGeneticInteractionsTask

  
  /**
   * Calls <code>calculateInteractionsAndDisplayGraph</code>
   */
  public void actionPerformed (ActionEvent event){
    SwingWorker worker =
      new SwingWorker (){
        public Object construct (){
          calculateInteractionsAndDisplayGraph(originalProject,
                                               projectsByPhenoEnviro,
                                               phenoEnviros,
                                               createNewGraph,
                                               taskProgress);
          return null;
        }//construct
      };//SwingWorker

      worker.start();
  }//actionPerfomed

  /**
   * Reads the XML project files, creates nodes from the genes in these files, and
   * then calculates the genetic interactions between these nodes.
   *
   * @param xml_project_files the files from which phenotype data is to be read
   * @param create_new_graph whether or not the graph should be a new one or if
   * it should be added to the currently displayed graph in Cytoscape
   */
  public static void calculateInteractionsAndDisplayGraph (Project original_project,
                                                           Project [] projects,
                                                           PhenoEnvironment [] pheno_enviros,
                                                           boolean create_new_graph,
                                                           TaskProgress task_progress){
    
    
    IndeterminateProgressBar pbar = 
      new IndeterminateProgressBar(Cytoscape.getDesktop(),
                                   "Progress", 
                                   "Setting up nodes from data...");
    pbar.pack();
    pbar.setLocationRelativeTo(Cytoscape.getDesktop());
    pbar.setVisible(true);

    // Create nodes from phenotype data
    CyNetwork cyNet = createNodes(original_project, create_new_graph);
    pbar.setVisible(false);
       
    // Calculate the genetic interactions:
    int [] newEdges = new int[0];
    
    try{
      newEdges = 
        GeneticInteractionCalculator.calculateInteractions(cyNet,
                                                           projects,
                                                           pheno_enviros,
                                                           task_progress);
    }catch(IllegalArgumentException exception){
      pbar.dispose();
      task_progress.done = true;
      exception.printStackTrace();
      JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
                                    "<html>Error while calculating interactions:<br>" +
                                    exception.getMessage() + "</html>");
    }//catch
        
    pbar.setLabelText("Drawing " + newEdges.length + " genetic interactions...");
    pbar.pack();
    pbar.setVisible(true);
    cyNet.restoreEdges(newEdges);
    pbar.dispose();
  }//calculateInteractionsAndDisplayGraph
    
  /**
   * If create_new_graph is true, then this method creates a new <code>CyNetwork</code>
   * with newly created nodes obtained from the given <code>Project</code> and returns it.
   * If create_new_graph is false, then this method adds new nodes to the current 
   * <code>CyNetwork</code> and returns it.
   */
  protected static CyNetwork createNodes (Project project, boolean create_new_graph){
    
    // Get the Set of genes in the project
    HashSet genesInProject = project.getGenes();
        
    // Create new nodes for genesInProject in the RootGraph
    // species is not yet handled correctly
    // First need (public) method to get "current" species 
    // that cytoscapeWindow should know about from -s command line flag
    String [] genes = (String [])genesInProject.toArray(new String[0]);
    
    int nodeCounter = 0;
    IntArrayList nodeIndices = new IntArrayList();
    for (int i = 0; i < genesInProject.size(); i++){
      CyNode newNode = Cytoscape.getCyNode(genes[i], true);
      nodeIndices.add(newNode.getRootGraphIndex());
    }
    nodeIndices.trimToSize();
    
    CyNetwork cyNetwork = null;
    
    if(create_new_graph){
      // Create a new network and a view for it
      String title = project.getName(); 
      Set allNets = Cytoscape.getNetworkSet();
      Iterator it = allNets.iterator();
      // the title probably followed by a '_<number>'
      String pattern = title + "(_[0-9]+)?";
      int maxNetNum = -1;
      while(it.hasNext()){
        CyNetwork cn = (CyNetwork)it.next();
        String netTitle = cn.getTitle();
        if(netTitle.matches(pattern)){
          System.out.println(netTitle + " matches " + pattern);
          int index = netTitle.lastIndexOf("_");
          if(index == -1 && maxNetNum < 0){
            // matches the title and does not have an ending "_"
            maxNetNum = 0;
            continue;
          }else{
            String sub = netTitle.substring(index+1);
            try{
              // parse an int
              int num = Integer.parseInt(sub);
              if(num > maxNetNum){
                maxNetNum = num;
              }
            }catch (NumberFormatException ex){
              continue;
            }//catch
          }//else (index != -1)
        }//match
      }//while it
      
      if(maxNetNum != -1){
        title = title + "_" + Integer.toString(maxNetNum+1);
      }
     
      cyNetwork = Cytoscape.createNetwork(nodeIndices.elements(), new int[0], title);
    
    }else{
      // Get the current network
      cyNetwork = Cytoscape.getCurrentNetwork();
      cyNetwork.restoreNodes(nodeIndices.elements());
    }
    
    CyNetworkView netView = Cytoscape.getNetworkView(cyNetwork.getIdentifier());
    if(netView == null){
      netView = Cytoscape.createNetworkView(cyNetwork);
    }
    Cytoscape.getDesktop().setFocus(cyNetwork.getIdentifier());
    netView.redrawGraph(true,true); // layout and vizmaps
    
    return cyNetwork;
  
  }//createNodes

  // ----- MonitoredTask methods ------- //
 
  /**
   * Gets called by the CytoscapeProgressMonitor when the user click 
   * on the Cancel button.
   */
  public void cancel (){
    
  }//cancel
  
  /**
   * @return true if the task is done.
   */
  public boolean done (){
    return this.taskProgress.done;
  }//done
  
  /**
   * @return the currentProgress parameter.
   */
  public int getCurrent (){
    return (int)this.taskProgress.currentProgress;
  }

  /**
   * @return the lenghtOfTask parameter.
   */
  public int getLengthOfTask (){
    return (int)this.taskProgress.taskLength;
  }
          
  /**
   * @return a String, possibly the message to be printed on a dialog.
   */
  public String getMessage (){
    return this.taskProgress.message;
  }
  
  /**
   * @return a String, possibly the message that describes the task being performed.
   */
  public String getTaskName (){
    return this.taskProgress.taskName;
  }
  
  /**
   * @return Initializes currentProgress (generally to zero) and then spawns a 
   * SwingWorker to start doing the work.
   */
  public void go (boolean wait){
    
    this.taskProgress.currentProgress = 0;
    this.taskProgress.done = false;

    SwingWorker worker = new SwingWorker (){

        public Object construct (){

          calculateInteractionsAndDisplayGraph(originalProject,
                                               projectsByPhenoEnviro,
                                               phenoEnviros,
                                               createNewGraph,
                                               taskProgress);
          return null;
        }//construct
        
      };
    
    worker.start();
    
    if(wait){
      worker.get();
    }
    
  }//go
         
  /**
   * Increments the progress by one
   */
  public void incrementProgress (){}

  /**
   * Stops the task by simply setting currentProgress to lengthOfTask, or if a boolean 
   * "done" variable is used, setting it to true.
   */
   
  public void stop (){
    this.taskProgress.done = true;
  }
  
  /**
   *
   */
  public boolean wasCanceled (){
    return false;
  }

}//CalculateGeneticInteractionsTask
