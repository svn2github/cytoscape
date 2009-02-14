/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.util;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.properties.bookmark.Attribute;
import org.cytoscape.properties.bookmark.Bookmarks;
import org.cytoscape.properties.bookmark.Category;
import org.cytoscape.properties.bookmark.DataSource;

/**
 * Utility methods for getting entries in the bookmark object.
 * 
 * @author kono
 * 
 */
public class BookmarksUtil {
	/**
	 * Traverse bookmark tree and get a list of data sources from the specified
	 * category.
	 * 
	 * @param categoryName
	 * @return
	 */
	public List<DataSource> getDataSourceList(String categoryName,
			List<Category> categoryList) {
		final Category targetCat = getCategory(categoryName, categoryList);

		if (targetCat != null) {
			return extractDataSources(targetCat);
		} else {
			return null;
		}
	}

	/**
	 * Select specific category from a list of categories.
	 * 
	 * @param categoryName
	 * @param categoryList
	 * @return
	 */
	public Category getCategory(String categoryName,
			List<Category> categoryList) {
		Category result = null;

		for (Category cat : categoryList) {
			if (cat.getName().equals(categoryName)) {
				result = cat;

				break;
			} else {
				List<Category> subCategories = extractCategory(cat);

				if ((subCategories.size() != 0) && (result == null)) {
					result = getCategory(categoryName, subCategories);
				}
			}

			if (result != null) {
				break;
			}
		}

		return result;
	}

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
	public String getAttribute(DataSource source, String attrName) {
		List<Attribute> attrs = source.getAttribute();

		for (Attribute attr : attrs) {
			if (attrName.equals(attr.getName())) {
				return attr.getContent();
			}
		}

		return null;
	}

	private List<DataSource> extractDataSources(Category cat) {
		final List<Object> entries = cat.getCategoryOrDataSource();
		final List<DataSource> datasourceList = new ArrayList<DataSource>();

		for (Object obj : entries) {
			if (obj.getClass() == DataSource.class) {
				datasourceList.add((DataSource) obj);
			}
		}

		return datasourceList;
	}

	private List<Category> extractCategory(Category cat) {
		final List<Object> entries = cat.getCategoryOrDataSource();
		final List<Category> categoryList = new ArrayList<Category>();

		for (Object obj : entries) {
			if (obj.getClass() == Category.class) {
				categoryList.add((Category) obj);
			}
		}

		return categoryList;
	}

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
			DataSource pDataSource) {
		if (pBookmarks == null) {
			pBookmarks = new Bookmarks();
		}

		List<Category> theCategoryList = pBookmarks.getCategory();

		// if the category does not exist, create it
		if (theCategoryList.size() == 0) {
			Category theCategory = new Category();
			theCategory.setName(pCategoryName);
			theCategoryList.add(theCategory);
		}

		Category theCategory = getCategory(pCategoryName, theCategoryList);

		if (theCategory == null) {
			Category newCategory = new Category();
			newCategory.setName(pCategoryName);
			theCategoryList.add(newCategory);
			theCategory = newCategory;
		}

		List<Object> theObjList = theCategory.getCategoryOrDataSource();

		theObjList.add(pDataSource);
	}

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
	public boolean deleteBookmark(Bookmarks pBookmarks,
			String pCategoryName, DataSource pDataSource) {
		if (!isInBookmarks(pBookmarks, pCategoryName, pDataSource)) {
			return false;
		}

		List<Category> theCategoryList = pBookmarks.getCategory();
		Category theCategory = getCategory(pCategoryName, theCategoryList);

		List<Object> theObjList = theCategory.getCategoryOrDataSource();

		for (int i = 0; i < theObjList.size(); i++) {
			Object obj = theObjList.get(i);

			if (obj instanceof DataSource) {
				DataSource theDataSource = (DataSource) obj;

				if (theDataSource.getName().equalsIgnoreCase(
						pDataSource.getName())) {
					theObjList.remove(i);
				}
			}
		}

		return true;
	}

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
	public boolean isInBookmarks(Bookmarks pBookmarks,
			String pCategoryName, DataSource pDataSource) {
		if (pBookmarks == null) {
			return false;
		}

		List<DataSource> theDataSources = getDataSourceList(pCategoryName,
				pBookmarks.getCategory());

		if ((theDataSources == null) || (theDataSources.size() == 0)) {
			return false;
		}

		for (DataSource theDataSource : theDataSources) {
			if (theDataSource.getName().equalsIgnoreCase(pDataSource.getName())) {
				return true;
			}
		}

		return false;
	}

}
