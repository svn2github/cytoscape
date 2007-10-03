package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates an Interaction Bundle Summary.
 *
 * @author Ethan Cerami.
 */
public interface InteractionBundleSummary extends NetworkSummary {

    /**
     * Gets total number of interactions in the bundle.
     * @return total number of interactions in the bundle.
     */
    public int getNumInteractions();

}
