package csplugins.isb.dreiss.cytoTalk;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.net.InetAddress;

import cytoscape.*;
import cytoscape.view.*;
import cytoscape.plugin.*;

import csplugins.isb.dreiss.httpdata.xmlrpc.*;
import csplugins.isb.dreiss.util.*;

/**
 * Class <code>CytoTalkPlugin</code> 
 *
 * @author <a href="mailto:dreiss@systemsbiology.org">David Reiss</a>
 * @version 1.0
 */
public class CytoTalkPlugin extends CytoscapePlugin {
   public static final String service = "cy";
   static final int DEFAULT_PORT = 8082;
   public static int STATIC_PORT = DEFAULT_PORT;
   public static boolean alreadyStartedCytoTalkAutomatically = false;

   MyXmlRpcServer server = null;
   CytoscapeDesktop cWindow;
   int localPort = STATIC_PORT;
   String lastChoice = "test.pl", lastDir = System.getProperty( "user.dir" );
   String clientFilter = null;
   boolean addLocalHost = true;

   static {
      Properties properties = MyUtils.readProperties( "csplugins/isb/dreiss/cytoTalk.properties" );
      try { STATIC_PORT = Integer.parseInt( (String) properties.get( "cytoTalk.port" ) ); }
      catch ( Exception ee ) { ee.printStackTrace(); STATIC_PORT = DEFAULT_PORT; }
   }

   public CytoTalkPlugin() throws Exception {
      this.cWindow = Cytoscape.getDesktop();
      JMenu menu = cWindow.getCyMenus().getOperationsMenu();
      JMenu menu2 = new JMenu( "CytoTalk..." );
      menu.add( menu2 );
      menu2.add( new StartCytoTalk() );
      menu2.add( new StopCytoTalk() );
      menu2.add( new SetCytoTalkPort() );
      menu2.add( new SetCytoTalkClientFilter() );
      menu2.add( new RunPerlScript() );
      menu2.add( new AboutCytoTalkPlugin() );

      Runtime.getRuntime().addShutdownHook( new Thread() { public void run() {
	 try { CytoTalkPlugin.this.shutdown(); } catch ( Exception e) { }; } } );

      cWindow.getMainFrame().addWindowListener( new WindowAdapter() {
	    public void windowClosing( WindowEvent we ) {
	       try { CytoTalkPlugin.this.shutdown(); } catch ( Exception e ) { };
	    } } );

      if ( ! alreadyStartedCytoTalkAutomatically ) {
	 String args[] = Cytoscape.getCytoscapeObj().getConfiguration().getArgs();
	 for ( int i = 0; i < args.length; i ++ ) {
	    if ( args[ i ].equals( "--startCytoTalk" ) ) {
	       alreadyStartedCytoTalkAutomatically = true;
	       if ( i < args.length - 1 ) {
		  int port = CytoTalkHandler.isIntParseable( args[ i + 1 ] );
		  if ( port == Integer.MIN_VALUE ) port = STATIC_PORT; 
		  startCytoTalk( false, port );
	       } else {
		  startCytoTalk( false, STATIC_PORT );
	       }
	       break;
	    }
	 }
      }
   }

   public void finalize() {
      try { shutdown(); } catch ( Exception e ) { };
   }

   protected void startCytoTalk() {
      startCytoTalk( true, STATIC_PORT );
   }

   protected void startCytoTalk( boolean showMessage, int port ) {
      if ( server != null ) return;
      try {
	 localPort = port;
	 STATIC_PORT = port + 1;
	 server = new MyXmlRpcServer( localPort );
	 server.addService( service, new CytoTalkHandler( server ) );
	 if ( clientFilter != null || addLocalHost ) {
	    org.apache.xmlrpc.WebServer wserver = server.getServer();
	    wserver.setParanoid( true );
	    //wserver.denyClient( "*.*.*.*" );
	    if ( clientFilter != null ) {
	       wserver.acceptClient( clientFilter );
	       System.err.println( "Accepting clients from " + clientFilter );
	    }
	    if ( addLocalHost ) {
	       wserver.acceptClient( "127.0.0.1" );
	       System.err.println( "Accepting clients from localhost" );
	    }
	 }
	 
	 if ( showMessage ) 
	    JOptionPane.showMessageDialog( cWindow, 
					   "Started a CytoTalk handler listening on port " + localPort );
	 else System.out.println( "Started a CytoTalk handler listening on port " + 
				  localPort );
	 
      } catch( Exception ee ) {
	 System.err.println( "Could not start service: " + ee.getMessage() );
      }
   }

   protected void shutdown() {
      if ( server == null ) return;
      server.removeService( service );
      server.shutdown();
      server = null;
   }

   protected class AboutCytoTalkPlugin extends AbstractAction {
      AboutCytoTalkPlugin() { super( "About CytoTalk Plugin..." ); }

