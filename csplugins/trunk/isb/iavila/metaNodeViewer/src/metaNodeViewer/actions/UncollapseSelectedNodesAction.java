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
 * @author Iliana Avila-Campillo iavila@systemsbiology.org
 * @version %I%, %G%
 * @since 2.0
 */
package metaNodeViewer.actions;
import java.util.*;

import metaNodeViewer.model.AbstractMetaNodeModeler;
import metaNodeViewer.MetaNodeUtils;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.visual.VisualMappingManager;

/**
 * Use metaNodeViewer.actions.ActionFactory to get an instance of this class.
 */
public class UncollapseSelectedNodesAction extends AbstractAction {
	
	protected AbstractMetaNodeModeler abstractingModeler;
	protected boolean recursive;
	protected boolean temporaryUncollapse;
	
	/**
	 * Use metaNodeViewer.actions.ActionFactory instead.
	 * 
	 * @param abstracting_modeler the AbstractMetaNodeModeler to use to uncollapse the nodes
	 * @param recursive_uncollapse whether to uncollapse all the way to the lowest level in the graph
	 * @param temporary_uncollapse if false, the meta-nodes are permanently removed after they
	 * are uncollapsed
	 * @param title the title for the action (appears as text on a button)
	 */
	protected UncollapseSelectedNodesAction (AbstractMetaNodeModeler abstracting_modeler,
			boolean recursive_uncollapse,
			boolean temporary_uncollapse,
			String title){
		super(title);
		this.abstractingModeler = abstracting_modeler;
		this.recursive = recursive_uncollapse;
		this.temporaryUncollapse = temporary_uncollapse;
	}//UncollapseSelectedNodesAction
	
	/**
	 * Sets whether or not this uncollapse should be recursive (uncollapse to the bottom level
	 * of the graph).
	 */
	public void setRecursiveUncollapse (boolean is_recursive){
		this.recursive = is_recursive;
	}//setRecursiveUncollapse
	
	/**
	 * Sets whether or not this uncollapse is temporary or not. If it is temporary, then the
	 * meta-node will be remembered for a subsequent collapse operation.
	 */
	public void setTemporaryUncollapse (boolean is_temporary){
		this.temporaryUncollapse = is_temporary;
	}//setTemporaryUncollapse
	
	/**
	 * Implements AbstractAction.actionPerformed by calling <code>uncollapseSelectedNodes</code>
	 */
	public void actionPerformed (ActionEvent e){
		uncollapseSelectedNodes(this.abstractingModeler, 
				this.recursive, 
				this.temporaryUncollapse);
	}//actionPerformed
	
	/**
	 * Uncollapses the selected nodes in the current CyNetwork.
	 *  
	 * @param abstracting_modeler the AbstractMetaNodeModeler to use to uncollapse the nodes
	 * @param recursive_uncollapse whether to uncollapse all the way to the leaves
	 * @param temporary_uncollapse if false, the meta-nodes are permanently removed after they
	 * are uncollapsed
	 */
	public static void uncollapseSelectedNodes (AbstractMetaNodeModeler abstractModeler,
			boolean recursive,
			boolean temporary){
        
        CyNetwork cyNetwork = Cytoscape.getCurrentNetwork();
        Iterator it = cyNetwork.getFlaggedNodes().iterator();
        // Pop-up a dialog if there are no selected nodes and return
        ArrayList selectedNodes = new ArrayList();
        while(it.hasNext()){
            selectedNodes.add(it.next());
        }
        
        if(selectedNodes.size() == 0) {
            JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
            "Please select one or more nodes.");
            return;
        }
        
		// Finally, uncollapse each node (if it is not a metanode, nothing happens)
		//int numUncollapsed = 
		  //  MetaNodeUtils.uncollapseNodes(cyNetwork,selectedNodes,recursive,temporary);
        boolean [] isExpandedArray = null;
        if(temporary)
            isExpandedArray = MetaNodeUtils.expandMetaNodes(cyNetwork,selectedNodes,recursive);
        else
            isExpandedArray = MetaNodeUtils.removeMetaNodes(cyNetwork,selectedNodes,recursive);
        int numExpanded = 0;
        for(int i = 0; i < isExpandedArray.length; i++)
            if(isExpandedArray[i]) numExpanded++;
        if(numExpanded == 0)
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(), 
                                    "None of the selected nodes are meta-nodes.");
        
        // This may make the operation slower. It would be nice to have applyAppearances(Collection nodes, Collection edges);
        VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
        vizmapper.applyAppearances();
	}//uncollapseSelectedNodes
	
}//class UncollapseSelectedNodesAction
