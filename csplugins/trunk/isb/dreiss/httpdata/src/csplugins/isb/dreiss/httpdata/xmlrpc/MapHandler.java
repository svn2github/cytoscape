package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.util.Hashtable;
import java.util.Vector;
import java.io.*;
import org.apache.xmlrpc.XmlRpc;

/**
 * Class <code>MapHandler</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 0.6 (Tue Sep 02 11:12:29 PDT 2003)
 */
public class MapHandler {
   String fname = "theMap.ser.gz";
   Hashtable theMap;

   public MapHandler() {
      restoreState();
      Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	 MapHandler.this.saveState(); } } );
      Thread thr = new Thread() { public void run() {
	 while( true ) { try { Thread.sleep( 60000 ); } catch( Exception e ) { };
	 MapHandler.this.saveState(); } } };
      thr.setPriority( Thread.MIN_PRIORITY );
      thr.setDaemon( true );
      thr.start();
   }

   public Hashtable get() { return (Hashtable) theMap; }
   public String put( String key, String val ) {
      if ( theMap == null ) theMap = new Hashtable(); 
      Object out = theMap.put( key, val ); 
      return out != null ? out.toString() : "null"; }
   public String get( String key ) {
      if ( theMap == null ) return "null"; 
      Object out = theMap.get( key ); return out != null ? out.toString() : "null"; }
   public boolean putAll( Hashtable t ) {
      if ( theMap == null ) theMap = new Hashtable(); 
      theMap.putAll( t ); return true; }
   public boolean containsKey( String key ) {
      return theMap != null ? theMap.containsKey( key ) : false; }
   public boolean containsValue( String val ) {
      return theMap != null ? theMap.containsValue( val ) : false; }
   public Vector keys() {
      Vector out = new Vector();
      if ( theMap != null ) out.addAll( theMap.keySet() ); return out; }
   public Vector values() {
      Vector out = new Vector();
      if ( theMap != null ) out.addAll( theMap.values() ); return out; }
   public String remove( String key ) {
      if ( theMap == null ) return "null"; 
      Object out = theMap.remove( key ); 
      if ( theMap.size() <= 0 ) theMap = null;
      return out != null ? out.toString() : "null"; }
   public boolean clear() { 
      if ( theMap != null ) { theMap.clear(); theMap = null; return true; } 
      return false; }
   public int size() { 
      return theMap != null ? theMap.size() : 0; }

   public void saveState() {
      if ( theMap == null ) return;
      System.err.println( "SAVING STATE TO " + fname );
      try {
	 OutputStream os = 
	    new java.util.zip.GZIPOutputStream( new FileOutputStream( fname ) );
	 ObjectOutputStream out = new ObjectOutputStream( os );
	 out.writeObject( theMap );
	 out.flush(); out.close();
      } catch( Exception e ) { e.printStackTrace(); }
   }

   public void restoreState() {
      try {
	 InputStream is = 
	    new java.util.zip.GZIPInputStream( new FileInputStream( fname ) );
	 ObjectInputStream in = new ObjectInputStream( is );
	 Hashtable map = (Hashtable) in.readObject();
	 if ( theMap != null ) theMap.putAll( map ); else theMap = map;
      } catch( Exception e ) { };
   }
}
