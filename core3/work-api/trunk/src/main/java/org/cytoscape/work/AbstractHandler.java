package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * Abstract handler for tunables. It provides constructor for Fields and Methods that have been detected by <code>AbstractTunableInterceptor</code>, 
 * basic methods to access to the handler components, and to manage the listeners for handler events.
 * 
 * It is considered as a super class for <code>AbstractGuiHandler</code> and <code>AbstractPropHandler</code>.
 * 
 * @author pasteur
 *
 */

public abstract class AbstractHandler implements Handler {

	/**
	 * Field that need to be intercepted
	 */
	protected Field f;
	
	/**
	 * Method that need to be intercepted. This getter method(to get the value of another Object from the class) need to be coupled with the smethod or setter method(to set the value of this previous Object)
	 * This method will be annotated as a <code>Tunable</code>
	 */
	protected Method gmethod;
	
	/**
	 * Method that need to be intercepted. This setter method(to set the value of another Object(not annotated as <code>Tunable</code> from the class) need to be coupled with the gmethod or getter method(to get the value of this Object)
	 * This method will be annotated as a <code>Tunable</code>
	 */
	protected Method smethod;
	//protected Method m;
	
	/**
	 * Object that is contained in the Field <code>f</code>
	 * 
	 * To access to the Object <code>o</code>, use : <code>f.get(o)</code>
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
	 * List of listeners that are applied to <code>Handlers</code>
	 */
	protected List<HandlerListener> listeners;
	

	/**
	 * Handler for Fields values
	 * @param f field that has been annotated
	 * @param o object contained in <code>f</code>
	 * @param t tunable associated to <code>f</code> 
	 */
	public AbstractHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		listeners = new ArrayList<HandlerListener>();
	}

	
//	/**
//	 * Handler for Methods values
//	 * @param m method that has been annotated
//	 * @param o object contained in <code>m</code>
//	 * @param t tunable associated to <code>m</code> 
//	 */
//	public AbstractHandler(Method m, Object o, Tunable t) {
//		this.m = m;
//		this.o = o;
//		this.t = t;
//	}

	
	/**
	 * Handler for 2 Methods : <code>gmethod</code> and <code>smethod</code>
	 * @param getmethod method that has been annotated as a <i>getter</i>
	 * @param setmethod method that has been annotated as a <i>setter</i>
	 * @param o object contained in methods
	 * @param tg tunable associated to <code>gmethod</code> 
	 * @param ts tunable associated to <code>smethod</code> 
	 */
	public AbstractHandler(Method getmethod, Method setmethod, Object o, Tunable tg, Tunable ts){
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
	public Field getField() {
		return f;
	}

	
//	/**
//	 * To get <code>Method m</code>
//	 * @return method component from the handler
//	 */
//	public Method getMethod() {
//		return m;
//	}

	/**
	 * To get <code>Method gmethod</code>
	 * @return method component from the handler
	 */
	public Method getGetMethod() {
		return gmethod;
	}
	
	/**
	 * To get <code>Method smethod</code>
	 * @return method component from the handler
	 */
	public Method getSetMethod() {
		return smethod;
	}

	
	
	/**
	 * To get <code>Object o</code> 
	 * @return object component from the handler
	 */
	public Object getObject() {
		return o;
	}

	
	/**
	 * To get <code>Tunable tg</code>
	 * @return tunable component from the handler
	 */
	public Tunable getGetTunable() {
		return tg;
	}
	
	/**
	 * To get <code>Tunable ts</code>
	 * @return tunable component from the handler
	 */
	public Tunable getSetTunable() {
		return ts;
	}
	/**
	 * To get <code>Tunable t</code>
	 * @return tunable component from the handler
	 */
	public Tunable getTunable() {
		return t;
	}
	
	
	/**
	 * To set a new Handler to an existing one
	 * 
	 * NEED TO BE IMPLEMENTED!
	 */
	public void handlerChanged(Handler h) {
		System.out.println("currently a no-op");
	}

	/**
	 * To add a <code>HandlerListener</code> to the Handler to detect modification events
	 */
	public void addHandlerListener(HandlerListener listener) {
		if ( listener == null )
			return;
		listeners.add(listener);
	}

	/**
	 * To remove a <code>HandlerListener</code>
	 */
	public boolean removeHandlerListener(HandlerListener listener) {
		if ( listener == null )
			return false;
		return listeners.remove(listener);	
	}
	
}
