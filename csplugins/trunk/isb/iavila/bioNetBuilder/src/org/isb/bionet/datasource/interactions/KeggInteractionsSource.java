package org.isb.bionet.datasource.interactions;

import java.util.*;

import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.bionet.datasource.synonyms.*;
import java.sql.*;

import org.isb.xmlrpc.handler.db.*;
import org.isb.xmlrpc.server.MyWebServer;

/**
 * @author iavila
 */
public class KeggInteractionsSource extends SQLDBHandler implements InteractionsDataSource {
   
    public static final String NAME = "KEGG";
    public static final String GENE_ID_TYPE = SynonymsSource.KEGG_ID;
    public static final String KEGG_INTERACTION_TYPE = "sharedCompound";
    public static final String COMPOUND = "compound";
    public static final String THRESHOLD_KEY = "Th"; // values are Doubles
    public static final String EDGE_PER_CPD_KEY = "oneEdgePerCpd"; // values are Booleans
    public static final int DEFAULT_THRESHOLD = 2300;
    /**
     * A Map that contains "fullname" taxid Strings mapped to the ids that KEGG uses to identify taxid
     */
    protected static Map SPECIES_CACHE = new Hashtable();
    protected boolean debug = true;
     
    /**
     * Empty constructor
     */
    public KeggInteractionsSource() {
        super(SQLDBHandler.MYSQL_JDBC_DRIVER);
        makeConnection(MyWebServer.PROPERTIES.getProperty(JDBC_URL_PROPERTY_KEY));
    }
    
