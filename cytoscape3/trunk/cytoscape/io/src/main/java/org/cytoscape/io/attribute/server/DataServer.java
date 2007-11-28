/**
 * 
 */
package org.cytoscape.io.attribute.server;



/**
 * @author skillcoy
 *
 */
public class DataServer {
	private static BioDataServer bioDataServer;
	/**
	 * New ontology server. This will replace BioDataServer.
	 */
	private static OntologyServer ontologyServer;

	/**
	 * A BioDataServer should be loadable from a file systems file or from a
	 * URL.
	 */
	public static BioDataServer loadBioDataServer(String location) {
		try {
			bioDataServer = new BioDataServer(location);
		} catch (Exception e) {
			System.err.println("Could not Load BioDataServer from: " + location);

			return null;
		}

		return bioDataServer;
	}

	/**
	 * @return the BioDataServer that was loaded, should not be null, but not
	 *         contain any data.
	 */
	public static BioDataServer getBioDataServer() {
		return bioDataServer;
	}

	/**
	 * This will replace the bioDataServer.
	 */
	public static OntologyServer buildOntologyServer() {
		try {
			ontologyServer = new OntologyServer();
		} catch (Exception e) {
			System.err.println("Could not build OntologyServer.");
			e.printStackTrace();

			return null;
		}

		return ontologyServer;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static OntologyServer getOntologyServer() {
		return ontologyServer;
	}

	
}
