/*
 * Created on Mar 6, 2005
 *
 * Copyright Alok Saldnaha, all rights reserved.
 */
package edu.stanford.genetics.treeview.plugin.treeanno;

import java.awt.*;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.plugin.dendroview.*;

/**
 * 
 * This is the main panel for the tree annotation editor
 * This same class will be used for editing the gene and array trees.
 *
 */
public class TreeAnnoPanel extends JPanel implements MainPanel {

	private ViewFrame viewFrame;
	private DataModel dataModel;
	private boolean windowActive;
	private NamedNodeView namedNodeView;
	private SingleNodeView singleView;
/*
	private LeftTreeDrawer leftTreeDrawer;
	private GTRView trview;
*/
//	private MapContainer globalYmap;
//	private JScrollBar globalYscrollbar;
	private TreeSelectionI selection;
	private HeaderInfo nodeInfo;
	private TableNodeView tableNodeView;

	/**
	 *  Constructor for the TreeAnno object
	 *  Note this will reuse any existing TreeAnno nodes in the documentconfig
	 *
	 * @param  tVModel   model this DendroView is to represent
	 * @param  vFrame  parent ViewFrame of DendroView
	 * @param  type   type of tree annotation to edit, either GENE_TREE or ARRAY_TREE
	 */
	public TreeAnnoPanel(DataModel tVModel, ViewFrame vFrame, int type) {
		super();
		viewFrame = vFrame;
		dataModel = tVModel;
		windowActive = true;
		if (dataModel.getDocumentConfig() != null ) {
		  bindConfig(dataModel.getDocumentConfig().fetchOrCreate("TreeAnno"));
		} else {
		  bindConfig(new DummyConfigNode("TreeAnno"));
		}

		// node info must be set before we set up views
		if (type == GENE_TREE) {
			nodeInfo = dataModel.getGtrHeaderInfo();
		} else {
			nodeInfo = dataModel.getAtrHeaderInfo();
		}
		setupViews();
		// selection must be set after we set up views
		if (type == GENE_TREE) {
			setSelection(viewFrame.getGeneSelection());
		} else {
			setSelection(viewFrame.getArraySelection());
		}
	}

	public TreeAnnoPanel(ViewFrame vFrame, ConfigNode root) {
		super();
		viewFrame = vFrame;
		dataModel = vFrame.getDataModel();
		windowActive = true;
		bindConfig(root);
		
		// node info must be set before we set up views
		if (type == GENE_TREE) {
			nodeInfo = dataModel.getGtrHeaderInfo();
		} else {
			nodeInfo = dataModel.getAtrHeaderInfo();
		}
		setupViews();
		// selection must be set after we set up views
		if (type == GENE_TREE) {
			setSelection(viewFrame.getGeneSelection());
		} else {
			setSelection(viewFrame.getArraySelection());
		}
	}
	
	/**
	 * 
	 */
	private void setupViews() {
		/*		
		globalYscrollbar = new JScrollBar(JScrollBar.VERTICAL,0,1,0,1);
		globalYmap = new MapContainer();
		globalYmap.setDefaultScale(2.0);
		globalYmap.setScrollbar(globalYscrollbar);
		leftTreeDrawer = new LeftTreeDrawer();
		trview = new GTRView();
		trview.setMap(globalYmap);
		trview.setLeftTreeDrawer(leftTreeDrawer);
		trview.getHeaderSummary().setIncluded(new int [] {0,3});
		trview.setViewFrame(viewFrame);
	*/
		namedNodeView = new NamedNodeView(nodeInfo);
		namedNodeView.setViewFrame(viewFrame);
		
		singleView = new SingleNodeView(nodeInfo);
		singleView.setViewFrame(viewFrame);
		
		
		tableNodeView = new TableNodeView(nodeInfo);
		tableNodeView.setViewFrame(viewFrame);
//		nodeEditor.setTree(trview);
		
		doSingleLayout();
		/*
		bindTrees();
		globalYmap.bindConfig(getConfigNode().fetchOrCreate("GlobalYMap"));
		// perhaps I could remember this stuff in the MapContainer...
		globalYmap.setIndexRange(0, dataModel.getDataMatrix().getNumRow() - 1);
		globalYmap.notifyObservers();
		*/
	}
	
