package csplugins.isb.dreiss.httpdata.client;

import java.util.Properties;
import java.util.Vector;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.httpdata.handlers.*;
import csplugins.isb.dreiss.util.*;

/**
 * Class <code>DataClientFactory</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class DataClientFactory {
   public static String DEFAULT_HOST, STATIC_HOST;
   public static String USERNAME = null, PASSWORD = null;
   public static Vector dataClients = new Vector();

   static {
      Properties properties = MyUtils.readProperties( "csplugins/isb/dreiss/httpdata.properties" );
      try { DEFAULT_HOST = (String) properties.get( "fetchers.host" ); }
      catch ( Exception ee ) { ee.printStackTrace(); DEFAULT_HOST = "local"; }
      STATIC_HOST = DEFAULT_HOST;
   }

   public static DataClient getClient( String service ) throws Exception {
      Properties props = MyUtils.readProperties( "csplugins/isb/dreiss/httpdata.properties" );

      if ( USERNAME == null ) {
	 String auth = "true";
	 try { auth = (String) props.get( "fetchers.auth" ); }
	 catch ( Exception ee ) { auth = "false"; }
	 if ( "true".equals( auth ) ) askForUserNamePassword();
	 else { USERNAME = PASSWORD = ""; }
      }

      return getClient( service, props );
   }

   public static void setUserNamePassword( String user, String pass ) {
      USERNAME = user;
      PASSWORD = pass;
      for ( int i = 0; i < dataClients.size(); i ++ ) {
	 DataClient dc = (DataClient) dataClients.get( i );
	 if ( dc instanceof AuthenticatedDataClient )
	    if ( ! "".equals( PASSWORD ) ) ( (AuthenticatedDataClient) dc ).setUserNamePassword( USERNAME, PASSWORD );
      }
   }

   public static void askForUserNamePassword() {
      SetUsernamePassword sunp = new SetUsernamePassword( null );
      sunp.setUsernameAndPassword( null );
   }

   public static synchronized DataClient getClient( String service, Properties props ) throws Exception {
      // NOTE: tspace-based handlers are deprecated and will no longer work!!!

      // Example lines in properties to use this class:
      // fetchers.host = http://localhost:8081
      // #fetchers.host = local # This is for a local hsqldb-based server
      // fetchers.favoriteHomolog = csplugins.httpdata.client.FavoriteHomologClient
      // fetchers.sequence = csplugins.httpdata.client.SequenceClient
      // fetchers.synonym = csplugins.httpdata.client.SynonymClient
      // fetchers.interaction = csplugins.httpdata.client.InteractionClient
      // fetchers.homolog = csplugins.httpdata.client.HomologClient
      // fetchers.blast = csplugins.httpdata.client.BlastClient
      // # You only need these props if you're running the servers locally (within a cytoscape instance):
      // #fetchers.blast.pathToExe=/local/dreiss/packages/biostuff/blast-2.2.6/blastall
      // #fetchers.blast.dataDir=/data/seqdb/blastformat/
      // #fetchers.blast.useSGDBlast=false

      // Then call, e.g.:
      // InteractionClient interactionFetcher = 
      //       (InteractionClient) DataClientFactory.getClient( "interaction", props );

      String host = STATIC_HOST;
      if ( host == null || host.length() == 0 ) host = DEFAULT_HOST;
      System.err.println( "HOST = " + host );

      String cName = props.getProperty( "fetchers." + service );
      if ( cName == null || cName.length() <= 0 ) 
	 throw new IllegalArgumentException( "DataClientFactory could not find class for service " + service );
      
      if ( host.toLowerCase().startsWith( "http://" ) ) {
	 Class cls = Class.forName( cName );
	 java.lang.reflect.Constructor constr = cls.getDeclaredConstructor( new Class[] { String.class } );
	 DataClient dc = (DataClient) constr.newInstance( new String[] { host } );
	 if ( dc instanceof AuthenticatedDataClient ) 
	    if ( ! "".equals( PASSWORD ) ) ( (AuthenticatedDataClient) dc ).setUserNamePassword( USERNAME, PASSWORD );
	 dataClients.add( dc );
	 return dc;
      }
      
      else if ( host.toLowerCase().startsWith( "local" ) ) {
	 /* Locally-running database-based handler. Need to do this:
	    0. check if we have a running xml-rpc web server running on localhost:8081
	    (assume we're always using MyXmlRpcServer and call "server.status")
	    1. if not, start one up and cache the fact that we started one (port=8081?)
	    2. check if the requested client's handler is registered with the server
	    (call "server.hasService")
	    3. if it's not, register one and cache the fact that we did it
	    4. return a new client that is setup to talk to localhost:8081 */
	 int localPort = 8081;
	 XmlRpcUtils.startWebServerIfNeeded( localPort );
	 Thread.sleep( 1000 );
	 String localhost = "http://localhost:" + localPort;
	 boolean running = XmlRpcUtils.isServiceRunning( service, localhost );
	 boolean okay = false;
	 if ( ! running ) {
	    if ( service.equalsIgnoreCase( "blast" ) ) {
	       String blastExe = "/local/dreiss/packages/biostuff/blast-2.2.6/blastall";
	       String sequenceDataDir = "/data/seqdb/blastformat/";
	       String useSGDblast = "false"; 
	       if ( props != null ) {
		  blastExe = props.getProperty( "fetchers.blast.pathToExe", blastExe );
		  sequenceDataDir = props.getProperty( "fetchers.blast.dataDir",
						       sequenceDataDir );
		  useSGDblast = props.getProperty( "fetchers.blast.useSGDBlast", useSGDblast );
	       }
	       if ( "false".equals( useSGDblast ) ) 
		  XmlRpcUtils.startService( "blast", localhost, cName,
					    new String[] { blastExe, sequenceDataDir } );
	       else XmlRpcUtils.startService( "blast", localhost, cName, null );
	       okay = true;
	       Thread.sleep( 1000 );
	    } else { // start, e.g. "sequence" handler reading table "sequences"
	       XmlRpcUtils.startService( service, localhost, cName, new String[] { service + "s" } );
	       okay = true;
	    }
	 }

	 if ( okay ) {
	    Class cls = Class.forName( cName );
	    java.lang.reflect.Constructor constr = cls.getDeclaredConstructor( new Class[] { String.class } );
	    DataClient dc = (DataClient) constr.newInstance( new String[] { localhost } );
	    if ( dc instanceof AuthenticatedDataClient )
	       if ( ! "".equals( PASSWORD ) ) ( (AuthenticatedDataClient) dc ).setUserNamePassword( USERNAME, PASSWORD );
	    dataClients.add( dc );
	    return dc;
	 }

      } else throw new IllegalArgumentException( "DataClientFactory could not parse uri '" + host + "'");
      return null;
   }
}
