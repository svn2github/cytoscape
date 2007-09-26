package cytoscape.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

public class Workflow_BiNGO_Action extends WorkflowPanelAction {
	
	private static final String BINGO_MENU_TEXT = "BiNGO 2.0";
	private String menuItemText;

	
	public Workflow_BiNGO_Action (String name)
	{
		super(name);
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
					if (menuItemText.equals(BINGO_MENU_TEXT))
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
