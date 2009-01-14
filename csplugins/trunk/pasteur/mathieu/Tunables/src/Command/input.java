package Command;

import Tunable.Tunable;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;


public class input implements command {
	

	@Tunable(description="This is an Integer with a long tunable's description to test the wrapping",group="Group1")
	public Integer integer1 = new Integer(10);
	@Tunable(description="Boolean",group="Group2")
	public Boolean boolean1 = new Boolean(false);
	@Tunable(description="Integer",group="Group2")
	public Integer integer3 = new Integer(45);
	@Tunable(description = "String",group="Group2")
	public String string1 = new String("Cytoscape");
	@Tunable(description="BoundedDouble",group="Group1")
	public BoundedDouble bounded1 = new BoundedDouble(-10.7,9.8,23.7,true,true);
	@Tunable(description="BoundedFloat",flag=Param.Slider,group="Group1")
	public BoundedFloat bounded3 = new BoundedFloat((float)-10.6,(float)14.3,(float)23.2,true,true);
	@Tunable(description="BoundedLong",flag=Param.Slider,group="Group1")
	public BoundedLong bounded4 = new BoundedLong((long)-10,(long)9,(long)100,false,true);
	@Tunable(description ="Double",group="Group1")
	public Double double1 = new Double(3.4);
	@Tunable(description="BoundedInteger",flag=Param.Nothing,group="Group1")
	public BoundedInteger bounded2 = new BoundedInteger(-5,3,10,true,false);
	@Tunable(description="Integer",group="Group1")
	public Integer integer2 = new Integer(33);
	@Tunable(description="ListSingleSelection with Strings",group="Group3")
	public ListSingleSelection<String> dayNames;
	@Tunable(description="ListMultipleSelection",group="Group3")
	public ListMultipleSelection<String> monthNames;
	@Tunable(description ="Double")
	public Double double2 = new Double(5.6);
	@Tunable(description ="Float",group="Group4")
	public Float float1 = new Float(64.9084);
	@Tunable(description ="Long",group="Group4")
	public Long long1 = new Long((long)223248997);
	@Tunable(description="ListSingleSelection with Integers",group="Group5")
	public ListSingleSelection<Integer> listOfIntegers;
	
	
	public input()
	{		
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
			testlist.add(29);
			testlist.add(26);
			testlist.add(37);
			testlist.add(34);
			listOfIntegers = new ListSingleSelection<Integer>(testlist);
	}
}

