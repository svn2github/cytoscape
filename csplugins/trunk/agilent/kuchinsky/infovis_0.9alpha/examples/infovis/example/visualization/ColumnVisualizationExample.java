/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.example.visualization;

import infovis.column.visualization.ColumnsVisualization;
import infovis.example.ExampleRunner;
import infovis.io.AbstractReader;
import infovis.table.DefaultTable;
import infovis.table.io.TableReaderFactory;

/**
 * Class ColumnVisualizationExample
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class ColumnVisualizationExample {
    public static void main(String args[]) {
        ExampleRunner example =
            new ExampleRunner(args, "ColumnsVisualizationExample");
        String file = example.getArg(0);
        DefaultTable t = new DefaultTable();
        AbstractReader reader = 
            TableReaderFactory.createTableReader(file, t);
        if (reader != null && reader.load()) {
/*
            Column c = null;
            for (int i = 0; i < t.getColumnCount(); i++) {
                c = t.getColumnAt(i);
                if (c.isInternal()) {
                    c = null;
                }
                else if (c instanceof NumberColumn)
                    break;
            }
            ColumnVisualization visualization =
                new ColumnVisualization(t, c);
*/
            
            ColumnsVisualization visualization =
                new ColumnsVisualization(t);
            example.createFrame(visualization);
        }
        else {
            System.err.println("cannot load " + file);
        }
    }

}
