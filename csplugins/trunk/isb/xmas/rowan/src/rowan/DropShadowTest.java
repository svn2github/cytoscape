package rowan;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;
import cytoscape.plugin.*;
import cytoscape.plugin.jar.*;

import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.beans.*;
import java.io.*;

import giny.view.*;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.nodes.*;

import com.sun.glf.goodies.*;
import com.sun.glf.*;

import javax.swing.*;

import phoebe.*;

public class DropShadowTest {


  public static void createDropShadowNode ( PLayer layer ) {

   

    java.util.List l = new ArrayList( layer.getChildrenReference() );
    Iterator i = l.iterator();
    while ( i.hasNext() ) {
      PPath path = ( PPath )i.next();
      PPath shadow = new PPath( path.getPathReference(), null );
      int offset = 3;
      shadow.setBounds( path.getX() + offset, path.getY() + offset, path.getWidth(), path.getHeight() );
      RadialGradientPaint rgp = new RadialGradientPaint( shadow.getBounds(), 
                                                       new Color( 0, 0, 0, .2f ),
                                                       new Color( 0, 0, 0, .01f ) );
      shadow.setPaint(rgp );

      path.reparent( shadow );
      layer.addChild( shadow );
    
    }

  }
  


}
