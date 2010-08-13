package CyAnnotator.annotations;

import CyAnnotator.ui.ModifyColor;
import CyAnnotator.ui.ModifyFont;
import CyAnnotator.ui.ModifyText;
import java.awt.*;
import java.awt.event.*;
import cytoscape.Cytoscape;
import cytoscape.ding.DingNetworkView;
import ding.view.*;
import javax.swing.*;
import java.util.*;

/**
 *
 * @author Avinash Thummala
 */

//A BasicTextAnnotation Class

public class TextAnnotation extends Component{

    private String text;
    private int componentNumber=0;
    private double zoom, tempZoom;
    
    private int initialFontSize=14;
    private Font font=new Font(Font.SERIF, Font.PLAIN,initialFontSize);

    private Color color=Color.BLACK;

    private boolean drawArrow=false, arrowDrawn=false, selected=false;
    private ArrayList arrowEndPoints;

    public TextAnnotation(){
        
    }

    public TextAnnotation(int x, int y, String text, int compCount, double zoom){
        
        this.text=text;
        this.componentNumber=compCount;
        this.zoom=zoom;
        this.setLocation(x, y);
    }

    //Verification methods

    public boolean isImageAnnotation(){

        return false;
    }

    public boolean isShapeAnnotation(){

        return false;
    }

    public boolean isTextAnnotation(){
        return true;
    }

    public boolean isSelected(){
        return selected;
    }

    public boolean isPointInComponent(int pX, int pY){

        int x=getX(), y=getY();

        if(pX>=x && pX<=(x+getTextWidth()) && pY>=y && pY<=(y+getTextHeight()) )
            return true;
        else
            return false;

    }

    //Get Methods

    public int getZone(int x, int y){

        if(isPointInComponent(x, y))
            return 0;

        int midX=getTopX()+getAnnotationWidth()/2, midY=getTopY();

        if(x<=midX){

            if(y<=midY)
                return 3;

            else if(y<=midY+getAnnotationHeight())
                return 4;

            else
                return 5;
        }
        else{

            if(y<=midY)
                return 2;

            else if(y<=midY+getAnnotationHeight())
                return 1;

            else
                return 6;
        }

    }

    public int getQuadrant(Point p1, Point p2){

        if(p2.x >= p1.x){

            if(p2.y<=p1.y)
                return 1;
            else
                return 4;
        }
        else{

            if(p2.y<=p1.y)
                return 2;
            else
                return 3;
        }

    }

    public Point getArrowStartPoint(TextAnnotation temp){

        int x=0, y=0, zone=getZone(temp.getX(), temp.getY());

        if(zone==1){
            x=getTopX()+getAnnotationWidth();
            y=getTopY()+getAnnotationHeight()/2;
        }

        else if(zone==2 || zone==3){
            x=getTopX()+getAnnotationWidth()/2;
            y=getTopY();
        }

        else if(zone==4){
            x=getTopX();
            y=getTopY()+getAnnotationHeight()/2;
        }

        else{
            x=getTopX()+getAnnotationWidth()/2;
            y=getTopY()+getAnnotationHeight();
        }

        return new Point(x,y);
    }

    public int getTopX(){
        return getX();
    }

    public int getTopY(){
        return getY();
    }

    public int getAnnotationWidth(){
        return getTextWidth();
    }

    public int getAnnotationHeight(){
        return getTextHeight();
    }

    public boolean getArrowDrawn(){
        return arrowDrawn;
    }

    public double getZoom(){
        return zoom;
    }

    public boolean getDrawArrow(){
        return drawArrow;
    }
    
    public int getComponentNumber(){
        return componentNumber;
    }    
    
    @Override
    public Font getFont(){
        return font;
    }

    public String getText(){
        return text;
    }    
    
    public double getTempZoom(){
        return tempZoom;
    }
    
    public int getTextWidth(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
        return fontMetrics.stringWidth(text);
    }

    public int getTextHeight(){

        FontMetrics fontMetrics=this.getGraphics().getFontMetrics(font);
        return fontMetrics.getHeight();
    }

    @Override
    public Component getComponentAt(int x, int y) {

        if(isPointInComponent(x,y))
            return this;
        else
            return null;

    }

