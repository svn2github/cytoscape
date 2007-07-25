/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.linkShapers;

import infovis.utils.BasicFactory;
import infovis.visualization.LinkShaper;

import java.lang.reflect.Constructor;
import java.util.*;

import org.apache.log4j.Logger;

/**
 * A LinkShaperFactory keeps track of all the LinkShaper classes compatible with
 * a specified LinkVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.6 $
 * 
 */
public class LinkShaperFactory extends BasicFactory {
    static LinkShaperFactory    instance;
    Map                         creators = new TreeMap();
    static private final Logger logger   = Logger.getLogger(LinkShaperFactory.class);

    public static LinkShaperFactory getInstance() {
        if (instance == null) {
            instance = new LinkShaperFactory();
        }
        return instance;
    }

    public static void setInstance(LinkShaperFactory shared) {
        instance = shared;
    }

    public LinkShaperFactory() {
        addDefaultCreators("linkshaperfactory");
    }

    protected void add(String name, String className, String data) {
        add(new Creator(name, className, data));
    }

    public static void addLinkShaperFactory(Creator c) {
        getInstance().add(c);
    }

    public void add(Creator creator) {
        creators.put(creator.getName(), creator);
    }
    
    public Iterator iterator() {
        return creators.values().iterator();
    }
//
//    public Creator[] getCompatibleCreators(LinkVisualization link) {
//        ArrayList list = new ArrayList();
//        for (Iterator iter = iterator(link); iter.hasNext();) {
//            Creator c = (Creator) iter.next();
//            list.add(c);
//        }
//        Creator[] creators = new Creator[list.size()];
//        list.toArray(creators);
//        return creators;
//    }
//
//    /**
//     * Returns an iterator over the LinkShaper creators compatible with the
//     * specified LinkVisualization.
//     * 
//     * @param link
//     *            the LinkVisualization
//     * @return an iterator over the visualization creators compatible with the
//     *         specified table.
//     */
//    public Iterator iterator(LinkVisualization link) {
//        return new CreatorIterator(link);
//    }
//
//    class CreatorIterator implements Iterator {
//        Iterator          entriesIterator;
//        LinkVisualization link;
//        Creator           current;
//
//        public CreatorIterator(LinkVisualization link) {
//            this.link = link;
//            entriesIterator = creators.entrySet().iterator();
//            skipIncompatible();
//        }
//
//        void skipIncompatible() {
//            while (entriesIterator.hasNext()) {
//                Map.Entry entry = (Map.Entry) entriesIterator.next();
//                current = (Creator) entry.getValue();
//                if (current.isCompatible(link))
//                    return;
//            }
//            current = null;
//        }
//
//        public boolean hasNext() {
//            return current != null;
//        }
//
//        public Object next() {
//            Creator ret = current;
//            current = null;
//            skipIncompatible();
//            return ret;
//        }
//
//        public void remove() {
//        }
//
//    }

    public static class Creator {
        protected String name;
        protected String linkShaperClassName;
        protected String data;
        protected transient Class linkShaperClass;

        public Creator(
                String name,
                String linkShaperClassName,
                String data) {
            this.name = name;
            this.linkShaperClassName = linkShaperClassName;
            this.data = data;
        }

        public LinkShaper create() {
            if (linkShaperClass == null) {
                try {
                    linkShaperClass = Class.forName(linkShaperClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Cannot find LinkShaper class "+linkShaperClassName, e);
                    return null;
                }
            }
            Constructor cons;
            Class[] paramTypes = null;
            if (data != null) {
                paramTypes = new Class[1];
                paramTypes[0] = String.class;
            }
            try {
                cons = linkShaperClass.getConstructor(paramTypes);
            } catch (NoSuchMethodException e) {
                logger.error("Cannot find empty constructor for LinkShaper class "
                       +linkShaperClassName, e);
                return null;
            }
            if (cons != null) {
                try {
                    Object[] args = null;
                    if (data != null) {
                        args = new Object[1];
                        args[0] = data;
                    }
                    return (LinkShaper) cons.newInstance(args);
                } catch (Exception e) {
                    logger.error("Cannot instantiate LinkShaper class "
                            +linkShaperClassName, e);
                }
            }
            return null;
        }

        public String getLinkShaperClassName() {
            return linkShaperClassName;
        }

        public String getName() {
            return name;
        }

    }
}
