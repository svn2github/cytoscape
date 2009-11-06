package org.cytoscape.property.internal.bookmark;

import java.io.IOException;

import org.cytoscape.property.PropertyFactory;
import org.cytoscape.property.internal.BookmarkCyProperty;

public class BookmarkFactory implements PropertyFactory<BookmarkCyProperty> {

	private String resourceLocation;
	private BookmarkReader reader;
	
	
	public BookmarkFactory(final String bookmarkLocation) throws IOException {
		this.resourceLocation = bookmarkLocation;
		reader = new BookmarkReader(bookmarkLocation);
		reader.read();
	}
	
	
	public BookmarkCyProperty createProperty() {
		return new BookmarkCyProperty(reader.getProperties());
	}

	public void setResourceLocation(String location) {
		this.resourceLocation = location;
		
	}

}
