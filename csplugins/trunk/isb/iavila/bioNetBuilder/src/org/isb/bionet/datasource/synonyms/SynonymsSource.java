
package org.isb.bionet.datasource.synonyms;

import java.util.Vector;
import java.util.Hashtable;
import org.isb.bionet.datasource.*;

public interface SynonymsSource extends DataSource {
    
    /**
     * The supported IDs
     */   
    public static final String PROLINKS_ID = "PL";
    public static final String KEGG_ID = "KEGG";
    public static final String GI_ID = "GI";
    public static final String ID_NOT_FOUND = "ID not found";
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param source_ids a Vector of ids of type source_id_type
     * @param target_id_type the type of id that the input ids should be translated to
     * @return a Hashtable from the input source_ids to the resulting translation
     */
    public Hashtable getSynonyms (String source_id_type, Vector source_ids, String target_id_type);
    
    /**
     * 
     * @param id an ID
     * @return one of:<br>
     * PROLINKS_ID, KEGG_ID, GI_ID, or ID_NOT_FOUND
     */
    public String getIdType (String id);
    
}