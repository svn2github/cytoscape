package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.io.IOException;
import java.util.*;
import java.net.*;
import org.apache.xmlrpc.*;

/**
 * Class <code>MyXmlRpcServer</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 0.6 (Tue Sep 02 11:12:29 PDT 2003)
 */
public class MyXmlRpcServer {
   WebServer webserver;
   Hashtable services, users, levels;

   public static void main( String args[] ) {
      if ( args.length < 1 ) {
	 System.err.println( "Usage: java MyXmlRpcServer <port>" );
	 System.exit( -1 );
      }

      try {
	 new MyXmlRpcServer( args );
      } catch( IOException e ) {
	 System.err.println( "Could not start server: " + e.getMessage() );
      }
   }

   public MyXmlRpcServer( int port ) throws IOException {
      //XmlRpc.setDebug( true );
      System.out.print( "Attempting to start XML-RPC server on port " + port +
			"..." );
      webserver = new WebServer( port );
      System.out.println( "...success!" );

      System.out.print( "Registering the server as a handler (service \"server\")..." );
      webserver.addHandler( "server", this );
      System.out.println( "...success!" );

      services = new Hashtable();
      services.put( "server", this );

      webserver.start();
   }

   public MyXmlRpcServer( String args[] ) throws IOException {
      this( Integer.parseInt( args[ 0 ] ) );
   }

   public WebServer getServer() { return webserver; }

   public boolean addAllowedClientIP( String ip ) {
      getServer().setParanoid( true );
      getServer().acceptClient( ip );
      return true;
   }

   public boolean addDeniededClientIP( String ip ) {
      getServer().setParanoid( true );
      getServer().denyClient( ip );
      return true;
   }

   public Vector listServiceCommands( String service ) {
      Vector out = new Vector();
      if ( services.get( service ) == null ) return out;
      Object obj = services.get( service );
      if ( obj instanceof AuthenticatedInvoker ) obj = ( (AuthenticatedInvoker) obj ).getTarget();
      java.lang.reflect.Method methods[] = obj.getClass().getMethods();
      String cname = obj.getClass().getName();
      String sname = obj.getClass().getSuperclass().getName();
      for ( int i = 0; i < methods.length; i ++ ) {
	 String mname = methods[ i ].toString();
	 if ( mname.indexOf( "java.lang.Object." ) > 0 ) continue;
	 mname = mname.replaceAll( "public ", "" );
	 mname = mname.replaceAll( "static ", "" );
	 mname = mname.replaceAll( "native ", "" );
	 mname = mname.replaceAll( "final ", "" );
	 mname = mname.replaceAll( "java.lang.", "" );
	 mname = mname.replaceAll( "java.util.", "" );
	 mname = mname.replaceAll( cname + ".", "" );
	 mname = mname.replaceAll( sname + ".", "" );
	 if ( mname.indexOf( "throws " ) > 0 ) 
	    mname = mname.substring( 0, mname.indexOf( "throws " ) - 1 );
	 if ( mname.startsWith( "void " ) ) continue;
	 out.add( mname );
      }
      return out;
   }

   public Vector listServices() {
      Vector out = new Vector();
      if ( services != null ) out.addAll( services.keySet() ); 
      return out; 
   }

   public Hashtable getServices() {
      Hashtable out = new Hashtable();
      java.util.Enumeration e = services.keys();
      while( e.hasMoreElements() ) {
	 String key = (String) e.nextElement();
	 Object obj = services.get( key );
	 if ( obj instanceof AuthenticatedInvoker ) obj = ( (AuthenticatedInvoker) obj ).getTarget();
	 out.put( key, obj.getClass().getName() );
      }
      return out;
   }

   public boolean hasService( String service ) {
      return services.get( service ) != null;
   }

   public boolean addUserNamePassword( String user, String pass, int level ) {
      System.err.println( "USERNAME = " + user + "; PASSWORD = " + pass + "; LEVEL = " + level );
      if ( users == null ) users = new Hashtable();
      users.put( user, pass );
      if ( level > 0 ) {
	 if ( levels == null ) levels = new Hashtable();
	 levels.put( user, new Integer( level ) );
      }
      return true;
   }

