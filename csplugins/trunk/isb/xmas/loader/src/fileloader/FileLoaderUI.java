package fileloader;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.*;

public class FileLoaderUI 
  extends 
    JFrame 
  implements 
    ActionListener {

  JComboBox otherBox, textDelimeter;
  JTextField fileField, fixedField;
  JButton load, browse;
  JRadioButton fixed, delimited, nodes, edges;
  
  JCheckBox tab, comma, semicolon, space, other, merge;


  JTable previewTable;
  TableModel previewModel;

  File file;

  public FileLoaderUI () {
    super( "Import Attribute Spreadsheet" );
    initialize();
  }

  protected void initialize () {

    // everything but the table goes on top
    JPanel top_panel = new JPanel();
    top_panel.setLayout( new BorderLayout() );


    // make the file panel
    JPanel file_panel = new JPanel();
    
    nodes = new JRadioButton( "Nodes" );
    nodes.setSelected( true );
    edges = new JRadioButton( "Edges" );
    ButtonGroup g1 = new ButtonGroup();
    g1.add( nodes );
    g1.add( edges );

    fileField = new JTextField( 20 );
    fileField.addActionListener( this );
    browse = new JButton( "Browse" );
    browse.addActionListener( this );
    file_panel.add( new JLabel( "File:" ) );
    file_panel.add( fileField );
    file_panel.add( browse );
    
    JPanel ne_panel = new JPanel();
    ne_panel.add( nodes );
    ne_panel.add( edges );
    ne_panel.setBorder( new TitledBorder("") );
    file_panel.add( ne_panel );

    top_panel.add( file_panel, BorderLayout.NORTH );
    

    //make the delimeter panel
    JPanel del_panel = new JPanel();
    del_panel.setLayout( new GridLayout( 4,4 ) );

    tab = new JCheckBox( "Tab" );
    tab.addActionListener( this );
    comma = new JCheckBox( "Comma" );
    tab.addActionListener( this );
    semicolon = new JCheckBox( "Semicolon" );
    semicolon.addActionListener( this );
    space = new JCheckBox( "Space" );
    space.addActionListener( this );
    other = new JCheckBox( "Other" );
    other.addActionListener( this );
    otherBox = getOtherComboBox();
    otherBox.addActionListener( this );
    merge = new JCheckBox("Merge Delimiters");
    merge.addActionListener( this );
    textDelimeter = createTextDelimiterBox();
    textDelimeter.addActionListener( this );

    ButtonGroup g2 = new ButtonGroup();
    fixed = new JRadioButton("Fixed Width");
    fixed.setEnabled( false );
    delimited = new JRadioButton( "Delimted" );
    delimited.setSelected(true);
    g2.add( fixed );
    g2.add( delimited );
    del_panel.add( fixed );
    del_panel.add( new JLabel("") );
    del_panel.add( new JLabel("") );
    del_panel.add( delimited );
    
    // row 1
    del_panel.add( tab );
    del_panel.add( comma );
    del_panel.add( other );
    del_panel.add( otherBox );
    
    // row 2
    del_panel.add( semicolon );
    del_panel.add( space );
    del_panel.add( new JLabel("") );
    del_panel.add( new JLabel("") );

    // row3
    del_panel.add( merge );
    del_panel.add( new JLabel("") );
    del_panel.add( new JLabel( "Text Delimiter" ) );
    del_panel.add( textDelimeter );

    top_panel.add( del_panel, BorderLayout.CENTER );

    previewModel = new DefaultTableModel();
    previewTable = new JTable( previewModel );
    JScrollPane scroll = new JScrollPane( previewTable );

    JSplitPane split = new JSplitPane( JSplitPane.VERTICAL_SPLIT, top_panel, scroll );


    setContentPane( split );
    pack();
    setVisible( true );

  }

  public void actionPerformed ( java.awt.event.ActionEvent e ) {

  }

  public JComboBox getOtherComboBox () {
    return new JComboBox();
  }

  public JComboBox createTextDelimiterBox () {
    return new JComboBox();
  }





}
