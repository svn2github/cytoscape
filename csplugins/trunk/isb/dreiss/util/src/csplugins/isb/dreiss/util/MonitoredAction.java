package csplugins.isb.dreiss.util;

import java.awt.event.*;
import javax.swing.*;

/**
 * Class <code>MonitoredAction</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class MonitoredAction extends AbstractAction {
   ProgressMonitor monitor;
   protected boolean done = false;
   String msg;
   JPanel panel;

   public MonitoredAction( JPanel panel, String name, String msgString ) { 
      super( name ); 
      this.msg = msgString;
      this.panel = panel;
   }

   public MonitoredAction( JPanel panel, String name, String msgString, int max ) { 
      this( panel, name, msgString ); 
      monitor = new ProgressMonitor( panel, new Object[] { msg }, "", 0, max );
      monitor.setMillisToDecideToPopup( 0 );
      monitor.setMillisToPopup( 0 );
      done = false;
   }

   public boolean done() { return done; }

   public void actionPerformed( ActionEvent e ) { };

   public void setProgress( int value, int max, String note ) {
      if ( monitor == null ) {
	 monitor = new ProgressMonitor( panel, new Object[] { msg }, note, 0, max );
	 monitor.setMillisToDecideToPopup( 0 );
	 monitor.setMillisToPopup( 0 );
	 done = false;
      }

      if ( monitor != null ) {
	 monitor.setProgress( value );  
	 monitor.setNote( note );
	 if ( monitor.isCanceled() || value >= max ) {
	    monitor.close();
	    monitor = null;
	    done = true;
	 }
      }
   }
}
