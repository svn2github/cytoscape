// ActivePathsParametersPopupDialog
//-----------------------------------------------------------------------------
// $Revision: 14177 $
// $Date: 2008-06-10 15:16:57 -0700 (Tue, 10 Jun 2008) $
// $Author: rmkelley $
//-----------------------------------------------------------------------------
package csplugins.jActiveModules.dialogs;
//-----------------------------------------------------------------------------
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingConstants;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import cytoscape.view.cytopanels.CytoPanel;

import java.util.List;
import java.util.Collections;
import java.util.Vector;
import java.io.IOException;

import org.jdesktop.layout.GroupLayout;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import csplugins.jActiveModules.ActiveModulesUI;
import csplugins.jActiveModules.ActivePaths;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;


public class ActivePathsParametersPopupDialog extends JPanel {
	JTextField readout;

	ActivePathFinderParameters apfParams;
	static String NONRANDOM = "Non-Random Starting Graph";
	static String ANNEAL = "Anneal";
	static String SEARCH = "Search";

	JPanel tStartPanel;
	JPanel tEndPanel;
	JTextField startNum;
	JTextField endNum;
	JLabel tempLabelStart;
	JLabel tempLabelEnd;

	JPanel intervalPanel;
	JTextField intervalNum;
	JLabel intervalLabel;

    JPanel overlapPanel;
    JTextField overlapNum;
    JLabel overlapLabel;

	JPanel pathPanel;
	JTextField pathNum;
	JLabel pathLabel;

	JPanel iterPanel;
	JTextField iterNum;
	JLabel iterLabel;

	JPanel annealExtPanel;
	JPanel generalExtPanel;
	JPanel hfcPanel;
	JCheckBox quenchCheck;
	JCheckBox edgesCheck;
	JCheckBox hubBox;
	JTextField hubNum;

	// hub adjustment
	JPanel hAdjPanel;
	JCheckBox hubAdjustmentBox;
	JTextField hubAdjustmentNum;

	// monte carlo: on/off
	JCheckBox mcBox;

	// regional scoring: on/off
	JCheckBox regionalBox;

	// greedy search rather than annealing
	JPanel searchPanel;
	JTextField searchDepth;
	JCheckBox searchFromNodesBox;
	JCheckBox maxBox;
	JTextField maxDepth;

	JRadioButton annealButton;
	JRadioButton searchButton;
	JPanel annealSearchControlPanel;
	JPanel annealSearchContentPanel;
	JPanel annealContentPanel;
	JPanel searchContentPanel;
	JPanel currentContentPanel;

	JPanel attrSelectPanel;
	JPanel optionsPanel;
	JList exprAttrsList;
	JButton findModulesButton;
	ActiveModulesUI parentUI;
	JDialog helpDialog;

	// -----------------------------------------------------------------------------
	public ActivePathsParametersPopupDialog(// ActivePathsParametersPopupDialogListener
			// listener,
			Frame parentFrame, String title,
			ActivePathFinderParameters incomingApfParams,
			ActiveModulesUI parentUI) {
		// uses copy constructor so that changes aren't committed if you
		// dismiss.
		// apfParams = new ActivePathFinderParameters(incomingApfParams);
		apfParams = incomingApfParams;
		this.parentUI = parentUI;

		if ( apfParams == null )
			System.out.println("WTF");

		JPanel childPanel = new JPanel(new GridBagLayout());
		
		readout = new JTextField(new String("seed: "
				+ apfParams.getRandomSeed()));
		RandomSeedTextListener readoutListener = new RandomSeedTextListener();
		readout.addFocusListener(readoutListener);

		createExtsController();
		createAnnealContentPanel();
		createSearchContentPanel();
		createAnnealSearchController();
		createHelpDialog();

		GridBagConstraints c = new GridBagConstraints();

		createAttrSelectionPanel();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;		c.gridy = 0;
		c.weightx = 1.0;	c.weighty = 1.0;
		childPanel.add(attrSelectPanel,c);

		JPanel subOptionsPanel = new JPanel(new GridBagLayout());
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;		c.gridy = 0;
		c.weightx = 1.0;	c.weighty = 0.0;
		subOptionsPanel.add(generalExtPanel,c);

		c.gridx = 0;		c.gridy = 1;
		subOptionsPanel.add(annealSearchControlPanel,c);

		optionsPanel = new JPanel(new CardLayout());
		optionsPanel.add(new JPanel(), "INACTIVE");
		optionsPanel.add(subOptionsPanel, "ACTIVE");
		childPanel.add(optionsPanel,c);

		// /////////////////////////////////////////
		JPanel buttonPanel = new JPanel();

		JButton helpButton = new JButton(new AbstractAction("Help")
		{
			public void actionPerformed(ActionEvent e)
			{
				helpDialog.setVisible(true);
			}
		});
		buttonPanel.add(helpButton, BorderLayout.EAST);
		JButton dismissButton = new JButton("Close");
		dismissButton.addActionListener(new DismissAction());
		buttonPanel.add(dismissButton, BorderLayout.EAST);
		findModulesButton = new JButton(new FindModulesAction());
		buttonPanel.add(findModulesButton, BorderLayout.WEST);

		c.gridx = 0;		c.gridy = 2;
		childPanel.add(buttonPanel,c);

		updateOptionsPanel();

		JScrollPane scrollPane = new JScrollPane(childPanel);
		setLayout(new GridLayout(1,1));
		add(scrollPane);

	} // PopupDialog ctor

