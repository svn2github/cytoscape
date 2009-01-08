package Command;

import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;


public class input<O extends Comparable<String>> implements command {
	
	@Tunable(description="This is a description for Integer",group="Group1")
	public Integer integer1 = new Integer(10);
	@Tunable(description="Boolean",group="Group2")
	public Boolean boolean1 = new Boolean(false);
	@Tunable(description="Integer",group="Group2")
	public Integer integer3 = new Integer(45);
	@Tunable(description = "String",group="Group2")
	public String string1 = new String("Cytoscape");
	@Tunable(description="Bounded",flag=Param.Double,group="Group1")
	public Bounded<String> boundObject1 = new Bounded<String>("-10","10", true, true);
	@Tunable(description ="Double",group="Group1")
	public Double double1 = new Double(3.4);
	@Tunable(description="Bounded",flag=Param.IntegerSlider,group="Group1")
	public Bounded<String> boundObject2 = new Bounded<String>("-2","40", true, true);
	@Tunable(description="Integer",group="Group1")
	public Integer integer2 = new Integer(33);
	@Tunable(description="Boolean",group="Group2")
	public Boolean boolean2 = new Boolean(true);
	@Tunable(description="ListSingleSelection",group="Group3")
	public ListSingleSelection<String> dayNames;
	@Tunable(description="ListMultipleSelection",group="Group3")
	public ListMultipleSelection<String> monthNames;
	@Tunable(description ="Double")
	public Double double2 = new Double(5.6);
	@Tunable(description ="Double")
	public Double double3 = new Double(15.9);
	@Tunable(description ="Double")
	public Double double4 = new Double(67.4);
	
	
	
	public input()
	{
			java.util.List<String> days = new ArrayList<String>();
			java.util.List<String> months = new ArrayList<String>();
			days.add("Monday");
			days.add("Tuesday");
			days.add("Wednesday");
			days.add("Thursday");
			days.add("Friday");
			days.add("Saturday");
			days.add("Sunday");
			dayNames = new ListSingleSelection<String>(days);
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
	}
}

