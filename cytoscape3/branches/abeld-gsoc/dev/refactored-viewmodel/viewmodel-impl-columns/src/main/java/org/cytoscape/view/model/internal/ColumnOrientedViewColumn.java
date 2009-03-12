package org.cytoscape.view.model.internal;

import java.util.HashMap;

import org.cytoscape.view.model.View;
import org.cytoscape.view.model.ViewColumn;
import org.cytoscape.view.model.VisualProperty;

/**
 * A column in the viewmodel table
 * 
 * TODO: current check lock value every time value is accessed. Might be more
 * efficient to optimize for access speed by swapping locked value and computed
 * value whenever lock is set or cleared.
 * 
 * @param <T>
 *            the data type of the VisualProperty this belongs to
 */
public class ColumnOrientedViewColumn<T> implements ViewColumn<T> {
	private static final String VIEW_IS_NULL = "View is null";

	private final HashMap<View<?>, T> values;
	private final HashMap<View<?>, T> lockedValues;
	// note: this surely could be done more efficiently...:
	private final HashMap<View<?>, Boolean> bypassLocks;
	private final VisualProperty<T> vp;
	/**
	 * The per-VisualStyle default value for the VisualProperty that this Column
	 * represents
	 */
	private T defaultValue;

	/**
	 * Create for given VP
	 * 
	 * @param vp
	 *            the VisualProperty
	 */
	public ColumnOrientedViewColumn(final VisualProperty<T> vp) {
		this.vp = vp;
		this.values = new HashMap<View<?>, T>();
		this.lockedValues = new HashMap<View<?>, T>();
		this.bypassLocks = new HashMap<View<?>, Boolean>();
		this.defaultValue = vp.getDefault();
	}

	public Class<T> getDataType() {
		return vp.getType();
	}

	public T getValue(View<?> view) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);

		final Boolean b = bypassLocks.get(view);
		if ((b != null) && b.booleanValue()) {
			return lockedValues.get(view);
		} else if (values.containsKey(view)) {
			return values.get(view);
		} else {
			return defaultValue;
		}
	}

	public void setValue(View<?> view, T value) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);

		values.put(view, value);
	}

	public void clearValue(View<?> view) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);

		values.remove(view);
	}

	/**
	 * Used by VisualStyle.apply to set the per-VisualStyle default value
	 * 
	 * @param value
	 *            the per-VisualStyle default value
	 */
	public void setDefaultValue(T value) {
		defaultValue = value;
	}

	public void setLockedValue(final View<?> view, final T value) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);
		lockedValues.put(view, value);
		bypassLocks.put(view, Boolean.TRUE);
	}

	/**
	 * @param vp
	 *            the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	public boolean isValueLocked(final View<?> view) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);

		final Boolean value = bypassLocks.get(view);

		if (value == null) {
			return false;
		} else {
			return value.booleanValue();
		}
	}

	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param vp
	 *            the VisualProperty
	 */
	public void clearValueLock(final View<?> view) {
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);
		lockedValues.remove(view);
		bypassLocks.put(view, Boolean.FALSE);
	}

}
