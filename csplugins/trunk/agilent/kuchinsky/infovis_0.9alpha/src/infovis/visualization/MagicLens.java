/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;

/**
 * A MagicLens is a see through lens that performs a transformation
 * on its contents.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public interface MagicLens {
    public static final String PROPERTY_LENS_POSITION_X = "lensPositionX";
    public static final String PROPERTY_LENS_POSITION_Y = "lensPositionY";
    public static final String PROPERTY_LENS_RADIUS = "focusRadius";
    public static final String PROPERTY_ENABLED = "enabled";
    
    /**
     * Returns whether this magic lens is enabled.
     * 
     * @return whether this magic lens is enabled.
     */
    public abstract boolean isEnabled();
    
    /**
     * Sets the enabled state of the magic lens.
     * 
     * @param set the enabled state to set
     */
    public abstract void setEnabled(boolean set); 
    
    /**
     * Returns the bounds of the transformed coordinates.
     * Coordintates outside these bounds are not transformed.
     * 
     * @return the bounds of the transformed coordinates.
     */
    public abstract Rectangle2D getBounds();

    /**
     * Sets for focus position
     *
     * @param x X coordinate of the position
     * @param y X coordinate of the position
     */
    public abstract void setLens(float x, float y);

    /**
     * Returns the lens X center position.
     *
     * @return float
     */
    public abstract float getLensX();

    /**
     * Returns the lens Y center position.
     *
     * @return float
     */
    public abstract float getLensY();

    /**
     * Sets the lensX.
     *
     * @param x The lest X to set
     */
    public abstract void setLensX(float x);

    /**
     * Sets the lensY.
     *
     * @param y The lens Y to set
     */
    public abstract void setLensY(float y);
    
    /**
     * Sets the lens radius.
     * 
     * @param radius the new radius.
     */
    public abstract void setLensRadius(float radius);
    
    /**
     * Returns the lens radius.
     * 
     * @return the lens radius.
     */
    public abstract float getLensRadius();


    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param l  The PropertyChangeListener to be added
     */
    public abstract void addPropertyChangeListener(PropertyChangeListener l);
    
    /**
     * Add a PropertyChangeListener for a specific property.  The listener
     * will be invoked only when a call on firePropertyChange names that
     * specific property.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The PropertyChangeListener to be added
     */
    public abstract void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param l  The PropertyChangeListener to be removed
     */
    public abstract void removePropertyChangeListener(PropertyChangeListener l);

    /**
     * Remove a PropertyChangeListener for a specific property.
     *
     * @param propertyName  The name of the property that was listened on.
     * @param listener  The PropertyChangeListener to be removed
     */
    public abstract void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);        
}
