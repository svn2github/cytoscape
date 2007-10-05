package org.mskcc.pathway_commons.web_service.model;

/**
 * Encapsulates organism information.
 *
 * @author Ethan Cerami
 */
public class Organism {
    private String commonName;
    private String speciesName;
    private int ncbiTaxonomyId;

    /**
     * Gets the Organism's Common Name.
     * @return organism common name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets the Organism's Common Name.
     * @param commonName organism common name.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Gets the Organism's Species Name.
     * @return organism species name.
     */
    public String getSpeciesName()  {
        return speciesName;
    }

    /**
     * Sets the Organism's Species Name.
     * @param speciesName organism species name.
     */
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    /**
     * Gets the Organism's NCBI Taxonomy ID.
     * @return NCBI Taxonomy ID.
     */
    public int getNcbiTaxonomyId() {
        return ncbiTaxonomyId;
    }

    /**
     * Sets the Organism's NCBI Taxonomy ID.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     */
    public void setNcbiTaxonomyId(int ncbiTaxonomyId) {
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }
}