	/**
	* this is meant to be called from setupViews.
	* It make sure that the trees are generated from the current model,
	* and enables/disables them as required.
	*
	* I factored it out because it is common betwen DendroView and KnnDendroView.
	*/
	/*
	protected void bindTrees() {
		DataModel tvmodel =  dataModel;
		if ((tvmodel != null) &&
				((type == ARRAY_TREE) && tvmodel.aidFound()
				||
				(type == GENE_TREE) && tvmodel.gidFound())) {
			try {
				trview.setEnabled(true);
				HeaderInfo trHeaderInfo;
				if (type == ARRAY_TREE) {
					leftTreeDrawer.setData(tvmodel.getAtrHeaderInfo(), tvmodel.getArrayHeaderInfo());
					trHeaderInfo = tvmodel.getAtrHeaderInfo();
				} else {
					leftTreeDrawer.setData(tvmodel.getGtrHeaderInfo(), tvmodel.getGeneHeaderInfo());
					trHeaderInfo = tvmodel.getGtrHeaderInfo();
				}
				if (trHeaderInfo.getIndex("NODECOLOR") >= 0) {
					TreeColorer.colorUsingHeader (leftTreeDrawer.getRootNode(),
					trHeaderInfo, trHeaderInfo.getIndex("NODECOLOR"));
				}	
			} catch (DendroException e) {
				Box mismatch = new Box(BoxLayout.Y_AXIS); mismatch.add(new JLabel(e.getMessage()));
				if (type == ARRAY_TREE) {
					mismatch.add(new JLabel("Perhaps there is a mismatch between your ATR and CDT files?"));
					mismatch.add(new JLabel("Ditching Array Tree, since it's lame."));
				} else {
					mismatch.add(new JLabel("Perhaps there is a mismatch between your ATR and CDT files?"));
					mismatch.add(new JLabel("Ditching Array Tree, since it's lame."));
				}
				JOptionPane.showMessageDialog(viewFrame, mismatch, "Tree Construction Error", JOptionPane.ERROR_MESSAGE);
				trview.setEnabled(false);
				try{leftTreeDrawer.setData(null, null);} catch (DendroException ex) {}
			}
		} else {
			trview.setEnabled(false);
			try{leftTreeDrawer.setData(null, null);} catch (DendroException ex) {}
		}
		leftTreeDrawer.notifyObservers();
	}
	*/
	private void doSingleLayout() {
		JPanel left = new JPanel();
		left.setLayout(new BorderLayout());
//		left.add(trview, BorderLayout.CENTER);
		left.add(namedNodeView);
//		left.add(globalYscrollbar, BorderLayout.EAST);
		JSplitPane right = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				singleView,
				tableNodeView);
		right.setResizeWeight(0.5);
		right.setOneTouchExpandable(true);
		JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				left,
				right);
		main.setResizeWeight(0.5);
		main.setOneTouchExpandable(true);
		setLayout(new BorderLayout());
		add(main, BorderLayout.CENTER);
	}
	private int type;
	public int getType() {
		return type;
	}
	/** 
	 * this shouldn't be changed once object is constructed.
	 * 
	 * @param type
	 */
	private void setType(int type) {
		this.type = type;
		if (root != null) root.setAttribute("tree_type", type, DEFAULT_TYPE);
	}
	public TreeSelectionI getSelection() {
		return selection;
	}
	public void setSelection(TreeSelectionI sel) {
		if (selection != null) {
//			selection.deleteObserver(this);	
		}
		selection = sel;
//		selection.addObserver(this);
//		trview.setGeneSelection(selection);
		namedNodeView.setSelection(sel);
		singleView.setSelection(sel);
		tableNodeView.setSelection(sel);
	}
	/*
	public MapContainer getGlobalYMap() {
		return globalYmap;
	}
	*/
	public static final int GENE_TREE = 0;
	public static final int ARRAY_TREE = 1;
	public static final int DEFAULT_TYPE = 0;
	
	private ConfigNode root;
	
	public void syncConfig() {
		// nothing to do, since type is static.
	}

	public ConfigNode getConfigNode() {
		return root;
	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.MainPanel#populateSettingsMenu(java.awt.Menu)
	 */
	public void populateSettingsMenu(Menu menu) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.MainPanel#populateAnalysisMenu(java.awt.Menu)
	 */
	public void populateAnalysisMenu(Menu menu) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.MainPanel#populateExportMenu(java.awt.Menu)
	 */
	public void populateExportMenu(Menu menu) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see edu.stanford.genetics.treeview.MainPanel#scrollToIndex(int)
	 */
	public void scrollToGene(int i) {
		LogBuffer.println("TreeAnnoPanel.scrollToGene not implemented");
	}
	public void scrollToArray(int i) {
		LogBuffer.println("TreeAnnoPanel.scrollToArray not implemented");
	}
	public void bindConfig(ConfigNode configNode) {
		root = configNode;
		setType(root.getAttribute("tree_type", DEFAULT_TYPE));
	}

	private static ImageIcon treeviewIcon = null;
	/**
	 * icon for display in tabbed panel
	 */
	public ImageIcon getIcon() {
		if (treeviewIcon == null)
			treeviewIcon = new ImageIcon("images/treeview.gif", "TreeView Icon");
		return treeviewIcon;
	}

}
