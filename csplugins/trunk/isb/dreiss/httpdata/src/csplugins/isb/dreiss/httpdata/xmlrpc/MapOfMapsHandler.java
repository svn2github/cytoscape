package csplugins.isb.dreiss.httpdata.xmlrpc;

import java.util.Hashtable;
import java.util.Vector;
import java.io.*;
import org.apache.xmlrpc.XmlRpc;

public class MapOfMapsHandler {
   String fname = "theMaps.ser.gz";
   Hashtable theMap = new Hashtable();

   public MapOfMapsHandler() {
      restoreState();
      Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	 MapOfMapsHandler.this.saveState(); } } );
      Thread thr = new Thread() { public void run() {
	 while( true ) { try { Thread.sleep( 60000 ); } catch( Exception e ) { };
	 MapOfMapsHandler.this.saveState(); } } };
      thr.setPriority( Thread.MIN_PRIORITY );
      thr.setDaemon( true );
      thr.start();
   }

   public Hashtable get() { return (Hashtable) theMap; }
   public Hashtable get( String name ) { return (Hashtable) theMap.get( name ); }
   public String put( String name, String key, String val ) {
      if ( theMap.get( name ) == null ) theMap.put( name, new Hashtable() ); 
      Object out = ( (Hashtable) theMap.get( name ) ).put( key, val ); 
      return out != null ? out.toString() : "null"; }
   public String get( String name, String key ) {
      if ( theMap.get( name ) == null ) return "null"; 
      Object out = ( (Hashtable) theMap.get( name ) ).get( key ); 
      return out != null ? out.toString() : "null"; }
   public boolean putAll( String name, Hashtable t ) {
      if ( theMap.get( name ) == null ) theMap.put( name, new Hashtable() );  
      ( (Hashtable) theMap.get( name ) ).putAll( t ); return true; }
   public boolean containsMap( String name ) { return theMap.get( name ) != null; }
   public boolean containsKey( String name, String key ) {
      return theMap.get( name ) != null ? ( (Hashtable) theMap.get( name ) ).
	 containsKey( key ) : false; }
   public boolean containsValue( String name, String val ) {
      return theMap.get( name ) != null ? ( (Hashtable) theMap.get( name ) ).
	 containsValue( val ) : false; }
   public Vector maps() {
      Vector out = new Vector();
      if ( theMap != null ) out.addAll( theMap.keySet() ); return out; }
   public Vector keys( String name ) {
      Vector out = new Vector();
      if ( theMap.get( name ) != null ) out.addAll( ( (Hashtable) theMap.get( name ) ).
						    keySet() ); return out; }
   public Vector values( String name ) {
      Vector out = new Vector();
      if ( theMap.get( name ) != null ) out.addAll( ( (Hashtable) theMap.get( name ) ).
						    values() ); return out; }
   public String remove( String name, String key ) {
      if ( theMap.get( name ) == null ) return "null"; 
      Object out = ( (Hashtable) theMap.get( name ) ).remove( key ); 
      if ( ( (Hashtable) theMap.get( name ) ).size() <= 0 ) theMap.remove( name );
      return out != null ? out.toString() : "null"; }
   public boolean clear( String name ) { 
      if ( theMap.get( name ) != null ) { ( (Hashtable) theMap.get( name ) ).clear(); 
      theMap.remove( name ); return true; } return false; }
   public int size( String name ) { 
      return theMap.get( name ) != null ? ( (Hashtable) theMap.get( name ) ).size() : 
	 0; }

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
