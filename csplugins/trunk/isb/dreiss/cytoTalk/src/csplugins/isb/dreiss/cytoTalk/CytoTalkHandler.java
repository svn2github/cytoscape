package csplugins.isb.dreiss.cytoTalk;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.URL;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;
//import csplugins.trial.pshannon.dataCube.*;

/**
 * Class <code>CytoTalkHandler</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 2.0
 */
public class CytoTalkHandler implements FlagEventListener {
   static boolean SPLIT_ON_SEMIS = true;

   MyXmlRpcServer server;
   
   //Map dataMatrices = null;
   Vector selectionListeners = null;
   boolean added = false;
   Set selection = null;

   public CytoTalkHandler( MyXmlRpcServer server ) {
      this.server = server;
   }

   public int isRunning() { return 1; }

   /**
    * Method <code>exposeNodeAttributes</code> 
    * Expose the node attributes object to the xmlrpc client, service "nodeAttr"
    * (dangerous?)
    *
    * @return a <code>boolean</code> value
    */
   public boolean exposeNodeAttributes() {
      if ( server == null ) return false;
      try {
	 server.addService( "nodeAttr", Cytoscape.getNodeNetworkData() );
      } catch( Exception ee ) {
	 System.err.println( "Could not start service \"nodeAttr\": " + ee.getMessage() );
	 return false;
      }
      return true;
   }

   /**
    * Method <code>exposeEdgeAttributes</code> 
    * Expose the edge attributes object to the xmlrpc client, service "edgeAttr"
    * (dangerous?)
    *
    * @return a <code>boolean</code> value
    */
   public boolean exposeEdgeAttributes() {
      if ( server == null ) return false;
      try {
	 server.addService( "edgeAttr", Cytoscape.getEdgeNetworkData() );
      } catch( Exception ee ) {
	 System.err.println( "Could not start service \"edgeAttr\": " + ee.getMessage() );
	 return false;
      }
      return true;
   }

   /**
    * Method <code>exposeCytoscapeDesktop</code> 
    * Expose the CytoscapeWindow object to the xmlrpc client, service "cyWindow" 
    * (dangerous?)
    *
    * @return a <code>boolean</code> value
    */
   public boolean exposeCytoscapeDesktop() {
      if ( server == null ) return false;
      try {
	 server.addService( "desktop", Cytoscape.getDesktop() );
      } catch( Exception ee ) {
	 System.err.println( "Could not start service \"desktop\": " + ee.getMessage() );
	 return false;
      }
      return true;
   }

   /**
    * Method <code>exposeAll</code> 
    * Expose the CytoscapeDesktop object, and the edge and node attributes and the
    * expressiondata object to the xmlrpc client (dangerous?)
    *
    * @return a <code>boolean</code> value
    */
   public boolean exposeAll() {
      if ( server == null ) return false;
      exposeNodeAttributes();
      exposeEdgeAttributes();
      exposeCytoscapeDesktop();

      try {
	 server.addService( "cyData", Cytoscape.getExpressionData() );
      } catch( Exception ee ) {
	 System.err.println( "Could not start service \"cyData\": " + ee.getMessage() );
	 return false;
      }

      return true;
   }

