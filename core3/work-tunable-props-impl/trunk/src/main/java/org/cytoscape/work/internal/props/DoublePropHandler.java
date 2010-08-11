package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.work.Tunable;


public class DoublePropHandler extends AbstractPropHandler {
	public DoublePropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public DoublePropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		final Properties p = new Properties();
		try {
			p.setProperty(propKey, getValue().toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(final Properties p) {
		try {
			if (p.containsKey(propKey)) {
				final String val = p.getProperty(propKey).toString();
				if (val != null)
					setValue(Double.valueOf(val));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
