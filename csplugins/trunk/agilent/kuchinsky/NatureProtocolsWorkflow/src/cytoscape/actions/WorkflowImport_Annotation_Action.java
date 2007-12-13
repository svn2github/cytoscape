package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

/**
 * Imports GO annotations
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowImport_Annotation_Action extends WorkflowPanelAction {

	private static final String IMPORT_MENU_TEXT = "Import";

	private static final String ANNOTATION_MENU_TEXT = "Ontology and Annotation...";

	private String menuItemText;

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowImport_Annotation_Action (String myString) {
		super(myString);

	}
	
	public String getToolTipText() 
	{
		return new String (
				"<html>Import Gene Ontology and Gene Association Files<br> " + 
				" Cytoscape has a list of URLs for commonly used ontology data and a complete <br>" + 
				" set of Gene Association files. </html>"
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
													.equals(ANNOTATION_MENU_TEXT)) {
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
