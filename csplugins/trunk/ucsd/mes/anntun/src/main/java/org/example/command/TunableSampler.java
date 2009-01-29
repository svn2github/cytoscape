

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;

public class TunableSampler implements Command {

	@Tunable(description="Test boolean value")
	public boolean testBool = false; 

//	@Tunable(description="Test bounded integer", group={"Numeric Tunables"}, flags="collapsable")
//	public BoundedInteger boundedInt = new BoundedInteger(-100,10,100,false,false);

	@Tunable(description="Test bounded integer (pretend slider)", group={"Numeric Tunables","pretend int slider"}, flags="collapsable")
	public BoundedInteger boundedInt2 = new BoundedInteger(-100,10,100,false,false);

	@Tunable(description="Test bounded double (pretend slider)", group={"Numeric Tunables","pretend double slider"})
	public BoundedDouble boundedDub = new BoundedDouble(-10.0,1.1,10.0,false,false);

	@Tunable(description="Single value edge attr", group={"Numeric Tunables","Attribute Tunables"}, flags={"collapsable"})
	public ListSingleSelection<String> edgeAttr = new ListSingleSelection<String>("interaction","canonicalName");
	
	@Tunable(description="Multi value node attr", group={"Numeric Tunables","Attribute Tunables"})
	public ListMultipleSelection<String> nodeAttr = new ListMultipleSelection<String>("canonicalName","description","SwissProtID");

	@Tunable(description="Single list", group={"Numeric Tunables","List Tunables"}, xorChildren=true)
	public ListSingleSelection<String> list1 = new ListSingleSelection<String>("Numbers","Colors","Names");
	
	@Tunable(description="Multi list", group={"Numeric Tunables","List Tunables","Colors"}, xorKey="Colors")
	public ListMultipleSelection<String> colors = new ListMultipleSelection<String>("Blue","Green","Red","Cyan","Magenta");
	@Tunable(description="Multi list", group={"Numeric Tunables","List Tunables","Names"}, xorKey="Names")
	public ListMultipleSelection<String> names = new ListMultipleSelection<String>("George","Jane","Herb","Sarah","Bill");
	@Tunable(description="Multi list", group={"Numeric Tunables","List Tunables","Numbers"}, xorKey="Numbers")
	public ListMultipleSelection<String> nums = new ListMultipleSelection<String>("one","two","3","four","V");

	@Tunable(description="Text input", group={"Numeric Tunables","String Tunables"})
	public String s1 = "";

	@Tunable(description="Immutable \"tunables\" don't make sense", group={"Numeric Tunables","String Tunables"})
	public String s2 = "existing text";


	public void execute() {
		System.out.println("executing tunable sampler");
	}
}
