package csplugins.jActiveModules;

import csplugins.jActiveModules.data.ActivePathFinderParameters;

import javax.swing.*;
import javax.swing.JOptionPane;
import java.util.*;
import java.io.*;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.ExpressionData;

public class ScoreDistributionThread extends Thread{
  
  CyNetwork cyNetwork;
  ActivePaths activePaths;
  ActivePathFinderParameters apfParams;
  Random randomGenerator;
  public ScoreDistributionThread(CyNetwork cyNetwork,
				 ActivePaths activePaths,
				 ActivePathFinderParameters apfParams){
    this.cyNetwork = cyNetwork;
    this.activePaths = activePaths;
    this.apfParams = apfParams;
    randomGenerator = new Random();
  }

  /**
   * This will iteratively run the activePaths algorithm
   * randomizing hte network before each iteration. The top
   * score from each trial is recorded into a file
   */
  public void run() {
    int i, f;
    JFrame mainFrame = Cytoscape.getDesktop().getMainFrame();
    String inputValue = JOptionPane.showInputDialog(mainFrame, "Number of runs");
    int numberOfRuns = Integer.parseInt(inputValue);
    PrintStream p = null;	
    try{
      FileOutputStream out = new FileOutputStream(JOptionPane.showInputDialog(mainFrame, "Output file"));
      p = new PrintStream(out);	
    }catch(Exception e){
      e.printStackTrace();
      System.out.println("Problem opening file for output");
      System.exit(-1);
    }

    activePaths.showTable = false;
    activePaths.hideOthers = false;
    randomizeGeneNames();
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
    for (i=1; i<numberOfRuns; i++) {
      randomizeGeneNames();
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
    }
    try{
      p.close();
    }catch(Exception e){
      e.printStackTrace();
      System.err.println("Failed to close output file");
      System.exit(-1);
    }

  }

  private void randomizeGeneNames() {
    Vector newNames = new Vector();
    Vector newDescripts = new Vector();
    ExpressionData expressionData = cyNetwork.getExpressionData();
    Vector geneNames = expressionData.getGeneNamesVector();
    Vector geneDescripts = expressionData.getGeneDescriptorsVector();
    int s = geneNames.size();
    int i;
    String name, descript;
    System.out.println("size = " + s);
    while (s > 0) {
      i = randomGenerator.nextInt(s--);
      name = (String) geneNames.remove(i);
      descript = (String) geneDescripts.remove(i);
      newNames.add(name);
      newDescripts.add(descript);
      // System.out.println(name);
    }
    //System.out.println("size = " + newNames.size());
    expressionData.setGeneNames(newNames);
    expressionData.setGeneDescriptors(newDescripts);
  }
  

}
