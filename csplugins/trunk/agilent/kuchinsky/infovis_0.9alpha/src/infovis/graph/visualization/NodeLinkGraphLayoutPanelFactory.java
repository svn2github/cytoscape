/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.graph.visualization;

import infovis.utils.BasicFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * Class NodeLinkGraphLayoutPanelFactory
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.4 $
 */
public class NodeLinkGraphLayoutPanelFactory extends BasicFactory {
    private static NodeLinkGraphLayoutPanelFactory instance;
    private static Logger logger = Logger.getLogger(NodeLinkGraphLayoutPanelFactory.class);

    protected ArrayList creators = new ArrayList();

    public static NodeLinkGraphLayoutPanelFactory getInstance() {
        if (instance == null) {
            instance = new NodeLinkGraphLayoutPanelFactory();
        }
        return instance;
    }

    public NodeLinkGraphLayoutPanelFactory() {
        addDefaultCreators("nodelinkgraphlayoutpanelfactory");
    }

    protected void add(String name, String className, String data) {
        try {
            Class dataClass = Class.forName(className);
            Constructor cons = dataClass.getConstructor(null);
            add((Creator) cons.newInstance(null));
        } catch (Exception e) {
            logger.error("Cannot instantiate class "+className, e);
        }
    }

    public void add(Creator c) {
        creators.add(c);
    }

    public interface Creator {
        public String getName();

        public JComponent create(NodeLinkGraphVisualization vis);
    }

}