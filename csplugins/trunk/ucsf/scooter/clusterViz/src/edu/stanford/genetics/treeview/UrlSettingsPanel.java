/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: UrlSettingsPanel.java,v $
 * $Revision: 1.4 $B
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


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
* This class displays editable Url settings.
*
* It requires a UrlExtractor, HeaderInfo and optionally a UrlPresets
*/
public class UrlSettingsPanel extends JPanel implements SettingsPanel {
	private UrlExtractor urlExtractor;
	private UrlPresets urlPresets = null;
	private HeaderInfo headerInfo;

	private JFrame top;
	private JDialog d;
    private Window window;
	public UrlSettingsPanel(UrlExtractor ue, UrlPresets up) {
		this(ue, ue.getHeaderInfo(), up);
	}
	public UrlSettingsPanel(UrlExtractor ue, HeaderInfo hi, UrlPresets up) {
		super();
		urlExtractor = ue;
		urlPresets = up;
		headerInfo = hi;
		templateField = new TemplateField();
		templateField.setText(urlExtractor.getUrlTemplate());

		redoLayout();
		updatePreview();
		UrlSettingsPanel.this.setEnabled(urlExtractor.isEnabled());
		
	}

    public static void main(String [] argv) {
	UrlPresets p = new UrlPresets(new DummyConfigNode("UrlPresets"));
	HeaderInfo hi = new DummyHeaderInfo();
	UrlExtractor ue = new UrlExtractor(hi);

	UrlSettingsPanel e  = new UrlSettingsPanel( ue, hi, p);
	Frame f = new Frame("Url Settings Test");
	f.add(e);
	f.addWindowListener(new WindowAdapter (){
		public void windowClosing(WindowEvent we) 
		{System.exit(0);}
	    });
	f.pack();
	f.show();
    }

	public void synchronizeFrom() {
		redoLayout();
		UrlSettingsPanel.this.setEnabled(urlExtractor.isEnabled());
	}

	public void synchronizeTo() {
	  //nothing to do...
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
				UrlSettingsPanel.this.setEnabled(enableBox.isSelected());
			  }
			});
			add(enableBox, BorderLayout.CENTER);
			
	  }
	  public boolean isSelected() {
		return enableBox.isSelected();
	  }
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
		showDialog(f, "Url Settings Test");
	}

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

	  private GridBagConstraints gbc;
	  public void redoLayout() {
		  String [] preset;
		  preset = urlPresets.getPresetNames();
		  int nPresets = preset.length;
		  removeAll();
		  setLayout(new GridBagLayout());
		  gbc = new GridBagConstraints();
		  gbc.gridwidth = 1;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  gbc.anchor = GridBagConstraints.NORTH;
		  gbc.gridy = 0;
		  gbc.gridx = 0;
		  gbc.weightx = 100;
		  final JCheckBox enableBox = new JCheckBox("Enable", urlExtractor.isEnabled());
		  enableBox.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  urlExtractor.setEnabled(enableBox.isSelected());
				  UrlSettingsPanel.this.setEnabled(enableBox.isSelected());
			  }
		  });
		  add(enableBox, gbc);
		  gbc.gridx  = 1;
		  add(templateField, gbc);
		  gbc.gridx  = 2;
		  headerChoice  = new HeaderChoice();
		  gbc.fill = GridBagConstraints.NONE;
		  gbc.weightx = 0;
		  add(headerChoice, gbc);
		  gbc.gridx  = 0;
		  gbc.gridy = 1;
		  gbc.gridwidth = 3;
		  gbc.fill = GridBagConstraints.HORIZONTAL;
		  previewField = new JTextField("Ex: " + urlExtractor.getUrl(0));
//		  previewField = new JTextField(urlExtractor.substitute(tester));
		  previewField.setEditable(false);
		  add(previewField, gbc);
		  JPanel presetPanel = new JPanel();
		  buttons = new JButton[nPresets];
		  for (int i = 0; i < nPresets; i++) {
			JButton presetButton = new JButton((urlPresets.getPresetNames()) [i]);
			final int index = i;
			presetButton.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				templateField.setText(urlPresets.getTemplate(index));
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


		  try {
			  headerChoice.setSelectedIndex(urlExtractor.getIndex());
		  } catch(  java.lang.IllegalArgumentException e) {
		  }
	  }

		private void updatePreview() {
		  urlExtractor.setUrlTemplate(templateField.getText());
		  urlExtractor.setIndex(headerChoice.getSelectedIndex());
		  previewField.setText("Ex: " + urlExtractor.getUrl(0));
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
