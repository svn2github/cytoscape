package cytoscape.tutorial20;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import giny.view.NodeView;
import ding.view.NodeContextMenuListener;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;

import browser.AttributeBrowser;
import browser.AttributeBrowserPlugin;
import static browser.DataObjectType.NODES;
import browser.DataObjectType;
/**
 * 
 */
public class Tutorial20 extends CytoscapePlugin {

	/**
	 * create a menu item
	 */
	public Tutorial20() {
		// Create an Action, add the action to Cytoscape menu
		MyPluginAction action = new MyPluginAction(this);
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) action);
	}
	
	public class MyPluginAction extends CytoscapeAction {

		private Object browser_clickedObject = null;
		private Object browser_RowID = null;
		private Object browser_AttriName = null;
		
		public MyPluginAction(Tutorial20 myPlugin) {
			// Add the menu item under menu pulldown "Plugins"
			super("Tutorial20");
			setPreferredMenu("Plugins");
		}


		public void actionPerformed(ActionEvent e) {
			
			if (Cytoscape.getCurrentNetworkView().getTitle() == null || Cytoscape.getCurrentNetworkView().getTitle().equalsIgnoreCase("null")) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"No network view!");	
				return;
			}

			MyNodeContextMenuListener l = new MyNodeContextMenuListener();
			Cytoscape.getCurrentNetworkView().addNodeContextMenuListener(l);	
			
			//Add a component to attribute browser content menu
			JMenuItem menuItem = new JMenuItem("MyMenuItem_browser");

			AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES).getattributeTable().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					//Remember the object clicked on attribute browser
					AttributeBrowser attBrowser = AttributeBrowserPlugin.getAttributeBrowser(browser.DataObjectType.NODES);
					final int column = attBrowser.getColumnModel().getColumnIndexAtX(e.getX());
					final int row = e.getY() / attBrowser.getattributeTable().getRowHeight();
					browser_clickedObject = attBrowser.getattributeTable().getValueAt(row, column);
					browser_RowID = attBrowser.getattributeTable().getValueAt(row, 0);
					browser_AttriName = attBrowser.getattributeTable().getTableHeader().getColumnModel().getColumn(column).getIdentifier();
				}
			});
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					String message = "MyMenuItem_browser is clicked!"+
					"\nbrowser_RowID = "+ browser_RowID + 
					"\nbrowser_AttrName = "+ browser_AttriName.toString()+
					"\nbrowser_clickedObject = "+ browser_clickedObject;

					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),message);	
				}
			});

			AttributeBrowserPlugin.addMenuItem(browser.DataObjectType.NODES, menuItem);
		}

		
		class MyNodeContextMenuListener implements NodeContextMenuListener {
			public void addNodeContextMenuItems(NodeView nodeView, JPopupMenu menu) 
			{ 
				JMenuItem myMenuItem = new JMenuItem("MyNodeMenuItem");
				
				myMenuItem.addActionListener(new MyNodeAction(nodeView));					

				if (menu == null) {
					menu = new JPopupMenu();
				}
				menu.add(myMenuItem); 
			} 
		}

		class MyNodeAction implements ActionListener {
			NodeView nodeView;
			public MyNodeAction(NodeView pNodeView) {
				nodeView = pNodeView;
			}
			
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"MyNodeMenuItem on node "+ nodeView.getNode().getIdentifier() + " is clicked");	
			}
		}
	}

}