    //Set methods

    public void setArrowPoints(int pX, int pY){

        int zone=getZone(pX,pY);

        if(zone==0){
            arrowDrawn=false;
            return;
        }

        else{

            if(arrowEndPoints==null)
                arrowEndPoints=new ArrayList();

            //The ArrowEndPoints are also set up as TextAnnotations of null size.
            //They have been implemented this way, so as to handle the change in viewports

            TextAnnotation arrowEndPoint=new TextAnnotation(pX, pY,"",((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount(), ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getZoom());
            arrowEndPoint.setSize(0, 0);

            ((ArbitraryGraphicsCanvas)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS)).add(arrowEndPoint);

            arrowEndPoints.add(arrowEndPoint);
        }
    }

    public void setDrawArrow(boolean val){
        drawArrow=val;
    }

    public void setArrowDrawn(boolean val){
        arrowDrawn=val;
    }

    public void setZoom(double zoom){
        this.zoom=zoom;
    }

    public void setSelected(boolean val){
        this.selected=val;
    }

    public void setComponentNumber(int val){
        componentNumber=val;
    }

    @Override
    public void setFont(Font newFont){
        font=newFont;
    }

    public void setColor(Color newColor){
        this.color=newColor;
    }

    public void setText(String newText){
        this.text=newText;
    }

    public void setTempZoom(double zoom){
        this.tempZoom=zoom;
    }

    @Override
    public void paint(Graphics g) {

        Graphics2D g2=(Graphics2D)g;

        //Setting up Anti-aliasing for high quality rendering

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        if(arrowDrawn){

            //For any annotation that points to some locations

            g2.setColor(Color.BLACK);

            g2.setStroke( new BasicStroke(2.0f) );

            for(int i=0;i<arrowEndPoints.size();i++){

                Point p=getArrowStartPoint((TextAnnotation)arrowEndPoints.get(i));
                g2.drawLine(p.x, p.y, ((TextAnnotation)arrowEndPoints.get(i)).getX(), ((TextAnnotation)arrowEndPoints.get(i)).getY());

                drawArrow(g2, p, new Point(((TextAnnotation)arrowEndPoints.get(i)).getX(), ((TextAnnotation)arrowEndPoints.get(i)).getY()) );
            }

        }

        if(!isImageAnnotation() && !isShapeAnnotation()){

            //For Annotations with text

            g2.setColor(color);
            g2.setFont(font);

            g2.drawChars(getText().toCharArray(), 0, getText().length(), getX(), getY()+getTextHeight());

        }

        if(isSelected() && ( isTextAnnotation() || isImageAnnotation() ) ){

            //Selected Annotations will have a yellow border

            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(2.0f));

            if(isImageAnnotation())
                g2.drawRect(getTopX(), getTopY(), getAnnotationWidth(), getAnnotationHeight());
            else
                g2.drawRect(getTopX(), getTopY(), getAnnotationWidth(), (int)(getAnnotationHeight()*1.5));
        }
        
    }  

