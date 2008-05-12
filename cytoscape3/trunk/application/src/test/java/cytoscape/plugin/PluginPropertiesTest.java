/**
 * 
 */
package cytoscape.plugin;

import junit.framework.TestCase;

import java.io.InputStream;

/**
 * @author skillcoy
 *
 */
public class PluginPropertiesTest extends TestCase {

	PluginProperties pp;
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		String FS = java.io.File.separator;
		String TestProps = System.getProperty("user.dir") + FS + "testData" + FS + "plugins" + FS + "test_plugin.props";
	  InputStream is = PluginPropertiesTest.class.getResourceAsStream(FS + "testData" + FS + "plugins" + FS + "test_plugin.props");
    pp = new PluginProperties(is);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginProperties#fillPluginInfoObject(DownloadableInfo)} 
	 */
	public void testNoMatchingCyVersion() throws cytoscape.plugin.ManagerException {
		// store original version number so that we can restore it after the test.
		// without this running this unit test would change global state as a side-effect.
		String version =  cytoscape.CytoscapeInit.getProperties().getProperty("cytoscape.version.number");
		cytoscape.CytoscapeInit.getProperties().setProperty("cytoscape.version.number", "2.4");
		PluginInfo info = pp.fillPluginInfoObject(null);
		assertEquals(info.getCytoscapeVersion(), "2.4");
		
		// restore global state
		cytoscape.CytoscapeInit.getProperties().setProperty("cytoscape.version.number", version);
	}

	public void testMatchingCyVersion() throws cytoscape.plugin.ManagerException {
		// store original version number so that we can restore it after the test.
		// without this running this unit test would change global state as a side-effect.
		String version =  cytoscape.CytoscapeInit.getProperties().getProperty("cytoscape.version.number");
		cytoscape.CytoscapeInit.getProperties().setProperty("cytoscape.version.number", "2.3.3");
		PluginInfo info = pp.fillPluginInfoObject(null);
		assertNotNull(info);
		
		// restore global state
		cytoscape.CytoscapeInit.getProperties().setProperty("cytoscape.version.number", version);
	}


}
