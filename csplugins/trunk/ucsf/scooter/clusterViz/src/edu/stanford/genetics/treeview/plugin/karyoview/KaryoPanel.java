/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: alokito $
 * $RCSfile: KaryoPanel.java,v $
 * $Revision: 1.2 $
 * $Date: 2006/09/27 04:23:43 $
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ProgressMonitor;

import edu.stanford.genetics.treeview.*;
import edu.stanford.genetics.treeview.model.TVModel;

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
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version $Revision: 1.2 $ $Date: 2006/09/27 04:23:43 $
 */
public class KaryoPanel extends DragGridPanel implements MainPanel {
	/**
	 *  Constructor for the KaryoPanel object
	 *
	 * @param  tvmodel   data model to represent
	 * @param geneSelection shared selection model 
	 * @param  vFrame  parent ViewFrame of DendroView
	 * @param configNode config node to take state from and store state in.
	 */
	public KaryoPanel(DataModel tvmodel, TreeSelectionI geneSelection, ViewFrame vFrame, ConfigNode configNode) {
		super(2, 3);
		viewFrame = vFrame;

		startingGenome = new Genome(tvmodel);
		genome = startingGenome;

		bindConfig(configNode);
		// requires that genome be set...
		karyoDrawer = new KaryoDrawer(genome, geneSelection, DataModel.NODATA);
		karyoDrawer.bindConfig(getFirst("KaryoDrawer"));
		karyoView = new KaryoView(karyoDrawer, tvmodel);
		karyoView.bindConfig(getFirst("KaryoView"));
	    parameterPanel = new KaryoViewParameterPanel(karyoDrawer, karyoView, this);
	    statusPanel = new MessagePanel("View Status");

		windowActive = true;
		setBorderWidth(2);
		setBorderHeight(2);
		setMinimumWidth(1);
		setMinimumHeight(1);
		setFocusWidth(1);
		setFocusHeight(1);

		setupViews();
		
		
		coordinatesTimer = new javax.swing.Timer(1000, new TimerListener());
		coordinatesTimer.stop();

		try {
			CoordinatesPresets coordinatesPresets = KaryoscopeFactory.getCoordinatesPresets();
			if (configNode.getAttribute("coordinates", "").length() > 0) {
				FileSet source = new FileSet(configNode.getAttribute("coordinates", ""),
						vFrame.getApp().getCodeBase().toString() +"coordinates/");
				getGenome(source);
			} else if (coordinatesPresets.getDefaultIndex() != -1) {
				// requires that karyoView be set, so we can get header info for matching.
				int index = coordinatesPresets.getDefaultIndex();
				FileSet def = coordinatesPresets.getFileSet(index);
//				System.out.println("default index is " + index +", fileset " + def);
				getGenome(def);
			} else {
				useOriginal();
			}
		} catch (LoadException e) {
			LogBuffer.println("Error loading coordinates " + e);
			e.printStackTrace();
			useOriginal();
		}

		KaryoColorPresets colorPresets = KaryoscopeFactory.getColorPresets();

		if (colorPresets.getDefaultIndex() != -1) {
			karyoDrawer.getKaryoColorSet().copyStateFrom(colorPresets.getDefaultColorSet());
		}

	}
	public void getGenome(FileSet fileSet) throws LoadException {
		TVModel model = new TVModel();
		model.setFrame(viewFrame);
		model.loadNew(fileSet);
		getGenome(model);
	}
	ProgressMonitor coordinatesMonitor;
	javax.swing.Timer coordinatesTimer;
	CoordinatesTask coordinatesTask;
	CoordinatesSettingsPanel coordinatesPanel = null;
	class TimerListener implements ActionListener { // manages the averagermonitor
		public void actionPerformed(ActionEvent evt) {
			if (coordinatesMonitor.isCanceled() || coordinatesTask.done()) {
				coordinatesMonitor.close();
				coordinatesTask.stop();
				// Toolkit.getDefaultToolkit().beep();
				coordinatesTimer.stop();
				if (coordinatesTask.done()) {
					coordinatesMonitor.setNote("Matching complete");
				}
				if (coordinatesPanel != null) {
					coordinatesPanel.setEnabled(true);
				}
			} else {
				coordinatesMonitor.setNote(coordinatesTask.getMessage());
				coordinatesMonitor.setProgress(coordinatesTask.getCurrent());
			}
			repaint();
		}
	}
	class CoordinatesTask {
		private int current = 0;
		private String statMessage;
		
		
		/**
		* Called to start the task. I don't know why we bother with the ActualTask class, so don't ask.
		*/
		void go(DataModel tvmodel) {
			final DataModel model = tvmodel;
			setCurrent(0);
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					return new ActualTask(model);
				}
			};
			worker.start();
		}
		
		
		/**
		* Called from ProgressBarDemo to find out how much work needs
		* to be done.
		*/
		int getLengthOfTask() {
			HeaderInfo existingHeaders = karyoView.getGeneInfo();
			return existingHeaders.getNumHeaders();
		}
		
		/**
		* Called from ProgressBarDemo to find out how much has been done.
		*/
		int getCurrent() {
			return current;
		}
		void setCurrent(int i) {
			current = i;
		}
		public void incrCurrent() { 
			current++;
		}
		/**
		* called to stop the averaging on a cancel...
		*/
		void stop() {
			current = getLengthOfTask();
		}
		
		
		/**
		* Called from ProgressBarDemo to find out if the task has completed.
		*/
		boolean done() {
			if (current >= getLengthOfTask()) {
				return true;
			} else {
				return false;
			}
		}
		
		String getMessage() {
			return statMessage;
		}
		class ActualTask {
			ActualTask(DataModel newModel) {
				Genome newGenome = new Genome(newModel);
				
				// set indexes to -1...
				int n = newGenome.getNumLoci();
				for (int i = 0; i < n ; i++) {
					newGenome.getLocus(i).setCdtIndex(-1);
				}
				
				statMessage = "Hashing new keys ";
				HeaderInfo newHeaders = newModel.getGeneHeaderInfo();
				int newN = newHeaders.getNumHeaders();
				Hashtable tempTable = new Hashtable((newN * 4) / 3, .75f);
				for (int j = 0; j < newN; j++) {
					tempTable.put(newHeaders.getHeader(j, "YORF"), new Integer(j));
				}

				statMessage = "Performing lookups";
				// match up indexes with using headerinfo...
				HeaderInfo existingHeaders = karyoView.getGeneInfo();
				int existingN =  existingHeaders.getNumHeaders();
				for (int i = 0; i < existingN; i++) {
					incrCurrent();
					String thisID = existingHeaders.getHeader(i, "YORF");
					if (thisID == null) continue;
					if (thisID.equals("")) continue;
					Integer j = (Integer) tempTable.get(thisID);
					if (j != null) {
						newGenome.getLocus(j.intValue()).setCdtIndex(i);
					} else {
						LogBuffer.println("Missing locus for " + thisID);
					}
					if (done()) break;
				}
				KaryoPanel.this.setGenome(newGenome);
				karyoDrawer.setGenome(newGenome);
				karyoView.recalculateAverages();
				karyoView.redoScale();
				stop();
			}
		}
	}

	public void getGenome(DataModel newModel) {
		coordinatesTask = new CoordinatesTask();
		coordinatesMonitor = new ProgressMonitor(this,
		"Finding matching coordinates",
		"Note", 0, coordinatesTask.getLengthOfTask());
		coordinatesMonitor.setProgress(0);
		coordinatesTask.go(newModel);
		coordinatesTimer.start();
	}
	public void useOriginal() {
		karyoDrawer.setGenome(startingGenome);
		karyoView.recalculateAverages();
		karyoView.redoScale();
	}
	
	/**
	 *  Sets the windowActive attribute of the DendroView object
	 *
	 * @param  flag  The new windowActive value
	 */
	private void setWindowActive(boolean flag) {
		windowActive = flag;
	}



	// accessors
	/**
	 *  This method should be called only during initial setup of the modelview
	 *
	 *  It sets up the views, which are reinitialized if the model
	 *  changes.
	 *
	 */
	private void setupViews() {

	    Rectangle rectangle  = new Rectangle(0, 0, 1, 1);
	    
	    addComponent(new JScrollPane(parameterPanel), rectangle);
	    rectangle.translate(1, 0);
	    
	    addComponent(statusPanel, rectangle);

	    rectangle.setSize(2, 2);
	    rectangle.translate(-1, 1);
	    addComponent(karyoView.getComponent(), rectangle);
		karyoView.setParameterPanel(parameterPanel);
	    karyoView.setStatusPanel(statusPanel);
	    karyoView.setViewFrame(viewFrame);

	    rectangle.translate(0, 1);
	    // addView(KaryoZoomView);
	}

	/**
	 *  Adds a component to the DendroView
	 *
	 * @param  component  The component to be added
	 * @param  rectangle  The location to add it in
	 */
	public void addComponent(Component component, Rectangle rectangle) {
		if (component != null) {
			addComponent(component, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		}
	}


	/**
	 *  Adds a ModelView to the KaryoPanel
	 *
	 * @param  modelView  The ModelView to be added
	 * @param  rectangle  The location to add it in
	 */
	public void addView(ModelView modelView, Rectangle rectangle) {
		addComponent(modelView.getComponent(), rectangle);
		modelView.setStatusPanel(statusPanel);
		modelView.setViewFrame(viewFrame);
	}


	/**
	 *  Determines if window is currently active
	 *
	 * @return    returns true if active
	 */
	public boolean windowActive() {
		return windowActive;
	}


	// MainPanel
		/**
	 *  This makes the persistent storage resemble the compnents, if it doesn't already. 
	 */
	public void syncConfig() {
	}

	
	public void showDisplayPopup() {
		SettingsPanel avePanel = new DisplaySettingsPanel(this, KaryoscopeFactory.getColorPresets(), viewFrame);
		JDialog popup = new ModelessSettingsDialog(viewFrame, "Display", avePanel);
		popup.addWindowListener(XmlConfig.getStoreOnWindowClose(viewFrame.getDataModel().getDocumentConfig()));
		popup.pack();
		popup.show();
	}
	public void showCoordinatesPopup() {
		coordinatesPanel = new CoordinatesSettingsPanel(KaryoPanel.this, 
				KaryoscopeFactory.getCoordinatesPresets(), viewFrame);
		JDialog popup = new ModelessSettingsDialog(viewFrame, "Coordinates", coordinatesPanel);
		popup.addWindowListener(XmlConfig.getStoreOnWindowClose(viewFrame.getDataModel().getDocumentConfig()));
		popup.pack();
		popup.show();
	}

	public void showAveragingPopup() {
		SettingsPanel avePanel = karyoView.getAveragerSettingsPanel();
		JDialog popup = new ModelessSettingsDialog(viewFrame, "Averaging", avePanel);
		popup.addWindowListener(XmlConfig.getStoreOnWindowClose(viewFrame.getDataModel().getDocumentConfig()));
		popup.pack();
		popup.show();
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

		JMenuItem aveItem = new JMenuItem("Averaging...");
		aveItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAveragingPopup();
			}
		});
		menu.add(aveItem);

		JMenuItem coordItem = new JMenuItem("Coordinates...");
		coordItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showCoordinatesPopup();
			}
		});
		menu.add(coordItem);
		JMenuItem urlItem = new JMenuItem("Url Links...");
		urlItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsPanel urlPanel = new UrlSettingsPanel(viewFrame.getUrlExtractor() , viewFrame.getGeneUrlPresets());
				JDialog popup = new ModelessSettingsDialog(viewFrame, "Url Linking", urlPanel);
				popup.pack();
				popup.show();
			}
		});
		menu.add(urlItem);
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

			  BitmapKaryoViewExportPanel bitmapPanel = new BitmapKaryoViewExportPanel
			(karyoView);
			bitmapPanel.setSourceSet(viewFrame.getDataModel().getFileSet());

			final JDialog popup = new CancelableSettingsDialog(viewFrame, "Export to Image", bitmapPanel);
			popup.pack();
			int width = popup.getWidth();
			int height = popup.getHeight();
			if (width < 500) width = 500;
			if (height < 300) height = 300;
			popup.setSize(width, height);
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
		//LogPanel.println("KaryoPanel.scrollToGene not implemented");
	}
	public void scrollToArray(int i) {
		//LogPanel.println("KaryoPanel.scrollToArray not implemented");
	}

	private ConfigNode configNode;
	/** Setter for configNode */
	public void bindConfig(ConfigNode configNode) {
		this.configNode = configNode;
	}
	/** Getter for configNode */
	public ConfigNode getConfigNode() {
		return configNode;
	}
