package csplugins.isb.dreiss.util;

import java.io.*;
import corejava.Format;

/**
 * Class <code>MyErr</code>
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9978 (Fri Nov 07 05:56:26 PST 2003)
 */
public class MyErr extends MyOut {
   public MyErr() {
      this( System.err );
   }

   public MyErr( PrintStream oldErr ) {
      super( oldErr );
   }
}
