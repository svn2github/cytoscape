package integration.view;

import integration.util.*;
import integration.data.*;
import integration.readers.*;
import javax.swing.*;
import java.awt.*;

import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
public class IntegrationWindow extends JPanel {


 
  Integration integration;

  public IntegrationWindow () {
    super();
    
    initialize();

  }
                               

  protected void initialize () {
    
    ReaderMTX mtx = new ReaderMTX();
    DataCube cube =  mtx.createDataCube( this );

    integration = new Integration( cube );
   //  JSplitPane center_lower = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, integration.getIntegrationViewComponent(), integration.getColumnView());
//     JSplitPane center_top = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, integration.getRowView(), center_lower );
//     JSplitPane all = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, integration.getSliceView(), center_top );
//     add( all );
   

   //  setLayout( new FlowLayout() );
//     add( integration.getSliceView());
//     add( integration.getRowView());
//     add( integration.getColumnView());
//     add( integration.getIntegrationViewComponent());
    setLayout( new BorderLayout() );
    
    add( integration.getRowView(), BorderLayout.NORTH );
    add( integration.getColumnView(), BorderLayout.SOUTH );
    add( integration.getSliceView(), BorderLayout.WEST );
    add( integration.getIntegrationViewComponent(), BorderLayout.CENTER );
  }

  public static void main ( String[] args ) {
    
//      if ( System.getProperty("os.name" ).startsWith("Windows") ) {
//       try {
//         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//       } catch (  Exception e ) {
//         // TODO: Error handling.
//         System.err.println( "Hey. Error loading L&F: " + e );
//         // TODO: REMOVE
//         e.printStackTrace();
//       }
//    } else {


//          try { 
//          UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//        } catch( Exception e ) {
//          // TODO: Error handling.
//          System.err.println( "Hey. Error loading L&F: " + e );
//          // TODO: REMOVE
//          e.printStackTrace();
//        }
//    }

    UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
    Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
    Options.setDefaultIconSize(new Dimension(18, 18));
    
    String lafName =
      LookUtils.isWindowsXP()
      ? Options.getCrossPlatformLookAndFeelClassName()
      : Options.getSystemLookAndFeelClassName();
    
    try {
      UIManager.setLookAndFeel(lafName);
    } catch (Exception e) {
      System.err.println("Can't set look & feel:" + e);
    }
    


    IntegrationWindow w = new IntegrationWindow(); 
    JFrame f = new JFrame( "Test" );
    f.getContentPane().add( w );
    f.pack();
    f.setVisible( true );
  }





}
