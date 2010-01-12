/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jan 25, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape;


/**
 * @author andrea
 * Interface implemented by all objects that can have their data stored into an anlaysis context
 * All classes implementing this interface must in their constructor call:
 * contextManager.addContextualizableElement(this);
 * the reason for this interface is not an abstract class is only the lack of multiple inheritance.
 * 
 */
public interface Contextualizable {
	/**
	 * Reads data from the active context
	 * @return true if the operation was succesfull
	 */
	boolean loadFromActiveContext();
	/**
	 * Save data to the active context
	 * @return true if the operation was succesfull
	 */
	boolean saveToContext();
	
}
