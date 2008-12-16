package Command;


import java.security.acl.Group;
import javax.swing.JList;
import Tunable.*;
import Tunable.Tunable.Param;
import Sliders.*;

public class input implements command {
		
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer1 = new Integer(10);
	
	@Tunable(description="Integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer11 = new Integer(33);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group1;	
	
	@Tunable(description="BoundedInteger", type=BoundedInteger.class,flag=Param.UseSlider)
	public BoundedInteger integer2 = new BoundedInteger(new Integer(50),new Integer(10),new Integer(200),new Boolean(true),new Boolean(true));
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group2;
	
	@Tunable(description ="Double", type=Double.class,flag=Param.Nothing)
	public Double double1 = new Double(3.4);
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group3;
	
	@Tunable(description="Boundeddouble", type=BoundedDouble.class,flag=Param.UseSlider)
	public BoundedDouble double2 = new BoundedDouble(new Double(15.4),new Double(0.0),new Double(200.0),new Boolean(true),new Boolean(true));
	
	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group4;
	
	@Tunable(description="List", type=JList.class, flag=Param.MultiSelect)
	public JList list = new JList(new Object[] {"a",2.5,new Boolean(false),1});

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group5;
	
	@Tunable(description="Boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean1 = new Boolean(false);

	@Tunable(description="Group",type=Group.class,flag=Param.Nothing )
	public Group Group6;
	
//	@Tunable(description="try with string", type=String.class, flag=Param.Nothing)
//	public String string1 = new String("cytoscape");
	
//	@Tunable(description="try with button", type=JButton.class, flag=Param.Nothing)
//	public JButton button1 = new JButton();
	
	/*
	@Tunable(description="Group", flag=NoInput, type=GROUP, available=True, lowerbound=0, upperbound=200, data={}, value="")
	public Integer Group3;	
		
	@Tunable(description="Button", flag=NoInput, type=BUTTON, available=True, lowerbound=0, upperbound=200, data={}, value="false")
	public JButton T4 = new JButton();
	*/

	//@Tunable(description="Attribute", flag=MultiSelect, type=NODEATTRIBUTE, available=TRUE, lowerbound=0, upperbound=200, data ={"aa","bb","cc","dd"}, value="")
	//public JList Attributes = new JList();

	//@Tunable(description="Attribute", flag=MultiSelect, type=EDGEATTRIBUTE, available=TRUE, lowerbound=0, upperbound=200, data ={"aa","bb","cc","dd"}, value="")
	//public JList Attributes = new JList();
	
	
}

