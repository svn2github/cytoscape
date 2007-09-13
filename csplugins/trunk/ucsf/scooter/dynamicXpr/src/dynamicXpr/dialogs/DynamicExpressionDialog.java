/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
package dynamicXpr.dialogs;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Dictionary;
import java.util.Hashtable;
import javax.swing.event.*;
import java.io.File;

import cytoscape.Cytoscape;
import dynamicXpr.DynamicExpression;
import cytoscape.data.ExpressionData;

/**
 * A user interface for DynamicExpression.java
 * 
 * @author Iliana Avila-Campillo iavila@systemsbiology.net
 * @version %I%, %G%
 * @since 1.1
 */

public class DynamicExpressionDialog extends JFrame {

	protected DynamicExpression listener;

	JPanel mainPanel;

	JPanel edgedPanel;

	JPanel filePanel;

	JLabel fileLabel;

	JTextField fileField;

	JButton browseButton;

	JPanel conditionsPanel;

	JLabel conditionsLabel;

	JSlider conditionsSlider;

	ConditionsSliderListener conditionsListener;

	JPanel speedPanel;

	JLabel speedLabel;

	JSlider speedSlider;

	JPanel buttonPanel;

	JButton playButton;

	JButton stopButton;

	JButton pauseButton;

	JPanel dismissPanel;

	JButton dismissButton;

	Border etched;

	Border paneEdge;

	File currentDirectory;

	boolean nodeColorChanged = false;

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            the <code>DynamicExpression</code> object that is listening
	 *            to the button requests of the dialog
	 * @param title
	 *            the title for the dialog to use
	 */
	public DynamicExpressionDialog(DynamicExpression listener, String title) {
		//super(Cytoscape.getDesktop(), false);
		this.listener = listener;
		setTitle(title);
		createUI();
		this.currentDirectory = new File(System.getProperty("user.dir"));
	}// DynamicExpressionDialog

	/**
	 * Creates the dialog
	 */
	protected void createUI() {
		if (mainPanel != null) {
			mainPanel.removeAll();
		}

		etched = BorderFactory.createEtchedBorder();
		paneEdge = BorderFactory.createEmptyBorder(5, 5, 5, 5);

		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.setBorder(paneEdge);

		edgedPanel = new JPanel();
		edgedPanel.setLayout(new BoxLayout(edgedPanel, BoxLayout.Y_AXIS));
		edgedPanel.setBorder(etched);

		filePanel = new JPanel();
		filePanel.setLayout(new BoxLayout(filePanel, BoxLayout.X_AXIS));
		filePanel
				.setBorder(BorderFactory.createTitledBorder("Expression File"));
		fileLabel = new JLabel("File:");
		filePanel.add(fileLabel);
		ExpressionData expData = Cytoscape.getExpressionData();
		String fileName = null;
		if (expData != null) {
			fileName = expData.getFileName();
		}

		fileField = new JTextField(15);
		if (fileName != null) {
			fileField.setText(fileName);
		}
		filePanel.add(Box.createRigidArea(new Dimension(8, 0)));
		filePanel.add(fileField);
		browseButton = new JButton("Browse");
		browseButton.addActionListener(new BrowserButtonListener());
		filePanel.add(browseButton);
		edgedPanel.add(filePanel);
		conditionsPanel = new JPanel();
		conditionsPanel.setLayout(new BoxLayout(conditionsPanel,
				BoxLayout.Y_AXIS));
		conditionsPanel.setBorder(BorderFactory
				.createTitledBorder("Conditions"));
		String[] conditions = null;
		if (expData != null) {
			conditions = expData.getConditionNames();
			conditionsLabel = new JLabel(conditions[0]);
		} else {
			conditionsLabel = new JLabel("No Expression Data");
		}
		conditionsPanel.add(conditionsLabel);
		int numConditions = 81; // So that the speed slider and the conditions
								// slider are
		// of the same size by default
		if (expData != null) {
			numConditions = expData.getNumberOfConditions();
		}
		conditionsSlider = new JSlider(JSlider.HORIZONTAL, 0,
				numConditions - 1, 0);
		conditionsSlider.setMajorTickSpacing(1);
		conditionsSlider.setSnapToTicks(true);
		conditionsSlider.setPaintTicks(true);
		if (expData != null) {
			conditionsSlider.setPaintLabels(true);
		} else {
			conditionsSlider.setPaintLabels(false);
		}
		conditionsListener = new ConditionsSliderListener();
		conditionsSlider.addChangeListener(conditionsListener);
		conditionsPanel.add(conditionsSlider);
		conditionsPanel.add(Box.createRigidArea(new Dimension(8, 0)));
		edgedPanel.add(conditionsPanel);

		speedPanel = new JPanel();
		speedPanel.setLayout(new BoxLayout(speedPanel, BoxLayout.Y_AXIS));
		speedPanel.setBorder(BorderFactory.createTitledBorder("Speed"));
		speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 45);
		speedSlider.setMajorTickSpacing(10);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setSnapToTicks(true);
		speedSlider.setPaintTicks(true);
		speedSlider.addChangeListener(new SpeedSliderListener());
		Dictionary labelTable = new Hashtable();
		labelTable.put(new Integer(0), new JLabel("Slow"));
		labelTable.put(new Integer(45), new JLabel("Medium"));
		labelTable.put(new Integer(90), new JLabel("Fast"));
		speedSlider.setLabelTable(labelTable);
		speedSlider.setPaintLabels(true);
		speedPanel.add(speedSlider);
		speedPanel.add(Box.createRigidArea(new Dimension(8, 0)));
		edgedPanel.add(speedPanel);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.setBorder(BorderFactory.createTitledBorder("Play"));
		ImageIcon playIcon = createImageIcon("/dynamicXpr/dialogs/images/play.jpg");
		if (playIcon == null) {
			playButton = new JButton("Play");
		} else {
			playButton = new JButton(playIcon);
		}
		playButton.addActionListener(new PlayActionListener());
		buttonPanel.add(playButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		ImageIcon pauseIcon = createImageIcon("/dynamicXpr/dialogs/images/pause.jpg");
		if (pauseIcon == null) {
			pauseButton = new JButton("Pause");
		} else {
			pauseButton = new JButton(pauseIcon);
		}
		pauseButton.addActionListener(new PauseActionListener());
		buttonPanel.add(pauseButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(16, 0)));
		ImageIcon stopIcon = createImageIcon("/dynamicXpr/dialogs/images/stop.jpg");
		if (stopIcon == null) {
			stopButton = new JButton("Stop");
		} else {
			stopButton = new JButton(stopIcon);
		}
		stopButton.setVerticalTextPosition(AbstractButton.CENTER);
		stopButton.setHorizontalTextPosition(AbstractButton.LEADING);
		stopButton.addActionListener(new StopActionListener());
		buttonPanel.add(stopButton);
		edgedPanel.add(buttonPanel);

