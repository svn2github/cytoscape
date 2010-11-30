package cytoscape.plugin;

import cytoscape.*;
import cytoscape.dialogs.plugins.PluginIndex;
import cytoscape.dialogs.plugins.PluginManageDialog;
import cytoscape.plugin.PluginInfo;

import java.io.*;
import java.net.URLClassLoader;
import java.util.*;
import junit.framework.TestCase;


/**
 * @author pwang
 */
//@SuppressWarnings({"JavadocReference"})
public class PluginIndexTest extends TestCase {
	private Vector allPluginVector1;
	
	private String siteName1 = "Cytoscape";
	private String siteName2 = "OtherSite";
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {

		allPluginVector1 = new Vector();	
				
		Vector pluginVect1 = new Vector();
		PluginInfo info1  = new PluginInfo();
		info1.setDescription("This is MiMI plugin");
		pluginVect1.add(PluginManageDialog.CURRENTLY_INSTALLED);					
		pluginVect1.add("categoty1");// category
		pluginVect1.add(info1);

		Vector pluginVect2 = new Vector();
		PluginInfo info2  = new PluginInfo();
		info2.setDescription("This is BioPax plugin");
		pluginVect2.add(PluginManageDialog.CURRENTLY_INSTALLED);					
		pluginVect2.add("categoty2");// category
		pluginVect2.add(info2);
				
		Vector pluginVect3 = new Vector();
		PluginInfo info3  = new PluginInfo();
		info3.setDescription("This is another");
		pluginVect3.add(PluginManageDialog.AVAILABLE_FOR_INSTALL);					
		pluginVect3.add("categoty3");// category
		pluginVect3.add(info3);
		
		allPluginVector1.add(pluginVect1);
		allPluginVector1.add(pluginVect2);
		allPluginVector1.add(pluginVect3);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() {

	}
	
	
	public void testGetSearchResult() {
		
		Vector  filteredPluginVector = new Vector();

		// 1. Before PluginIndex.getSearchResult(), null should be there
		try {
			filteredPluginVector = PluginIndex.getSearchResult("testStr",false, siteName1); 
		}
		catch(Exception e){}
		assertEquals(filteredPluginVector, null);	

		// execute setAllPluginVector() now, with boolean checkVersion = false
		try {
			PluginIndex.setAllPluginVector(allPluginVector1, false, siteName1);			
		}
		catch (Exception e){}

		// 2. After PluginIndex.setAllPluginVector(), if searchStr is not found, an empty vector should be returned 
		try {
			filteredPluginVector = PluginIndex.getSearchResult("abcd",false, siteName1); 						
		}
		catch (Exception e){}
		assertEquals(filteredPluginVector.size(), 0);

		// 3. After PluginIndex.setAllPluginVector(), if searchStr is found, it should be returned 
		try {
			filteredPluginVector = PluginIndex.getSearchResult("mimi",false, siteName1); 						
		}
		catch (Exception e){}
		assertEquals(filteredPluginVector.size(), 1);

		// 3a. Make sure the correct record is returned
		Vector returnedVect = (Vector)filteredPluginVector.elementAt(0);
		PluginInfo info = (PluginInfo) returnedVect.elementAt(2);
		assertEquals(info.getDescription(), "This is MiMI plugin");
		
		// 4. After PluginIndex.setAllPluginVector(), if searchStr is found, it should be returned 
		try {
			filteredPluginVector = PluginIndex.getSearchResult("plugin",false, siteName1); 			
		}
		catch (Exception e){}
		assertEquals(filteredPluginVector.size(), 2);
		
		// 5. if boolean versionCheck is set to true, null should be returned 
		try {
			filteredPluginVector = PluginIndex.getSearchResult("plugin",true, siteName1); 			
		}
		catch (Exception e){}
		assertEquals(filteredPluginVector, null);
		
		// 6. search otherSite, null should be returned 
		try {
			filteredPluginVector = PluginIndex.getSearchResult("plugin",false, siteName2); 			
		}
		catch (Exception e){}
		assertEquals(filteredPluginVector, null);
	}
}
