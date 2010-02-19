//the purpose of this class is to serve
//statistics regarding zvalues
package csplugins.jActiveModules;

//imported packages
import giny.model.Node;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import cytoscape.logger.CyLogger;


/**
 * This class keeps track of all parameter style
 * statistics generated for the components. The main
 * responsibility is to determine the mean and standard
 * deviaton of the simple score of networks of various
 * sizes. This correction is used to correct the score of 
 * networks so that larger networks do not dominate
 */
public class ParamStatistics implements Serializable{
  /**
   * means[i] is the mean of networks of size i+1
   */
  private double [] means;
  /**
   * stds[i] is the standard deviation of networks of size i+1
   */
  private double [] stds;
  /**
   * The default number of iterations to use in determining the
   * mean and standard deviation
   */
  public static int DEFAULT_ITERATIONS = 2000;
  /**
   * The random source to use in determining the mean and 
   * standard deviation (when shuffling the list of nodes)
   */
  private Random rand;
  /**
   * HashMap which maps from a node to it's oneNodeZScore
   */
  //private HashMap node2Z;
  /**
   * Used to do calculations on z statistics
   */
  private ZStatistics zStats;

   
  /**
   * Creates a new parameter statistics
   * @param temp The source to use in shuffling of the list of nodes
   * @param zt The object used ot do calculations on z values
   */
  public ParamStatistics(Random temp,ZStatistics zt){
    rand = temp;
    zStats = zt;
  }
    
  /**
   * Get the maximum network size for which a mean has been determined
   * @return the maximum network size
   */
  public int getNodeNumber(){
    return means.length;
  }

    
  /**
   * Calculate the mean and standard deviation for all networks of size 1 to nodes.length
   * @param nodes Array of nodes to use for the determination
   * @param trials Number of times to repeat the scoring process to determine the stats
   */
  public void calculateMeanAndStd(Node [] nodes,int trials, int maxThreads, MyProgressMonitor progress){
    double [] overall_tallyx = new double[nodes.length];
    double [] overall_tallyx_2 = new double[nodes.length];
    means = new double[nodes.length];
    stds = new double[nodes.length];
    Arrays.fill(overall_tallyx,0);
    Arrays.fill(overall_tallyx_2,0);
		
    //for each trial, shuffle the list of nodes, and add them successively
    //to a component, rescoring after each addition. Keep track of the sum
    //of scores for each size (and the sum of hte square).
    //turn off region scoring if it is active, while calculating the 
    //statistics
    boolean oldRegion = Component.regionScoring;
    Component.regionScoring = false;
    Vector threads = new Vector();
    //create the threads
    for(int i=0;i<maxThreads;i++){
      Thread thread = new MeanAndStdThread(overall_tallyx,overall_tallyx_2,trials,nodes,progress,rand);
      //thread.start();
      threads.add(thread);
    }

    //start the threads
    Iterator it = threads.iterator();
    while(it.hasNext()){
      ((Thread)it.next()).start();
    }

    //collect the threads
    it = threads.iterator();
    while(it.hasNext()){
      try{
	((Thread)it.next()).join();
      }catch(Exception e){
	CyLogger.getLogger(ParamStatistics.class).error("Unable to join worker thread",e);
	return;	
      }
    }
    Component.regionScoring = oldRegion;
	
    //use the tally information to generate the mean and standard deviation
    //for each network size
    for(int i=0;i<nodes.length;i++){
      means[i] = overall_tallyx[i]/trials;
      double var;
      if(trials>1){
	var = (overall_tallyx_2[i]-(trials*means[i]*means[i]))/(trials-1);
      }
      else{
	var = 0;
      }
      stds[i] = Math.sqrt(var+0.0000001);
    }
    smooth_stats();
    overall_tallyx = null;
    overall_tallyx_2 = null;
    if (progress != null) {
      progress.close();
    } // end of if ()
	
  }
    
    
  /**
   * Smooths out the mean and standard deviation. I think this lets us
   * get away withe doing fewer iterations
   */
  private void smooth_stats() {
    int idealMeanWinSize = smoothing_window_size_mean(DEFAULT_ITERATIONS);
    int idealStdWinSize = smoothing_window_size_std(DEFAULT_ITERATIONS);
    smooth_anything(stds, idealStdWinSize);
    smooth_anything(means,idealMeanWinSize);
  }

