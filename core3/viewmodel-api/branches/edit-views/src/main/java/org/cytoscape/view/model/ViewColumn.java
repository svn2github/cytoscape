package org.cytoscape.view.model;

import java.util.List;
import java.util.Map;

import org.cytoscape.view.model.View;

/**
 * A column in the viewmodel table
 * @param <T> the data type of the VisualProperty this belongs to
 */
public interface ViewColumn<T> {
	
	Class<T> getDataType();
	/**
	 * 
	 * @param view the "row" value of which to return
	 * @return the value
	 */
	T getValue(View<?> view);
	
	/**
	 * 
	 * Sets the computed value for the given view. Note that using this method
	 * for setting many values will be horribly inefficient. Use setValues()
	 * instead.
	 */
	<V extends T> void setValue(View<?> view, V value);
	
	/**
	 * Bulk method for setting many values at once. This fires only a single event and is thus much more efficient.
	 */
	public void setValues(Map<? extends View<?>, T> values, List<? extends View<?>> toClear);
	
	/**
	 * Remove the value stored for the given view.
	 * 
	 * This is needed to allow reverting the value for a given view to "the default value, whatever that is".
	 * 
	 * @param view the View for which to clear the value 
	 */
	void clearValue(View<?> view);
	/**
	 * Used by VisualStyle.apply to set the per-VisualStyle default value
	 * @param value the per-VisualStyle default value
	 */
	void setDefaultValue(T value);
	
	void setLockedValue(final View<?> view, final T value);
	
	/**
	 * @param view target view
	 * @return true if current VisualProperty value is locked
	 */
	boolean isValueLocked(final View<?> view);
	
	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param view target view 
	 */
	void clearValueLock(final View<?> view);
}
