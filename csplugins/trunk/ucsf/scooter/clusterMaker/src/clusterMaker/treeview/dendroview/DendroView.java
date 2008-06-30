/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: DendroView.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/10/04 16:17:58 $
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
package clusterMaker.treeview.dendroview;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;

import cytoscape.logger.CyLogger;

import clusterMaker.treeview.BrowserControl;
import clusterMaker.treeview.ConfigNode;
import clusterMaker.treeview.ConfigNodePersistent;
import clusterMaker.treeview.DataModel;
import clusterMaker.treeview.DragGridPanel;
import clusterMaker.treeview.DummyConfigNode;
import clusterMaker.treeview.FileSet;
import clusterMaker.treeview.HeaderInfo;
import clusterMaker.treeview.HeaderSummary;
import clusterMaker.treeview.LoadException;
import clusterMaker.treeview.MainPanel;
import clusterMaker.treeview.MessagePanel;
import clusterMaker.treeview.ModelessSettingsDialog;
import clusterMaker.treeview.ModelView;
import clusterMaker.treeview.PropertyConfig;
import clusterMaker.treeview.ReorderedTreeSelection;
import clusterMaker.treeview.ViewFrame;
import clusterMaker.treeview.TabbedSettingsPanel;
import clusterMaker.treeview.TreeDrawerNode;
import clusterMaker.treeview.TreeSelectionI;
import clusterMaker.treeview.model.AtrTVModel;
import clusterMaker.treeview.model.DataModelWriter;
import clusterMaker.treeview.model.ReorderedDataModel;
import clusterMaker.treeview.model.TVModel;

/**
 *  This class encapsulates a dendrogram view, which is the classic Eisen
 *  treeview. It uses a drag grid panel to lay out a bunch of linked
 *  visualizations of the data, a la Eisen. In addition to laying out
 *  components, it also manages the GlobalZoomMap. This is necessary since both
 *  the GTRView (gene tree) and GlobalView need to know where to lay out genes
 *  using the same map. The zoom map is managed by the ViewFrame- it represents
 *  the selected genes, and potentially forms a link between different views,
 *  only one of which is the DendroView.
 *
 * The intention here is that you create this from a model, and never replace that model. If you want to show another file, make another dendroview. All views should of course still listen to the model, since that can still be changed ad libitum.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.4 $ $Date: 2006/10/04 16:17:58 $
 */
public class DendroView extends JPanel implements ConfigNodePersistent, MainPanel, Observer {
	/**
	 *  Constructor for the DendroView object
	 * note this will reuse any existing MainView subnode of the documentconfig.
	 *
	 * @param  tVModel   model this DendroView is to represent
	 * @param  vFrame  parent ViewFrame of DendroView
	 */
	public DendroView(DataModel tVModel, ViewFrame vFrame) {
		this(tVModel, null, vFrame, "Dendrogram");
	}
	public DendroView(DataModel tVModel, ConfigNode root, ViewFrame vFrame) {
		this(tVModel, root, vFrame, "Dendrogram");
	}
	/**
	 *  Constructor for the DendroView object which binds to an explicit confignode
	 *
	 * @param  dataModel   model this DendroView is to represent
	 * @param  root   Confignode to which to bind this DendroView
	 * @param  vFrame  parent ViewFrame of DendroView
	 * @param  name name of this view.
	 */
	public DendroView(DataModel dataModel, ConfigNode root, ViewFrame vFrame, String name) {
		super.setName(name);

		viewFrame = vFrame;
		if (root == null) {
			if (dataModel.getDocumentConfig() != null ) {
				  bindConfig(dataModel.getDocumentConfig().fetchOrCreate("MainView"));
				} else {
				  bindConfig(new DummyConfigNode("MainView"));
				}
		} else {
			bindConfig(root);
		}
		if (dataModel.getArrayHeaderInfo().getIndex("GROUP") != -1) {
			HeaderInfo headerInfo = dataModel.getArrayHeaderInfo();
			int groupIndex = headerInfo.getIndex("GROUP");
			arrayIndex = getGroupVector(headerInfo, groupIndex);
		} else {
			arrayIndex = null;
		}
		if (dataModel.getGeneHeaderInfo().getIndex("GROUP") != -1) {
			HeaderInfo headerInfo = dataModel.getGeneHeaderInfo();
			int groupIndex = headerInfo.getIndex("GROUP");
			geneIndex = getGroupVector(headerInfo, groupIndex);
		} else {
			geneIndex = null;
		}
		if ((arrayIndex != null) ||(geneIndex != null)){
			dataModel = new ReorderedDataModel(dataModel, geneIndex, arrayIndex);
		}
		setDataModel(dataModel);
		
		setupViews();
		
		if (geneIndex != null) {
			setGeneSelection(new ReorderedTreeSelection(viewFrame.getGeneSelection(), geneIndex));
		} else {
			setGeneSelection(viewFrame.getGeneSelection());
		}

		if (arrayIndex != null){
			setArraySelection(new ReorderedTreeSelection(viewFrame.getArraySelection(), arrayIndex));
		} else {
			setArraySelection(viewFrame.getArraySelection());
		}
	}

