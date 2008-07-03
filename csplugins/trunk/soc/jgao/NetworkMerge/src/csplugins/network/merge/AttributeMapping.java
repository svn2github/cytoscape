/* File: AttributeMapping.java

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

import cytoscape.data.CyAttributes;

import java.util.Set;
import java.util.Map;

/**
 * Instore the information how to mapping the attributes 
 * in the original networks to those in the resulting networks
 * 
 * 
 */
public interface AttributeMapping {
    
    public CyAttributes getCyAttributes();
    
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
     * @return the original attribute if exist, null otherwise
     */
    public String getOriginalAttribute(String netID, String mergedAttributeName);
    
    /*
     * Get the original attribute name before merged, corresponding to the ith merged attribute
     * 
     * @return the original attribute if exist, null otherwise
     */
    public String getOriginalAttribute(String netID, int index);
    
    /*
     * Get the original attribute name in the network before merged, corresponding to the merged attribute
     * 
     * @return the original attribute if exist, null otherwise
     */
    public Map<String,String> getOriginalAttributeMap(String mergedAttributeName);
    
    /*
     * Get the original attribute name before merged, corresponding to the ith merged attribute
     * 
     * @return the original attribute if exist, null otherwise
     */
    public Map<String,String> getOriginalAttributeMap(int index);   
    
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
     * remove original attribute 
     * 
     */
    public String removeOriginalAttribute(String netID, String mergedAttributeName);
    
    /*
     * remove original attribute 
     * 
     */
    public String removeOriginalAttribute(String netID, int index);
    
    /*
     * remove merged attribute, along with the corresponding origianl attribute
     * 
     */
    public String removeMergedAttribute(String mergedAttributeName);
    
    /*
     * remove merged attribute, along with the corresponding origianl attribute
     * 
     */
    public String removeMergedAttribute(int index);
    
    /*
     * Add new attribute in the end for the current network
     * 
     */
    public void addAttributes(Map<String,String> mapNetIDAttributeName, String mergedAttrName);

    /*
     * Get the default attribute name for a new merged one 
     * 
     
    public String getDefaultMergedAttrName(String attr);
    
    /*
     * 
     * 
     */
    public void addNetwork(String netID);

    /*
     * 
     * 
     */
    public void removeNetwork(String netID);
        
    /*
     * Check whether original attribute in each network has the same type for the merged attribute
     * 
     */    
    public boolean isAttributeTypeSame(String mergedAttributeName);
    
    /*
     * Check whether ith original attribute in each network has the same type
     * 
     */    
    public boolean isAttributeTypeSame(int index);
    
    /*
     * Check whether two attributes have the same types
     * 
     */    
    public boolean isAttributeTypeSame(String attr1, String attr2);
    
    /*
     * Return the type of merged attribute
     * 
     */
    public byte getMergedAttributeType(String mergedAttributeName);
    
    /*
     * Return the type of merged attribute
     * 
     */
    public byte getMergedAttributeType(int index);
}
