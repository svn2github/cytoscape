/**
 * 
 */
package org.systemsbiology.cytoscape.dialog;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.JTextArea;
import javax.swing.JSeparator;

import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeToolBar;

/**
 * @author skillcoy
 */
public class GooseDialog extends JPanel
	{
	private JComboBox gooseChooser;

	private JButton registerButton;

	private JButton updateButton;

	private JButton setIdButton;

	private JButton showButton;

	private JButton hideButton;

	private JButton bcastListButton;

	private JButton bcastNetButton;

	private JButton bcastMatrixButton;

	private JButton bcastHashMapButton;

	private JTextArea messageArea;

	private JLabel messageLabel;

	public GooseDialog()
		{
		createDialog();
		}

	// TODO would be cleaner to just have a method to add an action to a button I
  // think

	public JComboBox getGooseBox()
		{
		return gooseChooser;
		}

	public JButton getIdButton()
		{
		return setIdButton;
		}

	public JButton getRegisterButton()
		{
		return registerButton;
		}

	public JButton getUpdateButton()
		{
		return updateButton;
		}

	public JButton getShowButton()
		{
		return showButton;
		}

	public JButton getHideButton()
		{
		return hideButton;
		}

	public JButton getListButton()
		{
		return bcastListButton;
		}

	public JButton getNetButton()
		{
		return bcastNetButton;
		}

	public JButton getMatrixButton()
		{
		return bcastMatrixButton;
		}

	public JButton getMapButton()
		{
		return bcastHashMapButton;
		}

	public JTextArea getMessageArea()
		{
		return messageArea;
		}

	public JLabel getMessageLabel()
		{
		return messageLabel;
		}

	private Box getConnectionPanel()
		{
		Box ConnectPanel = Box.createHorizontalBox();

		// register this goose with the boss
		registerButton = new JButton("Register");
		registerButton.setEnabled(true);

//		// update button to re-populate boss and all active geese
//		updateButton = new JButton("Update");

		// drop-down menu for goose selection
		gooseChooser = new JComboBox();
		gooseChooser.addItem("Boss");

		JPanel ButtonPane = new JPanel();
		// ButtonPane.add(registerButton);
		//ButtonPane.add(updateButton);

		ConnectPanel.add(ButtonPane);
		ConnectPanel.add(gooseChooser);

		ConnectPanel.add(Box.createHorizontalStrut(20));

		return ConnectPanel;
		}

	private Box getDisplayControlPanel()
		{
		Box DisplayControl = Box.createHorizontalBox();

		// update button to re-populate boss and all active geese
		updateButton = new JButton("Update");
		updateButton.setToolTipText("Update goose list");
		DisplayControl.add(updateButton);
		
		// Show selected goose
		showButton = new JButton(" Show ");
		showButton.setToolTipText("Show selected goose");
		DisplayControl.add(showButton);

		// Hide selected goose
		hideButton = new JButton(" Hide ");
		hideButton.setToolTipText("Hide selected goose");
		DisplayControl.add(hideButton);

		return DisplayControl;
		}

	private Box getBroadcastPanel()
		{
		Box BroadcastPanel = Box.createHorizontalBox();

		BroadcastPanel.add(createMapButton());
		BroadcastPanel.add(createMatrixButton());
		BroadcastPanel.add(createNetworkButton());
		BroadcastPanel.add(createListButton());
		
		
//		JPanel MapMatrixPane = new JPanel();
//		MapMatrixPane.add(createMapButton());
//		MapMatrixPane.add(createMatrixButton());
//
//		JPanel ListNetworkPane = new JPanel();
//		ListNetworkPane.add(createNetworkButton());
//		ListNetworkPane.add(createListButton());
//
//		JPanel All = new JPanel();
//		All.add(ListNetworkPane);
//		All.add(MapMatrixPane);
//
//		BroadcastPanel.add(All);

		return BroadcastPanel;
		}

	// Not currently in use
	private JButton createIdButton()
		{
		// TODO this permits users to change the alias used by cytoscape to
		// broadcast data
		setIdButton = new JButton(" Set Alias ");
		setIdButton.setToolTipText("Set node alias to use in broadcast");
		return setIdButton;
		}

	private JButton createMapButton()
		{
		// Broadcast HashMap
		bcastHashMapButton = new JButton("Map");
		bcastHashMapButton.setToolTipText("Broadcast HashMap");
		bcastHashMapButton.setForeground(Color.CYAN);
		return bcastHashMapButton;
		}

	private JButton createMatrixButton()
		{
		// Broadcast DataMatrix
		bcastMatrixButton = new JButton("Matrix");
		bcastMatrixButton.setToolTipText("Broadcast Matrix");
		bcastMatrixButton.setForeground(Color.BLUE);
		return bcastMatrixButton;
		}

	private JButton createNetworkButton()
		{
		// Broadcast network
		bcastNetButton = new JButton("Net");
		bcastNetButton.setToolTipText("Broadcast Selected Network");
		bcastNetButton.setForeground(Color.RED);
		return bcastNetButton;
		}

	private JButton createListButton()
		{
		// Broadcast name list
		bcastListButton = new JButton("List");
		bcastListButton.setToolTipText("Broadcast Selected Name List");
		bcastListButton.setForeground(Color.MAGENTA);
		return bcastListButton;
		}

	private JTextArea createMessageArea()
		{
		// message area for any info the goose may wish to show
		messageArea = new JTextArea();
		messageArea.setEditable(false);
		messageArea.setLineWrap(true);
		return messageArea;
		}

	private void createDialog()
		{
		Box GaggleToolBar = Box.createVerticalBox();
		JPanel Blank = new JPanel();
		Blank.setSize(5, 30);

		JPanel ConnectDisplayPane = new JPanel(new BorderLayout());
		ConnectDisplayPane.add(new JToolBar().add(getConnectionPanel()), BorderLayout.NORTH);
		ConnectDisplayPane.add( new JToolBar().add(getDisplayControlPanel()), BorderLayout.CENTER);
		ConnectDisplayPane.add(Blank, BorderLayout.SOUTH);

		JPanel Broadcast = new JPanel(new BorderLayout());
		Broadcast.add( Blank, BorderLayout.NORTH);
		Broadcast.add( new JLabel("Broadcast Data:"), BorderLayout.CENTER );
		Broadcast.add( new JToolBar().add(getBroadcastPanel()), BorderLayout.SOUTH);

		JPanel MessageDisplay = new JPanel(new BorderLayout());
		messageLabel = new JLabel("Current Data Type:");
		MessageDisplay.add(Blank, BorderLayout.NORTH);
		MessageDisplay.add(messageLabel, BorderLayout.CENTER);
		MessageDisplay.add(createMessageArea(), BorderLayout.SOUTH);

		GaggleToolBar.add(ConnectDisplayPane, BorderLayout.NORTH);
		GaggleToolBar.add(Broadcast, BorderLayout.CENTER);
		GaggleToolBar.add(MessageDisplay, BorderLayout.SOUTH);

		add(GaggleToolBar);
		}
	}
