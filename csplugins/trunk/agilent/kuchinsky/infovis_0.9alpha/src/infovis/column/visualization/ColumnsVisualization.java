/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.column.visualization;

import java.awt.geom.Rectangle2D;

import infovis.*;
import infovis.column.ObjectColumn;
import infovis.column.StringColumn;
import infovis.table.DefaultTable;
import infovis.utils.*;
import infovis.visualization.Orientation;
import infovis.visualization.render.VisualLabel;
import infovis.visualization.render.VisualVisualization;
import infovis.visualization.ruler.DiscreteRulersBuilder;

/**
 * Class ColumnsVisualization
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 */
public class ColumnsVisualization extends ColumnVisualization {
    public static final String PROPERTY_COLUMN_PERMUTATION = "columnPermutation";
    public static final String PROPERTY_ROW_PERMUTATION = PROPERTY_PERMUTATION;
    protected ObjectColumn visualizationColumn;
    protected Table realTable;
    
    public ColumnsVisualization(Table table) {
        super(new DefaultTable(), new ObjectColumn("visualization"));
        this.realTable = table;
        createRulers();
        visualizationColumn = (ObjectColumn)column;
        VisualVisualization vv = VisualVisualization.get(this);
        if (vv != null) {
            vv.setColumn(column);
        }
        StringColumn sc = new StringColumn("label");
        getTable().addColumn(sc);
        VisualLabel vl = VisualLabel.get(this);
        vl.setColumn(sc);
        vl.setShowingLabel(false);
        for (int i = 0; i < table.getColumnCount(); i++) {
            Column c = table.getColumnAt(i);
            if (! c.isInternal()) {
                ColumnVisualization cv = new ColumnVisualization(table, c);
                visualizationColumn.add(cv);
                sc.add(c.getName());
                //cv.setVisualColumn(VISUAL_LABEL, null);
            }
        }
        setOrientation(orientation);
    }
    
    public Visualization getVisualization(int index) {
        if (index < 0 || index >= visualizationColumn.size())
            return null;
        return (Visualization)visualizationColumn.get(index);
    }
    
    public void setOrientation(short orientation) {
        super.setOrientation(orientation);
        short turn = Orientation.turn90(orientation);
        for (RowIterator iter = visualizationColumn.iterator(); iter.hasNext(); ) {
            int row = iter.nextRow();
            Visualization vis = (Visualization)visualizationColumn.get(row);
            vis.setOrientation(turn);
        }
    }
    
    protected void computeRulers(Rectangle2D bounds) {
        super.computeRulers(bounds);
        if (rulers == null) return;
        if (visualizationColumn.size() == 0) return;
        ColumnVisualization vis = (ColumnVisualization)visualizationColumn.get(0);
        int nrows = realTable.getRowCount();
        double w;
        if (Orientation.isHorizontal(vis.getOrientation())) {
            w = bounds.getWidth() / nrows;
        }
        else {
            w = bounds.getHeight() / nrows;
        }
        int i = 0;
        for (RowIterator iter = vis.iterator(); iter.hasNext(); i++) {
            int row = iter.nextRow();
            String label = Integer.toString(row);
            int ruler = rulers.getRowCount();
            
            if (Orientation.isHorizontal(vis.getOrientation())) {
                DiscreteRulersBuilder.createVerticalRuler(bounds, label, w*i+w/2, rulers);
            }
            else {
                DiscreteRulersBuilder.createHorizontalRuler(bounds, label, w*i+w/2, rulers);
            }
            rulersSize.setExtend(ruler, w);
            rulersColor.setExtend(ruler, computeRulerColor(i));
            rulersRow.setExtend(ruler, row);
        }        
    }
    
    public Permutation getRowComparator() {
        Visualization vis = getVisualization(0);
        if (vis == null) return null;
        return vis.getPermutation();
    }

    public void setRowComparator(RowComparator comp) {
        Visualization vis = getVisualization(0);
        if (vis == null) return;
        vis.setPermutation(comp);
        comp = vis.getPermutation();
        
        for (int i = 1; i < visualizationColumn.size(); i++) {
            vis = getVisualization(i);
            vis.setPermutation(comp);
        }
    }
}
