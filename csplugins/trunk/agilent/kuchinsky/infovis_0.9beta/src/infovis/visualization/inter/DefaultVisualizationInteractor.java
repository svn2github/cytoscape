/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.visualization.inter;

import infovis.visualization.DefaultVisualization;
import infovis.visualization.ItemRenderer;

import java.util.ArrayList;

import javax.swing.JComponent;

/**
 * Class DefaultVisualizationInteractor
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.12 $
 * 
 * @infovis.factory InteractorFactory infovis.visualization.DefaultVisualization
 * @infovis.factory InteractorFactory infovis.table.visualization.TimeSeriesVisualization infovis.visualization.DefaultVisualization
 */
public class DefaultVisualizationInteractor 
    extends BasicVisualizationInteractor {
    protected ArrayList interactors;
    
    public DefaultVisualizationInteractor() {
    }
    
    public DefaultVisualizationInteractor(DefaultVisualization vis) {
        super(vis);
    }
    
    public DefaultVisualization getDefaultVisualization() {
        return (DefaultVisualization)getVisualization();
    }
    
    public void install(JComponent comp) {
        for (int i = 0; i < getInteractors().size(); i++) {
            getInteractor(i).setVisualization(getVisualization());
        }
        installInteractors(getVisualization().getItemRenderer(), comp);
//        comp.addMouseListener(this) ;
//        comp.addMouseMotionListener(this);
    }
    
    public void uninstall(JComponent comp) {
        uninstallInteractors(getVisualization().getItemRenderer(), comp);
//        comp.removeMouseListener(this);
//        comp.removeMouseMotionListener(this);
    }
    
    protected void installInteractors(ItemRenderer ir, JComponent comp) {
        BasicVisualizationInteractor inter = 
            RendererInteractorFactory.createInteractor(ir);
        if (inter != null) {
            addInteractor(inter);
        }
        for (int i = 0; i < ir.getRendererCount(); i++) {
            installInteractors(ir.getRenderer(i), comp);
        }
    }
    
    public void addInteractor(BasicVisualizationInteractor inter) {
        assert(indexOf(inter)==-1);
        getInteractors().add(inter);
        inter.setVisualization(getVisualization());
    }

    protected void uninstallInteractors(ItemRenderer ir, JComponent comp) {
        for (int i = getInteractors().size()-1; i >= 0; i--) {
            removeInteractor(i);
        }
    }
    
    public void removeInteractor(int i) {
        BasicVisualizationInteractor inter = getInteractor(i);
        inter.setVisualization(null);
        getInteractors().remove(i);
        assert(indexOf(inter)==-1);
    }
    
    public int indexOf(BasicVisualizationInteractor inter) {
        return getInteractors().indexOf(inter);
    }
    
    public int indexOf(Class cls) {
        for (int i = 0; i < getInteractors().size(); i++) {
            BasicVisualizationInteractor inter = getInteractor(i);
            if (cls.isInstance(inter)) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean removeInteractor(BasicVisualizationInteractor inter) {
        int index = indexOf(inter);
        if (index == -1) {
            return false;
        }
        removeInteractor(index);
        return true;
    }

    protected ArrayList getInteractors() {
        if (interactors == null) {
            interactors = new ArrayList();
        }
        return interactors;
    }

    public BasicVisualizationInteractor getInteractor(int index) {
        return (BasicVisualizationInteractor)getInteractors().get(index);
    }

}
