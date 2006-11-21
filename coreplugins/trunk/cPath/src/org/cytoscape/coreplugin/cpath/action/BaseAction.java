/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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
package org.cytoscape.coreplugin.cpath.action;

import org.cytoscape.coreplugin.cpath.ui.ErrorDisplay;
import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CytoscapeDesktop;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Base Action Class.
 *
 * @author Ethan Cerami
 */
public abstract class BaseAction extends AbstractAction {
    private CytoscapeDesktop desktop;
    private static final String HTTP_PREFIX = "http://";

    /**
     * Action Performed Event.
     *
     * @param event Action Event.
     */
    public abstract void actionPerformed(ActionEvent event);

    protected BaseAction() {
        this.desktop = Cytoscape.getDesktop();
    }

    /**
     * Determines if URL is Valid.
     *
     * @param urlString URL String.
     * @return URL Object.
     */
    protected URL createURL(String urlString) {
        URL url = null;

        if (urlString.trim().length() == 0) {
            //  Bug Fix:  #543
            this.showError("You did not enter a valid URL.  Please try again.");
            return null;
        } else if (!urlString.matches(".*://.*")) {
            //  Bug Fix:  #056.
            urlString = new String(HTTP_PREFIX + urlString);
        }
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            this.showError("Sorry, the URL you entered is invalid. Please "
                    + "check your spelling and try again.");
        }
        return url;
    }

    /**
     * Gets File Section from user.
     */
    protected String getFile(String[] extensions, String description) {
         CyFileFilter filter = new CyFileFilter();

        // Add accepted File Extensions
        for (int i=0; i<extensions.length; i++) {
            filter.addExtension(extensions[i]);
        }

        // Get the file name
        File file = FileUtil.getFile(description,
                FileUtil.LOAD, new CyFileFilter[]{filter});
        if (file != null) {
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Gets URL Selection from User.
     */
    protected URL getURL(String msg) {
        String urlString = JOptionPane.showInputDialog(desktop, msg);
        if (urlString != null) {
            URL url = this.createURL(urlString);
            if (url != null) {
                if (url.getProtocol().equalsIgnoreCase("http")
                        || url.getProtocol().equalsIgnoreCase("ftp")
                        || url.getProtocol().equalsIgnoreCase("file")) {
                    return url;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Shows Error Dialog Box.
     *
     * @param e Exception.
     */
    protected final void showError(Exception e) {
        ErrorDisplay errorDisplay = new ErrorDisplay(desktop);
        errorDisplay.displayError(e);
    }

    /**
     * Shows Error Dialog Box.
     *
     * @param msg Error Message.
     */
    protected final void showError(String msg) {
        ErrorDisplay errorDisplay = new ErrorDisplay(desktop);
        errorDisplay.displayError(msg);
    }
}