	private int [] getGroupVector(HeaderInfo headerInfo, int groupIndex) {
		int ngroup = 0;
		String cur = headerInfo.getHeader(0, groupIndex);
		for (int i = 0; i < headerInfo.getNumHeaders(); i++) {
			String test = headerInfo.getHeader(i, groupIndex);
			if (cur.equals(test) == false) {
				cur = test;
				ngroup++;
			}
		}
		int [] groupVector = new int[ngroup + headerInfo.getNumHeaders()];
		ngroup = 0;
		cur = headerInfo.getHeader(0, groupIndex);
		for (int i = 0; i < headerInfo.getNumHeaders(); i++) {
			String test = headerInfo.getHeader(i, groupIndex);
			if (cur.equals(test) == false) {
				groupVector[i+ngroup] = -1;
				cur = test;
				ngroup++;
			}
			groupVector[i + ngroup] = i;
		}
		return groupVector;
	}

	protected DendroView(int cols, int rows, String name) {
		super.setName(name);
	}
	
	/**
	* always returns an instance of the node, even if it has to create it.
	*/
	protected ConfigNode getFirst(String name) {
		return getConfigNode().fetchOrCreate(name);
	}

	public TreeSelectionI getGeneSelection() {
		return geneSelection;
	}
	public TreeSelectionI getArraySelection() {
		return arraySelection;
	}
	/**
	 *  This should be called after setDataModel has been set to the appropriate model
	 * @param arraySelection
	 */
	protected void setArraySelection(TreeSelectionI arraySelection) {
		if (this.arraySelection != null) {
			this.arraySelection.deleteObserver(this);	
		}
		this.arraySelection = arraySelection;
		arraySelection.addObserver(this);
		globalview.setArraySelection(arraySelection);
		zoomview.setArraySelection(arraySelection);
		atrview.setArraySelection(arraySelection);
		atrzview.setArraySelection(arraySelection);
		
		arraynameview.setArraySelection(arraySelection);
	}

	/**
	 *  This should be called after setDataModel has been set to the appropriate model
	 * @param geneSelection
	 */
	protected void setGeneSelection(TreeSelectionI geneSelection) {
		if (this.geneSelection != null) {
			this.geneSelection.deleteObserver(this);	
		}
		this.geneSelection = geneSelection;
		geneSelection.addObserver(this);
		globalview.setGeneSelection(geneSelection);
		zoomview.setGeneSelection(geneSelection);
		gtrview.setGeneSelection(geneSelection);
		
		textview.setGeneSelection(geneSelection);
		
	}
	
