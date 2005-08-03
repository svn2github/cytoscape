package org.isb.xmlrpc.handler.interactions;

import java.lang.*;
import java.util.*;
import java.lang.reflect.*;
import org.isb.xmlrpc.handler.DataSource;

/**
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 */
public class InteractionsHandler implements InteractionsDataSource {

	/**
	 * A collection of InteractionsDataSource objects
	 */
	protected Vector interactionSources;

	/**
	 * Constructor
	 */
	public InteractionsHandler() {
		this.interactionSources = new Vector();
	}

	/**
	 * 
	 * @param interaction_sources
	 *            a Vector of Strings of fully specified classes of
	 *            InteractionDataSources
	 */
	public InteractionsHandler(Vector interaction_sources) {
		this.interactionSources = new Vector();
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
	public boolean removeSource(String source_class) {
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
			return this.interactionSources.remove(sourceToRemove);
		}
		return false;
	}

	/**
	 * Add a source of interactions
	 * 
	 * @param interaction_source
	 *            fully specified class of the InteractionDataSource to add
	 * @return true if the source was added, false if it was not added (e.g. if
	 *         it was already there or there was an exception during creation)
	 */
	public boolean addSource(String source_class) {

		try {

			Class classForName = Class.forName(source_class);
			Object obj = classForName.newInstance();
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return true;
			}

			System.err
					.println("Requested class for source is not an InteractionsDataSource:"
							+ source_class);
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false; // for now
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
	 */
	public boolean addSource(String source_class, Object arg) {

		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] { arg
					.getClass() });
			Object obj = cons.newInstance(new Object[] { arg });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return true;
			}

			System.out
					.println("Requested class for source is not an InteractionsDataSource:"
							+ source_class);
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false; // for now
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
	 */
	public boolean addSource(String source_class, Object arg1, Object arg2) {

		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] {
					arg1.getClass(), arg2.getClass() });
			Object obj = cons.newInstance(new Object[] { arg1, arg2 });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return true;
			}

			System.out
					.println("Requested class for source is not an InteractionsDataSource:"
							+ source_class);
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false; // for now
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
	 */
	public boolean addSource(String source_class, Object arg1, Object arg2,
			Object arg3) {

		try {

			Class classForName = Class.forName(source_class);
			Constructor cons = classForName.getConstructor(new Class[] {
					arg1.getClass(), arg2.getClass(), arg3.getClass() });
			Object obj = cons.newInstance(new Object[] { arg1, arg2, arg3 });
			if (obj instanceof InteractionsDataSource) {
				this.interactionSources.add(obj);
				return true;
			}

			System.out
					.println("Requested class for source is not an InteractionsDataSource:"
							+ source_class);
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false; // for now
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
	 * @param source_class
	 *            the fully specified class of the InteractionsDataSource to
	 *            check
	 * @return true if an InteractionsDataSource with the given class already
	 *         exists, false otherwise
	 */
	public boolean containsSource(String source_class) {
		Iterator it = this.interactionSources.iterator();
		while (it.hasNext()) {
			InteractionsDataSource dataSource = (InteractionsDataSource) it
					.next();
			if (dataSource.equals(source_class)) {
				return true;
			}
		}// while it.hasNext()
		return false;
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
			Vector args) {
		Iterator it = this.interactionSources.iterator();
		InteractionsDataSource dataSource = null;
		while (it.hasNext()) {
			InteractionsDataSource source = (InteractionsDataSource) it.next();
			if (source.getClass().equals(source_class)) {
				dataSource = source;
				break;
			}
		}// while

		if (dataSource == null)
			return Boolean.FALSE;

		try {

			Class[] argTypes = new Class[args.size()];
			for (int i = 0; i < args.size(); i++) {
				argTypes[i] = args.get(i).getClass();
			}

			Method method = dataSource.getClass().getDeclaredMethod(
					method_name, argTypes);
			Object returnedObject = method.invoke(dataSource, args.toArray());
			return returnedObject;

		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		}// catch
	}

	// -------------- Methods that implement InteractionsDataSource ----//

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
		return new Vector();
	}

	/**
	 * @return false always
	 */
	public boolean requiresPassword() {
		return false;
	}

	/**
	 * @return "" always
	 */
	public String getVersion() {
		return "";
	}

	/**
	 * @return a Vector of Strings that specify types of IDs that this
	 *         InteractionsDataSource accepts for example, "ORF","GI", etc.
	 */
	public Vector getIDtypes() {
		return new Vector();
	}

	// ------------------------ get interactions en masse --------------------
	/**
	 * @param species
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction and is required to contain the following entries:<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getAllInteractions(String species) {
		return new Vector();
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
		return new Vector();
	}

	// -------------------------- 1st neighbor methods
	// ---------------------------

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
	public Vector getFirstNeighbors(String interactor, String species) {
		return new Vector();
	}

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions, etc)
	 * @return a Vector of Strings of all the nodes that have a direct
	 *         interaction with "interactor" and that take into account
	 *         additional parameters given in the Hashtable (an empty vector if
	 *         the interactor is not found, the interactor has no interactions,
	 *         or the data source does not contain infomation for the given
	 *         interactor)
	 */
	public Vector getFirstNeighbors(String interactor, String species,
			Hashtable args) {
		return new Vector();
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
		return new Vector();
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
		return new Vector();
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
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getAdjacentInteractions(String interactor, String species) {
		return new Vector();
	}

	/**
	 * @param interactor
	 *            an id that the data source understands
	 * @param species
	 *            the species
	 * @param args
	 *            a table of String->Object entries that the implementing class
	 *            understands (for example, p-value thresholds, directed
	 *            interactions only, etc)
	 * @return a Vector of Hashtables, each hash contains information about an
	 *         interaction (they are required to contain the following entries:)<br>
	 *         INTERACTOR_1 --> String <br>
	 *         INTERACTOR_2 --> String <br>
	 *         INTERACTION_TYPE -->String <br>
	 *         Each implementing class can add additional entries to the
	 *         Hashtables
	 */
	public Vector getAdjacentInteractions(String interactor, String species,
			Hashtable args) {
		return new Vector();
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
		return new Vector();
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
		return new Vector();
	}

	// -------------------------- connecting interactions methods
	// -----------------------

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
		return new Vector();
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
		return new Vector();
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
		return new Vector();
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
		return new Vector();
	}

}// InteractionsDataSource
