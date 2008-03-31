/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: UrlPresetsEditor.java,v $
 * $Revision: 1.1 $
 * $Date: 2005/08/19 01:35:44 $
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
package edu.stanford.genetics.treeview;


import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
/**
 * This class extracts Urls from HeaderInfo.
 * Also included is a class to pop up a configuration window.
 */
public class UrlPresetsEditor extends JPanel implements SettingsPanel {
    private UrlPresets presets;
    private Window window;

    /**
     * This class is to enable editing of a UrlPresets object.
	 * HACK I botched the design pretty badly here, but I'm too busy to clean it up now.
     */
    public UrlPresetsEditor(UrlPresets up) {
	  super();
	  presets = up;
	  presetEditPanel = new PresetEditPanel();
	  add(presetEditPanel);
    }
    
    /**
     * pops up a configuration dialog.
     */
	 public void showConfig(Frame f) {
	   if (window == null) {
		 Dialog d = new Dialog(f, getTitle(), false);
		 d.setLayout(new BorderLayout());
		 presetEditPanel = new PresetEditPanel();
		 d.add(presetEditPanel);
		 d.add(new JLabel(getTitle()), BorderLayout.NORTH);
		 d.add(new ButtonPanel(), BorderLayout.SOUTH);
		 d.addWindowListener(new WindowAdapter (){
		   public void windowClosing(WindowEvent we) 
		   {we.getWindow().hide();}
		 });
		 d.pack();
		 window = d;
	   }
	   window.show();
	 }

    public static void main(String [] argv) {
	UrlPresets p = new UrlPresets(new DummyConfigNode("UrlPresets"));
	UrlPresetsEditor e  = new UrlPresetsEditor(p);
	Frame f = new Frame(e.getTitle());
	e.addToFrame(f);

	f.addWindowListener(new WindowAdapter (){
		public void windowClosing(WindowEvent we) 
		{System.exit(0);}
	    });
	f.pack();
	f.show();
    }

    public void addToFrame(Frame f) {
	  f.setLayout(new BorderLayout());
	  presetEditPanel = new PresetEditPanel();
	  f.add(presetEditPanel);
	  //	f.add(new Label(getTitle(),Label.CENTER), BorderLayout.NORTH);
	  f.add(new ButtonPanel(), BorderLayout.SOUTH);
	  window = f;
    }
	private String title = "Url Preset Editor";
    public String getTitle() {return title;}
	public void setTitle(String s) { title = s;}

	private PresetEditPanel presetEditPanel;

