
/**
 * A simple object that holds statistics for MonitorableTasks
 *
 * @author Iliana Avila-Campillo
 */

package phenotypeGenetics.action;

import phenotypeGenetics.*;
import cytoscape.util.*;

public class TaskProgress {
  
  public double taskLength;
  public double currentProgress;
  public String message;
  public String taskName;
  public boolean done;
  
  /**
   * Initializes this.taskLength and this.currentProgress to 0.
   * the message and taskName to empty strings, and done to false
   */
  public TaskProgress (){
    this.taskLength = 0;
    this.currentProgress = 0;
    this.message = "";
    this.taskName = "";
    this.done = false;
  }//TaskProgress

}//TaskProgress
