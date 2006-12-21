package csplugins.mcode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: vukpavlovic
 * Date: Dec 17, 2006
 * Time: 12:33:12 PM
 * TODO: MAKE MORE GENERAL FOR REUSABILITY
 */
public class MCODELoader extends ImageIcon implements Runnable {
    JTable table;
    int selectedRow;
    ImageIcon graphImage;
    BufferedImage loader;
    Graphics2D g2;
    Color bg;
    int bgAlpha;
    int degrees;

    int progress;
    String process;

    Thread t;
    boolean run;

    MCODELoader (JTable table, int width, int height) {
        this.table = table;

        loader = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g2 = (Graphics2D) loader.getGraphics();
        g2.setFont(new Font("Arial", Font.PLAIN, 8));
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        bg = table.getSelectionBackground();

        this.setImage(loader);

        t = new Thread(this);
        t.start();
    }

    /**
     * TODO: write this
     * @param selectedRow
     */
    public void setLoader(int selectedRow) {
        this.selectedRow = selectedRow;
        graphImage = (ImageIcon) table.getValueAt(selectedRow, 0);
        bgAlpha = 0;
        degrees = 0;
        progress = 0;
        process = "Waiting";

        drawLoader();
        
        run = true;
    }

    public void run() {
        try {
            while (true) {
                if (run) {
                    drawLoader();
                    //In order to make the loader efficient, only the one cell is updated
                    Rectangle bounds = table.getCellRect(selectedRow, 0, false);
                    //Since the table consolidates paint updates, the animation would not show up unless
                    //we implicitly force it to repaint
                    table.paintImmediately(bounds);
                }
                //This sleep time generates a ~30 fps animation
                Thread.sleep(30);
            }
        } catch (Exception e) {}
    }

    /**
     * Sets the pivital boolean to false to stop the drawing process
     */
    public void loaded() {
        run = false;
    }

    /**
     * Initially, fades the graph into the background color, draws the Loading string and an animated disk to indicate
     * responsiveness.  If the user has stopped adjusting the slider, the processes start and a progress bar is drawn.
     */
    public void drawLoader() {
        //Get font info for centering
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);

        //Clear the image
        g2.setColor(bg);
        g2.fillRect(0, 0, loader.getWidth(), loader.getHeight());

        //draw graph as is, centered in the table cell
        int graphX = Math.round((((float) loader.getWidth() - graphImage.getIconWidth()) / 2));
        int graphY = Math.round((((float) loader.getHeight() - graphImage.getIconHeight()) / 2));

        g2.drawImage(graphImage.getImage(), graphX, graphY, this.getImageObserver());

