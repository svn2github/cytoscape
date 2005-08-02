package org.isb.xmlrpc.client;

import java.util.Properties;
import java.util.Vector;

import org.isb.xmlrpc.db.server.*;
import org.isb.xmlrpc.db.handlers.*;
import utils.*;

/**
 * Class <code>DataClientFactory</code>
 * 
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @version 1.0
 */

// TODO:
// -getClient method needs to be less table dependant
public class DataClientFactory {

	/**
	 * The default name of the file containing the client information:<br>
	 * host=<server url><br>
	 * password = <true | false><br>
	 * client.<service name> = <fully specified class of the client for this
	 * service><br>
	 * If a host is not specified, then a host running locally is assumed.
	 */
	public static final String DEFAULT_CLIENT_PROPS = "client.props";

	public static String DEFAULT_HOST, STATIC_HOST;

	public static String USERNAME = null, PASSWORD = null;

	public static Vector dataClients = new Vector();

	public static Properties properties;

	static {
		String propertiesFilePath = XmlRpcUtils
				.FindPropsFile(DEFAULT_CLIENT_PROPS);
		properties = MyUtils.readProperties(propertiesFilePath);

		try {
			DEFAULT_HOST = (String) properties.get("host");
		} catch (Exception ee) {
			ee.printStackTrace();
			DEFAULT_HOST = "local";
		}
		STATIC_HOST = DEFAULT_HOST;
	}

	/**
	 * @return the MyDataClient for the given service
	 */
	public static MyDataClient getClient(String service) throws Exception {

		if (USERNAME == null) {
			String password = "true";
			try {
				password = (String) properties.get("password");
			} catch (Exception ee) {
				password = "false";
			}
			if ("true".equals(password))
				askForUserNamePassword();
			else
				USERNAME = PASSWORD = "";
		}

		return getClient(service, properties);
	}

	public static void setUserNamePassword(String user, String pass) {

		USERNAME = user;
		PASSWORD = pass;

		if ("".equals(PASSWORD))
			return;

		for (int i = 0; i < dataClients.size(); i++) {
			MyDataClient dc = (MyDataClient) dataClients.get(i);
			if (dc instanceof AuthenticatedDataClient)
				((AuthenticatedDataClient) dc).setUserNamePassword(USERNAME,
						PASSWORD);
		}
	}

	public static void askForUserNamePassword() {
		SetUsernamePassword sunp = new SetUsernamePassword(null);
		sunp.setUsernameAndPassword(null);
	}

	// Iliana comments: this method contains a bunch of Strings that should be
	// user params

