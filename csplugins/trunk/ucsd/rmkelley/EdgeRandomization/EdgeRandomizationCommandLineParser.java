package ucsd.rmkelley.EdgeRandomization;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import java.util.*;
import java.io.*;

public class EdgeRandomizationCommandLineParser{
  protected boolean run = false;
  protected boolean exit = false;
  public EdgeRandomizationCommandLineParser(String [] args, EdgeRandomizationOptions options){
    for(int idx=0;idx<args.length;idx++){
      if(args[idx].startsWith("--ER")){
	if(args[idx].endsWith("run")){
	  run = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("run = "+run);
	}
	else if(args[idx].endsWith("exit")){
	  exit = Boolean.valueOf(args[idx+1]).booleanValue();
	  System.err.println("exit = "+exit);
	}
	else if(args[idx].endsWith("iterations")){
	  options.iterations = Integer.parseInt(args[idx+1]);
	  System.err.println("iterations = "+options.iterations);
	}
	else if(args[idx].endsWith("directedTypes")){
	  options.directedTypes = new Vector(Arrays.asList(args[idx+1].split(",")));
	  System.err.println("directedTypes = "+options.directedTypes);
	}
	else if(args[idx].endsWith("network")){
	  options.currentNetwork = getNetworkByTitle(args[idx+1]);
	  System.err.println("network = "+options.currentNetwork);
	}
	else if(args[idx].endsWith("saveFile")){
	  options.saveFile = new File(args[idx+1]);
	}
	idx++;
      }
    }
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