        //Fade background on top
        if (bgAlpha < 220) {
            bgAlpha += ((220 - bgAlpha) / 4);
        }
        Color bg2 = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), bgAlpha);
        g2.setColor(bg2);
        g2.fillRect(0, 0, loader.getWidth(), loader.getHeight());

        //Loading animation
        int r = 20;//radius of the disk
        //To draw the animated rotating disk, we must create the Mask, or drawable area, in which we will
        //rotate a triangular polygon around a common center
        //the inner circle is subracted from the outter to create the disk area
        Ellipse2D circOutter = new Ellipse2D.Double((loader.getWidth() / 2) - r, (loader.getHeight() / 2) - r, 2*r, 2*r);
        Ellipse2D circInner = new Ellipse2D.Double((loader.getWidth() / 2) - r/2, (loader.getHeight() / 2) - r/2, r, r);

        Area circI = new Area(circInner);
        Area circMask = new Area(circOutter);
        circMask.subtract(circI);
        //with consecutive frames the disk must move around the circle
        //this variable keeps track of the overall disk
        if(degrees >= 360) {
            degrees = 0;
        }
        //To produce the fading effect we must draw consecutively more transparent polygons around the disk
        //this vriable keeps track of the fading poligons
        //at the start of each frame we want to first draw the least transparent polygon in the same position on the rotating kisk
        double degreesLocal = degrees;
        //Here we find the center of the table cell
        Point2D center = new Point2D.Double(loader.getWidth() / 2, loader.getHeight() / 2);
        //these two points will represent the outer ends of the polygon, spinning around the center
        Point2D pointOnCircumference1 = new Point2D.Double();
        Point2D pointOnCircumference2 = new Point2D.Double();

        //these are the colors of the spinning disk
        Color lead = Color.WHITE;
        Color trail = Color.CYAN;
        //In order for the leading portion of the rotating disk to be one color and the trailing portion another we use
        //this weighting variable to slowly change the weighting of the two colors in finding the average between the two
        double progression = 1.0;
        //this is the transparency of the initial polygon which is exponantially decremented to fade away the polygons
        double fader = 255;

        //In order for the polygons to cover the disk entirely, the outter polygon points must circle in an orbit that is further
        //than the disk itself, otherwise we would get a flat line between the two points and the resulting circle would not be smooth
        r = r + 10;
        //We will only draw the polygons as long as they are visible
        while (((int) fader) > 0) {
            //these are the radians of rotation of each of the polygon edges
            double theta1 = 2 * Math.PI * (degreesLocal/360);
            double theta2 = 2 * Math.PI * ((degreesLocal - 6.0) / 360);//offset by 5 degrees
            //the outter spinning point locations can be determined by the circle equations here
            pointOnCircumference1.setLocation(((r * Math.cos(theta1))) + center.getX(), ((r * Math.sin(theta1))) + center.getY());
            pointOnCircumference2.setLocation(((r * Math.cos(theta2))) + center.getX(), ((r * Math.sin(theta2))) + center.getY());
            //this is the color with the decrementing alpha
            g2.setColor(new Color(
                    (int) ((lead.getRed() * progression) + (trail.getRed() * (1.0 - progression))), 
                    (int) ((lead.getGreen() * progression) + (trail.getGreen() * (1.0 - progression))),
                    (int) ((lead.getBlue() * progression) + (trail.getBlue() * (1.0 - progression))),
                    (int) Math.rint(fader)));
            if ((int) (progression*100) > 0) {
                progression = progression - (progression / 40);
            }
            //polygons need arrays of x and y values to be drawn, so these are triangles consisting of a center point and two outter points
            int[] xs = {(int) center.getX(), (int) pointOnCircumference1.getX(), (int) pointOnCircumference2.getX()};
            int[] ys = {(int) center.getY(), (int) pointOnCircumference1.getY(), (int) pointOnCircumference2.getY()};
            //the triangle is intersected with the disk first
            Polygon marker = new Polygon(xs, ys, 3);
            Area markerMask = new Area(marker);
            markerMask.intersect(circMask);
            //and drawn
            g2.fill(markerMask);
            //the alpha of the marker polygon is exponentially decreased
            fader = fader - (fader / 20);
            //the successive, more transparent marker is offset by 2 degrees backward to give the appearance of motion blur
            degreesLocal -= 2;
        }
        //The successive disk is rotated 15 degrees for an optimal speed given the fps
        degrees += 15;

        //Loading text
        String loading = "LOADING";
        //White outline
        g2.setColor(Color.WHITE);
        g2.drawString(loading, (loader.getWidth() / 2) - (fm.stringWidth(loading) / 2) - 1, (loader.getHeight() / 2) + (8 / 2) - 1);
        g2.drawString(loading, (loader.getWidth() / 2) - (fm.stringWidth(loading) / 2) - 1, (loader.getHeight() / 2) + (8 / 2) + 1);
        g2.drawString(loading, (loader.getWidth() / 2) - (fm.stringWidth(loading) / 2) + 1, (loader.getHeight() / 2) + (8 / 2) - 1);
        g2.drawString(loading, (loader.getWidth() / 2) - (fm.stringWidth(loading) / 2) + 1, (loader.getHeight() / 2) + (8 / 2) + 1);
        //Red text
        g2.setColor(Color.RED);
        g2.drawString(loading, (loader.getWidth() / 2) - (fm.stringWidth(loading) / 2), (loader.getHeight() / 2) + (8 / 2));

        //Draw progress bar
        if (!process.equals("Waiting")) {
            //Process
            g2.setColor(Color.BLACK);
            g2.drawString(process, 10, loader.getHeight()-2);

            g2.setColor(Color.BLUE);
            g2.fillRect(10, loader.getHeight()-20, (int) ((((double) progress) / 100)*(loader.getWidth()-20)), 10);//progress fill

            g2.setColor(new Color(0,0,0,50));
            g2.drawRect(10, loader.getHeight()-20, loader.getWidth()-20, 10);//outline

            //Progress
            g2.drawString(process, 10, loader.getHeight()-2);
            String progressDisplay = progress + "%";

            g2.setColor(Color.BLACK);
            g2.drawString(progressDisplay, (loader.getWidth() / 2) - (fm.stringWidth(progressDisplay) / 2) + 1, loader.getHeight() - 11);

            g2.setColor(Color.WHITE);
            g2.drawString(progressDisplay, (loader.getWidth() / 2) - (fm.stringWidth(progressDisplay) / 2), loader.getHeight() - 12);
        }
    }

    public void setProgress(int progress, String process) {
        this.progress = progress;
        this.process = process;
    }
}
