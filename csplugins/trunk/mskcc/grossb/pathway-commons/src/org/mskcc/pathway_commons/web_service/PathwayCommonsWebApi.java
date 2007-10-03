package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.web_service.model.PhysicalEntitySearchResponse;
import org.mskcc.pathway_commons.web_service.model.Organism;
import org.mskcc.pathway_commons.web_service.model.InteractionBundleSummary;

import java.util.ArrayList;

/**
 * Class for accessing the Pathway Commons Web API.
 *
 * @author Ethan Cerami
 */
public class PathwayCommonsWebApi {
    private ArrayList <PathwayCommonsWebApiListener> listeners =
            new ArrayList <PathwayCommonsWebApiListener>();

    /**
     * Searches Physical Entities in Pathway Commons.
     * @param keyword           Keyword to search for.
     * @param ncbiTaxonomyId    Organism filter (-1 to to search all organisms)
     * @param startIndex        Start index into search results; used to perform pagination.
     * @return
     */
    public PhysicalEntitySearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {

        // Notify all listeners of start
        for (int i=listeners.size() -1; i>=0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId, startIndex);
        }

        // Notify all listeners of end
        for (int i=listeners.size() -1; i>=0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId, startIndex);
        }
        return null;
    }

    /**
     * Gets a list of all Organisms currently available within Pathway Commons.
     * @return
     */
    public ArrayList<Organism> getOrganismList() {
        return null;
    }

    /**
     * Gets the interaction bundle for a physical entity, from one data source.
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @param dataSourceId (-1 to get data from all sources).
     * @param view (1=simple view, 2=complex view).
     * @return CyNetwork.
     */
    public void getInteractionBundle (long internalPhysicalEntityId, int dataSourceId,
            int view) {
    }

    /**
     * Gets the interaction bundle summaries for a physical entity
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @return ArrayList of Interaction Bundle Summary Objects.
     */
    public ArrayList<InteractionBundleSummary> getInteractionBundles
            (long internalPhysicalEntityId) {
        return null;
    }

    /**
     * Gets the specified pathway.
     * @param internalPathwayId Internal Pathway ID.
     * @param view (1=simple view, 2=complex view).
     * @return
     */
    public void getPathway (long internalPathwayId, int view) {

    }

    /**
     * Registers a new listener.
     * @param listener PathwayCommonsWebApi Listener.
     */
    public void addApiListener (PathwayCommonsWebApiListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     * @param listener PathwayCommonsWebApi Listener.
     */
    public void removeApiListener (PathwayCommonsWebApiListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the list of all registered listeners.
     * @return ArrayList of PathwayCommonsWebApiListener Objects.
     */
    public ArrayList <PathwayCommonsWebApiListener> getListeners() {
        return listeners;
    }
}
