package csplugins.jActiveModules;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import csplugins.jActiveModules.data.ActivePathFinderParameters;

import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.model.CyNetwork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import cytoscape.data.ExpressionData;


public class ScoreDistributionThread extends Thread{

  private static final Logger logger = LoggerFactory.getLogger(ScoreDistributionThread.class);
  CyNetwork cyNetwork;
  ActivePaths activePaths;
  ActivePathFinderParameters apfParams;
  private final JFrame desktopFrame;
  
  public ScoreDistributionThread(JFrame desktopFrame, CyNetwork cyNetwork,
				 ActivePaths activePaths,
				 ActivePathFinderParameters apfParams){
    this.desktopFrame = desktopFrame;
	this.cyNetwork = cyNetwork;
    this.activePaths = activePaths;
    this.apfParams = apfParams;
  }

  

/**
   * This will iteratively run the activePaths algorithm
   * randomizing hte network before each iteration. The top
   * score from each trial is recorded into a file
   */
  public void run() {
    int i;
    
    int numberOfRuns = 100;
    if(apfParams.getRun()){
	numberOfRuns = apfParams.getRandomIterations();
    }
    else{
	String inputValue = JOptionPane.showInputDialog(desktopFrame, "Number of runs");
	numberOfRuns = Integer.parseInt(inputValue);
    }
    PrintStream p = null;	
    try{
	if(apfParams.getRun()){
	    FileOutputStream out = new FileOutputStream(apfParams.getOutputFile());
	    p = new PrintStream(out);
	}
	else{
	    JFileChooser chooser = new JFileChooser();
	    int result = chooser.showSaveDialog(desktopFrame);
	    if(result == JFileChooser.APPROVE_OPTION){
		FileOutputStream out = new FileOutputStream(chooser.getSelectedFile());
		p = new PrintStream(out);
	    }
	    else{
		return;
	    }
	}
    }catch(Exception e){
	logger.error("Problem opening file for output",e);
	return;	
    }
    
    activePaths.showTable = false;
    activePaths.hideOthers = false;
    activePaths.randomize = true;
    Thread t = new Thread(activePaths);
    t.start();
    try{
      t.join();
      p.println(""+activePaths.getHighScoringPath().getScore());
    }catch(Exception e){
      logger.error("Failed to join thread",e);
   	  return; 
    }
    if(!(apfParams.getToUseMCFile())) {
      apfParams.setToUseMCFile(true);
      apfParams.setMcFileName("last.mc");
    }
				MyProgressMonitor monitor = new MyProgressMonitor(desktopFrame,"Running random trials","",0,numberOfRuns);
    for (i=1; i<numberOfRuns; i++) {
      t = new Thread(activePaths);
      t.start();
      try{
	t.join();
	p.println(""+activePaths.getHighScoringPath().getScore());
      }catch(Exception e){
	logger.error("Failed to join thread",e);
	return;	
      }
      monitor.update();
    }
    monitor.close();
    try{
	p.close();
    }catch(Exception e){
	logger.error("Failed to close output file",e);
	return;	
    }
    activePaths.randomize = false;
    activePaths.showTable = true;
    if(apfParams.getExit()){
	logger.info("Exiting from Score Distribution Thread");
    }
  }
}
