/*
 * ImagePanel.java
 *
 * Created on Aug 4, 2010, 11:05:52 AM
 */

package CyAnnotator.ui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author Avinash Thummala
 */

//This class is a Draggable JPanel with an overidden paint method

public class ImagePanel extends javax.swing.JPanel {
   
    public ImagePanel() {
        
        initComponents();

        //To make this panel a draggable component
        DraggableComponent panelDrag=new DraggableComponent();
    }

    class DraggableComponent extends DragSourceAdapter implements DragGestureListener{

        DragSource dragSource;

        DraggableComponent(){

            dragSource = new DragSource();
            dragSource.createDefaultDragGestureRecognizer( ImagePanel.this, DnDConstants.ACTION_COPY_OR_MOVE, this);

        }

        public void dragGestureRecognized(DragGestureEvent dge) {

            //Used a basic TextFlavour and transferred the string "ImageAnnotation", which will be verified in the drop method

            Transferable t = new StringSelection("ImageAnnotation");
            dragSource.startDrag (dge, DragSource.DefaultCopyDrop, t, this);
        }

    }

    //Overidden paint method for this JPanel in which we draw an Image.

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2=(Graphics2D)g;

        BufferedImage image=null;

        try{
            image= ImageIO.read( getClass().getResource("Image.JPG") );
        }
        catch(Exception ex){

            System.out.println("Unable to load Image in Panel");
        }        

        int x=(int)(this.getWidth()-image.getWidth())/2;
        int y=(int)(this.getHeight()-image.getHeight())/2;

        g2.drawImage(image, null, x, y);
    }

    private void initComponents() {

        setBorder(javax.swing.BorderFactory.createTitledBorder("Image"));
        setToolTipText("Create an Image Annotation");
        
        setMaximumSize(new java.awt.Dimension(391, 110));
        setMinimumSize(new java.awt.Dimension(391, 110));
        setPreferredSize(new java.awt.Dimension(391, 110));

        setLayout(null);
    }

}
