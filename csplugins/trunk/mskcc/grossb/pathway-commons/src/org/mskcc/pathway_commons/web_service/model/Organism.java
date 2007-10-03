package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates organism information.
 *
 * @author Ethan Cerami
 */
public interface Organism {

    /**
     * Gets the Organism's Common Name.
     * @return organism common name.
     */
    public String getCommonName();

    /**
     * Gets the Organism's Species Name.
     * @return organism species name.
     */
    public String getSpeciesName();

    /**
     * Gets the Organism's NCBI Taxonomy ID.
     * @return NCBI Taxonomy ID.
     */
    public int getNcbiTaxonomyId();
}
