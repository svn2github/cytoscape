package TunableDefinition;

import java.lang.annotation.*;



@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
//@Target({ElementType.FIELD,ElementType.TYPE}) // says we're just looking at fields and  methods

public @interface Tunable{

	String description();
	int type();
	boolean available();
	String value();
	int flag();
	double upperbound();
	double lowerbound();
	String[] data();
}
