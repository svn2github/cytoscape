/*
	
	StatisticsPlugin for Cytoscape (http://www.cytoscape.org/) 
	Copyright (C) 2007 Pekka Salmela

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	
	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
	
 */

package statisticsPlugin;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CyNetworkView;

/**
 * The main class of this plugin. 
 * It is used to create a menu entry for launhing the plugin and 
 * creating a thread for running the statistics calculation.
 * @author Pekka Salmela
 *
 */
public class StatisticsPlugin extends CytoscapePlugin{
	
	/**
	 * Task monitor used to monitor the progress
	 * of the calculation.
	 */
	private TaskMonitor taskMonitor;
	
	/**
	 * Instance of the class used to do the 
	 * actual calculation.
	 */
	private StatisticsManager algObj;
	
	/**
	 * Network view the statistics are calculated for.
	 */
	private CyNetworkView view;
	
	/**
	 * String representation of the result of the
	 * calculation.
	 */
	private String result;
	
	/**
	 * Class constructor.
	 */
	public StatisticsPlugin() {
		//create a new action to respond to menu activation
        StatisticsSelectionAction action = new StatisticsSelectionAction();
        //set the preferred menu
        action.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);
	}
	 
    /**
     * This class gets attached to the menu item.
     */
    public class StatisticsSelectionAction extends CytoscapeAction implements Task {

		private static final long serialVersionUID = 6717374006120504728L;
		
		/**
         * The constructor sets the text that should appear on the menu item.
         */
        public StatisticsSelectionAction() {super("Graph Statistics");}
        
        /**
         * This method is called when the user selects the menu item.
         */
		public void actionPerformed(ActionEvent ae) {
        	//get the network object; this contains the graph
            CyNetwork network = Cytoscape.getCurrentNetwork();
            //get the network view object
            view = Cytoscape.getCurrentNetworkView();
            //can't continue if either of these is null
            if (network == null || view == null) {return;} 
            System.out.println("Start StatisticsPlugin");
            JTaskConfig taskConfig = getNewDefaultTaskConfig();
    		TaskManager.executeTask(this, taskConfig);
    		JOptionPane.showMessageDialog(Cytoscape.getDesktop(), result, "Graph Statistics", JOptionPane.INFORMATION_MESSAGE);
    		System.out.println("Stop StatisticsPlugin");
        }
		
		/**
		 * Runs the calculation thread.
		 */
		public void run(){
    		algObj = new StatisticsManager(view);
    		algObj.setTaskMonitor(taskMonitor);
    		result = algObj.reportStatistics();
    		algObj = null;
    		System.gc();    		
		}
		
		/**
		 * Sets the task monitor.
		 */
    	public void setTaskMonitor(TaskMonitor _monitor) { taskMonitor = _monitor; }
    	
    	/**
    	 * Gets the title of the action.
    	 */
    	public String getTitle() 
    	{ 
    		return "Performing graph statistics calculation"; 
    	}
    	
    	/**
    	 * Sets the cancel flag to true.
    	 */
    	public void halt() { 
    		algObj.setCancel();
    	}
    	
    	/**
    	 * Creates a task configuration window
    	 * for the action.
    	 * @return New task configuration window.
    	 */
    	private JTaskConfig getNewDefaultTaskConfig()
    	{
    		JTaskConfig result = new JTaskConfig();
    		result.displayCancelButton(true);
    		result.displayCloseButton(true);
    		result.displayStatus(true);
    		result.displayTimeElapsed(true);
    		result.setAutoDispose(false);
    		result.setModal(true);
    		result.setOwner(Cytoscape.getDesktop());

    		return result;
    	}
    }
}