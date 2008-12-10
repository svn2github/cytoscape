
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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

package org.cytoscape.viewmodel.internal;

import org.cytoscape.viewmodel.View;
import org.cytoscape.viewmodel.VisualProperty;

import java.util.HashMap;

/**
 * The base interface that defines the methods used to set visual properties
 * for nodes, edges, and networks.
 *
 * Think of it as a row in the viewmodel table.  
 */
public class RowOrientedViewImpl<S> implements View<S>  {
    private S source;
    private HashMap vpValues;
    private HashMap bypassValues;

    private long suid;
    
    public RowOrientedViewImpl(S source){
	suid = IdFactory.getNextSUID();
	this.source = source;
	vpValues = new HashMap<VisualProperty, Object>();
	bypassValues = new HashMap<VisualProperty, Object>();
    }
	/**
	 * The VisualProperty object identifies which visual property to set and the Object
	 * determines the value.   We should probably consider doing something more type safe like
	 * what we're doing for Attributes.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 * @param o  DOCUMENT ME!
	 */
    public <T> void setVisualProperty(VisualProperty<T> vp, T o){
	vpValues.put(vp, o);
    }

	/**
	 * Getting visual properties in this way incurs lots of casting. We should probably
	 * consider doing something more type safe like what we're doing for Attributes.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public <T> T getVisualProperty(VisualProperty<T> vp){
	return (T) vpValues.get(vp);
    }

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
    public S getSource(){
	return source;
    }

        /**
         *  DOCUMENT ME!
         *
         * @return  DOCUMENT ME!
         */
        public long getSUID() {
                return suid;
        }

	/**
	 * Sets ByPass value. This value won't be override by
	 * VisualStyle or such, until it is cleared.
	 *
	 * Note: this should only be used when the user, interactively
	 * sets a bypass either through the gui or through a scripting
	 * api. All other access should be done by defining an
	 * appropriate MappingCalculator.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <T> void setByPass(VisualProperty<T> vp, T o){
	    	bypassValues.put(vp, o);
	}

	/**
	 * clears ByPass, signifying that the value for vp can be set
	 * next time a VisualStyle is applied.
	 *
	 * @param <T>  DOCUMENT ME!
	 * @param vp  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public <T> void clearByPass(VisualProperty<T> vp){
	    bypassValues.remove(vp);
	}

	/**
	 *
	 * Can also be used to query whether there is a bypass set by
	 * checking whether returned value is null.
	 *
	 * @returns the value set as bypass, or null, if bypass is not set for this vp.
	 */
	public <T> T getByPass(VisualProperty<T> vp){
	    return (T) bypassValues.get(vp);
	}
}
