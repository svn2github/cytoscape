package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates an Interaction Bundle Summary.
 *
 * @author Ethan Cerami.
 */
public class InteractionBundleSummary extends NetworkSummary {
    private int numInteractions;

    /**
     * Gets total number of interactions in the bundle.
     * @return total number of interactions in the bundle.
     */
    public int getNumInteractions() {
        return numInteractions;
    }

    /**
     * Sets the number of interactions.
     * @param numInteractions number of interactions.
     */
    public void setNumInteractions (int numInteractions) {
        this.numInteractions = numInteractions;
    }
}