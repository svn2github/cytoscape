

package org.example.command;

import org.example.tunable.Tunable;

public class PrintSomething implements Command {

	@Tunable(description="your first name",namespace="printSomething")
	public String firstName = "homer";

	@Tunable(description="your last name",namespace="printSomething")
	public String lastName = "simpson";

	public int age;

	public void execute() {
		System.out.println("Your name is: " + firstName + " " + lastName + " and your age is: " + age);
	}

	@Tunable(description="your age",namespace="printSomething")
	public void setAge(int a) {
		age = a;	
	}
}
