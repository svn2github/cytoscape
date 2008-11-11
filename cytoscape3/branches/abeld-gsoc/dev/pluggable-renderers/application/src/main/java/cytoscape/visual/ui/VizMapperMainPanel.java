/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
package cytoscape.visual.ui;

import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.Cytoscape;

import cytoscape.util.SwingWorker;

import cytoscape.util.swing.DropDownMenuButton;

import org.cytoscape.view.GraphView;

import cytoscape.view.CytoscapeDesktop;
import cytoscape.view.NetworkPanel;
import org.cytoscape.view.VisualProperty;
import org.cytoscape.vizmap.CalculatorCatalog;
import org.cytoscape.vizmap.VisualMappingManager;
import org.cytoscape.vizmap.VisualStyle;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * New VizMapper UI main panel.
 *
 * This panel consists of 3 panels:
 * <ul>
 * <li>Global Control Panel
 * <li>Default editor panel
 * <li>Visual Mapping Browser
 * </ul>
 *
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author Keiichiro Ono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends JPanel implements PropertyChangeListener, ChangeListener {
	private final static long serialVersionUID = 1202339867854959L;

	public enum DefaultEditor {
		NODE,
		EDGE,
		GLOBAL;
	}

	private static JPopupMenu optionMenu;
	private static JMenuItem newVS;
	private static JMenuItem renameVS;
	private static JMenuItem deleteVS;
	private static JMenuItem duplicateVS;
	private static JMenuItem createLegend;

	/*
	 * Icons used in this panel.
	 */
	private static final ImageIcon optionIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_form-properties.png"));
	private static final ImageIcon delIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_delete-16.png"));
	private static final ImageIcon addIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_data-new-table-16.png"));
	private static final ImageIcon renameIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_redo-16.png"));
	private static final ImageIcon duplicateIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_slide-duplicate.png"));
	private static final ImageIcon legendIcon = new ImageIcon(Cytoscape.class.getResource("/images/ximian/stock_graphic-styles-16.png"));

	/*
	 * This is a singleton.
	 */
	private static VizMapperMainPanel panel;

	/*
	 * Visual mapping manager. All parameters should be taken from here.
	 */
	private VisualMappingManager vmm;

	/** The Visual Style that is currently being edited by this MainPanel.
	 * Note that the value of this may change as network views are focued
	 * (i.e. this variable should be the only place it is stored in)
	 *  
	 */
	private VisualStyle currentlyEditedVS;
	
	/** The GraphView, the VisualStyle of which is currently edited.
	 * Note that the value of this may change as network views are focued
	 * (i.e. this variable should be the only place it is stored in)
	 * 
	 * Also note that it might be null (when editing a VisualStyle on its own)
	 */
	private GraphView currentView;

	private VisualStyle lastVS = null;
	private Map<VisualStyle, Image> defaultImageManager = new HashMap<VisualStyle, Image>();

	/** Creates new form AttributeOrientedPanel */
	private VizMapperMainPanel() {
		vmm = Cytoscape.getVisualMappingManager();
		vmm.addChangeListener(this);
		setMenu();

		// Need to register listener here, instead of CytoscapeDesktop.
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);
		
		// This can't be here because it would create an inifinite loop since this constructor gets called
		// when there is no CytoscapeDesktop object yet, and Cytoscape.getDesktop() would cann that constr
		// again, which would call this constr. etc.
		// as a workaround, hook up this listener in Cytoscape.getDesktop() 
		//Cytoscape.getDesktop().getSwingPropertyChangeSupport().addPropertyChangeListener(this);		
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(new VizMapListener());

		initComponents();
	}

	/**
	 * Get an instance of VizMapper UI panel. This is a singleton.
	 *
	 * @return
	 */
	public static VizMapperMainPanel getVizMapperUI() {
		if (panel == null)
			panel = new VizMapperMainPanel();

		return panel;
	}

	/**
	 * Setup menu items.<br>
	 *
	 * This includes both icon menu and right-click menu.
	 *
	 */
	private void setMenu() {
		/*
		 * Option Menu
		 */
		newVS = new JMenuItem("Create new Visual Style...");
		newVS.setIcon(addIcon);
		newVS.addActionListener(new NewStyleListener());

		deleteVS = new JMenuItem("Delete Visual Style...");
		deleteVS.setIcon(delIcon);
		deleteVS.addActionListener(new RemoveStyleListener());

		renameVS = new JMenuItem("Rename Visual Style...");
		renameVS.setIcon(renameIcon);
		renameVS.addActionListener(new RenameStyleListener());

		duplicateVS = new JMenuItem("Copy existing Visual Style...");
		duplicateVS.setIcon(duplicateIcon);
		duplicateVS.addActionListener(new CopyStyleListener());

		createLegend = new JMenuItem("Create legend from current Visual Style");
		createLegend.setIcon(legendIcon);
		createLegend.addActionListener(new CreateLegendListener());
		optionMenu = new JPopupMenu();
		optionMenu.add(newVS);
		optionMenu.add(deleteVS);
		optionMenu.add(renameVS);
		optionMenu.add(duplicateVS);
		optionMenu.add(createLegend);

		/*
		 * Build right-click menu
		 */
		// add.addActionListener(l)
		// select.setIcon(vmIcon);
	}

	public static Object showValueSelectDialog(VisualProperty type, Component caller)
	    throws Exception {
		return EditorFactory.showDiscreteEditor(type);
	}
	public VisualStyle getCurrentlyEditedVS(){
		if (currentlyEditedVS == null){
			// FIXME: this shouldn't happen, i.e. nothing should call this method before currentlyEditedVS is initialized.
			// I think it happens due to out-of-order OSGi initialization 
			currentlyEditedVS = vmm.getCalculatorCatalog().getDefaultVisualStyle();
		}
		return currentlyEditedVS;
	}
	public GraphView getCurrentView(){
		return currentView;
	}
	public CalculatorCatalog getCalculatorCatalog(){
		return vmm.getCalculatorCatalog();
	}
	/** only reason this is public is so that Cytoscape.getDesktop() can hook up event listeners */
	public VisualPropertySheetPanel getVPSP(){
		return visualPropertySheetPanel;
	}

	/**
	 * GUI initialization code based on the auto-generated code from NetBeans
	 *
	 */
	private void initComponents() {
		mainSplitPane = new javax.swing.JSplitPane();
		defaultAppearencePanel = new javax.swing.JPanel();
		visualPropertySheetPanel = new VisualPropertySheetPanel(this);
		PropertySheetPanel propertySheetPanel = visualPropertySheetPanel.getPSP();
		
		vsSelectPanel = new javax.swing.JPanel();
		vsNameComboBox = new javax.swing.JComboBox();

		defaultAppearencePanel.setMinimumSize(new Dimension(100, 100));
		defaultAppearencePanel.setPreferredSize(new Dimension(mainSplitPane.getWidth(),
		                                                      this.mainSplitPane.getDividerLocation()));
		defaultAppearencePanel.setSize(defaultAppearencePanel.getPreferredSize());
		defaultAppearencePanel.setLayout(new BorderLayout());

		mainSplitPane.setDividerLocation(120);
		mainSplitPane.setDividerSize(4);
		mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		defaultAppearencePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                              "Defaults",
		                                                                              javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                              javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                              new java.awt.Font("SansSerif",
		                                                                                                1,
		                                                                                                12),
		                                                                              java.awt.Color.darkGray));
		
		mainSplitPane.setLeftComponent(defaultAppearencePanel);

		propertySheetPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                                "Visual Mapping Browser",
		                                                                                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                                javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                                new java.awt.Font("SansSerif",
		                                                                                                  1,
		                                                                                                  12),
		                                                                                java.awt.Color.darkGray));

		mainSplitPane.setRightComponent(propertySheetPanel);

		vsSelectPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null,
		                                                                     "Current Visual Style",
		                                                                     javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
		                                                                     javax.swing.border.TitledBorder.DEFAULT_POSITION,
		                                                                     new java.awt.Font("SansSerif",
		                                                                                       1, 12),
		                                                                     java.awt.Color.darkGray));

		vsNameComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					vsNameComboBoxActionPerformed(evt);
				}
			});

		optionButton = new DropDownMenuButton(new AbstractAction() {
	private final static long serialVersionUID = 1213748836776579L;
				public void actionPerformed(ActionEvent ae) {
					DropDownMenuButton b = (DropDownMenuButton) ae.getSource();
					optionMenu.show(b, 0, b.getHeight());
				}
			});

		optionButton.setToolTipText("Options...");
		optionButton.setIcon(optionIcon);
		optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		optionButton.setComponentPopupMenu(optionMenu);

		org.jdesktop.layout.GroupLayout vsSelectPanelLayout = new org.jdesktop.layout.GroupLayout(vsSelectPanel);
		vsSelectPanel.setLayout(vsSelectPanelLayout);
		vsSelectPanelLayout.setHorizontalGroup(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                          .add(vsSelectPanelLayout.createSequentialGroup()
		                                                                                  .addContainerGap()
		                                                                                  .add(vsNameComboBox,
		                                                                                       0,
		                                                                                       146,
		                                                                                       Short.MAX_VALUE)
		                                                                                  .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                                  .add(optionButton,
		                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                       64,
		                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                  .addContainerGap()));
		vsSelectPanelLayout.setVerticalGroup(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                        .add(vsSelectPanelLayout.createSequentialGroup()
		                                                                                .add(vsSelectPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                                                                        .add(vsNameComboBox,
		                                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                                                             org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                             org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                                                                        .add(optionButton)) // .addContainerGap(
		                                                                                                                            // org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                                                            // Short.MAX_VALUE)
		));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(vsSelectPanel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     Short.MAX_VALUE)
		                                .add(mainSplitPane,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280,
		                                     Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup()
		                                         .add(vsSelectPanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(mainSplitPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              510, Short.MAX_VALUE)));
	} // </editor-fold>

	// Variables declaration - do not modify
	private JPanel defaultAppearencePanel;
	private javax.swing.JSplitPane mainSplitPane;
	private DropDownMenuButton optionButton;
	private VisualPropertySheetPanel visualPropertySheetPanel;
	private javax.swing.JComboBox vsNameComboBox;
	private javax.swing.JPanel vsSelectPanel;


	// End of variables declaration
	private void vsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
		final String vsName = (String) vsNameComboBox.getSelectedItem();
		
		if (vsName != null) {
			VisualStyle vs = getVisualStyleFromName(vsName);
			if (currentView.equals(Cytoscape.getNullNetworkView())) {
				switchVS(vs, false);
			} else {
				switchVS(vs, true);
			}
		}
	}
	/** Since vsNameComboBox stores strings, have to provide a vsName -> VisualStyle lookup. */
	private VisualStyle getVisualStyleFromName(String vsName){
		// rather than storing a String->VisualStyle Map, look up each time:
		CalculatorCatalog catalog = vmm.getCalculatorCatalog();
		VisualStyle visualStyle = null;
		for (VisualStyle vs: catalog.getVisualStyles()){
			if (vs.getName().equals(vsName))
				visualStyle = vs;
		}
		if (visualStyle == null){ // just in case
			System.out.println("FIXME: can't happen!!");
			visualStyle = catalog.getDefaultVisualStyle();
		}
		return visualStyle;
	}
	
	/** Handle 'VISUALSTYLE_CHANGED' event: switch widgets to show new VisualStyle
	 * NOTE: this is an event handler and only modifies VMMP's widgets
	 * 
	 * currentNetwork.getVisualStyle() is the new VisualStyle, ie. the switch in viewmodel layer has happend already
	 */
	private void adaptToVisualStyleChanged(){
		currentlyEditedVS = vmm.getVisualStyleForView(currentView);
		if (lastVS  == currentlyEditedVS)
			return; // nothing to do

		// update default view
		Image defImg = defaultImageManager.get(currentlyEditedVS);

		if(defImg == null) {
			System.out.println("  Default image is not available in the buffer.  Create a new one.");
			updateDefaultImage(currentlyEditedVS,
									(GraphView) ((DefaultViewPanel) DefaultAppearenceBuilder.getDefaultView(currentlyEditedVS)).getView(),
									defaultAppearencePanel.getSize());
			defImg = defaultImageManager.get(currentlyEditedVS);
		}
		// Set the default view to the panel.
		setDefaultPanel(defImg);

		vsNameComboBox.setSelectedItem(currentlyEditedVS.getName());
	}
	private void switchVS(VisualStyle vs) {
		switchVS(vs, true);
	}

	/** switch the VS of the current network to visualStyle */
	private void switchVS(VisualStyle visualStyle, boolean redraw) {
		vmm.setVisualStyleForView(currentView, visualStyle);
		
		if (redraw)
			if (currentView != null) Cytoscape.redrawGraph(currentView);
	}

	/*
	 * Set Visual Style selector combo box.
	 */
	public void initVizmapperGUI() {
		List<String> vsNames = new ArrayList<String>(vmm.getCalculatorCatalog().getVisualStyleNames());

		final VisualStyle style = currentlyEditedVS;

		// Disable action listeners
		final ActionListener[] li = vsNameComboBox.getActionListeners();

		for (int i = 0; i < li.length; i++)
			vsNameComboBox.removeActionListener(li[i]);

		vsNameComboBox.removeAllItems();


		Collections.sort(vsNames);

		for (String name : vsNames) {
			vsNameComboBox.addItem(name);

			//JPanel defPanel;
			//final Dimension panelSize = defaultAppearencePanel.getSize();
			//GraphView view;
			// FIXME: ensure that this comment below is not true any more, ie. VisualMappingManager.setVisualStyle() is not called
			// MLC 03/31/08:
			// Deceptively, getDefaultView actually actually calls VisualMappingManager.setVisualStyle()
			// so each time we add a combobox item, the visual style is changing.
			// Make sure to set the lastVSName as we change the visual style:
			/*
			view = null;
			try{
			System.out.println("visual style name: "+name);
			defPanel = DefaultAppearenceBuilder.getDefaultView(name);
			view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			} catch(Exception e){
				e.printStackTrace();
			} catch(Error e){
				e.printStackTrace();
			}
			if (view != null) {
				System.out.println("Creating Default Image for " + name);
				updateDefaultImage(name, view, panelSize);
			}*/
			//FIXME: should check that this comment is not actually used
		}

		// Switch back to the original style.
		switchVS(style);
		
		// Restore listeners
		for (int i = 0; i < li.length; i++)
			vsNameComboBox.addActionListener(li[i]);
	}

	/**
	 * Create image of a default dummy network and save in a Map object.
	 *
	 * @param vsName
	 * @param view
	 * @param size
	 */
	private void updateDefaultImage(VisualStyle visualStyle, GraphView view, Dimension size) {
		Image image = defaultImageManager.remove(visualStyle);

		if (image != null) {
			image.flush();
			image = null;
		}

		defaultImageManager.put(visualStyle, view.createImage((int) size.getWidth(), (int) size.getHeight(), 0.9));
	}

	private void setDefaultPanel(final Image defImage) {
		if (defImage == null)
			return;

		defaultAppearencePanel.removeAll();

		final JButton defaultImageButton = new JButton();
		defaultImageButton.setUI(new BlueishButtonUI());
		defaultImageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		defaultImageButton.setIcon(new ImageIcon(defImage));
		defaultAppearencePanel.add(defaultImageButton, BorderLayout.CENTER);
		defaultImageButton.addMouseListener(new DefaultMouseListener(this));
	}

	class DefaultMouseListener extends MouseAdapter {
		private VizMapperMainPanel vmmp;
		public DefaultMouseListener(VizMapperMainPanel vmmp){super(); this.vmmp = vmmp;}
		public void mouseClicked(MouseEvent e) {
			if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
				final DefaultViewPanel panel = (DefaultViewPanel) DefaultAppearenceBuilder.showDialog(Cytoscape.getDesktop(), vmmp.getCurrentlyEditedVS());
				updateDefaultImage(currentlyEditedVS, (GraphView) panel.getView(), defaultAppearencePanel.getSize());
				setDefaultPanel(defaultImageManager.get(currentlyEditedVS));

				vmm.setVisualStyleForView(currentView, currentlyEditedVS);
				Cytoscape.getDesktop().repaint();
			}
		}
	}

	/**
	 * Handle propeaty change events.
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		//System.out.println("==================GLOBAL Signal: " + e.getPropertyName() + ", SRC = " + e.getSource().toString());
		if (e.getPropertyName().equals(Cytoscape.CYTOSCAPE_INITIALIZED)) {
			setDefaultPanel(defaultImageManager.get(currentlyEditedVS));
			vsNameComboBox.setSelectedItem(currentlyEditedVS.getName());
		} else if (e.getPropertyName().equals(Cytoscape.SESSION_LOADED)
		           || e.getPropertyName().equals(Cytoscape.VIZMAP_LOADED)) {
			lastVS = null;
			initVizmapperGUI();
			adaptToVisualStyleChanged(); // FIXME: is this needed?
		} else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUS)) {
			System.out.println("vmmp got NETWORK_VIEW_FOCUS: "+Cytoscape.getCurrentNetworkView());
			// update local state:
			//System.out.println("setting vmmp.currentView: "+currentView );
			//currentView = Cytoscape.getCurrentNetworkView(); // this will return null on first call!!
			//adaptToVisualStyleChanged(); // FIXME: is this needed?
		} else if (e.getPropertyName().equals(CytoscapeDesktop.NETWORK_VIEW_FOCUSED)) {
			// update local state:
			System.out.println("vmmp got NETWORK_VIEW_FOCUSED: "+Cytoscape.getCurrentNetworkView());
			currentView = Cytoscape.getCurrentNetworkView(); // this will return null on first call!!
			adaptToVisualStyleChanged(); // FIXME: is this needed?
		} 
	}

	/*
	 * Actions for option menu
	 */
	protected class CreateLegendListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836842554L;
		public void actionPerformed(ActionEvent e) {
			final SwingWorker worker = new SwingWorker() {
				public Object construct() {
					LegendDialog ld = new LegendDialog(Cytoscape.getDesktop(), currentlyEditedVS);
					ld.setLocationRelativeTo(Cytoscape.getDesktop());
					ld.setVisible(true);

					return null;
				}
			};

			worker.start();
		}
	}

	/**
	 * Create a new Visual Style.
	 *
	 * @author kono
	 *
	 */
	private class NewStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836872046L;
		public void actionPerformed(ActionEvent e) {
			final String name = getStyleName(null);

			// If name is null, do not create style.
			if (name == null)
				return;

			// Create the new style:
			final VisualStyle newStyle = new VisualStyle(name);
			// add it to the catalog
			vmm.getCalculatorCatalog().addVisualStyle(newStyle);
			// Apply the new style
			vmm.setVisualStyleForView(currentView, newStyle);

			final JPanel defPanel = DefaultAppearenceBuilder.getDefaultView(newStyle);
			final GraphView view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			final Dimension panelSize = defaultAppearencePanel.getSize();

			if (view != null) {
				System.out.println("Creating Default Image for new visual style " + name);
				updateDefaultImage(newStyle, view, panelSize);
				setDefaultPanel(defaultImageManager.get(newStyle));
			}

			vsNameComboBox.addItem(name);
			switchVS(newStyle);
		}
	}

	/**
	 * Get a new Visual Style name by asking the user with a dialog
	 *
	 * @param s
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	private String getStyleName(VisualStyle s) {
		String suggestedName = null;

		if (s != null)
			suggestedName = vmm.getCalculatorCatalog().checkVisualStyleName(s.getName());

		// keep prompting for input until user cancels or we get a valid
		// name
		while (true) {
			String ret = (String) JOptionPane.showInputDialog(Cytoscape.getDesktop(),
			                                                  "Please enter new name for the visual style.",
			                                                  "Enter Visual Style Name",
			                                                  JOptionPane.QUESTION_MESSAGE, null,
			                                                  null, suggestedName);

			if (ret == null)
				return null;

			String newName = vmm.getCalculatorCatalog().checkVisualStyleName(ret);

			if (newName.equals(ret))
				return ret;

			int alt = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(),
			                                        "Visual style with name " + ret
			                                        + " already exists,\nrename to " + newName
			                                        + " okay?", "Duplicate visual style name",
			                                        JOptionPane.YES_NO_OPTION,
			                                        JOptionPane.WARNING_MESSAGE, null);

			if (alt == JOptionPane.YES_OPTION)
				return newName;
		}
	}

	/**
	 * Rename a Visual Style<br>
	 *
	 */
	private class RenameStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836901018L;
		public void actionPerformed(ActionEvent e) {
			final VisualStyle currentStyle = currentlyEditedVS;
			final String oldName = currentStyle.getName();
			final String newName = getStyleName(currentStyle);

			if (newName == null) {
				return;
			}
			// Update name
			currentStyle.setName(newName);
			// Update combo box (widgets)
			vsNameComboBox.removeItem(oldName);
			vsNameComboBox.addItem(newName);
			vsNameComboBox.setSelectedItem(newName);

			vmm.setVisualStyleForView( currentView, currentStyle ); // FIXME: is this actually needed?
		}
	}

	/**
	 * Remove selected visual style.
	 */
	private class RemoveStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836929313L;
		public void actionPerformed(ActionEvent e) {
			VisualStyle vs = currentlyEditedVS;
			if (vs == vmm.getCalculatorCatalog().getDefaultVisualStyle()) {
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				                              "You cannot delete default style.",
				                              "Cannot remove style!", JOptionPane.ERROR_MESSAGE);

				return;
			}

			// make sure the user really wants to do this
			final String styleName = vs.getName();
			final String checkString = "Are you sure you want to permanently delete"
			                           + " the visual style '" + styleName + "'?";
			int ich = JOptionPane.showConfirmDialog(Cytoscape.getDesktop(), checkString,
			                                        "Confirm Delete Style",
			                                        JOptionPane.YES_NO_OPTION);

			if (ich == JOptionPane.YES_OPTION) {
				final CalculatorCatalog catalog = vmm.getCalculatorCatalog();
				catalog.removeVisualStyle(vs);

				// try to switch to the default style
				VisualStyle currentStyle = catalog.getDefaultVisualStyle();

				/*
				 * Update Visual Mapping Browser.
				 */
				vsNameComboBox.removeItem(styleName);
				vsNameComboBox.setSelectedItem(currentStyle.getName());
				switchVS(currentStyle);
				defaultImageManager.remove(vs);
				visualPropertySheetPanel.removeWidgetsFor(vs);

				vmm.setVisualStyleForView( currentView, currentStyle );
				if (currentView != null) Cytoscape.redrawGraph(currentView);
			}
		}
	}

	protected class CopyStyleListener extends AbstractAction {
	private final static long serialVersionUID = 1213748836957944L;
		public void actionPerformed(ActionEvent e) {
			final VisualStyle currentStyle = currentlyEditedVS;
			VisualStyle clone = null;

			try {
				clone = (VisualStyle) currentStyle.clone();
			} catch (CloneNotSupportedException exc) {
				System.err.println("Clone not supported exception!");
				exc.printStackTrace();
			}

			final String newName = getStyleName(clone);
			if ((newName == null) || (newName.trim().length() == 0)) return;
			clone.setName(newName);

			// add new style to the catalog
			vmm.getCalculatorCatalog().addVisualStyle(clone);
			vmm.setVisualStyleForView(currentView, clone);

			final JPanel defPanel = DefaultAppearenceBuilder.getDefaultView(clone);
			final GraphView view = (GraphView) ((DefaultViewPanel) defPanel).getView();
			final Dimension panelSize = defaultAppearencePanel.getSize();

			if (view != null) {
				System.out.println("Creating Default Image for new visual style " + newName);
				updateDefaultImage(clone, view, panelSize);
				setDefaultPanel(defaultImageManager.get(newName));
			}

			vsNameComboBox.addItem(newName);
			switchVS(clone);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param e
	 *            DOCUMENT ME!
	 */
	public void stateChanged(ChangeEvent e) {
		//System.out.println("vizmappermainpanel: statechanged"+e);
		final String selectedName = (String) vsNameComboBox.getSelectedItem();
		final String currentName = currentlyEditedVS.getName();

		//System.out.println("Got VMM Change event.  Cur VS in VMM: " + currentName);

		if ((selectedName == null) || (currentName == null) || (currentView == null) || currentView.equals(Cytoscape.getNullNetworkView()))
			return;

		// Update GUI based on CalcCatalog's state.
		if (!findVSName(currentName)) {
			syncStyleBox(); // FIXME!!
		} else {
			// Bug fix: 0001802: if VS already existed in combobox, select it
			for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
				if (vsNameComboBox.getItemAt(i).equals(currentName)) {
					vsNameComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		
		// kono: should be placed here.
		// MLC 03/31/08 BEGIN:
		// Make sure we update the lastVSName based on anything that changes the visual style:
		lastVS = currentlyEditedVS;
		// MLC 03/31/08 END.
	}

	// FIXME: why is this needed at all? shouldn't the event listeners keep the combobox and the CalculatorCatalog in sync at all times?
	private void syncStyleBox() {
	///** Add all currently defined VisualStyles to vsNameComboBox */
	//private void fillStyleBox() {

		String curStyleName = currentlyEditedVS.getName();

		String styleName;
		List<String> namesInBox = new ArrayList<String>();
		namesInBox.addAll(vmm.getCalculatorCatalog().getVisualStyleNames());

		for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
			styleName = vsNameComboBox.getItemAt(i).toString();

			if (getVisualStyleFromName(styleName) == vmm.getCalculatorCatalog().getDefaultVisualStyle() && !styleName.equals("default")) {
				// No longer exists in the VMM.  Remove.
				//System.out.println("No longer exists in the VMM.  Removing in syncStyleBox()");
				vsNameComboBox.removeItem(styleName);
				defaultImageManager.remove(styleName);
				visualPropertySheetPanel.removeWidgetsFor(getVisualStyleFromName(styleName));
			}
		}

		Collections.sort(namesInBox);

		// Reset combobox items.
		vsNameComboBox.removeAllItems();

		for (String name : namesInBox)
			vsNameComboBox.addItem(name);

		// Bug fix: 0001721: 
		//Note: Because vsNameComboBox.removeAllItems() will fire unwanted event, 
		// vmm.getVisualStyle().getName() will not be the same as curStyleName
		if ((curStyleName == null) || curStyleName.trim().equals(""))
			switchVS(currentlyEditedVS);
		else
			switchVS(currentlyEditedVS);
	}

	// return true iff 'match' is found as a name within the
	// vsNameComboBox.
	private boolean findVSName(String match) {
		for (int i = 0; i < vsNameComboBox.getItemCount(); i++) {
			if (vsNameComboBox.getItemAt(i).equals(match)) {
				return true;
			}
		}

		return false;
	}
}

