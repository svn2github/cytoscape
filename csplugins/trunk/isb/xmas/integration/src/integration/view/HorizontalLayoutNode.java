package integration.view;

import java.util.Iterator;
import edu.umd.cs.piccolo.PNode;

public class HorizontalLayoutNode extends PNode {

 

  double dx = 150;

  public HorizontalLayoutNode () {
    super();

  }
  
  public void layoutChildren() {		
		 double xOffset = 0;
     double yOffset = 0;
    Iterator i = getChildrenIterator(); 							
    while (i.hasNext()) {
      PNode each = (PNode) i.next();
      each.setOffset(xOffset - each.getX(), yOffset);
      //xOffset += each.getWidth();
      xOffset += dx;
    }
  }
  

}
