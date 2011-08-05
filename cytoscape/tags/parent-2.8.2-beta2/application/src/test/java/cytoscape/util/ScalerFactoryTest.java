package cytoscape.util;


import junit.framework.*;


public class ScalerFactoryTest extends TestCase {
	public void testGetKnownScaler() {
		final Scaler linearScaler = ScalerFactory.getScaler("linear");
		assertTrue(linearScaler instanceof LinearScaler);

		final Scaler rankScaler = ScalerFactory.getScaler("rank");
		assertTrue(rankScaler instanceof RankScaler);
	}

	public void testGetUnknownScaler() {
		try {
			final Scaler scaler = ScalerFactory.getScaler("random junk!#$");
			fail();
		} catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
	}
}