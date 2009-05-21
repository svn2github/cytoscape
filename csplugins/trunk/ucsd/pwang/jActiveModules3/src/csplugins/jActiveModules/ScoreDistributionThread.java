package csplugins.jActiveModules;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ExpressionData;

public class ScoreDistributionThread extends Thread{
  
  CyNetwork cyNetwork;
  ActivePaths activePaths;
  ActivePathFinderParameters apfParams;
  public ScoreDistributionThread(CyNetwork cyNetwork,
				 ActivePaths activePaths,
				 ActivePathFinderParameters apfParams){
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
    JFrame mainFrame = Cytoscape.getDesktop();
    
    int numberOfRuns = 100;
    if(apfParams.getRun()){
	numberOfRuns = apfParams.getRandomIterations();
    }
    else{
	String inputValue = JOptionPane.showInputDialog(mainFrame, "Number of runs");
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
	    int result = chooser.showSaveDialog(mainFrame);
	    if(result == JFileChooser.APPROVE_OPTION){
		FileOutputStream out = new FileOutputStream(chooser.getSelectedFile());
		p = new PrintStream(out);
	    }
	    else{
		return;
	    }
	}
    }catch(Exception e){
	e.printStackTrace();
	System.out.println("Problem opening file for output");
	System.exit(-1);
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
      e.printStackTrace();
      System.err.println("Failed to join thread");
      System.exit(-1);
    }
    if(!(apfParams.getToUseMCFile())) {
      apfParams.setToUseMCFile(true);
      apfParams.setMcFileName("last.mc");
    }
				MyProgressMonitor monitor = new MyProgressMonitor(mainFrame,"Running random trials","",0,numberOfRuns);
    for (i=1; i<numberOfRuns; i++) {
      t = new Thread(activePaths);
      t.start();
      try{
	t.join();
	p.println(""+activePaths.getHighScoringPath().getScore());
      }catch(Exception e){
	e.printStackTrace();
	System.err.println("Failed to join thread");
	System.exit(-1);
      }
      monitor.update();
    }
    monitor.close();
    try{
	p.close();
    }catch(Exception e){
	e.printStackTrace();
	System.err.println("Failed to close output file");
	System.exit(-1);
    }
    activePaths.randomize = false;
    activePaths.showTable = true;
    if(apfParams.getExit()){
	System.err.println("Exiting from Score Distribution Thread");
	System.exit(0);
    }
  }
}
