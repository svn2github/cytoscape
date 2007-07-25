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
 * Class TreeLayoutFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class TreeLayoutFactory extends BasicFactory {
    private static TreeLayoutFactory instance;
    private static Logger logger = Logger.getLogger(TreeLayoutFactory.class);

    protected TreeMap creators = new TreeMap();

    public static TreeLayoutFactory getInstance() {
        if (instance == null) {
            instance = new TreeLayoutFactory();
        }
        return instance;
    }

    public static void setInstance(TreeLayoutFactory inst) {
        instance = inst;
    }

    public static NodeLinkTreeLayout createLayout(String name,
            NodeLinkTreeVisualization visualization) {
        return getInstance().create(name, visualization);
    }

    public static void addLayout(String name, Class c) {
        getInstance().add(name, c);
    }

    public static Iterator layoutNamesIterator() {
        return getInstance().iterator();
    }

    public TreeLayoutFactory() {
        addDefaultCreators("treelayoutfactory");
    }

    public Creator getCreator(String name) {
        return (Creator) creators.get(name);
    }

    public NodeLinkTreeLayout create(String name,
            NodeLinkTreeVisualization vis) {
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

        public NodeLinkTreeLayout create(NodeLinkTreeVisualization vis);
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

    static final Class[] param0 = { };

    static final Class[] param1 = { String.class };

    public static class DefaultCreator extends AbstractCreator {
        Class clas;

        String param;

        public DefaultCreator(String name, Class c) {
            super(name);
            assert (NodeLinkTreeLayout.class.isAssignableFrom(c));
            this.clas = c;
        }

        public DefaultCreator(String name, Class c, String param) {
            super(name);
            assert (NodeLinkTreeLayout.class.isAssignableFrom(c));
            this.clas = c;
            this.param = param;
        }

        public NodeLinkTreeLayout create(NodeLinkTreeVisualization vis) {
            try {
                Class[] paramTypes = (param == null) ? param0 : param1;
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
