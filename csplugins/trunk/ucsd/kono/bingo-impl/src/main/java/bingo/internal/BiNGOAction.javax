package BiNGO.internal;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.AbstractCyAction;

import BiNGO.internal.ui.SettingsPanel;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import java.io.File;

public class BiNGOAction extends AbstractCyAction {

	
	private static final String CURRENT_WORKING_DIRECTORY = "user.dir";
	private static final String MENU_NAME = "Start BiNGO Plugin";
	private static final String MENU_CATEGORY = "Tools";
	
	private static final String WINDOW_TITLE = "BiNGO Settings";

	private String bingoDir;

	private CySwingApplication desktopApp;

	public BiNGOAction(CySwingApplication desktopApp){
		// Add a sub-menu item -- Apps->Sample04->sample04
		super("BiNGO");
		setPreferredMenu("Apps");
		//Specify the menuGravity value to put the menuItem in the desired place
		setMenuGravity(2.0f);

		this.desktopApp = desktopApp;
	}


	/**
	* This method opens the BiNGO settingspanel upon selection of the menu
	* item and opens the settingspanel for BiNGO.
	* 
	* @param event
	*            event triggered when BiNGO menu item clicked.
	*/
	public void actionPerformed(ActionEvent e) {

		String cwd = System.getProperty(CURRENT_WORKING_DIRECTORY);
		bingoDir = new File(cwd, "plugins").toString();

		final JFrame window = new JFrame(WINDOW_TITLE);
		final SettingsPanel settingsPanel = new SettingsPanel(bingoDir);
		window.getContentPane().add(settingsPanel);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.pack();
	
		window.setLocationRelativeTo(this.desktopApp.getJFrame());
		window.setVisible(true);			
	}

}

