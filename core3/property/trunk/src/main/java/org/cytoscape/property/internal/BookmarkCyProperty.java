package org.cytoscape.property.internal;

import java.io.IOException;
import java.io.OutputStream;

import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;

public class BookmarkCyProperty implements CyProperty<Bookmarks> {

	private Bookmarks bookmarks;
	
	public BookmarkCyProperty(Bookmarks bookmarks) {
		this.bookmarks = bookmarks;
		
		System.out.println("============= Bookmark injection is OK==================");
	}

	public Bookmarks getProperties() {
		return bookmarks;
	}

	public void store(OutputStream os) throws IOException {
		// TODO Auto-generated method stub

	}

}
