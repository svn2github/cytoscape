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
import infovis.visualization.LinkVisualization;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.HashMap;
import java.util.Map;

/**
 * A LinkShaperFactory keeps track of all the LinkShaper classes compatible with a
 * specified LinkVisualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 */
public class LinkShaperFactory extends BasicFactory {
    static LinkShaperFactory instance;
    Map creators = new HashMap();

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

    protected void createDefaults() {
        setDefault(
            "Default",
            LinkVisualization.class,
            "infovis.visualization.linkShapers.DefaultLinkShaper");
        setDefault(
            "Curved",
            LinkVisualization.class,
            "infovis.visualization.linkShapers.CurvedLinkShaper");            
    }

    protected void add(String name, String className, String data) {
        add(new Creator(name, LinkVisualization.class, className));
    }
    public static void addLinkShaperFactory(Creator c) {
    	getInstance().add(c);    
    }
    
    
    public void add(Creator creator) {
        creators.put(creator.getName(), creator);
    }
    
    public void setDefault(String name, Class linkVisualizationClass, String linkShaperClass) {
        add(new Creator(name, linkVisualizationClass, linkShaperClass));
    }
    
    public Creator[] getCompatibleCreators(LinkVisualization link) {
        ArrayList list = new ArrayList();
        for (Iterator iter = iterator(link); iter.hasNext(); ) {
            Creator c = (Creator)iter.next();
            list.add(c);
        }
        Creator[] creators = new Creator[list.size()];
        list.toArray(creators);
        return creators;
    }
    

    
    /**
     * Returns an iterator over the LinkShaper creators 
     * compatible with the specified LinkVisualization.
     * 
     * @param link the LinkVisualization
     * @return an iterator over the visualization creators
     * compatible with the specified table.
     */    
    public Iterator iterator(LinkVisualization link) {
        return new CreatorIterator(link); 
    }
    
    class CreatorIterator implements Iterator {
        Iterator entriesIterator;
        LinkVisualization link;
        Creator current;
        
        public CreatorIterator(LinkVisualization link) {
            this.link = link;
            entriesIterator = creators.entrySet().iterator();
            skipIncompatible();
        }
        
        void skipIncompatible() {
            while (entriesIterator.hasNext()) {
                Map.Entry entry = (Map.Entry)entriesIterator.next();
                current = (Creator)entry.getValue();
                if (current.isCompatible(link))
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

    public static class Creator {
        String name;
        Class linkVisualizationClass;
        String linkShaperClass;

        public Creator(
            String name,
            Class linkVisualizationClass,
            String linkShaperClass) {
            assert(
                linkVisualizationClass.isAssignableFrom(
                    linkVisualizationClass));
            this.name = name;
            this.linkVisualizationClass = linkVisualizationClass;
            this.linkShaperClass = linkShaperClass;
        }

        public boolean isCompatible(LinkVisualization link) {
            return linkVisualizationClass.isAssignableFrom(
                link.getClass());
        }

        public LinkShaper create(LinkVisualization link) {
            Class c;
            try {
                c = Class.forName(linkShaperClass);
            }
            catch (ClassNotFoundException e) {
                return null;
            }
            Class[] parameterTypes = { linkVisualizationClass };
            Constructor cons;
            try {
                cons = c.getConstructor(parameterTypes);
            }
            catch (NoSuchMethodException e) {
                return null;
            }
            if (cons != null) {
                Object[] args = { link };
                try {
                    return (LinkShaper) cons.newInstance(args);
                }
                catch (InstantiationException e) {
                }
                catch (IllegalAccessException e) {
                }
                catch (InvocationTargetException e) {
                }
            }
            return null;
        }

        public String getLinkShaperClass() {
            return linkShaperClass;
        }

        public Class getLinkVisualizationClass() {
            return linkVisualizationClass;
        }

        public String getName() {
            return name;
        }

    }
}
