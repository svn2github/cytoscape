package SessionForWebPlugin;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.plugin.PluginInfo;

public class SessionForWebPlugin extends CytoscapePlugin
{
	public SessionForWebPlugin()
	{
		JMenuItem webMenuItem = new JMenuItem("Session for Web...");
		ActionListener webActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// check that we have at least 1 network loaded
				if (Cytoscape.getNetworkSet().size() == 0)
				{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"There needs to be at least one network loaded\n" +
						"before the session can be exported.", "Session for Web",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				SessionExporterSettings settings = new SessionExporterSettings();
				SessionExporterDialog dialog = new SessionExporterDialog(settings);
				dialog.setVisible(true);
			}
		};
		webMenuItem.addActionListener(webActionListener);
		getExportMenu().add(webMenuItem);
	}

	private JMenu getExportMenu()
	{
		JMenu fileMenu = Cytoscape.getDesktop().getCyMenus().getFileMenu();
		for (int i = 0; i < fileMenu.getItemCount(); i++)
		{
			JMenuItem menuItem = fileMenu.getItem(i);
			if (menuItem != null && menuItem.getText() != null && menuItem.getText().equals("Export"))
				return (JMenu) menuItem;
		}
		throw new RuntimeException("Could not find \"Export\" submenu in the \"File\" menu");
	}

	public PluginInfo getPluginInfoObject()
	{
		PluginInfo result = new PluginInfo();
		result.setName("Session For Web");
		result.setDescription("Prepares and exports a session for the web");
		result.setPluginVersion(2.1);
		result.setCytoscapeVersion("2.5");
		result.setCategory(PluginInfo.Category.NETWORK_ATTRIBUTE_IO);
		result.addAuthor("Samad Lotia", "UCSD");
		return result;
	}
}
