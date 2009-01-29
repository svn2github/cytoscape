
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractHandler implements Handler {

	protected Field f;
	protected Method m;
	protected Object o;
	protected Tunable t;
	protected List<Handler> dependentHandlers;

	public AbstractHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		dependentHandlers = new ArrayList<Handler>();
	}

	public AbstractHandler(Method m, Object o, Tunable t) {
		this.m = m;
		this.o = o;
		this.t = t;
	}

	public Field getField() {
		return f;
	}

	public Method getMethod() {
		return m;
	}

	public Object getObject() {
		return o;
	}

	public Tunable getTunable() {
		return t;
	}

	public void handlerChanged(Handler otherHandler) {
		System.out.println("currently a no-op");
	}

	public void addDependentHandler(Handler otherHandler) {
		if ( otherHandler == null )
			return;
		dependentHandlers.add(otherHandler);
	}

	public boolean removeDependentHandler(Handler otherHandler) {
		if ( otherHandler == null )
			return false;
		return dependentHandlers.remove(otherHandler);	
	}
}
