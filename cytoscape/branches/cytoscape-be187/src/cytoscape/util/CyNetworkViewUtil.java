/*
  File: CyNetworkViewUtil.java

  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package cytoscape.util;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;

import cytoscape.ding.CyGraphLOD;
import cytoscape.ding.DingNetworkView;

import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.layout.CyLayouts;

import cytoscape.view.CyNetworkView;

import cytoscape.visual.VisualStyle;


public class CyNetworkViewUtil {
    /**
     * Creates a CyNetworkView that is placed placed in a given visual style
     * and rendered with a given layout algorithm.
     * The CyNetworkView will become the current view and have focus.
     * NOTE: This method may want to be added to Cytoscape.java in the future.
     *
     * @param network
     *            the network to create a view of
     * @param title
     *            the title to use for the view
     * @param layout
     *            the CyLayoutAlgorithm to use for layout. If null, will
     *            use the default layout (CyLayouts.getDefaultLayout()).
     * @param vs the VisualStyle in which to render this new network. If null,
     *           the default visual style will be used.
     */
    public static CyNetworkView createNetworkView(CyNetwork network,
                                                  String title,
                                                  CyLayoutAlgorithm layout,
                                                  VisualStyle vs) {
        if (network == Cytoscape.getNullNetwork()) {
            return Cytoscape.getNullNetworkView();
        }

        if (Cytoscape.viewExists(network.getIdentifier())) {
            return Cytoscape.getNetworkView(network.getIdentifier());
        }

        final DingNetworkView view = new DingNetworkView(network, title);

        if (vs != null) {
            view.setVisualStyle(vs.getName());
            Cytoscape.getVisualMappingManager().setNetworkView(view);
            Cytoscape.getVisualMappingManager().setVisualStyle(vs);
            // Cytoscape.getDesktop().setVisualStyle(vs);
        }

        view.setGraphLOD(new CyGraphLOD());

        view.setIdentifier(network.getIdentifier());
        Cytoscape.getNetworkViewMap().put(network.getIdentifier(),
                                          view);
        view.setTitle(network.getTitle());

        Cytoscape.setSelectionMode(Cytoscape.getSelectionMode(),
                                   view);

        if (layout == null) {
            layout = CyLayouts.getDefaultLayout();
        }

        // This will call a boatload of listeners and fire
        // secondary events, like network focus events. It will also initially render
        // the network (boo-hoo):
        Cytoscape.firePropertyChange(cytoscape.view.CytoscapeDesktop.NETWORK_VIEW_CREATED,
                                     null, view);

        layout.doLayout(view);

        view.fitContent();

        if (vs != null) {
            // even though we setup the visual style settings before the firePropertyChange() call,
            // some bogus default rendering is done. So, redraw it for real:
            view.redrawGraph(false, true);
        }

        return view;
    }
}