      public void actionPerformed (ActionEvent e) {
         JOptionPane.showMessageDialog( cWindow, new Object[] { 
	    "CytoTalk plugin by David Reiss",
	    "Use R, Perl, Python, or nearly any external language to control a Cytoscape process.",
	    "Questions or comments: dreiss@systemsbiology.org" },
                                        "About CytoTalk plugin",
                                        JOptionPane.INFORMATION_MESSAGE );
      }
   }

   public class StartCytoTalk extends AbstractAction {
      StartCytoTalk() { super( "Start CytoTalk"); }
      public void actionPerformed (ActionEvent e) { startCytoTalk(); }
   }

   public class StopCytoTalk extends AbstractAction {
      StopCytoTalk() { super( "Stop CytoTalk" ); }
      public void actionPerformed( ActionEvent e ) { shutdown(); }
   }

   public class SetCytoTalkPort extends AbstractAction {
      SetCytoTalkPort() { super( "Set CytoTalk Port" ); }
      public void actionPerformed( ActionEvent e ) { setPort(); }
   }

   public class SetCytoTalkClientFilter extends AbstractAction {
      SetCytoTalkClientFilter() { super( "Set CytoTalk Client Address Filter" ); }
      public void actionPerformed( ActionEvent e ) { setClientFilter(); }
   }

