
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

package org.cytoscape.vizmap;

import org.cytoscape.attributes.CyAttributes;

import org.cytoscape.vizmap.ValueParser;
import org.cytoscape.vizmap.ObjectToString;


import org.cytoscape.view.EdgeView;
import org.cytoscape.view.NodeView;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.view.VisualPropertyCatalog;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import java.awt.Color;

import org.cytoscape.Edge;
import org.cytoscape.GraphObject;
import org.cytoscape.Node;


/**
 * A class that holds the appearance information for NodeViews and EdgeViews. 
 * The class is indexed on {@link VisualPropertyType}, so to access the node
 * shape you would use the VisualPropertyType.NODE_SHAPE enum to get and set
 * node shape values for this Appearance.
 */
public class Appearance {

	private static final String NODE_SIZE_LOCKED = ".nodeSizeLocked";
	protected HashMap<VisualProperty, Object> vizProps;
	protected boolean nodeSizeLocked = true;
	protected CyAttributes attrs;

	/**
	 * Creates a new Appearance object.
	 */
	public Appearance(CyAttributes attrs) {
		this.attrs = attrs;
		vizProps = new HashMap<VisualProperty, Object> ();

		for (VisualProperty vp : VisualPropertyCatalog.collectionOfVisualProperties())
					vizProps.put(vp, vp.getDefaultAppearanceObject());
	}

	public CyAttributes getCyAttributes() {
		return attrs;
	}

	/**
	 * Sets the appearance for the specified VisualPropertyType. 
	 *
	 * @param p The VisualProperty that identifies which aspect of the appearance
	 *          this particular object should be applied to.
	 * @param o The object the defines the appearance for the aspect of the appearance
	 *          defined by parameter p.
	 */
	public void set(VisualProperty vp, Object o) {
		if (o != null)
			vizProps.put(vp, o);
	}

	/**
	 * Gets the appearance for the specified VisualPropertyType. 
	 *
	 * @param p The VisualPropertyType that identifies which aspect of the appearance to get.
	 *
	 * @return An Object of varying type depending on the VisualPropertyType. The
	 *         VisualPropertyType enum defines what the type of this object will be.
	 */
	public Object get(VisualProperty vp) {
		return vizProps.get(vp);
	}

	/**
	 * Applies this appearance to the specified NodeView. 
	 *
	 * @param nodeView The NodeView that this appearance will be applied to. 
	 */
	public void applyAppearance(final NodeView nodeView) {
        for ( VisualProperty vp : VisualPropertyCatalog.collectionOfVisualProperties() )
            if ( vp.getName().equals("NODE_SIZE")){
                if ( nodeSizeLocked )
                    vp.applyToNodeView(nodeView,vizProps.get(vp));
                else
                    continue;
            } else if ( vp.getName().equals("NODE_WIDTH")|| vp.getName().equals("NODE_HEIGHT") ) {
                if ( nodeSizeLocked )
                    continue;
                else
                	vp.applyToNodeView(nodeView,vizProps.get(vp));
            } else
            	vp.applyToNodeView(nodeView,vizProps.get(vp));
	}

	/**
	 * Applies this appearance to the specified EdgeView. 
	 *
	 * @param edgeView The EdgeView that this appearance will be applied to. 
	 */
	public void applyAppearance(final EdgeView edgeView) {
		for (VisualProperty vp : VisualPropertyCatalog.collectionOfVisualProperties())
			vp.applyToEdgeView(edgeView, vizProps.get(vp));
	}

	/**
	 * Applies the specified properties as default values for this appearance. 
	 *
	 * @param nacProps The properties used to specify the default appearance. 
	 * @param baseKey A string identifying which specific properties should be
	 *                used for the appearance.
	 */
	public void applyDefaultProperties(final Properties nacProps, String baseKey) {
		for (VisualProperty vp: VisualPropertyCatalog.collectionOfVisualProperties()) {
			Object o = vp.parseProperty(nacProps, baseKey);

			if (o != null)
				vizProps.put(vp, o);
		}
		
		// Apply nodeSizeLock
		final String lockKey = baseKey + NODE_SIZE_LOCKED;
		final String lockVal = nacProps.getProperty(lockKey);
		if(lockVal == null || lockVal.equalsIgnoreCase("true")) {
			setNodeSizeLocked(true);
		} else {
			setNodeSizeLocked(false);
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

		for (VisualProperty vp: VisualPropertyCatalog.collectionOfVisualProperties()) {
			String key = vp.getName(); // FIXME FIXME: this was something different before refactoring
			String value = ObjectToString.getStringValue(vizProps.get(vp));
			if ( key != null && value != null ) {
//				System.out.println("(Key,val) = " + key + ", " + value + ", basekey = " + baseKey);
				props.setProperty(key,value);
			}
		}

		// Add node size lock as an extra prop.
		final String lockKey = baseKey + NODE_SIZE_LOCKED;
		final String lockVal = new Boolean(getNodeSizeLocked()).toString();
		props.setProperty(lockKey, lockVal);

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
		final StringBuilder sb = new StringBuilder();

		for (VisualProperty vp: VisualPropertyCatalog.collectionOfVisualProperties()) {
			if (vizProps.get(vp) != null) {
				sb.append(prefix);
				sb.append(vp.getName());
				sb.append(" = ");
				sb.append(ObjectToString.getStringValue(vizProps.get(vp)));
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

		final boolean actualLockState = na.getNodeSizeLocked();

        // set everything to false so that it copies correctly
        setNodeSizeLocked(false);
        na.setNodeSizeLocked(false);

		for (VisualProperty vp: VisualPropertyCatalog.collectionOfVisualProperties())
			vizProps.put(vp, na.get(vp));

        // now set the lock state correctly
        setNodeSizeLocked(actualLockState);
        na.setNodeSizeLocked(actualLockState);
	}

	/**
	 * Returns a clone of this Appearance. 
	 *
	 * @return A clone of this Appearance. 
	 */
	public Object clone() {
		Appearance ga = new Appearance(attrs);
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

		for (VisualProperty vp: VisualPropertyCatalog.collectionOfVisualProperties()) {
			Object bypass = getBypass(attrs, id, vp);

			if (bypass != null)
				vizProps.put(vp, bypass);
		}
	}

	/**
	 * A helper method that returns the specified bypass object if one happens to exist for
	 * this node/edge and property type.
	 *
	 * This method has default scope only to help with unit testing. 
	 *
	 * You really shouldn't have any reason to use this method!
	 */
    static Object getBypass( CyAttributes xattrs, String id, VisualProperty vp) {
		String attrName = vp.getName();

        final String value = xattrs.getStringAttribute(id, attrName);

        if (value == null)
            return null;

        ValueParser p = ValueParserCatalog.getValueParser(vp); 

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
        final String v = xattrs.getStringAttribute(id, attrName);

        if (v == null)
            return null;

        if (v.equals("0,0,0"))
            return ret;
        else
            return null;
    }

	/**
	 * Returns whether or not the node height and width are locked.
	 * @return Whether or not the node height and width are locked.
	 */
    public boolean getNodeSizeLocked() {
        return nodeSizeLocked;
    }

	/**
	 * Sets whether or not the node height and width are locked.
	 */
    public void setNodeSizeLocked(boolean b) {
        nodeSizeLocked = b;
    }

}
