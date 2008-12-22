package Command;


import java.security.acl.Group;
import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import Sliders.*;
import java.util.ArrayList;
import java.util.List;
import Utils.*;


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

	/*
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group2;
	
	@Tunable(description="List", type=List.class, flag=Param.Nothing)
	public ListSingleSelection<String> dayNames;

	@Tunable(description="try with button", type=JButton.class, flag=Param.Nothing)
	public JButton button1 = new JButton();			
*/

	public input()
	{
			java.util.List<String> choices = new ArrayList<String>();
			choices.add("A");
			choices.add("B");
			choices.add("C");
			//dayNames =new ListSingleSelection<String>(choices);
	}
}

