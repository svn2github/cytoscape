package ManualLayout.control;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.view.*;
import cytoscape.util.*;

import ManualLayout.control.view.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ControlAction extends AbstractAction {

  JFrame frame;
  protected CyNetworkView window;

  public ControlAction (  ) {
    super( "Align and Distribute" );
    this.window = Cytoscape.getCurrentNetworkView();
  }

  

  public void actionPerformed (ActionEvent e) {
    if ( frame == null ) {
      frame = new JFrame( "Align and Distribute" );

      JPanel panel = new JPanel();
      panel.setLayout( new BorderLayout() );
      panel.setBorder(javax.swing.BorderFactory
                                .createEmptyBorder(5,5,5,5));

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

