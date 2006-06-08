package org.isb.bionet.datasource.interactions;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.isb.bionet.datasource.synonyms.SynonymsSource;
import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.xmlrpc.handler.db.SQLUtils;

public class HPRDInteractionsSource extends SimpleInteractionsSource implements InteractionsDataSource {
    
    public static final String NAME = "HPRD";
    public static final String ID_TYPE = SynonymsSource.REFSEQ_ID; // but alternate names are GI and SPROT and PIR in that order
    
    // Detection methods
    public static final String IN_VIVO = "in vivo";
    public static final String IN_VITRO = "in vitro";
    public static final String Y2H = "two hybrid";
    public static final String UNCLASSIFIED = "unclassifi";
    public static final String [] DETECTION_METHOD_ARRAY = {IN_VIVO, IN_VITRO, Y2H, UNCLASSIFIED};
    
    // Arguments:
    
    /**
     * Whether to include interactions for which one of the proteins is not human (for example, the interaction was detected using a mouse protein)
     * Value is Boolean
     */
    public static final String INCLUDE_NON_HUMAN_BAITS = "non-human";
    
    /**
     * Detection methods to use, value is a Vector of recognized detection methods
     */
    public static final String DETECTION_METHODS = "detection methods";
    // Whether to use interactions from the Vidal data set, values are true or false
    public static final String USE_VIDAL = "include vidal";
    
    /**
     * Empty constructor
     */
    public HPRDInteractionsSource() {
        super();
    }
    
    /**
     * @return true
     */
    public Boolean requiresPassword (){return Boolean.TRUE;}
    
    /**
     * @return Boolean.TRUE always, since this data source does not require a password
     */
    public Boolean authenticate (String userName, String password){
        int index = this.url.indexOf("?user");
        String tempUrl = this.url.substring(0,index);
        tempUrl += "?user="+userName+"&password="+password;
        
        SQLDBHandler tempHandler = new SQLDBHandler(SQLDBHandler.MYSQL_JDBC_DRIVER);
        boolean connected = tempHandler.makeConnection(tempUrl);
        if(connected){
            tempHandler.shutdown();
            return Boolean.TRUE;
        }
        
        return Boolean.FALSE;
    }

    
    /**
     * Overrides super.makeConnection(String url)
     */
    public boolean makeConnection (String url){
        boolean ok = super.makeConnection(url);
        if(!ok){ 
            System.out.println("Could not make connection to " + url);
            return ok;
        }
        // Look for the current go database
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"hprd\"");
        String currentDb = null;
        try{
           if(rs.next()){
               currentDb = rs.getString(1);
           }
        }catch(Exception e){
            ok = false;
            e.printStackTrace();
        }
        System.out.println("Current HPRD database is: [" + currentDb + "]");
        if(currentDb == null || currentDb.length() == 0){
            ok = false;
            throw new IllegalStateException("Oh no! We don't know the name of the current HPRD database!!!!!");
        }
        ok = execute("USE " + currentDb);
        return ok;
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public HPRDInteractionsSource(String mysql_url) {
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
            String [] unknownCols = getUnknownColumnNames(rs);
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
               
               // Add the rest of the columns that we don't recognize
               for(int i = 0; i < unknownCols.length; i++){
                   String value = rs.getString(unknownCols[i]);
                   if(value.length() > 0)
                       intr.put(unknownCols[i],value);
               }
            
               interactions.add(intr);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("HPRDInteractionsSource: returning an emtpy vector");
            return EMPTY_VECTOR;
        }
        System.err.println("HPRDInteractionsSource: returning " + interactions.size() + " interactions");
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
             filtered.add(id); // GI:, SPROT: need to stay this way
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
       
