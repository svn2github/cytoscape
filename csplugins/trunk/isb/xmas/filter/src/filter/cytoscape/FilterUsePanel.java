package filter.cytoscape;

import javax.swing.*;
import javax.swing.border.*;
import filter.model.*;
import filter.view.*;
import java.beans.*;
import java.awt.*;
import java.awt.event.*;

public class FilterUsePanel extends JPanel 
  implements PropertyChangeListener,
             ActionListener {

  FilterListPanel filterListPanel;
  JRadioButton hideFailed, grayFailed, selectPassed;
  JButton apply, addFilters, removeFilters;
  JList selectedFilters;
  JRadioButton and, or, xor;

  public FilterUsePanel () {
    super();
  
    //--------------------//
    // Action Panel
    JPanel actionPanel = new JPanel();
    actionPanel.add( new JLabel( "This will most likely have tabs or something on it so that one can choose an action" ) );
    add( actionPanel, BorderLayout.NORTH );


    //--------------------//
    // Selected Filter Panel
    JPanel selected_filter_panel = new JPanel();
    add( selected_filter_panel, BorderLayout.CENTER );

    ButtonGroup logic_group = new ButtonGroup();
    and = new JRadioButton( "AND", true );
    or = new JRadioButton( "OR", false );
    xor = new JRadioButton( "XOR", false );
    JPanel logic_panel = new JPanel();
    logic_panel.setBorder( new TitledBorder( "Filter Combo Type" ) );
    logic_panel.add( and );
    logic_panel.add( or );
    logic_panel.add( xor );
    logic_group.add( and );
    logic_group.add( or );
    logic_group.add( xor );
    selected_filter_panel.add( logic_panel, BorderLayout.NORTH );


    JPanel filter_control_panel = new JPanel();
    addFilters = new JButton( "<-" );
    addFilters.addActionListener( this );
    removeFilters = new JButton( "->" );
    removeFilters.addActionListener( this );
   


  
  }
  
  public void propertyChange ( PropertyChangeEvent e ) {

     if ( e.getPropertyName() == FilterListPanel.FILTER_SELECTED ) {
       // do something on a Filter Selected
     }

  }
  




}
