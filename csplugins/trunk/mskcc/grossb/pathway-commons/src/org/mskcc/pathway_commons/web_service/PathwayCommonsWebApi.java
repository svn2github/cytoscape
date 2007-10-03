package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;
import org.mskcc.pathway_commons.web_service.model.Organism;
import org.mskcc.pathway_commons.web_service.model.InteractionBundleSummary;

import java.util.ArrayList;

/**
 * Interface for accessing all data in Pathway Commmons.
 *
 * @author Ethan Cerami
 */
public interface PathwayCommonsWebApi {

    /**
     * Searches Physical Entities in Pathway Commons.
     * @param keyword           Keyword to search for.
     * @param ncbiTaxonomyId    Organism filter (-1 to to search all organisms)
     * @param startIndex        Start index into search results; used to perform pagination.
     * @return
     */
    public PhysicalEntitySearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex);

    /**
     * Gets a list of all Organisms currently available within Pathway Commons.
     * @return
     */
    public ArrayList<Organism> getOrganismList();

    /**
     * Gets the interaction bundle for a physical entity, from one data source.
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @param dataSourceId (-1 to get data from all sources).
     * @param view (1=simple view, 2=complex view).
     * @return CyNetwork.
     */
    public void getInteractionBundle (long internalPhysicalEntityId, int dataSourceId,
            int view);

    /**
     * Gets the interaction bundle summaries for a physical entity
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @return ArrayList of Interaction Bundle Summary Objects.
     */
    public ArrayList<InteractionBundleSummary> getInteractionBundles (long internalPhysicalEntityId);

    /**
     * Gets the specified pathway.
     * @param internalPathwayId Internal Pathway ID.
     * @param view (1=simple view, 2=complex view).
     * @return
     */
    public void getPathway (long internalPathwayId, int view);
}
