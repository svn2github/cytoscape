package rowan;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.beans.*;

/**
 * This is the non-volatile implementation of a decorator group that paints a background rectangle based
 * on the bounds of its children.
 */
class NodeGroup extends PPath 
  implements PropertyChangeListener {
	int INDENT = 10;

	Rectangle2D cachedChildBounds = new PBounds();
	PBounds comparisonBounds = new PBounds();
	
  ArrayList children = new ArrayList(); 


	public NodeGroup() {
		super();
	}
	

  public void addGroupie ( PNode node ) {
    children.add( node );
    node.addPropertyChangeListener( this );
  }


 //  public void addChild ( PNode node ) {
//     super.addChild( node );
//      node.addPropertyChangeListener( this );
//   }

  public void propertyChange(PropertyChangeEvent evt) {
     computeGroupBounds();
   }

  public void computeGroupBounds () {
    Rectangle2D bounds = new PBounds();
    Iterator i = children.iterator();
    while ( i.hasNext() ) {
      PNode child =  (PNode)i.next();
      Rectangle2D child_bounds = child.getFullBounds();
      bounds.add( child_bounds );
    }
    
    bounds.setRect(bounds.getX()-INDENT,bounds.getY()-INDENT,bounds.getWidth()+2*INDENT,bounds.getHeight()+2*INDENT);
    cachedChildBounds = getParent().globalToLocal( bounds );
    
    setBounds( bounds );
    
    // System.out.println( "Bounds: "+bounds );
   }

	/**
	 * Change the default paint to fill an expanded bounding box based on its children's bounds
	 */
	public void paint(PPaintContext ppc) {
    //computeGroupBounds();
    Paint paint = getPaint();
    Graphics2D g2 = ppc.getGraphics();
    g2.setPaint(paint);
    g2.fill(cachedChildBounds);
    g2.setPaint( getStrokePaint() );
    g2.draw( cachedChildBounds );
    
	}
				
// 	/**
// 	 * Change the full bounds computation to take into account that we are expanding the children's bounds
// 	 * Do this instead of overriding getBoundsReference() since the node is not volatile
// 	 */
// 	public PBounds computeFullBounds(PBounds dstBounds) {
//     PBounds bounds = new PBounds();
//      Iterator i = children.iterator();
//      while ( i.hasNext() ) {
//        bounds.add( ( (PNode)i.next() ).getFullBounds() );
//      }
    
//      bounds.setRect(bounds.getX()-INDENT,bounds.getY()-INDENT,bounds.getWidth()+2*INDENT,bounds.getHeight()+2*INDENT);
//      cachedChildBounds = bounds;
// 		return bounds;		
// 	}
						
// 	/**
// 	 * This is a crucial step.  We have to override this method to invalidate the paint each time the bounds are changed so
// 	 * we repaint the correct region
// 	 */
// 	public boolean validateFullBounds() {
// 		 PBounds bounds = new PBounds();
//      Iterator i = children.iterator();
//      while ( i.hasNext() ) {
//        bounds.add( ( (PNode)i.next() ).getFullBounds() );
//      }
    
//      bounds.setRect(bounds.getX()-INDENT,bounds.getY()-INDENT,bounds.getWidth()+2*INDENT,bounds.getHeight()+2*INDENT);
//      cachedChildBounds = bounds;

//      comparisonBounds =  bounds;
//       //getUnionOfChildrenBounds(comparisonBounds);
	
// 		if (!cachedChildBounds.equals(comparisonBounds)) {
// 			setPaintInvalid(true);
// 		}
// 		return super.validateFullBounds();	
// 	}
}
		
