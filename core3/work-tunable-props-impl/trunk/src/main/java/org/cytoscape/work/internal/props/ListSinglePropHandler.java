package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;


public class ListSinglePropHandler<T> extends AbstractPropHandler {
	public ListSinglePropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
	}

	public ListSinglePropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
	}

	public Properties getProps() {
		Properties p = new Properties();
		try {
			p.setProperty(propKey, ((ListSingleSelection<T>)getValue()).getSelectedValue().toString());
		} catch(final Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	public void setProps(Properties p) {
		try {
			if (p.containsKey(propKey)) {
				final ListSingleSelection<T> lss = (ListSingleSelection<T>)getValue();
				final T val = (T)p.getProperty(propKey).toString(); //FIXME!
				if (val != null) {
					lss.setSelectedValue(val);
					setValue(lss);
				}
			}
		} catch(final Exception e) {
			e.printStackTrace();
		}
	}
}
