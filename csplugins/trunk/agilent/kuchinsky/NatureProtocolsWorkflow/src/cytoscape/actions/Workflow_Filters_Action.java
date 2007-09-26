package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;

public class Workflow_Filters_Action extends WorkflowPanelAction {
	
	private static final String FILTERS_MENU_TEXT = "Use Filters";
	private String menuItemText;

	
	public Workflow_Filters_Action (String name)
	{
		super(name);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Component[] pluginsMenuItems = Cytoscape.getDesktop().getCyMenus().getSelectMenu().getMenuComponents();
		for (int i = 0; i < pluginsMenuItems.length; i++)
		{
			Component comp = pluginsMenuItems[i];
			if (comp instanceof JMenuItem)
			{
				JMenuItem jItem = (JMenuItem) comp;
				menuItemText = jItem.getText();
				if (menuItemText != null)
				{
					if (menuItemText.equals(FILTERS_MENU_TEXT))
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
