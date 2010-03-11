package cytoscape.performance;

import cytoscape.*;
import cytoscape.actions.*;
import cytoscape.util.*;
import cytoscape.visual.*;
import cytoscape.view.*;
import cytoscape.layout.*;
import cytoscape.task.*;
import cytoscape.data.readers.*;
import cytoscape.data.writers.*;

import cytoscape.performance.track.*;

import javax.swing.JOptionPane;

public aspect CytoscapeProfiler {


	/**
	 * The primary pointcut for identifying methods whose performance we want to track.
	 */
	pointcut id() : execution(* Cytoscape.createNetworkView(CyNetwork,String)) ||
	                execution(* Cytoscape.addNetwork(..)) || 
	                execution(* Cytoscape.createNetwork(..)) || 
	                execution(* DingNetworkView.fitContent(..)) || 
	                execution(* DingNetworkView.fitSelected(..)) || 
	                execution(* CalculatorIO.loadCalculators(..)) || 
	                execution(* CytoscapeInit.init(..)) || 
	                execution(* VisualMappingManager.applyAppearances(..)) || 
	                execution(* CyLayoutAlgorithm.doLayout(CyNetworkView)) || 
	                execution(* CyLayoutAlgorithm.doLayout(CyNetworkView, TaskMonitor)) || 
	                execution(* CytoscapeAction.actionPerformed(..)) || 
	                execution(* CyNetworkView.redrawGraph(..)) || 
	                execution(* CytoscapeSessionWriter.writeSessionToDisk(..)) ||
	                execution(* CytoscapeSessionReader.read(..))
			      ;
	
	before() : id() {
		Tracker.track(State.BEGIN,thisJoinPoint.getSignature().toString());
	}

	after() : id() {
		Tracker.track(State.END,thisJoinPoint.getSignature().toString());
	}

}
