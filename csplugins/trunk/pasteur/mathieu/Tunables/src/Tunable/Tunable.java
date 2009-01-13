package Tunable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target({ElementType.FIELD,ElementType.TYPE}) // says we're just looking at fields and  methods

public @interface Tunable{
	public String description();
	public Param flag() default Param.Nothing;
	public String group() default "";
	public enum Param {Slider,Nothing}	
}
