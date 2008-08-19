package cytoscape.view;

import giny.model.GraphObject;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import javax.swing.SwingConstants;

import com.l2fprod.common.propertysheet.PropertySheet;

import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.swing.ColumnResizer;
import cytoscape.view.cytopanels.CytoPanel;
import cytoscape.view.cytopanels.CytoPanelListener;
import cytoscape.view.cytopanels.CytoPanelState;


public class PropertySheetPlugin extends CytoscapePlugin 


{

	
	
	public PropertySheetPlugin () {
		PropertySheetController controller = new PropertySheetController();
	}
			
}