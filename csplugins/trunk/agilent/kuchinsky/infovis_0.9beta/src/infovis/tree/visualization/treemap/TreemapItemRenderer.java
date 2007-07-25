/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.visualization.render.DefaultFillingItemRenderer;

/**
 * Default root item renderer for treemaps.
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.5 $
 * 
 * @infovis.factory ItemRendererFactory infovis.tree.visualization.TreemapVisualization
 */
public class TreemapItemRenderer extends DefaultFillingItemRenderer {
    
    /**
     * Constructor.
     *
     */
    public TreemapItemRenderer() {
        replaceNamed(new TreemapVisualLabel(null), this);
    }
}