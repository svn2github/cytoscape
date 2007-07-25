package com.agilent.labs.excentricLabelsPlugin;

/**
 * 
 * 
 * Cytoscape plugin that implements dynamic neighborhood labeling, 
 * also known as Excentric Labels, a visualization that enables
 * dynamic labeling of the neighborhood of Nodes located around the cursor.  
 * 
 * as described in 
 * J.-D. Fekete and C. Plaisant. Excentric labeling: Dynamic neighborhood labeling 
 * for data visualization. In K. Ehrlich and W. Newman, editors, Proceedings of the International Conference 
 * on Human Factors in Computing Systems (CHI 99), pages 512--519. ACM, May 1999 
 * 
 * and as implemented in the InfoViz Toolkit (http://ivtk.sourceforge.net/) 
 * 
 * @author Jean-Daniel Fekete (INRIA), Allan Kuchinsky (Agilent) 
 * @version 0.1
 * 
 * 
 */
import infovis.visualization.magicLens.DefaultExcentricLabels;
import infovis.visualization.magicLens.ExcentricLabels;

import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyMenus;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;

public class ExcentricLabelsPlugin extends CytoscapePlugin {

	private CyNetworkView firstNetworkView = Cytoscape.getCurrentNetworkView();

	private HashMap viewWrapperMap = new HashMap();

	public ExcentricLabelsPlugin() {
		MainPluginAction mpa = new MainPluginAction();

		CyMenus cyMenus = Cytoscape.getDesktop().getCyMenus();
		JMenu plugInMenu = cyMenus.getOperationsMenu();
		JMenuItem menuItem1 = new JMenuItem("Show excentric labels");
		plugInMenu.add(menuItem1);
		menuItem1.addActionListener(mpa);
	}


	// ~ Inner Classes
	// //////////////////////////////////////////////////////////

	/**
	 * This class gets attached to the menu item.
	 */
	public class MainPluginAction extends AbstractAction {
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MainPluginAction() {
			super("Visualize as Adjacency Matrix in InfoViz");
		}

		/**
		 * Gives a description of this plugin.
		 */
		public String describe() {
			StringBuffer sb = new StringBuffer();
			sb.append("Show excentric labels");
			return sb.toString();
		}

		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
			CyNetworkView newView = Cytoscape.getCurrentNetworkView();
			ExcentricLabels excentric = new DefaultExcentricLabels();
			excentric.setOpaque(true);
			CyLabeledComponent labeledComponent = new CyLabeledComponent(newView);
			excentric.setVisualization(labeledComponent);
//			JComponent component = Cytoscape.getDesktop()
//					.getNetworkViewManager().getComponentForView(
//							newView);
			JComponent component =
//				Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(newView);
				((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS);
			CyExcentricLabelsWrapper wrapper = new CyExcentricLabelsWrapper(
					excentric, component, labeledComponent);
			viewWrapperMap.put(newView.getIdentifier(), wrapper);
			((DGraphView) newView).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(wrapper);
//			if (component instanceof JInternalFrame) {
//				JInternalFrame j = (JInternalFrame) component;
//				j.setGlassPane(wrapper);
//				wrapper.setOpaque(false);
//				j.getGlassPane().setVisible(true);
				
				

//				if (!wrapper.isInstalled()) {
					wrapper.getInteractor().install(wrapper);
					wrapper.setInstalled(true);
					wrapper.addMouseMotionListener(wrapper.getInteractor());
					wrapper.setVisible(true);
					wrapper.setSize(component.getWidth(), component.getHeight());
//				

				
//			}
		}
	}
}
