/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: ScatterPanel.java,v $
 * $Revision: 1.4 $
 * $Date: 2006/09/28 12:45:34 $
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
package edu.stanford.genetics.treeview.plugin.scatterview;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.*;

import edu.stanford.genetics.treeview.*;

/**
*  ScatterPanel make scatterplots from an SPDatasource which are linked to other views by a TreeSelection object.
*
*/

public class ScatterPanel extends JPanel implements MainPanel, java.util.Observer {
    public String[]  getHints() {
	String [] hints = {
	    "Click to select points",
	};
	return hints;
    }
	private ConfigNode configNode;
	/** Setter for configNode */
	public void setConfigNode(ConfigNode configNode) {
		this.configNode = configNode;
	}
	/** Getter for configNode */
	public ConfigNode getConfigNode() {
		return configNode;
	}
	
	ScatterView scatterPane;
	/** Setter for scatterPane */
	public void setScatterPane(ScatterView scatterPane) {
		this.scatterPane = scatterPane;
	}
	/** Getter for scatterPane */
	public ScatterView getScatterPane() {
		return scatterPane;
	}
	ScatterParameterPanel scatterParameterPanel;
	private LinkedViewFrame viewFrame;
	/** Setter for viewFrame */
	public void setViewFrame(LinkedViewFrame viewFrame) {
		this.viewFrame = viewFrame;
	}
	/** Getter for viewFrame */
	public LinkedViewFrame getViewFrame() {
		return viewFrame;
	}
	public void scaleScatterPane() {
		System.out.println("scatterPane resized");
	}
	
	private HorizontalAxisPane horizontalAxisPane = null;
	private VerticalAxisPane verticalAxisPane = null;
	public ScatterPanel(LinkedViewFrame viewFrame,ConfigNode configNode)	{
		setViewFrame(viewFrame);
		setLayout(new BorderLayout());
		setConfigNode(configNode);
		int xType =  configNode.getAttribute("xtype", 0);
		int yType =  configNode.getAttribute("ytype", 0);
		int xIndex = configNode.getAttribute("xindex", 0);
		int yIndex = configNode.getAttribute("yindex", 0);

		SPDataSource dataSource = new DataModelSource(xType, yType, xIndex, yIndex);
		scatterPane = new ScatterView(dataSource);
		ScatterColorPresets colorPresets = ScatterplotFactory.getColorPresets();
		scatterPane.setDefaultColorSet(colorPresets.getDefaultColorSet());
		scatterPane.setConfigNode(getFirst("ScatterView"));

/*
		scrollPane = new JScrollPane(scatterPane);
		verticalAxisPane = new VerticalAxisPane(scatterPane.getYAxisInfo(), 
			scatterPane.getColorSet());
		scrollPane.setRowHeaderView(verticalAxisPane);
		horizontalAxisPane = new HorizontalAxisPane(scatterPane.getXAxisInfo(), 
			scatterPane.getColorSet());
		scrollPane.setColumnHeaderView(horizontalAxisPane);
		add(scrollPane, BorderLayout.CENTER);
*/			
		
		add(scatterPane.getComponent(), BorderLayout.CENTER);
		scatterParameterPanel = new ScatterParameterPanel(scatterPane, this);
		add(scatterParameterPanel, BorderLayout.NORTH);
		
	}
	

	public void showDisplayPopup() {
		SettingsPanel displayPanel = new DisplaySettingsPanel(scatterPane, ScatterplotFactory.getColorPresets(), 
		viewFrame);
		JDialog popup = new ModelessSettingsDialog(viewFrame, "Display", displayPanel);
		popup.addWindowListener(XmlConfig.getStoreOnWindowClose(getViewFrame().getDataModel().getDocumentConfig()));
		popup.pack();
		popup.show();
	}

    // Observer
    public void update(Observable o, Object arg) {	
	if (o == selection) {
	    scatterPane.selectionChanged();
	} else {
	    System.out.println("Scatterview got funny update!");
	}
    }
	TreeSelectionI selection;
    public void setSelection(TreeSelectionI selection) {
	if (this.selection != null) {
	    this.selection.deleteObserver(this);
	}
	this.selection = selection;
	this.selection.addObserver(this);
    }
    
    //  main Panel
		/**
	 *  This syncronizes the sub compnents with their persistent storage.
	 */
	public void syncConfig() {
	}


