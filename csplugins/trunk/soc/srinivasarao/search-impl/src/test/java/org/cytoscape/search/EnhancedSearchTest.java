package org.cytoscape.search;

import org.cytoscape.search.internal.EnhancedSearchFactoryImpl;

public class EnhancedSearchTest extends AbstractEnhancedSearchTest{

		public void setUp() {
			EnhancedSearchFactoryImpl esf = new EnhancedSearchFactoryImpl();
			es = esf.getGlobalEnhancedSearchInstance();
		}
		
		public void tearDown(){
			es = null;
		}
		
}