    public boolean makeConnection (String url){
        
        boolean ok = super.makeConnection(url);
        if(!ok){ 
            System.out.println("Could not make connection to " + url);
            return ok;
        }
        
        // Look for the current go database
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"kegg\"");
        String currentKeggDb = null;
        try{
           if(rs.next()){
               currentKeggDb = rs.getString(1); 
           }
        }catch(Exception e){
            ok = false;
            e.printStackTrace();
        }
        System.out.println("Current KEGG database is: [" + currentKeggDb + "]");
        if(currentKeggDb == null || currentKeggDb.length() == 0){
            throw new IllegalStateException("Oh no! We don't know the name of the current KEGG database!!!!!");
        }
        ok = execute("USE " + currentKeggDb);
        return ok;
    }

    /**
     * @param mysql_url
     *            the URL of the mySQL data base
     */
    public KeggInteractionsSource(String mysql_url) {
        super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
    }
    
    // ------------ Helper methods ----------------------//
    
    /**
     * @param taxid the NCBI taxid as a String
     * @return the "org" entry that has taxid as "fullname", or null if not found
     */
    protected String getSpeciesID (String taxid){
       String id = (String)SPECIES_CACHE.get(taxid);
       if(id == null){
           String sql = "SELECT org FROM org_taxid WHERE taxid = " + taxid;
           ResultSet rs = query(sql);
           try{
               if(rs.next()){
                   id = rs.getString(1);
                   SPECIES_CACHE.put(taxid, id);
               }
               return id;
           }catch (SQLException e){
               e.printStackTrace();
               return null;
           }//catch
       }//id == null
       return id;
    }
    
    /**
     * Protected helper method
     * 
     * @param rs contains 3 columns: <br>
     * gene1, gene2, cpd
     * @param one_edge whether pairs of genes should onlty contain 1 edge between them
     */
    protected Vector makeInteractionsVector (ResultSet rs, boolean one_edge){
       // if(one_edge){
         try{
            Vector interactions = new Vector();
            while (rs.next()) {
                String gene1 = GENE_ID_TYPE + ":" + rs.getString(1);
                String gene2 = GENE_ID_TYPE + ":" + rs.getString(2);
                String cpd = rs.getString(3);
                Hashtable inter = new Hashtable();
                inter.put(INTERACTOR_1, gene1);
                inter.put(INTERACTOR_2, gene2);
                if(one_edge){
                    inter.put(INTERACTION_TYPE,
                            (KEGG_INTERACTION_TYPE + ":" + cpd));
                }else{
                    inter.put(INTERACTION_TYPE,KEGG_INTERACTION_TYPE); 
                }
                inter.put(COMPOUND, cpd);
                inter.put(SOURCE, NAME);
                interactions.add(inter);
                if (debug && interactions.size() % 100 == 0) {
                    System.out.println("interactions = "
                            + interactions.size());
                }

            }// while rs.next
            return interactions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
//        }else{
//            try{
//                Hashtable genePairToInteraction = new Hashtable();
//                while (rs.next()) {
//                    String gene1 = rs.getString(1);
//                    String gene2 = rs.getString(2);
//                    String cpd = rs.getString(3);
//                    String genePair = gene1+"."+gene2;
//                    if(genePairToInteraction.containsKey(genePair)){
//                        Hashtable inter = (Hashtable)genePairToInteraction.get(genePair);
//                        String cpds = (String)inter.get(COMPOUND);
//                        cpds += ","+cpd;
//                        inter.put(COMPOUND,cpds);
//                    }else{
//                        Hashtable inter = new Hashtable();
//                        inter.put(INTERACTOR_1, GENE_ID_TYPE + ":" + gene1);
//                        inter.put(INTERACTOR_2, GENE_ID_TYPE + ":" + gene2);
//                        inter.put(INTERACTION_TYPE,KEGG_INTERACTION_TYPE);
//                        inter.put(COMPOUND, cpd);
//                        inter.put(SOURCE, NAME);
//                        genePairToInteraction.put(genePair,inter);
//                    }
//
//                }
//                return new Vector(genePairToInteraction.values());
                
 //           }catch(SQLException e){e.printStackTrace();}
            
 //       }
                
    
        return EMPTY_VECTOR;
    }//makeInteractionsVector
    
    /**
     * @param rs table with one column
     * @return Vector with Strings from column 1 in rs
     */
    protected Vector makeInteractorsVector (ResultSet rs){
        try{
            Vector interactors = new Vector();
            while(rs.next()){
                interactors.add(GENE_ID_TYPE + ":" + rs.getString(1));
            }//while
            return interactors;
        }catch(SQLException e){
            e.printStackTrace();
        }
        return EMPTY_VECTOR;
    }
    
    // ------------- DataSource methods -----------------//
    /**
     * @return the name of the data source, for example, "KEGG", "Prolinks", etc.
     */
    public String getDataSourceName (){
        return NAME;
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
     * source contains information (in this case, NCBI taxids)
     */
    public Vector getSupportedSpecies (){
        
        String sql = "SELECT taxid FROM org_taxid";
        ResultSet rs = query(sql);
        Vector taxid = new Vector();
        if(rs != null){
            try{
                while(rs.next()){
                    taxid.add(rs.getString(1));
                }//while rs.next
            }catch (SQLException e){
                e.printStackTrace();
            }
            
        }//if rs != null
        return taxid;
    }
    
    /**
     * 
     * @param taxid the NCBI taxid as a String
     * @return TRUE if the given taxid is supported by this data source, FALSE otherwise
     */
    public Boolean supportsSpecies (String taxid){
        if(getSpeciesID(taxid) != null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
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
    
    // --------------- InteractionsDataSource methods ---------------------//
    /**
     * @return the type of gene ID that this handler understands
     */
    public String getIDtype (){
        return GENE_ID_TYPE;
    }
    
    //------------------------ get interactions en masse --------------------
    /**
     * @param taxid
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * This class also adds these entries:<br>
     * COMPOUND-->String <br>
     * SOURCE -->String<br>
     */
    public Vector getAllInteractions (String taxid){
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        String sql = "SELECT gene1, gene2, cpd FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\"";
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs,false);
    }

    /**
     * @param taxid
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String taxid){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
        String sql = "SELECT COUNT(*) FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\"";
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
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
        
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getAllInteractions(taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return new Vector();
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getAllInteractions(taxid);
        
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        String sql;
        if(oneEdgePerCpd){
            sql = "SELECT gene1, gene2, cpd FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\" AND score <= " + threshold;
        }else{
            sql = "SELECT gene1, gene2, cpd FROM  gene_gene_score WHERE org = \"" + spID + "\" AND score <= " + threshold;
        }
        ResultSet rs = query(sql);
        
        return makeInteractionsVector(rs,oneEdgePerCpd);
    }
    
    
    /**
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String taxid, Hashtable args){
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getNumAllInteractions(taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return new Integer(0);
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getNumAllInteractions(taxid);
        
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
        
        String sql;
        if(oneEdgePerCpd)
            sql = "SELECT count(*) FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\" AND score <= " + threshold;
        else
            sql = "SELECT count(*) FROM gene_gene_score WHERE org = \"" + spID + "\" AND score <= " + threshold;
        
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
   
    /**
     * 
     * @param interactors list of gene ids with the prefix GENE_ID_TYPE:
     * @return or statement that uses gene ids without the prefix
     */
    protected String getOrStatementGene1 (Vector interactors){
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return "";
        String orStatement = "";
        String geneID;
        while(it.hasNext()){
            geneID = (String)it.next();
            int index = geneID.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneID = geneID.substring(index + GENE_ID_TYPE.length() + 1);
                if(orStatement.length() > 0){
                    orStatement += " OR gene1 = \"" + geneID + "\"";
                }else{
                    orStatement = " gene1 = \"" + geneID + "\"";
                }//else
            }//if
        }//while
        
        return orStatement;
    }
    
    /**
     * 
     * @param interactors list of gene ids with the prefix GENE_ID_TYPE:
     * @return or statement that uses gene ids without the prefix
     */
    protected String getOrStatementGene2 (Vector interactors){
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return "";
        String orStatement = "";
        String geneID;
        while(it.hasNext()){
            geneID = (String)it.next();
            int index = geneID.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneID = geneID.substring(index + GENE_ID_TYPE.length() + 1);
                if(orStatement.length() > 0){
                    orStatement += " OR gene2 = \"" + geneID + "\"";
                }else{
                    orStatement = " gene2 = \"" + geneID + "\"";
                }//else
            }//if
        }//while
        
        return orStatement;
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
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        String [] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
       
        // 1. Get the neighbors assuming the interactors are gene1
        String sql = "SELECT gene2 FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + ")"; 
        ResultSet rs = query(sql);
        Vector v1 = makeInteractorsVector(rs);
        
        // 2. Now assuming the interactors are gene2
        sql = "SELECT gene1 " + "FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[1] + ")"; 
        rs = query(sql);
        Vector v2 = makeInteractorsVector(rs);
        
        v1.removeAll(v2);
        v2.addAll(v1);
        return v2;
        
    }
    
    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String taxid){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
       
        String orStatement = getOrStatementGene1(interactors);
        if(orStatement.length() == 0) return new Integer(0);
       
        return new Integer(getFirstNeighbors(interactors,taxid).size());
    }
    
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
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        if(!args.containsKey(THRESHOLD_KEY)) return getFirstNeighbors(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        if(threshold <= 0) return EMPTY_VECTOR;
        if(threshold >= getMaxNumCompoundInteractions().intValue()) return getFirstNeighbors(interactors,taxid);
        
        String [] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
        
        String sql = "SELECT gene2 FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + ") AND score <= " + threshold; 
        ResultSet rs = query(sql);
        Vector v1 = makeInteractorsVector(rs);
        
        sql = "SELECT gene1 FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[1] + ") AND score <= " + threshold; 
        rs = query(sql);
        Vector v2 = makeInteractorsVector(rs);
    
        v1.removeAll(v2);
        v2.addAll(v1);
        return v2;
    }

    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String taxid, Hashtable args){
        if(!args.containsKey(THRESHOLD_KEY)) return getNumFirstNeighbors(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        if(threshold <= 0) return new Integer(0);
        if(threshold >= getMaxNumCompoundInteractions().intValue()) return getNumFirstNeighbors(interactors,taxid);
        
        return new Integer(getFirstNeighbors(interactors,taxid,args).size());
        
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
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        String[] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
      
        String sql = "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1] + ")"; 
        ResultSet rs = query(sql);
        return makeInteractorsVector(rs);
    }

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String taxid){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
       
        String[] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return new Integer(0);
        String sql = "SELECT COUNT(*) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1] + ")"; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
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
        
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getAdjacentInteractions(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return EMPTY_VECTOR;
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getAdjacentInteractions(interactors,taxid);
        
        String [] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
      
        String sql = null; 
        if(oneEdgePerCpd){
            sql= "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1]+
                ") AND score <= " + threshold;
        }else{
            sql= "SELECT gene1, gene2, cpd FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1]+
                ") AND score <= " + threshold;
        }
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs, oneEdgePerCpd);
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param taxid the taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of adjacent interations
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String taxid, Hashtable args){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
        
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getNumAdjacentInteractions(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return new Integer(0);
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getNumAdjacentInteractions(interactors,taxid);
        
        String [] orStatements = getOrStatementsGenes12(interactors);
        if(orStatements.length == 0) return new Integer(0);
      
        String sql;
        if(oneEdgePerCpd)
            sql = 
                    "SELECT COUNT(*) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1] + 
                    ") AND score <= " + threshold; 
        else
            sql = 
                    "SELECT COUNT(*) FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatements[0] + " OR " + orStatements[1] + 
                        ") AND score <= " + threshold;
        
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }

    //-------------------------- connecting interactions methods -----------------------

    protected String [] getOrStatementsGenes12 (Vector interactors){
        
        String [] statements = new String[2];
        String orStatement1 = "";
        String orStatement2 = "";
        statements[0] = orStatement1;
        statements[1] = orStatement2;
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return statements;
      
        
        String gene;
        while(it.hasNext()){ 
            gene = (String)it.next();
            int index = gene.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                gene = gene.substring(index + GENE_ID_TYPE.length() + 1);
                if(orStatement1.length() > 0){
                    orStatement1 += " OR gene1 = \"" + gene + "\"";
                    orStatement2 += " OR gene2 = \"" + gene + "\"";
                }else{
                    orStatement1 = " gene1 = \"" + gene + "\"";
                    orStatement2 = " gene2 = \"" + gene + "\"";
                }//else
            }//if
        }//while
        
        statements[0] = orStatement1;
        statements[1] = orStatement2;
        
        return statements;
    }
    
    
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
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        String [] ors = getOrStatementsGenes12(interactors); 
        if(ors[0].length() == 0) return EMPTY_VECTOR;
        
        String sql = "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ")"; 
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs,false);
    }
    
    /**
     * @param interactors
     * @param taxid
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String taxid){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
        String [] ors = getOrStatementsGenes12(interactors);
        if(ors[0].length() == 0) return new Integer(0);
     
        String sql = "SELECT COUNT(*) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0]+ ") AND (" + ors[1] + ")"; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
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
        String spID = getSpeciesID(taxid);
        if(spID == null) return EMPTY_VECTOR;
        
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getConnectingInteractions(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return EMPTY_VECTOR;
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getConnectingInteractions(interactors,taxid);
        
        String [] ors = getOrStatementsGenes12(interactors); 
        if(ors[0].length() == 0) return EMPTY_VECTOR;
        
        String sql = null; 
        if(oneEdgePerCpd){
            sql = "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ") AND score <= " + threshold; 
        }else{
            sql = "SELECT gene1, gene2, cpd FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ") AND score <= " + threshold;
        }
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs, oneEdgePerCpd);
        
    }
    
    /**
     * @param interactors
     * @param taxid
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String taxid, Hashtable args){
        String spID = getSpeciesID(taxid);
        if(spID == null) return new Integer(0);
        
        if(!args.containsKey(THRESHOLD_KEY) && !args.containsKey(EDGE_PER_CPD_KEY)) return getNumConnectingInteractions(interactors,taxid);
        
        int threshold = getMaxNumCompoundInteractions().intValue();
        if(args.containsKey(THRESHOLD_KEY)) threshold = ( (Integer)args.get(THRESHOLD_KEY) ).intValue();
        
        boolean oneEdgePerCpd = false;
        if(args.containsKey(EDGE_PER_CPD_KEY)) oneEdgePerCpd = ( (Boolean)args.get(EDGE_PER_CPD_KEY) ).booleanValue();
        
        if(threshold <= 0) return new Integer(0);
        if(threshold >= getMaxNumCompoundInteractions().intValue() && oneEdgePerCpd == false) return getNumConnectingInteractions(interactors,taxid);
        
        String [] ors = getOrStatementsGenes12(interactors); 
        if(ors[0].length() == 0) return new Integer(0);
        String sql;
        if(oneEdgePerCpd)
            sql = "SELECT COUNT(*) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ") AND score <= " + threshold; 
        else
            sql = "SELECT COUNT(*) FROM gene_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ") AND score <= " + threshold; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
    // ---------------------------- Additional methods -------------------------- //
    
    /**
     * @return a Hashtable from a String (comma separated list of compounds) to a String parsable as an Integer
     * that represents the score of the compounds
     */
    public Integer getMaxNumCompoundInteractions (){
        String sql = "SELECT MAX(score) FROM cpd_score";
        ResultSet rs = query(sql);
        Integer maxScore = new Integer(SQLUtils.getInt(rs));
        return maxScore;
    }
    
    /**
     * 
     * @param score the number of interactions that a compound has with genes
     * @param num_cpds_to_return the number of compounds to return that are closest (but not equal) the score
     * @return Hashtable with entries : (cpd:<id>,name)
     */
    public Hashtable getCompoundsWithScoreClosestTo (Integer score, Integer num_cpds_to_return){
        
        String sql = "SELECT cpd FROM cpd_score WHERE score < " + score + " ORDER by score DESC";
        ResultSet rs = query(sql);
        Vector cpds = new Vector();
        try{
            int limit = num_cpds_to_return.intValue();
            while(rs.next() && cpds.size() < limit) cpds.add(rs.getString(1));
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return getCompoundNames(cpds);
    }
    
    /**
     * 
     * @param compounds Vector of Strings with compound ids of the form cpd:<id>
     * @return Hashtable with entries : (cpd:<id>,name)
     */
    public Hashtable getCompoundNames (Vector compounds){
        String orStat = "";
        Iterator it = compounds.iterator();
        while(it.hasNext()){
            String compound = (String)it.next();
            if(orStat.length() == 0) 
                orStat = "cpd = \"" + compound + "\"";
            else
                orStat += " OR cpd = \"" + compound + "\"";
        }//it
        
        Hashtable table = new Hashtable();
        if(orStat.length() == 0) return table;
        String sql = "SELECT cpd, name FROM cpd_name WHERE " + orStat;
        ResultSet rs = query(sql);
        try{
            while(rs.next()) table.put(rs.getString(1),rs.getString(2));
        }catch(SQLException ex){
            ex.printStackTrace();
            return new Hashtable();
        }
        return table;
    }
    
}