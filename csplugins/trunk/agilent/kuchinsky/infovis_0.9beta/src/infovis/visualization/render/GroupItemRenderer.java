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
 * Class GroupItemRenderer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class GroupItemRenderer extends AbstractItemRenderer {
    public GroupItemRenderer() {
        super(null);
    }
    
    public GroupItemRenderer(ItemRenderer c1) {
        super(null);
        addRenderer(c1);
    }
    
    public GroupItemRenderer(Object[] list) {
        super(null);
        for (int i = 0; i < list.length; i++) {
            ItemRenderer c = (ItemRenderer)list[i];
            addRenderer(c);
        }
    }
    
    public GroupItemRenderer(ItemRenderer c1, ItemRenderer c2) {
        super(null);
        addRenderer(c1);
        addRenderer(c2);
    }
    
    public GroupItemRenderer(
            ItemRenderer c1, 
            ItemRenderer c2, 
            ItemRenderer c3) {
        super(null);
        addRenderer(c1);
        addRenderer(c2);
        addRenderer(c3);
    }
    
    public GroupItemRenderer(
            ItemRenderer c1, 
            ItemRenderer c2, 
            ItemRenderer c3,
            ItemRenderer c4) {
        super(null);
        addRenderer(c1);
        addRenderer(c2);
        addRenderer(c3);
        addRenderer(c4);
    }
    
    public GroupItemRenderer(
            ItemRenderer c1, 
            ItemRenderer c2, 
            ItemRenderer c3,
            ItemRenderer c4,
            ItemRenderer c5) {
        super(null);
        addRenderer(c1);
        addRenderer(c2);
        addRenderer(c3);
        addRenderer(c4);
        addRenderer(c5);
    }
    
    public void concat(ItemRenderer r) {
        for (int i = 0; i < r.getRendererCount(); i++) {
            addRenderer(r.getRenderer(i));
        }
    }

    public ItemRenderer compile() {
        return super.compileGroup();
    }
}
