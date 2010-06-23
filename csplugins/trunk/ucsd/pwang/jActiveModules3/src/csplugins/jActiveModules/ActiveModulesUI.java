package csplugins.jActiveModules;


import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.attr.MultiHashMapDefinitionListener;
import cytoscape.logger.CyLogger;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.view.CyHelpBroker;


/**
 * UI for Active Modules. Manages the various menu items
 */
public class ActiveModulesUI extends CytoscapePlugin {
	protected ActivePaths activePaths;
	protected ActivePathFinderParameters apfParams;
	protected ThreadExceptionHandler xHandler;

	private ActivePathsParameterPanel mainPanel;

	public static String JACTIVEMODULES_TOP_N_MODULE = "jactivemodules_top_n_modules";
	public static String JACTIVEMODULES_TOP_N_MODULE_DEFAULT = "5";
	
	public ActiveModulesUI() {
		final JMenuItem menuItem = new JMenuItem("jActiveModules...");
		menuItem.addActionListener(new SetParametersAction());

		Cytoscape.getDesktop().getCyMenus().getMenuBar().
			getMenu("Plugins.Module Finders...").add(menuItem);

		/* initialize variables */
		apfParams = new ActivePathFinderParameters(CytoscapeInit
				.getProperties());
		apfParams.reloadExpressionAttributes();
		AttrChangeListener acl = new AttrChangeListener();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
				Cytoscape.ATTRIBUTES_CHANGED, acl);
		Cytoscape.getNodeAttributes().getMultiHashMapDefinition()
				.addDataDefinitionListener(acl);
		xHandler = new ThreadExceptionHandler();
		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);

		addHelp();
	}

	/**
	 *  Hook plugin help into the Cytoscape main help system:
	 */
	private void addHelp() {
		final String HELP_SET_NAME = "/help/jhelpset";
		final ClassLoader classLoader = ActiveModulesUI.class.getClassLoader();
		URL helpSetURL;
		final CyLogger logger = CyLogger.getLogger(ActiveModulesUI.class);
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			CyHelpBroker.getHelpSet().add(newHelpSet);
		} catch (final Exception e) {
			logger.warn("Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName() == Cytoscape.CYTOSCAPE_INITIALIZED
				&& apfParams.getRun()) {
			if (apfParams.getRandomizeExpression()) {
				startRandomizeAndRun(mainPanel.getTargetNetwork());
			} else {
				activePaths = new ActivePaths(mainPanel.getTargetNetwork(),
						apfParams, this);
				Thread t = new Thread(activePaths);
				t.start();
			}
		}
	}

	/**
	 * Action to allow the user to change the current options for running
	 * jActiveModules, wiht a gui interface
	 */
	protected class SetParametersAction extends AbstractAction {
		private static final long serialVersionUID = -7836213413255212288L;

		public SetParametersAction() {
			super("jActiveModules");
		}

		public void actionPerformed(ActionEvent e) {
			if (apfParams.getPossibleExpressionAttributes().size() == 0) {
				JOptionPane
						.showMessageDialog(
								Cytoscape.getDesktop(),
								"JActiveModules cannot start because it cannot find\n"
										+ "any p-value attributes! JActiveModules requires at\n"
										+ "least one node attribute with values ranging between\n"
										+ "0 and 1 of type 'float' (i.e. decimal or real number).\n"
										+ "Please load an appropriate attribute and try again.",
								"jActiveModules", JOptionPane.ERROR_MESSAGE);
				return;
			}
			final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
					SwingConstants.WEST);
			if (mainPanel == null) {
				mainPanel = new ActivePathsParameterPanel(apfParams,
						ActiveModulesUI.this);
			}
			int index = cytoPanel.indexOfComponent(mainPanel);
			if (index < 0) {
				cytoPanel.add("jActiveModules", mainPanel);
				index = cytoPanel.indexOfComponent(mainPanel);
			}
			cytoPanel.setSelectedIndex(index);
			cytoPanel.setState(CytoPanelState.DOCK);
		}
	}

	public void startFindActivePaths(CyNetwork network) {
		try {
			activePaths = new ActivePaths(network, apfParams, this);
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Error running jActiveModules (1)!  " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Thread t = new Thread(activePaths);
		t.setUncaughtExceptionHandler(xHandler);
		t.start();
	}

	/**
	 * This action will run activePaths with the current parameters
	 */
	protected class FindActivePathsAction extends AbstractAction {

		FindActivePathsAction() {
			super("Active Modules: Find Modules");
		}

		public void actionPerformed(ActionEvent ae) {
			startFindActivePaths(mainPanel.getTargetNetwork());
		}
	}

	protected class ThreadExceptionHandler implements
			Thread.UncaughtExceptionHandler {
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace(System.err);
			CyLogger logger = CyLogger.getLogger(ActiveModulesUI.class);
			logger.warn("Non-fatal exception in Thread " + t.getName(), e);
			logger.warn("The previous exception was non-fatal - Don't panic!");
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Error running jActiveModules (2)!  " + e.getMessage(),
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * This action will generate a score for the currently selected nodes in the
	 * view
	 */
	protected class ScoreSubComponentAction extends AbstractAction {

		ScoreSubComponentAction() {
			super("Active Modules: Score Selected Nodes");
		}

		public void actionPerformed(ActionEvent e) {
			activePaths = new ActivePaths(mainPanel.getTargetNetwork(),
					apfParams, ActiveModulesUI.this);
			activePaths.scoreActivePath();
		}
	}

	public void startRandomizeAndRun(CyNetwork network) {
		activePaths = new ActivePaths(network, apfParams, ActiveModulesUI.this);
		Thread t = new ScoreDistributionThread(network, activePaths, apfParams);
		t.setUncaughtExceptionHandler(xHandler);
		t.start();
	}

	public class RandomizeAndRunAction extends AbstractAction {

		public RandomizeAndRunAction() {
			super("Active Modules: Score Distribution");
		}

		public void actionPerformed(ActionEvent e) {
			startRandomizeAndRun(mainPanel.getTargetNetwork());
		}
	}

	/**
	 * This is used to update the expression attributes in the params object so
	 * that they match those that exist in CyAttributes.
	 */
	protected class AttrChangeListener implements PropertyChangeListener,
			MultiHashMapDefinitionListener {

		public void propertyChange(PropertyChangeEvent e) {
			if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED))
				apfParams.reloadExpressionAttributes();
		}

		/**
		 * There is no point in listening to attributeDefined events because
		 * this only defines the attr and when this is fired, no attr values
		 * actually exist.
		 */
		public void attributeDefined(String attributeName) {
		}

		public void attributeUndefined(String attributeName) {
			apfParams.reloadExpressionAttributes();
		}
	}
}
