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
 * Factory of Treemap layout algorithms.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class TreemapFactory extends BasicFactory {
    private static TreemapFactory instance;
    private static Logger logger = Logger.getLogger(TreemapFactory.class);
    protected HashMap creators = new HashMap();

    /**
     * Returns the global instance of this factory.
     * @return the instance of this factory
     */
    public static TreemapFactory getInstance() {
        if (instance == null) {
            instance = new TreemapFactory();
        }
        return instance;
    }
    
    /**
     * Sets the global instance of this factory.
     * @param factory the TreemapFactory
     */
    public static void setInstance(TreemapFactory factory) {
        instance = factory;
    }
    
    /**
     * Creates a Treemap layout given its name.
     * @param name the name
     * @param vis the visualization
     * @return a Treemap layout or null
     */
    public static Treemap createTreemap(String name, TreemapVisualization vis) {
        return getInstance().create(name, vis);
    }
    
    /**
     * Constructor.
     */
    public TreemapFactory() {
        addDefaultCreators("treemapfactory");
    }
    
    protected void add(String name, String className, String data) {
        creators.put(name, new DefaultCreator(className));
    }

    /**
     * Creates a new Treemap layout from a specified name.
     * @param name the name
     * @param vis the visualization
     * @return a new Treemap layout
     */
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
    
    /**
     * Creator for Treemap layout objects.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.4 $
     */
    public interface Creator {
        /**
         * Creates the Treemap layout.
         * @param vis the visualization
         * @return a Treemap layout
         */
        Treemap create(TreemapVisualization vis);
    }
    
    /**
     * Default implementation of the Creator interface.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.4 $
     */
    public static class DefaultCreator implements Creator {
        protected String className;
        protected Class treemapClass;
        
        /**
         * Constructor.
         * @param className the Treemap layout class name
         */
        public DefaultCreator(String className) {
            this.className = className;
        }
        
        /**
         * {@inheritDoc}
         */
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
