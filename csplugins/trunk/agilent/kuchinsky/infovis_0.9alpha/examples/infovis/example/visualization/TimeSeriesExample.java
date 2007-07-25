/*****************************************************************************
 * Copyright (C) 2003 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the QPL Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/

package infovis.example.visualization;
import infovis.example.*;
import infovis.table.DefaultTable;
import infovis.table.io.TQDTableReader;
import infovis.table.visualization.TimeSeriesVisualization;

import java.io.File;

/**
 * Example of time series visualization.
 *
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */

public class TimeSeriesExample {
    public static void main(String args[]) {
        ExampleRunner example =
            new ExampleRunner(args, "TimeSeriesExample");
//        if (example.fileCount() != 1) {
//            System.err.println("Syntax: TimeSeriesExample <name>");
//            System.exit(1);
//        }

        File file = new File(example.getArg(0));
        DefaultTable t = new DefaultTable();
        if (TQDTableReader.load(file, t)) {
            TimeSeriesVisualization visualization =
                new TimeSeriesVisualization(t);

            visualization.setVisualColumn(
                TimeSeriesVisualization.VISUAL_LABEL,
                ExampleRunner.getStringColumn(t, 0));

            example.createFrame(visualization);
        }
        else {
            System.err.println("cannot load " + example.getArg(0));
        }
    }

}
