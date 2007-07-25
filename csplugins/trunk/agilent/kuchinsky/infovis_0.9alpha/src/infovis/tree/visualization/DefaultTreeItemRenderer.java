/*****************************************************************************
 * Copyright (C) 2003-2005 Jean-Daniel Fekete and INRIA, France              *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the X11 Software License    *
 * a copy of which has been included with this distribution in the           *
 * license-infovis.txt file.                                                 *
 *****************************************************************************/
package infovis.tree.visualization;

import infovis.visualization.render.DefaultFillingItemRenderer;

/**
 * 
 * Class DefaultTreeItemRenderer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.1 $
 * @infovis.factory ItemRendererFactory infovis.tree.visualization.TreeVisualization
 */
public class DefaultTreeItemRenderer extends DefaultFillingItemRenderer {

    public DefaultTreeItemRenderer() {
        replaceNamed(new TreeVisualSelection(null), this);
    }

}
