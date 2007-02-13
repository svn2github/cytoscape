
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package cytoscape;

import junit.extensions.jfcunit.*;
import junit.extensions.jfcunit.eventdata.*;
import junit.extensions.jfcunit.finder.*;

import junit.framework.*;

import javax.swing.*;


/**
 *
 */
public class CytoscapeTestJFC extends JFCTestCase {
	private CyMain application = null;

	/**
	 * Creates a new CytoscapeTestJFC object.
	 *
	 * @param name  DOCUMENT ME!
	 */
	public CytoscapeTestJFC(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();

		// Choose the test Helper
		setHelper(new JFCTestHelper()); // Uses the AWT Event Queue.
		                                // setHelper( new RobotTestHelper( ) ); // Uses the OS Event Queue.

		String[] args = { "-p", "plugins/core" };
		application = new CyMain(args);
	}

	protected void tearDown() throws Exception {
		application = null;
		//this.getHelper.cleanUp( this );
		super.tearDown();
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void testFileMenu() {
		NamedComponentFinder finder = new NamedComponentFinder(JMenu.class, "File");

		//JMenu fileMenu = ( JMenu ) finder.find( application, 0);
		JMenu fileMenu = (JMenu) finder.find();
		assertNotNull("Could not find the File Menu", fileMenu);
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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param args DOCUMENT ME!
	 */
	public static void main(String[] args) {
		junit.textui.TestRunner.run(new TestSuite(CytoscapeTestJFC.class));
	}
}
