
package org.cytoscape.internal.test.tunables;


import org.cytoscape.work.Tunable;


public class JustTunables {

	@Tunable(description="Enter a value:",groups="Examples of Tunables From Other Object")
	public int value = 5;

	@Tunable(description="Enter a name",groups="Examples of Tunables From Other Object")
	public String name = "Scooter";
}
