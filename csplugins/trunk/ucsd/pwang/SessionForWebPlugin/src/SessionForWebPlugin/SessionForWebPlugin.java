package SessionForWebPlugin;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.tree.TreeModel;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.plugin.CytoscapePlugin;
import ding.view.DGraphView;

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
				setupDialog();
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

	//
	//  Setup the dialog
	//

	private void setupDialog()
	{
		SessionExporterDialog dialog = new SessionExporterDialog(Cytoscape.getDesktop());
		dialog.addActionListener(new ExportListener(dialog));
		dialog.setUpdateNetworksTableListener(new UpdateListener(dialog));
		dialog.setVisible(true);
	}

	private class UpdateListener implements ActionListener
	{
		private SessionExporterDialog dialog;

		public UpdateListener(SessionExporterDialog dialog)
		{
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e)
		{
			Map<String,String> networkTitleToIDMap = networkTitleToIDMap();
			TreeModel model = Cytoscape.getDesktop().getNetworkPanel().getTreeTable().getTree().getModel();
			SessionExporterSettings settings = dialog.getSettings();
			System.gc();
			search(networkTitleToIDMap, settings, model, model.getRoot());
		}

		private void search(Map<String,String> networkTitleToIDMap, SessionExporterSettings settings, TreeModel model, Object network)
		{
			if (network != model.getRoot())
			{
				String networkTitle = network.toString();
				String networkID = networkTitleToIDMap.get(networkTitle);
				DGraphView networkView = (DGraphView) (Cytoscape.getNetworkView(networkID));
				Dimension imageSize = GraphViewToImage.getImageDimensions(networkView, settings);
				if (imageSize == null)
					dialog.addNetwork(networkID, networkTitle, 0, 0, false);
				else
					dialog.addNetwork(networkID, networkTitle, (int) imageSize.getWidth(), (int) imageSize.getHeight(), true);
			}
			for (int i = 0; i < model.getChildCount(network); i++)
				search(networkTitleToIDMap, settings, model, model.getChild(network, i));
		}
	}

	private class ExportListener implements ActionListener
	{
		private SessionExporterDialog dialog;

		public ExportListener(SessionExporterDialog dialog)
		{
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e)
		{
			SessionExporterSettings settings = dialog.getSettings();
			Bundle bundle = BundleChooser.chooseBundle(settings);
			if (bundle != null)
			{
				dialog.setVisible(false);
				HTMLSessionExporter exporter = new HTMLSessionExporter();
				exporter.export(settings, bundle);
			}
		}
	}

	public static Map<String,String> networkTitleToIDMap()
	{
		Map<String,String> map = new HashMap<String,String>();
		Iterator iterator = Cytoscape.getNetworkSet().iterator();
		while (iterator.hasNext())
		{
			CyNetwork network = (CyNetwork) iterator.next();
			String networkID = network.getIdentifier();
			String networkTitle = network.getTitle();
			map.put(networkTitle, networkID);
		}
		return map;
	}
}
