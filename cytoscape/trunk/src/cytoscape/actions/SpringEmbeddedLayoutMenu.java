
/*
  File: SpringEmbeddedLayoutMenu.java 
  
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)
  
  The Cytoscape Consortium is: 
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Pasteur Institute
  - Agilent Technologies
  
  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.
  
  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute 
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute 
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute 
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.
  
  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

/*
 * VERY STRONG WARNING!!!
 * AVOID USING OR LOOKING AT THIS CODE!!  IT IS GOING TO GO AWAY VERY SOON!!!
 */

package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.foo.GraphConverter;
import cytoscape.graph.legacy.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.legacy.layout.impl.SpringEmbeddedLayouter2;
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

/**
 * VERY STRONG WARNING!!!
 * AVOID USING OR LOOKING AT THIS CODE!!  IT IS GOING TO GO AWAY VERY SOON!!!
 * 
 * @deprecated Please avoid looking at or using this code -- this code
 *   is going away in the next Cytoscape release (the one after 2.1).
 **/
public class SpringEmbeddedLayoutMenu extends JMenu
{

  private final JMenuItem m_wholeGraph;
  private final JMenuItem m_selectedNodesOnly;

  public SpringEmbeddedLayoutMenu()
  {
    super("Apply Spring Embedded Layout");
    m_wholeGraph = new JMenuItem(new SpringEmbeddedLayoutAction(false));
    add(m_wholeGraph);
    m_selectedNodesOnly = new JMenuItem(new SpringEmbeddedLayoutAction(true));
    add(m_selectedNodesOnly);
    addMenuListener(new MenuListener() {
        public void menuCanceled(MenuEvent e) {}
        public void menuDeselected(MenuEvent e) {}
        public void menuSelected(MenuEvent e)
        {
          CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
          if (graphView.getSelectedNodeIndices().length == 0)
            m_selectedNodesOnly.setEnabled(false);
          else
            m_selectedNodesOnly.setEnabled(true);
        } });
  }

  private static class SpringEmbeddedLayoutAction extends AbstractAction
  {

    private final boolean m_selectedNodesOnly;

    public SpringEmbeddedLayoutAction(boolean selectedNodesOnly)
    {
      super((selectedNodesOnly ? "Selected Nodes Only" :
             "All Nodes"));
      m_selectedNodesOnly = selectedNodesOnly;
    }

    public void actionPerformed(ActionEvent e)
    {
      final MutablePolyEdgeGraphLayout nativeGraph =
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
      if (success) {
          GraphConverter.updateCytoscapeLayout(nativeGraph);
      }
    }
  }
}
