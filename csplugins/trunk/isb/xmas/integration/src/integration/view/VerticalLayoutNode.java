package integration.view;

import java.util.Iterator;
import edu.umd.cs.piccolo.PNode;

public class VerticalLayoutNode extends PNode {

  

  double dy = 150;

  public VerticalLayoutNode () {
    super();

  }
  
  public void layoutChildren() {	
    double xOffset = 0;
    double yOffset = 0;
    Iterator i = getChildrenIterator(); 							
    while (i.hasNext()) {
      PNode each = (PNode) i.next();
      each.setOffset(xOffset, yOffset - each.getY());
      // System.out.println( "Offset is: "+xOffset+ " "+( yOffset - each.getY() )+" for: "+each );
      //yOffset += each.getHeight();
      yOffset += dy;
    }
  }
  

}
