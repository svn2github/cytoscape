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
 */
package metaNodeViewer.actions;
import java.util.*;
import metaNodeViewer.model.AbstractMetaNodeModeler;
import metaNodeViewer.model.MetaNodeFactory;
//import metaNodeViewer.view.VisualStyleFactory;
import metaNodeViewer.MetaNodeUtils;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.*;
import cytoscape.visual.VisualMappingManager;

/**
 * Use metaNodeViewer.actions.ActionFactory to get an instance of this class.
 */
public class CollapseSelectedNodesAction extends AbstractAction {
	
	protected AbstractMetaNodeModeler abstractingModeler;
	protected boolean collapseExistentParents;
	protected boolean collapseRecursively;
	protected boolean multipleEdges;

	/**
	 * Use metaNodeViewer.actions.ActionFactory instead
	 *
	 * @param abstracting_modeler the AbstractMetaNodeModeler used to collapse nodes
	 * @param collapse_existent_parents if the selected nodes already have meta-parents, and
	 * if collapse_existent_parents is true, don't create new parent meta-nodes, simply collapse
	 * the existent parents
	 * @param collapse_recursively if collapse_existent_parents is true, whether or not to find
	 * the top-level meta-node parents and collapse them instead of finding the immediate parents
	 * and collapsing them
	 * @param title the title of the AbstractAction (appears as a button's text)
	 */
	protected CollapseSelectedNodesAction (AbstractMetaNodeModeler abstracting_modeler,
			boolean collapse_existent_parents,
			boolean collapse_recursively,
			String title){
		super(title);
		this.abstractingModeler = abstracting_modeler;
		this.collapseExistentParents = collapse_existent_parents;
		this.collapseRecursively = collapse_recursively;
    this.multipleEdges = abstracting_modeler.getMultipleEdges();
	}//CollapseSelectedNodesAction
	
	/**
	 * Sets whether or not the existent meta-node parents of the selected nodes should be collapsed instead
	 * of creating new meta-nodes for them
	 */
	public void setCollapseExistentParents (boolean collapse_existent_parents){
		this.collapseExistentParents = collapse_existent_parents;
	}//setCollapseExistentParents
	
	/**
	 * @return whether or not the existent meta-node parents of the selected nodes should be collapsed instead
	 * of creating new meta-nodes for them
	 */
	public boolean getCollapseExistentParents (){
		return this.collapseExistentParents;
	}//getCollapseExistentParents
	
	/**
	 * Sets whether or not the top-level meta-node parents of the selected nodes should be found and collapsed.
	 * 
	 * @param collapse_recursively
	 */
	public void setCollapseRecursively (boolean collapse_recursively){
		this.collapseRecursively = collapse_recursively;
	}//setCollapseRecursively
	
	/**
	 * 
	 * @return whether or not the top-level meta-node parents of the selected nodes should be found and collapsed.
	 */
	public boolean getCollapseRecursively (){
		return this.collapseRecursively;
	}//getCollapseRecursively

  /**
   * Sets whether multiple edges between meta-nodes and other nodes should be created
   */
  public void setMultipleEdges (boolean multiple_edges){
    this.multipleEdges = multiple_edges;
  }//setMultipleEdges

  /**
   * @return whether multiple edges between meta-nodes and other nodes should be created
   */
  public boolean getMultipleEdges (){
    return this.multipleEdges;
  }//getMultipleEdges
	
	/**
	 * Sets whether or not default names should be assigned to newly created metanodes
	 */
	public void setAssignDefaultNames (boolean assign){
		MetaNodeFactory.assignDefaultNames(assign);
	}//setAssignDefaultNames
	
	/**
	 * Implements AbstractAction.actionPerformed by calling <code>CollapseSelectedNodesAction.collapseSelectedNodes</code>
	 */
	public void actionPerformed (ActionEvent e){
		collapseSelectedNodes(this.abstractingModeler, 
                          this.collapseExistentParents,
                          this.collapseRecursively,
                          this.multipleEdges);
	}//actionPerformed
	
	
	/**
	 * Collapses into a meta-node(s) a set of selected nodes in the current CyNetwork.
	 * 
	 * @param abstractModeler the AbstractMetaNodeModeler to use for collapsing the nodes
	 * @param collapse_existent_parents whether or not the existent meta-node parents of the selected nodes should be collapsed instead
	 * of creating new meta-nodes for them
	 * @param collapse_recursively whether or not the top-level meta-node parents of the selected nodes should be found and collapsed, ignored if
	 * collapse_existent_parents is false
   * @param multiple_edges whether multiple edges between meta-nodes and other nodes should be created
	 */
	public static void collapseSelectedNodes (AbstractMetaNodeModeler abstractModeler,
                                            boolean collapse_existent_parents,
                                            boolean collapse_recursively,
                                            boolean multiple_edges){
		
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
		
      
        if(collapse_existent_parents){
            List parentMetanodes = new ArrayList();
            it = selectedNodes.iterator();
            if(collapse_recursively){
                while(it.hasNext()) parentMetanodes.addAll(MetaNodeUtils.getTopLevelParents(cyNetwork,(CyNode)it.next()));
            }else{
                while(it.hasNext()) parentMetanodes.addAll(MetaNodeUtils.getParents(cyNetwork,(CyNode)it.next()));
            }
            if(parentMetanodes.size() == 0){ 
                JOptionPane.showMessageDialog(Cytoscape.getDesktop(),"The selected nodes do not have parent meta-nodes.");
                return;
            }
            if(parentMetanodes.size() > 0){
                it = parentMetanodes.iterator();
                while(it.hasNext()) MetaNodeUtils.collapseMetaNode(cyNetwork,(CyNode)it.next(),multiple_edges);
            }
        }else{
            CyNetwork subnet = Cytoscape.getRootGraph().createNetwork(selectedNodes, new ArrayList());
            CyNode metanode = MetaNodeUtils.createMetaNode(cyNetwork,subnet);
            MetaNodeUtils.collapseMetaNode(cyNetwork,metanode,multiple_edges);
        }
        // This may make the operation slower. It would be nice to have applyAppearances(Collection nodes, Collection edges);
       VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
       vizmapper.applyAppearances();
        
	}//collapseSelectedNodes
	
	
}//class CollapseSelectedNodesAction

