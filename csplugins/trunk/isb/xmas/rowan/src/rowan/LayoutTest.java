package rowan;

import giny.model.*;

public class LayoutTest extends AbstractLayout {


  public LayoutTest ( GraphPerspective gp ) {
    super(gp);
  }

  public void layoutPartion ( GraphPerspective net ) {

    int count = net.getNodeCount();
    int sqrt = (int)Math.sqrt(count);

    int[] nodes = net.getNodeIndicesArray();
    for ( int i = 0; i < nodes.length; i++ ) {
      int y = sqrt*(i % sqrt) *10;
      int x = ( i / sqrt) * 100;

      //System.out.println( "i:"+i+"Sqrt: "+sqrt+" %: "+y+" /: "+x );

      layout.setX( nodes[i], (double)x );
      layout.setY( nodes[i], (double)y );
    }
  }

}