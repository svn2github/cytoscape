/*
  File: TunablesTestTask2.java

  Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.internal.test.tunables;


import java.io.File;

import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.*;


public class TunablesTestTask3 extends AbstractTask {
	@Tunable(description="Test boolean value")
	public boolean testBool = false;

	@Tunable(description="Test bounded integer", groups={"Numeric Tunables"})
	public BoundedInteger boundedInt1 = new BoundedInteger(-100,10,100,false,false);

	@Tunable(description="Test bounded integer (pretend slider)", groups={"Numeric Tunables","pretend int slider"}, params="displayState=collapsed")
	public BoundedInteger boundedInt2 = new BoundedInteger(-100,10,100,false,false);

	@Tunable(description="Test bounded double (pretend slider)", groups={"Numeric Tunables","pretend double slider"})
	public BoundedDouble boundedDoub = new BoundedDouble(-10.0,1.1,10.0,false,false);

	@Tunable(description="Single value edge attr", groups={"Numeric Tunables","Attribute Tunables"}, params="displayState=collapsed")
	public ListSingleSelection<String> edgeAttr = new ListSingleSelection<String>("interaction","canonicalName");

	@Tunable(description="Multi value node attr", groups={"Numeric Tunables","Attribute Tunables"})
	public ListMultipleSelection<String> nodeAttr = new ListMultipleSelection<String>("canonicalName","description","SwissProtID");

	@Tunable(description="Single list", groups={"Numeric Tunables","List Tunables"}, xorChildren=true)
	public ListSingleSelection<String> list1 = new ListSingleSelection<String>("Numbers","Colors","Names");

	@Tunable(description="Multi list", groups={"Numeric Tunables","List Tunables","Colors"}, xorKey="Colors")
	public ListMultipleSelection<String> colors = new ListMultipleSelection<String>("Blue","Green","Red","Cyan","Magenta");
	@Tunable(description="Multi list", groups={"Numeric Tunables","List Tunables","Names"}, xorKey="Names")
	public ListMultipleSelection<String> names = new ListMultipleSelection<String>("George","Jane","Herb","Sarah","Bill");
	@Tunable(description="Multi list", groups={"Numeric Tunables","List Tunables","Numbers"}, xorKey="Numbers")
	public ListMultipleSelection<String> nums = new ListMultipleSelection<String>("one","two","3","four","V");

	@Tunable(description="Text input", groups={"Numeric Tunables","String Tunables"})
	public String string1 = "";

	@Tunable(description="Immutable \"tunables\" don't make sense", groups={"Numeric Tunables","String Tunables"})
	public String string2 = "existing text";


	public void run(TaskMonitor e) {
		System.out.println("Results : \n \t testBool = "+testBool+"\n \t boundedInt = "+ boundedInt1.getValue()+"\n \t boundedInt2 = "+boundedInt2.getValue()+"\n \t boundedDub = "+boundedDoub.getValue()
				+"\n \t edgeAttr = "+edgeAttr.getSelectedValue()+"\n \t nodeAttr = "+nodeAttr.getSelectedValues()+"\n \t list1 = "+list1.getSelectedValue());
	}
}
