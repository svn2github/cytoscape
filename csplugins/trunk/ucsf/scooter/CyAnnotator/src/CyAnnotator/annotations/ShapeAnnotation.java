package CyAnnotator.annotations;

import CyAnnotator.annotations.TextAnnotation;
import java.awt.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.*;

/**
 *
 * @author Avinash Thummala
 */
public class ShapeAnnotation extends TextAnnotation{

    private int otherCornerX=0, otherCornerY=0, shapeType=0;
    private Color fillColor, edgeColor;
    private float edgeThickness;
    private boolean cornersAdjusted=false;
    private int shapeWidth=0, shapeHeight=0;

    public ShapeAnnotation(int x, int y, int shapeType, Color fillColor, Color edgeColor, float edgeThickness) {

        super(x, y, "", ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());

        this.shapeType=shapeType;
        this.fillColor=fillColor;
        this.edgeColor=edgeColor;
        this.edgeThickness=edgeThickness;
    }


    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        Point p1;
        int width, height;

        if(!cornersAdjusted){

            //We haven't finalized the shape yet
            //Comes into play when we created a ShapeAnnotation and move the mouse

            p1=getFirstCorner();//To obtain topLeftCorner
            Point p2=getSecondCorner();//To obtain the bottomRightCorner

            width=Math.abs(p2.x-p1.x);
            height=Math.abs(p2.y-p1.y);
        }

        else{
            p1=new Point(getX(), getY());

            width=this.shapeWidth;
            height=this.shapeHeight;
        }
            
        if(shapeType==0){//Rectangle
            
            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillRect( p1.x, p1.y, width, height);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);

            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawRect(p1.x, p1.y, width, height);
                
        }

        else if(shapeType==1){//Rounded Rectangle

            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillRoundRect( p1.x, p1.y, width, height, 5, 5);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);
            
            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawRoundRect(p1.x, p1.y, width, height, 5, 5);

        }

        else if(shapeType==2){//Oval

            if(fillColor!=null){

                g2.setColor(fillColor);
                g2.fillOval( p1.x, p1.y, width, height);
            }

            if(isSelected())
                g2.setColor(Color.YELLOW);
            else
                g2.setColor(edgeColor);
            
            g2.setStroke(new BasicStroke(edgeThickness));
            g2.drawOval(p1.x, p1.y, width, height);
        }

        //Now draw the arrows associated with this annotation

        super.paint(g);

    }


    @Override
    public boolean isShapeAnnotation() {
        return true;
    }


    @Override
    public boolean isTextAnnotation() {
        return false;
    }

    @Override
    public boolean isPointInComponent(int pX, int pY) {

        int x=getX(), y=getY();

        if(pX>=x && pX<=(x+shapeWidth) && pY>=y && pY<=(y+shapeHeight))
            return true;
        else
            return false;
    }

    @Override
    public int getAnnotationWidth() {

        return shapeWidth;
    }


    @Override
    public int getAnnotationHeight() {

        return shapeHeight;
    }

    @Override
    public void adjustFont(double newZoom) {

        float factor=(float)(newZoom/getZoom());

        setZoom(newZoom);

        int diffWidth=Math.abs(shapeWidth-(int)(shapeWidth*factor)), diffHeight=Math.abs(shapeHeight-(int)(shapeHeight*factor));

        shapeWidth=(int)(shapeWidth*factor);
        shapeHeight=(int)(shapeHeight*factor);

        //We resize the annotation all around
        //To do this, we have to change its location

        if(factor>1.0)
            setLocation(getX()-diffWidth/2, getY()-diffHeight/2);
        else
            setLocation(getX()+diffWidth/2, getY()+diffHeight/2);

        ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(this.getX(), this.getY(), this.getComponentNumber());
    }

    @Override
    public void adjustSpecificFont(double newZoom) {

        float factor=(float)(newZoom/getTempZoom());

        setTempZoom(newZoom);

        int diffWidth=Math.abs(shapeWidth-(int)(shapeWidth*factor)), diffHeight=Math.abs(shapeHeight-(int)(shapeHeight*factor));

        shapeWidth=(int)(shapeWidth*factor);
        shapeHeight=(int)(shapeHeight*factor);

        //We resize the annotation all around
        //To do this, we have to change its location

        if(factor>1.0)
            setLocation(getX()-diffWidth/2, getY()-diffHeight/2);
        else
            setLocation(getX()+diffWidth/2, getY()+diffHeight/2);

        ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(this.getX(), this.getY(), this.getComponentNumber());
    }
    
    public void adjustCorners(){

        //Comes into play when the shape has been created completely
        //We finalize the topLeft and bottomRight corners of the shape

        Point p1=getFirstCorner(), p2=getSecondCorner();

        this.setLocation(p1);

        ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).modifyComponentLocation(this.getX(), this.getY(), this.getComponentNumber());

        shapeWidth=Math.abs(p2.x-p1.x);
        shapeHeight=Math.abs(p2.y-p1.y);

        cornersAdjusted=true;
    }

    public Point getFirstCorner(){

        int x=getX(), y=getY();

        if(x<=otherCornerX && y<=otherCornerY)
            return new Point(x,y);

        else if(x>=otherCornerX && y<=otherCornerY)
            return new Point(otherCornerX, y);

        else if(x<=otherCornerX && y>=otherCornerY)
            return new Point(x, otherCornerY);

        else
            return new Point(otherCornerX, otherCornerY);
    }

    public Point getSecondCorner(){

        int x=getX(), y=getY();

        if(x<=otherCornerX && y<=otherCornerY)
            return new Point(otherCornerX,otherCornerY);

        else if(x>=otherCornerX && y<=otherCornerY)
            return new Point(x, otherCornerY);

        else if(x<=otherCornerX && y>=otherCornerY)
            return new Point(otherCornerX, y);

        else
            return new Point(x, y);

    }

    public void setOtherCorner(int x, int y){

        otherCornerX=x;
        otherCornerY=y;
    }

}
