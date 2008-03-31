/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: ScatterParameterPanel.java,v $
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
package edu.stanford.genetics.treeview.plugin.scatterview;

import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class ScatterParameterPanel extends JPanel {
	ScatterView scatterPane;
	/** Setter for scatterPane */
	public void setScatterView(ScatterView scatterPane) {
		this.scatterPane = scatterPane;
	}
	/** Getter for scatterPane */
	public ScatterView getScatterView() {
		return scatterPane;
	}

	ScatterPanel scatterPanel;
	/** Setter for scatterPanel */
	public void setScatterPanel(ScatterPanel scatterPanel) {
		this.scatterPanel = scatterPanel;
	}
	/** Getter for scatterPanel */
	public ScatterPanel getScatterPanel() {
		return scatterPanel;
	}
	
	public ScatterParameterPanel(ScatterView scatterPane, ScatterPanel scatterPanel) {
		setScatterView(scatterPane);
		setScatterPanel(scatterPanel);
		setupWidgets();
		getValues();
	}
	
	/**
	* what should the default sizes for the crosses be?
	*/
	private static final String [] sizeInts = new String [] {"1", "3","5","7"};

	DrawPanel drawPanel;
	SizePanel sizePanel;
	ColorPanel colorPanel;
	ZoomPanel zoomPanel;
	public void setupWidgets() {
		drawPanel = new DrawPanel();
		add(drawPanel);

		sizePanel = new SizePanel();
		add(sizePanel);

		zoomPanel = new ZoomPanel();
		add(zoomPanel);
		
		colorPanel = new ColorPanel();
		add(colorPanel);
		
	}
	
	public void getValues() {
		drawPanel.getValues();
		sizePanel.getValues();
	}
	
	public void setValues() {
		drawPanel.setValues();
		sizePanel.setValues();
	}
	

	class ColorPanel extends JPanel {
		JButton colorsButton, autoButton;
		ColorPanel() {
			colorsButton = new JButton("Display...");
			colorsButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						scatterPanel.showDisplayPopup();
					}
				});
			add(colorsButton);
/*
			autoButton = new JButton("Auto ");
			autoButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						scatterPanel.scaleScatterView();
					}
				});
			add(autoButton);
		*/
		}
	}
	class SizePanel extends JPanel {
		JComboBox sizeCombo;
		SizePanel() {
			add(new JLabel(" Size"));
			sizeCombo = new JComboBox(sizeInts);
			sizeCombo.setEditable(true);
			sizeCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setValues();
				}
			});
			add(sizeCombo);
		}
		void setValues() {
			try {
				Integer val = new Integer((String) sizeCombo.getSelectedItem());
				scatterPane.setDrawSize(val.intValue());
				scatterPane.repaint();
			} catch (java.lang.NumberFormatException e) {
			}
		}
		void getValues() {
			sizeCombo.setSelectedItem("" + scatterPane.getDrawSize());
		}
	}
	
	class DrawPanel extends JPanel {
		JComboBox drawCombo;
		DrawPanel() {
			add(new JLabel("Draw"));
			drawCombo = new JComboBox(ScatterView.drawStrings);
			drawCombo.setEditable(false);
			drawCombo.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setValues();
				}
			});
			add(drawCombo);
		}
		void setValues() {
			scatterPane.setDrawOrder(drawCombo.getSelectedIndex());
			scatterPane.repaint();
		}
		void getValues() {
			drawCombo.setSelectedIndex( scatterPane.getDrawOrder());
		}
	}
	
	class ZoomPanel extends JPanel {
		JCheckBox zoomBox;
		JTextField widthField;
		JTextField heightField;
		ZoomPanel() {
			zoomBox = new JCheckBox("Dimension");
			add( zoomBox );
			widthField =  new JTextField("" +scatterPane.getWidth(),5);
			heightField =  new JTextField(""+scatterPane.getHeight(),5);
			add (widthField);
			add (new JLabel("x"));
			add (heightField);
			addListeners();
		}
		private void addListeners() {
			zoomBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					setValues();
				}
			});
			
			widthField.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {/* setValue(); */}
				public void keyReleased(KeyEvent e) {/* setValue(); */}
				public void keyTyped(KeyEvent e) {
					zoomBox.setSelected(true);
					setEnabledValue();
				}
			});
			
			widthField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate  (DocumentEvent e) { setZoomValues();}
				public void removeUpdate  (DocumentEvent e) { setZoomValues();}
				public void changedUpdate (DocumentEvent e) { setZoomValues();}
			});				
			
			
			heightField.addKeyListener(new KeyListener() {
				public void keyPressed(KeyEvent e) {/* setValue(); */}
				public void keyReleased(KeyEvent e) {/* setValue(); */}
				public void keyTyped(KeyEvent e) {
					zoomBox.setSelected(true);
					setEnabledValue();
				}
			});
			
			heightField.getDocument().addDocumentListener(new DocumentListener() {
				public void insertUpdate  (DocumentEvent e) { setZoomValues();}
				public void removeUpdate  (DocumentEvent e) { setZoomValues();}
				public void changedUpdate (DocumentEvent e) { setZoomValues();}
			});				
			
		}
		
		public void setValues() {
			setEnabledValue();
			setZoomValues();
		}
		public void setEnabledValue() {
			if (zoomBox.isSelected()) {
			} else {
				scatterPane.setPreferredSize(null);
			}
			scatterPane.invalidate();
			scatterPane.revalidate();
			scatterPane.getComponent().repaint();
		}
		void setZoomValues() {
			
			if (zoomBox.isSelected()) {
				try {
					Integer widthVal = new Integer(widthField.getText());
					Integer heightVal = new Integer(heightField.getText());
					scatterPane.setPreferredSize(new Dimension(widthVal.intValue(), heightVal.intValue()));
					scatterPane.invalidate();
					scatterPane.revalidate();
					scatterPane.getComponent().repaint();
				} catch (java.lang.NumberFormatException e) {
				}
			}
		}
		void getValues() {
			widthField.setText("" + scatterPane.getWidth());
			heightField.setText("" + scatterPane.getHeight());
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
