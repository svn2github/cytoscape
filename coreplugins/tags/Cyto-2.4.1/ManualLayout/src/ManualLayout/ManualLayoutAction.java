package ManualLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cytoscape.Cytoscape;
import cytoscape.graph.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.util.CytoscapeAction;
import cytoscape.view.cytopanels.CytoPanelState;
import cytoscape.data.SelectEventListener;
import cytoscape.data.SelectEvent;
import cytoscape.view.cytopanels.CytoPanelImp;
import cytoscape.view.cytopanels.CytoPanelListener;

import ManualLayout.common.GraphConverter2;
import ManualLayout.rotate.RotatePanel;
import ManualLayout.rotate.RotationLayouter;
import ManualLayout.scale.ScaleLayouter;
import ManualLayout.scale.ScalePanel;

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
 * 
 */
public class ManualLayoutAction extends CytoscapeAction {

	private RotatePanelSliderListener rotatePanelSliderListener;

	private ScalePanelSliderListener scalePanelSliderListener;

	private TreeSelectListener treeSelectListener;

	private CytoPanel3Listener cytoPanel3Listener;

	private RotatePanel rotatePanel = (RotatePanel) Cytoscape.getDesktop()
			.getCytoPanel(SwingConstants.SOUTH_WEST).getComponentAt(0);

	private ScalePanel scalePanel = (ScalePanel) Cytoscape.getDesktop()
			.getCytoPanel(SwingConstants.SOUTH_WEST).getComponentAt(1);

	private MutablePolyEdgeGraphLayout[] nativeGraph = null;

	private RotationLayouter[] rotation = null;

	private ScaleLayouter[] scale = null;

	private int menuItemIndex = -1;

	public void actionPerformed(ActionEvent ev) {

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

			// Case 2: Panel is in the DOCK/FLOAT
		} else if (isAnyCheckBoxSelected()) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
					.setSelectedIndex(menuItemIndex);
		} else { // Case 3: The only checkBox is deSelected
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST).setState(
					CytoPanelState.HIDE);
			removeEventListeners();
		}

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
		cytoPanel3Listener = new CytoPanel3Listener();

		Cytoscape.getCurrentNetwork()
				.addSelectEventListener(treeSelectListener);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
				.addCytoPanelListener(cytoPanel3Listener);
	} // addEventListeners()

	private void removeEventListeners() {
		// if CytoPanelState become HIDE, we should remove the EventListeners to
		// avoid memory leak
		rotatePanel.jSlider.removeChangeListener(rotatePanelSliderListener);
		scalePanel.jSlider.removeChangeListener(scalePanelSliderListener);
		Cytoscape.getCurrentNetwork().removeSelectEventListener(
				treeSelectListener);
		Cytoscape.getDesktop().getCytoPanel(SwingConstants.SOUTH_WEST)
				.removeCytoPanelListener(cytoPanel3Listener);
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

	public class CytoPanel3Listener implements CytoPanelListener {
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

	} // End of CytoPanel3Listener
}
