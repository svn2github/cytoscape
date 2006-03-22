package org.isb.bionet.datasource.interactions;

//import java.sql.SQLException;
import java.util.*;
import java.lang.reflect.*;
import org.isb.bionet.datasource.*;
import org.isb.bionet.datasource.synonyms.*;

import sun.security.action.GetIntegerAction;

/**
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 *
 * TODO: If the cache gets too big (what is too big? > 6000??? How many nodes do people view in Cytoscape???) clear them.
 */
public class InteractionsHandler implements InteractionsDataSource {
    
    public static final int CACHE_MAX_SIZE = 6000;
    /**
     * The gene ID to use for all genes in the returned interactions
     */
    public static final String UNIVERSAL_GENE_ID_TYPE = SynonymsSource.GI_ID;
   /**
	 * A collection of InteractionsDataSource objects from which
     * interactions are obtained
	 */
	protected Vector interactionSources;
    
   /**
     * The source of gene synonyms across gene id types
     */
    protected SynonymsSource synonymsSource;
    
     // Maps used to save in memory gene id maps to improve performance (very primitive cache)  
    protected Hashtable universalToDbCache; // from String to HashSet
    protected Hashtable dbToUniversalCache; // from String to String
    protected Hashtable universalToOtherUniversals; // from String to Vector
    
    protected boolean debug;
    
    /**
     * Default constructr
     */
    public InteractionsHandler (){
        this.interactionSources = new Vector();
        this.synonymsSource = new SQLSynonymsHandler();
        this.universalToDbCache = new Hashtable();
        this.dbToUniversalCache = new Hashtable();
        this.universalToOtherUniversals = new Hashtable();
    }
    
	/**
	 * 
	 * @param interaction_sources
	 *            a Vector of Strings of fully specified classes of
	 *            InteractionDataSources
	 */
	public InteractionsHandler(Vector interaction_sources) {
		this.interactionSources = new Vector();
		this.synonymsSource = new SQLSynonymsHandler();
        this.universalToDbCache = new Hashtable();
        this.dbToUniversalCache = new Hashtable();
        this.universalToOtherUniversals = new Hashtable();
		Iterator it = interaction_sources.iterator();
		while (it.hasNext()) {
			String className = (String) it.next();
			addSource(className);
		}// while
	}
    
	/**
	 * 
	 * @param source_class
	 *            the fully specified class of an InteractionsDataSource to be
	 *            removed
	 * @return true if the InteractionsDataSource with the given class was found
	 *         and removed
	 */
	public Boolean removeSource(String source_class) {
		Iterator it = this.interactionSources.iterator();
		InteractionsDataSource sourceToRemove = null;
		while (it.hasNext()) {
			InteractionsDataSource source = (InteractionsDataSource) it.next();
			if (source.getClass().getName().equals(source_class)) {
				sourceToRemove = source;
				break;
			}
		}// while

		if (sourceToRemove != null) {
			return new Boolean(this.interactionSources.remove(sourceToRemove));
		}
		return Boolean.FALSE;
	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
	 * TODO: Check that the type of ID can be translated to UNIVERSAL_GENE_ID_TYPE???
     */
	public Boolean addSource(String source_class) {
        
        if(containsSource(source_class).booleanValue()){
            return Boolean.TRUE;
        }

		try {

			Class classForName = Class.forName(source_class);
			Object obj = classForName.newInstance();
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return Boolean.TRUE;
			}

			//System.err
			//		.println("Requested class for source is not an InteractionsDataSource:"
				//			+ source_class);
			return Boolean.FALSE;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE; // for now
		}// catch

	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @param arg
	 *            an argument to be used to create the InteractionDataSource
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
     *         
     * TODO: Check that the type of ID can be translated to UNIVERSAL_GENE_ID_TYPE???
	 */
	public Boolean addSource(String source_class, Object arg) {
	    
        if(containsSource(source_class).booleanValue()){
            return Boolean.TRUE;
        }
        
        
		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] { arg
					.getClass() });
			Object obj = cons.newInstance(new Object[] { arg });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return Boolean.TRUE;
			}

			//System.out
			//		.println("Requested class for source is not an InteractionsDataSource:"
				//			+ source_class);
			return Boolean.FALSE;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE; // for now
		}// catch

	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @param arg1
	 *            an argument to be used to create the InteractionDataSource
	 * @param arg2
	 *            an argument to be used to create the InteractionDataSource
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
	 * TODO: Check that the type of ID can be translated to UNIVERSAL_GENE_ID_TYPE???
     */
	public Boolean addSource(String source_class, Object arg1, Object arg2) {
        if(containsSource(source_class).booleanValue()){
            return Boolean.TRUE;
        }
		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] {
					arg1.getClass(), arg2.getClass() });
			Object obj = cons.newInstance(new Object[] { arg1, arg2 });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return Boolean.TRUE;
			}

			//System.out
			//		.println("Requested class for source is not an InteractionsDataSource:"
				//			+ source_class);
			return Boolean.FALSE;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE; // for now
		}// catch

	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @param arg1
	 *            an argument to be used to create the InteractionDataSource
	 * @param arg2
	 *            an argument to be used to create the InteractionDataSource
	 * @param arg3
	 *            an argument to be used to create the InteractionDataSource
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
     * TODO: Check that the type of ID can be translated to UNIVERSAL_GENE_ID_TYPE???
	 */
	public Boolean addSource(String source_class, Object arg1, Object arg2,
			Object arg3) {
        if(containsSource(source_class).booleanValue()){
            return Boolean.TRUE;
        }
		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] {
					arg1.getClass(), arg2.getClass(), arg3.getClass() });
			Object obj = cons.newInstance(new Object[] { arg1, arg2, arg3 });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return Boolean.TRUE;
			}

