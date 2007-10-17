/*
	
	MultiLevelLayoutPlugin for Cytoscape (http://www.cytoscape.org/) 
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

package multilevelLayoutPlugin;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;

import java.util.Hashtable; 

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
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
 * creating a thread for running the layout calculation. 
 * 
 * @author Pekka Salmela
 *
 */
public class MultilevelLayoutPlugin extends CytoscapePlugin {

	/**
	 * Class constructor.
	 */
	public MultilevelLayoutPlugin() {
		//create a new action to respond to menu activation
        MultilevelLayoutSelectionAction action = new MultilevelLayoutSelectionAction();
        //set the preferred menu
        action.setPreferredMenu("Plugins");
        //and add it to the menus
        Cytoscape.getDesktop().getCyMenus().addAction(action);
	}

    /**
     * This class gets attached to the menu item.
     */
    public class MultilevelLayoutSelectionAction extends CytoscapeAction implements Task {
        
    	/**
    	 * Task monitor used to report calculation progress.
    	 */
    	private TaskMonitor taskMonitor;
    	/**
    	 * Instance of the class used for layout calculation. 
    	 */
    	private MultilevelLayout algObj;
    	/**
    	 * The graph the layout is calculated for. 
    	 */
    	private CyNetwork network;
    	/**
    	 * View of the graph the layout is calculated for.
    	 */
    	private CyNetworkView view;
    	
		private static final long serialVersionUID = -4840201619467047796L;

		/**
         * Class constructor.
         */
        public MultilevelLayoutSelectionAction(){
        	super("Multilevel Layout");
        }
        
        private boolean ready = false;
        
        /**
         * Launches the plugin when the appropriate menu item is 
         * selected by the user.
         * @param ae Event triggered when the menu item is selected.
         */
        public void actionPerformed(ActionEvent ae){
        	System.out.println("Start MultiLevelPlugin");
        	
        	//get the network object
            network = Cytoscape.getCurrentNetwork();
            //get the network view object
            view = Cytoscape.getCurrentNetworkView();
            //can't continue if either of these is null
            if (network == null || view == null) {
            	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "There was no graph or graph view available, layout " +
            			"calculation aborted.", "Error: No graph or graph view available", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            //if there are only 0 nodes, do nothing
            if (network.getNodeCount() == 0) {
            	JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Current graph contains 0 nodes, " +
            			"calculation aborted.", "Error: Zero nodes in current graph", JOptionPane.ERROR_MESSAGE);
            	return;
            }
            //if the graph is not connected, do nothing
            /*
    		if(GraphConnectivityChecker.graphIsConnected(network) != null){
    			JOptionPane.showMessageDialog( Cytoscape.getDesktop(), "The graph is not connected. This version of " +
    					"MultiLevelLayout plugin only supports connected graphs.");
    			return;
    		}*/
            //apply the MultilevelLayout
    		
    		OptionsDialog optionsDialog = new OptionsDialog();
        	optionsDialog.setVisible(true);
        	if(optionsDialog.selected == OptionsDialog.CANCEL_PRESSED) return;
    		
    		ready = true;
            JTaskConfig taskConfig = getNewDefaultTaskConfig();
            TaskManager.executeTask(this, taskConfig);
            
    		CyAttributes nodesAttributes = Cytoscape.getNodeAttributes();
    		nodesAttributes.deleteAttribute("ml_previous");
    		nodesAttributes.deleteAttribute("ml_ancestor1");
    		nodesAttributes.deleteAttribute("ml_ancestor2");
    		nodesAttributes.deleteAttribute("ml_weight");
    		nodesAttributes.deleteAttribute("mllp_partition");
            
            System.out.println("Stop MultiLevelPlugin");
        }
        
    	/**
    	 * Runs the thread used for the actual layout calculation.
    	 */
        public void run(){
        	if(ready){
        		algObj = new MultilevelLayout();
        		algObj.setTaskMonitor(taskMonitor);
        		algObj.doLayout(view);
        		algObj = null;
        		System.gc();
        	}
    	}
        
        /**
         * Sets the task monitor to be used.
         */
    	public void setTaskMonitor(TaskMonitor _monitor){
    		taskMonitor = _monitor;
    	}
    	
    	/**
    	 * Gets the title of this task.
    	 * @return Title of this task.
    	 */
    	public String getTitle(){ 
    		return "Performing MultiLevelLayout"; 
    	}
    	
    	/**
    	 * Informs the algorithm it should be aborted.
    	 */
    	public void halt(){ 
    		algObj.setCancel();
    	}
    	
