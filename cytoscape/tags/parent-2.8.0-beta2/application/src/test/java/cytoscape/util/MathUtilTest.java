package cytoscape.util;


import junit.framework.*;


public class MathUtilTest extends TestCase {
	public void testGetExponent() {
		assertEquals(-127, MathUtil.getExponent(0.0f));
		assertEquals(0, MathUtil.getExponent(1.0f));
		assertEquals(-1, MathUtil.getExponent(0.5f));
		assertEquals(-127, MathUtil.getExponent(Float.MIN_VALUE));
		assertEquals(127, MathUtil.getExponent(Float.MAX_VALUE));
	}

	public void testAlmostEqual() {
		assertTrue(MathUtil.almostEqual(1.2345615f, 1.234562f));
		assertTrue(MathUtil.almostEqual(1.2345615e30f, 1.234562e30f));
		assertTrue(MathUtil.almostEqual(0.0f, 0.0f));
		assertTrue(MathUtil.almostEqual(3.3f, 3.3f));
		assertFalse(MathUtil.almostEqual(0.0f, 0.00000001f));
		assertFalse(MathUtil.almostEqual(-0.0000001f, +0.0000001f));
	}
}