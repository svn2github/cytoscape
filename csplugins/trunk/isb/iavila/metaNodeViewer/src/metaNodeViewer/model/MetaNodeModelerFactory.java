/**  Copyright (c) 2003 Institute for Systems Biology
 **  This program is free software; you can redistribute it and/or modify
 **  it under the terms of the GNU General Public License as published by
 **  the Free Software Foundation; either version 2 of the License, or
 **  any later version.
 **
 **  This program is distributed in the hope that it will be useful,
 **  but WITHOUT ANY WARRANTY; without even the implied warranty of
 **  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  The software and
 **  documentation provided hereunder is on an "as is" basis, and the
 **  Institute for Systems Biology has no obligations to provide maintenance, 
 **  support, updates, enhancements or modifications.  In no event shall the
 **  Institute for Systems Biology be liable to any party for direct, 
 **  indirect, special,incidental or consequential damages, including 
 **  lost profits, arising out of the use of this software and its 
 **  documentation, even if the Institute for Systems Biology 
 **  has been advised of the possibility of such damage. See the
 **  GNU General Public License for more details.
 **   
 **  You should have received a copy of the GNU General Public License
 **  along with this program; if not, write to the Free Software
 **  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **/
/**
 * @author Iliana Avila-Campillo iavila@systemsbiology.org, iliana.avila@gmail.com
 * @version %I%, %G%
 * @since 2.0
 * 
 * A class that contains all the meta-node modelers for Cytoscape. In most cases, a single modeler per/Cytoscape
 * is needed, so most modeler's constructors are protected, and only this factory can create them. So far we only have
 * one modeler (AbstractMetaNodeModeler) but more are to come.
 */
package metaNodeViewer.model;

import giny.model.RootGraph;
import cytoscape.*;
import metaNodeViewer.data.*;

public class MetaNodeModelerFactory {
	
	/**
	 * The default attributes handler for meta-nodes.
	 */
	public static final MetaNodeAttributesHandler DEFAULT_MN_ATTRIBUTES_HANDLER = new SimpleMetaNodeAttributesHandler();
	/**
	 * The AbstractMetaNodeModeler for all networks in Cytoscape.
	 * There is only ONE per RootGraph, and since all CyNetworks in a Cytoscape application share the same RootGraph,
	 * there is only ONE per Cytoscape application.
	 */
	protected static final AbstractMetaNodeModeler CYTOSCAPE_ABSTRACT_MODELER = new AbstractMetaNodeModeler(Cytoscape.getRootGraph());
	
	/**
	 * It returns a AbstractMetaNodeModeler for the RootGraph obtained through Cytoscape.getRootGraph() 
	 * 
	 * @return an AbstractMetaNodeModeler for all CyNetworks in Cytoscape
	 */
	public static AbstractMetaNodeModeler getCytoscapeAbstractMetaNodeModeler (){
		
		RootGraph rootGraph = Cytoscape.getRootGraph();
		
	    if(MetaNodeModelerFactory.CYTOSCAPE_ABSTRACT_MODELER.getRootGraph() != rootGraph){
			// Cytoscape is supposed to only have one RootGraph for all CyNetworks. If we got here, this assumption has been violated.
			throw new IllegalArgumentException("The RootGraph of the current ABSTRACT_MODELER is not equal to the current CyNetwork's RootGraph");	
		}// ABSTRACT_MODELER != null
		
		return MetaNodeModelerFactory.CYTOSCAPE_ABSTRACT_MODELER;
	}//getCytoscapeAbstractMetaNodeModeler
	
	/**
	 * It returns a AbstractMetaNodeModeler for the RootGraph obtained through Cytoscape.getRootGraph()
	 *  
	 * @param attributes_handler the default attributes handler that should be used when setting node/edge attributes of meta-nodes
	 * @return an AbstractMetaNodeModeler for all CyNetworks in Cytoscape
	 */
	public static AbstractMetaNodeModeler getCytoscapeAbstractMetaNodeModeler (MetaNodeAttributesHandler attributes_handler){
		
		RootGraph rootGraph = Cytoscape.getRootGraph();
		
	    if(MetaNodeModelerFactory.CYTOSCAPE_ABSTRACT_MODELER.getRootGraph() != rootGraph){
			// Cytoscape is supposed to only have one RootGraph for all CyNetworks. If we got here, this assumption has been violated.
			throw new IllegalArgumentException("The RootGraph of the current ABSTRACT_MODELER is not equal to the current CyNetwork's RootGraph");	
		}// ABSTRACT_MODELER != null
		MetaNodeModelerFactory.CYTOSCAPE_ABSTRACT_MODELER.setDefaultAttributesHandler(attributes_handler);
		return MetaNodeModelerFactory.CYTOSCAPE_ABSTRACT_MODELER;
	}//getCytoscapeAbstractMetaNodeModeler
	
}//MetaNodeModelerFactory