   /**
    * Method <code>setTitle</code> 
    * Set the current CyNetworkView title
    *
    * @param title a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean setTitle( String title ) {
      Cytoscape.getCurrentNetworkView().setTitle( title );
      return true;
   }

   /**
    * Method <code>getSelectedNodes</code> 
    * Get the canonical names of all selected nodes
    *
    * @return a <code>Vector</code> value
    */
   public Vector getSelectedNodes() {
      Set nodes = Cytoscape.getCurrentNetwork().getFlaggedNodes();
      Vector out = new Vector( nodes.size() );
      Iterator i = nodes.iterator();
      while ( i.hasNext() ) //out.add( ( ( CyNode )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getNodeAttributeValue( (CyNode) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>getSelectedNodeCommonNames</code> 
    * Get names of selected nodes (including common names where avail).
    * Right now just returns canonical names (same as getSelectedNodes()).
    *
    * @return a <code>Vector</code> value
    */
   public Vector getSelectedNodeCommonNames() {
      Set nodes = Cytoscape.getCurrentNetwork().getFlaggedNodes();
      Vector out = new Vector( nodes.size() );
      Iterator i = nodes.iterator();
      while ( i.hasNext() ) out.add( getCommonName( ( ( CyNode ) i.next() ).getIdentifier() ) );
      return out;
   }

   /**
    * Method <code>getCommonName</code> 
    * Get the common name for the given canonical name
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>String</code> value
    */
   public String getCommonName( String canonicalName ) {
      // TODO: replace with better naming service
      //return canonicalName;
      Vector v = getNodeAttribute( canonicalName, "commonName" );
      String out = v.size() > 0 ? getNodeAttribute( canonicalName, "commonName" ).get( 0 ).toString() : "";
      return out;
   }

   /**
    * Method <code>countSelectedNodes</code> 
    * Get the number of selected nodes
    *
    * @return an <code>int</code> value
    */
   public int countSelectedNodes() { 
      return Cytoscape.getCurrentNetwork().getFlaggedNodes().size();
   }

   /**
    * Method <code>storeSelection</code> 
    * Store the current selection in memory
    *
    */
   public boolean storeSelection() {
      this.selection = Cytoscape.getCurrentNetwork().getFlaggedNodes(); 
      return true;
   }

   /**
    * Method <code>doesNodeExist</code> 
    * Does the given named node exist in the graph?
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean doesNodeExist( String canonicalName ) {
      return Cytoscape.getCyNode( canonicalName, false ) != null;
   }

   /**
    * Method <code>isNodeSelected</code> 
    * Is the given named node selected?
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean isNodeSelected( String canonicalName ) {
      return Cytoscape.getCurrentNetwork().isFlagged( Cytoscape.getCyNode( canonicalName, false ) );
   }

   /**
    * Method <code>selectionChanged</code> 
    * Did the selection change since the last call of storeSelection() ?
    *
    * @return a <code>boolean</code> value
    */
   public boolean selectionChanged() {
      Vector sel = getSelectedNodes();
      return ( sel == null && selection != null ) ||
	 ( selection == null && sel != null ) ||
	 selection.size() != sel.size() ||
	 ! selection.equals( sel );
   }

   /**
    * Method <code>getAllNodes</code> 
    * Get the canonical names of all nodes in the graph
    *
    * @return a <code>Vector</code> value
    */
   public Vector getAllNodes() {
      List nodes = Cytoscape.getCurrentNetwork().nodesList();
      Vector out = new Vector( nodes.size() );
      Iterator i = nodes.iterator();
      while ( i.hasNext() )
	 //out.add( ( ( CyNode )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getNodeAttributeValue( (CyNode) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>countAllNodes</code> 
    * Get the total number of nodes in the graph
    *
    * @return an <code>int</code> value
    */
   public int countAllNodes() {
      return Cytoscape.getCurrentNetworkView().nodeCount();
   }

   /**
    * Method <code>getSelectedEdges</code> 
    * Get the canonical names of all selected edges
    *
    * @return a <code>Vector</code> value
    */
   public Vector getSelectedEdges() {
      Set edges = Cytoscape.getCurrentNetwork().getFlaggedEdges();
      Vector out = new Vector( edges.size() );
      Iterator i = edges.iterator();
      while ( i.hasNext() ) //out.add( ( ( CyEdge )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>countSelectedEdges</code> 
    * Get the number of selected edges
    *
    * @return an <code>int</code> value
    */
   public int countSelectedEdges() {
      return Cytoscape.getCurrentNetwork().getFlaggedEdges().size();
   }
   
   /**
    * Method <code>getAllEdges</code> 
    * Get the canonical names of all edges in the graph
    *
    * @return a <code>Vector</code> value
    */
   public Vector getAllEdges() {
      List edges = Cytoscape.getCurrentNetwork().edgesList();
      Vector out = new Vector( edges.size() );
      Iterator i = edges.iterator();
      while ( i.hasNext() ) 
	 //out.add( ( ( CyEdge )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>countAllEdges</code> 
    * Get the total number of edges in the graph
    *
    * @return an <code>int</code> value
    */
   public int countAllEdges() {
      return Cytoscape.getCurrentNetworkView().edgeCount();
   }

   /**
    * Method <code>clearSelection</code> 
    * Deselect all nodes and edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean clearSelection() {
      clearNodeSelection();
      clearEdgeSelection();
      return true;
   }

   /**
    * Method <code>clearNodeSelection</code> 
    * Deselect all nodes
    *
    * @return a <code>boolean</code> value
    */
   public boolean clearNodeSelection() {
      Cytoscape.getCurrentNetwork().unFlagAllNodes();
      return true;
   }

   /**
    * Method <code>clearEdgeSelection</code> 
    * Deselect all edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean clearEdgeSelection() {
      Cytoscape.getCurrentNetwork().unFlagAllEdges();
      return true;
   }

   /**
    * Method <code>selectNode</code> 
    * Select the node with the given canonical name
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNode( String canonicalName ) {
      CyNode node = Cytoscape.getCyNode( canonicalName, false );
      if ( node != null ) Cytoscape.getCurrentNetwork().setFlagged( node, true );
      return true;
   }

   /**
    * Method <code>deselectNode</code> 
    * Deselect the node with the given canonical name
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean deselectNode( String canonicalName ) {
      CyNode node = Cytoscape.getCyNode( canonicalName, false );
      if ( node != null ) Cytoscape.getCurrentNetwork().setFlagged( node, false );
      return true;
   }

   /**
    * Method <code>selectNodes</code> 
    * Select the nodes with the given canonical names
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNodes( Vector canonicalNames ) {
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ )
	 selectNode( (String) canonicalNames.get( i ) );
      return true;
   }

   protected CyEdge getEdge( String canonicalName ) {
      String toks[] = canonicalName.split( "\\s+" );
      if ( toks[ 1 ].startsWith( "(" ) ) toks[ 1 ] = toks[ 1 ].substring( 1 );
      if ( toks[ 1 ].endsWith( ")" ) ) toks[ 1 ] = toks[ 1 ].substring( 0, toks[ 1 ].length() - 1 );
      CyEdge edge = Cytoscape.getCyEdge( toks[ 0 ], canonicalName, toks[ 2 ], toks[ 1 ] );
      return edge;
   }

   /**
    * Method <code>selectEdge</code> 
    * Select the edge with the given canonical name
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectEdge( String canonicalName ) {
      CyEdge edge = getEdge( canonicalName );
      if ( edge != null ) Cytoscape.getCurrentNetwork().setFlagged( edge, true );
      return true;
   }

   /**
    * Method <code>selectEdges</code> 
    * Select the edges with the given canonical names
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectEdges( Vector canonicalNames ) {
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ )
	 selectEdge( (String) canonicalNames.get( i ) );
      return true;
   }

   /**
    * Method <code>getNodeAttributeNames</code> 
    * Get all the different possible node attribute names available
    *
    * @return a <code>Vector</code> value
    */
   public Vector getNodeAttributeNames() {
      String out[] = Cytoscape.getNodeNetworkData().getAttributeNames();
      return stringArrayToVector( out );
   }

   /**
    * Method <code>getEdgeAttributeNames</code> 
    * Get all the different possible edge attribute names available
    *
    * @return a <code>Vector</code> value
    */
   public Vector getEdgeAttributeNames() {
      String out[] = Cytoscape.getEdgeNetworkData().getAttributeNames();
      return stringArrayToVector( out );
   }

   /**
    * Method <code>graphHasNodeAttribute</code> 
    * Does the given node attribute exist?
    *
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean graphHasNodeAttribute( String attr ) {
      return Cytoscape.getNodeNetworkData().hasAttribute( attr );
   }

   /**
    * Method <code>hasNodeAttribute</code> 
    * Does the given node attribute exist for the given node?
    *
    * @param canonicalName a <code>String</code> value
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean hasNodeAttribute( String canonicalName, String attr ) {
      return Cytoscape.getNodeNetworkData().hasAttribute( attr, canonicalName );
   }

   /**
    * Method <code>graphHasEdgeAttribute</code> 
    * Does the given edge attribute exist?
    *
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean graphHasEdgeAttribute( String attr ) {
      return Cytoscape.getEdgeNetworkData().hasAttribute( attr );
   }

   /**
    * Method <code>hasEdgeAttribute</code> 
    * Does the given edge attribute exist for the given edge?
    *
    * @param canonicalName a <code>String</code> value
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean hasEdgeAttribute( String canonicalName, String attr ) {
      return Cytoscape.getEdgeNetworkData().hasAttribute( attr, canonicalName );
   }

   /**
    * Method <code>getNodeSpecies</code> 
    * Get the species of the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>String</code> value
    */
   public String getNodeSpecies( String canonicalName ) {
      Vector v = getNodeAttribute( canonicalName, "species" );
      String out = v.size() > 0 ? getNodeAttribute( canonicalName, "species" ).get( 0 ).toString() : null;
      if ( out == null || out.length() <= 0 ) out = cytoscape.data.Semantics.getDefaultSpecies( Cytoscape.getCurrentNetwork(), Cytoscape.getCytoscapeObj() );
      return out;
   }

   /**
    * Method <code>getNodesWithAttribute</code> 
    * Return the names of all nodes with the given attribute
    *
    * @param attr a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodesWithAttribute( String attr ) {
      return stringArrayToVector( Cytoscape.getNodeNetworkData().getObjectNames( attr ) );
   }

   /**
    * Method <code>getEdgesWithAttribute</code> 
    * Return the names of all edges with the given attribute
    *
    * @param attr a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getEdgesWithAttribute( String attr ) {
      return stringArrayToVector( Cytoscape.getEdgeNetworkData().getObjectNames( attr ) );
   }

   /**
    * Method <code>getNodeAttribute</code> 
    * Get the given attribute(s) for the given node
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodeAttribute( String canonicalName, String attrName ) {
      if ( Cytoscape.getNodeNetworkData().hasAttribute( attrName ) )
	 return fixVectorForXmlRpc( Cytoscape.getNodeNetworkData().getList( attrName, canonicalName ) );
      else return new Vector();
   }      

   /**
    * Method <code>getEdgeAttribute</code> 
    * Get the given attributes for the given edge
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getEdgeAttribute( String canonicalName, String attrName ) {
      if ( Cytoscape.getEdgeNetworkData().hasAttribute( attrName ) )
	 return fixVectorForXmlRpc( Cytoscape.getEdgeNetworkData().getList( attrName, canonicalName ) );
      else return new Vector();
   }      

   /**
    * Method <code>getNodesAttribute</code> 
    * Get the given attribute for the given list of nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @param attrName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodesAttribute( Vector canonicalNames, String attrName ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) {
	 if ( Cytoscape.getNodeNetworkData().hasAttribute( attrName ) )
	    out.add( getNodeAttribute( (String) canonicalNames.get( i ), attrName ) );
	 else out.add( "" );
      }
      return fixVectorForXmlRpc( out );
   }      

   /**
    * Method <code>getEdgesAttribute</code> 
    * Get the given attribute for the given list of edges
    *
    * @param canonicalNames a <code>Vector</code> value
    * @param attrName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getEdgesAttribute( Vector canonicalNames, String attrName ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) {
	 if ( Cytoscape.getEdgeNetworkData().hasAttribute( attrName ) )
	    out.add( getEdgeAttribute( (String) canonicalNames.get( i ), attrName ) );
	 else out.add( "" );
      }
      return fixVectorForXmlRpc( out );
   }      

   /**
    * Method <code>getNodeAttributes</code> 
    * Get all attributes for the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>Hashtable</code> value
    */
   public Hashtable getNodeAttributes( String canonicalName ) {
      Map map = Cytoscape.getNodeNetworkData().getAttributes( canonicalName );
      return fixMapForXmlRpc( map );
   }      

   /**
    * Method <code>getEdgeAttributes</code> 
    * Get all attributes for the given edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>Hashtable</code> value
    */
   public Hashtable getEdgeAttributes( String canonicalName ) {
      Map map = Cytoscape.getEdgeNetworkData().getAttributes( canonicalName );
      return fixMapForXmlRpc( map );
   }      

   /**
    * Method <code>getNodesAttributes</code> 
    * Get all attributes for the given list of nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodesAttributes( Vector canonicalNames ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ )
	 out.add( getNodeAttributes( (String) canonicalNames.get( i ) ) );
      return fixVectorForXmlRpc( out );
   }      

   /**
    * Method <code>getEdgesAttributes</code> 
    * Get all attributes for the given list of edges
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getEdgesAttributes( Vector canonicalNames ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ )
	 out.add( getEdgeAttributes( (String) canonicalNames.get( i ) ) );
      return fixVectorForXmlRpc( out );
   }      

   /**
    * Method <code>addNodeAttribute</code> 
    * Add the given attribute value to the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @param value a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean addNodeAttribute( String canonicalName, String attrName, String value ) {
      try { Cytoscape.getNodeNetworkData().append( attrName, canonicalName, parseAttribute( value ) ); }
      catch( Exception e ) { 
	 System.err.println( "ERROR in addNodeAttribute (1): " + canonicalName + " " + attrName + " " + value +
			     ": " + e.getMessage() ); 
	 try { Cytoscape.getNodeNetworkData().append( attrName, canonicalName, value ); }
	 catch ( Exception e2 ) { 
	    System.err.println( "ERROR in addNodeAttribute (2): " + canonicalName + " " + attrName + " " + value +
				": " + e2.getMessage() ); 
	 }
      }
      return true;
   }

   /**
    * Method <code>addNodeAttributes</code> 
    * Add the given attributes for the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @param attrs a <code>Hashtable</code> value
    * @return a <code>boolean</code> value
    */
   public boolean addNodeAttributes( String canonicalName, Hashtable attrs ) {
      try {
	 for ( Iterator it = attrs.keySet().iterator(); it.hasNext(); ) {
	    String key = (String) it.next();
	    Object val = attrs.get( key );
	    if ( val instanceof Vector ) 
	       for ( int i = 0, sz = ( (Vector) val ).size(); i < sz; i ++ )
		  addNodeAttribute( canonicalName, key.trim(), ( (Vector) val ).get( i ).toString() ); 
	    else addNodeAttribute( canonicalName, key.trim(), attrs.get( key ).toString() );
	 }
	 
      } catch (Exception e) {
	 e.printStackTrace();
      } // end of try-catch
      
      return true;
   }

   /**
    * Method <code>setNodeAttribute</code> 
    * Set the given attribute value to the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @param value a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean setNodeAttribute( String canonicalName, String attrName, String value ) {
      try { Cytoscape.getNodeNetworkData().set( attrName, canonicalName, parseAttribute( value ) ); }
      catch( Exception e ) { Cytoscape.getNodeNetworkData().set( attrName, canonicalName, value ); }
      return true;
   }

   /**
    * Method <code>setNodeAttributes</code> 
    * Set the given attributes for the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @param attrs a <code>Hashtable</code> value
    * @return a <code>boolean</code> value
    */
   public boolean setNodeAttributes( String canonicalName, Hashtable attrs ) {
      for ( Iterator it = attrs.keySet().iterator(); it.hasNext(); ) {
	 String key = it.next().toString();
	 deleteNodeAttribute( canonicalName, key.trim() );
      }
      addNodeAttributes( canonicalName, attrs );
      return true;
   }

   /**
    * Method <code>addEdgeAttribute</code> 
    * Add the given attribute value to the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @param value a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean addEdgeAttribute( String canonicalName, String attrName, String value ) {
      try { Cytoscape.getEdgeNetworkData().append( attrName, canonicalName, parseAttribute( value ) ); } 
      catch( Exception e ) { 
	 System.err.println( "ERROR in addEdgeAttribute (1): " + canonicalName + " " + attrName + " " + value +
			     ": " + e.getMessage() ); 
	 try { Cytoscape.getEdgeNetworkData().append( attrName, canonicalName, value ); } 
	 catch ( Exception e2 ) { 
	    System.err.println( "ERROR in addEdgeAttribute (2): " + canonicalName + " " + attrName + " " + value +
				": " + e2.getMessage() ); 
	 }
      }
      return true;
   }

   /**
    * Method <code>addEdgeAttributes</code> 
    * Add the given attributes for the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @param attrs a <code>Hashtable</code> value
    * @return a <code>boolean</code> value
    */
   public boolean addEdgeAttributes( String canonicalName, Hashtable attrs ) {
      for ( Iterator it = attrs.keySet().iterator(); it.hasNext(); ) {
	 String key = it.next().toString();
	 Object val = attrs.get( key );
	 if ( val instanceof Vector ) 
	    for ( int i = 0, sz = ( (Vector) val ).size(); i < sz; i ++ )
	       addEdgeAttribute( canonicalName, key.trim(), ( (Vector) val ).get( i ).toString() ); 
	 else addEdgeAttribute( canonicalName, key.trim(), attrs.get( key ).toString() );
      }
      return true;
   }

   /**
    * Method <code>setEdgeAttribute</code> 
    * Set the given attribute value to the given named edge    
    *
    * @param canonicalName a <code>String</code> value
    * @param attrName a <code>String</code> value
    * @param value a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean setEdgeAttribute( String canonicalName, String attrName, String value ) {
      try { Cytoscape.getEdgeNetworkData().set( attrName, canonicalName, parseAttribute( value ) ); }
      catch( Exception e ) { 
	 try { Cytoscape.getEdgeNetworkData().set( attrName, canonicalName, value ); } 
	 catch( Exception ee ) { 
	    System.err.println( "ERROR in setEdgeAttribute: " + canonicalName + " " + attrName + " " + value );
	 }
      }
      return true;
   }

   /**
    * Method <code>setEdgeAttributes</code> 
    * Set the given attributes for the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @param attrs a <code>Hashtable</code> value
    * @return a <code>boolean</code> value
    */
   public boolean setEdgeAttributes( String canonicalName, Hashtable attrs ) {
      try {      
	 for ( Iterator it = attrs.keySet().iterator(); it.hasNext(); ) {
	    String key = it.next().toString();
	    deleteEdgeAttribute( canonicalName, key.trim() );
	 }
	 addEdgeAttributes( canonicalName, attrs );
      } catch ( Exception e) {
	 e.printStackTrace();
      }
      return true;
   }

   /**
    * Method <code>deleteAllNodeAttribute</code> 
    * Delete the given node attribute for all nodes
    *
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean deleteAllNodeAttribute( String attr ) {
      Cytoscape.getNodeNetworkData().deleteAttribute( attr );
      return true;
   }

   /**
    * Method <code>deleteNodeAttribute</code> 
    * Delete the given attribute for the given node
    *
    * @param canonicalName a <code>String</code> value
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean deleteNodeAttribute( String canonicalName, String attr ) {
      Cytoscape.getNodeNetworkData().deleteAttribute( attr, canonicalName );
      return true;
   }

   /**
    * Method <code>deleteAllEdgeAttribute</code> 
    * Delete the given edge attribute for all edges
    *
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean deleteAllEdgeAttribute( String attr ) {
      Cytoscape.getEdgeNetworkData().deleteAttribute( attr );
      return true;
   }

   /**
    * Method <code>deleteEdgeAttribute</code> 
    * Delete the given attribute for the given edge
    *
    * @param canonicalName a <code>String</code> value
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean deleteEdgeAttribute( String canonicalName, String attr ) {
      Cytoscape.getEdgeNetworkData().deleteAttribute( attr, canonicalName );
      return true;
   }

   /**
    * Method <code>getNeighbors</code> 
    * Get all first neighbors (nodes) of the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNeighbors( String canonicalName ) {
      Vector out = new Vector();
      CyNode node = Cytoscape.getCyNode( canonicalName, false );
      if ( node == null ) return out;
      for ( Iterator i = Cytoscape.getCurrentNetwork().neighborsList( node ).iterator(); i.hasNext(); )
	 //out.add( ( ( CyNode )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getNodeAttributeValue( (CyNode) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>getAdjacentNodes</code> 
    * Get the two nodes connected to the given edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getAdjacentNodes( String canonicalName ) {
      Vector out = new Vector();
      CyEdge e =  ( CyEdge )Cytoscape.getEdgeNetworkData().getGraphObject( canonicalName );
      //out.add( e.getSource().getIdentifier() );
      //out.add( e.getTarget().getIdentifier() );
      out.add( Cytoscape.getNodeAttributeValue( e.getSource(), Semantics.CANONICAL_NAME ) );
      out.add( Cytoscape.getNodeAttributeValue( e.getTarget(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>getAdjacentEdges</code> 
    * Get all edges connected to the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getAdjacentEdges( String canonicalName ) {
      Vector out = new Vector();
      CyNode node = Cytoscape.getCyNode( canonicalName, false );
      if ( node == null ) return out;
      for ( Iterator i = Cytoscape.getCurrentNetwork().getAdjacentEdgesList( node, true, true, true ).iterator(); i.hasNext(); )
	 //out.add( ( ( CyEdge )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>getConnectedEdge</code> 
    * Get the first edge that connects the two given nodes
    *
    * @param canonicalName1 a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>String</code> value, even though there may be multiple edges between nodes
    */
   public String getConnectedEdge( String canonicalName1, String canonicalName2 ) {
      Vector out = getConnectedEdges( canonicalName1, canonicalName2 );
      if ( out.size() > 0 ) return (String) out.get( 0 );
      return "";
   }

   /**
    * Method <code>getConnectedEdges</code> 
    * Get the edges that connects the two given nodes
    *
    * @param canonicalName1 a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getConnectedEdges( String canonicalName1, String canonicalName2 ) {
      Vector v = new Vector( 2 ); 
      v.add( canonicalName1 );
      v.add( canonicalName2 );
      Vector out = getConnectedEdges( v );
      return out;
   }

   /**
    * Method <code>getConnectedEdges</code> 
    * Get the edges that connects the given nodes
    *
    * @param nodes a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getConnectedEdges( Vector nodes ) {
      Vector out = new Vector();
      for ( int i = 0; i < nodes.size(); ++i )
	 nodes.set( i, Cytoscape.getCyNode( (String) nodes.get( i ), false ) );
      for ( Iterator i = Cytoscape.getCurrentNetwork().getConnectingEdges( nodes ).iterator(); i.hasNext(); )
	 //out.add( ( ( CyEdge )i.next() ).getIdentifier() );
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      return out;
   }

   /**
    * Method <code>getNodesNeighbors</code> 
    * Get all first neighbors (nodes) of the given nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodesNeighbors( Vector canonicalNames ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) 
	 addVectorTo( out, getNeighbors( (String) canonicalNames.get( i ) ) );
      return out;
   }

   /**
    * Method <code>getNodesConnectedEdges</code> 
    * Get all edges connected to the given nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getNodesConnectedEdges( Vector canonicalNames ) {
      return getConnectedEdges( canonicalNames );
   }

   /**
    * Method <code>getEdgesAdjacentNodes</code> 
    * Get all nodes connected to the given edges
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getEdgesAdjacentNodes( Vector canonicalNames ) {
      Vector out = new Vector();
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) 
	 addVectorTo( out, getAdjacentNodes( (String) canonicalNames.get( i ) ) );
      return out;
   }

   /**
    * Method <code>getNetworkAsList</code> 
    * Get the entire network as a Vector of 2-element Vectors
    *
    * @return a <code>Vector</code> value
    */
   public Vector getNetworkAsList() {
      Vector out = new Vector();
      for( Iterator i = Cytoscape.getCurrentNetwork().edgesList().iterator(); i.hasNext(); ) {
	 Vector ends = new Vector( 2 );
	 CyEdge edge = ( CyEdge )i.next();
	 //ends.add( edge.getSource().getIdentifier() );
	 //ends.add( edge.getTarget().getIdentifier() );
	 ends.add( Cytoscape.getNodeAttributeValue( edge.getSource(), Semantics.CANONICAL_NAME ) );
	 ends.add( Cytoscape.getNodeAttributeValue( edge.getTarget(), Semantics.CANONICAL_NAME ) );
	 out.add( ends );
      }
      return out;
   }

   /**
    * Method <code>getSelectedNetworkAsList</code> 
    * Get the selected subnetwork as a Vector of 2-element Vectors
    *
    * @return a <code>Vector</code> value
    */
   public Vector getSelectedNetworkAsList() {
      Vector out = new Vector();
      //TODO: may fail... need to convert nodeviews to nodes and/or add stuff to CyNetwork(View)
      for ( Iterator i = Cytoscape.getCurrentNetwork().getConnectingEdges( getSelectedNodes() ).iterator();
	    i.hasNext(); ) {
	 Vector ends = new Vector( 2 );
	 CyEdge edge = ( CyEdge )i.next();
	 //ends.add( edge.getSource().getIdentifier() );
	 //ends.add( edge.getTarget().getIdentifier() );
	 ends.add( Cytoscape.getNodeAttributeValue( edge.getSource(), Semantics.CANONICAL_NAME ) );
	 ends.add( Cytoscape.getNodeAttributeValue( edge.getTarget(), Semantics.CANONICAL_NAME ) );
	 out.add( ends );
      }
      return out;
   }

   /**
    * Method <code>getNetworkAsHash</code> 
    * Get the entire network as a HashMap of Vectors
    *
    * @return a <code>Hashtable</code> value
    */
   public Hashtable getNetworkAsHash() {
      Hashtable out = new Hashtable();
      for ( Iterator i = Cytoscape.getCurrentNetwork().nodesList().iterator();
	    i.hasNext(); ) {
	 CyNode node = ( CyNode )i.next();
	 Vector connected = getNeighbors( (String) Cytoscape.getNodeAttributeValue( node, Semantics.CANONICAL_NAME ) );
	 out.put( Cytoscape.getNodeAttributeValue( node, Semantics.CANONICAL_NAME ), connected );
      }
      return out;
   }

   // I AM HERE!!!!

   /**
    * Method <code>getNetworkAsMatrix</code>
    * Get the network as a square symmetric matrix (Vector of Vectors) of ones and zeros.
    * The columns/rows are in the same order as given by getAllNodes().
    *
    * @return a <code>Vector</code> value
    */
   public Vector getNetworkAsMatrix() {
      Vector out = new Vector();
      Vector nodes = getAllNodes();
      Double zero = new Double( 0.0 );
      Double one = new Double( 1.0 );

      for ( int i = 0, sz = nodes.size(); i < sz; i ++ ) {
	 Vector v = new Vector();
	 v.setSize( sz );
	 out.add( v );
      }

      for ( int i = 0, sz = nodes.size(); i < sz; i ++ ) {
	 String node1 = (String) nodes.get( i );
	 for ( int j = 0; j < i; j ++ ) {
	    String node2 = (String) nodes.get( j );
	    String edge = getConnectedEdge( node1, node2 );
	    Double val = "".equals( edge ) ? zero : one;
	    ( (Vector) out.get( i ) ).set( j, val );
	    ( (Vector) out.get( j ) ).set( i, val );
	 }
	 String edge = getConnectedEdge( node1, node1 );
	 Double val = "".equals( edge ) ? zero : one;
	 ( (Vector) out.get( i ) ).set( i, val );
      }
      return out;
   }

   /**
    * Method <code>getSelectedNetworkAsMatrix</code> 
    * Get the selected subnetwork as a square symmetric matrix (Vector of Vectors) of ones and zeros.
    * The columns/rows are in the same order as given by getSelectedNodes().
    *
    * @return a <code>Vector</code> value
    */
   public Vector getSelectedNetworkAsMatrix() {
      Vector out = new Vector();
      Vector nodes = getSelectedNodes();
      Double zero = new Double( 0.0 );
      Double one = new Double( 1.0 );

      for ( int i = 0, sz = nodes.size(); i < sz; i ++ ) {
	 Vector v = new Vector();
	 v.setSize( sz );
	 out.add( v );
      }

      for ( int i = 0, sz = nodes.size(); i < sz; i ++ ) {
	 String node1 = (String) nodes.get( i );
	 for ( int j = 0; j < i; j ++ ) {
	    String node2 = (String) nodes.get( j );
	    String edge = getConnectedEdge( node1, node2 );
	    Double val = "".equals( edge ) ? zero : one;
	    ( (Vector) out.get( i ) ).set( j, val );
	    ( (Vector) out.get( j ) ).set( i, val );
	 }
	 String edge = getConnectedEdge( node1, node1 );
	 Double val = "".equals( edge ) ? zero : one;
	 ( (Vector) out.get( i ) ).set( i, val );
      }
      return out;
   }

   /**
    * Method <code>selectNeighbors</code> 
    * Select the first neighbors (nodes) of the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNeighbors( String canonicalName ) {
      return selectNodes( getNeighbors( canonicalName ) );
   }

   /**
    * Method <code>selectAdjacentEdges</code> 
    * Select the edges connected to the given node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectAdjacentEdges( String canonicalName ) {
      return selectEdges( getAdjacentEdges( canonicalName ) );
   }

   /**
    * Method <code>selectAdjacentNodes</code> 
    * Select the 2 nodes connected to the given edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectConnectedNodes( String canonicalName ) {
      return selectNodes( getAdjacentNodes( canonicalName ) );
   }

   /**
    * Method <code>selectNodesNeighbors</code> 
    * Select the first neighbors (nodes) of the given nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNodesNeighbors( Vector canonicalNames ) {
      return selectNodes( getNodesNeighbors( canonicalNames ) );
   }

   /**
    * Method <code>selectNodesConnectedEdges</code> 
    * Select the edges connected to the given nodes
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNodesConnectedEdges( Vector canonicalNames ) {
      return selectEdges( getNodesConnectedEdges( canonicalNames ) );
   }

   /**
    * Method <code>selectEdgesConnectedNodes</code> 
    * Select the pairs of nodes connected to each of the given edges
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectEdgesAdjacentNodes( Vector canonicalNames ) {
      return selectNodes( getEdgesAdjacentNodes( canonicalNames ) );
   }

   /**
    * Method <code>selectNodesWithoutAttribute</code> 
    * Select the nodes without the given named attribute
    *
    * @param attr a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean selectNodesWithoutAttribute( String attr ) {
      setWaitCursor();      
      Vector nodes = getAllNodes();
      for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
	 String node = (String) i.next();
	 if ( ! Cytoscape.getNodeNetworkData().hasAttribute( attr, node ) )
	    selectNode( node );
      }
      setDefaultCursor();
      return true;
   }

   /**
    * Method <code>createEdge</code> 
    * Create a new edge between the given named nodes with the given interaction type
    *
    * @param canonicalName1 a <code>String</code> value
    * @param edgeType a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public String createEdge( String canonicalName1, String edgeType, 
			     String canonicalName2 ) {
      CyNode n1 = Cytoscape.getCyNode( canonicalName1, false );
      if ( n1 == null ) return "";
      CyNode n2 = Cytoscape.getCyNode( canonicalName2, false );
      if ( n2 == null ) return "";
      CyEdge e = Cytoscape.getCyEdge( n1, n2, Semantics.INTERACTION, edgeType, true );
      Cytoscape.getCurrentNetwork().restoreEdge( e );
      //String edgeName = canonicalName1 + " (" + edgeType + ") " + canonicalName2;
      //Cytoscape.getEdgeNetworkData().addNameMapping( edgeName, e );
      //Cytoscape.getEdgeNetworkData().set( "interaction", edgeName, edgeType );
      return (String) Cytoscape.getEdgeAttributeValue( e, Semantics.CANONICAL_NAME ); //e.getIdentifier();
   }

   /**
    * Method <code>createEdges</code> 
    * Create multiple edges at one time
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean createEdges( Vector canonicalNames ) {
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) {
	 String name = (String) canonicalNames.get( i );
	 String toks[] = name.split( "\\s+" );
	 if ( toks[ 1 ].startsWith( "(" ) ) toks[ 1 ] = toks[ 1 ].substring( 1, toks[ 1 ].length() - 2 );
	 createEdge( toks[ 0 ], toks[ 1 ], toks[ 2 ] );
      }
      return true;
   }

   /**
    * Method <code>createNode</code> 
    * Create a new node of the given name (placed at coords 100,100)
    *
    * @param newCanonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean createNode( String newCanonicalName ) {
      CyNode n = Cytoscape.getCyNode( newCanonicalName, true );
      Cytoscape.getCurrentNetwork().restoreNode( n );
      //Cytoscape.getNodeNetworkData().addNameMapping( newCanonicalName, n );
      return true;
   }

   /**
    * Method <code>createNodeAt</code> 
    * Create a new node of the given name (placed at given coords, with given width, height)
    *
    * @param newCanonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    *
   public boolean createNodeAt( String newCanonicalName, int x, int y, int w, int h ) {
      CyNode n = cWindow.getGraph().createNode( x, y, w, h, newCanonicalName );
      Cytoscape.getNodeNetworkData().addNameMapping( newCanonicalName, n );
      return true;
   }
   */

   /**
    * Method <code>createNodes</code> 
    * Create multiple new nodes at one time (all placed at coords 100,100)
    *
    * @param canonicalNames a <code>Vector</code> value
    * @return a <code>boolean</code> value
    */
   public boolean createNodes( Vector canonicalNames ) {
      for ( int i = 0, sz = canonicalNames.size(); i < sz; i ++ ) 
	 createNode( (String) canonicalNames.get( i ) );
      return true;
   }

   /**
    * Method <code>redrawGraph</code> 
    * Refresh the graph window
    *
    * @return a <code>boolean</code> value
    */
   public boolean redrawGraph() {
      Cytoscape.getCurrentNetworkView().redrawGraph( false, true ); //updateView();
      return true;
   }

   /**
    * Method <code>relayoutGraph</code> 
    * Re-layout the graph
    *
    * @return a <code>boolean</code> value
    */
   public boolean relayoutGraph() {
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
      //view.redrawGraph( true, true );
      view.applyLayout( new cytoscape.layout.SpringEmbeddedLayouter( view ) );
      redrawGraph();
      return true;
   }

   /**
    * Method <code>doLayout</code> 
    * An easier alias for relayoutGraph()
    *
    * @return a <code>boolean</code> value
    */
   public boolean doLayout() {
      return relayoutGraph();
   }

   /**
    * Method <code>setWaitCursor</code> 
    * Show the cursor to the "wait" cursor
    *
    * @return a <code>boolean</code> value
    */
   public boolean setWaitCursor() {
      Cytoscape.getDesktop().setCursor( java.awt.Cursor.getPredefinedCursor( java.awt.Cursor.WAIT_CURSOR ) );
      return true;
   }

   /**
    * Method <code>setDefaultCursor</code> 
    * Set the cursor back to the default cursor
    *
    * @return a <code>boolean</code> value
    */
   public boolean setDefaultCursor() {
      Cytoscape.getDesktop().setCursor( java.awt.Cursor.getPredefinedCursor( java.awt.Cursor.DEFAULT_CURSOR ) );
      return true;
   }

   /**
    * Method <code>hideSelectedNodes</code> 
    * Hide all selected nodes
    *
    * @return a <code>boolean</code> value
    */
   public boolean hideSelectedNodes() { 
      Cytoscape.getCurrentNetwork().hideNodes( Cytoscape.getCurrentNetworkView().getSelectedNodes() );
      return true;
   }

   /**
    * Method <code>hideSelectedEdges</code> 
    * Hide all selected edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean hideSelectedEdges() { 
      Cytoscape.getCurrentNetwork().hideEdges( Cytoscape.getCurrentNetworkView().getSelectedEdges() );
      return true;
   }

   /**
    * Method <code>hideNode</code> 
    * Hide the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean hideNode( String canonicalName ) {
      Cytoscape.getCurrentNetwork().hideNode( Cytoscape.getCyNode( canonicalName, false ) );
      return true;
   }

   /**
    * Method <code>hideEdge</code> 
    * Hide the given edge between given nodes
    *
    * @param canonicalName1 a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean hideEdge( String canonicalName1, String canonicalName2 ) {
      Vector v = new Vector( 2 ); 
      v.add( canonicalName1 );
      v.add( canonicalName2 );
      Vector out = new Vector();
      for ( Iterator i = Cytoscape.getCurrentNetwork().getConnectingEdges( v ).iterator(); i.hasNext(); )
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      //out.add( ( ( CyEdge ) i.next() ).getIdentifier() );
      Cytoscape.getCurrentNetwork().hideEdges( out );
      return true;
   }

   /**
    * Method <code>hideEdge</code> 
    * Hide the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean hideEdge( String canonicalName ) {
      CyEdge edge = getEdge( canonicalName );
      Cytoscape.getCurrentNetwork().hideEdge( edge );
      return true;
   }

   /**
    * Method <code>removeNode</code> 
    * Remove the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean removeNode( String canonicalName ) {
      return hideNode( canonicalName );
   }

   /**
    * Method <code>removeEdge</code> 
    * Remove the given named edge
    *
    * @param canonicalName1 a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean removeEdge( String canonicalName1, String canonicalName2 ) {
      return hideEdge( canonicalName1, canonicalName2 );
   }

   /**
    * Method <code>removeEdge</code> 
    * Remove the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean removeEdge( String canonicalName ) {
      return hideEdge( canonicalName );
   }

   /**
    * Method <code>unhideNode</code> 
    * Unhide the given named node
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean unhideNode( String canonicalName ) {
      Cytoscape.getCurrentNetwork().restoreNode( Cytoscape.getCyNode( canonicalName, false ) );
      return true;
   }

   /**
    * Method <code>unhideEdge</code> 
    * Unhide the given named edge
    *
    * @param canonicalName1 a <code>String</code> value
    * @param canonicalName2 a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean unhideEdge( String canonicalName1, String canonicalName2 ) {
      Vector v = new Vector( 2 ); 
      v.add( canonicalName1 );
      v.add( canonicalName2 );
      Vector out = new Vector();
      for ( Iterator i = Cytoscape.getCurrentNetwork().getConnectingEdges( v ).iterator(); i.hasNext(); )
	 //out.add( ( ( CyEdge ) i.next() ).getIdentifier() );
	 out.add( Cytoscape.getEdgeAttributeValue( (CyEdge) i.next(), Semantics.CANONICAL_NAME ) );
      Cytoscape.getCurrentNetwork().restoreEdges( out );
      return true;
   }

   /**
    * Method <code>unhideEdge</code> 
    * Unhide the given named edge
    *
    * @param canonicalName a <code>String</code> value
    * @return a <code>boolean</code> value
    */
   public boolean unhideEdge( String canonicalName ) {
      CyEdge edge = getEdge( canonicalName );
      Cytoscape.getCurrentNetwork().restoreEdge( edge );
      return true;
   }

   /**
    * Method <code>hideAll</code> 
    * Hide all hidden nodes and edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean hideAll() {
      Cytoscape.getCurrentNetwork().hideNodes( Cytoscape.getCurrentNetwork().nodesList() );
      Cytoscape.getCurrentNetwork().hideEdges( Cytoscape.getCurrentNetwork().edgesList() );
      return true;
   }

   /**
    * Method <code>hideAllNodes</code> 
    * Hide all hidden nodes
    *
    * @return a <code>boolean</code> value
    */
   public boolean hideAllNodes() {
      Cytoscape.getCurrentNetwork().hideNodes( Cytoscape.getCurrentNetwork().nodesList() );
      return true;
   }

   /**
    * Method <code>hideAllEdges</code> 
    * Hide all hidden edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean hideAllEdges() {
      Cytoscape.getCurrentNetwork().hideEdges( Cytoscape.getCurrentNetwork().edgesList() );
      return true;
   }

   /**
    * Method <code>unhideAll</code> 
    * Unhide all hidden nodes and edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean unhideAll() {
      Cytoscape.getCurrentNetwork().restoreNodes( Cytoscape.getCurrentNetwork().nodesList() );
      Cytoscape.getCurrentNetwork().restoreEdges( Cytoscape.getCurrentNetwork().edgesList() );
      return true;
   }

   /**
    * Method <code>unhideAllNodes</code> 
    * Unhide all hidden nodes
    *
    * @return a <code>boolean</code> value
    */
   public boolean unhideAllNodes() {
      Cytoscape.getCurrentNetwork().restoreNodes( Cytoscape.getCurrentNetwork().nodesList() );
      return true;
   }

   /**
    * Method <code>unhideAllEdges</code> 
    * Unhide all hidden edges
    *
    * @return a <code>boolean</code> value
    */
   public boolean unhideAllEdges() {
      Cytoscape.getCurrentNetwork().restoreEdges( Cytoscape.getCurrentNetwork().edgesList() );
      return true;
   }

   /**
    * Method <code>getConditionNames</code> 
    * An interface to the ExpressionData class
    *
    * @return a <code>Vector</code> value
    */
   public Vector getConditionNames() {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return new Vector();
      return stringArrayToVector( data.getConditionNames() );
   }

   /**
    * Method <code>getGeneNames</code> 
    * An interface to the ExpressionData class
    *
    * @return a <code>Vector</code> value
    */
   public Vector getGeneNames() {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return new Vector();
      return data.getGeneNamesVector();
   }

   /**
    * Method <code>getMeasurement</code> 
    * An interface to the ExpressionData class
    *
    * @param gene a <code>String</code> value
    * @param condition a <code>String</code> value
    * @return a <code>double</code> value
    */
   public double getMeasurement( String gene, String condition ) {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return 0.0;
      return data.getMeasurement( gene, condition ).getRatio();
   }

   /**
    * Method <code>getMeasurementSignificance</code> 
    * An interface to the ExpressionData class
    *
    * @param gene a <code>String</code> value
    * @param condition a <code>String</code> value
    * @return a <code>double</code> value
    */
   public double getMeasurementSignificance( String gene, String condition ) {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return 0.0;
      return data.getMeasurement( gene, condition ).getSignificance();
   }
   
   /**
    * Method <code>getMeasurements</code> 
    * An interface to the ExpressionData class
    *
    * @param condition a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getMeasurements( String condition ) {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return new Vector();
      Vector temp = data.getMeasurements( condition );
      Vector out = new Vector();
      for ( int i = 0, sz = temp.size(); i < sz; i ++ ) 
	 out.add( new Double( ( (mRNAMeasurement) temp.get( i ) ).getRatio() ) );
      return out;
   }

   /**
    * Method <code>getMeasurementSignificances</code> 
    * An interface to the ExpressionData class
    *
    * @param condition a <code>String</code> value
    * @return a <code>Vector</code> value
    */
   public Vector getMeasurementSignificances( String condition ) {
      ExpressionData data = Cytoscape.getCurrentNetwork().getExpressionData();
      if ( data == null ) return new Vector();
      Vector temp = data.getMeasurements( condition );
      Vector out = new Vector();
      for ( int i = 0, sz = temp.size(); i < sz; i ++ ) 
	 out.add( new Double( ( (mRNAMeasurement) temp.get( i ) ).getSignificance() ) );
      return out;
   }

   /**
    * Method <code>addSelectionListener</code> 
    * Add a graph selection listener (only for non-remote usage)
    *
    * @param list a <code>CytoTalkSelectionListener</code> value
    * @return a <code>boolean</code> value
    */
   public boolean addSelectionListener( CytoTalkSelectionListener list ) {
      if ( selectionListeners == null ) selectionListeners = new Vector();
      if ( ! selectionListeners.contains( list ) ) selectionListeners.add( list );
      if ( ! added ) Cytoscape.getCurrentNetwork().addFlagEventListener( this );
      added = true;
      return true;
   }

   /**
    * Method <code>removeSelectionListener</code> 
    * Remove a graph selection listener (only for non-remote usage)
    *
    * @param list a <code>CytoTalkSelectionListener</code> value
    * @return a <code>boolean</code> value
    */
   public boolean removeSelectionListener( CytoTalkSelectionListener list ) {
      if ( selectionListeners.contains( list ) ) selectionListeners.remove( list );
      if ( selectionListeners.size() <= 0 ) {
	 Cytoscape.getCurrentNetwork().removeFlagEventListener( this );
	 added = false;
      }
      return true;
   }

   /**
    * Method <code>closeWindow</code> 
    * Close the cytoscape window that this handler is controlling
    *
    * @return a <code>boolean</code> value
    */
   public boolean closeWindow() {
      shutdown();
      Cytoscape.exit();
      return true;
   }

   public void finalize() {
      try { shutdown(); } catch ( Exception e) { };
   }

   protected void shutdown() {
      if ( server == null ) return;
      server.removeService( CytoTalkPlugin.service );
      server.shutdown();
      server = null;      
   }

   protected int startNewCytoTalk( /*CytoscapeWindow newWindow*/ ) {
      //if ( newWindow != null ) {

      /*String args[] = newWindow.getConfiguration().getArgs();
	for ( int i = 0; i < args.length; i ++ ) {
	if ( args[ i ].equals( "--startCytoTalk" ) )
	return CytoTalkPlugin.STATIC_PORT - 1;
	}*/

      try {
	 int localPort = CytoTalkPlugin.STATIC_PORT ++;
	 MyXmlRpcServer newServer = new MyXmlRpcServer( localPort );
	 final CytoTalkHandler newHandler = new CytoTalkHandler( newServer );
	 newServer.addService( CytoTalkPlugin.service, newHandler );

	 Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	    try { newHandler.shutdown(); } catch ( Exception e ) { }; } } );

	 Cytoscape.getDesktop().addWindowListener( new WindowAdapter() {
	       public void windowClosing( WindowEvent we ) {
		  try { newHandler.shutdown(); } catch ( Exception e ) { };
	       } } );

	 //JOptionPane.showMessageDialog( Cytoscape.getDesktop(), 
	 //	"Started a CytoTalk handler listening on port " + localPort );
	 return localPort;
      } catch( Exception ee ) {
	 System.err.println( "Could not start service: " + ee.getMessage() );
      }
      //}
      return -1;
   }

   public String getNodeAttributeClass( String attr ) {
      return Cytoscape.getNodeNetworkData().getClass( attr ) != null ? Cytoscape.getNodeNetworkData().getClass( attr ).getName() : "";
   }

   public Vector getUniqueNodeAttributeValues( String attr ) {
      return objArrayToVector( Cytoscape.getNodeNetworkData().getUniqueValues( attr ) );
   }

   protected void addVectorTo( Vector toVec, Vector inVec ) {
      for ( int i = 0, sz = inVec.size(); i < sz; i ++ )
	 if ( ! toVec.contains( inVec.get( i ) ) ) toVec.add( inVec.get( i ) );
   }

   protected Vector objArrayToVector( Object[] in ) {
      Vector out = new Vector();
      if ( in == null ) return out;
      for ( int i = 0; i < in.length; i ++ ) {
	 Object obj = in[ i ];
	 if ( obj instanceof Vector || obj instanceof Hashtable || 
	      obj instanceof Double || obj instanceof Integer || 
	      obj instanceof Boolean ) out.add( obj );
	 else out.add( obj.toString() );
      }
      return fixVectorForXmlRpc( out );
   }

   protected Vector stringArrayToVector( String[] in ) {
      Vector out = new Vector();
      if ( in == null ) return out;
      for ( int i = 0; i < in.length; i ++ ) out.add( in[ i ].toString() );
      return out;
   }

   protected Vector doubleArrayToVector( double[] in ) {
      Vector out = new Vector();
      if ( in == null ) return out;
      for ( int i = 0; i < in.length; i ++ ) out.add( new Double( in[ i ] ) );
      return out;
   }

   protected Hashtable fixMapForXmlRpc( Map map ) {
      Hashtable out = new Hashtable();
      if ( map == null ) return out;
      for ( Iterator it = map.keySet().iterator(); it.hasNext(); ) {
	 Object next = it.next();
	 Object obj = map.get( next );
	 if ( obj instanceof String || obj instanceof Double || 
	      obj instanceof Integer || obj instanceof Boolean ) out.put( next, obj );
	 else if ( obj instanceof Vector ) out.put( next, fixVectorForXmlRpc( (Vector) obj ) );
	 else if ( obj instanceof Map ) out.put( next, fixMapForXmlRpc( (Map) obj ) );
	 else out.put( next, obj.toString() );
      }
      return out;
   }

   protected Vector fixVectorForXmlRpc( Vector v ) {
      Vector out = new Vector();
      for ( int i = 0, sz = v.size(); i < sz; i ++ ) {
	 Object obj = v.get( i );
	 if ( obj instanceof String || obj instanceof Double || 
	      obj instanceof Integer || obj instanceof Boolean ) out.add( obj );
	 else if ( obj instanceof Vector ) out.add( fixVectorForXmlRpc( (Vector) obj ) );
	 else if ( obj instanceof Map ) out.add( fixMapForXmlRpc( (Map) obj ) );
	 else out.add( obj.toString() );
      }
      return out;
   }

   /**
    * Method <code>newWindowEmpty</code> 
    * Create a new empty cytoscape window with a new cytotalk handler listening on a new port
    *
    * @return an <code>int</code> value
    */
   public int newWindowEmpty() {
      clearSelection();
      return newWindowSelectedNodes();
   }

   /**
    * Method <code>newWindowSelectedNodes</code> 
    * Create a new cytoscape window with the currently-selected nodes and a new cytotalk handler 
    * listening on a new port
    *
    * @return an <code>int</code> value
    */
   public int newWindowSelectedNodes() {
      ( new cytoscape.actions.NewWindowSelectedNodesOnlyAction() ).actionPerformed( null );
      return startNewCytoTalk();      
   }

   /**
    * Method <code>newWindowSelected</code> 
    * Create a new cytoscape window with the currently-selected nodes and edges and a new cytotalk 
    * handler listening on a new port
    *
    * @return an <code>int</code> value
    */
   public int newWindowSelected() {
      ( new cytoscape.actions.NewWindowSelectedNodesEdgesAction() ).actionPerformed( null );
      return startNewCytoTalk();      
   }

   /**
    * Method <code>newWindowClone</code> 
    * Create a new cytoscape window with a clone of this graph and a new cytotalk handler listening on a new port
    *
    * @return an <code>int</code> value
    */
   public int newWindowClone() {
      ( new cytoscape.actions.CloneGraphInNewWindowAction() ).actionPerformed( null );
      return startNewCytoTalk();      
   }

   /**
    * Method <code>getDataMatrixNames</code> 
    * Get the names of the available data matrices
    *
    * @return a <code>Vector</code> value
    *
   public Vector getDataMatrixNames() {
      String args[] = cWindow.getConfiguration().getArgs();
      Vector list = new Vector();
      for ( int i = 0; i < args.length; i ++ ) {
	 if ( args[ i ].equals( "--matrix" ) ) {
	    if ( i + 1 > args.length ) 
	       throw new IllegalArgumentException( "error! no --matrix value" );
	    else list.add( args[ i + 1 ] );
	 }
      }
      return list;
   }

   /**
    * Method <code>getDataMatrixRowTitles</code> 
    * Get the row titles for the given named data matrix
    *
    * @param matrixName a <code>String</code> value
    * @return a <code>Vector</code> value
    *
   public Vector getDataMatrixRowTitles( String matrixName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      return mat != null ? stringArrayToVector( mat.getRowTitles() ) : new Vector();
   }

   /**
    * Method <code>getDataMatrixColumnTitles</code> 
    * Get the column titles for the given named data matrix
    *
    * @param matrixName a <code>String</code> value
    * @return a <code>Vector</code> value
    *
   public Vector getDataMatrixColumnTitles( String matrixName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      return mat != null ? stringArrayToVector( mat.getColumnTitles() ) : new Vector();
   }

   /**
    * Method <code>getDataMatrix</code> 
    * Get the values in the matrix as a Vector of Vectors (indexed by row)
    *
    * @param matrixName a <code>String</code> value
    * @return a <code>Vector</code> value
    *
   public Vector getDataMatrix( String matrixName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      Vector rows = getDataMatrixRowTitles( matrixName );
      Vector out = new Vector();
      for ( int i = 0, sz = rows.size(); i < sz; i ++ ) {
	 String rowName = (String) rows.get( i );
	 out.add( doubleArrayToVector( getRow( mat, rowName ) ) );
      }
      return out;
   }

   /**
    * Method <code>getDataMatrixValue</code> 
    * Get the value in the named matrix at the given named row,column coordinate
    *
    * @param matrixName a <code>String</code> value
    * @param rowName a <code>String</code> value
    * @param colName a <code>String</code> value
    * @return a <code>double</code> value
    *
   public double getDataMatrixValue( String matrixName, String rowName, String colName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      double arr[] = getRow( mat, rowName );
      Vector cols = getDataMatrixColumnTitles( matrixName );
      int ind = -1, nRows = mat.getRowCount();
      for ( int i = 0, sz = cols.size(); i < sz; i ++ ) 
	 if ( ( (String) cols.get( i ) ).equals( colName ) ) { ind = i; break; }
      if ( arr != null && ind >= 0 ) return arr[ ind ];
      return 0.0;
   }

   /**
    * Method <code>getDataMatrixRow</code> 
    * Get the values in the given named matrix row in the given named data matrix
    *
    * @param matrixName a <code>String</code> value
    * @param rowName a <code>String</code> value
    * @return a <code>double[]</code> value
    *
   public Vector getDataMatrixRow( String matrixName, String rowName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      return doubleArrayToVector( mat != null ? getRow( mat, rowName ) : new double[ 0 ] );
   }

   /**
    * Method <code>getDataMatrixRows</code> 
    * Get the values in the given named matrix rows in the given named data matrix
    *
    * @param matrixName a <code>String</code> value
    * @param rowNames a <code>Vector</code> value
    * @return a <code>Hashtable</code> value
    *
   public Hashtable getDataMatrixRows( String matrixName, Vector rowNames ) {
      DataMatrix mat = readDataMatrix( matrixName );
      Hashtable out = new Hashtable();
      for ( int i = 0, sz = rowNames.size(); i < sz; i ++ ) {
	 String rowName = (String) rowNames.get( i );
	 out.put( rowName, getDataMatrixRow( matrixName, rowName ) );
      }
      return out;
   }

   /**
    * Method <code>getDataMatrixAsRows</code> 
    * Return the entire named data matrix, indexed by row names
    *
    * @param matrixName a <code>String</code> value
    * @return a <code>Hashtable</code> value
    *
   public Hashtable getDataMatrixAsRows( String matrixName ) {
      return getDataMatrixRows( matrixName, getDataMatrixRowTitles( matrixName ) );
   }

   /**
    * Method <code>getDataMatrixColumn</code> 
    * Return the values in the given named column for the given named matrix
    *
    * @param matrixName a <code>String</code> value
    * @param colName a <code>String</code> value
    * @return a <code>Vector</code> value
    *
   public Vector getDataMatrixColumn( String matrixName, String colName ) {
      DataMatrix mat = readDataMatrix( matrixName );
      Vector cols = getDataMatrixColumnTitles( matrixName );
      int ind = -1, nRows = mat.getRowCount();
      for ( int i = 0, sz = cols.size(); i < sz; i ++ ) 
	 if ( ( (String) cols.get( i ) ).equals( colName ) ) { ind = i; break; }
      Vector out = new Vector();
      if ( ind >= 0 ) {
	 for ( int i = 0; i < nRows; i ++ ) 
	    out.add( new Double( mat.get( i, ind ) ) );
      }
      return out;
   }

   /**
    * Method <code>getDataMatrixColumns</code> 
    * Get the values in the given named matrix columns for the given named matrix
    *
    * @param matrixName a <code>String</code> value
    * @param colNames a <code>Vector</code> value
    * @return a <code>Hashtable</code> value
    *
   public Hashtable getDataMatrixColumns( String matrixName, Vector colNames ) {
      DataMatrix mat = readDataMatrix( matrixName );
      Hashtable out = new Hashtable();
      for ( int i = 0, sz = colNames.size(); i < sz; i ++ ) {
	 String colName = (String) colNames.get( i );
	 out.put( colName, getDataMatrixColumn( matrixName, colName ) );
      }
      return out;
   }

   /**
    * Method <code>getDataMatrixAsColumns</code> 
    * Return the entire named data matrix, indexed by column names
    *
    * @param matrixName a <code>String</code> value
    * @return a <code>Hashtable</code> value
    *
   public Hashtable getDataMatrixAsColumns( String matrixName ) {
      return getDataMatrixColumns( matrixName, getDataMatrixColumnTitles( matrixName ) );
   }
   */

   /*
   protected Node[] selectedNodes() {
      NodeCursor nc = cWindow.getGraph().selectedNodes();
      Node nodes[] = new Node[ nc.size() ];
      int i = 0;
      while ( nc.ok() ) {
	 nodes[ i ++ ] = nc.node();
	 nc.next();
      }
      return nodes;
   }

   protected Edge[] selectedEdges() {
      EdgeCursor ec = cWindow.getGraph().selectedEdges();
      Edge edges[] = new Edge[ ec.size() ];
      int i = 0;
      while ( ec.ok() ) {
	 edges[ i ++ ] = ec.edge();
	 ec.next();
      }
      return edges;
   }

   protected double[] getRow( DataMatrix mat, String rowName ) {
      String rowTitles[] = mat.getRowTitles();
      for (int i = 0; i < rowTitles.length; i ++ ) 
	 if ( rowTitles[ i ].equals( rowName ) )
	    return mat.get( i );
      return null;
   }

   protected DataMatrix readDataMatrix( String matName ) {
      if ( dataMatrices != null && dataMatrices.get( matName ) != null ) 
	 return (DataMatrix) dataMatrices.get( matName );

      try {
	 DataMatrixReader reader = DataMatrixReaderFactory.createReader( matName );
	 reader.read();
	 DataMatrix result[] = reader.get();
	 if ( dataMatrices == null ) dataMatrices = new HashMap();
	 dataMatrices.put( matName, result[ 0 ] );
	 return result[ 0 ];
      } catch ( Exception e ) {
	 e.printStackTrace();
	 return null;
      }      
   }
   */

   public void onFlagEvent( FlagEvent e ) {
      if ( selectionListeners.size() > 0 && 
	   ( e.getTargetType() == FlagEvent.SINGLE_NODE ||
	     e.getTargetType() == FlagEvent.NODE_SET ) ) {
	 Object obj = e.getTarget();
	 boolean sel = e.getEventType();
	 if ( e.getTargetType() == FlagEvent.SINGLE_NODE ) {
	    if ( ! ( obj instanceof CyNode ) ) return;
	    CyNode n = (CyNode) obj;
	    String canonicalName = Cytoscape.getNodeNetworkData().getCanonicalName( n );
	    for ( int i = 0, sz = selectionListeners.size(); i < sz; i ++ ) 
	       ( (CytoTalkSelectionListener) selectionListeners.get( i ) ).
		  nodeSelected( canonicalName, sel );
	 } else if ( e.getTargetType() == FlagEvent.NODE_SET ) {
	    if ( ! ( obj instanceof Set ) ) return;
	    for ( Iterator it = ( (Set) obj ).iterator(); it.hasNext(); ) {
	       CyNode n = (CyNode) it.next();
	       String canonicalName = Cytoscape.getNodeNetworkData().getCanonicalName( n );
	       for ( int i = 0, sz = selectionListeners.size(); i < sz; i ++ ) 
		  ( (CytoTalkSelectionListener) selectionListeners.get( i ) ).
		     nodeSelected( canonicalName, sel );
	    }
	 }
      }
   }

   /**
    * Method <code>splitAttributesOnSemis</code> 
    * Split any added node attribute on semicolons (and turn into a vector)?
    *
    * @param split a <code>boolean</code> value
    * @return a <code>boolean</code> value
    */
   public boolean splitAttributesOnSemis( boolean split ) {
      SPLIT_ON_SEMIS = split;
      return split;
   }

   protected static Object parseAttribute( String value ) {
      if ( value.startsWith( "http://" ) ) {
	 try { return new URL( value ); }
	 catch( Exception e ) { ; }
      }
      value = value.trim();
      if ( "true".equals( value ) ) return new Boolean( true );
      else if ( "false".equals( value ) ) return new Boolean( false );
      else {
	 int intval = isIntParseable( value );
	 if ( intval != Integer.MIN_VALUE ) {
	    return new Integer( intval );
	 } else {
	    double val = isDoubleParseable( value );
	    if ( ! Double.isNaN( val ) ) {
	       return new Double( val );
	    } else if ( SPLIT_ON_SEMIS && value.indexOf( ';' ) >= 0 || value.indexOf( '|' ) >= 0 ) {
	       String toks[] = value.indexOf( ';' ) >= 0 ? value.split( "\\;" ) : value.split( "\\|" );
	       Vector out = new Vector();
	       for ( int i = 0; i < toks.length; i ++ ) {
		  Object o = parseAttribute( toks[ i ] );
		  if ( o.getClass().isArray() ) {
		     Object oo[] = (Object[]) o;
		     for ( int j = 0; j < oo.length; j ++ ) out.add( oo[ j ] );
		  } else out.add( parseAttribute( toks[ i ] ) );
	       }
	       return vectorToArray( out );
	    }
	 }
      }
      return value;
   }

   protected static double isDoubleParseable( String s ) {
      double out = Double.NaN;
      try { out = Double.parseDouble( s ); }
      catch( Exception e ) { out = Double.NaN; }
      return out;
   }

   protected static int isIntParseable( String s ) {
      int out = Integer.MIN_VALUE;
      try { out = Integer.parseInt( s ); }
      catch( Exception e ) { out = Integer.MIN_VALUE; }
      return out;
   }

   protected static Object[] vectorToArray( Vector in ) {
      if ( in == null || in.size() <= 0 ) return new Object[ 0 ];
      Object[] out = new Object[ in.size() ];
      for ( int i = 0; i < in.size(); i ++ ) out[ i ] = in.get( i );
      return out;
   }
}
