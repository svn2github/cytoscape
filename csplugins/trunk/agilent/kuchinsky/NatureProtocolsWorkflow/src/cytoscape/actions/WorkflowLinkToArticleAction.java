package cytoscape.actions;

import java.awt.event.ActionEvent;

import cytoscape.util.OpenBrowser;

public class WorkflowLinkToArticleAction extends WorkflowPanelAction {
	
	private static final String URL = "http://www.nature.com/nprot/journal/v2/n10/abs/nprot.2007.324.html";

	public WorkflowLinkToArticleAction(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public void actionPerformed(ActionEvent e)
	{
		OpenBrowser.openURL(URL);
	
	}


}
