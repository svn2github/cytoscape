package rowan;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import cytoscape.*;

import filter.model.*;
import filter.view.*;
import filter.cytoscape.*;

public class AddAttributes 
  extends
    JFrame
  implements
    ActionListener {

  protected Class NODE_CLASS;
  protected Class EDGE_CLASS;
  protected Class NUMBER_CLASS;
  protected Class STRING_CLASS;
  protected Class filterClass;

  FilterListPanel filterListPanel;

  JTextField newAttributeField;
  
  JRadioButton allNodes, selectedNetwork;
  JComboBox networkBox;

  JButton apply;
  Map titleIdMap;

  JRadioButton set, append, add;

  JTextField setTrue, setFalse, appendTrue, appendFalse, addTrue, addFalse;

  JComboBox appendNodeAttributeBox;
  JComboBox addNodeAttributeBox;
  JComboBox appendEdgeAttributeBox;
  JComboBox addEdgeAttributeBox;

  ComboBoxModel appendNodeAttributeModel;
  ComboBoxModel appendEdgeAttributeModel;
  ComboBoxModel addNodeAttributeModel;
  ComboBoxModel addEdgeAttributeModel;

  JPanel action2, action3, action4;

  public AddAttributes () {
    super( "Create Attribute" );
    initialize();
  }

  protected void initialize () {

    titleIdMap = new HashMap();
        
    JPanel main_panel = new JPanel();
    main_panel.setLayout( new BorderLayout() );

    // set up the top panel
    JPanel top = new JPanel();
    allNodes = new JRadioButton( "All Networks" ); 
    selectedNetwork = new JRadioButton( "" );
    ButtonGroup net_group = new ButtonGroup();
    net_group.add( allNodes );
    net_group.add( selectedNetwork );
    top.add( allNodes );
    top.add( selectedNetwork );
    allNodes.setSelected( true );
    allNodes.addActionListener( this );
    selectedNetwork.addActionListener( this );

    networkBox = getNetworkBox();
    networkBox.setEnabled( false );
    top.add( networkBox );
    
    main_panel.add( top, BorderLayout.NORTH );

    
    // set up the filter panel
    JPanel filter = new JPanel();
    filter.setLayout( new BorderLayout() );
    filter.setBorder( new TitledBorder( "Filter" ) );
    filter.add( new JLabel( "Operate on all Nodes & Edges\nthat match the selected Filter." ), BorderLayout.NORTH );
    filterListPanel = new FilterListPanel();
    filter.add( filterListPanel, BorderLayout.CENTER );

    main_panel.add( filter, BorderLayout.WEST );


    // set up the action panel
    JPanel action = new JPanel();
    action.setLayout( new GridLayout( 0, 1 ) );

    JPanel action1 = new JPanel();
    action1.setLayout( new BorderLayout() );
    action1.add( new JLabel( "New Attribute Name:" ), BorderLayout.NORTH );
    newAttributeField = new JTextField(10);
    action1.add( newAttributeField, BorderLayout.CENTER );
    action.add( action1 );

    action2 = new JPanel();
    action2.setLayout( new BorderLayout() );
    action2.setBorder( new TitledBorder( "Set Attribute Values To" ) );
    set = new JRadioButton( "Set" );
    action2.add( set, BorderLayout.NORTH );
    
    JPanel action21 = new JPanel();
    action21.add( new JLabel( "True:" ) ); 
    setTrue = new JTextField(4);
    action21.add( setTrue );
    action21.add( new JLabel("False:" )) ;
    setFalse = new JTextField(4);
    action21.add( setFalse );
    action2.add( action21, BorderLayout.CENTER );

    action.add( action2 );
    
    try{
      STRING_CLASS = Class.forName("java.lang.String");
      NUMBER_CLASS = Class.forName("java.lang.Number");
      NODE_CLASS = Class.forName("giny.model.Node");
      EDGE_CLASS = Class.forName("giny.model.Edge");
      filterClass = Class.forName("filter.cytoscape.NumericAttributeFilter");
      appendNodeAttributeModel = new NodeAttributeComboBoxModel(STRING_CLASS);
      appendEdgeAttributeModel = new EdgeAttributeComboBoxModel(STRING_CLASS);
      addNodeAttributeModel = new NodeAttributeComboBoxModel(NUMBER_CLASS);
      addEdgeAttributeModel = new EdgeAttributeComboBoxModel(NUMBER_CLASS);
    }catch(Exception e){
      e.printStackTrace();
    }

    // append panel
    action3 = new JPanel();
    action3.setLayout( new BorderLayout() );
    append = new JRadioButton("Append" );
    action3.add( append, BorderLayout.NORTH );
    action3.setBorder( new TitledBorder( "Append String Value" ) );

    JPanel action31 = new JPanel();
    appendNodeAttributeBox = new JComboBox();
    appendNodeAttributeBox.setEditable( false );
    appendNodeAttributeBox.setModel( appendNodeAttributeModel );
    appendEdgeAttributeBox = new JComboBox();
    appendEdgeAttributeBox.setEditable( false );
    appendEdgeAttributeBox.setModel( appendEdgeAttributeModel );
    action31.add( new JLabel( "Node:" ) );
    action31.add( appendNodeAttributeBox );
    action31.add( new JLabel( "Edge:" ) );
    action31.add( appendEdgeAttributeBox );
    action3.add( action31 );
    
    JPanel action32 = new JPanel();
    action32.add( new JLabel( "True: ") );
    appendTrue = new JTextField(4);
    action32.add( appendTrue );
    action32.add( new JLabel("False:" ));
    appendFalse = new JTextField(4);
    action32.add( appendFalse );
    action3.add( action32, BorderLayout.SOUTH );

    action.add( action3 );


    action4 = new JPanel();
    action4.setLayout( new BorderLayout() );
    add = new JRadioButton("Add" );
    action4.add( add, BorderLayout.NORTH );
    action4.setBorder( new TitledBorder( "Add Numerical Value" ) );

    JPanel action41 = new JPanel();
    addNodeAttributeBox = new JComboBox();
    addNodeAttributeBox.setEditable( false );
    addNodeAttributeBox.setModel( addNodeAttributeModel );
    addEdgeAttributeBox = new JComboBox();
    addEdgeAttributeBox.setEditable( false );
    addEdgeAttributeBox.setModel( addEdgeAttributeModel );
    action41.add( new JLabel( "Node:" ) );
    action41.add( addNodeAttributeBox );
    action41.add( new JLabel( "Edge:" ) );
    action41.add( addEdgeAttributeBox );
    action4.add( action41 );
    
    JPanel action42 = new JPanel();
    action42.add( new JLabel( "True: ") );
    addTrue = new JTextField(4);
    action42.add( addTrue );
    action42.add( new JLabel("False:" ));
    addFalse = new JTextField(4);
    action42.add( addFalse );
    action4.add( action42, BorderLayout.SOUTH );

    action.add( action4 );

    ButtonGroup action_group = new ButtonGroup();
    action_group.add( set );
    action_group.add( append );
    action_group.add( add );

    main_panel.add( action, BorderLayout.CENTER );

    apply = new JButton( "Apply" );
    apply.addActionListener( this );
    main_panel.add( apply, BorderLayout.SOUTH );

    // to make sure everyting gets updated
    Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );

    getContentPane().add( main_panel );
    pack();
    setVisible( true );

  }

  public void actionPerformed ( ActionEvent e ) {

    if ( e.getSource() == selectedNetwork || e.getSource() == allNodes ) {
      if ( allNodes.isSelected() )
        networkBox.setEnabled( false );
      else
        networkBox.setEnabled( true );
    }

    if ( e.getSource() == set ||
         e.getSource() == append ||
         e.getSource() == add ) {
      if ( set.isSelected() ) {
        action2.setEnabled( true );
        action3.setEnabled( false );
        action4.setEnabled( false );
        
        action2.setForeground( Color.gray );
        action3.setForeground( Color.red );
        action4.setForeground( Color.red );

     } else if ( append.isSelected() ) {
        action2.setEnabled( false );
        action3.setEnabled( true );
        action4.setEnabled( false );

        action2.setForeground( Color.red );
        action3.setForeground( Color.gray );
        action4.setForeground( Color.red );

      } else {
        action2.setEnabled( false );
        action3.setEnabled( false );
        action4.setEnabled( true );

        action2.setForeground( Color.red );
        action3.setForeground( Color.red );
        action4.setForeground( Color.gray );
      }
    }

    if ( e.getSource() == apply ) {
      apply();
    }

  }

  protected void apply () {

    // set up the nodes and edges 
    int[] nodes;
    int[] edges;

    if ( allNodes.isSelected() ) {
      nodes = Cytoscape.getRootGraph().getNodeIndicesArray();
      edges = Cytoscape.getRootGraph().getEdgeIndicesArray();
    } else {
      String network_id = ( String )titleIdMap.get( networkBox.getSelectedItem() );
      CyNetwork selected_network  = Cytoscape.getNetwork( network_id );
      nodes = selected_network.getNodeIndicesArray();
      edges = selected_network.getEdgeIndicesArray();
    }


  }
  

   protected JComboBox getNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    vector.add( "Current Network" );
    while ( i.hasNext() ) {
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    return new JComboBox( model );
  }


}
