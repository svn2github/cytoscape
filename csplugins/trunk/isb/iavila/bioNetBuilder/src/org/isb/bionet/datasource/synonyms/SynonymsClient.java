package org.isb.bionet.datasource.synonyms;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.isb.xmlrpc.client.*;

public class SynonymsClient extends AuthenticatedDataClient{

    public static final String SERVICE_NAME = "synonyms";
    
    /**
     * Constructor
     */
    public SynonymsClient (String server_url) throws XmlRpcException,
        java.net.MalformedURLException{
        super(server_url);
        this.serviceName = SERVICE_NAME;
    }   
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param source_ids a Vector of ids of type source_id_type
     * @param target_id_type the type of id that the input ids should be translated to
     * @return a Hashtable from the input source_ids to the resulting translation
     */
    public Hashtable getSynonyms (String source_id_type, Vector source_ids, String target_id_type) throws XmlRpcException, IOException{
      Object out = call(this.serviceName + ".getSynonyms", source_id_type, source_ids, target_id_type);
      return (Hashtable) out;
    }
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param target_id_type one of the supported id types
     * @return true if a translation from source_id_type to target_id_type is supported, false otherwise
     */
    public boolean translationIsSupported (String source_id_type, String target_id_type)  throws XmlRpcException, IOException{
        Boolean ret = (Boolean)call(this.serviceName + ".translationIsSupported", source_id_type, target_id_type);
        return ret.booleanValue();
    }
    
    
    /**
     * @param pattern a pattern to match against
     * @return a Vector of species that match the given pattern, each element in the Vector is a Hashmap with elements:<br>
     * TAXID->String readable as integer
     * SPECIES_NAME->String, human readable name of matching species
     */
    public Vector getSpeciesLike (String pattern) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getSpeciesLike", pattern);
        return (Vector)out;
    }
    
    /**
     * @param species_taxid the taxid of the species in which to look for genes
     * @param pattern the pattern to match against
     * @returna Hashtable that has as a key a String that represents a GI_ID, and as a value the COMMON_NAME that matched the pattern
     */
    public Hashtable getGenesLike (String species_taxid, String pattern) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getGenesLike", species_taxid, pattern);
        return (Hashtable)out;
    }
    
    /**
     * 
     * @param gis
     * @return
     */
    public Hashtable getDefinitions (Vector gis) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getDefinitions", gis);
        return (Hashtable)out;
    }
    
    /**
     * 
     * @param gis
     * @return
     * @throws XmlRpcException
     * @throws IOException
     */
    public Hashtable getXrefIds (Vector gis) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getXrefIds", gis);
        return (Hashtable)out;

    }
    
    public Hashtable getGeneNames (Vector gis) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getGeneNames", gis);
        return (Hashtable)out;

    }
    
    public Hashtable getProdNames (Vector gis) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getProdNames", gis);
        return (Hashtable)out;

    }
    
    public Hashtable getEncodedBy (Vector gis) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getEncodedBy", gis);
        return (Hashtable)out;

    }
    
    /**
     * Not implemented in MyDataClient (to be implemented by implementing
     * classes)
     */
    public void test() throws Exception{}
   
}