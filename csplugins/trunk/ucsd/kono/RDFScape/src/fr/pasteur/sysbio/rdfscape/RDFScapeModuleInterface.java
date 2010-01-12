/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 * RDFScape is divided in modules implementing functionalities. Each module must implement this interface.
 */

/*
 * Created on Dec 7, 2005
 *
 */
package fr.pasteur.sysbio.rdfscape;

/**
 * @author andrea@pastuer.fr
 * the interface implemented by "elements" of RDFScape
 */
public interface RDFScapeModuleInterface {
	/**
	 * The part of the constructor that creates memory structures
	 * @return true if the initialization step was succesfull
	 */
	public boolean initialize(); 	
	
	/**
	 * Take the state of the object at the state it had after "initialize()". It does not necessarily re-create memory structures
	 */
	public void reset();			
	
	/**
	 * Something happened. The module has to check if the information it contains is consistent.
	 */
	public void touch();			
	
	/**
	 * Wether this module can operate correctly, that is pre-conditions are met and it is in a valid state.
	 * @return true if this module could work coherently. If false, functinalities of this module should not be used and may result even in run-time errors.
	 * 
	 */
	public boolean canOperate();
	
	
}
