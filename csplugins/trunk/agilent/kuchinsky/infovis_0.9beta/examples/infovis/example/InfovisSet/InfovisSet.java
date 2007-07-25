/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example.InfovisSet;

import infovis.*;
import infovis.column.visualization.ColumnsVisualization;
import infovis.graph.DefaultGraph;
import infovis.graph.io.GraphReaderFactory;
import infovis.graph.visualization.MatrixVisualization;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.panel.*;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.*;
import infovis.tree.DefaultTree;
import infovis.tree.io.TreeReaderFactory;
import infovis.tree.visualization.*;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

import org.apache.log4j.*;
import org.apache.log4j.Logger;

/**
 * Example showing all the existing visualization in one application.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.10 $
 */
public class InfovisSet extends JFrame {
    private final Logger logger = Logger.getLogger(InfovisSet.class);
    String tableFile;
    String treeFile;
    String graphFile;

    AbstractAction quitAction = new DefaultAction("Quit", 'Q') {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    };

    AbstractAction aboutAction = new AbstractAction("About...") {
        public void actionPerformed(ActionEvent e) {
            JOptionPane
                    .showMessageDialog(
                            null,
                            "InfovisSet\n(C) 2005 \nINRIA Futurs",
                            "about Infovis...",
                            JOptionPane.INFORMATION_MESSAGE);
        }
    };
    
	public InfovisSet() {
        this(
                "data/table/salivary.tqd",
                "data/tree/testtree.xml",
                "data/graph/iv04author-cocitations.xml");
    }
    
    public InfovisSet(String tableFile, String treeFile, String graphFile) {
		super("InfovisSet");
        this.tableFile = tableFile;
        this.treeFile = treeFile;
        this.graphFile = graphFile;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMenus();
        getContentPane().add(createTabs());
        setSize(new Dimension(1024, 800));
        setVisible(true);
	}
	
	/**
	 * Creates the menus.
	 */
	protected void createMenus() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.addSeparator();
        fileMenu.add(quitAction);
		menuBar.add(fileMenu);
        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(aboutAction);
		menuBar.add(helpMenu);
        setJMenuBar(menuBar);
	}
    
    private JTabbedPane createTabs() {
        Graph graph = new DefaultGraph();
        if (!GraphReaderFactory.readGraph(graphFile, graph)) {
            logger.error("Cannot read graph " + graphFile);
            graph = null;
        }
        Tree tree = new DefaultTree();
        if (!TreeReaderFactory.readTree(treeFile, tree)) {
            logger.error("Cannot read tree " + treeFile);
            tree = null;
        }
        Table table = new DefaultTable();
        if (!TableReaderFactory.readTable(tableFile, table)) {
            logger.error("Cannot read table " + tableFile);
            table = null;
        }
        JTabbedPane tab = new JTabbedPane();
        tab.add("About", new SplashPanel());
        if (graph != null) {
            tab.add("Graph Matrix",
                    createPane(new MatrixVisualization(graph)));
            tab.add("Graph Node-Link", 
                    createPane(new NodeLinkGraphVisualization(graph)));
        }
        if (tree != null) {
            tab.add("Tree Icicle", 
                    createPane(new IcicleTreeVisualization(tree)));
            tab.add("Treemap", 
                    createPane(new TreemapVisualization(tree)));
            tab.add("Tree Node-Link", 
                    createPane(new NodeLinkTreeVisualization(tree)));
        }
        if (table != null) {
            tab.add("Table Matrix", 
                    createPane(new ColumnsVisualization(table)));
            tab.add("Table Scatter Plot", 
                    createPane(new ScatterPlotVisualization(table)));
            tab.add("Table Time Series", 
                    createPane(new TimeSeriesVisualization(table)));
            tab.add("Table Parallel Coordinates", 
                    createPane(new ParallelCoordinatesVisualization(table)));
        }
        return tab;
    }
    
    private JComponent createPane(Visualization visualization) {
        return ControlPanelFactory.createScrollVisualization(visualization);
    }
    
    public static void main(String[] arguments) {
        File loggerConfig = new File("properties/log4j.properties");
        if (loggerConfig.exists()) {
            PropertyConfigurator.configure(loggerConfig.toString());
        } else {
            BasicConfigurator.configure();
        }
		JFrame.setDefaultLookAndFeelDecorated(true);
		new InfovisSet();
	}
}