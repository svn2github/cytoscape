
package org.cytoscape.command.internal.tunables;



import org.cytoscape.work.TunableHandler;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.AbstractTunableHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class DummyTunableHandler extends AbstractTunableHandler {

	public DummyTunableHandler(Field f, Object o, Tunable t) {
		super(f,o,t);
	}
	public DummyTunableHandler(Method get, Method set, Object o, Tunable t) {
		super(get,set,o,t);
	}
	public void handle() {
		System.out.println("handling! " + getName());
	}
}
