package org.cytoscape.work;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/** Provides the standard implementation for most of the methods declared by the TunableHandler interface.
 */
public class AbstractTunableHandler implements TunableHandler {
	private enum ParamsParseState {
		KEY_START, LOOKING_FOR_EQUAL_SIGN, VALUE_START, LOOKING_FOR_SEMICOLON;
	}

	final private Field field;
	final private Method getter;
	final private Method setter;
	final protected Object instance; // TODO: should this be private?
	final protected Tunable tunable; // TODO: should this be private?

	/** Standard base class constructor for TunableHandlers that deal with Tunables that annotate a field.
	 */
	public AbstractTunableHandler(final Field field, final Object instance, final Tunable tunable) {
		this.field = field;
		this.getter = null;
		this.setter = null;
		this.instance = instance;
		this.tunable = tunable;
	}

	/** Standard base class constructor for TunableHandlers that deal with Tunables that use getter and setter methods.
	 */
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

	/** Sets the value of the Tunable associated with this TunableHandler.
	 */
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
	 *  @return the associated <code>Tunable</code>'s groups or nesting hierarchy
	 */
	final public String[] getGroups() {
		return tunable.groups();
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

	/**
	 *  @return the name of the underlying class of the tunable followed by a dot and the name of the tunable field or getter/setter root name.
	 *
	 *  Please note that the returned String will always contain a single embedded dot.
	 */
	final public String getQualifiedName() {
		final String unqualifiedClassName =
			field == null ? getter.getDeclaringClass().toString() : field.getDeclaringClass().toString();
		
                return unqualifiedClassName.substring(unqualifiedClassName.lastIndexOf(".") + 1) + "." + getName();
	}

	/**
	 *  @return the parsed result from Tunable.getParams()
	 */
	final public Properties getParams() throws IllegalArgumentException {
		final String rawString = tunable.params();
		final Properties keyValuesPairs = new Properties();

		StringBuilder key = null;
		StringBuilder value = null;
		ParamsParseState state = ParamsParseState.KEY_START;
		boolean escaped = false;
		for (int i = 0; i < rawString.length(); ++i) {
			final char ch = rawString.charAt(i);

			switch (state) {
			case KEY_START:
				key = new StringBuilder();
				if (!Character.isLetter(ch))
					throw new IllegalArgumentException(getName() + "'s getParams() returns an invalid key!");
				key.append(ch);
				state = ParamsParseState.LOOKING_FOR_EQUAL_SIGN;
				break;
			case LOOKING_FOR_EQUAL_SIGN:
				if (ch == '=')
					state = ParamsParseState.VALUE_START;
				else {
					if (!Character.isLetter(ch))
						throw new IllegalArgumentException(getName() + "'s getParams() returns an invalid key!");
					key.append(ch);
				}
				break;
			case VALUE_START:
				value = new StringBuilder();
				if (ch == ';')
					throw new IllegalArgumentException(getName() + "'s getParams() returns an invalid value!");
				if (ch == '\\')
					escaped = true;
				else
					value.append(ch);
				state = ParamsParseState.LOOKING_FOR_SEMICOLON;
				break;
			case LOOKING_FOR_SEMICOLON:
				if (escaped) {
					value.append(ch);
					escaped = false;
				} else if (ch == ';') {
					keyValuesPairs.setProperty(key.toString(), value.toString());
					state = ParamsParseState.KEY_START;
				} else {
					if (ch == '\\')
						escaped = true;
					else
						value.append(ch);
				}
				break;
			}
		}

		if (escaped)
			throw new IllegalArgumentException(getName() + "'s getParams() returns an invalid escaped character!");
		if (state != ParamsParseState.KEY_START && state != ParamsParseState.LOOKING_FOR_SEMICOLON)
			throw new IllegalArgumentException(getName() + "'s getParams() returns an incomplete string: \"" + rawString + "\"!");

		if (key != null) {
			if (value == null)
				throw new IllegalArgumentException(getName() + "'s getParams() returns a key without a value!");
			keyValuesPairs.setProperty(key.toString(), value.toString());
		}

		return keyValuesPairs;
	}
}
