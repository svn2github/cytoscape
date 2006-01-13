
package org.isb.bionet.datasource.synonyms;

import java.io.IOException;
import java.util.Vector;
import java.util.Hashtable;

import org.apache.xmlrpc.XmlRpcException;
import org.isb.bionet.datasource.*;
//TODO:
//Add method translationIsSupported(sourceIDtype,targetIDtype);
public interface SynonymsSource extends DataSource {
    
    /**
     * The supported IDs
     */   
    public static final String PROLINKS_ID = "PL";
    public static final String KEGG_ID = "KEGG";
    /**
     * This is the protein GI, not the nucleotide GI
     */
    public static final String GI_ID = "GI";
    public static final String ID_NOT_FOUND = "ID not found";
    public static final String GENE_NAME = "Gene Name";
    public static final String GENE_ID = "GeneID"; // Entrez Gene Database, replaces NCBI Locus Link
    public static final String PROD_NAME = "Product Name";
    public static final String ORF_NAME = "ORF Name";
    public static final String TREMBL_ID = "TrEMBL";
    public static final String REFSEQ_ID = "RefSeq";
    public static final String HPRD_ID = "HPRD";
    public static final String HGNC_ID = "HGNC";
    public static final String TAXID = "TAXID";
    public static final String SPECIES_NAME = "Species Name";
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param source_ids a Vector of ids of type source_id_type
     * @param target_id_type the type of id that the input ids should be translated to
     * @return a Hashtable from the input source_ids to the resulting translation
     */
    public Hashtable getSynonyms (String source_id_type, Vector source_ids, String target_id_type);
    
    /**
     * @param pattern a pattern to match against
     * @return a Vector of species that match the given pattern, each element in the Vector is a Hashmap with elements:<br>
     * TAXID->String readable as integer
     * SPECIES_NAME->String, human readable name of matching species
     */
    public Vector getSpeciesLike (String pattern);
    
    /**
     * @param species_taxid the taxid of the species in which to look for genes
     * @param pattern the pattern to match against
     * @return a Hashtable that has as a key a String that represents a GI_ID, and as a value the COMMON_NAME that matched the pattern
     */
    public Hashtable getGenesLike (String species_taxid, String pattern);
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param target_id_type one of the supported id types
     * @return true if a translation from source_id_type to target_id_type is supported, false otherwise
     */
    public Boolean translationIsSupported (String source_id_type, String target_id_type);
    
    /**
     * 
     * @param gis
     * @return
     */
    public Hashtable getDefinitions (Vector gis);
    
    public Hashtable getXrefIds (Vector gis);
    
    public Hashtable getGeneNames (Vector gis);
    
    public Hashtable getProdNames (Vector gis);
    
    public Hashtable getEncodedBy (Vector gis);
}