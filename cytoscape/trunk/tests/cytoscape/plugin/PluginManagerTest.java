/**
 * 
 */
package cytoscape.plugin;

import cytoscape.plugin.PluginTracker.PluginStatus;

import java.io.File;
import java.util.*;
import cytoscape.CytoscapeInit;

import junit.framework.TestCase;

/**
 * @author skillcoy
 *
 */
public class PluginManagerTest extends TestCase {
	private PluginManager mgr;
	private PluginTracker tracker;
	private String	testUrl = "http://db.systemsbiology.net/cytoscape/skillcoyne/plugins.xml";
	private File tmpDir;
	private String fileName;
	
	private static void print(String s) {
		System.out.println(s);
	}
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		fileName = "test_tracker.xml";
		tmpDir = new File(System.getProperty("java.io.tmpdir"));
		tracker = new PluginTracker(tmpDir, fileName);

		mgr = PluginManager.getPluginManager(tracker);
		assertTrue( (new File(tmpDir, fileName)).exists() );
		print("setUp");
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		File TrackerFile = new File(tmpDir, fileName);
		tracker.delete();
		assertFalse( TrackerFile.exists() );
		System.out.println(TrackerFile.getAbsolutePath() + " exists " + TrackerFile.exists());
		print("tearDown");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#getPluginManager()}.
	 */
	public void testGetPluginManager() {
		assertNotNull(mgr);
		assertNotNull(PluginManager.getPluginManager(tracker));
		assertEquals(mgr, PluginManager.getPluginManager(tracker));
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#getTempDownloadDirecotry()}.
	 */
	public void testGetTempDownloadDirecotry() {
		assertNotNull(mgr.getTempDownloadDirectory());
		assertEquals(mgr.getTempDownloadDirectory().getAbsolutePath(), 
				CytoscapeInit.getConfigDirectory().getAbsolutePath() + File.separator + "plugins");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#getPlugins(cytoscape.plugin.PluginTracker.PluginStatus)}.
	 * TODO
	 */
	public void testGetPlugins() {
		// Not sure how to test this since I can't register anything
		// w/o a full Cytoscape startup
		assertEquals(mgr.getPlugins(PluginStatus.CURRENT).size(), 0);
		assertEquals(mgr.getPlugins(PluginStatus.INSTALL).size(), 0);
		assertEquals(mgr.getPlugins(PluginStatus.DELETE).size(), 0);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#inquire()}.
	 */
	public void testInquire() throws Exception {
		// this tests against the default url, the number of plugins could change from 
		// one test to another but there should always be more than 1
		assertNotNull(mgr.inquire());
		assertTrue(mgr.inquire().size() > 1);
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#inquire(java.lang.String)}.
	 */
	public void testInquireString() {
		String Url = "http://google.com/x.xml";
		try { 
			mgr.inquire(Url);
		} catch (ManagerError error) {
			assertNotNull(error);
			assertEquals(error.getCause().getClass(), java.io.FileNotFoundException.class );
		}
		// this should be a test url at cytoscape perhaps
		Url = testUrl;
		try {
			assertNotNull(mgr.inquire(Url));
			assertEquals(mgr.inquire(Url).size(), 6);
		} catch (ManagerError error) {
			error.printStackTrace();
		}
	
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#register(cytoscape.plugin.CytoscapePlugin, java.lang.String)}.
	 */
	public void testRegister() {
		// can't test this without a real plugin but can't create a plugin w/o cytoscape being started up?
		//fail("Not yet implemented, can't create a plugin w/o full Cytoscape");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#install()}.
	 */
	public void testInstall() throws Exception {
		print("testInstall");
		List<PluginInfo> Plugins = mgr.inquire(testUrl);
		File Downloaded = mgr.download(Plugins.get(0));
		assertTrue(Downloaded.exists());

		assertEquals(mgr.getPlugins(PluginStatus.INSTALL).size(), 1);
		
		mgr.install();
		List<PluginInfo> Current = mgr.getPlugins(PluginStatus.CURRENT);
		assertEquals(mgr.getPlugins(PluginStatus.INSTALL).size(), 0);
		assertEquals(mgr.getPlugins(PluginStatus.DELETE).size(), 0);
		assertEquals(Current.size(), 1);

		assertTrue( (new File(Current.get(0).getFileList().get(0)).exists()) );
		
		Downloaded.delete();
		assertFalse(Downloaded.exists());
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#delete(cytoscape.plugin.PluginInfo)}.
	 */
/*	public void testDeletePluginInfo() throws Exception{
		print("testDeletePluginInfo");
		List<PluginInfo> Plugins = mgr.inquire(testUrl);
		File Downloaded = mgr.download(Plugins.get(0));
		assertTrue(Downloaded.exists());

		assertEquals(mgr.getPlugins(PluginStatus.INSTALL).size(), 1);
		
		mgr.install();
		List<PluginInfo> Current = mgr.getPlugins(PluginStatus.CURRENT);
		for(PluginInfo i: Current) {
			print(i.getName());
		}
			
		assertEquals(mgr.getPlugins(PluginStatus.INSTALL).size(), 0);
		assertEquals(mgr.getPlugins(PluginStatus.DELETE).size(), 0);
		assertEquals(Current.size(), 1);

		File InstalledPlugin = new File(Current.get(0).getFileList().get(0));
		System.out.println(InstalledPlugin.getAbsolutePath());
		assertTrue( InstalledPlugin.exists() );
		
		mgr.delete(Plugins.get(0));
		assertEquals(mgr.getPlugins(PluginStatus.DELETE).size(), 1);
		
		mgr.delete();
		assertFalse( InstalledPlugin.exists() );
	}*/

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#delete()}.
	 */
	public void testDelete() {
	//	fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#findUpdates(cytoscape.plugin.PluginInfo)}.
	 */
	public void testFindUpdates() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#update(cytoscape.plugin.PluginInfo, cytoscape.plugin.PluginInfo)}.
	 */
	public void testUpdate() {
	//	fail("Not yet implemented");
	}

	/**
	 * Test method for {@link cytoscape.plugin.PluginManager#download(cytoscape.plugin.PluginInfo)}.
	 */
	public void testDownload() {
		//fail("Not yet implemented");
	}

	private PluginInfo getInfoObj() {
		PluginInfo infoObj = new PluginInfo("123");
		infoObj.setName("myTest");
		infoObj.setCategory("Test");
		infoObj.setCytoscapeVersion("2.5");
		infoObj.setPluginClassName("0.2");
		infoObj.setProjectUrl("http://test.com/x.xml");
		infoObj.setFiletype(PluginInfo.FileType.JAR);
		return infoObj;
	}
	
	// this won't work causes ExceptionInitializerError in the CytoscapePlugin
	private class MyPlugin extends CytoscapePlugin {
		public MyPlugin() {
			System.out.println("MyPlugin instantiated");
		}
		
		public PluginInfo getPluginInfoObj() {
			PluginInfo Info = new PluginInfo();
			Info.setName("myPlugin");
			Info.setDescription("None");
			Info.setPluginVersion("1.2.3");
			Info.setCytoscapeVersion("2.5");
			Info.setCategory("Test");
			return Info;
		}
		
	}

	
}
