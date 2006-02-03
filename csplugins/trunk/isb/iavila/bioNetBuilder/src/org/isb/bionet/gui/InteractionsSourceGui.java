package org.isb.bionet.gui;
import java.util.Hashtable;

/**
 * This is an interface for classes that serve as GUIs to set user parameters to retrieve
 * interactions from an interactions handler.
 * 
 * @author Iliana Avila-Campillo
 * @version %I%, %G%
 * @since 1.0
 *
 */
public interface InteractionsSourceGui {
    
    /**
     * Gets a Hashtable in which each entry represents a user parameter for an interactions handler.<p>
     * The key is a String that the interactions handler understands, and the value is of the type the
     * interactions handler expects for the given key.
     * @return a Hashtable, possibly empty
     */
    public Hashtable getArgsTable ();
    
}