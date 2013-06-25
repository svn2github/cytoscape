package bingo.internal;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFrame;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.util.swing.OpenBrowser;

import bingo.internal.ui.SettingsPanel;


public class BingoPluginAction extends AbstractCyAction {

	private static final String CURRENT_WORKING_DIRECTORY = "user.dir";
	private static final String MENU_NAME = "Start BiNGO Plugin";
	private static final String MENU_CATEGORY = "Tools";
	private static final String WINDOW_TITLE = "BiNGO Settings";
	private final CySwingAppAdapter adapter;
	private final OpenBrowser openBrowserService;
	private String bingoDir;
	private static final long serialVersionUID = 4190390703299860130L;

	// The constructor sets the text that should appear on the menu item.
	public BingoPluginAction(final CySwingAppAdapter adapter, final OpenBrowser openBrowserService) {
		super(MENU_NAME);
		this.adapter = adapter;
		this.openBrowserService = openBrowserService;
		setPreferredMenu(MENU_CATEGORY);
		
		//String cwd = System.getProperty(CURRENT_WORKING_DIRECTORY);
		String cwd =   System.getProperty("user.home");

		bingoDir = new File(cwd, adapter.getCoreProperties().DEFAULT_PROPS_CONFIG_DIR+"/3/apps/installed").toString();
	}

	/**
	 * This method opens the bingo settingspanel upon selection of the menu
	 * item and opens the settingspanel for bingo.
	 * 
	 * @param event
	 *            event triggered when bingo menu item clicked.
	 */
	public void actionPerformed(ActionEvent event) {
		final JFrame window = new JFrame(WINDOW_TITLE);
		final SettingsPanel settingsPanel = new SettingsPanel(bingoDir, (CySwingAppAdapter)adapter, openBrowserService);
		window.getContentPane().add(settingsPanel);
		window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		window.pack();

		// Cytoscape Main Window
		final JFrame desktop = adapter.getCySwingApplication().getJFrame();
		window.setLocationRelativeTo(desktop);
		window.setVisible(true);
	}
}
