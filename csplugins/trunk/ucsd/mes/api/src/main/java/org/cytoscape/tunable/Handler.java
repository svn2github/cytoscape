
package org.cytoscape.tunable;

/**
 * A place holder interface that identifies the code
 * that will set the {@link Tunable} {@link java.lang.reflect.Field} in 
 * the {@link java.lang.Object} based on whatever input this Handler
 * is able to generate.  We would expect Handlers to be implemented
 * for all primitive types (int, float, String, etc.) as well as
 * more exotic objects that might need to be set.
 */
public interface Handler { }
