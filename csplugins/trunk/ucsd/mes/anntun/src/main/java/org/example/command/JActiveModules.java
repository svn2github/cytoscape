

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;
import java.util.*;

public class JActiveModules implements Command {

	@Tunable(description="Expression Attributes for Analysis")
	ListMultipleSelection<String> attrs; 

	@Tunable(description="Number of Modules", group={"General Parameters"})
	BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);

	@Tunable(description="Overlap Threshold", group={"General Parameters"})
	BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);	

	@Tunable(description="Adjust for size?", group={"General Parameters"})
	boolean adjustForSize = true;

	@Tunable(description="Regional Scoring?", group={"General Parameters"})
	boolean regionalScoring = true;

	@Tunable(description="Strategy", group={"Strategy"}, flags={"radio"})
	ListSingleSelection<String> strategy;

	@Tunable(description="Search depth", group={"Strategy","Searching Parameters"})
	int depth = 1;

	@Tunable(description="Search from selected nodes?", group={"Strategy","Searching Parameters"})
	boolean searchFromSelected = true;

	@Tunable(description="Consider Max depth from start nodes?", group={"Strategy","Searching Parameters"})
	boolean maxDepth = true;

	@Tunable(description="Max depth from start nodes", group={"Strategy","Searching Parameters"})
	int maxDepthFromStart = 2;

	@Tunable(description="Iterations", group={"Strategy","Annealing Parameters"})
	BoundedInteger iterations = new BoundedInteger(0,2500,100000000,false,false);
			
	@Tunable(description="Start Temp", group={"Strategy","Annealing Parameters"})
	BoundedDouble startTemp = new BoundedDouble(0.0001,1.0,100.0,false,false);

	@Tunable(description="End Temp", group={"Strategy","Annealing Parameters"})
	BoundedDouble endTemp = new BoundedDouble(0.0001,0.01,100.0,false,false);

	@Tunable(description="Quenching", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	boolean quenching = true;

	@Tunable(description="Hubfinding", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	boolean hubfinding = false;

	@Tunable(description="Hubfinding Value", group={"Strategy","Annealing Parameters","Annealing Extensions"})
	int hubfindingValue = 10;

	@Tunable(description="Seed Graph Options", group={"Strategy","Annealing Parameters"}, flags={"radio"})
	ListSingleSelection<String> seedOption;


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
