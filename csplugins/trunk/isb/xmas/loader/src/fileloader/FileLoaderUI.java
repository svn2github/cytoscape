package fileloader;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.io.*;

import cytoscape.*;
import cytoscape.util.*;

public class FileLoaderUI 
  extends 
    JFrame 
  implements 
    ActionListener {

  JTextField otherBox, textDelimeter;
  JTextField fileField, fixedField;
  JButton load, browse;
  JRadioButton fixed, delimited, nodes, edges;
  
  JCheckBox tab, comma, semicolon, space, other, merge;


  JTable previewTable;
  DefaultTableModel previewModel;

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
    load = new JButton( "Load" );
    load.addActionListener( this );
    file_panel.add( load );
    

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
    tab.setSelected( true );
    comma = new JCheckBox( "Comma" );
    comma.addActionListener( this );
    semicolon = new JCheckBox( "Semicolon" );
    semicolon.addActionListener( this );
    space = new JCheckBox( "Space" );
    space.addActionListener( this );
    other = new JCheckBox( "Other" );
    other.addActionListener( this );
    otherBox = new JTextField( 5 );
    otherBox.addActionListener( this );
    merge = new JCheckBox("Merge Delimiters");
    merge.addActionListener( this );
    textDelimeter = new JTextField( 5 );
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

    JPanel table = new JPanel();
    table.setLayout( new BorderLayout () );
    table.add( previewTable.getTableHeader(), BorderLayout.PAGE_START);
    table.add( previewTable, BorderLayout.CENTER );
    

    JScrollPane scroll = new JScrollPane( table );
    scroll.setSize( new Dimension( 300, 100 ) );


    JSplitPane split = new JSplitPane( JSplitPane.VERTICAL_SPLIT, top_panel, scroll );


    setContentPane( split );
    pack();
    setVisible( true );

  }

  public void actionPerformed ( java.awt.event.ActionEvent e ) {

    if ( e.getSource() == load ) {

      StringBuffer delim = new StringBuffer();
      if ( tab.isSelected() )
        delim.append( "\t" );
      else if ( comma.isSelected() )
        delim.append(", " );
      else if ( other.isSelected() )
        delim.append( otherBox.getText() );
      else if ( space.isSelected() )
        delim.append( " " );
      else if ( semicolon.isSelected() )
        delim.append( ";" );
      
      Import.loadFileToNetwork( file.toString(),
                                delim.toString() );
      Cytoscape.firePropertyChange( Cytoscape.ATTRIBUTES_CHANGED, null, null );
      return;
    }
    

    if ( e.getSource() == browse ) {
     
      // get the file name
      final String name;
      try {
        name = FileUtil.getFile( "Load Spreadsheet",
                                 FileUtil.LOAD,
                                 new cytoscape.util.CyFileFilter[] {} ).toString();
      } catch ( Exception exp ) {
        // this is because the selection was canceled
        updatePreview();
        return;
      }
      fileField.setText( name );
      updatePreview();
    } 

   
    if ( e.getSource() == fileField ) {
      try {
        file = new File( fileField.getText() );
      } catch ( Exception ex ) {
        System.out.println( "File Field error" );
        updatePreview();
      }
    }

    updatePreview();

  }

  private void updatePreview () {

    if ( file == null )
      return;

    Vector data = new Vector( 20 );

    StringBuffer delim = new StringBuffer();
    if ( tab.isSelected() )
      delim.append( "\t" );
    else if ( comma.isSelected() )
      delim.append(", " );
    else if ( other.isSelected() )
      delim.append( otherBox.getText() );
    else if ( space.isSelected() )
      delim.append( " " );
    else if ( semicolon.isSelected() )
      delim.append( ";" );

    Vector titles = null;
    int max_col = 0;

    try {
      BufferedReader in
        = new BufferedReader(new FileReader( file ) );
      String oneLine = in.readLine();
      int count = 0;
      while (oneLine != null ) {//&& count++ < 20 ) {
         
        if (oneLine.startsWith("#")) {
          // comment
        } else {
          // read nodes in
          String[] line = oneLine.split( delim.toString() );
          if ( titles == null ) {
            // populate the title vector
            titles = new Vector( line.length );
            for ( int i = 0; i < line.length; ++i ) {
              titles.add( line[i] );
            }
          } else {
            Vector row = new Vector( line.length );
            for ( int i = 0; i < line.length; ++i ) {
              row.add( line[i] );
            }
            data.add( row );
          }
        }        
        oneLine = in.readLine();
      }
      
        in.close();
    } catch ( Exception ex ) {
      System.out.println( "File Read error" );
      ex.printStackTrace();
    }

  //   if ( titles.size() < data.size() ) {
//       for ( int i = titles.size(); i < data.size(); ++i ) {
//         titles.add( file.toString()+" col "+i );
//       }
//     }

    previewModel.setDataVector( data, titles );



  }
  



  public JComboBox getOtherComboBox () {
    return new JComboBox();
  }

  public JComboBox createTextDelimiterBox () {
    return new JComboBox();
  }





}
