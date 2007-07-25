/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Visualization;
import infovis.visualization.ItemRenderer;

import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Base class for ItemRenderer used as static objects. 
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public abstract class StaticItemRenderer implements ItemRenderer {
    public String getName() {
        return null;
    }

    public ItemRenderer getRenderer(int index) {
        return null;
    }

    public int getRendererCount() {
        return 0;
    }

    public ItemRenderer insertRenderer(int index, ItemRenderer r) {
        return this;
    }

    public ItemRenderer addRenderer(ItemRenderer r) {
        return this;
    }

    public ItemRenderer removeRenderer(int index) {
        return this;
    }

    public ItemRenderer setRenderer(int index, ItemRenderer r) {
        return this;
    }

    public Visualization getVisualization() {
        return null;
    }

    public void install(Graphics2D graphics) {
    }

    public void paint(Graphics2D graphics, int row, Shape shape) {
    }

    public void uninstall(Graphics2D graphics) {
    }
    
    public boolean isPrototype() {
        return true;
    }

    public ItemRenderer instantiate(Visualization vis) {
        return this;
    }
    
    public ItemRenderer compile() {
        return this;
    }

}
