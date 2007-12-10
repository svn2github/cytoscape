package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

/**
 * Imports a graph of arbitrary type. The types of graphs allowed are defined by
 * the ImportHandler.
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowImportNetworkFromTableAction extends WorkflowPanelAction {

	private static final String IMPORT_MENU_TEXT = "Import";

	private static final String GRAPH_FILE_MENU_TEXT = "Network from Table (Text/MS Excel)...";

	private String menuItemText;

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowImportNetworkFromTableAction(String myString) {
		super(myString);

	}

	public String getToolTipText()
	{
		return new String("<html> Import a network from a table. <br>" + 
				" Table can be delimited text or Excel spreadsheet.</html>"
				);
		
	}
	public void actionPerformed(ActionEvent e) {
		String menuItemText = "";

		Component[] pluginsMenuItems = Cytoscape.getDesktop().getCyMenus()
				.getFileMenu().getMenuComponents();
		for (int i = 0; i < pluginsMenuItems.length; i++) {
			Component comp = pluginsMenuItems[i];
			if (comp instanceof JMenuItem) {
				JMenuItem jItem = (JMenuItem) comp;
				menuItemText = jItem.getText();
				if (menuItemText != null) {
					if (menuItemText.equals(IMPORT_MENU_TEXT)) {
						MenuElement[] subMenuItems = jItem.getSubElements();
						for (int j = 0; j < subMenuItems.length; j++) {
							if (subMenuItems[j] instanceof JPopupMenu) {
								JPopupMenu kPopup = (JPopupMenu) subMenuItems[j];
								Component[] popupItems = kPopup.getComponents();
								for (int k = 0; k < popupItems.length; k++) {
									if (popupItems[k] instanceof JMenuItem) {
										JMenuItem kPopupItem = (JMenuItem) popupItems[k];
										menuItemText = kPopupItem.getText();
										System.out
												.println("SubMenu item text = "
														+ menuItemText);
										if (menuItemText != null) {
											if (menuItemText
													.equals(GRAPH_FILE_MENU_TEXT)) {
												ActionListener[] actionListeners = kPopupItem
														.getActionListeners();
												ActionListener action = actionListeners[0];
												System.out
														.println("Got actionListener: "
																+ action);
												if (action != null) {
													action
															.actionPerformed(null);
												}
											}
										}
									}

								}
							}
						}

					}
				}

			}

		}
	}
}
