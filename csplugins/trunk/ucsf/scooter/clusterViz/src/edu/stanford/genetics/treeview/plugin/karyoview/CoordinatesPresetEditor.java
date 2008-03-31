/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: CoordinatesPresetEditor.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
 * $Name:  $
 *
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
package edu.stanford.genetics.treeview.plugin.karyoview;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;
/**
 * This class allows graphical editing of ColorPresets
 * Also included is a class to pop up a configuration window.
 */
public class CoordinatesPresetEditor extends JPanel implements SettingsPanel {
    private CoordinatesPresets presets;
    private Window window;

    /**
     * This class is to enable editing of a UrlPresets object.
	 * HACK I botched the design pretty badly here, but I'm too busy to clean it up now.
     */
    public CoordinatesPresetEditor(CoordinatesPresets up) {
	  super();
	  presets = up;
	  presetEditPanel = new PresetEditPanel();
	  add(new JScrollPane(presetEditPanel));
    }
    
    /**
     * pops up a configuration dialog.
     */
	 public void showConfig(Frame f) {
		 if (window == null) {
			 Dialog d = new Dialog(f, getTitle(), true);
			 d.setLayout(new BorderLayout());
			 d.add( new JScrollPane(presetEditPanel));
			 //		 d.add(presetEditPanel);
			 d.add(new JLabel(getTitle()), BorderLayout.NORTH);
			 d.add(new ButtonPanel(), BorderLayout.SOUTH);
			 d.addWindowListener(new WindowAdapter (){
				 public void windowClosing(WindowEvent we) 
				 {we.getWindow().dispose();}
			 });
			 d.pack();
			 window = d;
		 }
		 window.show();
	 }

    public static void main(String [] argv) {
	final CoordinatesPresets p = new CoordinatesPresets(new DummyConfigNode("CoordinatesPresets"));
	CoordinatesPresetEditor e  = new CoordinatesPresetEditor(p);
	Frame f = new Frame(e.getTitle());
	e.showConfig(f);
	System.out.println("on exit, presets were\n" + p.toString());
	System.exit(0);
/*
	e.addToFrame(f);

	f.addWindowListener(new WindowAdapter (){
		public void windowClosing(WindowEvent we) 
		{
		  System.exit(0);
		}
	});
	f.pack();
	f.show();
*/
    }

    public void addToFrame(Frame f) {
	  f.setLayout(new BorderLayout());
	  presetEditPanel = new PresetEditPanel();
	  f.add( new JScrollPane(presetEditPanel));
	  //	f.add(new Label(getTitle(),Label.CENTER), BorderLayout.NORTH);
	  f.add(new ButtonPanel(), BorderLayout.SOUTH);
	  window = f;
    }
	private String title = "Coordinates Presets Editor";
    public String getTitle() {return title;}
	public void setTitle(String s) { title = s;}

	private PresetEditPanel presetEditPanel;

