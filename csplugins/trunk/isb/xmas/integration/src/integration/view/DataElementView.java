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
import javax.swing.text.*;
import java.awt.*;

import java.lang.reflect.Method;



public class DataElementView extends PPath {

  String text;
  PStyledText styledText;

  public DataElementView ( int value ) {}

  public DataElementView ( double value, String text ) {
    this.text = text;
    setPathToRectangle( 0, 0, 150, 50 );

   //  DefaultStyledDocument pod = new DefaultStyledDocument();
//     try {
//       pod.insertString( 0, ( new Integer( value ) ).toString(), new SimpleAttributeSet() );
//     } catch ( Exception e ) {
//       System.out.println( "Text setting failed" );
//       e.printStackTrace();
//     }

//     styledText = new PStyledText();
//     styledText.setDocument( pod );
    
//     double w = styledText.getWidth();
//     double h = styledText.getHeight();
//     styledText.setOffset( 10, 5 );
//     setBounds( 0, 0, w + 20, h + 10 );
//     setPaint( Color.yellow );
//     addChild( styledText );


    PText label = new PText( text );
    double w = label.getWidth();
    double h = label.getHeight();
    label.setOffset( 10, 5 );
    setBounds( 0, 0, w + 20, h + 10 );
    if ( value == -5 ) {
      setPaint( Color.yellow );
      scale( 2 );
    } else {
    setPaint( ColorInterpolator.colorFromValue( 0, Color.white,  -.2, Color.blue, .2, Color.red, value ) );
    }
    addChild( label );


  }



//   protected void paintAfterChildren ( PPaintContext paintContext ) {

//     double w = styledText.getWidth();
//     double h = styledText.getHeight();
//     styledText.setOffset( 10, 5 );
//     setBounds( 0, 0, w + 20, h + 10 );
//     super.paintAfterChildren( paintContext );


//   }

  



}
