/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package csplugins.id.mapping;

import cytoscape.CyNetwork;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

import java.util.Set;
import java.util.Map;

/**
 *
 * @author gjj
 */
public interface AttibuteBasedIDMappingService {

    /**
     * For each node in each network, given its attribute and the corresponding
     * source id types, create new attributes of the destination type.
     *
     * @param networks
     * @param mapSrcAttrIDTypes
     *      key: source attribute
     *      value: corresponding ID types
     * @param MapTgtIDTypeAttrName
     *      key: target ID type
     *      value: attribute name
     */
    public void map(Set<CyNetwork> networks, Map<String,Set<DataSource>> mapSrcAttrIDTypes,
            Map<DataSource, String> MapTgtIDTypeAttrName) throws IDMapperException;
    
}
