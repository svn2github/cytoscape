package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;
import java.io.InputStream;

import org.cytoscape.work.Tunable;


public class InputStreamPropHandler extends AbstractPropHandler {
	public InputStreamPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public InputStreamPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, ((InputStream)getValue()).toString());
		} catch(Exception e){
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				String val = p.getProperty(propKey);
				if ( val != null )
					setValue(val);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
