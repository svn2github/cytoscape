/**
 * 
 */
package cytoscape.plugin;

import cytoscape.plugin.PluginStatus;

import java.io.*;
import org.jdom.*;
import org.jdom.input.*;
import java.util.*;
import junit.framework.TestCase;

/**
 * @author skillcoy
 * 
 */
public class PluginTrackerTest extends TestCase {
	private SAXBuilder builder;

	//private Document xmlDoc;

	private PluginTracker tracker;

	private String fileName = "test_tracker.xml";

	private File tmpDir;

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		builder = new SAXBuilder(false);
		tmpDir = new File(System.getProperty("java.io.tmpdir"));
		tracker = new PluginTracker(tmpDir, fileName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		tracker.delete();
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginTracker#PluginTracker()}.
	 */
	public void testPluginTracker() throws Exception {
		Document Doc = getDoc();
		assertNotNull(Doc);

		assertEquals(Doc.getRootElement().getName(), "CytoscapePlugin");
		assertEquals(Doc.getRootElement().getChildren().size(), 3);

		assertNotNull(Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName()));
		assertNotNull(Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName()));
		assertNotNull(Doc.getRootElement().getChild(
				PluginStatus.DELETE.getTagName()));

		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName()).getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName()).getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.DELETE.getTagName()).getChildren().size(), 0);
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginTracker#getListByStatus(cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testGetListByStatus() throws Exception {
		tracker.addDownloadable(getInfoObj(), PluginStatus.CURRENT);

		assertNotNull(tracker.getPluginListByStatus(PluginStatus.CURRENT));
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		// lets just check with the xml doc itself to be sure
		Document Doc = getDoc();
		Element Current = Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName());
		assertEquals(Current.getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName()).getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.DELETE.getTagName()).getChildren().size(), 0);
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginTracker#addPlugin(cytoscape.plugin.PluginInfo, cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testAddPlugin() throws Exception {
		tracker.addDownloadable(getInfoObj(), PluginStatus.CURRENT);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		PluginInfo obj = getInfoObj();
		obj.setName("myInstallTest");
		obj.setDownloadableURL("http://booya.com/foo.xml");
		tracker.addDownloadable(obj, PluginStatus.INSTALL);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 1);

		// changing the name of the object will not change the object if
		// the id/projurl stay the same
		obj.setName("mySecondInstallTest");
		tracker.addDownloadable(obj, PluginStatus.INSTALL);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 1);

		// whole new object will get added though
		PluginInfo newObj = new PluginInfo(
				"this is my unique key for my new plugin");
		newObj.setName("mySecondInstallTest");
		newObj.setProjectUrl("http://foobar.com/booya.xml");
		newObj.setFiletype(PluginInfo.FileType.JAR);
		newObj.addCytoscapeVersion("2.5.1");
		tracker.addDownloadable(newObj, PluginStatus.INSTALL);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 2);

		// lets just check with the xml doc itself to be sure
		Document Doc = getDoc();
		Element Install = Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName());
		assertEquals(Install.getChildren().size(), 2);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName()).getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.DELETE.getTagName()).getChildren().size(), 0);
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginTracker#addTheme(ThemeInfo, PluginStatus)}
	 * 
	 * @throws Exception
	 */
	public void testAddTheme() throws Exception {
		ThemeInfo themeObj = new ThemeInfo("themeTest123");
		themeObj.setName("Test Theme");
		themeObj.setDownloadableURL("http://booya.com/foo.xml");
		themeObj.addCytoscapeVersion("2.5.1");

		PluginInfo obj = getInfoObj();
		obj.setName("myInstallTest");
		obj.setDownloadableURL("http://booya.com/foo.xml");

		themeObj.addPlugin(obj);

		tracker.addDownloadable(themeObj, PluginStatus.INSTALL);

		assertEquals(tracker.getThemeListByStatus(PluginStatus.INSTALL).size(),
				1);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.INSTALL)
				.size(), 1);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 0);

		Document Doc = getDoc();
		Element Install = Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName());
		assertEquals(Install.getChildren().size(), 1);
		assertNotNull(Install.getChild(PluginXml.THEME.getTag()).getChild(
				PluginXml.PLUGIN_LIST.getTag()));
	}
	
	public void testRemoveTheme() throws Exception {
		ThemeInfo themeObj = new ThemeInfo("themeTest123");
		themeObj.setName("Test Theme");
		themeObj.setDownloadableURL("http://booya.com/foo.xml");
		themeObj.addCytoscapeVersion("2.5.1");

		PluginInfo obj = getInfoObj();
		obj.setName("myInstallTest");
		obj.setDownloadableURL("http://booya.com/foo.xml");

		themeObj.addPlugin(obj);

		tracker.addDownloadable(themeObj, PluginStatus.INSTALL);
		assertEquals(tracker.getThemeListByStatus(PluginStatus.INSTALL).size(),
				1);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.INSTALL)
				.size(), 1);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 0);

		tracker.removeDownloadable(themeObj, PluginStatus.INSTALL);
		assertEquals(tracker.getThemeListByStatus(PluginStatus.INSTALL).size(),
				0);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.INSTALL)
				.size(), 0);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.CURRENT)
				.size(), 0);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.DELETE)
				.size(), 0);
	}

	public void testAddThemeAndPlugin() throws Exception {
		ThemeInfo themeObj = new ThemeInfo("1A");
		themeObj.setName("Test Theme");
		themeObj.setDownloadableURL("http://booya.com/foo.xml");
		themeObj.addCytoscapeVersion("2.6");

		PluginInfo themePlugin = getInfoObj();
		themePlugin.addCytoscapeVersion("2.6");
		themeObj.addPlugin(themePlugin);
		
		tracker.addDownloadable(themeObj, PluginStatus.CURRENT);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.CURRENT).size(), 1);
		
		PluginInfo pluginObj = new PluginInfo("1A");
		pluginObj.setName("Plugin Test");
		pluginObj.addCytoscapeVersion("2.6");
		pluginObj.setDownloadableURL("http://booya.com/foo.xml");
		pluginObj.setFiletype(PluginInfo.FileType.JAR);
		
		tracker.addDownloadable(pluginObj, PluginStatus.CURRENT);
		assertEquals(tracker.getDownloadableListByStatus(PluginStatus.CURRENT).size(), 2);
	}
	
	
	public void testAddSamePlugin() throws Exception {
		PluginInfo InfoObject = getInfoObj();

		tracker.addDownloadable(InfoObject, PluginStatus.CURRENT);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		String NewName = "DuplicatePluginTest";
		InfoObject.setName(NewName);
		tracker.addDownloadable(InfoObject, PluginStatus.CURRENT);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		DownloadableInfo info = tracker.getPluginListByStatus(
				PluginStatus.CURRENT).get(0);

		assertTrue(info.getName().equals(InfoObject.getName()));

		Document Doc = getDoc();
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName()).getChild("plugin")
				.getChildTextTrim("name"), NewName);
	}

	/**
	 * Test method for
	 * {@link cytoscape.plugin.PluginTracker#removeDownloadable(cytoscape.plugin.DownloadableInfo, cytoscape.plugin.PluginTracker.PluginStatus)}.
	 */
	public void testRemovePlugin() throws Exception {
		tracker.addDownloadable(getInfoObj(), PluginStatus.CURRENT);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		PluginInfo obj = new PluginInfo("999");
		obj.setName("myInstallTest");
		obj.setDownloadableURL("http://foobar.org/y.xml");
		obj.setCategory("Test");
		obj.setFiletype(PluginInfo.FileType.JAR);
		obj.addCytoscapeVersion("2.5.1");

		tracker.addDownloadable(obj, PluginStatus.INSTALL);

		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 1);

		// won't change because this object wasn't an install object
		tracker.removeDownloadable(getInfoObj(), PluginStatus.INSTALL);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 1);

		tracker.removeDownloadable(obj, PluginStatus.INSTALL);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.INSTALL).size(), 0);

		Document Doc = getDoc();
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.CURRENT.getTagName()).getChildren().size(), 1);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.INSTALL.getTagName()).getChildren().size(), 0);
		assertEquals(Doc.getRootElement().getChild(
				PluginStatus.DELETE.getTagName()).getChildren().size(), 0);
	}

	// regression test
	public void testAddRemovePluginWithSameID() throws Exception {
		PluginInfo objA = getInfoObj();
		PluginInfo objB = new PluginInfo(objA.getID());
		objB.setName("Different Test");
		objB.setDownloadableURL("http://test.com/blue.xml");
		objB.setFiletype(PluginInfo.FileType.JAR);
		objB.setPluginClassName("some.other.class.DifferentTest");
		objB.addCytoscapeVersion(objA.getCytoscapeVersion());

		tracker.addDownloadable(objA, PluginStatus.CURRENT);
		tracker.addDownloadable(objB, PluginStatus.CURRENT);
		assertEquals(
				tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 2);
		List<PluginInfo> CurrentList = tracker
				.getPluginListByStatus(PluginStatus.CURRENT);
		assertFalse(CurrentList.get(0).equals(CurrentList.get(1)));

		tracker.removeDownloadable(objA, PluginStatus.CURRENT);
		assertEquals(tracker.getPluginListByStatus(PluginStatus.CURRENT).size(), 1);

		// check that the correct object was actually removed
		PluginInfo Current = tracker
				.getPluginListByStatus(PluginStatus.CURRENT).get(0);
		assertEquals(Current.getName(), objB.getName());
	}

	public void testPartiallyCorruptedXml() {
		// partially corrupted file
		try {
			File file = new File("testData/plugins/track_plugins_c1.xml");
			PluginTracker tk = new PluginTracker(file);
			assertNotNull(tk);
			// Note: do NOT call write or you change the test file
			assertEquals(tk.getDownloadableListByStatus(PluginStatus.CURRENT).size(), 13);
			assertTrue(tk.hasCorruptedElements());
			assertEquals(tk.getTotalCorruptedElements(), 1);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			fail("Failed to handle corrupted xml: " + npe.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to read file: " + e.getMessage());
		}
	}

	public void testCompletelyCorruptedXml() {
		// completely corrupted file
		PluginTracker tk = null;
		File tempFile = null;
		try {
			File file = new File("testData/plugins/track_plugins_c2.xml");
			// need to copy file to new location because it will be deleted
			tempFile = copyFileToTempDir(file);
			tk = new PluginTracker(tempFile);
		} catch (NullPointerException npe) {
			npe.printStackTrace();
			fail("Failed to handle corrupted xml: " + npe.getMessage());
		} catch (TrackerException te) { // expected
			assertNotNull(te);
			//fail("Failed to build document from file: " + te.getMessage());
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Failed to read file: " + ioe.getMessage());
		} finally {
			if (tempFile != null && tempFile.exists())
				tempFile.delete();
		}
	}
	
	
	private File copyFileToTempDir(File fileToCopy) {
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		File tempFile = new File(tempDir, fileToCopy.getName());
		
		try {
			BufferedReader reader = new BufferedReader( new FileReader(fileToCopy) );
			BufferedWriter writer = new BufferedWriter( new FileWriter(tempFile) );
			String line = null;
			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.newLine();
				writer.flush();
			}
			reader.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tempFile;
	}

	private Document getDoc() throws Exception {
		File TestFile = new File(tmpDir, fileName);
		assertTrue(TestFile.exists());
		assertTrue(TestFile.canRead());
		// lets just check with the xml doc itself to be sure
		Document Doc = builder.build(TestFile);
		assertNotNull(Doc);
		return Doc;
	}

	private PluginInfo getInfoObj() {
		PluginInfo infoObj = new PluginInfo("123");
		infoObj.setName("myTest");
		infoObj.setCategory("Test");
		infoObj.addCytoscapeVersion("2.5.1");

		infoObj.setPluginClassName("some.class.MyTest");
		infoObj.setDownloadableURL("http://test.com/x.xml");
		infoObj.setFiletype(PluginInfo.FileType.JAR);
		return infoObj;
	}

	private PluginInfo fillInPI(PluginInfo obj) {
		obj.setCategory("Test");
		obj.addCytoscapeVersion("2.5.1");
		obj.setPluginClassName("some.class.MyTest");
		obj.setDownloadableURL("http://test.com/x.xml");
		obj.setFiletype(PluginInfo.FileType.JAR);
		obj.setDownloadableURL("http://booya.com/foo.xml");
		return obj;
	}

}
