package org.isb.bionet.datasource.interactions;

import java.sql.*;
import java.util.*;
import org.isb.xmlrpc.handler.db.*;
import org.isb.xmlrpc.server.MyWebServer;
import org.isb.bionet.datasource.synonyms.*;


/**
 * This class assumes:<br> 
 * - mySQL DB is the source (either local or remote)<br> 
 * - Each taxid has the following tables (suppose the taxid is S):<br>
 * S (all interactions) <br>
 * S_pp (all phylogenetic profile interactions)<br>
 * S_gn (all gene-neighbor interactions)<br>
 * S_rs (all rosetta-stone interactions)<br>
 * S_gc (all gene-cluster interactions)<br>
 * and, for each of the 4 interactions, there is also a table with lower pvals (see table "interaction_types" to find out pvalue thresholds) :<br>
 * S_pp_low <br>
 * S_gn_low <br>
 * S_rs_low <br>
 * S_gc_low <br>
 * In Prolinks, edge direction does not mean anything<br>
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class ProlinksInteractionsSource extends SQLDBHandler implements
		InteractionsDataSource {

	public static final String NAME = "Prolinks";
	public static final String GENE_ID_TYPE = SynonymsSource.PROLINKS_ID; 
	public static final String PVAL = "p";
	
	/**
	 * Phylogenetic profile interaction type
	 */
	public static final String PP = "PP";
	
	/**
	 * Gene-neighbor interaction type
	 */
	public static final String GN = "GN";
	
	/**
	 * Rosetta-Stone interaction type
	 */
	public static final String RS = "RS";
	
	/**
	 * Gene-Cluster interaction type
	 */
	public static final String GC = "GC";
	
	/**
	 * A map from an interactio type (String) to its description (String), contains all interaction types in Prolinks
	 */
	public static final Hashtable INT_TYPES = new Hashtable();
    static{
        INT_TYPES.put(PP, "phylogenetic profile");
        INT_TYPES.put(RS, "rosetta stone");
        INT_TYPES.put(GN, "gene neighbor");
        INT_TYPES.put(GC, "gene cluster");
    }
	
	/**
	 * A map from method of interaction (one of PP,GN,RS,GC) to a
	 * p-value threshold, interactions with pval <= to the threshold
	 * are located in tables with '_low' terminations
	 */
	protected static Map methodThresholds;
	
	/**
	 * A Map from taxid name to table name
	 */
	protected static Map cachedTableNames;

	/**
	 * Empty constructor
	 */
	public ProlinksInteractionsSource() {
        super(SQLDBHandler.MYSQL_JDBC_DRIVER);
        makeConnection(MyWebServer.PROPERTIES.getProperty(JDBC_URL_PROPERTY_KEY));
        initialize();
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
        ResultSet rs = query("SELECT dbname FROM db_name WHERE db=\"prolinks\"");
        String currentProlinksDb = null;
        try{
           if(rs.next()){
               currentProlinksDb = rs.getString(1); 
           }
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.println("Current Prolinks database is: [" + currentProlinksDb + "]");
        if(currentProlinksDb == null || currentProlinksDb.length() == 0){
            throw new IllegalStateException("Oh no! We don't know the name of the current Prolinks database!!!!!");
        }
        ok = execute("USE " + currentProlinksDb);
        return ok;
    }

	/**
	 * @param mysql_url
	 *            the URL of the mySQL data base
	 */
	public ProlinksInteractionsSource(String mysql_url) {
		super(mysql_url, SQLDBHandler.MYSQL_JDBC_DRIVER);
		initialize();
	}// ProlinksInteractionsSource

	/**
	 * Finds the big taxid, and initilizes the method thresholds map
	 */
	protected void initialize() {
		String sql = "SELECT * FROM interaction_types";
		ResultSet rs = query(sql);
		ProlinksInteractionsSource.methodThresholds = new Hashtable();
		ProlinksInteractionsSource.INT_TYPES.clear();
        if (rs != null) {
			try {
				while (rs.next()) {
					String method = rs.getString(1);
					double pval = rs.getFloat(2);
                     String desc = rs.getString(3);
					ProlinksInteractionsSource.methodThresholds.put(method,
							new Double(pval));
                    ProlinksInteractionsSource.INT_TYPES.put(method, desc);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		ProlinksInteractionsSource.cachedTableNames =  new Hashtable();
	}// initialize
	
	/**
	 * @param taxid the taxid
	 * @return the name of the table or view for the given taxid, or null if not found
	 */
	public String getTableName (String taxid){
		
		String tableName = (String)ProlinksInteractionsSource.cachedTableNames.get(taxid);
		
		if(tableName != null)
			return tableName;
		
		String sql = "SELECT tablename FROM species WHERE taxid = " + taxid;
		ResultSet rs = query(sql);
		
		try{
			if(rs.next()){
				tableName = rs.getString(1);
				System.out.println("Table name for taxid "  + taxid + " is : [" + tableName + "]");
				ProlinksInteractionsSource.cachedTableNames.put(taxid, tableName);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return tableName;
	}
		
	/**
	 * @param taxid the taxid
	 * @param method one of PP, GN, GC, or RS
	 * @param query_pval the pvalue that is going to be used to query the table ( >1 if pval is not important )
	 * @return the name of the table, or null if not found
	 */
	public String getTableNameForMethod (String taxid, String method, double query_pval){
        String tableName = getTableName(taxid);
		if(tableName == null) return null;
         
        tableName += "_" + method.toLowerCase();
        
        double pth = ((Double)ProlinksInteractionsSource.methodThresholds.get(method)).doubleValue();
         if(query_pval <= pth){
			tableName = tableName + "_low";
		}
		
         return tableName;
	}


	// Methods implementing DataSource interface:
	/**
	 * @return String the type of id that this handler understands
	 */
	public String getIDtype () {
		return GENE_ID_TYPE;
	}// getIDTypes

	/**
	 * @return the name of the data source, for example, "KEGG", "Prolinks",
	 *         etc.
	 */
	public String getDataSourceName() {
		return NAME;
	}// getDataSourceName

	/**
	 * @return the type of backend implementation (how requests to the data
	 *         source are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB,
	 *         MEMORY, MIXED
	 */
	public String getBackendType() {
		// need to figure out if mySQL DB is running locally or remotely
		return ""; // for now
	}

	/**
	 * @return a Vector of Strings representing the taxid for which the data
	 *         source contains information
	 */
	public Vector getSupportedSpecies() {
		String sql = "SELECT DISTINCT(taxid) FROM species";
		ResultSet rs = query(sql);
		if (rs == null) {
			return EMPTY_VECTOR;
		}
		Vector taxid = new Vector();
		try {
			while (rs.next()) {
				String sp = rs.getString(1);
				taxid.add(sp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return taxid;
		}
		return taxid;
	}
    
    /**
     * 
     * @param taxid
     * @return TRUE if the given taxid is supported by this data source, FALSE otherwise
     */
    public Boolean supportsSpecies (String taxid){
        if(getTableName(taxid) != null){
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

	/**
	 * @return a String denoting the version of the data source (could be a
	 *         release date, a version number, etc).
	 */

	public String getVersion() {
		// TODO:
		// this is a problem because we connected to prolinks not metainfo!!!
		// db table should contains meta-information like this
		// - Possible solution: MetaInfoHandler???
		// String sql = "SELECT timestamp FROM when_updated WHERE
		// db=\"metainfo\"";
		// ResultSet rs = query(sql);
		// if(rs == null)
		// return "ERROR";
		// try{
		// if(rs.last()){
		// Timestamp ts = rs.getTimestamp(1);
		// return ts.toString();
		// }
		// }catch (SQLException e){
		// e.printStackTrace();
		// return "ERROR";
		// }
		// return "ERROR";
		return "NO VERSION INFO"; // for now
	}

	/**
	 * @return boolean whether or not this data source requires a password from
	 *         the user in order to access it
	 */
	public Boolean requiresPassword() {
		return Boolean.FALSE;
	}
    
     /**
     * @return Boolean.TRUE always, since this data source does not require a password
     */
    public Boolean authenticate (String userName, String password){
        return Boolean.TRUE;
    }


	// Methods implementing InteractionsDataSource interface:
    
    /**
     * @return an or statement
     */
    protected String getOrStatementA (Vector interactors){
        
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return "";
        String orStatement = "";
        while(it.hasNext()){
            String geneID = (String)it.next();
            int index = geneID.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneID = geneID.substring(index + (GENE_ID_TYPE.length() + 1));
                if(orStatement.length() > 0){
                    orStatement += " OR gene_id_a = " + geneID;
                }else{
                    orStatement = " gene_id_a = " + geneID;
                }//else
            }//if
        }//while it.hasNext
        return orStatement;
    }
    
    /**
     * @return an or statement
     */
    protected String getOrStatementB (Vector interactors){
        
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return "";
        String orStatement = "";
        while(it.hasNext()){
            String geneID = (String)it.next();
            int index = geneID.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneID = geneID.substring(index + (GENE_ID_TYPE.length() + 1));
                if(orStatement.length() > 0){
                    orStatement += " OR gene_id_b = " + geneID;
                }else{
                    orStatement = " gene_id_b = " + geneID;
                }//else
            }//if
        }//while it.hasNext
        return orStatement;
    }
    
    /**
     * 
     * @param interactors
     * @return array of Strings of size 2, [0] contains orStatement_a, [1] contains orStatement_b
     */
    protected String[] getOrStatementsAB (Vector interactors){
        
        String [] statements = new String[2];
        statements[0] = "";
        statements[1] = "";
        
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return  statements;
        String geneName = (String)it.next();
        String orStatement_a = "";
        String orStatement_b = "";
        int index = geneName.indexOf(GENE_ID_TYPE + ":");
        if(index >= 0){
            geneName = geneName.substring(index + GENE_ID_TYPE.length() + 1);
            orStatement_a = " gene_id_a = " + geneName;
            orStatement_b = " gene_id_b = " + geneName;
        }
        
        while(it.hasNext()){
            geneName = (String)it.next();
            index = geneName.indexOf(GENE_ID_TYPE + ":");
            if(index >= 0){
                geneName = geneName.substring(index + GENE_ID_TYPE.length() + 1);
                orStatement_a += " OR gene_id_a = " + geneName;
                orStatement_b += " OR gene_id_b = " + geneName;
            }
            
        }//while it.hasNext
        statements[0] = orStatement_a;
        statements[1] = orStatement_b;
        return statements;
    }
	
	/**
	 * Protected helper method
	 * 
	 * @param rs contains 3 columns: <br>
	 * gene_id_a, gene_id_b, and p
	 * @param method one of PP, RS, GN, or GC 
	 */
	protected Vector makeInteractionsVector (ResultSet rs, String method){
		
		try{
			Vector interactions = new Vector();
			while (rs.next()) {
				String gene1 = GENE_ID_TYPE + ":" + rs.getString(1);
				String gene2 = GENE_ID_TYPE + ":" + rs.getString(2);
				double pval = rs.getDouble(3);
				Hashtable inter = new Hashtable();
				inter.put(INTERACTOR_1, gene1);
				inter.put(INTERACTOR_2, gene2);
				// if method == PP, then set the type to PhP to distinguish it from protein-protein
                 if(method.equals(PP)){method = "PhP";}
				inter.put(INTERACTION_TYPE, method);
				inter.put(PVAL, new Double(pval));
				inter.put(SOURCE, ProlinksInteractionsSource.NAME);
				interactions.add(inter);
				if(debug && interactions.size() % 10000 == 0){
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
	 * Protected helper method
	 *
	 * @param ResultSet contains the followign columns: gene_id_a, gene_id_b, p, method (in that order)
	 * @return a Vector of Hashtables
	 */
	protected Vector makeInteractionsVector (ResultSet rs){
		
		try{
			Vector allInteractions =  new Vector();
			while(rs.next()){
				String geneA = GENE_ID_TYPE + ":" + rs.getString(1);
				String geneB = GENE_ID_TYPE + ":" + rs.getString(2);
				double p = rs.getDouble(3);
				String method = rs.getString(4);
				Hashtable interaction = new Hashtable();
				interaction.put(INTERACTOR_1, geneA);
				interaction.put(INTERACTOR_2, geneB);
				interaction.put(PVAL, new Double(p));
				interaction.put(INTERACTION_TYPE, method);
				interaction.put(SOURCE, getDataSourceName());
				allInteractions.add(interaction);
				if(debug && allInteractions.size() % 10000 == 0){
					System.out.println("interactions = "
							+ allInteractions.size());
				}
			}// while
				
			return allInteractions;
		}catch (SQLException e){
			e.printStackTrace();
		}
		return EMPTY_VECTOR;
	}
	
	
	/**
	 * Protected helper method
	 * 
	 * @param rs ResultSet of one column with Prolinks gene IDs
	 * @return a Vector with the gene ids
	 */
	protected Vector makeInteractorsVector(ResultSet rs){
		try{
			Vector interactors = new Vector();
			while(rs.next()){
				String gene = GENE_ID_TYPE + ":" + rs.getString(1);
				interactors.add(gene);
				if(debug && interactors.size() % 10000 == 0){
					System.out.println("interactors = " + interactors.size());
				}
			}// while rs.next
			return interactors;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return EMPTY_VECTOR;
	}
	
	// ------------------------ get interactions en masse --------------------
	/**
	 * @param taxid
	 *            NCBI taxid, parsable as Integer
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction and is required to contain the following entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         PVAL --> Double <br>
	 *         SOURCE --> String <br>
	 */
	// TODO: This is giving us a OutOfMemoryError
	// Will need to add paging, or something like that
	public Vector getAllInteractions(String taxid) {
		String tableName = getTableName(taxid);
        if(tableName == null) return EMPTY_VECTOR;
        String sql = "SELECT gene_id_a,gene_id_b,p,method FROM " + tableName;
		ResultSet rs = query(sql);
		return makeInteractionsVector(rs);
	}
    
    /**
     * @param taxid
     *            NCBI taxid, parsable as Integer
     * @return the number of interactions
     */
    public Integer getNumAllInteractions(String taxid) {
        String tableName = getTableName(taxid);
        if(tableName == null) return new Integer(0);
        String sql = "SELECT COUNT(*) FROM " + tableName;
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }
    
	/**
	 * @param taxid
     *            NCBI taxid, parsable as Integer
	 * @param args
	 *            this class understands the following arguments:<br>
	 *            PVAL-->Double only interactions with p-values <= PVAL will be returned <br>
	 *            INTERACTION_TYPE-->Vector of Strings which can be: PP,GN,RS,or GC, only interactions of these types will be returned <br>
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction and is required to contain the following entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         PVAL --> Double <br>
	 *         SOURCE --> String <br>
	 */
	public Vector getAllInteractions(String taxid, Hashtable args) {
		double pval = 2; // PVALS' max value is 1
		if(args.containsKey(PVAL)){
			pval = ( (Double)args.get(PVAL) ).doubleValue();
		}
		
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }

		
		if(pval >= 1 && methods.size() == 4){
			// the same as calling getAllInteractions(taxid)
			return getAllInteractions(taxid);
		}
		
		Iterator it = methods.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			String method = (String)it.next();
			String tableName = getTableNameForMethod(taxid, method, pval);
             if(tableName == null) continue;
			String sql = "SELECT gene_id_a, gene_id_b, p FROM " + tableName;
			if(pval < 1){
				sql = sql + " WHERE p <= " + pval;
			}
			ResultSet rs = query(sql);
			allInteractions.addAll(makeInteractionsVector(rs,method));
		}//while it.hasNext
		
		return allInteractions;
	}


    /**
     * @param taxid
     * @param args
     *            this class understands the following arguments:<br>
     *            PVAL-->Double only interactions with p-values <= PVAL will be returned <br>
     *            INTERACTION_TYPE-->Vector of Strings which can be: PP,GN,RS,or GC, only interactions of these types will be returned <br>
     * @return the number of interactions
     */
    public Integer getNumAllInteractions(String taxid, Hashtable args) {
        //System.out.println("ProlinksInteractionsSource.getAllInteractions (" + taxid + ", " + args + ")");
        
        double pval = 2; // PVALS' max value is 1
        if(args.containsKey(PVAL)){
            pval = ( (Double)args.get(PVAL) ).doubleValue();
        //  System.out.println("pval = " + pval);
        }
        
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }

        
        if(pval >= 1 && methods.size() == 4){
            // the same as calling getNumAllInteractions(taxid)
            return getNumAllInteractions(taxid);
        }
        
        
        Iterator it = methods.iterator();
        int num = 0;
        while(it.hasNext()){
            String method = (String)it.next();
            //System.out.println(method);
            String tableName = getTableNameForMethod(taxid, method, pval);
            if(tableName == null) continue;
            //System.out.println("tableName = " + tableName);
            String sql = "SELECT COUNT(*) FROM " + tableName;
            if(pval < 1){
                sql = sql + " WHERE p <= " + pval;
            }
            ResultSet rs = query(sql);
            num += SQLUtils.getInt(rs);
        }//while it.hasNext
        
        return new Integer(num);
    }
    
    
    // Method that implement InteractionsDataSource:

    //   ------------- 1st neighbor methods -------------- //

   

	/**
	 * @param interactors
	 *            a Vector of Strings (ids that the data source understands)
	 * @param taxid
	 *            the taxid
	 * @return the number of 1st neighbors
	 */
	public Integer getNumFirstNeighbors(Vector interactors, String taxid) {        
        
        String tableName = getTableName(taxid);
        if(tableName == null) return new Integer(0);
        
        Iterator it = interactors.iterator();
        if(!it.hasNext())
            return new Integer(0);
        return new Integer(getFirstNeighbors(interactors,taxid).size());
	}
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param taxid
     *            the taxid
     * @return a Vector of String ids of all the nodes that have a
     *         direct interaction with the interactors in the given input
     *         vector
     */
    public Vector getFirstNeighbors(Vector interactors, String taxid) {        
        String tableName = getTableName(taxid);
        if(tableName == null) return EMPTY_VECTOR;
        
        Iterator it = interactors.iterator();
        if(!it.hasNext()) return EMPTY_VECTOR;
        
        String [] orStatements = getOrStatementsAB(interactors);
       
        if(orStatements.length == 0) return EMPTY_VECTOR;
        String sql = "SELECT gene_id_b FROM " + tableName + " WHERE " + orStatements[0];
        ResultSet rs = query(sql);
        Vector v1 = makeInteractorsVector(rs);
        
        sql = "SELECT gene_id_a FROM " + tableName + " WHERE " + orStatements[1];
        rs = query(sql);
        Vector v2 = makeInteractorsVector(rs);
    
        v1.removeAll(v2);
        v2.addAll(v1);
        return v2;
    
    }

	/**
	 * @param interactor
	 *            a Vector of Strings (ids that the data source understands)
	 * @param taxid
	 *            the taxid
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions, etc)
	 * @return a Vector of String ids of all the nodes that have a
	 *         direct interaction with the interactors in the given input
	 *         vector
	 */
	public Vector getFirstNeighbors(Vector interactors, String taxid,
			    Hashtable args) {
        
        Iterator it = interactors.iterator();
        if(!it.hasNext())
            return new Vector();
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ( (Double)args.get(PVAL) ).doubleValue();
        }
        //System.out.println(pval);
        
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }
    
        if(pval >= 1 && methods.size() == 4){
            // Just like calling with no args
            return getFirstNeighbors(interactors, taxid);
        }
        
        String [] orStatements = getOrStatementsAB(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
        
        String sql;
        Set neighbors = new HashSet();
        
        if(pval >= 1){
            
            // No pval restriction
            if(methods.size() < 4){
                // Methods are specified
                it = methods.iterator();
                while(it.hasNext()){
                    String method = (String)it.next();
                    String tableName = getTableNameForMethod(taxid, method, pval);
                    if(tableName == null) continue;
                    sql = "SELECT gene_id_b FROM " + tableName + " WHERE " + orStatements[0];
                    ResultSet rs = query(sql);
                    neighbors.addAll(makeInteractorsVector(rs));
                    sql = "SELECT gene_id_a FROM " + tableName + " WHERE " + orStatements[1];
                    rs = query(sql);
                    neighbors.addAll(makeInteractorsVector(rs));
                }//while it.hasNext
            }
        }else{
        
            // Pval restriction
            it = methods.iterator();
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                sql = "SELECT gene_id_b FROM " + tableName + " WHERE p <= " + pval + " AND (" + orStatements[0] + ")";
                ResultSet rs = query(sql);
                neighbors.addAll(makeInteractorsVector(rs));
                sql = "SELECT gene_id_b FROM " + tableName + " WHERE p <= " + pval + " AND (" + orStatements[1] + ")";
                rs = query(sql);
                neighbors.addAll(makeInteractorsVector(rs));
            }//while
        }// pval <= 1
        
        return new Vector(neighbors);
	}
    
    /**
     * @param interactor
     *            a Vector of Strings (ids that the data source understands)
     * @param taxid
     *            the taxid
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions, etc)
     * @return the number of 1st neighbors
     */
    public Integer getNumFirstNeighbors(Vector interactors, String taxid,
                Hashtable args) {
        
        Iterator it = interactors.iterator();
        if(!it.hasNext())
            return new Integer(0);
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ( (Double)args.get(PVAL) ).doubleValue();
        }
        //System.out.println(pval);
        
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }
    
        if(pval >= 1 && methods.size() == 4){
            // Just like calling with no args
            return getNumFirstNeighbors(interactors, taxid);
        }
        return new Integer( getFirstNeighbors(interactors,taxid,args).size() );
    }

	/**
	 * @param interactors
	 *            a Vector of Strings (ids that the data source understands)
	 * @param taxid
	 *            the taxid
	 * @return a Vector of Hashtables, each hash contains information
	 *         about an interaction (they are required to contain the following
	 *         entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables.<br>
	 *         The input and output vectors are parallel.
	 */
	public Vector getAdjacentInteractions(Vector interactors, String taxid) {
        String tableName = getTableName(taxid);
        if(tableName == null) return EMPTY_VECTOR;
        if(interactors.size() == 0) return EMPTY_VECTOR;
         
        String [] orStatements = getOrStatementsAB(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
        
        String sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE " + orStatements[0] + " OR " + orStatements[1];
        ResultSet rs = query(sql);
        return makeInteractionsVector(rs);
	}
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param taxid
     *            the taxid
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String taxid) {
        String tableName = getTableName(taxid);
        if(tableName == null) return new Integer(0);
        if(interactors.size() == 0) return new Integer(0);
        
        String [] orStatements = getOrStatementsAB(interactors);
        if(orStatements.length == 0) return new Integer(0);
        
        String sql = "SELECT COUNT(gene_id_a, gene_id_b) FROM " + tableName + " WHERE " + orStatements[0] + " OR " + orStatements[1];
        ResultSet rs = query(sql);
        return new Integer(SQLUtils.getInt(rs));
    }


	/**
	 * @param interactor
	 *            a Vector of Strings (ids that the data source understands)
	 * @param taxid
	 *            the taxid
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions only, etc)
	 * @return a Vector of Hashtables, each hash contains information
	 *         about an interaction (they are required to contain the following
	 *         entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables.<br>
	 *         The input and output vectors are parallel.
	 */
	public Vector getAdjacentInteractions(Vector interactors, String taxid,
			Hashtable args) {
		
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
        String [] orStatements = getOrStatementsAB(interactors);
        if(orStatements.length == 0) return EMPTY_VECTOR;
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ((Double)args.get(PVAL)).doubleValue();
        }
        
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }
        
        if(pval >= 1 && methods.size() == 4){
            return getAdjacentInteractions(interactors, taxid);
        }
        
        String sql;
        if(pval < 1){
            Iterator it = methods.iterator();
            Vector interactions = new Vector();
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE p <= " + pval + " AND (" + orStatements[0] + " OR " + orStatements[1] + ")";
                ResultSet rs = query(sql);
                interactions.addAll(makeInteractionsVector(rs));
            }//while
            return interactions;
        }else{
            // No pval restriction
            // Method restriction!
            Iterator it = methods.iterator();
            Vector interactions = new Vector();
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + 
                " WHERE " + orStatements[0] + " OR " + orStatements[1];
                ResultSet rs = query(sql);
                interactions.addAll(makeInteractionsVector(rs));
            }//while
            return interactions;             
        }//else
	}
    
    /**
     * @param interactor
     *            a Vector of Strings (ids that the data source understands)
     * @param taxid
     *            the taxid
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @return the number of interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String taxid,
                Hashtable args) {
        
        if(interactors.size() == 0){
            return new Integer(0);
        }
        
        String [] orStatements = getOrStatementsAB(interactors);
        if(orStatements.length == 0) return new Integer(0);
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ((Double)args.get(PVAL)).doubleValue();
        }
        
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
           methods = new Vector(INT_TYPES.keySet());
        }
        
        if(pval >= 1 && methods.size() == 4){
            return getNumAdjacentInteractions(interactors, taxid);
        }
        
        String sql;
        if(pval < 1){
            Iterator it = methods.iterator();
           int num = 0;
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                sql = "SELECT COUNT(*) FROM " + tableName + " WHERE p <= " + pval + " AND (" + orStatements[0] + " OR " + orStatements[1] + ")";
                ResultSet rs = query(sql);
                num += SQLUtils.getInt(rs);
            }//while
            return new Integer(num);
        }else{
            // No pval restriction
            // Method restriction!
            Iterator it = methods.iterator();
            int num = 0;
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                sql = "SELECT COUNT(*) FROM " + tableName + 
                " WHERE " + orStatements[0] + " OR " + orStatements[1];
                ResultSet rs = query(sql);
                num += SQLUtils.getInt(rs);
            }//while
            return new Integer(num);             
        }//else
    }

	// ------------ connecting interactions methods --------------//

	
	/**
	 * @param interactors
	 * @param taxid
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction between the two interactors, each hash contains these
	 *         entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getConnectingInteractions(Vector interactors, String taxid) {
        
        String tableName = getTableName(taxid);
        if(tableName == null) return EMPTY_VECTOR;
        if(interactors.size() == 0) return EMPTY_VECTOR;
        
       String [] ors = getOrStatementsAB(interactors);
       if(ors[0].length() == 0) return EMPTY_VECTOR;
        
        String sql = 
           "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE (" + ors[0] + " ) AND (" + ors[1] + ")";
        ResultSet rs = query(sql);
        
        return makeInteractionsVector(rs);
	}
    
    /**
     * @param interactors
     * @param taxid
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String taxid) {
        
        String tableName = getTableName(taxid);
        if(tableName == null) return new Integer(0);
        if(interactors.size() == 0) return new Integer(0);
        
        String [] ors = getOrStatementsAB(interactors);
        if(ors[0].length() == 0) return new Integer(0);
        
        String sql = 
           "SELECT COUNT(*) FROM " + tableName + " WHERE (" + ors[0] + " ) AND (" + ors[1] + ")";
        ResultSet rs = query(sql);
        
        return new Integer(SQLUtils.getInt(rs));
    }

	/**
	 * @param interactors
	 * @param taxid
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions only, etc)
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction between the two interactors, each hash contains these
	 *         entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getConnectingInteractions(Vector interactors, String taxid,
	        Hashtable args) {

        if(interactors.size() == 0){
            return new Vector();
        }
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ((Double)args.get(PVAL)).doubleValue();
        }
  
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
            methods = new Vector(INT_TYPES.keySet());
        }
        
        if(pval >= 1 && methods.size() == 4){
            return getConnectingInteractions(interactors, taxid);
        }
        
        String [] ors = getOrStatementsAB(interactors);
        if(ors[0].length() == 0) return EMPTY_VECTOR;
        
        if(pval >= 1){
            // pval does not matter
           Iterator it = methods.iterator();
            Vector interactions = new Vector();
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                String sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE (" + ors[0] + " ) AND (" + ors[1]+ ")";
                ResultSet rs = query(sql);
                interactions.addAll(makeInteractionsVector(rs, method));
            }//while
            return interactions;
        }else{
            // pval matters
            Iterator it = methods.iterator();
            Vector interactions = new Vector();
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                String sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE p <= " + pval + " AND (" + ors[0] + " ) AND (" + ors[1] + ")";
                ResultSet rs = query(sql);
                interactions.addAll(makeInteractionsVector(rs, method));
            }//while
            return interactions;
        }//else
	}
    
    /**
     * @param interactors
     * @param taxid
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String taxid,
            Hashtable args) {

        
        if(interactors.size() == 0){
            return new Integer(0);
        }
        
        double pval = 2;
        if(args.containsKey(PVAL)){
            pval = ((Double)args.get(PVAL)).doubleValue();
        }
  
        Vector methods;
        if(args.containsKey(INTERACTION_TYPE)){
            methods = (Vector)args.get(INTERACTION_TYPE);
        }else{
            methods = new Vector(INT_TYPES.keySet());
        }
        
        if(pval >= 1 && methods.size() == 4){
            return getNumConnectingInteractions(interactors, taxid);
        }
        
        String [] ors = getOrStatementsAB(interactors);
        if(ors[0].length() == 0) return new Integer(0);
        
        if(pval >= 1){
            // pval does not matter
            Iterator it = methods.iterator();
            int num = 0;
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE (" + ors[0] + " ) AND (" + ors[1] + ")";
                ResultSet rs = query(sql);
                num += SQLUtils.getInt(rs);
            }//while
            return new Integer(num/2);
        }else{
            // pval matters
            Iterator it = methods.iterator();
            int num = 0;
            while(it.hasNext()){
                String method = (String)it.next();
                String tableName = getTableNameForMethod(taxid, method, pval);
                if(tableName == null) continue;
                String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE p <= " + pval + " AND (" + ors[0] + " ) AND (" + ors[1] + ")";
                ResultSet rs = query(sql);
                num+= SQLUtils.getInt(rs);
            }//while
            return new Integer(num);
        }//else
    }
	
	/**
	 * Tests methods in this class.
	 */
	public Vector test (){
		return EMPTY_VECTOR;
	}//test
	

}// ProlinksInteractionsSource
