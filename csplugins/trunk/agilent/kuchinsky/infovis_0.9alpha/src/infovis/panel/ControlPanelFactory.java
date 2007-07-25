/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.panel;

import infovis.Visualization;
import infovis.utils.BasicFactory;
import infovis.visualization.DefaultAxisVisualization;
import infovis.visualization.Orientable;
import infovis.visualization.inter.InteractorFactory;
import infovis.visualization.render.VisualLabel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

/**
 * ControlPanelFactory create a control panel associated with a specified
 * visualization.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.15 $
 */
public class ControlPanelFactory extends BasicFactory {
    static ControlPanelFactory instance;

    static Logger logger = Logger.getLogger(ControlPanelFactory.class);

    Map creators = new HashMap();

    /**
     * Constructor for ControlPanelFactory.
     */
    public ControlPanelFactory() {
        addDefaultCreators("controlpanelfactory");
    }

    public static ControlPanelFactory getInstance() {
        if (instance == null) {
            instance = new ControlPanelFactory();
        }
        return instance;
    }

    public static void setInstance(ControlPanelFactory shared) {
        instance = shared;
    }

    /**
     * Creates a Control Panel from a Visualization.
     * 
     * @param visualization
     *            The Visualization.
     * 
     * @return A Control Panel.
     */
    public ControlPanel create(Visualization visualization) {
        ControlPanel ret = null;

        Creator creator = findCreator(visualization);
        if (creator != null) {
            ret = creator.create(visualization);
        }
        return ret;
    }

    public Creator findCreator(Visualization visualization) {
        // Firs try the visualization itelf
        Class visClass = Visualization.class;
        for (Class c = visualization.getClass(); visClass
                .isAssignableFrom(c); c = c.getSuperclass()) {
            Creator creator = getCreator(c);
            if (creator != null) {
                return creator; // ok, we have it
            }
        }
        // Then, search for sub visualizations
        for (int i = 0; visualization.getVisualization(i) != null; i++) {
            Creator creator = findCreator(visualization.getVisualization(i));
            if (creator != null) {
                return creator;
            }
        }

        return null;
    }

    public static ControlPanel createControlPanel(
            Visualization visualization) {
        return getInstance().create(visualization);
    }

    public static JSplitPane createSplitVisualization(ControlPanel cp) {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new VisualizationPanel(cp.getVisualization()), cp);
        InteractorFactory.installInteractor(cp.getVisualization());
        split.setResizeWeight(1.0);
        return split;
    }

    public static JSplitPane createSplitVisualization(
            Visualization visualization) {
        return createSplitVisualization(createControlPanel(visualization));
    }

    public static JSplitPane createScrollVisualization(ControlPanel cp) {
        JScrollPane scroll = new JScrollPane(new VisualizationPanel(cp
                .getVisualization()));
        Visualization vis = cp.getVisualization();
        InteractorFactory.installInteractor(vis);
        if (vis.getRulerTable() != null) {
            DefaultAxisVisualization column = new DefaultAxisVisualization(
                    vis,
                    Orientable.ORIENTATION_SOUTH);
            InteractorFactory.installInteractor(column);
            VisualLabel vl = VisualLabel.get(column);
            vl.setOrientation(Orientable.ORIENTATION_SOUTH);
            scroll.setColumnHeaderView(new VisualizationPanel(column));
            DefaultAxisVisualization row = new DefaultAxisVisualization(
                    vis,
                    Orientable.ORIENTATION_WEST);
            InteractorFactory.installInteractor(row);
            row.setOrientation(Orientable.ORIENTATION_EAST);
            scroll.setRowHeaderView(new VisualizationPanel(row));
            
            scroll.setCorner(
                    JScrollPane.UPPER_LEFT_CORNER,
                    new JPanner(scroll));
        }
        scroll.setWheelScrollingEnabled(false);
        scroll.setDoubleBuffered(false);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                scroll, cp);
        split.setResizeWeight(1.0);
        return split;
    }

    public static JSplitPane createScrollVisualization(
            Visualization visualization) {
        return createScrollVisualization(createControlPanel(visualization));
    }

    public void add(String name, Creator creator) {
        creators.put(name, creator);
    }

    public void add(String className, String controlClassName,
            String data) {
        add(className, new DefaultCreator(controlClassName));
    }

    public static void addControlPanel(Class c, Creator creator) {
        getInstance().add(c.getName(), creator);
    }

    public static void addControlPanel(Class c, Class cpClass) {
        getInstance().add(c.getName(), cpClass.getName(), null);
    }

    public void setDefault(Class visClass, Class cpClass) {
        if (getCreator(visClass) == null)
            add(visClass.getName(), cpClass.getName(), null);
    }

    public Creator getCreator(Class c) {
        return (Creator) creators.get(c.getName());
    }

    public Creator getCreator(String name) {
        return (Creator) creators.get(name);
    }

    public interface Creator {
        ControlPanel create(Visualization visualization);
    }

    public static class DefaultCreator implements Creator {
        String controlPanelClassName;
        Class controlPanelClass;

        public DefaultCreator(
                String controlPanelClassName) {
            this.controlPanelClassName = controlPanelClassName;
        }

        Class getControlPanelClass() throws ClassNotFoundException {
            if (controlPanelClass == null) {
                controlPanelClass = Class
                        .forName(controlPanelClassName);
            }
            return controlPanelClass;
        }

        public ControlPanel create(Visualization visualization) {
            try {
                Class[] parameterTypes = { Visualization.class };
                Constructor cons;
                Class cpClass = getControlPanelClass();
                cons = cpClass.getConstructor(parameterTypes);
                Object[] args = { visualization };
                return (ControlPanel) cons.newInstance(args);
            } catch (NoSuchMethodException e) {
                logger.error(
                        "Cannot find control panel constructor for "
                                + controlPanelClassName, e);
                return null;
            } catch (ClassNotFoundException e) {
                logger.error("Cannot find class named "
                        + controlPanelClassName, e);
                return null;
            } catch (InvocationTargetException e) {
                logger.error("Cannot instantiate control panel for "
                        + controlPanelClassName, e);
            } catch (Exception e) {
                logger.error("Cannot instantiate control panel for "
                        + controlPanelClassName, e);
            }
            return null;
        }

    }
}