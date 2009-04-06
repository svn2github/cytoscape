package org.cytoscape.command;


import org.cytoscape.work.Tunable;	
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.Tunable.Param;
import org.cytoscape.work.util.ListSingleSelection;

public class proxy implements Command,TunableValidator{

	@Tunable(description="Type")
	public ListSingleSelection<String> type = new ListSingleSelection<String>("direct","http","socks");
	
	@Tunable(description="Host name",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public String hostname="";	
	
	@Tunable(description="Port",group={""},dependsOn="type!=direct",alignment={Param.horizontal})
	public int port;
	
	public void execute(){
	}
	
	public void validate(String param){
		if(param.equals("type")){
			System.out.println("test for " + param);
		}
		if(param.equals("hostname")){
			System.out.println("test for " + param);
			if(hostname.contains("cache")) System.out.println("and the test is successful");
		}
		if(param.equals("port")){
			System.out.println("test for " + param);
		}
//		if(param.contains("cache.pasteur")){
//			System.out.println("Test Succeeded for hostname");
//			return true;
//		}
//		else{
//			System.out.println("Test Failed");
//			return false;
//		}
	}	
}