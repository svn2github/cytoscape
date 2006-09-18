package cytoscape.util.unitTests;

import java.util.List;

import junit.framework.TestCase;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;
import cytoscape.bookmarks.DataSource;
import cytoscape.data.readers.BookmarkReader;
import cytoscape.util.BookmarksUtil;

public class BookmarksUtilTest extends TestCase {

	private Bookmarks bk;

	protected void setUp() throws Exception {
		super.setUp();
		BookmarkReader reader = new BookmarkReader();
		reader.readBookmarks();

		bk = reader.getBookmarks();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	

	public void testGetCategory() {
		List<Category> rootCat = bk.getCategory();
		assertNotNull(rootCat);
		System.out.println("#### Number of bookmarks in the root category = " + rootCat.size());

		Category cat1 = BookmarksUtil.getCategory("annotation", rootCat);
		assertNotNull(cat1);
		assertEquals("annotation", cat1.getName());
		System.out.println("===============================================");
		
		Category cat2 = BookmarksUtil.getCategory("network", rootCat);
		assertNotNull(cat2);
		assertEquals("network", cat2.getName());
		System.out.println("===============================================");
		
		Category cat4 = BookmarksUtil.getCategory("aaa", rootCat);
		assertNull(cat4);

	}
	
	public void testGetDataSourceList() {
		List<Category> rootCat = bk.getCategory();
		List<DataSource> sources = BookmarksUtil.getDataSourceList("ontology", bk.getCategory());
		
		assertNotNull(sources);
		assertEquals(5, sources.size());
		
		List<DataSource> sources2 = BookmarksUtil.getDataSourceList("annotation", bk.getCategory());
		assertNotNull(sources2);
		assertEquals(33, sources2.size());
		
		List<DataSource> sources3 = BookmarksUtil.getDataSourceList("network", bk.getCategory());
		assertNotNull(sources3);
		assertEquals(1, sources3.size());
	}

}
