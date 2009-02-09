
package HandlerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.List;

import HandlerFactory.Handler;
import HandlerFactory.HandlerListener;

import Tunable.Tunable;

public abstract class AbstractHandler implements Handler {

	protected Field f;
	protected Method m;
	protected Object o;
	protected Tunable t;
	protected List<HandlerListener> listeners;
	
	
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
