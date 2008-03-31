/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: FUOptionsPanel.java,v $
 * $Revision: 1.1 $B
 * $Date: 2006/08/16 19:13:45 $
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
package edu.stanford.genetics.treeview.plugin.dendroview;

import edu.stanford.genetics.treeview.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* This class allows selection of Font and Url options.
*
* It requires a FontSelectable, a UrlExtractor, and optionally a UrlPresets
*/
public class FUOptionsPanel extends JPanel implements SettingsPanel {
	private FontSelectable fontSelectable;
	private UrlExtractor urlExtractor;
	private UrlPresets urlPresets = null;
	private UrlPanel urlPanel = null;
	private HeaderInfo headerInfo;
	private JFrame top;
	private JDialog d;
    private Window window;
	private JPanel innerPanel; // seems to help with the tabbedpane, go figure...
	public FUOptionsPanel(FontSelectable fs, UrlExtractor ue, UrlPresets up, HeaderInfo hi) {
		fontSelectable = fs;
		urlExtractor = ue;
		urlPresets = up;
		headerInfo = hi;
		innerPanel = new JPanel();
		add(innerPanel);
		setupWidgets();
	}
	public String getTitle() {return "Font and Url Options";}
    public static void main(String [] argv) {
	UrlPresets p = new UrlPresets(new DummyConfigNode("UrlPresets"));
	HeaderInfo hi = new DummyHeaderInfo();
	UrlExtractor ue = new UrlExtractor(hi);
	FontSelectable fs = new TextView(hi, ue);
	fs.setPoints(10);
	FUOptionsPanel e  = new FUOptionsPanel(fs, ue, p, hi);
	Frame f = new Frame(e.getTitle());
	f.add(e);
	f.addWindowListener(new WindowAdapter (){
		public void windowClosing(WindowEvent we) 
		{System.exit(0);}
	    });
	f.pack();
	f.show();
    }

	public void synchronizeFrom() {
	  setupWidgets();
	}

	public void synchronizeTo() {
	  //nothing to do...
	}
	private void setupWidgets() {
	  innerPanel.removeAll();
	  	  GridBagLayout gbl  = new GridBagLayout();
//		  setBackground(Color.red);
	  innerPanel.setLayout(gbl);
	  GridBagConstraints gbc  = new GridBagConstraints();
	  gbc.gridx = 0;
	  gbc.gridy = 0;
	  innerPanel.add(Box.createVerticalStrut(10), gbc);
	  gbc.gridy = 1;
	  gbc.gridwidth = 1;
	  gbc.insets = new Insets(5,5,5,5);
	  gbc.fill = GridBagConstraints.BOTH;
	  gbc.anchor = GridBagConstraints.NORTHEAST;
	  innerPanel.add(new JLabel ("Font:", JLabel.LEFT), gbc);

	  gbc.gridy = 2;
	  gbc.gridx = 0;
	  innerPanel.add(Box.createVerticalStrut(20), gbc);
	  
	  gbc.gridy = 3;
	  EnablePanel enablePanel = new EnablePanel();
	  // should make panel which includes enable checkbox...
	  innerPanel.add(enablePanel, gbc);
	  
	  gbc.gridy = 1;
	  gbc.gridx = 1;
	  innerPanel.add(new FontSettingsPanel(fontSelectable), gbc);

	  gbc.gridy = 3;
	  gbc.weightx= 100;
	  gbc.weighty= 100;
	  urlPanel =new UrlPanel(urlExtractor, urlPresets, headerInfo);
	  urlPanel.setEnabled(enablePanel.isSelected());
	  innerPanel.add(urlPanel, gbc);
	}
	class EnablePanel extends JPanel {
	  JCheckBox enableBox;
	  EnablePanel() {
		setLayout(new BorderLayout());
			add(new JLabel ("Web Link:", JLabel.LEFT), BorderLayout.NORTH);
			enableBox = new JCheckBox("Enable", urlExtractor.isEnabled());
			enableBox.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				urlExtractor.setEnabled(enableBox.isSelected());
				urlPanel.setEnabled(enableBox.isSelected());
			  }
			});
			add(enableBox, BorderLayout.CENTER);
			
	  }
	  public boolean isSelected() {
		return enableBox.isSelected();
	  }
	}
	
	
	/**
	 *  Create a toplevel frame with this component in it
	 */
	public void makeTop() {
		top = new JFrame(getTitle());
		top.add(this);
		top.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					we.getWindow().dispose();
				}
			});
		top.pack();
		top.show();
	}


	//
