import cytoscape.Cytoscape;

import cytoscape.task.util.TaskManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.WindowConstants.*;
import javax.swing.border.*;
import javax.swing.text.Position;





public class BooleanSettingsDialog extends JDialog implements ActionListener {

	private BooleanAlgorithm currentAlgorithm = null;
	
	private JButton vizButton = null;
	private JLabel titleLabel; // Our title
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses

	
	public BooleanSettingsDialog(BooleanAlgorithm algorithm) {
		super(Cytoscape.getDesktop(), algorithm.getName()+" Settings", false);
		currentAlgorithm = algorithm;
		initializeOnce(); // Initialize the components we only do once
	}
	
	public void actionPerformed(ActionEvent e) {
		// Are we the source of the event?
		String command = e.getActionCommand();

		if (command.equals("exit")) {
			setVisible(false);
		} else if (command.equals("apply")) {
			updateAllSettings();
		} else if (command.equals("add")) {
			// Cluster using the current layout
			updateAllSettings();
			//TaskManager.executeTask( new ClusterTask(currentAlgorithm, this),
			//                        ClusterTask.getDefaultTaskConfig() );
		} else if (command.equals("save")) {
			updateAllSettings();
		} else if (command.equals("cancel")) {
			// Call revertSettings for each layout
			revertAllSettings();
			setVisible(false);
		} else {
			// OK, initialize and display
			initialize();
			pack();
			setLocationRelativeTo(Cytoscape.getDesktop());
			setVisible(true);
		}
	}
	
	private void initializeOnce() {
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		// Create our main panel
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

		// Create a panel for algorithm's content
		this.algorithmPanel = currentAlgorithm.getSettingsPanel();

		Border selBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
		TitledBorder titleBorder = BorderFactory.createTitledBorder(selBorder,"Settings");
		titleBorder.setTitlePosition(TitledBorder.LEFT);
		titleBorder.setTitlePosition(TitledBorder.TOP);
		algorithmPanel.setBorder(titleBorder);
		mainPanel.add(algorithmPanel);

		// Create a panel for our button box
		this.buttonBox = new JPanel();

		JButton doneButton = new JButton("Add");
		doneButton.setActionCommand("add");
		doneButton.addActionListener(this);

		JButton saveButton = new JButton("Apply");
		saveButton.setActionCommand("apply");
		saveButton.addActionListener(this);

		JButton executeButton = new JButton("Save");
		executeButton.setActionCommand("save");
		executeButton.addActionListener(this);

		
		
		/*if (visualizer != null && visualizer.isAvailable())
			vizButton.setEnabled(true);
		else
			vizButton.setEnabled(false);
		*/
		JButton cancelButton = new JButton("Exit");
		cancelButton.setActionCommand("exit");
		cancelButton.addActionListener(this);
		buttonBox.add(executeButton);
		
		buttonBox.add(saveButton);
		buttonBox.add(cancelButton);
		buttonBox.add(doneButton);
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		mainPanel.add(buttonBox);
		setContentPane(mainPanel);
	}

	private void initialize() {
	}
	
	private void updateAllSettings() {
		currentAlgorithm.updateSettings();
	}

	private void revertAllSettings() {
		currentAlgorithm.revertSettings();
	}
	
}
