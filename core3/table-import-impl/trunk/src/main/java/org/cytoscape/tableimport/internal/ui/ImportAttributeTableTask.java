
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

package org.cytoscape.tableimport.internal.ui;

import org.cytoscape.io.read.CyNetworkViewReader;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableEntry;
import org.cytoscape.model.CyTableFactory;
import org.cytoscape.tableimport.internal.reader.GraphReader;
import org.cytoscape.tableimport.internal.util.CytoscapeServices;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.vizmap.VisualStyle;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskMonitor;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import org.cytoscape.tableimport.internal.reader.TextTableReader;
import org.cytoscape.io.read.CyTableReader;
import org.cytoscape.task.table.MapNetworkAttrTask;
import org.cytoscape.tableimport.internal.reader.TextTableReader.ObjectType;

/**
 *
 */
public class ImportAttributeTableTask extends AbstractTask implements CyTableReader {

	protected CyNetworkView[] cyNetworkViews;
	protected VisualStyle[] visualstyles;

	private final TextTableReader reader;
	private final String source;

	private CyTable[] cyTables; 
	private static int numImports = 0;
	
	/**
	 * Creates a new ImportNetworkTask object.
	 *
	 * @param reader  DOCUMENT ME!
	 * @param source  DOCUMENT ME!
	 */
	public ImportAttributeTableTask(final TextTableReader reader, String source) {
		this.reader = reader;
		this.source = source;
	}


	//@Override
	//public void runx(TaskMonitor tm) throws IOException {
	//	tm.setProgress(0.10);
		//this.reader.setNetwork(network);
	//	if (this.cancelled){
	//		return;
	//	}
	//	this.reader.readTable();
	//	tm.setProgress(1.0);
	//}

	
	@Override
	public void run(TaskMonitor tm) throws IOException {
	
		CyTable table = CytoscapeServices.tableFactory.createTable("AttrTable " + Integer.toString(numImports++), 
									   "name", String.class, true, true);
		cyTables = new CyTable[] { table };

		try {
			this.reader.readTable(table);
			//loadAttributesInternal(table);
		} finally 
		{
			//
		}

		Class<? extends CyTableEntry> type = getMappingClass();

		if ( CytoscapeServices.netMgr.getNetworkSet().size() > 0 && type != null ) 
			super.insertTasksAfterCurrentTask( new MapNetworkAttrTask(type,table,CytoscapeServices.netMgr,CytoscapeServices.appMgr) );
	}

	//
	private Class<? extends CyTableEntry> getMappingClass() {
		
		ObjectType type = reader.getMappingParameter().getObjectType();
		
		if (type == ObjectType.NODE){
			return CyNode.class;
		}
		else if (type == ObjectType.EDGE){
			return CyEdge.class;
		}
		else if (type == ObjectType.NETWORK){
			return CyNetwork.class;
		}
		return null; 
	}
	
	
	@Override
	public CyTable[] getCyTables(){
		return cyTables;
	}
}
