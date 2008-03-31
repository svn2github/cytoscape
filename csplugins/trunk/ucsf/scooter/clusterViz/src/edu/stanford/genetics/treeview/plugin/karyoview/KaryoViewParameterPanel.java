/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: KaryoViewParameterPanel.java,v $T
 * $Revision: 1.2 $
 * $Date: 2007/02/03 07:29:11 $
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *  This class graphically represents the state of the KaryoView, and allows the
 *  user to muck with it.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 */
class KaryoViewParameterPanel extends JPanel {
	/**
	 *  Client which this panel will configure
	 */
	private KaryoView karyoView;
	/**
	 *  Client which this panel will configure
	 */
	private KaryoDrawer karyoDrawer;
	/**
	 *  Used only to open some popups...
	 */
	private KaryoPanel karyoPanel;
	/**
	 *  Inner class, GUI to control Y Range
	 */
//	private ScalePanel scalePanel;
	/**
	 *  Inner class, GUI to control X and Y Scale settings
	 */
	private SizePanel sizePanel;
	/**
	 *  Inner class, GUI to set the experiment which is being viewed
	 */
	private ExperimentPanel experimentPanel;

	/**
	* Inner class, GUI to deal with selected genes.
	*/ 

	private PopupPanel popupPanel;

	/**
	 *  Constructor for the KaryoViewParameterPanel object
	 *
	 * @param  kView  Client which this panel will configure
	 */
	public KaryoViewParameterPanel(KaryoDrawer kDrawer, KaryoView kView, KaryoPanel kPanel) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		karyoDrawer = kDrawer;
		karyoView = kView;
		karyoPanel = kPanel;
		experimentPanel = new ExperimentPanel();
		add(experimentPanel);
//		scalePanel = new ScalePanel();
//		add(scalePanel);
		sizePanel = new SizePanel();
		add(sizePanel);
		popupPanel = new PopupPanel();
		add(popupPanel);
//		add(new ButtonPanel());
/*
		JPanel detailPanel    = new JPanel();
		JButton detailButton  = new JButton("More Settings...");
		detailButton.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					showDetailPopup();
				}
			});
		detailPanel.add(detailButton);
		add(detailPanel);
*/
	}


	/**
	 *  Causes the KaryoViewParameterPanel object to load values from the client
	 */
	public void getValues() {
//		scalePanel.getValues();
		sizePanel.getValues();
		experimentPanel.getValues();
//		popupPanel.getValues();
	}


	/**
	 *  Causes the KaryoViewParameterPanel object to send values to the client.
	 */
	public void setValues() {
//		scalePanel.setValues();
		sizePanel.setValues();
		experimentPanel.setValues();
//		popupPanel.setValues();
		karyoDrawer.notifyObservers();
	}


	class ExperimentPanel extends JPanel {
		JComboBox pulldown;


		/**
		 *  Constructor for the ExperimentPanel object
		 */
		public ExperimentPanel() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel holder = new JPanel();
			holder.add(new JLabel("Experiment:"));
			String[] names  = karyoView.getExperiments();
			pulldown = new JComboBox(names);
			pulldown.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
			holder.add(pulldown);
			getValues();
			add(holder);
			add(new ButtonPanel());
		}


		public void next() {
			int nextIndex  = pulldown.getSelectedIndex() + 1;
			int count      = pulldown.getItemCount();
			pulldown.setSelectedIndex(nextIndex % count);
		}


		public void prev() {
			int newval  = pulldown.getSelectedIndex() - 1;
			if (newval >= 0) {
				pulldown.setSelectedIndex(newval);
			}
		}


		public void getValues() {
			try {
				pulldown.setSelectedIndex(karyoView.getCurrentCol());
			} catch (java.lang.IllegalArgumentException e) {
			}
		}


		public void setValues() {
			karyoView.setCurrentCol(pulldown.getSelectedIndex());
		}
	}


	class ScalePanel extends JPanel {
		JTextField ppmField, ppvField;

		/**
		 *  Constructor for the ScalePanel object
		 */
		public ScalePanel() {
			DocumentListener documentListener = new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					 setValues();
					karyoDrawer.notifyObservers();
				}
				public void insertUpdate(DocumentEvent e) {
					setValues();
					karyoDrawer.notifyObservers();
				}
				public void removeUpdate(DocumentEvent e) {
					setValues();
					karyoDrawer.notifyObservers();
				}
			};

			add(new JLabel("Pixels per map: "));
			Double tmin  = new Double(karyoDrawer.getPixelPerMap());
			ppmField = new JTextField(tmin.toString());
			ppmField.setColumns(5);
			add(ppmField);
			add(new JLabel("Pixels per Value: "));
			Double tmax  = new Double(karyoDrawer.getPixelPerVal());
			ppvField = new JTextField(tmax.toString());
			ppvField.setColumns(5);
			add(ppvField);
			
			getValues();
			 ppmField.getDocument().addDocumentListener(documentListener);
			 ppvField.getDocument().addDocumentListener(documentListener);
		}


		public void getValues() {
			double tmin  = karyoDrawer.getPixelPerMap();
			double tmax  = karyoDrawer.getPixelPerVal();
			ppmField.setText(reformatDouble(tmin));
			ppvField.setText(reformatDouble(tmax));
			revalidate();
		}


		public void setValues() {
			try {
				Double tmin  = new Double(ppmField.getText());
				Double tmax  = new Double(ppvField.getText());
				karyoDrawer.setPixelPerMap(tmin.doubleValue());
				karyoDrawer.setPixelPerVal(tmax.doubleValue());
			} catch (java.lang.NumberFormatException e) {
				// ignore...
			}
		}
	}


	class SizePanel extends JPanel {
		JTextField widthField, heightField;
		private ScalePanel scalePanel;

		/**
		 *  Constructor for the SizePanel object
		 */
		public SizePanel() {
			
			DocumentListener documentListener = new DocumentListener() {
				public void changedUpdate(DocumentEvent e) {
					 setMyValues();
					karyoDrawer.notifyObservers();
				}
				public void insertUpdate(DocumentEvent e) {
					setMyValues();
					karyoDrawer.notifyObservers();
				}
				public void removeUpdate(DocumentEvent e) {
					setMyValues();
					karyoDrawer.notifyObservers();
				}
			};

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel holder = new JPanel();
			holder.add(new JLabel("Size"));
			holder.add(new JLabel("Width"));
			widthField = new JTextField();
			widthField.setColumns(5);


			holder.add(widthField);
			holder.add(new JLabel("Height"));
			heightField = new JTextField();
			heightField.setColumns(5);
			holder.add(heightField);
/*			
						JButton same  = new JButton("Update");
			same.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setValues();
					}
				});

				holder.add(same);
				*/
			JButton rescaleButton  = new JButton("Auto");
			rescaleButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						karyoView.redoScale();
					}
				});
			holder.add(rescaleButton);
			scalePanel = new ScalePanel();
			getValues();
			add(holder);

			add(scalePanel);
			/*
			* The following weirdness results if you use a documentListener here.
			*
			* setSize() forces a call to revalidate(), which causes an implicit resize.
			* the implicit resize here causes a call to getValue();
			* getValue() calls setText(), which then calls setSize();
			*/
			
			 widthField.getDocument().addDocumentListener(documentListener);
			 heightField.getDocument().addDocumentListener(documentListener);
		}

		
		
		
		public void getValues() {
			int width = karyoDrawer.getWidth();
			int height = karyoDrawer.getHeight();
//			LogPanel.println("got height " + height + " width " + width);
//			Exception e = new Exception(); e.printStackTrace();
			widthField.setText(reformatInt(width));
			heightField.setText(reformatInt(height));
			scalePanel.getValues();
			revalidate();
		}



		public void setMyValues() {
			try {
				Double tx  = new Double(widthField.getText());
				Double ty  = new Double(heightField.getText());
				karyoDrawer.setWidth((int) tx.doubleValue());
				karyoDrawer.setHeight((int) ty.doubleValue());
				karyoDrawer.notifyObservers();
			} catch (java.lang.NumberFormatException e) {
				// ignore...
			}
		}

		public void setValues() {
			try {
				scalePanel.setValues();
				Double tx  = new Double(widthField.getText());
				Double ty  = new Double(heightField.getText());
				karyoDrawer.setWidth((int) tx.doubleValue());
				karyoDrawer.setHeight((int) ty.doubleValue());
				karyoDrawer.notifyObservers();
			} catch (java.lang.NumberFormatException e) {
				// ignore...
			}
		}
	}
	class PopupPanel extends JPanel {
		/**
		 *  Constructor for the ButtonPanel object
		 */
		public PopupPanel() {
			JButton prev  = new JButton("Display...");
			prev.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						karyoPanel.showDisplayPopup();
					}
				});

			JButton same  = new JButton("Coordinates...");
			same.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						karyoPanel.showCoordinatesPopup();
					}
				});

			JButton next  = new JButton("Averaging...");
			next.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						karyoPanel.showAveragingPopup();
					}
				});

			add(prev);
			add(same);
			add(next);
		}
	}
	class SelectedPanel extends JPanel {
		JComboBox iconBox, iconSize;

		/**
		 *  Constructor for the SizePanel object
		 */
		public SelectedPanel() {
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
			});
			iconSize.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
						setValues();
				}
			});
		}

		public void getValues() {
			iconBox.setSelectedIndex(karyoDrawer.getIconType());
			iconSize.setSelectedIndex(karyoDrawer.getIconSize());
			revalidate();
		}

		public void setValues() {
			karyoDrawer.setIconType(iconBox.getSelectedIndex());
			karyoDrawer.setIconSize(iconSize.getSelectedIndex());
			karyoDrawer.notifyObservers();
		}
	}

	private static String reformatInt(int td) {
		Integer tx  = new Integer(td);
		return tx.toString();
	}
	private static String reformatDouble(double td) {
		int order = 1;
		if (Math.abs(td) < 0.0001) {
			Double tx  = new Double(td);
			return tx.toString();
		} 
		while (Math.abs(td * order) < 1000) {
			order *= 10;
		}
		int val    = (int) (td * order);
		Double tx  = new Double(((double) val) / order);
		return tx.toString();
	}


	class ButtonPanel extends JPanel {
		/**
		 *  Constructor for the ButtonPanel object
		 */
		public ButtonPanel() {
			JButton prev  = new JButton("Prev");
			prev.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						experimentPanel.prev();
						setValues();
					}
				});

			JButton same  = new JButton("Same");
			same.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setValues();
					}
				});

			JButton next  = new JButton("Next");
			next.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						experimentPanel.next();
						setValues();
					}
				});

			add(prev);
			add(same);
			add(next);
		}
	}
}

