package org.cytoscape.venneuler;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;


import org.dishevelled.venn.VennModel;
import org.dishevelled.venn.VennLayouter;
import org.dishevelled.venn.VennLayouter.PerformanceHint;
import static org.dishevelled.venn.VennLayouter.PerformanceHint.*;
import org.dishevelled.venn.VennLayout;
import java.awt.Rectangle;


public class VennEulerLayouterTest {

	VennEulerLayouter layouter;

	@Before
	public void setup() {
		layouter = new VennEulerLayouter();
	}

	@Test(expected=NullPointerException.class)
	public void testLayoutNullModel() {
		VennLayout vl = layouter.layout( null, new Rectangle(100,200), OPTIMIZE_FOR_SPEED);
	}

	@Test(expected=NullPointerException.class)
	public void testLayoutNullRect() {
		VennLayout vl = layouter.layout( new FakeVennModel( new int[]{1,2}, new int[]{2,3}), 
		                                 null, OPTIMIZE_FOR_CORRECTNESS);
	}

	@Test
	public void testLayoutNullPerfHint() {
		// we don't use performance hints, so null should be OK
		VennLayout vl = layouter.layout( new FakeVennModel( new int[]{1,2}, new int[]{2,3}), 
		                                 new Rectangle(100,200), null);
		assertNotNull(vl);
	}

	@Test
	public void testLayout() {
		VennLayout vl = layouter.layout( new FakeVennModel( new int[]{1,2}, new int[]{2,3}), 
		                                 new Rectangle(100,200), OPTIMIZE_FOR_SPEED);
		assertNotNull(vl);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testLayoutModelTooSmall() {
		VennLayout vl = layouter.layout( new FakeVennModel(), 
		                                 new Rectangle(100,200), OPTIMIZE_FOR_SPEED);
	}
}
