/** Copyright (c) 2002 Institute for Systems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology and the Whitehead Institute 
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
 //-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.view;
//-------------------------------------------------------------------------
import y.view.Graph2DView;

import cytoscape.data.CyNetwork;
import cytoscape.visual.VisualMappingManager;
//-------------------------------------------------------------------------
/**
 * This interface defines methods for displaying a graph without
 * specifying the details of the UI components surrounding the
 * graph display.
 */
public interface NetworkView {
    
    /**
     * Returns the network displayed by this object.
     */
    CyNetwork getNetwork();
    /**
     * Returns the view object for the graph display.
     */
    Graph2DView getGraphView();
    /**
     * Returns the visual mapper associated with this display.
     */
    VisualMappingManager getVizMapManager();
    
    /**
     * Controls whether the user can interact with the display;
     * often turned off during major graph operations.
     *
     * @param newState  true to allow interaction, false to disable
     */
    void setInteractivity(boolean newState);
    /**
     * Redraws the graph with default parameters.
     */
    void redrawGraph();
    /**
     * Redraws the graph. The arguments control what actions are
     * performed before repainting the view.
     *
     * @param doLayout     if true, applied the current layouter to the graph
     * @param applyAppearances  if true, the vizmapper will recalculate
     *                          the node and edge appearances
     */
    void redrawGraph(boolean doLayout, boolean applyAppearances);
}

