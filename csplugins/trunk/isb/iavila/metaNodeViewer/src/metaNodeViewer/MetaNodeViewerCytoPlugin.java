package metaNodeViewer;

import metaNodeViewer.model.*;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import giny.model.*;
import giny.view.*;
import cytoscape.GraphObjAttributes;
import cytoscape.data.CyNetwork;
import cytoscape.view.CyWindow;
import cytoscape.AbstractPlugin;
import cytoscape.util.*;

public class MetaNodeViewerCytoPlugin extends AbstractPlugin{

  protected CyWindow cyWindow;

  /**
   * Constructor.
   */
  public MetaNodeViewerCytoPlugin (CyWindow cy_window){
    this.cyWindow = cy_window;
    // Add actions to the plugins menu
    this.cyWindow.getCyMenus().getOperationsMenu().add(new ApplyModelAction());
    this.cyWindow.getCyMenus().getOperationsMenu().add(new UndoModel(true));
    this.cyWindow.getCyMenus().getOperationsMenu().add(new UndoModel(false));
    ( ( CytoscapeMenuBar )cyWindow.getCyMenus().getMenuBar() ).addAction( new GraphStateAction( cy_window ) );
    

  }//constructor

  // ---------- Testing Actions ----------- //
  public class ApplyModelAction extends AbstractAction {
    public ApplyModelAction () { super("Apply Top Level Model");}
    
    public void actionPerformed (ActionEvent event){
      System.out.println("--------------In ApplyModelAction.actionPerformed()--------------");
      GraphView graphView = cyWindow.getView();
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective mainGP = cyNetwork.getGraphPerspective();
      TopLevelMetaNodeModel.applyModel(mainGP);
    }//actionPerformed
  }//ApplyModelAction

  public class UndoModel extends AbstractAction {
    boolean recurse;
    public UndoModel (boolean r){
      super("Undo Model, recurse = " + r);
      this.recurse = r;
    }//cons
    
    public void actionPerformed (ActionEvent event){
      System.out.println("-------------In UndoModel.actionPerformed()----------------");
      GraphView graphView = cyWindow.getView();
      CyNetwork cyNetwork = cyWindow.getNetwork();
      GraphPerspective mainGP = cyNetwork.getGraphPerspective();
      TopLevelMetaNodeModel.undoModel(mainGP, this.recurse);
    }//actionPerformed
  }//UndoModel

}//class MetaNodeViewerCytoPlugin
