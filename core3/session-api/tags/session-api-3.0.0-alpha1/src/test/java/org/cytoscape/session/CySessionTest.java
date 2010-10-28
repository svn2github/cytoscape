package org.cytoscape.session;

import static org.junit.Assert.*;

import java.util.*;
import java.io.File;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.model.CyTable;
import org.cytoscape.session.CySession;
import org.cytoscape.property.session.Cysession;
import org.cytoscape.property.bookmark.Bookmarks;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

public class CySessionTest {


	protected CySession session;


	@Test
	public void testDefaultGetNetworkViews() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getNetworkViews());
	}

	@Test
	public void testSetNullNetworkViews() {
		session = new CySession.Builder().networkViews( null ).build();
		assertNotNull(session);
		assertNotNull(session.getNetworkViews());
		assertEquals(0,session.getNetworkViews().size());
	}

	@Test
	public void testSetNetworkViews() {
		CyNetworkView nv1 = mock(CyNetworkView.class); 
		CyNetworkView nv2 = mock(CyNetworkView.class); 

		Set<CyNetworkView> vs = new HashSet<CyNetworkView>();
		vs.add( nv1 );
		vs.add( nv2 );

		session = new CySession.Builder().networkViews( vs ).build();

		assertNotNull(session);
		assertNotNull(session.getNetworkViews());
		assertEquals(2,session.getNetworkViews().size());
		assertTrue(session.getNetworkViews().contains( nv1 ));
		assertTrue(session.getNetworkViews().contains( nv2 ));
	}

	@Test
	public void testDefaultGetTables() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getTables());
	}

	@Test
	public void testSetNullTables() {
		session = new CySession.Builder().tables( null ).build();
		assertNotNull(session);
		assertNotNull(session.getTables());
		assertEquals(0,session.getTables().size());
	}

	@Test
	public void testSetTables() {
		CyTable t1 = mock(CyTable.class); 
		CyTable t2 = mock(CyTable.class); 

		Set<CyTable> ts = new HashSet<CyTable>();
		ts.add( t1 );
		ts.add( t2 );

		session = new CySession.Builder().tables( ts ).build();

		assertNotNull(session);
		assertNotNull(session.getTables());
		assertEquals(2,session.getTables().size());
		assertTrue(session.getTables().contains( t1 ));
		assertTrue(session.getTables().contains( t2 ));
	}
	
	@Test
	public void testDefaultGetViewVisualStyleMap() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getViewVisualStyleMap());
	}

	@Test
	public void testSetNullViewVisualStyleMap() {
		session = new CySession.Builder().viewVisualStyleMap(null).build();
		assertNotNull(session);
		assertNotNull(session.getViewVisualStyleMap());
		assertEquals(0,session.getViewVisualStyleMap().size());
	}

	@Test
	public void testSetViewVisualStyleMap() {
		CyNetworkView nv1 = mock(CyNetworkView.class); 
		CyNetworkView nv2 = mock(CyNetworkView.class); 

		Map<CyNetworkView,String> vsm = new HashMap<CyNetworkView,String>();
		vsm.put(nv1,"vs1");
		vsm.put(nv2,"vs2");

		session = new CySession.Builder().viewVisualStyleMap(vsm).build();
		assertNotNull(session);
		assertNotNull(session.getViewVisualStyleMap());
		assertEquals(2,session.getViewVisualStyleMap().size());
		assertTrue(session.getViewVisualStyleMap().containsKey(nv1));
		assertTrue(session.getViewVisualStyleMap().containsKey(nv2));
		assertEquals("vs1",session.getViewVisualStyleMap().get(nv1));
		assertEquals("vs2",session.getViewVisualStyleMap().get(nv2));
	}
	
	@Test
	public void testDefaultGetCytoscapeProperties() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getCytoscapeProperties());
	}

	@Test
	public void testSetNullCytoscapeProperties() {
		session = new CySession.Builder().cytoscapeProperties(null).build();
		assertNotNull(session);
		assertNotNull(session.getCytoscapeProperties());
		assertEquals(0,session.getCytoscapeProperties().size());
	}

	@Test
	public void testSetCytoscapeProperties() {
		session = new CySession.Builder().cytoscapeProperties(getFakeProps()).build();
		assertNotNull(session);
		checkProps(session.getCytoscapeProperties());
	}

	@Test
	public void testDefaultGetVizmapProperties() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getVizmapProperties());
	}

	@Test
	public void testSetNullVizmapProperties() {
		session = new CySession.Builder().vizmapProperties(null).build();
		assertNotNull(session);
		assertNotNull(session.getVizmapProperties());
		assertEquals(0,session.getVizmapProperties().size());
	}

	@Test
	public void testSetVizmapProperties() {
		session = new CySession.Builder().vizmapProperties(getFakeProps()).build();
		assertNotNull(session);
		checkProps(session.getVizmapProperties());
	}

	@Test
	public void testDefaultGetBookmarks() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getBookmarks());
	}

	@Test
	public void testSetNullBookmarks() {
		session = new CySession.Builder().bookmarks(null).build();
		assertNotNull(session);
		assertNotNull(session.getBookmarks());
	}

	@Test
	public void testSetBookmarks() {
		Bookmarks b = mock(Bookmarks.class);
		session = new CySession.Builder().bookmarks(b).build();
		assertNotNull(session);
		assertNotNull(session.getBookmarks());
		assertEquals(b, session.getBookmarks());
	}

	@Test
	public void testDefaultGetSession() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getCysession());
	}

	@Test
	public void testSetNullSession() {
		session = new CySession.Builder().cysession(null).build();
		assertNotNull(session);
		assertNotNull(session.getCysession());
	}

	@Test
	public void testSetSession() {
		Cysession b = mock(Cysession.class);
		session = new CySession.Builder().cysession(b).build();
		assertNotNull(session);
		assertNotNull(session.getCysession());
		assertEquals(b, session.getCysession());
	}

	@Test
	public void testDefaultGetPluginFileListMap() {
		session = new CySession.Builder().build();
		assertNotNull(session);
		assertNotNull(session.getPluginFileListMap());
	}

	@Test
	public void testSetNullPluginFileListMap() {
		session = new CySession.Builder().pluginFileListMap(null).build();
		assertNotNull(session);
		assertNotNull(session.getPluginFileListMap());
		assertEquals(0,session.getPluginFileListMap().size());
	}

	@Test
	public void testSetPluginFileListMap() {
		File f1 = new File("f1");
		File f2 = new File("f2");

		List<File> l1 = new ArrayList<File>();
		l1.add(f1);
		l1.add(f2);

		File f3 = new File("f3");
		File f4 = new File("f4");

		List<File> l2 = new ArrayList<File>();
		l1.add(f3);
		l1.add(f4);

		Map<String,List<File>> pflm = new HashMap<String,List<File>>();
		pflm.put("plugin1",l1);
		pflm.put("plugin2",l2);

		session = new CySession.Builder().pluginFileListMap(pflm).build();
		assertNotNull(session);
		assertNotNull(session.getPluginFileListMap());
		assertEquals(2,session.getPluginFileListMap().size());
		assertEquals(l1,session.getPluginFileListMap().get("plugin1"));
		assertEquals(l2,session.getPluginFileListMap().get("plugin2"));
	}


	private void checkProps(Properties p) {
		assertNotNull(p);
		assertEquals(2,p.size());
		assertEquals("value1",p.getProperty("key1"));
		assertEquals("value2",p.getProperty("key2"));
	}

	private Properties getFakeProps() {
		Properties p = new Properties();
		p.setProperty("key1","value1");
		p.setProperty("key2","value2");
		return p;
	}
}
