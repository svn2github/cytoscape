
package org.example.tunable;

import java.lang.annotation.*;

/**
 * An annotation used to identifiy fields in an object that constitute values
 * which can be modified by a {@link TunableInterceptor}.
 */
@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target({ElementType.FIELD,ElementType.METHOD}) // says we're just looking at fields and  methods 
public @interface Tunable {
	String description();
	String[] flags() default {};
	String[] group() default {};
	String dependsOn() default "";
	boolean xorChildren() default false;
	String xorKey() default "";
}