	/**
	 * Finds the currently selected arrays, mirror image flips them, and then rebuilds all necessary trees and saved data to the .jtv file.
	 *
	 */
	private void flipSelectedATRNode()
	{
			int leftIndex, rightIndex;
			String selectedID;
			TreeDrawerNode arrayNode = invertedTreeDrawer.getNodeById(getArraySelection().getSelectedNode());
			
			if(arrayNode == null || arrayNode.isLeaf())
			{
					return;
			}
			
			selectedID = arrayNode.getId();
		
			//find the starting index of the left array tree, the ending index of the right array tree
			leftIndex = getDataModel().getArrayHeaderInfo().getHeaderIndex(arrayNode.getLeft().getLeftLeaf().getId());
			rightIndex = getDataModel().getArrayHeaderInfo().getHeaderIndex(arrayNode.getRight().getRightLeaf().getId());
			
			int num = getDataModel().getDataMatrix().getNumUnappendedCol();
			
			int [] newOrder = new int[num];
			
			for(int i = 0; i < num; i++)
			{
				newOrder[i] = i;
			}
			
			for(int i = 0; i <= (rightIndex - leftIndex); i++)
			{
				newOrder[leftIndex + i] = rightIndex - i;
			}
			
			/*System.out.print("Fliping to: ");
			for(int i = 0; i < newOrder.length; i++)
			{
				System.out.print(newOrder[i] + " ");
			}
			System.out.println("");*/
			
			((TVModel)getDataModel()).reorderArrays(newOrder);
			((TVModel)getDataModel()).saveOrder(newOrder);	
			((Observable)getDataModel()).notifyObservers();
			
			updateATRDrawer(selectedID);
	}
	
	
	
	/**
	 * Updates the ATRDrawer to reflect changes in the DataMode array order; rebuilds the TreeDrawerNode tree.
	 * @param selectedID ID of the node selected before a change in tree structure was made. This node is then found and reselected after the ATR tree is rebuilt.
	 */
	private void updateATRDrawer(String selectedID)
	{
		try {
			TVModel tvmodel = (TVModel)getDataModel();
			invertedTreeDrawer.setData(tvmodel.getAtrHeaderInfo(), tvmodel.getArrayHeaderInfo());
			HeaderInfo trHeaderInfo = tvmodel.getAtrHeaderInfo();
			if (trHeaderInfo.getIndex("NODECOLOR") >= 0) {
				TreeColorer.colorUsingHeader (invertedTreeDrawer.getRootNode(),
				trHeaderInfo,
				trHeaderInfo.getIndex("NODECOLOR"));
			
			}	
		}
		catch (DendroException e) {
			//				LogPanel.println("Had problem setting up the array tree : " + e.getMessage());
			//				e.printStackTrace();
			Box mismatch = new Box(BoxLayout.Y_AXIS); mismatch.add(new JLabel(e.getMessage()));
			mismatch.add(new JLabel("Perhaps there is a mismatch between your ATR and CDT files?"));
			mismatch.add(new JLabel("Ditching Array Tree, since it's lame."));
			JOptionPane.showMessageDialog(viewFrame, mismatch, "Tree Construction Error", JOptionPane.ERROR_MESSAGE);
			atrview.setEnabled(false);
			atrzview.setEnabled(false);
			try{invertedTreeDrawer.setData(null, null);} catch (DendroException ex) {}
		}
		
		TreeDrawerNode arrayNode = invertedTreeDrawer.getRootNode().findNode(selectedID);
		arraySelection.setSelectedNode(arrayNode.getId());
		atrzview.setSelectedNode(arrayNode);
		atrview.setSelectedNode(arrayNode);		
		arraySelection.notifyObservers();
		
		
		invertedTreeDrawer.notifyObservers();
	}

	// accessors
	/**
	 *  Gets the globalXmap attribute of the DendroView object
	 *
	 * @return    The globalXmap
	 */
	public MapContainer getGlobalXmap() {
		return globalXmap;
	}


	/**
	 *  Gets the globalYmap attribute of the DendroView object
	 *
	 * @return    The globalYmap
	 */
	public MapContainer getGlobalYmap() {
		return globalYmap;
	}


	/**
	 *  Gets the zoomXmap attribute of the DendroView object
	 *
	 * @return    The zoomXmap
	 */
	public MapContainer getZoomXmap() {
		return zoomXmap;
	}