//			System.out
	//				.println("Requested class for source is not an InteractionsDataSource:"
		//					+ source_class);
			return Boolean.FALSE;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE; // for now
		}// catch

	}

	/**
	 * @return the fully specified classes of the InteractionDataSources in this
	 *         handler
	 */
	public Vector getSources() {
		Vector classNames = new Vector();
		Iterator it = this.interactionSources.iterator();
		while (it.hasNext()) {
			InteractionsDataSource dataSource = (InteractionsDataSource) it
					.next();
			classNames.add(dataSource.getClass().getName());
		}// while it.hasNext
		return classNames;
	}
    
    /**
     * Gets an InteractionsDataSource of the given class, or null if not found
     * 
     * @param source_class the fully specified classes of the InteractionDataSources in this
     *         handler
     * @return an InteractionsDataSource of the given class, or null if not found
     */
    protected InteractionsDataSource getDataSourceForClass (String source_class){
        Iterator it = this.interactionSources.iterator();
        while (it.hasNext()) {
            InteractionsDataSource dataSource = (InteractionsDataSource) it
                    .next();
            if (dataSource.getClass().getName().equals(source_class)) {
                return dataSource;
            }
        }// while it.hasNext()
       // System.out.println("getDataSourceForClass returning null!!!!!!!!!!!");
        return null;
    }

	/**
	 * @param source_class
	 *            the fully specified class of the InteractionsDataSource to
	 *            check
	 * @return true if an InteractionsDataSource with the given class already
	 *         exists, false otherwise
	 */
	public Boolean containsSource(String source_class) {
		Iterator it = this.interactionSources.iterator();
		while (it.hasNext()) {
			InteractionsDataSource dataSource = (InteractionsDataSource) it
					.next();
			if (dataSource.getClass().getName().equals(source_class)) {
				return Boolean.TRUE;
			}
		}// while it.hasNext()
		return Boolean.FALSE;
	}

	/**
	 * Calls a method in a class that implements InteractionsDataSource and
	 * returns the answer to this call
	 * 
	 * @param source_class
	 *            the fully specified class of the InteractionsDataSource for
	 *            which the method will be called
	 * @param method_name
	 *            the name of the method
	 * @param args
	 *            the arguments for the method (possibly empty)
	 * @return the returned object by the called method (Java XML-RPC compliant)
     * 
	 * TODO: Since this class will be taking care of synonyms, and unifying gene names,
     * this method will need to have a way of translating gene names, or, add documentation??? 
     */
	public Object callSourceMethod(String source_class, String method_name,
			Vector args) {
        
		Iterator it = this.interactionSources.iterator();
		InteractionsDataSource dataSource = null;
		while (it.hasNext()) {
			InteractionsDataSource source = (InteractionsDataSource) it.next();
			if (source.getClass().getName().equals(source_class)) {
				dataSource = source;
				break;
			}
		}// while

		if (dataSource == null){
		    System.err.println("Could not find data source of class " + source_class);
            return Boolean.FALSE;
        }
		try {

			Class[] argTypes = new Class[args.size()];
			for (int i = 0; i < args.size(); i++) {
				argTypes[i] = args.get(i).getClass();
			}// for i
			
			Method method = dataSource.getClass().getDeclaredMethod(
					method_name, argTypes);
			Object returnedObject = method.invoke(dataSource, args.toArray());
			
            return returnedObject;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}// catch
	}

	// -------------- Methods that implement InteractionsDataSource -----------//

	/**
	 * @return the name of the data source, for example, "KEGG", "Prolinks",
	 *         etc.
	 */
	public String getDataSourceName() {
		return "InteractionsHandler";
	}

	/**
	 * @return the type of backend implementation (how requests to the data
	 *         source are implemented) one of WEB_SERVICE, LOCAL_DB, REMOTE_DB,
	 *         MEMORY, MIXED
	 */
	public String getBackendType() {
		return DataSource.MIXED;
	}

	/**
	 * @return a Vector of Strings representing the species for which the data
	 *         source contains information
	 */
	public Vector getSupportedSpecies() {
		Vector species = new Vector();
		Iterator it = this.interactionSources.iterator();
		while(it.hasNext()){
             InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
			Vector sp = dataSource.getSupportedSpecies();
			species.addAll(sp);
		}
		return species;
	}
    
    /**
     * @param species the species
     * @return TRUE if the given species is supported by at least one of the InteractionDataSources, FALSE otherwise
     */
    public Boolean supportsSpecies (String species){
        Iterator it = this.interactionSources.iterator();
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            Boolean supports = dataSource.supportsSpecies(species);
            if(supports.booleanValue()) return supports;
        }
        return Boolean.FALSE;
    }
    
    /**
     * @return a Hashtable from the fully described class of the data source (String) to
     * a Vector of Strings that are the species that that data source supports
     */
    public Hashtable getSupportedSpeciesForEachSource(){
        Hashtable table = new Hashtable();
        Iterator it = this.interactionSources.iterator();
        while(it.hasNext()){
            
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            System.out.println("Calling getSupportedSpecies for data source [" + dataSource.getClass().getName() + "]");
            Vector sp = dataSource.getSupportedSpecies();
            table.put(dataSource.getClass().getName(), sp);
        }
        return table;
    }
    
    /**
     * @return a Hashtable from a data source's fully specified class to the data sources name available throug the getDataSourceName method
     */
    public Hashtable getSourcesNames(){
        Hashtable table = new Hashtable();
        Iterator it = this.interactionSources.iterator();
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            String name = (String)dataSource.getDataSourceName();
            table.put(dataSource.getClass().getName(), name);
        }
        return table;
    }

	/**
	 * @return false always
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

	/**
	 * @return "" always
	 */
	public String getVersion() {
		return "";
	}
	
	/**
	 * 
	 * @param source_class the fully described class of the InteractionsDataSource
	 * @return the version of the given class
	 */
	public String getVersion (String source_class){
		Iterator it = this.interactionSources.iterator();
		while(it.hasNext()){
			InteractionsDataSource ds = (InteractionsDataSource)it.next();
			if(ds.getClass().getName().equals(source_class)){
				return ds.getVersion();
			}
		}
		return "ERROR";
	}
	
	/**
	 * @return  the type of gene id that this interactions source accepts
	 */
	public String getIDtype () {
        return UNIVERSAL_GENE_ID_TYPE;
	}

	// ------------------------ get interactions en masse --------------------
	/**
	 * @param species
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction and is required to contain the following entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         SOURCE --> String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getAllInteractions(String species) {

		Iterator it = interactionSources.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
			Vector interactions = dataSource.getAllInteractions(species);
             String sourceGeneID = dataSource.getIDtype();
             Vector translatedInteractions = translateInteractionsToUniversalGeneID(sourceGeneID, interactions);
			allInteractions.addAll(translatedInteractions);
		}//while it
		
		return allInteractions;
	}
    
    /**
     * @param species
     * @param source_class the class of the InteractionsDataSource to use
     * @return a Vector of Hashtables, each hash contains information about an
     *         interaction and is required to contain the following entries:<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         SOURCE --> String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables
     */
    public Vector getAllInteractions(String species, String source_class) {
       
        InteractionsDataSource dataSource = this.getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
        Vector interactions = dataSource.getAllInteractions(species);
        String sourceGeneID = dataSource.getIDtype();
        Vector translatedInteractions = translateInteractionsToUniversalGeneID(sourceGeneID, interactions);
        return translatedInteractions;
    }
    
    /**
     * @param species
     * @return new Integer(num)ber of interactions
     */
    public Integer getNumAllInteractions(String species) {

        Iterator it = interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            num+= dataSource.getNumAllInteractions(species).intValue();
        }//while it
        
        return new Integer(num);
    }
    
    /**
     * 
     * @param species the desired species
     * @param source_class source_class the class of the InteractionsDataSource to use
     * @return an Integer
     */
    public Integer getNumAllInteractions(String species, String source_class) {
        int num = 0;
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
        num = dataSource.getNumAllInteractions(species).intValue();     
        return new Integer(num);
    }
    

	/**
	 * @param species
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions, etc)
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction and is required to contain the following entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getAllInteractions(String species, Hashtable args) {
		Iterator it = interactionSources.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
			Vector interactions = dataSource.getAllInteractions(species, args);
             Vector translatedInteractions = translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions);
			allInteractions.addAll(translatedInteractions);
		}//while it
		
        
		return allInteractions;
	}
    
    /**
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions, etc)
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Hashtables, each hash contains information about an
     *         interaction and is required to contain the following entries:<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables
     */
    public Vector getAllInteractions(String species, Hashtable args, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
        Vector interactions = dataSource.getAllInteractions(species, args);
        Vector translatedInteractions = translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions);
        return translatedInteractions;
    }
    
    /**
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions, etc)
     * @return the number of interactions
     */
    public Integer getNumAllInteractions(String species, Hashtable args) {
        Iterator it = interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            num += (dataSource.getNumAllInteractions(species, args)).intValue();
        }//while it
        
        return new Integer(num);
    }
    
    /**
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions, etc)
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of interactions
     */
    public Integer getNumAllInteractions(String species, Hashtable args, String source_class) {
        int num = 0;
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(!dataSource.supportsSpecies(species).booleanValue()) new Integer(num);
        num = (dataSource.getNumAllInteractions(species, args)).intValue();
        return new Integer(num);
    }
 
	// ----------- 1st neighbor methods ------------//
	

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
		Iterator it = this.interactionSources.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
             Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
			Vector firstNeighbors = dataSource.getFirstNeighbors(translatedInteractors, species);
			allInteractions.addAll(translateInteractorsToUniversalGeneID(firstNeighbors,dataSource.getIDtype()));
		}//while it.hasNext
		return allInteractions;
	}
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Vectors of String ids of all the nodes that have a
     *         direct interaction with the interactors in the given input
     *         vector, positions in the input and output vectors are matched
     *         (parallel vectors)
     */
    public Vector getFirstNeighbors(Vector interactors, String species, String source_class) {
          InteractionsDataSource dataSource = getDataSourceForClass(source_class);
          if(dataSource == null) return EMPTY_VECTOR;
          if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
          Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
          Vector firstNeighbors = dataSource.getFirstNeighbors(translatedInteractors, species);
          return translateInteractorsToUniversalGeneID(firstNeighbors,dataSource.getIDtype());
    }
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @return the number of 1st neighbors
     */
    public Integer getNumFirstNeighbors(Vector interactors, String species) {
        Iterator it = this.interactionSources.iterator();
       int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            num += dataSource.getNumFirstNeighbors(translatedInteractors, species).intValue();
        }//while it.hasNext
        return new Integer(num);
    }
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of 1st neighbors
     */
    public Integer getNumFirstNeighbors(Vector interactors, String species, String source_class) {
       int num = 0;
       InteractionsDataSource dataSource = getDataSourceForClass(source_class);
       if(dataSource == null) return new Integer(0);
       if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
       Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
       num = dataSource.getNumFirstNeighbors(translatedInteractors, species).intValue();
       return new Integer(num);
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
		Iterator it = this.interactionSources.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;  
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
			Vector interactions = dataSource.getFirstNeighbors(translatedInteractors, species, args);
            allInteractions.addAll(translateInteractorsToUniversalGeneID(interactions, dataSource.getIDtype()));
		}//while it.hasNext
		return allInteractions;
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
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Vectors of String ids of all the nodes that have a
     *         direct interaction with the interactors in the given input
     *         vector, positions in the input and output vectors are matched
     *         (parallel vectors)
     */
    public Vector getFirstNeighbors(Vector interactors, String species,
            Hashtable args, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;  
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        Vector interactions = dataSource.getFirstNeighbors(translatedInteractors, species, args);
        return translateInteractorsToUniversalGeneID(interactions, dataSource.getIDtype());
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
     * @return the number of 1st neighbors
     */
    public Integer getNumFirstNeighbors(Vector interactors, String species,
            Hashtable args) {
        Iterator it = this.interactionSources.iterator();
       int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            num += dataSource.getNumFirstNeighbors(translatedInteractors, species, args).intValue();
        }//while it.hasNext
        return new Integer(num);
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
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of 1st neighbors
     */
    public Integer getNumFirstNeighbors(Vector interactors, String species,
            Hashtable args, String source_class) {
        int num = 0;
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) new Integer(0);
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        num = dataSource.getNumFirstNeighbors(translatedInteractors, species, args).intValue();
        
        return new Integer(num);
    }

	
	/**
	 * @param interactors
	 *            a Vector of Strings (ids that the data source understands)
	 * @param species
	 *            the species
	 * @return a Vector of Hashtables, each hash contains information
	 *         about an interaction (they are required to contain the following
	 *         entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables.<br>
	 */
	public Vector getAdjacentInteractions(Vector interactors, String species) {
		Iterator it = this.interactionSources.iterator();
		Vector allInteractions = new Vector();
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
			Vector interactions = dataSource.getAdjacentInteractions(translatedInteractors, species);
			allInteractions.addAll(translateInteractionsToUniversalGeneID(dataSource.getIDtype(), interactions));
		}//while it.hasNext
		return allInteractions;
	}
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Hashtables, each hash contains information
     *         about an interaction (they are required to contain the following
     *         entries:)<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables.<br>
     */
    public Vector getAdjacentInteractions(Vector interactors, String species, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        Vector interactions = dataSource.getAdjacentInteractions(translatedInteractors, species);
        return translateInteractionsToUniversalGeneID(dataSource.getIDtype(), interactions);
    }
    
    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String species) {
        Iterator it = this.interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            num += dataSource.getNumAdjacentInteractions(translatedInteractors, species).intValue();
        }//while it.hasNext
        return new Integer(num);
    }

    /**
     * @param interactors
     *            a Vector of Strings (ids that the data source understands)
     * @param species
     *            the species
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String species, String source_class) {
        int num = 0;
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        num = dataSource.getNumAdjacentInteractions(translatedInteractors, species).intValue();
        return new Integer(num);
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
     * @return a Vector of Hashtables, each hash contains information
     *         about an interaction (they are required to contain the following
     *         entries:)<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables.<br>
     */
    public Vector getAdjacentInteractions(Vector interactors, String species,
            Hashtable args) {
        Iterator it = this.interactionSources.iterator();
        Vector allInteractions = new Vector();
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            Vector interactions = dataSource.getAdjacentInteractions(translatedInteractors, species, args);
            allInteractions.addAll(translateInteractionsToUniversalGeneID(dataSource.getIDtype(), interactions));
        }//while it.hasNext
        return allInteractions;     
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
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Hashtables, each hash contains information
     *         about an interaction (they are required to contain the following
     *         entries:)<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables.<br>
     */
    public Vector getAdjacentInteractions(Vector interactors, String species,
            Hashtable args, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        Vector interactions = dataSource.getAdjacentInteractions(translatedInteractors, species, args);
        return translateInteractionsToUniversalGeneID(dataSource.getIDtype(), interactions);
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
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String species,
            Hashtable args) {
        Iterator it = this.interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
           num += dataSource.getNumAdjacentInteractions(translatedInteractors, species, args).intValue();
        }//while it.hasNext
        return new Integer(num);
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
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of adjacent interactions
     */
    public Integer getNumAdjacentInteractions(Vector interactors, String species,
            Hashtable args, String source_class) {
        int num = 0;
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        num = dataSource.getNumAdjacentInteractions(translatedInteractors, species, args).intValue();
        return new Integer(num);
    }


	// ------------------ connecting interactions methods ------------------- //


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
	public Vector getConnectingInteractions (Vector interactors, String species) {
        Iterator it = this.interactionSources.iterator();
        Vector allInteractions = new Vector();
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            Vector interactions = dataSource.getConnectingInteractions(translatedInteractors, species);
            allInteractions.addAll(translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions));
        }//while it.hasNext
        return allInteractions;     
	}
    
    /**
     * @param interactors
     * @param species
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return a Vector of Hashtables, each hash contains information about an
     *         interaction between the two interactors, each hash contains these
     *         entries:<br>
     *         INTERACTOR_1 --> String <br>
     *         INTERACTOR_2 --> String <br>
     *         INTERACTION_TYPE -->String <br>
     *         Each implementing class can add additional entries to the
     *         Hashtables
     */
    public Vector getConnectingInteractions (Vector interactors, String species, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        Vector interactions = dataSource.getConnectingInteractions(translatedInteractors, species);
        return translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions);
    }
    
    /**
     * @param interactor1
     * @param interactor2
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String species) {
        Iterator it = this.interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()) continue;
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            num += dataSource.getNumConnectingInteractions(translatedInteractors, species).intValue();
        }//while it.hasNext
        return new Integer(num);
    }
    
    /**
     * @param interactor1
     * @param interactor2
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return the number of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String species, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        int num = dataSource.getNumConnectingInteractions(translatedInteractors, species).intValue();
        return new Integer(num);
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
        
        Iterator it = this.interactionSources.iterator();
        Vector allInteractions = new Vector();
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()){
                continue;
            }
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            Vector interactions = dataSource.getConnectingInteractions(translatedInteractors, species, args);
            allInteractions.addAll(translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions));
        }//while it.hasNext
        return allInteractions;  
	}
    
    /**
     * @param interactors
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
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
            Hashtable args, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return EMPTY_VECTOR;
        if(!dataSource.supportsSpecies(species).booleanValue()) return EMPTY_VECTOR;    
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        Vector interactions = dataSource.getConnectingInteractions(translatedInteractors, species, args);
        return translateInteractionsToUniversalGeneID(dataSource.getIDtype(),interactions);
    }


    /**
     * @param interactors
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @return new Integer(num)ber of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String species,
            Hashtable args) {
       
        Iterator it = this.interactionSources.iterator();
        int num = 0;
        while(it.hasNext()){
            InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
            if(!dataSource.supportsSpecies(species).booleanValue()){
                continue;
            }
            Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
            num += dataSource.getNumConnectingInteractions(translatedInteractors, species, args).intValue();
        }//while it.hasNext
        return new Integer(num);
    }
    
    /**
     * @param interactors
     * @param species
     * @param args
     *            a table of String->Object entries that the implementing class
     *            understands (for example, p-value thresholds, directed
     *            interactions only, etc)
     * @param source_class source_class source_class the class of the InteractionsDataSource to use
     * @return new Integer(num)ber of connecting interactions
     */
    public Integer getNumConnectingInteractions(Vector interactors, String species,
            Hashtable args, String source_class) {
        InteractionsDataSource dataSource = getDataSourceForClass(source_class);
        if(dataSource == null) return new Integer(0);
        if(!dataSource.supportsSpecies(species).booleanValue()) return new Integer(0);
        Vector translatedInteractors = translateInteractorsFromUniversalGeneID(interactors,dataSource.getIDtype());
        int num = dataSource.getNumConnectingInteractions(translatedInteractors, species, args).intValue();
        return new Integer(num);
    }
    
    
	/**
	 * Calls test for each data source
	 * @return
	 */
	public Vector test (){
		
		Iterator it = this.interactionSources.iterator();
		Vector allResults = new Vector();
		
		
		while(it.hasNext()){
			InteractionsDataSource dataSource = (InteractionsDataSource)it.next();
			allResults.addAll(dataSource.test());
		}//it.hasNext
		return allResults;
	}
    
    //-------------- Translation of ids  methods ---------------//
    
    
    /**
     * Clears the synonyms maps
     */
    protected void clearCachesIfFull (){
        if(this.dbToUniversalCache.size() >= CACHE_MAX_SIZE || this.universalToDbCache.size() >= CACHE_MAX_SIZE || 
                this.universalToOtherUniversals.size() >= CACHE_MAX_SIZE){
            this.dbToUniversalCache.clear();
            this.universalToDbCache.clear();
            this.universalToOtherUniversals.clear();
        }
    }
    
    
    /**
     * Translates the gene names in the list interactions to UNIVERSAL_GENE_ID_TYPE when possible
     * 
     * @param sourceIDtype the type of id the original genes have
     * @param interactions the interactions (Vector of Hashtables)
     * @return translated interactions (Vector of Hashtables)
     */
    // This method is called before interactions are returned to the outside. It must make sure that
    // it assigns unique GIs to each database id, since they become the unique identifiers for Cytoscape nodes
    // Additional GIs that match the database ids are contained in an entry with INTERACTOR_1_IDS and INTERACTOR_2_IDS keys
    protected Vector translateInteractionsToUniversalGeneID (String sourceIDtype, Vector interactions){
        
        clearCachesIfFull();
        
        Vector translated = new Vector();
        HashSet alreadyTranslated = new HashSet();
        HashSet interactorsToTranslate = new HashSet();
        Iterator it = interactions.iterator();
        
        // Find interactors that have not already been translated:
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            String i1 = (String)interaction.get(INTERACTOR_1);
            String i2 = (String)interaction.get(INTERACTOR_2);
            
            if(i1 != null)
                if(!this.dbToUniversalCache.containsKey(i1)) interactorsToTranslate.add(i1);
                else alreadyTranslated.add(i1);
            
            if(i2 != null)
                if(!this.dbToUniversalCache.containsKey(i2)) interactorsToTranslate.add(i2);
                else alreadyTranslated.add(i2);
                
        }//while it.hasNext
       
        System.out.println("Num already translated ids = " + alreadyTranslated.size() + " num ids to translate = " + interactorsToTranslate.size());
        
        // Find UIDs of interactors that have not been already translated
        Hashtable translation = 
            this.synonymsSource.getSynonyms(sourceIDtype,new Vector(interactorsToTranslate),UNIVERSAL_GENE_ID_TYPE);
        
        System.out.println("Num translated = " + translation.size());
        
        // Traslate the interactions
        it = interactions.iterator();
        while(it.hasNext()){
            Hashtable interaction = (Hashtable)it.next();
            String d1 = (String)interaction.get(INTERACTOR_1);
            String d2 = (String)interaction.get(INTERACTOR_2);
            Vector universalIds1 = (Vector)translation.get(d1);
            Vector universalIds2 = (Vector)translation.get(d2);
         
            if(universalIds1 != null && universalIds1.size() > 0){
                
                // Choose a univesal id that:
                // 1. Is not already used by an interactor in the same db as d1
                // 2. Is preferably, already used by an interactor in another db different from d1's
                String u1 = "";
                Iterator it2 = universalIds1.iterator();
                while(it2.hasNext()){
                    String aUID = (String)it2.next();
                    Set dbIDs = getDbIdsFromUniversalCache(sourceIDtype,aUID);
                    if(dbIDs == null){
                        // There is no id from sourceIDtype that has
                        // been assigned to this unique ID
                        // Now see if it has been used in a different db, if so
                        // choose this id, if not, keep looking
                        HashSet set = (HashSet)this.universalToDbCache.get(aUID);
                        u1 = aUID;
                        if(set != null && set.size() > 0) break; // this meets both conds!
                    }
                }//while it2
                
                if(u1 == "" || u1 == null){
                    //For testing
                    throw new IllegalStateException(d1 + " has no u1!!!!!!!");
                }
                
                HashSet set = (HashSet)this.universalToDbCache.get(u1);
                if(set == null) set = new HashSet();
                set.add(d1);
                
                this.dbToUniversalCache.put(d1,u1);
                interaction.put(INTERACTOR_1,  u1);
                interaction.put(INTERACTOR_1_IDS, universalIds1);
                this.universalToOtherUniversals.put(u1,universalIds1);
                
            }else{
                String u1 = (String)this.dbToUniversalCache.get(d1);
                if(u1 != null){ 
                    interaction.put(INTERACTOR_1,  u1);
                    Vector otherUniversals = (Vector)this.universalToOtherUniversals.get(u1);
                    if(otherUniversals != null) interaction.put(INTERACTOR_1_IDS, otherUniversals);
                }else{
                    // remember that it does not have a synonym
                    this.dbToUniversalCache.put(d1,d1);
                }
            }
            
            if(universalIds2 != null){  
               
                // Choose a univesal id that:
                // 1. Is not already used by an interactor in the same db as d1
                // 2. Is preferably, already used by an interactor in another db different from d1's
                String u2 = "";
                Iterator it2 = universalIds2.iterator();
                while(it2.hasNext()){
                    String aUID = (String)it2.next();
                    Set dbIDs = getDbIdsFromUniversalCache(sourceIDtype,aUID);
                    if(dbIDs == null){
                        // There is no id from sourceIDtype that has
                        // been assigned to this unique ID
                        // Now see if it has been used in a different db, if so
                        // choose this id, if not, keep looking
                        HashSet set = (HashSet)this.universalToDbCache.get(aUID);
                        u2 = aUID;
                        if(set != null && set.size() > 0) break; // this meets both conds!
                    }
                }//while it2
                
                if(u2 == "" || u2 == null){
                    //For testing
                    throw new IllegalStateException(d2 + " has no u2!!!!!!!");
                }
                
                HashSet set = (HashSet)this.universalToDbCache.get(u2);
                if(set == null) set = new HashSet();
                set.add(d2);
                
                this.dbToUniversalCache.put(d2,u2);
                interaction.put(INTERACTOR_2,  u2);
                interaction.put(INTERACTOR_2_IDS, universalIds2);
                this.universalToOtherUniversals.put(u2, universalIds2);
            }else{
                String u2 = (String)this.dbToUniversalCache.get(d2);
                if(u2 != null){
                    interaction.put(INTERACTOR_2,  u2);
                    Vector otherUniversals = (Vector)this.universalToOtherUniversals.get(u2);
                    if(otherUniversals != null) interaction.put(INTERACTOR_2_IDS, otherUniversals);
                }else{
                    // remember that it does not have a synonym
                    this.dbToUniversalCache.put(d2,d2);
                }
            }
            translated.add(interaction);
        }
         
      
        return translated;
    }
    
    /**
     * @param interactors list of ids of type sourceIDtype
     * @param sourceIDtype the type of id of genes ins interactors
     * @return a Vector of Strings, converted to UNIVERSAL_GENE_ID_TYPE
     */
    protected Vector translateInteractorsToUniversalGeneID (Vector interactors, String sourceIDtype){
        clearCachesIfFull();
        // Find out which ones have not already been translated:
        HashSet translatedInteractors = new HashSet();
        HashSet toTranslate = new HashSet(); 
        Iterator it = interactors.iterator();
        while(it.hasNext()){
            String dbID = (String)it.next();
            if(this.dbToUniversalCache.containsKey(dbID)){
                translatedInteractors.add((String)this.dbToUniversalCache.get(dbID));
            }else{
                toTranslate.add(dbID);
            }
        }
        
        System.out.println("Num already translated = " + translatedInteractors.size() + " num ids to translate = " + toTranslate.size());
        
        // Find translations for the ones that are not already translated
        Hashtable translation =  this.synonymsSource.getSynonyms(sourceIDtype,new Vector(toTranslate),UNIVERSAL_GENE_ID_TYPE);
        System.out.println("Num translated = " + translation.size());
        
        // Translate intearctors
        it = toTranslate.iterator();
        while(it.hasNext()){
            String dbID = (String)it.next();
            Vector universalIDs = (Vector)translation.get(dbID);
            if(universalIDs != null){
                
                String uID = "";
                Iterator it2 = universalIDs.iterator();
                while(it2.hasNext()){
                  String aUID = (String)it2.next();
                  Set dbIDs = getDbIdsFromUniversalCache(sourceIDtype,aUID);
                  if(dbIDs == null){
                      uID = aUID;
                      HashSet set = (HashSet)this.universalToDbCache.get(aUID);
                      if(set != null && set.size() > 0) break;
                  }
                }//while it2
               
                
                if(uID == "" || uID == null){
                    //For testing
                    throw new IllegalStateException(dbID + " has no uID!!!!!!!");
                }
                
                HashSet set = (HashSet)this.universalToDbCache.get(uID);
                if(set == null) set = new HashSet();
                set.add(dbID);
                
                this.dbToUniversalCache.put(dbID, uID);
                this.universalToOtherUniversals.put(uID,universalIDs);
                // WHAT TO DO WITH THE OTHER universal IDS???????
                // Try this (???), we will see what happens...
                translatedInteractors.addAll(universalIDs);
            }else{
                // remember that it does not have a synonym
                this.dbToUniversalCache.put(dbID, dbID); 
            }
        }
        return new Vector(translatedInteractors);
    }
    
    /**
     * @param interactors list of ids of type UNIVERSAL_GENE_ID_TYPE
     * @param targetIDtype the type to which to convert
     * @return a Vector of Strings, converted ids (if available)
     */
    protected Vector translateInteractorsFromUniversalGeneID (Vector interactors, String targetIDtype){
        clearCachesIfFull();
        // Find the interactors that have not already been translated
        HashSet translatedInteractors = new HashSet();
        HashSet toTranslate = new HashSet(); 
        Iterator it = interactors.iterator();
        while(it.hasNext()){
            String uID = (String)it.next();
            HashSet set = (HashSet)this.universalToDbCache.get(uID);
            if(set == null){
                toTranslate.add(uID);
                continue;
            }
            Set dbIDs = getDbIdsFromUniversalCache(targetIDtype, uID);
            if(dbIDs != null){
                translatedInteractors.addAll(dbIDs);
            }else{
                toTranslate.add(uID);
            }
        }
        
        System.out.println("Num already translated = " + translatedInteractors.size() + " num ids to translate = " + toTranslate.size());
        
        // Find translations
        Hashtable translation =  this.synonymsSource.getSynonyms(UNIVERSAL_GENE_ID_TYPE, new Vector(toTranslate),targetIDtype);
        System.out.println("Num translated = " + translation.size());
        
        it = toTranslate.iterator();
        while(it.hasNext()){
            String uID = (String)it.next();
            Vector dbIDs = (Vector)translation.get(uID);
            if(dbIDs != null){
                // Took the following code out because it allows different db ids from the SAME db to have the same UID
//               Iterator it2 = dbIDs.iterator();
//               while(it2.hasNext()){
//                   String dbID = (String)it2.next();
//                   this.dbToUniversalCache.put(dbID, uID);
//                   HashSet set = (HashSet)this.universalToDbCache.get(uID);
//                   if(set == null){
//                       set = new HashSet();
//                       this.universalToDbCache.put(uID, set);
//                   }
//                   set.add(dbID);
//               }
               //translatedInteractors.add(dbID);
               // What to do with the rest of the db ids??????
               // try this:
               // This should be correct since we wish to get interactions/adjacent interactors of ALL matching db ids in the data base
               translatedInteractors.addAll(dbIDs);
            }
        }
        return new Vector(translatedInteractors);
     }
    
    /**
     * 
     * @param dbIDtype the type of db type (see SynonymsSource for types of db ids)
     * @param uID the gene id of type UNIVERSAL_GENE_ID_TYPE
     * @return the database ids of type dbIDtype that corresponds to the given universal id that is stored in the universal cache, null if not stored
     */
    //TODO: Should return set of matching db ids, which should only be ONE,but we wil leave it like this.
    protected Set getDbIdsFromUniversalCache (String dbIDtype, String uID){
        Set set = (HashSet)this.universalToDbCache.get(uID);
        
        if(set == null) return null;
        
        Iterator it = set.iterator();
        HashSet dbIds = new HashSet();
        while(it.hasNext()){
            String dbID = (String)it.next();
            if(dbID.startsWith(dbIDtype + ":")) dbIds.add(dbID);
        }
        if(dbIds.size() == 0) return null;
        return dbIds;   
    }
	
}// InteractionsDataSource
