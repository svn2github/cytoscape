package org.cytoscape.model.internal;

import java.util.*;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;

public class CyAttributesManagerImpl implements CyAttributesManager {

	private Map<String, Map<Long, Object>> attributes;
	private Map<String, Class<?>> types;

	public CyAttributesManagerImpl(Map<String,Class<?>> typeMap) {
		attributes = new HashMap<String,Map<Long,Object>>();
		if ( typeMap == null )
			types = new HashMap<String,Class<?>>();
		else {
			types = new HashMap<String,Class<?>>(typeMap);
			for ( String key : typeMap.keySet() )
				attributes.put( key, new HashMap<Long,Object>() );
		}

		attributes.put("selected",new HashMap<Long,Object>());
		types.put("selected",Boolean.class);
	}

	public Map<String,Class<?>> getTypeMap() {
		return new HashMap<String,Class<?>>(types);
	}


	public void deleteAttribute(String attributeName) {
		if ( attributes.containsKey(attributeName) ) {
			attributes.remove( attributeName );
			types.remove( attributeName );
		}
	}

	public <T> void createAttribute(String attributeName, Class<? extends T> type) {
		if ( attributeName == null ) 
			throw new NullPointerException("attribute name is null");
		if ( type == null )
			throw new NullPointerException("type is null");
		
		Class cls = getClass(type);

		Class curr = types.get( attributeName );
		if ( curr == null ) {
			types.put( attributeName, cls );
			attributes.put( attributeName, new HashMap<Long,Object>() );
		}
		else {
			if ( !curr.equals( cls ) )
				throw new IllegalArgumentException("attribute already exists for name: " + 
				                                   attributeName + " with type: " + cls.getName());
		}	
	}

	// internal methods

	private void removeX(long suid, String attributeName) {
		if ( attributes.containsKey(attributeName) ) {
			Map<Long,Object> map = attributes.get(attributeName);
			if ( map.containsKey( suid ) )
				map.remove( suid );
		}
	}

	private void setX(long suid, String attrName, Object value) {
		if ( value == null )
			throw new NullPointerException("value is null");

		if ( !types.containsKey(attrName) || !attributes.containsKey(attrName) ) 
			throw new IllegalArgumentException("attribute does not yet exist!");

		checkType(value);

		Map<Long,Object> vls = attributes.get(attrName);

		if ( types.get( attrName ).isAssignableFrom( value.getClass() ) )
			vls.put(suid,value);
		else
			throw new IllegalArgumentException("value is not of type: " + types.get(attrName));
	}

	private Object getXRaw(long suid, String attrName) {
		Map<Long,Object> vls = attributes.get(attrName);

		if (vls == null) 
			return null;

		return vls.get(suid);
	}

	private <T> T getX(long suid, String attrName, Class<? extends T> type) {
		Map<Long,Object> vls = attributes.get(attrName);

		if (vls == null) 
			return null;

		Object vl = vls.get(suid);

		if (vl == null) 
			return null;

		return type.cast(vl);
	}

	private Class<?> containsX(long suid, String attrName) {
		if ( !types.containsKey(attrName) )
			return null;
		
		Map<Long,Object> vls = attributes.get(attrName);

		if (vls == null) 
			return null;

		Object vl = vls.get(suid);

		if (vl == null) 
			return null;
		else
			return types.get(attrName);
	}

	private Class<?> getClass(Class<?> c) {
		if ( Integer.class.isAssignableFrom( c ) )
			return Integer.class;
		else if ( Double.class.isAssignableFrom( c ) )
			return Double.class;
		else if ( Boolean.class.isAssignableFrom( c ) )
			return Boolean.class;
		else if ( String.class.isAssignableFrom( c ) )
			return String.class;
		else if ( List.class.isAssignableFrom( c ) )
			return List.class;
		else if ( Map.class.isAssignableFrom( c ) )
			return Map.class;
		else
			throw new IllegalArgumentException("invalid class: " + c.getName());
	}
		

	private void checkType(Object o) {
		if ( o instanceof String )
			return;
		else if ( o instanceof Integer )
			return;
		else if ( o instanceof Boolean )
			return;
		else if ( o instanceof Double )
			return;
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

	public CyAttributes getCyAttributes(final long suid) { 
		return new Access(this,suid);
	}

	public <T> List<T> getAll(String attrName, Class<? extends T> type) {
		Map<Long,Object> vls = attributes.get(attrName);

		// TODO probably shouldn't return null here
		// Either return empty list or throw an exception
		if (vls == null) 
			return null;
		
		List<T> ret = new ArrayList<T>( vls.size() );
		for ( Object o : vls.values() ) 
			ret.add( type.cast(o) );

		return ret; 
	}

	private class Access implements CyAttributes {
	
		private final long suid;
		private final CyAttributesManager mgr;

		Access(CyAttributesManager mgr, long suid) {
			this.mgr = mgr;
			this.suid = suid;
		}

		public void set(String attributeName, Object value) {
			setX(suid,attributeName,value);
		}

		public <T> T get(String attributeName, Class<? extends T> c) {
			return getX(suid,attributeName,c);
		}

		public Class<?> contains(String attributeName) {
			return containsX(suid,attributeName);
		}

		public void remove(String attributeName) {
			removeX(suid,attributeName);
		}

		public Object getRaw(String attributeName) {
			return getXRaw(suid,attributeName);
		}
		
		public CyAttributesManager getAttrMgr() {
			return mgr;
		}
	} 
}
