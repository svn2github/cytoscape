
package org.cytoscape.work;

import java.lang.annotation.*;

/**
 * An annotation used to identifiy fields in an object that constitute values
 * which can be modified by a {@link TunableInterceptor}.  The name of the 
 * annotation is the name of the {@link java.lang.reflect.Field} being annotated.
 */
@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target(ElementType.FIELD) // says we're just looking at fields (not methods or constructors)
public @interface Tunable {

	/**
	 * A brief (approximately one sentence) description of this {@link java.lang.reflect.Field}.  
	 * The description should be suitable for presentation in a user interface.
	 */
	public String description();

	/**
	 * The namespace of the Tunable.  All Tunables in a class should have the 
	 * same namespace.  The namespace will be used to serialize the Tunable
	 * to {@link java.util.Properties} files so should not use spaces or the '.' character.
	 */
	public String namespace();

	/**
	 * The group can be specified to cluster Tunables within a class for more
	 * coherent presentation within a user interface.
	 */
	public String group() default "";
}
