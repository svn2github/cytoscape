package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

public class Workflow_cPath_Action extends WorkflowPanelAction {

	private static final String CPATH_NEW_MENU_TEXT = "New";

	private static final String CPATH_NETWORK_MENU_TEXT = "Network";

	private static final String CPATH_CONSTRUCT_MENU_TEXT = "Construct network using cPath...";

	private String menuItemText;

	
	public Workflow_cPath_Action(String name) {
		super(name);
	}
	
	public String getToolTipText()
	{
		return new String("<html> query, retrieve and visualize interactions from the " + 
				"cPath database.</html>"
				);
		
	}
	
	public void actionPerformed(ActionEvent e) {

		Component[] pluginsMenuItems = Cytoscape.getDesktop().getCyMenus()
				.getFileMenu().getMenuComponents();
		
		JMenuItem item = null;
		MenuElement [] subMenuItems = null;
		JPopupMenu popup = null;
		Component [] popupItems = null;
		
		// first look for 'File -> New ' menu item
		for (int i = 0; i < pluginsMenuItems.length; i++) {
			Component comp = pluginsMenuItems[i];
			if (comp instanceof JMenuItem) {
				item = (JMenuItem) comp;
				menuItemText = item.getText();
//				System.out
//				.println("SubMenu item text = "
//						+ menuItemText);
				if (menuItemText != null) {
					if (menuItemText.equals(CPATH_NEW_MENU_TEXT)) {
//						System.out.println ("File -> New is instanceof: " + item);
						subMenuItems = item.getSubElements();
						
						// now look for 'File -> New -> Network ' menu item
						for (int j = 0; j < subMenuItems.length; j++) {
							if (subMenuItems[j] instanceof JPopupMenu) {
								popup = (JPopupMenu) subMenuItems[j];
//								System.out.println ("sub-item of File -> New is instance of: " + popup);
//								System.out.println ("with text: " + popup.getLabel());
								popupItems = popup.getComponents();
								for (int k = 0; k < popupItems.length; k++) {
									if (popupItems[k] instanceof JMenuItem) {
										item = (JMenuItem) popupItems[k];
										menuItemText = item.getText();
//										System.out
//												.println("SubMenu item text = "
//														+ menuItemText);
										if (menuItemText != null) {
											if (menuItemText
													.equals(CPATH_NETWORK_MENU_TEXT)) {
												
												// now look for "File -> New -> Network -> Construct network using cPath..." item
												subMenuItems = item
														.getSubElements();
												for (int m = 0; m < subMenuItems.length; m++) {
													System.out.println 
													("SubMenu of this is of class: " + subMenuItems[m].getClass());
													if (subMenuItems[m] instanceof JPopupMenu) {
														popup = (JPopupMenu) subMenuItems[m];
														popupItems = popup
																.getComponents();
														for (int n = 0; n < popupItems.length; n++) {
															if (popupItems[n] instanceof JMenuItem) {
																item = (JMenuItem) popupItems[n];
																menuItemText = item.getText();
//																System.out
//																		.println("SubSubMenu item text = "
//																				+ menuItemText);
																if (menuItemText != null) {
																	if (menuItemText
																			.equals(CPATH_CONSTRUCT_MENU_TEXT)) {
																		ActionListener[] actionListeners = item
																				.getActionListeners();
																		ActionListener action = actionListeners[0];
//																		System.out
//																				.println("Got actionListener: "
//																						+ action);
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
					}
				}
			}
		}
	}
}
