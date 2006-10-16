// $Id: LaunchExternalBrowser.java,v 1.8 2006/06/15 22:06:02 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.biopax_plugin.action;

import cytoscape.util.OpenBrowser;
import org.mskcc.biopax_plugin.util.cytoscape.CytoscapeWrapper;
import org.mskcc.biopax_plugin.view.BioPaxDetailsPanel;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.net.URL;

/**
 * Launches the User's External Web Browser.
 *
 * @author Ethan Cerami.
 */
public class LaunchExternalBrowser implements HyperlinkListener {
    private BioPaxDetailsPanel detailsPanel;

    /**
     * Constructor.
     *
     * @param detailsPanel BioPAX Details Panel.
     */
    public LaunchExternalBrowser(BioPaxDetailsPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
    }

    /**
     * User has clicked on a HyperLink.
     *
     * @param evt HyperLink Event Object.
     */
    public void hyperlinkUpdate(HyperlinkEvent evt) {
        URL url = evt.getURL();
        if (url != null) {
            if (evt.getEventType() == HyperlinkEvent.EventType.ENTERED) {
                CytoscapeWrapper.setStatusBarMsg(url.toString());
            } else if (evt.getEventType() == HyperlinkEvent.EventType.EXITED) {
                CytoscapeWrapper.clearStatusBar();
            } else if (evt.getEventType()
                    == HyperlinkEvent.EventType.ACTIVATED) {
                OpenBrowser.openURL(url.toString());
            }
        }
    }
}
