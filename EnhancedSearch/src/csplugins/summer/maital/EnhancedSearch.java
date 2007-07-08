package csplugins.summer.maital;


import cytoscape.Cytoscape;
import cytoscape.util.CytoscapeAction;
import java.awt.event.ActionEvent;


public class EnhancedSearch {

   public EnhancedSearch () {
        Cytoscape.getDesktop().getCyMenus().getOperationsMenu().add(new MenuItemEnhancedSearch());
    }
    
	public class MenuItemEnhancedSearch extends CytoscapeAction {

		public MenuItemEnhancedSearch() {
			super("Enhanced Search");
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent e) {
			
			// Display dialog
			EnhancedSearchDialog dialog = new EnhancedSearchDialog();
			dialog.setVisible(true);

			if (!dialog.isCancelled()) {
				String query = dialog.getQuery();
				System.out.println("[EnhancedSearch] - " + query);
				IndexAndSearch mySearch = new IndexAndSearch(query);
			} else {
				System.out.println("[EnhancedSearch] - Search was canceled");
			}
		}
		
	}
}

