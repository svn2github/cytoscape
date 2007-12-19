package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

public class Workflow_jActiveModules_Action extends WorkflowPanelAction {
	
	private static final String JACTIVE_MODULES_MENU_TEXT = "jActiveModules";
	private String menuItemText;

	
	public Workflow_jActiveModules_Action (String name)
	{
		super(name);
	}
	
	public String getToolTipText()
	{
		return new String("<html> search this network for co-regulated subnetworks.</html>"
				);
		
	}	
	
	public void actionPerformed(ActionEvent e)
	{
		if (Cytoscape.getCurrentNetwork() == Cytoscape.getNullNetwork())
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Sorry, you must first have a network to analyze");
			return;
		} 
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
					if (menuItemText.equals(JACTIVE_MODULES_MENU_TEXT))
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
