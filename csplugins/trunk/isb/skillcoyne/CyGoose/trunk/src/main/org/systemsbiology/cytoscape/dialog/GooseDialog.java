/**
 * 
 */
package org.systemsbiology.cytoscape.dialog;

import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeToolBar;


/**
 * @author skillcoy
 */
public class GooseDialog 
	{
	public JScrollPane scrollPane;
	// geese available for broadcast
	public JComboBox gooseChooser;
	public JPanel gagglePluginPanel;
	public JButton registerButton;
	public JButton updateButton;
	public JButton setIdButton;
	public JButton showButton;
	public JButton hideButton;
	public JButton bcastListButton;
	public JButton bcastNetButton;
	public JButton bcastMatrixButton;
	public JButton bcastHashMapButton;

	public GooseDialog()
		{
		createDialog();
		}

	private void createDialog() 
		{
		CytoscapeToolBar cyToolBar = Cytoscape.getDesktop().getCyMenus().getToolBar();
		
		// new panel for gaggle plugin
		gagglePluginPanel = new JPanel();
		gagglePluginPanel.setBorder(BorderFactory.createEtchedBorder());

		/* register this goose with the boss button automatically disables when
		  goose is successfully connected to the boss  */
		registerButton = new JButton(" Register ");
		registerButton.setEnabled(true);

		// update button to re-populate boss and all active geese
		updateButton = new JButton(" Update ");

		// TODO this permits users to change the alias used by cytoscape  to broadcast data
		setIdButton = new JButton(" Set Alias ");
		setIdButton.setToolTipText("Set node alias to use in broadcast");
		
		// drop-down menu for goose selection
		gooseChooser = new JComboBox();
		gooseChooser.setPrototypeDisplayValue("-Cyto Networks-"); // ???
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
		// TODO: would rather not resize, but add the toolbar to the bottom of the
		// panel
		Cytoscape.getDesktop().setSize(Cytoscape.getDesktop().getWidth()+270, 
																	 Cytoscape.getDesktop().getHeight());
		}
	}
