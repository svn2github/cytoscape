package org.cytoscape.provenance.internal;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.io.File;
import java.net.URL;

import org.cytoscape.work.Tunable;
import org.cytoscape.work.TunableHandlerFactory;
import org.cytoscape.work.util.*;


public class ProvenanceHandlerFactory implements TunableHandlerFactory<ProvenanceHandler> {
	public ProvenanceHandler getHandler(final Field field, final Object instance, final Tunable tunable) {
		final Class<?> type = field.getType();
		return new BasicProvenanceHandler(field, instance, tunable);
/*
		if (type == Boolean.class || type == boolean.class)
			return new BooleanPropHandler(field, instance, tunable);
		else if (type == String.class)
			return new StringPropHandler(field, instance, tunable);
		else if ((type == int.class || type == Integer.class))
			return new IntPropHandler(field, instance, tunable);
		else if ((type == float.class || type == Float.class))
			return new FloatPropHandler(field, instance, tunable);
		else if ((type == long.class || type == Long.class))
			return new LongPropHandler(field, instance, tunable);
		else if (type == Double.class || type == double.class)
			return new DoublePropHandler(field, instance, tunable);
		else if (type == BoundedDouble.class)
			return new BoundedPropHandler<BoundedDouble>(field, instance, tunable);
		else if (type == BoundedInteger.class)
			return new BoundedPropHandler<BoundedInteger>(field, instance, tunable);
		else if (type == BoundedLong.class)
			return new BoundedPropHandler<BoundedLong>(field, instance, tunable);
		else if (type == BoundedFloat.class)
			return new BoundedPropHandler<BoundedFloat>(field, instance, tunable);
		else if (type == ListSingleSelection.class)
			return new ListSinglePropHandler<Object>(field, instance, tunable);
		else if (type == ListMultipleSelection.class)
			return new ListMultiplePropHandler<Object>(field, instance, tunable);
		else if (type == File.class)
			return new FilePropHandler(field, instance, tunable);
		else if (type == URL.class)
			return new URLPropHandler(field, instance, tunable);
		return null;
		*/
	}

	public ProvenanceHandler getHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		final Class<?> type = getter.getReturnType();
		return new BasicProvenanceHandler(getter, setter, instance, tunable);
/*
		if (type == Boolean.class || type == boolean.class)
			return new BooleanPropHandler(getter, setter, instance, tunable);
		else if (type == String.class)
			return new StringPropHandler(getter, setter, instance, tunable);
		else if ((type == int.class || type == Integer.class))
			return new IntPropHandler(getter, setter, instance, tunable);
		else if ((type == float.class || type == Float.class))
			return new FloatPropHandler(getter, setter, instance, tunable);
		else if ((type == long.class || type == Long.class))
			return new LongPropHandler(getter, setter, instance, tunable);
		else if (type == Double.class || type == double.class)
			return new DoublePropHandler(getter, setter, instance, tunable);
		else if (type == BoundedDouble.class)
			return new BoundedPropHandler<BoundedDouble>(getter, setter, instance, tunable);
		else if (type == BoundedInteger.class)
			return new BoundedPropHandler<BoundedInteger>(getter, setter, instance, tunable);
		else if (type == BoundedLong.class)
			return new BoundedPropHandler<BoundedLong>(getter, setter, instance, tunable);
		else if (type == BoundedFloat.class)
			return new BoundedPropHandler<BoundedFloat>(getter, setter, instance, tunable);
		else if (type == ListSingleSelection.class)
			return new ListSinglePropHandler<Object>(getter, setter, instance, tunable);
		else if (type == ListMultipleSelection.class)
			return new ListMultiplePropHandler<Object>(getter, setter, instance, tunable);
		else if (type == File.class)
			return new FilePropHandler(getter, setter, instance, tunable);
		else if (type == URL.class)
			return new URLPropHandler(getter, setter, instance, tunable);
		return null;
		*/
	}
}
