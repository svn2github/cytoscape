/**
 * 
 */
package cytoscape.actions;

import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginInfo;

import cytoscape.Cytoscape;
import cytoscape.dialogs.PluginInstallDialog;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import cytoscape.util.CytoscapeAction;


/**
 * @author skillcoy
 *
 */
public class PluginManagerAction extends CytoscapeAction
	{

	/**
	 * 
	 */
	public PluginManagerAction()
		{
		super("Manage Plugins");
		setPreferredMenu("Plugins");
		}


	/* (non-Javadoc)
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
		{
		/*
		 * This will actually pop up the "currently installed" dialog box which will 
		 * have a button to "install plugins" poping up the PluginInstallDialog
		 */
		
		PluginManager Mgr = new PluginManager();
		Map<String, List<PluginInfo>> Plugins = Mgr.getPluginsByCategory();
		Iterator<String> catI = Plugins.keySet().iterator();
		
		// obviously this is not all that will be done
		PluginInstallDialog pid = new PluginInstallDialog();
		int index = 0;
		while(catI.hasNext())
			{
			String CategoryName = catI.next();
			pid.addCategory(CategoryName, Plugins.get(CategoryName), index);
			if (index == 0) index++; //something about how trees work index needs to be only 0 or 1??
			}
	
		pid.pack();
		pid.setVisible(true);
		}

	}
