/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization.nodelink;

import infovis.tree.visualization.NodeLinkTreeLayout;
import infovis.tree.visualization.NodeLinkTreeVisualization;
import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Factory of Tree Layout objects.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 */
public class TreeLayoutFactory extends BasicFactory {
    private static TreeLayoutFactory instance;
    private static Logger logger = Logger.getLogger(TreeLayoutFactory.class);

    protected TreeMap creators = new TreeMap();

    /**
     * Returns the instance of this factory.
     * @return the instance of this factory.
     */
    public static TreeLayoutFactory getInstance() {
        if (instance == null) {
            instance = new TreeLayoutFactory();
        }
        return instance;
    }

    /**
     * Sets the instance of TreeLayoutFactory.
     * @param inst the new instance.
     */
    public static void setInstance(TreeLayoutFactory inst) {
        instance = inst;
    }

    /**
     * Returns a NodeLinkTreeLayout given its name and a NodeLinkTreeVisualization.
     * @param name the name
     * @param visualization the visualization
     * @return a NodeLinkTreeLayout given its name and a NodeLinkTreeVisualization.
     */
    public static NodeLinkTreeLayout createLayout(
            String name,
            NodeLinkTreeVisualization visualization) {
        return getInstance().create(name, visualization);
    }

    /**
     * Register a new layout class.
     * @param name the class name
     * @param c the class
     */
    public static void addLayout(String name, Class c) {
        getInstance().add(name, c);
    }

    /**
     * Returns an iterator over the names of layouts.
     * @return an iterator over the names of layouts.
     */
    public static Iterator layoutNamesIterator() {
        return getInstance().iterator();
    }

    /** 
     * Constructor.
     */
    public TreeLayoutFactory() {
        addDefaultCreators("treelayoutfactory");
    }

    /** 
     * Returns a Creator for a specified layout name.
     * @param name the name
     * @return a Creator for the specified layout name.
     */
    public Creator getCreator(String name) {
        return (Creator) creators.get(name);
    }

    /**
     * Returns a Creator for the specified layout name
     * and visualization.
     * @param name the name
     * @param vis the visualization
     * @return a Creator for the specified layout name
     * and visualization.
     */
    public NodeLinkTreeLayout create(
            String name,
            NodeLinkTreeVisualization vis) {
        Creator c = getCreator(name);
        if (c == null) {
            logger.debug("Creator not found for "+name);
            return null;
        }
        return c.create(vis);
    }


    /**
     * Returns an iterator over the names of layouts.
     * @return an iterator over the names of layouts.
     */
    public Iterator iterator() {
        return creators.keySet().iterator();
    }

    /**
     * {@inheritDoc}
     */
    public void add(String name, String className, String data) {
        try {
            Class dataClass = Class.forName(className);
            if (data == null) {
                add(new DefaultCreator(name, dataClass));
            } else {
                add(new DefaultCreator(name, dataClass, data));
            }
        } catch (Exception e) {
            logger.error("Class not found for "+className, e);
        }
    }

    /**
     * Registers a new creator with a specified name.
     * @param name the layout name
     * @param c the creator
     */
    public void add(String name, Creator c) {
        creators.put(name, c);
    }

    /**
     * Registers a new creator.
     * 
     * @param c the creator.
     */
    public void add(Creator c) {
        add(c.getName(), c);
    }

    /**
     * Register a new creator with a specified name
     * and a specified class.
     * @param name the name
     * @param c the class
     */
    public void add(String name, Class c) {
        add(name, new DefaultCreator(name, c));
    }

    /**
     * Removes a creator given its name.
     * @param name the name
     */
    public void remove(String name) {
        creators.remove(name);
    }

    /**
     * Creator for NodeLinkTreeLayout objects.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.3 $
     */
    public interface Creator {
        /**
         * Returns the layout name.
         * @return the layout name
         */
        String getName();

        /**
         * Creates the layout associated with a specified visualization.
         * @param vis the visualization
         * @return the layout associated with the visualization
         */
        NodeLinkTreeLayout create(NodeLinkTreeVisualization vis);
    }

    /**
     * Class abstract class implementing the Creator interface.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.3 $
     */
    public abstract static class AbstractCreator implements Creator {
        protected String name;

        /**
         * Constructor.
         * @param name the name
         */
        public AbstractCreator(String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return name;
        }
    }

    private static final Class[] PARAM0 = { };

    private static final Class[] PARAM1 = { String.class };

    /**
     * 
     * Default implementation of the Creator interface.
     * 
     * @author Jean-Daniel Fekete
     * @version $Revision: 1.3 $
     */
    public static class DefaultCreator extends AbstractCreator {
        Class clas;

        String param;

        /**
         * Constructor.
         * @param name the name
         * @param c the class
         */
        public DefaultCreator(String name, Class c) {
            super(name);
            assert (NodeLinkTreeLayout.class.isAssignableFrom(c));
            this.clas = c;
        }

        /**
         * Constructor.
         * @param name the name
         * @param c the class
         * @param param an additional parameter
         */
        public DefaultCreator(String name, Class c, String param) {
            super(name);
            assert (NodeLinkTreeLayout.class.isAssignableFrom(c));
            this.clas = c;
            this.param = param;
        }

        /**
         * {@inheritDoc}
         */
        public NodeLinkTreeLayout create(NodeLinkTreeVisualization vis) {
            try {
                Class[] paramTypes = (param == null) ? PARAM0 : PARAM1;
                Constructor cons = clas.getConstructor(paramTypes);
                if (cons == null) {
                    logger.error("Constructor not found for "+clas);
                    return null;
                }
                if (param == null) {
                    Object[] args = { };

                    return (NodeLinkTreeLayout) cons.newInstance(args);
                } else {
                    Object[] args = { param };

                    return (NodeLinkTreeLayout) cons.newInstance(args);
                }
            } catch (Exception e) {
                logger.error("Cannot instantiate NodeLinkTreeLayout"+clas, e);                
            }
            return null;
        }
    }
}
