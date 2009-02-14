package org.cytoscape.property.internal.bookmark;

import java.io.IOException;

import org.cytoscape.property.BookmarkCyProperty;
import org.cytoscape.property.PropertyFactory;

public class BookmarkFactory implements PropertyFactory<BookmarkCyProperty> {

	private String resourceLocation;
	private BookmarkReader reader;
	
	
	public BookmarkFactory(String bookmarkLocation) throws IOException {
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
