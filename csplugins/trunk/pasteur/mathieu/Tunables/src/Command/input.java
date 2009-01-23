package Command;

import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;


public class input implements command {
	

	@Tunable(description = "FirstName",group="Name")
	public String string1 = new String("John");
	@Tunable(description = "LastName",group="Name")
	public String string2 = new String("Smith");

	@Tunable(description="Day",group="Today's date")
	public ListSingleSelection<String> dayNames;
	@Tunable(description="Month",group="Today's date")
	public ListMultipleSelection<String> monthNames;
	@Tunable(description="Number",group="Today's date")
	public ListSingleSelection<Integer> listOfIntegers;
	
	@Tunable(description ="Identification Code",group="Other")
	public Long long1 = new Long((long)223248997);
	@Tunable(description="Number of children",group="Other")
	public Integer integer1 = new Integer(0);
	
	@Tunable(description="Age",flag={Param.Slider},group="Birth")
	public BoundedInteger bounded2 = new BoundedInteger(0,18,130,false,true);

	@Tunable(description="How many $ for 1 euro ?",group="Euro/Dollar")
	public BoundedDouble bounded3 = new BoundedDouble((double)1,(double)1.4,(double)1.8,true,true);
	@Tunable(description="Do you like Tunables",group="Tunable Test")
	public Boolean boolean1 = new Boolean(false);
	
	@Tunable(description="Someone",flag=Param.Collapsable)
	public Group bigGroup1;
	@Tunable(description="Questions")
	public Group bigGroup2;
	
//	@Tunable(description="BoundedLong",flag=Param.Slider,group="Group1")
//	public BoundedLong bounded4 = new BoundedLong((long)-10,(long)9,(long)100,false,true);
//	@Tunable(description="BoundedDouble",group="Group1")
//	public BoundedDouble bounded1 = new BoundedDouble(-10.7,9.8,23.7,true,true);
//	@Tunable(description="Integer",group="Group2")
//	public Integer integer3 = new Integer(45);
	
	
	
	public input()
	{		
			ArrayList<String> grouplist1 = new ArrayList<String>();
			grouplist1.add("Other");
			grouplist1.add("Birth");
			grouplist1.add("Name");			
			bigGroup1 = new Group(grouplist1,false);

			ArrayList<String> grouplist2 = new ArrayList<String>();
			grouplist2.add("Euro/Dollar");
			grouplist2.add("Tunable Test");
			grouplist2.add("Today's date");
			bigGroup2 = new Group(grouplist2,true);


			java.util.List<String> days = new ArrayList<String>();
			days.add("Monday");
			days.add("Tuesday");
			days.add("Wednesday");
			days.add("Thursday");
			days.add("Friday");
			days.add("Saturday");
			days.add("Sunday");
			dayNames = new ListSingleSelection<String>(days);
			
			java.util.List<String> months = new ArrayList<String>();
			months.add("January");
			months.add("February");
			months.add("March");
			months.add("April");
			months.add("May");
			months.add("June");
			months.add("July");
			months.add("August");
			months.add("September");
			months.add("October");
			months.add("November");
			months.add("December");	
			monthNames = new ListMultipleSelection<String>(months);
			
			java.util.List<Integer> testlist = new ArrayList<Integer>();
			for(int i=1;i<=31;i++){
			testlist.add(i);}
//			testlist.add(26);
//			testlist.add(37);
//			testlist.add(34);
			listOfIntegers = new ListSingleSelection<Integer>(testlist);

	}
}

