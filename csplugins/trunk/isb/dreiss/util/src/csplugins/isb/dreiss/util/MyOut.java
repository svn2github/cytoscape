package csplugins.isb.dreiss.util;

import java.io.*;
import corejava.Format;

/**
 * Class <code>MyOut</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9978 (Fri Nov 07 05:56:26 PST 2003)
 *
 * An extension to PrintStream that allows formatted printing
 */
public class MyOut extends PrintStream {
   protected int debug = 0;
   protected PrintStream saveOut;
   public PrintStream LOG;

   public MyOut() {
      this( System.out );
   }

   public MyOut( PrintStream oldOut ) {
      super( oldOut, true );
   }

   public void printf( String fmt, double val ) {
      Format.print( this, fmt, val ); 
   }

   public void printf( String fmt, int val ) {
      Format.print( this, fmt, val ); 
   }

   public void printf( String fmt, String val ) {
      Format.print( this, fmt, val ); 
   }
}
