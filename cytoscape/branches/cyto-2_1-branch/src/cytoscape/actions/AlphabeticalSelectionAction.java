// $Revision$
// $Date$
// $Author$

//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import cytoscape.view.CyNetworkView;
import cytoscape.data.CyNetworkUtilities;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;

import ViolinStrings.Strings;


public class AlphabeticalSelectionAction 
  extends 
    CytoscapeAction  
  implements 
    ActionListener {
   
  JDialog dialog;
  JButton search, cancel;
  JTextField searchField;


  public AlphabeticalSelectionAction () {
    super("By Name...");
    setPreferredMenu( "Select.Nodes" );
    setAcceleratorCombo( java.awt.event.KeyEvent.VK_F, ActionEvent.CTRL_MASK );
  }


  public void actionPerformed (ActionEvent e) {

    if ( e.getSource() == cancel ) {
      dialog.setVisible( false );
      return;
    }

    if ( e.getSource() == searchField || e.getSource() == search ) {
      String search_string = searchField.getText();
      CyNetworkUtilities.selectNodesStartingWith( Cytoscape.getCurrentNetwork(),
                                                  search_string,
                                                  Cytoscape.getCurrentNetworkView() );
      return;
    }

    if ( dialog == null )
      createDialog();
    dialog.setVisible( true );
    
  }

  private JDialog createDialog () {

    dialog = new JDialog( Cytoscape.getDesktop(),
                          "Select Nodes By Name",
                          false );
    
    JPanel main_panel = new JPanel();
    main_panel.setLayout( new BorderLayout() );

    JLabel label = new JLabel(  "<HTML>Select nodes whose <B>name or synonym</B> is like <small>(use \"*\" and \"?\" for wildcards)</small></HTML>");
    main_panel.add( label, BorderLayout.NORTH );


    searchField = new JTextField( 30 );
    searchField.addActionListener( this );
    main_panel.add( searchField, BorderLayout.CENTER );

    JPanel button_panel = new JPanel();
    search = new JButton( "Search" );
    cancel = new JButton( "Cancel" );
    search.addActionListener( this );
    cancel.addActionListener( this );
    button_panel.add( search );
    button_panel.add( cancel );
    main_panel.add( button_panel, BorderLayout.SOUTH );

    dialog.setContentPane( main_panel );
    dialog.pack();
    return dialog;
  }


}

