package org.jmathplot.gui.components;

import java.security.*;
import java.awt.event.*;
import javax.swing.*;
import org.jmathplot.gui.*;

/**
 * Allow a separate floatable toolbar which can
 * manage multiple plotpanels (they become selected)


 * <p>Copyright : BSD License</p>

 * @author Yann RICHET - horst.fiedler@tifff.com
 * @version 1.0
 */

public class PlotToolBar
	extends JToolBar {

	//private int actionMode;

	protected ButtonGroup buttonGroup;
	protected JToggleButton buttonCenter;
	protected JToggleButton buttonZoom;
	protected JToggleButton buttonRotate;
	protected JToggleButton buttonViewCoords;
	protected JButton buttonSetScales;
	protected JButton buttonDatas;
	protected JButton buttonSaveGraphic;
	protected JButton buttonReset;

	private boolean denySaveSecurity;
	private JFileChooser fileChooser;

	/** the currently selected PlotPanel */
	private PlotPanel plotPanel;

	public PlotToolBar(PlotPanel pp) {
		plotPanel = pp;

		try {
			fileChooser = new JFileChooser();
		} catch (AccessControlException ace) {
			denySaveSecurity = true;
		}

		buttonGroup = new ButtonGroup();


	//	buttonCenter = new JToggleButton("Center",new ImageIcon("icons\\center.png")); //jmathplot\gui\icons
	//	buttonCenter.setToolTipText("Center axes");

		buttonZoom = new JToggleButton("Zoom",new ImageIcon("icons\\zoom.png"));
		buttonZoom.setToolTipText("Zoom");

//		buttonViewCoords = new JToggleButton("View", new ImageIcon("icons\\position.png"));
//		buttonViewCoords.setToolTipText("View coordinates / View entire plot");

//		buttonSetScales = new JButton("Scales",new ImageIcon("icons\\scale.png"));
//		buttonSetScales.setToolTipText("Set scales");

//		buttonDatas = new JButton("Get datas",new ImageIcon("icons\\data.png"));
//		buttonDatas.setToolTipText("Get datas");

		buttonSaveGraphic = new JButton("Save", new ImageIcon("icons\\tofile.png"));
		buttonSaveGraphic.setToolTipText("Save graphics in a .PNG File");

		buttonReset = new JButton("Reset",new ImageIcon("icons\\back.png"));
		buttonReset.setToolTipText("Reset axes");

		buttonZoom.setSelected(true);
		buttonZoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.ActionMode = PlotPanel.ZOOM;
			}
		});
	/*	buttonCenter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.ActionMode = PlotPanel.TRANSLATION;
			}
		});*/
/*		buttonViewCoords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.setNoteCoords(buttonViewCoords.isSelected());
			}
		});*/
	/*	buttonSetScales.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.displaySetScalesFrame();
			}
		});*/
	/*	buttonDatas.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.displayDatasFrame();
			}
		});*/
		buttonSaveGraphic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseFile();
			}
		});
		buttonReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				plotPanel.resetBase();
			}
		});

		buttonGroup.add(buttonCenter);
		buttonGroup.add(buttonZoom);

	//	add(buttonCenter, null);
		add(buttonZoom, null);
	//	add(buttonViewCoords, null);
	//	add(buttonSetScales, null);
		add(buttonSaveGraphic, null);
	//	add(buttonDatas, null);
		add(buttonReset, null);

		if (!denySaveSecurity) {
			fileChooser.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					saveFile();
				}
			});
		} else {
			buttonSaveGraphic.setEnabled(false);
		}

		// allow mixed (2D/3D) plots managed by one toolbar
		/*if (plotPanel instanceof Plot3DPanel) {
			if (buttonRotate == null) {
				buttonRotate = new JToggleButton(new ImageIcon(PlotPanel.class.
					       getResource("icons/rotation.png")));
				buttonRotate.setToolTipText("Rotate axes");

				buttonRotate.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						plotPanel.ActionMode = Plot3DPanel.ROTATION;
					}
				});
				buttonGroup.add(buttonRotate);
				add(buttonRotate, null, 2);
			} else {
				buttonRotate.setEnabled(true);
			}
		} else {
			if (buttonRotate != null) {
				// no removal/disabling just disable
				if (plotPanel.ActionMode == Plot3DPanel.ROTATION) {
					plotPanel.ActionMode = PlotPanel.ZOOM;
				}
				buttonRotate.setEnabled(false);
			}
		}*/
	}

	public int getActionMode() {
		return plotPanel.ActionMode;
	}

	public PlotPanel getPlotPanel() {
		return plotPanel;
	}

	void chooseFile() {
		fileChooser.showSaveDialog(this);
	}

	void saveFile() {
		java.io.File file = fileChooser.getSelectedFile();
		plotPanel.toGraphicFile(file);
	}
}