package cytoscape.performance;

import cytoscape.*;
import cytoscape.actions.*;
import cytoscape.util.*;
import cytoscape.visual.*;
import cytoscape.view.*;
import cytoscape.layout.*;
import cytoscape.task.*;

import javax.swing.JOptionPane;
import yfiles.*; 

public aspect CytoscapeProfiler {


	/**
	 * The primary pointcut for identifying methods whose performance we want to track.
	 */
	pointcut id() : execution(* Cytoscape.createNetworkView(CyNetwork,String)) ||
	                execution(* Cytoscape.addNetwork(..)) || 
	                execution(* CytoscapeInit.init(..)) || 
	                //execution(* VisualMappingManager.applyNodeAppearances(CyNetwork,CyNetworkView)) ||
	                //execution(* VisualMappingManager.applyEdgeAppearances(CyNetwork,CyNetworkView)) ||
	                execution(* LayoutAlgorithm.doLayout(CyNetworkView)) || 
	                execution(* LayoutAlgorithm.doLayout(CyNetworkView, TaskMonitor)) || 
	                execution(* CytoscapeAction.actionPerformed(..)) //|| 
			      ;
	
	before() : id() {
		Tracker.track(State.BEGIN,thisJoinPoint.getSignature().toString());
	}

	after() : id() {
		Tracker.track(State.END,thisJoinPoint.getSignature().toString());
	}

	/**
	 * This pointcut is needed to identify when layouts finish. The advice that uses
	 * it will popup a dialog that will be used by swingunit to know when a layout
	 * are finished.
	 */
	pointcut layoutSupport() : execution(* LayoutAlgorithm.doLayout(CyNetworkView)); 

	after() : layoutSupport() {
		 JOptionPane.showMessageDialog(Cytoscape.getDesktop(), "Layout Finished", 
		                               "Layout Finished", JOptionPane.INFORMATION_MESSAGE);
	}
		
}
