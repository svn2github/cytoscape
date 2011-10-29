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

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.task.AbstractNodeViewTaskFactory;
import org.cytoscape.task.NodeViewTaskFactory;
import org.cytoscape.view.model.View;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
//import cytoscape.view.CyHelpBroker;
//import cytoscape.view.cytopanels.CytoPanel;
//import cytoscape.view.cytopanels.CytoPanelImp;
//import cytoscape.view.cytopanels.CytoPanelState;
//import cytoscape.view.cytopanels.BiModalJSplitPane;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.view.model.events.NetworkViewAddedEvent;
import org.cytoscape.view.model.events.NetworkViewAddedListener;
import org.cytoscape.view.model.events.NetworkViewDestroyedEvent;
import org.cytoscape.view.model.events.NetworkViewDestroyedListener;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.model.events.NetworkDestroyedListener;
import org.cytoscape.model.events.NetworkDestroyedEvent;
import org.cytoscape.view.model.CyNetworkView;


/**
 * PanGIA Plugin main class.
 * 
 * <p>
 * 
 * 
 * @author kono, ruschein, ghannum
 *
 */
public class PanGIAPlugin extends AbstractCyAction implements NetworkViewAddedListener, NetworkDestroyedListener{

	// Main GUI Panel for this plugin.  Should be a singleton.
	private JScrollPane scrollPane;
	
	private static VisualStyleObserver vsObserver;
	
	private static final String PLUGIN_NAME = "PanGIA";
	public static final String VERSION = "1.1";
	public static final Map<String,PanGIAOutput> output = new HashMap<String,PanGIAOutput>();
	private final CytoPanel cytoPanelWest;

	private SearchPropertyPanel searchPanel;
	
	public PanGIAPlugin(SearchPropertyPanel searchPanel) {
		super(PLUGIN_NAME, ServicesUtil.cyApplicationManagerServiceRef);
		this.setPreferredMenu("Plugins");
		vsObserver = new VisualStyleObserver();
		this.searchPanel = searchPanel;
		cytoPanelWest = ServicesUtil.cySwingApplicationServiceRef.getCytoPanel(CytoPanelName.WEST);

		
//		// Node right-click menu
//		Dictionary<String, String> dict = new Hashtable<String, String>();
//		dict.put("preferredMenu", "PanGIA");
//		
//		NodeViewTaskFactory nvtf = new PanGIANodeViewTaskFactory();
//		ServicesUtil.cyServiceRegistrarServiceRef.registerService(nvtf, NodeViewTaskFactory.class, dict);
//		
//		// Edge right-click menu
		

		addHelp();		
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void actionPerformed(ActionEvent e) {
		// If the state of the cytoPanelEast is HIDE, show it
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}	

		// Select the jActiveModules panel
		int index = cytoPanelWest.indexOfComponent(searchPanel);
		if (index == -1) {
			return;
		}
		
		cytoPanelWest.setSelectedIndex(index);		
		
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
			//CyHelpBroker.getHelpSet().add(newHelpSet);
		} catch (final Exception e) {
			System.err.println("PanGIA: Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}

//	class PluginAction implements ActionListener {
//		public void actionPerformed(ActionEvent e) {
//			final CytoPanelImp cytoPanel = (CytoPanelImp)Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
//			int index = cytoPanel.indexOfComponent(scrollPane);
//			if (index < 0) {
//				final SearchPropertyPanel searchPanel = new SearchPropertyPanel();
//				scrollPane = new JScrollPane(searchPanel);
//				searchPanel.setContainer(scrollPane);
//				searchPanel.updateAttributeLists();
//				searchPanel.setVisible(true);
//				scrollPane.setMinimumSize(new Dimension(400,400));
//				cytoPanel.add(PLUGIN_NAME, scrollPane);
//				index = cytoPanel.indexOfComponent(scrollPane);
//				
//				BiModalJSplitPane bmj = (BiModalJSplitPane)cytoPanel.getParent();
//				bmj.setDividerLocation(400);				
//			}
//			cytoPanel.setSelectedIndex(index);
//			cytoPanel.setState(CytoPanelState.DOCK);
//		}
//	}
	
	public static void setModuleLabels(String nattr)
	{
		vsObserver.setModuleLabels(nattr);
	}
	
	public void saveSessionStateFiles(List<File> pFileList)
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("./PanGIA.session.tmp"));
			
			bw.write(output.size()+"\n");
			
			for (Entry<String,PanGIAOutput> e : output.entrySet())
			{
				bw.write(e.getValue().getOverviewNetwork().getCyRow().get("name", String.class)+"\n");
				bw.write(e.getValue().getOrigPhysNetwork().getCyRow().get("name", String.class)+"\n");
				bw.write(e.getValue().getOrigGenNetwork().getCyRow().get("name", String.class)+"\n");
				bw.write(e.getValue().getNodeAttrName()+"\n");
				bw.write(e.getValue().getPhysEdgeAttrName()+"\n");
				bw.write(e.getValue().getGenEdgeAttrName()+"\n");
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
				String nodeAttr = in.readLine();
				String physEdgeAttr = in.readLine();
				String genEdgeAttr = in.readLine();
				boolean isSigned = Boolean.valueOf(in.readLine());
				//output.put(overviewNet.getIdentifier(),new PanGIAOutput(overviewNet, physNet, genNet, nodeAttr, physEdgeAttr, genEdgeAttr, isSigned));
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
		for (CyNetwork net : ServicesUtil.cyNetworkManagerServiceRef.getNetworkSet())
		{
			String title1 = net.getCyRow().get("name", String.class);
			if (title1.equals(title)) return net;
		}
		return null;
	}
	
	//
	
	public void handleEvent(NetworkViewAddedEvent e){

		
	}


	public void handleEvent(NetworkDestroyedEvent e){
//      {
//    	CyNetwork net = Cytoscape.getNetwork((String)event.getNewValue());
//    	
//    	if (PanGIAPlugin.output.containsKey(net.getTitle())) System.out.println("Removing PanGIA result: "+net.getTitle());
//    	PanGIAPlugin.output.remove(net.getIdentifier());
//    }
//    
//    if (Cytoscape.NETWORK_TITLE_MODIFIED.equals(event.getPropertyName()))
//    {
//    	CyNetworkTitleChange ctc = (CyNetworkTitleChange)event.getNewValue();
//    	String newName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
//    	
//    	for (CyNetwork net : Cytoscape.getNetworkSet())
//    		if (net.getTitle().equals(newName) && !net.getIdentifier().equals(ctc.getNetworkIdentifier()) && (PanGIAPlugin.output.containsKey(net.getIdentifier()) || PanGIAPlugin.output.containsKey(ctc.getNetworkIdentifier())))
//    			System.out.println("PanGIA WARNING: Two overview networks have the same name!");
//    		
//    		
//    	//CyNetworkTitleChange ctc = (CyNetworkTitleChange)event.getNewValue();
//    	//ctc.getNetworkTitle()
//    	
//    	//String oldName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
//    	//String newName = Cytoscape.getNetwork(ctc.getNetworkIdentifier()).getTitle();
//    	
//    	/*
//    	String oldName = ((CyNetworkTitleChange)event.getOldValue()).getNetworkTitle();
//    	String newName = ((CyNetworkTitleChange)event.getNewValue()).getNetworkTitle();
//    	System.out.println("Changing: "+oldName+" to "+newName);
//    	
//    	*/

	}

}

