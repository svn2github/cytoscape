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

import infovis.Visualization;
import infovis.utils.BasicFactory;
import infovis.visualization.VisualizationInteractor;

/**
 * Factory for VisualizationInteractor objects.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.9 $
 */
public class InteractorFactory extends BasicFactory {
    private static InteractorFactory instance;
    private static Logger logger = Logger.getLogger(InteractorFactory.class);
    protected HashMap creators = new HashMap();
    
    public static InteractorFactory getInstance() {
        if (instance == null) {
            instance = new InteractorFactory();
        }
        return instance;
    }
    
    public static void setInstance(InteractorFactory i) {
        instance = i;
    }
    
    public static Creator getVisualizationInteractorCreator(Visualization vis) {
        return getInstance().getCreator(vis);
    }
    
    public static VisualizationInteractor 
        createVisualizationInteractor(Visualization vis) {
        Creator c = getVisualizationInteractorCreator(vis);
        if (c != null) {
            return c.create(vis);
        }
        return null;
    }
    
    public static void installInteractor(Visualization vis) {
        if (vis.getInteractor() == null) {
            vis.setInteractor(createVisualizationInteractor(vis));
        }
        int i = 0;
        for (Visualization sub = vis.getVisualization(i++);
            sub != null;
            sub = vis.getVisualization(i++)) {
            installInteractor(sub);
        }
    }
    
    public InteractorFactory() {
        addDefaultCreators("interactorfactory");
    }
    
    public Creator getCreator(Visualization vis) {
        Class visClass = Visualization.class;
        for (Class c = vis.getClass(); 
            visClass.isAssignableFrom(c);
            c = c.getSuperclass()) {
            Creator creator = (Creator)creators.get(c.getName());
            if (creator != null) {
                return creator;
            }
        }
        //logger.info("No interactor creator for "+vis.getClass());
        return null;
    }
    
    public boolean removeCreator(Creator c) {
        return creators.remove(c) != null;
    }
    
    protected void add(String name, String className, String data) {
        if (data == null) {
            creators.put(name, new DefaultCreator(name, className));
        }
        else {
            creators.put(name, new DefaultCreator(data, className));
        }
    }
    
    public interface Creator {
        public abstract String getVisualizationClassName();
        public abstract VisualizationInteractor create(Visualization vis);
    }
    
    public static class DefaultCreator implements Creator {
        protected String visClassName;
        protected Class visClass;
        protected String interClassName;
        protected Class interClass;
        
        public DefaultCreator(String visClassName, String interClassName) {
            this.interClassName = interClassName;
            this.visClassName = visClassName;
        }
        
        public String getVisualizationClassName() {
            return visClassName;
        }
        
        public VisualizationInteractor create(Visualization vis) {
            if (interClass == null) {
                try {
                    interClass = Class.forName(interClassName);
                } catch (ClassNotFoundException e) {
                    logger.error("Cannot load interactor class "+interClassName, e);
                    return null;
                }
            }
            if (visClass == null) {
                try {
                    visClass = Class.forName(visClassName);
                }
                catch (ClassNotFoundException e) {
                    logger.error("Cannot load visualization class "+visClassName, e);
                }
            }
            Class[] parameterTypes = { visClass };

            Constructor cons;
            try {
                cons = interClass.getConstructor(parameterTypes);
            } catch (NoSuchMethodException e) {
                logger.error("Cannot find constructor for "+interClassName, e);
                return null;
            }
            if (cons != null) {
                Object[] args = { vis };
                try {
                    return (VisualizationInteractor) cons.newInstance(args);
                } catch (Exception e) {
                    logger.error(
                            "Cannot instantiate new interactor "+interClassName, 
                            e);
                }
            }
            return null;
        }
    }
}
