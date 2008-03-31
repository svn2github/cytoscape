/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: DisplaySettingsPanel.java,v $
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

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.app.*;
import edu.stanford.genetics.treeview.model.TVModel;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.io.*;

class DisplaySettingsPanel extends JPanel implements SettingsPanel {
	
	private KaryoColorPresets presets;
	/** Setter for presets */
	public void setPresets(KaryoColorPresets presets) {
		this.presets = presets;
	}
	/** Getter for presets */
	public KaryoColorPresets getPresets() {
		return presets;
	}
	
	private KaryoPanel karyoPanel;
	/** Setter for karyoPanel */
	public void setKaryoPanel(KaryoPanel karyoPanel) {
		this.karyoPanel = karyoPanel;
	}
	/** Getter for karyoPanel */
	public KaryoPanel getKaryoPanel() {
		return karyoPanel;
	}
	

	public static final void main(String [] argv) {
		LinkedViewApp statview = new LinkedViewApp();
		ViewFrame testf = new LinkedViewFrame(statview);

		KaryoPanel kp = new KaryoPanel(new TVModel(),  new TreeSelection(2), testf, new DummyConfigNode("Display Settings Panel"));
		KaryoColorPresets kcp = new KaryoColorPresets();


		DisplaySettingsPanel panel = new DisplaySettingsPanel(kp, kcp, testf);
		panel.revalidate();
		JFrame test = new JFrame("Test Display Settings Panel");
		test.getContentPane().add(panel);
		test.pack();
		test.show();
	}
	
	public DisplaySettingsPanel(KaryoPanel karyoPanel, KaryoColorPresets presets, ViewFrame frame) {
		setKaryoPanel(karyoPanel);
		setPresets(presets);
		setFrame(frame);
		addWidgets();
	}

	private ViewFrame frame  = null;
	/** Setter for frame */
	public void setFrame(ViewFrame frame) {
		this.frame = frame;
	}
	/** Getter for frame */
	public ViewFrame getFrame() {
		return frame;
	}
	private ColorConfigPanel colorPanel;
	private ColorPresetsPanel colorPresetsPanel;
	private DrawPanel drawPanel;
	private ScalePanel scalePanel;
	private SelectedPanel selectedPanel;
	private void addWidgets() {
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 100;
		gc.weighty = 100;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		
		add(new JLabel("Draw"),gc);
		gc.gridx = 1;
		drawPanel = new DrawPanel();
		add(drawPanel,gc);
		
		gc.gridx = 0;
		gc.gridy = 1;
		add(new JLabel("Scale Lines"), gc);
		gc.gridx = 1;
		scalePanel = new ScalePanel();
		add(scalePanel,gc);
		
		gc.gridx = 0;
		gc.gridy = 2;
		add(new JLabel("Colors"), gc);
		gc.gridx = 1;
		colorPanel = new ColorConfigPanel();
		add(colorPanel,gc);
		
		gc.gridx = 0;
		gc.gridy = 3;
		add(new JLabel("Selected"), gc);
		gc.gridx = 1;
		selectedPanel = new SelectedPanel();
		add(selectedPanel,gc);
	}
	
	public void synchronizeTo() {
		selectedPanel.setValues();
		drawPanel.setValues();
		scalePanel.setValues();
	}
	
	public void synchronizeFrom() {
		selectedPanel.getValues();
		drawPanel.getValues();
		scalePanel.getValues();
	}
	
	/**
	* panel with checkboxes for whether to draw lines and/or bars
	*/
	class DrawPanel extends JPanel {
		JCheckBox lineBox, barBox;
		DrawPanel() {
			setAlignmentX(JPanel.LEFT_ALIGNMENT);
			lineBox = new JCheckBox("lines");
			barBox = new JCheckBox("bars");
			add(lineBox);
			add(barBox);
			lineBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
			barBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
		}
		public void getValues() {
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			lineBox.setSelected(karyoDrawer.getLineChart());
			barBox.setSelected(karyoDrawer.getBarChart());
			revalidate();
		}
		public void setValues() {
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			karyoDrawer.setLineChart(lineBox.isSelected());
			karyoDrawer.setBarChart(barBox.isSelected());
			karyoDrawer.notifyObservers();
		}
	}
	
