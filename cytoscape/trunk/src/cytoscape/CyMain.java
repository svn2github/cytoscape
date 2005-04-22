package cytoscape;

import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import java.util.logging.*;
import javax.swing.Timer;

import com.jgoodies.plaf.FontSizeHints;
import com.jgoodies.plaf.LookUtils;
import com.jgoodies.plaf.Options;
import com.jgoodies.plaf.plastic.Plastic3DLookAndFeel;

//------------------------------------------------------------------------------
/**
 * This is the main startup class for Cytoscape. It creates a CytoscapeConfig
 * object using the command-line arguments, uses the information in that config
 * to create other data objects, and then constructs the first CyWindow.
 * Construction of that class triggers plugin loading, and after that control
 * passes to the UI thread that responds to user input.<P>
 *
 * This class monitors the set of windows that exist and exits the application
 * when the last window is closed.
 */
public class CyMain implements WindowListener {
  protected static Vector windows = new Vector ();
  
  protected CytoscapeVersion version = new CytoscapeVersion();
  protected Logger logger;
   
  protected boolean headless = false;

  protected String[] args;

  /**
   * Primary Method for Starting Cytoscape. Use the passed
   * args to create a CytoscapeConfig object.
   */
  public CyMain ( String [] args ) throws Exception {
    this.args = args;
 
    // create the CytoscapeInit class
    // TODO: store this in Cytoscape.
    CytoscapeInit init = new CytoscapeInit();

    // pass the command line arguments.
    // if false, then we need to stop, since help was requested
    if ( !init.init( args ) ) {
      System.out.println( init.getHelp() );
      System.exit( 0 );
    }
    // continue normally

  } 


  /**
   * on linux (at least) a killed window generates a 'windowClosed' event; trap that here
   */
  public void windowClosing     (WindowEvent e) {windowClosed (e);}

  public void windowDeactivated (WindowEvent e) {}
  public void windowDeiconified (WindowEvent e) {}
  public void windowIconified   (WindowEvent e) {}
  public void windowActivated   (WindowEvent e) {}

  public void windowOpened      (WindowEvent e) {
    windows.add (e.getWindow ());
  }

  public void windowClosed     (WindowEvent e) {
    Window window = e.getWindow();
    if (windows.contains(window)) {windows.remove (window);}

    if (windows.size () == 0) {
      exit(0);
    }
  }

  public static void exit(int exitCode) {
    for (int i=0; i < windows.size (); i++) {
      Window w = (Window) windows.elementAt(i);
      w.dispose();
    }
    System.exit(exitCode);
  }

  public static void main(String args []) throws Exception {

    // first thing is to set up the GUI environment, even though it may not be used...
    UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
    Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
    Options.setDefaultIconSize(new Dimension(18, 18));

    try {
      if ( LookUtils.isWindowsXP() ) {
        // use XP L&F
        UIManager.setLookAndFeel( Options.getSystemLookAndFeelClassName() );
      } else if ( System.getProperty("os.name").startsWith( "Mac" ) ) {
        // do nothing, I like the OS X L&F
      } else {
        // this is for for *nix
        // I happen to like this color combo, there are others
      
        // GTK
        //UIManager.setLookAndFeel( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );


        // jgoodies
        Plastic3DLookAndFeel laf = new Plastic3DLookAndFeel();
        laf.setTabStyle( Plastic3DLookAndFeel.TAB_STYLE_METAL_VALUE );
        laf.setHighContrastFocusColorsEnabled(true);
        laf.setMyCurrentTheme( new com.jgoodies.plaf.plastic.theme.ExperienceBlue() );
        UIManager.setLookAndFeel( laf );
      }
    } catch (Exception e) {
      System.err.println("Can't set look & feel:" + e);
    }

    CyMain app = new CyMain(args);
  } // main

}

