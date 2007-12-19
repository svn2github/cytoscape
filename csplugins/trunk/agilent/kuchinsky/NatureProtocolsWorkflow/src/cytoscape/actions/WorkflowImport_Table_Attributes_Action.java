package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

/**
 * Imports attributes from a table, such as Excel
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowImport_Table_Attributes_Action extends WorkflowPanelAction {

	private static final String IMPORT_MENU_TEXT = "Import";

	private static final String TABLE_ATTRIBUTES_MENU_TEXT = "Attribute from Table (Text/MS Excel)...";

	private String menuItemText;

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowImport_Table_Attributes_Action(String myString) {
		super(myString);

	}
	
	public String getToolTipText()
	{
		return new String("Import node and edge information from a delimited text file or MS Excel worksheet.");
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
													.equals(TABLE_ATTRIBUTES_MENU_TEXT)) {
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