	/**
	 *  Gets the zoomYmap attribute of the DendroView object
	 *
	 * @return    The zoomYmap
	 */
	public MapContainer getZoomYmap() {
		return zoomYmap;
	}

	public void scrollToGene(int i) {
		getGlobalYmap().scrollToIndex(i);
		getGlobalYmap().notifyObservers();
	}
	public void scrollToArray(int i) {
		getGlobalXmap().scrollToIndex(i);
		getGlobalXmap().notifyObservers();
	}
	
    public void update(Observable o, Object arg) {
    		if (o == geneSelection) {
    			gtrview.scrollToNode(geneSelection.getSelectedNode());
    		}
	}
	/**
	 *  This method should be called only during initial setup of the modelview
	 *
	 *  It sets up the views and binds them all to config nodes.
	 *
	 */
	protected void setupViews() {
		colorPresets = new ColorPresets(root);
		cpresetEditor = new ColorPresetEditor(colorPresets);
		ColorExtractor colorExtractor = new ColorExtractor();
		colorExtractor.bindConfig(root);
		colorExtractor.setDefaultColorSet(colorPresets.getDefaultColorSet());
		colorExtractor.setMissing(DataModel.NODATA, DataModel.EMPTY);
		
		hintpanel = new MessagePanel("Usage Hints");
		statuspanel = new MessagePanel("View Status");

		DoubleArrayDrawer dArrayDrawer = new DoubleArrayDrawer();
		dArrayDrawer.setColorExtractor(colorExtractor);
		arrayDrawer = dArrayDrawer;
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

		arraynameview = new ArrayNameView(getDataModel().getArrayHeaderInfo());
		arraynameview.setUrlExtractor(viewFrame.getArrayUrlExtractor());
		arraynameview.setDataModel(getDataModel());

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
		
		// set data first to avoid adding auto-genereated contrast to documentConfig.
		dArrayDrawer.setDataMatrix(getDataModel().getDataMatrix());
		dArrayDrawer.bindConfig(getFirst("ArrayDrawer"));

		// this is here because my only subclass shares this code.
		bindTrees();
		
		zoomview.setHeaders(getDataModel().getGeneHeaderInfo(), getDataModel().getArrayHeaderInfo());
		
		globalXmap.bindConfig(getFirst("GlobalXMap"));
		globalYmap.bindConfig(getFirst("GlobalYMap"));
		getZoomXmap().bindConfig(getFirst("ZoomXMap"));
		getZoomYmap().bindConfig(getFirst("ZoomYMap"));

		textview.bindConfig(getFirst("TextView"));			
		
		arraynameview.bindConfig(getFirst("ArrayNameView"));
		HeaderSummary atrSummary = atrview.getHeaderSummary();
		atrzview.setHeaderSummary(atrSummary);
		atrSummary.bindConfig(getFirst("AtrSummary"));
		gtrview.getHeaderSummary().bindConfig(getFirst("GtrSummary"));

		// perhaps I could remember this stuff in the MapContainer...
		globalXmap.setIndexRange(0, dataModel.getDataMatrix().getNumCol() - 1);
		globalYmap.setIndexRange(0, dataModel.getDataMatrix().getNumRow() - 1);
		getZoomXmap().setIndexRange(-1, -1);
		getZoomYmap().setIndexRange(-1, -1);

		globalXmap.notifyObservers();
		globalYmap.notifyObservers();
		getZoomXmap().notifyObservers();
		getZoomYmap().notifyObservers();
	}
	/**
	* this is meant to be called from setupViews.
	* It make sure that the trees are generated from the current model,
	* and enables/disables them as required.
	*
	* I factored it out because it is common betwen DendroView and KnnDendroView.
	*/
	protected void bindTrees() {
		DataModel tvmodel =  getDataModel();

		if ((tvmodel != null) && tvmodel.aidFound()) {
			try {
				atrview.setEnabled(true);
				atrzview.setEnabled(true);
				invertedTreeDrawer.setData(tvmodel.getAtrHeaderInfo(), tvmodel.getArrayHeaderInfo());
				HeaderInfo trHeaderInfo = tvmodel.getAtrHeaderInfo();
				if (trHeaderInfo.getIndex("NODECOLOR") >= 0) {
					TreeColorer.colorUsingHeader (invertedTreeDrawer.getRootNode(),
					trHeaderInfo,
					trHeaderInfo.getIndex("NODECOLOR"));
					
				}	
			} catch (DendroException e) {
				//				LogPanel.println("Had problem setting up the array tree : " + e.getMessage());
				//				e.printStackTrace();
				Box mismatch = new Box(BoxLayout.Y_AXIS); mismatch.add(new JLabel(e.getMessage()));
				mismatch.add(new JLabel("Perhaps there is a mismatch between your ATR and CDT files?"));
				mismatch.add(new JLabel("Ditching Array Tree, since it's lame."));
				JOptionPane.showMessageDialog(viewFrame, mismatch, "Tree Construction Error", JOptionPane.ERROR_MESSAGE);
				atrview.setEnabled(false);
				atrzview.setEnabled(false);
				try{invertedTreeDrawer.setData(null, null);} catch (DendroException ex) {}
			}
		} else {
			atrview.setEnabled(false);
			atrzview.setEnabled(false);
				try{invertedTreeDrawer.setData(null, null);} catch (DendroException ex) {}
		}
		invertedTreeDrawer.notifyObservers();

		if ((tvmodel != null) && tvmodel.gidFound()) {
			try {
				leftTreeDrawer.setData(tvmodel.getGtrHeaderInfo(), tvmodel.getGeneHeaderInfo());
				HeaderInfo gtrHeaderInfo = tvmodel.getGtrHeaderInfo();
				if (gtrHeaderInfo.getIndex("NODECOLOR") >= 0) {
					TreeColorer.colorUsingHeader (leftTreeDrawer.getRootNode(),
					tvmodel.getGtrHeaderInfo(),
					gtrHeaderInfo.getIndex("NODECOLOR"));
					
				} else {
					TreeColorer.colorUsingLeaf(leftTreeDrawer.getRootNode(),
							tvmodel.getGeneHeaderInfo(),
							tvmodel.getGeneHeaderInfo().getIndex("FGCOLOR")
							);
				}

				gtrview.setEnabled(true);
			} catch (DendroException e) {
//				LogPanel.println("Had problem setting up the gene tree : " + e.getMessage());
//				e.printStackTrace();
				Box mismatch = new Box(BoxLayout.Y_AXIS); mismatch.add(new JLabel(e.getMessage()));
				mismatch.add(new JLabel("Perhaps there is a mismatch between your GTR and CDT files?"));
				mismatch.add(new JLabel("Ditching Gene Tree, since it's lame."));
				JOptionPane.showMessageDialog(viewFrame, mismatch, "Tree Construction Error", JOptionPane.ERROR_MESSAGE);
				gtrview.setEnabled(false);
				try{leftTreeDrawer.setData(null, null);} catch (DendroException ex) {}
			}
		} else {
			gtrview.setEnabled(false);
			try{leftTreeDrawer.setData(null, null);} catch (DendroException ex) {}
		}
		leftTreeDrawer.notifyObservers();

	}

