package org.cytoscape.io.internal.read.bookmarks;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.io.internal.read.AbstractPropertyReader;

public class BookmarkReader extends AbstractPropertyReader {

	private static final String BOOKMARK_PACKAGE = Bookmarks.class.getPackage().getName();

	public BookmarkReader(InputStream is) {
		super(is);
	}

	public void run(TaskMonitor tm) throws Exception {

		final JAXBContext jaxbContext = JAXBContext.newInstance(BOOKMARK_PACKAGE);

		final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

		propertyObject = (Bookmarks) unmarshaller.unmarshal(inputStream);
	}
}
