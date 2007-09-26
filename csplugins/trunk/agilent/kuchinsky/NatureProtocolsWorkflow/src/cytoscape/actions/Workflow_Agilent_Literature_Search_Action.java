package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;

public class Workflow_Agilent_Literature_Search_Action extends WorkflowPanelAction {
	
	private static final String AGILENT_LITERATURE_SEARCH_MENU_TEXT = "Agilent Literature Search";
	private String menuItemText;

	
	public Workflow_Agilent_Literature_Search_Action (String name)
	{
		super(name);
	}
	
	public void actionPerformed(ActionEvent e)
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
					if (menuItemText.equals(AGILENT_LITERATURE_SEARCH_MENU_TEXT))
					{
						Action action = jItem.getAction();
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
