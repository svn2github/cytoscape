package csplugins.networkedit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.event.InputEvent;
import java.awt.geom.Point2D;

import phoebe.*;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolox.util.*;
import giny.view.NodeView;
import giny.model.*;

import cytoscape.*;
import cytoscape.view.*;

public class NetworkEditEventHandler extends PBasicInputEventHandler {
  
  //the node that will be dropped
  protected NodeView node;
  protected PPath edge;
  protected boolean edgeStarted;

  //the mouse press location for the drop point
  protected Point2D startPoint;
  protected Point2D nextPoint;

  //An ArrayList that holds multiple Point2D's
  protected PCanvas canvas;
  protected PGraphView view;
  PNodeLocator locator;

  protected static int counter = 0;

  /**
   * Creates a new PGraphEditEventHandler object.
   *
   * @param canvas DOCUMENT ME!
   * @param view DOCUMENT ME!
   */
  public NetworkEditEventHandler () {
    locator = new PNodeLocator(new PNode());
    setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
  }

  public void start ( PGraphView view ) {
    System.out.println( "Starting Editing.." );
    this.view = view;
    this.canvas = view.getCanvas();
    canvas.addInputEventListener( this );

  }

  public void stop () {
    if ( canvas != null ) {
      System.out.println( "Stopping Editing.." );
      canvas.removeInputEventListener( this );
      this.view = null;
      this.canvas = null;
    }
  }


  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  public PCanvas getCanvas() {
    return canvas;
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mousePressed(PInputEvent e) {
    super.mousePressed(e);

    if (e.isControlDown()) {
      nextPoint = e.getPosition();

      boolean onNode = false;

      if ( e.getPickedNode() instanceof NodeView ) {
        onNode = true;
        locator.setNode(e.getPickedNode());
        locator.locatePoint(nextPoint);
        nextPoint = e.getPickedNode().localToGlobal(nextPoint);
      }

      if ( onNode && !edgeStarted ) {
        // Begin Edge creation
        edgeStarted = true;
        node = (NodeView) e.getPickedNode();
        edge = new PPath();
        getCanvas().getLayer().addChild(edge);

        edge.setStroke( new PFixedWidthStroke( 3 ) );
        edge.setPaint(Color.black);
        startPoint = nextPoint;
        updateEdge();
      } else if ( onNode && edgeStarted && ( e.getPickedNode() != node) ) {
        // Finish Edge Creation
        edgeStarted = false;
        updateEdge();

        // From the Pick Path
        NodeView target = (NodeView)e.getPickedNode();
        // From Earlier
        NodeView source = node;
              
        Node source_node = source.getNode();
        Node target_node = target.getNode();

        Cytoscape.getCurrentNetwork().restoreEdge( Cytoscape.getCyEdge( source_node, target_node, cytoscape.data.Semantics.INTERACTION, "default" , true ) );
       
        getCanvas().getLayer().removeChild(edge);
        edge = null;
        node = null;
      } else if (!onNode && !edgeStarted) {
      
        // Create a Node on Click
        CyNode cn = Cytoscape.getCyNode( "default"+counter, true );
        counter++;
        Cytoscape.getCurrentNetwork().restoreNode( cn );
        NodeView nv = Cytoscape.getCurrentNetworkView().getNodeView( cn ); 
        //System.out.println( "nv class: "+nv.getClass() );
        nv.setOffset( nextPoint.getX(), nextPoint.getY() );
      }
    }
  }
  //mousePressed

  /**
   * DOCUMENT ME!
   *
   * @param e DOCUMENT ME!
   */
  public void mouseMoved(PInputEvent e) {
    super.mouseMoved(e);

    if (edgeStarted) {
      //we need to update the latest section of the edge
      nextPoint = e.getPosition();
      updateEdge();
    }

    if ( e.getPickedNode() instanceof NodeView ) {
      final PNode node = e.getPickedNode();
      final Color c = ( Color )( ( NodeView ) node).getUnselectedPaint();
      //System.out.println( "Pulsing node: "+node );
      PColorActivity repeatReversePulseActivity = new PColorActivity( 300, 0, 1, PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE, new PColorActivity.Target() {
          public Color getColor() {
            return (Color) node.getPaint();
          }
          public void setColor(Color color) {
            node.setPaint(color);
          }
        }, new Color( ( 255 - c.getRed() ), ( 255 - c.getGreen() ), ( 255 - c.getBlue() ) ) ) {
        
          protected void activityFinished () {
            node.setPaint(c);
          }
            
        };
      node.getRoot().getActivityScheduler().addActivity( repeatReversePulseActivity );
      
      
    }


  }

  /**
   * DOCUMENT ME!
   */
  public void updateEdge() {
    double x1 = startPoint.getX();
    double y1 = startPoint.getY();
    double x2 = nextPoint.getX();
    double y2 = nextPoint.getY();
    double lineLen = Math.sqrt(((x2 - x1) * (x2 - x1)) +
                               ((y2 - y1) * (y2 - y1)));
    double offset = 5;

    if (lineLen == 0)
      lineLen = 1;

    y2 = y2 + (((y1 - y2) / lineLen) * offset);
    x2 = x2 + (((x1 - x2) / lineLen) * offset);

    nextPoint.setLocation(x2, y2);

    edge.setPathToPolyline(new Point2D[] {
      startPoint,
      nextPoint
    });
  }
}
