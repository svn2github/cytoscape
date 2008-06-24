
package org.example.tunable;

import java.lang.annotation.*;

/**
 * An annotation used to identifiy fields in an object that constitute values
 * which can be modified by a {@link TunableInterceptor}.
 */
@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target(ElementType.FIELD) // says we're just looking at fields (not methods or constructors)
public @interface Tunable {
	String description();
	String namespace();
}
