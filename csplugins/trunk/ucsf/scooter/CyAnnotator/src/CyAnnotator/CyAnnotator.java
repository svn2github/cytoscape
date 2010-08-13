//This class is the main cytoscape plugin that handles everything
//Also contains viewport, mouse, mouseMotion and some other listeners

package CyAnnotator;

import CyAnnotator.annotations.ShapeAnnotation;
import CyAnnotator.annotations.TextAnnotation;
import CyAnnotator.ui.CreateTextAnnotation;
import CyAnnotator.ui.CreateTWithShapeAnnotation;
import CyAnnotator.ui.CreateImageAnnotation;
import CyAnnotator.ui.CreateShapeAnnotation;
import CyAnnotator.ui.AnnotationEditor;

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import cytoscape.Cytoscape;
import cytoscape.plugin.CytoscapePlugin;
import cytoscape.ding.DingNetworkView;
import ding.view.*;
import cytoscape.view.cytopanels.*;
import java.util.ArrayList;


public class CyAnnotator extends CytoscapePlugin{

        private AnnotationEditor annotationEditor=null;
        private MyViewportChangeListener myViewportChangeListener=null;

        private static boolean USE_FONT_RESIZE=true, DRAG_VAL=false, annotationEnlarge=false, drawShape=false;

        private ArrayList selectedAnnotations=new ArrayList();
        private double prevZoom=1;

        private CreateShapeAnnotation createShape=null;
        private ShapeAnnotation newShape=null;

	public CyAnnotator() {
            
                annotationEditor=new AnnotationEditor();

                //Add the Annotation editor to the CytoPanel

                Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).add("Annotation Editor", annotationEditor);

