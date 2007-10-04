package org.mskcc.pathway_commons.web_service.model;

import java.util.ArrayList;

/**
 * Encapsulates the Response to a Physical Entity Search.
 *
 * @author Ethan Cerami
 */
public class PhysicalEntitySearchResponse {
    private ArrayList<PhysicalEntitySummary> peSummaryList;

    /**
     * Gets global number of hits which match the search criteria.
     * @return total number of hits which match the search criteria.
     */
    public int getGlobalNumHits() {
        return -1;
    }

    /**
     * Gets number of hits in the current cursor.
     * @return number of hits in the current cursor.
     */
    public int getCurrentCursorNumHits() {
        return -1;
    }

    /**
     * Gets the current cursor index.
     * @return cursor index.
     */
    public int getCurrentCursorIndex() {
        return -1;
    }

    /**
     * Gets all physical entity summary objects in the current cursor.
     * @return ArrayList of PhysicalEntitySummary objects.
     */
    public ArrayList<PhysicalEntitySummary> getPhysicalEntitySummartList() {
        return peSummaryList;
    }

    /**
     * Sets all the physical entity summary objects in the current cursor.
     *
     * @param peSummaryList ArrayList of PhysicalEntitySummary objects.
     */
    public void setPhysicalEntitySummaryList (ArrayList<PhysicalEntitySummary>
            peSummaryList) {
        this.peSummaryList = peSummaryList;
    }
}
