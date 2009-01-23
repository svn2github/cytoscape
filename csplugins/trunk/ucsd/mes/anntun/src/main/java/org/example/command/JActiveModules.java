

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;
import java.util.*;

public class JActiveModules implements Command {

	@Tunable(description="Expression Attributes for Analysis")
	public ListMultipleSelection<String> attrs; 

	// everything else depends on attrs
	
	@Tunable(description="Number of Modules", group={"General Parameters"})
	public BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);

	@Tunable(description="Overlap Threshold", group={"General Parameters"})
	public BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);	

	@Tunable(description="Adjust for size?", group={"General Parameters"})
	public boolean adjustForSize = true;

	@Tunable(description="Regional Scoring?", group={"General Parameters"})
	public boolean regionalScoring = true;

	// strategy determines whether to present the Searching or Annealing

	@Tunable(description="Strategy", group={"Strategy"}, flags={"radio"})
	public ListSingleSelection<String> strategy;


	// search

	@Tunable(description="Search depth", group={"Strategy","Searching Parameters"})
	public int depth = 1;

	@Tunable(description="Search from selected nodes?", group={"Strategy","Searching Parameters"})
	public boolean searchFromSelected = true;

	@Tunable(description="Consider Max depth from start nodes?", group={"Strategy","Searching Parameters"})
	public boolean maxDepth = true;

	// depends on maxDepth

	@Tunable(description="Max depth from start nodes", group={"Strategy","Searching Parameters"}, dependsOn="maxDepth=true")
	public int maxDepthFromStart = 2;


	// anneal

	@Tunable(description="Iterations", group={"Strategy","Annealing Parameters"})
	public BoundedInteger iterations = new BoundedInteger(0,2500,100000000,false,false);
			
	@Tunable(description="Start Temp", group={"Strategy","Annealing Parameters"})
	public BoundedDouble startTemp = new BoundedDouble(0.0001,1.0,100.0,false,false);

	@Tunable(description="End Temp", group={"Strategy","Annealing Parameters"})
	public BoundedDouble endTemp = new BoundedDouble(0.0001,0.01,100.0,false,false);

	@Tunable(description="Quenching", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	public boolean quenching = true;

	@Tunable(description="Hubfinding", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	public boolean hubfinding = false;

	// depends on hubfinding

	@Tunable(description="Hubfinding Value", group={"Strategy","Annealing Parameters","Annealing Extensions"}, dependsOn="hubfinding=true")
	public int hubfindingValue = 10;

	@Tunable(description="Seed Graph Options", group={"Strategy","Annealing Parameters"}, flags={"radio"})
	public ListSingleSelection<String> seedOption;

	// depends on seedOption

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
	}

}
