
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

package cytoscape.filter.cytoscape;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.filter.model.Filter;
import cytoscape.filter.model.FilterEditorManager;
import cytoscape.filter.model.FilterManager;
import cytoscape.util.CytoscapeAction;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Iterator;



/**
 *
 */
public class CsFilter  implements BundleActivator, PropertyChangeListener {
	protected JFrame frame;
	protected FilterUsePanel filterUsePanel;

	/**
	 * Creates a new CsFilter object.
	 */
	public void start(BundleContext bc) {
		initialize();
	}

	public void stop(BundleContext bc) {
	}

	
	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void propertyChange(PropertyChangeEvent e) {
		if (e.getPropertyName() == Cytoscape.CYTOSCAPE_EXIT) {
			Iterator i = FilterManager.defaultManager().getFilters();

			// StringBuffer buffer = new StringBuffer();
			try {
				File filter_file = CytoscapeInit.getConfigFile("filter.props");

				BufferedWriter writer = new BufferedWriter(new FileWriter(filter_file));

				while (i.hasNext()) {
					try {
						Filter f = (Filter) i.next();
						writer.write(FilterManager.defaultManager().getFilterID(f) + "\t"
						             + f.getClass() + "\t" + f.output());
						writer.newLine();
					} catch (Exception ex) {
						System.out.println("Error with Filter output");
					}
				}

				writer.close();
			} catch (Exception ex) {
				System.out.println("Filter Write error");
				ex.printStackTrace();
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void initialize() {
		Cytoscape.getSwingPropertyChangeSupport().addPropertyChangeListener(this);

		try {
			File filter_file = CytoscapeInit.getConfigFile("filter.props");
			BufferedReader in = new BufferedReader(new FileReader(filter_file));
			String oneLine = in.readLine();

			while (oneLine != null) {
				if (oneLine.startsWith("#")) {
					// comment
				} else {
					FilterManager.defaultManager().createFilterFromString(oneLine);
				}

				oneLine = in.readLine();
			}

			in.close();
		} catch (Exception ex) {
			System.out.println("Filter Read error");
			ex.printStackTrace();
		}

		// create icons
		ImageIcon icon = new ImageIcon(getClass().getResource("/images/ximian/stock_filter-data-by-criteria.png"));
		ImageIcon icon2 = new ImageIcon(getClass()
		                                    .getResource("/images/ximian/stock_filter-data-by-criteria-16.png"));

		// 
		//FilterPlugin action = new FilterPlugin(icon, this);
		FilterMenuItem menu_action = new FilterMenuItem(icon2, this);
		//Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menu_action);

		//CytoscapeDesktop desktop = Cytoscape.getDesktop();
		//CyMenus cyMenus = desktop.getCyMenus();
		//CytoscapeToolBar toolBar = cyMenus.getToolBar();
		//JButton button = new JButton(icon);
		//button.addActionListener(action);
		//button.setToolTipText("Create and apply filters");
		//button.setBorderPainted(false);
		//toolBar.add(button);

		FilterEditorManager.defaultManager().addEditor(new NumericAttributeFilterEditor());
		FilterEditorManager.defaultManager().addEditor(new StringPatternFilterEditor());
		FilterEditorManager.defaultManager().addEditor(new NodeTopologyFilterEditor());
		FilterEditorManager.defaultManager().addEditor(new BooleanMetaFilterEditor());
		FilterEditorManager.defaultManager().addEditor(new NodeInteractionFilterEditor());
		FilterEditorManager.defaultManager().addEditor(new EdgeInteractionFilterEditor());
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public FilterUsePanel getFilterUsePanel() {
		if (filterUsePanel == null) {
			filterUsePanel = new FilterUsePanel(frame);
		}

		return filterUsePanel;
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void show() {
		if (frame == null) {
			frame = new JFrame("Use Filters");
			frame.getContentPane().add(getFilterUsePanel());
			frame.pack();

			//Cytoscape.getDesktop().getCytoPanel( SwingConstants.SOUTH ).add(getFilterUsePanel()); 
		}

		frame.setVisible(true);
	}
}
