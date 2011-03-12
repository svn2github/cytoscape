package org.cytoscape.command.internal.tunables;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TunableHandlerFactory;
import org.cytoscape.work.TunableHandler;
import org.cytoscape.work.TunableValidator;
import org.cytoscape.work.spring.SpringTunableInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommandTunableInterceptorImpl extends SpringTunableInterceptor<TunableHandler> {
	private boolean newValuesSet;

	public CommandTunableInterceptorImpl(final TunableHandlerFactory<TunableHandler> factory) {
		super(factory);
	}

    public boolean execUI(Object... objs) {
		return validateAndWriteBackTunables(objs);
	}
   
    public boolean validateAndWriteBackTunables(Object... objs) {
		for ( Object o : objs ) {
			Map<String,TunableHandler> handlers = getHandlers(o);
			for ( String s : handlers.keySet() ) {
				System.out.println("got handler for tunable param: " + s);
			}
		}	
		return true;
	}
}
