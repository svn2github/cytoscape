package org.cytoscape.model.attrs.impl;

import java.util.*;
import org.cytoscape.model.attrs.CyAttributes;

public class CyAttributesImpl implements CyAttributes {

	private final Map<String, Attr> attributes;

	public CyAttributesImpl() {
		attributes = Collections.synchronizedMap( new HashMap<String,Attr>() );
	}

	public String[] getAttributeNames() {
		Set<String> keys = attributes.keySet();
		String[] ret = new String[ keys.size() ];
		int i = 0;
		for ( String s : keys )
			ret[i++] = s;

		return ret;
	}

	/**
	 * It's almost like we need attributes of Attributes...
	 */
	public void setAttributeDescription(String attributeName, String description) {
		if ( attributeName != null &&
		     description != null && 
		     attributes.containsKey( attributeName ) )
			attributes.get( attributeName ).setDescription( description );
	}

	/**
	 *
	 */
	public String getAttributeDescription(String attributeName) {
		return attributes.get(attributeName).getDescription();
	}

	public Class getAttributeType(String attributeName) {
		return attributes.get(attributeName).getType();
	}

	/**
	 *
	 */
	public void setUserVisible(String attributeName, boolean value) {
		if ( attributeName != null &&
			 attributes.containsKey( attributeName ) )
			 attributes.get(attributeName).setVisible(value);
	}

	/**
	 *
	 */
	public boolean getUserVisible(String attributeName) {
		return attributes.get(attributeName).isVisible();
	}

	/**
	 *
	 */
	public void setUserEditable(String attributeName, boolean value) {
		if ( attributeName != null &&
			 attributes.containsKey( attributeName ) )
			 attributes.get(attributeName).setEditable(value);
	}

	/**
	 *
	 */
	public boolean getUserEditable(String attributeName) {
		return attributes.get(attributeName).isEditable();
	}


	public void deleteAttribute(String attributeName) {
		if ( attributes.containsKey(attributeName) ) {
			// TODO fire AttributeDeletedEvent
			attributes.remove( attributeName );
		}
	}

	public void set(String attrName, Object value) {
		if ( value == null )
			throw new NullPointerException("value is null");
		if ( attrName == null )
			throw new NullPointerException("attribute name is null");

		checkType(value);

		// create new attribute
		if ( !attributes.containsKey(attrName) ) {
			attributes.put( attrName, new Attr( value, "", true, true ) );	

		// set existing attribute
		} else {
			attributes.get( attrName ).setAttr(value);
		}

		// TODO AttributeSetEvent 
	}

	public <T> T get(String attrName, Class<? extends T> type) {
		checkType(type);

		Attr vl = attributes.get(attrName);

		if (vl == null) {
			return null;
		}

		return type.cast(vl.getAttr());
	}

	public <T> boolean contains(String attrName, Class<? extends T> type) {
		checkType(type);

		Attr vl = attributes.get(attrName);
		
		if (vl == null) {
			return false;
		}

		if ( type.isAssignableFrom(vl.getType()) ) {
			return true;
		} else {
			return false;
		}
	}

	private <T> void checkType(Class<? extends T> type) {
		if ( type == Integer.class ||
		     type == Double.class ||
		     type == Boolean.class ||
		     type == String.class ||
		     type == List.class ||
		     type == Map.class )
		 	return;
		else
			throw new IllegalArgumentException("invalid type: " + type.getName());
	}

	private void checkType(Object o) {
		if ( o instanceof String )
			return ;
		else if ( o instanceof Integer )
			return ;
		else if ( o instanceof Boolean )
			return ;
		else if ( o instanceof Double )
			return ;
		else if ( o instanceof List ) {
			List l = (List)o;
			if ( l.size() <= 0 )
				throw new IllegalArgumentException("empty list");
			else
				checkType(l.get(0));
		} else if ( o instanceof Map ) {
			Map m = (Map)o;
			Object[] keys = m.keySet().toArray();
			if ( keys.length <= 0 ) {
				throw new IllegalArgumentException("empty map");
			} else {
				checkType(m.get(keys[0]));
				checkType(keys[0]);
			}
		} else
			throw new IllegalArgumentException("invalid type: " + o.getClass().toString() );	
	}
}
