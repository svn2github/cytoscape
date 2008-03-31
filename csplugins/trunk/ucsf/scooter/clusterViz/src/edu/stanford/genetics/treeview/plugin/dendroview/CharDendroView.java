/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: rqluk $
 * $RCSfile: CharDendroView.java,v $
 * $Revision: 1.1 $
 * $Date: 2006/08/16 19:13:45 $
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
package edu.stanford.genetics.treeview.plugin.dendroview;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JDialog;
import javax.swing.JScrollBar;

import edu.stanford.genetics.treeview.*;

/**
 *  This class encapsulates a dendrogram view, which is the classic Eisen
 *  treeview. It uses a drag grid panel to lay out a bunch of linked
 *  visualizations of the data, a la Eisen. In addition to laying out
 *  components, it also manages the GlobalZoomMap. This is necessary since both
 *  the GTRView (gene tree) and KnnGlobalView need to know where to lay out genes
 *  using the same map. The zoom map is managed by the ViewFrame- it represents
 *  the selected genes, and potentially forms a link between different views,
 *  only one of which is the KnnDendroView.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.1 $ $Date: 2006/08/16 19:13:45 $
 */
public class CharDendroView extends DendroView {
	/**
	 *  Constructor for the CharDendroView object
	 *
	 * @param  vFrame  parent ViewFrame of CharDendroView
	 * @param  configNode   node in which to store persistent configuration info (if desired)
	 */
	public CharDendroView(ViewFrame vFrame, ConfigNode configNode) {
		super(vFrame.getDataModel(), configNode, vFrame, "CharDendroView");
		// this is where it gets interesting...
		setArraySelection(new TreeSelection(arrayDrawer.getNumCol()));
	}
	


	public void populateExportMenu(Menu menu)
	{
		/*
		PostscriptExportPanel doesn't currently support character drawing. Otherwise, this works.
		
		MenuItem psItem = new MenuItem("Export to Postscript...", new MenuShortcut(KeyEvent.VK_X));
		psItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				
				
				MapContainer initXmap, initYmap;
				if ((arraySelection.getNSelectedIndexes() != 0) || (geneSelection.getNSelectedIndexes() != 0)){
					initXmap = getZoomXmap();
					initYmap = getZoomYmap();
				} else {
					initXmap = getGlobalXmap();
					initYmap = getGlobalYmap();
				}
				PostscriptExportPanel psePanel = new PostscriptExportPanel
				(arraynameview.getHeaderInfo(), 
				getModel().getGeneHeaderInfo(),
				geneSelection, arraySelection,
				invertedTreeDrawer,
				leftTreeDrawer, arrayDrawer, initXmap, initYmap);
				psePanel.setSourceSet(dataModel.getFileSet());
				psePanel.setGeneFont(textview.getFont());
				psePanel.setArrayFont(arraynameview.getFont());
				psePanel.setIncludedArrayHeaders(arraynameview.getHeaderSummary().getIncluded());
				psePanel.setIncludedGeneHeaders(textview.getHeaderSummary().getIncluded());
				final JDialog popup = new CancelableSettingsDialog(viewFrame, "Export to Postscript", psePanel);
				popup.pack();
				popup.show();
			}
		});
		menu.add(psItem);
		*/
		
		MenuItem bitmapItem = new MenuItem("Export to Image...");
		bitmapItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				
				MapContainer initXmap, initYmap;
				if ((getArraySelection().getNSelectedIndexes() != 0) || (getGeneSelection().getNSelectedIndexes() != 0)){
					initXmap = getZoomXmap();
					initYmap = getZoomYmap();
				} else {
					initXmap = getGlobalXmap();
					initYmap = getGlobalYmap();
				}
				
				BitmapExportPanel bitmapPanel = new BitmapExportPanel
				(arraynameview.getHeaderInfo(), 
				getDataModel().getGeneHeaderInfo(),
				getGeneSelection(), getArraySelection(),
				invertedTreeDrawer,
				leftTreeDrawer, arrayDrawer, initXmap, initYmap,
				true);
				bitmapPanel.setSourceSet(getDataModel().getFileSet());
				bitmapPanel.setGeneFont(textview.getFont());
				bitmapPanel.setArrayFont(arraynameview.getFont());
				bitmapPanel.setIncludedArrayHeaders(arraynameview.getHeaderSummary().getIncluded());
				bitmapPanel.setIncludedGeneHeaders(textview.getHeaderSummary().getIncluded());
				
				final JDialog popup = new CancelableSettingsDialog(viewFrame, "Export to Image", bitmapPanel);
				popup.pack();
				popup.show();
			}
		});
		menu.add(bitmapItem);
		
		
	}

	/**
	 *  This method should be called only during initial setup of the modelview
	 *
	 *  It sets up the views and binds them all to config nodes.
	 *
	 */
	protected void setupViews() {

		CharColorExtractor colorExtractor = new CharColorExtractor();
		
		hintpanel = new MessagePanel("Usage Hints");
		statuspanel = new MessagePanel("View Status");

		CharArrayDrawer cArrayDrawer = new CharArrayDrawer();
		cArrayDrawer.setColorExtractor(colorExtractor);
		// set data first to avoid adding auto-genereated contrast to documentConfig.
		cArrayDrawer.setHeaderInfo(getDataModel().getGeneHeaderInfo(), getConfigNode().getAttribute("headerName", "ALN"));
		arrayDrawer = cArrayDrawer;




		((Observable)getDataModel()).addObserver(arrayDrawer);

		globalview = new GlobalView();
		
		
		// scrollbars, mostly used by maps
		globalXscrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 0,1,0,1);
		globalYscrollbar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,1);
		zoomXscrollbar = new JScrollBar(JScrollBar.HORIZONTAL, 0,1,0,1);
		zoomYscrollbar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,1);



		 zoomXmap = new MapContainer();
		 zoomXmap.setDefaultScale(12.0);
		 zoomXmap.setScrollbar(zoomXscrollbar);
		 zoomYmap = new MapContainer();
		 zoomYmap.setDefaultScale(12.0);
		 zoomYmap.setScrollbar(zoomYscrollbar);

		 // globalmaps tell globalview, atrview, and gtrview
		 // where to draw each data point.
	// the scrollbars "scroll" by communicating with the maps.
		globalXmap = new MapContainer();
		globalXmap.setDefaultScale(2.0);
		globalXmap.setScrollbar(globalXscrollbar);
		globalYmap = new MapContainer();
		globalYmap.setDefaultScale(2.0);
		globalYmap.setScrollbar(globalYscrollbar);

		globalview.setXMap(globalXmap);
		globalview.setYMap(globalYmap);

		globalview.setZoomYMap(getZoomYmap());
		globalview.setZoomXMap(getZoomXmap());
		globalview.setArrayDrawer(arrayDrawer);

		charHeaderInfo = new CharHeaderInfo(arrayDrawer.getNumCol());
		arraynameview = new ArrayNameView(charHeaderInfo);
		arraynameview.setUrlExtractor(viewFrame.getArrayUrlExtractor());

		leftTreeDrawer = new LeftTreeDrawer();
		gtrview = new GTRView();
		gtrview.setMap(globalYmap);
		gtrview.setLeftTreeDrawer(leftTreeDrawer);
		gtrview.getHeaderSummary().setIncluded(new int [] {0,3});
		
		invertedTreeDrawer = new InvertedTreeDrawer();
		atrview = new ATRView();
		atrview.setMap(globalXmap);
		atrview.setInvertedTreeDrawer(invertedTreeDrawer);
		atrview.getHeaderSummary().setIncluded(new int [] {0,3});

		atrzview = new ATRZoomView();
		atrzview.setZoomMap(getZoomXmap());
		atrzview.setHeaderSummary(atrview.getHeaderSummary());
		atrzview.setInvertedTreeDrawer(invertedTreeDrawer);

		zoomview = new ZoomView();
		zoomview.setYMap(getZoomYmap());
		zoomview.setXMap(getZoomXmap());
		zoomview.setArrayDrawer(arrayDrawer);

		arraynameview.setMapping(getZoomXmap());

		textview = new TextViewManager(getDataModel().getGeneHeaderInfo(), viewFrame.getUrlExtractor());
		textview.setMap(getZoomYmap());

		doDoubleLayout();
		
		// reset persistent popups
		settingsFrame = null;
		settingsPanel = null;

		// urls
		colorExtractor.bindConfig(getFirst("ColorExtractor"));
		

		bindTrees();
		
		zoomview.setHeaders(getDataModel().getGeneHeaderInfo(), charHeaderInfo);
		zoomview.setShowVal(true);
		globalXmap.bindConfig(getFirst("GlobalXMap"));
		globalYmap.bindConfig(getFirst("GlobalYMap"));
		getZoomXmap().bindConfig(getFirst("ZoomXMap"));
		getZoomYmap().bindConfig(getFirst("ZoomYMap"));

		textview.bindConfig(getFirst("TextViewParent"));
