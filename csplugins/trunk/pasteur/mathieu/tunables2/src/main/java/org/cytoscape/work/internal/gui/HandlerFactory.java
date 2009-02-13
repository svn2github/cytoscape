package org.cytoscape.work.internal.gui;

import java.lang.reflect.Field;

import org.cytoscape.work.Handler;
import org.cytoscape.work.Tunable;


public interface HandlerFactory<H extends Handler>{
	
	 H getHandler(Field f, Object o, Tunable t);
	 
}