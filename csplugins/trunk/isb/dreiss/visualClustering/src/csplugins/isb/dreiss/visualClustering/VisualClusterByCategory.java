package csplugins.isb.dreiss.visualClustering;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>VisualClusterByCategory</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class VisualClusterByCategory extends VisualClustering {
   protected HashMap categoryNodes = new HashMap();

   public VisualClusterByCategory( CytoscapeDesktop cWindow, CytoTalkHandler handler ) {
      init( cWindow, handler );
   }

   protected void init( CytoscapeDesktop cWindow, CytoTalkHandler handler ) {
      this.cWindow = cWindow;
      this.handler = handler;
   }

   public void doCallback( String attributes[], AttributeChooser chooser ) {
      chooser.hide();
      performLayoutByAttribute( attributes, chooser );
   }

   public void performLayoutByAttribute( String attributes[], AttributeChooser chooser ) {
      deleteCategoryNodes();

      boolean reLayout = chooser.doRelayout();
      boolean hideOthers = chooser.hideOthers();
      if ( reLayout && hideOthers ) handler.hideAllEdges();
      boolean deleteEdgesLater = chooser.deleteEdges();
      Vector addedEdges = null;
      if ( deleteEdgesLater ) addedEdges = new Vector();
      boolean restoreOthers = chooser.restoreOthers();

      for ( int i = 0; i < attributes.length; i ++ ) {
	 String attributeName = attributes[ i ];
	 Vector categories = getCurrentAttributeValues( attributeName );
	 createCategoryNodes( categories );
	 Vector newEdges = addCategoryEdges( attributeName, chooser );
	 if ( deleteEdgesLater ) addedEdges.addAll( newEdges );
      }

      if ( reLayout ) {
	 handler.relayoutGraph();
	 if ( hideOthers && restoreOthers ) handler.unhideAllEdges();
      }
      if ( deleteEdgesLater ) { // Hide if not too many; delete if there are a lot
	 for ( int i = 0, sz = addedEdges.size(); i < sz; i ++ ) {
	    if ( sz < 1000 ) handler.hideEdge( (String) addedEdges.get( i ) );
	    else handler.removeEdge( (String) addedEdges.get( i ) );
	 }
      }
      handler.redrawGraph();
   }

   Vector getCurrentAttributeValues( String attributeName ) {
      String nodes[] = (String[]) handler.getSelectedNodes().toArray( new String[ 0 ] );
      Vector categoriesFound = new Vector();

      for (int i=0; i < nodes.length; i++) {
	 String canonicalName = nodes[ i ];
	 if ( canonicalName == null ) continue;
	 Vector allValuesThisNode = handler.getNodeAttribute( canonicalName, attributeName );
	 if ( allValuesThisNode == null ) continue;
	 for (int c = 0; c < allValuesThisNode.size(); c ++ ) {
	    if ( ! categoriesFound.contains( allValuesThisNode.get( c ) ) )
	       categoriesFound.add( allValuesThisNode.get( c ) );
	 }
      }
      return categoriesFound;
   }

   protected Vector addCategoryEdges( String attributeName, AttributeChooser chooser ) {
      boolean deleteEdgesLater = chooser.deleteEdges();
      Vector addedEdges = null;
      if ( deleteEdgesLater ) addedEdges = new Vector();

      if ( attributeName.equals( MRNA_ATTRIBUTE ) ) {
	 System.err.println("CO-EXPRESSION");
	 //ExpressionData data = cWindow.getExpressionData();
	 return addedEdges;
      } else if ( attributeName.equals( HOMOLOGY_ATTRIBUTE ) ) {
	 System.err.println("HOMOLOGY");
      }

      String nodes[] = (String[]) handler.getSelectedNodes().toArray( new String[ 0 ] );
      for (int i=0; i < nodes.length; i++) {
	 String canonicalName = nodes[ i ];
	 if ( canonicalName == null ) continue;
	 Vector values = handler.getNodeAttribute( canonicalName, attributeName );
	 for (int c = 0; c < values.size(); c ++ ) {
	    Object value = values.get( c );
	    String groupingNode = (String) categoryNodes.get( values.get( c ) );
	    String newEdge = handler.createEdge( groupingNode, attributeName + "=" + value, nodes[ i ] );
	    handler.setNodeAttribute( newEdge, "VisualCategorizing", attributeName );
	    if ( deleteEdgesLater ) addedEdges.add( newEdge );
	 }
      }
      return addedEdges;
   }

   /**
    *  create a new node for each annotation named category.  give each new node an
    *  attribute (categorizer='true') which may be used to control its visual appearance.
    *  register the new nodes node-to-name mapping.  finally, save a reference to this
    *  node in a hash, by name, so that 
    *  <ul>
    *     <li> they may be deleted later (as when another level of annotation layout is desired)
    *     <li> we can create temporary edges, for layout, between regular nodes (which have annotation)
    *          and these nodes (which represent the annotation category)
    *     <li> so that these temporary edges can be deleted
    *  </ul>
    */
   protected void createCategoryNodes(Vector categories) {
      for ( int i = 0; i < categories.size(); i ++ ) {
	 Object category = categories.get( i );
	 String newNodeName = category.toString();
	 handler.createNode( newNodeName );
	 handler.addNodeAttribute( newNodeName, "VisualClusteringCategorizer", "true" );
	 categoryNodes.put( category, newNodeName );
      }
      //nodeAttributes.setCategory( "VisualClusteringCategorizer", "categorizer" );
      //handler.redrawGraph();
   }

   protected void deleteCategoryNodes() {
      if (categoryNodes == null || categoryNodes.size() == 0) return;

      String[] names = (String[]) categoryNodes.keySet().toArray(new String[0]);
      for (int i=0; i < names.length; i++) {
	 String node = (String) categoryNodes.get (names[i]);
	 handler.removeNode( node );
      }

      categoryNodes = new HashMap();
   }

   protected void performSave() { };
}
