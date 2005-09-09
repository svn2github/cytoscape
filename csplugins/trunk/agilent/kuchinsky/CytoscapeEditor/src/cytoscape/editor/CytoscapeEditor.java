/*
 * Created on Jul 29, 2005
 *
 */
package cytoscape.editor;

import java.util.List;

import cytoscape.CyNetwork;
import cytoscape.editor.event.NetworkEditEventAdapter;

/**
 * 
 * <b>CytoscapeEditor</b> provides a framework for developers to implement specialized, semantics
 * driven graphical
 * editors and incorporate them into Cytoscape.  For example, a developer might build a <em>BioPAX</em>
 * editor whose operations adhere to the semantics of the <em>BioPAX</em> specification.   
 * The basic idea of the framework is to provide, on the Cytotoscape side, a set of common operations that
 * all editors would use to interact with the Cytoscape environment.  Such common operations include drag/drop
 * support, mouse event handling, management of CyNetworks and CyNetworkViews, and wrappers for Cytoscape core
 * methods that add and delete Cytoscape nodes and edges.  
 * <p>
 * <b>CytoscapeEditor</b> is an interface that specifies common methods which all editors must implement.
 * These methods will be called by methods in the framework.
 * <p>
 * @author Allan Kuchinsky, Agilent Technologies
 * @version 1.0
 *  
 */
public interface CytoscapeEditor {

	
	/**
	 * specialized initialization code for editor, called by CyNetworkEditorFactory, should be overridden
	 * @param args an arbitrary list of arguments to be used in initializing the editor
	 */
	public  void initializeControls (List args);
	
	/**
	 * specialized code that disables and/or removes controls when a ntework view changes, should be 
	 * overridden by the developer
	 * @param args arbitrary list of arguments 
	 */
	public void disableControls (List args);
	
	/**
	 * specialized code that enables and/or adds controls when a network view changes.  Currently not used; 
	 * rather a combination of intializeControls() and disableControls() is used to manage the association of
	 * visual controls with Network views.
	 * @param args
	 */
	public void enableControls (List args);
	


	/**
	 * set the name of the editor.  This name also serves as 'Editor Type' attribute.
	 * @param editorName
	 */
	public void setEditorName (String editorName);
	
	/**
	 * gets the name of the editor
	 * @return the name of the editor
	 */
	public String getEditorName ();
	
	
}