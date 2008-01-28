//import diamondedge.util.*;
//import diamondedge.ado.*;
//import diamondedge.vb.*;
//import java.awt.*;
//import javax.swing.*;
//import diamondedge.swing.*;
//import java.applet.*;
//
//public class Project1 extends JApplet {
//  static {
//    try {
//      UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
//    }
//    catch (Exception e) { System.out.println(e); }
//  }
//  public static Application app = new Application( "Project1" );
//  public static String Title = "";
//  public static String ProductName = "";
//  public static int MajorVersion = 1;
//  public static int MinorVersion = 0;
//  public static int Revision = 0;
//  public static String HelpFile = "";
//  public static String Comments = "";
//  public static String FileDescription = "";
//  public static String CompanyName = "";
//  public static String LegalCopyright = "";
//  public static String LegalTrademarks = "";
//  public static Form1 Form1 = null;
//
//  public Project1() {
//    try {
//      app.addForm( "Form1", Form1 = new Form1() );
//    } catch(Exception e) { Err.set(e); }
//  }
//
//  // called only when running as an applet
//  public void init() {
//    getContentPane().setLayout( new java.awt.BorderLayout() );
//    Application.setApplication( app );
//    if( Form1.getParent() == null )
//    {
//      getContentPane().add( Form1, BorderLayout.CENTER );
//      app.setApplet( this );
//      Form1.init();
//      Form1.Form_Activate();
//    }
//  }
//
//  public String getAppletInfo() {
//    return "Project1" + " " + LegalCopyright;
//  }
//
//  // called only when running as a stand-alone application
//  public static void main( String args[] ) {
//    app.setApplication( new Project1(), args );
//    try {
//      Form1.Show();
//    } catch(Exception e) { Err.set(e); }
//    app.endApplication();
//  }
//}