        if(args.size() == 0) return getAllInteractions(taxid);
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        		( (Boolean)args.get(USE_VIDAL) != null ? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getAllInteractions(taxid);
    		
        if(detectionMethods != null && detectionMethods.size() == 0) return EMPTY_VECTOR;
        
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        String sql = "SELECT i1,interactionType,i2,primaryPubMed,detectionMethod FROM interactions WHERE " + 
        				(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : "(taxid1 = " + taxid + " OR taxid2 = " + taxid + ")") +
        				(!includeVidalInteractions ? " AND id NOT LIKE \"VIDAL%\"" : "") +
        				(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "");
        
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
    	  	if(args.size() == 0) return getNumAllInteractions(taxid);
          
          // Get the arguments
          Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
          boolean includeNonHumanBaits = 
          		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
          boolean includeVidalInteractions =
          		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
          
          if( (detectionMethods == null || detectionMethods.size() == 4) &&
          		!includeNonHumanBaits && includeVidalInteractions) return getNumAllInteractions(taxid);
          
          if(detectionMethods.size() == 0) return new Integer(0);
          
          String methodStatement = null;
          if(detectionMethods != null && detectionMethods.size() < 4){
          		Iterator it = detectionMethods.iterator();
          		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
          		while(it.hasNext()){
          			methodStatement += ",\"" + (String)it.next() + "\"";
          		}
          }
          
          String sql = "SELECT COUNT(*) FROM interactions WHERE " + 
          				(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : "(taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
          				(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
          				(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "");
          
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
        
        if(args.size() == 0) return getFirstNeighbors(interactors,taxid);
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        	
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null  || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getFirstNeighbors(interactors,taxid);
    		
        if(detectionMethods.size() == 0) return EMPTY_VECTOR;
        
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT i2 FROM interactions WHERE " + 
        				(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
        				(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
        				(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
        				(inInteractors.length() > 0 ? " AND i1 IN (" + inInteractors + ")" : "");
        
        ResultSet rs = query(sql);
        Vector fNeighbors = makeInteractors(rs);
        

        sql = "SELECT i1 FROM interactions WHERE " + 
        				(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
        				(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
        				(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
        				(inInteractors.length() > 0 ? " AND i2 IN (" + inInteractors + ")" : "");
        
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
        
        
        if(args.size() == 0) return getNumFirstNeighbors(interactors,taxid);
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        	
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null  || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getNumFirstNeighbors(interactors,taxid);
    		
        if(detectionMethods.size() == 0) return new Integer(0);
        
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE " + 
        				(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
        				(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
        				(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
        				(inInteractors.length() > 0 ? " AND ( i1 IN (" + inInteractors + ")" : "") +
        				(inInteractors.length() > 0 ? " OR i2 IN (" + inInteractors + ") )" : "");
        
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
        
        if(args.size() == 0) return getAdjacentInteractions(interactors,taxid);
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        	
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getAdjacentInteractions(interactors,taxid);
    		
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT * FROM interactions WHERE " + 
		(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
		(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
		(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
		(inInteractors.length() > 0 ? " AND (i1 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " OR i2 IN (" + inInteractors + ") )" : "");
        
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
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return new Integer(0);
    		if(detectionMethods.size() == 0) return new Integer(0);
        
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE " + 
		(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
		(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
		(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
		(inInteractors.length() > 0 ? " AND (i1 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " OR i2 IN (" + inInteractors + ") )" : "");
        
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
        
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getConnectingInteractions(interactors,taxid);
        
        if(detectionMethods.size() == 0) return EMPTY_VECTOR;
    		
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        if(inInteractors.length() == 0)  return EMPTY_VECTOR;
        
        String sql = "SELECT * FROM interactions WHERE " + 
		(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
		(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
		(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
		(inInteractors.length() > 0 ? " AND (i1 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " AND i2 IN (" + inInteractors + ") )" : "") +
		(inInteractors.length() > 0 ? " OR (i2 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " AND i1 IN (" + inInteractors + ") )" : "");
        
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
        // Get the arguments
        Vector detectionMethods = (Vector)args.get(DETECTION_METHODS);
        boolean includeNonHumanBaits = 
        		( (Boolean)args.get(INCLUDE_NON_HUMAN_BAITS) != null ? ((Boolean)args.get(INCLUDE_NON_HUMAN_BAITS)).booleanValue() : false);
        boolean includeVidalInteractions =
        	
        		( (Boolean)args.get(USE_VIDAL) != null? ((Boolean)args.get(USE_VIDAL)).booleanValue() : false);
        
        if( (detectionMethods == null || detectionMethods.size() == 0 || detectionMethods.size() == 4) &&
        		!includeNonHumanBaits && includeVidalInteractions) return getNumConnectingInteractions(interactors,taxid);
    		
        String methodStatement = null;
        if(detectionMethods != null && detectionMethods.size() < 4){
        		Iterator it = detectionMethods.iterator();
        		if(it.hasNext()) methodStatement = "\"" + (String)it.next() + "\"";
        		while(it.hasNext()){
        			methodStatement += ",\"" + (String)it.next() + "\"";
        		}
        }
        
        Iterator it = filterInteractors(interactors).iterator();
        String inInteractors = "";
        if(it.hasNext()) inInteractors = "\"" + (String)it.next() + "\"";
        while(it.hasNext()) inInteractors +=  ",\"" + (String)it.next() + "\"";
        if(inInteractors.length() == 0)  return new Integer(0);
        
        String sql = "SELECT COUNT(*) FROM interactions WHERE " + 
		(!includeNonHumanBaits ? "taxid1 = " + taxid + " AND taxid2 = " + taxid : " (taxid1 = " + taxid + " OR taxid2 = " + taxid + ")" ) +
		(!includeVidalInteractions ? " AND id not like \"VIDAL%\"" : "") +
		(methodStatement != null ? " AND detectionMethod IN (" + methodStatement + ")" : "") +
		(inInteractors.length() > 0 ? " AND (i1 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " AND i2 IN (" + inInteractors + ") )" : "") +
		(inInteractors.length() > 0 ? " OR (i2 IN (" + inInteractors + ")" : "") +
		(inInteractors.length() > 0 ? " AND i1 IN (" + inInteractors + ") )" : "");
        
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
        
    }
    
}