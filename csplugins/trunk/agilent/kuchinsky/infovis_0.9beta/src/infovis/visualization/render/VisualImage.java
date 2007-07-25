/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.visualization.ItemRenderer;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.log4j.Logger;

/**
 * Class VisualImage
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class VisualImage extends AbstractVisualColumn 
    implements Runnable {
    public static final String VISUAL_URL = "url";
    protected Column urlColumn;
    protected LinkedList iconBeingLoaded;
    protected Hashtable iconLoaded;
    protected Thread thread;
    protected int iconSize = 128;
    protected MediaTracker tracker;
    protected boolean synchronous;
    private static final Logger logger = Logger.getLogger(VisualImage.class);

    public static VisualImage get(Visualization vis) {
        return (VisualImage) findNamed(VISUAL_URL, vis.getItemRenderer());
    }

    public VisualImage() {
        super(VISUAL_URL);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto, Visualization vis) {
        super.instantiateChildren(proto, vis);
        urlColumn = vis.getTable().getColumn(VISUAL_URL);
        iconBeingLoaded = new LinkedList();
        iconLoaded = new Hashtable();
        return this;
    }    

    public Column getColumn() {
        return urlColumn;
    }

    public void setColumn(Column column) {
        if (column == urlColumn)
            return;
        super.setColumn(column);
        this.urlColumn = column;
        thread = null;
        iconBeingLoaded.clear();
        iconLoaded.clear();
        invalidate();
    }
    
    public void install(Graphics2D graphics) {
        super.install(graphics);
        if (urlColumn == null || graphics == null) {
            return;
        }
        Component comp = getVisualization().getParent();
        if (comp == null) {
            synchronous = true; // rendering on a windowless visualization
            comp = new Component() { }; 
        }
        tracker = new MediaTracker(comp);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    public String getURLAt(int row) {
        return urlColumn.getValueAt(row);
    }
    
    public Image getIcon(String url) {
        return (Image)iconLoaded.get(url);
    }
    
    public void putIcon(String url, Image img) {
        iconLoaded.put(url, img);        
    }
    
    public boolean isImage(String url) {
        return url.endsWith(".png") || url.endsWith("PNG")
                || url.endsWith(".jpg") || url.endsWith(".JPG")
                || url.endsWith(".jpeg") || url.endsWith(".JPEG")
                || url.endsWith(".gif") || url.endsWith(".GIF");
    }

    public Image loadIconAt(int row) {
        if (urlColumn == null) {
            return null;
        }
        synchronized (iconBeingLoaded) {
            String url = getURLAt(row);
            Image img = getIcon(url);
            if (img != null) {
                return img;
            }
            if (!isImage(url)) {
                putIcon(url, NULL_IMAGE);
                return NULL_IMAGE;
            }
            iconBeingLoaded.remove(url);
            iconBeingLoaded.addFirst(url);
            if (synchronous) {
                run();
                return getIcon(url);
            }
            else if (! iconBeingLoaded.isEmpty()
                    && thread == null) {
                thread = new Thread(this);
                thread.start();
                //logger.info("Creating image loader thread");
            }
        }
        return null;
    }
    
    public Image completeLoad(Image img, String url) {
        tracker.addImage(img, 0);
        try {
            tracker.waitForID(0, 0);
        }
        catch(InterruptedException e) {
            logger.error("Interrupted while loading image "+url, e);
        }
        int status = tracker.statusID(0, false);
        tracker.removeImage(img);
        switch (status) {
        case MediaTracker.LOADING:
            logger.error("media tracker still loading image after requested to wait until finished "+url);
            break;
        case MediaTracker.COMPLETE:
            break;

        case MediaTracker.ABORTED:
            logger.error("media tracker aborted image load "+url);
            img = null;
            break;

        case MediaTracker.ERRORED:
            logger.error("media tracker errored image load "
                    +url
                    +"("+img.getWidth(null)+","+img.getHeight(null)+")");
            img = null;
            break;
        }
        return img;
    }
    
    public Image computeIcon(Image img, String url) {
        int iw = img.getWidth(null);
        int ih = img.getHeight(null);
        if (iw <= 0 || iw <= 0) {
            logger.error("Invalid size for image "+url);
            return null;
        }
        double sx = (double)iconSize / iw;
        double sy = (double)iconSize / ih;

        if (sx < sy) {
            sy = sx;
            iw = iconSize;
            ih = (int) (ih * sy);
        } else {
            sx = sy;
            iw = (int) (iw * sx);
            ih = iconSize;
        }
        if (sx < 1) {
            //System.out.println("Rescaling by "+sx+" to "+iw+","+ih);
            Image scaled = img.getScaledInstance(iw, ih, Image.SCALE_FAST);
            scaled = completeLoad(scaled, url);
            img.flush();
            img = scaled;
        }
        System.gc();
        return img;
    }
    
    public Image loadResizeImage(String url) {
        Image img = Toolkit.getDefaultToolkit().createImage(url);
        img = completeLoad(img, url);
        if (img == null) {
            return null;
        }
        else {
            return computeIcon(img, url);
        }
    }
    
    public void run() {
        // when we want to interrupt, we set thread to null
        while (synchronous || thread != null) {
            String url;
            synchronized (iconBeingLoaded) {
                if (iconBeingLoaded.isEmpty()) {
                    break;
                }
                url = (String) iconBeingLoaded.get(0);
                iconBeingLoaded.remove(0);
            }
            if (iconLoaded.contains(url)) {
                continue;
            }
            Image img = loadResizeImage(url);
            if (img == null) {
                putIcon(url, NULL_IMAGE);
            }
            else {
                putIcon(url, img);
                repaint();
            }
        }
        thread = null;
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        Rectangle2D bounds = shape.getBounds2D();
        Image icon = loadIconAt(row);
        if (bounds.getWidth() >= 3 && bounds.getWidth() >= 3
                && icon != null) {
            int iw = icon.getWidth(null);
            int ih = icon.getHeight(null);
            if (iw != -1 && ih != -1 && !(iw == 1 && ih == 1)) {
                double sx = bounds.getWidth() / iw;
                double sy = bounds.getHeight() / ih;

                if (sx < sy) {
                    sy = sx;
                    iw = (int) (bounds.getWidth());
                    ih = (int) (ih * sy);
                } else {
                    sx = sy;
                    iw = (int) (iw * sx);
                    ih = (int) (bounds.getHeight());
                }

                //System.out.println("Painting " + row);
                int x = (int) (bounds.getX() + (bounds.getWidth() - iw) / 2);
                int y = (int) (bounds.getY() + (bounds.getHeight() - ih) / 2);
                graphics
                        .drawImage(icon, x, y, x + iw, y + ih, 0, 0,
                                icon.getWidth(null), icon
                                        .getHeight(null), null);
            }
        }
        super.paint(graphics, row, shape);
    }
    
    
    public boolean isSynchronous() {
        return synchronous;
    }
    
    public void setSynchronous(boolean synchronous) {
        if (this.synchronous == synchronous) return;
        this.synchronous = synchronous;
        invalidate();
    }

    public static final Image NULL_IMAGE = new Image() {
        public void flush() {
        }

        public Graphics getGraphics() {
            return null;
        }

        public int getHeight(ImageObserver observer) {
            return 0;
        }

        public Object getProperty(String name, ImageObserver observer) {
            return null;
        }

        public ImageProducer getSource() {
            return null;
        }

        public int getWidth(ImageObserver observer) {
            return 0;
        }
    };

    public int getIconSize() {
        return iconSize;
    }
    public void setIconSize(int iconSize) {
        if (this.iconSize == iconSize) return;
        this.iconSize = iconSize;
        iconLoaded.clear();
        invalidate();
        
    }
}