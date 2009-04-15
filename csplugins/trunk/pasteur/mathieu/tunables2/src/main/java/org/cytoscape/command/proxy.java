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
	
	//full test on each tunable defined in the class!!!!
	public String validate(){
		if(!type.getSelectedValue().equals("direct") && !hostname.contains("cache"))
			return new String("There is a Problem");
		else return null;
	}	
}