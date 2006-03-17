package org.isb.bionet.datasource.interactions;


import java.sql.ResultSet;
import java.util.*;

import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.xmlrpc.handler.db.SQLUtils;
import org.isb.xmlrpc.server.MyWebServer;
import org.isb.bionet.datasource.synonyms.*;

/**
 * This is a handler for interaction sources that contain only one database "interactions" table with the following description:
 * <p>
 * +-----------------+-------------+------+-----+---------+-------+
 * | Field           | Type        | Null | Key | Default | Extra |
 * +-----------------+-------------+------+-----+---------+-------+
 * | id              | varchar(25) | YES  | MUL | NULL    |       |
 * | i1              | varchar(25) | YES  |     | NULL    |       |
 * | interactionType | varchar(2)  | YES  |     | NULL    |       |
 * | i2              | varchar(25) | YES  |     | NULL    |       |
 * | taxid1          | int(11)     | YES  | MUL | NULL    |       |
 * | taxid2          | int(11)     | YES  | MUL | NULL    |       |
 * +-----------------+-------------+------+-----+---------+-------+
 * <p>
 * Interactions sources can extend this class to add more specific behaviour.
 * 
 * @author Iliana Avila-Campillo
 *
 */
public class SimpleInteractionsSource extends SQLDBHandler implements InteractionsDataSource {
    
    public static final String NAME = "SimpleInteractionsSource";
    public static final String ID_TYPE = SynonymsSource.REFSEQ_ID;
    
    /**
     * Empty constructor
     */
    public SimpleInteractionsSource() {
        super(SQLDBHandler.MYSQL_JDBC_DRIVER);
        makeConnection(MyWebServer.PROPERTIES.getProperty(JDBC_URL_PROPERTY_KEY));
    }
    
    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public SimpleInteractionsSource(String mysql_url) {
        super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
    }
    
    public String getDataSourceName (){
        return NAME;
    }
    
    public String getIDtype(){
        return ID_TYPE;
    }
    
    /**
     * @return the type of backend implementation (how requests to the data source
     * are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB, MEMORY, MIXED
     */
    // TODO: Implement
    public String getBackendType (){
        // Need a way to find out local or remote
        return "";
    }

    /**
     * @return a Vector of Strings representing the taxid for which the data
     * source contains information, in this case, this is a vector of Strings parsable as ints (NCBI taxids)
     */
    public Vector getSupportedSpecies (){
        // Taxid can be 0 if the interactos are not one of DNA, protein, RNA or gene
        String sql = "(SELECT taxid1 FROM interactions WHERE taxid1 != 0) UNION (SELECT taxid2 FROM interactions WHERE taxid2 != 0)";
        ResultSet rs = query(sql);
        Vector taxids = new Vector();
        try{
            while(rs.next()){
               String taxid = rs.getString(1);
               taxids.add(taxid);
            }
        }catch(Exception e){
            e.printStackTrace();
            return EMPTY_VECTOR;
        }
        return taxids;
    }
    
    /**
     * 
     * @param taxid a String parsable as int (NCBI taxid)
     * @return TRUE if the given taxid is supported by this data source, FALSE otherwise
     */
    public Boolean supportsSpecies (String taxid){
        Boolean b = new Boolean(getSupportedSpecies().contains(taxid));
        return b;
    }

    /**
     * @return a String denoting the version of the data source (could be a release date,
     * a version number, etc).
     */
    //TODO: Implement
    public String getVersion (){
        return "NO VERSION INFO";
    }

    /**
     * @return boolean whether or not this data source requires a password from the user
     * in order to access it
     */
    public Boolean requiresPassword (){
        return Boolean.FALSE;
    }
    
    /**
     * @return Boolean.TRUE always, since this data source does not require a password
     */
    public Boolean authenticate (String userName, String password){
        return Boolean.TRUE;
    }


    /**
     * Runs tests on the data source
     * @return a vector of results
     */
    public Vector test (){
        return getSupportedSpecies();
    }
    
    /**
     * Sets SOURCE to this.NAME. Extending classes may want to write their own makeInteractions method
     * 
     * @param rs has columns: i1, type, i2 (all strings)
     * @return
     */
    public Vector makeInteractions (ResultSet rs){
        Vector interactions = new Vector();
        try{
            while(rs.next()){
               String i1 =  rs.getString(1);
               int index = i1.indexOf(":");
               
               if(index < 0){
                   i1 = SynonymsSource.REFSEQ_ID + ":" + i1;
               }
               
               String i2 =  rs.getString(3);
               index = i2.indexOf(":");
               if(index < 0){
                   i2 = SynonymsSource.REFSEQ_ID + ":" + i2;
               }
               
               String type = rs.getString(2);
               Hashtable intr = new Hashtable();
               intr.put(INTERACTOR_1, i1);
               intr.put(INTERACTOR_2, i2);
               intr.put(INTERACTION_TYPE, type);
               intr.put(SOURCE, NAME);
               interactions.add(intr);
            }
        }catch(Exception e){
            e.printStackTrace();
            return EMPTY_VECTOR;
        }
        return interactions;
    }
    
    /**
     * Adds a REFSEQ_ID: prefix to each id in the result set. Extending classes may want to have their own makeInteractors method
     * @param rs has 1 column (interactor)
     * @return a Vector of unique interactors
     */
    public Vector makeInteractors (ResultSet rs){
        HashSet interactors = new HashSet();
        try{
            while(rs.next()) {
                String id = rs.getString(1);
                int index = id.indexOf(":");
                if(index < 0){id = SynonymsSource.REFSEQ_ID + ":" + id;}
                interactors.add(id);
            }
        }catch(Exception e){
            e.printStackTrace();
            return EMPTY_VECTOR;
        }
        return new Vector(interactors);
    }
    
