package ucsd.rmkelley.BetweenPathway;
import java.io.*;
import java.util.*;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
public class BetweenPathwayParser{
  protected boolean run = false;
  protected boolean exit = true;
  protected boolean generateCutoff = false;
  protected boolean generateResults = false;
  protected File outputFile;
  public BetweenPathwayParser(String [] args, BetweenPathwayOptions options){
    for(int idx = 0;idx<args.length;idx++){
      if(args[idx].startsWith("--BP")){
	if(args[idx].endsWith("run")){
	  run = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("run = "+run);
	}
	else if(args[idx].endsWith("exit")){
	  exit = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("exit = "+exit);
	}
	else if(args[idx].endsWith("generateCutoff")){
	  generateCutoff = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("generateCutoff = "+generateCutoff);
	}
	else if(args[idx].endsWith("generateResults")){
	  generateResults = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("generateResults = "+generateResults);
	}
	else if(args[idx].endsWith("outputFile")){
	  outputFile = new File(args[idx+1]);
	  System.err.println("outputFile = "+outputFile);
	}
	else if(args[idx].endsWith("physicalNetwork")){
	  options.physicalNetwork = getNetworkByTitle(args[idx+1]);
	  System.err.println("physicalNetwork = "+options.physicalNetwork);
	}
	else if(args[idx].endsWith("geneticNetwork")){
	  options.geneticNetwork = getNetworkByTitle(args[idx+1]);
	  System.err.println("geneticNetwork = "+options.geneticNetwork);
	}
	else if(args[idx].endsWith("physicalScores")){
	  options.physicalScores = new File(args[idx+1]);
	  System.err.println("physicalScore = "+options.physicalScores);
	}
	else if(args[idx].endsWith("geneticScores")){
	  options.geneticScores = new File(args[idx+1]);
	  System.err.println("geneticScores = "+options.geneticScores);
	}
	else if(args[idx].endsWith("alpha")){
	  options.alpha = Double.parseDouble(args[idx+1]);
	  System.err.println("alpha = "+options.alpha);
	}
	else if(args[idx].endsWith("cutoff")){
	  options.cutoff = Double.parseDouble(args[idx+1]);
	  System.err.println("cutoff = "+options.cutoff);
	}
	else if(args[idx].endsWith("beta")){
	  options.beta = Double.parseDouble(args[idx+1]);
	  System.err.println("beta = "+options.beta);
	}
	else if(args[idx].endsWith("iterations")){
	  options.iterations = Integer.parseInt(args[idx+1]);
	  System.err.println("iterations = "+options.iterations);
	}
	else if(args[idx].endsWith("physicalDirectedTypes")){
	  options.physicalDirectedTypes = Arrays.asList(args[idx+1].split(","));
	  System.err.println("physicalDirectedTypes = "+options.physicalDirectedTypes);
	}
	else if(args[idx].endsWith("geneticDirectedTypes")){
	  options.geneticDirectedTypes = Arrays.asList(args[idx+1].split(","));
	  System.err.println("geneticDirectedTypes = "+options.geneticDirectedTypes);
	}
	idx++;
      }
    }
    
  }

  public File getOutputFile(){
    return outputFile;
  }
  public boolean generateResults(){
    return generateResults;
  }

  public boolean generateCutoff(){
    return generateCutoff;
  }

  public boolean run(){
    return run;
  }

  public boolean exit(){
    return exit;
  }

    /**
   * Return the first network which matches the given 
   * title. Throws an exception if no such network exists
   */
  public CyNetwork getNetworkByTitle(String title){
    Set networkSet = Cytoscape.getNetworkSet();
    for(Iterator networkIt = networkSet.iterator();networkIt.hasNext();){
      CyNetwork cyNetwork = (CyNetwork)networkIt.next();
      if(cyNetwork.getTitle().equals(title)){
	return cyNetwork;
      }
    }
    throw new RuntimeException("No network found with title "+title+", please load this network and try again");

  }
}
