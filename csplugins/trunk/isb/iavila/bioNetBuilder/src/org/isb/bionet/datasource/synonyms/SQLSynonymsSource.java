
package org.isb.bionet.datasource.synonyms;

import java.util.*;
import java.sql.*;
import org.isb.bionet.datasource.*;
import org.isb.xmlrpc.handler.db.*;

public class SQLSynonymsSource extends SQLDBHandler implements SynonymsSource {
    
    /**
     * Empty constructor
     */
    public SQLSynonymsSource() {
        // TODO: Remove, this should be read from somewhere!!!
        this("jdbc:mysql://biounder.kaist.ac.kr/synonym3?user=bioinfo&password=qkdldhWkd");
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public SQLSynonymsSource(String mysql_url) {
        super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
    }
    
    
    //------------- SynonymsSource methods ---------//
    
    /**
     * 
     * @param source_id_type one of the supported id types
     * @param source_ids a Vector of ids of type source_id_type
     * @param target_id_type the type of id that the input ids should be translated to
     * @return a Hashtable from the input source_ids to the resulting translation
     */
    public Hashtable getSynonyms (String source_id_type, Vector source_ids, String target_id_type){
        
        if(source_id_type.equals(PROLINKS_ID) && target_id_type.equals(GI_ID)){
            return prolinksToGi(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(PROLINKS_ID)){
            return giToProlinks(source_ids);
        }
        
        if(source_id_type.equals(KEGG_ID) && target_id_type.equals(GI_ID)){
            return keggToGi(source_ids);
        }
        
        if(source_id_type.equals(GI_ID) && target_id_type.equals(KEGG_ID)){
            return giToKegg(source_ids);
        }
         
        return new Hashtable();
    
   }
    //--------- Helper protected methods -----------//
    protected Hashtable prolinksToGi (Vector prolinks_ids){
        
        Iterator it = prolinks_ids.iterator();
        if(!it.hasNext())
            return new Hashtable();
        String id = (String)it.next();
        String or = " prolinksid = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR prolinksid = " + id;
        }
        
        String sql = "SELECT prolinksid, gi FROM key_prolinks WHERE" + or;
        
        ResultSet rs = query(sql);
        try{
            Hashtable pToG = new Hashtable();
            while(rs.next()){
                pToG.put(rs.getString(1), rs.getString(2));
            }
            return pToG;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
    
    protected Hashtable giToProlinks (Vector gi_ids){
        
        Iterator it = gi_ids.iterator();
        if(!it.hasNext())
            return new Hashtable();
        String id = (String)it.next();
        String or = " gi = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR gi = " + id;
        }
        
        String sql = "SELECT gi,prolinksid FROM key_prolinks WHERE" + or;
        
        ResultSet rs = query(sql);
        try{
            Hashtable gToP = new Hashtable();
            while(rs.next()){
                gToP.put(rs.getString(1), rs.getString(2));
            }
            return gToP;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
           
    }
   
    protected Hashtable keggToGi (Vector kegg_ids){
        
        Iterator it = kegg_ids.iterator();
        
        if(!it.hasNext()) return new Hashtable();
        
        String id = (String)it.next();
        String or = " kegg_id = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR kegg_id = " + id;
        }
        
        String sql = "SELECT kegg_id, gi FROM gi_kegg WHERE " + or;
        ResultSet rs = query(sql);
        
        try{
            Hashtable kToG = new Hashtable();
            while(rs.next()){
                kToG.put(rs.getString(1), rs.getString(2));
            }
            return kToG;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
    
    protected Hashtable giToKegg (Vector gi_ids){
        
        Iterator it = gi_ids.iterator();
        
        if(!it.hasNext()) return new Hashtable();
        
        String id = (String)it.next();
        String or = " gi = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR gi = " + id;
        }
        
        String sql = "SELECT gi, kegg_id FROM gi_kegg WHERE " + or;
        ResultSet rs = query(sql);
        
        try{
            Hashtable gToK = new Hashtable();
            while(rs.next()){
                gToK.put(rs.getString(1), rs.getString(2));
            }
            return gToK;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
    }
  
    //------------- DataSource methods -------------//

    /**
     * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
     */
    public String getDataSourceName (){
        return "Synonyms";
    }
    
    /**
     * @return the type of backend implementation (how requests to the data source
     * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
     */
    //TODO: Need to figure out if DB is running locally or remotely
    public String getBackendType (){
        return DataSource.REMOTE_DB; // for now
    }

    /**
     * @return an empty Vector
     */
    public Vector getSupportedSpecies (){
        return new Vector();
    }

    /**
     * @return a String denoting the version of the data source (could be a release date,
     * a version number, etc).
     */

    public String getVersion (){
        return ""; //for now
    }

    /**
     * @return false
     */
    public boolean requiresPassword (){
        return false;
    }

    /**
     * Runs tests on the data source
     * @return a vector of results
     */
    public Vector test (){
        return new Vector();
    }
    
}