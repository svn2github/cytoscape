package Command;

import Tunable.Tunable;
import Utils.*;
import java.util.*;

import org.cytoscape.command.command;

public class AbstractActive implements command {

	@Tunable(description="Expression Attributes for Analysis")
	public ListMultipleSelection<String> attrs; 

	@Tunable(description="Number of Modules", group={"General Parameters"})
	public BoundedInteger numMod = new BoundedInteger(0,5,1000,false,false);

	@Tunable(description="Overlap Threshold", group={"General Parameters"})
	public BoundedDouble overlap = new BoundedDouble(0.0,0.8,1.0,false,false);	

	@Tunable(description="Adjust for size?", group={"General Parameters"})
	public boolean adjustForSize = true;

	@Tunable(description="Regional Scoring?", group={"General Parameters"})
	public Boolean regionalScoring = new Boolean(true);

	public AbstractActive() {
        List<String> l = new ArrayList<String>();
        l.add("gal1RGSig");
        l.add("gal4RGSig");
        l.add("gal80RSig");

        attrs = new ListMultipleSelection<String>(l);
	}
	
	public void execute() {
		System.out.println(this.getClass().getSimpleName()+" has been executed");
	}
}
