package csplugins.network.merge;


import java.util.Set;
import java.util.Collection;

/**
 *
 * @author JGao
 */
public interface MatchingAttribute {

    /*
     * Get the attribute of network for matching node
     * 
     */
    public String getAttributeForMatching(String netID);
    
    /*
     * Set the attribute of network for matching node
     * 
     */
    public void putAttributeForMatching(String netID, String attributeName);
    
    /*
     * add/select the attribute of network for matching node
     * 
     *
    public void addNetwork(String netID, String[] attributeNames);
    */
    
    /*
     * Remove the network, return the attribute
     * 
     */
    public String removeNetwork(String netID);
    
    /*
     * 
     * 
     */
    public int size();
    
    /*
     * 
     * 
     */
    public Set<String> getNetworkSet();
    
    /*
     * 
     * 
     */
    public Collection<String> getAttributes();
            
    //TODO: ID types of the attribute could be store here
}
