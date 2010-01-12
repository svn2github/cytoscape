/**
 * Copyright 2006 Andrea Splendiani
 * Released under GPL license
 */

/*
 * Created on Jan 26, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package fr.pasteur.sysbio.rdfscape;

/**
 * @author andrea
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface MemoryViewer {
	/**
	 * The memory changed part of its content. Update related infos!
	 *
	 */
	void updateView();
	
	/**
	 * The memory changed part of its content related to a specific namespace. 
	 * Update related infos!
	 */
	void updateNamespaceView(String namespace);
}
