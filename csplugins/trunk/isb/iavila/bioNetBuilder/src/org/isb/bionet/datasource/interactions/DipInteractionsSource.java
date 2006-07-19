package org.isb.bionet.datasource.interactions;

import java.sql.ResultSet;
import java.util.*;
import org.isb.bionet.datasource.synonyms.*;

public class DipInteractionsSource extends SimpleInteractionsSource implements InteractionsDataSource {
    
    public static final String NAME = "DIP";
    public static final String ID_TYPE = SynonymsSource.REFSEQ_ID; // but alternate names are GI and SPROT and PIR in that order
    
    /**
     * Empty constructor
     */
    public DipInteractionsSource() {
        super();
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
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"dip\"");
        String currentDipDb = null;
        try{
           if(rs.next()){
               currentDipDb = rs.getString(1);
           }
        }catch(Exception e){
            ok = false;
            e.printStackTrace();
        }
        System.out.println("Current DIP database is: [" + currentDipDb + "]");
        if(currentDipDb == null || currentDipDb.length() == 0){
            ok = false;
            throw new IllegalStateException("Oh no! We don't know the name of the current DIP database!!!!!");
        }

	/*kdrew: shutdown connection to bionetbuilder_info database*/
	super.shutdown();

	String newURL = url.replaceFirst("bionetbuilder_info", currentDipDb);
	/*kdrew: reconnect to go database, this solves the problem of reconnecting after timeout*/
	ok = super.makeConnection(newURL);
        if(!ok){ 
            System.out.println("Could not make connection to " + url);
            return ok;
        }

	/*kdrew: dip db is already selected*/
        //ok = execute("USE " + currentDipDb);
        return ok;
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public DipInteractionsSource(String mysql_url) {
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
             filtered.add(id); // GI:, SPROT: need to stay this way
         }
     }
     return new Vector(filtered);
    }
    
}
