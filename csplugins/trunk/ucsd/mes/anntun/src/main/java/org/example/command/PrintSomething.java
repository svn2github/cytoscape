package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.HandlerController;
import org.example.tunable.Handler;
import org.example.tunable.util.BoundedInteger;
import org.example.tunable.util.BoundedDouble;
import org.example.tunable.util.FlexiblyBoundedInteger;

import java.util.Map;

public class PrintSomething implements Command, HandlerController {

	@Tunable(description="your first name", group={"stuff"})
	public String firstName = "homer";

	@Tunable(description="your last name", group={"stuff","advanced"}, flags={"collapsable"} )
	public String lastName = "simpson";

	@Tunable(description="your foot size", group={"stuff","advanced"})
	public BoundedDouble rootSize = new BoundedDouble(5.0, 8.5, 13.5, true, false);

	@Tunable(description="the number of children you have")
	public BoundedInteger kids = new BoundedInteger(0, 1, 10, true, false);

	@Tunable(description="your yearly income" )
	public FlexiblyBoundedInteger income = new FlexiblyBoundedInteger(0, 20000, 100000, false, false);

	@Tunable(description="boolean test")
	public boolean bool = false;
	
	public void execute() {
		System.out.println("Your name is: " + firstName + " " + lastName + " your age is: " + age + " your foot size is: " + rootSize.getValue() + " you have " + kids.getValue() + " kids and you make: $ " + income.getValue() + " per year" + " and result for bool = " + bool);
	}

	public int age;

	@Tunable(description="your age")
	public void setAge(int a) {
		age = a;	
	}

	public void controlHandlers(Map<String,Handler> hands) {
		for ( Handler h : hands.values() )
			System.out.println("controlling handler for tunable: " + h.getTunable().description());
	}
}