	/**
	 * Lays out components in two DragGridPanel separated by a
	 * JSplitPane, so that you can expand/contract with one click.
	 *
	 */

	protected void doDoubleLayout() {
	  DragGridPanel left = new DragGridPanel(2,2);
	  left.setName("LeftDrag");
	  DragGridPanel right = new DragGridPanel(2,3);
	 right.setName("RightDrag");
	    left.setBorderWidth(2);
		left.setBorderHeight(2);
		left.setMinimumWidth(1);
		left.setMinimumHeight(1);
		left.setFocusWidth(1);
		left.setFocusHeight(1);

	    right.setBorderWidth(2);
		right.setBorderHeight(2);
		right.setMinimumWidth(1);
		right.setMinimumHeight(1);
		right.setFocusWidth(1);
		right.setFocusHeight(1);

		float lheights []  = new float[2];
		lheights [0] = (float) .15;
		lheights[1] = (float) .85;
		left.setHeights(lheights);

		float lwidths []  = new float[2];
		lwidths [0] = (float) .35;
		lwidths[1] = (float) .65;
		left.setWidths(lwidths);

		float rheights [] = new float[3];
		rheights[0] = (float).15;
		rheights[1] = (float).05;
		rheights[2] = (float).8;
		right.setHeights(rheights);

		
		Rectangle rectangle  = new Rectangle(0, 0, 1, 1);

		left.addComponent(statuspanel, rectangle);
		rectangle.translate(1, 0);

		left.addComponent(atrview.getComponent(), rectangle);
		registerView(atrview);

		rectangle.translate(-1, 0);
		right.addComponent(arraynameview.getComponent(), rectangle);
		registerView(arraynameview);
		
		rectangle.translate(0, 1);
		right.addComponent(atrzview.getComponent(), rectangle);
		registerView(atrzview);

		rectangle.setSize(1, 2);
		rectangle.translate(1, -1);
		right.addComponent(hintpanel, rectangle);

		rectangle = new Rectangle(0, 1, 1, 1);
		JPanel gtrPanel = new JPanel();
		gtrPanel.setLayout(new BorderLayout());
		gtrPanel.add(gtrview, BorderLayout.CENTER);
		gtrPanel.add(new JScrollBar(JScrollBar.HORIZONTAL, 0,1,0,1), BorderLayout.SOUTH);
		left.addComponent(gtrPanel, rectangle);
		gtrview.setHintPanel(hintpanel);
		gtrview.setStatusPanel(statuspanel);
		gtrview.setViewFrame(viewFrame);

		// global view
		rectangle.translate(1, 0);
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(globalview, BorderLayout.CENTER);
		panel.add(globalYscrollbar, BorderLayout.EAST);
		panel.add(globalXscrollbar, BorderLayout.SOUTH);	
		left.addComponent(panel, rectangle);
		registerView(globalview);

		// zoom view
		rectangle.translate(-1, 1);
		JPanel zoompanel = new JPanel();
		zoompanel.setLayout(new BorderLayout());
		zoompanel.add(zoomview, BorderLayout.CENTER);
		zoompanel.add(zoomXscrollbar, BorderLayout.SOUTH);	
		zoompanel.add(zoomYscrollbar, BorderLayout.EAST);
		right.addComponent(zoompanel, rectangle);
		registerView(zoomview);


		rectangle.translate(1, 0);
		JPanel textpanel = new JPanel();
		textpanel.setLayout(new BorderLayout());
		textpanel.add(textview.getComponent(), BorderLayout.CENTER);
		right.addComponent(textpanel, rectangle);
		registerView(textview);

		JSplitPane innerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				left, right);
		innerPanel.setOneTouchExpandable(true);
		innerPanel.setDividerLocation(300);

