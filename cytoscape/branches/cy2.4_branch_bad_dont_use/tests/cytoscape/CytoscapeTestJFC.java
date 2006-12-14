
package cytoscape;

import junit.extensions.jfcunit.*;
import junit.extensions.jfcunit.finder.*;
import junit.extensions.jfcunit.eventdata.*;
import junit.framework.*;

import javax.swing.*;

public class CytoscapeTestJFC extends JFCTestCase {

    private CyMain application = null;

    public CytoscapeTestJFC( String name ) {
        super( name );
    }

    protected void setUp( ) throws Exception {
        super.setUp( );

        // Choose the test Helper
        setHelper( new JFCTestHelper( ) ); // Uses the AWT Event Queue.
        // setHelper( new RobotTestHelper( ) ); // Uses the OS Event Queue.


	String[] args = {"-p","plugins/core"};
	application = new CyMain(args);

    }

    protected void tearDown( ) throws Exception {
        application = null;
        //this.getHelper.cleanUp( this );
        super.tearDown( );
    }

    public void testFileMenu() {

	NamedComponentFinder finder = new NamedComponentFinder(JMenu.class, "File" );
	//JMenu fileMenu = ( JMenu ) finder.find( application, 0);
	JMenu fileMenu = ( JMenu ) finder.find();
	assertNotNull( "Could not find the File Menu", fileMenu );
	System.out.println("hello jfc test");
	/*
	JDialog dialog;

	NamedComponentFinder finder = new NamedComponentFinder(JComponent.class, "ExitButton" );
	JButton exitButton = ( JButton ) finder.find( loginScreen, 0);
	assertNotNull( "Could not find the Exit button", exitButton );

	finder.setName( "EnterButton" );
	JButton enterButton = ( JButton ) finder.find( loginScreen, 0 );
	assertNotNull( "Could not find the Enter button", enterButton );

	finder.setName( "LoginNameTextField" );
	JTextField userNameField = ( JTextField ) finder.find( loginScreen, 0 );
	assertNotNull( "Could not find the userNameField", userNameField );
	assertEquals( "Username field is empty", "", userNameField.getText( ) );

	finder.setName( "PasswordTextField" );
	JTextField passwordField = ( JTextField ) finder.find( loginScreen, 0 );
	assertNotNull( "Could not find the passwordField", passwordField );
	assertEquals( "Password field is empty", "", passwordField.getText( ) );

	getHelper().enterClickAndLeave( new MouseEventData( this, enterButton ) );
	DialogFinder dFinder = new DialogFinder( loginScreen );
	showingDialogs = dFinder.findAll();
	assertEquals( "Number of dialogs showing is wrong", 1, showingDialogs.size( ) );
	dialog = ( JDialog )showingDialogs.get( 0 );
	assertEquals( "Wrong dialog showing up", "Login Error", dialog.getTitle( ) );
	getHelper().disposeWindow( dialog, this );
	*/
    }

        public static void main (String[] args) {
	           junit.textui.TestRunner.run (new TestSuite(CytoscapeTestJFC.class));
        }

}
    
