package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.work.Tunable;


public class BooleanPropHandler extends AbstractPropHandler{
	public BooleanPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}
	
	public BooleanPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps(){
		Properties p = new Properties();
	 	try {
	 		p.setProperty(propKey, getValue().toString());
	 	} catch(final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p){
		try {
			if (p.containsKey(propKey)) {
				final String val = p.getProperty(propKey).toString();
				if (val != null)
					setValue(Boolean.valueOf(val));
			}
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}
