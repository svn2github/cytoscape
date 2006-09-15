package cytoscape.data.readers.unitTests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;
import cytoscape.data.readers.BookmarkReader;
import junit.framework.TestCase;

/**
 * Unit test fopr the bookmark reader.
 * 
 * @author kono
 *
 */
public class BookmarkReaderTest extends TestCase {

	private BookmarkReader reader;
	
	protected void setUp() throws Exception {
		super.setUp();
		reader = new BookmarkReader();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		reader = null;
	}

	public void testReadBookmarks() {
		try {
			reader.readBookmarks();
			
			Bookmarks bkm = reader.getBookmarks();
			List cat = bkm.getCategory();
			List<String> nameList = new ArrayList<String>();
			for(Object item: cat) {
				if(item.getClass() == Category.class) {
					nameList.add(((Category)item).getName());
				}
			}
			assertTrue(nameList.contains("ontology"));
			assertTrue(nameList.contains("network"));
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
