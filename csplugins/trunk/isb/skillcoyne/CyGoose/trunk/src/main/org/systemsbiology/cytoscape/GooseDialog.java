/**
 * 
 */
package org.systemsbiology.cytoscape;

import java.awt.Color;
import java.awt.BorderLayout;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.rmi.server.UnicastRemoteObject;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JDialog;


import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeToolBar;

/**
 * @author skillcoy
 *
 */
public class GooseDialog 
	{
	protected JScrollPane scrollPane;

	// geese available for broadcast 	
	protected JComboBox gooseChooser;
	protected JPanel gagglePluginPanel;

	protected JButton registerButton;
	protected JButton updateButton;
	protected JButton setIdButton; 
	protected JButton showButton;
	protected JButton hideButton;
	protected JButton bcastListButton;
	protected JButton bcastNetButton;
	protected JButton bcastMatrixButton;
	protected JButton bcastHashMapButton;

	public GooseDialog()
		{ createDialog(); }
	
	public void createDialog()
		{
		CytoscapeToolBar cyToolBar = Cytoscape.getDesktop().getCyMenus().getToolBar();

		/*
     * new panel for gaggle plugin
     */
		gagglePluginPanel = new JPanel();
		gagglePluginPanel.setBorder(BorderFactory.createEtchedBorder());

		/*
     * register this goose with the boss button automatically disables when
     * goose is successfully connected to the boss
     */
		registerButton = new JButton(" Register ");
		registerButton.setEnabled(true);


		/*
     * update button to re-populate boss and all active geese
     */
		updateButton = new JButton(" Update ");

		/*
     * configure NodeId
     */
		setIdButton = new JButton(" Set ID ");

		/*
     * drop-down menu for goose selection
     */
		gooseChooser = new JComboBox();
		gooseChooser.setPrototypeDisplayValue("-Cyto Networks-");
		gooseChooser.addItem("Boss");

		// Show selected goose
		showButton = new JButton(" S ");
		showButton.setToolTipText("Show Selected Goose");
		showButton.setForeground(Color.BLACK);
		// Hide selected goose
		hideButton = new JButton(" H ");
		hideButton.setToolTipText("Hide Selected Goose");
		hideButton.setForeground(Color.MAGENTA);
		// Broadcast name list
		bcastListButton = new JButton(" L ");
		bcastListButton.setToolTipText("Broadcast Selected Name List");
		bcastListButton.setForeground(Color.GRAY);
		// Broadcast network
		bcastNetButton = new JButton(" N ");
		bcastNetButton.setToolTipText("Broadcast Selected Network");
		bcastNetButton.setForeground(Color.RED);
		// Broadcast DataMatrix
		bcastMatrixButton = new JButton(" M ");
		bcastMatrixButton.setToolTipText("Broadcast Matrix");
		bcastMatrixButton.setForeground(Color.BLUE);
		// Broadcast HashMap
		bcastHashMapButton = new JButton(" P ");
		bcastHashMapButton.setToolTipText("Broadcast HashMap");
		bcastHashMapButton.setForeground(Color.ORANGE);


		// gagglePluginPanel.add(setIdButton);
		gagglePluginPanel.add(registerButton);
		gagglePluginPanel.add(updateButton);
		gagglePluginPanel.add(gooseChooser);
		gagglePluginPanel.add(showButton);
		gagglePluginPanel.add(hideButton);
		gagglePluginPanel.add(bcastListButton);
		gagglePluginPanel.add(bcastNetButton);
		gagglePluginPanel.add(bcastMatrixButton);
		gagglePluginPanel.add(bcastHashMapButton);

		cyToolBar.add(gagglePluginPanel, BorderLayout.SOUTH);

		// resize Cytoscape desktop to show the plugin panel
		// TODO: would rather not resize, but add the toolbar to the bottom of the panel
		int curWinWidth = Cytoscape.getDesktop().getWidth();
		int curWinHeight = Cytoscape.getDesktop().getHeight();
		Cytoscape.getDesktop().setSize(curWinWidth + 270, curWinHeight);
		}
	
	
	}
