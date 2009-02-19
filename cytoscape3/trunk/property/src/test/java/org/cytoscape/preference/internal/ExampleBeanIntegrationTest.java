package org.cytoscape.preference.internal;

import org.cytoscape.properties.bookmark.Bookmarks;
import org.cytoscape.property.internal.BookmarkCyProperty;
import org.cytoscape.property.internal.bookmark.BookmarkFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Integration test the bundle locally (outside of OSGi). Use AbstractOsgiTests
 * and a separate integration test project for testing inside of OSGi.
 */
public class ExampleBeanIntegrationTest extends
		AbstractDependencyInjectionSpringContextTests {
	private BookmarkFactory bookmarkFactory;

	protected String[] getConfigLocations() {
		return new String[] { "META-INF/spring/bundle-context.xml" };
	}

	public void setBookmarkFactory(BookmarkFactory bookmarkFactory) {
		this.bookmarkFactory = bookmarkFactory;
	}

	public void testCreateProperty() {
		System.out
				.println("============= Testing factory bean ==================");

		BookmarkCyProperty prop = bookmarkFactory.createProperty();
		Bookmarks data = prop.getProperties();

		assertNotNull(data);

	}
}
