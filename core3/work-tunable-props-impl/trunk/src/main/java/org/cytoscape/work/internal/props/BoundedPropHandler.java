package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.AbstractBounded;


public class BoundedPropHandler<T extends AbstractBounded<?>> extends AbstractPropHandler {
	public BoundedPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public BoundedPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, ((T)getValue()).getValue().toString());
        } catch(final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(final Properties p) {
		try {
			if (p.containsKey(propKey)){
				T bo = (T)getValue();
				String val = p.getProperty(propKey).toString();
				if (val != null) {
					bo.setValue(val);
					setValue(bo);
				}
			}
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}