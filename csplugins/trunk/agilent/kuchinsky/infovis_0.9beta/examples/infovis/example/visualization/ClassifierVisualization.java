/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;


import infovis.*;
import infovis.column.*;
import infovis.panel.*;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.NodeLinkTreeControlPanel;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.tree.visualization.nodelink.RTLayout;
import infovis.utils.RowIterator;
import infovis.visualization.LinkVisualization;
import infovis.visualization.color.OrderedColor;
import infovis.visualization.linkShapers.DendrogramLinkShaper;
import infovis.visualization.render.*;

import java.awt.Color;
import java.text.*;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;


public class ClassifierVisualization {
    static Color[] ramp = { Color.WHITE, Color.BLUE };
    
    /**
     * Column managin a list of strings
     */
    static public class ListColumn extends BasicObjectColumn {
        public ListColumn(String name) {
            super(name);
            setFormat(ListFormat.instance);
        }
        public String[] get(int index) {
            return (String[]) getObjectAt(index);
        }
        public void set(int index, String[] element) {
            setObjectAt(index, element);
        }
        public void setExtend(int index, String[] element) {
            super.setExtend(index, element);
        }
        public void add(String[] element) {
            super.add(element);
        }
        public void fill(String[] val) {
            super.fill(val);
        }
        
        public static ListColumn getColumn(Table t, int index) {
            Column c = t.getColumnAt(index);

            if (c instanceof ListColumn) {
                return (ListColumn) c;
            }
            else {
                return null;
            }
        }

        public static ListColumn getColumn(Table t, String name) {
            Column c = t.getColumn(name);

            if (c instanceof ListColumn) {
                return (ListColumn) c;
            }
            else {
                return null;
            }
        }
        
        /**
         * Format for importing/exporting list of string
         * specified by strings separated by semi-colons.
         */
        static class ListFormat extends Format {
            static ListFormat instance = new ListFormat();
            
            public Object parseObject(String source, ParsePosition pos) {
                pos.setIndex(source.length());
                return source.split(";");
            }
            
            public StringBuffer format(
                    Object obj,
                    StringBuffer toAppendTo,
                    FieldPosition pos) {
                String[] s = (String[])obj;
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < s.length; i++) {
                    if (i != 0) {
                        sb.append(';');
                    }
                    sb.append(s[i]);
                }
                
                toAppendTo.append(sb.toString());
                return toAppendTo;
            }
        }
        public static StringColumn findColumn(Table t, String name) {
            Column c = t.getColumn(name);

            if (c == null) {
                c = new StringColumn(name);
                t.addColumn(c);
            }

            return (StringColumn) c;
        }

        public Class getValueClass() {
            return String[].class;
        }
        
        static private final String[] DEFINED = new String[0];

