package org.cytoscape.command;

import org.cytoscape.work.*;
import org.cytoscape.work.util.*;
import org.cytoscape.work.Tunable.Param;

public class PrintSomething implements Command {

	@Tunable(description="your first name", group={"stuff"})
	public String firstName = "homer";

	@Tunable(description="your last name", group={"stuff","advanced"}, flag={Param.collapsed})
	public String lastName = "simpson";

	@Tunable(description="Test",dependsOn="monthNames=[April]")
	public Double test = new Double(6.5);	
	
	@Tunable(description="your foot size", group={"stuff2"})
	public BoundedDouble footSize = new BoundedDouble(5.0, 8.5, 13.5, true, false);

	@Tunable(description="the number of children you have",flag={Param.slider})
	public BoundedInteger kids = new BoundedInteger(0, 1, 10, true, false);

	@Tunable(description="Month",group={"Today's date"})
	public ListMultipleSelection<String> monthNames = new ListMultipleSelection<String>("January","February","March","April","May","June","July","August","September","October","November","December");
	
	@Tunable(description="Day",group={"Today's date"})
	public ListSingleSelection<String> dayNames = new ListSingleSelection<String>("Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday");

	@Tunable(description="Number of children",group={"Other"})
	public Integer integer1 = new Integer(0);


	
	
	public PrintSomething(){}
	
//	@Tunable(description="your age")
//	public void setAge(int a) {
//		age = a;	
//	}
	
	public void execute() {
		//System.out.println("Your name is: " + firstName + " " + lastName + " your age is: your foot size is: " + footSize.getValue() + " and you have " + kids.getValue() + " kids.");
		System.out.println(this.getClass().getSimpleName()+" has been executed");
	}

	public int age;

	
}
