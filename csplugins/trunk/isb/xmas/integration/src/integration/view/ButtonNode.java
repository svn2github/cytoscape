package integration.view;

import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolo.event.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolo.util.*;
import edu.umd.cs.piccolox.*;
import edu.umd.cs.piccolox.activities.*;
import edu.umd.cs.piccolox.event.*;
import edu.umd.cs.piccolox.handles.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.swing.*;
import edu.umd.cs.piccolox.util.*;

import integration.util.*;
import integration.data.*;

import javax.swing.*;
import java.awt.*;

import java.lang.reflect.Method;

public class ButtonNode extends P3DRect {

  boolean doAction = false;
  boolean pressed = false;

  Method method;
  Object invokeApon;
  Object[] arguments;

  public ButtonNode ( String text, Method m, Object parent, Object[] args ) {
    super( 0, 0, 100, 100 );

    this.method = m;
    this.invokeApon = parent;
    this.arguments = args;

    setRaised( true );
    PText label = new PText( "text" );
    double w = label.getWidth();
    double h = label.getHeight();
    label.setOffset( 10, 5 );
    setBounds( 0, 0, w + 20, h + 10 );
    setPaint( Color.lightGray );
    addChild( label );

    addInputEventListener(new PBasicInputEventHandler() {


        public void mousePressed (PInputEvent event) {
          setRaised( false );
          doAction = true;
          pressed = true;
          setPaint( Color.gray );
        }
        
        public void mouseExited (PInputEvent event) {
          if ( pressed ) {
            doAction = false;
            setRaised( true );
          }
        }

        public void mouseEntered ( PInputEvent event ) {
          if ( pressed ) {
            doAction = true;
            setRaised( false );
          }
        }

        public void mouseReleased ( PInputEvent event ) {
          if ( doAction ) {
            //System.out.println( "Doing action" );
            try {
              method.invoke( invokeApon, arguments );
            } catch ( Exception e ) {
              System.out.println( "Action Didn't happen" );
              e.printStackTrace();
              
            }
            doAction = false;
          }
          pressed = false;
          setRaised( true );
          setPaint( Color.lightGray );
        }
      } );
    
  }
}
