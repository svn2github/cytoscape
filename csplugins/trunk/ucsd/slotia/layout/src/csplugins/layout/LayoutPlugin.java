package csplugins.layout;

import cytoscape.*;
import cytoscape.plugin.*;
import cytoscape.util.*;
import cytoscape.layout.*;
import cytoscape.view.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import phoebe.util.GraphPartition;

import csplugins.layout.algorithms.*;
import csplugins.layout.algorithms.springEmbedded.SpringEmbeddedLayoutAction;
import csplugins.layout.algorithms.hierarchicalLayout.HierarchicalLayoutListener;
//import csplugins.layout.algorithms.radialHierarchicalLayout.RadialHierarchicalLayoutAlgorithm;
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayoutMenu;
//import csplugins.layout.algorithms.graphPartition.ISOMLayout;
import csplugins.layout.algorithms.graphPartition.DegreeSortedCircleLayout;

import giny.view.NodeView;

public class LayoutPlugin extends CytoscapePlugin
{
  public LayoutPlugin()
  {
    initialize();
  }

  protected void initialize()
  {
    // NOTE:
    // The CircleGraphLayoutAlgorithm does not implement a decent layout,
    // therefore it has been commented out.

    /*
    JMenuItem circle = new JMenuItem(new AbstractAction("Circle")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            final SwingWorker worker = new SwingWorker()
            {
              public Object construct()
              {
                LayoutAlgorithm layout = new
                  CircleGraphLayoutAlgorithm(Cytoscape.getCurrentNetworkView());
                Cytoscape.getCurrentNetworkView().applyLayout(layout);
                return null;
              } // end construct()

            }; // end SwingWorker

            worker.start();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction
    */

    // NOTE: The RadialTreeLayoutAlgorithm is broken, thus it has been
    // commented out.

    /*
    JMenuItem radial = new JMenuItem(new AbstractAction("Radial")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            final SwingWorker worker = new SwingWorker()
            {
              public Object construct()
              {
                LayoutAlgorithm layout = new
                  RadialTreeLayoutAlgorithm(Cytoscape.getCurrentNetworkView());
                Cytoscape.getCurrentNetworkView().applyLayout(layout);
                return null;
              } // end construct()

            }; // end SwingWorker

            worker.start();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction
    */

    // NOTE: The SpringEmbeddedLayoutAlgorithm is broken. Therefore
    // it has been commented out.

    /*
    JMenuItem spring = new JMenuItem(new AbstractAction("Spring")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            final SwingWorker worker = new SwingWorker()
            {
              public Object construct()
              {
                LayoutAlgorithm layout = new
                  SpringEmbeddedLayoutAlgorithm( Cytoscape.getCurrentNetworkView() );
                Cytoscape.getCurrentNetworkView().applyLayout(layout);
                return null;
              } // end construct()

            }; // end SwingWorker

            worker.start();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction
    */

    // NOTE: The ISOMLayoutAlgorithm is broken. Therefore, it has been
    // commented out.

    /*
    JMenuItem isom = new JMenuItem(new AbstractAction("ISOM")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            final SwingWorker worker = new SwingWorker()
            {
              public Object construct()
              {
                LayoutAlgorithm layout = new
                  ISOMLayoutAlgorithm(Cytoscape.getCurrentNetworkView());
                Cytoscape.getCurrentNetworkView().applyLayout(layout);
                return null;
              }
            };
            worker.start();
          }
        });
      }
    });
    */

    JMenuItem hierarchical = new JMenuItem("Hierarchical");
    {
      HierarchicalLayoutListener hierarchicalListener = new HierarchicalLayoutListener();
      hierarchical.addActionListener(hierarchicalListener);
    }
    
    JMenuItem springEmbAll = new JMenuItem(new SpringEmbeddedLayoutAction(true));
    JMenuItem springEmbSome = new JMenuItem(new SpringEmbeddedLayoutAction(false));
    JMenu springEmbMenu = new JMenu("Spring Embedded");
    springEmbMenu.add(springEmbAll);
    springEmbMenu.add(springEmbSome);

    // NOTE: ISOMLayout is broken. Therefore it has been commented out.
    
    /*
    JMenuItem isomLay = new JMenuItem(new AbstractAction("ISOM Layout")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            ISOMLayout layout = new ISOMLayout(Cytoscape.getCurrentNetwork());
            layout.layout();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction
    */

    JMenuItem degSortCircle = new JMenuItem(new AbstractAction("Degree Sorted Circle Layout")
    {
      public void actionPerformed (ActionEvent e )
      {
        SwingUtilities.invokeLater(new Runnable()
        {
          public void run()
          {
            DegreeSortedCircleLayout layout = new DegreeSortedCircleLayout(Cytoscape.getCurrentNetwork());
            layout.layout();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction

    JMenuItem debug_NodeDump = new JMenuItem(new AbstractAction("Node Dump")
    {
      public void actionPerformed(ActionEvent e)
      {
        Iterator iterator = Cytoscape.getCurrentNetworkView().getNodeViewsIterator();
	while (iterator.hasNext())
	{
	  NodeView nodeView = (NodeView) iterator.next();
	  System.err.println(nodeView.getNode().getIdentifier() + " (" +
	                     nodeView.getXPosition() + ", " +
			     nodeView.getYPosition() + ") [" +
			     nodeView.getWidth() + "x" +
			     nodeView.getHeight() + "]");
	}
      }
    });

    JMenu menu = new JMenu("Cytoscape Layouts");
    //menu.add(circle);
    //menu.add(radial);
    //menu.add(spring);
    //menu.add(isom);
    menu.add(hierarchical);
    //menu.add(radHier);
    menu.add(springEmbMenu);
    menu.add(new AttributeCircleLayoutMenu());
    //menu.add(isomLay);
    menu.add(degSortCircle);

    JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
                                .getMenu("Layout");
    layoutMenu.add(menu);
    layoutMenu.add(new JGraphLayoutMenu());
    layoutMenu.add(debug_NodeDump);
  }
}

