package org.cytoscape.model.internal;

import java.util.*;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyDataTable;

public class CyDataTableImpl implements CyDataTable {

	private Map<String, Map<Long, Object>> attributes;
	private Map<String, Class<?>> types;
	private Map<String, Boolean> unique;
	private String name;
	private boolean pub;
	private final long suid;

	public long getSUID() {
		return suid;
	}
	public boolean isPublic() {
		return pub;
	}
	public String getTitle() {
		return name;
	}
	public void setTitle(String title) {
		name = title;
	}

	public CyDataTableImpl(Map<String,Class<?>> typeMap, String name, boolean pub) {
		this.name = name;
		this.pub = pub;
		this.suid = IdFactory.getNextSUID();
		attributes = new HashMap<String,Map<Long,Object>>();
		if ( typeMap == null ) {
			types = new HashMap<String,Class<?>>();
			unique = new HashMap<String,Boolean>();
		} else {
			types = new HashMap<String,Class<?>>(typeMap);
			// TODO!
			unique = new HashMap<String,Boolean>();
		}

		// setup defaults
		types.put("name",String.class);
		unique.put("name",false);

		types.put("selected",Boolean.class);
		unique.put("selected",false);
		
		for ( String key : types.keySet() )
			attributes.put( key, new HashMap<Long,Object>() );
	}

	public Map<String,Class<?>> getColumnTypeMap() {
		return new HashMap<String,Class<?>>(types);
	}


	public void deleteColumn(String attributeName) {
		if ( attributes.containsKey(attributeName) ) {
			attributes.remove( attributeName );
			types.remove( attributeName );
		}
	}

	public <T> void createColumn(String attributeName, Class<? extends T> type, boolean u) {
		if ( attributeName == null ) 
			throw new NullPointerException("attribute name is null");
		if ( type == null )
			throw new NullPointerException("type is null");
		
		Class cls = getClass(type);

		Class curr = types.get( attributeName );
		if ( curr == null ) {
			types.put( attributeName, cls );
			attributes.put( attributeName, new HashMap<Long,Object>() );
			unique.put(attributeName, u);
		} else {
			if ( !curr.equals( cls ) )
				throw new IllegalArgumentException("attribute already exists for name: " + 
				                                   attributeName + " with type: " + cls.getName());
		}	
	}

	public List<String> getUniqueColumns() {
		List<String> l = new ArrayList<String>();
		for ( String s : unique.keySet() ) {
			if ( unique.get(s) )
				l.add(s);
		}
		return l;
	}

	public <T> List<? extends T> getColumnValues(String columnName, Class<? extends T> type) {
		if ( columnName == null ) 
			throw new NullPointerException("column name is null");
		Map<Long,Object> vals = attributes.get(columnName);
		if ( vals == null )
			throw new NullPointerException("attribute does not exist");

		List<T> l =  new ArrayList<T>( vals.size() );
		for ( Object o : vals.values() )
			l.add( type.cast(o) );

		return l;
	}

	public CyRow getRow(final long suid) { 
		return new Access(suid);
	}

	public CyRow addRow() {
		return null;	
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

	private <T> T getX(long suid, String attrName, Class<? extends T> type) {
		Map<Long,Object> vls = attributes.get(attrName);

		if (vls == null) 
			return null;

		Object vl = vls.get(suid);

		if (vl == null) 
			return null;

		return type.cast(vl);
	}

	private <T> boolean containsX(long suid, String attrName, Class<? extends T> type) {
		Map<Long,Object> vls = attributes.get(attrName);
		
		if (vls == null) 
			return false;

		Object vl = vls.get(suid);

		if (vl == null) 
			return false;

		if ( types.get(attrName).isAssignableFrom(type) ) 
			return true;
		else 
			return false;
	}

	private Class<?> containsX(long suid, String attrName) {
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


	private class Access implements CyRow {
	
		private final long suid;

		Access(long suid) {
			this.suid = suid;
		}

		public void set(String attributeName, Object value) {
			setX(suid,attributeName,value);
		}

		public <T> T get(String attributeName, Class<? extends T> c) {
			return getX(suid,attributeName,c);
		}

		public <T> boolean contains(String attributeName, Class<? extends T> c) {
			return containsX(suid,attributeName,c);
		}

		public Class<?> contains(String attributeName) {
			return containsX(suid,attributeName);
		}

		public void remove(String attributeName) {
			removeX(suid,attributeName);
		}

		public Map<String,Object> getAllValues() {
			Map<String,Object> m = new HashMap<String,Object>( attributes.size() );	
			for ( String attr : attributes.keySet() ) 
				m.put( attr, attributes.get(attr).get(suid) );
			return m;
		}
	} 
}
