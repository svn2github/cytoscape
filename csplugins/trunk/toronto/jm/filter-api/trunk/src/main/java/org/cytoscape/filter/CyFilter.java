package org.cytoscape.filter;

import java.util.Map;

import javax.swing.JComponent;

/**
 * Selects nodes and edges from a <code>CyNetwork</code> through some
 * implementation-defined heuristic.
 */
public interface CyFilter {
	/**
	 * Returns the name of this filter.
	 * @return
	 */
    String getName();
    
    /**
     * Sets the name of this filter to <code>name</code>.
     * @param name
     */
    void setName(String name);
    
    /**
     * Returns the value associated with the property named <code>name</code>,
     * or <code>null</code> if no value was previously associated.
     * @param <T> the type of the property value to fetch.
     * @param name the name of the property
     * @param type
     * @return
     */
    <T> T getProperty(String name, Class<T> type);
    
    /**
     * Associates the given value with the property named <code>name</code>.
     * Setting the value to <code>null</code> effectively removes the
     * association.
     * @param <T> the type of the property value to set.
     * @param name
     * @param value
     */
    <T> void setProperty(String name, T value);
    
    /**
     * Returns a <code>Map</code> where the keys are property names and the
     * values are their respective values.  Each supported property name is
     * present as a key in this map.  The values of unset properties is
     * <code>null</code>.
     * @return
     */
    Map<String, Object> getAllProperties();
    
    /**
     * Adds the nodes and edges selected by the filter to <code>result</code>.
     * @param result
     */
    void apply(CyFilterResult result);
    
    /**
     * Returns a Swing component that contains a GUI for configuring the
     * properties of this <code>CyFilter</code>.
     * @return
     */
    JComponent getSettingsUI();
}
