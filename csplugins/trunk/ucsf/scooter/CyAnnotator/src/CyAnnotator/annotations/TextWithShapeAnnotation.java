package CyAnnotator.annotations;


import CyAnnotator.ui.ModifyShape;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

/**
 *
 * @author Avinash Thummala
 */

//A TextWithShapeAnnotation class that extends TextAnnotation

public class TextWithShapeAnnotation extends TextAnnotation{

    private int shapeType=1; //0-Rect 1-RoundRect 2-Ovel
    private Color shapeColor=Color.BLUE;
    private boolean fillVal=false;

    public TextWithShapeAnnotation(int x, int y, String text, int compCount, double zoom, Color shapeColor, boolean fillVal, int shapeType){

        super(x, y, text, compCount, zoom);

        this.shapeType=shapeType;
        this.shapeColor=shapeColor;

        this.fillVal=fillVal;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;

        //Setting up anti-aliasing for high quality rendering
        
        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(shapeColor);

        if(!fillVal)
            g2.setStroke(new BasicStroke(2.0f));

        if(shapeType==0){            
            //Rectangle

            if(fillVal)
                g2.fillRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2);
            else
                g2.drawRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2);
        }
        else if(shapeType==1){
            //Rounded Rectangle

            if(fillVal)
                g2.fillRoundRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2, 10, 10);
            else
                g2.drawRoundRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2, 10, 10);
        }
        else if(shapeType==2){
            //Oval

            if(fillVal)
                g2.fillOval(getX()-getTextWidth()/4, getY(), getTextWidth()*3/2, getTextHeight()*3/2);
            else
                g2.drawOval(getX()-getTextWidth()/4, getY(), getTextWidth()*3/2, getTextHeight()*3/2);
        }

        //To draw the arrows along with text

        super.paint(g);

        if(isSelected()){

            g2.setColor(Color.YELLOW);

            g2.setStroke(new BasicStroke(2.0f));

            if(shapeType==0)            
                    g2.drawRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2);
            
            else if(shapeType==1)            
                    g2.drawRoundRect(getX()-getTextHeight()/4, getY(), getTextWidth()+getTextHeight()/2, getTextHeight()*3/2, 10, 10);

            else if(shapeType==2)
                    g2.drawOval(getX()-getTextWidth()/4, getY(), getTextWidth()*3/2, getTextHeight()*3/2);
        }

    }
    
    public void setShapeType(int val){
        shapeType=val;
    }

    public void setFillVal(boolean val){
        fillVal=val;
    }

    public boolean getFillVal(){
        return fillVal;
    }

    public int getShapeType(){
        return shapeType;
    }

    @Override
    public int getTopX(){

        if(shapeType==0 || shapeType==1)
            return getX()-getTextHeight()/4;

        else
            return getX()-getTextWidth()/4;
    }

    @Override
    public int getAnnotationWidth(){

        if(shapeType==0 || shapeType==1)
            return getTextWidth()+getTextHeight()/2;

        else
            return getTextWidth()*3/2;
    }

    @Override
    public int getAnnotationHeight(){
        return getTextHeight()*3/2;
    }

    @Override
    public boolean isTextAnnotation() {
        return false;
    }


    @Override
    public JPopupMenu createPopUp() {

        //Add some more MenuItems to the popup created as part of TextAnnotation

        JPopupMenu popup=super.createPopUp();

        popup.add(new JSeparator());

        JMenuItem modifyShape=new JMenuItem("Change Shape");
        modifyShape.addActionListener(new modifyShapeListener());

        popup.add(modifyShape);

        return popup;
    }

    class modifyShapeListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            ModifyShape mShape=new ModifyShape(TextWithShapeAnnotation.this);

            mShape.setVisible(true);
            mShape.setLocation(TextWithShapeAnnotation.this.getX(), TextWithShapeAnnotation.this.getY());

        }

    }


    @Override
    public boolean isPointInComponent(int pX, int pY){

        int x=getX(), y=getY();

        if(shapeType==0 || shapeType==1){

            if( pX>=(x-getTextHeight()/4) && pX<=(x+getTextWidth()+getTextHeight()/2) && pY>=y && pY<=(y+getTextHeight()*3/2) )
                return true;
            else
                return false;
        }
        else if(shapeType==2){

            if( pX>=(x-getTextWidth()/4) && pX<=(x+getTextWidth()*3/2) && pY>=y && pY<=(y+getTextHeight()*3/2) )
                return true;
            else
                return false;
        }

        return false;

    }

}
