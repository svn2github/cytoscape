package metaNodeViewer;

import metaNodeViewer.ui.MNcollapserDialog;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import cytoscape.view.CyWindow;
import cytoscape.plugin.AbstractPlugin;
import cytoscape.util.*;

public class MetaNodeViewerCytoPlugin extends AbstractPlugin{

  protected CyWindow cyWindow;
  
  /**
   * Constructor.
   */
  public MetaNodeViewerCytoPlugin (CyWindow cy_window){
    this.cyWindow = cy_window;

    final MNcollapserDialog dialog = new MNcollapserDialog(this.cyWindow);
    dialog.setOptions(false,true,false,false);
    dialog.setResizable(false);
    this.cyWindow.getCyMenus().getOperationsMenu().add(
                                                       new AbstractAction ("Meta Node Collapser"){
                                                         public void actionPerformed (ActionEvent e){
                                                           dialog.pack();
                                                           dialog.setVisible(true);
                                                         }//actionPerformed
                                                       }
                                                       );
  }//constructor

}//class MetaNodeViewerCytoPlugin
