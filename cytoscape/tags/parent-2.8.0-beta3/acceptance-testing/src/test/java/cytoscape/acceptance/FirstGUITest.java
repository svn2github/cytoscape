package cytoscape.acceptance;

import org.junit.Test; 
import org.junit.Before; 
import org.junit.After; 
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.edt.*;
import org.fest.swing.timing.*;
import org.fest.swing.core.matcher.*;

import cytoscape.CyMain;
import cytoscape.Cytoscape;
import cytoscape.view.CytoscapeDesktop;

public class FirstGUITest {

	private FrameFixture desktop;

	public FirstGUITest() {
		FailOnThreadViolationRepaintManager.install();
	}

	@Before public void setUp() {
		CytoscapeDesktop frame = GuiActionRunner.execute(new GuiQuery<CytoscapeDesktop>() {
			protected CytoscapeDesktop executeInEDT() {
				try {
				String[] args = new String[]{"-p","plugins"};
				new CyMain(args);  
				return Cytoscape.getDesktop();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		desktop = new FrameFixture(frame);
		desktop.show(); // shows the frame to test
	}

	@After 
	public void tearDown() {
		desktop.cleanUp();
	}

	/**
	 * Since starting Cytoscape takes a while, it's probably best
	 * to run many different tests as a single test case.  To maintain
	 * sanity, please add additional tests as separate method calls
	 * within this test case.
	 */
	@Test 
	public void runTests() {
		openAndClosePluginManager(); 
	}


	private void openAndClosePluginManager() {
		desktop.menuItemWithPath("Plugins","Manage Plugins")
		       .click();
		Pause.pause(5000);
		desktop.dialog(DialogMatcher.withTitle("Manage Plugins"))
		       .show()
		       .button(JButtonMatcher.withText("Close"))
		       .click();
	}
}

