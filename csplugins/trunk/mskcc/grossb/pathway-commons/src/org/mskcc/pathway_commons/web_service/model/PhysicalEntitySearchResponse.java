package org.mskcc.pathway_commons.web_service.model;

import java.util.ArrayList;

/**
 * Encapsulates the Response to a Physical Entity Search.
 *
 * @author Ethan Cerami
 */
public interface PhysicalEntitySearchResponse {

    /**
     * Gets global number of hits which match the search criteria.
     * @return total number of hits which match the search criteria.
     */
    public int getGlobalNumHits();

    /**
     * Gets number of hits in the current cursor.
     * @return number of hits in the current cursor.
     */
    public int getCurrentCursorNumHits();

    /**
     * Gets the current cursor index.
     * @return cursor index.
     */
    public int getCurrentCursorIndex();

    /**
     * Gets all physical entity summary objects in the current cursor.
     * @return
     */
    public ArrayList<PhysicalEntitySummary> getPhysicalEntitySummaries();
}
