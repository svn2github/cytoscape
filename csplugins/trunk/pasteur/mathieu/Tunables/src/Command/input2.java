package Command;


import java.security.acl.Group;
import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;


public class input2<O extends Comparable<String>> implements command {
	
	@Tunable(description="Integer",group="")
	public Integer integer1 = new Integer(10);
	
	@Tunable(description="Integer",group="")
	public Integer integer2 = new Integer(33);
	
	@Tunable(description="Group",group="")
	public Group Group1;

	@Tunable(description = "String",group="")
	public String string1 = new String("cytoscape");
	
	@Tunable(description="Group",group="")
	public Group Group2;
	
	@Tunable(description="BoundedObject",flag=Param.Double,group="")
	public Bounded<String> boundObject = new Bounded<String>("0","100", true, true);
	
	@Tunable(description="Group",group="")
	public Group Group3;
	
	@Tunable(description ="Double",group="")
	public Double double1 = new Double(3.4);
	
	@Tunable(description="Group",group="")
	public Group Group4;
	
	@Tunable(description="Boolean",group="")
	public Boolean boolean1 = new Boolean(false);

	@Tunable(description="Group",group="")
	public Group Group5;

	@Tunable(description="ListSingleSelection",group="")
	public ListSingleSelection<String> dayNames;

	@Tunable(description="Group",group="")
	public Group Group6;
	
	@Tunable(description="ListMultipleSelection",group="")
	public ListMultipleSelection<String> monthNames;

	@Tunable(description="Group",group="")
	public Group Group7;
	
	@Tunable(description="Button",group="")
	public myButton button1 = new myButton();

	@Tunable(description="Group",group="")
	public Group Group8;

	
	
	public input2()
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

