package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.work.Tunable;


public class IntPropHandler extends AbstractPropHandler {
	public IntPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public IntPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, getValue().toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				final String val = p.getProperty(propKey).toString();
				if (val != null)
					setValue(Integer.valueOf(val));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
