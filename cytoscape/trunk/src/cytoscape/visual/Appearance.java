
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

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

package cytoscape.visual;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.visual.parsers.*;

import giny.model.Edge;
import giny.model.GraphObject;
import giny.model.Node;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;


/**
 * A class that holds the appearance information for NodeViews and EdgeViews. 
 * The class is indexed on {@link VisualPropertyType}, so to access the node
 * shape you would use the VisualPropertyType.NODE_SHAPE enum to get and set
 * node shape values for this Appearance.
 */
public class Appearance {

	protected Object[] vizProps;

	/**
	 * Creates a new Appearance object.
	 */
	public Appearance() {
		vizProps = new Object[VisualPropertyType.values().length];

		for (VisualPropertyType type : VisualPropertyType.values())
			vizProps[type.ordinal()] = type.getVisualProperty().getDefaultAppearanceObject();
	}

	/**
	 * Sets the appearance for the specified VisualPropertyType. 
	 *
	 * @param p The VisualPropertyType that identifies which aspect of the appearance
	 *          this particular object should be applied to.
	 * @param o The object the defines the appearance for the aspect of the appearance
	 *          defined by parameter p.
	 */
	public void set(VisualPropertyType p, Object o) {
		if (o != null)
			vizProps[p.ordinal()] = o;
	}

	/**
	 * Gets the appearance for the specified VisualPropertyType. 
	 *
	 * @param p The VisualPropertyType that identifies which aspect of the appearance to get.
	 *
	 * @return An Object of varying type depending on the VisualPropertyType. The
	 *         VisualPropertyType enum defines what the type of this object will be.
	 */
	public Object get(VisualPropertyType p) {
		return vizProps[p.ordinal()];
	}

	/**
	 * Applies this appearance to the specified NodeView. 
	 *
	 * @param nodeView The NodeView that this appearance will be applied to. 
	 */
	public void applyAppearance(final NodeView nodeView) {
		for (VisualPropertyType type : VisualPropertyType.values())
			type.getVisualProperty().applyToNodeView(nodeView, vizProps[type.ordinal()]);
	}

	/**
	 * Applies this appearance to the specified EdgeView. 
	 *
	 * @param edgeView The EdgeView that this appearance will be applied to. 
	 */
	public void applyAppearance(final EdgeView edgeView) {
		for (VisualPropertyType type : VisualPropertyType.values())
			type.getVisualProperty().applyToEdgeView(edgeView, vizProps[type.ordinal()]);
	}

	/**
	 * Applies the specified properties as default values for this appearance. 
	 *
	 * @param nacProps The properties used to specify the default appearance. 
	 * @param baseKey A string identifying which specific properties should be
	 *                used for the appearance.
	 */
	public void applyDefaultProperties(final Properties nacProps, String baseKey) {
		for (VisualPropertyType type : VisualPropertyType.values()) {
			Object o = type.getVisualProperty().parseProperty(nacProps, baseKey);

			if (o != null)
				vizProps[type.ordinal()] = o;
		}
	}

	/**
	 * Returns a Properties object that defines this appearance. 
	 *
	 * @param baseKey The key to use to identify the specific properties for this appearance.
	 *
	 * @return A Properties object that defines this appearance. 
	 */
	public Properties getDefaultProperties(String baseKey) {
		Properties props = new Properties();

		for (VisualPropertyType type : VisualPropertyType.values())
			props.setProperty(type.getDefaultPropertyKey(baseKey),
			                  ObjectToString.getStringValue(vizProps[type.ordinal()]));

		return props;
	}

	/**
	 * Returns a string describing this appearance. 
	 *
	 * @param prefix Can be used to apply an identifying prefix to the output strings.  
	 *
	 * @return A string describing the appearance. 
	 */
	public String getDescription(String prefix) {
		if (prefix == null)
			prefix = "";

		final String lineSep = System.getProperty("line.separator");
		final StringBuffer sb = new StringBuffer();

		for (VisualPropertyType type : VisualPropertyType.values()) {
			if (vizProps[type.ordinal()] != null) {
				sb.append(prefix);
				sb.append(type.getName());
				sb.append(" = ");
				sb.append(ObjectToString.getStringValue(vizProps[type.ordinal()]));
				sb.append(lineSep);
			}
		}

		return sb.toString();
	}

	/**
	 * Returns a string describing this appearance with no identifying prefix. 
	 *
	 * @return A string describing the appearance. 
	 */
	public String getDescription() {
		return getDescription(null);
	}

	/**
	 * Copies the specified Appearance into <i>this</i> Appearance object. 
	 *
	 * @param na The Appearance object that will be copied into <i>this</i> Appearance object. 
	 */
	public void copy(final Appearance na) {
		for (VisualPropertyType type : VisualPropertyType.values())
			this.vizProps[type.ordinal()] = na.get(type);
	}

	/**
	 * Returns a clone of this Appearance. 
	 *
	 * @return A clone of this Appearance. 
	 */
	public Object clone() {
		Appearance ga = new Appearance();
		ga.copy(this);

		return ga;
	}

	/**
	 * Applies the visual bypass values specified in the node (edge) attributes
	 * for the specified node (edge) to the node (edge). 
	 *
	 * @param n The {@link Node} or {@link Edge} object that the visual bypass 
	 *          should be applied to.
	 */
	public void applyBypass(final GraphObject n) {
		if (n == null)
			return;

		final String id = n.getIdentifier();
		CyAttributes attrs = null;

		if (n instanceof Node)
			attrs = Cytoscape.getNodeAttributes();
		else if (n instanceof Edge)
			attrs = Cytoscape.getEdgeAttributes();
		else

			return;

		for (VisualPropertyType type : VisualPropertyType.values()) {
			Object bypass = getBypass(attrs, id, type.getBypassAttrName(), type.getDataType());

			if (bypass != null)
				vizProps[type.ordinal()] = bypass;
		}
	}

	private static Map<Class,ValueParser> parsers = new HashMap<Class,ValueParser>();

	/**
	 * A helper method that returns the specified bypass object if one happens to exist for
	 * this node/edge and property type.
	 *
	 * This method has default scope only to help with unit testing. 
	 *
	 * You really shouldn't have any reason to use this method!
	 */
    static Object getBypass(CyAttributes attrs, String id, String attrName, Class type) {

        final String value = attrs.getStringAttribute(id, attrName);

        if (value == null)
            return null;

        ValueParser p = parsers.get(type);

        if (p == null) {
            p = ParserFactory.getParser(type);
            parsers.put(type, p);
        }

        Object ret = null;
        if (p != null)
            ret = p.parseStringValue(value);
        else
            return null;

        // now do color...
        if ( ret == null || !(ret instanceof Color) || !(ret.equals(Color.black)))
            return ret;

        // now check to see that the attribute actually specifies black,
        // and isn't returning black by default
        final String v = attrs.getStringAttribute(id, attrName);

        if (v == null)
            return null;

        if (v.equals("0,0,0"))
            return ret;
        else
            return null;
    }
}
