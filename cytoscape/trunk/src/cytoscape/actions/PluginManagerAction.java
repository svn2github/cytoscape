/**
 * 
 */
package cytoscape.actions;

import cytoscape.plugin.PluginManager;
import cytoscape.plugin.PluginInfo;
import cytoscape.util.CytoscapeAction;
import cytoscape.Cytoscape;
import cytoscape.dialogs.PluginInstallDialog;

import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;


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
		System.out.println("Default URL: " + Mgr.getDefaultUrl());
		Map<String, List<PluginInfo>> Plugins = Mgr.getPluginsByCategory();
		Iterator<String> catI = Plugins.keySet().iterator();
		System.out.println("Plugin categories: " + Plugins.size());

		if (Plugins.size() > 0)
			{
			// obviously this is not all that will be done
			PluginInstallDialog pid = new PluginInstallDialog();
			int index = 0;
			while(catI.hasNext())
				{
				String CategoryName = catI.next();
				System.out.println("Categor: " + CategoryName);
				pid.addCategory(CategoryName, Plugins.get(CategoryName), index);
				if (index == 0) index++; //something about how trees work index needs to be only 0 or 1??
				}
		
			pid.pack();
			pid.setVisible(true);
			}
		else 
			{ // again, this is not how it will stay implemented
			String Msg = "No plugins were found at " + Mgr.getDefaultUrl() + " for Cytoscape version " + cytoscape.CytoscapeVersion.version;
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), Msg, "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}

	}
