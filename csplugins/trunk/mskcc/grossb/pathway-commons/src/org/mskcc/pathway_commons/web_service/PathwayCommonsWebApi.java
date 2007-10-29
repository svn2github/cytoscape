package org.mskcc.pathway_commons.web_service;

import org.mskcc.pathway_commons.schemas.search_response.*;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.io.StringReader;

import cytoscape.task.TaskMonitor;

/**
 * Class for accessing the Pathway Commons Web API.
 *
 * @author Ethan Cerami
 */
public class PathwayCommonsWebApi {
    private ArrayList<PathwayCommonsWebApiListener> listeners =
            new ArrayList<PathwayCommonsWebApiListener>();
    private volatile CPathProtocol protocol;

    /**
     * Searches Physical Entities in Pathway Commons.
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms)
     * @param startIndex     Start index into search results; used to perform pagination.
     * @return SearchResponseType Object.
     */
    public SearchResponseType searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            int startIndex, TaskMonitor taskMonitor) throws CPathException {

        // Notify all listeners of start
        for (int i = listeners.size() - 1; i >= 0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchInitiatedForPhysicalEntities(keyword, ncbiTaxonomyId, startIndex);
        }

        protocol = new CPathProtocol();
        protocol.setCommand(CPathProtocol.COMMAND_GET_BY_KEYWORD);
        protocol.setFormat(CPathProtocol.FORMAT_XML);
        protocol.setQuery(keyword);

        SearchResponseType searchResponse;
        try {
            if (keyword.equalsIgnoreCase("dummy")) {
                searchResponse = this.createDummySearchResults();
                searchResponse.setTotalNumHits(10L);
            } else {
                String responseXml = protocol.connect(taskMonitor);
                StringReader reader = new StringReader(responseXml);

                Class[] classes = new Class[2];
                classes[0] = org.mskcc.pathway_commons.schemas.search_response.SearchResponseType.class;
                classes[1] = org.mskcc.pathway_commons.schemas.search_response.ObjectFactory.class;
                try {
                    JAXBContext jc = JAXBContext.newInstance(classes);
                    Unmarshaller u = jc.createUnmarshaller();
                    JAXBElement element = (JAXBElement) u.unmarshal(reader);
                    searchResponse = (SearchResponseType) element.getValue();
                } catch(Throwable e){
                    throw new CPathException (CPathException.ERROR_XML_PARSING, e);
                }
            }
        } catch (EmptySetException e) {
            searchResponse = new SearchResponseType();
            searchResponse.setTotalNumHits((long) 0);
        }

        //SearchResponseType searchResponse = createDummySearchResults();
        // Notify all listeners of end
        for (int i = listeners.size() - 1; i >= 0; i--) {
            PathwayCommonsWebApiListener listener = listeners.get(i);
            listener.searchCompletedForPhysicalEntities(searchResponse);
        }
        return null;
    }

    /**
     * Abort the Request.
     */
    public void abort() {
        protocol.abort();
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

            List synList = new ArrayList();
            synList.add("Synonym 1");
            synList.add("Synonym 2");
            synList.add("Synonym 3");
            synList.add("Synonym 4");
            searchHit.getSynonym().addAll(synList);

            List <XRefType> xrefList = new ArrayList();
            for (int j=0; j<3; j++) {
                XRefType xref = new XRefType();
                xref.setDb("Database_" + j);
                xref.setId("ID_" + j);
                xref.setUrl("http://www.yahoo.com");
                xrefList.add(xref);
            }
            searchHit.getXref().addAll(xrefList);

            List comments = searchHit.getComment();
            comments.add("Vestibulum pharetra laoreet ante dictum dolor sed, "
                    + "elementum egestas nunc nullam, pede mauris mattis, eros nam, elit "
                    + "aliquam lorem vestibulum duis a tortor. Adipiscing elit habitant justo, "
                    + "nonummy nunc wisi eros, dictum eget orci placerat metus vehicula eu.");

            comments.add("Vestibulum pharetra laoreet ante dictum dolor sed, "
                    + "elementum egestas nunc nullam, pede mauris mattis, eros nam, elit "
                    + "aliquam lorem vestibulum duis a tortor. Adipiscing elit habitant justo, "
                    + "nonummy nunc wisi eros, dictum eget orci placerat metus vehicula eu.");

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
     * @return ArrayList of Organism Type Objects.
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