                //Also add a CytoPanel change listener, so that when the AnnotationEditor is selected, we initialize everything.

                Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).addCytoPanelListener(new MyCytoPanelListener());
        }

        class MyViewportChangeListener implements ViewportChangeListener{

            public void viewportChanged(int x, int y, double width, double height, double newZoom) {

                //We adjust the font size of all the created annotations if the  if there are changes in viewport

                Component[] annotations=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponents();

                for(int i=0;i<annotations.length;i++){
                    ((TextAnnotation)annotations[i]).adjustFont(newZoom);
                }

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
            }
        }
        
        class MyCytoPanelListener implements CytoPanelListener{

                public void onComponentSelected(int componentIndex){

                       if(componentIndex == Cytoscape.getDesktop().getCytoPanel(SwingConstants.WEST).indexOfComponent(annotationEditor)){

                            //Annotation Editor is selected

                            //Add the mouse, mouseMotion and Key Listeners to the foregroundCanvas

                            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addMouseListener(new ForegroundMouseListener());
                            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addMouseMotionListener(new ForegroundMouseMotionListener());

                            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).addKeyListener(new ForegroundKeyListener());

                            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).setFocusable(true);

                            //Set up the foregroundCanvas as a dropTarget, so that we can drag and drop JPanels, created Annotations onto it.
                            //We also set it up as a DragSource, so that we can drag created Annotations
                           
                            addDropTarget((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)));
                            addDragSource((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)));

                            if(USE_FONT_RESIZE){

                                //The created annotations resize (Their font changes), if we zoom in and out

                                ((ArbitraryGraphicsCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS))).addMouseWheelListener(new MyMouseWheelListener());

                                //We also setup this class as a ViewportChangeListener to the current networkview

                                myViewportChangeListener=new MyViewportChangeListener();
                                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).addViewportChangeListener(myViewportChangeListener);

                            }
                       }
                       else
                       {
                            //Annotation Editor is not selected

                            //Remove everything added above

                            MouseListener[] mouseListeners=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getMouseListeners();
                            
                            for(int i=0;i<mouseListeners.length;i++)
                                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).removeMouseListener(mouseListeners[i]);

                            MouseMotionListener[] mouseMotionListeners=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getMouseMotionListeners();

                            for(int i=0;i<mouseMotionListeners.length;i++)
                                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).removeMouseMotionListener(mouseMotionListeners[i]);

                            KeyListener[] keyListeners=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getKeyListeners();

                            for(int i=0;i<keyListeners.length;i++)
                                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).removeKeyListener(keyListeners[i]);

                            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).setFocusable(false);

                            if(USE_FONT_RESIZE){
                                
                                MouseWheelListener[] mouseWheelListeners=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getMouseWheelListeners();

                                for(int i=0;i<mouseWheelListeners.length;i++)
                                    ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).removeMouseWheelListener(mouseWheelListeners[i]);                                
                                
                                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).removeViewportChangeListener(myViewportChangeListener);                                
                                
                            }
                       }

                }

                public void onStateChange(CytoPanelState newState){}

                public void onComponentAdded(int count){}

                public void onComponentRemoved(int count){}

        }

        public void startDrawShape(CreateShapeAnnotation createShape){

            drawShape=true;
            this.createShape=createShape;

            //createShape will have all the properties associated with the shape to be drawn
            //Create a shapeAnnotattion based on these properties and add it to foregroundCanvas

            newShape= new ShapeAnnotation(createShape.getX(), createShape.getY(), createShape.getShapeType(), createShape.getFillCoor(), createShape.getEdgeColor(), createShape.getEdgeThickness());

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).add(newShape);
        }

        public void addDragSource(ArbitraryGraphicsCanvas foregroundCanvas){

            DragSourceComponent source=new DragSourceComponent(foregroundCanvas);
        }

        class DragSourceComponent extends DragSourceAdapter implements DragGestureListener{

            //Add the foregroundCanvas as DraggableComponent

            DragSource dragSource;

            DragSourceComponent(ArbitraryGraphicsCanvas foregroundCanvas){

                dragSource = new DragSource();
                dragSource.createDefaultDragGestureRecognizer( foregroundCanvas, DnDConstants.ACTION_COPY_OR_MOVE, this);

            }

            public void dragGestureRecognized(DragGestureEvent dge) {

                Component annotation=((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt((int)(dge.getDragOrigin().getX()), (int)(dge.getDragOrigin().getY()));

                //Add the component number of the annotation being dragged in the form of string to transfer information
                
                if(annotation!=null){

                    Transferable t = new StringSelection(new Integer(((TextAnnotation)annotation).getComponentNumber()).toString());
                    dragSource.startDrag (dge, DragSource.DefaultCopyDrop, t, this);
                }
            }    

    }

    public void addDropTarget(ArbitraryGraphicsCanvas foregroundCanvas){

        DropTargetComponent target=new DropTargetComponent(foregroundCanvas);
    }

    public class DropTargetComponent implements DropTargetListener
    {
        //Add the foregroundCanvas as a drop Target

        public DropTargetComponent(ArbitraryGraphicsCanvas foregroundCanvas)
        {
            new DropTarget(foregroundCanvas, this);
        }

        public void dragEnter(DropTargetDragEvent evt){}

        public void dragOver(DropTargetDragEvent evt){}

        public void dragExit(DropTargetEvent evt){}

        public void dropActionChanged(DropTargetDragEvent evt){}

        public void drop(DropTargetDropEvent evt)
        {

            try
            {
                Transferable t = evt.getTransferable();

                if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
                {
                    evt.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                    String s = (String)t.getTransferData(DataFlavor.stringFlavor);

                    evt.getDropTargetContext().dropComplete(true);

                    //Get hold of the transfer information and complete the drop

                    //Based on that information popup appropriate JFrames to create those Annotatons

                    if(s.equals("TextAnnotation"))
                    {

                        CreateTextAnnotation aText=new CreateTextAnnotation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());

                        aText.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        aText.setVisible(true);

                        aText.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
                    }

                    else if(s.equals("TWithShapeAnnotation")){

                        CreateTWithShapeAnnotation aTextWShape=new CreateTWithShapeAnnotation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());

                        aTextWShape.setVisible(true);
                        aTextWShape.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                        aTextWShape.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
                    }

                    else if(s.equals("ImageAnnotation")){

                        CreateImageAnnotation aImage=new CreateImageAnnotation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());

                        aImage.setVisible(true);
                        aImage.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                        aImage.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
                    }

                    else if(s.equals("ShapeAnnotation")){

                        CreateShapeAnnotation aShape=new CreateShapeAnnotation((int)evt.getLocation().getX(),(int)evt.getLocation().getY(), CyAnnotator.this /*It is passed so as to set a boolean value in it to true when we start drawing a shape*/);

                        aShape.setVisible(true);
                        aShape.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
                    }

                    else{

                        TextAnnotation annotation=((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(Integer.parseInt(s)));

                        if(annotation.getDrawArrow()){

                            //The drop has been done to create a new Arrow from an Annotation

                            annotation.setDrawArrow(false);
                            annotation.setArrowDrawn(true);

                            annotation.setArrowPoints((int)evt.getLocation().getX(),(int)evt.getLocation().getY());
                        }

                        else{

                            //The drop has been done to move an annotation to a new location

                            annotation.setLocation((int)evt.getLocation().getX(),(int)evt.getLocation().getY());

                            //This will modify the initial location of this annotation stored in an array in foregroundCanvas
                            //Very important. Without it you won't be able to handle change in viewports
                            
                            ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(annotation.getX(), annotation.getY(), annotation.getComponentNumber());
                        }

                        //Repaint the whole network

                        Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
                    }
                }
                else
                {
                    evt.rejectDrop();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                evt.rejectDrop();
            }

        }

    }

    class MyMouseWheelListener implements MouseWheelListener{

        //To handle zooming in and out
        
        public void mouseWheelMoved(MouseWheelEvent e) {

            int notches = e.getWheelRotation();
            double factor = 1.0;

            // scroll up, zoom in
            if (notches < 0)
                    factor = 1.1;
            else
                    factor = 0.9;

            if(annotationEnlarge){

                //If some annotations are selected

                for(int i=0;i<selectedAnnotations.size();i++)
                    ((TextAnnotation)selectedAnnotations.get(i)).adjustSpecificFont( prevZoom * factor  );

                //In that case only increase the size (Change font in some cases) for those specific annotations
                
                prevZoom*=factor;
            }
            else{

                //Otherwise just increase the scalefactor value stored in InnerCanvas and setViewportChanged to true
                //Then out viewportChangeListener will handle it but adjusting the font of all the Annotations

                synchronized (((DingNetworkView)Cytoscape.getCurrentNetworkView()).getLock()) {
                        ((InnerCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.NETWORK_CANVAS))).setScaleFactor(  ((InnerCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.NETWORK_CANVAS))).getScaleFactor()* factor  );
                }

                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).setViewportChanged(true);
            }

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        }

    }

    //Returns a boolean value, whether this is a Mac Platform or not

    private boolean isMacPlatform() {

            String MAC_OS_ID = "mac";
            String os = System.getProperty("os.name");

            return os.regionMatches(true, 0, MAC_OS_ID, 0, MAC_OS_ID.length());
    }

    class ForegroundMouseListener implements MouseListener{

        public ForegroundMouseListener() {
        }

        
        public void mousePressed(MouseEvent e) {

            TextAnnotation newOne=(TextAnnotation)((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt(e.getX(), e.getY());

            if(newOne!=null){

                //We might drag this annotation
                DRAG_VAL=true;

                //We have right clicked on the Annotation, show a popup
                if( (e.getButton() == MouseEvent.BUTTON3) || ( isMacPlatform()  && e.isControlDown()) )
                    newOne.showChangePopup(e);
            }
            else{

                //Let the InnerCanvas handle this event
                ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mousePressed(e);
            }
            
        }


        public void mouseReleased(MouseEvent e) {

            //We might have finished dragging this Annotation
            DRAG_VAL=false;

            //Let the InnerCanvas handle this event
            ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseReleased(e);
        }

        public void mouseClicked(MouseEvent e) {

            TextAnnotation newOne=(TextAnnotation)((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).getComponentAt(e.getX(), e.getY());

            if(e.getClickCount()==2 && newOne!=null){

                //We have doubled clicked on an Annotation

                annotationEnlarge=true;

                //Add this Annotation to the list of selected Annotations

                selectedAnnotations.add(newOne);

                //This preVZoom value will help in resizing the selected Annotations

                prevZoom=((InnerCanvas)(((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.NETWORK_CANVAS))).getScaleFactor();

                newOne.setTempZoom(prevZoom);
                newOne.setSelected(true);

                //We reuest focus in this window, so that we can move these selected Annotations around using arrow keys

                ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).requestFocusInWindow();

                //Repaint the whole network. The selected annotations will have a yellow outline now

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
            }
            else if(drawShape){

                drawShape=false;

                //We have finished drawing a shapeAnnotation
                //We set the otherCorner of that Annotation
                newShape.setOtherCorner(e.getX(), e.getY());

                newShape.adjustCorners();

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
            }
            else if(newOne==null)
            {
                //We have clicked somewhere else on the network, de-select all the selected Annotations

                annotationEnlarge=false;

                if(!selectedAnnotations.isEmpty()){

                    for(int i=0;i<selectedAnnotations.size();i++)
                        ((TextAnnotation)selectedAnnotations.get(i)).setSelected(false);

                    selectedAnnotations.clear();
                }

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();

                //Let the InnerCanvas handle this event

                ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseClicked(e);
            }

        }

        public void mouseEntered(MouseEvent e) {

            ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseEntered(e);
        }

        public void mouseExited(MouseEvent e) {

            ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseExited(e);
        }

    }

    class ForegroundMouseMotionListener implements MouseMotionListener{

        public ForegroundMouseMotionListener() {
        }

        public void mouseDragged(MouseEvent e) {

            //If we are not dragging an Annotation then let the InnerCanvas handle this event

            if(!DRAG_VAL)
                ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseDragged(e);
        }

        public void mouseMoved(MouseEvent e) {

            if(drawShape){

                //We are drawing a shape

                newShape.setOtherCorner(e.getX(), e.getY());

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();                
            }
            else
                ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).mouseMoved(e);
        }
        
    }

    class ForegroundKeyListener implements KeyListener{

        public void keyPressed(KeyEvent e) {

            int code = e.getKeyCode();

            if(annotationEnlarge && ( (code == KeyEvent.VK_UP) || (code == KeyEvent.VK_DOWN) || (code == KeyEvent.VK_LEFT)|| (code == KeyEvent.VK_RIGHT) ) )
            {
                //Some annotations have been double clicked and selected

                int move=2;

                for(int i=0;i<selectedAnnotations.size();i++){

                    TextAnnotation temp=((TextAnnotation)selectedAnnotations.get(i));

                    int x=temp.getX(), y=temp.getY();

                    if (code == KeyEvent.VK_UP)
                        y-=move;

                    else if (code == KeyEvent.VK_DOWN)
                        y+=move;

                    else if (code == KeyEvent.VK_LEFT)
                        x-=move;

                    else if (code == KeyEvent.VK_RIGHT)
                        x+=move;

                    //Adjust the locations of the selected annotations

                    temp.setLocation(x,y);

                    ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(temp.getX(), temp.getY(), temp.getComponentNumber());

                }

                Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
            }
            else
                ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyPressed(e);
        }

        public void keyReleased(KeyEvent e) {

            ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyReleased(e);
        }

        public void keyTyped(KeyEvent e) {

            ((InnerCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas()).keyTyped(e);
        }
        
    }
        
}
