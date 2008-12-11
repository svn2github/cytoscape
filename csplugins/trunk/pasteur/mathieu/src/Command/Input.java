package Command;


import TunableDefinition.Tunable;
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
	final public static int LIST2 = 9;
	
	// Flags
	final public static int NoInput = 0x1;
	final public static int NumericAttribute = 0x2;
	final public static int MultiSelect = 0x4;
	final public static int UseSlider = 0x8;
	
	// Available
	final public static boolean True=true;
	final public static boolean False=false;
		
	
	//ADD LISTSCROLLER IF "MULTISELECT" FOR LIST AND ATTRIBUTES
	
	
	@Tunable(description="Integer", flag=UseSlider, type=INTEGER, available=True, lowerbound=0, upperbound=200, data={}, value="10")
	public Integer integer1;
	
	@Tunable(description="Integer", flag=UseSlider, type=INTEGER, available=True, lowerbound=0, upperbound=200, data={}, value="15")
	public Integer integer2;
	
	@Tunable(description="Group", flag=NoInput, type=GROUP, available=True, lowerbound=0, upperbound=200, data={}, value="")
	public Integer Group1;
	
	@Tunable(description="Double", flag=UseSlider, type=DOUBLE, available=True, lowerbound=0, upperbound=150, data={}, value="12.4")
	public Double double1;
	
	@Tunable(description="Group", flag=NoInput, type=GROUP, available=True, lowerbound=0, upperbound=200, data={}, value="")
	public Integer Group2;
	
	@Tunable(description="String", flag=NoInput, type=STRING, available=True, lowerbound=0, upperbound=200, data={}, value="dad")
	public String string1;
	
	@Tunable(description="Group", flag=NoInput, type=GROUP, available=True, lowerbound=0, upperbound=200, data={}, value="")
	public Integer Group3;
	
	@Tunable(description="Boolean", flag=NoInput, type=BOOLEAN, available=True, lowerbound=0, upperbound=200, data={}, value="false")
	public Boolean boolean1;
	
	@Tunable(description="Boolean", flag=NoInput, type=BOOLEAN, available=True, lowerbound=0, upperbound=200, data={}, value="true")
	public Boolean boolean2;
	
	@Tunable(description="Group", flag=NoInput, type=GROUP, available=True, lowerbound=0, upperbound=200, data={}, value="")
	public Integer Group4;
	
	/*
	@Tunable(description="Button", flag=NoInput, type=BUTTON, available=True, lowerbound=0, upperbound=200, data={}, value="false")
	public JButton T4 = new JButton();
	
	@Tunable(description="List", flag=MultiSelect, type=LIST, available=True, lowerbound=0, upperbound=200, data ={"aa","bb","cc","dd"}, value="")
	public JList T5 = new JList();
	*/

	//@Tunable(description="Attribute", flag=MultiSelect, type=NODEATTRIBUTE, available=TRUE, lowerbound=0, upperbound=200, data ={"aa","bb","cc","dd"}, value="")
	//public JList Attributes = new JList();

	//@Tunable(description="Attribute", flag=MultiSelect, type=EDGEATTRIBUTE, available=TRUE, lowerbound=0, upperbound=200, data ={"aa","bb","cc","dd"}, value="")
	//public JList Attributes = new JList();
	
	
}

