package org.cytoscape.work;


import java.lang.reflect.InvocationTargetException;


public interface TunableHandler {
	/**
	 * @return an object describing a field annotated with @Tunable or null if no field has been associated with this handler
	 */
	Object getValue() throws IllegalAccessException, InvocationTargetException;

	void setValue(final Object newValue) throws IllegalAccessException, InvocationTargetException;

	/**
	 *  @return the associated <code>Tunable</code>'s description
	 */
	String getDescription();

	/**
	 *  @return the associated <code>Tunable</code>'s flags
	 */
	Tunable.Param[] getFlags();

	/**
	 *  @return the associated <code>Tunable</code>'s alignments
	 */
	Tunable.Param[] getAlignments();

	/**
	 *  @return the associated <code>Tunable</code>'s groups or nesting hierarchy
	 */
	String[] getGroups();

	/**
	 *  @return the associated <code>Tunable</code>'s group titles' flags
	 */
	Tunable.Param[] getGroupTitleFlags();

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
}
