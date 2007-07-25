/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.Visualization;
import infovis.column.ColumnFilter;
import infovis.utils.LogFunction;
import infovis.utils.SqrtFunction;
import infovis.visualization.render.AbstractVisualColumn;
import infovis.visualization.render.VisualSize;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * Visual Panel for additive visual attribute.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 */
public class AdditiveTreeVisualPanel extends TreeVisualPanel {

    /**
     * Constructor for TreemapVisualPanel.
     * @param visualization the visualization
     * @param filter the ColumnFilter
     */
    public AdditiveTreeVisualPanel(Visualization visualization, ColumnFilter filter) {
        super(visualization, filter);
    }

    protected JComponent getPanelFor(AbstractVisualColumn vc) {
        JComponent comp = super.getPanelFor(vc);
        if (vc.getName().equals(VisualSize.VISUAL)) {
            addSize(comp);
        }
        return comp;
    }

    protected void addSize(JComponent comp) {
        Box sizeFunctionBox = Box.createHorizontalBox();
        sizeFunctionBox.setAlignmentX(LEFT_ALIGNMENT);
        ButtonGroup group = new ButtonGroup();
        JRadioButton noTransform = new JRadioButton(
                "None", 
                getTreeVisualization().getTransformFunction() == null);
        noTransform.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTreeVisualization().setTransformFunction(null);
            }
        });
        group.add(noTransform);
        sizeFunctionBox.add(noTransform);
        
        JRadioButton logTransform = new JRadioButton(
                "Log",
                getTreeVisualization().getTransformFunction() 
                == LogFunction.getInstance());
        logTransform.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTreeVisualization().setTransformFunction(LogFunction.getInstance());
            }
        });
        group.add(logTransform);
        sizeFunctionBox.add(logTransform);
        
        JRadioButton sqrtTransform = new JRadioButton(
                "Sqrt",
                getTreeVisualization().getTransformFunction() 
                == SqrtFunction.getInstance());
        sqrtTransform.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getTreeVisualization().setTransformFunction(SqrtFunction.getInstance());
            }
        });
        group.add(sqrtTransform);
        sizeFunctionBox.add(sqrtTransform);
        setTitleBorder(sizeFunctionBox, "Size Transforms");
        comp.add(sizeFunctionBox);
    }    
}
