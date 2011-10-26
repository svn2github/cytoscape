package org.cytoscape.cpathsquared.internal.webservice;

import java.util.ArrayList;
import java.util.List;

import org.biopax.paxtools.model.level3.BioSource;
import org.biopax.paxtools.model.level3.Entity;
import cpath.service.jaxb.*;
import org.cytoscape.work.TaskMonitor;

/**
 * Interface for accessing the cPath Web API.
 *
 * @author Ethan Cerami, Igor Rodchenkov
 */
public interface CPathWebService {

    /**
     * Searches for Physical Entities in cPath Instance.
     * Given a keyword, such as "BRCA1", this method returns the first 10 physical entities
     * which contain this keyword.  For each matching physical entity, you will receive
     * entity details, such as name, synonyms, external links, and list of all pathways in
     * which this entity participates. 
     *
     * @param keyword        Keyword to search for.
     * @param ncbiTaxonomyId Organism filter (-1 to to search all organisms).
     * @param taskMonitor    TaskMonitor Object (can be null);
     * @return SearchResponseType Object.
     * @throws CPathException   CPath Connect Error.
     * @throws EmptySetException    No matches found to specified query.
     */
    public SearchResponse searchPhysicalEntities(String keyword, int ncbiTaxonomyId,
            TaskMonitor taskMonitor) throws CPathException, EmptySetException;

    /**
     * Gets parent summaries for specified record.
     * For example, if primaryId refers to protein A, the "parent" records are all
     * interactions in which protein A participates.  If primaryId refers to interaction X,
     * the "parent" records are all parent interactions which control or modulate X.
     * To retrieve the full record (instead of just the summary), you must extract the primary
     * ID, and follow-up with a call to getRecordsByIds(). 
     *
     * @param primaryId     Primary ID of Record.
     * @param taskMonitor   Task Monitor Object.
     * @return List			of parent BioPAX Entities
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public List<Entity> getParentSummaries (String primaryId, TaskMonitor taskMonitor)
            throws CPathException, EmptySetException;

    /**
     * Gets One or more records by primary ID.
     * You can obtain primary IDs for physical entities and/or pathways via the
     * searchPhysicalEntities() method.
     * 
     * @param ids               Array of Primary IDs.
     * @param format            CPathResponseFormat.BIOPAX or CPathResponseFormat.BINARY_SIF.
     * @param taskMonitor       Task Monitor Object.
     * @return  BioPAX XML String or SIF String.
     * @throws CPathException       CPath Error.
     * @throws EmptySetException    Empty Set Error.
     */
    public String getRecordsByIds(String[] ids, CPathResponseFormat format, TaskMonitor taskMonitor)
            throws CPathException, EmptySetException;

    /**
     * Gets a list of all Organisms currently available within the cPath instance.
     *
     * @return List of Organism (BioSource) Objects.
     */
    public List<BioSource> getOrganismList();

    /**
     * Abort the Request.
     */
    public void abort();

    /**
     * Registers a new listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void addApiListener(CPathWebServiceListener listener);

    /**
     * Removes the specified listener.
     *
     * @param listener CPathWebService Listener.
     */
    public void removeApiListener(CPathWebServiceListener listener);

    /**
     * Gets the list of all registered listeners.
     *
     * @return ArrayList of CPathWebServiceListener Objects.
     */
    public ArrayList<CPathWebServiceListener> getListeners();
}