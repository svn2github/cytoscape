
/*
  File: CreditScreen.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape.util;

import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.awt.geom.*;
import javax.swing.*;
import java.net.URL;
import edu.umd.cs.piccolo.*;
import edu.umd.cs.piccolo.activities.*;
import edu.umd.cs.piccolox.activities.*;
import edu.umd.cs.piccolo.nodes.*;
import edu.umd.cs.piccolox.nodes.*;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolo.util.*;
import cytoscape.util.shadegrown.WindowUtilities;
import edu.umd.cs.piccolox.swing.PScrollPane;


public abstract class CreditScreen  {

  


  public static void moveCredits ( PNode node ) {
    node.offset( 0, -.5 );
    //PBounds bounds = node.getBounds();
    //node.animateToBounds( 0, -.3, bounds.getWidth(), bounds.getHeight(), 10 );
  }

	public static void rotateNode( PNode node ) {
    node.rotateInPlace(Math.toRadians(2));
	}


  public static void showCredits ( URL url, String lines) {
    final JWindow creditsWindow = new JWindow();
    JPanel panel = new JPanel();
    PCanvas canvas = new PCanvas();
    canvas.addMouseListener( 
                            new MouseListener () {
                              public  void 	mouseClicked(MouseEvent e) {
                                creditsWindow.dispose();
                              }
                              
                              public void 	mouseEntered(MouseEvent e) {}
                              
                              public void 	mouseExited(MouseEvent e){}
                              
                              public void 	mousePressed(MouseEvent e){}
                              
                              public void 	mouseReleased(MouseEvent e) {}
                            }
                            );

   
   
    canvas.setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
    PImage image_node = new PImage( url ); 
    PLayer layer = canvas.getLayer();
		PRoot root = canvas.getRoot();
    layer.addChild( image_node );
    //canvas.getCamera().animateViewToCenterBounds(layer.getGlobalFullBounds(), true, 0);
    //canvas.setSize( new Dimension( (int)image_node.getFullBounds().getWidth(), 
    //                               (int)image_node.getFullBounds().getHeight() ) );
    canvas.setMinimumSize(  new Dimension( 354, 426 ) );

//    System.out.println( "Image: "+image_node.getBounds() );


    PClip credit_clip = new PClip();
    credit_clip.setStrokePaint( null );
    credit_clip.setPaint( new java.awt.Color( 1, 1, 1, .1f ) );
    credit_clip.setPathToRectangle( 80, 60, 230, 280 );
    layer.addChild( credit_clip );

    final PNode credits = new PText( lines );
   //  for ( int i = 0; i < lines.length; ++i ) {
//       PText line = new PText( lines[i] );
//       credits.addChild( line );
//       line.offset( 0, i * 15 );
//       System.out.println( "Add credit :" +lines[i] );
//     }
    credit_clip.addChild( credits );
    
    
    

    //layer.addChild( credits );

    PBounds clip_bounds = credit_clip.getBounds();
    PBounds credit_bounds = credits.getBounds();
    

    PNode fade = new PNode();
    fade.offset(  clip_bounds.getX() , clip_bounds.getY() );
    credit_clip.addChild( fade );
    fade.setPaint( new GradientPaint( 155, 60,  new java.awt.Color( 1, 1, 1, 1f ),
                                      155, 90,  new java.awt.Color( 1, 1, 1, 0f ) ) );
                   


//    System.out.println( "cliP :"+clip_bounds );
//    System.out.println( "credit: "+credit_bounds );

    credits.offset( clip_bounds.getX() + 5, clip_bounds.getY() + clip_bounds.getHeight() - 10);
    

    // credits.animateToBounds(  0,//clip_bounds.getX() + 5, 
    //                          clip_bounds.getY() - credit_bounds.getHeight() + 50,
    //                          credit_bounds.getWidth(),
    //                          credit_bounds.getHeight(),
    //                         9000 );

    
    PActivity a1 = new PActivity(-1, 20) {
        public void activityStep(long currentTime) {
          super.activityStep(currentTime);
          moveCredits(credits);
        }
      };
    root.addActivity(a1);
    
    panel.setLayout( new BorderLayout() );
    //PScrollPane scroll = new PScrollPane(canvas);
    panel.add( canvas, BorderLayout.CENTER );

    //WindowUtilities.showSplash( panel, 3000 );

    creditsWindow.getContentPane().add( panel );
    creditsWindow.pack();

    creditsWindow.validate(); 	
		canvas.requestFocus();

    creditsWindow.setSize( 354, 426 );
    WindowUtilities.centerWindowLocation( creditsWindow );
    creditsWindow.setVisible( true );


    

//    System.out.println( "Credits being shown" );

  } // static showCredits( JComponent, int, boolean );


}