   public void addUserNamePassword( String uname, String passwd ) {
      addUserNamePassword( uname, passwd, -1 );
   }

   public int getUserLevel( String uname ) {
      return levels.containsKey( uname ) ? ( (Integer) levels.get( uname ) ).intValue() : -1;
   }
   
   public boolean addService( String service, String className ) throws Exception {
      Object obj = services.get( service );
      if ( obj == null ) 
	 return addService( service, Class.forName( className ).newInstance() );
      return false;
   }

   public boolean addService( String service, String className, String handlerArg1 ) 
      throws Exception {
      return addService( service, className, new String[] { handlerArg1 } );
   }

   public boolean addService( String service, String className, String handlerArg1,
			      String handlerArg2 ) 
      throws Exception {
      return addService( service, className, new String[] { handlerArg1, handlerArg2 } );
   }

   public boolean addService( String service, String className, String handlerArg1,
			      String handlerArg2, String handlerArg3 ) 
      throws Exception {
      return addService( service, className, 
			 new String[] { handlerArg1, handlerArg2, handlerArg3 } );
   }

   public boolean addService( String service, String className, Object handlerArgs[] ) 
      throws Exception {
      Object obj = services.get( service );
      if ( obj == null ) {
	 try { 
	    Class c = Class.forName( className );
	    Class types[] = new Class[ handlerArgs.length ];
	    for ( int i = 0; i < types.length; i ++ ) 
	       types[ i ] = handlerArgs[ i ].getClass();
	    java.lang.reflect.Constructor constr = c.getDeclaredConstructor( types );
	    if ( constr != null ) {
	       Object handler = constr.newInstance( handlerArgs );
	       return addService( service, handler );
	    } else {
	       System.err.println( "ERROR: REQUESTED CONSTRUCTOR NOT FOUND IN CLASS " + 
				   className );
	    }
	 } catch( Exception e ) { System.err.println( e ); e.printStackTrace(); }
      }
      return false;
   }

   public boolean addMultiCallService() throws Exception {
      boolean out = false;
      Object obj = services.get( "system" );
      if ( obj == null ) {
	 SystemHandler system = new SystemHandler();
	 system.addDefaultSystemHandlers();
	 addService( "system", system );
	 return true;
      }
      return false;
   }

   public boolean addService( String service, String className, Vector handlerArgs ) 
      throws Exception {
      return addService( service, className, 
			 (String[]) handlerArgs.toArray( new String[ 0 ] ) );
   }

   public boolean addService( String service, Object handler ) throws Exception { 
      Object obj = services.get( service );
      if ( obj == null ) {
	 String className = handler.getClass().getName();
	 System.out.print( "Registering a " + className + 
			   " as a handler (service \"" + service + "\")..." );
	 if ( users == null ) {
	    webserver.addHandler( service, handler );
	    services.put( service, handler );
	 } else {
	    AuthenticatedInvoker ai = new AuthenticatedInvoker( handler );
	    for ( Iterator it = users.keySet().iterator(); it.hasNext(); ) {
	       String user = (String) it.next();
	       int level = levels.containsKey( user ) ? ( (Integer) levels.get( user ) ).intValue() : -1;
	       ai.addUserNamePassword( user, (String) users.get( user ), level );
	    }
	    webserver.addHandler( service, ai );
	    services.put( service, ai );
	 }
	 
	 System.out.println( "...success!" );
	 return true;
      }
      return false;
   }

   public boolean removeService( String service ) {
      Object obj = services.get( service );
      if ( obj != null ) {
	 System.out.println( "Removing service \"" + service + "\"." );
	 services.remove( service );
	 webserver.removeHandler( service );
	 return true;
      }
      return false;
   }

   public boolean shutdown() {
      System.out.println( "Shutting down XML-RPC server." );
      webserver.shutdown();
      return true;
   }

   public boolean setDebug( String deb ) { return debug( Boolean.valueOf( deb ).booleanValue() ); }

   public boolean setDebug( boolean deb ) { return debug( deb ); }

   public boolean debug( boolean deb ) { 
      XmlRpc.setDebug( deb ); 
      return deb; 
   }

   public boolean status() { return true; }

   public boolean exit() {
      webserver.shutdown();
      return true;
  }
}
