package CyAnnotator.annotations;

import CyAnnotator.annotations.TextAnnotation;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 *
 * @author Avinash Thummala
 */

//ImageAnnotation class that extends TextAnnotation class

public class ImageAnnotation extends TextAnnotation{

    private BufferedImage image;
    private int imageMax=100, imageMin=15;

    private int imageWidth=0, imageHeight=0, minImageWidth=0, minImageHeight=0;
    private BufferedImage resizedImage;

    public ImageAnnotation(int x, int y, BufferedImage image, int compCount, double zoom) {

        super(x, y, "", compCount, zoom);

        this.image=image;

        initializeImageSize();

        resizedImage=resize(image, imageWidth, imageHeight);
    }

    //Fixes up the maximum of the two (Image's width and height) and then according to the aspect ratio modifier the other value

    public void initializeImageSize(){

        imageWidth=image.getWidth();
        imageHeight=image.getHeight();

        if(imageWidth>imageMax || imageWidth>imageMax){

            if(imageWidth>=imageHeight){

                minImageWidth=imageMin*imageWidth/imageHeight;
                minImageHeight=imageMin;

                imageHeight=imageMax*imageHeight/imageWidth;
                imageWidth=imageMax;
            }
            else{

                minImageHeight=imageMin*imageHeight/imageWidth;
                minImageWidth=imageMin;

                imageWidth=imageMax*imageWidth/imageHeight;
                imageHeight=imageMax;
            }
        }
    }

    //Returns a resized high quality BufferedImage

    private static BufferedImage resize(BufferedImage image, int width, int height)
    {
        int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();

        BufferedImage resizedImage = new BufferedImage(width, height, type);

        Graphics2D g = resizedImage.createGraphics();
        
        g.setComposite(AlphaComposite.Src);

        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();

        return resizedImage;
    }

    @Override
    public void paint(Graphics g) {                
        
        Graphics2D g2=(Graphics2D)g;

        g2.setComposite(AlphaComposite.Src);

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2.drawImage(resizedImage, getX(), getY(), null);
        
        super.paint(g);
    }


    @Override
    public void adjustSpecificFont(double newZoom) {

        double factor=newZoom/getTempZoom();

        //The resizedImage must not be larger than the original image and it must not be smaller than some specific dimensions

        if((imageWidth*factor < image.getWidth()) && (imageHeight*factor < image.getHeight()) && (imageWidth*factor >= minImageWidth) && (imageHeight*factor >= minImageHeight) )
        {
            imageWidth=(int)(imageWidth*factor);
            imageHeight=(int)(imageHeight*factor);

            setTempZoom(newZoom);

            resizedImage=resize(image, imageWidth, imageHeight);

            setBounds(getX(), getY(), getTextWidth(), getTextHeight());
        }
    }


    @Override
    public void adjustFont(double newZoom) {

        double factor=newZoom/getZoom();

        //The resizedImage must not be larger than the original image and it must not be smaller than some specific dimensions

        if((imageWidth*factor < image.getWidth()) && (imageHeight*factor < image.getHeight()) && (imageWidth*factor >= minImageWidth) && (imageHeight*factor >= minImageHeight) )
        {
            imageWidth=(int)(imageWidth*factor);
            imageHeight=(int)(imageHeight*factor);

            setZoom(newZoom);

            resizedImage=resize(image, imageWidth, imageHeight);

            setBounds(getX(), getY(), getTextWidth(), getTextHeight());
        }
    }


    @Override
    public int getTextWidth() {
        return imageWidth;
    }

    @Override
    public int getTextHeight() {
        return imageHeight;
    }

    @Override
    public boolean isImageAnnotation() {
        return true;
    }

    @Override
    public boolean isTextAnnotation() {
        return false;
    }

}
