package DenovoPGNetworkAlignmentPlugin;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;
import java.io.File;
import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.util.FileUtil;
import cytoscape.view.cytopanels.CytoPanel;

public class SearchPanel extends JPanel
{
	JTextField classFileField;
	File classFile = null;
	JTextField expressionMatrixFileField;
	File expressionMatrixFile = null;
	JComboBox networkComboBox;
	JComboBox scoreModelComboBox;
	JSpinner numOfTrialsSpinner;
	JSpinner st1Spinner;
	JSpinner st2Spinner;
	JSpinner st3Spinner;
	JSpinner st3TrialsSpinner;
	JSpinner maxNodeDegreeSpinner;
	JSpinner minImprovementSpinner;
	JSpinner maxModuleSizeSpinner;
	JSpinner maxRadiusSpinner;
	JButton searchButton;
	
	public SearchPanel()
	{
		setLayout(new GridBagLayout());

		JLabel classFileLabel = new JLabel("Class file:");
		classFileField = new JTextField("None chosen");
		classFileField.setEditable(false);
		JButton classFileButton = new JButton("Choose...");
		classFileButton.addActionListener(new ChooseClassFileAction());
		JLabel expressionMatrixFileLabel = new JLabel("Expression matrix file:");
		expressionMatrixFileField = new JTextField("None chosen");
		expressionMatrixFileField.setEditable(false);
		JButton expressionMatrixFileButton = new JButton("Choose...");
		expressionMatrixFileButton.addActionListener(new ChooseExpressionMatrixFileAction());
		JLabel networkLabel = new JLabel("Network:");
		networkComboBox = new JComboBox();
		loadNetworks();
		{
			JPanel filePanel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(5,5,5,5);

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;	c.weighty = 0.0;
			filePanel.add(classFileLabel, c);

			c.gridx = 1;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filePanel.add(classFileField, c);

			c.gridx = 2;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;	c.weighty = 0.0;
			filePanel.add(classFileButton, c);

			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;	c.weighty = 0.0;
			filePanel.add(expressionMatrixFileLabel, c);

			c.gridx = 1;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filePanel.add(expressionMatrixFileField, c);

			c.gridx = 2;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;	c.weighty = 0.0;
			filePanel.add(expressionMatrixFileButton, c);

			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0.0;	c.weighty = 0.0;
			filePanel.add(networkLabel, c);

			c.gridx = 1;		c.gridy = 2;
			c.gridwidth = 2;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			filePanel.add(networkComboBox, c);

			c.gridx = 0;		c.gridy = 0;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(filePanel, c);
		}

		JSeparator separator0 = new JSeparator();
		{
			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);
			c.gridx = 0;		c.gridy = 1;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(separator0, c);
		}

