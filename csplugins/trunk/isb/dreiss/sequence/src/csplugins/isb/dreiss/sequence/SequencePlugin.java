package csplugins.isb.dreiss.sequence;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.util.MonitoredAction;
import csplugins.isb.dreiss.httpdata.client.*;
import csplugins.isb.dreiss.httpdata.*;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>SequencePlugin</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class SequencePlugin extends SynonymPlugin {
   SequenceClient sequenceFetcher;

   public SequencePlugin() throws Exception {
      super();
   }

   public void initialize() throws Exception {
      JMenu menu = cWindow.getCyMenus().getOperationsMenu();
      this.handler = new CytoTalkHandler( null );

      JMenu menu2 = new JMenu( "Sequences..." );
      menu.add( menu2 );
      menu2.add (new GetSequences("gene"));
      menu2.add (new GetSequences("protein"));
      menu2.add (new GetSequences("upstream"));
      menu2.add (new SelectNodesWithoutSequence());
      //menu2.add (new FetchNewSequences());
      menu2.add( new JMenuItem( new SetUsernamePassword( cWindow.getMainFrame() ) ) );
      menu2.add( new JMenuItem( new SetHttpDataHost( cWindow.getMainFrame() ) ) );
      menu2.add (new AboutSequencePlugin());
   }

   /*public void executePlugin( CytoscapeWindow cWind, String seqType ) {
      this.cWindow = cWind;
      this.nodeAttr = cWindow.getNodeAttributes();
      new GetSequences( seqType ).actionPerformed( new ActionEvent( this, 0, "" ) );
      waitTillDone();
   }
   */

   protected class AboutSequencePlugin extends AbstractAction {
      AboutSequencePlugin() { super( "About Sequences Plugin..." ); }

      public void actionPerformed (ActionEvent e) {
         JOptionPane.showMessageDialog( cWindow, new Object[] { 
	    "Sequences plugin by David Reiss",
	    "Fetch genome/protein sequences from a remote database.",
	    "Questions or comments: dreiss@systemsbiology.org" },
                                        "About Sequences plugin",
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   protected void loadFetchers() {
      if ( sequenceFetcher == null ) sequenceFetcher = (SequenceClient) loadDataFetcher( "sequence" );
   }

   public class GetSequences extends MonitoredAction {
      String type;
      GetSequences( String type ) { 
	 super (cWindow.getNetworkPanel(), "Get known " + type + " sequences for selected nodes",
		"Fetching sequences for selected nodes..." ); 
	 this.type = type;
      }
    
      public void actionPerformed (ActionEvent e) {
	 loadFetchers();

	 ( new Thread() { public void run() {
	    Vector nodes = handler.getSelectedNodes();
	    Vector nodeNames = handler.getSelectedNodeCommonNames();
	    for ( int i = 0; i < nodes.size(); i ++ ) {
	       String nodeName = (String) nodeNames.get( i );
	       String canonicalName = (String) nodes.get( i );
	       if ( nodeName.equals( "" ) || nodeName.startsWith( "node index" ) ) nodeName = canonicalName;
	       setProgress( i, nodes.size(), nodeName + " (" + canonicalName + ")" );
	       if ( done ) break; 
	       String species = handler.getNodeSpecies( canonicalName );
	       handler.deleteNodeAttribute( canonicalName, "sequence" );
	       Vector v = handler.getNodeAttribute( canonicalName, type + "_sequence" );
	       String seq = v.size() > 0 ? (String) v.get( 0 ) : null;
	       if ( seq == null || seq.length() <= 0 ) {
		  try {
		     Vector refseqs = getRefSeqsFromSynonyms( canonicalName, handler );
		     seq = sequenceFetcher.get( (String) refseqs.get( 0 ), species, type );
		     //System.err.println("HEREX: '"+refseq+"' '"+species+"' '"+type+"' '"+seq+"'");
		  } catch ( Exception ee ) {
		     String msg = "Exception while fetching " + type + " sequences in sequence fetcher " +
			"for node " + canonicalName + ": " + ee.getMessage();
		     //JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
		     System.err.println( msg );
		     ee.printStackTrace();
		     continue;
		  }
	       }
	       if ( seq != null && seq.length() > 0 ) {
		  if ( seq.equals( "UNKNOWN" ) ) seq = "";
		  handler.setNodeAttribute( canonicalName, type + "_sequence", seq );
		  handler.setNodeAttribute( canonicalName, "sequence", seq );
	       }

	       /*else {
		  try { System.err.println("HERE: "+canonicalName);
		  sequenceFetcher.put( canonicalName, species, "UNKNOWN" ); }
		  catch ( Exception ee ) { ee.printStackTrace(); }
		  }*/
	       
	    }
	    setProgress( nodes.size() + 1, nodes.size(), "Done" );
	 } } ).start();
      }
   }

   public class SelectNodesWithoutSequence extends AbstractAction {
      SelectNodesWithoutSequence() { super ("Highlight nodes without any cached sequence"); }
    
      public void actionPerformed (ActionEvent e) {
	 loadFetchers();
	 handler.selectNodesWithoutAttribute( "sequence" );
      }
   }

   public class FetchNewSequences extends MonitoredAction {
      FetchNewSequences() { 
	 super (cWindow.getNetworkPanel(), "Try to find new sequences for selected nodes",
		"Fetching new sequences from NCBI..."); }

      public void actionPerformed (ActionEvent e) {
	 loadFetchers();

	 ( new Thread() { public void run() {	 
	    Vector nodes = handler.getSelectedNodes();
	    Vector nodeNames = handler.getSelectedNodeCommonNames();
	    for ( int i = 0; i < nodes.size(); i ++ ) {
	       String nodeName = (String) nodeNames.get( i );
	       String canonicalName = (String) nodes.get( i );
	       if ( nodeName.equals( "" ) || nodeName.startsWith( "node index" ) ) nodeName = canonicalName;
	       setProgress( i, nodes.size(), nodeName + " (" + canonicalName + ")" );
	       if ( done ) break; 
	       if ( handler.hasNodeAttribute( canonicalName, "sequence" ) ) continue;
	       String species = handler.getNodeSpecies( canonicalName );
	       String sequence = null;
	       try {
		  sequence = getSequenceFromNCBI (canonicalName, species);
	       } catch (Exception e1) {
		  String msg = "NCBI exception fetching sequence for " + canonicalName + ": " + e1.getMessage();
		  //JOptionPane.showMessageDialog (cWindow.getMainFrame(), msg);
		  System.err.println( msg );
		  continue;
	       }
	       /*if (sequence == null) {
		  String msg = "<html>Could not find sequence for <color font='blue' size=+1>";
		  msg +=  canonicalName + "</font></html>";
		  JOptionPane.showMessageDialog (cWindow.getMainFrame(), msg);
		  continue;
	       }
	       System.err.println ("--- new sequence for");
	       System.err.println ("    canonicalName: " + canonicalName);
	       System.err.println ("    species:       " + species);
	       System.err.println ("    sequence:      " + sequence);
	       */
	       try {
		  if ( sequence != null && ! "".equals( sequence ) ) 
		     sequenceFetcher.put( canonicalName, species, sequence );
		  else if ( sequence == null || "UNKNOWN".equals( sequence ) )
		     sequenceFetcher.put( canonicalName, species, "UNKNOWN" );
	       } catch (Exception e2) {
		  String msg = "<html>Exception while saving sequence for " + canonicalName + " <p>";
		  msg += "to sequence fetcher: <p>";
		  msg +=  e2.getMessage() + "</html>";
		  JOptionPane.showMessageDialog (cWindow.getMainFrame(), msg);
		  continue;
	       }
	    }
	    setProgress( nodes.size() + 1, nodes.size(), "Done" );
	 } } ).start();
      }
   }

   private String getSequenceFromNCBI (String proteinCanonicalName, String species) throws Exception {
      return NCBIUtils.getSequence( proteinCanonicalName, species );
   }
}
