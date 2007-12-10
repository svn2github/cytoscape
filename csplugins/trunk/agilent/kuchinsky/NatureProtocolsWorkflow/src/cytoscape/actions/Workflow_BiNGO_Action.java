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
	
	public String getToolTipText()
	{
//		return new String ("<html>BiNGO is a Java-based tool to determine which" + 
//				" Gene Ontology (GO) categories are statistically overrepresented in<br>" + 
//				" a set of genes or a subgraph of a biological network.<br>" + 
//				"BiNGO is implemented as a plugin for Cytoscape, which is a an open source <br>" + 
//				" bioinformatics software platform for visualizing and integrating <br>" +
//				" molecular interaction networks. BiNGO maps the predominant functional <br>" +
//				" themes of a given gene set on the GO hierarchy, and outputs this mapping as a Cytoscape graph. <br>" +
//				" Gene sets can either be selected or computed from a Cytoscape network (as subgraphs) <br>" + 
//				" or compiled from sources other than Cytoscape (e.g. a list of genes that are <br>" + 
//				"significantly upregulated in a microarray experiment). <br>" + 
//				" The main advantage of BiNGO over other GO tools is the fact that it can be used <br>" +
//				"directly and interactively on molecular interaction graphs. <br>" +
//				"Another plus is that BiNGO takes full advantage of Cytoscape's versatile <br>" +
//				"visualization environment. This allows you to produce customized high-quality figures. <br>" +
//				"</html>");
		return new String (
				"<html> determine which Gene Ontology (GO) categories are statistically overrepresented <br>" + 
				        " in a set of genes or a subgraph of a biological network."
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
