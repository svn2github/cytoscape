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
 * Class <code>HomologPlugin</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class HomologPlugin extends SynonymPlugin {
   HomologClient homologFetcher;

   public HomologPlugin() throws Exception {
      super();
   }

   public void initialize() throws Exception {
      JMenu menu = cWindow.getCyMenus().getOperationsMenu();
      this.handler = new CytoTalkHandler( null );

      JMenu menu2 = new JMenu( "Homologs..." );
      menu.add( menu2 );
      menu2.add (new GetHomologs());
      menu2.add (new SelectNodesWithoutHoms());
      menu2.add( new JMenuItem( new SetUsernamePassword( cWindow.getMainFrame() ) ) );
      menu2.add( new JMenuItem( new SetHttpDataHost( cWindow.getMainFrame() ) ) );
      menu2.add (new AboutHomologPlugin());
   }

   /*public void executePlugin( CytoscapeWindow cWind ) {
     this.cWindow = cWind;
     this.nodeAttr = cWindow.getNodeAttributes();
     new GetHomologs().actionPerformed( new ActionEvent( this, 0, "" ) );
     waitTillDone();
     }
   */

   protected class AboutHomologPlugin extends AbstractAction {
      AboutHomologPlugin() { super( "About Homologs Plugin..." ); }
      public void actionPerformed (ActionEvent e) {
         JOptionPane.showMessageDialog( cWindow, new Object[] { 
	    "Homolog plugin by David Reiss",
	    "Fetch protein/gene homologs from a remote database.",
	    "Questions or comments: dreiss@systemsbiology.org" },
                                        "About Homolog plugin",
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   public class SelectNodesWithoutHoms extends AbstractAction {
      SelectNodesWithoutHoms() { super ("Highlight nodes without any known homologs"); }
    
      public void actionPerformed (ActionEvent e) {
	 loadFetchers();
	 handler.selectNodesWithoutAttribute( "homolog" );
      }
   }

   protected void loadFetchers() {
      if ( homologFetcher == null ) homologFetcher = (HomologClient) loadDataFetcher( "homolog" );
   }

   public class GetHomologs extends MonitoredAction {
      GetHomologs() { super( cWindow.getNetworkPanel(), "Get all known homologs for selected nodes",
			     "Fetching homologs for selected nodes..." ); }
        
      public void actionPerformed( ActionEvent e ) {
	 loadFetchers();

	 ( new Thread() { public void run() {
	    Vector nodes = handler.getSelectedNodes();
	    Vector nodeNames = handler.getSelectedNodeCommonNames();
	    //try { homologFetcher.setMultiCall( 10 ); } catch ( Exception ee ) { ; }
	    //Vector refseqs = new Vector();
	    for ( int i = 0; i < nodes.size(); i ++ ) {
	       String nodeName = (String) nodeNames.get( i );
	       String canonicalName = (String) nodes.get( i );
	       if ( nodeName.equals( "" ) || nodeName.startsWith( "node index" ) ) nodeName = canonicalName;
	       setProgress( i, nodes.size(), nodeName + " (" + canonicalName + ")" );
	       if ( done ) break; 
	       if ( handler.hasNodeAttribute( canonicalName, "homolog" ) ) continue;
	       String species = handler.getNodeSpecies( canonicalName );
	       Vector response = null;
	       Vector rseqs = getRefSeqsFromSynonyms( canonicalName, handler );
	       for ( int qq = 0, qsz = rseqs.size(); qq < qsz; qq ++ ) {
		  try {
		     response = homologFetcher.getAllHomologsAndInfo( (String) rseqs.get( qq ), species );
		     //refseqs.add( canonicalName );
		  } catch ( Exception ee ) {
		     String msg = "Exception while fetching homologs in homolog fetcher " +
			"for node " + canonicalName + ": " + ee.getMessage();
		     //JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
		     System.err.println( msg );
		     ee.printStackTrace();
		     response = null;
		  }
	       
		  /*if ( response == null || response.size() <= 0 ) {
		     if ( i >= nodes.size() - 1 ) {
			try { response = homologFetcher.endMultiCall(); }
			catch ( Exception ee ) { response = null; }
			try { synonymFetcher.setMultiCall( 10 ); } catch ( Exception ee ) { ; }
		     }
		     if ( response == null || response.size() <= 0 ) continue;
		     }*/
	       
		  //for ( int ii = 0, szz = response.size(); ii < szz; ii ++ ) {
		  Vector homs = response;
		  //try {
		  //homs = (Vector) ( (Vector) response.get( ii ) ).get( 0 );
		  //} catch ( Exception ee ) {
		  //homs = new Vector(); homs.add( response.get( ii ) );
		  //} // end of try-catch
		  //canonicalName = (String) refseqs.get( ii );
		  if ( homs != null && homs.size() > 0 ) {
		     for ( int j = 0, sz = homs.size(); j < sz; j ++ ) {
			Vector alreadyHas = handler.getNodeAttribute( canonicalName, "homolog" );
			Map map = (Map) homs.get( j );
			String homolog = (String) map.get( "homolog" );
			if ( alreadyHas != null && alreadyHas.contains( homolog ) ) continue;
			String info = (String) map.get( "info" );
			String cname2 = getInfoArg( "commonName2", info );
			handler.addNodeAttribute( canonicalName, "homolog", homolog );
			handler.addNodeAttribute( canonicalName, "homolog species", (String) map.get( "species" ) );
			handler.addNodeAttribute( canonicalName, "homolog score", map.get( "score" ) + "%" );
			if ( cname2 != null && ! "".equals( cname2 ) && ! "--".equals( cname2 ) ) 
			   handler.addNodeAttribute( canonicalName, "homolog common name", cname2 );
		     }
		  }
	       }
	       //refseqs.clear();
	       //}
	    }
	    setProgress( nodes.size() + 1, nodes.size(), "Done" );
	 } } ).start();
      }
   }

   protected String getInfoArg( String param, String infoString ) {
      String toks[] = infoString.split( "\\|" );
      for ( int j = 0; j < toks.length; j ++ )
	 if ( toks[ j ].startsWith( param + "=" ) )
	    return toks[ j ].substring( ( param + "=" ).length() );
      return "";
   }
}
