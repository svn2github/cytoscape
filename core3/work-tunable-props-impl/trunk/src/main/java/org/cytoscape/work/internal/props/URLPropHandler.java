package org.cytoscape.work.internal.props;


import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.cytoscape.work.Tunable;


public class URLPropHandler extends AbstractPropHandler {
	public URLPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public URLPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, getValue().toString());
		} catch(final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				final String val = p.getProperty(propKey);
				if (val != null) {
					try {
						URL url = new URL(val);
						setValue(url);
					} catch (final Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
