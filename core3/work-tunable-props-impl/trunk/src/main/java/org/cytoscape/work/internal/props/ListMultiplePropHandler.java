package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListMultipleSelection;


public class ListMultiplePropHandler<T> extends AbstractPropHandler {
	public ListMultiplePropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public ListMultiplePropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try{
			p.setProperty(propKey, ((ListMultipleSelection<T>)getValue()).getSelectedValues().toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				final ListMultipleSelection<T> lms = (ListMultipleSelection<T>)getValue();
				T[] tab = (T[])p.getProperty(propKey).split(",");
				if (tab != null) {
					lms.setSelectedValues(Arrays.asList(tab));
					setValue(lms);
				}
			}
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}

