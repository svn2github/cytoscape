/*
 * TextPanel.java
 *
 * Created on Jun 10, 2010, 3:22:03 PM
 */

package CyAnnotator.ui;

import java.awt.*;
import java.awt.geom.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 *
 * @author Avinash Thummala
 */

//This class is a Draggable JPanel with an overidden paint method

public class TextPanel extends javax.swing.JPanel {

    public TextPanel() {
        initComponents();

        //To make this panel a draggable component
        DraggableComponent panelDrag=new DraggableComponent();
    }

    class DraggableComponent extends DragSourceAdapter implements DragGestureListener{

        DragSource dragSource;

        DraggableComponent(){

            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer( TextPanel.this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        }

        public void dragGestureRecognized(DragGestureEvent dge) {

            //Used a basic TextFlavour and transferred the string "TextAnnotation", which will be verified in the drop method

            Transferable t = new StringSelection("TextAnnotation");
            dragSource.startDrag (dge, DragSource.DefaultCopyDrop, t, this);
        }

    }

    //Overidden paint method for this JPanel in which we draw a String.

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2=(Graphics2D)g;

        g2.setFont(new java.awt.Font("Tahoma", 1, 24));

        Rectangle2D rectangle=g2.getFont().getStringBounds("Text Annotation", g2.getFontRenderContext());

        int x=(int)(this.getWidth()-rectangle.getWidth())/2;
        int y=(int)(this.getHeight()+rectangle.getHeight())/2;

        g2.drawString("Text Annotation", x, y);
    }


    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder("Text Annotation"));
        setToolTipText("Create a Text Annotation");
        setMaximumSize(new java.awt.Dimension(390, 81));
        setMinimumSize(new java.awt.Dimension(390, 81));
        setPreferredSize(new java.awt.Dimension(390, 81));

        setLayout(null);

    }

}
