

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;

public class JActiveModules implements Command {

//	@Tunable(description="Expression Attributes for Analysis")
//	ListMultipleSelection<String> attrs = new ListMultipleSelection<String>();
	//attrs.add("gal1RGSig");
	//attrs.add("gal4RGSig");
	//attrs.add("gal80RSig");

	@Tunable(description="Number of Modules", group={"General Parameters"})
	BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);

	@Tunable(description="Overlap Threshold", group={"General Parameters"})
	BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);	

	@Tunable(description="Adjust for size?", group={"General Parameters"})
	boolean adjustForSize = true;

	@Tunable(description="Regional Scoring?", group={"General Parameters"})
	boolean regionalScoring = true;

	@Tunable(description="Search depth", group={"Searching Parameters"})
	int depth = 1;

	@Tunable(description="Search from selected nodes?", group={"Searching Parameters"})
	boolean searchFromSelected = true;

	@Tunable(description="Consider Max depth from start nodes?", group={"Searching Parameters"})
	boolean maxDepth = true;

	@Tunable(description="Max depth from start nodes", group={"Searching Parameters"})
	int maxDepthFromStart = 2;
	
	

	public void execute() {
		System.out.println("Running JActiveModules...");
	}

}
