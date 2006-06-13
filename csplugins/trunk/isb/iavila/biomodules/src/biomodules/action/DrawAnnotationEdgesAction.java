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
 * An action that draws edges of type 'commonAnnotation' between nodes that
 * have the same annotations, this action is only to be added to the annotations.ui.ModuleAnnotationsTable,
 * otherwise it has no effect.
 * 
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */
package biomodules.action;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import cytoscape.*;
import cytoscape.data.Semantics;
import annotations.ui.*;
import annotations.*;
import cytoscape.data.*;

public class DrawAnnotationEdgesAction extends AbstractAction{

	protected CyNetwork network;
	
	/**
	 * Creates an action that will draw edges of type "commonAnnotation" between nodes
	 * with matching annotations.
	 */
	public DrawAnnotationEdgesAction (CyNetwork net){
		super("Draw annotation edges");
		this.network = net;
	}//DrawAnnotationEdgesAction
	
	/**
	 * Sets the CyNetwork where the annotation edges need to be created
	 * @param net CyNetwork
	 */
	public void setNetwork (CyNetwork net){
		this.network = net;
	}//setNetwork

	
	/**
	 * Calls <code>DrawAnnotationEdgesAction.drawAnnotationEdges (this.network, this.nodesToAnnotations)</code>
	 */
	public void actionPerformed (ActionEvent event){
		Object source = event.getSource();
		if(source instanceof JButton){
			JButton button = (JButton)source;
			int hashCode = button.hashCode();
			Object value = getValue(String.valueOf(hashCode));
			if(value instanceof ModuleAnnotationsTable){
				ModuleAnnotationsTable table = (ModuleAnnotationsTable)value;
				ModuleAnnotationsMap annotationsMap = table.getModuleAnnotationsMap();
				Map nodesToAnnotations = new HashMap();
				Object [] metaNodes = annotationsMap.getModuleIDs();
				for(int i = 0; i < metaNodes.length; i++){
					if(!(metaNodes[i] instanceof CyNode)){
						continue;
					}
					CyNode node = (CyNode)metaNodes[i];
					ModuleAnnotation [] moduleAnnotations = annotationsMap.get(metaNodes[i],table.getMostSpecific());
					String [] annotationNames = new String[moduleAnnotations.length];
					for(int j = 0; j < annotationNames.length; j++){
						annotationNames[j] = moduleAnnotations[j].getOntologyTerm().getName();
					}//for j
					nodesToAnnotations.put(node, annotationNames);
				}//for i
				drawAnnotationEdges(this.network, nodesToAnnotations);
			}// if instanceof ModuleAnnotationsTable
		}// if instanceof JButton
		
	}//actionPerformed
	
	/**
	 * Creates and draws edges between nodes that have annotations in common. These new edges
	 * are of 'commonAnnotation' interaction type, and have an 'annotation' attribute that has as a value
	 * the annotation in common that the connecting nodes have.
	 * 
	 * @param network the CyNetwork where the annotation edges need to be added
	 * @param node_to_annotations a Map from CyNodes to String [] that contain the
	 * annotations attached to the node
	 */
	//TODO: Somehow optimize this method.
	public static void drawAnnotationEdges (CyNetwork network, Map node_to_annotations){
		CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
		CyNode [] nodes = (CyNode[])node_to_annotations.keySet().toArray(new CyNode[node_to_annotations.size()]);
		int newEdges = 0;
		for(int i = 0; i < nodes.length; i++){
			String [] nodeiAnnots = (String[])node_to_annotations.get(nodes[i]);
			for(int j = i + 1; j < nodes.length; j++){
				String [] nodejAnnots = (String[])node_to_annotations.get(nodes[j]);
				for(int k = 0; k < nodeiAnnots.length; k++){
					for(int m = 0; m < nodejAnnots.length; m++){
						if(nodeiAnnots[k].equals(nodejAnnots[m])){
							// Matching annotation, so draw an edge between this two nodes
							CyEdge edge = Cytoscape.getCyEdge(nodes[i], nodes[j], Semantics.INTERACTION, "commonAnnotation", true);
							if(edge == null){
								throw new IllegalStateException("Cytoscape.getCyEdge(" + nodes[i] + "," + nodes[j] + 
										", Semantics.INTERACTION, \"commonAnnotation\",true) returned null!");
							}
							//Cytoscape.setEdgeAttributeValue(edge,"annotation",nodeiAnnots[k]);
							edgeAttributes.setAttribute(edge.getIdentifier(),"annotation", nodeiAnnots[k]);
							network.addEdge(edge);
							newEdges++;
						}// matching annotation
					}//for m
				}//for k
			}// for j
		}// for i
		if(newEdges > 0){
			Cytoscape.getNetworkView(network.getIdentifier()).redrawGraph(false, true);// no layout, but do apply vizmaps
		}
	}//drawAnnotationEdges

}//DrawAnnotationEdgesAction