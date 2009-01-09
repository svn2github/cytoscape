package cytoscape.util.shadegrown;

import java.awt.Window;
import java.awt.Frame;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import javax.swing.JDesktopPane;
import javax.swing.JWindow;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.util.Map;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * WindowUtilities keeps track of open windows and may close down the JVM when
 * all primary windows are closed (see {@link #setExitJVMWhenAllWindowsClose}
 * and {@link #addPrimaryWindow( Window )}).
 */
// TODO: add createDialog stuff...
public abstract class WindowUtilities
  implements WindowConstants {

  protected static JWindow splashWindow = null;
  protected static JComponent splashContent = null;
  // protected static javax.swing.Timer splashTimer = null;

 

  public static void centerWindowOnScreen ( Window window ) {
    centerWindowSize( window );
    centerWindowLocation( window );
    window.setVisible( true );
  } // static centerWindowOnScreen( Window )

  public static void centerWindowSize ( Window window ) {
    Dimension screen_size =
      Toolkit.getDefaultToolkit().getScreenSize();
    GraphicsConfiguration configuration =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Insets screen_insets =
      Toolkit.getDefaultToolkit().getScreenInsets( configuration );

    screen_size.width -= screen_insets.left;
    screen_size.width -= screen_insets.right;
    screen_size.height -= screen_insets.top;
    screen_size.height -= screen_insets.bottom;

    Dimension frame_size = window.getSize();
    frame_size.width = ( int )( screen_size.width * .75 );
    frame_size.height = ( int )( screen_size.height * .75 );
    window.setSize( frame_size );
  } // static centerWindowSize( Window )

 
  public static void centerWindowLocation ( Window window ) {
    Dimension screen_size =
      Toolkit.getDefaultToolkit().getScreenSize();
    GraphicsConfiguration configuration =
      GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    Insets screen_insets =
      Toolkit.getDefaultToolkit().getScreenInsets( configuration );

    screen_size.width -= screen_insets.left;
    screen_size.width -= screen_insets.right;
    screen_size.height -= screen_insets.top;
    screen_size.height -= screen_insets.bottom;

    Dimension frame_size = window.getSize();
    window.setLocation(
      ( ( screen_size.width / 2 ) -
        ( frame_size.width / 2 ) ) + screen_insets.left,
      ( ( screen_size.height / 2 ) -
        ( frame_size.height / 2 ) ) + screen_insets.top
    );
  } // static centerWindowLocation( Window )

  public static void showSplash ( ImageIcon image, int milliseconds ) {
    showSplash( image, milliseconds, true );
  } // static showSplash( ImageIcon, int );

  public static void showSplash (
    ImageIcon image,
    int milliseconds,
    boolean start_timer
  ) {
    showSplash( new JLabel( image ), milliseconds, start_timer );
  } // static showSplash( ImageIcon, int );

  public static void showSplash ( JComponent content, int milliseconds ) {
    showSplash( content, milliseconds, true );
  } // static showSplash( JComponent, int );

  public static void showSplash (
    JComponent content,
    int milliseconds,
    boolean start_timer
  ) {
    hideSplash();
    if( splashWindow == null ) {
      splashWindow = new JWindow();
    }
    splashContent = content;
    splashWindow.getContentPane().add( splashContent );
    splashWindow.pack();
    centerWindowLocation( splashWindow );
    splashWindow.setVisible( true );


    splashContent.addMouseListener( 
                                   new MouseListener () {
                                     public  void 	mouseClicked(MouseEvent e) {
                                       hideSplash();
                                     }
                                     
                                     public void 	mouseEntered(MouseEvent e) {}
                                     
                                     public void 	mouseExited(MouseEvent e){}
                                     
                                     public void 	mousePressed(MouseEvent e){}
                                     
                                     public void 	mouseReleased(MouseEvent e) {}
                                   }
                                   );


//     if( splashTimer == null ) {
//       splashTimer = new javax.swing.Timer (
//         milliseconds,
//         new ActionListener () {
//             public void actionPerformed ( ActionEvent event ) {
//               WindowUtilities.hideSplash();
//             }
//           }
//       );
//       splashTimer.setRepeats( false );
//     } else {
//       splashTimer.setDelay( milliseconds );
//     }

    // if( start_timer ) {
//       splashTimer.start();
//     }


  } // static showSplash( JComponent, int, boolean );

//   public static Timer getSplashTimer () {
//     return splashTimer;
//   } // static getSplashTimer()

  // public static void startSplashTimer () {
//     if( splashTimer == null ) {
//       throw new IllegalStateException( "The splashTimer is null.  Call showSplash(..) first." );
//     }
//     splashTimer.start();
//   } // static startSplashTimer()

  public static void hideSplash () {
   //  if( ( splashTimer != null ) && splashTimer.isRunning() ) {
//       splashTimer.stop();
//     }
    if( ( splashWindow != null ) && splashWindow.isVisible() ) {
      splashWindow.setVisible( false );
      if( splashContent != null ) {
        splashWindow.getContentPane().remove( splashContent );
        splashContent = null;
      }
    }
  } // static hideSplash()

} // class WindowUtilities
