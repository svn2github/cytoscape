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
import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>SynonymPlugin</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class SynonymPlugin extends CytoscapePlugin {
   CytoscapeDesktop cWindow;
   SynonymClient synonymFetcher;
   CytoTalkHandler handler;

   public SynonymPlugin() throws Exception { 
      this.cWindow = Cytoscape.getDesktop();
      initialize();
   }

   public void initialize() throws Exception {
      JMenu menu = cWindow.getCyMenus().getOperationsMenu();
      this.handler = new CytoTalkHandler( null );
      //handler.splitAttributesOnSemis( false );
      JMenu menu2 = new JMenu( "Synonyms..." );
      menu.add( menu2 );
      menu2.add (new GetSynonyms());
      //menu2.add (new MergeNodesWithCommonSynonyms());
      menu2.add (new SelectNodesWithoutSyns());
      menu2.add( new JMenuItem( new SetUsernamePassword( cWindow.getMainFrame() ) ) );
      menu2.add( new JMenuItem( new SetHttpDataHost( cWindow.getMainFrame() ) ) );
      menu2.add (new AboutSynonymPlugin());
   }

   protected class AboutSynonymPlugin extends AbstractAction {
      AboutSynonymPlugin() { super( "About Synonyms Plugin..." ); }

      public void actionPerformed (ActionEvent e) {
         JOptionPane.showMessageDialog( cWindow, new Object[] { 
	    "Synonym plugin by David Reiss",
	    "Fetch protein/gene synonyms from a remote database.",
	    "Questions or comments: dreiss@systemsbiology.org" },
                                        "About Synonym plugin",
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   public class SelectNodesWithoutSyns extends AbstractAction {
      SelectNodesWithoutSyns() { super ("Highlight nodes without any known synonyms"); }
    
      public void actionPerformed (ActionEvent e) {
	 loadFetchers();
	 handler.selectNodesWithoutAttribute( "synonym" );
      }
   }

   protected void loadFetchers() {
      //if ( synonymFetcher == null ) 
      synonymFetcher = (SynonymClient) loadDataFetcher( "synonym" );
   }

   protected DataClient loadDataFetcher( String type ) {
      DataClient out = null;
      try {
	 System.err.println( "Loading data fetcher: " + type );
	 out = DataClientFactory.getClient( type );
      } catch (Exception eee) {
	 String msg = "<html> Failed to contact " + type + " fetcher<p>";
	 msg += "<p> exception message: " + eee.getMessage() + "</html>";
	 JOptionPane.showMessageDialog (cWindow.getMainFrame(), msg);
	 eee.printStackTrace();
      }
      return out;
   }

   public class GetSynonyms extends MonitoredAction {
      GetSynonyms() { 
	 super( cWindow.getNetworkPanel(), "Get all known synonyms for selected nodes",
		"Fetching synonyms for selected nodes..." ); 
      }
        
      public void actionPerformed( ActionEvent e ) {
	 loadFetchers();

	 ( new Thread() { public void run() {
	    Vector nodes = handler.getSelectedNodes();
	    Vector nodeNames = handler.getSelectedNodeCommonNames();
	    //try { synonymFetcher.setMultiCall( 10 ); } catch ( Exception ee ) { }
	    //Vector refseqs = new Vector();
	    for ( int i = 0; i < nodes.size(); i ++ ) {
	       String nodeName = (String) nodeNames.get( i );
	       String canonicalName = (String) nodes.get( i );
	       if ( nodeName.equals( "" ) || nodeName.startsWith( "node index" ) ) nodeName = canonicalName;
	       setProgress( i, nodes.size(), nodeName + " (" + canonicalName + ")" );
	       if ( done ) break; 
	       if ( handler.hasNodeAttribute( canonicalName, "synonym" ) ) continue;
	       String species = handler.getNodeSpecies( canonicalName );
	       Vector response = null;
	       try {
		  response = synonymFetcher.getSynonyms( canonicalName, species );
		  //refseqs.add( canonicalName );
	       } catch ( Exception ee ) {
		  String msg = "Exception while fetching synonyms in synonym fetcher " +
		     "for node " + canonicalName + " (" + species + "): " + ee.getMessage();
		  //JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
		  System.err.println( msg );
		  ee.printStackTrace();
		  response = null;
	       }

	       Vector v = handler.getNodeAttribute( canonicalName, "commonName" );
	       String commonName = v.size() > 0 ? (String) v.get( 0 ) : null;
	       /*if ( response == null || response.size() <= 0 ) {
		  if ( i >= nodes.size() - 1 ) {
		     try { response = synonymFetcher.endMultiCall(); }
		     catch ( Exception ee ) { response = null; }
		     try { synonymFetcher.setMultiCall( 10 ); } catch ( Exception ee ) { }
		  }
		  if ( response == null || response.size() <= 0 ) continue;
		  }*/
	       
	       //for ( int ii = 0, szz = response.size(); ii < szz; ii ++ ) {
	       Vector syns = response; //(Vector) ( (Vector) response.get( ii ) ).get( 0 );
	       //canonicalName = (String) refseqs.get( ii );
	       if ( syns != null && syns.size() > 0 ) {
		  //if ( ! canonicalName.equalsIgnoreCase( nodeName ) )
		  //handler.addNodeAttribute( canonicalName, "synonym", "canonical=" + canonicalName );
		  for ( int j = 0, sz = syns.size(); j < sz; j ++ ) {
		     String name = (String) syns.get( j );
		     // Prevent e.g. "Hs.732" from bringing back "Hs.7324"...
		     //if ( name.startsWith( canonicalName ) && ! name.equalsIgnoreCase( canonicalName ) ) break; // Not possible anymore
		     
		     if ( name.equalsIgnoreCase( canonicalName ) ||
			  name.equalsIgnoreCase( nodeName ) ||
			  name.equalsIgnoreCase( "Unknown" ) ||
			  name.equalsIgnoreCase( "None" ) ||
			  name.equalsIgnoreCase( "Null" ) ) continue;
		     handler.addNodeAttribute( canonicalName, "synonym", name );
		     if ( commonName == null || "".equals( commonName ) || 
			  commonName.equalsIgnoreCase( canonicalName ) || 
			  commonName.equalsIgnoreCase( nodeName ) ||
			  commonName.startsWith( "NP_" ) || commonName.startsWith( "XP_" ) || 
			  commonName.startsWith( "LOC" ) || commonName.startsWith( "GI" ) ) {
			if ( name.startsWith( "gene=" ) ) {
			   if ( name.indexOf( ";" ) > 0 ) name = name.substring( 0, name.indexOf( ";" ) );
			   commonName = name.substring( 5 );
			   handler.setNodeAttribute( canonicalName, "commonName", commonName );
			} else if ( name.startsWith( "short=" ) ) {
			   if ( name.indexOf( ";" ) > 0 ) name = name.substring( 0, name.indexOf( ";" ) );
			   commonName = name.substring( 6 );
			   handler.setNodeAttribute( canonicalName, "commonName", commonName );
			}
		     } 
		  }
	       }
	       //}
	       //refseqs.clear();
	    }
	    handler.redrawGraph();
	    setProgress( nodes.size() + 1, nodes.size(), "Done" );
	 } } ).start();
      }
   }

   protected static Vector getSynonymsStartingWith( String canonicalName, String prefix, CytoTalkHandler handler ) {
      Vector out = new Vector();
      if ( canonicalName.startsWith( prefix ) ) { out.add( canonicalName ); return out; }
      Vector syns = handler.getNodeAttribute( canonicalName, "synonym" );
      for ( int i = 0; i < syns.size(); i ++ ) {
	 if ( ! ( syns.get( i ) instanceof String ) ) continue;
	 String syn = ( (String) syns.get( i ) ).trim();
	 if ( syn.startsWith( prefix ) ) out.add( syn );
      }
      //if ( out.length() <= 0 ) out.add( canonicalName );
      return out;
   }

   //public static Vector getRefSeqsFromSynonyms( String canonicalName, CytoTalkHandler handler ) {
   //   return getRefSeqsFromSynonyms( canonicalName, handler, true );
   // }

   public static Vector getRefSeqsFromSynonyms( String canonicalName, CytoTalkHandler handler //,
						//boolean onlyFirstIfSemis
						) {
      Vector out = getSynonymsStartingWith( canonicalName, "ref=NP_", handler );
      if ( out == null || out.size() <= 0 ) {
	 out = getSynonymsStartingWith( canonicalName, "ref=XP_", handler );
	 if ( out == null || out.size() <= 0 ) 
	    out = getSynonymsStartingWith( canonicalName, "ref=NM_", handler );
      }
      Vector out2 = getSynonymsStartingWith( canonicalName, "NP_", handler );
      if ( out2 == null || out2.size() <= 0 ) 
	 out2 = getSynonymsStartingWith( canonicalName, "XP_", handler );
      if ( out2 == null || out2.size() <= 0 ) 
	 out2 = getSynonymsStartingWith( canonicalName, "NM_", handler );
      if ( ( out2 == null || out2.size() <= 0 ) && 
	   ( canonicalName.startsWith( "NP_" ) || canonicalName.startsWith( "XP_" ) ) )
	 out2.add( canonicalName );
      Vector out3 = getSynonymsStartingWith( canonicalName, "version=NP_", handler );
      if ( out3 == null || out3.size() <= 0 ) 
	 out3 = getSynonymsStartingWith( canonicalName, "version=XP_", handler );
      if ( out3 == null || out3.size() <= 0 ) 
	 out3 = getSynonymsStartingWith( canonicalName, "version=NM_", handler );
      Vector out4 = getSynonymsStartingWith( canonicalName, "nm=NM_", handler );

      for ( int i = 0, sz = out2.size(); i < sz; i ++ ) 
	 if ( ! out.contains( out2.get( i ) ) ) out.add( out2.get( i ) );
      for ( int i = 0, sz = out3.size(); i < sz; i ++ ) 
	 if ( ! out.contains( out3.get( i ) ) ) out.add( out3.get( i ) );
      for ( int i = 0, sz = out4.size(); i < sz; i ++ ) 
	 if ( ! out.contains( out4.get( i ) ) ) out.add( out4.get( i ) );
	 
      for ( int i = 0, sz = out.size(); i < sz; i ++ ) {
	 String s = (String) out.get( i );
	 if ( s.startsWith( "ref=" ) ) s = s.substring( 4 );
	 else if ( s.startsWith( "version=" ) ) s = s.substring( 8 );
	 else if ( s.startsWith( "nm=" ) ) s = s.substring( 3 );
	 if ( s.indexOf( '.' ) > 0 ) s = s.substring( 0, s.indexOf( '.' ) );
	 out.set( i, s );
      } 

      return out;
   }
}