        public Object definedValue() {
            return DEFINED;
        }
    }
    
    /**
     * Class for selecting all the links connecting a selected
     * node to its root.
     */
    public static class TreePathSelection extends ColumnLink {
        BooleanColumn pathSelection;
        Tree tree;
        TreePathSelection(
                Tree tree,
                BooleanColumn selection, 
                BooleanColumn pathSelection) {
            super(selection, pathSelection);
            this.pathSelection = pathSelection;
            this.tree = tree;
        }
        
        public void update() {
            pathSelection.clear();
            for (RowIterator iter = fromColumn.iterator(); iter.hasNext(); ) {
                for (int row = iter.nextRow(); 
                    row != Tree.ROOT; 
                    row = tree.getParent(row)) {
                    pathSelection.set(row, true);
                }
            }
        }
    }
    public static void main(String[] args) {
        // Add the new column class to the factory that automatically create the columns
        // when loading a file
        ColumnFactory.getInstance().add("list", ListColumn.class.getName(), "DENSE");
        // Create the InfoVis Tree
        Tree t = new DefaultTree();
        String arg = args.length == 0 ?  "data/tree/classifier.tm3" : args[0];
        // Try to load it
        if (! TreeReaderFactory.readTree(arg, t)) {
            System.err.println("cannot load " + arg);
            System.exit(1);
        }
        t.getColumn("name").setValueOrNullAt(Tree.ROOT, arg);
        // Create the column which will contain the paths to the selected nodes
        BooleanColumn pathSelection = new BooleanColumn("#pathSelection");
        // Add it to the Tree
        t.addColumn(pathSelection);
        
        // Create a visualization for the tree
        NodeLinkTreeVisualization visualization = new NodeLinkTreeVisualization(t);
        // The layout will use DendrogGrams
        RTLayout layout = new RTLayout();
        layout.setSiblingSeparation(100);
        visualization.setLayout(layout);
        
        // Create an object that recomputes the path selection from the selection
        new TreePathSelection(t, visualization.getSelection(), pathSelection);
        
        // Change the default label visibility and color
        DefaultVisualLabel vl = (DefaultVisualLabel)VisualLabel.get(visualization);
        vl.setClipped(false);
        vl.setDefaultColor(Color.BLACK);
        
        VisualSize vs = VisualSize.get(visualization);
        vs.setDefaultSize(25);
        
        // Change the color ramp for the items
        OrderedColor.setDefaultRamp(ramp);
        // Color is assoicated with the column called "faux positifs"
        visualization.setVisualColumn(
                NodeLinkTreeVisualization.VISUAL_COLOR,
                t.getColumn("faux positifs"));
        
        LinkVisualization lv = visualization.getLinkVisualization();
        // Change the shape of links to DendroGrams
        lv.setLinkShaper(new DendrogramLinkShaper());
        // Link color is associated with the column called "verif test precedent" 
        lv.setVisualColumn(
                NodeLinkTreeVisualization.VISUAL_COLOR,
                t.getColumn("verif test précédent"));
        // Link selection is associated with the column we have created and managed
        lv.setVisualColumn(
                NodeLinkTreeVisualization.VISUAL_SELECTION,
                pathSelection);
        
        // Change the control panel to add more details: the list and the tree
        ControlPanel cp = new NodeLinkTreeControlPanel(visualization) {
            DefaultListModel model;
            ListColumn column;
            BooleanColumn sel;
            JScrollPane scroll;
            protected JComponent createDetailControlPanel() {
                sel = visualization.getSelection();
                model = new DefaultListModel();
                column = (ListColumn)getVisualization().getTable().getColumn("examples");

                Box panel = Box.createVerticalBox();
                panel.add(DetailTable.createDetailJTable(
                        getFilteredTable(), 
                        sel));
                JList list = new JList(model);
                scroll = new JScrollPane(
                        list,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                sel.addChangeListener(new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        TreeSet set = new TreeSet();
                        for (RowIterator iter = column.iterator(); iter.hasNext(); ) {
                            int row = iter.nextRow();
                            if (sel.get(row)) {
                                String[] l = column.get(row);
                                if (l != null) {
                                    for (int i = 0; i < l.length; i++) {
                                        set.add(l[i]);
                                    }
                                }
                            }
                        }
                        model.clear();
                        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
                            model.addElement(iter.next());
                        }
                        scroll.invalidate();
                    }
                });
                panel.add(scroll);
                
                JTree jtree = new JTree(createTree());
                JScrollPane treeScroll = new JScrollPane(jtree);
                panel.add(treeScroll);
                return panel;
            }            
        };
        //example.createFrame(visualization);
        JFrame frame = new JFrame("Classifier Visualization");
        JSplitPane split = ControlPanelFactory.createScrollVisualization(cp);
        split.setResizeWeight(1.0);
        frame.getContentPane().add(split);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static DefaultTreeModel createTree() {
        DefaultMutableTreeNode top =
            new DefaultMutableTreeNode("Racine");
        DefaultMutableTreeNode f1 = new DefaultMutableTreeNode("f1");
        top.add(f1);
        DefaultMutableTreeNode f2 = new DefaultMutableTreeNode("f2");
        top.add(f2);
        
        return new DefaultTreeModel(top);
    }
}