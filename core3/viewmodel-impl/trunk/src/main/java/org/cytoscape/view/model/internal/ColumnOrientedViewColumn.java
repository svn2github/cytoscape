package org.cytoscape.view.model.internal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/**
	 * 
	 * Sets the computed value for the given view. Note that using this method
	 * for setting many values will be horribly inefficient. Use setValues()
	 * instead.
	 */
	public void setValue(View<?> view, T value) {
		internal_setValue(view, value);
		// FIXME: fire event!
	}

	/**
	 * Bulk method for setting many values at once. This fires only a single event and is thus much more efficient.
	 */
	public void setValues(Map<? extends View<?>, T> values, List<? extends View<?>> toClear) {
		for (Map.Entry<? extends View<?>, T> entry : values.entrySet()){
			internal_setValue(entry.getKey(), entry.getValue());
		}
		if ( toClear != null ) {
			for (View<?>v: toClear){
				internal_clearValue(v);
			}
		}
		// FIXME: fire event!
	}
	
	/** An internal method, to avoid duplicating this code in setValue() and setValues(). This method does not fire events!
	 * (I hope the compiler optimizes this away...)
	 */
	private void internal_setValue(View<?> view, T value){
		if (view == null)
			throw new NullPointerException(VIEW_IS_NULL);

		values.put(view, value);
	}
	
	public void clearValue(View<?> view) {
		internal_clearValue(view);
		// FIXME: fire event!
	}
	
	private void internal_clearValue(View<?> view) {
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
