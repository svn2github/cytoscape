package org.isb.bionet.datasource.interactions;

import java.sql.ResultSet;
import java.util.*;

import org.isb.xmlrpc.handler.db.SQLUtils;
import org.isb.bionet.datasource.synonyms.*;

public class BindInteractionsSource extends SimpleInteractionsSource implements InteractionsDataSource {
    
    public static final String NAME = "BIND";
    public static final String ID_TYPE = SynonymsSource.REFSEQ_ID; // optional ids are UniProt and EMBL
    public static final String INT_TYPES_ARG = "InteractionTypes";
    
    // Types of interactinons
    /**
     * Protein-protein interaction
     */
    public static final String PP_INTERACTION = "pp";
    /**
     * Protein-DNA interaction
     */
    public static final String PD_INTERACTION = "pd";
    /**
     * Protein-RNA interaction
     */
    public static final String PR_INTERACTION = "pr";
    
    /**
     * A map from an interactio type (String) to its description (String), contains all interaction types in Prolinks
     */
    public static final Hashtable INT_TYPES = new Hashtable();
    static{
        INT_TYPES.put(PP_INTERACTION, "protein-protein");
        INT_TYPES.put(PD_INTERACTION, "protein-DNA");
        INT_TYPES.put(PR_INTERACTION, "protein-RNA");
    }
  
    /**
     * Empty constructor
     */
    public BindInteractionsSource() {
      super();
    }
    
    /**
     * Overrides super.makeConnection(url)
     */
   public boolean makeConnection (String url){
       boolean ok = super.makeConnection(url);
       if(!ok){ 
           System.out.println("Could not make connection to " + url);
           return ok;
       }
       
        // Look for the current go database
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"bind\"");
        String currentBindDb = null;
        try{
           if(rs.next()){
               currentBindDb = rs.getString(1);
           }
        }catch(Exception e){
            e.printStackTrace();
            ok = false;
        }
        System.out.println("Current BIND database is: [" + currentBindDb + "]");
        if(currentBindDb == null || currentBindDb.length() == 0){
            ok = false;
            throw new IllegalStateException("Oh no! We don't know the name of the current BIND database!!!!!");
            
        }

	/*kdrew: shutdown connection to bionetbuilder_info database*/
	super.shutdown();

	String newURL = url.replaceFirst("bionetbuilder_info", currentBindDb);
	/*kdrew: reconnect to go database, this solves the problem of reconnecting after timeout*/
	ok = super.makeConnection(newURL);
        if(!ok){ 
            System.out.println("Could not make connection to " + url);
            return ok;
        }

