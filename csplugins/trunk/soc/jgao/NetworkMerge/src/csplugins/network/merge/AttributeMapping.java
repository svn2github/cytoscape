/* File: RandomNetworkPlugin.java

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

package csplugins.network.merge;

import java.util.Set;

/**
 * Instore the information how to mapping the attributes 
 * in the original networks to those in the resulting networks
 * 
 * @author JGao
 */
public interface AttributeMapping {
    
    /*
     * Get attributes' names in the merged network
     * 
     */
    public String[] getMergedAttributes();
        
    /*
     * Get number of the attribute in the merged network
     * 
     */
    public int getSizeMergedAttributes();
    
    /*
     * Get the ith attribute name in the merged network
     * 
     */
    public String getMergedAttribute(int index);
     
    /*
     * Set the ith attribute name in the merged network
     * 
     */
    public String setMergedAttribute(int index, String attributeName);
    
    /*
     * Check if an attribute exists in the merged attributes
     * 
     */
    public boolean containsMergedAttributes(String attributeName);
    
    /*
     * get all network titles
     * 
     */
    public Set<String> getNetworkSet();
    
    /*
     * get number of networks
     * 
     */
    public int getSizeNetwork();
    
    /*
     * Get the original attribute name in the network before merged, corresponding to the merged attribute
     * 
     */
    public String getOriginalAttribute(String netID, String mergedAttributeName);
    
    /*
     * Get the original attribute name before merged, corresponding to the ith merged attribute
     * 
     */
    public String getOriginalAttribute(String netID, int index);
    
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(String netID, String attributeName, String mergedAttributeName);
    
    /*
     * Set attribute mapping
     * 
     */
    public String setOriginalAttribute(String netID, String attributeName, int index);
    
    /*
     * Add a new attribute at ith with attributeName in the original network and add a new attribute as attrMerged in resulting network
     * 
     */
    public void addNewAttribute(String netID, String attributeName, String attrMerged);

    /*
     * Get the default attribute name for a new merged one 
     * 
     */
    public String getDefaultMergedAttrName(String attr);
    
    /*
     * 
     * 
     */
    public void addNetwork(String netID, String[] attributeNames);

    /*
     * 
     * 
     */
    public void removeNetwork(String netID);

}
