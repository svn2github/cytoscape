package org.isb.iavila.ontology.xmlrpc;

import java.util.*;
import java.sql.*;
import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.xmlrpc.handler.db.SQLUtils;

/**
 * 
 * @author iavila
 * TODO: jdbc URL should be read from a file (xmlrpc.props?)
 * TODO: Create an interface for ontologies
 */
public class GOHandler extends SQLDBHandler {
    
    /**
     * The name of the data source
     */
    public static final String NAME = "Gene Ontology";
    
    // TODO: Maybe move these to an interface for ontology?
    public static final String TERM_NAME = "termName";
    public static final String TERM_ID = "termID";
    public static final String TERM_TYPE = "termType";
    public static final String TERM_ACC = "termACC";
    public static final String IS_OBSOLETE = "isObsolete";
    public static final String IS_ROOT = "isRoot";
    public static final String GENUS = "genus";
    public static final String SPECIES = "species";
    public static final String SPECIES_ID = "spID";
    public static final String SP_COMMON_NAME = "spCommonName";
    public static int ROOT_TERM_ID;
    
    /**
     * Calls this(String mysql_url)
     */
    public GOHandler (){
      
        super("jdbc:mysql://wavelength.systemsbiology.net/metainfo?user=cytouser&password=bioNetBuilder", SQLDBHandler.MYSQL_JDBC_DRIVER);
        
        // Look for the current go database
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"go\"");
        String currentGoDb = null;
        try{
           if(rs.next()){
               currentGoDb = rs.getString(1); 
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Current GO database is: [" + currentGoDb + "]");
        if(currentGoDb == null || currentGoDb.length() == 0){
            throw new IllegalStateException("Oh no! We don't know the name of the current GO database!!!!!");
        }
        execute("USE " + currentGoDb);
        initialize();
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public GOHandler (String mysql_url) {
        super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
        initialize();
    }// ProlinksInteractionsSource
    
    /**
     * Initializes  internal variables
     */
    protected void initialize (){
       // Find out the term id of the root
        String sql = "SELECT id FROM term WHERE term_type = \"universal\"";
        ResultSet rs = query(sql);
        ROOT_TERM_ID = SQLUtils.getInt(rs);
    }
    
   
    /**
     * @return a Hashtable of term id's (Strings that are parsable as Integers) to Vectors of their
     * children term ids (Strings that are parsable as Integers)
     */
    public Hashtable getTermsChildren (){
        String sql = "SELECT * FROM term_children";
        ResultSet rs = query(sql);
        return getTermToChildrenTable(rs);    
    }
   
    
    /**
     * @param termID
     * @return Vector of Hashtables
     * @see getTermsInfo
     */
    public Vector getChildren (int termID){
        
        String sql = 
        
        "SELECT child.id, child.name, child.term_type, child.acc, child.is_obsolete, child.is_root " + 
        "FROM term AS parent, " + 
             "term2term, " +
             "term AS child " + 
        "WHERE " +
             "parent.id = term2term.term1_id AND " +
             "parent.id = " + termID + " AND " +
             "child.id  = term2term.term2_id AND " +
             "parent.id != child.id AND " +
             "child.is_obsolete = 0";
        ResultSet rs = query(sql);
        
        Vector children = makeTermsVector(rs);
        //System.out.println(termID + " has " + children.size() + "children");
        return children;
    }
    
    /**
     * 
     * @param termID
     * @return Vector of Strings parsable as integers
     */
    public Vector getChildrenIDs (int termID){
        
        String sql = 
        
        "SELECT child.id " + 
        "FROM term AS parent, " + 
             "term2term, " +
             "term AS child " + 
        "WHERE " +
             "parent.id = term2term.term1_id AND " +
             "parent.id = " + termID + " AND " +
             "child.id  = term2term.term2_id AND " +
             "parent.id != child.id AND " +
             "child.is_obsolete = 0";
        ResultSet rs = query(sql);
        
        Vector children = new Vector();
        try{
            while(rs.next()){
                int id = rs.getInt(1);
                children.add(Integer.toString(id));
            }//while
            //System.out.println(termID + " has " + children.size() + "children");
            return children;
        }catch(SQLException e){
            e.printStackTrace();
            return new Vector();
        }
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
    public Hashtable getTermsInfo (Vector termIDs){
        
        Iterator it = termIDs.iterator();
        
        if(!it.hasNext()) return new Hashtable();
        
        String id = (String)it.next();
        String termsList = id;
        
        while(it.hasNext()){
            id = (String)it.next();
            termsList += ", " + id;
        }
        String sql = "SELECT id,name,term_type,acc,is_obsolete,is_root FROM term WHERE id IN (" + termsList + ")";
        ResultSet rs = query(sql);
       return makeTermsHash(rs);
    }
    
    /**
     * @param termIDs Vector of Strings parsable as Integers
     * @return a Vector Strings correspinding to the term names given in the input vector
     */
    public Vector getTermsNames (Vector termIDs){
        Iterator it = termIDs.iterator();
        
        if(!it.hasNext()) return new Vector();
        String id = (String)it.next();
        String termsList = id;
        
        while(it.hasNext()){
            id = (String)it.next();
            termsList += ", " + id;
        }
       
        String sql = "SELECT name FROM term WHERE id IN (" + termsList + ")";
        ResultSet rs = query(sql);
        Vector names = new Vector();
        try{
            while(rs.next()){
                String name = rs.getString(1);
                names.add(name);
            }//while
            return names;
        }catch(SQLException e){
            e.printStackTrace();
            return new Vector();
        }
       
    }
    
    /**
     * @return a Hashtable from term ids to their names (String to String, parsable as Integers)
     */
    public Hashtable getTermsToNames (){
        String sql = "SELECT id, name FROM term";
        Hashtable termToName = new Hashtable();
        ResultSet rs = query(sql);
        try{
            while(rs.next()){
                String id = Integer.toString(rs.getInt(1));
                String name = rs.getString(2);
                termToName.put(id, name);
            }
            return termToName;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
    }
    
    /**
     * @return the ID of the root of this ontology (in GO, this is called "all" and the id is usually 1)
     */
    public Integer getRootTermID (){
        return new Integer(ROOT_TERM_ID);
    }
    
    /**
     * @return a Vector of Strings that are parsable as Integers
     */
    public Vector getAllTermIDs (){
       String sql = "SELECT id FROM term";
       ResultSet rs = query(sql);
       try{
           Vector ids = new Vector();
           while(rs.next()){
               int id = rs.getInt(1);
               ids.add(Integer.toString(id));
           }
           return ids;
       }catch(SQLException ex){
           ex.printStackTrace();
           return new Vector();
       }
    }
    
    /**
     * @param termIDs a Vector of Strings parsable as integers representing term ids
     * @param speciesID the species for which to return genes
     * @param recursive if true, then the returned table will contain entries for descendants terms of the given termIDs
     * @return a Hashtable from Strings (termIDs parsable as Integers) to Vectors of Strings representing genes
     * with the given key term
     */
    public Hashtable getGenesWithTerms (Vector termIDs, String speciesID, boolean recursive){
        String sql = "SELECT ncbi_taxa_id FROM species WHERE id = " + speciesID;
        int taxid = -1;
        try{
            ResultSet rs = query(sql);
            if(rs.next()) taxid = rs.getInt(1);
        }catch(SQLException e){e.printStackTrace(); return new Hashtable();}
        
        if(taxid == -1) return new Hashtable();
        if(!recursive){
            Hashtable table = getGenesWithTerms(termIDs,taxid);
            return table;
        }
        Vector dTerms = new Vector(); // stores ids of descendants terms of termIDs
        getTermsDescendantTerms(termIDs,taxid,dTerms);
        return getGenesWithTerms(dTerms, taxid);
        }
    
    /**
     * Accumulates in descendantTerms the terms that are descendants of the given terms in termIDs
     * @param termIDs a Vector of Strings parsable as integers representing term ids
     * @param taxid the NCBI taxonomy id of the species
     * @param descendantTerms vector where terms of descendants will be stored
     */
    protected void getTermsDescendantTerms (Vector termIDs,int taxid, Vector descendantTerms){
        if(descendantTerms == null) descendantTerms = new Vector();
        
        Iterator it = termIDs.iterator();
        while(it.hasNext()){
            String currentTerm = (String)it.next();
            // get the children for this term
            Vector childrenIDs = null;
            try{
                childrenIDs = getChildrenIDs(Integer.parseInt(currentTerm));
            }catch(Exception e){e.printStackTrace(); return;}
            
            if(childrenIDs.size() > 0){
                descendantTerms.addAll(childrenIDs);
                getTermsDescendantTerms(childrenIDs,taxid,descendantTerms);
            }
            
        }//it.hasNext
        
    }
    
    /**
     * @param termIDs a Vector of Strings parsable as integers representing term ids
     * @param speciesID the species for which to return genes
     * @return a Hashtable from Strings (the given termIDs) to Vectors of Strings representing genes
     * with the given key term, NOT RECURSIVE
     */
    protected Hashtable getGenesWithTerms (Vector termIDs, int taxid){
      
      // 1. get acc ids for termIDs
      Iterator it = termIDs.iterator();
      String termList = (String)it.next();
      while(it.hasNext()){
          termList += "," + (String)it.next();
      }
      
      String sql = "SELECT acc,id FROM term WHERE id IN (" + termList + ")";
      ResultSet rs = query(sql);
      String accList = "";
      Hashtable accToId = new Hashtable();
      try{
          while(rs.next()){
              String acc = rs.getString(1);
              if(accList.length() == 0) accList += "\"" + acc + "\""; 
              else accList += ",\"" + acc + "\"";  
              accToId.put(acc,rs.getString(2));
          }
      }catch(SQLException e){e.printStackTrace(); return new Hashtable();}
      
      if(accList.length() == 0) return new Hashtable();
        
      // 2. get genes with given accs and of the given speciesID from gi2go  
      
      sql = "SELECT gi,goid FROM gi2go WHERE taxid = " + taxid + " AND goid IN (" + accList + ")";
      rs = query(sql);
      
      Hashtable result = new Hashtable();
      
      try{
          while(rs.next()){
              String gi = "GI:" + rs.getString(1);
              String acc = rs.getString(2);
              String termID = (String)accToId.get(acc);
              Vector gis = (Vector)result.get(termID);
              if(gis == null) gis = new Vector();
              gis.add(gi);
              result.put(termID, gis);
          }
      }catch(SQLException e){e.printStackTrace();return new Hashtable();}
      
      return result;
    }
    
    /**
     * 
     * @return a Vector of Hashtables which contain the following information:<br>
     * SPECIES_ID --> String parsable as Integer<br>
     * GENUS --> String<br>
     * SPECIES --> String<br>
     * SP_COMMON_NAME --> String<br>
     * TODO: This method uses up all memory, do not use it!!!
     * @see getSpeciesLike
     */
    public Vector getSpeciesInfo (){
        String sql = "SELECT id, genus, species, common_name FROM species";
        ResultSet rs = query(sql);
        return makeSpeciesVector(rs);
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
    public Vector getSpeciesLike (String likePattern){
        
        String [] split = likePattern.split("\\s");
        String sqlPattern = "";
        for(int i = 0; i < split.length; i++){
            sqlPattern += "%" + split[i];
        }
        sqlPattern += "%";
        
        // First try the pattern with common_name, species, or genus
        Vector species = null;
        String sql = "SELECT id, genus, species, common_name " +
                " FROM species " +
                " WHERE common_name LIKE \'" + sqlPattern + "\' OR species LIKE \'" + sqlPattern + "\'" +" OR genus LIKE \'" + sqlPattern + "\'";
        ResultSet rs = query(sql);
        species = makeSpeciesVector(rs);
        if(species.size() > 0) return species;
     
        if(split.length >= 2){
            sql = "SELECT id, genus, species, common_name FROM species WHERE genus LIKE \'%" + split[0] + "%\' OR species LIKE \'%" + split[1] + "%\'";
            rs = query(sql);
            species = makeSpeciesVector(rs);
        }
        
        return species;    
    }
    
    // -------------- Protected helper methods ----------------//
    
    /**
     * @param rs table with two columns, 1st contains a term id (parsable integer) and the 2nd one
     * contains a comma separated list of term ids (each parsable integers).
     * @return a Hashtable of term id's (Strings that are parsable as Integers) to Vectors of their
     * children term ids (Strings that are parsabel as Integers)
     */
    protected Hashtable getTermToChildrenTable (ResultSet rs){
        
        Hashtable termToChildren = new Hashtable();
        
        try{
            while(rs.next()){
                int parentTermID = rs.getInt(1);
                String termList = rs.getString(2);
                String [] children = termList.split(",");
                Vector childrenV = new Vector();
                for(int i = 0; i < children.length; i++){
                    Integer child = new Integer(Integer.parseInt(children[i]));
                    childrenV.add(child.toString());
                }//for i
                termToChildren.put(Integer.toString(parentTermID), childrenV);
            }//while rs.next
            return termToChildren;
        }catch(SQLException e){
            e.printStackTrace();
            return new Hashtable();
        }
        
    }//getTermToChildren
    
    /**
     * 
     * @param rs with columns: 1 = int species id, 2 = String genus, 3 = string species
     * @return a Vector of Hashtables which contain the following species information:<br>
     * SPECIES_ID --> String parsable as Integer<br>
     * GENUS --> String<br>
     * SPECIES --> String<br> 
     * SP_COMMON_NAME --> String<br>
     */
    protected Vector makeSpeciesVector (ResultSet rs){
        try{
            Vector species = new Vector();
            while(rs.next()){
                Hashtable info = new Hashtable();
                String id = Integer.toString(rs.getInt(1));
                String genus = rs.getString(2);
                String sp = rs.getString(3);
                String cm = rs.getString(4);
                info.put(SPECIES_ID, id);
                info.put(GENUS, genus);
                info.put(SPECIES, sp);
                if(cm != null){
                    info.put(SP_COMMON_NAME, cm);
                }else{
                    info.put(SP_COMMON_NAME,"");
                    
                }
                species.add(info);
            }//next
            return species;
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Vector();
        }//catch
    }
    
    /**
     * 
     * @param rs ResultSet columns are: int id, String name, String type, String acc, int isObsolete, int isRoot
     * @return Hashtable form id (String) to Hashtables that contain as keys: TERM_NAME,TERM_TYPE,TERM_ACC,IS_OBSOLETE,IS_ROOT
     */
    protected Hashtable makeTermsHash (ResultSet rs){
       Hashtable table = new Hashtable();
       try{
           while(rs.next()){
               Hashtable info = new Hashtable();
               int ID = rs.getInt(1);
               String name = rs.getString(2);
               String type = rs.getString(3);
               String acc = rs.getString(4);
               Boolean obs = Boolean.FALSE;
               if(rs.getInt(5) == 1){
                   obs = Boolean.TRUE;
               }
               Boolean root = Boolean.FALSE;
               if(rs.getInt(6) == 1){
                   root = Boolean.TRUE;
               }
               info.put(TERM_NAME, name);
               info.put(TERM_TYPE, type);
               info.put(TERM_ACC, acc);
               info.put(IS_OBSOLETE, obs);
               info.put(IS_ROOT, root);
               table.put(Integer.toString(ID),info);
           }//next
       }catch(SQLException ex){
           ex.printStackTrace();
           return new Hashtable();
       }//catch
       return table;
    }
    
    /**
     * Assumes ResultSet columns are: int id, String name, String type, String acc, int isObsolete, int isRoot
     * @param rs
     * @return Vector of Hashtables
     */
    protected Vector makeTermsVector (ResultSet rs){
        Vector termsInfo = new Vector();
        try{
            while(rs.next()){
                int ID = rs.getInt(1);
                String name = rs.getString(2);
                String type = rs.getString(3);
                String acc = rs.getString(4);
                Boolean obs = Boolean.FALSE;
                if(rs.getInt(5) == 1){
                    obs = Boolean.TRUE;
                }
                Boolean root = Boolean.FALSE;
                if(rs.getInt(6) == 1){
                    root = Boolean.TRUE;
                }
                Hashtable term = new Hashtable();
                term.put(TERM_ID, new Integer(ID));
                term.put(TERM_NAME, name);
                term.put(TERM_TYPE, type);
                term.put(TERM_ACC, acc);
                term.put(IS_OBSOLETE, obs);
                term.put(IS_ROOT, root);
                termsInfo.add(term);
            }//while
            return termsInfo;
        }catch(SQLException e){
            e.printStackTrace();
            return new Vector(); 
        }
        
    }
    
}