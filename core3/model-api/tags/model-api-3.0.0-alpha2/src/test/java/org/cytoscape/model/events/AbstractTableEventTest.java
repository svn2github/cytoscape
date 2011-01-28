package org.cytoscape.model.events;


import org.cytoscape.model.CyTable;

import static org.junit.Assert.*;
import org.junit.Test;

import static org.mockito.Mockito.*;


public class AbstractTableEventTest {
	@Test
	public final void testGetTable() {
		final CyTable table = mock(CyTable.class);
		final AbstractTableEvent event = new AbstractTableEvent(this, Object.class, table);
		assertEquals("Table returned by getTable() is *not* the one passed into the constructor!",
			     table, event.getTable());
	}

	@Test
	public final void testNullTableConstructorFailure() {
		try {
			final AbstractTableEvent event = new AbstractTableEvent(this, Object.class, null);
		} catch (final NullPointerException e) {
			return;
		}

		fail("The expected NullPointerException was *not* triggered!");
	}
}