package de.layclust.taskmanaging.gui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

public class SimpleTree extends JFrame{
	
	  public SimpleTree() {
		    super("Creating a Simple JTree");
		    WindowUtilities.setNativeLookAndFeel();
		    addWindowListener(new ExitListener());
		    Container content = getContentPane();
		    Object[] hierarchy =
		      { "javax.swing",
		        "javax.swing.border",
		        "javax.swing.colorchooser",
		        "javax.swing.event",
		        "javax.swing.filechooser",
		        new Object[] { "javax.swing.plaf",
		                       "javax.swing.plaf.basic",
		                       "javax.swing.plaf.metal",
		                       "javax.swing.plaf.multi" },
		        "javax.swing.table",
		        new Object[] { "javax.swing.text",
		                       new Object[] { "javax.swing.text.html",
		                                      "javax.swing.text.html.parser" },
		                       "javax.swing.text.rtf" },
		        "javax.swing.tree",
		        "javax.swing.undo" };
		    DefaultMutableTreeNode root = processHierarchy(hierarchy);
		    JTree tree = new JTree(root);
		    content.add(new JScrollPane(tree), BorderLayout.CENTER);
		    setSize(275, 300);
		    setVisible(true);
		  }

		  /**
			 * Small routine that will make node out of the first entry in the
			 * array, then make nodes out of subsequent entries and make them
			 * child nodes of the first one. The process is repeated recursively
			 * for entries that are arrays.
			 */
		    
		  private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
		    DefaultMutableTreeNode node =
		      new DefaultMutableTreeNode(hierarchy[0]);
		    DefaultMutableTreeNode child;
		    for(int i=1; i<hierarchy.length; i++) {
		      Object nodeSpecifier = hierarchy[i];
		      if (nodeSpecifier instanceof Object[])  // Ie node with children
		        child = processHierarchy((Object[])nodeSpecifier);
		      else
		        child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
		      node.add(child);
		    }
		    return(node);
		  }
	

}
