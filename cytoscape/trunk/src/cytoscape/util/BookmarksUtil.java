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

import cytoscape.bookmarks.Attribute;
import cytoscape.bookmarks.Bookmarks;
import cytoscape.bookmarks.Category;
import cytoscape.bookmarks.DataSource;

import cytoscape.data.readers.BookmarkReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


/**
 * Utility methods for getting entries in the bookmark object.
 *
 * @author kono
 *
 */
public abstract class BookmarksUtil {
	/**
	 * Traverse bookmark tree and get a list of data sources from the specified category.
	 *
	 * @param categoryName
	 * @return
	 */
	public static List<DataSource> getDataSourceList(String categoryName,
	                                                 List<Category> categoryList) {
		Category targetCat = getCategory(categoryName, categoryList);

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
	public static Category getCategory(String categoryName, List<Category> categoryList) {
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
	 *  DOCUMENT ME!
	 *
	 * @param source DOCUMENT ME!
	 * @param attrName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static String getAttribute(DataSource source, String attrName) {
		List<Attribute> attrs = source.getAttribute();

		for (Attribute attr : attrs) {
			if (attrName.equals(attr.getName())) {
				return attr.getContent();
			}
		}

		return null;
	}

	private static List<DataSource> extractDataSources(Category cat) {
		final List<Object> entries = cat.getCategoryOrDataSource();
		final List<DataSource> datasourceList = new ArrayList<DataSource>();

		for (Object obj : entries) {
			if (obj.getClass() == DataSource.class) {
				datasourceList.add((DataSource) obj);
			}
		}

		return datasourceList;
	}

	private static List<Category> extractCategory(Category cat) {
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
	 *  DOCUMENT ME!
	 *
	 * @param bookmarkUrl DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 *
	 * @throws JAXBException DOCUMENT ME!
	 * @throws IOException DOCUMENT ME!
	 */
	public static Bookmarks getBookmarks(URL bookmarkUrl) throws JAXBException, IOException {
		BookmarkReader reader = new BookmarkReader();
		reader.readBookmarks(bookmarkUrl);

		return reader.getBookmarks();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarkFile DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Bookmarks getBookmarks(java.io.File pBookmarkFile) {
		Bookmarks theBookmarks = null;

		// Load the Bookmarks object from given xml file  
		try {
			theBookmarks = BookmarksUtil.getBookmarks(pBookmarkFile.toURL());
		} catch (IOException e) {
			System.out.println("Can not read the bookmark file, the bookmark file may not exist!");
		} catch (JAXBException e) {
			System.out.println("JAXBException -- bookmarkSource");
		}

		return theBookmarks;
	}

	private static String bookmarkPackageName = "cytoscape.bookmarks";

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 * @param pFos DOCUMENT ME!
	 *
	 * @throws JAXBException DOCUMENT ME!
	 * @throws IOException DOCUMENT ME!
	 */
	public static void saveBookmark(Bookmarks pBookmarks, String pCategoryName,
	                                DataSource pDataSource, FileOutputStream pFos)
	    throws JAXBException, IOException {
		List<Category> theCategoryList = pBookmarks.getCategory();

		// if the category does not exist, create it
		if (theCategoryList.size() == 0) {
			Category theCategory = new Category();
			theCategory.setName(pCategoryName);
			theCategoryList.add(theCategory);
		}

		Category theCategory = getCategory(pCategoryName, theCategoryList);

		List<Object> theObjList = theCategory.getCategoryOrDataSource();

		theObjList.add(pDataSource);

		// Write the bookmarks objects back into a file
		JAXBContext jc = JAXBContext.newInstance(bookmarkPackageName);
		Marshaller m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		m.marshal(pBookmarks, pFos);
	}

	// Save one bookmark (DataSource object) belonged to specified category into a XML file 
	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarkURL DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean saveBookmark(URL pBookmarkURL, String pCategoryName,
	                                   DataSource pDataSource) {
		Bookmarks theBookmarks = null;

		try {
			theBookmarks = BookmarksUtil.getBookmarks(pBookmarkURL);
		} catch (Exception e) {
			theBookmarks = new Bookmarks();
		}

		java.io.File tmpFile = new java.io.File(pBookmarkURL.getFile());

		if (!tmpFile.exists()) {
			try {
				tmpFile.createNewFile();
			} catch (Exception ex) {
				System.out.println("Bookmark file may not exist, failed to create new one.");
			}
		}

		try {
			FileOutputStream fos = new FileOutputStream(pBookmarkURL.getFile());
			saveBookmark(theBookmarks, pCategoryName, pDataSource, fos);
			fos.close();
		} catch (JAXBException e) {
			e.printStackTrace();

			return false;
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}

	/**
	 *  Write the bookmarks object into a file
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param pFile DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean saveBookmark(Bookmarks pBookmarks, File pFile) {
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(pFile);
			saveBookmark(pBookmarks,fos);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	/**
	 *  Write the bookmarks object into an outputstream
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param os DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean saveBookmark(Bookmarks pBookmarks, OutputStream os) {
		try {
			JAXBContext jc = JAXBContext.newInstance(bookmarkPackageName);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(pBookmarks, os);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} 
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 */
	public static void saveBookmark(Bookmarks pBookmarks, String pCategoryName,
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
	 *  DOCUMENT ME!
	 *
	 * @param filename DOCUMENT ME!
	 * @param pBookmarks DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean deleteBookmark(String filename, Bookmarks pBookmarks,
	                                     String pCategoryName, DataSource pDataSource) {
		List<Category> theCategoryList = pBookmarks.getCategory();
		Category theCategory = getCategory(pCategoryName, theCategoryList);

		List<Object> theObjList = theCategory.getCategoryOrDataSource();

		for (int i = 0; i < theObjList.size(); i++) {
			Object obj = theObjList.get(i);

			if (obj instanceof DataSource) {
				DataSource theDataSource = (DataSource) obj;

				if (theDataSource.getName().equalsIgnoreCase(pDataSource.getName())) {
					theObjList.remove(i);

					try {
						// Write the bookmarks objects back into a file
						JAXBContext jc = JAXBContext.newInstance(bookmarkPackageName);
						Marshaller m = jc.createMarshaller();
						m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

						m.marshal(pBookmarks, new FileOutputStream(filename));
					} catch (Exception e) {
						e.printStackTrace();
					}

					return true;
				}
			}
		}

		return false;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean deleteBookmark(Bookmarks pBookmarks, String pCategoryName,
	                                     DataSource pDataSource) {
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

				if (theDataSource.getName().equalsIgnoreCase(pDataSource.getName())) {
					theObjList.remove(i);
				}
			}
		}

		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarks DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean isInBookmarks(Bookmarks pBookmarks, String pCategoryName,
	                                    DataSource pDataSource) {
		if (pBookmarks == null) {
			return false;
		}

		List<DataSource> theDataSources = getDataSourceList(pCategoryName, pBookmarks.getCategory());

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

	/**
	 *  DOCUMENT ME!
	 *
	 * @param pBookmarkURL DOCUMENT ME!
	 * @param pCategoryName DOCUMENT ME!
	 * @param pDataSource DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static boolean isInBookmarks(URL pBookmarkURL, String pCategoryName,
	                                    DataSource pDataSource) {
		Bookmarks theBookmarks = null;

		try {
			theBookmarks = getBookmarks(pBookmarkURL);
		} catch (Exception e) {
			return false;
		}

		return isInBookmarks(theBookmarks, pCategoryName, pDataSource);
	}
}
