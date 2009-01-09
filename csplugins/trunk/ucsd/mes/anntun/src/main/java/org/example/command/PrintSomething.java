

package org.example.command;

import org.example.tunable.Tunable;
import org.example.tunable.util.Bounded;

public class PrintSomething implements Command {

	@Tunable(description="your first name",namespace="printSomething")
	public String firstName = "homer";

	@Tunable(description="your last name",namespace="printSomething")
	public String lastName = "simpson";

	@Tunable(description="your foot size",namespace="printSomething")
	public Bounded<Double> footSize = new Bounded<Double>(Double.valueOf(5.0), Double.valueOf(8.5), Double.valueOf(13.5), true, false);

	public int age;

	public void execute() {
		System.out.println("Your name is: " + firstName + " " + lastName + " your age is: " + age + " and your foot size is: " + footSize.getValue());
	}

	@Tunable(description="your age",namespace="printSomething")
	public void setAge(int a) {
		age = a;	
	}
}