    /**
     * Returns a vector of interactors that are of one of the id types that this source accepts without the ID prefix
     * @param ineractors a list of interactors of any ID type
     * @return a filtered set of interactors that are of ID type SynonymsSource.REFSEQ_ID
     */
    public Vector filterInteractors (Vector interactors){
     Iterator it = interactors.iterator();
     HashSet filtered = new HashSet();
     while(it.hasNext()){
         String id = (String)it.next();
         int index = id.indexOf(SynonymsSource.REFSEQ_ID + ":");
         if(index >= 0){
             id = id.substring(index + SynonymsSource.REFSEQ_ID.length() + 1);
             filtered.add(id);
         }else{
             filtered.add(id); // GI:, SPROT: need to stay this way
         }
     }
     return new Vector(filtered);
    }
   
    //------------------------ get interactions en masse --------------------
    /**
     * @param taxid
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables
     */
    public Vector getAllInteractions (String taxid){
        String sql = "SELECT i1,interactionType,i2 FROM interactions WHERE taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        Vector interactions = makeInteractions(rs);
        return interactions;
    }

    /**
     * @param taxid
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String taxid){
        String sql = "SELECT COUNT(*) FROM interactions WHERE taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
    /**
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc), for this class it is ignored
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables
     */
    public Vector getAllInteractions (String taxid, Hashtable args){
        return getAllInteractions(taxid);
    }
              
    /**
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc), for this class, it is ignored
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String taxid, Hashtable args) {
        return getNumAllInteractions(taxid);
    }
   
    
    //-------------------------- 1st neighbor methods ---------------------------
      
    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return a Vectors of String ids of all the nodes that
     * have a direct interaction with the interactors in the given input vector, positions
     * in the input and output vectors are matched (parallel vectors)
     */
    public Vector getFirstNeighbors (Vector interactors, String taxid){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
     
        String sql = "SELECT i2 FROM interactions WHERE i1 IN (" +inInteractors + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        Vector fNeighbors = makeInteractors(rs);
        
        sql = "SELECT i1 FROM interactions WHERE i2 IN (" +inInteractors + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        rs = query(sql);
        Vector fNeighbors2 = makeInteractors(rs);
        
        fNeighbors.removeAll(fNeighbors2);
        fNeighbors.addAll(fNeighbors2);
        return fNeighbors;
    }
    
    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String taxid){
      
        if(interactors.size() == 0) return new Integer(0);
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE i1 IN (" +inInteractors + ") OR i2 IN (" + inInteractors + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer (SQLUtils.getInt(rs));
    }
    
    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc), ignored for this class
     * @return a Vector of String ids of all the nodes that
     * have a direct interaction with the interactors in the given input vector, positions
     * in the input and output vectors are matched (parallel vectors)
     */
    public Vector getFirstNeighbors (Vector interactors, String taxid, Hashtable args){
        
        return getFirstNeighbors(interactors, taxid);
        
    }

    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc), for this class it is ignored
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String taxid, Hashtable args){
            return getNumFirstNeighbors(interactors,taxid);
    }

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction (they are required to contain the following entries:)<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables.<br>
     * The input and output vectors are parallel.
     */
    public Vector getAdjacentInteractions (Vector interactors, String taxid){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
     
        String sql = "SELECT i1,interactionType,i2 FROM interactions WHERE i1 IN (" +inInteractors + ") OR i2 IN (" + inInteractors + 
            ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        Vector interactions = makeInteractions(rs);
        return interactions;    
    }

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String taxid){
        
        if(interactors.size() == 0) return new Integer(0);
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
     
        String sql = "SELECT COUNT(*) FROM interactions WHERE i1 IN (" +inInteractors + ") OR i2 IN (" + inInteractors + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));      
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc), ignored for this class
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction (they are required to contain the following entries:)<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables.<br>
     * The input and output vectors are parallel.
     */
    public Vector getAdjacentInteractions (Vector interactors, String taxid, Hashtable args){
        return getAdjacentInteractions(interactors,taxid);
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc), ignored for this class
     * @return the number of adjacent interations
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String taxid, Hashtable args){
        return getNumAdjacentInteractions(interactors,taxid);
    }

    //-------------------------- connecting interactions methods -----------------------

    
    /**
     * @param interactors
     * @param taxid
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction between the two interactors, each hash contains these entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables 
     */
    public Vector getConnectingInteractions (Vector interactors, String taxid){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
     
        String sql = "SELECT i1,interactionType,i2 FROM interactions WHERE ( i1 IN (" +inInteractors + ") AND i2 IN (" + inInteractors + ") ) OR " +
            "( i2 IN (" +inInteractors + ") AND i1 IN (" + inInteractors + ") ) AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        Vector interactions = makeInteractions(rs);
        return interactions;    
    }
    
    /**
     * @param interactors
     * @param taxid
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String taxid){
      
        if(interactors.size() == 0) return new Integer(0);
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
     
        String sql = "SELECT COUNT(*) FROM interactions WHERE ( i1 IN (" +inInteractors + ") AND i2 IN (" + inInteractors + ") ) OR " +
            "( i2 IN (" +inInteractors + ") AND i1 IN (" + inInteractors + ") ) AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
    /**
     * @param interactors
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc), ignored for this class
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction between the two interactors, each hash contains these entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables 
     */
    public Vector getConnectingInteractions (Vector interactors, String taxid, Hashtable args){
        return getConnectingInteractions(interactors,taxid);
    }
    
    /**
     * @param interactors
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc), ignored for this class
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String taxid, Hashtable args){
        return getNumConnectingInteractions(interactors,taxid);
    }
    
}