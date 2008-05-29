

package org.cytoscape.event;

import java.util.*;

public class MockListenerProvider implements ListenerProvider<MockCyEventListener> {

	List<MockCyEventListener> l;

	MockListenerProvider( int num ) {
		l = new LinkedList<MockCyEventListener>();
		for ( int i = 0; i < num; i++ ) 
			l.add( new MockCyEventListener() );
	}
	
	public List<MockCyEventListener> getListeners() {
		return l;	
	}
}