//		arraynameview.bindConfig(getFirst("ArrayNameView"));

		// perhaps I could remember this stuff in the MapContainer...
		globalXmap.setIndexRange(0, arrayDrawer.getNumCol() - 1);
		globalYmap.setIndexRange(0, arrayDrawer.getNumRow() - 1);
		getZoomXmap().setIndexRange(-1, -1);
		getZoomYmap().setIndexRange(-1, -1);

		globalXmap.notifyObservers();
		globalYmap.notifyObservers();
		getZoomXmap().notifyObservers();
		getZoomYmap().notifyObservers();
	}
	private HeaderInfo charHeaderInfo;
}
class CharHeaderInfo implements HeaderInfo {
	String [] holder = new String [1];
	String [] names = new String [1];
	int numChars;
	CharHeaderInfo(int n) {
		numChars = n;
	}

	public String[] getHeader(int i) {
		holder[0] = "" + i;
		return holder;
	}

	public String getHeader(int i, String name) {
		return "" + i;
	}
	public String getHeader(int rowIndex, int columnIndex) {
		return "" + rowIndex;
	}

	public String[] getNames() {
		names[0] = "Column";
		return names;
	}

	public int getNumNames() {
		return 1;
	}

	public int getNumHeaders() {
		return numChars;
	}

	public int getIndex(String name) {
		return 0;
	}

	public int getHeaderIndex(String id) {
		return 0;
	}
	/**
	 * noop, since this object is static.
	 */
	public void addObserver(Observer o) {}		
	public void deleteObserver(Observer o) {}		
	public boolean addName(String name, int location) {return false;}
	public boolean setHeader(int i, String name, String value) {return false;}
	public boolean getModified() {return false;}
	public void setModified(boolean mod) {}		
}

