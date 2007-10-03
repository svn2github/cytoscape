package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates a Pathway Summary.
 *
 * @author Ethan Cerami.
 */
public interface PathwaySummary extends NetworkSummary{

    /**
     * Gets Pathway Name.
     * @return pathway name.
     */
    public String getName();

    /**
     * Gets Pathway Description.
     * @return pathway description.
     */
    public String getDescription();

    /**
     * Get the internal ID.
     * @return internal ID.
     */
    public long getInternalId();
}
