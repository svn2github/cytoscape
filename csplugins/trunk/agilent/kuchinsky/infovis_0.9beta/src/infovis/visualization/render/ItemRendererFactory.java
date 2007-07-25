/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.render;

import infovis.Visualization;
import infovis.utils.BasicFactory;
import infovis.visualization.ItemRenderer;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.log4j.Logger;

/**
 * Class ItemRendererFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class ItemRendererFactory extends BasicFactory {
    private static ItemRendererFactory instance;
    private static Logger logger = Logger.getLogger(ItemRendererFactory.class);
    
    protected HashMap creators = new HashMap();
    
    public static ItemRendererFactory getInstance() {
        if (instance == null) {
            instance = new ItemRendererFactory();            
        }
        return instance;
    }
    
    public static ItemRendererFactory setInstance(ItemRendererFactory factory) {
        ItemRendererFactory old = instance;
        instance = factory;
        return old;
    }
    
    public static ItemRenderer createItemRenderer(Visualization vis) {
        return getInstance().create(vis);
    }
    
    public ItemRendererFactory() {
        addDefaultCreators("itemrendererfactory");
    }
    
    protected void add(String name, String className, String data) {
        creators.put(name, new DefaultCreator(className));
    }
    
    protected void add(String visClassName, ItemRenderer proto) {
        creators.put(visClassName, new DefaultCreator(proto));
    }
    
    public ItemRenderer create(Visualization visualization) {
        for (Class c = visualization.getClass(); 
            c != Object.class;
            c = c.getSuperclass()) {
            Creator creator = (Creator)creators.get(c.getName());
            if (creator != null) {
                return creator.create(visualization);
            }
        }
        logger.debug(
                "Factory cannot create ItemRenderer for "
                +visualization.getClass());
        return null;
    }
    
    public static interface Creator {
        public abstract ItemRenderer create(Visualization visualization);
    }
    
    public static class DefaultCreator implements Creator {
        protected String rendererName;
        protected ItemRenderer prototype;
        
        public DefaultCreator(String rendererName) { 
            this.rendererName = rendererName;
        }
        
        public DefaultCreator(ItemRenderer proto) {
            this.prototype = proto;
        }
        
        public ItemRenderer create(Visualization visualization) {
            if (prototype == null) {
                try {
                    Class protoClass = Class.forName(rendererName);
                    Constructor cons = protoClass.getConstructor(null);
                    prototype = (ItemRenderer)cons.newInstance(null);
                }
                catch(Exception e) {
                    logger.error(
                            "Cannot instantiate ItemRenderer class named "+rendererName, 
                            e);
                    return null;
                }
                
            }
            return prototype.instantiate(visualization);
        }

    }
}
