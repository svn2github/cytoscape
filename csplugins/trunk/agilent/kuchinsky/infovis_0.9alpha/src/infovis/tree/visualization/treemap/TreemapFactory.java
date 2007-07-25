/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.tree.visualization.TreemapVisualization;
import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;

/**
 * Class TreemapFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class TreemapFactory extends BasicFactory {
    private static TreemapFactory instance;
    private static Logger logger = Logger.getLogger(TreemapFactory.class);
    protected HashMap creators = new HashMap();
    
    public static TreemapFactory getInstance() {
        if (instance == null) {
            instance = new TreemapFactory();
        }
        return instance;
    }
    
    public static void setInstance(TreemapFactory factory) {
        instance = factory;
    }
    
    public static Treemap createTreemap(String name, TreemapVisualization vis) {
        return getInstance().create(name, vis);
    }
    
    public TreemapFactory() {
        addDefaultCreators("treemapfactory");
    }
    
    protected void add(String name, String className, String data) {
        creators.put(name, new DefaultCreator(className));
    }
    
    public Treemap create(String name, TreemapVisualization vis) {
        Creator c = (Creator)creators.get(name);
        if (c == null) {
            return null;
        }
        return c.create(vis);
    }
    
    /**
     * Returns an iterator over the name of all the creators.
     * 
     * @return an iterator over the name of all the creators.
     */
    public Iterator iterator() {
        return creators.keySet().iterator();
    }
    
    public interface Creator {
        public Treemap create(TreemapVisualization vis);
    }
    
    public static class DefaultCreator implements Creator {
        protected String className;
        protected Class treemapClass;
        
        public DefaultCreator(String className) {
            this.className = className;
        }
        
        public Treemap create(TreemapVisualization vis) {
            if (treemapClass == null) {
                try {
                    treemapClass = Class.forName(className);
                }
                catch(ClassNotFoundException e) {
                    logger.error("Cannot load class named "+className, e);
                }
            }
            Class[] parameterTypes = { };
            Constructor cons = null;
            try {
                cons = treemapClass.getConstructor(parameterTypes);
            }
            catch(NoSuchMethodException ex) {
                logger.error(
                        "Cannot find constructor for Treemap class "+className, 
                        ex);
            }
            if (cons == null) return null;
            Object[] args = { };
            try {
                return (Treemap)cons.newInstance(args);
            }
            catch(Exception e) {
                logger.error("Cannot instantiate "+className, e);
            }
            return null;            
        }
    }
}
