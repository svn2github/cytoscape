package org.idekerlab.PanGIAPlugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.help.HelpSet;
import java.net.URL;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.idekerlab.PanGIAPlugin.ui.SearchPropertyPanel;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyHelpBroker;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;

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


	public PanGIAPlugin() {
		this.vsObserver = new VisualStyleObserver();
		addHelp();
		final JMenuItem menuItem = new JMenuItem(PLUGIN_NAME);
		menuItem.addActionListener(new PluginAction());
		Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu(
				"Plugins.Module Finders...").add(menuItem);
		
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
			final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
			int index = cytoPanel.indexOfComponent(scrollPane);
			if (index < 0) {
				final SearchPropertyPanel searchPanel = new SearchPropertyPanel();
				scrollPane = new JScrollPane(searchPanel);
				searchPanel.setContainer(scrollPane);
				searchPanel.updateAttributeLists();
				searchPanel.setVisible(true);
				cytoPanel.add(PLUGIN_NAME, scrollPane);
				index = cytoPanel.indexOfComponent(scrollPane);
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}
}
