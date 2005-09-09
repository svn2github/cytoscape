package org.isb.bionet.datasource.interactions;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcException;
import org.isb.xmlrpc.client.*;

/**
 * The class that applications use to get interactions from the server
 *
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @version 1.0
 */
public class InteractionDataClient extends AuthenticatedDataClient{
	
	public static final String SERVICE_NAME = "interactions";
	
	/**
	 * Constructor
	 */
	public InteractionDataClient (String server_url) throws XmlRpcException,
    	java.net.MalformedURLException{
		super(server_url);
		this.serviceName = SERVICE_NAME;
	}	
	
	/**
	 * 
	 * @param source_class
	 *            the fully specified class of an InteractionsDataSource to be
	 *            removed
	 * @return true if the InteractionsDataSource with the given class was found
	 *         and removed
	 */
	public boolean removeSource (String source_class) throws XmlRpcException, IOException {
		Object out = call(this.serviceName + ".removeSource", source_class);
		return ((Boolean)out).booleanValue();
	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
	 */
	public boolean addSource(String source_class) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".addSource", source_class);
		return ((Boolean)out).booleanValue();
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
	 */
	public boolean addSource(String source_class, Object arg) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".addSource", source_class, arg);
		return ((Boolean)out).booleanValue();
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
	 */
	public boolean addSource(String source_class, Object arg1, Object arg2) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".addSource", source_class, arg1, arg2);
		return ((Boolean)out).booleanValue();
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
	 */
	public boolean addSource(String source_class, Object arg1, Object arg2,
			Object arg3) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".addSource", source_class, arg1, arg2, arg3);
		return ((Boolean)out).booleanValue();

	}

	/**
	 * @return the fully specified classes of the InteractionDataSources in this
	 *         handler
	 */
	public Vector getSources() throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".getSources");
		return (Vector)out;
	}

	/**
	 * @param source_class
	 *            the fully specified class of the InteractionsDataSource to
	 *            check
	 * @return true if an InteractionsDataSource with the given class already
	 *         exists, false otherwise
	 */
	public boolean containsSource(String source_class) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".containsSource", source_class);
		return ((Boolean)out).booleanValue();
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
	 */
	public Object callSourceMethod(String source_class, String method_name,
			Vector args) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".callSourceMethod", source_class, method_name, args);
		return out;
	}
	
	
	/**
	   * @return a Vector of Strings that specify types of IDs that this InteractionsDataSource accepts
	   * for example, "ORF","GI", etc.
	   */
	  public Vector getIDtypes () throws XmlRpcException, IOException{
		  Object out = call( this.serviceName + ".getIDtypes" );
	      return (Vector)out;
	  }
      

        /**
         * @return a Vector of Strings representing the species for which the data
         *         source contains information
         */
        public Vector getSupportedSpecies() throws XmlRpcException, IOException{
            Object out = call(this.serviceName + ".getSupportedSpecies");
            return (Vector)out;
        }
        
        /**
         * @return a Hashtable from the fully described class of the data source (String) to
         * a Vector of Strings that are the species that that data source supports
         * @throws XmlRpcException
         * @throws IOException
         */
        public Hashtable getSupportedSpeciesForEachSource() throws XmlRpcException, IOException{
            Object out = call(this.serviceName + ".getSupportedSpeciesForEachSource");
            return (Hashtable)out;
        }
        
        /**
         * @return a Hashtable from a data source's fully specified class to the data sources name available throug the getDataSourceName method
         */
        public Hashtable getSourcesNames() throws XmlRpcException, IOException{
            Object out = call(this.serviceName + ".getSourcesNames");
            return (Hashtable)out;
        }
	  
	  //------------------------ get interactions en masse --------------------
	  /**
	   * @param species
	   * @return a Vector of Hashtables, each hash contains information about an
	   * interaction and is required to contain the following entries:<br>
	   * INTERACTOR_1 --> String <br>
	   * INTERACTOR_2 --> String <br>
	   * INTERACTION_TYPE -->String <br>
	   * Each implementing class can add additional entries to the Hashtables
	   */
	  public Vector getAllInteractions (String species) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getAllInteractions", species);
		  return (Vector) out;
	  }
      
      /**
       * @param species
       * @return number of interactions
       */
      public int getNumAllInteractions (String species) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumAllInteractions", species);
          return ((Integer)out).intValue();
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
	  public Vector getAllInteractions (String species, Hashtable args) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getAllInteractions", species, args);
		  return (Vector)out;
	  }
      
      /**
       * @param species
       * @param args a table of String->Object entries that the implementing
       * class understands (for example, p-value thresholds, directed interactions, etc)
       * @return the number of interactions
       */
      public int getNumAllInteractions (String species, Hashtable args) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumAllInteractions", species, args);
          return ((Integer)out).intValue();
      }
	  
	  
	  //-------------------------- 1st neighbor methods ---------------------------
	    
	  
	  /**
	   * @param interactors a Vector of Strings (ids that the data source understands)
	   * @param species the species
	   * @return a Vector of Vectors of String ids of all the nodes that
	   * have a direct interaction with the interactors in the given input vector, positions
	   * in the input and output vectors are matched (parallel vectors)
	   */
	  public Vector getFirstNeighbors (Vector interactors, String species) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getFirstNeighbors", interactors, species);
		  return (Vector) out;
	  }
      
      /**
       * @param interactors a Vector of Strings (ids that the data source understands)
       * @param species the species
       * @return the number of neighbors
       */
      public int getNumFirstNeighbors (Vector interactors, String species) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumFirstNeighbors", interactors, species);
          return ((Integer)out).intValue();
      }
	  
	  /**
	   * @param interactor a Vector of Strings (ids that the data source understands)
	   * @param species the species
	   * @param args a table of String->Object entries that the implementing
	   * class understands (for example, p-value thresholds, directed interactions, etc)
	   * @return a Vector of Vectors of String ids of all the nodes that
	   * have a direct interaction with the interactors in the given input vector, positions
	   * in the input and output vectors are matched (parallel vectors)
	   */
	  public Vector getFirstNeighbors (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getFirstNeighbors", interactors, species, args);
		  return (Vector) out;
	  }
      
      /**
       * @param interactor a Vector of Strings (ids that the data source understands)
       * @param species the species
       * @param args a table of String->Object entries that the implementing
       * class understands (for example, p-value thresholds, directed interactions, etc)
       * @return the number of neighbors
       */
      public int getNumFirstNeighbors (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumFirstNeighbors", interactors, species, args);
          return ((Integer)out).intValue();
      }

	 
	    /**
	   * @param interactors a Vector of Strings (ids that the data source understands)
	   * @param species the species
	   * @return the number of adjacent interactions
       */
	  public int getNumAdjacentInteractions (Vector interactors, String species) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getNumAdjacentInteractions", interactors, species);
		  return ((Integer)out).intValue();
	  }
      
      /**
       * @param interactors a Vector of Strings (ids that the data source understands)
       * @param species the species
       * @return a Vector of Vectors of Hashtables, each hash contains information about an
       * interaction (they are required to contain the following entries:)<br>
       * INTERACTOR_1 --> String <br>
       * INTERACTOR_2 --> String <br>
       * INTERACTION_TYPE -->String <br>
       * Each implementing class can add additional entries to the Hashtables.<br>
       * The input and output vectors are parallel.
       */
      public Vector getAdjacentInteractions (Vector interactors, String species) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getAdjacentInteractions", interactors, species);
          return (Vector)out;
      }


	  /**
	   * @param interactor a Vector of Strings (ids that the data source understands)
	   * @param species the species
	   * @param args a table of String->Object entries that the implementing
	   * class understands (for example, p-value thresholds, directed interactions only, etc)
	   * @return a Vector of Vectors of Hashtables, each hash contains information about an
	   * interaction (they are required to contain the following entries:)<br>
	   * INTERACTOR_1 --> String <br>
	   * INTERACTOR_2 --> String <br>
	   * INTERACTION_TYPE -->String <br>
	   * Each implementing class can add additional entries to the Hashtables.<br>
	   * The input and output vectors are parallel.
	   */
	  public Vector getAdjacentInteractions (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getAdjacentInteractions", interactors, species, args);
		  return (Vector) out;
	  }
      
      /**
       * @param interactor a Vector of Strings (ids that the data source understands)
       * @param species the species
       * @param args a table of String->Object entries that the implementing
       * class understands (for example, p-value thresholds, directed interactions only, etc)
       * @return the number of adjcent interactions
       */
      public int getNumAdjacentInteractions (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumAdjacentInteractions", interactors, species, args);
          return ((Integer)out).intValue();
      }

	  //-------------------------- connecting interactions methods -----------------------

	 
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
	  public Vector getConnectingInteractions (Vector interactors, String species) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getConnectingInteractions", interactors, species);
		  return (Vector) out;
	  }
      
      /**
       * @param interactors
       * @param species
       * @return the number of connecting interactions
       */
      public int getNumConnectingInteractions (Vector interactors, String species) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumConnectingInteractions", interactors, species);
          return ((Integer)out).intValue();
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
	  public Vector getConnectingInteractions (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
		  Object out = call(this.serviceName + ".getConnectingInteractions", interactors, species, args);
		  return (Vector) out;
	  }
      
      /**
       * @param interactors
       * @param species
       * @param args a table of String->Object entries that the implementing
       * class understands (for example, p-value thresholds, directed interactions only, etc)
       * @return the number of connecting edges
       */
      public int getNumConnectingInteractions (Vector interactors, String species, Hashtable args) throws XmlRpcException, IOException{
          Object out = call(this.serviceName + ".getNumConnectingInteractions", interactors, species, args);
          return ((Integer) out).intValue();
      }
	  
	/**
	 * Not implemented in MyDataClient (to be implemented by implementing
	 * classes)
	 */
	public void test() throws Exception{}
	
	public Vector testHashAsArg (Hashtable a_hash) throws XmlRpcException, IOException{
		Object out = call(this.serviceName + ".testHashAsArg", a_hash);
		return (Vector)out;
	}

}