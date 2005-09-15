package rowan.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import java.io.*;

import cytoscape.Cytoscape;
import cytoscape.data.CytoscapeData;
import cytoscape.util.FileUtil;
import cytoscape.util.CyFileFilter;
import javax.swing.*;
import cytoscape.view.cytopanels.*;

public class DataTable {

  AttributePanel attributePanel;
  ModPanel modPanel;
  DataTableModel tableModel;

  int attributePanelIndex;
  int modPanelIndex;
  int tableIndex;

  
  // Each Attribute Browser operates on one CytoscapeData object, and on either Nodes or Edges.
  CytoscapeData data;

  public static int NODES = 0;
  public static int EDGES = 1;
  public int graphObjectType;

  public DataTable ( CytoscapeData data, int graphObjectType ) {

    this.data = data;
    this.graphObjectType = graphObjectType;

    //setLayout(new BorderLayout());
    //setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
    //setPreferredSize(new Dimension(400, 400));
    

    tableModel = (DataTableModel)makeModel( data );
    attributePanel =  new AttributePanel( data, 
                                          new AttributeModel( data ),
                                          new LabelModel( data ) );
    attributePanel.setTableModel( tableModel );
    

    modPanel = new ModPanel(  data, tableModel, attributePanel, graphObjectType );
    

    //add( attributePanel, BorderLayout.WEST );
    //add( modPanel, BorderLayout.NORTH );
    //add(new JScrollPane(new JSortTable( tableModel ) ), BorderLayout.CENTER );
    //add( new JButton(getFileSaveAction()), BorderLayout.SOUTH );

    String type = "Node";
    if ( graphObjectType != NODES )
      type = "Edge";

    JScrollPane scroll = new JScrollPane(new JSortTable( tableModel ) );

    Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).add( type+"Attributes" ,attributePanel );
    Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).add( type+"Attribute Modifications", modPanel );
    Cytoscape.getDesktop().getCytoPanel( SwingConstants.EAST ).add( type+"Browser",  scroll);
    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu( "Plugins" ).add(  new JMenuItem(getFileSaveAction(type)) );
    

    attributePanelIndex = Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).indexOfComponent( attributePanel );
    modPanelIndex = Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).indexOfComponent( modPanel );
    tableIndex =  Cytoscape.getDesktop().getCytoPanel( SwingConstants.EAST ).indexOfComponent( scroll );

    Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).addCytoPanelListener( new Listener( -1, modPanelIndex, tableIndex, attributePanelIndex ) );
    Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).addCytoPanelListener( new Listener( attributePanelIndex, -1, tableIndex, modPanelIndex ) );
    Cytoscape.getDesktop().getCytoPanel( SwingConstants.EAST ).addCytoPanelListener( new Listener( attributePanelIndex, modPanelIndex, -1, tableIndex ) );

    
    // JFrame frame = new JFrame("DataTable");
//     frame.getContentPane().setLayout(new GridLayout());
//     frame.getContentPane().add(this);
    
//     frame.pack();
//     frame.setSize( 900, 700 );
//     frame.show();

  }

  class Listener implements CytoPanelListener {

    int WEST;
    int SOUTH;
    int EAST;
    int myIndex;
    Listener(  int w,
               int s,
               int e,
               int my ) {
      
      WEST = w;
      SOUTH = s;
      EAST = e;
      myIndex = my;
     
    }
    public void onComponentAdded ( int count ) {}

    public void	onComponentRemoved ( int count ) {}
    
    public void	onComponentSelected ( int componentIndex ) {
    
      if ( componentIndex == myIndex ) {
        if ( WEST != -1 ) 
          Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST ).setSelectedIndex( WEST );

        if ( SOUTH != -1 ) 
          Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).setSelectedIndex( SOUTH );

        if ( EAST != -1 )
          Cytoscape.getDesktop().getCytoPanel( SwingConstants.EAST ).setSelectedIndex( EAST );
      }
      
    }
  
         
  public void	onStateChange ( CytoPanelState newState ) {}
  }


  public AbstractAction getFileSaveAction ( String type ) {
   return new AbstractAction( type+"Export Table" ) {
       public void actionPerformed ( java.awt.event.ActionEvent e ) {
         // Do this in the GUI Event Dispatch thread...
         SwingUtilities.invokeLater( new Runnable() {
             public void run() {
               final String name;
               try {
                 name = FileUtil.getFile( "Export Table",
                                          FileUtil.SAVE,
                                          new CyFileFilter[] {} ).toString();
               } catch ( Exception exp ) {
                 // this is because the selection was canceled
                 return;
               }
               String export = tableModel.exportTable();
               // write to file
               System.out.println( "Write to: "+name+" "+export );
               try {
                 File file = new File( name );
                 BufferedWriter writer = new BufferedWriter(new FileWriter( file ));
                 writer.write( export );
                 writer.close();
               }catch ( Exception ex ) {
                 System.out.println( "Table Export Write error" );
                 ex.printStackTrace();
               }
               
              }
           } ); } };
  }

  public int getGraphObjectType () {
    return graphObjectType;
  }

  public CytoscapeData getData () {
    return data;
  }


  protected SortTableModel makeModel ( CytoscapeData data ) {

    List attributes = Arrays.asList( data.getAttributeNames() );
    List graph_objects = getFlaggedGraphObjects();

    DataTableModel model = new DataTableModel();
    model.setTableData( data,
                        graph_objects,
                        attributes);
    return model;
  }

  private List getFlaggedGraphObjects () {
    if ( graphObjectType == NODES )
      return new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedNodes() );
    else 
      return new ArrayList( Cytoscape.getCurrentNetwork().getFlaggedEdges() );
  }



  public static void main(String[] args)
  {
    JFrame frame = new JFrame("JSortTable Test");
    frame.getContentPane().setLayout(new GridLayout());
    frame.getContentPane().add(new JSortTableTest());
    frame.pack();
    frame.show();
  }
}
