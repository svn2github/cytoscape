package csplugins.isb.dreiss.motifFinder;

import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import djr.util.MyUtils;
import djr.util.bio.Sequence;
import djr.util.array.ObjVector;
import djr.motif.sampler.Sampler;
import djr.motif.model.AlignmentMotifModel;
import djr.motif.gui.MotifDisplayPanel;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;
import csplugins.isb.dreiss.cytoTalk.CytoTalkSelectionListener;

/**
 * Class <code>MotifFinder</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class MotifFinder implements ActionListener, CytoTalkSelectionListener {
   static final boolean DEBUG = true;

   CytoscapeDesktop cWindow;
   ProgressMonitor monitor;
   Sampler sampler;
   Map seqsMap;
   String orfNames[];
   MotifDisplayPanel mdp = null;
   CytoTalkHandler handler;

   public MotifFinder( CytoscapeDesktop cWindow ) {
      this.cWindow = cWindow;
      this.handler = new CytoTalkHandler( null );
      MyUtils.SetNoExit( true );
   }

   public void actionPerformed( ActionEvent event ) {
      String cmd = event.getActionCommand();

      if ( MotifFinderPlugin.FIND_NEW.equals( cmd ) ) {
	 loadSequences();
	 sampler = runMotifFinder();

      } else if ( sampler != null && cmd.equals( "PROGRESS_CHANGE" ) ) {
	 if ( monitor == null ) {
	    monitor = new ProgressMonitor( cWindow,
					   new Object[] { "Searching for motifs in " + orfNames.length + " sequences..." },
					   null, 0, 100 );
	    monitor.setMillisToDecideToPopup( 0 );
	    monitor.setMillisToPopup( 0 );
	 }
	 if ( monitor.isCanceled() ) {
	    monitor = null;
	    sampler.actionPerformed( new ActionEvent( this, 0, "Cancel" ) );
	    sampler = null;
	 } else monitor.setProgress( event.getID() );      
      } else if ( sampler != null && cmd.equals( "Pause" ) ) {
	 sampler.actionPerformed( event );	 
      } else if ( sampler != null && cmd.equals( "PROGRESS_COMPLETE" ) ) {
	 monitor = null;
	 handler.addSelectionListener( this );
	 DisplayMotifSites();
      } else if ( "select sequence".equals( cmd ) ) {
         Sequence s = (Sequence) event.getSource();
	 if ( ! handler.isNodeSelected( orfNames[ event.getModifiers() ] ) )
	    handler.selectNode( orfNames[ event.getModifiers() ] );
      } else if ( "deselect sequence".equals( cmd ) ) {
         Sequence s = (Sequence) event.getSource();
	 if ( handler.isNodeSelected( orfNames[ event.getModifiers() ] ) ) 
	    handler.deselectNode( orfNames[ event.getModifiers() ] );
      } else if ( "select motif".equals( cmd ) ) {
         int mot = ( (Integer) event.getSource() ).intValue();
         System.err.println( "Selected motif #" + event.getModifiers() + " " + mot );
      } else if ( "display motif".equals( cmd ) ) {
         int mot = ( (Integer) event.getSource() ).intValue();
         System.err.println( "Displaying motif #" + event.getModifiers() + " " + mot );
      } else if ( "Select All".equals( cmd ) ) {
	 JButton but = (JButton) event.getSource();
	 but.setText( "Select None" ); but.repaint();
	 getDisplayForButton( but ).selectAllWithCurrentMot();
      } else if ( "Select None".equals( cmd ) ) {
	 JButton but = (JButton) event.getSource();
	 but.setText( "Select All" ); but.repaint();
	 getDisplayForButton( but ).selectAll( false );
      } else if ( "?".equals( cmd ) ) {
	 showInfo( "Help is coming one day...", "Motif Display Panel Help" );
      } else if ( "Close".equals( cmd ) ) {
	 handler.removeSelectionListener( this );
	 sampler = null;
	 cWindow = null;
	 mdp = null;
      } else if ( MotifFinderPlugin.LOCATE_OLD.equals( cmd ) ) {
	 showInfo( "Not implemented yet. Please check back later.", "Motif Locator" );
      }
   }

   protected void loadSequences() {
      String nodes[] = (String[]) handler.getSelectedNodes().toArray( new String[ 0 ] );
      if ( nodes == null || nodes.length <= 0 ) {
	 showInfo( "Please select more elements to search.", "Motif search error" );
	 return;
      }

      if ( seqsMap == null ) seqsMap = new HashMap();
      orfNames = new String[ nodes.length ];
      boolean needsSequence = false;
      for ( int i = 0; i < nodes.length; i ++ ) {
	 if ( nodes[ i ] == null ) continue;
	 orfNames[ i ] = nodes[ i ];
	 Vector seqs = handler.getNodeAttribute( orfNames[ i ], "sequence" );
	 String seq = seqs.size() > 0 ? (String) seqs.get( 0 ) : null;
	 if ( seq != null && seq.length() > 0 ) {
	    seqsMap.put( orfNames[ i ], new Sequence( seq, orfNames[ i ] ) );
	 } else {
	    needsSequence = true;
	    System.err.println( "Need sequence for " + orfNames[ i ] );
	 }	 
      }

      if ( DEBUG ) {
	 //System.err.println("VALID: "+seqsMap.keySet());
	 //MyUtils.Print(orfNames);
	 //System.err.println("HERE: "+seqsMap.size()+" "+orfNames.length);
      }

      if ( orfNames == null || orfNames.length <= 0 ) {
	 showInfo( "Please select more elements to search.", "Motif search error" );
	 return;
      }
   }

   protected void showInfo( String message, String title ) {
      JOptionPane.showMessageDialog( cWindow, 
				     new Object[] { message }, title, 
				     JOptionPane.INFORMATION_MESSAGE );
   }

   protected MotifDisplayPanel getDisplayForButton( JButton but ) {
      java.awt.Container c = but.getParent(), out = null;
      while( ! ( c instanceof djr.util.gui.MyJFrame ) ) c = c.getParent();
      if ( c != null ) out = ( (djr.util.gui.MyJFrame) c ).getComponent();
      if ( out != null && out instanceof MotifDisplayPanel ) 
	 return (MotifDisplayPanel) out;
      return null;
   }

   public boolean edgeSelected( String canonicalName, boolean sel ) { return false; }

   public boolean nodeSelected( String canonicalName, boolean sel ) {
      if ( mdp == null ) return false;
      for ( int i = 0; i < orfNames.length; i ++ ) {
	 String name = orfNames[ i ];
	 if ( name.equals( canonicalName ) ) mdp.setSelected( i, sel, true );
      }
      return true;
   }

   public Sampler runMotifFinder() {
      Sequence out[] = null;
      if ( orfNames != null ) {
	 int nValid = 0;
	 for ( int i = 0; i < orfNames.length; i ++ ) {
	    if ( orfNames[ i ] != null && seqsMap.get( orfNames[ i ] ) != null ) nValid ++;
	    else System.err.println( "Could not find sequence for " + orfNames[ i ] );
	 }
	 out = new Sequence[ nValid ]; 
	 String outNames[] = new String[ nValid ];
	 for ( int i = 0, ind = 0; i < orfNames.length; i ++ ) {
	    String name = orfNames[ i ];
	    Sequence seq = (Sequence) seqsMap.get( name );
	    if ( seq != null ) { out[ ind ] = seq; outNames[ ind ++ ] = name; }
	 }
	 orfNames = outNames;
      }

      if ( out.length <= 0 ) {
	 showInfo( "No sequences for selected nodes. Please run the Sequence plugin on these nodes.", 
		   "No sequences..." );
	 return null;
      }

      Properties props = csplugins.isb.dreiss.util.MyUtils.readProperties( "csplugins/isb/dreiss/motifFinder.properties" );
      String finderClass = (String) props.get( "plugin.motifFinder.class" );
      String finderArgs = (String) props.get( out[ 0 ].GetTypeName().equals( "protein" ) ? 
	      "plugin.motifFinder.args.protein" : "plugin.motifFinder.args.dna" );
      System.err.println("finderArgs = "+finderArgs);

      sampler = null;
      try {
	 Class samplerClass = Class.forName( finderClass );
	 java.lang.reflect.Constructor samplerConst = samplerClass.
	    getDeclaredConstructor( new Class[] { java.lang.String.class } );
	 sampler = (Sampler) samplerConst.newInstance( new Object[] { finderArgs } );
	 sampler.SetSequences( out );
      } catch( Exception e ) { 
	 DEBUG( e );
	 return null;
      }

      if ( ! sampler.IsInitialized() ) return null;

      sampler.AddActionListener( this );
      sampler.SetNoExit();

      sampler.Run();
      return sampler;
   }

   public void DisplayMotifSites() {
      final ObjVector states = sampler.GetBestMotifStates();
      final Sampler samp = sampler;
      final MotifFinder mf = this;
      ( new Thread() { public void run() {
	 mf.mdp = new MotifDisplayPanel( "Identified motifs", states, samp );
	 mf.mdp.putInFrame();
	 mf.mdp.addActionListener( mf );
	 mf.mdp.getFrame().removeButton( "Save" );
	 mf.mdp.getFrame().addButton( "Select All", mf );
	 mf.mdp.getFrame().addButton( "?", mf );
      } } ).start();
      handler.clearNodeSelection();
   }

   static final void DEBUG( Exception e ) {
      if ( DEBUG ) e.printStackTrace();
   }
}
