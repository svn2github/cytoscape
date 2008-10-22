
package org.cytoscape.tunable;

import org.cytoscape.model.CyDataTable;
import org.cytoscape.tunable.impl.ModulePropertiesImpl;
import org.cytoscape.tunable.impl.TunableImpl;


public class TunableFactory {

	public static Tunable getTunable(String name, String desc, int type, Object value) {
		return new TunableImpl( name, desc, type, value, null, null, 0, false, null);
	}

	public static Tunable getTunable(String name, String desc, int type, Object value, int flag) {
		return new TunableImpl(name, desc, type, value, null, null, flag, false, null);
	}

	public static Tunable getTunable( String name, String desc, int type, Object value, 
	                                  Object lowerBound, Object upperBound, int flag) {
		return new TunableImpl(name, desc, type, value, lowerBound, upperBound, flag, false, null);

	}

	public static Tunable getTunable(String name, String desc, int type, Object value, 
	                                 Object lowerBound, Object upperBound, int flag, 
									 boolean immutable) {
		return new TunableImpl(name, desc, type, value, lowerBound, upperBound, flag, immutable, null);
	}

	public static Tunable getTunable(String name, String desc, int type, Object value, 
	                                 Object lowerBound, Object upperBound, int flag, 
									 boolean immutable, CyDataTable attr) {
		return new TunableImpl(name, desc, type, value, lowerBound, upperBound, flag, immutable, attr);
	}

	public static ModuleProperties getModuleProperties(String propertyPrefix, String moduleType) {
		return new ModulePropertiesImpl(propertyPrefix, moduleType);
	}
}
