package rowan;

import filter.model.*;

public class NeighborView 
  extends 
    JFrame
  implements
    ActionListener, 
    PropertyChangeListener {

  // variables

  JComboBox networkBoxFrom, networkBoxTo, neighborCountBox;
  FilterListPanel filterListPanel;
  JButton apply;

  JRadioButton allNodes, selectedNetwork;


  public NeighborView () {
    super( "Add Neighbors" );

  }

  protected void initialize () {

   Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this );
   titleIdMap = new HashMap();

   JPanel main_panel = new JPanel();
   
   JPanel top_panel = new JPanel();
   networkBoxTo = getNetworkBox();
   top_panel.add( new JLabel( "Add Nodes to: "));
   top_panel.add( networkBoxTo );

   JPanel bottom_panel = new JPanel();
   filterListPanel = new FilterListPanel(1);
   bottom_panel.add( filterListPanel );


   JPanel side_panel = new JPanel();
   side_panel.setLayout( new BorderLayout() );

   JPanel c_p = new JPanel();
   Integer[] counts = new Integer[20];
   for ( int i = 0; i < 20; ++i ) {
     counts[i] = new Integer( i + 1 );
   }
   neighborCountBox = new JComboBox(counts);
   c_p.add( new JLabel( "Number of Neighbors" ) );
   c_p.add( neighborCountBox );
   side_panel.add( c_p, BorderLayout.NORTH );

   JPanel f_p = new JPanel();
   f_p.setLayout( new BorderLayout() );

   
   allNodes = new JRadioButton( "All Networks" ); 
   selectedNetwork = new JRadioButton( "Selected Network" );
   ButtonGroup group = new ButtonGroup();
   group.add( allNodes );
   group.add( selectedNetwork );
   






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
      CyNetwork net = Cytoscape.getNetwork( ( String )i.next() );
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    networkBoxFrom.setModel( model );
    model =new DefaultComboBoxModel( vector );
    networkBoxTo.setModel( model );
  }


  protected JComboBox getNetworkBox () {
    Iterator i = Cytoscape.getNetworkSet().iterator();
    Vector vector = new Vector();
    vector.add( "Current Network" );
    while ( i.hasNext() ) {
      CyNetwork net = Cytoscape.getNetwork( ( String )i.next() );
      titleIdMap.put( net.getTitle(), net.getIdentifier() );
      vector.add( net.getTitle() );
    }
    DefaultComboBoxModel model =new DefaultComboBoxModel( vector );
    return new JComboBox( model );
  }


}
