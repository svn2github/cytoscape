package org.mskcc.pathway_commons.web_service.model;

import java.util.ArrayList;

/**
 * Encapsulates a Physical Entity, as retrieved from Pathway Commons.
 *
 * @author Ethan Cerami.
 */
public class PhysicalEntitySummary {
    private String name;
    private Organism organism;
    private long internalId;
    private ArrayList<String> synonymList;
    private ArrayList<String> matchingExcerptList;
    private ArrayList<InteractionBundleSummary> interactionBundleList;
    private ArrayList<PathwaySummary> pathwayList;
    private String description;

    /**
     * Gets the name of the physical entity.
     * @return physical entity name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the phsyical entity.
     * @param name Physical Entity Name.
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Gets the organism information.
     * @return Organism Object.
     */
    public Organism getOrganism() {
        return organism;
    }

    /**
     * Sets the organism information.
     * @param organism Organism Object.
     */
    public void setOrganism(Organism organism) {
        this.organism = organism;
    }

    /**
     * Gets the internal ID for this physical entity.
     * @return internal ID.
     */
    public long getInternalId() {
        return internalId;
    }

    /**
     * Sets the internal ID for this physical entity.
     * @param internalId Internal ID.
     */
    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    /**
     * Gets list of synonyms.
     * @return ArrayList of String Objects.
     */
    public ArrayList<String> getSynomyms() {
        return synonymList;
    }

    /**
     * Sets the list of synonyms.
     * @param synonymList ArrayList of String Objects.
     */
    public void setSynonyms (ArrayList <String> synonymList) {
        this.synonymList = synonymList;
    }

    /**
     * Gets functional description.
     * @return functional description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the functional description.
     * @param description functional description.
     */
    public void setDescription (String description) {
        this.description = description;
    }

    /**
     * Gets list of matching excerpts that match original client keyword search.
     * @return list of matching excerpts, in HTML with original keyword search appearing in bold.
     */
    public ArrayList<String> getMatchingExcerpts() {
        return matchingExcerptList;
    }

    /**
     * Sets the lsit of matching excerpts that match original client keyword search.
     * @param matchingExcerptList list of matching excerpts, in HTML with original keyword
     * search appearing in bold.
     */
    public void setMatchingExceprts (ArrayList <String> matchingExcerptList) {
        this.matchingExcerptList = matchingExcerptList;
    }

    /**
     * Get the list of all interaction bundle summaries.
     * @return ArrayList of Interaction Bundle Summary Objects.
     */
    public ArrayList<InteractionBundleSummary> getInteractionBundleList () {
        return interactionBundleList;
    }

    /**
     * Sets the list of all interaction bundle summaries.
     * @param interactionBundleList ArrayList of Interaction Bundle Summary Objects.
     */
    public void setInterationBundleList (ArrayList <InteractionBundleSummary>
            interactionBundleList) {
        this.interactionBundleList = interactionBundleList;
    }

    /**
     * Get the list of all pathway summaries.
     * @return ArrayList of Pathway Summary Objects.
     */
    public ArrayList <PathwaySummary> getPathwayList() {
        return pathwayList;
    }

    /**
     * Sets the list of all pathway summaries.
     * @param pathwayList ArrayList of Pathway Summary Objects.
     */
    public void setPathwayList (ArrayList <PathwaySummary> pathwayList){
        this.pathwayList = pathwayList;
    }
}