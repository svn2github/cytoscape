/* -*-Java-*-
********************************************************************************
*
* File:         BasicCytoShapeEntity.java
* RCS:          $Header: $
* Description:
* Author:       Allan Kuchinsky
* Created:      Sun May 29 11:22:33 2005
* Modified:     Tue Dec 05 04:40:28 2006 (Michael L. Creech) creech@w235krbza760
* Language:     Java
* Package:
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2006, Agilent Technologies, all rights reserved.
*
********************************************************************************
*
* Revisions:
*
* Tue Dec 05 04:39:09 2006 (Michael L. Creech) creech@w235krbza760
*  Changed computation of BasicCytoShapeEntity size to allow for
*  larger CytoShapeIcons.
* Sun Aug 06 11:22:50 2006 (Michael L. Creech) creech@w235krbza760
*  Added generated serial version UUID for serializable classes.
********************************************************************************
*/
package cytoscape.editor.impl;

import cytoscape.Cytoscape;

import cytoscape.editor.GraphicalEntity;

import cytoscape.editor.event.BasicCytoShapeTransferHandler;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;


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
public class BasicCytoShapeEntity extends JComponent
    implements DragGestureListener, GraphicalEntity {
    // MLC 07/27/06:
    private static final long serialVersionUID = -5229827235046946347L;

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
    DragGestureRecognizer         myDragGestureRecognizer;
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
        _image              = image;
        this.attributeName  = attributeName;
        this.attributeValue = attributeValue;

        if (image instanceof ImageIcon) {
            _img = ((ImageIcon) image).getImage();
        }

        _cytoShape = new JLabel(image);

        if (this.attributeName != null) {
            if (this.attributeName.equals("NODE_TYPE")) {
                _cytoShape.setToolTipText("<html>To add a node to a network,<br>" +
                                          "drag and drop a shape<br>from the palette onto the canvas.</html>");
            } else if (this.attributeName.equals("EDGE_TYPE")) {
                _cytoShape.setToolTipText("<html>To connect two nodes with an edge<br>" +
                                          "drag and drop the arrow onto a node<br>on the canvas, " +
                                          "then move the cursor<br>over a second node and click the mouse.</html>");
            }
        }

        this.setLayout(new GridLayout(1, 1));

        TitledBorder t2 = BorderFactory.createTitledBorder(title);
        this.add(_cytoShape);
        this.setBorder(t2);

        myDragSource = new DragSource();
        // MLC 07/27/06:
        myDragSource.addDragSourceListener(new TestDragSourceListener());
        myDragGestureRecognizer = myDragSource.createDefaultDragGestureRecognizer(_cytoShape,
                                                                                  DnDConstants.ACTION_COPY,
                                                                                  this);
        handler                 = (new BasicCytoShapeTransferHandler(this, null));
        this.setTransferHandler(handler);

        // MLC 12/04/06 BEGIN:
        // force height to be at least
        // CytoscapeShapeIcon.DEFAULT_HEIGHT but larger if needed:
        Dimension mySize = new Dimension(((JPanel) Cytoscape.getDesktop()
                                                            .getCytoPanel(SwingConstants.WEST)).getSize().width -
                                         5,
                                         Math.max(_image.getIconHeight(),
                                                  CytoShapeIcon.DEFAULT_HEIGHT) +
                                         CytoShapeIcon.DEFAULT_HEIGHT);

        this.setMaximumSize(mySize);
        this.setMinimumSize(mySize);
        this.setPreferredSize(mySize);

        //        this.setMaximumSize(new Dimension(((JPanel) Cytoscape.getDesktop()
        //                                                             .getCytoPanel(SwingConstants.WEST)).getSize().width -
        //                5, 2 * CytoShapeIcon.HEIGHT));
        //
        //        this.setMinimumSize(new Dimension(((JPanel) Cytoscape.getDesktop()
        //                                                             .getCytoPanel(SwingConstants.WEST)).getSize().width -
        //                5, 2 * CytoShapeIcon.HEIGHT));
        //
        //        this.setPreferredSize(new Dimension(((JPanel) Cytoscape.getDesktop()
        //                                                               .getCytoPanel(SwingConstants.WEST)).getSize().width -
        //                5, 2 * CytoShapeIcon.HEIGHT));
        // MLC 12/04/06 END.
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
    public Icon getIcon() {
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
    public Image get_image() {
        return _img;
    }

    /**
     * @param _img The _img to set.
     */
    public void set_image(Image _img) {
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

    public void dragGestureRecognized(DragGestureEvent e) {
        e.startDrag(DragSource.DefaultCopyDrop,
                    handler.createTransferable(this));
    }

    // MLC 07/27/06 BEGIN:
    private class TestDragSourceListener extends DragSourceAdapter {
        public void dragEnter(DragSourceDragEvent dsde) {
            dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);

            // CytoscapeEditorManager.log("dragEnter = " + comp);
        }

        // public void dragOver(DragSourceDragEvent dsde) {
        //    DragSourceContext dsc = (DragSourceContext) dsde.getSource();
        //   Component comp = dsc.getComponent();
        //   CytoscapeEditorManager.log("dragOver = " + comp);
        //}
        public void dragExit(DragSourceEvent dse) {
            dse.getDragSourceContext().setCursor(DragSource.DefaultCopyNoDrop);

            // CytoscapeEditorManager.log("dragExit");
        }

        // MLC 07/27/06 END.
    }
}
