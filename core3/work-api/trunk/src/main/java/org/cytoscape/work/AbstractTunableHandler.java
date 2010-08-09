package org.cytoscape.work;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class AbstractTunableHandler implements TunableHandler {
	final protected Field field;
	final protected Method getter;
	final protected Method setter;
	final protected Object instance;
	final protected Tunable tunable;

	public AbstractTunableHandler(final Field field, final Object instance, final Tunable tunable) {
		this.field = field;
		this.getter = null;
		this.setter = null;
		this.instance = instance;
		this.tunable = tunable;
	}

	public AbstractTunableHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		this.field = null;
		this.getter = getter;
		this.setter = setter;
		this.instance = instance;
		this.tunable = tunable;
	}

	/**
	 * @return an object describing a field annotated with @Tunable or null if no field has been associated with this handler
	 */
	final public Object getValue() throws IllegalAccessException, InvocationTargetException {
		return field != null ? field.get(instance) : getter.invoke(instance);
	}

	final public void setValue(final Object newValue) throws IllegalAccessException, InvocationTargetException {
		if (field != null)
			field.set(instance, newValue);
		else
			setter.invoke(instance, newValue);
	}

	/**
	 *  @return the associated <code>Tunable</code>'s description
	 */
	final public String getDescription() {
		return tunable.description();
	}

	/**
	 *  @return the associated <code>Tunable</code>'s flags
	 */
	final public Tunable.Param[] getFlags() {
		return tunable.flags();
	}

	/**
	 *  @return the associated <code>Tunable</code>'s alignments
	 */
	final public Tunable.Param[] getAlignments() {
		return tunable.alignment();
	}

	/**
	 *  @return the associated <code>Tunable</code>'s groups or nesting hierarchy
	 */
	final public String[] getGroups() {
		return tunable.groups();
	}

	/**
	 *  @return the associated <code>Tunable</code>'s group titles' flags
	 */
	final public Tunable.Param[] getGroupTitleFlags() {
		return tunable.groupTitles();
	}

	/**
	 *  @return true if the associated <code>Tunable</code> controls nested children, else false
	 */
	final public boolean controlsMutuallyExclusiveNestedChildren() {
		return tunable.xorChildren();
	}

	/**
	 *  @return returns the name of the key that determines the selection of which controlled nested child is currently presented, or the empty string if
	 *          controlsMutuallyExclusiveNestedChildren() returns false.
	 */
	final public String getChildKey() {
		return tunable.xorKey();
	}

	/**
	 *  @return the dependsOn property of the tunable
	 */
	final public String dependsOn() {
		return tunable.dependsOn();
	}

	/**
	 * @return a name representing a tunable property
	 */
	final public String getName() {
		if (field != null)
			return field.getName();
		else
			return setter.getName().substring(3);
	}
}
