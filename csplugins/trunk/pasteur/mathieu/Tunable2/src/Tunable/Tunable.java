package Tunable;

import java.lang.annotation.*;

//import javax.lang.model.element.Modifier;



@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target({ElementType.FIELD,ElementType.TYPE}) // says we're just looking at fields and  methods

public @interface Tunable{
	public String description();
	public Class<?> type();
	public Param flag();
	public boolean available() default true;	
	public enum Param { UseSlider, Nothing,MultiSelect }	
}
