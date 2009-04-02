package org.cytoscape.command;

import java.util.ArrayList;
import org.cytoscape.work.*;
import org.cytoscape.work.util.*;
import org.cytoscape.work.Tunable.Param;


public class input implements Command {
	
	//@Tunable(description = "FirstName",group={"Person","Identity"})
	//public String string1 = new String("John");

	//@Tunable(description = "LastName",group={"Person","Identity"})
	//public String string2 = new String("Smith");

	//@Tunable(description="Day",group={"Today's date"})
	//public ListSingleSelection<String> days = new ListSingleSelection<String>("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday");
	
	//@Tunable(description="Month",group={"Today's date"})
	//public ListMultipleSelection<String> monthNames = new ListMultipleSelection<String>("January","February","March","April","May","June","July","August","September","October","November","December");
	
	//@Tunable(description="Number",group={"Today's date"})
	//public ListSingleSelection<Integer> listOfIntegers;
	
	@Tunable(description="Age",flag={Param.slider},group={"Person","Birth"})
	public BoundedInteger bounded = new BoundedInteger(0,45,130,false,true);

	
	@Tunable(description = "Linkage",group={"Hierarchical cluster Settings",""},alignment={Param.vertical,Param.horizontal})
	public ListSingleSelection<String> linkages = new ListSingleSelection<String>("pairwise average-linkage","pairwise single-linkage","pairwise maximum-linkage","pairwise centroid-linkage");

	@Tunable(description = "Distance Metric",group={"Hierarchical cluster Settings",""})
	public ListSingleSelection<String> distances = new ListSingleSelection<String>("Euclidean Distance","City block distance","Pearson correlation","Pearson correlation, absolute value");

	@Tunable(description="Array sources",group={"Hierarchical cluster Settings","Source for array data"})
	public ListMultipleSelection<String> sources = new ListMultipleSelection<String>("node.degree","node.gal1RGexp","node.gal1RGsig","node.gal4RGexp","node.gal4RGsig","node.gal80RGexp");
	
	@Tunable(description="Cluster attributes as well as nodes",group={"Hierarchical cluster Settings"},alignment={Param.vertical})
	public Boolean bool = new Boolean(false);
	
	
	
//	@Tunable(description="Do you like Tunables",group={"Tunable Test"})
//	public boolean boolean1 = false;
//	@Tunable(description ="Identification Code",group={"Other"})
//	public Long long1 = new Long((long)223248997);
	
	public input()
	{					
			java.util.List<Integer> testlist = new ArrayList<Integer>();
			for(int i=1;i<=31;i++)testlist.add(i);
	//		listOfIntegers = new ListSingleSelection<Integer>(testlist);
	}

	public void execute() {
		System.out.println(this.getClass().getSimpleName()+" has been executed");
	}
}

