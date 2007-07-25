/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.utils;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Pool of rectangle that can be allocated or freed.
 * 
 * <p>Improves speed by avoiding garbage collection of
 * rectangles that are used extensively by the toolkit.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class RectPool {
    private static RectPool instance = new RectPool();
    protected ArrayList freeList;
    protected boolean enabled = true;
    private transient HashSet debug;
    private transient int numAllocations;
    private transient int numFree;
    private transient int maxFree;
    
    /**
     * Returns the current instance of the Rectangle pool.
     * @return the current instance of the Rectangle pool.
     */
    public static RectPool getInstance() {
        return instance;
    }
    
    /**
     * Returns an available rectangle.
     * @return an available rectangle.
     */
    public static Rectangle2D.Float allocateRect() {
        return getInstance().allocate();
    }
    
    /**
     * Returns the copy of a specified rectangle.
     * @param rect the rectangle to copy
     * @return the copy of a specified rectangle.
     */
    public static Rectangle2D.Float copyRect(Rectangle2D rect) {
        return getInstance().copy(rect);
    }
    /**
     * Frees a shape, adding it back to the pool
     * if it is a rectangle.
     * @param r the shape
     */
    public static void freeRect(Shape r) {
        getInstance().free(r);
    }
    
    /**
     * Creates a rectangle pool.
     */
    public RectPool() {
        freeList = new ArrayList();
        assert(setDebugging(true));
    }

    /**
     * Returns an available rectangle from the pool.
     * @return an available rectangle from the pool.
     */
    public Rectangle2D.Float allocate() {
        Rectangle2D.Float r;
        if (freeList.isEmpty()) {
            r = new Rectangle2D.Float();
            numAllocations++;
        }
        else {
            int last = freeList.size()-1;
            r = (Rectangle2D.Float)freeList.get(last);
            freeList.remove(last);
            if (debug != null) {
                debug.remove(r);
            }
        }
        return r;
    }
    
    /**
     * Returns the copy of a specified rectangle.
     * @param rect the rectangle to copy
     * @return the copy of a specified rectangle.
     */
    public Rectangle2D.Float copy(Rectangle2D rect) {
        Rectangle2D.Float r = allocate();
        r.setRect(rect);
        return r;
    }

    /**
     * Frees a shape, adding it back to the pool
     * if it is a rectangle.
     * @param r the shape
     */
    public void free(Shape r) {
        if (enabled
                && r != null
                && r instanceof Rectangle2D.Float) {
            if (debug != null) {
                if (debug.contains(r)) {
                    throw new RuntimeException("Rect already freed");
                }
            }
            freeList.add(r);
            numFree++;
            maxFree = Math.max(maxFree, freeList.size());
        }
    }

    /**
     * Returns true if debugging the pool.
     * @return  true if debugging the pool.
     */
    public boolean isDebugging() {
        return debug != null;
    }
    
    /**
     * Sets the debugging parameter.
     * @param s value of the parameter
     * @return true if the value changed.
     */
    public boolean setDebugging(boolean s) {
        if (s == isDebugging()) {
            return false;
        }
        if (s) {
            debug = new HashSet(freeList);
            if (debug.size() != freeList.size()) {
                throw new RuntimeException("Dulicate rects in free list");
            }
        }
        else {
            debug = null;
        }
        return true;
    }
    
    /**
     * Clears the whole pool.
     */
    public void reset() {
        freeList = new ArrayList();
        if (debug != null) {
            debug = new HashSet();
        }
    }

    /**
     * Returns statistics of use.
     * @return statistics of use.
     */
    public String getStatistics() {
        return "allocated="+numAllocations
            +" freed="+numFree
            +" maxFreeList="+maxFree;
    }
    
    /**
     * Returns true if the pool manages rectangles.
     * @return true if the pool manages rectangles.
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Allow/disallow the pool to manage rectangles.
     * @param enabled set to true if the pool has to manage
     * rectangles, false otherwise.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
