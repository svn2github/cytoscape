package ManualLayout;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import cytoscape.Cytoscape;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.data.SelectEventListener;
import cytoscape.data.SelectEvent;
import cytoscape.view.cytopanels.CytoPanelListener;
import ManualLayout.common.GraphConverter2;
import ManualLayout.rotate.RotatePanel;
import ManualLayout.rotate.RotationLayouter;
import ManualLayout.scale.ScaleLayouter;
import ManualLayout.scale.ScalePanel;
import java.awt.Dimension;
import cytoscape.view.cytopanels.BiModalJSplitPane;
import javax.swing.JPanel;
/**
 * 
 * This class is enabled only when ManualLayout plugin is loaded. This action is
 * under "Layout" menu.
 * 
 * Change history:
 *  version 0.1    9/14/2006  Peng-Liang Wang   Original creation 
 *  version 0.2    9/17/2006  Peng-Liang Wang   Change the anonymous classes (listeners) to named classes  
 *  version 0.21   9/21/2006  Peng-Liang Wang   Fix a null pointer exception  
 *  version 0.3   10/03/2006  Peng-Liang Wang   Move the manualLayout to cytoPanel_SOUTH_WEST  
 *  version 0.31  01/25/2007  Peng-Liang Wang   Add propertyChnageListener to cytoPanel5, to keep track of state for each view  
 *  version 0.32  01/28/2007  Peng-Liang Wang   Move it to BordLayout.SOUTH of cytopanel_1  
 * 
 */
public class ManualLayoutAction extends CytoscapeAction {

	private RotatePanelSliderListener rotatePanelSliderListener;
	private ScalePanelSliderListener scalePanelSliderListener;
	private TreeSelectListener treeSelectListener;
	private CytoPanel5Listener cytoPanel5Listener;
	private RotatePanel rotatePanel = (RotatePanel) Cytoscape.getDesktop()
			.getCytoPanel(SwingConstants.SOUTH_WEST).getComponentAt(0);
	private ScalePanel scalePanel = (ScalePanel) Cytoscape.getDesktop()
			.getCytoPanel(SwingConstants.SOUTH_WEST).getComponentAt(1);
	private MutablePolyEdgeGraphLayout[] nativeGraph = null;
	private RotationLayouter[] rotation = null;
	private ScaleLayouter[] scale = null;
	private int menuItemIndex = -1;
	
	private String preFocusedViewId = "none";
	private String curFocusedViewId = "none";

