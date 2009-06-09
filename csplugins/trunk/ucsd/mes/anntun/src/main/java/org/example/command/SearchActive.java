package org.example.command;

import org.example.tunable.Tunable;


public class SearchActive extends AbstractActive {

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


	public SearchActive() {
		super();
	}

	public void execute() {
		System.out.println("Running JActiveModules SEARCH...");
	}

}
