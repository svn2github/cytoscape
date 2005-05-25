package csplugins.isb.dreiss.visualClustering;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>VisualClusteringPlugin</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class VisualClusteringPlugin extends CytoscapePlugin implements ActionListener {
   public final static String DESCRIPTION = "Visual Clustering";

   protected CytoscapeDesktop cWindow;
   protected VisualClusterByNodes clusterByNodes;
   protected VisualClusterByCategory clusterByCategory;
   protected CytoTalkHandler handler;

   public VisualClusteringPlugin() {
      this.cWindow = Cytoscape.getDesktop();
      init( cWindow );
   }
   
   protected void init( CytoscapeDesktop cWindow ) {
      this.cWindow = cWindow;
      this.handler = new CytoTalkHandler( null );
      JMenu menu = new JMenu( DESCRIPTION );
      cWindow.getCyMenus().getOperationsMenu().add( menu );
      this.clusterByNodes = new VisualClusterByNodes( cWindow, handler );
      JMenuItem item = new JMenuItem( "Create Edges Between Nodes by Category" );
      item.addActionListener( clusterByNodes );
      menu.add( item );
      this.clusterByCategory = new VisualClusterByCategory( cWindow, handler );
      item = new JMenuItem( "Layout Nodes by Category" );
      item.addActionListener( clusterByCategory );
      menu.add( item );
      //item = new JMenuItem( "Save correlation and/or homology values" );
      //item.addActionListener( this );
      //menu.add( item );
      item = new JMenuItem( "About " + DESCRIPTION + " plugin" );
      item.addActionListener( this );
      menu.add( item );
   }

   public String describe() {
      return DESCRIPTION;
   }
   
   public void actionPerformed( ActionEvent e ) {
      String cmd = e.getActionCommand();
      /*if ( cmd.startsWith( "Save" ) ) {
	 performSave();
	 } else {
      */
      if ( cmd.startsWith( "About" ) ) {
	 JOptionPane.showMessageDialog( cWindow.getNetworkPanel(), 
					new Object[] { DESCRIPTION + " plugin",
					"by David Reiss, ISB",
					"Questions or comments: dreiss@systemsbiology.org" }, 
					"About " + DESCRIPTION + " plugin", 
					JOptionPane.INFORMATION_MESSAGE );
	 
      }
   }
}

