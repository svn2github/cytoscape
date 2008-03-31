/*
 * Created on Jul 1, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.dendroview;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.PluginManager;

/**
 * @author aloksaldanha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class DendrogramFactory extends PluginFactory {
	// presets must be set before static initializer.
	private static ColorPresets colorPresets = new ColorPresets();
	private ColorPresetEditor  cpresetEditor;
	private JFrame cpresetFrame = null;
	static {
		PluginManager.registerPlugin(new DendrogramFactory());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#getName()
	 */
	public String getPluginName() {
		return "Dendrogram";
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#createPlugin(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public MainPanel restorePlugin(ConfigNode node, ViewFrame viewFrame) {
		DendroView dendroView = new DendroView(viewFrame.getDataModel(), node, viewFrame);
		dendroView.setName("Dendrogram");
		return dendroView;
	}
	
	
	
	public DendrogramFactory() {
		super();
		cpresetEditor = new ColorPresetEditor(colorPresets);
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#setGlobalNode(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public void setGlobalNode(ConfigNode node) {
		super.setGlobalNode(node);
		colorPresets.bindConfig(node.fetchOrCreate("ColorPresets"));
		if (colorPresets.getNumPresets() == 0) {
		  colorPresets.addDefaultPresets();
		}
		cpresetEditor.synchronizeFrom();
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#addPluginConfig(java.awt.Menu)
	 */
	public void addPluginConfig(Menu globalMenu, final ViewFrame frame) {
		super.addPluginConfig(globalMenu, frame);
		MenuItem pluginItem = new MenuItem("Dendrogram Color Presets...");
		pluginItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (cpresetFrame == null) {
					cpresetFrame = new JFrame("Dendrogram Color Presets");
					SettingsPanelHolder holder = new SettingsPanelHolder(cpresetFrame, 
							frame.getApp().getGlobalConfig().getRoot());
					holder.addSettingsPanel(cpresetEditor);
					cpresetFrame.getContentPane().add(holder);
				}
				cpresetFrame.pack();
				cpresetFrame.show();
			}
		});
		globalMenu.add(pluginItem);
	}
	
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#setGlobalNode(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public SettingsPanel getPresetEditor () {
		return cpresetEditor;
	}
	/**
	 * mechanism by which Dendroview can access the presets.
	 * @return color presets for dendrogram view
	 */
	public static ColorPresets getColorPresets() {
		return colorPresets;
	}


}
