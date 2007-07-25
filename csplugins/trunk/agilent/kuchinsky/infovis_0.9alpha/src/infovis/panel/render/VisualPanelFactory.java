/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel.render;

import infovis.utils.BasicFactory;
import infovis.visualization.render.AbstractVisualColumn;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * Class VisualPanelFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 */
public class VisualPanelFactory extends BasicFactory {
    protected HashMap creators = new HashMap();
    private static final Logger logger = Logger.getLogger(VisualPanelFactory.class);
    
    private static VisualPanelFactory instance;
    
    public static VisualPanelFactory getInstance() {
        if (instance == null) {
            instance = new VisualPanelFactory();
        }
        return instance;
    }
    
    public static JComponent createVisualPanel(AbstractVisualColumn vc) {
        Creator creator = getInstance().getCreator(vc);
        if (creator != null) {
            return creator.create(vc);
        }
        return null;
    }
    
    public VisualPanelFactory() {
        addDefaultCreators("visualpanelfactory");
    }
    
    protected void add(String name, String className, String data) {
        creators.put(name, new DefaultCreator(className));
    }
    
    public Creator getCreator(AbstractVisualColumn vc) {
        for (Class cls = vc.getClass(); cls != Object.class; cls = cls.getSuperclass()) {
            Creator c = (Creator)creators.get(cls.getName());
            if (c != null) {
                return c;
            }
        }
        return null;
    }

    public interface Creator {
        public JComponent create(AbstractVisualColumn vc);
    }
    
    public static class DefaultCreator implements Creator {
        String visualPanelClassName;
        Class visualPanelClass;
        
        public DefaultCreator(String visualPanelClassName) {
            this.visualPanelClassName = visualPanelClassName;
        }
        
        public JComponent create(AbstractVisualColumn vc) {
            try {
                if (visualPanelClass == null) {
                    visualPanelClass = Class.forName(visualPanelClassName);
                }
                Class[] params = { AbstractVisualColumn.class };
                Constructor cons = visualPanelClass.getConstructor(params);
                Object[] args = { vc };
                return (JComponent) cons.newInstance(args);
            }
            catch (Exception e) {
                logger.error(
                        "Cannot instantiate VisualPanelFactor for "
                        +visualPanelClassName, 
                        e);
            }
            return null;
        }
    }
}
