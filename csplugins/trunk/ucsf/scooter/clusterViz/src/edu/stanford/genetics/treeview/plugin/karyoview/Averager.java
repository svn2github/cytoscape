/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: Averager.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:49 $
 * $Name:  $
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
package edu.stanford.genetics.treeview.plugin.karyoview;

import edu.stanford.genetics.treeview.*;

/**
* this base class doesn't actually average. All other methods should subclass.
*/
class Averager {
	public final static int SIMPLE = 0;
	public final static int NEAREST = 1;
	public final static int NEIGHBOR = 2;
	public final static int INTERVAL = 3;
	
	protected KaryoView karyoView;
	/** Setter for karyoView */
	public void setKaryoView(KaryoView karyoView) {
		this.karyoView = karyoView;
	}
	/** Getter for karyoView */
	public KaryoView getKaryoView() {
		return karyoView;
	}
	
	  private ConfigNode configNode = new DummyConfigNode("Averager");
	  /** Setter for configNode */
	  public void bindConfig(ConfigNode configNode) {
		  this.configNode = configNode;
	  }
	  /** Getter for configNode */
	  public ConfigNode getConfigNode() {
		  return configNode;
	  }

	
	
	protected String [] message = new String [2];
	public String[] getDescription(ChromosomeLocus locus, int col) {
		int row = locus.getCdtIndex();
		message[0] = "Gene " + getKaryoView().getGeneInfo().getHeader(row, "YORF");
		message[1] = "No averaging";
		return message;
	}
	public double getValue(ChromosomeLocus locus,int col) {
		int row = locus.getCdtIndex();
		int numCol = karyoView.getNumCol();
		return karyoView.getDataMatrix().getValue(col, row);
	}
	public int getType() {
		return Averager.SIMPLE;
	}
	public String getArg() {
		return null;
	}
}

