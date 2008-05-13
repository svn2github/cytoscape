package org.cytoscape.attrs.impl;

import java.util.*;
import org.cytoscape.attrs.CyAttributes;

public class CyAttributesImpl implements CyAttributes {

	private Map<String, Map<Integer, Object>> attributes;
	private Map<String, Class> types;
	private Map<String, String> descriptions;
	private Map<String, Boolean> editable;
	private Map<String, Boolean> visible;

	public CyAttributesImpl() {
		attributes = new HashMap<String,Map<Integer,Object>>();
		types = new HashMap<String,Class>();
		descriptions = new HashMap<String,String>();
		editable = new HashMap<String,Boolean>();
		visible = new HashMap<String,Boolean>();
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
			descriptions.put( attributeName, description );
	}

	/**
	 *
	 */
	public String getAttributeDescription(String attributeName) {
		return descriptions.get(attributeName);
	}

	/**
	 *
	 */
	public void setUserVisible(String attributeName, boolean value) {
		if ( attributeName != null &&
			 attributes.containsKey( attributeName ) )
			 visible.put(attributeName, value);
	}

	/**
	 *
	 */
	public boolean getUserVisible(String attributeName) {
		return visible.get(attributeName);
	}

	/**
	 *
	 */
	public void setUserEditable(String attributeName, boolean value) {
		if ( attributeName != null &&
			 attributes.containsKey( attributeName ) )
			 editable.put(attributeName, value);
	}

	/**
	 *
	 */
	public boolean getUserEditable(String attributeName) {
		return editable.get(attributeName);
	}


	public void deleteAttribute(String attributeName) {
		if ( attributes.containsKey(attributeName) )
			attributes.remove( attributeName );
	}

	public void deleteAttribute(int suid, String attributeName) {
		if ( attributes.containsKey(attributeName) ) {
			Map<Integer,Object> map = attributes.get(attributeName);
			if ( map.containsKey( suid ) )
				map.remove( suid );
		}
	}

	public void set(int suid, String attrName, Object value) {
		if ( value == null )
			throw new RuntimeException("value is null");

		checkType(value);
		
		if ( !types.containsKey(attrName) ) 
			types.put(attrName,value.getClass());

		if ( !attributes.containsKey(attrName) ) {
			attributes.put( attrName, new HashMap<Integer,Object>() );	
			visible.put( attrName, true );
			editable.put( attrName, true );
		}

		Map<Integer,Object> vls = attributes.get(attrName);

		if ( value.getClass().equals( types.get( attrName ) ) )
			vls.put(suid,value);
		else
			throw new RuntimeException("value is not of type: " + types.get(attrName));
	}

	public <T> T get(int suid, String attrName, Class<? extends T> type) {
		Map<Integer,Object> vls = attributes.get(attrName);

		if (vls == null) {
			return null;
		}

		Object vl = vls.get(suid);

		if (vl == null) {
			return null;
		}

		return type.cast(vl);
	}

	public <T> boolean contains(int suid, String attrName, Class<? extends T> type) {
		Map<Integer,Object> vls = attributes.get(attrName);
		
		if (vls == null) {
			return false;
		}

		Object vl = vls.get(suid);

		if (vl == null) {
			return false;
		}

		if ( vl.getClass() == type ) {
			return true;
		} else {
			return false;
		}
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
				throw new RuntimeException("empty list");
			else
				checkType(l.get(0));
		} else if ( o instanceof Map ) {
			Map m = (Map)o;
			Object[] keys = m.keySet().toArray();
			if ( keys.length <= 0 ) {
				throw new RuntimeException("empty map");
			} else {
				checkType(m.get(keys[0]));
				checkType(keys[0]);
			}
		} else
			throw new RuntimeException("invalid type: " + o.getClass().toString() );	
	}
}
