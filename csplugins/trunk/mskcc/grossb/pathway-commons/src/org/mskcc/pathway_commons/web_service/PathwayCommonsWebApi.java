package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.schemas.search_response.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for accessing the Pathway Commons Web API.
 *
 * @author Ethan Cerami
 */
public class PathwayCommonsWebApi {
    private ArrayList<PathwayCommonsWebApiListener> listeners =
            new ArrayList<PathwayCommonsWebApiListener>();

    /**
     * Searches Physical Entities in Pathway Commons.
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms)
     * @param startIndex     Start index into search results; used to perform pagination.
     * @return
     */
    public SearchResponseType searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex) {

        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId, startIndex);
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }

        //  Some dummy code for now.
        SearchResponseType searchResponse = createDummySearchResults();

        // Notify all listeners of end
        for (int i = listeners.size() - 1; i >= 0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchCompletedForPhysicalEntities(searchResponse);
        }
        return null;
    }

    private SearchResponseType createDummySearchResults() {
        SearchResponseType searchResponse = new SearchResponseType();
        List<SearchHitType> searchHits = searchResponse.getSearchHit();
        for (int i = 0; i < 10; i++) {
            SearchHitType searchHit = new SearchHitType();
            searchHit.setName("Protein " + i);

            OrganismType organism = new OrganismType();
            organism.setCommonName("Human");
            organism.setSpeciesName("Homo Sapiens");
            searchHit.setOrganism(organism);

            List comments = searchHit.getComment();
            comments.add("Vestibulum pharetra laoreet ante dictum dolor sed, "
                    + "elementum egestas nunc nullam, pede mauris mattis, eros nam, elit "
                    + "aliquam lorem vestibulum duis a tortor. Adipiscing elit habitant justo, "
                    + "nonummy nunc wisi eros, dictum eget orci placerat metus vehicula eu.");

            ObjectFactory factory = new ObjectFactory();
            PathwayListType pathwayListType = factory.createPathwayListType();
            List <PathwayType> pathwayList = pathwayListType.getPathway();
            searchHit.setPathwayList(pathwayListType);
            for (int j = 0; j < 10; j++) {
                PathwayType pathwaySummary = new PathwayType();
                pathwaySummary.setName("Pathway " + j + "[" + i + "]");
                pathwaySummary.setPrimaryId((long) j);
                DataSourceType dataSource = new DataSourceType();
                dataSource.setName("Data Source " + j);
                pathwaySummary.setDataSource(dataSource);
                pathwayList.add(pathwaySummary);
            }

            InteractionBundleListType interactionBundleListType =
                    factory.createInteractionBundleListType();
            List<InteractionBundleType> interactionBundleList =
                    interactionBundleListType.getInteractionBundle();
            searchHit.setInteractionBundleList(interactionBundleListType);
            
            for (int j = 0; j < 10; j++) {
                InteractionBundleType interactionBundle = new InteractionBundleType();
                DataSourceType dataSource = new DataSourceType();
                dataSource.setName("Data Source " + j);
                interactionBundle.setDataSource(dataSource);
                interactionBundle.setNumInteractions(BigInteger.valueOf(i * j));
                interactionBundleList.add(interactionBundle);
            }
            searchHits.add(searchHit);
        }
        return searchResponse;
    }

    /**
     * Gets a list of all Organisms currently available within Pathway Commons.
     *
     * @return
     */
    public ArrayList<OrganismType> getOrganismList() {
        return null;
    }

    /**
     * Gets the interaction bundle for a physical entity, from one data source.
     *
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @param dataSourceId             (-1 to get data from all sources).
     * @param view                     (1=simple view, 2=complex view).
     * @return CyNetwork.
     */
    public void getInteractionBundle(long internalPhysicalEntityId, int dataSourceId,
            int view) {
    }

    /**
     * Gets the interaction bundle summaries for a physical entity
     *
     * @param internalPhysicalEntityId Internal ID for physical entity of interest.
     * @return ArrayList of Interaction Bundle Summary Objects.
     */
    public ArrayList<InteractionBundleType> getInteractionBundles
            (long internalPhysicalEntityId) {
        return null;
    }

    /**
     * Gets the specified pathway.
     *
     * @param internalPathwayId Internal Pathway ID.
     * @param view              (1=simple view, 2=complex view).
     * @return
     */
    public void getPathway(long internalPathwayId, int view) {

    }

    /**
     * Registers a new listener.
     *
     * @param listener PathwayCommonsWebApi Listener.
     */
    public void addApiListener(PathwayCommonsWebApiListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     *
     * @param listener PathwayCommonsWebApi Listener.
     */
    public void removeApiListener(PathwayCommonsWebApiListener listener) {
        listeners.remove(listener);
    }

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of PathwayCommonsWebApiListener Objects.
     */
    public ArrayList<PathwayCommonsWebApiListener> getListeners() {
        return listeners;
    }
}
