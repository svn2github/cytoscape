/*
 * Created on Aug 1, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview;

import java.awt.Frame;

/**
 * @author aloksaldanha
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GeneFinder extends HeaderFinder {

	/**
	 * @param f
	 * @param hI
	 * @param geneSelection
	 */
	public GeneFinder(ViewFrame f, HeaderInfo hI, TreeSelectionI geneSelection) {
		super(f, hI, geneSelection, "Search Gene Text for Substring");
	}
	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.HeaderFinder#scrollToIndex(int)
	 */
	public void scrollToIndex(int i) {
		viewFrame.scrollToGene(i);
	}

}
