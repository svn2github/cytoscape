package edu.stanford.genetics.treeview.core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;

public class MenuHelpPluginsFrame extends JDialog {

	private JTextField tf_dir = new JTextField();

	private JLabel l_pluginlist = new JLabel("");



	/*
	 * EFFECTS: Sets <l_pluginslist> text to currently loaded plugins RETURNS: #
	 * of plugins loaded
	 */
	private int setLabelText() {
		final PluginFactory[] plugins = PluginManager.getPluginManager().getPluginFactories();
		String s = null;
		int height = 0;
		if (plugins == null || plugins.length == 0) {
			s = "No Plugins Found";
			height = 1;
		} else {
			s = "<html><br><ol>";
			for (int i = 0; i < plugins.length; i++) {
				s += "<li>" + plugins[i].getPluginName();
			}
			s += "</ol><br></html>";
			height = plugins.length;
			LogBuffer.println("LabelHeight: " + height);
		}
		l_pluginlist.setText(s);
		return height;
	}

	/**
	 * @param url
	 */
	public void setSourceText(String url) {
		tf_dir.setText(url);
		MenuHelpPluginsFrame.this.pack();
	}

	public MenuHelpPluginsFrame(String string, final TreeViewFrame frame) {
		super(frame, string, false);
		GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.BOTH;
		this.getContentPane().setLayout(gridbag);
		setLabelText();
		this.getContentPane().add(l_pluginlist, c);

		JPanel dirPanel = new JPanel();

		dirPanel.add(tf_dir, BorderLayout.CENTER);
		JButton b_browse = new JButton("Browse...");
		b_browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    int returnVal = chooser.showOpenDialog(MenuHelpPluginsFrame.this);
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			    	String url = chooser.getSelectedFile().getAbsolutePath();
			    	setSourceText(url);
			    }
			}
		});
		dirPanel.add(b_browse, BorderLayout.EAST);
		this.getContentPane().add(dirPanel, c);
		
		JButton b_scan = new JButton("Scan new plugins");
		b_scan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File[] files = PluginManager.getPluginManager().readdir(tf_dir.getText());
				if (files == null || files.length == 0) {
					JOptionPane.showMessageDialog(MenuHelpPluginsFrame.this, 
							"Directory contains no plugins");
				} else {
					PluginManager.getPluginManager().loadPlugins(files, true);
				}
				PluginManager.getPluginManager().pluginAssignConfigNodes(frame.getApp().getGlobalConfig().getNode("Plugins"));
				setLabelText();
				MenuHelpPluginsFrame.this.validate();
				frame.rebuildMainPanelMenu();
			}
		});
		this.getContentPane().add(b_scan, c);
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(frame);
	}
}