

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.*;
import java.util.*;

public abstract class AbstractActive implements Command {

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

	public AbstractActive() {
        List<String> l = new ArrayList<String>();
        l.add("gal1RGSig");
        l.add("gal4RGSig");
        l.add("gal80RSig");

        attrs = new ListMultipleSelection<String>(l);
	}

	//public abstract void execute();
}
