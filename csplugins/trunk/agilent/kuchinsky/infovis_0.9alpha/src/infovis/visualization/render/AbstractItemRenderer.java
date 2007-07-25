/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Column;
import infovis.Visualization;
import infovis.utils.CompositeShape;
import infovis.visualization.ItemRenderer;
import infovis.visualization.VisualColumnDescriptor;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * Class AbstractItemRenderer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public abstract class AbstractItemRenderer implements ItemRenderer, Cloneable {
    protected String name;
    protected transient Visualization visualization;
    protected ArrayList renderers;
    
    public static ItemRenderer findNamed(String name, ItemRenderer ir) {
        if (ir == null) {
            return null;
        }
        if (name.equals(ir.getName())) {
            return ir;
        }
        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer ret = findNamed(name, ir.getRenderer(i));
            if (ret != null) {
                return ret;
            }
        }
        return null;
    }
    
    public static ItemRenderer findNamed(String name, Visualization vis) {
        VisualColumnDescriptor vc = vis.getVisualColumnDescriptor(name);
        if (vc instanceof ItemRenderer) {
            ItemRenderer ir = (ItemRenderer) vc;
            return ir;
        }
        return null;
    }
    
    public static boolean replaceNamed(ItemRenderer named, ItemRenderer ir) {
        if (ir == null) {
            return false;
        }

        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer c = ir.getRenderer(i);
            if (named.getName().equals(c.getName())) {
                for (int j = 0; j < c.getRendererCount(); j++) {
                    named.addRenderer(c.getRenderer(j));
                }
                ir.setRenderer(i, named);
                return true;
            }
            else {
                if (replaceNamed(named, c)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean replaceNamed(String name, ItemRenderer nir, ItemRenderer ir) {
        if (ir == null) {
            return false;
        }

        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer c = ir.getRenderer(i);
            if (name.equals(c.getName())) {
                for (int j = 0; j < c.getRendererCount(); j++) {
                    nir.addRenderer(c.getRenderer(j));
                }
                ir.setRenderer(i, nir);
                return true;
            }
            else {
                if (replaceNamed(name, nir, c)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean insertBeforeNamed(String name, ItemRenderer nir, ItemRenderer ir) {
        if (ir == null) {
            return false;
        }

        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer c = ir.getRenderer(i);
            if (name.equals(c.getName())) {
                ir.insertRenderer(i, nir);
                return true;
            }
            else {
                if (insertBeforeNamed(name, nir, c)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean insertAfterNamed(String name, ItemRenderer nir, ItemRenderer ir) {
        if (ir == null) {
            return false;
        }

        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer c = ir.getRenderer(i);
            if (name.equals(c.getName())) {
                ir.insertRenderer(i+1, nir);
                return true;
            }
            else {
                if (insertAfterNamed(name, nir, c)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean removeNamed(String name, ItemRenderer ir) {
        if (ir == null) {
            return false;
        }

        for (int i = 0; i < ir.getRendererCount(); i++) {
            ItemRenderer c = ir.getRenderer(i);
            if (name.equals(c.getName())) {
                c.removeRenderer(i);
                return true;
            }
        }
        return false;
    }
    
//    protected AbstractItemRenderer(ItemRenderer other, Visualization vis) {
//        if (other != null) {
//            this.name = other.getName();
//        }
//        this.visualization = vis;
//    }
    
    public AbstractItemRenderer(String name) {
        this.name = name;
    }
    
    public ItemRenderer instantiate(Visualization vis) {
        return ((AbstractItemRenderer)clone()).instantiateChildren(this, vis);
    }
    
    protected ItemRenderer instantiateChildren(
            AbstractItemRenderer proto,
            Visualization vis) {
        visualization = vis;
        for (int i = 0; i < proto.getRendererCount(); i++) {
            addRenderer(proto.getRenderer(i).instantiate(vis));
        }
        return this;
    }
    
    protected Object clone() {
        try {
            AbstractItemRenderer other = (AbstractItemRenderer) super.clone();
            other.renderers = new ArrayList();
            return other;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getName() {
        return name;
    }
    
    public Visualization getVisualization() {
        return visualization;
    }
    
    public int getRendererCount() {
        if (renderers == null)
            return 0;
        return renderers.size();
    }
    
    public ItemRenderer getRenderer(int index) {
        if (renderers == null
                || index >= getRendererCount()) {
            return null;
        }
        return (ItemRenderer)renderers.get(index); 
    }
    
    protected ArrayList getRenderers() {
        if (renderers == null) {
            renderers = new ArrayList();
        }
        return renderers;
    }
    
    public int indexOf(ItemRenderer r) {
        if (renderers == null) {
            return -1;
        }
        return renderers.indexOf(r);
    }
    
    public ItemRenderer insertRenderer(int index, ItemRenderer r) {
        if (r != null) {
            getRenderers().add(index, r);
        }
        return this;
    }
    
    public ItemRenderer addRenderer(ItemRenderer r) {
        if (r != null) { 
            getRenderers().add(r);
        }
        return this;
    }
    
    public ItemRenderer setRenderer(int index, ItemRenderer r) {
        getRenderers().set(index, r);
        return this;
    }
    
    public ItemRenderer removeRenderer(int index) {
        if (renderers != null) {
            renderers.remove(index);
        }
        return this;
    }
    
    public ItemRenderer compile() {
        return this;
    }
    
    public ItemRenderer compileGroup() {
        switch(getRendererCount()) {
        case 0:
            return null;
        case 1:
            return getRenderer(0).compile();
        }
        GroupItemRenderer group = new GroupItemRenderer();
        for (int i = 0; i < getRendererCount(); i++) {
            ItemRenderer c = getRenderer(0);
            ItemRenderer cc = c.compile();
            if (cc != null) {
                if (cc instanceof GroupItemRenderer) {
                    group.concat(cc); // flatten
                }
                else {
                    group.addRenderer(cc);
                }
            }
        }
        switch(group.getRendererCount()) {
        case 0:
            return null;
        case 1:
            return group.getRenderer(0).compile();
        }
        return group;
    }
    
    public void install(Graphics2D graphics) {
        for (int i = 0; i < getRendererCount(); i++) {
            getRenderer(i).install(graphics);
        }
    }
    
    public void paint(Graphics2D graphics, int row, Shape shape) {
        for (int i = 0; i < getRendererCount(); i++) {
            if (shape instanceof CompositeShape) {
                CompositeShape s2 = (CompositeShape)shape;
                for (int j = 0; j < s2.getShapeCount(); j++) {
                    paint(graphics, row, s2.getShape(j));
                }
            }
            else {
                getRenderer(i).paint(graphics, row, shape);
            }
        }
    }
    
    public void uninstall(Graphics2D graphics) {
        for (int i = getRendererCount()-1; i >= 0; i--) {
            getRenderer(i).uninstall(graphics);
        }
    }
    
    public boolean pick(Rectangle2D hitBox, int row, Shape shape) {
        for (int i = 0; i < getRendererCount(); i++) {
            if (shape instanceof CompositeShape) {
                CompositeShape s2 = (CompositeShape)shape;
                for (int j = 0; j < s2.getShapeCount(); j++) {
                    if (pick(hitBox, row, s2.getShape(j))) {
                        return true;
                    }
                }
            }
            else {
                if (getRenderer(i).pick(hitBox, row, shape)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isPrototype() {
        return visualization == null;
    }

    public void invalidate(Column c) {
        if (getVisualization() != null)
            getVisualization().invalidate(c);
    }
    
    public void repaint() {
        getVisualization().repaint();
    }
}
