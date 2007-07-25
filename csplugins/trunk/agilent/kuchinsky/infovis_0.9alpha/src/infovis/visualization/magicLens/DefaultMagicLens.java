/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.magicLens;

import infovis.visualization.MagicLens;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

/**
 * Class DefaultMagicLens
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class DefaultMagicLens implements MagicLens {
    protected boolean enabled;
    protected float lensX;
    protected float lensY;
    protected float lensRadius = 30;
    protected transient Rectangle2D.Float bounds = new Rectangle2D.Float();
    protected SwingPropertyChangeSupport changeSupport;
    
    public DefaultMagicLens() {
        super();
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean set) {
        if (this.enabled == set) return;
        this.enabled = set;
        firePropertyChange(PROPERTY_ENABLED, !this.enabled, this.enabled);
    }

    /*
     * @see infovis.visualization.MagicLens#getBounds()
     */
    public Rectangle2D getBounds() {
        if (bounds == null) {
            bounds =
                new Rectangle2D.Float(
                    lensX - lensRadius,
                    lensY - lensRadius,
                    2 * lensRadius,
                    2 * lensRadius);
        }
        else {
            bounds.setRect(
                    lensX - lensRadius,
                    lensY - lensRadius,
                    2 * lensRadius,
                    2 * lensRadius);
        }
        return bounds;
    }

    /*
     * @see infovis.visualization.MagicLens#setFocus(float, float)
     */
    public void setLens(float x, float y) {
        setLensX(x);
        setLensY(y);
    }

    /*
     * @see infovis.visualization.MagicLens#getFocusX()
     */
    public float getLensX() {
        return lensX;
    }

    /*
     * @see infovis.visualization.MagicLens#getFocusY()
     */
    public float getLensY() {
        return lensY;
    }

    /*
     * @see infovis.visualization.MagicLens#setFocusX(float)
     */
    public void setLensX(float focusX) {
        if (this.lensX == focusX) return;
        firePropertyChange(PROPERTY_LENS_POSITION_X, this.lensX, focusX);
        this.lensX = focusX;
    }

    /*
     * @see infovis.visualization.MagicLens#setFocusY(float)
     */
    public void setLensY(float focusY) {
        if (this.lensY == focusY) return;
        firePropertyChange(PROPERTY_LENS_POSITION_Y, this.lensY, focusY);
        this.lensY = focusY;
    }

    /*
     * @see infovis.visualization.MagicLens#getFocusRadius()
     */
    public float getLensRadius() {
        return lensRadius;
    }
    
    /*
     * @see infovis.visualization.MagicLens#setFocusRadius(float)
     */
    public void setLensRadius(float focusRadius) {
        if (this.lensRadius == focusRadius) return;
        firePropertyChange(PROPERTY_LENS_RADIUS, this.lensRadius, focusRadius);
        this.lensRadius = focusRadius;
    }
    
    /*
     * @see infovis.visualization.MagicLens#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        if (l == null) return;
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(l);
    }
    
    /*
     * 
     * @see infovis.visualization.MagicLens#addPropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) return;
        if (changeSupport == null) {
            changeSupport = new SwingPropertyChangeSupport(this);
        }
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    /*
     * 
     * @see infovis.visualization.MagicLens#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener l) {
        if (l == null) return;
        if (changeSupport == null) return;
        changeSupport.removePropertyChangeListener(l);
    }
    
    /*
     * 
     * @see infovis.visualization.MagicLens#removePropertyChangeListener(java.lang.String, java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null) return;
        if (changeSupport == null) return;

        changeSupport.removePropertyChangeListener(propertyName, listener);
    }

    public void firePropertyChange(String property, Object oldV, Object newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, oldV, newV);
    }
    
    public void firePropertyChange(String property, int oldV, int newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, new Integer(oldV), new Integer(newV));
    }
    
    public void firePropertyChange(String property, float oldV, float newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, new Float(oldV), new Float(newV));
    }
    
    public void firePropertyChange(String property, boolean oldV, boolean newV) {
        if (changeSupport == null) return;
        changeSupport.firePropertyChange(property, new Boolean(oldV), new Boolean(newV));
    }
}
