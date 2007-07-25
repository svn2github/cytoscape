/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.apache.log4j.Logger;

import infovis.utils.BasicFactory;
import infovis.visualization.ItemRenderer;

/**
 * Factory of ItemInteractors
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.2 $
 */
public class RendererInteractorFactory extends BasicFactory {
    private static RendererInteractorFactory instance;
    private static Logger logger = Logger.getLogger(RendererInteractorFactory.class);

    public static RendererInteractorFactory getInstance() {
        if (instance == null) {
            instance = new RendererInteractorFactory();
        }
        return instance;
    }
    
    public static void setInstance(RendererInteractorFactory inst) {
        instance = inst;
    }
    
    public static BasicVisualizationInteractor createInteractor(ItemRenderer renderer) {
        return getInstance().create(renderer);
    }
    
    protected HashMap creators = new HashMap();
    
    protected RendererInteractorFactory() {
        addDefaultCreators("rendererinteractorfactory");
    }
    
    public void add(String name, String className, String data) {
        if (creators.get(name) != null) {
            logger.info("Replacing Creator for "+name+" by "+className); 
        }
        creators.put(name, new Creator(className));
    }
    
    public boolean remove(String name) {
        return creators.remove(name) != null;
    }
    
    public BasicVisualizationInteractor create(ItemRenderer renderer) {
        for (Class cls = renderer.getClass(); 
            cls != Object.class; 
            cls = cls.getSuperclass()) {
            Creator c = (Creator)creators.get(cls.getName());
            if (c != null) {
                return c.create(renderer);
            }
        }
        return null;
    }

    public static class Creator {
        protected String rendererClassName;
        protected Class rendererClass;
        
        public Creator(String rendererClassName) {
            this.rendererClassName = rendererClassName;
        }
        
        public BasicVisualizationInteractor create(ItemRenderer renderer) {
            try {
                if (rendererClass == null) {
                    rendererClass = Class.forName(rendererClassName);
                }
                Class[] parameterTypes = { ItemRenderer.class };
                Constructor cons = rendererClass.getConstructor(parameterTypes);
                Object[] args = { renderer }; 
                return (BasicVisualizationInteractor)cons.newInstance(args);
            }
            catch(Exception e) {
                logger.error("Cannot instantiate new RendererInteractor ", e);
            }
            return null;
        }
    }
}
