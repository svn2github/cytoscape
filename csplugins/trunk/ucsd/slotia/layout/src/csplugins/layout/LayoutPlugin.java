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
import csplugins.layout.algorithms.radialHierarchicalLayout.RadialHierarchicalLayoutAlgorithm;
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayoutMenu;
import csplugins.layout.algorithms.graphPartition.ISOMLayout;

public class LayoutPlugin extends CytoscapePlugin
{
  public LayoutPlugin()
  {
    initialize();
  }

  protected void initialize()
  {
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
              } // end construct()

            }; // end SwingWorker

            worker.start();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction

    JMenuItem hierarchical = new JMenuItem("Hierarchical");
    {
      HierarchicalLayoutListener hierarchicalListener = new HierarchicalLayoutListener();
      hierarchical.addActionListener(hierarchicalListener);
    }

    JMenuItem radHier = new JMenuItem(new AbstractAction("Radial Hierarchical")
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
                 RadialHierarchicalLayoutAlgorithm(Cytoscape.getCurrentNetworkView());
                Cytoscape.getCurrentNetworkView().applyLayout(layout);
                return null;
              } // end construct()

            }; // end SwingWorker

            worker.start();
          } // end run()

        }); // end new Runnable()

      } // end actionPerformed()

    }); // end new AbstractAction

    JMenuItem springEmbAll = new JMenuItem(new SpringEmbeddedLayoutAction(true));
    JMenuItem springEmbSome = new JMenuItem(new SpringEmbeddedLayoutAction(false));
    JMenu springEmbMenu = new JMenu("Apply Spring Embedded");
    springEmbMenu.add(springEmbAll);
    springEmbMenu.add(springEmbSome);

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

    JMenu menu = new JMenu("Layout");
    menu.add(circle);
    menu.add(radial);
    menu.add(spring);
    menu.add(isom);
    menu.add(hierarchical);
    menu.add(radHier);
    menu.add(springEmbMenu);
    menu.add(new AttributeCircleLayoutMenu());
    menu.add(isomLay);

    Cytoscape.getDesktop().getCyMenus().getMenuBar().getMenu("Layout").add(menu);
  }
}