	/**
	 *  Add items related to settings
	 *
	 * @param  menu  A menu to add items to.
	 */
	public void populateSettingsMenu(JMenu menu) {
		JMenuItem dispItem = new JMenuItem("Display...");
		dispItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDisplayPopup();
			}
		});
		menu.add(dispItem);
	}


	/**
	 *  Add items which do some kind of analysis
	 *
	 * @param  menu  A menu to add items to.
	 */
	public void populateAnalysisMenu(JMenu menu) {
	}


	/**
	 *  Add items which allow for export, if any.
	 *
	 * @param  menu  A menu to add items to.
	 */
	public void populateExportMenu(JMenu menu) {

		JMenuItem bitmapItem = new JMenuItem("Export to Image...");
		bitmapItem.addActionListener(new ActionListener() {
		  public void actionPerformed(ActionEvent actionEvent) {

			  BitmapScatterViewExportPanel bitmapPanel = new BitmapScatterViewExportPanel
			(scatterPane);
			bitmapPanel.setSourceSet(viewFrame.getDataModel().getFileSet());

			final JDialog popup = new CancelableSettingsDialog(viewFrame, "Export to Image...", bitmapPanel);
			popup.setSize(500,300);
			popup.show();
		  }
		});
		menu.add(bitmapItem);
	}


	/**
	 *  ensure a particular index is visible. Used by Find.
	 *
	 * @param  i  Index of gene in cdt to make visible
	 */
	public void scrollToGene(int i) {
		LogBuffer.println("ScatterPanel.scrollToGene not implemented");
	}
	public void scrollToArray(int i) {
		LogBuffer.println("ScatterPanel.scrollToArray not implemented");
	}

	
