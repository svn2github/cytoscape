

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.BoundedInteger;
import org.example.tunable.util.BoundedDouble;

public class PrintSomething implements Command {

	@Tunable(description="your first name",namespace="printSomething")
	public String firstName = "homer";

	@Tunable(description="your last name",namespace="printSomething")
	public String lastName = "simpson";

	@Tunable(description="your foot size",namespace="printSomething")
	public BoundedDouble footSize = new BoundedDouble(5.0, 8.5, 13.5, true, false);

	@Tunable(description="the number of children you have",namespace="printSomething")
	public BoundedInteger kids = new BoundedInteger(0, 1, 10, true, false);

	public int age;

	public void execute() {
		System.out.println("Your name is: " + firstName + " " + lastName + " your age is: " + age + " your foot size is: " + footSize.getValue() + " and you have " + kids.getValue() + " kids.");
	}

	@Tunable(description="your age",namespace="printSomething")
	public void setAge(int a) {
		age = a;	
	}
}
