package csplugins.isb.dreiss.httpdata.handlers.tspace;
import java.io.*;

import lights.*;
import lights.interfaces.*;

import csplugins.isb.dreiss.httpdata.*;

/**
 * Class <code>TSpaceDataHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public abstract class TSpaceDataHandler {
   String tsname;
   TupleSpace tspace;
   boolean changed = false;

   public TSpaceDataHandler( String tsname ) {
      this.tsname = tsname;
      tspace = new TupleSpace (tsname);
      restoreState();
      Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
         TSpaceDataHandler.this.saveState(); } } );
      Thread thr = new Thread() { public void run() {
         while( true ) { try { Thread.sleep( 60000 ); } catch( Exception e ) { };
         TSpaceDataHandler.this.saveState(); } } };
      thr.setPriority( Thread.MIN_PRIORITY );
      thr.setDaemon( true );
      thr.start();
   }

   public void saveState() {
      if ( tspace == null || changed == false ) return;
      String fname = "saved_state/" + tsname + ".ser.gz";
      System.err.println( "SAVING STATE TO " + fname );
      try {
	 File of = new File( fname );
	 if ( ! of.exists() ) of.getParentFile().mkdirs();
         OutputStream os = 
            new java.util.zip.GZIPOutputStream( new FileOutputStream( fname ) );
         ObjectOutputStream out = new ObjectOutputStream( os );
         out.writeObject( tspace );
         out.flush(); out.close();
	 changed = false;
      } catch( Exception e ) { e.printStackTrace(); }
   }

   public void restoreState() {
      try {
	 String fname = "saved_state/" + tsname + ".ser.gz";
         InputStream is = 
            new java.util.zip.GZIPInputStream( new FileInputStream( fname ) );
         ObjectInputStream in = new ObjectInputStream( is );
	 TupleSpace ts = (TupleSpace) in.readObject();
	 System.err.println( "RESTORING STATE FROM " + fname );
         if ( ts != null ) tspace = ts;
	 changed = false;
      } catch( Exception e ) { };
   }

   public static final ITuple createTuple( Object objs[] ) throws Exception {
      ITuple out = new Tuple();
      for ( int i = 0; i < objs.length; i ++ ) {
         Object obj = objs[ i ];
         if ( obj instanceof Class ) out.add( new Field().setToFormal( (Class) obj ) );
         else if ( obj instanceof Field ) out.add( (Field) obj );
         else if ( obj instanceof java.io.Serializable ) 
            out.addActual( (java.io.Serializable) obj );
      }
      return out;
   }
}
