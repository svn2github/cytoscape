package Command;


import java.awt.Button;
import java.security.acl.Group;
import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import java.util.ArrayList;
import java.util.List;


public class input<O extends Comparable<String>> implements command {
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer1 = new Integer(10);
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer2 = new Integer(33);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group1;

	@Tunable(description = "String",type=String.class,flag=Param.Nothing)
	public String string1 = new String("cytoscape");
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group2;
	
	@SuppressWarnings("unchecked")
	@Tunable(description="BoundedObject",type=Bounded.class,flag=Param.Double)
	public Bounded<O> boundObject= new Bounded<O>((O)"0", (O)"100", true, true);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group3;
	
	@Tunable(description ="Double", type=Double.class,flag=Param.Nothing)
	public Double double1 = new Double(3.4);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group4;
	
	@Tunable(description="Boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean1 = new Boolean(false);

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group5;

	@Tunable(description="ListSingleSelection", type=List.class, flag=Param.Nothing)
	public ListSingleSelection<String> dayNames;

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group6;
	
	@Tunable(description="ListMultipleSelection", type=List.class, flag=Param.MultiSelect)
	public ListMultipleSelection<String> monthNames;

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group7;
	
	@Tunable(description="Button", type=Button.class, flag=Param.Nothing)
	public myButton button1 = new myButton();

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group8;

	
	
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
			dayNames =new ListSingleSelection<String>(days);
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
			monthNames =new ListMultipleSelection<String>(months);			
	}
}

