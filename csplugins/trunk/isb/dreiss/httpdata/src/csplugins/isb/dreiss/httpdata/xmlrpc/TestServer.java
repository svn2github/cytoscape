package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.io.IOException;

/**
 * Class <code>TestServer</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 0.6 (Tue Sep 02 11:12:29 PDT 2003)
 */
public class TestServer extends MyXmlRpcServer {
   public TestServer( String args[] ) throws IOException { super( args ); }

   public static void main( String args[] ) {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java TestServer <port>" );
	 System.exit( -1 );
      }

      try {
	 TestServer server = new TestServer( args );
	 server.addService( "map", "csplugins.isb.dreiss.httpdata.xmlrpc.MapHandler" );
	 server.addService( "mom", "csplugins.isb.dreiss.httpdata.xmlrpc.MapOfMapsHandler" );
	 server.addService( "oh", "csplugins.isb.dreiss.httpdata.xmlrpc.ObjectHandler" );
	 //server.addService( "ht", "java.util.Hashtable" );
      } catch( Exception e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }
}
