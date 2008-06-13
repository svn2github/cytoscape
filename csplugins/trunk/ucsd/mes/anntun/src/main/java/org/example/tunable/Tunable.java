
package org.example.tunable;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME) // makes this availabe for reflection
@Target(ElementType.FIELD) // says we're just looking at fields (not methods or constructors)
public @interface Tunable {
	String description();
	// String getName(); ??
}
