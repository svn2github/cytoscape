
package org.isb.iavila.ontology.xmlrpc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.io.IOException;
import org.apache.xmlrpc.XmlRpcException;
import org.isb.xmlrpc.client.AuthenticatedDataClient;


public class GOClient extends AuthenticatedDataClient{
    
    public static final String SERVICE_NAME = "geneOntology";
    
    /**
     * Constructor
     */
    public GOClient (String server_url) throws XmlRpcException,
        java.net.MalformedURLException{
        super(server_url);
        this.serviceName = SERVICE_NAME;
    }   

    /**
     * @return a Hastable of terms (Hashtables) to their children (Vectors of Hashtables)
     * @see getTermInfo
     */
    public Hashtable getTermsChildren () throws XmlRpcException, IOException {
        Object out = call(this.serviceName + ".getTermsChildren");
        return (Hashtable)out;
    }
   
    
    /**
     * 
     * @param termID
     * @return Vector of Hashtables
     * @see getTermsInfo
     */
    public Vector getChildren (Integer termID) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getChildren", termID);
        return (Vector)out;
        
    }
    
    
    /**
     * 
     * @param termID
     * @return Vector of Integers
     */
    public Vector getChildrenIDs (Integer termID) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getChildrenIDs", termID);
        return (Vector)out;
    }
    
    /**
     * 
     * @param termIDs Vector of Strings parsable as integers
     * @return a Hashtable from a termIDs (Strings) to Hashtables with the following entries:<br>
     * TERM_NAME -> String <br>
     * TERM_TYPE -> String <br>
     * TERM_ACC -> String<br>
     * IS_OBSOLETE -> Boolean <br>
     * IS_ROOT -> Boolean <br>
     */
    public Hashtable getTermsInfo (Vector termIDs) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getTermsInfo", termIDs);
        return (Hashtable)out;
    }
    
    /**
     * @param termIDs Vector of Integers
     * @return a Vector Strings correspinding to the term names given in the input vector
     */
    public Vector getTermsNames (Vector termIDs) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getTermsNames", termIDs);
        return (Vector)out;
    }
    
    
    /**
     * @return a Hashtable from term ids to their names (Integer to String)
     */
    public Hashtable getTermsToNames () throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getTermsToNames");
        return (Hashtable)out;
    }
    
    /**
     * @return the ID of the root of this ontology (in GO, this is called "all" and the id is usually 1)
     */
    public Integer getRootTermID () throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getRootTermID");
        return (Integer)out;
    }
    
    /**
     * @return a Vector of Strings that are parsable as Integers
     */
    public Vector getAllTermIDs() throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getAllTermIDs");
        return (Vector)out;
    }
    
    /**
     * @param termIDs a Vector of Integers representing term ids
     * @param speciesID the species for which to return genes
     * @return a Hashtable from Strings (termIDs parsable as Integers) to Vectors of Strings representing genes
     * with the given key term
     */
    public Hashtable getGenesWithTerms (Vector termIDs, String speciesID) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getGenesWithTerms", termIDs, speciesID);
        return (Hashtable)out; 
    }
    
    /**
     * 
     * @return a Vector of Hashtables which contain the following information:<br>
     * SPECIES_ID --> String parsable as Integer<br>
     * GENUS --> String<br>
     * SPECIES --> String<br>
     * SP_COMMON_NAME --> String<br>
     */
    public Vector getSpeciesInfo () throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getSpeciesInfo");
        return (Vector)out; 
    }
    
    
    /**
     * Finds all species whose 'common_name', 'genus', and/or 'species' fields look like the input string
     * 
     * @param likePattern  a String that may be a species.common_name, species.genus, or a species.species
     * @return Vector of Hashtables which contain the following species information:<br>
     * SPECIES_ID --> String parsable as Integer<br>
     * GENUS --> String<br>
     * SPECIES --> String<br> 
     * SP_COMMON_NAME --> String<br>
     */
    public Vector getSpeciesLike (String likePattern) throws XmlRpcException, IOException{
        Object out = call(this.serviceName + ".getSpeciesLike", likePattern);
        return (Vector)out; 
    }
    
    /**
     * Does nothing right now
     */
    public void test (){
        
    }
    
    
}