package org.cytoscape.viewmodel;

import org.cytoscape.viewmodel.View;

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
	
	void setValue(View<?> view, T value);
	
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
	 * @param vp the VisualProperty
	 * @return true if current VisualProperty value is locked
	 */
	boolean isValueLocked(final View<?> view);
	
	/**
	 * Clear value lock for given VisualProperty.
	 * 
	 * @param vp the VisualProperty 
	 */
	void clearValueLock(final View<?> view);
}
