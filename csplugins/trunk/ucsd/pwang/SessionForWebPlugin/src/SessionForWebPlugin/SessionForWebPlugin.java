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
import cytoscape.data.CyAttributes;


public class SessionForWebPlugin extends CytoscapePlugin
{
	public SessionForWebPlugin()
	{
		JMenuItem webMenuItem = new JMenuItem("Session for Web...");
		JMenuItem webMenuItem2 = new JMenuItem("Session for CellCircuits web site...");
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
		
		ActionListener webActionListener2 = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// check that we have at least 1 network loaded
				if (Cytoscape.getNetworkSet().size() == 0)
				{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"There needs to be at least one network loaded\n" +
						"before the session can be exported.", "Session for CellCircuits web site",
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				setupDialog2();
			}
		};

		webMenuItem.addActionListener(webActionListener);
		webMenuItem2.addActionListener(webActionListener2);
		
		getExportMenu().add(webMenuItem);
		getExportMenu().add(webMenuItem2);
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

	
	private void setupDialog2()
	{
		SessionExporterDialog2 dialog2 = new SessionExporterDialog2(Cytoscape.getDesktop());
		dialog2.addActionListener(new ExportListener2(dialog2));
		dialog2.setUpdateNetworksTableListener(new UpdateListener2(dialog2));
		dialog2.setVisible(true);
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
			Map<String,String> networkTitleToSpeciesMap = networkTitleToSpeciesMap();
			TreeModel model = Cytoscape.getDesktop().getNetworkPanel().getTreeTable().getTree().getModel();
			SessionExporterSettings settings = dialog.getSettings();
			System.gc();
			search(networkTitleToIDMap, networkTitleToSpeciesMap, settings, model, model.getRoot());
		}

		private void search(Map<String,String> networkTitleToIDMap, Map<String,String> networkTitleToSpeciesMap, SessionExporterSettings settings, TreeModel model, Object network)
		{
			if (network != model.getRoot())
			{
				String networkTitle = network.toString();
				String networkID = networkTitleToIDMap.get(networkTitle);
				String species = networkTitleToSpeciesMap.get(networkTitle);
				DGraphView networkView = (DGraphView) (Cytoscape.getNetworkView(networkID));
				Dimension imageSize = GraphViewToImage.getImageDimensions(networkView, settings);
				if (imageSize == null)
					dialog.addNetwork(networkID, networkTitle, 0, 0, false);
				else
					dialog.addNetwork(networkID, networkTitle,  (int) imageSize.getWidth(), (int) imageSize.getHeight(), true);
			}
			for (int i = 0; i < model.getChildCount(network); i++)
				search(networkTitleToIDMap, networkTitleToSpeciesMap, settings, model, model.getChild(network, i));
		}
	}

	private class UpdateListener2 implements ActionListener
	{
		private SessionExporterDialog2 dialog;

		public UpdateListener2(SessionExporterDialog2 dialog)
		{
			this.dialog = dialog;
		}

		public void actionPerformed(ActionEvent e)
		{
			Map<String,String> networkTitleToIDMap = networkTitleToIDMap();
			Map<String,String> networkTitleToSpeciesMap = networkTitleToSpeciesMap();
			TreeModel model = Cytoscape.getDesktop().getNetworkPanel().getTreeTable().getTree().getModel();
			SessionExporterSettings settings = dialog.getSettings();
			System.gc();
			search(networkTitleToIDMap, networkTitleToSpeciesMap, settings, model, model.getRoot());
		}

		private void search(Map<String,String> networkTitleToIDMap, Map<String,String> networkTitleToSpeciesMap, SessionExporterSettings settings, TreeModel model, Object network)
		{
			if (network != model.getRoot())
			{
				String networkTitle = network.toString();
				String networkID = networkTitleToIDMap.get(networkTitle);
				String species = networkTitleToSpeciesMap.get(networkTitle);
				DGraphView networkView = (DGraphView) (Cytoscape.getNetworkView(networkID));
				Dimension imageSize = GraphViewToImage.getImageDimensions(networkView, settings);
				if (imageSize == null)
					dialog.addNetwork(networkID, networkTitle, 0, 0, false);
				else
					dialog.addNetwork(networkID, networkTitle,  (int) imageSize.getWidth(), (int) imageSize.getHeight(), true);
			}
			for (int i = 0; i < model.getChildCount(network); i++)
				search(networkTitleToIDMap, networkTitleToSpeciesMap, settings, model, model.getChild(network, i));
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

	private class ExportListener2 implements ActionListener
	{
		private SessionExporterDialog2 dialog;

		public ExportListener2(SessionExporterDialog2 dialog)
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
	
	public static Map<String,String> networkTitleToSpeciesMap()
	{
		Map<String,String> map = new HashMap<String,String>();
		CyAttributes cyNetworkAttrs = Cytoscape.getNetworkAttributes();
		Iterator iterator = Cytoscape.getNetworkSet().iterator();
		while (iterator.hasNext())
		{
			CyNetwork network = (CyNetwork) iterator.next();
			String networkID = network.getIdentifier();
			String networkTitle = network.getTitle();
			String species = cyNetworkAttrs.getStringAttribute(networkID, "species");
			if (species == null) {
				species = "Saccharomyces cerevisiae";
			}
			map.put(networkTitle, species);
		}
		return map;
	}
	
}
