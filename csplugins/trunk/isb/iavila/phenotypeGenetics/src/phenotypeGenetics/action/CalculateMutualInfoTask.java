/**
 * TODO: DESC
 * @author Iliana Avila
 */

package phenotypeGenetics.action;

import phenotypeGenetics.*;
import cytoscape.*;
import cytoscape.util.*;

public class CalculateMutualInfoTask implements MonitoredTask {
  
  protected TaskProgress taskProgress;
  protected CyNetwork cyNet;
  protected MutualInfo [] mutualInfos;
  
  /**
   * Constructor
   *
   * @param cy_net the CyNetwork on which to calculate Mutual Information
   */
  public CalculateMutualInfoTask (CyNetwork cy_net){
    setCyNet(cy_net);
    this.taskProgress = new TaskProgress();
    this.taskProgress.taskLength = (int)MutualInfoCalculator.getLengthOfTask(cy_net);
  }//CalculateMutualInfoTask

  /**
   * Sets the CyNetwork on which to calculate mutual information
   */
  public void setCyNet (CyNetwork cy_net){
    this.cyNet = cy_net;
  }//setCyNet

  /**
   * @return the calculated mutual information
   */
  public MutualInfo [] getMutualInfo (){
    return this.mutualInfos;
  }

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
          mutualInfos = 
            MutualInfoCalculator.findMutualInformation(cyNet, taskProgress);
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
  
}//CalculateMutualInfoTask
