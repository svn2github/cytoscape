//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.graphutil;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.util.*;

import giny.model.*;
import giny.view.*;

import phoebe.util.*;
import phoebe.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;

import java.beans.*;

import java.io.*;

import java.util.*;

import javax.swing.event.*;

public class ArrowNode extends PNodeView {

  PPath inPort;
  PPath outPort;
  
  
  public ArrowNode ( int node_index, PGraphView view ) {
    super( node_index, view );
    
  }

  public PNode getInPort () {
    return inPort;
  }

  public PNode getOutPort () {
    return outPort;
  }


  protected void initializeNodeView () {

   

     setOffset( view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_X_POSITION ),
               view.getNodeDoubleProperty( rootGraphIndex, PGraphView.NODE_Y_POSITION ) );
   
     setStroke( (new BasicStroke( 3 ) ) );

     setUnselectedPaint( null );

     setStrokePaint( java.awt.Color.green );
     //setBounds( 0, 0, 100, 100 );
     //float x = (float)getX();
     //float y = (float)getY();


     //inPort = new ArrowPort( getIndex() );
     //outPort = new ArrowPort( getIndex() );

     inPort = PPath.createEllipse(  -10,40, 10, 10 );
     outPort = PPath.createEllipse( 125, 20, 10, 10 );

     float x = 0;
     float y = 50;

    moveTo( 0, 50 );
     lineTo( 55, 50 );
     lineTo( 55, 30 );
     lineTo( 100, 30 );
     lineTo( 100, 40 );
     lineTo( 125, 25 );
     lineTo( 100, 10 );
     lineTo( 100, 20 );
     lineTo( 45, 20 );
     lineTo( 45, 40 );
     lineTo( 0, 40 );
     closePath();
    

     
   /*  moveTo( 0, 50 );
     lineTo( 125, 50 );
     lineTo( 100, 50 );
     lineTo( 100, 25 );
     lineTo( 125, 25 );
     lineTo( 100, 0 );
     lineTo( 125, 25 );
     lineTo( 100, 50 );
     */
     
     
     addChild( inPort );
     addChild( outPort );


     //label = new PLabel( ( new Integer( getIndex() ) ).toString(), this );
     RootGraph graph = view.getRootGraph();
     Node node = graph.getNode(getIndex());
     String l = node.getIdentifier();
     label = new PLabel ( l, this);
     label.updatePosition();
     label.setPickable(false);
     addChild(label);

     this.visible = true;
     this.selected = false;
     this.notUpdated = false;
     setPickable(true);
     invalidatePaint();

  }

  public boolean setBounds ( java.awt.geom.Rectangle2D newBounds ) {
    
    boolean r = super.setBounds( newBounds );
    inPort.setOffset( 0, getHeight() );
    outPort.setOffset( getWidth(), .5 * getHeight() );
    return r;
  }

  


}

    
