package cytoscape.editor;


import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

import cytoscape.Cytoscape;
import cytoscape.editor.event.BasicNetworkEditEventHandler;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.view.CyNetworkView;
import cytoscape.view.CytoscapeDesktop;
import ding.view.DGraphView;
import ding.view.InnerCanvas;

/**
 * test plugin for CyAnnotationEditor.
 * 
 * @author Alexander Pico, UCSF
 * @version 1.0
 * 
 */
public class LayoutRegionPlugin extends CytoscapePlugin implements
		PropertyChangeListener {

	
	public JPanel cyAnnPanel;
	protected InnerCanvas canvas;
	
	
	/**
	 * 
	 */
	public LayoutRegionPlugin() {
		
		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(
		CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);

		Cytoscape.getDesktop().getSwingPropertyChangeSupport()
		.addPropertyChangeListener(
		CytoscapeDesktop.NETWORK_VIEW_CREATED, this);

		MainPluginAction mpa = new MainPluginAction();
		mpa.initializeCyAnnotationEditor();
		
	}

	public void propertyChange(PropertyChangeEvent e) {

//		if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {   //added 8/17
		
		CyNetworkView newView = Cytoscape.getCurrentNetworkView();  //added 8/17
		BasicNetworkEditEventHandler handler = new BasicNetworkEditEventHandler();
		handler.start((DGraphView) Cytoscape.getCurrentNetworkView());
		
		//			JComponent component = Cytoscape.getDesktop().getNetworkViewManager().getComponentForView(newView);
////			canvas = ((DGraphView) newView).getCanvas();  //added 8/17
//			cyAnnPanel = new JPanel();
////			canvas.add(cyAnnPanel);  //added 8/17
//			if (component instanceof JInternalFrame) {
//			JInternalFrame j = (JInternalFrame) component;
//			j.setGlassPane(cyAnnPanel); //mod 8/17
////			canvas.setVisible(false); //added 8/17
//			cyAnnPanel.setOpaque(false);
//			cyAnnPanel.setBounds(component.getX(), component.getY(), component.getWidth(), component.getHeight());
//			j.getGlassPane().setBackground(Color.RED);
//			j.getGlassPane().setVisible(true);
//			System.out.println("AP: Glass pane set to: " + j.getGlassPane());
//			
//			//test draw
//			Graphics g = canvas.getGraphics();
//		      g.setColor(Color.green);
//		      g.fillRect(0,0,50,50);
//		      g.setColor(Color.red);
//		      g.drawRect(0,0,40,40);
//		      g.dispose();
//		      canvas.repaint();
		      	
//			try {
//			CytoscapeEditor cyAnnEd = CytoscapeEditorFactory.INSTANCE.getEditor("CyAnnotationEditor");

//			}
//			catch(InvalidEditorException ie){
//			}
//		}  //added 8/17
			}
			

	// }
	
//	public InnerCanvas getCanvas() {
//		return canvas;
//	}
	
	/**
	 * This class gets attached to the menu item.
	 */
	public class MainPluginAction extends AbstractAction {
		/**
		 * The constructor sets the text that should appear on the menu item.
		 */
		public MainPluginAction() {
			super("CyAnnotation Editor");
		}
		/**
		 * This method is called when the user selects the menu item.
		 */
		public void actionPerformed(ActionEvent ae) {
//			initializeCyAnnotationEditor();
		}
		public void initializeCyAnnotationEditor() {
			
			System.out.println("AP: CyAnnotationEditor initialized");

			CytoscapeEditorManager.register(
					"CyAnnotationEditor", 
					"cytoscape.editor.event.PaletteNetworkEditEventHandler", 
					"CyAnnotationVisualStyle");
				//Note: CyAnnotationVisualStyle is a duplication of default VisualStyle class in cytoscape.visual package.

			CytoscapeEditorManager.initializeEditor(
					"CyAnnotationEditor", 
					"PaletteNetworkEditEventHandler");
		}	

	}
	
	
}