		dismissPanel = new JPanel();
		dismissPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		dismissButton = new JButton("Dismiss");
		dismissButton.addActionListener(new DismissActionListener());
		dismissPanel.add(dismissButton);

		mainPanel.add(edgedPanel);
		mainPanel.add(dismissPanel);

		setContentPane(mainPanel);
	}// createUI

	/**
	 * Enables or disables the components of this dialog.
	 */
	public void enable(boolean enable) {
		conditionsSlider.setEnabled(enable);
		speedSlider.setEnabled(enable);
		playButton.setEnabled(enable);
		stopButton.setEnabled(enable);
		pauseButton.setEnabled(enable);
	}// enable

	/**
	 * Updates the conditions slider bar to reflect a new expression data set.
	 */
	public void update() {
		String[] conditions = null;
		ExpressionData expData = Cytoscape.getExpressionData();
		if (expData != null) {
			conditions = expData.getConditionNames();
			conditionsLabel.setText(conditions[0]);
			listener.adjustForNewExpressionData();
		} else {
			conditionsLabel.setText("No Expression Data");
		}
		int numConditions = 81;
		if (expData != null) {
			numConditions = expData.getNumberOfConditions();
			conditionsSlider.setPaintLabels(true);
		} else {
			conditionsSlider.setPaintLabels(false);
		}
		conditionsSlider.setMinimum(0);
		conditionsSlider.setMaximum(numConditions - 1);
		conditionsSlider.setValue(0);
	}// update

	/**
	 * @return the <code>String</code> currently showing in the Expression
	 *         Data file-name field.
	 */
	public String getFileName() {
		return this.fileField.getText();
	}// getFileName

	/**
	 * Sets the file name of the currently loaded ExpressionData.
	 */
	public void setFileName(String file_name) {
		this.fileField.setText(file_name);
		update();
	}// setFileName

	/**
	 * Enables or disables the listener for the condition slider
	 */
	public void conditionsSliderEnabled(boolean enable) {
		if (enable) {
			conditionsSlider.addChangeListener(conditionsListener);
		} else {
			conditionsSlider.removeChangeListener(conditionsListener);
		}
	}// conditionsSliderEnabled

	/**
	 * Sets the conditionsSlider to the given condition
	 */
	public void updateConditionsSlider(int condition, String conditionName) {
		conditionsSlider.setValue(condition);
		conditionsLabel.setText(conditionName);
	}// updateConditionsSlider

	// ----------- internal classes -------------
	class BrowserButtonListener extends AbstractAction {
		BrowserButtonListener() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser(currentDirectory);
			if (chooser.showOpenDialog(DynamicExpressionDialog.this) == JFileChooser.APPROVE_OPTION) {
				currentDirectory = chooser.getCurrentDirectory();
				String fileName = chooser.getSelectedFile().toString();
				if (!Cytoscape.loadExpressionData(fileName, false)) { // try
																		// true
					// Unsuccesful data load
					JOptionPane.showMessageDialog(chooser,
							"The expression file you loaded created an error. "
									+ System.getProperty("line.separator"),
							"Error", JOptionPane.ERROR_MESSAGE);
				} else {
					// Succesful data load
					DynamicExpressionDialog.this.pack();
					fileField.setText(fileName);
					DynamicExpressionDialog.this.update();
					DynamicExpressionDialog.this.enable(true);
					nodeColorChanged = true;
				}
			}

		}
	}// BrowserButtonListener

	class PlayActionListener extends AbstractAction {
		PlayActionListener() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			nodeColorChanged = true;
			if (listener.isPaused() == 1) {
				// Was paused, change back the pause icon to black
				ImageIcon pauseIcon = createImageIcon("/dynamicXpr/dialogs/images/pause.jpg");
				if (pauseIcon != null) {
					pauseButton.setIcon(pauseIcon);
				}
			} else if (listener.isPaused() == 0) {
				// Not paused, playing
				return; // wait to finish
			}
			int delay = speedSlider.getValue();
			delay = 100 - delay;
			delay *= 30;
			conditionsSliderEnabled(false);
			listener.play(delay);
		}
	}// PlayActionListener

	class PauseActionListener extends AbstractAction {
		PauseActionListener() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			if (listener.isPaused() == 0) {
				// not paused, but about to become paused
				ImageIcon pauseIcon = createImageIcon("/dynamicXpr/dialogs/images/redPause.jpg");
				if (pauseIcon != null) {
					pauseButton.setIcon(pauseIcon);
				}
				listener.pause();
			} else if (listener.isPaused() == 1) {
				// paused, about to play again
				ImageIcon pauseIcon = createImageIcon("/dynamicXpr/dialogs/images/pause.jpg");
				if (pauseIcon != null) {
					pauseButton.setIcon(pauseIcon);
				}
				listener.pause();
			}

		}
	}// PauseActionListener

	class StopActionListener extends AbstractAction {
		StopActionListener() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			listener.stop();
			conditionsSliderEnabled(true);
		}
	}// StopActionListener

	public class DismissActionListener extends AbstractAction {
		DismissActionListener() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			if (nodeColorChanged) {
				int n = JOptionPane.showConfirmDialog(null,
						"Restore previous node colors?", "",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					listener.restoreOldNodeColorCalculator();

				}
			}// if nodeColorChanged
			DynamicExpressionDialog.this.dispose();
			nodeColorChanged = false;
		}

	} // DismissActionListener

	public class SpeedSliderListener implements ChangeListener {
		SpeedSliderListener() {}

		public void stateChanged (ChangeEvent e) {
			JSlider jslider = (JSlider) e.getSource();
			int delay = jslider.getValue();
			delay = 100 - delay;
			delay *= 30;
			listener.setTimerDelay(delay);
		}// stateChanged

	}// ConditionsSliderListener

	public class ConditionsSliderListener implements ChangeListener {
		ConditionsSliderListener() {
		}

		public void stateChanged (ChangeEvent e) {
			JSlider jslider = (JSlider) e.getSource();
			if (jslider.getValueIsAdjusting()) {
				return;
			}
			int i = jslider.getValue(); // this is a value between 0-100
			ExpressionData data = Cytoscape.getExpressionData();
			if(data != null){
				String[] conditions = data.getConditionNames();
				DynamicExpression.displayCondition(conditions[i], i);
				conditionsLabel.setText(conditions[i]);
			}
		}// stateChanged

	}// ConditionsSliderListener

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected static ImageIcon createImageIcon(String path) {

		java.net.URL imgURL = DynamicExpression.class.getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			System.err.println("----- Couldn't find file: " + path
					+ " --------");
			System.err.flush();
			return null;
		}
	}

}// DynamicExpression