	/**
	* panel with checkboxes and configuration for scale lines
	*/
	class ScalePanel extends JPanel {
		JCheckBox aboveBox, belowBox;
		JTextField baseField, maxField;
		ScalePanel() {
			setAlignmentX(JPanel.LEFT_ALIGNMENT);
			aboveBox = new JCheckBox("above");
			belowBox = new JCheckBox("below");
			baseField = new JTextField("2.0");
			maxField = new JTextField("5");

			add(aboveBox);
			add(belowBox);
			add(new JLabel(" base"));
			add(baseField);
			add(new JLabel(" #"));
			add(maxField);
			
			aboveBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
			belowBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
			baseField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate  (DocumentEvent e) { setBase();}
				public void removeUpdate  (DocumentEvent e) { setBase();}
				public void changedUpdate (DocumentEvent e) { setBase();}
			});
			maxField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate  (DocumentEvent e) { setMax();}
				public void removeUpdate  (DocumentEvent e) { setMax();}
				public void changedUpdate (DocumentEvent e) { setMax();}
			});
		}
		public void getValues() {
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			aboveBox.setSelected(karyoDrawer.getLinesAbove());
			belowBox.setSelected(karyoDrawer.getLinesBelow());
			baseField.setText(reformatDouble(karyoDrawer.getLinesBase()));
			int max = karyoDrawer.getLinesMax();
			maxField.setText(reformatInt(max));
			revalidate();
		}

		public void setBase() {
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			try {
				Double temp = new Double(baseField.getText());
				karyoDrawer.setLinesBase(temp.doubleValue());
			} catch (java.lang.NumberFormatException e) {
			}
			karyoDrawer.notifyObservers();
		}
		public void setMax() { 
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			try {
				Integer temp = new Integer(maxField.getText());
				karyoDrawer.setLinesMax(temp.intValue());
			} catch (java.lang.NumberFormatException e) {
			}
			karyoDrawer.notifyObservers();
		}

		public void setValues() {
			KaryoDrawer karyoDrawer= karyoPanel.getKaryoDrawer();
			karyoDrawer.setLinesAbove(aboveBox.isSelected());
			karyoDrawer.setLinesBelow(belowBox.isSelected());
			setBase();
			setMax();
			karyoDrawer.notifyObservers();
		}
	}
	
	/**
	* Panel which allows configuration of all colors
	*/
	class ColorConfigPanel extends JPanel {
		private final ColorPanel []  colorPanels = new ColorPanel[6];
		ColorConfigPanel() {
			try {
				setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
			} catch (java.lang.NoSuchMethodError err) {
				// god damn MRJ for os 9.
			}
			for (int i =0;i <6;i++) {
				colorPanels[i] = new ColorPanel(i);
			}
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel row1 = new JPanel();
			row1.add(colorPanels[0]);
			row1.add(colorPanels[1]);
			row1.add(colorPanels[2]);
			add(row1);
			JPanel row2 = new JPanel();
			row2.add(colorPanels[3]);
			row2.add(colorPanels[4]);
			row2.add(colorPanels[5]);
			add(row2);
			
			
			JPanel row3 = new JPanel();
			JButton loadButton = new JButton("Load...");
			loadButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showOpenDialog(DisplaySettingsPanel.this);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
						colorSet.load(f.getPath());
						for (int i =0;i <6;i++) {
							colorPanels[i].redoColor();
						}
						repaint();
						karyoPanel.getKaryoView().repaint();
							/*
							try {
						} catch (IOException ex) {
							JOptionPane.showMessageDialog(DisplaySettingsPanel.this, "Could not load from " + f.toString() + "\n" + ex);
						}
*/
					}
				}
			});
			row3.add(loadButton);

			JButton saveButton = new JButton("Save...");
			saveButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JFileChooser chooser = new JFileChooser();
					int returnVal = chooser.showSaveDialog(DisplaySettingsPanel.this);
					if(returnVal == JFileChooser.APPROVE_OPTION) {
						File f = chooser.getSelectedFile();
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
							colorSet.save(f.getPath());
					}
				}
			});
			row3.add(saveButton);

			JButton makeButton = new JButton("Make Preset");
			makeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					KaryoColorSet temp = new KaryoColorSet();
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
					temp.copyStateFrom(colorSet);
					temp.setName("UserDefined");
					presets.addColorSet(temp);
					colorPresetsPanel.redoLayout();
					colorPresetsPanel.invalidate();
					colorPresetsPanel.revalidate();
					colorPresetsPanel.repaint();
				}
			});
			row3.add(makeButton);

			add(row3);
			colorPresetsPanel = new ColorPresetsPanel();
			add(new JScrollPane(colorPresetsPanel));
		}
		public void copyStateFrom(KaryoColorSet otherSet) {
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
			colorSet.copyStateFrom(otherSet);
			for (int i =0;i <6;i++) {
				colorPanels[i].redoColor();
			}
			repaint();
			karyoPanel.getKaryoView().repaint();
		}
		public void getValues() {}
		public void setValues() {}
	}

	/**
	* this class allows the presets to be selected...
	*/
	class ColorPresetsPanel extends JPanel {
	  ColorPresetsPanel() {
		redoLayout();
	  }
	  public void redoLayout() {
		removeAll();
		int nPresets = presets.getNumPresets();
		JButton [] buttons = new JButton[nPresets];
		for (int i = 0; i < nPresets; i++) {
		  JButton presetButton = new JButton((presets.getPresetNames()) [i]);
		  final int index = i;
		  presetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			  colorPanel.copyStateFrom(presets.getColorSet(index));
			}
		  });
		  add(presetButton);
		  buttons[index] = presetButton;
		}
	  }
	}
	/**
	* inner class, must be inner so it can notify karyoDrawer when it changes the colorSet.
	*/
	public class ColorPanel extends JPanel {
		ColorIcon colorIcon;
		int type;
		public ColorPanel(int i) {
			type = i;
			redoComps();
		} 
		public void redoColor() {
			colorIcon.setColor(getColor());
		}
		public void redoComps() {
			removeAll();
			colorIcon = new ColorIcon(10, 10, getColor());
			JButton pushButton = new JButton(getLabel(), colorIcon);
			pushButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color trial = JColorChooser.showDialog(ColorPanel.this, "Pick Color for " + getLabel(), getColor());
					if (trial != null) {
						setColor(trial);
						karyoPanel.getKaryoView().repaint();
					}
				}
			});
			
			add(pushButton);
		}
		private void setColor(Color c) {
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
			colorSet.setColor(type, c);
			colorIcon.setColor(getColor());
			repaint();
		}
		private String getLabel() {
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
			return colorSet.getType(type);
		}
		private Color getColor() {
			KaryoDrawer karyoDrawer = getKaryoPanel().getKaryoDrawer();
			KaryoColorSet colorSet = karyoDrawer.getKaryoColorSet();
			return colorSet.getColor(type);
		}
	}
	class SelectedPanel extends JPanel {
		JComboBox iconBox, iconSize;
		private KaryoDrawer karyoDrawer = null;
		/**
		 *  Constructor for the SizePanel object
		 */
		public SelectedPanel() {
			if (karyoPanel != null) karyoDrawer = karyoPanel.getKaryoDrawer();
			iconBox = new JComboBox();
			
			String [] types = karyoDrawer.getIconTypes();
			for (int i = 0; i <types.length ; i++) {
				iconBox.addItem(types[i]);				
			}
			

			iconSize = new JComboBox();
			int [] sizes = karyoDrawer.getIconSizes();
			for (int i = 0 ; i < sizes.length; i++) {
				iconSize.addItem(reformatInt(sizes[i]));
			}
			add(new JLabel("Highlight Selected with "));
			add(iconBox);
			add(iconSize);
			
			getValues();

			iconBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			}
			);
			iconSize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
		}

		public void getValues() {
			if (karyoDrawer != null) {
				iconBox.setSelectedIndex(karyoDrawer.getIconType());
				iconSize.setSelectedIndex(karyoDrawer.getIconSize());
				revalidate();
			}
		}

		public void setValues() {
			if (karyoDrawer != null) {
				karyoDrawer.setIconType(iconBox.getSelectedIndex());
				karyoDrawer.setIconSize(iconSize.getSelectedIndex());
				karyoDrawer.notifyObservers();
			}
		}
	}
	private static String reformatInt(int td) {
		Integer tx  = new Integer(td);
		return tx.toString();
	}
	private static String reformatDouble(double td) {
		int order = 1;
		if (Math.abs(td) < 0.0000001) {
			return "0.0000";
		} 
		while (Math.abs(td * order) < 1000) {
			order *= 10;
		}
		int val    = (int) (td * order);
		Double tx  = new Double(((double) val) / order);
		return tx.toString();
	}

}

