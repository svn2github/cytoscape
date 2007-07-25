/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import java.awt.Color;

import infovis.visualization.ItemRenderer;
import infovis.visualization.render.VisualSelection;

public class TreeVisualSelection extends VisualSelection {

    public TreeVisualSelection(ItemRenderer child) {
        super(child);
    }

    public TreeVisualSelection(
            ItemRenderer child,
            Color selectedColor,
            Color unselectedColor) {
        super(child, selectedColor, unselectedColor);
    }

}
