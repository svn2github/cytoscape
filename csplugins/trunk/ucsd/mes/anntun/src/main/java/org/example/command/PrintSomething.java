package org.example.command;

import java.util.Map;

import org.example.tunable.Handler;
import org.example.tunable.HandlerController;
import org.example.tunable.Tunable;
import org.example.tunable.util.BoundedDouble;
import org.example.tunable.util.BoundedInteger;
import org.example.tunable.util.FlexiblyBoundedInteger;
import org.example.tunable.util.ListMultipleSelection;
import org.example.tunable.util.ListSingleSelection;

public class PrintSomething implements Command, HandlerController {

	@Tunable(description="your first name", group={"stuff"})
	public String firstName = "homer";

	@Tunable(description="your last name", group={"stuff","advanced"}, flags={"collapsable"} )
	public String lastName = "simpson";

	@Tunable(description="an integer test", group={"stuff","advanced"}, flags={"collapsable"} )
	public int test = 3;
	
	@Tunable(description="your foot size", group={"stuff","advanced"})
	public BoundedDouble footSize = new BoundedDouble(5.0, 8.5, 13.5, true, false);

	@Tunable(description="the number of children you have")
	public BoundedInteger kids = new BoundedInteger(0, 1, 10, true, false);

	@Tunable(description="your yearly income" )
	public FlexiblyBoundedInteger income = new FlexiblyBoundedInteger(0, 20000, 100000, false, false);

	@Tunable(description="your boolean")
	public boolean bool = false;
	
	@Tunable(description="listSingleSelection")
	public ListSingleSelection<String> lss = new ListSingleSelection<String>("1","2","3","4");
	
	@Tunable(description="listMultipleSelection")
	public ListMultipleSelection<String> lms = new ListMultipleSelection<String>("one","two","three","four");
	
	
	
	
	// test of methods
	private Integer age = Integer.valueOf(25);	
	@Tunable(description="to set your age")
	public void setAge(Integer a) {
		if ( a == null )throw new NullPointerException("age is null");
		age = a;
	}
	@Tunable(description="to get your age")
	public Integer getAge(){return age;}
	
	
	private Boolean booltest = new Boolean(false);
	@Tunable(description="to set the booleantest")
	public void setBoolTest(Boolean b){
		if(b == null) throw new NullPointerException("boolean is null");
		booltest = b;
	}
	@Tunable(description="to get the booleantest")
	public Boolean getBoolTest(){return booltest;}

	
	private String line = new String("cytoscape development");
	@Tunable(description="to set the line")
	public void setLine(String st){
		if(st == null) throw new NullPointerException("line is null");
		line = st;
	}
	@Tunable(description="to get the line")
	public String getLine(){return line;}
	
	
	
	//may have to be modified, but is working
	private BoundedInteger height = new BoundedInteger(0,150,200,false,false);
	@Tunable(description="to set the Height")
	public void setHeight(BoundedInteger bi) {
		if(bi == null) throw new NullPointerException("height is null");		
		height = bi;
	}
	@Tunable(description="to get the height")
	public BoundedInteger getHeight(){return height;}

	private FlexiblyBoundedInteger size = new FlexiblyBoundedInteger(0,3,78,true,true);
	@Tunable(description="to set the Size")
	public void setSize(FlexiblyBoundedInteger fbi){
		if(fbi == null)throw new NullPointerException("size is null!!");
		size = fbi;
	}
	@Tunable(description="to get the Size")
	public FlexiblyBoundedInteger getSize(){
		return size;
	}
	
	private ListSingleSelection<String> colors = new ListSingleSelection<String>("red","blue","green","yellow");
	@Tunable(description="to set the colors")
	public void setColors(ListSingleSelection<String> list){
		if(list==null) throw new NullPointerException("the list is null");
		colors = list;
	}
	@Tunable(description="to get the colors")
	public ListSingleSelection<String> getColors(){return colors;}
	
	
	
	//DOESN'T work, cause cannot use generic type Integer. just String for the moment??
	private ListMultipleSelection<Integer> numbers = new ListMultipleSelection<Integer>(100,200,300,400,500);
	@Tunable(description="to set the numbers")
	public void setNumbers(ListMultipleSelection<Integer> lmsi){
		if(lmsi==null)throw new NullPointerException("the numbers is null");
		numbers = lmsi;
	}
	@Tunable(description="to get the numbers") 
	public ListMultipleSelection<Integer> getNumbers(){return numbers;}
	
	
	
	
	
	public void execute() {
		System.out.println("\t name : " + firstName + " " + lastName + "\n \t age : " + age + "\n \t foot size : " + footSize.getValue() + "\n \t kids = " + kids.getValue() + "\n \t income : $" + income.getValue() + "\n \t result for boolean = " + bool + "\n \t listsingleselection = "+lss.getSelectedValue() + "\n \t listmultipleselection = "+lms.getSelectedValues() + "\n \t height = "+height.getValue());
		System.out.println("\n\nRESULT FOR GETSET METHODS = ");
		
		System.out.println("testBoolean = " + booltest);
		System.out.println("testString = " + line);
		System.out.println("testBoundedInteger value = " + height.getValue());
		System.out.println("testFlexiblyBoundedInteger value= " + size.getValue());
		System.out.println("testFlexiblyBoundedInteger lowerbound= " + size.getLowerBound());
		System.out.println("testFlexiblyBoundedInteger upperbound= " + size.getUpperBound());
		System.out.println("testListSingleSelection = "+colors.getPossibleValues());
		System.out.println("testListSingleSelection = "+colors.getSelectedValue());
		System.out.println("testListMultipleSelection = "+numbers.getPossibleValues());
		System.out.println("testListMultipleSelection = "+numbers.getSelectedValues());
	}
	

	public void controlHandlers(Map<String,Handler> hands) {
		for ( Handler h : hands.values() )
			System.out.println("controlling handler for tunable: " + h.getTunable().description());
	}
}