  /**
   *Empirical determiation of the best smoothing window size
   *to use for standard devaition
   * @param iter number of iterations used in determingin standard deviation
   */
  private int smoothing_window_size_std(int iter) {
    // this is an empirically determined function.
    // 10000 -> 50
    // 1000 -> 200
    // 100 -> 500
    // ergo 5000/sqrt(iterations)
    // 10000 -> 50
    // 1000 -> 160
    // 100 -> 500
    int retval1 = (int) (5000.0 / Math.sqrt((double)iter));
    int retval2 = stds.length / 6;
    return (retval1>retval2?retval2:retval1);
  }
    
   
  /**
   *Empirical determiation of the best smoothing window size
   *to use for mean
   * @param iter the number of iterations used in determining mean?
   */
  private int smoothing_window_size_mean(int iter) {
    // this is an empirically determined function.
    // 10000 -> 1
    // 1000 -> 3
    // 100 -> 10
    // ergo 100/sqrt(iterations)
    // 10000 -> 0
    // 1000 -> 3
    // 100 -> 10 or thereabouts
    int retval1 = (int) (100.0 / Math.sqrt((double)iter));
    int retval2 = stds.length / 6;
    return (retval1>retval2?retval2:retval1);
  }

  
     
  /**
   * Given a window size and an array of data, this function will smooth
   * it out
   * @param toBeSmoothed the data that will be smoothed
   * @param idealWindowSize the window size to use for smoothing
   */
  private void smooth_anything(double [] toBeSmoothed, int idealWindowSize) {
    //didn't write this function so I can't really provide any meaningful
    //comments. Took the code from (MonteCarlo.cc?) the activeModules C++
    //plugin
    int numberOfThings = toBeSmoothed.length;
    double [] smoothed = new double[numberOfThings];

    for (int count = 0; count < numberOfThings; count++) {
      int winSize = idealWindowSize<count?idealWindowSize:count;
      winSize = winSize<((numberOfThings-count)-1)?winSize:((numberOfThings-count)-1);
      double tempsum=0.0;
      for (int subCount = count-winSize; subCount <= count+winSize; subCount++) {
	tempsum = tempsum + toBeSmoothed[subCount];
      }
      smoothed[count] = tempsum / (2.0*((double)winSize)+1.0);
    }
    for (int count = 0; count < numberOfThings; count++) {
      toBeSmoothed[count] = smoothed[count];
    }
  }
    
  /**
   * Get the mean simple score for network of size size
   * @param size the size of the network
   */
  public double getMean(int size){
					return means[size-1];
  }
  /**
   * Get the standard deviation for network of size size
   * @param size the size of the network
   */
  public double getStd(int size){
    return stds[size-1];
  }

  public String toString(){
    String result = "";
    for(int i=0;i<means.length;i++){
      result += i + ": " + means[i] + " " + stds[i] + "\n";
    }
    return result;
  }

   
}


class MeanAndStdThread extends Thread{
  private double [] overall_tallyx;
  private double [] overall_tallyx_2;
  private int total_trials;
  private static int current_trials = 0;
  private Node[] nodes;
  private MyProgressMonitor progress;
  private Random rand;
  public MeanAndStdThread(double [] overall_tallyx,double [] overall_tallyx_2,int total_trials,Node [] nodes,MyProgressMonitor progress,Random rand){
    this.overall_tallyx = overall_tallyx;
    this.overall_tallyx_2 = overall_tallyx_2;
    this.total_trials = total_trials;
    this.nodes = nodes;
    this.progress = progress;
    this.rand = rand;
    current_trials = 0;
  }
  /**
   * Does the actual work of calculating the mean and 
   * standard deviation tallys
   */
  public void run(){
    double [] tallyx = new double[nodes.length];
    double [] tallyx_2 = new double[nodes.length];
    Arrays.fill(tallyx,0);
    Arrays.fill(tallyx_2,0);
    Vector nodeList = null;
    synchronized (nodes) {
      nodeList = new Vector(Arrays.asList(nodes));
    }
    boolean done = false;
    
    synchronized (nodes){
      if(current_trials < total_trials){
	current_trials++;
	if (progress != null) {
	  progress.update();
	} // end of if ()
      }
      else{
	done = true;
      }
    }
    
    while(!done){
      Collections.shuffle(nodeList,rand);
      Component comp = new Component();
      Iterator it = nodeList.iterator();
      int i = 0;
      while(it.hasNext()){
	comp.addNode((Node)it.next());
	double score = comp.calculateSimpleScore();
	tallyx[i] += score;
	tallyx_2[i] += score*score;
	i++;
      }
	    
      synchronized (nodes){
	if(current_trials < total_trials){
	  current_trials++;
	  if (progress != null) {
	    progress.update();
	  } // end of if ()
	}
	else{
	  done = true;
	}
      }
    }
    synchronized (overall_tallyx){
      for(int j=0;j<nodes.length;j++){
	overall_tallyx[j] += tallyx[j];
      }
    }
    synchronized (overall_tallyx_2){
      for(int j=0;j<nodes.length;j++){
	overall_tallyx_2[j] += tallyx_2[j];
      }
    }

  }
    
}
