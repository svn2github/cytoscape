package org.cytoscape.coreplugin.cpath2.view.model;

/**
 * Encapsulates Organism Information.
 *
 * @author Ethan Cerami
 */
public class Organism {
    private String commonName;
    private int ncbiTaxonomyId;

    /**
     * Constructor.
     * @param commonName      Organism Commmon Name.
     * @param ncbiTaxonomyId  NCBI Taxonomy ID.
     */
    public Organism (String commonName, int ncbiTaxonomyId) {
        this.commonName = commonName;
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }

    /**
     * Gets Organism Common Name.
     * @return organism common name.
     */
    public String getCommonName() {
        return commonName;
    }

    /**
     * Sets Organism Common Name.
     * @param commonName organism common name.
     */
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    /**
     * Gets Organism NCBI Taxonomy ID.
     * @return NCBI Taxonomy ID.
     */
    public int getNcbiTaxonomyId() {
        return ncbiTaxonomyId;
    }

    /**
     * Sets Organism NCBI Taxonomy ID.
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     */
    public void setNcbiTaxonomyId(int ncbiTaxonomyId) {
        this.ncbiTaxonomyId = ncbiTaxonomyId;
    }

    /**
     * Over-rides toString() to return common name.
     * @return Organism common name.
     */
    public String toString() {
        return commonName;
    }
}
