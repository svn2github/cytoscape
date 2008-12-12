package TunableDefinition;

import java.lang.annotation.*;

//import javax.lang.model.element.Modifier;



@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target({ElementType.FIELD,ElementType.TYPE}) // says we're just looking at fields and  methods

public @interface Tunable{

	String[] description();
//	int type();
	Class<?> type();
	Param flag();
	boolean available() default true;
	//String value();
	//int flag();
	//double upperbound();
	//double lowerbound();
	//String[] data();
	
	public enum Param { UseSlider, Nothing,MultiSelect }
	
}
