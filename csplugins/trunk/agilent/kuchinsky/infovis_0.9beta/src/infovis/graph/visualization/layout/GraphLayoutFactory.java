/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization.layout;

import infovis.graph.visualization.NodeLinkGraphLayout;
import infovis.graph.visualization.NodeLinkGraphVisualization;
import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.log4j.Logger;

/**
 * Class GraphLayoutFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.7 $
 */
public class GraphLayoutFactory extends BasicFactory {
    private static GraphLayoutFactory instance;
    private static Logger logger = Logger.getLogger(GraphLayoutFactory.class);

    protected TreeMap creators = new TreeMap();

    public static GraphLayoutFactory getInstance() {
        if (instance == null) {
            instance = new GraphLayoutFactory();
        }
        return instance;
    }

    public static void setInstance(GraphLayoutFactory inst) {
        instance = inst;
    }

    public static NodeLinkGraphLayout createLayout(String name,
            NodeLinkGraphVisualization visualization) {
        return getInstance().create(name, visualization);
    }

    public static void addLayout(String name, Class c) {
        getInstance().add(name, c);
    }

    public static Iterator layoutNamesIterator() {
        return getInstance().iterator();
    }

    public GraphLayoutFactory() {
        addDefaultCreators("graphlayoutfactory");
    }

    public Creator getCreator(String name) {
        return (Creator) creators.get(name);
    }

    public NodeLinkGraphLayout create(String name,
            NodeLinkGraphVisualization vis) {
        Creator c = getCreator(name);
        if (c == null) {
            logger.debug("Creator not found for "+name);
            return null;
        }
        return c.create(vis);
    }

    public Iterator iterator() {
        return creators.keySet().iterator();
    }

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

    public void add(String name, Creator c) {
        creators.put(name, c);
    }

    public void add(Creator c) {
        add(c.getName(), c);
    }

    public void add(String name, Class c) {
        add(name, new DefaultCreator(name, c));
    }

    public void remove(String name) {
        creators.remove(name);
    }

    public interface Creator {
        public abstract String getName();

        public NodeLinkGraphLayout create(NodeLinkGraphVisualization vis);
    }

    public abstract static class AbstractCreator implements Creator {
        protected String name;

        public AbstractCreator(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Class[] PARAM0 = { };

    private static final Class[] PARAM1 = { String.class };

    public static class DefaultCreator extends AbstractCreator {
        Class clas;

        String param;

        public DefaultCreator(String name, Class c) {
            super(name);
            assert (NodeLinkGraphLayout.class.isAssignableFrom(c));
            this.clas = c;
        }

        public DefaultCreator(String name, Class c, String param) {
            super(name);
            assert (NodeLinkGraphLayout.class.isAssignableFrom(c));
            this.clas = c;
            this.param = param;
        }

        public NodeLinkGraphLayout create(NodeLinkGraphVisualization vis) {
            try {
                Class[] paramTypes = (param == null) ? PARAM0 : PARAM1;
                Constructor cons = clas.getConstructor(paramTypes);
                if (cons == null) {
                    logger.error("Constructor not found for "+clas);
                    return null;
                }
                if (param == null) {
                    Object[] args = { };

                    return (NodeLinkGraphLayout) cons.newInstance(args);
                } else {
                    Object[] args = { param };

                    return (NodeLinkGraphLayout) cons.newInstance(args);
                }
            } catch (Exception e) {
                logger.error("Cannot instantiate NodeLinkGraphLayout"+clas, e);                
            }
            return null;
        }
    }
}