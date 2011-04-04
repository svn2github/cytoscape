package org.cytoscape.internal.shutdown;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.application.swing.events.CytoscapeShutdownEvent;
import org.cytoscape.application.swing.events.CytoscapeShutdownListener;
import org.cytoscape.property.CyProperty;
import org.cytoscape.io.CyFileFilter;
import org.cytoscape.io.write.CyPropertyWriterFactory;
import org.cytoscape.io.write.CyPropertyWriterManager;
import org.cytoscape.io.write.CyWriter;
import org.cytoscape.work.SequentialTaskFactory;


public class ConfigDirPropertyWriter implements CytoscapeShutdownListener {
	private final CyPropertyWriterManager propertyWriterManager;
	private final Map<CyProperty, Map> configDirProperties;
	private final Set<CyPropertyWriterFactory> propertyWriterFactories;
	private final SequentialTaskFactory sequentialTaskFactory;

	ConfigDirPropertyWriter(final CyPropertyWriterManager propertyWriterManager)
	{
		this.propertyWriterManager = propertyWriterManager;
		configDirProperties = new HashMap<CyProperty, Map>();
		propertyWriterFactories = new HashSet<CyPropertyWriterFactory>();
		sequentialTaskFactory = new SequentialTaskFactory();
	}

	public void handleEvent(final CytoscapeShutdownEvent event) {
		for (final Map.Entry<CyProperty, Map> keyAndValue : configDirProperties.entrySet()) {
			final String propertyName = (String)keyAndValue.getValue().get("cyPropertyName");
			final String outputFileName =
				System.getProperty("user.home") + "/" + CyProperty.DEFAULT_CONFIG_DIR
				+ "/" + propertyName + ".props";
			final File outputFile = new File(outputFileName);
			boolean foundCorrectFileFiler = false;
			for (final CyPropertyWriterFactory propertyWriterFactory : propertyWriterFactories) {
				final CyFileFilter fileFilter = propertyWriterFactory.getCyFileFilter();
System.err.println("+++++++++++++ trying "+fileFilter);
				try {
					final CyWriter writer =
						propertyWriterManager.getWriter(keyAndValue.getKey(), fileFilter,
										outputFile);
					sequentialTaskFactory.addTask(writer);
					foundCorrectFileFiler = true;
System.err.println("+++++++++++++ SUCCESS!");
					break;
				} catch (final Exception e) {
				}
			}
			if (!foundCorrectFileFiler)
				System.err.println("+++ Failed create a CyWriter for \""
						   + outputFileName + "\"!");
		}
	}

	public void addCyProperty(final CyProperty newCyProperty, final Map properties) {
		if (newCyProperty.getSavePolicy() == CyProperty.SavePolicy.CONFIG_DIR)
			configDirProperties.put(newCyProperty, properties);
	}

	public void removeCyProperty(final CyProperty oldCyProperty, final Map properties) {
		if (oldCyProperty.getSavePolicy() == CyProperty.SavePolicy.CONFIG_DIR)
			configDirProperties.remove(oldCyProperty);
		
	}

	public void addCyPropertyFileWriterFactory(final CyPropertyWriterFactory newCyPropertyWriterFactory,
						   final Map properties)
	{
		propertyWriterFactories.add(newCyPropertyWriterFactory);
	}

	public void removeCyPropertyFileWriterFactory(final CyPropertyWriterFactory oldCyPropertyWriterFactory,
						      final Map properties)
	{
		propertyWriterFactories.remove(oldCyPropertyWriterFactory);
	}
}