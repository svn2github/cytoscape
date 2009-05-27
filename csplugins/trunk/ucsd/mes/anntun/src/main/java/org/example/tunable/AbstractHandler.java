
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

import java.util.List;
import java.util.ArrayList;

public abstract class AbstractHandler implements Handler {

	protected Field f;
	protected Method gmethod;
	protected Method smethod;
	protected Method m;
	protected Object o;
	protected Tunable t;
	protected Tunable tg;
	protected Tunable ts;
	
	protected List<HandlerListener> listeners;

	public AbstractHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
		listeners = new ArrayList<HandlerListener>();
	}

	public AbstractHandler(Method m, Object o, Tunable t) {
		this.m = m;
		this.o = o;
		this.t = t;
	}
	
	public AbstractHandler(Method getm, Method setm, Object o, Tunable tg, Tunable ts){
		this.gmethod = getm;
		this.smethod = setm;
		this.o = o;
		this.tg = tg;
		this.ts = ts;
	}

	public Field getField() {
		return f;
	}

	public Method getMethod() {
		return m;
	}

	public Method getGetMethod() {
		return gmethod;
	}
	public Method getSetMethod() {
		return smethod;
	}
	
	
	public Object getObject() {
		return o;
	}

	public Tunable getTunable() {
		return t;
	}

	public void handlerChanged(Handler h) {
		//System.out.println("currently a no-op");
	}

	public void addHandlerListener(HandlerListener listener) {
		if ( listener == null )
			return;
		listeners.add(listener);
	}

	public boolean removeHandlerListener(HandlerListener listener) {
		if ( listener == null )
			return false;
		return listeners.remove(listener);	
	}
}
