package diff;

// cytoscape import
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.util.*;

// plugin import
import filter.model.*;
import filter.view.*;

// java import
import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;

public class DiffView 
  extends 
    JFrame 
  implements
    ActionListener, 
    PropertyChangeListener {

  // pre-diff variables
  FilterListPanel filterListPanel1, filterListPanel2;
  JComboBox networkBox1, networkBox2;
  JButton filterApply, networkApply;
  Map titleIdMap;
  
  // post-diff variables
  JButton createNetwork, createFilter;
  TableModel diffTableModel;
  JTable diffTable;
  
  JPanel post_panel;

  String diff1, diff2;
 

  public DiffView () {
    super( "DiffViewer" );
    initialize();
  }

  protected void initialize () {
    
    titleIdMap = new HashMap();
    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );

    // pre-diff setup
    JPanel pre_panel = new JPanel();
    JTabbedPane tabs = new JTabbedPane();
    
    JPanel network_diff_panel = new JPanel();
    network_diff_panel.setLayout( new BorderLayout() );
    networkBox1 = getNetworkBox();
    networkBox2 = getNetworkBox();

    JPanel n1 = new JPanel();
    n1.add( new JLabel( "Network 1:" ) );
    n1.add( networkBox1 );
    JPanel n2 = new JPanel();
    n2.add( new JLabel( "Network 2:" ) );
    n2.add( networkBox2 );
    network_diff_panel.add( n1, BorderLayout.WEST );
    network_diff_panel.add( n2, BorderLayout.EAST );
    networkApply = new JButton( "Diff" );
    networkApply.addActionListener( this );
    network_diff_panel.add( networkApply, BorderLayout.SOUTH );
    tabs.addTab( "Networks", network_diff_panel );

    JPanel filter_diff_panel = new JPanel();
    filter_diff_panel.setLayout( new BorderLayout() );
    filterListPanel1 = new FilterListPanel(1);
    filterListPanel2 = new FilterListPanel(1);
    JPanel f1 = new JPanel();
    f1.setLayout( new BorderLayout() );
    f1.add( new JLabel( "Filter 1:" ), BorderLayout.NORTH );
    f1.add( filterListPanel1, BorderLayout.CENTER );
    filter_diff_panel.add( f1, BorderLayout.EAST );
    JPanel f2 = new JPanel();
    f2.setLayout( new BorderLayout() );
    f2.add( new JLabel( "Filter 2:" ), BorderLayout.NORTH );
    f2.add( filterListPanel2, BorderLayout.CENTER );
    filter_diff_panel.add( f1, BorderLayout.WEST);
    filter_diff_panel.add( f2, BorderLayout.EAST);
    filterApply = new JButton( "Diff" );
    filterApply.addActionListener( this );
    filter_diff_panel.add( filterApply, BorderLayout.SOUTH );
    tabs.add( "Filters", filter_diff_panel );

    pre_panel.add( tabs );


    // post-diff setup
    post_panel = new JPanel();
    post_panel.setLayout( new BorderLayout() );

    JPanel button_panel = new JPanel();
    JButton createNetwork = new JButton( "Create Network" );
    createNetwork.addActionListener( this );
    JButton createFilter = new JButton( "Create Filter" );
    createFilter.addActionListener( this );
    button_panel.add( createNetwork );
    button_panel.add( createFilter );
    post_panel.add( button_panel, BorderLayout.SOUTH );

    diffTableModel = new DefaultTableModel( new Object[] { "1", "diff", "2" }, 1);
    diffTable = new JTable( diffTableModel );
    JScrollPane scroll = new JScrollPane( diffTable );
    post_panel.add( scroll, BorderLayout.CENTER );

    JSplitPane split = new JSplitPane( JSplitPane.VERTICAL_SPLIT, pre_panel, post_panel);
    setContentPane( split );
    pack();
  }

   public void actionPerformed ( ActionEvent e ) {

     if ( e.getSource() == filterApply ) {
       filterDiff();
     } else if ( e.getSource() == networkApply ) {
       networkDiff();
     }

   }

  protected void filterDiff () {

    Iterator nodes_i = Cytoscape.getRootGraph().nodesList().iterator();
    Set passes1 = new HashSet();
    Set passes2 = new HashSet();
    CyNode node;
    Filter filter1 = filterListPanel1.getSelectedFilter();
    Filter filter2 = filterListPanel2.getSelectedFilter();

    while ( nodes_i.hasNext() ) {
      node = ( CyNode )nodes_i.next();
      try {
        if ( filter1.passesFilter( node ) ) {
          passes1.add( node );
        }
        if ( filter2.passesFilter( node ) ) {
          passes2.add( node );
        }
      } catch(StackOverflowError soe){
        return ;
      }
    }

    List list1 = new ArrayList( passes1 );
    List list2 = new ArrayList( passes2 );
    List diff = Diff.nodesDiff( list1, list2 );
    updateModel( list1, list2, diff );

  }

  protected void networkDiff () {

    String network_id_1 = ( String )titleIdMap.get( networkBox1.getSelectedItem() );
    String network_id_2 = ( String )titleIdMap.get( networkBox2.getSelectedItem() );

    CyNetwork n1 = Cytoscape.getNetwork( network_id_1 );
    CyNetwork n2 = Cytoscape.getNetwork( network_id_2 );

    List list1 = n1.nodesList();
    List list2 = n2.nodesList();
    List diff = Diff.nodesDiff( list1, list2 );
    updateModel( list1, list2, diff );
    
  }

  protected void updateModel ( List list1, List list2, List diff ) {

    Vector data = new Vector();

   
    Map node_vector = new HashMap();

    Iterator i1 = list1.iterator();
    Iterator i2 = list2.iterator();
    Iterator di = diff.iterator();

    Set shared_set = new HashSet();

    while ( i1.hasNext() ) {
      Vector row;
      CyNode node = ( CyNode )i1.next();
      if ( !node_vector.containsKey( node ) ) {
        row = new Vector(3);
        row.add("");
        row.add("");
        row.add("");
        node_vector.put( node, row );
      } else {
        row = ( Vector )node_vector.get( node );
      }
      row.add( 0, node );
    }

    while ( di.hasNext() ) {
      Vector row;
      CyNode node = ( CyNode )di.next();
      if ( !node_vector.containsKey( node ) ) {
        row = new Vector(3);
        row.add("");
        row.add("");
        row.add("");
        node_vector.put( node, row );
      } else {
        row = ( Vector )node_vector.get( node );
      }

      row.add( 1, node );
    }
    
    while ( i2.hasNext() ) {
      Vector row;
      CyNode node = ( CyNode )i2.next();
      if ( !node_vector.containsKey( node ) ) {
        row = new Vector(3);
        row.add("");
        row.add("");
        row.add("");
        node_vector.put( node, row );
      } else {
        row = ( Vector )node_vector.get( node );
      }
      
      if ( row.get(0) instanceof CyNode ) {
        shared_set.add( node );
      }
      row.add( 2, node );
    }

        
    Set in_set = new HashSet();
    
    i1 = list1.iterator();
    i2 = list2.iterator();
    di = shared_set.iterator();
    
    while ( i1.hasNext() ) {
      CyNode node = ( CyNode )i1.next();
      if ( !in_set.contains( node ) && !shared_set.contains( node ) ) {
        data.add( node_vector.get( node ) );
        in_set.add( node );
      }
    }

    while ( di.hasNext() ) {
      CyNode node = ( CyNode )di.next();
      if ( !in_set.contains( node ) ) {
        data.add( node_vector.get( node ) );
        in_set.add( node );
      }
    }

    while ( i2.hasNext() ) {
      CyNode node = ( CyNode )i2.next();
      if ( !in_set.contains( node ) && !shared_set.contains( node ) ) {
        data.add( node_vector.get( node ) );
        in_set.add( node );
      }
    }

    //data.add( new Vector( list1 ) );
    //data.add( new Vector( diff ) );
    //data.add( new Vector( list2 ) );
    




    Vector col = new Vector( 3 );
    col.add( "1" );
    col.add( "diff" );
    col.add( "2" );

    System.out.println( "list1 size: "+list1.size()+"list2 size: "+list2.size()+"diff size: "+diff.size() );


    ( ( DefaultTableModel )diffTableModel ).setDataVector( data, col );
    //( ( DefaultTableModel )diffTableModel ).setRowCount( data.size() );
    //diffTable.doLayout();
    //diffTable = new JTable( diffTableModel );
    //post_panel.removeAll();
    //JScrollPane scroll = new JScrollPane( diffTable );
    //post_panel.add( scroll, BorderLayout.CENTER );
    //post_panel.add( new JLabel( "Diff Worked, Table Didn't" ) );
    //pack();
    
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
    networkBox1.setModel( model );
    model =new DefaultComboBoxModel( vector );
    networkBox2.setModel( model );
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
