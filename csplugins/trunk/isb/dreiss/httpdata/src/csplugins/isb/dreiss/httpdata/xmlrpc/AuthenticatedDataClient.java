package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.util.*;
import java.io.IOException;
import org.apache.xmlrpc.*;

/**
 * Class <code>AuthenticatedDataClient</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public abstract class AuthenticatedDataClient extends DataClient {
   protected String username = null, password = null;

   public AuthenticatedDataClient( String url ) throws XmlRpcException,
						       java.net.MalformedURLException {
      super( url );
   }

   public AuthenticatedDataClient( String url, String username, String password ) 
      throws XmlRpcException,
	     java.net.MalformedURLException {
      super( url );
      setUserNamePassword( username, password );
   }

   public void setUserNamePassword( String uname, String passwd ) {
      username = uname; password = passwd;
   }

   public synchronized int getUserLevel() throws XmlRpcException, IOException {
      if ( username == null ) return -1;
      Vector v = new Vector(); v.add( username );
      return ( (Integer) super.execute( "server.getUserLevel", v ) ).intValue();
   }

   public synchronized Object execute( String method, Vector params ) throws XmlRpcException, IOException {
      if ( password != null ) params.insertElementAt( password, 0 );
      if ( username != null ) params.insertElementAt( username, 0 );
      return super.execute( method, params );
   }
}
