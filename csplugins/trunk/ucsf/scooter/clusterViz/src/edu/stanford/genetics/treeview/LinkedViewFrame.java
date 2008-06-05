/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: LinkedViewFrame.java,v $
 * $Revision: 1.48 $
 * $Date: 2007/06/17 18:22:09 $
 * $Name:  $
 *s
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular, 
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER 
 */
package edu.stanford.genetics.treeview;


import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.model.*;
import edu.stanford.genetics.treeview.app.*;
import edu.stanford.genetics.treeview.core.PluginManager;

/** 
 * This class implements the GUI portion of the LinkedView application 
 * 
 */
public class LinkedViewFrame extends TreeViewFrame implements Observer
{




	private static String appName = "Java TreeView";
	public String getAppName() {
		return appName;
	}
	public LinkedViewFrame(LinkedViewApp treeview)
	{
		super(treeview, appName);
	}
	public LinkedViewFrame(TreeViewApp treeview, String subName)
	{
		// sorry this is so ugly, but can't call getAppName until
		// superclass constructor's done.
		super(treeview, subName);
	}
	
	private JDialog presetDialog = new JDialog(LinkedViewFrame.this, "Presets", true);
	

	private String getStyle(FileSet fileSet) {
		if (fileSet.getStyle() == FileSet.AUTO_STYLE) {
			return "auto";
		}
		if (fileSet.getStyle() == FileSet.CLASSIC_STYLE) {
			return "classic";
		}
		if (fileSet.getStyle() == FileSet.KMEANS_STYLE) {
			return "kmeans";
		}
		if (fileSet.getStyle() == FileSet.LINKED_STYLE) {
			return "linked";
		}
		return "unknown";
	}
	
	/**
	 r     * This is the workhorse. It creates a new DataModel of the
	 * file, and then sets the Datamodel. 
	 * A side effect of setting the datamodel is to
	 * update the running window.
	 */
	public void loadFileSet(FileSet fileSet)  throws LoadException {
		LogBuffer.println("initial style " + getStyle(fileSet));
		if (fileSet.getStyle() == FileSet.AUTO_STYLE) {
			if (fileSet.getKag().equals("") && fileSet.getKgg().equals("")) {
				super.loadFileSet(fileSet); // loads into TVModel.
			} else {
				loadKnnModel(fileSet);
			}
		} else {
			if (fileSet.getStyle() == FileSet.KMEANS_STYLE) {
				loadKnnModel(fileSet);
			} else {
				super.loadFileSet(fileSet);
			}
		}
	} 
	
	private void loadKnnModel(FileSet fileSet) throws LoadException {
		KnnModel knnModel = new KnnModel();
		knnModel.setFrame(this);
		try {
			knnModel.loadNew(fileSet);
			fileSet.setStyle(FileSet.KMEANS_STYLE);
			setDataModel(knnModel);
		} catch (LoadException e) {
			JOptionPane.showMessageDialog(this, e);
			throw e;
		}
	}
	
	 protected void setupRunning() {
		 FileSet fileSet = getDataModel().getFileSet();
		 if (fileSet == null) {
		 	//default to linked
		 	fileSet = new FileSet(null,null);
		 	fileSet.setStyle(FileSet.LINKED_STYLE);
		 } else if (fileSet.getStyle() == FileSet.AUTO_STYLE) {
			 HeaderInfo geneHeaders = getDataModel().getGeneHeaderInfo();
			 HeaderInfo arrayHeaders = getDataModel().getArrayHeaderInfo();
			 if ((geneHeaders.getNumNames() > 4) || (arrayHeaders.getNumNames() > 3)) {
				 fileSet.setStyle(FileSet.LINKED_STYLE);
			 } else {
				 fileSet.setStyle(FileSet.CLASSIC_STYLE);
			 }
		 }
		 
		 
		 if (fileSet.getStyle() == FileSet.LINKED_STYLE) {
			 LinkedPanel linkedPanel  = new LinkedPanel(this);
			 linkedPanel.addChangeListener(new ChangeListener() {
				 public void stateChanged(ChangeEvent e) {
					 // rebulid menus...?
					 //				 menuBar.rebuildMainPanel();
					 // rebuildMainPanelMenu();
				 }
			 });
			 ConfigNode documentConfig = getDataModel().getDocumentConfig();
			 linkedPanel.setConfigNode(documentConfig.fetchOrCreate("Views"));
			 running = linkedPanel;
		 } else if (fileSet.getStyle() == FileSet.KMEANS_STYLE) {
			// make sure selection objects are set up before instantiating plugins
			 PluginFactory [] plugins = PluginManager.getPluginManager().getPluginFactories();
			 for (int j =0; j < plugins.length; j++) {
				 if ("KnnDendrogram".equals(plugins[j].getPluginName())) {
					 running = plugins[j].restorePlugin(null, this);
					 break;
				 }
			 }
		 } else  {
			// make sure selection objects are set up before instantiating plugins
			 PluginFactory [] plugins = PluginManager.getPluginManager().getPluginFactories();
			 for (int j =0; j < plugins.length; j++) {
				 if ("Dendrogram".equals(plugins[j].getPluginName())) {
					 running = plugins[j].restorePlugin(null, this);
					 break;
				 }
			 }
		 }
		LogBuffer.println("final style " + getStyle(fileSet));
    }
	 
