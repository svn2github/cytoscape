/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: UrlEditor.java,v $
 * $Revision: 1.4 $
 * $Date: 2004/12/21 03:28:14 $
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
public class UrlEditor {
    private UrlExtractor extractor;
    private UrlPresets presets;
    private Window window;
    private HeaderInfo headerInfo;

    /**
     * This class must be constructed around a HeaderInfo
     */
    public UrlEditor(UrlExtractor ue, UrlPresets up, HeaderInfo hI) {
	  super();
	  extractor = ue;
	  presets = up;
	  headerInfo = hI;
    }
    
    /**
     * pops up a configuration dialog.
     */
    public void showConfig(Frame f) {
	if (window == null) {
	    Dialog d = new Dialog(f, getTitle(), false);
	    d.setLayout(new BorderLayout());
	    d.add(new UrlEditPanel());
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
	UrlEditor e  = new UrlEditor(new UrlExtractor(null), 
				     p,
				     null);
	Frame f = new Frame(getTitle());
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
	f.add(new UrlEditPanel());
	//	f.add(new Label(getTitle(),Label.CENTER), BorderLayout.NORTH);
	f.add(new ButtonPanel(), BorderLayout.SOUTH);
	window = f;
    }

    private static String getTitle() {return "Url Link Editor";}

    //inner classes
    private class ButtonPanel extends Panel {
	ButtonPanel() {
	    JButton close_button = new JButton("Close");
	    close_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			window.dispose();
		    }
		});
	    add(close_button);
	}
    }    
    private class UrlEditPanel extends Panel {
	  UrlEditPanel () {
		redoLayout();
		templateField.setText(extractor.getUrlTemplate());
		headerChoice.select(extractor.getIndex());
		updatePreview();
	  }
	  private GridBagConstraints gbc;
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
		  gbc.weighty = 0;
		  addTemplate();
		  gbc.gridy  = 1;
		  addHeader();
		  gbc.gridy = 2;
		  addPreview();
		  gbc.gridy = 3;
		  gbc.gridx = 0;
		  gbc.gridwidth = 3;
		  gbc.weighty = 100;
		  add(new JLabel("Url Presets (Can edit under Program Menu)", JLabel.CENTER), gbc);
		  gbc.gridwidth = 1;
		  for (int i = 0; i < nPresets; i++) {
		  gbc.gridy++;
			addPreset(i);
		  }
	  	}

		String tester = "YAL039W";
		JTextField previewField;
		private void addPreview() {
		  gbc.gridx = 0;
		  gbc.weightx = 0;
		  add(new JLabel("Preview:"), gbc);
		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  previewField = new JTextField(extractor.substitute(tester));
		  previewField.setEditable(false);
		  add(previewField, gbc);
		  JButton update= new JButton("Update");
		  update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
		    updatePreview();
			}
		  });
		  gbc.gridx = 2;
		  gbc.weightx = 0;
//		  add(update, gbc);
		}

		private void updatePreview() {
		  extractor.setUrlTemplate(templateField.getText());
		  extractor.setIndex(headerChoice.getSelectedIndex());
		  previewField.setText(extractor.getUrl(0));
		}

		private HeaderChoice headerChoice;
		private void addHeader() {
		  gbc.gridx = 0;
		  gbc.weightx = 0;
		  add(new JLabel("Header:"), gbc);
		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  headerChoice  = new HeaderChoice();
		  add(headerChoice, gbc);
		}



    private class HeaderChoice extends Choice implements ItemListener {
	  HeaderChoice() {
	    super();
	    String [] headers;
		int lastI;
	    if (headerInfo != null) {
		  headers = headerInfo.getNames();
		  lastI = headers.length;
		  if (headerInfo.getIndex("GWEIGHT") != -1) {
			lastI--;
		  }
	    } else {
		  headers = new String [] {"Dummy1", "Dummy2", "Dummy3"};
		  lastI = headers.length;
	    }

	    for (int i = 0; i < lastI; i++) {
		  add(headers[i]);
	    }
	    addItemListener(this);
	  }
	  public void itemStateChanged(ItemEvent e) {
	    updatePreview();
	  }
    }
    


		private TemplateField templateField;
		private void addTemplate() {
		  gbc.gridx = 0;
		  gbc.weightx = 0;
		  add(new JLabel("Template:"), gbc);
		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  templateField = new TemplateField();
		  add(templateField, gbc);
		  JButton updateButton = new JButton ("Update");
		  updateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  updatePreview();
			}
		  });
		  gbc.gridx = 2;
		  gbc.weightx = 0;
		  add(updateButton, gbc);
		}
		private class TemplateField extends TextField {
		  TemplateField () {
			super("enter url template");
			addActionListener(new ActionListener() {
		    	public void actionPerformed(ActionEvent e) {
				  updatePreview();
				}
			});
		  }
		}

		private void addPreset(int i) {
		  final int index = i;
		  gbc.gridx = 0;
		  add(new JLabel((presets.getPresetNames()) [index]), gbc);
		  gbc.gridx = 1;
		  gbc.weightx = 100;
		  add(new JTextField(presets.getTemplate(index)), gbc);
		  gbc.gridx = 2;
		  gbc.weightx = 0;
		  JButton set = new JButton("Set");
		  set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  templateField.setText(presets.getTemplate(index));
			  updatePreview();
			}
		  });
		  add(set, gbc);
		}
	}
}