    	/**
    	 * Creates a new task configuration.
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
    	
    	private class OptionsDialog extends JDialog {
    		
			private static final long serialVersionUID = 9031568205322814419L;
			public JSlider repCSlider;
			public JSlider tolSlider;
			public JCheckBox clustBox;
			public static final int CANCEL_PRESSED = 0;
			public static final int OK_PRESSED = 1;
			public int selected = CANCEL_PRESSED;
			private int width = 340;
			private int height = 440; 
    		
    		public OptionsDialog(){
    			super(Cytoscape.getDesktop(), "Multilevel Layout Plugin: Set options", true);
    			
	    		JLabel header = new JLabel("Set options");
	    		header.setFont(new Font("SansSerif", Font.BOLD, 16));
	    		
	    		JLabel repCLabel = new JLabel("<html><b>Set the volume of repulsive force:</b> Higher values create more space " +
	    				"between nodes, and may affect the quality of the resulting layout. (Default: 0.2)</html>");
	    		
	    		repCSlider = new JSlider(JSlider.HORIZONTAL, 1, 9, (int)(MultilevelConfig.C*10));
	    		repCSlider.setMajorTickSpacing(5);
	    		repCSlider.setMinorTickSpacing(1);
	    		repCSlider.setPaintTicks(true);
	    		Hashtable<Integer, JLabel> repCLabelTable = new Hashtable<Integer, JLabel>();
	    		repCLabelTable.put( new Integer(1), new JLabel("0.1") );
	    		repCLabelTable.put( new Integer(3), new JLabel("0.3") );
	    		repCLabelTable.put( new Integer(5), new JLabel("0.5") );
	    		repCLabelTable.put( new Integer(7), new JLabel("0.7") );
	    		repCLabelTable.put( new Integer(9), new JLabel("0.9") );
	    		repCSlider.setLabelTable(repCLabelTable);
	    		repCSlider.setPaintLabels(true);
	    		repCSlider.setPreferredSize(new Dimension(300, 50));
	    		
	    		JLabel tolLabel = new JLabel("<html><b>Set the amount of tolerance allowed:</b> Higher values speed up " +
	    				"the calculation, but decrease quality of the resulting layout. (Default: 0.01)</html>");
	    		tolLabel.setSize(300, 50);
	    		
	    		tolSlider = new JSlider(JSlider.HORIZONTAL, 1, 9, (int)(MultilevelConfig.tolerance*100));
	    		tolSlider.setMajorTickSpacing(5);
	    		tolSlider.setMinorTickSpacing(1);
	    		tolSlider.setPaintTicks(true);
	    		Hashtable<Integer, JLabel> tolLabelTable = new Hashtable<Integer, JLabel>();
	    		tolLabelTable.put( new Integer(1), new JLabel("0.01") );
	    		tolLabelTable.put( new Integer(3), new JLabel("0.03") );
	    		tolLabelTable.put( new Integer(5), new JLabel("0.05") );
	    		tolLabelTable.put( new Integer(7), new JLabel("0.07") );
	    		tolLabelTable.put( new Integer(9), new JLabel("0.09") );
	    		tolSlider.setLabelTable(tolLabelTable);
	    		tolSlider.setPaintLabels(true);
	    		tolSlider.setPreferredSize(new Dimension(300, 50));
	    		
	    	    clustBox = new JCheckBox("<html><b>Use clustering option</b></html>");
	    	    clustBox.setMnemonic(KeyEvent.VK_L);
	    	    if(MultilevelConfig.clusteringEnabled) clustBox.setSelected(true);
	    	    else clustBox.setSelected(false);
	    		
	    	    JPanel buttonPanel = new JPanel();
	    	    buttonPanel.setLayout(new GridLayout(1, 0));
	    	    JButton okButton = new JButton("OK");
	    	    okButton.addActionListener(new OkButtonListener(this));
	    	    okButton.setMnemonic(KeyEvent.VK_O);
	    	    JButton defaultsButton = new JButton("<html>Default values</html>");
	    	    defaultsButton.addActionListener(new DefaultsButtonListener(this));
	    	    defaultsButton.setMnemonic(KeyEvent.VK_D);
	    	    JButton cancelButton = new JButton("Cancel");
	    	    cancelButton.addActionListener(new CancelButtonListener(this));
	    	    cancelButton.setMnemonic(KeyEvent.VK_C);
	    	    buttonPanel.add(okButton);
	    	    buttonPanel.add(defaultsButton);
	    		buttonPanel.add(cancelButton);
	    	    
	    		JPanel panel = new JPanel();
	    		panel.setLayout(new GridLayout(0, 1, 5, 10));
	    		panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
	    		panel.add(header);
	    		panel.add(repCLabel);
	    		panel.add(repCSlider);
	    		panel.add(tolLabel);
	    		panel.add(tolSlider);
	    		panel.add(clustBox);
	    		panel.add(buttonPanel);
	    		
	    		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	    		this.setPreferredSize(new Dimension(width, height));
	    		this.setLocationRelativeTo(Cytoscape.getDesktop());
	    		this.setLocation(this.getX() - width/2, this.getY() - height/2);
	    		this.setResizable(false);
	    		this.add(panel);
	    		this.pack();	
    		}
    		
    		public class OkButtonListener implements ActionListener{
    			
    			private OptionsDialog dialog;
    			
    			public OkButtonListener(OptionsDialog d){
    				this.dialog = d;
    			}
    			
    			public void actionPerformed(ActionEvent e){
    				dialog.setVisible(false);
    				MultilevelConfig.C = (double)dialog.repCSlider.getValue()/10.0;
    				MultilevelConfig.tolerance = (double)dialog.tolSlider.getValue()/100.0;
    				MultilevelConfig.clusteringEnabled = dialog.clustBox.isSelected();
    				dialog.selected = OK_PRESSED;
    			}
    		}
    			
    		public class CancelButtonListener implements ActionListener{
        			
        		private OptionsDialog dialog;
        			
        		public CancelButtonListener(OptionsDialog d){
        			this.dialog = d;
        		}
        			
        		public void actionPerformed(ActionEvent e){
        			dialog.setVisible(false);
        			dialog.selected = CANCEL_PRESSED;
        		}
    		}
    		
    		public class DefaultsButtonListener implements ActionListener{
    			
        		private OptionsDialog dialog;
        			
        		public DefaultsButtonListener(OptionsDialog d){
        			this.dialog = d;
        		}
        		
        		//defaults are: C = 0.2, tolerance = 0.01, clusteringEnabled = false
        		public void actionPerformed(ActionEvent e){
        			dialog.repCSlider.setValue(2);
        			MultilevelConfig.C = 0.2;
        			dialog.tolSlider.setValue(1);
        			MultilevelConfig.tolerance = 0.01;
        			dialog.clustBox.setSelected(false);
        			MultilevelConfig.clusteringEnabled = false;
        		}
    		}
    	}
    }
}