// the allowed font styles
//
	/**
	 *  Description of the Field
	 */
	public final static String[] styles  = {
			"Plain",
			"Italic",
			"Bold",
			"Bold Italic"
			};

	/**
	* turn a style number from class java.awt.Font into a string
	 *
	 * @param  style  style index
	 * @return        string description
	 */
	public final static String decode_style(int style) {
		switch (style) {
						case Font.PLAIN:
							return styles[0];
						case Font.ITALIC:
							return styles[1];
						case Font.BOLD:
							return styles[2];
						default:
							return styles[3];
		}
	}

	/**
	* turn a string into a style number
	 *
	 * @param  style  string description
	 * @return        integer encoded representation
	 */
	public final static int encode_style(String style) {
		return
				style == styles[0] ? Font.PLAIN :
				style == styles[1] ? Font.ITALIC :
				style == styles[2] ? Font.BOLD :
				Font.BOLD + Font.ITALIC;
	}

	/**
	 *  Create a blocking dialog containing this component
	 *
	 * @param  f  frame to block
	 */
	public void showDialog(Frame f, String title) {
		d = new JDialog(f, title);
		window = d;
		d.setLayout(new BorderLayout());
		d.add(this, BorderLayout.CENTER);
		d.add(new ButtonPanel(), BorderLayout.SOUTH);
		d.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					we.getWindow().dispose();
				}
			});
		d.pack();
		d.show();
	}
	public void showDialog(Frame f) {
		showDialog(f, getTitle());
	}

	class UrlPanel extends JPanel {
	  
	      private UrlExtractor extractor;
    private UrlPresets presets;
    private Window window;
    private HeaderInfo headerInfo;
	private String tester = "YAL039W";
	private JButton[] buttons;
	private JTextField previewField;
		private TemplateField templateField;
		private HeaderChoice headerChoice;

		
		public void setEnabled(boolean b) {
		  templateField.setEnabled(b);
		  headerChoice.setEnabled(b);
		  previewField.setEnabled(b);
		  for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] != null)
			  buttons[i].setEnabled(b);
			  
		  }
		}
    /**
     * This class must be constructed around a HeaderInfo
     */
    public UrlPanel(UrlExtractor ue, UrlPresets up, HeaderInfo hI) {
	  super();
	  extractor = ue;
	  presets = up;
	  headerInfo = hI;
	  redoLayout();
	  templateField.setText(extractor.getUrlTemplate());
	  try {
		headerChoice.setSelectedIndex(extractor.getIndex());
	  } catch(  java.lang.IllegalArgumentException e) {
	  }
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
		  gbc.gridwidth = 1;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  gbc.anchor = GridBagConstraints.NORTH;
		  gbc.gridy = 0;
		  gbc.weightx = 100;
		  templateField = new TemplateField();
		  add(templateField, gbc);
		  gbc.gridx  = 1;
		  headerChoice  = new HeaderChoice();
		  gbc.fill = GridBagConstraints.NONE;
		  gbc.weightx = 0;
		  add(headerChoice, gbc);
		  gbc.gridx  = 0;
		  gbc.gridy = 1;
		  gbc.gridwidth = 2;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  previewField = new JTextField(extractor.substitute(tester));
		  previewField.setEditable(false);
		  add(previewField, gbc);
		  JPanel presetPanel = new JPanel();
		  buttons = new JButton[nPresets];
		  for (int i = 0; i < nPresets; i++) {
			JButton presetButton = new JButton((presets.getPresetNames()) [i]);
			final int index = i;
			presetButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				templateField.setText(presets.getTemplate(index));
				updatePreview();
			  }
			});
				presetPanel.add(presetButton);
				buttons[index] = presetButton;
		  }
		  gbc.gridy = 2;
		  gbc.fill = GridBagConstraints.BOTH;
		  gbc.weighty = 100;
		  gbc.weightx = 100;
