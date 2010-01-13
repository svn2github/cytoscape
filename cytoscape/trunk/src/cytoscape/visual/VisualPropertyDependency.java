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

import java.util.Properties;

public interface VisualPropertyDependency {

	/**
	 * An enum that lists the possible dependencies.
	 */
	public enum Definition {
		NODE_SIZE_LOCKED("nodeSizeLocked",true),
		ARROW_COLOR_MATCHES_EDGE("arrowColorMatchesEdge",false),
		;

		private String propKey;
		private boolean defaultValue;

		private Definition(String propKey, boolean defaultValue) {
			this.propKey = propKey;
			this.defaultValue = defaultValue;
		}

		/**
		 * Will return the key used to identify this Definition for use in Properties objects.
		 */
		public String getDefaultPropertyKey() {
			return propKey;
		}

		/**
		 * Will return the default state for this Definition.
		 */
		public boolean getDefault() {
			return defaultValue;
		}
	}

	/**
	 * Checks the state of the specified dependency definition. If a value has not
	 * been explicitly set for the specified definition then the default value for
	 * that definition will be returned.
	 */
	boolean check(final Definition s);

	/**
	 * Sets the state of the specified definition to the specified value. 
	 */
	void set(final Definition s, final boolean b);

	/**
	 * Clears the current object and copies the state of the specified VisualPropertyDependency
	 * into this object.  Once complete this object should have the identical settings to
	 * the specified object.
	 */
	void copy(VisualPropertyDependency v);

	/** 
	 * Will search the specified Properties using the specified basedKey for
	 * each dependency definition and will update this object according
	 * to any properties found.
	 */
	void applyDefaultProperties(final Properties props, final String baseKey);

	/** 
	 * Will return a new Properties object with properties set based on the
	 * specified baseKey and any dependency definitions set in this object.
	 */
	Properties getDefaultProperties(final String baseKey);
}
