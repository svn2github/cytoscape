package org.cytoscape.work;


import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;


/** Interface for classes that deal with reading out and writing back <code>Tunable</code>s and their properties.
 */
public interface TunableHandler {
	/**
	 * @return an object describing a field annotated with @Tunable or null if no field has been associated with this handler
	 */
	Object getValue() throws IllegalAccessException, InvocationTargetException;

	/** Attempts to set the value "newValue" on the associated Tunable.
	 *  @param newValue the value to be written into the tunable property
	 */
	void setValue(final Object newValue) throws IllegalAccessException, InvocationTargetException;

	/**
	 *  @return the associated <code>Tunable</code>'s description
	 */
	String getDescription();

	/**
	 *  @return the associated <code>Tunable</code>'s groups or nesting hierarchy
	 */
	String[] getGroups();

	/**
	 *  @return true if the associated <code>Tunable</code> controls nested children, else false
	 */
	boolean controlsMutuallyExclusiveNestedChildren();

	/**
	 *  @return returns the name of the key that determines the selection of which controlled nested child is currently presented, or the empty string if
	 *          controlsMutuallyExclusiveNestedChildren() returns false.
	 */
	String getChildKey();

	/**
	 *  @return the dependsOn property of the tunable
	 */
	String dependsOn();

	/**
	 * @return a name representing a tunable property
	 */
	String getName();

	/**
	 *  @return the name of the underlying class of the tunable followed by a dot and the name of the tunable field or getter/setter root name.
	 *
	 *  Please note that the returned String will always contain a single embedded dot.
	 */
	String getQualifiedName();

	/**
	 *  @return the parsed result from Tunable.getParams()
	 */
	Properties getParams();
}
