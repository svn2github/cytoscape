package cytoscape.util;

import java.util.ArrayList;
import java.util.List;

import cytoscape.bookmarks.Category;
import cytoscape.bookmarks.DataSource;

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
	public static Category getCategory(String categoryName,
			List<Category> categoryList) {
		Category result = null;

		for (Category cat : categoryList) {
			if (cat.getName().equals(categoryName)) {
				result = cat;
				break;
			} else {
				List<Category> subCategories = extractCategory(cat);
				if (subCategories.size() != 0 && result == null) {
					result = getCategory(categoryName, subCategories);
				}
			}
			if (result != null) {
				break;
			}
		}
		return result;
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

}
