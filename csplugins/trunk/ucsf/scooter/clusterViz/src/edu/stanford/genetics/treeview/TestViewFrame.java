/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: TestViewFrame.java,v $
 * $Revision: 1.13 $
 * $Date: 2006/10/04 16:34:01 $
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
package edu.stanford.genetics.treeview;
import javax.swing.JLabel;


/**
 *  Internal test class, used only by <code>main</code> test case.
 */
public class TestViewFrame extends ViewFrame {


	TestViewFrame() {
		super("Test Export Panel");
		getContentPane().add(new JLabel("test        test"));
	}


	public void setLoaded(boolean b) { }


	public void update(java.util.Observable obs, java.lang.Object obj) {
	}

	public double noData() {
		return 0.0;
	}

	public UrlPresets getGeneUrlPresets() {
		return null;
	}
	public UrlPresets getArrayUrlPresets() {
		return null;
	}
// hmmm this is kind of an insane dependancy... should get rid of it, methinks.
	public edu.stanford.genetics.treeview.plugin.dendroview.ColorPresets getColorPresets(){return null;}


	public boolean getLoaded() {
		return false;
	}

		public TreeViewApp getApp() {
			return null;
		}

	public DataModel getDataModel() {
		return null;
	}

	public void setDataModel(DataModel model) {
	}

	public HeaderFinder getGeneFinder() {
		return null;
	}


	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.ViewFrame#scrollToGene(int)
	 */
	public void scrollToGene(int i) {
		// nothing
		
	}


	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.ViewFrame#scrollToArray(int)
	 */
	public void scrollToArray(int i) {
		// nothing
		
	}


	public MainPanel[] getMainPanelsByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}


	public MainPanel[] getMainPanels() {
		// TODO Auto-generated method stub
		return null;
	}
}

