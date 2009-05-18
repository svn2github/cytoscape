

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;
import java.util.*;

public class JActiveModules implements Command {

	final String gp = "General Parameters";
	final String strat = "Strategy";
	final String sp = "Searching Parameters";

	@Tunable(description="Expression Attributes for Analysis")
	public ListMultipleSelection<String> attrs; 

	// everything else depends on attrs
	
	@Tunable(description="Number of Modules", group=gp)
	public BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);

	@Tunable(description="Overlap Threshold", group=gp)
	public BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);	

	@Tunable(description="Adjust for size?", group=gp)
	public boolean adjustForSize = true;

	@Tunable(description="Regional Scoring?", group=gp)
	public boolean regionalScoring = true;

	// strategy determines whether to present the Searching or Annealing

	@Tunable(description="Select search strategy", group={"Strategy"}, flags={"radio"}, xorChildren=true)
	public ListSingleSelection<String> strategy;


	// search

	@Tunable(description="Search depth", group={strat,sp}, xorKey="Search")
	public int depth = 1;

	@Tunable(description="Search from selected nodes?", group={strat,sp})
	public boolean searchFromSelected = true;

	@Tunable(description="Consider Max depth from start nodes?", group={strat,sp})
	public boolean maxDepth = true;

	@Tunable(description="Max depth from start nodes", group={strat,sp}, dependsOn="maxDepth=true")
	public int maxDepthFromStart = 2;


	// anneal

	@Tunable(description="Iterations", group={"Strategy","Annealing Parameters"}, xorKey="Anneal")
	public BoundedInteger iterations = new BoundedInteger(0,2500,100000000,false,false);
			
	@Tunable(description="Start Temp", group={"Strategy","Annealing Parameters"})
	public BoundedDouble startTemp = new BoundedDouble(0.0001,1.0,100.0,false,false);

	@Tunable(description="End Temp", group={"Strategy","Annealing Parameters"})
	public BoundedDouble endTemp = new BoundedDouble(0.0001,0.01,100.0,false,false);

	@Tunable(description="Quenching", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	public boolean quenching = true;

	@Tunable(description="Hubfinding", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	public boolean hubfinding = false;

	@Tunable(description="Hubfinding Value", group={"Strategy","Annealing Parameters","Annealing Extensions"}, dependsOn="hubfinding=true")
	public int hubfindingValue = 10;

	@Tunable(description="Seed Graph Options", group={"Strategy","Annealing Parameters"}, flags={"radio"})
	public ListSingleSelection<String> seedOption;

	@Tunable(description="Seed", group={"Strategy","Annealing Parameters"},dependsOn="seedOption=Non-Random Starting Graph")
	public int seed = 0;



	public JActiveModules() {
		List<String> l = new ArrayList<String>();
		l.add("gal1RGSig");
		l.add("gal4RGSig");
		l.add("gal80RSig");

		attrs = new ListMultipleSelection<String>(l);

		List<String> s = new ArrayList<String>();
		s.add("Search");
		s.add("Anneal");

		strategy = new ListSingleSelection<String>(s);

		List<String> seed = new ArrayList<String>();
		seed.add("Non-Random Starting Graph");
		seed.add("Random Based on Current Time");

		seedOption = new ListSingleSelection<String>(seed);
	}

	public void execute() {
		System.out.println("Running JActiveModules...");
		System.out.println("\t expression attribute : " + attrs.getSelectedValues()  + "\n \t number of modules : " + numMod.getValue() + "\n \t overlap Threshold : " + overlap.getValue() +
				"\n \t adjust For Size ? = " + adjustForSize + "\n \t Regional Scroing ? " + regionalScoring + "\n \t select Strategy = " + strategy.getSelectedValue() + "\n \t Search Depth = "+ depth +
				"\n \t search From Selected nodes? = "+searchFromSelected + "\n \t Consider Max Depth from start nodes = " + maxDepth + "\n \t Max depth from start nodes = " + maxDepthFromStart +
				"\n \t Iterations : "+ iterations.getValue() + "\n \t Start Temp : " + startTemp.getValue() + "\n \t end Temp : " + endTemp.getValue() + "\n \t Seed Graph Options : " + seedOption.getSelectedValue());
	}

}
