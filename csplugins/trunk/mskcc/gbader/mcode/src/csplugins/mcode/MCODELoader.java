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
        bg = table.getSelectionBackground();

        this.setImage(loader);

        t = new Thread(this);
        t.start();
    }

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
                    Rectangle bounds = table.getCellRect(selectedRow, 0, false);
                    table.paintImmediately(bounds);
                }
                Thread.sleep(30);
            }
        } catch (Exception e) {}
    }

    public void loaded() {
        run = false;
    }

    public void drawLoader() {
        Font f = g2.getFont();
        FontMetrics fm = g2.getFontMetrics(f);

        //Clear the image
        g2.setColor(bg);
        g2.fillRect(0, 0, loader.getWidth(), loader.getHeight());

        //draw graph as is
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
        int r = 20;
        Ellipse2D circOutter = new Ellipse2D.Double((loader.getWidth() / 2) - r, (loader.getHeight() / 2) - r, 2*r, 2*r);
        Ellipse2D circInner = new Ellipse2D.Double((loader.getWidth() / 2) - r/2, (loader.getHeight() / 2) - r/2, r, r);

        Area circI = new Area(circInner);
        Area circMask = new Area(circOutter);
        circMask.subtract(circI);

        if(degrees >= 360) {
            degrees = 0;
        }
        double degreesLocal = degrees;
        Point2D center = new Point2D.Double(loader.getWidth() / 2, loader.getHeight() / 2);
        Point2D pointOnCircumference1 = new Point2D.Double();
        Point2D pointOnCircumference2 = new Point2D.Double();
        g2.setColor(Color.CYAN);
        double fader = 255;
        r = r+10;
        while (((int) fader) > 0) {
            double theta1 = 2 * Math.PI * (degreesLocal/360);
            double theta2 = 2 * Math.PI * ((degreesLocal - 5.0) / 360);
            pointOnCircumference1.setLocation(((r * Math.cos(theta1))) + center.getX(), ((r * Math.sin(theta1))) + center.getY());
            pointOnCircumference2.setLocation(((r * Math.cos(theta2))) + center.getX(), ((r * Math.sin(theta2))) + center.getY());
            g2.setColor(new Color(g2.getColor().getRed(), g2.getColor().getGreen(), g2.getColor().getBlue(), (int) Math.rint(fader)));
            int[] xs = {(int) center.getX(), (int) center.getX(), (int) pointOnCircumference1.getX(), (int) pointOnCircumference2.getX()};
            int[] ys = {(int) center.getY(), (int) center.getY(), (int) pointOnCircumference1.getY(), (int) pointOnCircumference2.getY()};
            Polygon marker = new Polygon(xs, ys, 4);
            Area markerMask = new Area(marker);
            markerMask.intersect(circMask);
            g2.fill(markerMask);
            fader = fader - (fader / 20);
            degreesLocal -= 2;
        }
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

            if (progress > 50 && progress <= 94) {
                progress++;
            } else if (progress > 95) {
                progress++;
            }

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
