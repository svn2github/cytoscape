package org.cytoscape.property.bookmark;

import java.util.List;

import org.cytoscape.properties.bookmark.Bookmarks;
import org.cytoscape.properties.bookmark.Category;
import org.cytoscape.properties.bookmark.DataSource;

public interface BookmarksUtil {

	/**
	 * Traverse bookmark tree and get a list of data sources from the specified
	 * category.
	 * 
	 * @param categoryName
	 * @return
	 */
	public List<DataSource> getDataSourceList(String categoryName,
			List<Category> categoryList);

	/**
	 * Select specific category from a list of categories.
	 * 
	 * @param categoryName
	 * @param categoryList
	 * @return
	 */
	public Category getCategory(String categoryName, List<Category> categoryList);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param source
	 *            DOCUMENT ME!
	 * @param attrName
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getAttribute(DataSource source, String attrName);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pBookmarks
	 *            DOCUMENT ME!
	 * @param pCategoryName
	 *            DOCUMENT ME!
	 * @param pDataSource
	 *            DOCUMENT ME!
	 */
	public void saveBookmark(Bookmarks pBookmarks, String pCategoryName,
			DataSource pDataSource);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pBookmarks
	 *            DOCUMENT ME!
	 * @param pCategoryName
	 *            DOCUMENT ME!
	 * @param pDataSource
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean deleteBookmark(Bookmarks pBookmarks, String pCategoryName,
			DataSource pDataSource);

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pBookmarks
	 *            DOCUMENT ME!
	 * @param pCategoryName
	 *            DOCUMENT ME!
	 * @param pDataSource
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public boolean isInBookmarks(Bookmarks pBookmarks, String pCategoryName,
			DataSource pDataSource);

}