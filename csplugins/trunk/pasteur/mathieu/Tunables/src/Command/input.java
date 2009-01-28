package Command;

import Tunable.Tunable;
import Utils.*;
import java.util.ArrayList;
import Tunable.Tunable.Param;


public class input implements command {
	
	@Tunable(description = "FirstName",group={"Name"},flag={Param.Collapsable})
	public String string1 = new String("John");
	@Tunable(description = "LastName",group={"Name"})
	public String string2 = new String("Smith");

	@Tunable(description="Day",group={"Today's date"})
	public ListSingleSelection<String> days = new ListSingleSelection<String>("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday");

	@Tunable(description="Month",group={"Today's date"})
	public ListMultipleSelection<String> monthNames = new ListMultipleSelection<String>("January","February","March","April","May","June","July","August","September","October","November","December");
//	@Tunable(description="Number",group={"Today's date"})
//	public ListSingleSelection<Integer> listOfIntegers;
	
//	@Tunable(description ="Identification Code",group={"Other"})
//	public Long long1 = new Long((long)223248997);
	@Tunable(description="Number of children",group={"Other"})
	public Integer integer1 = new Integer(0);
	
	@Tunable(description="Age",flag={Param.Slider},group={"Birth"})
	public BoundedInteger bounded2 = new BoundedInteger(0,18,130,false,true);

//	@Tunable(description="Do you like Tunables",group={"Tunable Test"})
//	public Boolean boolean1 = new Boolean(false);

	@Tunable(description="Do you like Tunables",group={"Tunable Test"})
	public boolean boolean1 = false;
		
	
	public input()
	{					
			java.util.List<Integer> testlist = new ArrayList<Integer>();
			for(int i=1;i<=31;i++)testlist.add(i);
//			listOfIntegers = new ListSingleSelection<Integer>(testlist);
	}

	public void execute() {
		System.out.println(this.getClass().getSimpleName()+" has been executed");
	}
}

