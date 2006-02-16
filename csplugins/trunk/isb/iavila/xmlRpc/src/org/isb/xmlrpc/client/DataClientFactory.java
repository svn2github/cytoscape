package org.isb.xmlrpc.client;

import java.util.Properties;
import java.util.Vector;

import org.isb.xmlrpc.util.*;
import org.isb.xmlrpc.server.*;
import org.isb.xmlrpc.*;
import utils.*;


/**
 * Class <code>DataClientFactory</code>, to use, first start MyWebServer.
 * 
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @author <a href="mailto:iavila@systemsbiology.org">Iliana Avila-Campillo</a>
 * @version 1.0
 */

// TODO:
// - check to see if MyWebServer is running, if so, then create clients, if not, give a warning?

public class DataClientFactory {

          // TODO: Have a single file called "xmlservices.props" that contains:
          // service.client = class
          //  service.handler = class
          // etc.
	/**
	 * The default name of the file containing the client information:<br>
	 * host=<server url><br>
	 * password = <true | false><br>
	 * client.<service name> = <fully specified class of the client for this
	 * service><br>
	 * If a host is not specified, then a host running locally is assumed.
	 */
	public static final String DEFAULT_CLIENT_PROPS = "xmlrpc.props";

	public static String DEFAULT_HOST, STATIC_HOST;

	public static String USERNAME = null, PASSWORD = null;

	public static Vector DATA_CLIENTS = new Vector();
	
	public static Properties PROPERTIES;

	static {
		String propertiesFilePath = XmlRpcUtils
				.FindPropsFile(DEFAULT_CLIENT_PROPS);
		PROPERTIES = MyUtils.readProperties(propertiesFilePath);

		if(PROPERTIES != null){
			DEFAULT_HOST = (String) PROPERTIES.get("host");
		}
		
		if(DEFAULT_HOST == null){
			DEFAULT_HOST = "local";
		}
		
		STATIC_HOST = DEFAULT_HOST;
	}
    
    /**
     * Sets the URL of the host
     * 
     * @param host
     */
    public static void setHost (String host){
        STATIC_HOST = host;
    }

	/**
	 * @return the MyDataClient for the given service, the class of the given service is obtained from PROPERTIES
     * returns null if the web-server is not running
	 */
	public static MyDataClient getClient (String service) throws Exception {
	    
		if (USERNAME == null) {
			String password;
			if(PROPERTIES != null){
				password = (String) PROPERTIES.get("password");
			}else{
				System.out.println("PROPERTIES are null. Setting password to false.");
				password = "false";
			}
			
			if ("true".equals(password)){
				askForUserNamePassword();
            }else{
				USERNAME = PASSWORD = "";
            }
		}
        
        if(PROPERTIES != null){
            String className = PROPERTIES.getProperty("client." + service);
            if (className == null || className.length() <= 0)
                throw new IllegalArgumentException(
                        "DataClientFactory could not find class for service "
                        + service);
            return getClient (service, className);
        }
        return null;
    }
    

    /**
     * For all AuthenticatedDataClients, it sets their username and password.
     * 
     * @param user
     * @param pass
     */
	public static void setUserNamePassword (String user, String pass) {

		USERNAME = user;
		PASSWORD = pass;

		if ("".equals(PASSWORD))
			return;

		for (int i = 0; i < DATA_CLIENTS.size(); i++) {
			MyDataClient dc = (MyDataClient) DATA_CLIENTS.get(i);
			if (dc instanceof AuthenticatedDataClient)
				((AuthenticatedDataClient) dc).setUserNamePassword(USERNAME,
						PASSWORD);
		}
	}

    /**
     * 
     *
     */
	public static void askForUserNamePassword() {
		SetUsernamePassword sunp = new SetUsernamePassword(null);
		sunp.setUsernameAndPassword(null);
	}

	
	/**
	 * @param service the name of the service
     * @param className the fully described class of the client class
	 * @return a MyDataClient for the given service
	 */
    public static synchronized MyDataClient getClient (String service,
			String className) throws Exception {
        
		String host = STATIC_HOST;
		if (host == null || host.length() == 0)
			host = DEFAULT_HOST;

		if (host.toLowerCase().startsWith("http://")) {
			
			// assumes the server already has the handler for this client running
			Class cls = Class.forName(className);
			
			// Constructor for clients takes a String representing the URL
			java.lang.reflect.Constructor constr = cls
					.getDeclaredConstructor(new Class[] { String.class });
			
			MyDataClient dc = (MyDataClient) constr
					.newInstance(new String[] { host });

			if (dc instanceof AuthenticatedDataClient)
				if (!"".equals(PASSWORD))
					((AuthenticatedDataClient) dc).setUserNamePassword(
							USERNAME, PASSWORD);
			DATA_CLIENTS.add(dc);

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
				System.out.println("Service " + service + " is not running in localhost");
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
					// XmlRpcUtils.startService( "blast", localhost, className,
					// new String[] { blastExe, sequenceDataDir } );
					// else XmlRpcUtils.startService( "blast", localhost, className,
					// null );
					// okay = true;
					// Thread.sleep( 1000 );
				} else {
					// Not blast
					
					// David's comment: start, e.g. "sequence" handler reading table "sequences"
					
					// Iliana's comment: this is dependant on the table names!!! we will
					// need to have static variables with table names
					// Why in the local case, do we make sure that the handler is running?
					System.out.println("Starting service " + service + " on localhost.");
					XmlRpcUtils.startService(service, localhost, className,
							new String[] { service + "s" });
					
					okay = true;
				}
			}// !running

			if (okay){
				Class cls = Class.forName(className);
				java.lang.reflect.Constructor constr = cls
						.getDeclaredConstructor(new Class[] { String.class });
				MyDataClient dc = (MyDataClient) constr
						.newInstance(new String[] { localhost });

				if (dc instanceof AuthenticatedDataClient)
					if (!"".equals(PASSWORD))
						((AuthenticatedDataClient) dc).setUserNamePassword(
								USERNAME, PASSWORD);

				DATA_CLIENTS.add(dc);
				
				System.out.println("getClient returning client = " + dc);

				return dc;
			}

		} else
			throw new IllegalArgumentException(
					"DataClientFactory could not parse uri '" + host + "'");
		return null;
	}
}