	private void createAttrSelectionPanel() {
		attrSelectPanel = new JPanel();
		attrSelectPanel.setLayout(new GridLayout(1,1));
		attrSelectPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		Border border = BorderFactory.createLineBorder(Color.black);
		Border titledBorder = BorderFactory.createTitledBorder(border,
				"Expression Attributes For Analysis", TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION);
		attrSelectPanel.setBorder(titledBorder);
	
		List<String> selectedNames = apfParams.getExpressionAttributes();
		Vector<String> allNames = new Vector<String>(apfParams.getPossibleExpressionAttributes());
		exprAttrsList = new JList(allNames);
		exprAttrsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		exprAttrsList.setLayoutOrientation(JList.VERTICAL);
		exprAttrsList.setVisibleRowCount(3);
		exprAttrsList.addMouseListener(new ExprAttrsListListener(exprAttrsList, apfParams));
		for ( String name : selectedNames ) { 
			int index = allNames.indexOf(name);
			if ( index != -1 )
				exprAttrsList.addSelectionInterval(index, index);
		}


		JScrollPane scrollPane = new JScrollPane(exprAttrsList);
		attrSelectPanel.add(scrollPane);
	}

	protected void updateOptionsPanel()
	{
		if (exprAttrsList == null || optionsPanel == null || findModulesButton == null)
			return;
		boolean showOptions = exprAttrsList.getSelectedIndices().length != 0;
		CardLayout cl = (CardLayout) optionsPanel.getLayout();
		if (showOptions)
		{
			cl.show(optionsPanel, "ACTIVE");
			findModulesButton.setEnabled(true);
		}
		else
		{
			cl.show(optionsPanel, "INACTIVE");
			findModulesButton.setEnabled(false);
		}
	}

	private void createAnnealContentPanel() {
		annealContentPanel = new JPanel();
		annealContentPanel.setLayout(new BoxLayout(annealContentPanel, BoxLayout.PAGE_AXIS));
		GridBagConstraints c = new GridBagConstraints();

		createIterationsController();
		annealContentPanel.add(iterPanel);

		TempController tc = new TempController();
		annealContentPanel.add(tStartPanel);

		annealContentPanel.add(tEndPanel);
		annealContentPanel.add(annealExtPanel);

		JPanel rsPanel = createRandomSeedController();
		annealContentPanel.add(rsPanel);

		Border border = BorderFactory.createLineBorder(Color.black);
		Border titledBorder = BorderFactory.createTitledBorder(border,
				"Annealing Parameters", TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION);
		annealContentPanel.setBorder(titledBorder);
	}

	private void createSearchContentPanel() {
		searchContentPanel = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		searchContentPanel.setLayout(gridbag);

		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.ipadx = 10;
		c.ipady = 10;
		// /////////////////////////////////////////

		c.gridx = 0;
		c.gridy = 8;
		c.gridwidth = GridBagConstraints.REMAINDER; // end row
		c.anchor = GridBagConstraints.CENTER;
		createSearchController();
		gridbag.setConstraints(searchPanel, c);
		searchContentPanel.add(searchPanel);

		Border border = BorderFactory.createLineBorder(Color.black);
		Border titledBorder = BorderFactory.createTitledBorder(border,
				"Searching Parameters", TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION);
		searchContentPanel.setBorder(titledBorder);
	}

