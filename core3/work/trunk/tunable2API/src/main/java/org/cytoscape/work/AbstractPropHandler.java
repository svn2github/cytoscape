package org.cytoscape.work;

import java.lang.reflect.*;
import java.util.*;

public abstract class AbstractPropHandler extends AbstractHandler implements PropHandler {

	protected String propKey;

	public AbstractPropHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
		String n = f.getDeclaringClass().toString();

		propKey = n.substring( n.lastIndexOf(".") + 1) + "." + f.getName();	
	}
	
	public abstract Properties getProps();
	public abstract void setProps(Properties p);
}
