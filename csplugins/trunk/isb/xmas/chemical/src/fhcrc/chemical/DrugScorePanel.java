package fhcrc.chemical;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.*;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.event.*;
import java.awt.*;
import java.beans.*;
import java.util.List;

import giny.model.*;
import giny.view.*;
import phoebe.*;

import filter.model.*;
import filter.view.*;
import filter.cytoscape.*;

public class DrugScorePanel 
  extends 
    JPanel 
  implements 
    ActionListener,
    ChangeListener,
    ListSelectionListener,
    PropertyChangeListener,
    ItemListener

{

  protected Class NUMBER_CLASS;

  JComboBox nodeAttributeBox;
  ComboBoxModel nodeAttributeModel;
  
  JSlider chemicalScoreSlider;
  JTextField chemicalScoreValue;
  JLabel chemicalLabel;

  
  JSlider drugScoreSlider2;
  JTextField drugScoreValue2;

  JSlider drugScoreSlider;
  JTextField drugScoreValue;

  JList interactionTypeList;
  DefaultListModel interactionTypeModel;

  JButton compute;
  JButton find, reset;
  JRadioButton shade, hide, select;

  public DrugScorePanel () {
    super();
    initialize();
  }

  protected void initialize () {

    Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener( this  );

    // set up node attribute combo box
    try {
      NUMBER_CLASS = Class.forName("java.lang.Number");
    } catch ( Exception e ) {
    }
    nodeAttributeModel = new NodeAttributeComboBoxModel(NUMBER_CLASS);
    nodeAttributeBox = new JComboBox();
    nodeAttributeBox.setModel( nodeAttributeModel );
    nodeAttributeBox.addItemListener( this );
    
    // set up edge types box
    interactionTypeModel = getCurrentInteractionModel();
    interactionTypeList = new JList(); 
    interactionTypeList.addListSelectionListener( this );
    interactionTypeList.setModel( interactionTypeModel );
  
    // set up the drug slider
    drugScoreSlider = new JSlider(JSlider.VERTICAL);
    drugScoreSlider.addChangeListener( this );
    drugScoreValue = new JTextField(3);
    drugScoreValue.addActionListener( this );

    drugScoreSlider.setMajorTickSpacing(5);
    drugScoreSlider.setMinorTickSpacing(1);
    drugScoreSlider.setPaintTicks( true );
    drugScoreSlider.setPaintLabels( true );
    drugScoreSlider.setMinimum( 0 );
    drugScoreSlider.setMaximum( 1 );
    drugScoreSlider.setSnapToTicks( true );

    // set up the drug slider2
    drugScoreSlider2 = new JSlider(JSlider.VERTICAL);
    drugScoreSlider2.addChangeListener( this );
    drugScoreValue2 = new JTextField(3);
    drugScoreValue2.addActionListener( this );

    drugScoreSlider2.setMajorTickSpacing(5);
    drugScoreSlider2.setMinorTickSpacing(1);
    drugScoreSlider2.setPaintTicks( true );
    drugScoreSlider2.setPaintLabels( true );
    drugScoreSlider2.setMinimum( 0 );
    drugScoreSlider2.setMaximum( 1 );
    drugScoreSlider2.setSnapToTicks( true );


    // set up the chemical slider
    chemicalScoreSlider = new JSlider(JSlider.VERTICAL);
    chemicalScoreSlider.addChangeListener( this );
    chemicalScoreValue = new JTextField(3);
    chemicalScoreValue.addActionListener( this );

    chemicalScoreSlider.setMajorTickSpacing(5);
    chemicalScoreSlider.setMinorTickSpacing(1);
    chemicalScoreSlider.setPaintTicks( true );
    chemicalScoreSlider.setPaintLabels( true );
    chemicalScoreSlider.setMinimum( 0 );
    chemicalScoreSlider.setMaximum( 1 );
    chemicalScoreSlider.setSnapToTicks( true );

    chemicalLabel = new JLabel("");

    // set up the compute button
    compute = new JButton("Compute");
    compute.addActionListener( this );
   
    // set up the find button
    find = new JButton( "Find" );
    find.addActionListener( this );

    shade = new JRadioButton( "Shade" );
    hide = new JRadioButton( "Hide" );
    select = new JRadioButton( "Select" );
    ButtonGroup g = new ButtonGroup();
    g.add( shade );
    g.add( hide );
    g.add( select );
    select.setSelected( true );

    JPanel findp = new JPanel();
    findp.setLayout( new BorderLayout() );
    findp.add( find, BorderLayout.EAST );
     
    reset = new JButton( "Reset" );
    reset.addActionListener( this );
    findp.add( reset, BorderLayout.WEST );

    JPanel gp = new JPanel();
    gp.setBorder( new TitledBorder( "Action" ) );
    gp.add( shade );
    gp.add( hide );
    gp.add( select );
    
    findp.add( gp, BorderLayout.CENTER );


    JPanel sliderp = new JPanel();
    sliderp.setLayout( new BorderLayout() );
    sliderp.setBorder( new TitledBorder( "Drug Score Compute" ) );
    
    JPanel ds = new JPanel();
    ds.setLayout( new BorderLayout() );
    ds.add( nodeAttributeBox, BorderLayout.NORTH );
    ds.add( new JLabel( "Drug Score >=" ), BorderLayout.WEST );
    ds.add( drugScoreValue, BorderLayout.CENTER );
    sliderp.add( ds, BorderLayout.NORTH );

    sliderp.add( drugScoreSlider, BorderLayout.CENTER );
    sliderp.add( compute, BorderLayout.SOUTH );

    JPanel edgep = new JPanel();
    edgep.setLayout( new BorderLayout() );
    edgep.setBorder( new TitledBorder( "Included Interaction Types" ) );
    edgep.add( interactionTypeList, BorderLayout.CENTER );

    JPanel chemicalp = new JPanel();
    chemicalp.setLayout( new BorderLayout() );
    chemicalp.setBorder( new TitledBorder( "Chemical Score" ) );

    JPanel cs = new JPanel();
    cs.setLayout( new BorderLayout() );
    cs.add( new JLabel( "Chemical Score >=" ), BorderLayout.WEST );
    cs.add( chemicalScoreValue, BorderLayout.CENTER );
    chemicalp.add( cs, BorderLayout.NORTH );
    chemicalp.add( chemicalScoreSlider, BorderLayout.CENTER );
    

    JPanel d2p = new JPanel();
    d2p.setLayout( new BorderLayout() );
    d2p.setBorder( new TitledBorder( "Drug Score" ) );

    JPanel d2ps = new JPanel();
    d2ps.setLayout( new BorderLayout() );
    d2ps.add( new JLabel( "Drug Score >=" ), BorderLayout.WEST );
    d2ps.add( drugScoreValue2, BorderLayout.CENTER );
    d2p.add( d2ps, BorderLayout.NORTH );
    d2p.add( drugScoreSlider2, BorderLayout.CENTER );
   
    JPanel compp = new JPanel();
    compp.setBorder( new TitledBorder( "Analysis" ) );
    compp.setLayout( new BorderLayout() );
    compp.add( chemicalLabel, BorderLayout.NORTH );
    compp.add( d2p, BorderLayout.WEST );
    compp.add( chemicalp, BorderLayout.EAST );
    compp.add(  findp, BorderLayout.SOUTH );


    setLayout( new BorderLayout() );
    add( sliderp, BorderLayout.CENTER );
    add( edgep, BorderLayout.WEST );
    
    add( compp, BorderLayout.EAST );

  }
   
  protected void find () {
    
    String drug_name = ( String )nodeAttributeBox.getSelectedItem();
    String chemical_name = drug_name+"_score";
    int drug_cutoff = drugScoreSlider2.getValue();
    int chem_cutoff = chemicalScoreSlider.getValue();
    
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView view = Cytoscape.getCurrentNetworkView();
    List nodes = network.nodesList();
    network.unFlagAllNodes();
    List passed_nodes = new ArrayList();

    for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
      CyNode node = ( CyNode )i.next();
      double d,c;
      try {
        d = ( ( Double )Cytoscape.getNodeAttributeValue( node, drug_name ) ).doubleValue();
        c = ( ( Double )Cytoscape.getNodeAttributeValue( node, chemical_name ) ).doubleValue();

        //System.out.println( node.getIdentifier()+" "+drug_name+" "+d+" "+chemical_name+" "+c);

      }  catch ( Exception e ) {
        //continue;
        d = 0;
        Cytoscape.setNodeAttributeValue( node, drug_name, new Double( 0 ) );
        c = 0;
        System.out.println( node.getIdentifier()+" "+drug_name+" "+d+" "+chemical_name+" "+c);
      }
      
      System.out.println( node.getIdentifier()+" "+drug_name+" "+d+" "+chemical_name+" "+c+" chem: "+chem_cutoff+" drug: "+drug_cutoff);

      if ( c >= chem_cutoff && d >= drug_cutoff ) {
        // passes
        
        System.out.println( node.getIdentifier()+" Passes" );
        PNodeView nview = ( PNodeView )view.getNodeView( node );
        nview.setTransparency( 1f );
        view.showGraphObject( nview );
        passed_nodes.add( node );
        
        if ( select.isSelected() ) {
          network.setFlagged( node, true );
        }

      } else {
        // doesn't pass
         System.out.println( node.getIdentifier()+" doesnt Passes" );
        PNodeView nview = ( PNodeView )view.getNodeView( node );
        if ( shade.isSelected() ) {
          nview.setTransparency( .5f );
        }
        
        if ( hide.isSelected() ) {
          view.hideGraphObject( nview );
        }
      }

    }

    Iterator edges = network.edgesIterator();
    while ( edges.hasNext() ) {
      CyEdge edge = ( CyEdge )edges.next();
      PEdgeView ev = ( PEdgeView )view.getEdgeView( edge );
      if ( hide.isSelected() ) {
        view.hideGraphObject( ev );
      }
      if ( shade.isSelected() ) {
        ev.setTransparency( .5f );
      }
    }

    for ( Iterator i = network.getConnectingEdges( passed_nodes ).iterator(); i.hasNext(); ) {
      CyEdge edge = ( CyEdge )i.next();
      PEdgeView ev = ( PEdgeView )view.getEdgeView( edge );
      if ( hide.isSelected() ) {
        view.showGraphObject( ev );
      }
      if ( shade.isSelected() ) {
        ev.setTransparency( 1f );
      }
    }

  }


 
  protected void compute () {

    String drug_name = ( String )nodeAttributeBox.getSelectedItem();
    int cutoff = drugScoreSlider.getValue();
    Object[] types = interactionTypeList.getSelectedValues();

    DrugScore.computeDrugScore( drug_name,
                                cutoff,
                                Arrays.asList( types ) );

    updateChemicalSlider();

  }


  protected void updateChemicalSlider () {

    String drug_name = ( String )nodeAttributeBox.getSelectedItem();
    String chemical_name = drug_name+"_score";
    
    Object[] range = Cytoscape.getNodeNetworkData().getUniqueValues( chemical_name );
      
    if ( range == null )
      return;

    if ( range != null || range.length != 0 ) {
      
      for ( int i = 0; i < range.length; i++ ) {
        System.out.println( i+" Range: "+range[i]+" "+range[i].getClass() );
      }

      Arrays.sort( range );
      chemicalScoreSlider.setMaximum( ( ( Double )range[range.length-1]).intValue() );
      chemicalScoreSlider.setMinimum(  0 );
                                     //( ( Double )range[0]).intValue() );
      
      System.out.println( "Max: "+range[range.length-1]+" Min: "+range[0] );
      chemicalScoreSlider.validate();
      chemicalLabel.setText( chemical_name );
    }
  }


  public void itemStateChanged ( ItemEvent e ) {

    if ( e.getSource () == nodeAttributeBox ) {

      Object[] range = Cytoscape.getNodeNetworkData().getUniqueValues( ( String )nodeAttributeBox.getSelectedItem() );
      Arrays.sort( range );
      drugScoreSlider.setMaximum( ( ( Double )range[range.length-1]).intValue() );
      drugScoreSlider.setMinimum( 0);
                                 //( ( Double )range[0]).intValue() );

      System.out.println( "Max: "+range[range.length-1]+" Min: "+range[0] );
      drugScoreSlider.validate();

      drugScoreSlider2.setMaximum( ( ( Double )range[range.length-1]).intValue() );
      drugScoreSlider2.setMinimum( 0);
      drugScoreSlider2.validate();


      updateChemicalSlider();

    }
    
  }

  public void actionPerformed ( ActionEvent e ) {
    
    if ( e.getSource() == compute ) {
    
      compute();
    }

    if ( e.getSource() == reset ) {
      CyNetwork network = Cytoscape.getCurrentNetwork();
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
      List nodes = network.nodesList();
      network.unFlagAllNodes();
      for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
        CyNode node = ( CyNode )i.next();
        PNodeView nview = ( PNodeView )view.getNodeView( node );
        unshadeNode( nview );
        unhideNode( nview );
      }
    }

    if ( e.getSource() == find ) {
      CyNetwork network = Cytoscape.getCurrentNetwork();
      CyNetworkView view = Cytoscape.getCurrentNetworkView();
      List nodes = network.nodesList();
      network.unFlagAllNodes();
      for ( Iterator i = nodes.iterator(); i.hasNext(); ) {
        CyNode node = ( CyNode )i.next();
        PNodeView nview = ( PNodeView )view.getNodeView( node );
        unshadeNode( nview );
        unhideNode( nview );
      }
      find();
    }
    


    if ( e.getSource() == drugScoreValue ) {
      int i;
      try {
        i = ( new Integer( drugScoreValue.getText() ) ).intValue();
      } catch ( Exception ex ) {
        return;
      }

      if ( i <= drugScoreSlider.getMaximum() && i >= drugScoreSlider.getMinimum() ) {
        drugScoreSlider.setValue( i );
      }
    }

    if ( e.getSource() == drugScoreValue2 ) {
      int i;
      try {
        i = ( new Integer( drugScoreValue2.getText() ) ).intValue();
      } catch ( Exception ex ) {
        return;
      }

      if ( i <= drugScoreSlider2.getMaximum() && i >= drugScoreSlider2.getMinimum() ) {
        drugScoreSlider2.setValue( i );
      }
    }



    if ( e.getSource() == chemicalScoreValue ) {
      int i;
      try {
        i = ( new Integer( chemicalScoreValue.getText() ) ).intValue();
      } catch ( Exception ex ) {
        return;
      }

      if ( i <= chemicalScoreSlider.getMaximum() && i >= chemicalScoreSlider.getMinimum() ) {
        chemicalScoreSlider.setValue( i );
      }
    }


  }

  public void propertyChange ( PropertyChangeEvent e ) {
    int[] si = interactionTypeList.getSelectedIndices();
    interactionTypeModel = getCurrentInteractionModel();
    interactionTypeList.setModel( interactionTypeModel );
    interactionTypeList.setSelectedIndices( si );

  }

  public void stateChanged ( ChangeEvent e ) {
    // update value field
    if ( e.getSource() == drugScoreSlider ) {
    drugScoreValue.setText( ( new Integer( drugScoreSlider.getValue() ) ).toString() );
    } 

    if ( e.getSource() == drugScoreSlider2 ) {
    drugScoreValue2.setText( ( new Integer( drugScoreSlider2.getValue() ) ).toString() );
    } 

    if ( e.getSource() == chemicalScoreSlider ) {
      chemicalScoreValue.setText( ( new Integer( chemicalScoreSlider.getValue() ) ).toString() );
    }
  }

  public void valueChanged ( ListSelectionEvent e ) {

  }


  private DefaultListModel getCurrentInteractionModel () {
    DefaultListModel new_model = new DefaultListModel();
    GraphObjAttributes data = Cytoscape.getEdgeNetworkData();
    Object[] types = data.getUniqueValues(Semantics.INTERACTION);
    if ( types == null ) 
      return new_model;
    System.out.println( types.length+" types" );
    for ( int i = 0; i < types.length; ++i ) {
      System.out.println( "Type: "+types[i] );
      new_model.addElement( types[i] );
    }
    return new_model;
  }

  public static void shadeNode ( PNodeView nv ) {
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView view = Cytoscape.getCurrentNetworkView();
    List edges = network.getAdjacentEdgesList( nv.getNode(), true , true, true );
    for ( Iterator i = edges.iterator(); i.hasNext(); ) {
      PEdgeView ev = ( PEdgeView )view.getEdgeView( ( CyEdge )i.next() );
      ev.setTransparency( .5f );
    }
    nv.setTransparency( .5f );
  }

  public static void unshadeNode ( PNodeView nv ) {
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView view = Cytoscape.getCurrentNetworkView();
    List edges = network.getAdjacentEdgesList( nv.getNode(), true , true, true );
    for ( Iterator i = edges.iterator(); i.hasNext(); ) {
      PEdgeView ev = ( PEdgeView )view.getEdgeView( ( CyEdge )i.next() );
      ev.setTransparency( 1f );
    }
    nv.setTransparency( 1f );
  }

   public static void hideNode ( PNodeView nv ) {
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView view = Cytoscape.getCurrentNetworkView();
    List edges = network.getAdjacentEdgesList( nv.getNode(), true , true, true );
    for ( Iterator i = edges.iterator(); i.hasNext(); ) {
      PEdgeView ev = ( PEdgeView )view.getEdgeView( ( CyEdge )i.next() );
      view.hideGraphObject( ev );
    }
    view.hideGraphObject( nv );
  }

  public static void unhideNode ( PNodeView nv ) {
    CyNetwork network = Cytoscape.getCurrentNetwork();
    CyNetworkView view = Cytoscape.getCurrentNetworkView();
    List edges = network.getAdjacentEdgesList( nv.getNode(), true , true, true );
    for ( Iterator i = edges.iterator(); i.hasNext(); ) {
      PEdgeView ev = ( PEdgeView )view.getEdgeView( ( CyEdge )i.next() );
      view.showGraphObject( ev );
    }
    view.showGraphObject( nv );
  }

  

}
