/**
 * 
 */
package cytoscape.actions;

import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginInfo;

import java.awt.event.ActionEvent;
//import java.util.List;
//import java.util.ArrayList;
import java.util.Iterator;

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
		super("Manage");
		setPreferredMenu("Plugins");
		}


	/* (non-Javadoc)
	 * @see cytoscape.util.CytoscapeAction#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e)
		{
		// obviously this is not all that will be done
		PluginManager Mgr = new PluginManager();
		Iterator<PluginInfo> infoI = Mgr.inquire().iterator();
		System.out.println("PLUGINS:");
		while (infoI.hasNext())
			{
			PluginInfo Info = infoI.next();
			System.out.println("  " + Info.getName() + " : " + Info.getUrl());
			
			System.out.println("Installing: " + Mgr.install(Info));
			}
		}

	}
