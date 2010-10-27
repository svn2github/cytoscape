package org.cytoscape.property.internal.bookmark;

import java.io.InputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookmarkReader implements CyProperty<Bookmarks> {

	private static final String BOOKMARK_PACKAGE = Bookmarks.class.getPackage().getName();
	private static final Logger logger = LoggerFactory.getLogger(BookmarkReader.class);

	private Bookmarks bookmarks;

	/**
	 * Creates a new BookmarkReader object.
	 */
	public BookmarkReader(String resourceLocation) {
		
		InputStream is = null;

		try {
			if ( resourceLocation == null )
				throw new NullPointerException("resourceLocation is null");

			is = this.getClass().getClassLoader().getResourceAsStream(resourceLocation);

			if (is == null)
				throw new IllegalArgumentException("Failed to open resource: " + resourceLocation);

			final JAXBContext jaxbContext = JAXBContext.newInstance(BOOKMARK_PACKAGE);

			final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

			bookmarks = (Bookmarks) unmarshaller.unmarshal(is);
		} catch (Exception e) {
			logger.warn("Could not read bookmark file - using empty bookmarks.", e);
			bookmarks = new Bookmarks();
		} finally {
			if (is != null) {
				try { is.close(); } catch (IOException ioe) {}
				is = null;
			}
		}
	}

	public Bookmarks getProperties() {
		return bookmarks;
	}
}
