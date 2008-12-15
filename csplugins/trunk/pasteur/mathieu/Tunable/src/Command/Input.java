package Command;


import TunableDefinition.*;
import TunableDefinition.Tunable.Param;
import javax.swing.*;


public class Input implements Command {
		
	//Type
	final public static int INTEGER = 0;
	final public static int DOUBLE = 1;
	final public static int BOOLEAN = 2;
	final public static int STRING = 3;
	final public static int NODEATTRIBUTE = 4;
	final public static int EDGEATTRIBUTE = 5;
	final public static int LIST = 6;
	final public static int GROUP = 7;
	final public static int BUTTON = 8;
	

	@Tunable(description="try with integer", type=Integer.class,flag=Param.Nothing)
	public Integer integer1 = new Integer(10);
	
	@Tunable(description="try with boundedinteger", type=BoundedInteger.class,flag=Param.UseSlider)
	public BoundedInteger integer2 = new BoundedInteger(new Integer(50),new Integer(10),new Integer(200),new Boolean(true),new Boolean(true));
	
	@Tunable(description ="try with double", type=Double.class,flag=Param.Nothing)
	public Double double1 = new Double(3.4);
	
	@Tunable(description="try with boundeddouble", type=BoundedDouble.class,flag=Param.UseSlider)
	public BoundedDouble double2 = new BoundedDouble(new Double(15.4),new Double(0.0),new Double(200.0),new Boolean(true),new Boolean(true));
	
	@Tunable(description="try with list", type=JList.class, flag=Param.MultiSelect)
	public JList list = new JList(new Object[] {"d","d","f"});
	
	@Tunable(description="try with boolean",type=Boolean.class ,flag=Param.Nothing)
	public Boolean boolean1 = new Boolean(false);
	
	@Tunable(description="try with string", type=String.class, flag=Param.Nothing)
	public String string1 = new String("cytoscape");
	
	@Tunable(description="try with button", type=JButton.class, flag=Param.Nothing)
	public JButton button1 = new JButton();
	
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

