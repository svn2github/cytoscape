/*
 * Created on Aug 18, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.scatterview;

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
public class ScatterplotFactory extends PluginFactory {
	private static ScatterColorPresets colorPresets = new ScatterColorPresets();
	private ScatterColorPresetEditor  cpresetEditor = null;
	private JFrame cpresetFrame = null;
	private TabbedSettingsPanel tabbedPanel;
	static {
		PluginManager.registerPlugin(new ScatterplotFactory());
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#getPluginName()
	 */
	public String getPluginName() {
		return "Scatterplot";
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#restorePlugin(edu.stanford.genetics.treeview.ConfigNode, edu.stanford.genetics.treeview.ViewFrame)
	 */
	public MainPanel restorePlugin(ConfigNode node, ViewFrame viewFrame) {
		ScatterPanel gsp = new ScatterPanel((LinkedViewFrame) viewFrame, node);
		gsp.setSelection(viewFrame.getGeneSelection());
		gsp.setName(getPluginName());
		return gsp;
	}

	public ScatterplotFactory() {
		super();
		cpresetEditor = new ScatterColorPresetEditor(colorPresets);
		cpresetEditor.setTitle("Scatterplot Color Presets");
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
		MenuItem pluginItem = new MenuItem("Scatterplot Color...");
		pluginItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				if (cpresetFrame == null) {
					setupPresetsFrame(frame.getApp().getGlobalConfig().getRoot());
				}
				tabbedPanel.setSelectedComponent(cpresetEditor);
				cpresetFrame.setVisible(true);
			}
		});
		globalMenu.add(pluginItem);
	}

	/**
	 * 
	 * @param frame ViewFrame that contains relevant global config node
	 */
	private void setupPresetsFrame(ConfigNode node) {
		cpresetFrame = new JFrame("Scatterplot Color");
		SettingsPanelHolder holder = 
			new SettingsPanelHolder(cpresetFrame, node);
		holder.addSettingsPanel(cpresetEditor);
		cpresetFrame.getContentPane().add(holder);
		cpresetFrame.pack();
	}
	
	/**
	 * mechanism by which ScatterPanel can access the presets.
	 * @return color presets for scatterplot view
	 */
	public static ScatterColorPresets getColorPresets() {
		return colorPresets;
	}

	public void configurePlugin(ConfigNode node, ViewFrame frame) {
			GraphDialog gd = new GraphDialog(node, frame);
			try {
				gd.setLocationRelativeTo(frame);
			} catch (java.lang.NoSuchMethodError err) {
				// god damn MRJ for os9.
			}
			gd.pack();
			gd.setVisible(true);
	}


/**
 * this class pops up a dialog window that allows one to make a
 * graph.
 */

private class GraphDialog extends JDialog {
	StatPanel xPanel, yPanel;
	ViewFrame frame;
	ConfigNode node;
	int npre, nexpr;
	GraphDialog(ConfigNode node, ViewFrame frame) {
		super(frame, "Create Graph...", true);
		this.frame = frame;
		this.node = node;
		JPanel box = new JPanel();
		box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
		yPanel = new StatPanel("Y Axis:");
		xPanel = new StatPanel("X Axis:");
		box.add(new JLabel("Create Graph:"));
		box.add(yPanel);
		box.add(xPanel);
		box.add(new ButtonPanel());
		setContentPane(box);
	}
	
	class  StatPanel extends JPanel {
		JComboBox statPulldown;
		StatPanel(String title) {
			super(false);
			
			DataModel dataModel = frame.getDataModel();
			HeaderInfo arrayInfo = dataModel.getArrayHeaderInfo();
			HeaderInfo geneInfo  = dataModel.getGeneHeaderInfo();
			//		String [][] aHeaders = dataModel.getArrayHeaders();
			//		int gidRow = dataModel.getGIDIndex();
			int gidRow = 0;
			npre =  geneInfo.getNumNames();
			nexpr = arrayInfo.getNumHeaders();
			String [] statNames = new String[npre + nexpr + 1];
			// Index
			statNames[0] = "INDEX";
			// stat columns
			String [] pre = geneInfo.getNames();
			for (int i = 0; i < npre; i++) {
				statNames[i+1] = pre[i];		    
			}
			// experiment ratios
			for (int i = 0; i < nexpr; i++) {
				statNames[i+1+npre] = arrayInfo.getHeader(i) [gidRow];
			}
			add(new JLabel(title));
			statPulldown = new JComboBox(statNames);
			add(statPulldown);
		}
		int getType() {
			if (statPulldown.getSelectedIndex() == 0)
				return ScatterPanel.INDEX;
			if (statPulldown.getSelectedIndex() <= npre)
				return ScatterPanel.PREFIX;
			return ScatterPanel.RATIO;
		}
		// will return either an index into aHeaders or into 
		// genePrefix depending...
		int getIndex() {
			if (getType() == ScatterPanel.PREFIX) 
				return statPulldown.getSelectedIndex() - 1;
			if (getType() == ScatterPanel.RATIO) 
				return statPulldown.getSelectedIndex() - 1 - npre;
			return -1;
		}
	}
	MenuItem ratioItem = new MenuItem("Make Scatterplot of Genes...");

	class ButtonPanel extends JPanel {
		private JButton  closeButton, goButton;
		ButtonPanel() {
			super();
			
			goButton = new JButton("Go!");
			goButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int xtype = xPanel.getType();
					int xindex = xPanel.getIndex();
					int ytype = yPanel.getType();
					int yindex = yPanel.getIndex();
					node.setAttribute("type", "Scatterplot", null);
					node.setAttribute("xtype", xtype, 0);
					node.setAttribute("ytype", ytype, 0);
					node.setAttribute("xindex", xindex, 0);
					node.setAttribute("yindex", yindex, 0);
					GraphDialog.this.dispose();
				}
			});
			add(goButton);
			/*
			closeButton = new JButton("Cancel");
			closeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					GraphDialog.this.dispose();
				}
			});
			add(closeButton);
			*/
		}
	}

 	}
}
	

