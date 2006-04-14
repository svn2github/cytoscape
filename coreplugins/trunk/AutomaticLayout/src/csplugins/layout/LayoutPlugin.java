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
import csplugins.layout.algorithms.graphPartition.AttributeCircleLayoutMenu;
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

    JMenu menu = new JMenu("Cytoscape Layouts");
    menu.add(new AttributeLayoutMenu());
    menu.add(hierarchical);
    menu.add(springEmbMenu);
    menu.add(new AttributeCircleLayoutMenu());
    menu.add(degSortCircle);

    JMenu layoutMenu = Cytoscape.getDesktop().getCyMenus().getMenuBar()
                                .getMenu("Layout");
    layoutMenu.add(menu);
    layoutMenu.add(new JGraphLayoutMenu());
  }
}

