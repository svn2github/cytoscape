/*
 * Created on May 28, 2005
 *
 */
package cytoscape.editor;

import java.awt.Image;
import java.awt.dnd.DragSource;

import javax.swing.Icon;

/**
 * NOTE: THE CYTOSCAPE EDITOR FUNCTIONALITY IS STILL BEING EVOLVED AND IN A STATE OF TRANSITION TO A 
 * FULLY EXTENSIBLE EDITING FRAMEWORK FOR CYTOSCAPE VERSION 2.3.  
 * 
 * THE JAVADOC COMMENTS ARE OUT OF DATE IN MANY PLACES AND ARE BEING UPDATED.  
 * THE APIs WILL CHANGE AND THIS MAY IMPACT YOUR CODE IF YOU 
 * MAKE EXTENSIONS AT THIS POINT.  PLEASE CONTACT ME (mailto: allan_kuchinsky@agilent.com) 
 * IF YOU ARE INTENDING TO EXTEND THIS CODE AND I WILL WORK WITH YOU TO HELP MINIMIZE THE IMPACT TO YOUR CODE OF 
 * FUTURE CHANGES TO THE FRAMEWORK
 *
 * PLEASE SEE http://www.cytoscape.org/cgi-bin/moin.cgi/CytoscapeEditorFramework FOR 
 * DETAILS ON THE EDITOR FRAMEWORK AND PLANNED EVOLUTION FOR CYTOSCAPE VERSION 2.3.
 *
 */


/**
 * 
 * Interface for defining draggable/droppable visual components in the 
 * Cytoscape editor framework.  The framework provides for dragging and dropping graphical
 * entities from palette onto the canvas.   Graphical entities are associated with
 * semantic objects, i.e. nodes and edges, that are created when the graphical entities
 * are dropped onto the canvas.
 *  
 * @author Allan Kuchinsky, Agilent Technologies
 *
 */
public interface GraphicalEntity {

	/**
	 * get the Title of the graphical entity
	 * @return the Title
	 */
	public String getTitle() ;

	/**
	 * set the Title of the graphical entity
	 * @param title The title to set.
	 */
	public void setTitle(String title) ;

	
	
	
	/**
	 * retrieves the source of the drag operation, used when dragging a graphical entity from the palette onto
	 * the canvas
	 * @return the drag source
	 */
	public DragSource getMyDragSource() ;

	
	/**
	 * defines the source of the drag operation, used when dragging a graphical entity from the palette onto
	 * the canvas
	 * @param myDragSource the drag source
	 */
	public void setMyDragSource(DragSource myDragSource) ;

	
	
	/**
	 * get the image for the icon used on the palette to represent the graphical entity
	 * @return the image
	 */
	public Image get_image() ;

	/**
	 * set the image for the icon used on the palette to represent the graphical entity
	 * @param _image the icon to set
	 *            
	 */
	public void set_image(Image _image) ;
	
	


	/**
	 * get the icon used on the palette to represent the graphical entity
	 * @return the icon used on the palette to represent the graphical entity
	 */
	public Icon getIcon() ;
	
	/**
	 * set the icon used on the palette to represent the graphical entity
	 * @param icon the icon to set
	 */
	public void setIcon(Icon icon) ;
	
	
	/**
	 * returns the name of the attribute associated with the Graphical Entity.  
	 * This is used to determine whether a Node or an Edge has been dropped on the canvas.
	 * This attribute will also be set for the CyNode or CyEdge created as a result of the drop operation.
	 * @return the attribute name
	 */
	public String getAttributeName() ;
	
	
	/**
	 * sets the name of the attribute associated with the Graphical Entity.  
	 * This is used to determine whether a Node or an Edge has been dropped on the canvas.
	 * This attribute will also be set for the CyNode or CyEdge created as a result of the drop operation.	
	 * @param attributeName the attribute name to set
	 */
	public void setAttributeName(String attributeName) ;

	
	/**
	 * returns the value of the attribute associated with the Graphical Entity.  
	 * This attribute will be set for the CyNode or CyEdge created as a result of the drop operation.
	 * @return the attribute value
	 */
	public String getAttributeValue() ;

	
	/**
	 * sets the value of the attribute associated with the Graphical Entity.  
	 * This attribute will be set for the CyNode or CyEdge created as a result of the drop operation.
	 * @param attributeValue The attributeValue to set.
	 */
	public void setAttributeValue(String attributeValue) ;
}