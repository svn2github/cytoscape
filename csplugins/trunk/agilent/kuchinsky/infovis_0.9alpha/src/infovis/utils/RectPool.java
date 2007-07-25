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
 * Class RectPool
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class RectPool {
    private static RectPool instance = new RectPool();
    protected ArrayList freeList;
    protected boolean enabled = true;
    private transient HashSet debug;
    private transient int numAllocations;
    private transient int numFree;
    private transient int maxFree;
    
    public static RectPool getInstance() {
        return instance;
    }
    
    public static Rectangle2D.Float allocateRect() {
        return getInstance().allocate();
    }
    
    public static void freeRect(Shape r) {
        getInstance().free(r);
    }
    
    public RectPool() {
        freeList = new ArrayList();
        assert(setDebugging(true));
    }
    
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
    
    public boolean isDebugging() {
        return debug != null;
    }
    
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
    
    public void reset() {
        freeList = new ArrayList();
        if (debug != null) {
            debug = new HashSet();
        }
    }
    
    public String getStatistics() {
        return "allocated="+numAllocations
            +" freed="+numFree
            +" maxFreeList="+maxFree;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
