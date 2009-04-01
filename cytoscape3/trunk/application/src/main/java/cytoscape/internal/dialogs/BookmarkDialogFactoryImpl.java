package cytoscape.internal.dialogs;

import java.awt.Frame;

import org.cytoscape.property.CyProperty;
import org.cytoscape.property.bookmark.Bookmarks;
import org.cytoscape.property.bookmark.BookmarksUtil;

import cytoscape.dialogs.BookmarkDialog;
import cytoscape.dialogs.BookmarkDialogFactory;

public class BookmarkDialogFactoryImpl implements BookmarkDialogFactory {

	private CyProperty<Bookmarks> bookmarksProp;
	private BookmarksUtil bkUtil;

	public BookmarkDialogFactoryImpl(CyProperty<Bookmarks> bookmarksProp,
			BookmarksUtil bkUtil) {
		this.bookmarksProp = bookmarksProp;
		this.bkUtil = bkUtil;
	}

	public BookmarkDialog getBookamrkDialog(Frame parent) {
		return new BookmarkDialogImpl(parent, bookmarksProp.getProperties(),
				bkUtil);
	}
}
