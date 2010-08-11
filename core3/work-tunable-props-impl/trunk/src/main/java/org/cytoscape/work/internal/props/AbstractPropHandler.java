package org.cytoscape.work.internal.props;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.work.AbstractTunableHandler;
import org.cytoscape.work.Tunable;


public abstract class AbstractPropHandler extends AbstractTunableHandler implements PropHandler {
	protected String propKey;

	public AbstractPropHandler(final Field field, final Object instance, final Tunable tunable) {
		super(field, instance, tunable);
		propKey = getQualifiedName();
	}

	public AbstractPropHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		super(getter, setter, instance, tunable);
		propKey = getQualifiedName();
	}
	
	public abstract Properties getProps();
	public abstract void setProps(Properties p);
}
