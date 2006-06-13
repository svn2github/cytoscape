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
 * 
 * $revision : $
 * $date: $
 * $author: Iliana Avila <iavila@systemsbiology.org, iliana.avila@gmail.com>
 */
package biomodules.action;
import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import cytoscape.*;
import annotations.ModuleAnnotation;
import annotations.ModuleAnnotationsMap;
import annotations.ui.ModuleAnnotationsTable;
import cytoscape.data.*;

public class SaveAnnotationsToAttribute extends AbstractAction{
	
	public SaveAnnotationsToAttribute (){
		super("Save annotations to node attributes...");
	}//SaveAnnotationsToAttribute
	
	
	/**
	 * Implements AbstractAction.actionPerformed.
	 * To be added to annotations.ui.ModuleAnnotationsTable.
	 * Calls saveAnnotationsToAttribute(annotations_map, attribute_name, specific_annotations)
	 */
	public void actionPerformed(ActionEvent event){
		Object source = event.getSource();
		if(source instanceof JButton){
			JButton button = (JButton)source;
			int hashCode = button.hashCode();
			Object value = getValue(String.valueOf(hashCode));
			if(value instanceof ModuleAnnotationsTable){
				ModuleAnnotationsTable table = (ModuleAnnotationsTable)value;
				ModuleAnnotationsMap annotationsMap = table.getModuleAnnotationsMap();
				String attributeName = table.getAnnotation().getCurator() + " " + table.getAnnotation().getType();
				saveAnnotationsToAttribute(annotationsMap, attributeName, table.getMostSpecific());
			}// if instanceof ModuleAnnotationsTable
		}// if instanceof JButton
			
	}//actionPerformed
	
	/**
	 * 
	 * @param annotations_map the ModuleAnnotaitonsMap where the keys are CyNodes
	 * @param attribute_name the name of the node attribute to which the annotation should be saved 
	 * @param specific_annotations whether the annotations should be specific or not
	 */
	public static void saveAnnotationsToAttribute (ModuleAnnotationsMap annotations_map, String attribute_name, boolean specific_annotations){
		CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
		Object [] keys = annotations_map.getModuleIDs();
		for(int i = 0; i < keys.length; i++){
			if(keys[i] instanceof CyNode){
				ModuleAnnotation [] annotations = annotations_map.get(keys[i],specific_annotations);
				if(annotations.length > 0){
					Vector annots = new Vector();
					for(int j = 0; j < annotations.length; j++){
						annots.add(annotations[j].getOntologyTerm().getName());
					}//for j
					//Cytoscape.setNodeAttributeValue((CyNode)keys[i], attribute_name, annots);
					nodeAtts.setAttributeList( ((CyNode)keys[i]).getIdentifier(), attribute_name, annots);
				}// if annotations.length > 0
			}// if keys[i] instanceof CyNode
		}//for i
	}//saveAnnotationsToAttribute
	
}//SaveAnnotationsToAttribute