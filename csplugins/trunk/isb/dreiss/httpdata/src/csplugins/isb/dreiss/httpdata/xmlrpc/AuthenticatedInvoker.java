package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.io.IOException;
import java.util.*;

import org.apache.xmlrpc.*;

public class AuthenticatedInvoker extends Invoker {
   Object target = null;
   Hashtable users = null, levels = null;

   public AuthenticatedInvoker( Object target ) {
      super( target );
      this.target = target;
   }

   public AuthenticatedInvoker( Object target, String username, String password ) {
      this( target );
      addUserNamePassword( username, password );
   }

   public Object getTarget() { return target; }

   public void addUserNamePassword( String uname, String passwd, int level ) {
      if ( users == null ) users = new Hashtable();
      users.put( uname, passwd );
      if ( level > 0 ) {
	 if ( levels == null ) levels = new Hashtable();
	 levels.put( uname, new Integer( level ) );
      }
   }

   public void addUserNamePassword( String uname, String passwd ) {
      addUserNamePassword( uname, passwd, -1 );
   }

   public int getLevelForUser( String user ) {
      if ( levels.get( user ) == null ) return -1;
      return ( (Integer) levels.get( user ) ).intValue();
   }

   public Object execute( String methodName, Vector params ) throws Exception {
      if ( users == null ) return super.execute( methodName, params );
      if ( params.size() < 2 ) throw new Exception( "No username or password supplied." );
      String user = (String) params.get( 0 );
      String pass = (String) params.get( 1 );
      if ( ! users.containsKey( user ) ) throw new Exception( "Incorrect username supplied." );
      else if ( ! pass.equals( (String) users.get( user ) ) ) throw new Exception( "Incorrect password supplied." );
      params.removeElementAt( 0 );
      params.removeElementAt( 0 );
      Object out = super.execute( methodName, params );

      // if object responds with a 2-element hashtable, key1="level" with allowable data level for response, and
      // key2="response" with the actual response, then check the level against the user's permission level.
      if ( out instanceof Hashtable && levels != null && levels.containsKey( user ) && 
	   ( (Hashtable) out ).containsKey( "levelXXX" ) && ( (Hashtable) out ).containsKey( "responseXXX" ) ) {
	 int level = ( (Integer) ( (Hashtable) out ).get( "level" ) ).intValue();
	 int usrLevel = ( (Integer) levels.get( user ) ).intValue();
	 if ( level > usrLevel ) 
	    throw new Exception( "User's permission level is too low (" + usrLevel + " < " + level + ")" );
	 out = ( (Hashtable) out ).get( "responseXXX" );
      }
      return out;
   }   
}
