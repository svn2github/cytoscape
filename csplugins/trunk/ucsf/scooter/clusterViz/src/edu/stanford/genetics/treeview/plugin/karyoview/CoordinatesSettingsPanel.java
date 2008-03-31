/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: CoordinatesSettingsPanel.java,v $
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

// for settings panel
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;

class CoordinatesSettingsPanel extends JPanel implements SettingsPanel {
	private KaryoPanel karyoPanel;
	/** Setter for karyoPanel */
	public void setKaryoPanel(KaryoPanel karyoPanel) {
		this.karyoPanel = karyoPanel;
	}
	/** Getter for karyoPanel */
	public KaryoPanel getKaryoPanel() {
		return karyoPanel;
	}
	
	private CoordinatesPresets coordinatesPresets;
	/** Setter for coordinatesPresets */
	public void setCoordinatesPresets(CoordinatesPresets coordinatesPresets) {
		this.coordinatesPresets = coordinatesPresets;
	}
	/** Getter for coordinatesPresets */
	public CoordinatesPresets getCoordinatesPresets() {
		return coordinatesPresets;
	}

	public CoordinatesSettingsPanel(KaryoPanel karyoPanel, CoordinatesPresets coordsPresets, ViewFrame frame) {
		setKaryoPanel(karyoPanel);
		setCoordinatesPresets(coordsPresets);
		setFrame(frame);
		configureWidgets();
		addWidgets();
	}
	public void setEnabled(boolean enabled) {
		fileButton.setEnabled(enabled);
		originalButton.setEnabled(enabled);
		for (int i =0; i < presetButtons.length; i++) {
			presetButtons[i].setEnabled(enabled);
		}
	}
	private JButton fileButton, originalButton;
	private JButton [] presetButtons;
	private ViewFrame frame  = null;
	/** Setter for frame */
	public void setFrame(ViewFrame frame) {
		this.frame = frame;
	}
	/** Getter for frame */
	public ViewFrame getFrame() {
		return frame;
	}
	private void addWidgets() {
		this.removeAll();
		setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 100;
		gc.weighty = 100;
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;

		add(originalButton, gc);
		
		JPanel presetPanel = new JPanel();
		for (int i =0; i < presetButtons.length;i++) {
			presetPanel.add(presetButtons[i]);
		}
		presetPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		gc.gridy = 1;
		//		  add(new JScrollPane(presetPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), gbc);

		add(presetPanel, gc);
		gc.gridy = 2;
		add(fileButton, gc);

	}
	
	private void configureWidgets() {
		originalButton = new JButton("Extract from Cdt");
		originalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				karyoPanel.useOriginal();
			}
		});

		final CoordinatesPresets presets = getCoordinatesPresets();
		int nPresets = presets.getNumPresets();
		presetButtons = new JButton[nPresets];
		for (int i = 0; i < nPresets; i++) {
			JButton presetButton = new JButton((presets.getPresetNames()) [i]);
			final int index = i;
			presetButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					switchFileset(presets.getFileSet(index));
				}
			});
			presetButtons[index] = presetButton;
		}
		fileButton = new JButton("Edit Presets...");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsPanel presetEditor = KaryoscopeFactory.getCoordinatesPresetsEditor();
				JDialog popup = new JDialog(getFrame(), "Edit Coordinates Presets");
				SettingsPanelHolder holder = new SettingsPanelHolder(popup, getFrame().getApp().getGlobalConfig().getRoot());
				holder.addSettingsPanel(presetEditor);
				popup.getContentPane().add(holder);
				popup.setModal(true);
				popup.pack();
				popup.setVisible(true);
				configureWidgets();
				addWidgets();
				revalidate();
				repaint();
			}
		});
	}
	private void switchFileset(FileSet fileSet1) {
		try { 
			setEnabled(false);
			karyoPanel.getGenome(fileSet1);
		} catch (LoadException ex) {
			setEnabled(true);
			LogBuffer.println("CoordinatesSettingsPanel got error" + ex.toString());
			JOptionPane.showMessageDialog(getFrame(), ex.toString(), "Load Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	public void synchronizeTo() {
		
	}
	
	public void synchronizeFrom() {
	}

}
