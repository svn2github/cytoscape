/*
 * Created on Dec 17, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.treeanno;

import java.awt.Menu;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.core.PluginManager;

public class ArrayAnnoFactory extends PluginFactory {
	static {
		PluginManager.registerPlugin(new ArrayAnnoFactory());
	}
	public ArrayAnnoFactory() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#getName()
	 */
	public String getPluginName() {
		return "ArrayTreeAnno";
	}
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#createPlugin(edu.stanford.genetics.treeview.ConfigNode)
	 */
	public MainPanel restorePlugin(ConfigNode node, ViewFrame viewFrame) {
		if (viewFrame.getDataModel().aidFound() == false) {
			JOptionPane.showMessageDialog(viewFrame, new JTextArea("DataModel does not have array tree"));
			return null;
		} else {
			// make sure the annotation columns are there...
			HeaderInfo info = viewFrame.getDataModel().getAtrHeaderInfo();
			info.addName("NAME", info.getNumNames());
			info.addName("ANNOTATION", info.getNumNames());

			// restore and return panel
			TreeAnnoPanel panel = new TreeAnnoPanel(viewFrame, node);
			panel.setName(getPluginName());
			return panel;
		}
	}
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.PluginFactory#configurePlugin(edu.stanford.genetics.treeview.ConfigNode, edu.stanford.genetics.treeview.ViewFrame)
	 */
	public void configurePlugin(ConfigNode node, ViewFrame viewFrame) {
		// TODO Auto-generated method stub
		super.configurePlugin(node, viewFrame);
		node.setAttribute("tree_type", TreeAnnoPanel.ARRAY_TREE, TreeAnnoPanel.DEFAULT_TYPE);
	}
}
