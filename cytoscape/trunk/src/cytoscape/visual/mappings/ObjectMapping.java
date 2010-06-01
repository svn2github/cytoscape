/*
  File: ObjectMapping.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.visual.mappings;

import java.util.Map;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;

import cytoscape.CyNetwork;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.parsers.ValueParser;


/**
 * Mappings should implement this interface. Mappings are classes that map from
 * a value stored in the edge attributes or node attributes HashMap in
 * {@link cytoscape.CyAttributes}. The range of the mapping depends on the
 * {@link cytoscape.visual.calculators.AbstractCalculator} that owns
 * the mapping.
 * <p>
 * All classes implementing this interface <b>MUST</b> have a constructor that
 * takes the arguments Object, CyNetwork, byte, where Object is the default object
 * the mapper should map to, CyNetwork is the CyNetwork object representing the network
 * displayed in Cytoscape, and the byte is one of {@link #EDGE_MAPPING} or
 * {@link #NODE_MAPPING}.
 * 
 * K - Key attribute value.  Can be any type (for this implementation, number, string, boolean, list, or map).
 * V - Mapped visual value, such as color, node shape, etc.
 * 
 */
public interface ObjectMapping<V> extends Cloneable {
    
	@Deprecated // Use attribute name to determine mapping type.
    public static final byte EDGE_MAPPING = 0;
	@Deprecated
    public static final byte NODE_MAPPING = 1;

	
	/**
	 * Class of mapped object.  For example, if this is an Node Color mapping,
	 * this value is Color.class.
	 * 
	 * @return
	 */
    public Class<V> getRangeClass();

    /**
     * Return the classes that the ObjectMapping can map from, eg. the contents
     * of the data of the controlling attribute.
     * <p>
     * For example, DiscreteMapping {@link DiscreteMapping} can only accept
     * String types in the mapped attribute data. Likewise, ContinuousMapping
     * {@link ContinuousMapping} can only accept numeric types in the mapped
     * attribute data since it must interpolate.
     * <p>
     * Return null if this mapping has no restrictions on the domain type.
     *
     * @return Array of accepted attribute data class types
     */
    public Class<?>[] getAcceptedDataClasses();

    /**
     * Set the controlling attribute name. The current mappings will be unchanged
     * if preserveMapping is true and cleared otherwise. The network argument is
     * provided so that the current values for the given attribute name can
     * be loaded for UI purposes. Null values for the network argument are allowed.
     * 
     * Do not use this method.  None of the network, preserveMapping parameters are used in Cytoscape.
     * 
     * @deprecated Will be removed in 2.8. Use setControllingAttributeName(final String controllingAttrName) instead.
     * 
     */
    @Deprecated
    public void setControllingAttributeName(String attrName, CyNetwork network,
        boolean preserveMapping);

    
    /**
     * Set controlling attribute of this mapping.
     * 
     * @param controllingAttrName - name of the attribute associated with this mapping.
     * 
     */
    public void setControllingAttributeName(final String controllingAttrName);
    
    
    /**
     * Get the controlling attribute name
     */
    public String getControllingAttributeName();

    /**
     * Add a ChangeListener to the mapping. When the state underlying the
     * mapping changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param    l    ChangeListener to add
     */
    public void addChangeListener(ChangeListener l);

    /**
     * Remove a ChangeListener from the mapping. When the state underlying the
     * mapping changes, all ChangeListeners will be notified.
     *
     * This is used in the UI classes to ensure that the UI panes stay consistent
     * with the data held in the mappings.
     *
     * @param    l    ChangeListener to add
     */
    public void removeChangeListener(ChangeListener l);

    /**
     * Create a mapped visual representation from the given attribute value.
     * 
     * @param attrBundle
     * @return
     */
    public V calculateRangeValue(final Map<String, Object> attrBundle);

    public JPanel getLegend(VisualPropertyType type);

    public Object clone();

    public void applyProperties(Properties props, String baseKey, ValueParser<V> parser);

    public Properties getProperties(String baseKey);
    
    /**
     * Do not use this method.  Will be removed in next release (2.8)
     * 
     * It was for old VizMape GUI which was removed in 2.5.
     * 
     * @param parent
     * @param network
     * @return
     */
    @Deprecated
    public JPanel getUI(JDialog parent, CyNetwork network);
}