	 /**
	  * This class implements controls for file opening options.
	  * It is factored into a separate class because it is used by
	  * both the offerSelection() and offerUrlSelection dialogs.
	  * 
	  * @author aloksaldanha
	  *
	  */
	 private class FileOptionsPanel extends Box {
		 private JComboBox dataList;
		 private JCheckBox quoteBox;
		 public FileOptionsPanel() {
			 super(BoxLayout.Y_AXIS);
			 //XXX- should modify to use static accessors of FileSet to get types.
			 String[] data = {"Auto", "Classic", "Kmeans", "Linked"};
			 dataList = new JComboBox(data);	
			 dataList.setEditable(false);
			 
			 JPanel stylePanel = new JPanel();
			 JLabel style = new JLabel("Style:");
			 stylePanel.add(style);
			 stylePanel.add(dataList);
				
			 JPanel quotePanel = new JPanel();
			 quoteBox = new JCheckBox("Parse quoted strings");
			 quotePanel.add(quoteBox);

			 // values from last time...
			 quoteBox.setSelected(fileMru.getParseQuotedStrings());
			 dataList.setSelectedIndex(fileMru.getStyle());
				
			 add(stylePanel);
			 add(quotePanel);
			 add(Box.createGlue());
			 try {
				 setBorder(BorderFactory.createTitledBorder("Options"));
			 } catch (Exception e) {
				 LogBuffer.println("Could not create border in LinkedViewFrame.offerSelection");
			 }
		 }
		 public int getSelectedStyleIndex() {
			 fileMru.setStyle(dataList.getSelectedIndex());
			 return dataList.getSelectedIndex();
		 }
		public boolean isQuoteSelected() {
			fileMru.setParseQuotedStrings(quoteBox.isSelected());
			return quoteBox.isSelected();
		}

	 }
	/**
	* Open a dialog which allows the user to select a new data file
	*
	* @return The fileset corresponding to the dataset.
	*/
	protected FileSet offerSelection()
	throws LoadException
	{
		FileSet fileSet1; // will be chosen...
		JFileChooser fileDialog = new JFileChooser();
		setupFileDialog(fileDialog);
		FileOptionsPanel boxPanel = new FileOptionsPanel();
		fileDialog.setAccessory(boxPanel); 
		int retVal = fileDialog.showOpenDialog(this);
		if (retVal == JFileChooser.APPROVE_OPTION) {
			File chosen = fileDialog.getSelectedFile();
			
			fileSet1 = new FileSet(chosen.getName(), chosen.getParent()+File.separator);
		} else {
			throw new LoadException("File Dialog closed without selection...", LoadException.NOFILE);
		}
		fileSet1.setStyle(boxPanel.getSelectedStyleIndex());
		fileSet1.setParseQuotedStrings(boxPanel.isQuoteSelected());
		return fileSet1;
	}

	  protected FileSet offerUrlSelection()
	  throws LoadException
	  {
		  FileSet fileSet1;
		  // get string from user...
		  FileOptionsPanel boxPanel = new FileOptionsPanel();
		  Box panel = new Box(BoxLayout.Y_AXIS);
		  panel.add(boxPanel);
		  panel.add(new JLabel("Enter a Url:"));
		  String urlString = JOptionPane.showInputDialog(this, panel);
		  if (urlString != null) {
			  // must parse out name, parent + sep...
			  int postfix = urlString.lastIndexOf("/") + 1;
			  String name = urlString.substring(postfix);
			  String parent = urlString.substring(0,postfix);
			  fileSet1 = new FileSet(name, parent);
		  } else {
			  throw new LoadException("Input Dialog closed without selection...", LoadException.NOFILE);
		  }
		  
		  fileSet1.setStyle(boxPanel.getSelectedStyleIndex());
		  fileSet1.setParseQuotedStrings(boxPanel.isQuoteSelected());
		  return fileSet1;
	  }
}


