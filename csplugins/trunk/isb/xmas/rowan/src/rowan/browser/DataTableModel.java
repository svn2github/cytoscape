package rowan.browser;

import java.util.*;

import giny.model.GraphObject;
import cytoscape.data.CytoscapeData;

import javax.swing.table.DefaultTableModel;
import cytoscape.data.attr.*;

public class DataTableModel 
  extends DefaultTableModel
  implements SortTableModel,
             CyDataListener {

  private CytoscapeData data;
  private List graph_objects;
  private List attributes;
 
  private int graphObjectType = 0;
 
  public DataTableModel () {}

  public DataTableModel ( int rows, int cols ) {
    super(rows, cols);
  }

  public DataTableModel ( Object[][] data, Object[] names ) {
    super(data, names);
  }

  public DataTableModel ( Object[] names, int rows ) {
    super(names, rows);
  }

  public DataTableModel ( Vector names, int rows ) {
    super(names, rows);
  }

  public DataTableModel ( Vector data, Vector names ) {
    super(data, names);
  }

  


  public void setTableData ( CytoscapeData data, 
                             List graph_objects,
                             List attributes ) {
    this.data = data;
    this.graph_objects = graph_objects;
    this.attributes = attributes;
    data.addDataListener( this );
    setTable();
  }

  public void setTableData ( List graph_objects,
                             List attributes ) {

    this.graph_objects = graph_objects;
    this.attributes = attributes;
    setTable();
  }

  public void setTableDataAttributes ( List attributes ) {
    this.attributes = attributes;
    setTable();
  }

  public void setTableDataObjects ( List graph_objects ) {
    this.graph_objects = graph_objects;
    setTable();
  }

  public void setGraphObjectType( int got ) {
    graphObjectType = got;
  }

  protected void setTable () {
    int att_length = attributes.size()+1;
    int go_length = graph_objects.size();

    Object[][] data_vector = new Object[go_length][att_length];  
    Object[] column_names = new Object[att_length];
    
    column_names[0] = "NODENAME";
    for ( int j = 0; j < go_length; ++j ) {
      GraphObject obj = ( GraphObject )graph_objects.get(j);
        
      data_vector[j][0] = obj.getIdentifier();
    }


    for ( int i1 = 0; i1 < attributes.size(); ++i1 ) {
      int i = i1 + 1;
      column_names[i] = attributes.get(i1);
      String attribute = ( String )attributes.get(i1);
      for ( int j = 0; j < go_length; ++j ) {
        GraphObject obj = ( GraphObject )graph_objects.get(j);
        
        Object value = data.getAttributeValue( obj.getIdentifier(),
                                               attribute );
        data_vector[j][i] = value;
      }
    }

    setDataVector( data_vector, column_names );
     
  }

  public String exportTable () {
    return exportTable( "\t", "\n" );
  }

  public String exportTable ( String element_delim, String eol_delim ) {
    
    StringBuffer export = new StringBuffer();

    int att_length = attributes.size()+1;
    int go_length = graph_objects.size();

    Object[][] data_vector = new Object[go_length][att_length];  
    Object[] column_names = new Object[att_length];
    
    column_names[0] = "NODENAME";
    for ( int j = 0; j < go_length; ++j ) {
      GraphObject obj = ( GraphObject )graph_objects.get(j);
        
      data_vector[j][0] = obj.getIdentifier();
    }


    for ( int i1 = 0; i1 < attributes.size(); ++i1 ) {
      int i = i1 + 1;
      column_names[i] = attributes.get(i1);
      String attribute = ( String )attributes.get(i1);
      for ( int j = 0; j < go_length; ++j ) {
        GraphObject obj = ( GraphObject )graph_objects.get(j);
        
        Object value = data.getAttributeValue( obj.getIdentifier(),
                                               attribute );
        data_vector[j][i] = value;
      }
    }

    for ( int i = 0; i < column_names.length; ++i ) {
      export.append( column_names[i]+element_delim );
    }
    export.append(eol_delim );
  
    for ( int i = 0; i < data_vector.length; i++ ) {
      for ( int j = 0; j < data_vector[i].length; ++j ) {
        export.append( data_vector[i][j]+element_delim );
      }
      export.append( eol_delim );
    }
    
    return export.toString();

  }


  public List getGraphObjects() {
    return graph_objects;
  }


  public void attributeValueAssigned(java.lang.String objectKey,
                                     java.lang.String attributeName,
                                     java.lang.Object[] keyIntoValue,
                                     java.lang.Object oldAttributeValue,
                                     java.lang.Object newAttributeValue) {
    //System.out.println( "attributeValueAssigned" );
    //setTable();
  }
  public void attributeValueRemoved(java.lang.String objectKey,
                                    java.lang.String attributeName,
                                    java.lang.Object[] keyIntoValue,
                                    java.lang.Object attributeValue ) {
    //setTable();
  }

  public void allAttributeValuesRemoved(java.lang.String objectKey,
                                        java.lang.String attributeName) {
    //setTable();
  }



  ////////////////////////////////////////
  // Implements JSortTable

  public boolean isSortable ( int col ) {
    return true;
  }

  public void sortColumn ( int col, boolean ascending ) {
    Collections.sort(getDataVector(),
      new ColumnComparator(col, ascending));
  }

  /**
   * Instead of using a listener, just overwrite this method
   * to save time and write to the temp object
   */
  public void setValueAt ( Object aValue,
                           int rowIndex,
                           int columnIndex ) {
    //System.out.println( "SetValueAt  row "+rowIndex+" : "+getValueAt(rowIndex, columnIndex )+" column "+columnIndex+" : "+attributes.get( columnIndex - 1)+ "ID: "+getValueAt( rowIndex, 0 ) );
    
    // TODO: set the edit
    //super.setValueAt( aValue, rowIndex, columnIndex );
    
    DataEditAction edit = new DataEditAction( this, 
                                              (String)getValueAt( rowIndex, 0 ),
                                              (String)attributes.get( columnIndex - 1),
                                              null,
                                              getValueAt(rowIndex, columnIndex ),
                                              aValue,
                                              graphObjectType );
    cytoscape.Cytoscape.getDesktop().addEdit( edit );
    
  }




}
