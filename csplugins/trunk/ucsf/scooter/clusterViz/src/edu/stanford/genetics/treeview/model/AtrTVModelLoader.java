/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: avsegal
 * $RCSfile: AtrTVModelLoader.java
 * $Revision: 
 * $Date: Jun 25, 2004
 * $Name:  
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * http://www.gnu.org/licenses/gpl.txt
 *
 * END_HEADER
 */
 
package edu.stanford.genetics.treeview.model;


import edu.stanford.genetics.treeview.LoadException;


/**
 * @author avsegal
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class AtrTVModelLoader extends TVModelLoader2 {

	/**
	 * @param targetModel
	 */
	public AtrTVModelLoader(AtrTVModel targetModel) {
		super(targetModel);
	}
	
	protected void  parseCDT(RectData tempVector) throws LoadException {
		super.findCdtDimensions(tempVector);
		super.loadArrayAnnotation(tempVector);
		super.loadGeneAnnotation(tempVector);
	}
	
}
