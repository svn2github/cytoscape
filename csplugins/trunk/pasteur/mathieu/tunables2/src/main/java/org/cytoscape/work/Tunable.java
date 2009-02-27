package org.cytoscape.work;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.TYPE})

public @interface Tunable{
	public String description();
	Param[] flag() default {};
	public String[] group() default {};
	boolean xorChildren() default false;
	String xorKey() default "";
	String dependsOn() default "";
	public enum Param {slider,nothing,horizontal,vertical,uncollapsed,collapsed}
}
