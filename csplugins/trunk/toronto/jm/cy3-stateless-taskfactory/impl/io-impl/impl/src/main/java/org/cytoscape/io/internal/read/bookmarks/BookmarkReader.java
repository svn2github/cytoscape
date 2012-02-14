package org.cytoscape.io.internal.read.bookmarks;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.io.internal.read.AbstractPropertyReader;
import org.cytoscape.io.read.InputStreamTaskContext;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.work.TaskMonitor;

public class BookmarkReader extends AbstractPropertyReader {

	private static final String BOOKMARK_PACKAGE = Bookmarks.class.getPackage().getName();

	public BookmarkReader(InputStreamTaskContext context) {
		super(context.getInputStream());
	}

	public void run(TaskMonitor tm) throws Exception {
		tm.setProgress(0.0);
		final JAXBContext jaxbContext = JAXBContext.newInstance(BOOKMARK_PACKAGE, getClass().getClassLoader());
		tm.setProgress(0.1);
		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		tm.setProgress(0.5);
		propertyObject = (Bookmarks) unmarshaller.unmarshal(inputStream);
		tm.setProgress(1.0);
	}
}