//		  add(new JScrollPane(presetPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);
		  add(presetPanel, gbc);
	  	}

		private void updatePreview() {
		  extractor.setUrlTemplate(templateField.getText());
		  extractor.setIndex(headerChoice.getSelectedIndex());
		  previewField.setText("Ex: " + extractor.getUrl(0));
		}

		private void setupPreview() {
		  previewField = new JTextField("Ex: " + extractor.getUrl(0));
		  previewField.setEditable(false);
		}

		private void setupHeader() {
		  headerChoice  = new HeaderChoice();
		}



    private class HeaderChoice extends JComboBox implements ItemListener {
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
		  if (headers[i] == null) {
			addItem("-- NULL --");
		  } else {
			addItem(headers[i]);
		  }
		}
	    addItemListener(this);
	  }
	  public void itemStateChanged(ItemEvent e) {
	    updatePreview();
	  }
    }
    


		private class TemplateField extends JTextField {
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


	class FontPanel extends JPanel {
	  
	private String[] fonts               = FontSelector.fonts;
	/*
	 *  {
	 *  "Courier",
	 *  "Default",
	 *  "Dialog",
	 *  "DialogInput",
	 *  "Helvetica",
	 *  "TimesRoman",
	 *  "ZapfDingbats"
	 *  };
	 */

	 private String title;
	private JComboBox font_choice;
	private JComboBox style_choice;
	private NatField size_field;
	private JButton display_button;
	private FontSelectable client;
	private JLabel exampleField;
	private JButton updateButton;

	String size_prop, face_prop, style_prop;



	FontPanel(FontSelectable client) {
	  this.client = client;
	  setupWidgets();
	  				  updateExample();

	}
	  
	  private void setupFontChoice() {
		font_choice = new JComboBox(fonts);
		font_choice.setSelectedItem(client.getFace());
	  }
	  private void setupStyleChoice() {
		style_choice = new JComboBox(styles);
		style_choice.setSelectedItem(decode_style(client.getStyle()));
	  }

	  private void synchronizeClient() {
				  String string  = (String) font_choice.getSelectedItem();
				int i          = encode_style((String) style_choice.getSelectedItem());
				int size       = size_field.getNat();
					client.setFace(string);
					client.setStyle(i);
					client.setPoints(size);
	  }
		/**
	 *  Sets up widgets
	 */
	private void setupWidgets() {
	  GridBagLayout gbl  = new GridBagLayout();
	  setLayout(gbl);
	  GridBagConstraints gbc  = new GridBagConstraints();
	  gbc.gridx = 0;
	  gbc.gridy = 0;
	  gbc.gridwidth = 1;
	  gbc.fill = GridBagConstraints.HORIZONTAL;

	  setupFontChoice();
	  add(font_choice, gbc);

	  setupStyleChoice();
	  gbc.gridx = 1;
	  add(style_choice, gbc);
	  
	  size_field = new NatField(client.getPoints(), 3);
	  gbc.gridx = 2;
	  add(size_field, gbc);
	  
	  display_button = new JButton("Display");
	  display_button.addActionListener(
			new ActionListener() {
			  
				public void actionPerformed(ActionEvent actionEvent) {
				  updateExample();
				  synchronizeClient();
				}
			});
	  gbc.gridx = 3;
	  add(display_button, gbc);
	  gbc.gridx = 0;
	  gbc.gridy = 1;
	  gbc.gridwidth = 3;
	  gbc.fill = GridBagConstraints.BOTH;
	  exampleField = new JLabel("Example Text", JLabel.CENTER);
	  add(exampleField, gbc);
	}

	private void updateExample() {
	  				  String string  = (String)font_choice.getSelectedItem();
				int i          = encode_style((String)style_choice.getSelectedItem());
				int size       = size_field.getNat();
//				System.out.println("Setting size to " + size);
	  exampleField.setFont(new Font(string, i, size) );
	  exampleField.revalidate();
	  exampleField.repaint();
	}
	}
	
	
	private class ButtonPanel extends JPanel {
	ButtonPanel() {
	    JButton save_button = new JButton("Close");
	    save_button.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
			window.hide();
		    }
		});
	    add(save_button);

    }    
	}
}
