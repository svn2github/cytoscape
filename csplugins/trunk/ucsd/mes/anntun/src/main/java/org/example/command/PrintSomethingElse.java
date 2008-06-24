

package org.example.command;

import org.example.tunable.Tunable;

public class PrintSomethingElse implements Command {

	@Tunable(description="the value we need",namespace="printElse")
	private int value = 4; 


	public void execute() {
		System.out.println("Printing value: " + value);
	}
}
