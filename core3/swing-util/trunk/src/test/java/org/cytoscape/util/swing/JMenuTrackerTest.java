
package org.cytoscape.util.swing;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

public class JMenuTrackerTest {

	JMenuTracker tracker;
	JMenuTracker menuBarTracker;
	JPopupMenu popup;
	JMenuBar menuBar;

	@Before
	public void setUp() {
		popup = new JPopupMenu("test");
		tracker = new JMenuTracker(popup);	

		menuBar = new JMenuBar();
		menuBarTracker = new JMenuTracker(menuBar);
	}

	@Test(expected=NullPointerException.class)
	public void testGetNullString() {
		tracker.getMenu(null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testGetZeroLengthString() {
		tracker.getMenu("");
	}

	@Test
	public void testOneMenu() {
		JMenu m = tracker.getMenu("File");
		assertNotNull(m);
		assertEquals("title","File",m.getText());
	}

	@Test
	public void testSubMenu() {
		JMenu m = tracker.getMenu("File.Import");
		assertNotNull(m);
		assertEquals("title","Import",m.getText());
	}

	@Test
	public void testSpacesInName() {
		JMenu m = tracker.getMenu("File.Network Import");
		assertNotNull(m);
		assertEquals("title","Network Import",m.getText());
	}

	@Test
	public void testBasicMenuOrder() {
		JMenu file = tracker.getMenu("File");
		JMenu first = tracker.getMenu("File.First");
		JMenu second = tracker.getMenu("File.Second");

		assertEquals("num sub menus",2,file.getItemCount());
		assertEquals("first sub menu",first,file.getItem(0));
		assertEquals("second sub menu",second,file.getItem(1));
	}

	@Test
	public void testMenuOrderSpecification() {
		JMenu file = tracker.getMenu("File");
		JMenu first = tracker.getMenu("File.First",0);
		// using the same parent position should put 
		// the second menu item before the first
		JMenu second = tracker.getMenu("File.Second",0);

		assertEquals("num sub menus",2,file.getItemCount());
		assertEquals("first sub menu",first,file.getItem(1));
		assertEquals("second sub menu",second,file.getItem(0));
	}

	@Test
	public void testPopupContainsFirst() {
		JMenu file = tracker.getMenu("File");

		assertTrue("popup contains menu", popup.getComponentIndex(file) >= 0 );
	}

	@Test
	public void testPopupDoesntContainChildren() {
		JMenu imp = tracker.getMenu("File.Import");

		assertTrue("popup contains menu", popup.getComponentIndex(imp) < 0 );
	}

	@Test
	public void testMenuBarContainsFirst() {
		JMenu file = menuBarTracker.getMenu("File");

		assertTrue("menubar contains menu", menuBar.getComponentIndex(file) >= 0 );
	}

	@Test
	public void testMenuBarDoesntContainChildren() {
		JMenu imp = menuBarTracker.getMenu("File.Import");

		assertTrue("menubar contains menu", menuBar.getComponentIndex(imp) < 0 );
	}
}
