package HandlerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import Tunable.Tunable;


public interface Handler{
	Field getField();
	Method getMethod();
	Object getObject();
	Tunable getTunable();
	
	void addHandlerListener(HandlerListener listener);
	boolean removeHandlerListener(HandlerListener listener); 

	void handlerChanged(Handler otherHandler);
	
}