		JLabel scoreModelLabel = new JLabel("Score model:");
		String[] scoreModelOptions = {"Mutual Information", "T Test"};
		scoreModelComboBox = new JComboBox(scoreModelOptions);
		{
			JPanel scoreModelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			scoreModelPanel.add(scoreModelLabel);
			scoreModelPanel.add(scoreModelComboBox);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);
			c.gridx = 0;		c.gridy = 2;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(scoreModelPanel, c);
		}

		JLabel numOfTrialsLabel = new JLabel("Number of random trials:");
		numOfTrialsSpinner = newIntSpinner(100);
		{
			JPanel numOfTrialsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			numOfTrialsPanel.add(numOfTrialsLabel);
			numOfTrialsPanel.add(numOfTrialsSpinner);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);
			c.gridx = 0;		c.gridy = 3;
			c.gridwidth = 1;	c.gridheight = 1;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(numOfTrialsPanel, c);
		}

		JLabel st1Label = new JLabel("ST1 P-value cutoff:");
		st1Spinner = newDoubleSpinner(0.05);
		JLabel st2Label = new JLabel("ST2 P-value cutoff:");
		st2Spinner = newDoubleSpinner(0.05);
		JLabel st3Label = new JLabel("ST3 P-value cutoff:");
		st3Spinner = newDoubleSpinner(0.00005);
		JLabel st3TrialsLabel = new JLabel("Number of ST3 trials:");
		st3TrialsSpinner = newIntSpinner(20000);
		{
			JPanel testsPanel = new JPanel(new GridBagLayout());
			testsPanel.setBorder(BorderFactory.createTitledBorder("Statistical Tests"));

			{
				JPanel st1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				st1Panel.add(st1Label);
				st1Panel.add(st1Spinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 0;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				testsPanel.add(st1Panel, c);
			}

			{
				JPanel st2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				st2Panel.add(st2Label);
				st2Panel.add(st2Spinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				testsPanel.add(st2Panel, c);
			}

			{
				JPanel st3Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				st3Panel.add(st3Label);
				st3Panel.add(st3Spinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 2;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				testsPanel.add(st3Panel, c);
			}

			{
				JPanel st3TrialsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				st3TrialsPanel.add(st3TrialsLabel);
				st3TrialsPanel.add(st3TrialsSpinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 3;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				testsPanel.add(st3TrialsPanel, c);
			}

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(5,5,5,5);
			c.gridwidth = 1;	c.gridheight = 1;
			c.gridx = 0;		c.gridy = 4;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(testsPanel, c);
		}

		JLabel maxNodeDegreeLabel = new JLabel("Max node degree:");
		maxNodeDegreeSpinner = newIntSpinner(200);
		JLabel minImprovementLabel = new JLabel("Min improvement:");
		minImprovementSpinner = newDoubleSpinner(0.05, 0.0);
		JLabel maxModuleSizeLabel = new JLabel("Max module size:");
		maxModuleSizeSpinner = newIntSpinner(20);
		JLabel maxRadiusLabel = new JLabel("Max radius:");
		maxRadiusSpinner = newIntSpinner(0,2);
		JLabel infLabel = new JLabel("(set radius to 0 for infinite radius)");
		{
			JPanel searchPanel = new JPanel(new GridBagLayout());
			searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

			{
				JPanel maxNodeDegreePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				maxNodeDegreePanel.add(maxNodeDegreeLabel);
				maxNodeDegreePanel.add(maxNodeDegreeSpinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 0;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				searchPanel.add(maxNodeDegreePanel, c);
			}

			{
				JPanel minImprovementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				minImprovementPanel.add(minImprovementLabel);
				minImprovementPanel.add(minImprovementSpinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 1;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				searchPanel.add(minImprovementPanel, c);
			}

			{
				JPanel maxModuleSizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				maxModuleSizePanel.add(maxModuleSizeLabel);
				maxModuleSizePanel.add(maxModuleSizeSpinner);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 2;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;

				searchPanel.add(maxModuleSizePanel, c);
			}

			{
				JPanel maxRadiusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
				maxRadiusPanel.add(maxRadiusLabel);
				maxRadiusPanel.add(maxRadiusSpinner);
				maxRadiusPanel.add(infLabel);

				GridBagConstraints c = new GridBagConstraints();
				c.insets = new Insets(5,5,5,5);
				c.gridwidth = 1;	c.gridheight = 1;
				c.gridx = 0;		c.gridy = 3;
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;	c.weighty = 0.0;
				searchPanel.add(maxRadiusPanel, c);

			}

			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.FIRST_LINE_START;
			c.insets = new Insets(5,5,5,5);
			c.gridwidth = 1;	c.gridheight = 1;
			c.gridx = 0;		c.gridy = 5;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;	c.weighty = 0.0;
			add(searchPanel, c);
		}

		searchButton = new JButton("Search");
		updateSearchButton();
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new CloseAction());
		{
			JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			buttonsPanel.add(closeButton);
			buttonsPanel.add(searchButton);

			GridBagConstraints c = new GridBagConstraints();
			c.insets = new Insets(5,5,5,5);
			c.anchor = GridBagConstraints.LAST_LINE_END;
			c.gridwidth = 1;	c.gridheight = 1;
			c.gridx = 0;		c.gridy = 6;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 1.0;	c.weighty = 1.0;
			add(buttonsPanel, c);
		}

		NetworkChangeListener networkChangeListener = new NetworkChangeListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_CREATED, networkChangeListener);
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED, networkChangeListener);
	}

	private JSpinner newDoubleSpinner(double defaultValue)
	{
		return newDoubleSpinner(defaultValue, Double.MIN_VALUE);
	}

	private JSpinner newDoubleSpinner(double defaultValue, double minValue)
	{
		return newNumberSpinner(new SpinnerNumberModel(defaultValue, minValue, 1.0, 0.01));
	}

	private JSpinner newIntSpinner(int defaultValue)
	{
		return newIntSpinner(1, defaultValue);
	}

	private JSpinner newIntSpinner(int minValue, int defaultValue)
	{
		return newNumberSpinner(new SpinnerNumberModel(defaultValue, minValue, Integer.MAX_VALUE, 1));
	}

	private JSpinner newNumberSpinner(SpinnerNumberModel numberModel)
	{
		JSpinner spinner = new JSpinner();
		spinner.setModel(numberModel);
		new JSpinner.NumberEditor(spinner);
		((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(5);
		((JSpinner.NumberEditor) spinner.getEditor()).getFormat().setMaximumFractionDigits(9);
		return spinner;
	}

	class Network
	{
		public CyNetwork network;

		public Network(CyNetwork network)
		{
			this.network = network;
		}

		public String toString()
		{
			return network.getTitle();
		}

		public boolean equals(final Network other)
		{
			if (other == null) return false;
			return this.network.equals(other.network);
		}
	}

	private void loadNetworks()
	{
		Network selectedNetwork = null;
		Object selectedItem = networkComboBox.getSelectedItem();
		if (selectedItem != null && selectedItem instanceof Network)
			selectedNetwork = (Network) selectedItem;
		
		networkComboBox.removeAllItems();
		Iterator networks = Cytoscape.getNetworkSet().iterator();
		int selectedIndex = -1;
		while (networks.hasNext())
		{
			Network newNetwork = new Network((CyNetwork) networks.next());
			networkComboBox.addItem(newNetwork);
			if (newNetwork.equals(selectedNetwork))
				selectedIndex = networkComboBox.getItemCount() - 1;
		}
		if (networkComboBox.getItemCount() == 0)
		{
			networkComboBox.addItem("No networks are available");
			networkComboBox.setEnabled(false);
		}
		else
		{
			networkComboBox.setEnabled(true);
			if (selectedIndex != -1)
				networkComboBox.setSelectedIndex(selectedIndex);
		}
	}

	private void updateSearchButton()
	{
		searchButton.setEnabled(false);
		if (classFile == null)
			searchButton.setToolTipText("A class file must be selected before searching.");
		else if (expressionMatrixFile == null)
			searchButton.setToolTipText("An expression matrix file must be selected before searching.");
		else if (networkComboBox.getItemCount() == 0)
			searchButton.setToolTipText("There must be at least one network available before searching.");
		else
		{
			searchButton.setEnabled(true);
			searchButton.setToolTipText(null);
		}
	}

	class ChooseClassFileAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			File file = FileUtil.getFile("DenovoPGNetworkAlignment: Open class file", FileUtil.LOAD);
			if (file != null)
			{
				classFile = file;
				classFileField.setText(classFile.getPath());
			}
			updateSearchButton();
		}
	}

	class ChooseExpressionMatrixFileAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			File file = FileUtil.getFile("DenovoPGNetworkAlignment: Open expression matrix file", FileUtil.LOAD);
			if (file != null)
			{
				expressionMatrixFile = file;
				expressionMatrixFileField.setText(expressionMatrixFile.getPath());
			}
			updateSearchButton();
		}
	}

	class CloseAction implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			cytoPanel.remove(SearchPanel.this);
		}
	}
	
	class NetworkChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent event)
		{
			loadNetworks();
			updateSearchButton();
		}
	}

	public void addSearchActionListener(ActionListener l)
	{
		searchButton.addActionListener(l);
	}

	public File getClassFile()
	{
		return classFile;
	}

	public File getExpressionMatrixFile()
	{
		return expressionMatrixFile;
	}

	public CyNetwork getNetwork()
	{
		return ((Network) networkComboBox.getSelectedItem()).network;
	}

	public String getScoreModel()
	{
		return (String) scoreModelComboBox.getSelectedItem();
	}

	public int getNumOfTrials()
	{
		return ((Number) numOfTrialsSpinner.getValue()).intValue();
	}

	public double getST1Cutoff()
	{
		return ((Number) st1Spinner.getValue()).doubleValue();
	}

	public double getST2Cutoff()
	{
		return ((Number) st2Spinner.getValue()).doubleValue();
	}

	public double getST3Cutoff()
	{
		return ((Number) st3Spinner.getValue()).doubleValue();
	}

	public int getNumOfST3Trials()
	{
		return ((Number) st3TrialsSpinner.getValue()).intValue();
	}

	public int getMaxNodeDegree()
	{
		return ((Number) maxNodeDegreeSpinner.getValue()).intValue();
	}

	public double getMinImprovement()
	{
		return ((Number) minImprovementSpinner.getValue()).doubleValue();
	}

	public int getMaxModuleSize()
	{
		return ((Number) maxModuleSizeSpinner.getValue()).intValue();
	}

	public int getMaxRadius()
	{
		int maxRadius = ((Number) maxRadiusSpinner.getValue()).intValue();
		return (maxRadius == 0 ? Integer.MAX_VALUE : maxRadius);
	}
}
