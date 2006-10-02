package cytoscape;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import swingunit.extensions.ExtendedRobotEventFactory;
import swingunit.framework.EventPlayer;
import swingunit.framework.ExecuteException;
import swingunit.framework.FinderMethodSet;
import swingunit.framework.RobotEventFactory;
import swingunit.framework.Scenario;
import swingunit.framework.TestUtility;
import junit.framework.TestCase;

import cytoscape.*;
import cytoscape.view.*;

public class Tutorial1TestSwing extends TestCase {
	private Scenario scenario;
	private RobotEventFactory robotEventFactory = new ExtendedRobotEventFactory();
	private FinderMethodSet methodSet = new FinderMethodSet();
	private Robot robot;
	
	private CyMain application;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		System.setProperty("TestSetting","testData/TestSetting.properties");

		// Start application.
		Runnable r = new Runnable() {
			public void run() {
				try {
					String[] args = {"-p", "plugins/core/AutomaticLayout.jar"};
					//String[] args = {};
					application = new CyMain(args);
					//LayoutPlugin layoutPlugin = new LayoutPlugin();
				} catch (Exception e) { e.printStackTrace(); }
			}
		};
		SwingUtilities.invokeAndWait(r);

		robot = new Robot();
		TestUtility.waitForCalm();
		
		// To make sure to load the scenario file. 
		// CytoscapeTestSwing.xml is placed on the same package directory.
		String filePath = CytoscapeTestSwing.class.getResource("Tutorial1TestSwing.xml").getFile();
		// Create Scenario object and create XML file.
		scenario = new Scenario(robotEventFactory, methodSet);
		scenario.read(filePath);
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		// Terminate application.
		Runnable r = new Runnable() {
			public void run() {
			//	if(application != null) {
			//		application.setVisible(false);
			//	}
			}
		};
		SwingUtilities.invokeAndWait(r);

		application = null;
		scenario = null;
		robot = null;
	}

	public void testOpenNetworkFile() throws ExecuteException {
		// Use keyword substitution.
		scenario.setTestSetting("OPEN_NETWORK_FILE","FILE_TO_OPEN","RUAL.subset.sif");
		EventPlayer player = new EventPlayer(scenario);
		player.run(robot, "OPEN_NETWORK_FILE");
		//scenario.setTestSetting("APPLY_SPRING_LAYOUT");
		//scenario.setTestSetting("APPLY_SPRING_LAYOUT","FILE_TO_OPEN","RUAL.subset.sif");
		//player = new EventPlayer(scenario);
		//player.run(robot, "APPLY_SPRING_LAYOUT");
		//player.run(robot, "SELECT_EDGES");
		scenario.setTestSetting("SELECT_NODE_BY_NAME","NODE_NAME","7157");
		player = new EventPlayer(scenario);
		player.run(robot, "SELECT_NODE_BY_NAME");
		player.run(robot, "SELECT_FIRST_NEIGHBORS");

		// write assertion code here.
	//	Set s = Cytoscape.getNetworkSet();
	//	assertTrue("exected 1, got: " + s.size(), s.size() == 1 );
	}
	
/**	public void testApplySpringLayout() throws ExecuteException{
		scenario.setTestSetting("APPLY_SPRING_LAYOUT","FILE_TO_OPEN","RUAL.subset.sif");
		EventPlayer player = new EventPlayer(scenario);
		player.run(robot, "APPLY_SPRING_LAYOUT");
	}*/
}