	// -----------------------------------------------------------------------------
	private void createExtsController() {
		annealExtPanel = new JPanel();

		createHubfindingController();
		createHubAdjustmentController();
		createMontecarloController();

		quenchCheck = new JCheckBox("Quenching", apfParams.getToQuench());
		QuenchCheckListener qcListener = new QuenchCheckListener();
		quenchCheck.addItemListener(qcListener);
		JPanel quenchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		quenchPanel.add(quenchCheck);
		
		JLabel annealingExtLabel = new JLabel("Annealing Extensions:");

		GroupLayout layout = new GroupLayout(annealExtPanel);
		annealExtPanel.setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.add(annealingExtLabel)
				.add(quenchPanel)
				.add(hfcPanel)
		);
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.add(annealingExtLabel)
				.add(quenchPanel)
				.add(hfcPanel)
		);

		// -------------------------------------------------------------

		generalExtPanel = new JPanel();
		layout = new GroupLayout(generalExtPanel);
		generalExtPanel.setLayout(layout);

		Border generalBorder = BorderFactory.createLineBorder(Color.black);
		Border generalTitledBorder = BorderFactory.createTitledBorder(
				generalBorder, "General Parameters", TitledBorder.CENTER,
				TitledBorder.DEFAULT_POSITION);
		generalExtPanel.setBorder(generalTitledBorder);

		createRegionalScoringController();
		createPathsController();
		createOverlapController();

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.add(pathPanel)
			.add(overlapPanel)
				.add(mcBox)
				.add(regionalBox)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.add(pathPanel)
			.add(overlapPanel)
				.add(mcBox)
				.add(regionalBox)
		);

		return;

	}

	private void createAnnealSearchController() {
		annealSearchControlPanel = new JPanel();
		annealSearchContentPanel = new JPanel(new CardLayout());
		GroupLayout layout = new GroupLayout(annealSearchControlPanel);
		annealSearchControlPanel.setLayout(layout);

		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		annealButton = new JRadioButton(ANNEAL);
		searchButton = new JRadioButton(SEARCH);
		// temporarily disabled while option unavailable
		ButtonGroup annealSearchGroup = new ButtonGroup();
		annealSearchGroup.add(annealButton);
		annealSearchGroup.add(searchButton);
		buttonsPanel.add(annealButton);
		buttonsPanel.add(searchButton);
		
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.add(buttonsPanel)
				.add(annealSearchContentPanel)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.add(buttonsPanel)
				.add(annealSearchContentPanel)
		);
		Border ascBorder = BorderFactory.createLineBorder(Color.black);
		Border ascTitledBorder = BorderFactory.createTitledBorder(ascBorder,
				"Strategy", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION);
		annealSearchControlPanel.setBorder(ascTitledBorder);

		AnnealSearchSwitchListener switchListener = new AnnealSearchSwitchListener();
		annealButton.addActionListener(switchListener);
		searchButton.addActionListener(switchListener);

		CardLayout cl = (CardLayout) annealSearchContentPanel.getLayout();
		annealSearchContentPanel.add(searchContentPanel, SEARCH);
		annealSearchContentPanel.add(annealContentPanel, ANNEAL);
		if (apfParams.getGreedySearch()) {
			currentContentPanel = searchContentPanel;
			switchAnnealSearchContentPanel(SEARCH);
			searchButton.setSelected(true);
		} else {
			currentContentPanel = annealContentPanel;
			switchAnnealSearchContentPanel(ANNEAL);
			annealButton.setSelected(true);
		}
		annealSearchControlPanel.add(annealSearchContentPanel);
	}

	private void switchAnnealSearchContentPanel(String name)
	{
		CardLayout cl = (CardLayout) annealSearchContentPanel.getLayout();
		cl.show(annealSearchContentPanel, name);
	}

	private JPanel createRandomSeedController() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 1));
		JLabel seedGraphOptionsLabel = new JLabel("Seed Graph Options:");
		JRadioButton smallPrimeNumberSeedButton = new JRadioButton(NONRANDOM);
		JRadioButton dateBasedSeedButton = new JRadioButton(
				"Random Based on Current Time");
		dateBasedSeedButton.setSelected(true);

		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(smallPrimeNumberSeedButton);
		buttonGroup.add(dateBasedSeedButton);

		panel.add(seedGraphOptionsLabel);
		panel.add(smallPrimeNumberSeedButton);
		panel.add(dateBasedSeedButton);

		RandomSeedListener listener = new RandomSeedListener();
		smallPrimeNumberSeedButton.addActionListener(listener);
		dateBasedSeedButton.addActionListener(listener);

		panel.add(smallPrimeNumberSeedButton); // , BorderLayout.CENTER);
		panel.add(dateBasedSeedButton); // , BorderLayout.CENTER);
		panel.add(readout);
		return panel;

	} // createRandomSeedController

	// -----------------------------------------------------------------------------
	private void createHubfindingController() {
		hfcPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		HFListener listener = new HFListener();
		boolean hfInit = (apfParams.getMinHubSize() == 0) ? false : true;
		hubBox = new JCheckBox("Hubfinding: ", hfInit);
		hubNum = new JTextField(Integer.toString(apfParams.getMinHubSize()));

		if (!hfInit) {
			hubNum.setText("10");
			hubNum.setEnabled(false);
			hubBox.setSelected(false);
			apfParams.setMinHubSize(0);
		}
		hubBox.addItemListener(listener);
		hubNum.addFocusListener(listener);

		hfcPanel.add(hubBox);
		hfcPanel.add(hubNum);

		return;

	} // createHubfindingController

	private void createHubAdjustmentController() {
		hAdjPanel = new JPanel();
		hAdjPanel.setLayout(new GridLayout(1, 2));
		HAListener listener = new HAListener();
		boolean haInit = (apfParams.getHubAdjustment() == 0) ? false : true;
		hubAdjustmentBox = new JCheckBox("Hub Penalty", haInit);
		// temporarily disabled while option not available
		hubAdjustmentBox.setEnabled(false);
		hubAdjustmentNum = new JTextField(Double.toString(apfParams
				.getHubAdjustment()));

		if (!haInit) {
			hubAdjustmentNum.setText("0.406");
			hubAdjustmentNum.setEnabled(false);
			hubAdjustmentBox.setSelected(false);
			apfParams.setHubAdjustment(0);
		}
		hubAdjustmentBox.addItemListener(listener);
		hubAdjustmentNum.addFocusListener(listener);

		hAdjPanel.add(hubAdjustmentBox);
		hAdjPanel.add(hubAdjustmentNum);

		return;

	} // createHubAdjustmentController

	private void createSearchController() {
		searchPanel = new JPanel();
		GroupLayout layout = new GroupLayout(searchPanel);
		searchPanel.setLayout(layout);

		AnnealSearchSwitchListener listener = new AnnealSearchSwitchListener();
		searchDepth = new JTextField(Integer.toString(apfParams
				.getSearchDepth()), 2);
		searchFromNodesBox = new JCheckBox("Search from selected nodes?",
				apfParams.getSearchFromNodes());
		JPanel searchFromNodesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchFromNodesPanel.add(searchFromNodesBox);

		JPanel searchDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		searchDepthPanel.add(new JLabel("Search depth: "));
		searchDepthPanel.add(searchDepth);
		
		maxBox = new JCheckBox("Max depth from start nodes:", apfParams
				.getEnableMaxDepth());
		maxDepth = new JTextField(Integer.toString(apfParams.getMaxDepth()), 2);
		MListener mListener = new MListener();
		maxDepth.setEnabled(apfParams.getEnableMaxDepth());
		maxBox.addItemListener(mListener);
		maxDepth.addFocusListener(mListener);

		JPanel maxDepthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		maxDepthPanel.add(maxBox);
		maxDepthPanel.add(maxDepth);
		searchDepth.addFocusListener(listener);
		searchFromNodesBox.addItemListener(new SFNListener());

		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.add(searchDepthPanel)
				.add(searchFromNodesPanel)
				.add(maxDepthPanel)
		);

		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.add(searchDepthPanel)
				.add(searchFromNodesPanel)
				.add(maxDepthPanel)
		);

		return;

	} // createSearchController

	// -----------------------------------------------------------------------------
	private void createIterationsController() {
		iterPanel = new JPanel();
		iterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		IterListener listener = new IterListener();
		iterLabel = new JLabel("Iterations (0-10^8)");
		iterNum = new JTextField(Integer.toString(apfParams
				.getTotalIterations()), 5);

		iterNum.addFocusListener(listener);

		iterPanel.add(iterLabel);
		iterPanel.add(iterNum);

		return;

	} // createIterationsController

	// -----------------------------------------------------------------------------
	private void createPathsController() {
		pathPanel = new JPanel();
		GroupLayout layout = new GroupLayout(pathPanel);
		pathPanel.setLayout(layout);

		pathLabel = new JLabel("Number of Modules (1-1000): ");
		pathNum = new JTextField(Integer.toString(apfParams.getNumberOfPaths()));
		java.awt.FontMetrics fontMetrics = pathNum.getFontMetrics(pathNum.getFont());
		pathNum.setMaximumSize(new Dimension( fontMetrics.charWidth('m') * 7, fontMetrics.getHeight() ));
		pathNum.addFocusListener(new PathListener());

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.add(pathLabel)
				.add(pathNum)
		);

		layout.setVerticalGroup(
			layout.createParallelGroup()
				.add(pathLabel)
				.add(pathNum)
		);
	} // createPathsController

	private void createOverlapController() {
		overlapPanel = new JPanel();
		GroupLayout layout = new GroupLayout(overlapPanel);
		overlapPanel.setLayout(layout);

		overlapLabel = new JLabel("Overlap Threshold: ");
		overlapNum = new JTextField(Double.toString(apfParams.getOverlapThreshold()));
		java.awt.FontMetrics fontMetrics = overlapNum.getFontMetrics(pathNum.getFont());
		overlapNum.setMaximumSize(new Dimension( fontMetrics.charWidth('m') * 7, fontMetrics.getHeight() ));
		overlapNum.addFocusListener(new OverlapListener());

		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.add(overlapLabel)
				.add(overlapNum)
		);

		layout.setVerticalGroup(
			layout.createParallelGroup()
				.add(overlapLabel)
				.add(overlapNum)
		);
	} // createPathsController
    
	// -----------------------------------------------------------------------------

	class TempController {

		public TempController() {

			tStartPanel = new JPanel();
			tEndPanel = new JPanel();
			startNum = new JTextField(Double.toString(apfParams
					.getInitialTemperature()), 5);
			endNum = new JTextField(Double.toString(apfParams
					.getFinalTemperature()), 5);

			TempListener listener = new TempListener();
			tempLabelStart = new JLabel("Start Temp (0.0001 - 100)");
			tempLabelEnd = new JLabel("End Temp (0.0001 - Start)");

			//tStartPanel.setLayout(new GridLayout(0, 1));
			tStartPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
			tStartPanel.add(tempLabelStart);
			tStartPanel.add(startNum);

			//tEndPanel.setLayout(new GridLayout(0, 1));
			tEndPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
			tEndPanel.add(tempLabelEnd);
			tEndPanel.add(endNum);

			startNum.addFocusListener(listener);
			endNum.addFocusListener(listener);
		}

	} // TempController

	// -----------------------------------------------------------------------------
	private void createIntervalController() {

		intervalNum = new JTextField(Integer.toString(apfParams
				.getDisplayInterval()));
		intervalNum.setEnabled(false);
		intervalLabel = new JLabel("Display Interval (1-1e05)");
		intervalPanel = new JPanel();

		intervalPanel.setLayout(new GridLayout(0, 1));
		IntervalListener listener = new IntervalListener();

		intervalNum.addFocusListener(listener);

		intervalPanel.add(intervalLabel);
		intervalPanel.add(intervalNum);

	} // createIntervalController

	private void createMontecarloController() {
		boolean mcInit = apfParams.getMCboolean();
		mcBox = new JCheckBox("Adjust score for size?", mcInit);
		mcBox.addItemListener(new MCListener());
	} // createMontecarloController

	private void createRegionalScoringController() {
		boolean regionalInit = apfParams.getRegionalBoolean();
		regionalBox = new JCheckBox("Regional Scoring?", regionalInit);
		// temporarily disabled while option not available
		// regionalBox.setEnabled(false);
		regionalBox.addItemListener(new RSListener());
	} // createRegionalScoringController


	private void createHelpDialog()
	{
		helpDialog = new JDialog(Cytoscape.getDesktop(), "jActiveModules Help");
		helpDialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		try
		{
			JEditorPane helpPane = new JEditorPane(parentUI.getClass().getResource("/help.html"));
			helpPane.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(helpPane);
			helpDialog.setContentPane(scrollPane);
			helpDialog.setPreferredSize(new Dimension(750, 400));
		}
		catch (IOException e)
		{
			JLabel label = new JLabel("Could not find help.html.");
			helpDialog.setContentPane(label);
		}
		helpDialog.pack();
	}

	// -----------------------------------------------------------------------------
	class RandomSeedListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String setting = e.getActionCommand();
			if (setting.equals(NONRANDOM))
				apfParams.setRandomSeed(17);
			else
				apfParams.setRandomSeed(Math.abs((int) System
						.currentTimeMillis()));
			readout.setText("seed: " + apfParams.getRandomSeed());
		}

	} // RandomSeedListener

	// -----------------------------------------------------------------------------
	class AnnealSearchSwitchListener implements ActionListener, FocusListener {
		public void focusGained(FocusEvent e) {
			validate();
		}
		public void focusLost(FocusEvent e) {
			validate();
		}
		public void actionPerformed(ActionEvent e) {
			String setting = e.getActionCommand();
			if (setting.equals(ANNEAL)) {
				validate();
				switchToAnneal();
			} else if (setting.equals(SEARCH)) {
				validate();
				switchToSearch();
			}
		}
		private void switchToAnneal() {
			if (currentContentPanel != annealContentPanel) {
				currentContentPanel = annealContentPanel;
				switchAnnealSearchContentPanel(ANNEAL);
				apfParams.setGreedySearch(false);
			}
		}
		private void switchToSearch() {
			if (currentContentPanel != searchContentPanel) {
				currentContentPanel = searchContentPanel;
				switchAnnealSearchContentPanel(SEARCH);
				apfParams.setGreedySearch(true);
			}
		}
		private void validate() {
			if (searchButton.isSelected()) {
				String st = searchDepth.getText();
				//String st2 = st.replaceAll("[^0-9]", ""); // ditch all
				// non-numeric
				if (st.length() > 0) {
					try {
						int si = Integer.parseInt(st);
						if (si < 0) {
							searchDepth.setText("0");
							apfParams.setSearchDepth(0);
						} else if (si > 10) {
							searchDepth.setText("10");
							apfParams.setSearchDepth(10);
						} else {
							searchDepth.setText(st);
							apfParams.setSearchDepth(si);
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Not an int: " + st);
						searchDepth.setText("1");
						apfParams.setSearchDepth(1);
					}
				} else {
					searchDepth.setText("1");
					apfParams.setSearchDepth(1);
				}
			} else
				apfParams.setSearchDepth(1);
		}

	} // AnnealSearchSwitchListener

	// -----------------------------------------------------------------------------
	class RandomSeedTextListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String rt = readout.getText();
			String rt2 = rt.replaceAll("[^0-9]", ""); // ditch all non-numeric
			if (rt2.length() > 0) {
				// System.out.println(" length " + rt2.length());
				try {
					int seed = Integer.parseInt(rt2);
					apfParams.setRandomSeed(seed);
					readout.setText("seed: " + apfParams.getRandomSeed());
				} catch (NumberFormatException nfe) {
					System.out.println("Not an integer: " + rt2);
					apfParams.setRandomSeed(0);
					readout.setText("seed: " + apfParams.getRandomSeed());
				}
			} // if gt 0
			else {
				apfParams.setRandomSeed(0);
				readout.setText("seed: " + apfParams.getRandomSeed());
			} // if gt 0 (else)
		} // validate()
	} // RandomSeedTextListener

	// -----------------------------------------------------------------------------
	class MCListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			apfParams.setMCboolean(mcBox.isSelected());
		}
	}
	class RSListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			apfParams.setRegionalBoolean(regionalBox.isSelected());
		}
	}
	class SFNListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			apfParams.setSearchFromNodes(searchFromNodesBox.isSelected());
		}
	}
	class QuenchCheckListener implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			JCheckBox jcb = (JCheckBox) e.getItem();
			apfParams.setToQuench(jcb.isSelected());
		}
	}

	class HFListener implements ItemListener, FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}
		public void itemStateChanged(ItemEvent e) {
			hubNum.setEnabled(hubBox.isSelected());
			validate();
		}

		private void validate() {
			if (hubBox.isSelected()) {
				String ht = hubNum.getText();
				String ht2 = ht.replaceAll("[^0-9]", ""); // ditch all
				// non-numeric
				if (ht.length() > 0) {
					// System.out.println(" length " + ht.length());
					try {
						int hf = Integer.parseInt(ht2);
						if (hf <= 0) {
							hubNum.setText("10");
							hubNum.setEnabled(false);
							hubBox.setSelected(false);
							apfParams.setMinHubSize(0);
						} else if (hf > 10000) {
							hubNum.setText("10000");
							apfParams.setMinHubSize(10000);
						} else {
							hubNum.setText(ht2);
							apfParams.setMinHubSize(hf);
						}

					} catch (NumberFormatException nfe) {
						System.out.println("Not an integer: " + ht2);
						hubNum.setText("10");
						apfParams.setMinHubSize(10);
						// JOptionPane.showMessageDialog (mainPanel, "Not an
						// integer: " + ht2);
					}
				} else {
					// System.out.println(" length " + ht.length());
					// System.out.println(" going for 10.");
					hubNum.setText("10");
					apfParams.setMinHubSize(10);
				}
			} else
				apfParams.setMinHubSize(0);
		}

	} // HFListener

	class HAListener implements ItemListener, FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}
		public void itemStateChanged(ItemEvent e) {
			hubAdjustmentNum.setEnabled(hubAdjustmentBox.isSelected());
			validate();
		}

		private void validate() {
			if (hubAdjustmentBox.isSelected()) {
				String ht = hubAdjustmentNum.getText();
				String ht2 = ht.replaceAll("[^0-9.]", ""); // ditch all
				// non-numeric
				if (ht.length() > 0) {
					// System.out.println(" length " + ht.length());
					try {
						double hf = Double.parseDouble(ht2);
						if (hf <= 0) {
							hubAdjustmentNum.setText("0.406");
							hubAdjustmentNum.setEnabled(false);
							hubAdjustmentBox.setSelected(false);
							apfParams.setHubAdjustment(0);
						} else if (hf > 100) {
							hubAdjustmentNum.setText("100");
							apfParams.setHubAdjustment(100);
						} else {
							hubAdjustmentNum.setText(ht2);
							apfParams.setHubAdjustment(hf);
						}

					} catch (NumberFormatException nfe) {
						System.out.println("Not a double: " + ht2);
						hubAdjustmentNum.setText("0.406");
						apfParams.setHubAdjustment(0.406);
					}
				} else {
					hubAdjustmentNum.setText("0.406");
					apfParams.setHubAdjustment(0.406);
				}
			} else
				apfParams.setHubAdjustment(0);
		}

	} // HAListener

	class MListener implements ItemListener, FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}
		public void itemStateChanged(ItemEvent e) {
			maxDepth.setEnabled(maxBox.isSelected());
			apfParams.setEnableMaxDepth(maxBox.isSelected());
			validate();
		}
		private void validate() {
			if (maxBox.isSelected()) {
				String st = maxDepth.getText();
				//String st2 = st.replaceAll("[^0-9]", ""); // ditch all
				// non-numeric
				if (st.length() > 0) {
					try {
						int si = Integer.parseInt(st);
						if (si < 0) {
							maxDepth.setText("0");
							// maxDepth.setEnabled(false);
							// maxBox.setSelected(false);
							apfParams.setMaxDepth(0);
						} else if (si > 10) {
							maxDepth.setText("10");
							apfParams.setMaxDepth(10);
						} else {
							maxDepth.setText(st);
							apfParams.setMaxDepth(si);
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Not an int: " + st);
						maxDepth.setText("1");
						apfParams.setMaxDepth(1);
					}
				} else {
					maxDepth.setText("1");
					apfParams.setMaxDepth(1);
				}
			} else {
				maxDepth.setEnabled(false);
				// apfParams.setMaxDepth(1);
			}
			// }
		}
	} // MListener

	// -----------------------------------------------------------------------------
	class IterListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String it = iterNum.getText();
			String it2 = it.replaceAll("[^0-9]", ""); // ditch all
			// non-numeric
			if (it2.length() > 0) {
				if (it2.length() >= 9) {
					iterNum.setText("100000000");
					apfParams.setTotalIterations(100000000);
				} else {
					// System.out.println(" length " + it.length());
					try {
						int iters = Integer.parseInt(it2);
						if (iters <= 0) {
							iterNum.setText("0");
							apfParams.setTotalIterations(0);
						} else if (iters > 100000000) {
							iterNum.setText("100000000");
							apfParams.setTotalIterations(100000000);
						} else {
							iterNum.setText(it2);
							apfParams.setTotalIterations(iters);
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Not an integer: " + it2);
						iterNum.setText("0");
						apfParams.setTotalIterations(0);
					}
				} // if gt 9 (else)
			} // if gt 0
			else {
				iterNum.setText("0");
				apfParams.setTotalIterations(0);
			} // if gt 0 (else)
		}

	} // IterListener

	// -----------------------------------------------------------------------------
	class OverlapListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String pt = overlapNum.getText();
			try{
			    double overlap = Double.parseDouble(pt);
			    if(overlap < 0.0){
				overlap = 0.0;
			    }
			    if(overlap > 1.0){
				overlap = 1.0;
			    }
			    overlapNum.setText((new Double(overlap)).toString());
			    apfParams.setOverlapThreshold(overlap);
			} catch (NumberFormatException nfe) {
			    overlapNum.setText("0.8");
			    apfParams.setOverlapThreshold(0.8);
			}
		}

	} // PathListener
	class PathListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String pt = pathNum.getText();
			String pt2 = pt.replaceAll("[^0-9]", ""); // ditch all
			// non-numeric
			if (pt2.length() > 0) {
				if (pt2.length() > 3) {
					pathNum.setText("1000");
					apfParams.setNumberOfPaths(1000);
				} else {
					// System.out.println(" length " + pt.length());
					try {
						int paths = Integer.parseInt(pt2);
						if (paths <= 0) {
							pathNum.setText("0");
							apfParams.setNumberOfPaths(0);
						} else if (paths > 1000) {
							pathNum.setText("1000");
							apfParams.setNumberOfPaths(1000);
						} else {
							pathNum.setText(pt2);
							apfParams.setNumberOfPaths(paths);
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Not an integer: " + pt2);
						pathNum.setText("0");
						apfParams.setNumberOfPaths(0);
					}
				} // if gt 3 (else)
			} // if gt 0
			else {
				pathNum.setText("0");
				apfParams.setNumberOfPaths(0);
			} // if gt 0 (else)
		}

	} // PathListener

	// -----------------------------------------------------------------------------
	class TempListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String st = startNum.getText();
			String st2 = st.replaceAll("[^0-9.]", ""); // ditch all
			// non-numeric
			String et = endNum.getText();
			String et2 = et.replaceAll("[^0-9.]", ""); // ditch all
			// non-numeric

			// //////////////////////////////////////////////
			// first handle start temp
			if (st2.length() > 0) {
				// System.out.println(" length " + st2.length());
				try {
					double startTemp = Double.parseDouble(st2);
					if (startTemp <= 0) {
						startNum.setText("0.0001");
						apfParams.setInitialTemperature(0.0001);
					} else if (startTemp > 100.0) {
						startNum.setText("100.0");
						apfParams.setInitialTemperature(100.0);
					} else {
						startNum.setText(st2);
						apfParams.setInitialTemperature(startTemp);
					}
				} catch (NumberFormatException nfe) {
					System.out.println("Not a number: " + st2);
					startNum.setText("1.000");
					apfParams.setInitialTemperature(1.000);
				}
			} // if gt 0
			else {
				startNum.setText("1.000");
				apfParams.setInitialTemperature(1.000);
			} // if gt 0 (else)

			// //////////////////////////////////////////////
			// then handle end temp
			if (et2.length() > 0) {
				// System.out.println(" length " + et2.length());
				try {
					double endTemp = Double.parseDouble(et2);
					if (endTemp <= 0) {
						endNum.setText("0.0001");
						apfParams.setFinalTemperature(0.0001);
					} else if (endTemp > apfParams.getInitialTemperature()) {
						endNum.setText(startNum.getText());
						apfParams.setFinalTemperature(apfParams
								.getInitialTemperature());
					} else {
						endNum.setText(et2);
						apfParams.setFinalTemperature(endTemp);
					}
				} catch (NumberFormatException nfe) {
					System.out.println("Not a number: " + et2);
					endNum.setText("0.0001");
					apfParams.setFinalTemperature(0.0001);
				}
			} // if gt 0
			else {
				endNum.setText("0.0001");
				apfParams.setFinalTemperature(0.0001);
			} // if gt 0 (else)

		} // validate

	} // TempListener

	// -------------------------------------------------------------
	class IntervalListener implements FocusListener {
		public void focusGained(FocusEvent e) {
			// System.out.println("gained");
			validate();
		}
		public void focusLost(FocusEvent e) {
			// System.out.println("lost");
			validate();
		}

		private void validate() {
			String it = intervalNum.getText();
			String it2 = it.replaceAll("[^0-9]", ""); // ditch all
			// non-numeric
			if (it2.length() > 0) {
				if (it2.length() >= 6) {
					intervalNum.setText("100000");
					apfParams.setDisplayInterval(100000);
				} else {
					// System.out.println(" length " + it2.length());
					try {
						int intervals = Integer.parseInt(it2);
						if (intervals <= 0) {
							intervalNum.setText("0");
							apfParams.setDisplayInterval(0);
						} else if (intervals > 100000) {
							intervalNum.setText("100000");
							apfParams.setDisplayInterval(100000);
						} else {
							intervalNum.setText(it2);
							apfParams.setDisplayInterval(intervals);
						}
					} catch (NumberFormatException nfe) {
						System.out.println("Not an integer: " + it2);
						intervalNum.setText("0");
						apfParams.setDisplayInterval(0);
					}
				} // if gte 6 (else)
			} // if gt 0
			else {
				intervalNum.setText("0");
				apfParams.setDisplayInterval(0);
			} // if gt 0 (else)
		}

	} // IntervalListener

	// } // QuitAction
	// -----------------------------------------------------------------------------
	public class DismissAction extends AbstractAction {

		DismissAction() {
			super("");
		}

		public void actionPerformed(ActionEvent e) {
			// listener.cancelActivePathsFinding ();
			CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			cytoPanel.remove(ActivePathsParametersPopupDialog.this);
		}

	} // DismissAction

	public class FindModulesAction extends AbstractAction {

		public FindModulesAction() {
			super("Find Modules");
		}

		public void actionPerformed(ActionEvent e) {
			parentUI.startFindActivePaths(Cytoscape.getCurrentNetwork());
		}

	}
	
	class GenericListener extends FocusAdapter implements ActionListener{
		public void focusLost(FocusEvent fe){
			this.actionPerformed(new ActionEvent(this,0,""));
		}
		public void actionPerformed(ActionEvent ae){
			//do nothing
		}
		
	}

	class ExprAttrsListListener extends MouseAdapter
	{
		private JList parentList;
		private ActivePathFinderParameters apfParams;

		public ExprAttrsListListener(JList parentList, ActivePathFinderParameters apfParams)
		{
			this.parentList = parentList;
			this.apfParams = apfParams;
		}

		public void mouseClicked(MouseEvent e)
		{
			String selectedString = parentList.getSelectedValue().toString();
			if (apfParams.getExpressionAttributes().contains(selectedString))
				apfParams.removeExpressionAttribute(selectedString);
			else
				apfParams.addExpressionAttribute(selectedString);

			int[] indices = new int[apfParams.getExpressionAttributes().size()];
			int   count = 0;
			for (String exprAttr : apfParams.getExpressionAttributes())
			{
				int i = apfParams.getPossibleExpressionAttributes().indexOf(exprAttr);
				if (i != -1)
					indices[count++] = i;
			}

			parentList.setSelectedIndices(indices);
			updateOptionsPanel();

		}
	}

	private boolean isSignificanceValue(String name) {
		return true;		
	}

	// -----------------------------------------------------------------------------
} // class ActivePathsParametersPopupDialog

