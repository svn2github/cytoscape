package ManualLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
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

import ManualLayout.common.GraphConverter2;
import ManualLayout.rotate.RotatePanel;
import ManualLayout.rotate.RotationLayouter;
import ManualLayout.scale.ScaleLayouter;
import ManualLayout.scale.ScalePanel;
/**
 * 
 * This class is enabled only when ManualLayout plugin is loaded.
 * This action is under "Layout" menu.
 * 
 *  	Original creation   	9/14/2006		Peng-Liang Wang
 * 
 */
public class ManualLayoutAction extends CytoscapeAction {

	String thisAction = "Rotate";
	
	public ManualLayoutAction(String pAction) {
		super(pAction); //pAction = "Rotate"/"Scale"/"Control"
		thisAction = pAction;
		if (thisAction.equals("Align and Distribute"))
		{
			thisAction = "Control";
		}
	}

	public void actionPerformed(ActionEvent ev) {

		// Check the state of the manual layout Panel
		CytoPanelState curState = Cytoscape.getDesktop().getCytoPanel(
				SwingConstants.EAST).getState();

		int targetIndex = 0;

		if (thisAction.equals("Scale"))
		{
			targetIndex = 1;
		}
		else if (thisAction.equals("Control"))
		{
			targetIndex = 2;			
		}
		
		// Case 1: Panel is disabled
		if (curState == CytoPanelState.HIDE) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.FLOAT);
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST)
					.setSelectedIndex(targetIndex);
			//CytoPanelImp theCytoPane = (CytoPanelImp) Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST);
			//theCytoPane.setPreferredSize(new Dimension(100,50));
			addEventListeners();
			
		// Case 2: Panel is in the Dock
		} else if (curState == CytoPanelState.DOCK) {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			removeEventListeners();
			
		// Case 3: Panel is FLOAT
		} else {
			Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).setState(
					CytoPanelState.HIDE);
			removeEventListeners();
		}

	}// action performed
	
	
	private void addEventListeners()
	{

    	final RotatePanel rotatePanel = (RotatePanel) Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).getComponentAt(0);
    	final ScalePanel scalePanel = (ScalePanel) Cytoscape.getDesktop().getCytoPanel(SwingConstants.EAST).getComponentAt(1);

 	    final MutablePolyEdgeGraphLayout[] nativeGraph = new MutablePolyEdgeGraphLayout[]
	  	        { GraphConverter2.getGraphReference(16.0d, true, false) };
	  	      
	  	final RotationLayouter[] rotation = new RotationLayouter[] { new RotationLayouter(nativeGraph[0]) };

	    final ScaleLayouter[] scale = new ScaleLayouter[] { new ScaleLayouter(nativeGraph[0]) };

	    rotatePanel.jSlider.addChangeListener(
	      new ChangeListener()
	      {
  
	        int prevValue = rotatePanel.jSlider.getValue();

		    public void stateChanged(ChangeEvent e)
		    {
		        if (rotatePanel.jSlider.getValue() == prevValue) return;
		  
		        nativeGraph[0] = GraphConverter2.getGraphReference(128.0d, true, rotatePanel.jCheckBox.isSelected());
                rotation[0] = new RotationLayouter(nativeGraph[0]);

		        double radians = ((double) (rotatePanel.jSlider.getValue() - prevValue)) *
		                   2.0d * Math.PI / 360.0d;
	            rotation[0].rotateGraph(radians);
		        Cytoscape.getCurrentNetworkView().updateView();

		        prevValue = rotatePanel.jSlider.getValue();
		   }
	      }
	    );

		
	    scalePanel.jSlider.addChangeListener(
	    	      new ChangeListener()
	    	      {
	    	        private int prevValue = scalePanel.jSlider.getValue();
	    		
	    		public void stateChanged(ChangeEvent e)
	    		{
	    	          if (prevValue == scalePanel.jSlider.getValue()) return;

	    	    	  nativeGraph[0] = GraphConverter2.getGraphReference
	                     (128.0d, true, scalePanel.jCheckBox.isSelected());
                      scale[0] = new ScaleLayouter(nativeGraph[0]);

	    	          double prevAbsoluteScaleFactor =
	    	            Math.pow(2, ((double) prevValue) / 100.0d);

	    	          double currentAbsoluteScaleFactor =
	    	            Math.pow(2, ((double) scalePanel.jSlider.getValue()) / 100.0d);

	    	          double neededIncrementalScaleFactor =
	    	            currentAbsoluteScaleFactor / prevAbsoluteScaleFactor;
	    		    
	    	          scale[0].scaleGraph(neededIncrementalScaleFactor);
	    	          Cytoscape.getCurrentNetworkView().updateView();
	    	          prevValue = scalePanel.jSlider.getValue();
	    	        }
	    	      }
	    	    );
	    
	    Cytoscape.getCurrentNetwork().addSelectEventListener(new SelectEventListener()
	    {
	    	public void onSelectEvent(SelectEvent event) {
	    	    if (Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length == 0)
	    	    {
	    	      rotatePanel.jCheckBox.setEnabled(false);
	    	      scalePanel.jCheckBox.setEnabled(false);
	    	    }
	    	    else
	    	    {
		    	    rotatePanel.jCheckBox.setEnabled(true);
		    	    rotatePanel.jCheckBox.setSelected(false);
	    	    	scalePanel.jCheckBox.setEnabled(true);
	    	    	scalePanel.jCheckBox.setSelected(false);
		    	}
	    	}
	    }	
	    );

	} // addEventListeners()
	
	private void removeEventListeners()
	{
		// if CytoPanelState become HIDE,  we should remove the EventListeners to play safe
		
	}
}
