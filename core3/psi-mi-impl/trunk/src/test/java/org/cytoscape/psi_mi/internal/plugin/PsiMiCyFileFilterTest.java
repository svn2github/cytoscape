package org.cytoscape.psi_mi.internal.plugin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.DataCategory;
import org.junit.Before;
import org.junit.Test;

public class PsiMiCyFileFilterTest {
	private CyFileFilter filter;

	@Before
	public void setUp() {
		filter = new PsiMiCyFileFilter("PSI");
	}
	
	@Test
	public void testAcceptPsiMi1() throws Exception {
		File file = new File("src/test/resources/testData/psi_sample1.xml");
		assertTrue(filter.accept(new FileInputStream(file), DataCategory.NETWORK));
		assertTrue(filter.accept(file.toURI(), DataCategory.NETWORK));
	}
	
	@Test
	public void testAcceptPsiMi25() throws Exception {
		File file = new File("src/test/resources/testData/psi_sample_2_5_2.xml");
		assertTrue(filter.accept(new FileInputStream(file), DataCategory.NETWORK));
		assertTrue(filter.accept(file.toURI(), DataCategory.NETWORK));
	}
	
	@Test
	public void testAcceptRandomXml() throws Exception {
		File file = new File("src/test/resources/testData/galFiltered.xgmml");
		assertFalse(filter.accept(new FileInputStream(file), DataCategory.NETWORK));
		assertFalse(filter.accept(file.toURI(), DataCategory.NETWORK));
	}
}
