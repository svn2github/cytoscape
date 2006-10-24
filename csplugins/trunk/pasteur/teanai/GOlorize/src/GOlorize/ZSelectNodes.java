/*
 * ZSelectNodes.java
 *
 * Created on May 13, 2006, 7:19 PM
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * The software and documentation provided hereunder is on an "as is" basis,
 * and the Pasteur Institut
 * has no obligations to provide maintenance, support,
 * updates, enhancements or modifications.  In no event shall the
 * Pasteur Institut
 * be liable to any party for direct, indirect, special,
 * incidental or consequential damages, including lost profits, arising
 * out of the use of this software and its documentation, even if
 * the Pasteur Institut
 * has been advised of the possibility of such damage. See the
 * GNU General Public License for more details: 
 *                http://www.gnu.org/licenses/gpl.txt.
 *
 * Authors: Olivier Garcia
 */

package GOlorize;

import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import cytoscape.*;
import cytoscape.view.*;
import cytoscape.data.annotation.*;
import giny.view.NodeView;
import giny.model.*;

public class ZSelectNodes implements ActionListener{
    HashSet goSelected;
    ResultAndStartPanel result;
    Annotation annotation;
    
    /** Creates a new instance of ZSelectNodes */
    public ZSelectNodes(ResultAndStartPanel result) {
        this.result = result;
        
    }
    
    public void actionPerformed(ActionEvent ev){
        this.annotation=result.getAnnotation();
        JTable jTable = result.getJTable();
        //goSelected = ZDisplayGoNodes3.getSelectedGoSet(jTable);
        goSelected = getSelectedGoSet(jTable);
        CyNetworkView currentNetworkView = Cytoscape.getCurrentNetworkView();
        
        
        if (result instanceof ResultPanel){
            this.annotation=result.getAnnotation();
            Iterator itNView = currentNetworkView.getNodeViewsIterator();
            while (itNView.hasNext()){
                NodeView nodeView = (NodeView)itNView.next();
                
                int[] goAnnot = annotation.getClassifications(nodeView.getNode().getIdentifier());

                if (goAnnot != null){
                        for (int i=0;i<goAnnot.length;i++){

                            if (goSelected.contains((new Integer(goAnnot[i])).toString())){
                                Cytoscape.getCurrentNetwork().setFlagged(nodeView.getNode(),true);

                                continue;
                            }
                        }
                }

            //this.annotation=null;    
            }
        }
        else {
            Iterator itGo= goSelected.iterator();
            while (itGo.hasNext()){
                String term = (String)itGo.next();
                int termint = Integer.parseInt(term);
                annotation = result.getAnnotation(term);
                Iterator itNView = currentNetworkView.getNodeViewsIterator();
                while (itNView.hasNext()){
                    NodeView nodeView = (NodeView)itNView.next();
                    int[] goAnnot = annotation.getClassifications(nodeView.getNode().getIdentifier());
                    if (goAnnot != null){
                        for (int i=0;i<goAnnot.length;i++){

                            if (goAnnot[i]==termint){
                                Cytoscape.getCurrentNetwork().setFlagged(nodeView.getNode(),true);

                                continue;
                            }
                        }
                    }
                }
                
            }
        }
        
        
        this.annotation = null;
    }
    private HashSet getSelectedGoSet (JTable jTable1){
        //return a hashmap key = GO terms selected, //n'est utile aue si on s'interesse qu'au genes refiles a bingo
        HashSet goSet = new HashSet();
        ArrayList genesList ;
        
        for (int i=0;i<jTable1.getRowCount();i++){
            if (((Boolean)jTable1.getValueAt(i,result.getSelectColumn())).booleanValue()==true){
                
                goSet.add((String)jTable1.getValueAt(i ,result.getGoTermColumn()));
                
            }
        }
        return goSet;
   }
}
