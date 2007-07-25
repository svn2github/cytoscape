/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;
import infovis.Table;
import infovis.example.ExampleRunner;
import infovis.io.AbstractReader;
import infovis.panel.ControlPanel;
import infovis.panel.DynamicQueryPanel;
import infovis.panel.dqinter.NumberColumnBoundedRangeModel;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;
import infovis.table.visualization.ScatterPlotVisualization;

import java.util.Locale;

/**
 * Example of scatter plot visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class ScatterPlotExample {
    public static void main(String[] args) {
        ExampleRunner example =
            new ExampleRunner(args, "ScatterPlotExample");
//        if (example.fileCount() != 1) {
//            System.err.println("Syntax: ScatterPlotExample <name>");
//            System.exit(1);
//        }
        Locale.setDefault(Locale.US);

        Table t = new DefaultTable();
        AbstractReader reader =
            TableReaderFactory.createTableReader(example.getArg(0), t);

        if (reader != null && reader.load()) {
            ScatterPlotVisualization visualization =
                new ScatterPlotVisualization(t);
//            ItemRenderer ir = new GroupItemRenderer(
//                    new LayoutVisual(new VisualSize(null)),
//                    new VisualShape(
//                            new VisualAlpha(new VisualColor(Fill.instance)),
//                            new VisualSelection(Stroke.instance)),
//                    new VisualLabel(null));
//            visualization.setItemRenderer(ir);
//            visualization.setVisualColumn("shape", t.getColumn("12 hour"));
            //RulerVisualization ruler = new RulerVisualization(visualization);
            ControlPanel control = example.createFrame(visualization); //visualization);

            // Connect dynamic query components to XAxis and YAxis components of
            // scatter plot to also control the scaling.
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
