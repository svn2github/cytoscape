package filter.view;

import filter.model.*;
import javax.swing.*;
import javax.swing.tree.*;
//import giny.model.*;
//import giny.view.*;

public class FilterView extends JPanel {

  FilterEditorPanel filterEditorPanel;
  FilterTreeView filterTreeView;
  FilterListPanel filterListPanel;


  public  FilterView () {
    super();
    
    initialize();
  }

  protected void initialize () {
    
   


    filterEditorPanel = new FilterEditorPanel();
    filterListPanel = new FilterListPanel( FilterListPanel.SHOW_TOGETHER);
    filterTreeView = new FilterTreeView( filterListPanel);
    JSplitPane pane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, filterTreeView, filterListPanel );
    JSplitPane pane0 = new JSplitPane( JSplitPane.VERTICAL_SPLIT, filterEditorPanel, pane );
    add( pane0 );

    filterListPanel.getSwingPropertyChangeSupport().addPropertyChangeListener( filterEditorPanel );
    //FilterManager.defaultManager().addEditor( new DefaultFilterEditor() );
    //FilterManager.defaultManager().addEditor( new FilterTreeEditor() );
  }

 //  /**
//    * In order to 
//    */
//   public void addFilterTypeEditor ( FilterTypeEditor new_editor ) {
//     filterTypeEditors.addTab( new_editor.getFilterType(), 
//                               new_editor.getIcon(),
//                               new_editor,
//                               new_editor.getToolTip() );
//   }
    

  public static void main ( String[] args ) {

    if ( System.getProperty("os.name" ).startsWith("Windows") ) {
        try {
          UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (  Exception e ) {
          // TODO: Error handling.
          System.err.println( "Hey. Error loading L&F: on Windows" );
          // TODO: REMOVE
          // e.printStackTrace();
        }
      } else {
        
        
        try { 
          UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch( Exception e ) {
          // TODO: Error handling.
          System.err.println( "Hey. Error loading L&F: on NOT Windows"  );
          // TODO: REMOVE
          //e.printStackTrace();
        }
      }

     JFrame frame = new JFrame( "Filters" );
     frame.getContentPane().add( new FilterView() );
     frame.pack();
     frame.setVisible( true );


  }

 

}
