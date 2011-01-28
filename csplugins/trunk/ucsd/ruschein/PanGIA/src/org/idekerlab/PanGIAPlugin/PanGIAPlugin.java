package org.idekerlab.PanGIAPlugin;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.help.HelpSet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.idekerlab.PanGIAPlugin.ui.SearchPropertyPanel;
import org.idekerlab.PanGIAPlugin.utilities.files.FileUtil;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyHelpBroker;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.cytopanels.BiModalJSplitPane;

/**
 * PanGIA Plugin main class.
 * 
 * <p>
 * 
 * 
 * @author kono, ruschein
 *
 */
public class PanGIAPlugin extends CytoscapePlugin {

	// Main GUI Panel for this plugin.  Should be a singleton.
	private JScrollPane scrollPane;
	
	private final VisualStyleObserver vsObserver;
	
	private static final String PLUGIN_NAME = "PanGIA";
	public static final String VERSION = "1.01";
	public static final PanGIAOutput output = new PanGIAOutput();

	public PanGIAPlugin() {
		this.vsObserver = new VisualStyleObserver();
		addHelp();
		final JMenuItem menuItem = new JMenuItem(PLUGIN_NAME);
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins.Module Finders...").add(menuItem);
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED,new PanGIANetworkListener());
	}

	/**
	 *  Hook plugin help into the Cytoscape main help system:
	 */
	private void addHelp() {
		final String HELP_SET_NAME = "/help/jhelpset";
		final ClassLoader classLoader = PanGIAPlugin.class.getClassLoader();
		URL helpSetURL;
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			CyHelpBroker.getHelpSet().add(newHelpSet);
		} catch (final Exception e) {
			System.err.println("PanGIA: Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}

	class PluginAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			final CytoPanelImp cytoPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			int index = cytoPanel.indexOfComponent(scrollPane);
			if (index < 0) {
				final SearchPropertyPanel searchPanel = new SearchPropertyPanel();
				scrollPane = new JScrollPane(searchPanel);
				searchPanel.setContainer(scrollPane);
				searchPanel.updateAttributeLists();
				searchPanel.setVisible(true);
				scrollPane.setMinimumSize(new Dimension(400,400));
				cytoPanel.add(PLUGIN_NAME, scrollPane);
				index = cytoPanel.indexOfComponent(scrollPane);
				
				BiModalJSplitPane bmj = (BiModalJSplitPane)cytoPanel.getParent();
				bmj.setDividerLocation(400);				
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}
	
	public void saveSessionStateFiles(List<File> pFileList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("./PanGIA.session.tmp"));
			
			bw.write(output.isAvailable()+"\n");
			
			if (output.isAvailable())
			{
				bw.write(output.getOverviewNetwork().getTitle()+"\n");
				bw.write(output.getOrigPhysNetwork().getTitle()+"\n");
				bw.write(output.getOrigGenNetwork().getTitle()+"\n");
				bw.write(output.getPhysAttrName()+"\n");
				bw.write(output.getGenAttrName()+"\n");
			}
			
			bw.close();
			
			pFileList.add(new File("./PanGIA.session.tmp"));
		}catch (Exception e)
		{
			System.out.println("Error saving PanGIA session file.");
			e.printStackTrace();
		}
	}
	
	public void restoreSessionState(List<File> pStateFileList)
	{
		try
		{

			if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
				//No previous state to restore
				return;
			}
			
			File prop_file = pStateFileList.get(0);

			BufferedReader in = new BufferedReader(new FileReader(prop_file));
			boolean isAvailable = Boolean.valueOf(in.readLine());
			
			if (isAvailable)
			{
				String overviewNetID = in.readLine();
				System.out.println("Overview net title: "+overviewNetID);
				
				CyNetwork overviewNet = getNetworkFromTitle(overviewNetID);
				CyNetwork physNet = getNetworkFromTitle(in.readLine());
				CyNetwork genNet = getNetworkFromTitle(in.readLine());
				String physAttr = in.readLine();
				String genAttr = in.readLine();
				output.initialize(overviewNet, physNet, genNet, physAttr, genAttr);
			}else output.reset();
			
			
			in.close();
			
		}catch (Exception e)
		{
			System.out.println("Error loading PanGIA session file.");
			e.printStackTrace();
		}
	}
	
	private static CyNetwork getNetworkFromTitle(String title)
	{
		for (CyNetwork net : Cytoscape.getNetworkSet())
			if (net.getTitle().equals(title)) return net;
		
		return null;
	}
}

