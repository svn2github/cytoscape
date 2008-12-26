package Tunable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target({ElementType.FIELD,ElementType.TYPE}) // says we're just looking at fields and  methods

public @interface Tunable{
	public String description();
	public Class<?> type();
	public Param flag();	
	public enum Param { IntegerSlider,DoubleSlider,Integer,Double,Nothing,MultiSelect }	
}
