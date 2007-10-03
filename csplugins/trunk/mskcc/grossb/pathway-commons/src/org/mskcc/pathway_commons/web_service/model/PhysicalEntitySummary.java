package org.mskcc.pathway_commons.web_service.model;

import java.util.ArrayList;

/**
 * Encapsulates a Physical Entity, as retrieved from Pathway Commons.
 *
 * @author Ethan Cerami.
 */
public interface PhysicalEntitySummary {

    /**
     * Gets the name of the physical entity.
     * @return physical entity name.
     */
    public String getName();

    /**
     * Gets the organism information.
     * @return Organism Object.
     */
    public Organism getOrganism();

    /**
     * Gets the internal ID for this physical entity.
     * @return internal ID.
     */
    public long getInternalId();

    /**
     * Gets list of synonyms.
     * @return ArrayList of String Object.
     */
    public ArrayList<String> getSynomyms();

    /**
     * Gets functional description.
     * @return functional description.
     */
    public String getDescription();

    /**
     * Gets list of matching excerpts that match original client keyword search.
     * @return list of matching excerpts, in HTML with original keyword search appearing in bold.
     */
    public ArrayList<String> getMatchingExcerpts();

    /**
     * Gets total number of interaction bundles.  One bundle per data source.
     * @return number of interaction bundle summmaries.
     */
    public int getNumInteractionBundleSummaries();

    /**
     * Get the interaction bundle summary at the specified index.
     * @param index index value.
     * @return Interaction Bundle Summary.
     */
    public InteractionBundleSummary getInteractionBundleSummary (int index);

    /**
     * Gets number of pathways connected to this physical entity.
     * @return number of pathways.
     */
    public int getNumPathways();

    /**
     * Get the pathway summary at the specified index.
     * @param index index value.
     * @return Pathway Summary.
     */
    public PathwaySummary getPathwaySummary (int index);

}
