package org.cytoscape.work;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



public interface HandlerFactory<H extends Handler>{
	
	 H getHandler(Field f, Object o, Tunable t);
	 
	 H getHandler(Method m, Object o, Tunable t);
	 
}