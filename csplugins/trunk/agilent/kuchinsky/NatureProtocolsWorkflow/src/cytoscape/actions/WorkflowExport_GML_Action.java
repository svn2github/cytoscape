package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

/**
 * Imports node attributes
 * 
 * OY GEVALT! This is cloned from the code in cytoscape.actions. figure out a
 * better way to do this without duplicating code.
 * 
 */
public class WorkflowExport_GML_Action extends WorkflowPanelAction {

	private static final String EXPORT_MENU_TEXT = "Export";

	private static final String GML_MENU_TEXT = "Network as GML...";

	private String menuItemText;

	/**
	 * Constructor.
	 * 
	 * @param windowMenu
	 *            WindowMenu Object.
	 */
	public WorkflowExport_GML_Action(String myString) {
		super(myString);

	}
	
	public String getToolTipText()
	{
		return new String ("<html> Export a network in GML format. </html>");
	}

	public void actionPerformed(ActionEvent e) {
		String menuItemText = "";
		if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork())
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Sorry, you must first have a network to export");
			return;
		} 
		Component[] pluginsMenuItems = Cytoscape.getDesktop().getCyMenus()
				.getFileMenu().getMenuComponents();
		for (int i = 0; i < pluginsMenuItems.length; i++) {
			Component comp = pluginsMenuItems[i];
			if (comp instanceof JMenuItem) {
				JMenuItem jItem = (JMenuItem) comp;
				menuItemText = jItem.getText();
				if (menuItemText != null) {
					if (menuItemText.equals(EXPORT_MENU_TEXT)) {
						MenuElement[] subMenuItems = jItem.getSubElements();
						for (int j = 0; j < subMenuItems.length; j++) {
							if (subMenuItems[j] instanceof JPopupMenu) {
								JPopupMenu kPopup = (JPopupMenu) subMenuItems[j];
								Component[] popupItems = kPopup.getComponents();
								for (int k = 0; k < popupItems.length; k++) {
									if (popupItems[k] instanceof JMenuItem) {
										JMenuItem kPopupItem = (JMenuItem) popupItems[k];
										menuItemText = kPopupItem.getText();

										if (menuItemText != null) {
											if (menuItemText
													.equals(GML_MENU_TEXT)) {
												ActionListener[] actionListeners = kPopupItem
														.getActionListeners();
												ActionListener action = actionListeners[0];

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

