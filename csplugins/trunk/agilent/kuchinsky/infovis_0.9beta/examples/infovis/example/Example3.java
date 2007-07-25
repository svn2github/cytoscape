/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.BasicConfigurator;

import infovis.column.ColumnFilter;
import infovis.column.filter.InternalFilter;
import infovis.io.AbstractReader;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.VisualizationPanel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualPanel;
import infovis.table.visualization.ScatterPlotVisualization;

/**
 * Class Example3
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class Example3 {
    public static void main(String[] args) {
        BasicConfigurator.configure(); // Configure log4j
        String fileName =
            (args.length == 0) ? "data/salivary.tqd" : args[0];
        DefaultTable table = new DefaultTable();
        table.setName(fileName);
        AbstractReader reader =
            TableReaderFactory.createTableReader(fileName, table);
        if (reader == null || !reader.load()) {
            System.err.println("cannot load " + fileName);
            return;
        }
        ScatterPlotVisualization plot = new ScatterPlotVisualization(table); 
        DynamicQueryPanel dq = new DynamicQueryPanel(plot, table); 
        ColumnFilter filter = new InternalFilter();
         
        ScatterPlotVisualPanel panel = new ScatterPlotVisualPanel(plot, filter, dq);
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Visual", panel);
        tabs.add("Filters", dq);
         
        JFrame frame = new JFrame("Scatterplot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                new VisualizationPanel(plot),
                tabs);
        split.setResizeWeight(1);
        frame.getContentPane().add(split);
        frame.setVisible(true); 
        //frame.pack(); 
    }
}
