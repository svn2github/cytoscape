package Command;


import java.awt.Button;
import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;
import java.util.List;


public class input<O extends Comparable<String>> implements command {
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer1 = new Integer(10);
	@Tunable(description="Boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean1 = new Boolean(false);
	@Tunable(description = "String",type=String.class,flag=Param.Nothing)
	public String string1 = new String("cytoscape");
	@Tunable(description="BoundedObject",type=Bounded.class,flag=Param.Double)
	public Bounded<String> boundObject = new Bounded<String>("0","100", true, true);
	@Tunable(description ="Double", type=Double.class,flag=Param.Nothing)
	public Double double1 = new Double(3.4);
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer2 = new Integer(33);
	@Tunable(description="Boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean2 = new Boolean(true);
	@Tunable(description="ListSingleSelection", type=List.class, flag=Param.Nothing)
	public ListSingleSelection<String> dayNames;
	@Tunable(description="ListMultipleSelection", type=List.class, flag=Param.Nothing)
	public ListMultipleSelection<String> monthNames;
	@Tunable(description="Button", type=Button.class, flag=Param.Nothing)
	public myButton button1 = new myButton();

	
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

