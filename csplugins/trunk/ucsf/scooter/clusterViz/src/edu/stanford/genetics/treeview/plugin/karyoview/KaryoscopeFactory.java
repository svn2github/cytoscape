/*
 * Created on Aug 15, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.karyoview;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JFrame;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.PluginManager;

/**
 * @author aloksaldanha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class KaryoscopeFactory extends PluginFactory {
	private static KaryoColorPresets colorPresets = new KaryoColorPresets();
	private static CoordinatesPresets coordPresets = new CoordinatesPresets();
	private static KaryoColorPresetEditor  cpresetEditor = null;
	private static CoordinatesPresetEditor  coordEditor = null;
	private JFrame cpresetFrame = null;
	private TabbedSettingsPanel tabbedPanel;
	static {
		PluginManager.registerPlugin(new KaryoscopeFactory());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#getPluginName()
	 */
	public String getPluginName() {
		return "Karyoscope";
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#restorePlugin(edu.stanford.genetics.treeview.ConfigNode, edu.stanford.genetics.treeview.ViewFrame)
	 */
	public MainPanel restorePlugin(ConfigNode node, ViewFrame viewFrame) {
		KaryoPanel karyoPanel = new KaryoPanel(viewFrame.getDataModel(), 
				viewFrame.getGeneSelection(), viewFrame, node);
		karyoPanel.setName(getPluginName());
		return karyoPanel;
	}
	
	public KaryoscopeFactory() {
		super();
		cpresetEditor = new KaryoColorPresetEditor(colorPresets);
		cpresetEditor.setTitle("Karyoscope Color Presets");
		coordEditor  = new CoordinatesPresetEditor(coordPresets);
		coordEditor.setTitle("Karyoscope Coordinates Presets");
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
		/*
		coordPresets.bindConfig(node.fetchOrCreate("CoordPresets"));
		if (coordPresets.getNumPresets() == 0) {
			coordPresets.addDefaultPresets();
		}
		coordEditor.synchronizeFrom();
		*/
	}

	
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#addPluginConfig(java.awt.Menu)
	 */
	public void addPluginConfig(Menu globalMenu, final ViewFrame frame) {
		super.addPluginConfig(globalMenu, frame);
		MenuItem pluginItem = new MenuItem("Karyoscope Color...");
		pluginItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (cpresetFrame == null) {
					setupPresetsFrame(frame.getApp().getGlobalConfig().getRoot());
				}
				tabbedPanel.setSelectedComponent(cpresetEditor);
				cpresetFrame.show();
			}
		});
		globalMenu.add(pluginItem);
		if (coordPresets.getNumPresets() == 0) {
			try {
				coordPresets.scanUrl(new URL(frame.getApp().getCodeBase().toString() +"/coordinates"));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			coordEditor.synchronizeFrom();
		}
		pluginItem = new MenuItem("Karyoscope Coordinates...");
		pluginItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (cpresetFrame == null) {
					setupPresetsFrame(frame.getApp().getGlobalConfig().getRoot());
				}
				tabbedPanel.setSelectedComponent(coordEditor);
				cpresetFrame.show();
			}
		});
		globalMenu.add(pluginItem);
		
	}
	/**
	 * 
	 * @param frame ViewFrame that contains relevant global config node
	 */
	private void setupPresetsFrame(ConfigNode node) {
		tabbedPanel = new TabbedSettingsPanel();
		tabbedPanel.addSettingsPanel("Color", cpresetEditor);
		tabbedPanel.addSettingsPanel("Coordinates", coordEditor);
		
		cpresetFrame = new JFrame("Karyoscope Presets");
		SettingsPanelHolder holder = 
			new SettingsPanelHolder(cpresetFrame, node);
		holder.addSettingsPanel(tabbedPanel);
		cpresetFrame.getContentPane().add(holder);
		cpresetFrame.pack();
	}
	
	/**
	 * mechanism by which KaryoPanel can access the presets.
	 * @return color presets for dendrogram view
	 */
	public static KaryoColorPresets getColorPresets() {
		return colorPresets;
	}
	/**
	 * mechanism by which KaryoPanel can access the presets.
	 * @return color presets for dendrogram view
	 */
	public static CoordinatesPresets getCoordinatesPresets() {
		return coordPresets;
	}
	/** returns JPanel that allowed editing of coordinates presets */
	public static CoordinatesPresetEditor getCoordinatesPresetsEditor() {
		return coordEditor;
	}
}
