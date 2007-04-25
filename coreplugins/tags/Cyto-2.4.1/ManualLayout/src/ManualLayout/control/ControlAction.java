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

  public ControlAction (  ) {
    super( "Align and Distribute" );
  }

  

  public void actionPerformed (ActionEvent e) {
    if ( frame == null ) {
      frame = new JFrame( "Align and Distribute" );

      JPanel panel = new JPanel();
      panel.setLayout( new BorderLayout() );
      panel.setBorder(javax.swing.BorderFactory
                                .createEmptyBorder(5,5,5,5));

      AlignPanel ap = new AlignPanel();
      DistPanel dp = new DistPanel();

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

