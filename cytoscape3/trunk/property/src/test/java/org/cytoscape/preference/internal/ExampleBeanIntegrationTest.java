package org.cytoscape.preference.internal;

import org.cytoscape.properties.bookmark.Bookmarks;
import org.cytoscape.property.BookmarkCyProperty;
import org.cytoscape.property.ExampleBean;
import org.cytoscape.property.internal.bookmark.BookmarkFactory;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Integration test the bundle locally (outside of OSGi). Use AbstractOsgiTests
 * and a separate integration test project for testing inside of OSGi.
 */
public class ExampleBeanIntegrationTest extends
		AbstractDependencyInjectionSpringContextTests {
	private ExampleBean myBean;
	private BookmarkFactory bookmarkFactory;
	
	
	
	protected String[] getConfigLocations() {
		return new String[] { "META-INF/spring/bundle-context.xml" };
	}

	public void setBean(ExampleBean bean) {
		this.myBean = bean;
	}
	
	public void setBookmarkFactory(BookmarkFactory bookmarkFactory) {
		this.bookmarkFactory = bookmarkFactory;
	}

	
	
	public void testBeanIsABean() {
		assertTrue(this.myBean.isABean());
	}
	
	public void testCreateProperty() {
		System.out.println("============= Testing factory bean ==================");
		
		BookmarkCyProperty prop = bookmarkFactory.createProperty();
		Bookmarks data = prop.getProperties();
		
		assertNotNull(data);
		
		
	}
}
