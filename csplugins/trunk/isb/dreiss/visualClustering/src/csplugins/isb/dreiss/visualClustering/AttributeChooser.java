package csplugins.isb.dreiss.visualClustering;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

import java.util.*;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;
import csplugins.isb.dreiss.cytoTalk.CytoTalkHandler;

/**
 * Class <code>AttributeChooser</code>.
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.9962 (Tue Aug 26 01:44:23 PDT 2003)
 */
public class AttributeChooser extends JDialog 
                       implements ActionListener, ListSelectionListener {
   JList listbox;
   JButton applyButton, configButton;
   JTabbedPane tabbedPane;
   String sortedAttributeNames[];
   VisualClustering client;
   String selectedCategories[];
   Map configs = new HashMap();
   CytoTalkHandler handler;

   public AttributeChooser( VisualClustering client, CytoscapeDesktop cWindow, 
			    CytoTalkHandler handler, String attributeNames[] ) {
      super( cWindow.getMainFrame(), false );
      this.client = client;
      this.handler = handler;
      setTitle ("Choose attribute(s) for graph layout");
      sortedAttributeNames = attributeNames;
      Arrays.sort(sortedAttributeNames, String.CASE_INSENSITIVE_ORDER);
  
      tabbedPane = new JTabbedPane();
      tabbedPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

      JPanel mainPanel = new JPanel();
      mainPanel.setLayout( new BorderLayout( 20, 20 ) );
      mainPanel.setBorder(new EmptyBorder(new java.awt.Insets(20, 40, 20, 40)));
      mainPanel.add( new JLabel( "Select multiple attributes by Shift-clicking." ),
		     BorderLayout.NORTH );
  
      listbox = new JList( sortedAttributeNames );
      // listbox.setSelectedIndex( 0 );
      listbox.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
      ListSelectionModel lsm = listbox.getSelectionModel();
      lsm.addListSelectionListener( this );
      listbox.addMouseListener( new MouseAdapter() {
	    public void mouseClicked( MouseEvent e ) {
	       if ( e.getClickCount() == 2 ) AttributeChooser.this.addConfigurationPanes();
	    } } );

      JPanel lPane = new JPanel( new BorderLayout( 20, 20 ) );
      JScrollPane listPane = new JScrollPane( listbox );
      lPane.add( listPane, BorderLayout.CENTER );

      JPanel cbPane = new JPanel( new GridLayout( 3, 2 ) ); //new FlowLayout( FlowLayout.LEFT, 20, 20 ) );
      JCheckBox cb = new JCheckBox( "Re-layout", true );
      cbPane.add( cb );
      configs.put( "re-layout", cb );
      cb = new JCheckBox( "Hide old edges first", true );
      cbPane.add( cb );
      configs.put( "hide others", cb );
      cb = new JCheckBox( "Hide new edges", false );
      cbPane.add( cb );
      configs.put( "hide", cb );
      cb = new JCheckBox( "Restore old edges", false );
      cbPane.add( cb );
      configs.put( "restore", cb );

      cb = new JCheckBox( "Edges between closest only", true );
      cbPane.add( cb );
      configs.put( "closest", cb );
      
      lPane.add( cbPane, BorderLayout.SOUTH );
      mainPanel.add( lPane );

      JPanel buttonPanel = new JPanel();
      configButton = new JButton( "Configure" );
      configButton.addActionListener( this );
      configButton.setEnabled( false );
      buttonPanel.add( configButton );
      mainPanel.add( buttonPanel, BorderLayout.SOUTH );

      buttonPanel = new JPanel();
      applyButton = new JButton( "Apply" );
      applyButton.addActionListener( this );
      applyButton.setEnabled( false );
      buttonPanel.add( applyButton );
      JButton cancelButton= new JButton( "Cancel" );
      cancelButton.addActionListener( this );
      buttonPanel.add( cancelButton, BorderLayout.EAST );
      //mainPanel.add( buttonPanel, BorderLayout.SOUTH );
      getContentPane().add( buttonPanel, BorderLayout.SOUTH );

      //setContentPane( mainPanel );
      tabbedPane.addTab( "Main", mainPanel );
      getContentPane().add( tabbedPane, BorderLayout.CENTER );
   }

   public void actionPerformed (ActionEvent e) {
      String cmd = e.getActionCommand();
      if ( cmd.equals( "Configure" ) ) {
	 addConfigurationPanes();
      } else if ( cmd.equals( "Cancel" ) ) {
	 configs.clear();
	 AttributeChooser.this.dispose();
      } else if ( cmd.equals( "Apply" ) ) {
	 setCursor( Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR ) );
	 if ( selectedCategories != null && selectedCategories.length > 0 )
	    client.doCallback( selectedCategories, this );
	 setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
	 configs.clear();
	 AttributeChooser.this.dispose();
      }
   }

   public boolean doRelayout() {
      return ( (JCheckBox) configs.get( "re-layout" ) ).isSelected(); }
   public boolean hideOthers() {
      return ( (JCheckBox) configs.get( "hide others" ) ).isSelected(); }
   public boolean deleteEdges() {
      return ( (JCheckBox) configs.get( "hide" ) ).isSelected(); }
   public boolean restoreOthers() {
      return ( (JCheckBox) configs.get( "restore" ) ).isSelected(); }
   public Map getConfiguration( String attribute ) {
      return (Map) configs.get( attribute ); }
   public boolean closestOnly() {
      return ( (JCheckBox) configs.get( "closest" ) ).isSelected(); }
   public double getScaling( String attribute ) {
      Map confVec = getConfiguration( attribute );
      if ( confVec == null || confVec.get( "scaling" ) == null ) return 50.0;
      return Double.parseDouble( ( (JLabel) confVec.get( "scaling" ) ).getText().trim() );
   }
   public double getRange( String attribute ) {
      Map confVec = getConfiguration( attribute );
      if ( confVec == null || confVec.get( "range" ) == null ) return 0.0;
      return Double.parseDouble( ( (JLabel) confVec.get( "range" ) ).getText().trim() );
   }
   public boolean getRelative( String attribute ) {
      Map confVec = getConfiguration( attribute );
      if ( confVec == null || confVec.get( "relative" ) == null ) return false;
      return ( (JCheckBox) confVec.get( "relative" ) ).isSelected();
   }
   public boolean getCombineViaAnd( String attribute ) {
      Map confVec = getConfiguration( attribute );
      if ( confVec == null || confVec.get( "andor" ) == null ) return false;
      return ( (JCheckBox) confVec.get( "andor" ) ).isSelected();
   }
   public int getCorrelationType( String attribute ) { // 1 if DOTNORM; 2 if PEARSON; 3 if EQUIVALENCE
      Map confVec = getConfiguration( attribute );
      if ( confVec == null || confVec.get( "corrType" ) == null ) return 1;
      ButtonGroup grp = (ButtonGroup) confVec.get( "corrType" );
      Enumeration e = grp.getElements();
      while( e.hasMoreElements() ) {
	 JRadioButton b = (JRadioButton) e.nextElement();
	 if ( b.isSelected() ) {
	    if ( b.getText().toLowerCase().startsWith( "Dot" ) ) return 1;
	    else if ( b.getText().toLowerCase().startsWith( "Pearson" ) ) return 2;
	    else if ( b.getText().toLowerCase().startsWith( "Equivalence" ) ) return 3;
	 }
      }
      return 1;
   }

   protected void addConfigurationPanes() {
      // Calling nodeAttributes.getClass() returns java.lang.String for booleans 
      //     ("true"/"false") java.lang.Integer for integer types, 
      //     and java.lang.Double for double types

      if ( selectedCategories == null || selectedCategories.length <= 0 ) return;
      for ( int i = tabbedPane.getTabCount() - 1 ; i > 0; i -- ) {
	 String name = tabbedPane.getTitleAt( i );
	 tabbedPane.remove( i );
	 configs.remove( name );
      }
      for ( int i = 0; i < selectedCategories.length; i ++ ) {
	 String attr = selectedCategories[ i ];
	 boolean isExpressionAttr = attr.equals( VisualClustering.MRNA_ATTRIBUTE );
	 boolean isHomologyAttr = ! isExpressionAttr &&
	    attr.equals( VisualClustering.HOMOLOGY_ATTRIBUTE );
	 Map map = new HashMap();
	 configs.put( attr, map );
	 String type = handler.getNodeAttributeClass( attr );
	 Vector attrList = handler.getUniqueNodeAttributeValues( attr );
	 JPanel p = new JPanel( new FlowLayout( FlowLayout.CENTER, 10, 10 ) );
	 p.setBorder( new TitledBorder( new EtchedBorder(), 
					"Node attribute: " + attr ) );

	 JPanel p1 = new JPanel( new BorderLayout( 5, 5 ) );
	 JPanel topP = new JPanel( new BorderLayout() );

	 JCheckBox andor = new JCheckBox( "Add via AND (not OR)" );
	 topP.add( andor, BorderLayout.NORTH );
	 map.put( "andor", andor );

	 JTextArea text = new JTextArea();
	 text.setBackground( (Color) UIManager.getDefaults().get("Button.background"));
	 text.setEditable(false);
	 text.setFont(new java.awt.Font("Dialog", 1, 12));
	 text.setLineWrap(true);
	 text.setText( "Set the relative influence of attribute \"" + attr + 
		       "\" on the clustering:" );
	 text.setWrapStyleWord(true);
	 topP.add( text, BorderLayout.CENTER );
	 p1.add( topP, BorderLayout.NORTH );

	 JPanel sliderp = new JPanel( new FlowLayout( FlowLayout.LEFT, 10, 10 ) );
	 JSlider slider = new JSlider( 0, 100, 50 ); 
	 class MyLabel extends JLabel implements ChangeListener {
	    public void stateChanged( ChangeEvent e ) {
	       setText( "" + 
			(double) ( (JSlider) e.getSource() ).getValue() / 100.0 ); } };
	 MyLabel label = new MyLabel();
	 label.setText( "0.5 " );
	 map.put( "scaling", label );
	 slider.addChangeListener( label );
	 sliderp.add( slider );
	 sliderp.add( label );
	 topP.add( sliderp, BorderLayout.SOUTH );
	 p.add( p1 );

	 //System.err.println("HERE: '"+attr+"' '"+type+"'");

	 if ( ( ! "".equals( type ) && type.equals( "java.lang.Integer" ) ) ||
	      ( ! "".equals( type ) && type.equals( "java.lang.Double" ) ) ||
	      ( type == null && ( isExpressionAttr || isHomologyAttr ) ) ) {
	    JPanel p2 = new JPanel( new BorderLayout( 5, 5 ) );
	    text = new JTextArea();
	    text.setBackground( (Color) UIManager.getDefaults().get("Button.background"));
	    text.setEditable(false);
	    text.setFont(new java.awt.Font("Dialog", 1, 12));
	    text.setLineWrap(true);
	    text.setWrapStyleWord(true);
	    if ( isExpressionAttr )
	       text.setText( "Set the minimum correlation value to use for nodes with " +
			     "equivalent expression levels:" );
	    else if ( isHomologyAttr )
	       text.setText( "Set the minimum Smith-Waterman match score to use for " +
			     "nodes with equivalent expression levels:" );
	    else text.setText( "Set the range within which nodes with attribute \"" + 
			       attr + "\" are judged to be equivalent:" );
	    p2.add( text, BorderLayout.NORTH );

	    sliderp = new JPanel( new FlowLayout( FlowLayout.LEFT, 10, 10 ) );
	    if ( ! "".equals( type ) && type.equals( "java.lang.Integer" ) ) {
	       int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
	       for ( int j = 0; j < attrList.size(); j ++ ) {
		  int val = Integer.parseInt( attrList.get( j ).toString() );
		  if ( val < min ) min = val; else if ( val > max ) max = val;
	       }
	       slider = new JSlider( 0, max-min, 1 ); 
	       label = new MyLabel();
	       label.setText( "0     " );
	    } else { // Double or mrna expression or sequence homology
	       double min = Double.MAX_VALUE, max = Double.MIN_VALUE, deflt = 1.0;
	       if ( ! "".equals( type ) && type.equals( "java.lang.Double" ) ) {
		  for ( int j = 0; j < attrList.size(); j ++ ) {
		     double val = Double.parseDouble( attrList.get( j ).toString() );
		     if ( val < min ) min = val; else if ( val > max ) max = val;
		  }
		  deflt = ( max - min ) / 10.0;
	       } else if ( isExpressionAttr ) {
		  //System.err.println("MRNA EXPRESSION");
		  min = 0.0; max = 1.0; deflt = 0.9;
	       } else if ( isHomologyAttr ) { 
		  //System.err.println("SEQUENCE HOMOLOGY");
		  min = 0.0; max = 2000.0; deflt = 100.0;
	       } 
	       final double nsteps = 10000.0;
	       int range = (int) ( ( max - min ) * nsteps ) + 1;
	       slider = new JSlider( 0, range, (int) ( deflt * nsteps ) ); 
	       class MyDoubleLabel extends MyLabel {
		  public void stateChanged( ChangeEvent e ) {
		     setText( "" + 
		      ( (double) ( (JSlider) e.getSource() ).getValue() ) / nsteps ); } };
	       label = new MyDoubleLabel();
	       label.setText( ( (double) (int) ( deflt * 100.0 ) / 100.0 ) + "  " );
	    } 
	    map.put( "range", label );
	    slider.addChangeListener( label );
	    sliderp.add( slider );
	    sliderp.add( label );
	    p2.add( sliderp, BorderLayout.CENTER );
	    if ( ! isExpressionAttr && ! isHomologyAttr ) {
	       JCheckBox relative = new JCheckBox( "Relative" );
	       sliderp.add( relative );
	       map.put( "relative", relative );
	    } else if ( isExpressionAttr ) {
	       JPanel expP = new JPanel( new GridLayout( 4, 1 ) ); 
	       expP.add( new JLabel( "Select correlation type to use:" ) );
	       ButtonGroup grp = new ButtonGroup();
	       JRadioButton pearsonBut = new JRadioButton( "Pearson correlation", true );
	       grp.add( pearsonBut ); expP.add( pearsonBut );
	       JRadioButton dotBut = new JRadioButton( "Dot product" );
	       grp.add( dotBut ); expP.add( dotBut );
	       JRadioButton equivBut = new JRadioButton( "Equivalence (within tolerance)" );
	       grp.add( equivBut ); expP.add( equivBut );

	       map.put( "corrType", grp );
	       p2.add( expP, BorderLayout.SOUTH );
	    }
	    p.add( p2, BorderLayout.CENTER );
	 } else if ( ! "".equals( type ) ) { // Discrete categories
	    JPanel p2 = new JPanel( new BorderLayout( 5, 5 ) );
	    String attribs[] = (String[]) attrList.toArray( new String[ 0 ] );
	    Arrays.sort( attribs, String.CASE_INSENSITIVE_ORDER );
	    JPanel lpanel = new JPanel( new BorderLayout( 10, 10 ) );
	    lpanel.add( new JLabel( "Available discrete categories:" ),
			BorderLayout.NORTH );
	    JList lbox = new JList( attribs );
	    lbox.setBackground( (Color) UIManager.getDefaults().get("Button.background"));
	    //lbox.setEnabled(false);
	    JScrollPane listPane = new JScrollPane( lbox );
	    lpanel.add( listPane, BorderLayout.CENTER );
	    p2.add( lpanel, BorderLayout.SOUTH );
	    p.add( p2 );
	 }
	 tabbedPane.add( attr, p );
      }
      tabbedPane.setSelectedIndex( 1 );
      tabbedPane.repaint();
   }

   public void valueChanged( ListSelectionEvent e ) {
      if ( e.getValueIsAdjusting() ) return;
      Object objs[] = listbox.getSelectedValues();
      selectedCategories = new String[ objs.length ];
      applyButton.setEnabled( selectedCategories.length > 0 );
      configButton.setEnabled( selectedCategories.length > 0 );
      for ( int i = 0; i < objs.length; i ++ ) 
	 selectedCategories[ i ] = objs[ i ].toString();
   }

   public void doubleClickedOnListBox( int index ) {
      System.err.println("DOUBLE CLIKED ON INDEX " + index );
   }
}
