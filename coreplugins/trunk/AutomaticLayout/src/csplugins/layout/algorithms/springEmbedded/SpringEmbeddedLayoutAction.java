package csplugins.layout.algorithms.springEmbedded;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class SpringEmbeddedLayoutAction extends AbstractAction
{
  private final boolean m_selectedNodesOnly;

  public SpringEmbeddedLayoutAction(boolean selectedNodesOnly)
  {
    super(selectedNodesOnly ? "Selected Nodes Only" : "All Nodes");
    m_selectedNodesOnly = selectedNodesOnly;
  }

  public void actionPerformed(ActionEvent e)
  {
    MutablePolyEdgeGraphLayout nativeGraph =
      GraphConverter.getGraphCopy(0.0d, false, m_selectedNodesOnly);
    Task task = new SpringEmbeddedLayouter2(nativeGraph);

    //////////////////////////////////////////////////////////
    // BEGIN: The thread and process related code starts here.
    //////////////////////////////////////////////////////////

    //  Configure UI Options.
    JTaskConfig jTaskConfig = new JTaskConfig();
    jTaskConfig.setAutoDispose(true);
    jTaskConfig.setOwner(Cytoscape.getDesktop());
    jTaskConfig.displayCloseButton(true);
    jTaskConfig.displayCancelButton(true);
    jTaskConfig.displayStatus(true);

    //  Execute Task
    //  This method will block until Pop-up Dialog Box is diposed/closed.
    boolean success = TaskManager.executeTask(task, jTaskConfig);

    // Whatever you do, make sure that task.run() is finished by the time
    // we exit out of this code block.  This may require a synchronized
    // block with Object.wait() if we call task.run() from another thread.

    //////////////////////////////////////////////////////
    // END: The thread and process related code ends here.
    //////////////////////////////////////////////////////

    //  Update the UI only if Task was Successful.
    if (success)
    {
        GraphConverter.updateCytoscapeLayout(nativeGraph);
        Cytoscape.getCurrentNetworkView().fitContent();
    }
  }
}

