package csplugins.jActiveModules;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import csplugins.jActiveModules.data.ActivePathFinderParameters;
import csplugins.jActiveModules.dialogs.ActivePathsParameterPanel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.CytoscapeStartEvent;
import org.cytoscape.application.events.CytoscapeStartListener;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.event.CyEventHelper;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.subnetwork.CyRootNetworkFactory;
import org.cytoscape.property.CyProperty;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.TaskManager;
//import cytoscape.data.attr.MultiHashMapDefinitionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;


/**
 * UI for Active Modules. Manages the various menu items
 */
public class ActiveModulesUI extends AbstractCyAction implements CytoscapeStartListener {
	protected ActivePaths activePaths;
	protected ActivePathFinderParameters apfParams;
	protected ThreadExceptionHandler xHandler;

	private ActivePathsParameterPanel mainPanel;

	public static String JACTIVEMODULES_TOP_N_MODULE = "jactivemodules_top_n_modules";
	public static String JACTIVEMODULES_TOP_N_MODULE_DEFAULT = "5";
	
	private CySwingApplication desktopApp;
	private final CytoPanel cytoPanelWest;
	private static final Logger logger = LoggerFactory.getLogger(ActiveModulesUI.class);
	private CyHelpBrokerImpl cyHelpBroker = new CyHelpBrokerImpl();

	private CyProperty<Properties> cytoscapeProperties;
	private CyNetworkManager cyNetworkManager;
	private CyNetworkViewManager cyNetworkViewManager;
	private VisualMappingManager visualMappingManager;
	private CyRootNetworkFactory cyRootNetworkFactory;
	private CyNetworkViewFactory cyNetworkViewFactory;
	private CyNetworkFactory cyNetworkFactory;
	private CyLayoutAlgorithmManager cyLayoutsService;
	private TaskManager taskManagerService;
	private CyApplicationManager cyApplicationManagerService;
	private CyEventHelper cyEventHelperService;
	
	public ActiveModulesUI(CyApplicationManager appMgr, CySwingApplication desktopApp,CyProperty<Properties> cytoscapeProperties,
			CyNetworkManager cyNetworkManager, CyNetworkViewManager cyNetworkViewManager, VisualMappingManager visualMappingManager,
			CyNetworkFactory cyNetworkFactory, CyRootNetworkFactory cyRootNetworkFactory, 
			CyNetworkViewFactory cyNetworkViewFactory,CyLayoutAlgorithmManager cyLayoutsService, 
			TaskManager taskManagerService, CyEventHelper cyEventHelperService, 
			ActivePathFinderParameters apfParams, ActivePathsParameterPanel mainPanel) {
		
		super("jActiveModules...", appMgr);
		setPreferredMenu("Plugins.jActiveModules");
		//setMenuGravity(2.0f);
		
		this.desktopApp = desktopApp;
		this.cyNetworkManager = cyNetworkManager;
		this.cyNetworkViewManager = cyNetworkViewManager;
		this.visualMappingManager = visualMappingManager;
		this.cyNetworkFactory = cyNetworkFactory;
		this.cyRootNetworkFactory = cyRootNetworkFactory;
		this.cyNetworkViewFactory = cyNetworkViewFactory;
		this.cyLayoutsService = cyLayoutsService;
		this.taskManagerService = taskManagerService;
		this.cyApplicationManagerService = appMgr;
		this.cyEventHelperService = cyEventHelperService;
		
		this.cytoscapeProperties = cytoscapeProperties;
		this.mainPanel = mainPanel;
		this.mainPanel.setPluginMainClass(this);
		
		this.apfParams = apfParams;
		this.apfParams.reloadExpressionAttributes();
		
		AttrChangeListener acl = new AttrChangeListener();
		//Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(
		//		Cytoscape.ATTRIBUTES_CHANGED, acl);
		//Cytoscape.getNodeAttributes().getMultiHashMapDefinition()
		//		.addDataDefinitionListener(acl);
		//xHandler = new ThreadExceptionHandler();
		//Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(this);
		
		cytoPanelWest = desktopApp.getCytoPanel(CytoPanelName.WEST);
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
		int index = cytoPanelWest.indexOfComponent(mainPanel);
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
		final ClassLoader classLoader = ActiveModulesUI.class.getClassLoader();
		URL helpSetURL;
		try {
			helpSetURL = HelpSet.findHelpSet(classLoader, HELP_SET_NAME);
			final HelpSet newHelpSet = new HelpSet(classLoader, helpSetURL);
			cyHelpBroker.getHelpSet().add(newHelpSet);
		} catch (final Exception e) {
			logger.warn("Could not find help set: \"" + HELP_SET_NAME + "!");
		}
	}

