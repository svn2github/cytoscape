package org.cytoscape.view.model;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

public abstract class AbstractViewTest<S> {

	protected VisualProperty<Integer> intVP = new IntegerVisualProperty("zero",Integer.valueOf(0),"intvp","INTVP");
	protected View<S> view;

	@Test
	public void testGetSetVisualProperty() {
		view.setVisualProperty(intVP,1);
		assertEquals("intVP value",1,view.getVisualProperty(intVP).intValue());
		view.setVisualProperty(intVP,5);
		assertEquals("intVP value",5,view.getVisualProperty(intVP).intValue());
		view.setVisualProperty(intVP,-12345);
		assertEquals("intVP value",-12345,view.getVisualProperty(intVP).intValue());

		// TODO improve unit test
	}

	@Test
	public void testSetLockedValue() {
		// TODO add unit test
	}

	@Test
	public void testIsValueLocked() {
		// TODO add unit test
	}

	@Test
    public void testClearValueLock() {
		// TODO add unit test
	}

	@Test
    public void testGetSource() {
		assertNotNull( view.getSource() );

		// how to test type of source?
	}

	@Test
    public void testAddViewChangeListener() {
		ViewChangeListener mock = createMock(ViewChangeListener.class);
		mock.visualPropertySet(intVP,5);
		replay(mock);

		view.addViewChangeListener( mock );
		view.setVisualProperty(intVP,5);

		verify(mock);
	}

	@Test
    public void testRemoveViewChangeListener() {
		ViewChangeListener mock = createMock(ViewChangeListener.class);
		mock.visualPropertySet(intVP,5);
		replay(mock);

		view.addViewChangeListener( mock );
		view.setVisualProperty(intVP,5);

		view.removeViewChangeListener( mock );
		view.setVisualProperty(intVP,10);

		verify(mock);
	}
}

