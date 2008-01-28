//import diamondedge.util.*;
//import diamondedge.ado.*;
//import diamondedge.vb.*;
//import java.awt.*;
//import javax.swing.*;
//import diamondedge.swing.*;
//import javax.swing.event.*;
//import java.awt.event.*;
//
//public class Form1 extends JForm
//{
//  public VbLabelEx Label18 = new VbLabelEx();
//  public VbLabelEx Label2 = new VbLabelEx();
//  public VbLabelEx Label3 = new VbLabelEx();
//  public VbLabelEx Label5 = new VbLabelEx();
//  public ListBox List2 = new ListBox();
//  public CommandButton Command10 = new CommandButton();
//  public VbLabelEx Label6 = new VbLabelEx();
//  public CommandButton Command11 = new CommandButton();
//  public CommandButton Command1 = new CommandButton();
//  public VbLabelEx Label7 = new VbLabelEx();
//  public CommandButton Command12 = new CommandButton();
//  public CommandButton Command2 = new CommandButton();
//  public CommandButton Command3 = new CommandButton();
//  public CommandButton Command13 = new CommandButton();
//  public ScrollBar VScroll1 = new ScrollBar(JScrollBar.VERTICAL);
//  public VbImage Image1 = new VbImage();
//  public ScrollBar VScroll2 = new ScrollBar(JScrollBar.VERTICAL);
//  public CommandButton Command14 = new CommandButton();
//  public CommandButton Command7 = new CommandButton();
//  public CommandButton Command8 = new CommandButton();
//  public CommandButton Command9 = new CommandButton();
//  public VbLabelEx Label16 = new VbLabelEx();
//  public VbLabelEx Label1 = new VbLabelEx();
//
//  public Form1() {
//  }
//
//  public void init() {
//    if( initialized )
//      return;
//    initialize(1);
//    try
//    {
//      setLayout( null );
//      setName( "Form1" );
//      setTitle( "Form1" );
//      //unsup LinkTopic = "Form1";
//      setImage( Screen.loadImage(this, "resources/Form1_Picture.png") );
//      setScaleHeight( 10095 );
//      setScaleWidth( 7110 );
//      setStartUpPosition( 3 );
//      setFocusTraversalPolicy( TAB_ORDER_FOCUS_TRAVERSAL_POLICY );
//      setFormLocation( 4, 30 );
//      setSize( 474, 673 );
//      setFormSize( 474, 673 );
//
//      Command7.setName( "Command7" );
//      add( Command7 );
//      Command7.setText( "Select None" );
//      Command7.setTabOrder( 21 );
//      Command7.setLocation( 88, 624 );
//      Command7.setSize( 73, 21 );
//
//      Command8.setName( "Command8" );
//      add( Command8 );
//      Command8.setText( "Select All" );
//      Command8.setTabOrder( 20 );
//      Command8.setLocation( 344, 624 );
//      Command8.setSize( 73, 21 );
//
//      Command14.setName( "Command14" );
//      add( Command14 );
//      Command14.setText( "Pathways" );
//      Command14.setTabOrder( 19 );
//      Command14.setLocation( 296, 368 );
//      Command14.setSize( 145, 21 );
//
//      Command2.setName( "Command2" );
//      add( Command2 );
//      Command2.setText( "Database" );
//      Command2.setTabOrder( 18 );
//      Command2.setLocation( 184, 368 );
//      Command2.setSize( 113, 21 );
//
//      Command1.setName( "Command1" );
//      add( Command1 );
//      Command1.setText( "Title" );
//      Command1.setTabOrder( 17 );
//      Command1.setLocation( 8, 368 );
//      Command1.setSize( 177, 21 );
//
//      VScroll1.setName( "VScroll1" );
//      add( VScroll1 );
//      VScroll1.setTabOrder( 16 );
//      VScroll1.setLocation( 440, 392 );
//      VScroll1.setSize( 17, 225 );
//
//      Command13.setName( "Command13" );
//      add( Command13 );
//      Command13.setText( "Cancel" );
//      Command13.setTabOrder( 15 );
//      Command13.setLocation( 368, 288 );
//      Command13.setSize( 81, 33 );
//
//      Command12.setName( "Command12" );
//      add( Command12 );
//      Command12.setText( "Restart" );
//      Command12.setTabOrder( 14 );
//      Command12.setLocation( 280, 288 );
//      Command12.setSize( 73, 33 );
//
//      Command11.setName( "Command11" );
//      add( Command11 );
//      Command11.setText( "Refresh Progress" );
//      Command11.setTabOrder( 13 );
//      Command11.setLocation( 192, 288 );
//      Command11.setSize( 73, 33 );
//
//      Command10.setName( "Command10" );
//      add( Command10 );
//      Command10.setText( "Pause" );
//      Command10.setTabOrder( 12 );
//      Command10.setLocation( 104, 288 );
//      Command10.setSize( 73, 33 );
//
//      Command9.setName( "Command9" );
//      add( Command9 );
//      Command9.setText( "Cancel" );
//      Command9.setTabOrder( 11 );
//      Command9.setLocation( 16, 288 );
//      Command9.setSize( 73, 33 );
//
//      VScroll2.setName( "VScroll2" );
//      add( VScroll2 );
//      VScroll2.setTabOrder( 10 );
//      VScroll2.setLocation( 432, 136 );
//      VScroll2.setSize( 17, 145 );
//
//      List2.setName( "List2" );
//      List2.removeAllItems();
//      add( List2 );
//      //unsup List2.FontUnderline = false;
//      //unsup List2.FontStrikethrough = false;
//      //unsup List2.Style = 1;
//      List2.setTabOrder( 9 );
//      List2.setLocation( 8, 136 );
//      List2.setSize( 441, 148 );
//      List2.setFont( new Font("SansSerif", Font.BOLD, 11) );
//
//      Command3.setName( "Command3" );
//      add( Command3 );
//      Command3.setText( "Add to Existing Network" );
//      //unsup Command3.FontUnderline = false;
//      //unsup Command3.FontStrikethrough = false;
//      Command3.setTabOrder( 0 );
//      Command3.setLocation( 208, 616 );
//      Command3.setSize( 97, 29 );
//      Command3.setFont( new Font("SansSerif", Font.BOLD, 10) );
//
//      Image1.setName( "Image1" );
//      add( Image1 );
//      Image1.setImage( Screen.loadImage(this, "resources/Form1_Image1_Picture.png") );
//      Image1.setLocation( 8, 392 );
//      Image1.setSize( 429, 222 );
//
//      Label2.setName( "Label2" );
//      add( Label2 );
//      Label2.setText( "Current Node ID:" );
//      Label2.setFontUnderline( DsLabel.NONE );
//      Label2.setFontStrikeThrough( false );
//      //unsup Label2.TabIndex = 7;
//      Label2.setLocation( 16, 40 );
//      Label2.setSize( 145, 25 );
//      Label2.setFont( new Font("SansSerif", Font.BOLD, 14) );
//
//      Label3.setName( "Label3" );
//      add( Label3 );
//      Label3.setText( "mapk" );
//      Label3.setFontUnderline( DsLabel.NONE );
//      Label3.setFontStrikeThrough( false );
//      //unsup Label3.TabIndex = 4;
//      Label3.setLocation( 160, 40 );
//      Label3.setSize( 185, 25 );
//      Label3.setFont( new Font("SansSerif", Font.PLAIN, 14) );
//
//      Label7.setName( "Label7" );
//      add( Label7 );
//      Label7.setText( "IntAct" );
//      Label7.setFontUnderline( DsLabel.NONE );
//      Label7.setFontStrikeThrough( false );
//      //unsup Label7.TabIndex = 5;
//      Label7.setLocation( 160, 64 );
//      Label7.setSize( 185, 25 );
//      Label7.setFont( new Font("SansSerif", Font.PLAIN, 14) );
//
//      Label6.setName( "Label6" );
//      add( Label6 );
//      Label6.setText( "Database Name:" );
//      Label6.setFontUnderline( DsLabel.NONE );
//      Label6.setFontStrikeThrough( false );
//      //unsup Label6.TabIndex = 6;
//      Label6.setLocation( 16, 64 );
//      Label6.setSize( 281, 41 );
//      Label6.setFont( new Font("SansSerif", Font.BOLD, 14) );
//
//      Label16.setName( "Label16" );
//      add( Label16 );
//      Label16.setHorizontalAlignment( SwingConstants.CENTER );
//      Label16.setText( "Nodes Loaded" );
//      Label16.setFontUnderline( DsLabel.NONE );
//      Label16.setFontStrikeThrough( false );
//      //unsup Label16.TabIndex = 2;
//      Label16.setWordWrap( true );
//      Label16.setLocation( 168, 112 );
//      Label16.setSize( 145, 25 );
//      Label16.setFont( new Font("SansSerif", Font.BOLD, 12) );
//
//      Label1.setName( "Label1" );
//      add( Label1 );
//      Label1.setHorizontalAlignment( SwingConstants.CENTER );
//      Label1.setText( "GUI Design #2" );
//      Label1.setFontUnderline( DsLabel.NONE );
//      Label1.setFontStrikeThrough( false );
//      //unsup Label1.TabIndex = 8;
//      Label1.setLocation( 0, 0 );
//      Label1.setSize( 473, 81 );
//      Label1.setFont( new Font("SansSerif", Font.BOLD, 16) );
//
//      Label5.setName( "Label5" );
//      add( Label5 );
//      Label5.setText( "Available Databases" );
//      Label5.setFontUnderline( DsLabel.NONE );
//      Label5.setFontStrikeThrough( false );
//      //unsup Label5.TabIndex = 3;
//      Label5.setLocation( 8, 112 );
//      Label5.setSize( 177, 41 );
//      Label5.setFont( new Font("SansSerif", Font.BOLD, 12) );
//
//      Label18.setName( "Label18" );
//      add( Label18 );
//      Label18.setText( "Results:" );
//      Label18.setFontUnderline( DsLabel.NONE );
//      Label18.setFontStrikeThrough( false );
//      //unsup Label18.TabIndex = 1;
//      Label18.setLocation( 8, 344 );
//      Label18.setSize( 281, 41 );
//      Label18.setFont( new Font("SansSerif", Font.BOLD, 14) );
//    } catch(Exception _e_) { Err.set(_e_,"Form1"); }
//
//    if( !unloaded )
//      enableEvents();
//    super.init();
//  }
//
//  // methods
//
//  public void Form_Load() {
//    try {
//      List2.addItem( "IntAct   14 pathways   *PAUSED* " );
//      List2.addItem( "MINT   223 pathways" );
//      List2.addItem( "HPRD    6 pathways" );
//      List2.addItem( "BioCyc  0 pathways" );
//      List2.addItem( "PUMA2   542 pathways *Complete*" );
//      List2.addItem( "Reactome   0 pathways" );
//      List2.addItem( "KEGG   0" );
//    } catch(Exception _e_) { Err.set(_e_,"Form_Load"); }
//  }
//
//  // events
//
//  // implementation of Listener interfaces
//
//  public void enableEvents() {
//  }
//}
