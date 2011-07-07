package org.cytoscape.work.internal.tunables;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.cytoscape.property.CyProperty;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.internal.tunables.utils.SupportedFileTypesManager;
import org.cytoscape.work.swing.GUITunableHandler;
import org.cytoscape.work.swing.GUITunableHandlerFactory;

public class FileHandlerFactory implements GUITunableHandlerFactory {

	private SupportedFileTypesManager fileTypesManager;
	private Properties props;

	public FileHandlerFactory(final CyProperty<Properties> p, final SupportedFileTypesManager fileTypesManager) {
		this.props = p.getProperties();
		this.fileTypesManager = fileTypesManager;
	}

	public GUITunableHandler getHandler(Field field, Object instance, Tunable tunable) {
		if ( field.getType() != File.class)
			return null;

		return new FileHandler(field, instance, tunable, fileTypesManager, props);
	}

	public GUITunableHandler getHandler(Method getter, Method setter, Object instance, Tunable tunable) {
		if ( getter.getReturnType() != File.class)
			return null;

		return new FileHandler(getter, setter, instance, tunable, fileTypesManager, props);
	}

}

