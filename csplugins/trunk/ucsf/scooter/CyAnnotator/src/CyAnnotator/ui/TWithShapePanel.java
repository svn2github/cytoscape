/*
 * TAWithRectangle.java
 *
 * Created on Jun 10, 2010, 2:23:25 PM
 */

package CyAnnotator.ui;

import java.awt.Color;
import java.awt.*;
import java.awt.geom.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

/**
 *
 * @author Avinash Thummala
 */

//This class is a Draggable JPanel with an overidden paint method

public class TWithShapePanel extends javax.swing.JPanel {


    public TWithShapePanel() {
        initComponents();

        //To make this panel a draggable component
        DraggableComponent panelDrag=new DraggableComponent();
    }

    class DraggableComponent extends DragSourceAdapter implements DragGestureListener{

        DragSource dragSource;

        DraggableComponent(){

            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer( TWithShapePanel.this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        }

        public void dragGestureRecognized(DragGestureEvent dge) {

            //Used a basic TextFlavour and transferred the string "TWithShapeAnnotation", which will be verified in the drop method

            Transferable t = new StringSelection("TWithShapeAnnotation");
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

        g2.setStroke(new BasicStroke(2.0f));
        g2.setColor(Color.BLUE);

        g2.drawRoundRect(x-(int)rectangle.getWidth()/8, y-(int)rectangle.getHeight(), (int)rectangle.getWidth()*5/4, (int)rectangle.getHeight()*5/4, 10, 10);

    }


    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder("Bounded by Some Shape"));
        setToolTipText("Create a Text Annotation bounded by some shape");
        setMaximumSize(new java.awt.Dimension(390, 107));
        setMinimumSize(new java.awt.Dimension(390, 107));
        setPreferredSize(new java.awt.Dimension(390, 107));

        setLayout(null);
    }

}
