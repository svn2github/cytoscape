package org.cytoscape.work;

import java.lang.reflect.Field;


public interface HandlerFactory<H extends Handler>{
	
	 H getHandler(Field f, Object o, Tunable t);
	 
}