	/*kdrew: bind db is already selected*/
        //ok = execute("USE " + currentBindDb);
        return ok;
   }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public BindInteractionsSource(String mysql_url) {
        super(mysql_url);
    }
    
    public String getDataSourceName (){
        return NAME;
    }
    
    public String getIDtype(){
        return ID_TYPE;
    }
    
    /**
     * 
     * @param rs has columns: i1, type, i2 (all strings)
     * @return
     */
    public Vector makeInteractions (ResultSet rs){
        Vector interactions = new Vector();
        try{
            while(rs.next()){
               String i1 =  rs.getString(SimpleInteractionsSource.INTERACTOR1);
               int index = i1.indexOf(":");
               
               if(index < 0){
                   i1 = SynonymsSource.REFSEQ_ID + ":" + i1;
               }
               
               String i2 =  rs.getString(SimpleInteractionsSource.INTERACTOR2);
               index = i2.indexOf(":");
               if(index < 0){
                   i2 = SynonymsSource.REFSEQ_ID + ":" + i2;
               }
               
               String type = rs.getString(SimpleInteractionsSource.INTERACTION_TYPE);
               Hashtable intr = new Hashtable();
               intr.put(INTERACTOR_1, i1);
               intr.put(INTERACTOR_2, i2);
               intr.put(InteractionsDataSource.INTERACTION_TYPE, type);
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
     * 
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
             filtered.add(id); // UniProt: and EMBL: need to stay
         }
     }
     return new Vector(filtered);
    }
    
    // Methods that take a Hashtable args. The ones that do not take this argument are handled by SimpleInteractionsSource
    
    //------------------------ get interactions en masse --------------------
    
    /**
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables
     */
    public Vector getAllInteractions (String taxid, Hashtable args){
       
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getAllInteractions(taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        String sql = "SELECT * FROM interactions WHERE interactionType IN (" + interactionTypes + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return makeInteractions(rs);
        
    }
              
    /**
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String taxid, Hashtable args) {
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getNumAllInteractions(taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE interactionType IN (" + interactionTypes + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
        
    }
   
    
    //-------------------------- 1st neighbor methods ---------------------------
    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return a Vector of String ids of all the nodes that
     * have a direct interaction with the interactors in the given input vector, positions
     * in the input and output vectors are matched (parallel vectors)
     */
    public Vector getFirstNeighbors (Vector interactors, String taxid, Hashtable args){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getFirstNeighbors(interactors,taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT i2 FROM interactions WHERE i1 IN (" +inInteractors + ") AND interactionType IN (" + interactionTypes + 
            ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        Vector fNeighbors = makeInteractors(rs);
        
        sql = "SELECT i1 FROM interactions WHERE i2 IN (" +inInteractors + ") AND interactionType IN (" + interactionTypes + 
            ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        rs = query(sql);
        Vector fNeighbors2 = makeInteractors(rs);
        
        fNeighbors.removeAll(fNeighbors2);
        fNeighbors.addAll(fNeighbors2);
        return fNeighbors;
        
    }

    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String taxid, Hashtable args){
        if(interactors.size() == 0) return new Integer(0);
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getNumFirstNeighbors(interactors, taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE i1 IN (" +inInteractors + ") OR i2 IN (" + inInteractors + 
        ") AND interactionType IN (" + interactionTypes + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer (SQLUtils.getInt(rs));
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction (they are required to contain the following entries:)<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables.<br>
     * The input and output vectors are parallel.
     */
    public Vector getAdjacentInteractions (Vector interactors, String taxid, Hashtable args){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getAdjacentInteractions(interactors, taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT i1,interactionType,i2 FROM interactions WHERE i1 IN (" + inInteractors + ") OR i2 IN (" + inInteractors + 
            ") AND interactionType IN (" + interactionTypes + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return makeInteractions(rs);
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of adjacent interations
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String taxid, Hashtable args){
        
        if(interactors.size() == 0) return new Integer(0);
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getNumAdjacentInteractions(interactors, taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE i1 IN (" + inInteractors + ") OR i2 IN (" + inInteractors + 
            ") AND interactionType IN (" + interactionTypes + ") AND taxid1 = " + taxid + " AND taxid2 = " + taxid;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
        
    }

    //-------------------------- connecting interactions methods -----------------------
    /**
     * @param interactors
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction between the two interactors, each hash contains these entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables 
     */
    public Vector getConnectingInteractions (Vector interactors, String taxid, Hashtable args){
        
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getAdjacentInteractions(interactors, taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        if(interactionTypes.length() == 0)  return EMPTY_VECTOR;
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        if(inInteractors.length() == 0)  return EMPTY_VECTOR;
        
        String sql = "SELECT i1,interactionType,i2 FROM interactions WHERE ( i1 IN (" +inInteractors + ") AND i2 IN (" + inInteractors + ") ) OR " +
        "( i2 IN (" +inInteractors + ") AND i1 IN (" + inInteractors + ") ) AND taxid1 = " + taxid + " AND taxid2 = " + taxid + " AND interactionType IN (" + interactionTypes + ")";
        ResultSet rs = query(sql);
        Vector interactions = makeInteractions(rs);
        return interactions;
        
    }
    
    /**
     * @param interactors
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String taxid, Hashtable args){
      
        if(interactors.size() == 0) return new Integer(0);
        
        Vector intTypes = (Vector)args.get(INT_TYPES_ARG);
        if(intTypes == null || intTypes.size() == 0) return getNumAdjacentInteractions(interactors, taxid);
        
        String interactionTypes = "";
        Iterator it = intTypes.iterator();
        if(it.hasNext()) interactionTypes = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) interactionTypes += "," + "\"" + (String)it.next() + "\"";
        
        it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        if(inInteractors.length() == 0)  return new Integer(0);
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE ( i1 IN (" +inInteractors + ") AND i2 IN (" + inInteractors + ") ) OR " +
        "( i2 IN (" +inInteractors + ") AND i1 IN (" + inInteractors + ") ) AND taxid1 = " + taxid + " AND taxid2 = " + taxid +
        " AND interactionType IN (" + interactionTypes + ")";
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
        
    }
    
}
