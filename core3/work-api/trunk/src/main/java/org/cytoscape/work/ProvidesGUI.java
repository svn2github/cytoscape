package org.cytoscape.work;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})


/**
 * An annotation type that can be applied to a <i>method</i> in order to allow <code>TunableInterceptor</code> to catch it,
 * and so to use its members to create a corresponding interface for a user.
 * 
 * Here is an example of how to use a <code>ProvidesGUI</code> annotation:
 * <p><pre>
 * <code>
 * 	@ProvidesGUI
 * 	public JPanel getGUI() { return mySetupPanel; }
 * </code>
 * </pre></p>
 * 
 *  Please not that the method annotated with this needs to return a JPanel and take no arguments.
 */
public @interface ProvidesGUI {
}
