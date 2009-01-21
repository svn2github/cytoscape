
package org.example.tunable;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

public abstract class AbstractHandler implements Handler {

	protected Field f;
	protected Method m;
	protected Object o;
	protected Tunable t;

	public AbstractHandler(Field f, Object o, Tunable t) {
		this.f = f;
		this.o = o;
		this.t = t;
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
}