	private HashMap<String, int[]> layoutStateMap = new HashMap<String, int[]>();
	private CytoPanelImp cytoPanel1 = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST);
	private CytoPanelImp manualLayoutPanel = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST);
	private BiModalJSplitPane split; 
	
	public ManualLayoutAction() {
		split = new BiModalJSplitPane(Cytoscape.getDesktop(),
				JSplitPane.VERTICAL_SPLIT, BiModalJSplitPane.MODE_HIDE_SPLIT,
				new JPanel(), manualLayoutPanel);
		split.setResizeWeight(0);
		manualLayoutPanel.setCytoPanelContainer(split);

		manualLayoutPanel.setMinimumSize(new Dimension(180, 185));
		manualLayoutPanel.setMaximumSize(new Dimension(180, 185));
		manualLayoutPanel.setPreferredSize(new Dimension(180, 185));
	}
	
	public void actionPerformed(ActionEvent ev) {

		// preFocusedViewId will be used to restore state, if it is focused again
		if (preFocusedViewId.equals("none")) {
			preFocusedViewId = Cytoscape.getCurrentNetworkView().getIdentifier();
		}
		
		Object _source = ev.getSource();

		if (_source instanceof JCheckBoxMenuItem) {

			JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) _source;
			JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus()
					.getLayoutMenu();
			if (menuItem == (JCheckBoxMenuItem) layoutMenu.getMenuComponent(0)) {
				menuItemIndex = 0;
			} else if (menuItem == (JCheckBoxMenuItem) layoutMenu
					.getMenuComponent(1)) {
				menuItemIndex = 1;
			}
			if (menuItem == (JCheckBoxMenuItem) layoutMenu.getMenuComponent(2)) {
				menuItemIndex = 2;
			}
		}

		// Check the state of the manual layout Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.SOUTH_WEST).getState();

		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).setState(
					CytoPanelState.DOCK);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
					.setSelectedIndex(menuItemIndex);

			if (nativeGraph == null) {
				nativeGraph = new MutablePolyEdgeGraphLayout[] { GraphConverter2.getGraphReference(16.0d, true, false) };
				rotation = new RotationLayouter[] { new RotationLayouter(nativeGraph[0]) };
				scale = new ScaleLayouter[] { new ScaleLayouter(nativeGraph[0]) };				
			}

			addEventListeners();

			cytoPanel1.addComponentToSouth(split);	

			// Case 2: Panel is in the DOCK/FLOAT
		} else if (isAnyCheckBoxSelected()) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
					.setSelectedIndex(menuItemIndex);
		
		} else { // Case 3: The only checkBox is deSelected
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).setState(
					CytoPanelState.HIDE);
			removeEventListeners();
			//Remove the manuallayoutPanel
			//removeComponentAtSouth(split) does not work, overwrite it is a workaround
			cytoPanel1.addComponentToSouth(new javax.swing.JLabel());
		}

		cytoPanel1.validate();
	}// action performed

	private boolean isAnyCheckBoxSelected() {
		JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getLayoutMenu();

		JCheckBoxMenuItem rotateCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
				.getMenuComponent(0);
		JCheckBoxMenuItem scaleCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
				.getMenuComponent(1);
		JCheckBoxMenuItem controlCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
				.getMenuComponent(2);

		if (rotateCheckBoxMenuItem.getState()
				|| scaleCheckBoxMenuItem.getState()
				|| controlCheckBoxMenuItem.getState())
			return true;
		else
			return false;
	}

	private void addEventListeners() {
		rotatePanelSliderListener = new RotatePanelSliderListener();
		rotatePanel.jSlider.addChangeListener(rotatePanelSliderListener);

		scalePanelSliderListener = new ScalePanelSliderListener();
		scalePanel.jSlider.addChangeListener(scalePanelSliderListener);

		treeSelectListener = new TreeSelectListener();
		cytoPanel5Listener = new CytoPanel5Listener();

		Cytoscape.getCurrentNetwork()
				.addSelectEventListener(treeSelectListener);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
				.addCytoPanelListener(cytoPanel5Listener);
	} // addEventListeners()

	private void removeEventListeners() {
		// if CytoPanelState become HIDE, we should remove the EventListeners to
		// avoid memory leak
		rotatePanel.jSlider.removeChangeListener(rotatePanelSliderListener);
		scalePanel.jSlider.removeChangeListener(scalePanelSliderListener);
		Cytoscape.getCurrentNetwork().removeSelectEventListener(
				treeSelectListener);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
				.removeCytoPanelListener(cytoPanel5Listener);
	}

	// inner class definitions for all the event listeners
	public class RotatePanelSliderListener implements ChangeListener {

		int prevValue = rotatePanel.jSlider.getValue();

		public void stateChanged(ChangeEvent e) {
			if (rotatePanel.jSlider.getValue() == prevValue)
				return;

			nativeGraph[0] = GraphConverter2.getGraphReference(128.0d, true,
					rotatePanel.jCheckBox.isSelected());
			rotation[0] = new RotationLayouter(nativeGraph[0]);

			double radians = ((double) (rotatePanel.jSlider.getValue() - prevValue))
					* 2.0d * Math.PI / 360.0d;
			rotation[0].rotateGraph(radians);
			Cytoscape.getCurrentNetworkView().updateView();

			prevValue = rotatePanel.jSlider.getValue();
		}
	} // End of class RotatePanelSliderListener

	public class ScalePanelSliderListener implements ChangeListener {
		private int prevValue = scalePanel.jSlider.getValue();

		public void stateChanged(ChangeEvent e) {
			if (prevValue == scalePanel.jSlider.getValue())
				return;

			nativeGraph[0] = GraphConverter2.getGraphReference(128.0d, true,
					scalePanel.jCheckBox.isSelected());
			scale[0] = new ScaleLayouter(nativeGraph[0]);

			double prevAbsoluteScaleFactor = Math.pow(2,
					((double) prevValue) / 100.0d);

			double currentAbsoluteScaleFactor = Math.pow(2,
					((double) scalePanel.jSlider.getValue()) / 100.0d);

			double neededIncrementalScaleFactor = currentAbsoluteScaleFactor
					/ prevAbsoluteScaleFactor;

			scale[0].scaleGraph(neededIncrementalScaleFactor);
			Cytoscape.getCurrentNetworkView().updateView();
			prevValue = scalePanel.jSlider.getValue();
		}
	};// End of class ScalePanelSliderListener

	public class TreeSelectListener implements SelectEventListener {

		public void onSelectEvent(SelectEvent event) {
			if (Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length == 0) {
				rotatePanel.jCheckBox.setEnabled(false);
				scalePanel.jCheckBox.setEnabled(false);
			} else {
				rotatePanel.jCheckBox.setEnabled(true);
				rotatePanel.jCheckBox.setSelected(false);
				scalePanel.jCheckBox.setEnabled(true);
				scalePanel.jCheckBox.setSelected(false);
			}
		}
	} // End of class TreeSelectListener

	public class CytoPanel5Listener implements CytoPanelListener, PropertyChangeListener {

		public CytoPanel5Listener() {
			Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
			.addPropertyChangeListener(this);
		}
		
		//Keep track of the state for each view focused
		public void propertyChange(PropertyChangeEvent e) {

			if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED) {

				curFocusedViewId = Cytoscape.getCurrentNetworkView().getIdentifier(); 
				
				// detect duplicate NETWORK_VIEW_FOCUSED event 
				if (preFocusedViewId.equals(curFocusedViewId)) {
					return;
				}
				
				//save layout state for the previous network view
				int[] stateValue = {rotatePanel.jSlider.getValue(),scalePanel.jSlider.getValue()};
				layoutStateMap.put(preFocusedViewId, stateValue);

				//Remove event listener before restore the value
				removeEventListeners();
				
				//Restore layout state for the current network view, if any
				stateValue = layoutStateMap.get(curFocusedViewId);
				if (stateValue == null) {
					rotatePanel.jSlider.setValue(0);
					scalePanel.jSlider.setValue(1);
				}
				else {
					rotatePanel.jSlider.setValue(stateValue[0]);
					scalePanel.jSlider.setValue(stateValue[1]);					
				}

				//Restore event listener after restore the state value
				addEventListeners();

				preFocusedViewId = curFocusedViewId;				
			}
		}

		public void onComponentAdded(int count) {
		}

		public void onComponentRemoved(int count) {
		}

		public void onComponentSelected(int componentIndex) {
			// Sync MenuItem Check Box of Layout
			JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus()
					.getLayoutMenu();

			JCheckBoxMenuItem rotateCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
					.getMenuComponent(0);
			JCheckBoxMenuItem scaleCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
					.getMenuComponent(1);
			JCheckBoxMenuItem controlCheckBoxMenuItem = (JCheckBoxMenuItem) layoutMenu
					.getMenuComponent(2);

			switch (componentIndex) {
			case 0: // "Rotate"
				rotateCheckBoxMenuItem.setSelected(true);
				scaleCheckBoxMenuItem.setSelected(false);
				controlCheckBoxMenuItem.setSelected(false);
				break;
			case 1: // "Scale"
				rotateCheckBoxMenuItem.setSelected(false);
				scaleCheckBoxMenuItem.setSelected(true);
				controlCheckBoxMenuItem.setSelected(false);
				break;
			case 2: // "Control"
				rotateCheckBoxMenuItem.setSelected(false);
				scaleCheckBoxMenuItem.setSelected(false);
				controlCheckBoxMenuItem.setSelected(true);
			}
		}

		public void onStateChange(CytoPanelState newState) {
		}

	} // End of CytoPanel5Listener
}
