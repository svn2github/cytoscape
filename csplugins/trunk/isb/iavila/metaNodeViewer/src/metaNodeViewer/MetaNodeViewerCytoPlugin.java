package metaNodeViewer;

import metaNodeViewer.model.*;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.util.*;
import giny.model.*;
import giny.view.*;
import cytoscape.data.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.plugin.AbstractPlugin;
import cytoscape.util.*;

public class MetaNodeViewerCytoPlugin extends AbstractPlugin{

  protected CyWindow cyWindow;
  protected AbstractMetaNodeModeler modeler;

  /**
   * Constructor.
   */
  // TODO: LEFT HERE, TEST NEW AbstractMetaNodeModeler
  public MetaNodeViewerCytoPlugin (CyWindow cy_window){
    this.cyWindow = cy_window;
    // Add actions to the plugins menu
    this.cyWindow.getCyMenus().getOperationsMenu().add(new ApplyModelForSelectedNodesAction());
    //this.cyWindow.getCyMenus().getOperationsMenu().add(new UndoModel());
    this.cyWindow.getCyMenus().getOperationsMenu().add(new UndoModelForSelectedNodes("Uncollapse",false));
    this.cyWindow.getCyMenus().getOperationsMenu().add(new UndoModelForSelectedNodes("Uncollapse recursive",true));
    
    //( ( CytoscapeMenuBar )cyWindow.getCyMenus().getMenuBar() ).addAction( new GraphStateAction( cy_window ) );
    

  }//constructor

  // ---------- Testing Actions ----------- //
  public class ApplyModelForSelectedNodesAction extends AbstractAction {
    public ApplyModelForSelectedNodesAction () {
      super("Collapse Selected Metanodes");
    }// cons

    public void actionPerformed (ActionEvent event){
      System.err.println("------------------ In ApplyModelForSelectedNodesAction ---------------");
      GraphView graphView = cyWindow.getView();
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective mainGP = cyNetwork.getGraphPerspective();
      int [] selectedNodeIndices = graphView.getSelectedNodeIndices();
      if(modeler == null){
        modeler = new AbstractMetaNodeModeler(mainGP.getRootGraph());
      }
      System.out.println("Got " + selectedNodeIndices.length + " selected nodes.");
      if(selectedNodeIndices != null && selectedNodeIndices.length > 0){
        HashSet uniqueParents = new HashSet();
        for(int i = 0; i < selectedNodeIndices.length; i++){
          System.err.println("Selected node = " + selectedNodeIndices[i]);
          
          // TODO: GraphView has a bug that returns node indices as selected
          // even if this nodes are no longer in GraphPerspective, Rowan will fix this
          // For now, check that selected nodes are in mainGP
          
          // For each selected node, get its parents
          if(selectedNodeIndices[i] > mainGP.getNodeCount()){
            System.err.println("Node index " + selectedNodeIndices[i] + " is > " +
                               mainGP.getNodeCount() + " (node count), so skipping node");
            continue;
          }
          int rootIndex = mainGP.getRootGraphNodeIndex(selectedNodeIndices[i]);
          System.err.println("Selected node RootGraph  index = " + rootIndex);
          int [] parents = 
            mainGP.getRootGraph().getNodeMetaParentIndicesArray(rootIndex); 
          if(parents != null){
            System.err.println("Selected node " + rootIndex + " has " +
                               parents.length + " parents");
          }
          for(int j = 0; j < parents.length; j++){
            uniqueParents.add(new Integer(parents[j]));
          }// for j
        }// for i
        Iterator it = uniqueParents.iterator();
        while(it.hasNext()){
          int parentRootIndex =  ((Integer)it.next()).intValue();
          modeler.applyModel(mainGP, parentRootIndex);
         
        }// while it
      }// if
    }//actionEvent
  }//ApplyModelForSelectedNodesAction
  
  
  // public class ApplyModelAction extends AbstractAction {
//     public ApplyModelAction () { super("Apply Top Level Model");}
    
//     public void actionPerformed (ActionEvent event){
//       System.out.println("--------------In ApplyModelAction.actionPerformed()--------------");
//       GraphView graphView = cyWindow.getView();
//       CyNetwork cyNetwork = cyWindow.getNetwork();
//       GraphPerspective mainGP = cyNetwork.getGraphPerspective();
//       TopLevelMetaNodeModel.applyModel(mainGP);
//     }//actionPerformed
//   }//ApplyModelAction

//   public class UndoModel extends AbstractAction {
//     public UndoModel (){
//       super("Undo Model");
//     }//cons
    
//     public void actionPerformed (ActionEvent event){
//       System.out.println("-------------In UndoModel.actionPerformed()----------------");
//       GraphView graphView = cyWindow.getView();
//       CyNetwork cyNetwork = cyWindow.getNetwork();
//       GraphPerspective mainGP = cyNetwork.getGraphPerspective();
//       TopLevelMetaNodeModel.undoModel(mainGP);
//     }//actionPerformed
//   }//UndoModel

  public class UndoModelForSelectedNodes extends AbstractAction{
    
    boolean recursive;
    
    public UndoModelForSelectedNodes(String name, boolean recursive){
      super(name);
      this.recursive = recursive;
    }//cons
    
    public void actionPerformed (ActionEvent event){
      System.out.println("----------------------- In UndoModelForSelectedNodes() -----------------");
      GraphView graphView = cyWindow.getView();
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective mainGP = cyNetwork.getGraphPerspective();
      int [] selectedNodeIndices = graphView.getSelectedNodeIndices();
      if(selectedNodeIndices != null && selectedNodeIndices.length > 0){
        for(int i = 0; i < selectedNodeIndices.length; i++){
          modeler.undoModel(mainGP,selectedNodeIndices[i],this.recursive,false);
        }
      }
    }//actionEvent
    
  }//UndoModelForSelectedNodes

}//class MetaNodeViewerCytoPlugin
