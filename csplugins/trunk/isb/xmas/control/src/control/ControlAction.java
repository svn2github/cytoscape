package control;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import control.view.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ControlAction extends CytoscapeAction {

  JFrame frame;
  protected CyWindow window;

  public ControlAction (  ) {
    super( "Align" );
    this.window = ( CyWindow )Cytoscape.getDesktop();
    setPreferredMenu( "Layout" );
  }

  

  public void actionPerformed (ActionEvent e) {
    if ( frame == null ) {
      frame = new JFrame( "Align and Distribute" );

      JPanel panel = new JPanel();
      panel.setLayout( new BorderLayout() );

      AlignPanel ap = new AlignPanel( window );
      DistPanel dp = new DistPanel( window );

      panel.add( ap, BorderLayout.NORTH );
      panel.add( dp, BorderLayout.SOUTH );

      frame.getContentPane().add( panel );
      frame.pack();
    }
    frame.setVisible( true );
  }

  public boolean isInToolBar () {
    return false;
  }

  public boolean isInMenuBar () {
    return true;
  }

}

