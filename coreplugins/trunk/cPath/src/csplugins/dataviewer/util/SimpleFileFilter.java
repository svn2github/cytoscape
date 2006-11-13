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
package csplugins.dataviewer.util;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * Simple File Filter
 *
 * @author Ethan Cerami
 */
public class SimpleFileFilter extends FileFilter {
    private String extensions[];
    private String description;

    /**
     * Constructor.
     *
     * @param exts List of File extentions, e.g. *.xml, *.gif.
     * @param desc Filter Description.
     */
    public SimpleFileFilter(String[] exts, String desc) {
        this.extensions = exts;
        this.description = desc;
    }

    /**
     * Determines which files are acceptable.
     *
     * @param f File to test.
     * @return true or false.
     */
    public boolean accept(File f) {
        //  We always allow directories, regardless of their extensions
        if (f.isDirectory()) {
            return true;
        }
        String name = f.getName().toLowerCase();
        for (int i = 0; i < extensions.length; i++) {
            if (name.endsWith(extensions[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets Filter Description (used in pull-down menu).
     *
     * @return Description String.
     */
    public String getDescription() {
        return this.description;
    }
}