/*
 * Created on Jul 29, 2005
 *
 */
package cytoscape.editor;

import giny.model.Node;

import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeModifiedNetworkManager;
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
	
	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * this method will ensure that the node added is unique.  If it finds that 
	 * there is an existing node for <em>nodeName</em>, it will attempt to 
	 * generate a new, unique, <em>nodeName</em> by extending the <em>nodeName</em> 
	 * argument with a randomly generated extension.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param attribute
	 *            a defining property for the node, that can be used in
	 *            conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes. Also can be used,
	 *            by the canvas when handling a dropped item, to distinguish
	 *            between nodes and edges, so should be set to something like
	 *            "NodeType".
	 * @param value
	 *            the value of the attribute for this node. This can be used in
	 *            conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes, for example to
	 *            assign a violet diamond shape to a 'smallMolecule' node type.
	 * @return the CyNode that has been either reused or created.
	 */
	public CyNode addNode(String nodeName, String attribute,
			String value); 
	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param nodeType
	 *            the value of the 'NodeType' attribute for this node. This can
	 *            be used in conjunction with the Visual Mapper to assign visual
	 *            characteristics to different types of nodes. Also can be used,
	 *            by the canvas when handling, a dropped item, to distinguish
	 *            between nodes and edges.
	 * @return the CyNode that has been either reused or created.
	 */
	public CyNode addNode(String nodeName, String nodeType);

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @param create
	 *            if true, then create a node if one does not already exist.
	 *            Otherwise, only return a node if it already exists.
	 * @return the CyNode that has been either reused or created.
	 */
	public  CyNode addNode(String nodeName, boolean create);

	/**
	 * wrapper for adding a node in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This form of addNode()
	 * will create a node in all cases, whether it previously exists or not.
	 * 
	 * @param nodeName
	 *            the name of the node to be created. This will be used as a
	 *            unique identifier for the node.
	 * @return the CyNode that has been either reused or created.
	 */
	public CyNode addNode(String nodeName);

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param create
	 *            if true, then create an edge if one does not already exist.
	 *            Otherwise, return the edge if it already exists.
	 * @param edgeType
	 *            a value for the "EdgeType" attribute assigned to the edge.
	 *            This can be used in conjunction with the Visual Mapper.
	 * @return the CyEdge that has either been reused or created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create, String edgeType);
	

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This version always
	 * creates an edge, whether or not one already exists.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @return the CyEdge that has been created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value) ;

	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor. This version always
	 * creates an edge, whether or not one already exists.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param edgeType
	 *            a value for the "EdgeType" attribute assigned to the edge.
	 *            This can be used in conjunction with the Visual Mapper.
	 * @return the CyEdge that has been created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, String edgeType);
	/**
	 * 
	 * wrapper for adding an edge in Cytoscape. This is intended to be called by
	 * the CytoscapeEditor in lieu of making direct modifications to the
	 * Cytoscape model. Thus, it provides an insulating level of abstraction
	 * between the CytoscapeEditor and the Cytoscape implementation, allowing
	 * for portability and extensibility of the editor.
	 * 
	 * @param node_1
	 *            Node at one end of the edge
	 * @param node_2
	 *            Node at the other end of the edge
	 * @param attribute
	 *            the attribute of the edge to be searched, a common one is
	 *            Semantics.INTERACTION
	 * @param attribute_value
	 *            a value for the attribute, like "pp" or "default"
	 * @param create
	 *            if true, then create an edge if one does not already exist.
	 *            Otherwise, return the edge if it already exists.
	 * @return the CyEdge that has either been reused or created
	 *  
	 */
	public CyEdge addEdge(Node node_1, Node node_2, String attribute,
			Object attribute_value, boolean create) ;
	
	/**
	 * Deletes (hides) a node from the current network
	 * 
	 * @param node
	 *            the node to be deleted
	 */
	public void deleteNode(Node node) ;
	
	
	/**
	 * Deletes (hides) an edge from the current network
	 * 
	 * @param edge
	 *            the edge to be deleted
	 */
	public void deleteEdge(CyEdge edge);

	
}