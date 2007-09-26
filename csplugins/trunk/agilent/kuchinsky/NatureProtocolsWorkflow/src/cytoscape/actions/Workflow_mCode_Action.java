package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

import cytoscape.Cytoscape;

public class Workflow_mCode_Action extends WorkflowPanelAction {
	
	private static final String MCODE_MENU_TEXT = "MCODE";
	private static final String MCODE_START_MENU_TEXT = "Start MCODE";

	private String menuItemText;

	
	public Workflow_mCode_Action (String name)
	{
		super(name);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork())
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Sorry, you must first have a network to analyze");
		} 
		else
		{
			Component[] pluginsMenuItems = Cytoscape.getDesktop().getCyMenus().getOperationsMenu().getMenuComponents();
			for (int i = 0; i < pluginsMenuItems.length; i++)
			{
				Component comp = pluginsMenuItems[i];
				if (comp instanceof JMenuItem)
				{
					JMenuItem jItem = (JMenuItem) comp;
					menuItemText = jItem.getText();
					if (menuItemText != null)
					{
						if (menuItemText.equals(MCODE_MENU_TEXT))
						{
							MenuElement [] subMenuItems = jItem.getSubElements();
							for (int j = 0; j < subMenuItems.length; j++)
							{
								if (subMenuItems[j] instanceof JPopupMenu)
								{
									JPopupMenu kPopup = (JPopupMenu) subMenuItems[j];
								    Component[] popupItems = kPopup.getComponents();
								    for (int k = 0; k < popupItems.length; k++)
								    {
								    	if (popupItems[k] instanceof JMenuItem)
								    	{
								    		JMenuItem kPopupItem = (JMenuItem) popupItems[k];
											menuItemText = kPopupItem.getText();
											System.out.println("SubMenu item text = " + menuItemText);
											if (menuItemText != null)
											{
												if (menuItemText.equals(MCODE_START_MENU_TEXT))
												{
													ActionListener [] actionListeners = kPopupItem.getActionListeners();
													ActionListener action = actionListeners[0];
													System.out.println("Got actionListener: " + action);
													if (action != null)
													{
														action.actionPerformed(null);
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