	public void handleEvent(CytoscapeStartEvent e){
		//evt.getPropertyName() == Cytoscape.CYTOSCAPE_INITIALIZED
		if ( apfParams.getRun()) {
			if (apfParams.getRandomizeExpression()) {
				startRandomizeAndRun(mainPanel.getTargetNetwork());
			} else {
				activePaths = new ActivePaths(mainPanel.getTargetNetwork(),
						apfParams, this, ActiveModulesUI.this.desktopApp, ActiveModulesUI.this.cytoscapeProperties,
						this.cyNetworkManager, this.cyNetworkViewManager, this.visualMappingManager, 
						this.cyNetworkFactory, this.cyRootNetworkFactory, 
						this.cyNetworkViewFactory, this.cyLayoutsService, this.taskManagerService, 
						this.cyApplicationManagerService, this.cyEventHelperService);
				Thread t = new Thread(activePaths);
				t.start();
			}
		}
	}
	

	/**
	 * Action to allow the user to change the current options for running
	 * jActiveModules, with a gui interface
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
								ActiveModulesUI.this.desktopApp.getJFrame(),
								"JActiveModules cannot start because it cannot find\n"
										+ "any p-value attributes! JActiveModules requires at\n"
										+ "least one node attribute with values ranging between\n"
										+ "0 and 1 of type 'float' (i.e. decimal or real number).\n"
										+ "Please load an appropriate attribute and try again.",
								"jActiveModules", JOptionPane.ERROR_MESSAGE);
				return;
			}

			
//			final CytoPanel cytoPanel = Cytoscape.getDesktop().getCytoPanel(
//					SwingConstants.WEST);
//			
//			if (mainPanel == null) {
//				mainPanel = new ActivePathsParameterPanel(apfParams,
//						ActiveModulesUI.this);
//			}
//			int index = cytoPanel.indexOfComponent(mainPanel);
//			if (index < 0) {
//				cytoPanel.add("jActiveModules", mainPanel);
//				index = cytoPanel.indexOfComponent(mainPanel);
//			}
//			cytoPanel.setSelectedIndex(index);
//			//cytoPanel.setState(CytoPanelState.DOCK);
			
		}
	}

	public void startFindActivePaths(CyNetwork network) {
		try {
			
			activePaths = new ActivePaths(network, apfParams, this, 
					this.desktopApp, this.cytoscapeProperties,
					this.cyNetworkManager, this.cyNetworkViewManager,
					this.visualMappingManager, this.cyNetworkFactory, this.cyRootNetworkFactory, 
					this.cyNetworkViewFactory, this.cyLayoutsService,
					this.taskManagerService, this.cyApplicationManagerService,
					this.cyEventHelperService);
			
		} catch (final Exception e) {
			e.printStackTrace(System.err);
			JOptionPane.showMessageDialog(this.desktopApp.getJFrame(),
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
			logger.warn("Non-fatal exception in Thread " + t.getName(), e);
			logger.warn("The previous exception was non-fatal - Don't panic!");
			JOptionPane.showMessageDialog(ActiveModulesUI.this.desktopApp.getJFrame(),
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
			activePaths = new ActivePaths(mainPanel.getTargetNetwork(), apfParams, ActiveModulesUI.this, 
					ActiveModulesUI.this.desktopApp, ActiveModulesUI.this.cytoscapeProperties,
					ActiveModulesUI.this.cyNetworkManager, ActiveModulesUI.this.cyNetworkViewManager,
					ActiveModulesUI.this.visualMappingManager, ActiveModulesUI.this.cyNetworkFactory, ActiveModulesUI.this.cyRootNetworkFactory, 
					ActiveModulesUI.this.cyNetworkViewFactory, ActiveModulesUI.this.cyLayoutsService,
					ActiveModulesUI.this.taskManagerService, ActiveModulesUI.this.cyApplicationManagerService,
					ActiveModulesUI.this.cyEventHelperService);
			activePaths.scoreActivePath();
		}
	}

	public void startRandomizeAndRun(CyNetwork network) {
		activePaths = new ActivePaths(network, apfParams, ActiveModulesUI.this, ActiveModulesUI.this.desktopApp, ActiveModulesUI.this.cytoscapeProperties,
				this.cyNetworkManager, this.cyNetworkViewManager, ActiveModulesUI.this.visualMappingManager,
				ActiveModulesUI.this.cyNetworkFactory, ActiveModulesUI.this.cyRootNetworkFactory, ActiveModulesUI.this.cyNetworkViewFactory,
				ActiveModulesUI.this.cyLayoutsService, this.taskManagerService, this.cyApplicationManagerService,
				ActiveModulesUI.this.cyEventHelperService);
		Thread t = new ScoreDistributionThread(desktopApp.getJFrame(), network, activePaths, apfParams);
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
	protected class AttrChangeListener implements PropertyChangeListener { //,
			//MultiHashMapDefinitionListener {

		public void propertyChange(PropertyChangeEvent e) {
			//if (e.getPropertyName().equals(Cytoscape.ATTRIBUTES_CHANGED))
			//	apfParams.reloadExpressionAttributes();
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
