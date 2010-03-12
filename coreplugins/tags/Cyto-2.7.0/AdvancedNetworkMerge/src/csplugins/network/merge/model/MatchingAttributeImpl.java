/* File: MatchingAttributeImpl.java

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

package csplugins.network.merge.model;

import cytoscape.data.CyAttributes;

import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

/**
 * Class to instore the information which attribute to be used 
 * for matching nodes
 * 
 * 
 */
public class MatchingAttributeImpl implements MatchingAttribute {
    private Map<String,String> attributeForMatching; // network name to attribute name
    private CyAttributes cyAttributes; // use map if local attribute realized
    
    public MatchingAttributeImpl(final CyAttributes cyAttributes) {
        this.cyAttributes = cyAttributes;
        attributeForMatching = new HashMap<String,String>();
    }

    /**
     * {@inheritDoc}
     */
    public Map<String,String> getNetAttrMap() {
        return attributeForMatching;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getAttributeForMatching(final String netID) {
        if (netID == null) {
            throw new java.lang.NullPointerException();
        }
        
        return attributeForMatching.get(netID);
    }
    
    /**
     * {@inheritDoc}
     */
    public void putAttributeForMatching(final String netID, final String attributeName) {
        if (netID==null || attributeName==null) {
            throw new java.lang.NullPointerException();
        }
        
        attributeForMatching.put(netID, attributeName);
    }

    /**
     * {@inheritDoc}
     */
    public void addNetwork(final String netID) {
        if (netID == null) {
            throw new java.lang.NullPointerException();
        }
        
        final String[] attributeNames = cyAttributes.getAttributeNames();
        final Collection<String> values = attributeForMatching.values();
        final int n = attributeNames.length;
        for (int i=0; i<n; i++) {
            if (values.contains(attributeNames[i])) {
                putAttributeForMatching(netID,attributeNames[i]);
                return;
            }
        }
        //TODO remove in Cytoscape3
        putAttributeForMatching(netID,"canonicalName");
        
        //putAttributeForMatching(netID,attributeNames[i]); //use in Cytoscape3
    }
            
    /**
     * {@inheritDoc}
     */
    public String removeNetwork(final String netID) {
        if (netID == null) {
            throw new java.lang.NullPointerException();
        }
        
        return attributeForMatching.remove(netID);
    }
    
    /**
     * {@inheritDoc}
     */
    public int getSizeNetwork() {
        return attributeForMatching.size();
    }
    
    /**
     * {@inheritDoc}
     */
    public Set<String> getNetworkSet() {
        return attributeForMatching.keySet();
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        attributeForMatching.clear();
    }
}
