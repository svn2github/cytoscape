package integration.view;

import integration.data.*;
import integration.util.*;

import edu.umd.cs.piccolox.event.PNotificationCenter;
import edu.umd.cs.piccolox.event.PNotification;

import javax.swing.*;

/**
 * The Integration class will initialize all of the components.  The Componets will all
 * communicate via PNotificationCenter methods, which will simplify things,
 * and lead to a speedy implementation.
 * @author Rowan Christmas
 */
public class Integration {

  static IntegrationView integrationView;
  static ElementView sliceView;
  static ElementView rowView;
  static ElementView columnView;
 

  static final int SLICE = 0;
  static final int ROW = 1;
  static final int COLUMN = 2;


  // this could be a collection of data cubi.
  static DataCube masterDataCube;

  public Integration () {

     initializeCube();
    initialize();
   
  }

  public Integration ( DataCube cube ) {
    masterDataCube = cube;
    initialize();
  }


  public void initializeCube() {


    //----------------------------------------//
    // Make a Sample Data Cube
    //----------------------------------------//
    Object[][][] cube = new Object[][][] {
      { 
        { new Double( 1 ), new Double( 3 ), new Double( 6 ) },
        { new Double( 2 ), new Double( 4 ), new Double( 1 ) },
        { new Double( 3 ), new Double( 4 ), new Double( 2 ) }
      },
      { 
        { new Double( 3 ), new Double( 6 ), new Double( 1 ) },
        { new Double( 4 ), new Double( 1 ), new Double( 2 ) },
        { new Double( 5 ), new Double( 2 ), new Double( 3 ) }
      },
      { 
        { new Double( 6 ), new Double( 1 ), new Double( 3 ) },
        { new Double( 1 ), new Double( 2 ), new Double( 4 ) },
        { new Double( 2 ), new Double( 3 ), new Double( 5 ) }
      }
    };

    String[] slice_names = new String[] { "Gene1", "Gene2", "Gene3" };
    String[] row_name = new String[] { "Experiment1", "Experiment2", "Experiment3" };
    String[] column_names = new String[] { "MT1", "MT2", "MT3" };
    masterDataCube = new DataCube( cube, slice_names, row_name, column_names );
    //----------------------------------------//
    
  }
  public void initialize () {
      
    sliceView = new ElementView( this, "slice_view", SLICE );
    rowView = new ElementView( this, "row_view", ROW );
    columnView = new ElementView( this, "column_view", COLUMN );
    integrationView = new IntegrationView( this );


  }

  public void updateView () {

    
    Object[] slices = sliceView. getSelectionList();
    Object[] rows = rowView.getSelectionList();
    Object[] columns = columnView.getSelectionList();


    System.out.println( "Slices: "+slices.length+" Rows: "+rows.length+" Columns: "+columns.length );

    if ( slices.length == 0 ||
         rows.length == 0 ||
         columns.length == 0 ) {
      System.err.println( "No Selectin Made!!" );
      return;
    }
      
    String[] s = new String[ slices.length ];
    for ( int i = 0; i < slices.length; ++i ) {
      s[i] = ( String )slices[i];
      System.out.println( "SLice: "+s[i] );
    }
    String[] r = new String[ rows.length ];
    for ( int i = 0; i < rows.length; ++i ) {
      r[i] = ( String )rows[i];
      System.out.println( "Row: "+r[i] );
    }


    Object item = new Object[] { s, r, ( String )columns[0] };
    PNotificationCenter.defaultCenter().postNotification( "INTEGRATION_VIEW_UPDATE", item );


  }


  public  DataCube getDataCube () {
    return masterDataCube;
  }
  
  public  ElementView getSliceView () {
    return sliceView;
  }

  public  ElementView getRowView () {
    return rowView;
  }

  public  ElementView getColumnView () {
    return columnView;
  }

  public JComponent getIntegrationViewComponent() {
    return integrationView.getComponent();
  }

}