	public void synchronizeTo() {
			presetEditPanel.saveAll();
	}
	public void synchronizeFrom() {
			  presetEditPanel.redoLayout();
	}
    //inner classes
    private class ButtonPanel extends JPanel {
	ButtonPanel() {
	    JButton save_button = new JButton("Save");
	    save_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			  synchronizeTo();
			  window.hide();
		    }
		});
	    add(save_button);

	    JButton cancel_button = new JButton("Cancel");
	    cancel_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			  synchronizeFrom();
			  window.hide();
		    }
		});
	    add(cancel_button);
	}
    }    

    private class PresetEditPanel extends JPanel {
	  PresetEditPanel () {
		redoLayout();
	  }
	  private GridBagConstraints gbc;
	  private JCheckBox[] presetEnablings;
	  private JRadioButton[] defaultButtons;
	  private JTextField[] presetNames;
	  private JTextField[] presetHeaders;
	  private JTextField[] presetTemplates;
	  
	  public void redoLayout() {
		  String [] preset;
		  preset = presets.getPresetNames();
		  int nPresets = preset.length;
		  removeAll();
		  setLayout(new GridBagLayout());
		  gbc = new GridBagConstraints();
		  gbc.weighty = 100;
		  gbc.gridwidth = 1;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  gbc.anchor = GridBagConstraints.NORTH;
		  gbc.gridy = 0;
		  gbc.gridx = 0;
		  gbc.gridwidth = 4;
		  gbc.weighty = 100;
		  add(new JLabel("Modify Url Presets", JLabel.CENTER), gbc);
		  gbc.gridwidth = 1;
		  gbc.weighty = 0;
		  gbc.gridy = 1;
		  gbc.gridx = 0;
		  gbc.ipadx = 15;
		  add(new JLabel("Enabled"), gbc);
		  gbc.gridx = 1;
		  add(new JLabel("Header"), gbc);
		  gbc.gridx = 2;
		  add(new JLabel("Name"), gbc);
		  gbc.gridx = 3;
		  add(new JLabel("Template"), gbc);
		  gbc.gridx = 4;
		  add(new JLabel("Default?"), gbc);

		  defaultButtons = new JRadioButton[nPresets + 1];
		  presetEnablings = new JCheckBox[nPresets + 1];
		  presetNames = new JTextField[nPresets + 1];
		  presetHeaders = new JTextField[nPresets + 1];
		  presetTemplates = new JTextField[nPresets + 1];

		  ButtonGroup bob = new ButtonGroup();
		  for (int i = 0; i < nPresets; i++) {
			gbc.gridy++;
			addPreset(i);
			bob.add(defaultButtons[i]);
		  }
		  
		  gbc.gridy++;
		  addNonePreset(nPresets);
		  bob.add(defaultButtons[nPresets]);
		  if (presets.getDefaultPreset() == -1) {
			defaultButtons[nPresets].setSelected(true);
		  } else {
			defaultButtons[presets.getDefaultPreset()].setSelected(true);
		  }
	  	}
		private void saveAll() {
		  int n = presetNames.length - 1; //for null...
		  for (int i = 0; i < n; i++) {
			presets.setPresetHeader(i, presetHeaders[i].getText());			
		  }
		  for (int i = 0; i < n; i++) {
			presets.setPresetName(i, presetNames[i].getText());			
		  }
		  for (int i = 0; i < n; i++) {
			presets.setPresetTemplate(i, presetTemplates[i].getText());			
		  }
		  for (int i = 0; i < n; i++) {
			presets.setPresetEnabled(i, presetEnablings[i].isSelected());			
		  }
		  
		}
		private void addPreset(int i) {
		  final int index = i;
		  final JTextField templateField = new JTextField(50);
		  final JTextField nameField = new JTextField();
		  final JTextField headerField = new JTextField();
		  final JCheckBox enabledField = new JCheckBox();
		  
		  
		  gbc.gridx = 0;
		  enabledField.setSelected((presets.getPresetEnablings()) [index]);
		  presetEnablings[index] = enabledField;
		  add(enabledField, gbc);
		  
		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  headerField.setText((presets.getPresetHeaders()) [index]);
		  presetHeaders[index] = headerField;
		  add(headerField, gbc);
		  
		  gbc.gridx = 2;
		  nameField.setText((presets.getPresetNames()) [index]);
		  presetNames[index] = nameField;
		  add(nameField, gbc);
		  
		  gbc.gridx = 3;
		  gbc.weightx = 100;
		  templateField.setText(presets.getTemplate(index));
		  presetTemplates[index] = templateField;
		  add(templateField, gbc);
		  
		  gbc.gridx = 4;
		  gbc.weightx = 0;
		  JRadioButton set = new JRadioButton();
		  defaultButtons[index] = set;
		  set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  presets.setDefaultPreset(index);
			}
		  });
		  add(set, gbc);
		}

		private void addNonePreset(int i) {
		  final int index = i;
		  final JTextField templateField = new JTextField();
		  final JTextField nameField = new JTextField();
		  gbc.gridx = 2;
		  nameField.setText("None");
		  nameField.setEditable(false);
		  presetNames[index] = nameField;
		  add(nameField, gbc);
		  gbc.gridx = 3;
		  gbc.weightx = 100;
//		  templateField.setText(presets.getTemplate(index));
		  presetTemplates[index] = null;
//		  add(templateField, gbc);
		  gbc.gridx = 4;
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
	}
}

