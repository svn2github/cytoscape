/*
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


package cytoscape.cythesaurus.service;

import java.util.Map;
import java.util.Set;

/**
 *
 * @author gjj
 */
public interface CyThesaurusServiceClient {

    /**
     * Check if the service if available (CyThesaurus plugin has been installed).
     * @return true if service is available; false, otherwise.
     */
    public boolean isServiceAvailable();

    /**
     * Get the version of CyThesaurus ID mapping service
     * @return service Version
     */
    public double serviceVersion();

    /**
     * Open the attribute configuration dialog.
     * @return true if configured; false, otherwise.
     */
    public boolean openAttributeConfigDialog();

    /**
     * Open the ID mapping resources configuration dialog.
     * @return true if successfully requested; false, otherwise.
     */
    public boolean openMappingResourceConfigDialog();

    /**
     *
     * @return connection strings of all registered ID mappers.
     */
    public Set<String> allIDMappers();

    /**
     *
     * @return connection strings of the selected ID mappers.
     */
    public Set<String> selectedIDMappers();

    /**
     * Register an ID mapper and select it.
     * @param connectionString connection string of the ID mapper.
     * @param classString class path of the ID mapper.
     * @param displayName displayName of the ID mapper.
     * @return true if successfully registered; false otherwise.
     */
    public boolean registerIDMapper(String connectionString, String classPath, String displayName);

    /**
     * Unregister an ID mapper.
     * @param connectionString connection string of the ID mapper.
     * @return true if successfully unregistered; false otherwise.
     */
    public boolean unregisterIDMapper(String connectionString);

    /**
     * Select/unselect the ID mapper corresponding to the connection string.
     * @param connectionString connection string of the ID mapper.
     * @param selected true if select the ID mapper; false if unselect it.
     * @return true if successfully requested; false, otherwise.
     */
    public boolean setIDMapperSelect(String connectionString, boolean selected);

    /**
     * Get the supported source ID types, which are the supported source ID
     * types of the selected ID mappers.
     * @return the supported source ID types, or null of service is not
     * available.
     */
    public Set<String> supportedSrcIDTypes();

    /**
     * Get the supported target ID types, which are the supported target ID
     * types of the selected ID mappers.
     * @return the supported target ID types, or null of service is not
     * available.
     */
    public Set<String> supportedTgtIDTypes();

    /**
     * Check if it is supported to map from srcType to tgtType.
     * @param srcType source type
     * @param tgtType target type
     * @return true if supported; false otherwise.
     */
    public boolean isMappingSupported(String srcType, String tgtType);

    /**
     * Attribute based ID mapping. IDs in the source attribute of the networks
     * will translated into the target Type and saved to the target attribute.
     * @param netIds network IDs. If null, all networks will be selected for
     *        this service.
     * @param srcAttrName name of the node attribute containing the source IDs.
     * @param tgtAttrName name of the node attribute to save the target IDs to.
     *        This can be existing attribute with List type or new attribute,
     *        which will be defined as List attribute.
     * @param srcIDTypes source ID types.
     * @param tgtIDType target ID type.
     * @return true if successfully requested; false, otherwise.
     */
    public boolean mapID(Set<String> netIds, String srcAttrName,
            String tgtAttrName, Set<String> srcIDTypes, String tgtIDType);

    /**
     * Attribute based ID mapping. IDs in the source attribute of the networks
     * will be translated into the target types and saved to the default target
     * attributes.
     * @param netIds network IDs. If null, all networks will be selected for
     *        this service.
     * @param srcAttrName name of the node attribute containing the source IDs.
     * @param srcIDTypes source ID types. if null, all supported source ID types
     *        will be used.
     * @param tgtIDTypes target ID types.
     * @return Map from target ID type to the attribute containing the target
     * IDs; null if failed.
     */
    public Map<String,String> mapID(Set<String> netIds, String srcAttrName,
            Set<String> srcIDTypes, Set<String> tgtIDTypes);

    /**
     * Map source IDs from the source ID type to the target ID type and return
     * the map from source ID to its target IDs.
     * @param srcIDs source IDs.
     * @param srcIDType source ID type.
     * @param tgtIDType target ID type.
     * @return Map from source ID to its target IDs; null if service is not
     * available.
     */
    public Map<String, Set<String>> mapID(Set<String> srcIDs, String srcIDType,
            String tgtIDType);

    /**
     * Check whether an ID exists in the ID type.
     * @param id ID.
     * @param type ID type.
     * @return true if the ID exists in the ID type; false, otherwise.
     */
    public boolean idExists(String id, String type);
}
