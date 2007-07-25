/*****************************************************************************
 * Copyright (C) 2004 Jean-Daniel Fekete and INRIA, France                   *
 * ------------------------------------------------------------------------- *
 * See the file LICENCE.TXT for license information.                         *
 *****************************************************************************/
package infovis.tree.visualization.treemap;

import infovis.visualization.render.DefaultFillingItemRenderer;

/**
 * Class TreemapItemRenderer
 * 
 * @author Jean-Daniel Fekete
 * @version $Revision: 1.3 $
 * 
 * @infovis.factory ItemRendererFactory infovis.tree.visualization.TreemapVisualization
 */
public class TreemapItemRenderer extends DefaultFillingItemRenderer { 
    public TreemapItemRenderer() {
        super();
        replaceNamed(new TreemapVisualLabel(null), this);
    }
}