package rowan;

import filter.model.*;

public abstract class AddFirstNeighbors {

  JFrame frame;
  JComboBox networkBoxFrom, networkBoxTo;

  public static void addFirstNeighborsToNetwork ( CyNetwork network ) {

    



  }

  public static void createFrame () {
    frame = new JFrame( "Add First Neighbors" );

   


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
