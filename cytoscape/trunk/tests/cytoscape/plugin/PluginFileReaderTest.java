/**
 * 
 */
package cytoscape.plugin;

import junit.framework.TestCase;
import java.util.List;

/**
 * @author skillcoy
 *
 */
public class PluginFileReaderTest extends TestCase {

	private String url;
	private PluginFileReader reader;
	private String fileName = "test_plugin.xml";


	private String getFileUrl() {
		String FS = "/";
		String UserDir = System.getProperty("user.dir");
		UserDir = UserDir.replaceFirst("/", "");
		return "file:///" + UserDir + FS + "testData"
				+ FS + "plugins" + FS;
	}

	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		url = getFileUrl() + fileName;
		reader = new PluginFileReader(url);
		assertNotNull(reader);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#PluginFileReader(java.lang.String)}.
	 */
	public void testPluginFileReader() throws Exception {
		assertNotNull(reader);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#getProjectName()}.
	 */
	public void testGetProjectName() {
		assertNotNull(reader.getProjectName());
		assertTrue(reader.getProjectName().equals("Cytoscape Plugins"));
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#getProjectDescription()}.
	 */
	public void testGetProjectDescription() {
		assertNotNull(reader.getProjectDescription());
		assertTrue(reader.getProjectDescription().equals("Test"));
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#getProjectUrl()}.
	 */
	public void testGetProjectUrl() {
		assertNotNull(reader.getProjectUrl());
		assertTrue(reader.getProjectUrl().equals("http://cytoscape.org/plugin-inquiry-url"));
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#getPlugins()}.
	 */
	public void testGetPlugins() {
		assertNotNull(reader.getPlugins());
		assertEquals(reader.getPlugins().size(), 5);
	}

	public void testGetPluginsLicense() {
		List<PluginInfo> All = reader.getPlugins();
		assertNotNull(All.get(0).getLicenseText());
	}
	
	
	
}
