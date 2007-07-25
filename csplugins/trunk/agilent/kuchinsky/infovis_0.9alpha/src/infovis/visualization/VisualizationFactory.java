/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization;

import infovis.Visualization;
import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * A VisualizationFactory is used to keep track of all the visualizations
 * compatible with a specified data structure and to create the visualizations
 * from the data structures.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.13 $
 */
public class VisualizationFactory extends BasicFactory {
    private static VisualizationFactory instance;
    private static Logger logger = Logger.getLogger(VisualizationFactory.class);

    Map creators = new TreeMap();

    public VisualizationFactory() {
        addDefaultCreators("visualizationfactory");
    }
    
    protected void add(String name, String visualization, String data) {
        if (visualization == null || data == null) {
            return;
        }
        try {
            Class dataClass = Class.forName(data);
            setDefault(name, dataClass, visualization);
        } catch (ClassNotFoundException e) {
        }
    }

    public static VisualizationFactory getInstance() {
        if (instance == null) {
            instance = new VisualizationFactory();
        }
        return instance;
    }

    public static void setInstance(VisualizationFactory shared) {
        instance = shared;
    }

    public void add(Creator creator) {
        creators.put(creator.getName(), creator);
    }

    public static void addVisualizationFactory(Creator c) {
        getInstance().add(c);
    }

    public void setDefault(String name, Class tableClass,
            String visualizationClass) {
        add(new DefaultCreator(name, tableClass, visualizationClass));
    }

    public Creator[] getCompatibleCreators(Object table) {
        ArrayList list = new ArrayList();
        for (Iterator iter = iterator(table); iter.hasNext();) {
            Creator c = (Creator) iter.next();
            list.add(c);
        }
        Creator[] creators = new Creator[list.size()];
        list.toArray(creators);
        return creators;
    }

    /**
     * Returns an iterator over the visualization creators compatible with the
     * specified table.
     * 
     * @param table
     *            the table
     * @return an iterator over the visualization creators compatible with the
     *         specified table.
     */
    public Iterator iterator(Object table) {
        return new VisualizationIterator(table);
    }

    class VisualizationIterator implements Iterator {
        Iterator entriesIterator;

        Object table;

        Creator current;

        public VisualizationIterator(Object table) {
            this.table = table;
            entriesIterator = creators.entrySet().iterator();
            skipIncompatible();
        }

        void skipIncompatible() {
            while (entriesIterator.hasNext()) {
                Map.Entry entry = (Map.Entry) entriesIterator.next();
                current = (Creator) entry.getValue();
                if (current.isCompatible(table))
                    return;
            }
            current = null;
        }

        public boolean hasNext() {
            return current != null;
        }

        public Object next() {
            Creator ret = current;
            current = null;
            skipIncompatible();
            return ret;
        }

        public void remove() {
        }

    }

    public interface Creator {
        Visualization create(Object table);

        String getName();

        boolean isCompatible(Object table);
    }

    public static class DefaultCreator implements Creator {
        String name;
        
        Class tableClass;

        String visualizationClass;

        public DefaultCreator(String name, Class tableClass,
                String visualizationClass) {
            this.name = name;
            this.tableClass = tableClass;
            this.visualizationClass = visualizationClass;
        }

        public String getName() {
            return name;
        }

        public boolean isCompatible(Object table) {
            return tableClass.isAssignableFrom(table.getClass());
        }

        public Visualization create(Object table) {
            Class c;
            try {
                c = Class.forName(visualizationClass);
                Class[] parameterTypes = { tableClass };
    
                Constructor cons = c.getConstructor(parameterTypes);
                Object[] args = { table };
                return (Visualization) cons.newInstance(args);
            }
            catch (Exception e) {
                logger.error(
                        "Cannot instantiate Visualization "+visualizationClass, 
                        e);
            }
            return null;
        }
    }
}