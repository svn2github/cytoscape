package org.isb.iavila.ontology.xmlrpc;

import java.util.*;
import java.sql.*;
import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.xmlrpc.handler.db.SQLUtils;

/**
 * 
 * @author iavila
 * TODO: Create term_children table should be in the Perl scripts Jung created
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
        // TODO: Remove, this should be read from somewhere!!!
        this("jdbc:mysql://biounder.kaist.ac.kr/go?user=bioinfo&password=qkdldhWkd");
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
        // This only needs to be done when the database is updated. Need to move it to Jung's database perl scripts:
        //createChildrenTable();
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
        System.out.println(termID + " has " + children.size() + "children");
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
        String or = " id = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR id = " + id;
        }
        String sql = "SELECT id,name,term_type,acc,is_obsolete,is_root FROM term WHERE " + or;
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
        String or = " id = " + id;
        
        while(it.hasNext()){
            id = (String)it.next();
            or += " OR id = " + id;
        }
        String sql = "SELECT name FROM term WHERE " + or;
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
     * @return a Hashtable from Strings (termIDs parsable as Integers) to Vectors of Strings representing genes
     * with the given key term
     */
    public Hashtable getGenesWithTerms (Vector termIDs, String speciesID){
        
        Hashtable termToGenes  = new Hashtable();
        Hashtable accToTermID = new Hashtable();
        
        // The term ids are Strings parsable as Integers. Get the GO: term identifiers.
        String or = "";
        Iterator it = termIDs.iterator();
        
        if(!it.hasNext()) return termToGenes;
        
        or = " id = " + (String)it.next();
        while(it.hasNext()){
            or += " OR id = " + (String)it.next();
        }//it.hasNext
        
        System.err.println(or);
        
        if(or.length() == 0) return termToGenes;
        
        String sql = "SELECT acc, id FROM term WHERE " + or;
        ResultSet rs = query(sql);
        
        or = "";
        try{
            while(rs.next()){
                String goID = rs.getString(1);
                int intID = rs.getInt(2);
                if (or.length() == 0){
                         or = " acc = \"" + goID + "\"";
                }else{
                    or += " acc = \"" + goID + "\"";
                }//else
                accToTermID.put(goID, new Integer(intID));
            }// while rs.next
        }catch(Exception ex){
            ex.printStackTrace();
            return termToGenes;
        }//catch
        
        System.err.println(or);
        
        if(or.length() == 0) return termToGenes;
        
        sql = "SELECT acc,gi FROM gi2go WHERE " + or;
        rs = query(sql);
        
        try{
            while(rs.next()){
                String acc = rs.getString(1);
                int giID = rs.getInt(2);
                Integer intTermID = (Integer)accToTermID.get(acc);
                Vector genes = (Vector)termToGenes.get(intTermID.toString());
                if(genes == null){
                    genes = new Vector();
                    termToGenes.put(intTermID.toString(), genes);
                }
                genes.add("GI:"+Integer.toString(giID));
            }
        }catch(Exception ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        
        System.err.println(termToGenes);
        
        return termToGenes;
        
//        Iterator it = termIDs.iterator();
//        Hashtable termToGenes = new Hashtable();
//        while(it.hasNext()){
//            String termID = (String)it.next();
//            String sql = "SELECT a.gene_product_id " + 
//                "FROM association AS a, gene_product AS gene " + 
//                "WHERE a.term_id = " +  termID +
//                " AND gene.id = a.gene_product_id " +
//                " AND gene.species_id = " + speciesID;
//            ResultSet rs = query(sql);
//            try{
//                Vector genes = new Vector();
//                while(rs.next()){
//                    int gene_id = rs.getInt(1);
//                    genes.add(Integer.toString(gene_id));
//                }//while rs.next
//                termToGenes.put(termID,genes);
//            }catch(SQLException ex){
//                ex.printStackTrace();
//            }//catch
//        
//        }//while it.hasNext
//        return termToGenes;
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
     * TODO: Refine this!!!!!
     */
    public Vector getSpeciesLike (String likePattern){
        // First try the pattern as it is with common_name and species
        Vector species = null;
        String sql = "SELECT id, genus, species, common_name FROM species WHERE common_name LIKE \'" + likePattern + "\' OR species LIKE \'" + likePattern + "\'";
        ResultSet rs = query(sql);
        species = makeSpeciesVector(rs);
        if(species.size() > 0) return species;
        
        // Now try the pattern with wild-cards for common_name and species
        String pattern = "\'%" + likePattern + "%\'";
        sql = "SELECT id, genus, species, common_name FROM species WHERE common_name LIKE " + pattern + " OR species LIKE " + pattern;
        rs = query(sql);
        species = makeSpeciesVector(rs);
        if(species.size() > 0) return species;
        
        // Now try to split the pattern by white space, and see if the first part is genus, and the second species 
        String [] split = likePattern.split("\\s");
        if(split.length >= 2){
            sql = "SELECT id, genus, species, common_name FROM species WHERE genus LIKE \'%" + split[0] + "%\' AND species LIKE \'%" + split[1] + "%\'";
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
           Vector species = new Vector();
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
    
    
    /**
     * Creates a table in go db that contains term ids as keys, and a comma separated list of their children ids.
     */
    //TODO: This needs to be in the Perl scripts Jung wrote
    protected void createChildrenTable (){
        // The table already exists, comment out
        //String sql = "CREATE TABLE term_children ( term_id INT(11), children LONGTEXT);";
        //execute(sql);
        
        insertChildren(ROOT_TERM_ID, new Hashtable());
    }
    
    
    /**
     * Recursively inserts rows into table term_children, starts with the given id and then inserts rows for the children<br>
     * TODO: This should be moved to Jung's perl scripts
     * @param termID
     */
    protected void insertChildren (int termID, Hashtable alreadyInserted){
        
        Integer ID = new Integer(termID);
        
        if(alreadyInserted.contains(ID)) return;
        
        Vector children = getChildrenIDs(termID);
        Iterator it = children.iterator();
        if(!it.hasNext()) return;
        String childrenList = ((Integer)it.next()).toString();
        while(it.hasNext()){
            childrenList += "," + ( (Integer)it.next() ).toString();
        }
        String sql = "INSERT INTO term_children VALUES (" + ID.toString() + ", \"" + childrenList + "\")";
        execute(sql);
        alreadyInserted.put(ID,Boolean.TRUE);
        
        it = children.iterator();
        while(it.hasNext()){
            insertChildren(((Integer)it.next()).intValue(), alreadyInserted);
        }
        
    }
    
    /**
     * Creates and populates a table "term_species_genes" that contains three entries:
     * term_id, species_id,list of genes in species with term
     * TODO: Move to perl scripts
     * TODO: OK. This was silly. It takes LONG. Do not use anymore.
     */
    protected void createTermGenesTable (){
        //String sql = "CREATE TABLE term_species_genes ( term_id INT(11), species_id INT(11), genes LONGTEXT);";
        //execute(sql);
        
        String sql = "SELECT id FROM species";
        ResultSet rs = query(sql);
        Vector speciesIDs = new Vector();
        try{
            while(rs.next()){
                speciesIDs.add(new Integer(rs.getInt(1)));
            }//while
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        Iterator it = speciesIDs.iterator();
        Vector termIDs = getAllTermIDs();
        while(it.hasNext()){
            
            Integer spID = (Integer)it.next();
            Iterator it2 = termIDs.iterator();
            
            while(it2.hasNext()){
                Integer tID = new Integer((String)it2.next());
               sql = "SELECT a.gene_product_id " + 
                             "FROM association AS a, gene_product AS gene " + 
                             "WHERE a.term_id = " + tID +
                                  " AND gene.id = a.gene_product_id " +
                                  " AND gene.species_id = " + spID;
                rs = query(sql);
                try{
                    String geneList = null;
                    int geneId;
                    if(rs.next()){
                        geneId = rs.getInt(1);
                        geneList = Integer.toString(geneId);
                    }//if
                    while(rs.next()){
                        geneId = rs.getInt(1);
                        geneList += "," + Integer.toString(geneId);
                    }//while rs.next
                    
                    System.out.println(geneList);
                    sql = "INSERT INTO term_species_genes VALUES (" + tID.toString() + "," + spID.toString() + 
                        ", \"" + geneList + "\")";
                    execute(sql);
                }catch(Exception e){
                    e.printStackTrace();
                }//catch
                     
            }//while it2
        }//while it
        
    }
    
    
}