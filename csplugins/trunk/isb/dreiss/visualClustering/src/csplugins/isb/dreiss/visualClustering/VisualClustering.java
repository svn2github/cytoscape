package csplugins.isb.dreiss.visualClustering;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

public class VisualClustering implements ActionListener {
   public static final String MRNA_ATTRIBUTE = "mRNA co-regulation";
   public static final String HOMOLOGY_ATTRIBUTE = "sequence homology";

   protected CytoscapeDesktop cWindow;
   protected CytoTalkHandler handler;

   public VisualClustering() { }

   public VisualClustering( CytoscapeDesktop cWindow, CytoTalkHandler handler ) {
      init( cWindow, handler );
   }

   protected void init( CytoscapeDesktop cWindow, CytoTalkHandler handler ) {
      this.cWindow = cWindow;
      this.handler = handler;
   }

   public void doCallback( String attributes[], AttributeChooser chooser ) { };

   /*protected void performSave() { 
      clusterByNodes.performSave();
      clusterByCategory.performSave();
   }
   */

   public void actionPerformed( ActionEvent e ) {
      String cmd = e.getActionCommand();
      String[] attributeNames = (String[]) handler.getNodeAttributeNames().toArray( new String[ 0 ] );
      attributeNames = filterAttributeNames( attributeNames );
      JDialog dialog = new AttributeChooser( this, cWindow, handler, attributeNames );
      dialog.pack();
      dialog.setLocationRelativeTo (cWindow.getMainFrame());
      dialog.setVisible (true);
      cWindow.setInteractivity (true);
   }

   public String[] filterAttributeNames( String oldNames[] ) {
      Vector out = new Vector();
      //boolean hasSequence = false;
      for ( int i = 0; i < oldNames.length; i ++ ) {
	 String name = oldNames[ i ];
	 if ( name.equals( "canonicalName" ) || name.equals( "commonName" ) ||
	      name.equals( "species" ) ) continue;
	 if ( Cytoscape.getCurrentNetwork().getExpressionData() != null && 
	      ( name.endsWith( "sig" ) || name.endsWith( "exp" ) ) ) continue;
	 out.add( name );
	 //if ( name.indexOf( "sequence" ) >= 0 ) hasSequence = true;
      }
      if ( this instanceof VisualClusterByNodes ) {
	 if ( Cytoscape.getCurrentNetwork().getExpressionData() != null ) out.add( MRNA_ATTRIBUTE );
	 /*if ( hasSequence )*/ out.add( HOMOLOGY_ATTRIBUTE );
      }
      return (String[]) out.toArray( new String[ 0 ] );
   }
}
