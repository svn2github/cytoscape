/*
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

package cytoscape.visual;

import java.util.Map;
import java.util.EnumMap;
import java.util.Properties;

class VisualPropertyDependencyImpl implements VisualPropertyDependency {

	private final Map<Definition,Boolean> state;

	VisualPropertyDependencyImpl() {
		state = new EnumMap<Definition,Boolean>(VisualPropertyDependency.Definition.class);
	}

	public boolean check(final Definition s) {
		if ( state.containsKey(s) )
			return state.get(s);
		else
			return s.getDefault();
	}

	public void set(final Definition s, final boolean b) {
		if ( s != null )
			state.put(s,b);
	}

	public Boolean get(final Definition s) {
		return state.get(s);
	}

	public void applyDefaultProperties(final Properties props, final String baseKey) {
		for ( Definition d : Definition.values() ) {
			final String lockKey = baseKey + "." + d.getDefaultPropertyKey();
			final String lockVal = props.getProperty(lockKey);
			// Don't set any value if we don't explicitly match something (rely on default).
	        if (lockVal != null) {
				if ( lockVal.equalsIgnoreCase("true") ) 
					set(d,true);
				else if ( lockVal.equalsIgnoreCase("false") ) 
					set(d,false);
			}
		}
	}

	public Properties getDefaultProperties(final String baseKey) {
		Properties props = new Properties();
		// only set properties that have been explicitly set
		for ( Definition d : state.keySet() ) {
			final String lockKey = baseKey + "." + d.getDefaultPropertyKey();
			props.setProperty(lockKey, Boolean.valueOf(check(d)).toString());
		}
		return props;	
	}

	/**
	 * First clear this VisualPropertyDependency and the copy the provided 
	 * VisualPropertyDependency settings into this one.
	 */
	public void copy(VisualPropertyDependency d) {
		if ( this == d )
			return;
		state.clear();
		for ( Definition def : Definition.values() ) {
			Boolean b = d.get(def);
			if ( b != null ) 
				state.put( def, b );
		}
	}


	@Override
	public String toString() {
		String ret = "dependency state (" + hashCode() + "): ";
		for ( Definition d : state.keySet() )
			ret += "  [" + d + " " + state.get(d) + "]";

		return ret;
	}
}
