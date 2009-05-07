package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.HandlerController;
import org.example.tunable.Handler;
import org.example.tunable.util.BoundedInteger;
import org.example.tunable.util.BoundedDouble;
import org.example.tunable.util.FlexiblyBoundedInteger;
import org.example.tunable.util.ListMultipleSelection;
import org.example.tunable.util.ListSingleSelection;

import java.util.Map;

public class PrintSomething implements Command, HandlerController {

	@Tunable(description="your first name", group={"stuff"})
	public String firstName = "homer";

	@Tunable(description="your last name", group={"stuff","advanced"}, flags={"collapsable"} )
	public String lastName = "simpson";

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
	
	
	public int age;
	public BoundedInteger height = new BoundedInteger(0,150,200,false,false);
	
	@Tunable(description="to set the Height")
	public void sHeight(int h) {
		height.setValue(h);
	}

	
	@Tunable(description="your age")
	public void sAge(int a) {
		age = a;
	}
	
	public void execute() {
		System.out.println("\t name : " + firstName + " " + lastName + "\n \t age : " + age + "\n \t foot size : " + footSize.getValue() + "\n \t kids = " + kids.getValue() + "\n \t income : $" + income.getValue() + "\n \t result for boolean = " + bool + "\n \t listsingleselection = "+lss.getSelectedValue() + "\n \t listmultipleselection = "+lms.getSelectedValues() + "\n \t height = "+height.getValue());
	}
	

	public void controlHandlers(Map<String,Handler> hands) {
		for ( Handler h : hands.values() )
			System.out.println("controlling handler for tunable: " + h.getTunable().description());
	}
}