    public void drawArrow(Graphics2D g, Point p1, Point p2){

        double angle=Math.atan(((double)(p1.y-p2.y))/((double)(p2.x-p1.x)));
        int quad=getQuadrant(p1, p2), arrowLength=5;

        if(angle >=0 ){

            if(angle<=Math.PI/4){

                double m1=Math.tan(angle + 3*Math.PI/4);
                double m2=Math.tan(angle + Math.PI/4);

                if(quad==1){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );
                }
                else if(quad==3){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                }
            }

            else if(angle<=Math.PI/2){

                double m1=Math.tan(angle - Math.PI/4);
                double m2=Math.tan(angle + Math.PI/4);

                if(quad==1){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                }
                else if(quad==3){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );
                }
            }

        }
        else{

            if(angle>=-1*Math.PI/4){

                double m1=Math.tan(3*Math.PI/4 + angle);
                double m2=Math.tan(Math.PI/4 + angle);

                if(quad==4){

                    double x2=p2.x-arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                }

                else if(quad==2){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x+arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                }
            }

            else{

                double m1=Math.tan(3*Math.PI/4 + angle);
                double m2=Math.tan(5*Math.PI/4 - angle);

                if(quad==4){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y-Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y-Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );
                }
                
                else if(quad==2){

                    double x2=p2.x+arrowLength/(Math.sqrt(1+m1*m1));
                    double y2=p2.y+Math.abs(arrowLength*m1/(Math.sqrt(1+m1*m1)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );

                    x2=p2.x-arrowLength/(Math.sqrt(1+m2*m2));
                    y2=p2.y+Math.abs(arrowLength*m2/(Math.sqrt(1+m2*m2)));

                    g.drawLine(p2.x, p2.y, (int)x2, (int)y2 );
                }
                
            }

        }

    }

    public void adjustSpecificFont(double newZoom){

        font=font.deriveFont(((float)(newZoom/tempZoom))*font.getSize2D());
        tempZoom=newZoom;

        setBounds(getX(), getY(), getTextWidth(), getTextHeight());
    }

    public void adjustFont(double newZoom){
        
        font=font.deriveFont(((float)(newZoom/zoom))*font.getSize2D());
        zoom=newZoom;

        setBounds(getX(), getY(), getTextWidth(), getTextHeight());
    }

    public JPopupMenu createPopUp(){

        JPopupMenu popup=new JPopupMenu();

        if(!isImageAnnotation() && !isShapeAnnotation()){

            JMenuItem modifyText=new JMenuItem("Modify Text");
            modifyText.addActionListener(new modifyTextListener());

            JMenuItem modifyFont=new JMenuItem("Modify Font");
            modifyFont.addActionListener(new modifyFontListener());

            JMenuItem modifyColor=new JMenuItem("Modify Color");
            modifyColor.addActionListener(new modifyColorListener());
            
            popup.add(modifyText);
            popup.add(modifyFont);
            popup.add(modifyColor);

            popup.add(new JSeparator());

        }

        JMenuItem removeAnnotation=new JMenuItem("Remove Annotation");
        removeAnnotation.addActionListener(new removeAnnotationListener());

        JMenuItem addArrow=new JMenuItem("Add Arrow");

        addArrow.addActionListener( new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                TextAnnotation.this.setDrawArrow(true);
            }

        });

        popup.add(removeAnnotation);
        popup.add(addArrow);

        return popup;
    }

    public void showChangePopup(MouseEvent e){

        createPopUp().show(e.getComponent(), e.getX(), e.getY());
    }

    class modifyTextListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            ModifyText mText=new ModifyText(TextAnnotation.this);

            mText.setVisible(true);
            mText.setLocation(TextAnnotation.this.getX(), TextAnnotation.this.getY());

        }

    }

    class modifyFontListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            ModifyFont mFont=new ModifyFont(TextAnnotation.this);

            mFont.setVisible(true);
            mFont.setLocation(TextAnnotation.this.getX(), TextAnnotation.this.getY());

        }
    }

    class modifyColorListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            ModifyColor mColor=new ModifyColor(TextAnnotation.this);

            mColor.setVisible(true);
            mColor.setLocation(TextAnnotation.this.getX(), TextAnnotation.this.getY());

        }

    }

    class removeAnnotationListener implements ActionListener{

        public void actionPerformed(ActionEvent e){

            //When an Annotation is removed we have to adjust the componentNumbers of the anotations added
            //after this Annotation

            int remPos=TextAnnotation.this.getComponentNumber();
            int num=((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponentCount();

            for(int i=remPos+1;i<num;i++)
                ((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(i)).setComponentNumber(i-1);

            ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove(TextAnnotation.this);

            if(TextAnnotation.this.getArrowDrawn()){

                for(int temp=0; temp<TextAnnotation.this.arrowEndPoints.size(); temp++){

                    remPos=((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(temp)).getComponentNumber()-1;
                    num--;

                    for(int i=remPos+1;i<num;i++)
                        ((TextAnnotation)((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).getComponent(i)).setComponentNumber(i-1);

                    ((DingNetworkView)Cytoscape.getCurrentNetworkView()).getCanvas(DGraphView.Canvas.FOREGROUND_CANVAS).remove((TextAnnotation)TextAnnotation.this.arrowEndPoints.get(temp));
                }

            }

            Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(Cytoscape.getCurrentNetworkView()).repaint();
        }

    }

}
