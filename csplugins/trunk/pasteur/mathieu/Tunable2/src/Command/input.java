package Command;


import java.security.acl.Group;
import Tunable.*;
import Tunable.Tunable.Param;
import Utils.*;
import Sliders.*;

import java.util.ArrayList;
import java.util.List;





public class input implements command {
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer1 = new Integer(10);
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer11 = new Integer(33);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group1;

	@Tunable(description="List", type=List.class, flag=Param.MultiSelect)
	public List<String> list;

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group2;

	@Tunable(description ="Double", type=Double.class,flag=Param.Nothing)
	public Double double1 = new Double(3.4);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group3;
	
 	@Tunable(description="BoundedInteger", type=BoundedInteger.class,flag=Param.UseSlider)
	public BoundedInteger integer2 = new BoundedInteger(new Integer(50),new Integer(10),new Integer(200),new Boolean(true),new Boolean(true));
	
 	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group4;
 	
	@Tunable(description="Boundeddouble", type=BoundedDouble.class,flag=Param.UseSlider)
	public BoundedDouble double2 = new BoundedDouble(new Double(15.4),new Double(0.0),new Double(200.0),new Boolean(true),new Boolean(true));

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group5;
	
	@Tunable(description="Boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean1 = new Boolean(false);

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing)
	public Group Group6;
	
/*
	@Tunable(description="try with button", type=JButton.class, flag=Param.Nothing)
	public JButton button1 = new JButton();			
*/

	public input()
	{
			java.util.List<String> choices = new ArrayList<String>();
			choices.add("A");
			choices.add("B");
			choices.add("C");
			list =new ArrayList<String>(choices);
	}
}