		JPanel outerPanel = new JPanel(new BorderLayout());
		outerPanel.add(innerPanel, BorderLayout.CENTER);
		outerPanel.add(getButtonBox(), BorderLayout.SOUTH);
		setLayout(new CardLayout());
		add(outerPanel, "running");

	}

	private JPanel getButtonBox() {
		// Get our border
		JPanel buttonBox = new JPanel();
		buttonBox.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		// Now add our buttons

		// The Settings button will bring up the Pixel Settings dialog
		{
			JButton settingsButton = new JButton("Settings...");
			settingsButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						ColorExtractor ce = null;
						try {
							ce = ((DoubleArrayDrawer) arrayDrawer).getColorExtractor();
						} catch (Exception e) {
						}
						PixelSettingsSelector pssSelector
									 = new PixelSettingsSelector
									(globalXmap, globalYmap,
									getZoomXmap(), getZoomYmap(),
									ce, colorPresets);
	
						JDialog popup = new ModelessSettingsDialog(viewFrame, "Pixel Settings", pssSelector);
				 		popup.addWindowListener(PropertyConfig.getStoreOnWindowClose(getDataModel().getDocumentConfig()));	
						popup.pack();
						popup.setVisible(true);
					}
				});
			buttonBox.add(settingsButton);
		}

		// The Save Data button brings up a file dialog and saves the .CDT, .GTR, and .ATR files
		{
			JButton saveButton = new JButton("Save Data...");
			saveButton.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent actionEvent) {
						JFileChooser chooser = new JFileChooser();
						int returnVal = chooser.showSaveDialog(viewFrame);
						if (returnVal == JFileChooser.APPROVE_OPTION) {
							// Save the data

							// Get the name of the file
							String filePath = chooser.getSelectedFile().getAbsolutePath();
							if (filePath.length() == 0)
								return;

							DataModelWriter writer = new DataModelWriter(dataModel);
							if (dataModel.aidFound())
								writer.writeAtr(filePath+".atr");
							if (dataModel.gidFound())
								writer.writeGtr(filePath+".gtr");
							writer.writeCdt(filePath+".cdt");
						}
					}
				});
			buttonBox.add(saveButton);
		}

		// The Export Graphics button brings up a new dialog that allows the user
		// to produce images for publication
		{
			JButton exportButton = new JButton("Export Graphics...");
			buttonBox.add(exportButton);
		}

		// The Close button exits clusterViz
		{
			JButton closeButton = new JButton("Close");
    	closeButton.addActionListener(new ActionListener() {
      	// called when close button hit
      	public void actionPerformed(ActionEvent evt) {
					viewFrame.closeWindow();
      	}
      });
      //
			buttonBox.add(closeButton);
		}
		return buttonBox;
	}


	/**
	 *  registers a modelview with the hint and status panels, and the viewFrame.
	 *
	 * @param  modelView  The ModelView to be added
	 */
	private void registerView(ModelView modelView) {
		modelView.setHintPanel(hintpanel);
		modelView.setStatusPanel(statuspanel);
		modelView.setViewFrame(viewFrame);
	}

	
	  /**
	  * prompts and returns file path, or null if cancelled.
	  */
	  private String getFilePath() {
			try {
			  JFileChooser chooser = new JFileChooser();
			  int returnVal = chooser.showSaveDialog(this);
			  if(returnVal == JFileChooser.APPROVE_OPTION) {
				 return chooser.getSelectedFile().getCanonicalPath();
			  }
			} catch (java.io.IOException ex) {
				CyLogger.getLogger(DendroView.class).warn("Got exception " + ex);
			}
			return null;
	  }
	  
	  /**
	   * show summary of the specified indexes
	   */
	  public void showSubDataModel(int [] indexes) {
	  	getViewFrame().showSubDataModel(indexes, null, null);
	  }


	/**
	 *  this function changes the info in the confignode to match the current panel sizes. 
	 * this is a hack, since I don't know how to intercept panel resizing.
	 * Actually, in the current layout this isn't even used.
	 */
	public void syncConfig() {
		/*
		DragGridPanel running   = this;
		floa	t[] heights         = running.getHeights();
		ConfigNode heightNodes[]  = root.fetch("Height");
		for (int i = 0; i < heights.length; i++) {
			if (i < heightNodes.length) {
				heightNodes[i].setAttribute("value", (double) heights[i],
						1.0 / heights.length);
			} else {
				ConfigNode n  = root.create("Height");
					n.setAttribute("value", (double) heights[i],
							1.0 / heights.length);
			}
		}

	float[] widths          = running.getWidths();
	ConfigNode widthNodes[]   = root.fetch("Width");
		for (int i = 0; i < widths.length; i++) {
			if (i < widthNodes.length) {
				widthNodes[i].setAttribute("value", (double) widths[i],
						1.0 / widths.length);
			} else {
			ConfigNode n  = root.create("Width");
				n.setAttribute("value", (double) widths[i], 1.0 / widths.length);
			}
		}
*/
	}


	/**
	 *  binds this dendroView to a particular confignode, resizing the panel sizes
	 *  appropriately.
	 *
	 * @param  configNode  ConfigNode to bind to
	 */

	public void bindConfig(ConfigNode configNode) {
		root = configNode;
		/*
	ConfigNode heightNodes[]  = root.fetch("Height");
	ConfigNode widthNodes[]   = root.fetch("Width");

	float heights[];
	float widths[];
		if (heightNodes.length != 0) {
			heights = new float[heightNodes.length];
			widths = new float[widthNodes.length];
			for (int i = 0; i < heights.length; i++) {
				heights[i] = (float) heightNodes[i].getAttribute("value", 1.0 / heights.length);
			}
			for (int j = 0; j < widths.length; j++) {
				widths[j] = (float) widthNodes[j].getAttribute("value", 1.0 / widths.length);
			}
		} else {
			widths = new float[]{2 / 11f, 3 / 11f, 3 / 11f, 3 / 11f};
			heights = new float[]{3 / 16f, 1 / 16f, 3 / 4f};
		}
		setHeights(heights);
		setWidths(widths);
		*/
	}


	protected ViewFrame viewFrame;
	/** Setter for viewFrame */
	public void setViewFrame(ViewFrame viewFrame) {
		this.viewFrame = viewFrame;
	}
	/** Getter for viewFrame */
	public ViewFrame getViewFrame() {
		return viewFrame;
	}
	// holds the thumb and zoom panels
	protected ScrollPane panes[];
	protected boolean loaded;

	private DataModel dataModel;
	/** Setter for dataModel 
	 * 
	 * */
	protected void setDataModel(DataModel dataModel) {
		this.dataModel = dataModel;
	}
	/** 
	 * 	* gets the model this dendroview is based on
	 */
	protected DataModel getDataModel() {
		return this.dataModel;
	}
	/**
	 * The following arrays allow translation to and from screen and datamatrix 
	 * I had to add these in order to have gaps in the dendroview of k-means
	 */
	private int [] arrayIndex  = null;
	private int [] geneIndex   = null;
	
    protected JScrollBar globalXscrollbar, globalYscrollbar;
    protected GlobalView globalview;

    protected JScrollBar zoomXscrollbar, zoomYscrollbar;
	protected ZoomView zoomview;
	protected TextViewManager textview;
	protected ArrayNameView arraynameview;
	protected GTRView gtrview;
	protected ATRView atrview;
	protected ATRZoomView atrzview;
	protected InvertedTreeDrawer invertedTreeDrawer;
	protected LeftTreeDrawer leftTreeDrawer;

	private TreeSelectionI geneSelection = null;
	private TreeSelectionI arraySelection = null;

	protected MapContainer globalXmap, globalYmap;
	protected MapContainer zoomXmap,   zoomYmap;

	protected MessagePanel hintpanel;
	protected MessagePanel statuspanel;
	protected BrowserControl browserControl;
	protected ArrayDrawer arrayDrawer;
	protected ConfigNode root;
	private ColorPresets colorPresets;
	private ColorPresetEditor  cpresetEditor;
	/** Setter for root  - may not work properly
	public void setConfigNode(ConfigNode root) {
		this.root = root;
	}
	/** Getter for root */
	public ConfigNode getConfigNode() {
		return root;
	}
	
	// persistent popups
	protected JDialog settingsFrame;
	protected TabbedSettingsPanel settingsPanel;
	
	private static ImageIcon treeviewIcon = null;
	/**
	 * icon for display in tabbed panel
	 */
	public ImageIcon getIcon() {
		if (treeviewIcon == null)
			try {
				treeviewIcon = new ImageIcon("images/treeview.gif", "TreeView Icon");
			} catch (java.security.AccessControlException e) {
				// need form relative URL somehow...
			}
		return treeviewIcon;
	}
	public ArrayNameView getArraynameview() {
		return arraynameview;
	}
	public ATRView getAtrview() {
		return atrview;
	}
	public GTRView getGtrview() {
		return gtrview;
	}
	public TextViewManager getTextview() {
		return textview;
	}
}