private ViewFrame viewFrame;
	private boolean loaded;

	private boolean windowActive;
	/** store original coordinates here... */
	private Genome startingGenome;
	private Genome genome;
	/** Setter for genome */
	public void setGenome(Genome genome) {
		FileSet fileSet = genome.getFileSet();
		configNode.setAttribute("coordinates", 
				fileSet.getRoot() + fileSet.getExt(),
				"");
		this.genome = genome;
	}
	/** Getter for genome */
	public Genome getGenome() {
		return genome;
	}
	
	private KaryoDrawer karyoDrawer;
	/** Setter for karyoDrawer */
	public void setKaryoDrawer(KaryoDrawer karyoDrawer) {
		this.karyoDrawer = karyoDrawer;
	}
	/** Getter for karyoDrawer */
	public KaryoDrawer getKaryoDrawer() {
		return karyoDrawer;
	}

	private KaryoView karyoView;
	/** Setter for karyoView */
	public void setKaryoView(KaryoView karyoView) {
		this.karyoView = karyoView;
	}
	/** Getter for karyoView */
	public KaryoView getKaryoView() {
		return karyoView;
	}

    private KaryoViewParameterPanel parameterPanel;

	private MessagePanel statusPanel;

	/**
	* always returns an instance of the node, even if it has to create it.
	*/
	private ConfigNode getFirst(String name) {
		ConfigNode cand = getConfigNode().fetchFirst(name);
		return (cand == null)? getConfigNode().create(name) : cand;
	}
	private static ImageIcon karyoIcon = null;
	/**
	 * icon for display in tabbed panel
	 */
	public ImageIcon getIcon() {
		if (karyoIcon == null) {
			try {
			karyoIcon = new ImageIcon("images/karyoscope.gif", "Karyoscope Icon");
			} catch (java.security.AccessControlException e) {
				// need form relative URL somehow...
			}		
		}
		return karyoIcon;
	}
}

