package org.isb.bionet.datasource.interactions;

import java.util.*;

import org.isb.xmlrpc.handler.db.SQLDBHandler;
import org.isb.bionet.datasource.synonyms.*;
import java.sql.*;
import org.isb.xmlrpc.handler.db.*;

public class KeggInteractionsSource extends SQLDBHandler implements InteractionsDataSource {
   
    public static final String NAME = "KEGG";
    public static final String GENE_ID_TYPE = SynonymsSource.KEGG_ID;
    public static final String KEGG_INTERACTION_TYPE = "sharedCompound";
    public static final String COMPOUND = "compound";
    /**
     * A Map that contains "fullname" species Strings mapped to the ids that KEGG uses to identify species
     */
    protected static Map SPECIES_CACHE = new Hashtable();
    protected boolean debug = true;
     
    /**
     * Empty constructor
     */
    public KeggInteractionsSource() {
        // TODO: Remove, this should be read from somewhere!!!
        this("jdbc:mysql://biounder.kaist.ac.kr/kegg?user=bioinfo&password=qkdldhWkd");
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
     * @param species_fullname the "fullname" entry in "org_name" table
     * @return the "org" entry that has species_fullname as "fullname", or null if not found
     */
    protected String getSpeciesID (String species_fullname){
       String id = (String)SPECIES_CACHE.get(species_fullname);
       if(id == null){
           String sql = "SELECT org FROM org_name WHERE fullname = \"" + species_fullname + "\"";
           ResultSet rs = query(sql);
           try{
               if(rs.next()){
                   id = rs.getString(1);
                   SPECIES_CACHE.put(species_fullname, id);
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
     */
    protected Vector makeInteractionsVector (ResultSet rs){
        
        try{
            Vector interactions = new Vector();
            while (rs.next()) {
                String gene1 = GENE_ID_TYPE + ":" + rs.getString(1);
                String gene2 = GENE_ID_TYPE + ":" + rs.getString(2);
                String cpd = rs.getString(3);
                Hashtable inter = new Hashtable();
                inter.put(INTERACTOR_1, gene1);
                inter.put(INTERACTOR_2, gene2);
                inter.put(INTERACTION_TYPE, (KEGG_INTERACTION_TYPE + ":"+ cpd));
                inter.put(COMPOUND, cpd);
                inter.put(SOURCE, NAME);
                interactions.add(inter);
                if(debug && interactions.size() % 100 == 0){
                    System.out.println("interactions = "
                            + interactions.size());
                }
                
            }// while rs.next
            return interactions;
        }catch (SQLException e){
            e.printStackTrace();
        }
    
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
     * @return a Vector of Strings representing the species for which the data
     * source contains information
     */
    public Vector getSupportedSpecies (){
        
        String sql = "SELECT fullname FROM org_name";
        ResultSet rs = query(sql);
        Vector species = new Vector();
        if(rs != null){
            try{
                while(rs.next()){
                    species.add(rs.getString(1));
                }//while rs.next
            }catch (SQLException e){
                e.printStackTrace();
            }
            
        }//if rs != null
        return species;
    }
    
    /**
     * 
     * @param species
     * @return TRUE if the given species is supported by this data source, FALSE otherwise
     */
    public Boolean supportsSpecies (String species){
        if(getSpeciesID(species) != null){
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
    public boolean requiresPassword (){
        return false;
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
     * @param species_fullname
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * This class also adds these entries:<br>
     * COMPOUND-->String <br>
     * SOURCE -->String<br>
     */
    public Vector getAllInteractions (String species_fullname){
        String spID = getSpeciesID(species_fullname);
        if(spID == null) return EMPTY_VECTOR;
        String sql = "SELECT gene1, gene2, cpd FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\"";
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs);
    }

    /**
     * @param species_fullname
     * @return the number of interactions
     */
    public Integer getNumAllInteractions (String species_fullname){
        String spID = getSpeciesID(species_fullname);
        if(spID == null) return new Integer(0);
        String sql = "SELECT COUNT(*) FROM  gene_cpd_gene_score WHERE org = \"" + spID + "\"";
        ResultSet rs = query(sql);
        // Contains both directions...
        return new Integer(SQLUtils.getInt(rs)/2);
    }
    
    /**
     * @param species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction and is required to contain the following entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables
     */
    //TODO: Arguments?
    public Vector getAllInteractions (String species, Hashtable args){
        return getAllInteractions(species);
    }
    
    /**
     * @param species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactions
     */
    //TODO: Arguments?
    public Integer getNumAllInteractions (String species, Hashtable args){
        return getNumAllInteractions(species);
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
        String geneID = (String)it.next();
        int index = geneID.indexOf(GENE_ID_TYPE + ":");
        if(index >= 0){
            geneID = geneID.substring(index + GENE_ID_TYPE.length() + 1);
            orStatement = "gene1 = \"" + geneID + "\"";
        }
        
        while(it.hasNext()){
            geneID = (String)it.next();
            index = geneID.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneID = geneID.substring(index + GENE_ID_TYPE.length() + 1);
                orStatement += " OR gene1 = \"" + geneID + "\"";
            }
        }
        
        return orStatement;
    }
    
    //-------------------------- 1st neighbor methods ---------------------------

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @return a Vectors of String ids of all the nodes that
     * have a direct interaction with the interactors in the given input vector, positions
     * in the input and output vectors are matched (parallel vectors)
     */
    public Vector getFirstNeighbors (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return EMPTY_VECTOR;
        
        String orStatement = getOrStatementGene1(interactors);
        if(orStatement.length() == 0) return EMPTY_VECTOR;
       
        String sql = "SELECT gene2 FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatement + ")"; 
        ResultSet rs = query(sql);
        return makeInteractorsVector(rs);
    }
    
    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @return the number of interactors
     */
    public Integer getNumFirstNeighbors (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return new Integer(0);
       
        String orStatement = getOrStatementGene1(interactors);
        if(orStatement.length() == 0) return new Integer(0);
        
        String sql = "SELECT COUNT(gene2) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatement + ")"; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return a Vector of String ids of all the nodes that
     * have a direct interaction with the interactors in the given input vector, positions
     * in the input and output vectors are matched (parallel vectors)
     */
    //TODO: Arguments?
    public Vector getFirstNeighbors (Vector interactors, String species, Hashtable args){
        return getFirstNeighbors(interactors, species);
    }

    
    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions, etc)
     * @return the number of interactors
     */
    //TODO: Arguments?
    public Integer getNumFirstNeighbors (Vector interactors, String species, Hashtable args){
        return getNumFirstNeighbors(interactors, species);
    }

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction (they are required to contain the following entries:)<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables.<br>
     * The input and output vectors are parallel.
     */
    public Vector getAdjacentInteractions (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return EMPTY_VECTOR;
        
        String orStatement = getOrStatementGene1(interactors);
        if(orStatement.length() == 0) return EMPTY_VECTOR;
      
        String sql = "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatement + ")"; 
        ResultSet rs = query(sql);
        return makeInteractorsVector(rs);
    }

    /**
     * @param interactors a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return new Integer(0);
       
        String orStatement = getOrStatementGene1(interactors);
        if(orStatement.length() == 0) return new Integer(0);
        
        String sql = "SELECT gene2 FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + orStatement + ")"; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param species the species
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
    //TODO: Arguments?
    public Vector getAdjacentInteractions (Vector interactors, String species, Hashtable args){
        return getAdjacentInteractions(interactors, species);
    }

    /**
     * @param interactor a Vector of Strings (ids that the data source understands)
     * @param species the species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of adjacent interations
     */
    //TODO: Arguments?
    public Integer getNumAdjacentInteractions (Vector interactors, String species, Hashtable args){
        return getNumAdjacentInteractions(interactors, species);
    }

    //-------------------------- connecting interactions methods -----------------------

    protected String [] getOrStatementsGenes12 (Vector interactors){
        
        String [] statements = new String[2];
        statements[0] = "";
        statements[1] = "";
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return statements;
        
        String gene = (String)it.next();
        int index = gene.indexOf(GENE_ID_TYPE + ":");
        String orStatement1 = "";
        String orStatement2 = "";
        
        if(index >= 0){
            gene = gene.substring(index + GENE_ID_TYPE.length() + 1);
            orStatement1 = "gene1 = \"" + gene + "\"";
            orStatement2 = "gene2 = \"" + gene + "\"";
        }
        
        while(it.hasNext()){
         
            gene = (String)it.next();
            index = gene.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                gene = gene.substring(index + GENE_ID_TYPE.length() + 1);
                orStatement1 += " OR gene1 = \"" + gene + "\"";
                orStatement2 += " OR gene2 = \"" + gene + "\"";
            }
        }
            
        return statements;
    }
    
    
    /**
     * @param interactors
     * @param species
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction between the two interactors, each hash contains these entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables 
     */
    public Vector getConnectingInteractions (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return EMPTY_VECTOR;
        
        String [] ors = getOrStatementsGenes12(interactors); 
        if(ors[0].length() == 0) return EMPTY_VECTOR;
        
        String sql = "SELECT gene1, gene2, cpd FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0] + ") AND (" + ors[1] + ")"; 
        ResultSet rs = query(sql);
        return makeInteractorsVector(rs);
    }
    
    /**
     * @param interactors
     * @param species
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions (Vector interactors, String species){
        String spID = getSpeciesID(species);
        if(spID == null) return new Integer(0);
        String [] ors = getOrStatementsGenes12(interactors); 
        if(ors[0].length() == 0) return new Integer(0);
     
        String sql = "SELECT COUNT(*) FROM gene_cpd_gene_score WHERE org = \"" + spID + "\"" + " AND (" + ors[0]+ ") AND (" + ors[1] + ")"; 
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs)/2);
    }
    
    /**
     * @param interactors
     * @param species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return a Vector of Hashtables, each hash contains information about an
     * interaction between the two interactors, each hash contains these entries:<br>
     * INTERACTOR_1 --> String <br>
     * INTERACTOR_2 --> String <br>
     * INTERACTION_TYPE -->String <br>
     * Each implementing class can add additional entries to the Hashtables 
     */
    //TODO: Arguments?
    public Vector getConnectingInteractions (Vector interactors, String species, Hashtable args){
        return getConnectingInteractions(interactors, species);
    }
    
    /**
     * @param interactors
     * @param species
     * @param args a table of String->Object entries that the implementing
     * class understands (for example, p-value thresholds, directed interactions only, etc)
     * @return the number of connecting interactions
     */
    //TODO: Arguments?
    public Integer getNumConnectingInteractions (Vector interactors, String species, Hashtable args){
        return getNumConnectingInteractions(interactors,species);
    }
     
}