package org.cytoscape.work.internal.tunables;


import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;

import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.BookmarksUtil;
import org.cytoscape.property.bookmark.Bookmarks;

import org.cytoscape.work.HandlerFactory;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.swing.GUITunableHandler;
import org.cytoscape.work.util.BoundedDouble;
import org.cytoscape.work.util.BoundedFloat;
import org.cytoscape.work.util.BoundedInteger;
import org.cytoscape.work.util.BoundedLong;
import org.cytoscape.work.util.ListMultipleSelection;
import org.cytoscape.work.util.ListSingleSelection;


/**
 * Provides a factory to create <code>GUITunableHandler</code> depending on their type
 * A <code>GUITunableHandler</code> is simply a <code>Handler</code> with the GUI aspect to display the handled object to the user in a proper way.
 *
 * @author pasteur
 */
public class GUITunableHandlerFactory implements HandlerFactory<GUITunableHandler> {
	private Bookmarks bookmarks;
	private BookmarksUtil bkUtil;

	/**
	 * creates a new GUITunableHandlerFactory object
	 * @param book	informations and properties of the <code>Bookmarks</code> registered
	 * @param bkUtil object that provides tools to manage the <code>Bookmarks</code>
	 */
	public GUITunableHandlerFactory(CyProperty<Bookmarks> book, BookmarksUtil bkUtil) {
		this.bookmarks = book.getProperties();
		this.bkUtil = bkUtil;
	}

	/**
	 * To get a <code>Handler</code> for a get and set Methods annotated as <code>Tunable</code>
	 *
	 * @param	getter   the annotated get method
	 * @param	setter   the annotated set method
	 * @param	instance object on which the getter and setter methods will invoked
	 * @param	tunable  Tunable annotation of the getter method
	 * @return a <code>GUITunableHandler</code> object depending on the Method's type
	 */
	public GUITunableHandler getHandler(final Method getter, final Method setter, final Object instance, final Tunable tunable) {
		final Class<?> type = getter.getReturnType();

		if (type == Boolean.class || type == boolean.class)
			return new BooleanHandler(getter, setter, instance, tunable);
		if (type == String.class)
			return new StringHandler(getter, setter, instance, tunable);
		if (type == Integer.class || type == int.class)
			return new IntegerHandler(getter, setter, instance, tunable);
		if (type == Double.class || type == double.class)
			return new DoubleHandler(getter, setter, instance, tunable);
		if (type == Float.class || type == float.class)
			return new FloatHandler(getter, setter, instance, tunable);
		if (type == Long.class || type == long.class)
			return new LongHandler(getter, setter, instance, tunable);
		if (type == BoundedInteger.class)
			return new BoundedHandler<BoundedInteger>(getter, setter, instance, tunable);
		if (type == BoundedLong.class)
			return new BoundedHandler<BoundedLong>(getter, setter, instance, tunable);
		if (type == BoundedFloat.class)
			return new BoundedHandler<BoundedFloat>(getter, setter, instance, tunable);
		if (type == BoundedDouble.class)
			return new BoundedHandler<BoundedDouble>(getter, setter, instance, tunable);
		if (type == ListSingleSelection.class)
			return new ListSingleHandler<String>(getter, setter, instance, tunable);
		if (type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(getter, setter, instance, tunable);
		if (type == File.class)
			return new FileHandler(getter, setter, instance, tunable);
		if (type == URL.class)
			return new URLHandler(getter, setter, instance, tunable, bookmarks, bkUtil);
		if (type == InputStream.class)
			return new InputStreamHandler(getter, setter, instance, tunable, bookmarks, bkUtil);

		return null;
	}

	/**
	 * To get a <code>Handler</code> for a Method annotated as <code>Tunable</code>
	 *
	 * @param	m	the annotated method
	 * @param	o	object of the <code>Handler</code>
	 * @param	t	tunable of the <code>Handler</code>
	 * @return a <code>GUITunableHandler</code> object depending on the Method's type
	 */
	public GUITunableHandler getHandler(Method m, Object o, Tunable t) {
		return null;
	}

	/**
	 * To get a <code>Handler</code> for a Field annotated as <code>Tunable</code> for a specific type of object
	 *
	 * @param	field    the annotated field
	 * @param	instance object on which we will get/set the field
	 * @param	tunable  a representation of the @Tunable on the field
	 * @return a <code>GUITunableHandler</code> object depending on the field's type
	 */
	public GUITunableHandler getHandler(final Field field, final Object instance, final Tunable tunable) {
		final Class<?> type = field.getType();

		if (type == Boolean.class || type == boolean.class)
			return new BooleanHandler(field, instance, tunable);
		if (type == String.class)
			return new StringHandler(field, instance, tunable);
		if (type == Integer.class || type == int.class)
			return new IntegerHandler(field, instance, tunable);
		if (type == Double.class || type == double.class)
			return new DoubleHandler(field, instance, tunable);
		if (type == Float.class || type == float.class)
			return new FloatHandler(field, instance, tunable);
		if (type == Long.class || type == long.class)
			return new LongHandler(field, instance, tunable);
		if (type == BoundedInteger.class)
			return new BoundedHandler<BoundedInteger>(field, instance, tunable);
		if (type == BoundedLong.class)
			return new BoundedHandler<BoundedLong>(field, instance, tunable);
		if (type == BoundedFloat.class)
			return new BoundedHandler<BoundedFloat>(field, instance, tunable);
		if (type == BoundedDouble.class)
			return new BoundedHandler<BoundedDouble>(field, instance, tunable);
		if (type == ListSingleSelection.class)
			return new ListSingleHandler<String>(field, instance, tunable);
		if (type == ListMultipleSelection.class)
			return new ListMultipleHandler<String>(field, instance, tunable);
		if (type == File.class)
			return new FileHandler(field, instance, tunable);
		if (type == URL.class)
			return new URLHandler(field, instance, tunable, bookmarks, bkUtil);
		if (type == InputStream.class)
			return new InputStreamHandler(field, instance, tunable, bookmarks, bkUtil);

		return null;
	}
}
