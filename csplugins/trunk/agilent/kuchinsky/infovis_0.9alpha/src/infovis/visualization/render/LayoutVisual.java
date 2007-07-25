/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.visualization.ItemRenderer;

/**
 * LayoutVisual should contain renderer that are used for layout
 * only. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class LayoutVisual extends NoVisual {
    public LayoutVisual(ItemRenderer child) {
        super(child);
    }

}
