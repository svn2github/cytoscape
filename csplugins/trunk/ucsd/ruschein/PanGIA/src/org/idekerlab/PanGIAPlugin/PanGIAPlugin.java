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
import java.util.*;
import java.util.Map.Entry;

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
 * @author kono, ruschein, ghannum
 *
 */
public class PanGIAPlugin extends CytoscapePlugin {

	// Main GUI Panel for this plugin.  Should be a singleton.
	private JScrollPane scrollPane;
	
	private final VisualStyleObserver vsObserver;
	
	private static final String PLUGIN_NAME = "PanGIA";
	public static final String VERSION = "1.1";
	public static final Map<String,PanGIAOutput> output = new HashMap<String,PanGIAOutput>();

	public PanGIAPlugin() {
		this.vsObserver = new VisualStyleObserver();
		addHelp();
		final JMenuItem menuItem = new JMenuItem(PLUGIN_NAME);
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Plugins.Module Finders...").add(menuItem);
		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_CREATED,new PanGIANetworkListener());
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_DESTROYED,new PanGIANetworkListener());
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.NETWORK_TITLE_MODIFIED,new PanGIANetworkListener());
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
			
			bw.write(output.size()+"\n");
			
			for (Entry<String,PanGIAOutput> e : output.entrySet())
			{
				bw.write(e.getValue().getOverviewNetwork().getTitle()+"\n");
				bw.write(e.getValue().getOrigPhysNetwork().getTitle()+"\n");
				bw.write(e.getValue().getOrigGenNetwork().getTitle()+"\n");
				bw.write(e.getValue().getPhysAttrName()+"\n");
				bw.write(e.getValue().getGenAttrName()+"\n");
				bw.write(e.getValue().isSigned()+"\n");
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
			
			String rCount = in.readLine();
			
			int numResults;
			if (rCount.equals("true")) numResults = 1;         //For compatibility with sessions from version 1.0
			else if (rCount.equals("false")) numResults = 0;
			else numResults = Integer.valueOf(rCount);
			
			for (int i=0;i<numResults;i++)
			{
				String overviewNetTitle = in.readLine();
				System.out.println("Overview net title "+i+": "+overviewNetTitle);
				
				CyNetwork overviewNet = getNetworkFromTitle(overviewNetTitle);
				CyNetwork physNet = getNetworkFromTitle(in.readLine());
				CyNetwork genNet = getNetworkFromTitle(in.readLine());
				String physAttr = in.readLine();
				String genAttr = in.readLine();
				boolean isSigned = Boolean.valueOf(in.readLine());
				output.put(overviewNet.getIdentifier(),new PanGIAOutput(overviewNet, physNet, genNet, physAttr, genAttr, isSigned));
			}		
			
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

