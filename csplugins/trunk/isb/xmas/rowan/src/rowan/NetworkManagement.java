package rowan;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

import javax.swing.*;
import javax.swing.border.*;

import ViolinStrings.Strings;

import cytoscape.*;
import filter.view.*;
import filter.model.*;
import java.beans.*;

public class NetworkManagement 
  extends
    JFrame 
  implements 
    ActionListener,
    PropertyChangeListener{

  FilterListPanel filterListPanel;
  JTextArea nodeListArea;
  JComboBox networkBox;
  JTextField newNetworkField;
  JRadioButton append, create, addNodes, removeNodes;
  JTextField fileField;
  JButton browse;
  JButton filterApply, listApply, fileApply;
  Map titleIdMap;

  public NetworkManagement () {
    super( "Network +/-" );
    initialize();
  }

  protected void initialize () {
    
    titleIdMap = new HashMap();
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
    
    JPanel main_panel = new JPanel();
    main_panel.setLayout( new BorderLayout() );


    // set up the top panel
    JPanel top_panel = new JPanel();
   
    JPanel add_to_panel = new JPanel();
    add_to_panel.setLayout( new BorderLayout() );
    append = new JRadioButton( "Append to:" );
    append.setSelected( true );
        
    networkBox = getNetworkBox();
    networkBox.setMaximumSize( new Dimension(  15, (int)networkBox.getPreferredSize().getHeight()) );
    add_to_panel.add( append, BorderLayout.NORTH );
    add_to_panel.add( networkBox, BorderLayout.CENTER );
    add_to_panel.setBorder( new TitledBorder("") );

    JPanel add_remove_panel = new JPanel();
    ButtonGroup add_remove = new ButtonGroup();
    addNodes = new JRadioButton( "Add Nodes" );
    addNodes.setSelected( true );
    removeNodes = new JRadioButton( "RemoveNodes" );
    add_remove.add( addNodes );
    add_remove.add( removeNodes );
    add_remove_panel.add( addNodes );
    add_remove_panel.add( removeNodes );


    JPanel create_new_panel = new JPanel();
    create_new_panel.setLayout( new BorderLayout() );
    create = new JRadioButton( "Create new Network:" );
    newNetworkField = new JTextField( 10 );
    create_new_panel.add( create, BorderLayout.NORTH );
    create_new_panel.add( newNetworkField, BorderLayout.CENTER );
    create_new_panel.setBorder( new TitledBorder("") );

    top_panel.add( add_to_panel );
    top_panel.add( create_new_panel );
    main_panel.add( top_panel, BorderLayout.NORTH );

    ButtonGroup group = new ButtonGroup();
    group.add( append );
    group.add( create );


    JPanel tab_panel = new JPanel();
    tab_panel.setLayout( new BorderLayout() );
    JTabbedPane tabs = new JTabbedPane();
    tab_panel.add( tabs, BorderLayout.CENTER );
    tab_panel.add( add_remove_panel, BorderLayout.SOUTH );

    JPanel filter = new JPanel();
    filter.setLayout( new BorderLayout() );
    filter.setBorder( new TitledBorder( "Filter" ) );
    filter.add( new JLabel( "All Nodes that match the selected Filter." ), BorderLayout.NORTH );
    filterListPanel = new FilterListPanel();
    filter.add( filterListPanel, BorderLayout.CENTER );
    filterApply = new JButton( "Apply" );
    filterApply.addActionListener( this );
    filter.add( filterApply, BorderLayout.SOUTH );
    tabs.addTab("Filter", filter );
    
    JPanel list = new JPanel();
    list.setLayout( new BorderLayout() );
    list.setBorder( new TitledBorder( "List" ) );
    list.add( new JLabel( "All Nodes that are in this list." ), BorderLayout.NORTH );
    nodeListArea = new JTextArea( 7, 15 );
    JScrollPane scroll = new JScrollPane( nodeListArea );
    list.add( scroll, BorderLayout.CENTER );
    listApply = new JButton( "Apply" );
    listApply.addActionListener( this );
    list.add( listApply, BorderLayout.SOUTH );
    tabs.addTab("List", list );

    JPanel file = new JPanel();
    file.setLayout( new BorderLayout() );
    file.setBorder( new TitledBorder( "File" ) );
    file.add( new JLabel( "All Nodes that are in this File." ), BorderLayout.NORTH );
    JPanel browse_panel = new JPanel();
    fileField = new JTextField( 13 );
    browse_panel.add( fileField );
    browse = new JButton( "Browse" );
    browse.addActionListener( this );
    browse_panel.add( browse );
    file.add( browse_panel, BorderLayout.CENTER );
    fileApply = new JButton( "Apply" );
    fileApply.addActionListener( this );
    file.add( fileApply, BorderLayout.SOUTH );
    tabs.addTab("File", file );

    main_panel.add( tab_panel, BorderLayout.SOUTH );


    setContentPane( main_panel );
    pack();
    
  }

  public void actionPerformed ( ActionEvent e ) {

   

    if ( e.getSource() == filterApply ) 
       getMatchingNodes( "filter" );
    else if ( e.getSource() == listApply )
       getMatchingNodes( "list" );
    else if ( e.getSource() == fileApply ) 
       getMatchingNodes( "file" );
    else if ( e.getSource() == browse ) {

      File currentDirectory = Cytoscape.getCytoscapeObj().getCurrentDirectory();
      JFileChooser fChooser = new JFileChooser(currentDirectory);     
      fChooser.setDialogTitle("Load Node File");
      switch (fChooser.showOpenDialog( Cytoscape.getDesktop() ) ) {
                
      case JFileChooser.APPROVE_OPTION:
        fileField.setText( fChooser.getSelectedFile().getAbsolutePath() );
        currentDirectory = fChooser.getCurrentDirectory();
        Cytoscape.getCytoscapeObj().setCurrentDirectory(currentDirectory);
        break;

    }
    

    }
  }
    

  protected void getMatchingNodes ( String function ) {
    Iterator edges_i = Cytoscape.getRootGraph().edgesList().iterator();
    Iterator nodes_i = Cytoscape.getRootGraph().nodesList().iterator();
    Set nodes = new HashSet();
    Set edges = new HashSet();
    CyNode node;
    CyEdge edge;
    if ( function.equals("filter") && filterListPanel.getSelectedFilter() != null ) {
      Filter filter = filterListPanel.getSelectedFilter();
      while ( edges_i.hasNext() ) {
        edge = ( CyEdge )edges_i.next();
        try {
          if ( filter.passesFilter(edge) ) {
            edges.add( edge );
          }
        } catch(StackOverflowError soe){
          return ;
        }
      }

      while ( nodes_i.hasNext() ) {
        node = ( CyNode )nodes_i.next();
        try {
          if ( filter.passesFilter(node) ) {
            nodes.add( node );
          }
        } catch(StackOverflowError soe){
          return ;
        }
      }
    }

    else if ( function.equals("list") ) {
      String node_list = nodeListArea.getText();
      String[] list_array = node_list.split( "[ \n\t\r]");
      for ( int i = 0; i < list_array.length; ++i ) {
        node = Cytoscape.getCyNode( list_array[i], false );
        if ( node != null ) {
          nodes.add( node );
        }
      }
    }

    else if ( function.equals("file") ) {
      File file = new File( fileField.getText() );
      try {
        FileReader fin = new FileReader(file);
        BufferedReader bin = new BufferedReader(fin);
        String s;
        while ( ( s = bin.readLine() ) != null) {
          String trimName = s.trim();
          node = Cytoscape.getCyNode( s, false );
          if ( node != null ) {
            nodes.add( node );
          }
        }
        fin.close();
      } catch ( Exception e ) {}
    }


      
    
    //TODO: Remove
    CyNode[] node_array = ( CyNode[] )nodes.toArray( new CyNode[] {} );

    for ( int i = 0; i < node_array.length; ++i ) {
      System.out.println( node_array[i].getIdentifier() );
    } 

    operateOnNetwork( new ArrayList( nodes ), true, new ArrayList( edges )  );

  }

  protected void operateOnNetwork ( List nodes, boolean restore_incident_edges, List edges )  {

    if ( append.isSelected() && addNodes.isSelected() ) {
      String network_id = ( String )titleIdMap.get( networkBox.getSelectedItem() );
      if ( network_id == null && networkBox.getSelectedItem().equals( "Current Network" ) ) {
        Cytoscape.getCurrentNetwork().restoreNodes( nodes, restore_incident_edges );
        Cytoscape.getCurrentNetwork().restoreEdges( edges );
      }
      Cytoscape.getNetwork( network_id ).restoreNodes( nodes, restore_incident_edges );
      Cytoscape.getNetwork( network_id ).restoreEdges( edges );
    } else if ( append.isSelected() && removeNodes.isSelected() ) {
      String network_id = ( String )titleIdMap.get( networkBox.getSelectedItem() );
      if ( network_id == null && networkBox.getSelectedItem().equals( "Current Network" ) ) {
        Cytoscape.getCurrentNetwork().hideNodes( nodes );
        Cytoscape.getCurrentNetwork().hideEdges( edges );
      }
      Cytoscape.getNetwork( network_id ).hideNodes( nodes );
      Cytoscape.getNetwork( network_id ).hideEdges( edges );
    } else {
      // create
      String network_title = newNetworkField.getText();
      CyNetwork network = Cytoscape.createNetwork( network_title );
      network.restoreNodes( nodes, restore_incident_edges );
      network.restoreEdges( edges );
    }
    
  }



  public void propertyChange ( PropertyChangeEvent e ) {

    if ( e.getPropertyName().equals( Cytoscape.NETWORK_CREATED ) ||  e.getPropertyName().equals( Cytoscape.NETWORK_DESTROYED ) ) {
      updateNetworkBox();
    }
  }
  
  protected void updateNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    vector.add( "Current Network" );
    while ( i.hasNext() ) {
      //System.out.println( i.next().getClass() );
      CyNetwork net = ( CyNetwork )i.next();
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    networkBox.setModel( model );
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
