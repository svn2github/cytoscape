// GetInteractions.java: expand the network by getting interactions from remote database(s)
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$

package csplugins.isb.dreiss.getInteractions;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.util.*;
import csplugins.isb.dreiss.httpdata.client.*;
import csplugins.isb.dreiss.sequence.*;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>GetInteractions</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class GetInteractions extends CytoscapePlugin {
   static protected Properties properties;

   protected CytoscapeDesktop cWindow;
   protected CytoTalkHandler handler;
   protected InteractionClient interactionFetcher;
   protected HomologClient homologFetcher;
   protected int addedEdges, addedNodes;
   protected Map arguments = new HashMap();
   protected Map sources, homologSpecies, nodeLookup, hSpeciesLookup;
   protected Vector nodesToBeSelected = new Vector(), edgesToBeSelected = new Vector();

   static {
      properties = MyUtils.readProperties( "csplugins/isb/dreiss/getInteractions.properties" );
   }

   public GetInteractions() {
      this.cWindow = Cytoscape.getDesktop();
      this.handler = new CytoTalkHandler( null );
      JMenu mainMenuEntry = new JMenu( "Fetch Interactions..." );
      mainMenuEntry.setToolTipText( "Get additional interactions from remote databases" );
      cWindow.getCyMenus().getOperationsMenu().add( mainMenuEntry );
      JMenuItem item2 = new JMenuItem( new GetInteractionsAction() );
      mainMenuEntry.add( item2 );
      mainMenuEntry.add( new JMenuItem( new SetUsernamePassword( cWindow.getMainFrame() ) ) );
      mainMenuEntry.add( new JMenuItem( new SetHttpDataHost( cWindow.getMainFrame() ) ) );
      mainMenuEntry.add( new JMenuItem( new AboutGetInteractions() ) );
   }

   protected InteractionClient getInteractionFetcher() {
      try {
	 interactionFetcher = (InteractionClient) DataClientFactory.getClient( "interaction" );
      } catch ( Exception e ) { e.printStackTrace(); interactionFetcher = null; }
      try { 
	 int level = interactionFetcher.getUserLevel();
	 sources = interactionFetcher.getSources( level ); 
      } catch ( Exception e ) { e.printStackTrace(); sources = null; }
      System.err.println("INTERACTION SOURCES: "+sources);
      return interactionFetcher;
   }

   protected HomologClient getHomologFetcher() {
      try {
	 homologFetcher = (HomologClient) DataClientFactory.getClient( "homolog" );
      } catch ( Exception e ) { e.printStackTrace(); homologFetcher = null; }
      try { homologSpecies = homologFetcher.getAvailableSpecies(); }
      catch ( Exception e ) { e.printStackTrace(); homologSpecies = null; }
      System.err.println("HOMOLOG SPECIES: "+homologSpecies);
      return homologFetcher;
   }

   public CytoTalkHandler getHandler() { return handler; }

   protected class GetInteractionsAction extends AbstractAction {
      GetInteractionsAction() { super( "Interactions..." ); }

      public void actionPerformed( ActionEvent e ) {
	 getInteractionFetcher();
	 getHomologFetcher();

	 JDialog dialog = new InteractionsDialog( GetInteractions.this,
						  GetInteractions.this.arguments, 
						  GetInteractions.this.sources,
						  GetInteractions.this.homologSpecies );
	 if ( dialog.isValid() ) {
	    dialog.pack();
	    dialog.setLocationRelativeTo( cWindow.getMainFrame() );
	    dialog.setVisible( true );
	 } else {
	    dialog.dispose();
	 }
      } 
   }

   /**
    *  query the graph to see if there is already an edge of this same type (ie.,
    *  that we added by using this plugin before) between node1 and node2.
    */
   protected String hasAddedEdge( String node1, String node2, //Node node1, Node node2, 
				  String source, String interactionType ) {
      String name = handler.getConnectedEdge( node1, node2 );
      if ( name.length() > 0 ) {
	 String attr = handler.hasEdgeAttribute( name, "getInteractionsPlugin" ) ?
	    (String) handler.getEdgeAttribute( name, "getInteractionsPlugin" ).get( 0 ) : "";
	 if ( attr.equals( "true" ) ) {
	    attr = handler.hasEdgeAttribute( name, "source" ) ?
	       (String) handler.getEdgeAttribute( name, "source" ).get( 0 ) : "";
	    if ( attr.equals( sources.get( source ) ) ) {
	       attr = handler.hasEdgeAttribute( name, "interaction" ) ?
		  (String) handler.getEdgeAttribute( name, "interaction" ).get( 0 ) : "";
	       if ( attr.equals( interactionType ) ) return name;
	    }
	 }
      }
      return null;
   }

   protected String hasEdge( String node1, String node2, String interactionType ) {
      String name = handler.getConnectedEdge( node1, node2 );
      if ( name.length() > 0 ) {
	 String attr = handler.hasEdgeAttribute( name, "interaction" ) ?
	    (String) handler.getEdgeAttribute( name, "interaction" ).get( 0 ) : "";
	 if ( attr.equals( interactionType ) ) return name;
      }
      return null;
   }

   protected void preloadNodeLookup( boolean useHlogs, boolean useSynonyms, 
				     boolean onlyBetweenSelected ) {
      Vector nodes = handler.getAllNodes();
      nodeLookup = new HashMap();
      hSpeciesLookup = new HashMap();

      //MonitoredAction mAction = new MonitoredAction( cWindow.getNetworkPanel(), "", "Gathering all necessary information...", 
      //					     nodes.size()+1 );

      for ( int i = 0; i < nodes.size(); i ++ ) {
	 String name = (String) nodes.get( i );
	 if ( onlyBetweenSelected && ! handler.isNodeSelected( name ) ) continue;

	 /*
	 if ( i % 10 == 0 ) {
	    String cname = handler.getCommonName( name );
	    String commonName = ( cname.equals( "" ) || cname.startsWith( "node index:" ) ) ? name : cname;
	    mAction.setProgress( i, nodes.size()+1, "Preprocessing node: " + commonName + " (" + name + ")" );
	    if ( mAction.done() ) break;
	    Thread.yield();
	 }
	 */

	 if ( name.startsWith( "NP_" ) || name.startsWith( "XP_" ) || name.startsWith( "NM_" ) ) nodeLookup.put( name, name );

	 if ( useSynonyms && handler.graphHasNodeAttribute( "synonym" ) ) {
	    Vector syns = SynonymPlugin.getRefSeqsFromSynonyms( name, handler );
	    //String syns[] = (String[]) handler.getNodeAttribute( name, "synonym" ).toArray( new String[ 0 ] );
	    if ( syns != null ) {
	       for ( int j = 0; j < syns.size(); j ++ ) {
		  if ( syns.get( j ) == null ) continue;
		  String syn = ( (String) syns.get( j ) ).trim();
		  if ( syn.equals( "Unknown" ) ) break;
		  if ( syn.startsWith( "ref=" ) ) {
		     syn = syn.substring( 4 );
		     if ( syn.indexOf( ';' ) >= 0 ) {
			String ss[] = syn.split( "\\;" );
			for ( int k = 0; k < ss.length; k ++ ) nodeLookup.put( ss[ k ], name );
		     } else nodeLookup.put( syn, name );
		  } else if ( syn.startsWith( "NP_" ) || syn.startsWith( "XP_" ) || 
			      syn.startsWith( "NM_" ) ) nodeLookup.put( syn, name );
	       }
	    }
	 }

	 if ( useHlogs && handler.graphHasNodeAttribute( "homolog" ) ) {
	    String hlogs[] = (String[]) handler.getNodeAttribute( name, "homolog" ).toArray( new String[ 0 ] );
	    String specs[] =(String[]) handler.getNodeAttribute( name, "homolog species" ).toArray( new String[ 0 ] );
	    if ( hlogs != null ) {
	       for ( int j = 0; j < hlogs.length; j ++ ) {
		  String hlog = hlogs[ j ];
		  if ( hlog.startsWith( "NP_" ) || hlog.startsWith( "XP_" ) || hlog.startsWith( "NM_" ) ) {
		     nodeLookup.put( hlog, name );
		     hSpeciesLookup.put( hlog, specs[ j ] );
		  }
	       }
	    }
	 }
      }
      //mAction.setProgress( nodes.size() + 2, nodes.size()+1, "Done" );
   }

   protected void findBindingPartners() {
      if ( handler.countAllNodes() <= 0 ) {
	 String msg = "The current graph is empty...";
	 JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
	 return;
      }

      if ( handler.countSelectedNodes() == 0 ) {
	 String msg = "Please select one or more nodes and try again.";
	 JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
	 return;
      }

      Vector list = handler.getSelectedNodes();
      Vector nodeList = new Vector();

      for ( int i = 0; i < list.size(); i ++ ) {
	 String canonicalName = (String) list.get( i );
	 String name = handler.getCommonName( canonicalName );
	 name = ( name.equals( "" ) || name.startsWith( "node index:" ) ) ? canonicalName : name;
	 nodeList.add( name );
      }

      final boolean addNewInteractors = getBooleanArgValue( "addNewNodes" );
      final String names[] = (String[]) list.toArray( new String[ 0 ] );
      final String nodeNames[] = (String[]) nodeList.toArray( new String[ 0 ] );

      final Vector homologsToUse = new Vector();
      final boolean useHlogs = getBooleanArgValue( "useHomologs" );
      if ( useHlogs ) {
	 for ( Iterator it = arguments.keySet().iterator(); it.hasNext(); ) {
	    String key = (String) it.next();
	    if ( ! key.startsWith( "HOMOLOG " ) ) continue;
	    boolean useit = getBooleanArgValue( key );
	    if ( ! useit ) continue;
	    String spec = key.substring( "HOMOLOG ".length() );
	    if ( ! spec.equals( "???" ) ) homologsToUse.add( spec );
	 }
      }

      nodesToBeSelected.clear();
      edgesToBeSelected.clear();

      ( new Thread() { public void run() {
	 boolean useSynonyms = getBooleanArgValue( "useSynonyms" );
	 boolean onlyBetweenSelected = getBooleanArgValue( "onlyBetweenSelected" );
	 preloadNodeLookup( useHlogs, useSynonyms, onlyBetweenSelected );

	 MonitoredAction mAction = new MonitoredAction( cWindow.getNetworkPanel(), "", "Fetching all connected interact" + 
							( addNewInteractors ? "ors" : "ions" ) +
							" from remote databases...", names.length+1 );

	 addedEdges = addedNodes = 0;
	 for ( int i = 0; i < names.length; i ++ ) {
	    try {	
	       String species = handler.getNodeSpecies( names[ i ] );
	       if ( species == null || "".equals( species ) ) continue;
	       mAction.setProgress( i+1, names.length+1, nodeNames[ i ] + " (" + 
				    names[ i ] + ") [" + species + "]" );
	       if ( mAction.done() ) break;
	       Thread.yield();
	       createEdgesForOneNode( names[ i ], addNewInteractors, homologsToUse );
	    } catch ( Exception ex ) {
	       //String msg = "exception in GetInteractions: " + ex.getMessage();
	       ex.printStackTrace();
	       //JOptionPane.showMessageDialog( cWindow.getMainFrame(), msg );
	    }
	 }
	 if ( addedEdges + addedNodes > 0 ) {
	    mAction.setProgress( names.length, names.length+1, 
				 "Added " + addedEdges + " edges and " + addedNodes + 
				 " nodes. Laying out the network..." );

	    boolean selectNew = getBooleanArgValue( "selectNew" );
	    if ( selectNew ) {
	       if ( nodesToBeSelected.size() > 0 ) {
		  handler.clearSelection();
		  handler.selectNodes( nodesToBeSelected );
	       }
	       if ( edgesToBeSelected.size() > 0 )
		  handler.selectEdges( edgesToBeSelected );
	    }
	    boolean doLayout = getBooleanArgValue( "relayout" );
	    if ( doLayout ) handler.doLayout();
	    handler.redrawGraph();
	 }
	 mAction.setProgress( names.length + 2, names.length+1, "Done" );
	 nodeLookup = hSpeciesLookup = null;

      } } ).start();
   }

   protected boolean loadingHomologsAlready = false; // prevent infinite recursion   

   protected void createEdgesForOneNode( String nodeCanonicalName, 
					 boolean addNewInteractors,
					 Vector homologsToUse ) throws Exception {

      String species = handler.getNodeSpecies( nodeCanonicalName );
      if ( species == null || "".equals( species ) ) return;

      HashMap neighborHash = getNeighborsFromDB( nodeCanonicalName, species );

      boolean internalOnly = getBooleanArgValue( "internalOnly" );
      boolean onlyBetweenSelected = getBooleanArgValue( "onlyBetweenSelected" );

      // if requested, get the homologs for this node and then get their interactions/ors
      if ( ! loadingHomologsAlready && homologsToUse != null && homologsToUse.size() > 0 ) {
	 loadingHomologsAlready = true;
	 HashMap hlogsHash = getNodeHomologsForSpecies( nodeCanonicalName );
	 if ( hlogsHash.size() > 0 ) {
	    for ( int i = 0; i < homologsToUse.size(); i ++ ) {
	       String spec = (String) homologsToUse.get( i );
	       Vector hlogs = (Vector) hlogsHash.get( spec );
	       if ( hlogs == null || hlogs.size() <= 0 ) continue;
	       for ( int j = 0; j < hlogs.size(); j ++ ) {
		  String hlog = (String) hlogs.get( j );
		  HashMap hlogNeighbors = getNeighborsFromDB( hlog, spec );
		  if ( hlogNeighbors.size() <= 0 ) continue;
		  MyUtils.JoinMaps( neighborHash, hlogNeighbors );
		  for ( Iterator it = hlogNeighbors.keySet().iterator(); it.hasNext(); ) {
		     Map m = (Map) hlogNeighbors.get( it.next() );
		     m.put( "homologInferred", "true" );
		     m.put( "homologSpecies", spec );
		  }
	       }
	    }
	 }
	 loadingHomologsAlready = false;
      }

      if ( neighborHash == null || neighborHash.size() <= 0 ) return;

      String neighborNames[] = (String[]) neighborHash.keySet().toArray( new String[0] );

      for ( int i = 0; i < neighborNames.length ; i ++ ) {
	 String neighbor = neighborNames[ i ];
	 if ( neighbor == null || "None".equalsIgnoreCase( neighbor ) || 
	      "null".equalsIgnoreCase( neighbor ) ) continue;

	 // See if the partner is in the selected network
	 String partner = neighbor;
	 if ( ! handler.doesNodeExist( partner ) ) partner = null;
	 if ( partner != null && onlyBetweenSelected && ! handler.isNodeSelected( partner ) ) partner = null;

	 // See if the partner (with the right synonym) is in the selected network
	 if ( partner == null ) {
	    partner = (String) nodeLookup.get( neighbor );
	    if ( partner != null && onlyBetweenSelected && ! handler.isNodeSelected( partner ) ) partner = null;
	 }

	 // Get the interaction data
	 Map neighborData = (Map) neighborHash.get( neighbor );
	 String source = (String) neighborData.get( "source" );

	 // Bug in hprd network -- too many self-interactions are not real.
	 if ( "hprd".equals( source ) && neighbor.equals( nodeCanonicalName ) ) continue;

	 String info = (String) neighborData.get( "info" );
	 String interactionType = (String) neighborData.get( "type" );
	 String pvalue = "" + neighborData.get( "pval" );
	 String homologInferred = (String) neighborData.get( "homologInferred" );
	 if ( homologInferred == null ) homologInferred = "false";

	 String spec = species;
	 if ( "true".equals( homologInferred ) ) 
	    spec = (String) neighborData.get( "homologSpecies" );

	 // No nodes found -- see if there's a homolog in the selected network, instead
	 if ( partner == null ) {
	    partner = findHomologNode( neighbor, spec );
	    if ( partner != null && onlyBetweenSelected && ! handler.isNodeSelected( partner ) ) partner = null;
	 }

	 // No nodes found -- add a new partner node if that's desired; set the attributes
	 boolean newNode = false;
	 if ( partner == null && addNewInteractors ) {
	    createNewPartnerNode( spec, neighbor, source );
	    addedNodes ++;
	    partner = neighbor;
	    newNode = true;
	    if ( newNode ) {
	       nodesToBeSelected.add( neighbor );
	       Map urls = getNeighborNodeURLs( source, neighbor, spec, info );
	       for ( Iterator it = urls.keySet().iterator(); it.hasNext(); ) {
		  String urlName = (String) it.next();
		  String url = (String) urls.get( urlName );
		  handler.addNodeAttribute( neighbor, urlName, url );
	       }
	    }
	    handler.setNodeAttribute( neighbor, "homologInferred", homologInferred );
	 }

	 // Add various attributes to the new edge and/or node
	 if ( partner != null ) {
	    //String inferredEdge = hasAddedEdge( candidateNode, partnerNode, source, interactionType );
	    String inferredEdge = hasEdge( nodeCanonicalName, partner/*neighbor*/, interactionType );
	    boolean isNewEdge = false;
	    if ( inferredEdge == null ) {
	       inferredEdge = handler.createEdge( nodeCanonicalName, interactionType, partner );
	       addedEdges ++;
	       isNewEdge = true;
	       edgesToBeSelected.add( inferredEdge );
	    }
	    String edgeName = inferredEdge;

	    if ( isNewEdge ) {
	       handler.setEdgeAttribute( edgeName, "getInteractionsPlugin", "true" );
	       handler.setEdgeAttribute( edgeName, "homologInferred", homologInferred );
	       if ( "true".equals( homologInferred ) ) 
		  handler.setEdgeAttribute( edgeName, "homologSpecies", (String) neighborData.get( "homologSpecies" ) );
	    }
	     
	    if ( ! "".equals( source ) && ! "None".equals( source ) )
	       handler.addEdgeAttribute( edgeName, "source", source );
	    if ( ! "".equals( pvalue ) && ! "None".equals( pvalue ) )
	       handler.addEdgeAttribute( edgeName, "pvalue", pvalue );
	    if ( ! "".equals( info ) && ! "None".equals( info ) )
	       handler.addEdgeAttribute( edgeName, "info", info );

	    // Add any urls that are in the info string to the edge.
	    Map urls = getInteractionURLs( source, nodeCanonicalName, species, info );
	    for ( Iterator it = urls.keySet().iterator(); it.hasNext(); ) {
	       String urlName = (String) it.next();
	       String url = (String) urls.get( urlName );
	       handler.addEdgeAttribute( edgeName, urlName, url );
	    }

	    // Parse the info string and add the different attributes to the node.
	    // if there's an attribute that ends with "1" then that means the attribute
	    // applies to this node; if "2" then it applies to any new node that was added.
	    // Otherwise, put it on the edge.
	    //if ( isNewEdge ) {
	    if ( info.indexOf( '|' ) >= 0 ) {
	       String toks[] = info.split( "\\|" );
	       for ( int j = 0; j < toks.length; j ++ ) {
		  if ( toks[ j ].indexOf( '=' ) > 0 ) {
		     String ttoks[] = toks[ j ].split( "\\=" );
		     if ( ttoks.length > 1 ) {
			if ( newNode && isNewEdge && ttoks[ 0 ].endsWith( "2" ) ) {
			   String ttok = ttoks[ 0 ].substring( 0, ttoks[ 0 ].length() - 1 );
			   handler.setNodeAttribute( neighbor, ttok, ttoks[ 1 ] );
			} else if ( newNode && isNewEdge && ttoks[ 0 ].endsWith( "1" ) ) {
			   String ttok = ttoks[ 0 ].substring( 0, ttoks[ 0 ].length() - 1 );
			   if ( ! handler.hasNodeAttribute( nodeCanonicalName, ttok ) )
			      handler.setNodeAttribute( nodeCanonicalName, ttok, ttoks[ 1 ] );
			} else if ( ! ttoks[ 0 ].endsWith( "1" ) &&
				    ! ttoks[ 0 ].endsWith( "2" ) &&
				    ! handler.hasEdgeAttribute( edgeName, ttoks[ 0 ] ) ) {
			   if ( ! "canonicalName".equalsIgnoreCase( ttoks[ 0 ] ) &&
				! "commonName".equalsIgnoreCase( ttoks[ 0 ] ) )
			      handler.setEdgeAttribute( edgeName, ttoks[ 0 ], ttoks[ 1 ] );
			   if ( newNode && isNewEdge && "commonName".equalsIgnoreCase( ttoks[ 0 ] ) )
			      handler.setNodeAttribute( neighbor, "commonName", ttoks[ 1 ] );
			}			
		     }
		  }
	       }
	    }
	    //}
	 }

	 if ( newNode && ! handler.hasNodeAttribute( neighbor, "commonName" ) )
	    handler.setNodeAttribute( neighbor, "commonName", neighbor );
      }
   }

   protected HashMap getNodeHomologsForSpecies( String nodeCanonicalName ) {
      String hlogs[] = (String[]) handler.getNodeAttribute( nodeCanonicalName, "homolog" ).toArray( new String[ 0 ] );
      String specs[] = (String[]) handler.getNodeAttribute( nodeCanonicalName, "homolog species" ).toArray( new String[ 0 ] );
      HashMap out = new HashMap();
      for ( int i = 0; i < hlogs.length; i ++ ) {
	 String spec = specs[ i ].trim();
	 Vector v = (Vector) out.get( spec );
	 if ( v == null ) { v = new Vector(); out.put( spec, v ); }
	 v.add( hlogs[ i ].trim() );
      }
      return out;
   }

   protected String createNewPartnerNode( String species, String partnerName,
					String source ) {
      handler.createNode( partnerName );
      if ( partnerName.startsWith( "NP_" ) || partnerName.startsWith( "XP_" ) || partnerName.startsWith( "NM_" ) ) 
	 nodeLookup.put( partnerName, partnerName );
      handler.setNodeAttribute( partnerName, "source", source );
      handler.setNodeAttribute( partnerName, "species", species );
      handler.setNodeAttribute( partnerName, "getInteractionsPlugin", "true" );
      return partnerName;
   }

   protected String findHomologNode( String homolog, String hspec ) {
      String name = (String) nodeLookup.get( homolog );
      if ( name == null || "".equals( name ) ) return null;
      if ( ! ( (String) hSpeciesLookup.get( homolog ) ).equals( hspec ) ) return null;
      return name;
   }

   protected Map getNeighborNodeURLs( String source, String neighborName, String species, String infoString ) {
      Map map = new HashMap();


      if ( neighborName.startsWith( "NP_" ) || neighborName.startsWith( "XP_" ) || neighborName.startsWith( "NM_" ) ) {
	 String refseqURL = (String) properties.get( "refseq.url" ); 
	 refseqURL = refseqURL.replaceAll( "\\$\\{1\\}", neighborName );
	 map.put( "RefSeq URL", refseqURL );
      }

      if ( source.startsWith( "hprd" ) ) {
	 String hprdid = getInfoArg( "HPRDID2", infoString );
	 String hprdURL = (String) properties.get( "hprd.url" );
	 hprdURL = hprdURL.replaceAll( "\\$\\{1\\}", hprdid );
	 if ( ! "".equals( hprdid ) ) map.put( "HPRD URL", hprdURL );
      }

      if ( source.startsWith( "dip" ) ) {
	 String dipid = getInfoArg( "dipID2", infoString );
	 dipid = dipid.replaceAll( "N", "" );
	 String url = (String) properties.get( "dip.node.url" );
	 url = url.replaceAll( "\\$\\{1\\}", dipid );
	 if ( ! "".equals( dipid ) ) map.put( "DIP URL", url );
      }

      return map;
   }

   protected boolean inHere = false; // prevent infinite recursion

   protected HashMap getNeighborsFromDB( String nodeCanonicalName, String species ) {
      HashMap out = new HashMap();
      InteractionClient fetcher = interactionFetcher;
      if ( fetcher == null ) return out;

      boolean useSynonyms = getBooleanArgValue( "useSynonyms" );
      // NOTE: REQUIRES REFSEQ (NP_ OR XP_ OR NM_ NUMBERS!)
      String refseq = nodeCanonicalName;
      String refs[] = new String[] { refseq };
      if ( refseq.indexOf( ';' ) > 0 ) refs = refseq.split( "\\;" );

      if ( useSynonyms && ! inHere ) {
	 Vector v = SynonymPlugin.getRefSeqsFromSynonyms( nodeCanonicalName, handler );
	 refs = (String[]) v.toArray( new String[0] );
      }

      //try { fetcher.setMultiCall( 100 ); } catch ( Exception e ) { };
      
      int fromPrebind = 0;
      for ( int r = 0; r < refs.length; r ++ ) {
	 refseq = refs[ r ];
      
	 Vector response = null;
	 for ( Iterator it = sources.keySet().iterator(); it.hasNext(); ) {
	    String source = (String) it.next();
	    boolean getit = getBooleanArgValue( source );
	    if ( ! getit ) continue;
	    try { response = fetcher.getAllInteractionsAndInfo( refseq, species, source, true ); }
	    catch ( Exception e ) { };
	    //}

	    //Vector response = null;
	    //try { response = fetcher.endMultiCall(); } catch ( Exception e ) { e.printStackTrace(); response = null; }

	    boolean okay = false;
	    //for ( int j = 0, ssz = response.size(); j < ssz; j ++ ) {
	    Vector intInfo = response; //(Vector) ( (Vector) response.get( j ) ).get( 0 );
	    if ( intInfo != null && intInfo.size() > 0 ) {
	       for ( int i = 0, sz = intInfo.size(); i < sz; i ++ ) {
		  Hashtable map = (Hashtable) intInfo.get( i );
		  String partner = (String) map.get( "partner" );
		  String src = (String) map.get( "source" );
		  if ( src.startsWith( "prebind" ) ) fromPrebind ++;
		  if ( arguments.containsKey( src ) && getBooleanArgValue( src ) ) {
		     out.put( partner, map );
		     okay = true;
		  }
	       }
	    }
	 }
	 //}
	 //if ( okay ) break;
      }

      // try to fetch prebind interactions from prebind website
      if ( ! inHere && fromPrebind == 0 && 
	   ( getBooleanArgValue( "prebindProbably" ) ||
	     getBooleanArgValue( "prebindPossibly" ) ||
	     getBooleanArgValue( "prebindYes" ) ) ) {
	 for ( int r = 0; r < refs.length; r ++ ) {
	    refseq = refs[ r ];
	    inHere = true;
	    Map pout = getPrebindNeighbors( refseq, species );
	    inHere = false;
	    if ( pout != null && pout.size() > 0 ) {
	       MyUtils.JoinMaps( out, pout );
	       break;
	    }
	 }
      }
      return out;
   }

   protected boolean getBooleanArgValue( String param ) {
      Boolean val = (Boolean) arguments.get( param );
      return val != null && val.booleanValue();
   }

   public Map getInteractionURLs( String source, String nodeName, 
				  String species, String infoString ) throws Exception {
      Map out = new HashMap();

      // Add a prebind url if that's the source, using the webAnchor number from
      // the info string
      if ( source.startsWith( "prebind" ) ) {
	 try {
	    String url = PreBind.constructURL( nodeName, species );
	    String anchor = getInfoArg( "webAnchor", infoString );
	    if ( ! "".equals( anchor ) ) url += "#anchor" + anchor;
	    out.put( "PreBIND URL", url );
	 } catch ( Exception e ) { };

	 // Add a bind url if that's the source, using the bind id in the info string
      } else if ( "bind".equals( source ) ) {
	 String bid = getInfoArg( "BindID", infoString );
	 String bindURL = (String) properties.get( "bind.url" );
	 bindURL = bindURL.replaceAll( "\\$\\{1\\}", bid );
	 if ( ! "".equals( bid ) ) out.put( "BIND URL", bindURL );

	 // Add a hprd url from the HPRD id, if that's the source
      } else if ( source.startsWith( "hprd" ) ) {
	 String hprdid = getInfoArg( "HPRDID1", infoString );
	 String hprdURL = (String) properties.get( "hprd.url" );
	 hprdURL = hprdURL.replaceAll( "\\$\\{1\\}", hprdid );
	 if ( ! "".equals( hprdid ) ) out.put( "HPRD URL", hprdURL );
      }

      // Add a pubmed url if there's a PubMedID string in the info
      if ( infoString.indexOf( "PubMedID=" ) >= 0 ) {
	 String pmid = getInfoArg( "PubMedID", infoString );
	 String url = (String) properties.get( "pubmed.url" );
	 url = url.replaceAll( "\\$\\{1\\}", pmid );
	 if ( ! "".equals( pmid ) ) out.put( "PubMed URL", url );
      }

      // Add a DIP url if there's a DIPID string in the info
      if ( infoString.indexOf( "dipID=" ) >= 0 ) {
	 String dipid = getInfoArg( "dipID", infoString );
	 dipid = dipid.replaceAll( "E", "" );
	 String url = (String) properties.get( "dip.url" );
	 url = url.replaceAll( "\\$\\{1\\}", dipid );
	 if ( ! "".equals( dipid ) ) out.put( "DIP URL", url );
      }

      // Add any other urls that are in the info string (e.g. if the info string contains
      // "mycrapURL=http://crap" then add "http://crap" with the key being "mycrapURL").
      String toks[] = infoString.split( "\\|" );
      for ( int j = 0; j < toks.length; j ++ )
	 if ( toks[ j ].indexOf( "URL" ) >= 0 )
	    out.put( toks[ j ], toks[ j ].substring( toks[ j ].indexOf( '=' ) ) );

      return out;
   }

   protected String getInfoArg( String param, String infoString ) {
      String toks[] = infoString.split( "\\|" );
      for ( int j = 0; j < toks.length; j ++ )
	 if ( toks[ j ].startsWith( param + "=" ) )
	    return toks[ j ].substring( ( param + "=" ).length() );
      return "";
   }

   protected Map getPrebindNeighbors( String refseq, String species ) {
      // try to fetch prebind info from prebind website
      String ref = refseq.indexOf( ';' ) >= 0 ? refseq.substring( 0, refseq.indexOf( ';' ) - 1 ) : refseq;
      Map pout = new HashMap();
      try {
	 PreBind finder = new PreBind( ref, species );
	 pout = finder.getNPs();
      } catch ( Exception e ) { e.printStackTrace(); }
      if ( pout.size() <= 0 ) {
	 Vector v = new Vector();
	 v.add( new Integer( -1 ) );
	 v.add( "None" );
	 v.add( "None" );
	 pout.put( "None", v );
      } 
      try {
	 savePrebindNeighborsToDB( refseq, species, pout );
	 pout = getNeighborsFromDB( refseq, species );
      } catch ( Exception e ) { 
	 pout = null;
	 System.err.println( e.toString() );/*e.printStackTrace();*/ 
      }
      return pout;
   }

   protected void savePrebindNeighborsToDB( String nodeCanonicalName, String species, Map neighborHash ) {
      InteractionClient fetcher = interactionFetcher;
      if ( fetcher == null ) return;
      String neighborNames[] = (String[]) neighborHash.keySet().toArray( new String[0] );

      int probable = 0, possible = 0, yes = 0;
      for ( int i = 0; i < neighborNames.length; i ++ ) {
	 String neighbor = neighborNames[ i ];
	 Vector neighborMetadata = (Vector) neighborHash.get( neighbor );
	 int webPageAnchorNumber = ( (Integer) neighborMetadata.get( 0 ) ).intValue();
	 String likelihoodEstimate = (String) neighborMetadata.get( 1 );
	 String commonName = (String) neighborMetadata.get( 2 );
	 String info = "webAnchor=" + webPageAnchorNumber + "|commonName2=" + commonName;
	 if ( likelihoodEstimate.equals( "Probably" ) ) probable ++;
	 else if ( likelihoodEstimate.equals( "Possibly" ) ) possible ++;
	 else if ( likelihoodEstimate.equals( "Yes" ) ) yes ++;
	 try {
	    fetcher.put( nodeCanonicalName, "pp", neighbor, 0.0, "prebind" + likelihoodEstimate, info, species );
	 } catch ( Exception e ) { e.printStackTrace(); }
      }

      if ( yes == 0 ) 
	 try {
	    fetcher.put( nodeCanonicalName, "pp", "None", 0.0, "prebindYes", "None", species );
	 } catch ( Exception e ) { e.printStackTrace(); }
      if ( probable == 0 ) 
	 try {
	    fetcher.put( nodeCanonicalName, "pp", "None", 0.0, "prebindProbably", "None", species );
	 } catch ( Exception e ) { e.printStackTrace(); }
      if ( possible == 0 ) 
	 try {
	    fetcher.put( nodeCanonicalName, "pp", "None", 0.0, "prebindPossibly", "None", species );
	 } catch ( Exception e ) { e.printStackTrace(); }      
   }

   protected class AboutGetInteractions extends AbstractAction {
      AboutGetInteractions() { super( "About Interactions Plugin..." ); }
      public void actionPerformed( ActionEvent e ) {
	 JOptionPane.showMessageDialog( cWindow, new Object[] { 
	    "Interactions plugin, by David Reiss, ISB",
	    "Questions or comments: dreiss@systemsbiology.org.",
	    "" }, 
                                        "About Interactions plugin", 
                                        JOptionPane.INFORMATION_MESSAGE );
      } }
} 
