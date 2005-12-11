/*
 * Created on May 29, 2005
 *
 */
package cytoscape.editor.impl;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import cytoscape.Cytoscape;
import cytoscape.editor.GraphicalEntity;
import cytoscape.editor.event.BasicCytoShapeTransferHandler;


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
 * The <b>BasicCytoShapeEntity</b> class defines draggable/droppable visual components in the 
 * Cytoscape editor framework.  The framework provides for dragging and dropping graphical
 * entities from palette onto the canvas.   BasicCytoShapeEntity objects are associated with
 * semantic objects, i.e. nodes and edges, that are created when the graphical entities
 * are dropped onto the canvas.
 * @author Allan Kuchinsky
 * @version 1.0
 * 
 */
public class BasicCytoShapeEntity extends JComponent implements
// AJK: 11/15/05 add drag/drop support
        DragGestureListener,
		GraphicalEntity  {

	/**
	 * used for setting tooltip text
	 */
	JLabel _cytoShape;

	/**
	 * the title of the shape
	 */
	String title;

	/**
	 * attribute name for the shape
	 * should be one of "NodeType" or "EdgeType"
	 */
	String attributeName;  
	
	/**
	 * value for the attribute assigned to the shape
	 * for example a "NodeType" of "protein"
	 */
	String attributeValue; 

	/**
	 * the icon associated with the shape
	 */
	Icon _image;
	
	/**
	 * the source of a drag event
	 */
	DragSource myDragSource;
	
	DragGestureRecognizer myDragGestureRecognizer;
	
	BasicCytoShapeTransferHandler handler;
	
	/**
	 * the image associated with the Icon for the shape
	 */
	Image _img;


	/**
	 * 
	 * @param attributeName attribute name for the shape, should be one of "NodeType" or "EdgeType"
	 * @param attributeValue value for the attribute assigned to the shape, for example a "NodeType" of "protein"
	 * @param image the icon for the shape
	 * @param title the title of the shape
	 */
	public BasicCytoShapeEntity(String attributeName, String attributeValue, 
			Icon image, String title) {
		super();
		this.setTitle(title);
		_image = image;
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
		
		if (image instanceof ImageIcon)
		{
			_img = ((ImageIcon) image).getImage();
		}

		_cytoShape = new JLabel(image);
		_cytoShape.setToolTipText(title);
		this.setToolTipText(title);

		this.setLayout(new GridLayout(1, 1));
		TitledBorder t2 = BorderFactory.createTitledBorder(title);
		this.add(_cytoShape);
		this.setBorder(t2);
		


		// AJK: 11/15/05 BEGIN
		//   add drag/drop support
		myDragSource = new DragSource();
		myDragGestureRecognizer =
					myDragSource.createDefaultDragGestureRecognizer( 
							_cytoShape, 
							DnDConstants.ACTION_COPY, 
							this);
		handler = (new BasicCytoShapeTransferHandler (this, null));
		this.setTransferHandler(handler);
		
		this.setMaximumSize(new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5, 
        		2 * CytoShapeIcon.HEIGHT));		

		this.setMinimumSize(new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5, 
        		2 * CytoShapeIcon.HEIGHT));		
	

		this.setPreferredSize(new Dimension(
        		((JPanel) Cytoscape.getDesktop().getCytoPanel( SwingConstants.WEST )).getSize().width - 5, 
        		2 * CytoShapeIcon.HEIGHT));		
	
		// AJK: 11/15/05 END
	}

	

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title The title to set.
	 *            
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return Returns the DragSource.
	 */
	public DragSource getMyDragSource() {
		return myDragSource;
	}

	
	/**
	 * @param myDragSource The DragSource to set.
	 */
	public void setMyDragSource(DragSource myDragSource) {
		this.myDragSource = myDragSource;
	}
	
	/**
	 * @return Returns the icon associated with the shape
	 */
	public Icon getIcon () {
		return _image;
	}

	/**
	 * @param _image the icon to set for the shape
	 *            
	 */
	public void setIcon(Icon _image) {
		this._image = _image;
	}
	


	/**
	 * @return Returns the image associated with the shape's icon
	 */
	public Image get_image () {
		return _img;
	}
	/**
	 * @param _img The _img to set.
	 */
	public void set_image (Image _img) {
		this._img = _img;
	}
	/**
	 * @return Returns the attributeName.
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * @param attributeName The attributeName to set.
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * @return Returns the attributeValue.
	 */
	public String getAttributeValue() {
		return attributeValue;
	}
	/**
	 * @param attributeValue The attributeValue to set.
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}

	// AJK: 11/15/05 BEGIN
	//    implement the drag gesture recognized
	public void dragGestureRecognized (DragGestureEvent e)
	{
		e.startDrag(DragSource.DefaultCopyDrop, 
				handler.createTransferable(this));
	}
	// AJK: 11/15/05 END
	

}