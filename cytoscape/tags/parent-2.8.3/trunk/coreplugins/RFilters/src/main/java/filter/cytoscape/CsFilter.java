
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

package filter.cytoscape;

import cytoscape.*;
import cytoscape.data.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.view.*;
import filter.model.*;
import filter.view.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;


/**
 *
 */
public class CsFilter extends CytoscapePlugin { //implements PropertyChangeListener {
	protected JFrame frame;
	protected FilterUsePanel filterUsePanel;

	/**
	 * Creates a new CsFilter object.
	 */
	public CsFilter() {
		initialize();
	}
	
	/**
	 *  DOCUMENT ME!
	 */
	public void initialize() {

		// create icons
		ImageIcon icon = new ImageIcon(getClass().getResource("/stock_filter-data-by-criteria.png"));
		ImageIcon icon2 = new ImageIcon(getClass()
		                                    .getResource("/stock_filter-data-by-criteria-16.png"));

		//FilterPlugin action = new FilterPlugin(icon, this);
		FilterMenuItem menu_action = new FilterMenuItem(icon2, this);
		//Cytoscape.getDesktop().getCyMenus().addCytoscapeAction( ( CytoscapeAction )action );
		Cytoscape.getDesktop().getCyMenus().addCytoscapeAction((CytoscapeAction) menu_action);

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
		}

		frame.setVisible(true);
	}
	
	// override the following two methods to save state.
	/**
	 * DOCUMENT ME!
	 * 
	 * @param pStateFileList
	 *            DOCUMENT ME!
	 */
	public void restoreSessionState(List<File> pStateFileList) {
	
		if ((pStateFileList == null) || (pStateFileList.size() == 0)) {
			cytoscape.logger.CyLogger.getLogger(CsFilter.class).warn("\tNo previous old filter state to restore.");
			return;
		}

		try {
			File filter_file = pStateFileList.get(0);
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
			cytoscape.logger.CyLogger.getLogger(CsFilter.class).error("Filter Read error",ex);
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param pFileList
	 *            DOCUMENT ME!
	 */
	public void saveSessionStateFiles(List<File> pFileList) {
		
		Iterator i = FilterManager.defaultManager().getFilters();

		// If not filter defined, do nothing
		if (!i.hasNext()){
			return;
		}

		// There are filters, save them now
		
		// Create an empty file on system temp directory
		String tmpDir = System.getProperty("java.io.tmpdir");
		File session_filter_file = new File(tmpDir, "old_filters.props");

		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(session_filter_file));
			while (i.hasNext()) {
				Filter f = (Filter) i.next();
				writer.write(FilterManager.defaultManager().getFilterID(f) + "\t"
						+ f.getClass() + "\t" + f.output());
				writer.newLine();
			}
		} 

		catch (Exception ex) {
			cytoscape.logger.CyLogger.getLogger(CsFilter.class).error("Session old filter Write error",ex);
		}

		finally {
			if ( writer != null) {
				try {
					writer.close();            		
				}
				catch (Exception ex){
					cytoscape.logger.CyLogger.getLogger(CsFilter.class).error("Session old filter Write error",ex);            		
				}
			}
		}	

        if ((session_filter_file != null) && (session_filter_file.exists())) {
            pFileList.add(session_filter_file);
        }
	}
}
