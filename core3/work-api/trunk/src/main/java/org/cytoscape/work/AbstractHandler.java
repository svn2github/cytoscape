package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

	protected Field f;
	protected Method m;
	protected Object o;
	protected Tunable t;
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
	}

	
	/**
	 * Handler for Methods values
	 * @param m method that has been annotated
	 * @param o object contained in <code>m</code>
	 * @param t tunable associated to <code>m</code> 
	 */
	public AbstractHandler(Method m, Object o, Tunable t) {
		this.m = m;
		this.o = o;
		this.t = t;
	}

	/**
	 * To get <code>Field f</code>
	 * @return field component from the handler
	 */
	public Field getField() {
		return f;
	}

	
	/**
	 * To get <code>Method m</code>
	 * @return method component from the handler
	 */
	public Method getMethod() {
		return m;
	}

	/**
	 * To get <code>Object o</code> 
	 * @return object component from the handler
	 */
	public Object getObject() {
		return o;
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