/*
 * this class encapsulates the possible ways to extract per-gene stats.
 */
 
    public static final int INDEX = 0;  // stat is simple gene index
    public static final int RATIO = 1;  // stat is an array ratio
    public static final int PREFIX = 2; // stat is a prefix column

	/**
	* always returns an instance of the node, even if it has to create it.
	*/
	private ConfigNode getFirst(String name) {
		ConfigNode cand = getConfigNode().fetchFirst(name);
		return (cand == null)? getConfigNode().create(name) : cand;
	}
	
	/**
	 * This class probably belongs in the scatterview package. Oh well.
	 */
	class DataModelSource implements SPDataSource {
		private int xIndex; // meaningful for RATIO and PREFIX
		private int xType;
		
		private int yIndex; // meaningful for RATIO and PREFIX
		private int yType;
		
		public int getNumPoints() {
			return getViewFrame().getDataModel().getDataMatrix().getNumRow();
		}
		public double getX(int i) throws NoValueException {
			if (xVals == null) setupVals();
			if (xVals[i] == DataModel.NODATA) throw new NoValueException("NODATA");
			if (xVals[i] == DataModel.EMPTY) throw new NoValueException("EMPTY");
			return xVals[i];
			// return getValue(xType, xIndex, i);
		}
		public double getY(int i) throws NoValueException {
			if (yVals == null) setupVals();
			if (yVals[i] == DataModel.NODATA) throw new NoValueException("NODATA");
			return yVals[i];
			//	return getValue(yType, yIndex, i);
		}
		public String getLabel(int geneIndex) {
			DataModel tvmodel = getViewFrame().getDataModel();
			HeaderInfo info = tvmodel.getGeneHeaderInfo();
			return  info.getHeader(geneIndex) [info.getIndex("YORF")];
		}
		public Color getColor(int i) {
			if (getViewFrame().geneIsSelected(i)) {
				return scatterPane.getColorSet().getColor("Selected");
			} else {
				return scatterPane.getColorSet().getColor("Data");
			}
		}
		public String getTitle() {
			return getXLabel() + " vs. " + getYLabel();
		}
		public String getXLabel() {
			return getName(xType, xIndex);
		}
		public String getYLabel() {
			return getName(yType, yIndex);
		}
		
		public void select(int i) {
			getViewFrame().extendRange(i);
		}
		double [] xVals = null;
		double [] yVals = null;
		private void setupVals() {
			int n = getNumPoints();
			xVals = new double [n];
			yVals = new double [n];
			for (int i = 0; i < n; i++) {
				xVals[i] = getSimpleValue(xType, xIndex, i);
				yVals[i] = getSimpleValue(yType, yIndex, i);
			}
		}
		public void select(double xL, double yL, double xU, double yU) {
			if (xVals == null) setupVals();
			int n = getNumPoints();
			int first = -1;
			int last = -1;
			//		TreeSelection treeSelection = getViewFrame().getGeneSelection();
			TreeSelectionI treeSelection = selection;
			for (int i = 0; i < n; i++) {
				double x = xVals[i];
				if (x == DataModel.NODATA) continue;
				double y = yVals[i];
				if (y == DataModel.NODATA) continue;
				
				if ((x > xL) && (x < xU) && (y > yL) && (y < yU)) {
					//								System.out.println("selecting (" +x+ ", " + y +")");
					treeSelection.setIndex(i, true);
					last = i;
					if (first == -1) first = i;
					//				select(i);
				}
			}
			if (last != -1) {
				if (treeSelection.getMinIndex() == -1) {
					getViewFrame().seekGene(first);
				}
				treeSelection.notifyObservers();
				getViewFrame().scrollToGene(first);
			}
		}
		public void deselectAll() {
			getViewFrame().deselectAll();
		}
		public boolean isSelected(int i) {
			return getViewFrame().geneIsSelected(i);
		}
		public DataModelSource(int xT, int yT, int xI, int yI) {
			xType = xT;
			yType = yT;
			
			xIndex = xI;
			yIndex = yI;
		}
		
		/**
		 * throws exception on nodata.
		 */
		public double getValue(int type, int index, int geneIndex) 
		throws NoValueException {
			if (type == ScatterPanel.INDEX) return geneIndex;
			DataModel tvmodel = getViewFrame().getDataModel();
			if (type == ScatterPanel.RATIO) {
				DataMatrix dataMatrix = tvmodel.getDataMatrix();
				double val = dataMatrix.getValue(index, geneIndex);
				if (val == DataModel.NODATA) {
					throw new NoValueException("NODATA");
				} else {
					return val;
				}
			}
			if (type == ScatterPanel.PREFIX) {
				HeaderInfo info = tvmodel.getGeneHeaderInfo();
				String sval = info.getHeader(geneIndex) [index];
				if (sval == null) {
					throw new NoValueException("NODATA");
				} else {
					Double d = new Double(sval);
					return d.doubleValue();
				}
			}
			System.out.println("Illegal Type Specified");
			throw new NoValueException("Illegal Type Specified");
		}
		/**
		 * just returns the value, even if it's no data.
		 */
		public double getSimpleValue(int type, int index, int geneIndex) {
			DataModel tvmodel =  getViewFrame().getDataModel();
			switch (type) {
			case ScatterPanel.INDEX:
				return geneIndex;
			case ScatterPanel.RATIO:
				DataMatrix dataMatrix = tvmodel.getDataMatrix();
			return dataMatrix.getValue(index, geneIndex);
			case ScatterPanel.PREFIX:
				HeaderInfo info = tvmodel.getGeneHeaderInfo();
			String sval = info.getHeader(geneIndex) [index];
			if (sval == null) {
				return DataModel.NODATA;
			} else {
				Double d = new Double(sval);
				return d.doubleValue();
				
			}
			}
			System.out.println("Illegal Type Specified");
			return DataModel.NODATA;
		}
		public String getName(int type, int index) {
			if (type == ScatterPanel.INDEX) {
				return "INDEX";
			}
			DataModel tvmodel = getViewFrame().getDataModel();
			if (type == ScatterPanel.RATIO) {
				HeaderInfo info = tvmodel.getArrayHeaderInfo();
				return info.getHeader(index) [0];
			}
			if (type == ScatterPanel.PREFIX) {
				HeaderInfo info = tvmodel.getGeneHeaderInfo();
				return info.getNames() [index];
			}
			return null;
		}
		
	}
	
	private static ImageIcon scatterIcon = null;
	/**
	 * icon for display in tabbed panel
	 */
	public ImageIcon getIcon() {
		if (scatterIcon == null) {
			try {
				scatterIcon = new ImageIcon("images/plot.gif", "Plot Icon");
			} catch (java.security.AccessControlException e) {
				// need form relative URL somehow...
			}		
		}
		return scatterIcon;
	}	
	
	
}
