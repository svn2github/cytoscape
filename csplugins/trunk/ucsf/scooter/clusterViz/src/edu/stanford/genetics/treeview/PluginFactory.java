/*
 * Created on Jul 1, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview;

import java.awt.Menu;

/**
 * 
 * This interface allows the application to get
 * information about the plugin and to create instances of the
 * plugin.
 * 
 * Technically, you only need to make a concrete subclass in order to 
 * define a new plugin.
 * 
 * One global factory is created per plugin type, and is then used
 * to instantiate plugins and manage their global settings.
 * 
 * Each plugin implements a MainPanel that is associated with a LinkedPanel
 * 
 * @author aloksaldanha
 *
 */
public abstract class PluginFactory {
	/**
	 * 
	 * This should really be static, but there's no way to do 
	 * abstract static methods in Java (or C++ for that matter)
	 * I suppose I could have a PluginFactoryFactory, but I don't
	 * think that would really help things.
	 * 
	 * Please just return a simple text string, thanks!
	 * 
	 * @return name of corresponding plugin
	 * 
	 */
	public abstract String getPluginName(); 
	
	
	/**
	 * This method is called to "restore" old instances of the plugin
	 * from a confignode, which is derived from the JTV file.The actual 
	 * structure of the confignode is determined by the plugin.
	 * 
	 *  You will probably want to grab the DataModel from the ViewFrame
	 *  and display it in the MainPanel that is returned. It's probably 
	 * also a good idea to listen to the relevant TreeSelection(s).
	 * 
	 * @param node ConfigNode holding configuration of plugin
	 * @return new plugin object
	 */
	public abstract MainPanel restorePlugin(ConfigNode node, ViewFrame viewFrame);

	/**
	 * This method is used by the application to  configure new 
	 * instances of the plugin.
	 * 
	 * The plugin may query the viewFrame and set variables in the node. 
	 * The plugin may also query the user through dialog windows.
	 * No other configuration is permitted.
	 * The node serves as a persistent store for the configuration,
	 * allowing the instance to be recreated if file loaded again.
	 * 
	 * @param viewFrame ViewFrame to which to add new plugin
	 * @return menuitem to trigger add
	 */
	public void configurePlugin(ConfigNode node, ViewFrame viewFrame) {
		// no configuration by default
	}
	
	/**
	 * This is called during the generation of the
	 * Settings->Global menu. It should add menu items that configure
	 * either the presets or defaults associated with the plugin
	 * 
	 */
	public void addPluginConfig(Menu globalMenu, ViewFrame viewFrame) {
		// no presets by default.
	}

	private ConfigNode globalNode;
	/**
	 * The ConfigNode passed into configurePlugin() and restorePlugin()
	 * is a document-specific configuration node. There is also a global 
	 * ConfigNode associated with each plugin type. This global node is
	 * stored as a member of PluginFactory, and is set by the system when
	 * the plugin is loaded. 
	 * 
	 * Subclasses can override this if they want to bind shared objects,
	 * such as color presets, to a particular subnode of the global
	 * configuration node.
	 * 
	 * Plugin instances can access it through the getPresetsNode() method
	 * if they are not content to interact with shared objects provided
	 * by the factory subclass.
	 * 
	 * @param node
	 */
	public void setGlobalNode(ConfigNode node) {
		globalNode = node;
	}
	
	/**
	 * get global configuration node for this plugin. This is a place to
	 * store default values and presets that must be shared between 
	 * instances of the plugins, in particular by new plugins.
	 * 
	 * Note that ConfigNode is not Observable, so this mechanism cannot
	 * be used to synchronize instances the way that TreeSelection does.
	 * 
	 * @return global configuration node for plugin type
	 */
	public ConfigNode getGlobalNode() {
		return globalNode;
	}
	
}
