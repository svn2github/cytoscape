

package org.example.command;

import org.example.tunable.Tunable;

public class PrintSomething implements Command {

	@Tunable(description="your first name")
	private String firstName = "homer";

	@Tunable(description="your last name")
	private String lastName = "simpson";

	@Tunable(description="your age")
	private int age;

	public void execute() {
		System.out.println("Your name is: " + firstName + " " + lastName + " and your age is: " + age);
	}
}