	public void synchronizeFrom() {
	  presetEditPanel.initialize();
	  presetEditPanel.redoLayout();
	}
	public void synchronizeTo() {
	  presetEditPanel.saveAll();
	}
    //inner classes
    private class ButtonPanel extends JPanel {
	ButtonPanel() {
	    JButton save_button = new JButton("Save");
	    save_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			  synchronizeTo();
			  window.dispose();
		    }
		});
	    add(save_button);

	    JButton cancel_button = new JButton("Cancel");
	    cancel_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			  synchronizeFrom();
			  window.dispose();
		    }
		});
	    add(cancel_button);
	}
    }    

    private class PresetEditPanel extends JPanel {
	  PresetEditPanel () {
		initialize();
		redoLayout();
	  }
	  private GridBagConstraints gbc;
	  private JRadioButton[] defaultButtons;
	  private JTextField[] presetNames;
	  private FileSet[] presetFiles;
	  private ButtonGroup bob		   = new ButtonGroup();
	  private void initialize() {
		  int nPresets = presets.getNumPresets();
		  defaultButtons = new JRadioButton[nPresets + 1];
		  presetNames = new JTextField[nPresets + 1];
		  presetFiles = new FileSet[nPresets + 1];
		  for (int i = 0; i < nPresets; i++) {
			initializePreset(i);
			bob.add(defaultButtons[i]);
		  }
		  initializeNonePreset(nPresets); // put none preset at end 
		  bob.add(defaultButtons[nPresets]);

		  if (presets.getDefaultIndex() == -1) { // select the none preset
			  defaultButtons[nPresets].setSelected(true);
		  } else {
			  defaultButtons[presets.getDefaultIndex()].setSelected(true);
		  }
	  }
		private void initializeNonePreset(int i) {
			final int index = i;
			final JTextField templateField = new JTextField();
			final JTextField nameField = new JTextField();
			nameField.setText("None");
			presetNames[index] = nameField;
			//		  templateField.setText(presets.getTemplate(index));
			presetFiles[index] = null;
			//		  add(templateField, gbc);
			JRadioButton set = new JRadioButton();
			defaultButtons[index] = set;
			set.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					presets.setDefaultIndex(-1);
				}
			});
		}

		/**
		* Creates components and copies state from presets for preset i...
		*/
		private void initializePreset(int i) {
		  final int index = i;
		  final FileSet fileSet = new FileSet(new DummyConfigNode ( "Dummy FileSet"));
		  final JTextField nameField = new JTextField();
		  nameField.setText((presets.getPresetNames()) [index]);
		  presetNames[index] = nameField;

		  fileSet.copyState(presets.getFileSet(index));
		  presetFiles[index] = fileSet;

		  JRadioButton set = new JRadioButton();
		  defaultButtons[index] = set;
		  set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  presets.setDefaultIndex(index);
			}
		  });
		}


	  /**
	  * Assumes that defaultButtons, presetNames, and presetColors have been properly set up.
	  */
	  public void redoLayout() {
		  int nPresets = defaultButtons.length;
		  removeAll();
		  setLayout(new GridBagLayout());
		  gbc = new GridBagConstraints();
		  gbc.weighty = 100;
		  gbc.gridwidth = 1;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  gbc.anchor = GridBagConstraints.NORTH;
		  gbc.gridy = 0;
		  gbc.gridx = 0;

		  gbc.weighty = 100;
		  add(new JLabel("Modify Coordinates Presets", JLabel.CENTER), gbc);
		  gbc.weighty = 0;
		  gbc.gridy = 1;
		  gbc.gridx = 0;
		  add(new JLabel("Name"), gbc);
		  gbc.gridx = 1;
		  add(new JLabel("Source"), gbc);
		  gbc.gridx = 3;
		  add(new JLabel("Default?"), gbc);

		  for (int i = 0; i < nPresets; i++) {
			gbc.gridy++;
			addPreset(i);
		  }
		  
		  gbc.gridy++;
		  
		  JButton addP = new JButton("Add");
		  addP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  createPreset();
			}
		  });
		  gbc.gridy++;
		  gbc.gridx  = 2;
		  add(addP, gbc);
		  revalidate();
		  repaint();
	  	}

		/**
		* This just adds a preset to the GUI, assuming that presetNames, presetColors and defaultButtons are properly set up...
		*/
		private void addPreset(int i) {
		  final int index = i;
		  gbc.gridx = 0;
		  add(presetNames[index], gbc);

		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  if (presetFiles[index] != null) {
			  add(new FileSetEditor(presetFiles[index], window), gbc);
			  gbc.gridx = 2;
			  gbc.weightx = 0;
			  JButton rem = new JButton("Remove");
			  rem.addActionListener(new ActionListener() {
				  public void actionPerformed(ActionEvent e) {
					  removePreset(index);
				  }
			  });
			  add(rem, gbc);
		  }
		  
		  gbc.gridx = 3;
		  add(defaultButtons[index], gbc);
		}

		private void saveAll() {
		  // first, make sure that there's the same number of presets...
		  int n = presetNames.length - 1; //number of presets to be
		  int o = presets.getNumPresets();// current number of presets...

		  while (n > o) {// need to add more presets...
			  FileSet temp = new FileSet(new DummyConfigNode("Dummy FileSet"));
			  temp.setName("Preset" + o);
			  presets.addFileSet(temp);
			  o++;
		  }

		  while (o > n) { // need to delete some...
			o--;
			presets.removeFileSet(o);
		  }

		  // next, copy the state over
		  for (int i = 0; i < n; i++) {
			presetFiles[i].setName(presetNames[i].getText());
		  }
		  for (int i = 0; i < n; i++) {
			presets.getFileSet(i).copyState(presetFiles[i]);
		  }
		}

		// this removes a preset
		private void removePreset(int mark) {
		  FileSet [] tPresetFiles = new FileSet[presetFiles.length - 1];
		  JTextField [] tPresetNames = new JTextField[presetNames.length - 1];
		  JRadioButton [] tDefaultButtons = new JRadioButton[defaultButtons.length - 1];
		  for (int i = 0 ;i < tPresetFiles.length; i++) {
			int j = i;
			if (i >= mark) j++;
			tPresetFiles[i] = presetFiles[j];
			tPresetNames[i] = presetNames[j];
			tDefaultButtons[i] = defaultButtons[j];
		  }
		  bob.remove(defaultButtons[mark]);
		  int selectedIndex = 0;
		  for (int i = 0; i < (defaultButtons.length); i++) {
			if (defaultButtons[i] == null) continue;
			if (defaultButtons[i].isSelected()) {
			  selectedIndex = i;
			}
		  }

		  if (selectedIndex > (tPresetNames.length - 2)) {
			tDefaultButtons[tPresetNames.length - 2].setSelected(true);
		  } else {
			tDefaultButtons[selectedIndex].setSelected(true);
		  }

		  presetNames = tPresetNames;
		  presetFiles = tPresetFiles;
		  defaultButtons = tDefaultButtons;
		  redoLayout();
		  
		}
		  // this creates a brand new preset and adds stuff to the GUI for it...
		private void createPreset() {
		  FileSet [] tPresetFiles = new FileSet[presetFiles.length + 1];
		  JTextField [] tPresetNames = new JTextField[presetNames.length + 1];
		  JRadioButton [] tDefaultButtons = new JRadioButton[defaultButtons.length + 1];
		  for (int i = 0 ;i < presetFiles.length; i++) {
			tPresetFiles[i] = presetFiles[i];
			tPresetNames[i] = presetNames[i];
			tDefaultButtons[i] = defaultButtons[i];
		  }
		  
		  // for null...
		  final int newIndex =tPresetNames.length - 2;
		  tPresetFiles[newIndex + 1] = tPresetFiles[newIndex];
		  tPresetNames[newIndex + 1] = presetNames[newIndex];
		  tDefaultButtons[newIndex + 1] = tDefaultButtons[newIndex];
		  
		  tPresetNames[newIndex] = new JTextField("Preset" + newIndex);
		  tPresetFiles[newIndex] = new FileSet(new DummyConfigNode("DummyFileSet"));
		  JRadioButton set = new JRadioButton();
		  tDefaultButtons[newIndex] = set;
		  set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  presets.setDefaultIndex(newIndex);
			}
		  });
		  bob.add(set);

		  presetNames = tPresetNames;
		  presetFiles = tPresetFiles;
		  defaultButtons = tDefaultButtons;
		  redoLayout();
		}
		
/*
		private void addNonePreset(int i) {
		  final int index = i;
		  final JTextField templateField = new JTextField();
		  final JTextField nameField = new JTextField();
		  gbc.gridx = 0;
		  nameField.setText("None");
		  presetNames[index] = nameField;
		  add(nameField, gbc);
		  gbc.gridx = 1;
		  gbc.weightx = 100;
//		  templateField.setText(presets.getTemplate(index));
		  presetTemplates[index] = null;
//		  add(templateField, gbc);
		  gbc.gridx = 2;
		  gbc.weightx = 0;
		  JRadioButton set = new JRadioButton();
		  defaultButtons[index] = set;
		  set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  presets.setDefaultPreset(-1);
			}
		  });
		  add(set, gbc);
		}
	*/
	}
}

