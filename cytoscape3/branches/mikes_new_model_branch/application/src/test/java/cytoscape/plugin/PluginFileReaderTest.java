/**
 * 
 */
package cytoscape.plugin;

import junit.framework.TestCase;

import java.io.File;
import java.io.InputStream;
import java.util.List;


/**
 * @author skillcoy
 * 
 */
public class PluginFileReaderTest extends TestCase {

	private String url;

	private PluginFileReader reader;

	private File tempTestFile;
	
	private String getFileUrl() {
		String FS = "/";
		String UserDir = System.getProperty("user.dir");
		UserDir = UserDir.replaceFirst(FS, "");
    String Url = UserDir + FS + "src" + FS + "test" + FS + "resources" + FS + "testData" + FS + "plugins" + FS;
  
    return "file:///" + Url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		// transform the test files first to get the version numbers up to date
    InputStream is = PluginTestXML.class.getResourceAsStream("/testData/plugins/test_plugin.xml");
    tempTestFile = PluginTestXML.transformXML(is, getFileUrl());
		url = "file:///" + tempTestFile.getAbsolutePath();
		reader = new PluginFileReader(url);
		assertNotNull(reader);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		tempTestFile.delete();
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginFileReader#PluginFileReader(java.lang.String)}.
	 */
	public void testPluginFileReader() throws Exception {
		assertNotNull(reader);
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginFileReader#getProjectName()}.
	 */
	public void testGetProjectName() {
		assertNotNull(reader.getProjectName());
		assertTrue(reader.getProjectName().equals("Cytoscape Plugins"));
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginFileReader#getProjectDescription()}.
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
		assertTrue(reader.getProjectUrl().equals("http://cytoscape.org"));
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginFileReader#getPlugins()}.
	 */
	public void testGetPlugins() {
		assertNotNull(reader.getPlugins());
		assertEquals(reader.getPlugins().size(), 8);
	}

	public void testGetPluginsLicense() {
		List<PluginInfo> All = reader.getPlugins();
		assertNotNull(All.get(0).getLicenseText());
	}

	public void testGetThemes() {
		assertNotNull(reader.getThemes());
		assertEquals(reader.getThemes().size(), 2);
		assertEquals(reader.getThemes().get(0).getPlugins().size(), 2);
		for (DownloadableInfo i: reader.getThemes()) {
			assertEquals(i.getCategory(), Category.THEME.getCategoryText());
		}
	}

	// regression test, not all files will contain the <theme> tags
	public void testReadFileMissingThemes() throws org.jdom.JDOMException,
			java.io.IOException {
    InputStream is = PluginFileReaderTest.class.getResourceAsStream("/testData/plugins/test_plugin_no_themes.xml");
    PluginFileReader readerNoThemes = new PluginFileReader(is);
		assertNotNull(readerNoThemes.getThemes());
		assertEquals(readerNoThemes.getThemes().size(), 0);
	}
	
}
