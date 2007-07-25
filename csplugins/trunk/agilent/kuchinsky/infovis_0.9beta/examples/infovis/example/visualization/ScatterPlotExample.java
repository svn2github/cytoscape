/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;
import infovis.Table;
import infovis.Visualization;
import infovis.example.ExampleRunner;
import infovis.io.AbstractReader;
import infovis.panel.*;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualization;
import infovis.visualization.DefaultAxisVisualization;
import infovis.visualization.Orientable;
import infovis.visualization.inter.InteractorFactory;
import infovis.visualization.render.DefaultVisualLabel;
import infovis.visualization.render.VisualLabel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

/**
 * Example of scatter plot visualization.
 * 
 * Uses a specific Layout for the axis
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.8 $
 */
public class ScatterPlotExample extends ExampleRunner {
    
    /**
     * Creates a ScatterPlotExample from args and a name.
     * @param args the main program args
     * @param name the name
     */
    public ScatterPlotExample(String[] args, String name) {
        super(args, name);
    }
    
    /**
     * Creates a non-standard control panel for the scatterplot.
     * 
     * @param frame the JFrame of this example
     * @param visualization the visualization 
     * @return a ControlPanel
     */
    public ControlPanel create(JFrame frame, Visualization visualization) {
        this.visualization = visualization;
        menuBar = new JMenuBar();

        frame.setJMenuBar(menuBar);
        fileMenu = createFileMenu();
        menuBar.add(fileMenu);
        ControlPanel control = ControlPanelFactory
                .createControlPanel(visualization);
        if (control == null) {
            return control;
        }
        InteractorFactory.installInteractor(control.getVisualization());
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        DefaultAxisVisualization column = new DefaultAxisVisualization(
                visualization,
                Orientable.ORIENTATION_SOUTH);
        InteractorFactory.installInteractor(column);
        VisualLabel vl = VisualLabel.get(column);
        if (vl instanceof DefaultVisualLabel) {
            DefaultVisualLabel dvl = (DefaultVisualLabel)vl;
            dvl.setOrientation(Orientable.ORIENTATION_SOUTH);
        }
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        pane.add(new VisualizationPanel(column), c);

        DefaultAxisVisualization row = new DefaultAxisVisualization(
                visualization,
                Orientable.ORIENTATION_EAST);
        InteractorFactory.installInteractor(row);
        vl = VisualLabel.get(row);
        if (vl instanceof DefaultVisualLabel) {
            DefaultVisualLabel dvl = (DefaultVisualLabel)vl;
            dvl.setOrientation(Orientable.ORIENTATION_EAST);
        }
        c.fill = GridBagConstraints.VERTICAL;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(new VisualizationPanel(row), c);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        pane.add(new VisualizationPanel(control.getVisualization()), c);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                pane, control);
        split.setResizeWeight(1.0);
        frame.getContentPane().add(split);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        return control;
    }

    /**
     * Main program.
     * @param args argument list.
     */
    public static void main(String[] args) {
        ExampleRunner example =
            new ScatterPlotExample(args, "ScatterPlotExample");

        final Table t = new DefaultTable();
        AbstractReader reader =
            TableReaderFactory.createTableReader(example.getArg(0), t);

        if (reader != null && reader.load()) {
            ScatterPlotVisualization visualization =
                new ScatterPlotVisualization(t);
            ControlPanel control = example.createFrame(visualization);
            DynamicQueryPanel jquery = control.getDynamicQueryPanel();

            // CHECK++
            visualization.setXAxisModel(
                (
                    NumberColumnBoundedRangeModel) jquery
                        .getColumnDynamicQuery(
                    visualization.getXAxisColumn()));
            visualization.setYAxisModel(
                (
                    NumberColumnBoundedRangeModel) jquery
                        .getColumnDynamicQuery(
                    visualization.getYAxisColumn()));

        }
        else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }
}
