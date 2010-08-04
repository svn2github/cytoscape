package org.cytoscape.work;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Abstract handler for tunables. It provides constructor for Fields and Methods that have been detected by <code>AbstractTunableInterceptor</code>, 
 * basic methods to access to the handler components, and to manage the listeners for handler events.
 * 
 * This is a super class for <code>AbstractGUIHandler</code> and <code>AbstractPropHandler</code>.
 * 
 * @author pasteur
 *
 */
public abstract class AbstractHandler implements Handler {
	/**
	 * Field that needs to be intercepted
	 * 
	 * To access to the field use : <code>f.get(o)</code>
	 */
	protected Field f;
	
	/**
	 * Method that need to be intercepted. This getter method(to get the value of another Object from the class)
	 * need to be coupled with the smethod or setter method(to set the value of this previous Object)
	 * This method will be annotated as a <code>Tunable</code>
	 */
	protected Method gmethod;
	
	/**
	 * Method that need to be intercepted. This setter method(to set the value of another Object(not annotated as <code>Tunable</code> from the class) need to be coupled with the gmethod or getter method(to get the value of this Object)
	 * This method will be annotated as a <code>Tunable</code>
	 */
	protected Method smethod;
	
	/**
	 * Object that contains the field <code>f</code> or the setter <code>smethod</code> and getter <code>gmethod</code>
	 */
	protected Object o;
	
	/**
	 * <code>Tunable</code> annotations of the Field <code>f</code> annotated as <code>Tunable</code>
	 * 
	 * To access to <code>Tunable</code>, use : <code>f.getAnnotation(Tunable.class)</code> 
	 */
	protected Tunable t;
	
	/**
	 * <code>Tunable</code> annotations of the Method <code>gmethod</code>(getter method) annotated as <code>Tunable</code>
	 */
	protected Tunable tg;
	
	/**
	 * <code>Tunable</code> annotations of the Method <code>smethod</code>(setter method) annotated as <code>Tunable</code>
	 */
	protected Tunable ts;

	/**
	 * Handler for Field values
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code> 
	 */
	public AbstractHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
	}
	
	/**
	 * Handler for getter/setter methods <code>gmethod</code> and <code>smethod</code>.
	 *
	 * @param getmethod method that has been annotated as a <i>getter</i>
	 * @param setmethod method that has been annotated as a <i>setter</i>
	 * @param o object contained in methods
	 * @param tg tunable associated to <code>gmethod</code> 
	 * @param ts tunable associated to <code>smethod</code> 
	 */
	public AbstractHandler(Method getmethod, Method setmethod, Object o, Tunable tg, Tunable ts) {
		this.gmethod = getmethod;
		this.smethod = setmethod;
		this.o = o;
		this.tg = tg;
		this.ts = ts;
	}
	
	/**
	 * To get <code>Field f</code>
	 * @return field component from the handler
	 */
	final public Field getField() {
		return f;
	}

	/**
	 * To get <code>Method gmethod</code>
	 * @return method component from the handler
	 */
	final public Method getGetMethod() {
		return gmethod;
	}
	
	/**
	 * To get <code>Method smethod</code>
	 * @return method component from the handler
	 */
	final public Method getSetMethod() {
		return smethod;
	}
	
	/**
	 * To get <code>Object o</code> 
	 * @return object component from the handler
	 */
	final public Object getObject() {
		return o;
	}

	
	/**
	 * @return tunable component for the getter method
	 */
	final public Tunable getGetTunable() {
		return tg;
	}
	
	/**
	 * @return tunable component for the setter method
	 */
	final public Tunable getSetTunable() {
		return ts;
	}

	/**
	 * @return tunable component for a field
	 */
	public Tunable getTunable() {
		return t;
	}
}
