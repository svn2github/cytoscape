package csplugins.metabolic;

import cytoscape.*;
import cytoscape.giny.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.Color;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.beans.*;
import java.util.*;

import javax.swing.JMenuItem;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.*;
import phoebe.*;
import phoebe.util.*;
import giny.model.*;
import giny.view.*;

public class ShadowNode 
  extends 
    PNodeView {
  

  private List children;
 

  public ShadowNode ( int index, PGraphView view ) {
    super( index, view );
  }

 
  protected void initializeNodeView () {

    view.addNodeView( getRootGraphIndex(), this );
    children = new ArrayList();
    splitNode();
    setNodePosition( false );
   //  setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
//                view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
   
//     setHeight( 20 );
//     setWidth( 20 );
   

    //  setStrokePaint( Color.black  );
    //   setPaint( Color.white );
  //   this.visible = true;
//     this.selected = false;
//     this.notUpdated = false;
//     setPickable(true);
//     invalidatePaint();
  }


  protected void splitNode () {
    
    // for each edge, we make a new ShadowAlias
    int index = getRootGraphIndex();
    GraphPerspective perspective = getGraphView().getGraphPerspective();
    int[] edges = perspective.getAdjacentEdgeIndicesArray(index, true, true, true );
    PhoebeNetworkView view = ( PhoebeNetworkView )getGraphView();

    // iterate through each edge, and figure out which node is not this one
    for ( int i = 0; i < edges.length; ++i ) {
      int source = perspective.getEdgeSourceIndex( edges[i] );
      if ( source > 0 )
        source = perspective.getRootGraphNodeIndex( source );
      
      int target = perspective.getEdgeTargetIndex( edges[i] );
      if ( target > 0 )
        target = perspective.getRootGraphNodeIndex( target );

      // assinging the other_index to the index that is not the index of this node
      int other_index;
      if ( source == index )
        other_index = target;
      else
        other_index = source;

      //System.out.println( "Making new ShadowAlias: "+index+" opposte: "+other_index );
      ShadowAlias alias = new ShadowAlias(  index,  view, other_index, this );
      //System.out.println( "ShadowAlias: "+alias );
      children.add( alias );
      view.addToNodeLayer( alias );
  
      PLabel label = new PLabel( getNode().getIdentifier(), alias );
      alias.setLabel( label );

      PEdgeView edge = ( PEdgeView )view.getEdgeView( edges[i] );
      //System.out.println( "edge: "+edge.getRootGraphIndex()+" source: "+edge.source.getRootGraphIndex()+" target: "+edge.target.getRootGraphIndex() );
     
       if ( source == index )
         edge.setSourceNode( alias );
       else
         edge.setTargetNode( alias );
    

    }

    removeFromParent();
    ( ( PLabel )getLabel() ).removeFromParent();

    view.redrawGraph();

  }

 
  public void setSelectedPaint ( Paint paint ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_SELECTION_PAINT,
                                paint );
    if ( selected ) {
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setPaint( paint );
      }
    }
  }

  public void setUnselectedPaint ( Paint paint ) {
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_PAINT,
                                paint );
   
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setPaint( paint );
      }
   
  }


 /**
   * @param b_paint the paint the border will use
   */ 
  public void setBorderPaint ( Paint b_paint ) { 
    view.setNodeObjectProperty( rootGraphIndex,
                                PGraphView.NODE_BORDER_PAINT,
                                b_paint );
   
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setStrokePaint( b_paint );
      }
   
  }
 


  /**
   * @param border_width The width of the border.
   */
  public void setBorderWidth ( float border_width ) {
    view.setNodeFloatProperty( rootGraphIndex,
                               PGraphView.NODE_BORDER_WIDTH,
                               border_width );
   
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setStroke( new BasicStroke( border_width ) );
      }
   
  }


  /**
   * @param stroke the new stroke for the border
   */
  public void setBorder ( Stroke stroke ) {
   
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setStroke( stroke );
      }
   
  }

 /**
   * TODO: Reconcile with Border Methods
   * @param width the currently set width of this node
   */
  public boolean setWidth ( double width ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_WIDTH,
                               width );
  
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        alias.setWidth( width );
      }
   
    return true;
  }
 /**
   * TODO: Reconcile with Border Methods
   * @param height the currently set height of this node
   */
  public boolean setHeight ( double height ) {
    view.setNodeDoubleProperty( rootGraphIndex,
                               PGraphView.NODE_HEIGHT,
                               height );
    
   
      for ( Iterator i = children.iterator(); i.hasNext(); ) {
        ShadowAlias alias = ( ShadowAlias )i.next();
        //System.out.println( "Setting the height of shadow alias, :"+alias );
        alias.setHeight( height );
      }
  
    return true;
  }

 public void setNodePosition(boolean animate) {
   if (animate) {
     // animate
     for ( Iterator i = children.iterator(); i.hasNext(); ) {
       ShadowAlias alias = ( ShadowAlias )i.next();
       NodeView opposite = alias.getOppositeNodeView();
       alias.animateToPositionScaleRotation( view.getNodeDoubleProperty( opposite.getRootGraphIndex(),
                                                                         PGraphView.NODE_X_POSITION ) - 30,
                                             view.getNodeDoubleProperty( opposite.getRootGraphIndex(),
                                                                         PGraphView.NODE_Y_POSITION )- 30 ,
                                             1, 0, 2000);
     }
   } else {
     // don't animate
     for ( Iterator i = children.iterator(); i.hasNext(); ) {
       ShadowAlias alias = ( ShadowAlias )i.next();
       NodeView opposite = alias.getOppositeNodeView();
       alias.setOffset( view.getNodeDoubleProperty( opposite.getRootGraphIndex(),
                                                                         PGraphView.NODE_X_POSITION )- 30 ,
                                             view.getNodeDoubleProperty( opposite.getRootGraphIndex(),
                                                                         PGraphView.NODE_Y_POSITION )- 30 );
     }
   }
  }
  

  /**
   * This draws us as selected
   */
  public void select () {
    selected = true;
    Paint selpaint = ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                          PGraphView.NODE_SELECTION_PAINT );
    
    for ( Iterator i = children.iterator(); i.hasNext(); ) {
       ShadowAlias alias = ( ShadowAlias )i.next();
       NodeView opposite = alias.getOppositeNodeView();
       alias.setPaint( selpaint );
     }
    view.nodeSelected( this );
  }

  /**
   * This draws us as unselected
   */
  public void unselect () {
    selected = false;
    Paint paint = ( Paint )view.getNodeObjectProperty( rootGraphIndex,
                                                     PGraphView.NODE_PAINT );
    for ( Iterator i = children.iterator(); i.hasNext(); ) {
      ShadowAlias alias = ( ShadowAlias )i.next();
      NodeView opposite = alias.getOppositeNodeView();
      alias.setPaint( paint );
    }
    view.nodeUnselected( this );
  }


  public void setShape(int shape) {
    view.setNodeIntProperty( rootGraphIndex,
                             PGraphView.NODE_SHAPE,
                             shape );

    for ( Iterator i = children.iterator(); i.hasNext(); ) {
      ShadowAlias alias = ( ShadowAlias )i.next();
      alias.setShape( shape );
    }
  }

}
