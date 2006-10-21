/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.psi_mi.cyto_mapper;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.coreplugin.psi_mi.data_mapper.Mapper;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.AttributeBag;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;

/**
 * Maps Cytoscape Graph Objects to Data Service Interaction objects.
 * This class performs the inverse mapping of the MapToCytoscape
 * class.
 *
 * @author Ethan Cerami
 */
public class MapFromCytoscape implements Mapper {
    private CyNetwork cyNetwork;
    private CyAttributes nodeAttributes;
    private CyAttributes edgeAttributes;

    /**
     * All new interactions.
     */
    private ArrayList interactions;

    /**
     * Constructor.
     *
     * @param cyNetwork CyNetwork Object.
     */
    public MapFromCytoscape(CyNetwork cyNetwork) {
        this.cyNetwork = cyNetwork;
        this.nodeAttributes = Cytoscape.getNodeAttributes();
        this.edgeAttributes = Cytoscape.getEdgeAttributes();
    }

    /**
     * Perform Mapping.
     */
    public void doMapping() {
        interactions = new ArrayList();
        List edgeList = cyNetwork.edgesList();
        for (int i = 0; i < edgeList.size(); i++) {
            Interaction interaction = new Interaction();
            CyEdge edge = (CyEdge) edgeList.get(i);
            if (edge != null) {
                CyNode sourceNode = (CyNode) edge.getSource();
                CyNode targetNode = (CyNode) edge.getTarget();
                Interactor sourceInteractor = new Interactor();
                Interactor targetInteractor = new Interactor();
                transferNodeAttributes(sourceNode, sourceInteractor);
                transferNodeAttributes(targetNode, targetInteractor);
                ArrayList interactors = new ArrayList();
                interactors.add(sourceInteractor);
                interactors.add(targetInteractor);
                interaction.setInteractors(interactors);
                transferEdgeAttributes(edge, interaction);
                interactions.add(interaction);
            }
        }
    }

    /**
     * Gets an ArrayList of Interaction objects.
     *
     * @return ArrayList of Interaction objects.
     */
    public ArrayList getInteractions() {
        return this.interactions;
    }

    /**
     * Transfers all Edge Attributes from Cytoscape to Data Service Objects.
     *
     * @param edge        Cytoscape Edge.
     * @param interaction Data Service Interaction Object.
     */
    private void transferEdgeAttributes(CyEdge edge, Interaction interaction) {
        String attributeNames[] = edgeAttributes.getAttributeNames();
        transferAllAttributes(attributeNames, edgeAttributes, edge.getIdentifier(),
                (AttributeBag) interaction);
    }

    /**
     * Transfers all Node Attributes from Cytoscape to Data Service objects.
     *
     * @param node       Cytoscape Node.
     * @param interactor Data Service Interactor object.
     */
    private void transferNodeAttributes(CyNode node, Interactor interactor) {
        interactor.setName(node.getIdentifier());
        String attributeNames[] = nodeAttributes.getAttributeNames();
        transferAllAttributes(attributeNames, nodeAttributes, node.getIdentifier(),
                (AttributeBag) interactor);
    }

    /**
     * Transfers all Node / Edge Attributes.
     */
    private void transferAllAttributes(String[] attributeNames,
            CyAttributes attributes, String nodeName,
            AttributeBag bag) {
        String dbNames[] = null;
        String dbIds[] = null;
        for (int i = 0; i < attributeNames.length; i++) {
            String attributeName = attributeNames[i];
            /**
            if (attributeName.equals(CommonVocab.XREF_DB_NAME)) {
                dbNames = (String[]) attributes.get
                        (attributeName, nodeName);
            } else if (attributeName.equals(CommonVocab.XREF_DB_ID)) {
                dbIds = (String[]) attributes.get
                        (attributeName, nodeName);
            } else {
            **/
                String value = attributes.getStringAttribute(nodeName, attributeName);
                bag.addAttribute(attributeName, value);
            //}
        }
        addExternalReferences(dbNames, dbIds, (AttributeBag) bag);
    }

    /**
     * Adds External References.
     */
    private void addExternalReferences(String dbNames[], String dbIds[],
            AttributeBag bag) {
        if (dbNames != null & dbIds != null) {
            ExternalReference refs[] = new ExternalReference[dbNames.length];
            for (int i = 0; i < dbNames.length; i++) {
                String dbName = (String) dbNames[i];
                String dbId = (String) dbIds[i];
                ExternalReference ref = new ExternalReference(dbName, dbId);
                refs[i] = ref;
            }
            bag.setExternalRefs(refs);
        }
    }
}