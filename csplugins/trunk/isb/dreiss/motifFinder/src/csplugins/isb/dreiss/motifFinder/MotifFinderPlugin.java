package csplugins.isb.dreiss.motifFinder;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

/**
 * Class <code>MotifFinderPlugin</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class MotifFinderPlugin extends CytoscapePlugin implements ActionListener {
   public final static String DESCRIPTION = "Find Motifs...";
   public final static String LOAD_SEQS = "Load sequences";
   public final static String FIND_NEW = "Find new motifs in selected sequences";
   public final static String LOCATE_OLD = "Identify instances of motifs in new sequences";

   protected CytoscapeDesktop cWindow;

   public MotifFinderPlugin() {
      this.cWindow = Cytoscape.getDesktop();
      MotifFinder mf = new MotifFinder( cWindow );
      JMenu menu = new JMenu( DESCRIPTION );
      cWindow.getCyMenus().getOperationsMenu().add( menu );
      JMenuItem item;
      item = new JMenuItem( FIND_NEW );
      item.addActionListener( mf );
      menu.add( item );
      item = new JMenuItem( LOCATE_OLD );
      item.addActionListener( mf );
      menu.add( item );
      item = new JMenuItem( "About motifFinder plugin" );
      item.addActionListener( this );
      menu.add( item );
   }

   public String describe() {
      return DESCRIPTION;
   }

   public void actionPerformed( ActionEvent e ) {
      String cmd = e.getActionCommand();
      if ( cmd.startsWith( "About" ) ) {
	 JOptionPane.showMessageDialog( cWindow, 
					new Object[] { "motifFinder plugin",
						       "by David Reiss, ISB",
						       "Questions or comments: dreiss@systemsbiology.org" }, 
					"About motifFinder plugin", 
					JOptionPane.INFORMATION_MESSAGE );
      } 
   }
}
