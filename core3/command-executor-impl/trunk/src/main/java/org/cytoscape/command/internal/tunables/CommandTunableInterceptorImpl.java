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


public class CommandTunableInterceptorImpl extends SpringTunableInterceptor<StringTunableHandler> {
	private String args;

	public CommandTunableInterceptorImpl(final TunableHandlerFactory<StringTunableHandler> factory) {
		super(factory);
	}

    public boolean execUI(Object... objs) {
		return validateAndWriteBackTunables(objs);
	}

	public void setArgString(String args) {
		this.args = args;
	}
   
    public boolean validateAndWriteBackTunables(Object... objs) {
		for ( Object o : objs ) {
			Map<String,StringTunableHandler> handlers = getHandlers(o);
			for ( StringTunableHandler h : handlers.values() ) {
				h.setArgString(args);
				h.handle();
			}
		}
		return true;
	}
}
