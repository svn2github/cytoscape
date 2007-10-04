package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates a Pathway Summary.
 *
 * @author Ethan Cerami.
 */
public class PathwaySummary extends NetworkSummary{
    private String name;
    private String description;
    private long internalId;

    /**
     * Gets the Pathway Name.
     * @return pathway name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the Pathway Name.
     * @param name pathway name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets Pathway Description.
     * @return pathway description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets Pathway Description.
     * @param description pathway description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the internal ID.
     * @return internal ID.
     */
    public long getInternalId() {
         return this.internalId;
    }

    /**
     * Sets the internal ID.
     * @param internalId Internal ID.
     */
    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }
}