   protected void setPort() {
      final JDialog dialog = new JDialog();
      JPanel cp = (JPanel) dialog.getContentPane();
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout( new FlowLayout() );
      centerPanel.add( new JLabel( "Use this port: " ) );
      final JTextField portField = new JTextField( 10 );
      portField.setText( "" + localPort );
      centerPanel.add( portField );
      cp.add( centerPanel, BorderLayout.NORTH );
      centerPanel = new JPanel();
      JPanel butPanel = new JPanel();
      butPanel.setLayout( new FlowLayout() );
      JButton but = new JButton( "Default" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { portField.setText( "" + DEFAULT_PORT ); } } );
      butPanel.add( but );
      but = new JButton( "Cancel" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { dialog.dispose(); } } );
      butPanel.add( but );
      but = new JButton( "OK" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
	       try { 
		  int port = Integer.parseInt( portField.getText() );
		  if ( port < DEFAULT_PORT )
		     JOptionPane.showMessageDialog( cWindow, 
						    "Please enter a port number greater than " + DEFAULT_PORT + "." );
		  else dialog.dispose();
		  localPort = STATIC_PORT = port;
	       } catch ( Exception ee ) {
		  JOptionPane.showMessageDialog( cWindow, 
						 "Please enter a port number as an integer greater than " + DEFAULT_PORT + "." );
		  //ee.printStackTrace();
	       }
	    } } );
      butPanel.add( but );
      cp.add( butPanel, BorderLayout.SOUTH );
      dialog.doLayout();
      dialog.pack();
      dialog.setLocationRelativeTo( cWindow.getMainFrame() );
      dialog.setVisible( true );
   }

   protected void setClientFilter() {
      final JDialog dialog = new JDialog();
      JPanel cp = (JPanel) dialog.getContentPane();
      JPanel centerPanel = new JPanel();
      centerPanel.setLayout( new FlowLayout() );
      centerPanel.add( new JLabel( "Only allow these IP addresses to connect: " ) );
      final JTextField hostField = new JTextField( 20 );
      hostField.setText( getLocalHostAddr() );
      centerPanel.add( hostField );
      cp.add( centerPanel, BorderLayout.NORTH );

      final JCheckBox addLocalHostCB = new JCheckBox( "Allow localhost connections by default?", addLocalHost );
      cp.add( addLocalHostCB, BorderLayout.CENTER );

      centerPanel = new JPanel();
      JPanel butPanel = new JPanel();
      butPanel.setLayout( new FlowLayout() );
      JButton but = new JButton( "Default" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { 
	       hostField.setText( getLocalHostAddr() ); } } );
      butPanel.add( but );
      but = new JButton( "Cancel" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) { dialog.dispose(); } } );
      butPanel.add( but );
      but = new JButton( "OK" );
      but.addActionListener( new ActionListener() {
	    public void actionPerformed( ActionEvent e ) {
	       try { 
		  clientFilter = hostField.getText();
		  dialog.dispose();
		  if ( server != null && ! "".equals( clientFilter ) ) {
		     System.err.println( "Accepting clients from " + clientFilter );
		     server.getServer().setParanoid( true );
		     server.getServer().acceptClient( clientFilter );
		     addLocalHost = addLocalHostCB.isSelected();
		     if ( addLocalHost ) {
			server.getServer().acceptClient( "127.0.0.1" );
			System.err.println( "Accepting clients from localhost" );
		     }
		  }
	       } catch ( Exception ee ) {
		  JOptionPane.showMessageDialog( cWindow, 
			"Please enter an IP address filter (e.g. 192.168.0.*)." );
		  //ee.printStackTrace();
	       }
	 } } );
	 butPanel.add( but );
	 cp.add( butPanel, BorderLayout.SOUTH );
	 dialog.doLayout();
	 dialog.pack();
	 dialog.setLocationRelativeTo( cWindow.getMainFrame() );
	 dialog.setVisible( true );
   }

   public class RunPerlScript extends AbstractAction {
      JTextField fileField, argsField;

      RunPerlScript() { super( "Execute an R, Perl, or Python script..."); }

      public void actionPerformed( ActionEvent e ) {
	 final JDialog dialog = new JDialog();
	 JPanel cp = (JPanel) dialog.getContentPane();
	 JPanel centerPanel = new JPanel();
	 centerPanel.setLayout( new FlowLayout() );
	 fileField = new JTextField( 30 );
	 fileField.setText( lastChoice );
	 centerPanel.add( fileField );
	 JButton but = new JButton( "Browse" );
	 centerPanel.add( but );
	 but.addActionListener( new ActionListener() {
	       public void actionPerformed( ActionEvent e ) {
		  JFileChooser chooser = 
		     new JFileChooser( new File( lastDir ) );
		  if ( chooser.showOpenDialog( dialog ) == chooser.APPROVE_OPTION ) {
		     lastChoice = chooser.getSelectedFile().toString();
		     lastDir = chooser.getCurrentDirectory().toString();
		     fileField.setText( lastChoice );
		  }
	       } } );
	 cp.add( centerPanel, BorderLayout.NORTH );
	 centerPanel = new JPanel();
	 argsField = new JTextField( 25 );
	 centerPanel.setLayout( new FlowLayout() );
	 centerPanel.add( new JLabel( "Arguments: " ) );
	 centerPanel.add( argsField );
	 cp.add( centerPanel, BorderLayout.CENTER );
	 JPanel butPanel = new JPanel();
	 butPanel.setLayout( new FlowLayout() );
	 but = new JButton( "Cancel" );
	 but.addActionListener( new ActionListener() {
	       public void actionPerformed( ActionEvent e ) { dialog.dispose(); } } );
	 butPanel.add( but );
	 but = new JButton( "GO" );
	 but.addActionListener( new ActionListener() {
	       public void actionPerformed( ActionEvent e ) {
		  dialog.dispose();
		  final boolean started = server == null;
		  if ( server == null ) startCytoTalk();
		  final String fname = fileField.getText();
		  final String args = argsField.getText();
		  String shortName = fname.substring( fname.lastIndexOf( File.separator ) + 1 );
		  final JDialog prog = 
		     new IndeterminateProgressBar( cWindow.getMainFrame(), 
						   fname, "Executing the script \"" + shortName + "\"..." );
		  prog.doLayout();
		  prog.pack();
		  prog.setLocationRelativeTo( cWindow.getMainFrame() );
		  prog.show();
		  //try {
		  ( new Thread() { public void run() {
		     try {
			ExecRunner child = new ExecRunner();
			int result; 
			if ( ! fname.toUpperCase().endsWith( ".R" ) ) {
			   result = child.exec( fname + " " + args + " -port " + localPort, 
						System.out, System.err );
			} else {
			   String file = MyUtils.ReadFile( fname );
			   file = "port <- " + localPort + "\n" + file;
			   file += "q()\n";
			   Properties props = MyUtils.readProperties( 
					 "csplugins/isb/dreiss/cytoTalk.properties" );
			   String cp = (String) System.getProperty( "java.class.path" );
			   String r_libs = (String) props.get( "cytoTalk.R_LIBS" );
			   String lib_path = (String) props.get( "cytoTalk.LD_LIBRARY_PATH" );
			   String env[] = new String[] { 
			      "CLASSPATH=" + cp, "R_LIBS=" + r_libs, 
			      "LD_LIBRARY_PATH=" + lib_path };
			   child.setEnvironment( env );
			   String rCommand = (String) props.get( "cytoTalk.R_COMMAND" );
			   result = child.exec( rCommand, file, System.out, System.err );
			}	
			System.err.println( "RESULT: " + result );
		     } catch ( Exception ee ) { ee.printStackTrace(); }
		     prog.dispose();
		     if ( started ) shutdown();
		  } } ).start();
		  //} catch ( Exception ee ) { ee.printStackTrace(); }
	       } } );
	 butPanel.add( but );
	 cp.add( butPanel, BorderLayout.SOUTH );
	 dialog.doLayout();
	 dialog.pack();
	 dialog.setLocationRelativeTo( cWindow.getMainFrame() );
	 dialog.setVisible( true );
      }
   }

   static String getLocalHostAddr() {
      try { return InetAddress.getLocalHost().getHostAddress(); }
      catch ( Exception e ) { return null; }
   }      
}
