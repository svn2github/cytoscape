package csplugins.isb.dtenenbaum.sharedData;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;


/**
 * Provides a single object which plugins can use to share 
 * data with each other.
 * 
 * <B>NOTE:</B> The Data Cube plugin 
 * <code>csplugins.trial.pshannon.dataCube</code> is now dependent on this
 * plugin. (Is that stil true?)In fact, it is intended that any future plugin 
 * which desires to share data  with others, also be dependent on this plugin--
 * perhaps this should be moved to the core after more features are added.
 *    
 * @author Dan Tenenbaum
 */
public class SharedDataPlugin extends CytoscapePlugin  {
	private SharedDataSingleton sharedData = SharedDataSingleton.getInstance();
	private SharedDataPlugin.SharedObjectsDialog sod;
	private JButton okButton;
	
	public SharedDataPlugin () throws Exception {
		
		CytoscapeAction sharedAction = new SharedObjectsAction ();
		sharedAction.setPreferredMenu ("Plugins");
		Cytoscape.getDesktop().getCyMenus().addAction(sharedAction);		
	}
	
	/**
	 * Describes the plugin.
	 */
	public String describe() {
		return "Allows objects to be shared by all plugins.";
	}
	
	/**
	 * Action performed when this plugin's menu item is selected. 
	 */
	private class SharedObjectsAction extends CytoscapeAction  {
		SharedObjectsAction() {
			super ("View Shared Objects...");
		}
		
		/**
		 * Pops open the Shared Objects dialog.
		 */
		public void actionPerformed(ActionEvent e) {
			sod = new SharedObjectsDialog();
			//sod.pack();
			sod.show();
		}
	}
	
	/**
	 * A dialog box that displays the currently existing shared objects.
	 */
	private class SharedObjectsDialog extends JDialog {
		SharedObjectsDialog() {
			super(Cytoscape.getDesktop(),"Shared Objects",true);
			sod = this;
			setSize(500,200);
			placeInCenter();
			JPanel mainPanel = new JPanel();
			//mainPanel.setLayout(new GridLayout(2,1));
			mainPanel.setLayout(new BorderLayout());
			String[] columnNames = {"Type",
									"Identifier"};
			
			String[][] d = new String[sharedData.size()][2]; 
			Set set = sharedData.keySet();
			Iterator it = set.iterator();
			String key;
			Object value;
			int row = 0;
			int col = 0;
			while (it.hasNext()) {
				key = (String)it.next();
				value = sharedData.get(key);
				String strVal = value.getClass().getName();
				String[] segs = strVal.split("\\.");
				d[row][0] = segs[segs.length-1];
				d[row][1] = key;
				row++;
			}
			JTable table = new JTable(d, columnNames);
			
			
			
			JScrollPane scrollPane = new JScrollPane(table);
			table.setPreferredScrollableViewportSize(new Dimension(800,1500));
			table.setColumnSelectionAllowed(false);
			table.setRowSelectionAllowed(false);
			//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			JPanel topPanel = new JPanel();
			topPanel.setLayout(new GridLayout(1,1));
			topPanel.add(scrollPane);
			
			JPanel botPanel = new JPanel(); 
			botPanel.setLayout(new FlowLayout());
			okButton = new JButton();
			okButton.setAction(new OKAction());
			botPanel.add(okButton);
			
			mainPanel.add(topPanel, BorderLayout.CENTER);
			mainPanel.add(botPanel, BorderLayout.SOUTH);
			this.getContentPane().add(mainPanel);
		}
		
		public void placeInCenter () {
		  GraphicsConfiguration gc = getGraphicsConfiguration ();
		  int screenHeight = (int) gc.getBounds().getHeight ();
		  int screenWidth = (int) gc.getBounds().getWidth ();
		  int windowWidth = getWidth ();
		  int windowHeight = getHeight ();
		  setLocation ((screenWidth-windowWidth)/2, (screenHeight-windowHeight)/2);
	
		} // placeInCenter
		
		
	}
	
	
	/**
	 * Action performed when OK button is clicked in the 
	 * Shared Object Dialog. (Dialog exits.)
	 */
	private class OKAction extends AbstractAction {
		protected OKAction () {
			super("OK");
		}
		
		public void actionPerformed(ActionEvent e) {
			sod.dispose();
			return;
		}
	}
	
}