	/**
	 * @param props
	 *            contains client.props settings
	 * @return a MyDataClient for the given service
	 * 
	 * client.props file sample for this method:<br>
	 * 
	 * host = http://localhost:8081 #host = local # This is for a local
	 * hsqldb-based server<br>
	 * client.favoriteHomolog = org.isb.xmlrpc.client.FavoriteHomologClient<br>
	 * client.sequence = org.isb.xmlrpc.client.SequenceClient<br>
	 * client.synonym = org.isb.xmlrpc.client.SynonymClient<br>
	 * client.interaction = org.isb.xmlrpc.client.InteractionClient<br>
	 * client.homolog = org.isb.xmlrpc.client.HomologClient<br>
	 * client.blast = org.isb.xmlrpc.client.BlastClient<br> # You only need
	 * these props if you're running the servers locally (within a cytoscape
	 * instance):<br>
	 * #client.blast.pathToExe=/local/dreiss/packages/biostuff/blast-2.2.6/blastall<br>
	 * #client.blast.dataDir=/data/seqdb/blastformat/<br>
	 * #client.blast.useSGDBlast=false<br>
	 * 
	 * Then call, e.g.: InteractionClient interactionFetcher =<br>
	 * (InteractionClient)DataClientFactory.getClient("interaction", props);<br>
	 */
	public static synchronized MyDataClient getClient (String service,
			Properties props) throws Exception {

		String host = STATIC_HOST;
		if (host == null || host.length() == 0)
			host = DEFAULT_HOST;

		System.err.println("HOST = " + host);

		String cName = props.getProperty("client." + service);
		if (cName == null || cName.length() <= 0)
			throw new IllegalArgumentException(
					"DataClientFactory could not find class for service "
							+ service);

		if (host.toLowerCase().startsWith("http://")) {
			
			// running on another server
			// assumes the server already has the handler for this client running
			Class cls = Class.forName(cName);
			
			// Constructor for clients takes a String representing the URL
			java.lang.reflect.Constructor constr = cls
					.getDeclaredConstructor(new Class[] { String.class });
			
			MyDataClient dc = (MyDataClient) constr
					.newInstance(new String[] { host });

			if (dc instanceof AuthenticatedDataClient)
				if (!"".equals(PASSWORD))
					((AuthenticatedDataClient) dc).setUserNamePassword(
							USERNAME, PASSWORD);
			dataClients.add(dc);

			return dc;

		} else if (host.toLowerCase().startsWith("local")) {
			/*
			 * Locally-running database-based handler. Need to do this: 
			 * 0. check if we have a running xml-rpc web server running on localhost:8081
			 * (assume we're always using MyWebServer and call "server.status")
			 * 1. if not, start one up and cache the fact that we started one
			 * (port=8081?) 
			 * 2. check if the requested client's handler is
			 * registered with the server (call "server.hasService") 
			 * 3. if it's not, register one and cache the fact that we did it
			 * 4. return a new client that is setup to talk to localhost:8081
			 */
			int localPort = 8081;
			XmlRpcUtils.startWebServerIfNeeded(localPort);
			Thread.sleep(1000);
			String localhost = "http://localhost:" + localPort;
			boolean running = XmlRpcUtils.isServiceRunning(service, localhost);
			boolean okay = false;
			
			// TODO: This needs cleaning up!!!! (iliana)
			
			if (!running) {
				
				if(service.equalsIgnoreCase("blast")) {
					// For now, we are not using BLAST (Iliana)
					System.err
							.println("blast client will not be returned, we don't support that service yet");

					// Iliana : this needs to be user parameters
					// String blastExe =
					// "/local/dreiss/packages/biostuff/blast-2.2.6/blastall";
					// String sequenceDataDir = "/data/seqdb/blastformat/";
					// String useSGDblast = "false";
					// if ( props != null ) {
					// blastExe = props.getProperty( "fetchers.blast.pathToExe",
					// blastExe );
					// sequenceDataDir = props.getProperty(
					// "fetchers.blast.dataDir",
					// sequenceDataDir );
					// useSGDblast = props.getProperty(
					// "fetchers.blast.useSGDBlast", useSGDblast );
					// }
					// if ( "false".equals( useSGDblast ) )
					// XmlRpcUtils.startService( "blast", localhost, cName,
					// new String[] { blastExe, sequenceDataDir } );
					// else XmlRpcUtils.startService( "blast", localhost, cName,
					// null );
					// okay = true;
					// Thread.sleep( 1000 );
				} else {
					// Not blast
					
					// David's comment: start, e.g. "sequence" handler reading table "sequences"
					
					// Iliana's comment: this is dependant on the table names!!! we will
					// need to have static variables with table names
					// Why in the local case, do we make sure that the handler is running?
					XmlRpcUtils.startService(service, localhost, cName,
							new String[] { service + "s" });
					
					okay = true;
				}
			}// !running

			if (okay){
				Class cls = Class.forName(cName);
				java.lang.reflect.Constructor constr = cls
						.getDeclaredConstructor(new Class[] { String.class });
				MyDataClient dc = (MyDataClient) constr
						.newInstance(new String[] { localhost });

				if (dc instanceof AuthenticatedDataClient)
					if (!"".equals(PASSWORD))
						((AuthenticatedDataClient) dc).setUserNamePassword(
								USERNAME, PASSWORD);

				dataClients.add(dc);

				return dc;
			}

		} else
			throw new IllegalArgumentException(
					"DataClientFactory could not parse uri '" + host + "'");
		return null;
	}
}
