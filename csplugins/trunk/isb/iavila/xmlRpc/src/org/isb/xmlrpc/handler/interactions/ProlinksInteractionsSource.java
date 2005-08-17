package org.isb.xmlrpc.handler.interactions;

import java.sql.*;
import java.util.*;
import org.isb.xmlrpc.handler.db.*;


/**
 * This class assumes:<br> 
 * - mySQL DB is the source (either local or remote)<br> 
 * - Each "big" species (specified in table "big_species") has the following tables (suppose the species is S):<br>
 * S (all interactions) <br>
 * S_pp (all protein-protein interactions)<br>
 * S_gn (all gene-neighbor interactions)<br>
 * S_rs (all rosetta-stone interactions)<br>
 * S_gc (all gene-cluster interactions)<br>
 * and, for each of the 4 interactions, there is also a table with lower pvals (see table "method_threshold") :<br>
 * S_pp_low <br>
 * S_gn_low <br>
 * S_rs_low <br>
 * S_gc_low <br>
 * The species that are not big contain their interactions in the "prolinks" table, which also had the divisions above. <br>
 * TODO: Describe tables
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class ProlinksInteractionsSource extends SQLDBHandler implements
		InteractionsDataSource {

	/**
	 * This is the same name as in the MyQSL DB
	 */
	public static final String NAME = "prolinks";
	public static final String GENE_ID_TYPE = "ProlinksID";
	public static final String PVAL = "p";
	
	/**
	 * Protein-protein interaction type
	 */
	public static final String PP = "pp";
	
	/**
	 * Gene-neighbor interaction type
	 */
	public static final String GN = "gn";
	
	/**
	 * Rosetta-Stone interaction type
	 */
	public static final String RS = "rs";
	
	/**
	 * Gene-Cluster interaction type
	 */
	public static final String GC = "gc";
	
	/**
	 * All the interaction types in Prolinks
	 */
	public static final Vector INT_TYPES = new Vector();
	static {
		INT_TYPES.add(PP);
		INT_TYPES.add(GN);
		INT_TYPES.add(RS);
		INT_TYPES.add(GC);
	}

	/**
	 * The species whose interactions are containes in their own separate tables
	 */
	public static String[] bigSpecies;

	/**
	 * A map from method of interaction (one of PP,GN,RS,GC) to a
	 * p-value threshold, interactions with pval <= to the threshold
	 * are located in tables with '_low' terminations
	 */
	protected static Map methodThresholds;
	

	/**
	 * Empty constructor
	 */
	public ProlinksInteractionsSource() {
		// TODO: Remove, this should be read from somewhere!!!
		this("jdbc:mysql://biounder.kaist.ac.kr/prolinks0?user=bioinfo&password=qkdldhWkd");
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
	 * Finds the big species, and initilizes the method thresholds map
	 */
	protected void initialize() {
		String sql = "SELECT species FROM big_species";
		ResultSet rs = query(sql);
		if (rs != null) {

			try {
				ArrayList bSpecies = new ArrayList();
				while (rs.next()) {
					String sp = (String) rs.getObject(1);
					bSpecies.add(sp);
				}
				ProlinksInteractionsSource.bigSpecies = (String[]) bSpecies
						.toArray(new String[bSpecies.size()]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}// rs != null

		sql = "SELECT * FROM method_threshold";
		rs = query(sql);
		ProlinksInteractionsSource.methodThresholds = new Hashtable();
		if (rs != null) {
			try {
				while (rs.next()) {
					String method = rs.getString(1);
					double pval = rs.getFloat(2);
					ProlinksInteractionsSource.methodThresholds.put(method,
							new Double(pval));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}// initialize

	/**
	 * @param species
	 *            the name of the species
	 * @return true if the given species is so big that its interactions are
	 *         divided into 2 tables
	 */
	public boolean isBigSpecies(String species) {
		if (ProlinksInteractionsSource.bigSpecies == null) {
			return false;
		}
		for (int i = 0; i < ProlinksInteractionsSource.bigSpecies.length; i++) {
			if (ProlinksInteractionsSource.bigSpecies[i].equals(species)) {
				return true;
			}
		}// for i
		return false;
	}// isBigSpecies

	// Methods implementing DataSource interface:
	/**
	 * @return a Vector of Strings that specify types of IDs that this
	 *         InteractionsDataSource accepts for example, "ORF","GI", etc.
	 */
	public Vector getIDtypes() {
		Vector ids = new Vector();
		ids.add(GENE_ID_TYPE);
		return ids;
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
	 * @return a Vector of Strings representing the species for which the data
	 *         source contains information
	 */
	public Vector getSupportedSpecies() {
		String sql = "SELECT species FROM species";
		ResultSet rs = query(sql);
		if (rs == null) {
			return EMPTY_VECTOR;
		}
		Vector species = new Vector();
		try {
			while (rs.next()) {
				String sp = (String) rs.getObject(1);
				species.add(sp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return species;
		}
		return species;
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
		return "NOT IMPLEMENTED"; // for now
	}

	/**
	 * @return boolean whether or not this data source requires a password from
	 *         the user in order to access it
	 */
	public boolean requiresPassword() {
		return false;
	}

	// Methods implementing InteractionsDataSource interface:
	
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
				String gene1 = rs.getString(1);
				String gene2 = rs.getString(2);
				double pval = rs.getDouble(3);
				Hashtable inter = new Hashtable();
				inter.put(INTERACTOR_1, gene1);
				inter.put(INTERACTOR_2, gene2);
				inter.put(INTERACTION_TYPE, method);
				inter.put(PVAL, new Double(pval));
				inter.put(SOURCE, ProlinksInteractionsSource.NAME);
				interactions.add(inter);
				if(debug && interactions.size() % 1000 == 0){
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
				String geneA = rs.getString(1);
				String geneB = rs.getString(2);
				double p = rs.getDouble(3);
				String method = rs.getString(4);
				Hashtable interaction = new Hashtable();
				interaction.put(INTERACTOR_1, geneA);
				interaction.put(INTERACTOR_2, geneB);
				interaction.put(PVAL, new Double(p));
				interaction.put(INTERACTION_TYPE, method);
				interaction.put(SOURCE, getDataSourceName());
				allInteractions.add(interaction);
				if(debug && allInteractions.size() % 1000 == 0){
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
				String gene = rs.getString(1);
				interactors.add(gene);
				if(debug && interactors.size() % 1000 == 0){
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
	 * @param species
	 *            the name of the species (should be one of getSupportedSpecies)
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
	public Vector getAllInteractions(String species) {
		String tableName = species;
		if (!isBigSpecies(species)) {
			tableName = "prolinks";
		}
		String sql = "SELECT gene_id_a,gene_id_b,p,method FROM " + tableName;
		ResultSet rs = query(sql);
		return makeInteractionsVector(rs);
	}

	/**
	 * @param species
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
	public Vector getAllInteractions(String species, Hashtable args) {
		
		String tableName = species;
		
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		double pval = 2; // PVALS' max value is 1
		if(args.containsKey(PVAL)){
			pval = ( (Double)args.get(PVAL) ).doubleValue();
		}
		
		Vector methods = INT_TYPES;
		if(args.containsKey(INTERACTION_TYPE)){
			methods = (Vector)args.get(INTERACTION_TYPE);
		}
		
		if(pval > 1 && methods.size() == 4){
			// the same as calling getAllInteractions(species)
			return getAllInteractions(species);
		}
		
		Iterator it = methods.iterator();
		
		Vector allInteractions = new Vector();
		
		if(pval > 1){
			// No pvalue restriction, so get all interactions for the specified methods
			while(it.hasNext()){
				String method = (String)it.next();
				String sql = "SELECT gene_id_a, gene_id_b, p FROM " + tableName + "_" + method;
				ResultSet rs = query(sql);
				allInteractions.addAll(makeInteractionsVector(rs,method));
			}//while it.hasNext
		}//if pval > 1
		else{
			// Pvalue is restricted
			while(it.hasNext()){
				String method = (String)it.next();
				double th = ( (Double)ProlinksInteractionsSource.methodThresholds.get(method) ).doubleValue();
				String sql;
				String completeTableName = tableName + "_" + method;
				
				if(pval <= th){
					completeTableName = completeTableName + "_low";
				}
				
				sql = "SELECT gene_id_a, gene_id_b, p FROM " + completeTableName + " WHERE p <= " + pval;
				ResultSet rs = query(sql);
				allInteractions.addAll(makeInteractionsVector(rs, method));
				
			}//while it.hasNext
			
		}
		return allInteractions;
	}

	// Method that implement InteractionsDataSource:

	// ------------- 1st neighbor methods -------------- //

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @return a Vector of Strings of all the nodes that have a direct
	 *         interaction with "interactor", or an empty vector if no
	 *         interactors are found, the interactor is not in the data source,
	 *         or, the species is not supported
	 */
	public Vector getFirstNeighbors (String interactor, String species) {
		
		String tableName = species;
		
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		// Get both directions with no repeats:
		String sql = "(SELECT DISTINCT gene_id_b FROM " + tableName + " WHERE gene_id_a=" + interactor + ") UNION " + 
			"(SELECT DISTINCT gene_id_a FROM " + tableName + " WHERE gene_id_b=" + interactor + ")";
		ResultSet rs = query(sql);
		Vector neighbors = makeInteractorsVector(rs);
		return neighbors;
	}

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object, understood args are: <br>
	 *			 PVAL-->Double only interactions with p-values <= PVAL will be returned <br>
	 *            INTERACTION_TYPE-->Vector of Strings which can be: PP,GN,RS,or GC, only interactions of these types will be returned <br>
	 *			 DIRECTED-->Boolean if TRUE, then, only interactions where interactor1 = interactor are included<br>
	 * @return a Vector of Strings of all the nodes that have a direct
	 *         interaction with "interactor" and that take into account
	 *         additional parameters given in the Hashtable (an empty vector if
	 *         the interactor is not found, the interactor has no interactions,
	 *         or the data source does not contain infomation for the given
	 *         interactor)
	 */
	public Vector getFirstNeighbors(String interactor, String species,
			Hashtable args) {
		
		String tableName = species;
		
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		double pval = 2;
		if(args.containsKey(PVAL)){
			pval = ( (Double)args.get(PVAL) ).doubleValue();
		}
		
		Vector methods = INT_TYPES;
		if(args.containsKey(INTERACTION_TYPE)){
			methods = (Vector)args.get(INTERACTION_TYPE);
		}
		
		boolean directed = false;
		if(args.containsKey(DIRECTED)){
			directed = ( (Boolean)args.get(DIRECTED)).booleanValue();
		}
		
		if(pval > 1 && methods.size() == 4 && !directed){
			// Just like calling with no args
			return getFirstNeighbors(interactor, species);
		}
		
		
		String sql;
		HashSet set = new HashSet();
		
		if(pval > 1){
			
			// No pval restriction
			if(methods.size() < 4){
				// Methods are specified
				Iterator it = methods.iterator();
				while(it.hasNext()){
					String method = (String)it.next();
					String completeTableName = tableName + "_" + method;
					sql = "(SELECT DISTINCT gene_id_b FROM " + completeTableName + " WHERE gene_id_a = " + interactor + ")";
					if(!directed){
					
						sql = sql + " UNION (SELECT DISTINCT gene_id_a FROM " + completeTableName + " WHERE gene_id_b = " + interactor + ")";
					}
					ResultSet rs = query(sql);
					Vector someInteractors = makeInteractorsVector(rs);
					set.addAll(someInteractors);
				
				}//while it.hasNext
			}else{
				// Only restriction is "directed"!
				sql = "SELECT DISTINCT gene_id_b FROM " + tableName + " WHERE gene_id_a=" + interactor;
				ResultSet rs = query(sql);
				return makeInteractorsVector(rs);
			}
		
		}else{
		
			// Pval restriction
			Iterator it = methods.iterator();
			while(it.hasNext()){
				String method = (String)it.next();
				double th = ( (Double)ProlinksInteractionsSource.methodThresholds.get(method) ).doubleValue();
				String completeTableName = tableName + "_" + method;
				if(pval <= th){
					completeTableName = completeTableName + "_low";
				}
				sql = "(SELECT DISTINCT gene_id_b FROM " + completeTableName + " WHERE gene_id_a = " + interactor + " AND p <= " + pval + ")";
				if(!directed){
					sql = sql + " UNION (SELECT DISTINCT gene_id_a FROM " + completeTableName + " WHERE gene_id_b = " + interactor + 
						" AN	D p <= " + pval + ")"; 
				}
				ResultSet rs = query(sql);
				Vector someInteractors = makeInteractorsVector(rs);
				set.addAll(someInteractors);
			}//while
		}// pval <= 1
		
		Vector neighbors = new Vector(set);
		return neighbors;
	}

	/**
	 * @param interactors
	 *            a Vector of Strings (ids that the data source understands)
	 * @param species
	 *            the species
	 * @return a Vector of Vectors of String ids of all the nodes that have a
	 *         direct interaction with the interactors in the given input
	 *         vector, positions in the input and output vectors are matched
	 *         (parallel vectors)
	 */
	public Vector getFirstNeighbors(Vector interactors, String species) {
		Vector vectorOfVectors = new Vector();
		Iterator it = interactors.iterator();
		while(it.hasNext()){
			String gene = (String)it.next();
			vectorOfVectors.add(getFirstNeighbors(gene, species));
		}//while it.hasNext
		return vectorOfVectors;
	}

	/**
	 * @param interactor
	 *            a Vector of Strings (ids that the data source understands)
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions, etc)
	 * @return a Vector of Vectors of String ids of all the nodes that have a
	 *         direct interaction with the interactors in the given input
	 *         vector, positions in the input and output vectors are matched
	 *         (parallel vectors)
	 */
	public Vector getFirstNeighbors(Vector interactors, String species,
			Hashtable args) {
		Iterator it = interactors.iterator();
		Vector vOfv = new Vector();
		while(it.hasNext()){
			String gene = (String)it.next();
			vOfv.add(getFirstNeighbors(gene,species,args));
		}//while it.hasNext
		return vOfv;
	}

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction (they are required to contain the following entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         PVAL --> Double <br>
	 *         SOURCE --> String <br>
	 */
	public Vector getAdjacentInteractions(String interactor, String species) {
	
		String tableName = species;
		
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		// not directed
		String sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE gene_id_a = " + interactor +
		" OR gene_id_b = " + interactor;
		ResultSet rs = query(sql);
		return makeInteractionsVector(rs);
	}

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object entries, this class understands:<br>
	 *            PVAL-->Double only interactions with p-values <= PVAL will be returned <br>
	 *            INTERACTION_TYPE-->Vector of Strings which can be: PP,GN,RS,or GC, only interactions of these types will be returned <br>
	 *            DIRECTED--> Boolean that specifies whether both interactor --> GENEX and GENEX-->interactor should be returned          
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction (they are required to contain the following entries:)<br>
	 *	       INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         PVAL --> Double <br>
	 *         SOURCE --> String <br>
	 */
	public Vector getAdjacentInteractions(String interactor, String species,
			Hashtable args) {
		 String tableName = species;
		 
		 if(!isBigSpecies(species)){
			 tableName = "prolinks";
		 }
		 
		 double pval = 2;
		 if(args.containsKey(PVAL)){
			 pval = ( (Double)args.get(PVAL)).doubleValue();
		 }
		 
		 Vector methods = INT_TYPES;
		 if(args.containsKey(INTERACTION_TYPE)){
			 methods = (Vector)args.get(INTERACTION_TYPE);
		 }
		 
		 boolean directed = false;
		 if(args.containsKey(DIRECTED)){
			 directed = ((Boolean)args.get(DIRECTED)).booleanValue();
		 }
		 
		 if(pval > 1 && methods.size() == 4 && !directed){
			 return getAdjacentInteractions(interactor,species,args);
		 }
		 
		 String sql;
		 Vector allInteractions = new Vector();
		 if(pval > 1){
			 if(methods.size() == 4){
				 // Only thing that matters is that the interaction is directed
				 sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE gene_id_a = " + interactor;
				 ResultSet rs = query(sql);
				 return makeInteractionsVector(rs);
			 }else{
				 // The method matters, direction may matter
				 Iterator it = methods.iterator();
				 while(it.hasNext()){
					 String method= (String)it.next();
					 sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + "_" + method + " WHERE gene_id_a = " + interactor;
					 if(!directed){
						 sql = sql + " OR gene_id_b = " + interactor;
					 }
					 ResultSet rs = query(sql);
					 allInteractions.addAll(makeInteractionsVector(rs));
				 }//while
				 return allInteractions;
			 }//else
		 }else{
			 // Pvalue is restricted
			 Iterator it = methods.iterator();
			 String completeTableName;
			 while(it.hasNext()){
				String method = (String)it.next();
				double pth = ( (Double)ProlinksInteractionsSource.methodThresholds.get(method) ).doubleValue();
				completeTableName = tableName + "_" + method;
				if(pval <= pth){
					completeTableName = completeTableName + "_low"; 
				}
					
				if(directed){
					sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + completeTableName + " WHERE gene_id_a = " + interactor + " AND p <= " +  pval;
				}else{
					sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + completeTableName + " WHERE (gene_id_a = " + interactor + 
					" OR gene_id_b = " + interactor + ") AND p <= " +  pval;
				}
				ResultSet rs = query(sql);
				allInteractions.addAll(makeInteractionsVector(rs));
			 }//while
			 return allInteractions;
		 }//else
		 
	}

	/**
	 * @param interactors
	 *            a Vector of Strings (ids that the data source understands)
	 * @param species
	 *            the species
	 * @return a Vector of Vectors of Hashtables, each hash contains information
	 *         about an interaction (they are required to contain the following
	 *         entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables.<br>
	 *         The input and output vectors are parallel.
	 */
	public Vector getAdjacentInteractions(Vector interactors, String species) {
		Iterator it = interactors.iterator();
		Vector vOfv = new Vector();
		while(it.hasNext()){
			String gene = (String)it.next();
			vOfv.add(getAdjacentInteractions(gene, species));
		}//while
		return vOfv;
	}

	/**
	 * @param interactor
	 *            a Vector of Strings (ids that the data source understands)
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions only, etc)
	 * @return a Vector of Vectors of Hashtables, each hash contains information
	 *         about an interaction (they are required to contain the following
	 *         entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables.<br>
	 *         The input and output vectors are parallel.
	 */
	public Vector getAdjacentInteractions(Vector interactors, String species,
			Hashtable args) {
		Iterator it = interactors.iterator();
		Vector vOfv = new Vector();
		while(it.hasNext()){
			String gene = (String)it.next();
			vOfv.add(getAdjacentInteractions(gene, species, args));
		}//while
		return vOfv;
	}

	// ------------ connecting interactions methods --------------//

	/**
	 * @param interactor1
	 * @param interactor2
	 * @param species
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction between the two interactors, each hash contains these
	 *         entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getConnectingInteractions(String interactor1,
			String interactor2, String species) {
		String tableName = species;
		
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		String sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE (gene_id_a = " + interactor1 + " AND gene_id_b = " + interactor2 + ") OR " +
		"(gene_id_a = " + interactor2 + "AND gene_id_b = " + interactor1 + ")";
		
		ResultSet rs = query(sql);
		return makeInteractionsVector(rs);
	}

	/**
	 * @param interactor1
	 * @param interactor2
	 * @param species
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
	public Vector getConnectingInteractions(String interactor1,
			String interactor2, String species, Hashtable args) {
		String tableName = species;
		if(!isBigSpecies(species)){
			tableName = "prolinks";
		}
		
		double pval = 2;
		if(args.containsKey(PVAL)){
			pval = ((Double)args.get(PVAL)).doubleValue();
		}
		
		Vector methods = INT_TYPES;
		if(args.containsKey(INTERACTION_TYPE)){
			methods = (Vector)args.get(INTERACTION_TYPE);
		}
		
		boolean directed = false;
		if(args.containsKey(DIRECTED)){
			directed = ((Boolean)args.get(DIRECTED)).booleanValue();
		}
		
		if(pval > 1 && methods.size() == 4 && !directed){
			return getConnectingInteractions(interactor1, interactor2, species);
		}
		
		String sql;
		if(pval <= 1){
			Iterator it = methods.iterator();
			Vector interactions = new Vector();
			while(it.hasNext()){
				String method = (String)it.next();
				double pth = ((Double)ProlinksInteractionsSource.methodThresholds.get(method)).doubleValue();
				String completeTableName = tableName + "_" + method;
				if(pval <= pth){
					completeTableName = completeTableName + "_low";
				}
				if(directed){
					sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + completeTableName + " WHERE gene_id_a = " + 
					interactor1 + " AND gene_id_b = " + interactor2 + " AND p <= " + pval;
				}else{
					sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + completeTableName + " WHERE ( (gene_id_a = " + 
					interactor1 + " AND gene_id_b = " + interactor2 + ") OR ( gene_id_a = " + interactor2 + " AND gene_id_b = " + interactor1 + ") ) " + 
					"AND p <= " + pval;
				}//else
				ResultSet rs = query(sql);
				interactions.addAll(makeInteractionsVector(rs));
			}//while
			return interactions;
		}else{
			// No pval restriction
			if(methods.size() == 4){
				// pval does not matter, method does not matter, only directed matters
				sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + " WHERE (gene_id_a = " + interactor1 + "AND gene_id_b = " + interactor2 + ")";
				if(!directed){
					sql = sql + " OR ( gene_id_a = " + interactor2 + " AND gene_id_b = " + interactor1 + ")";
				}
				ResultSet rs = query(sql);
				return makeInteractionsVector(rs);
			}else{
		
			   // Method restriction, maybe direction restriction
				Iterator it = methods.iterator();
				Vector interactions = new Vector();
				while(it.hasNext()){
					String method = (String)it.next();
					sql = "SELECT gene_id_a, gene_id_b, p, method FROM " + tableName + "_" + method + 
					" WHERE (gene_id_a = " + interactor1 + " AND gene_id_b = " + interactor2 + ")";
					if(!directed){
						sql = sql + " OR (gene_id_a = " + interactor2 + " AND gene_id_b = " + interactor1 + ")"; 
					}
					ResultSet rs = query(sql);
					interactions.addAll(makeInteractionsVector(rs));
				}//while
				return interactions;
			}//else
			
		}//else
		
	}

	/**
	 * @param interactors
	 * @param species
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction between the two interactors, each hash contains these
	 *         entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getConnectingInteractions(Vector interactors, String species) {
		String [] genes = (String[])interactors.toArray(new String [interactors.size()]);
		Vector interactions = new Vector();
		for(int i = 0; i < genes.length; i++){
			String gene1 = genes[i];
			for(int j = i; j < genes.length; j++){
				String gene2 = genes[j];
				interactions.addAll(getConnectingInteractions(gene1, gene2, species));
			}//for j
		}//for i
		return interactions;
	}

	/**
	 * @param interactors
	 * @param species
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
	public Vector getConnectingInteractions(Vector interactors, String species,
			Hashtable args) {
		String [] genes = (String[])interactors.toArray(new String [interactors.size()]);
		Vector interactions = new Vector();
		for(int i = 0; i < genes.length; i++){
			String gene1 = genes[i];
			for(int j = i; j < genes.length; j++){
				String gene2 = genes[j];
				interactions.addAll(getConnectingInteractions(gene1, gene2, species, args));
			}//for j
		}//for i
		return interactions;
	}
	
	/**
	 * Tests methods in this class.
	 */
	public Vector test (){
		String species = "Homo_sapiens";
		
		Hashtable args = new Hashtable();
		Vector methods = new Vector();
		methods.add(PP);
		
		args.put(INTERACTION_TYPE, methods);
		args.put(PVAL, new Double(0.005));
		args.put(DIRECTED, Boolean.TRUE);
		
		System.out.println("Calling getFirstNeighbors(553645, Homo_sapiens)...");
		Vector fn = getFirstNeighbors("553645",species);
		Iterator it = fn.iterator();
		System.out.println("Done:");
		while(it.hasNext()){
			System.out.print(" " + it.next());
		}
		
		System.out.println();
		System.out.println();
		
		System.out.println("Calling getAllInteractions(Homo_sapiens, Hashtable (PP,0.005))");
		Vector ints = getAllInteractions(species, args);
		System.out.println("Done: interactions = " + ints.size());
		
		System.out.println();
		System.out.println();
		
		//System.out.println("Calling getFirstNeighbors()");
		
		
		return EMPTY_VECTOR;
	}//test
	

	  /**
	   * If called, System.out.print statements will be called
	   * for debugging
	   */
	  public boolean printDebug (){
		  this.debug = true;
		  return this.debug;
	  }
	  
	  /**
	   * If calles, no System.out.print statemets will be called
	   *
	   */
	  public boolean noPrintDebug () {
		  this.debug = false;
		  return this.debug;
	  }

}// ProlinksInteractionsSource
