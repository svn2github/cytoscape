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
package csplugins.dataviewer.mage;

import java.io.File;
import java.util.List;

/**
 * Encapsulates Minimal MAGE_ML Data, as required by PlugIn.
 *
 * @author Ethan Cerami.
 */
public class MageData {
    private List experimentDescriptionList;
    private List organizationContactList;
    private List fileList;
    private File file;

    /**
     * Gets a List of Experiment Description Strings.
     *
     * @return List of Strings.
     */
    public List getExperimentDescriptionList() {
        return experimentDescriptionList;
    }

    /**
     * Sets a List of Experiment Description Strings.
     *
     * @param experimentDescriptionList List of Strings.
     */
    public void setExperimentDescriptionList(List experimentDescriptionList) {
        this.experimentDescriptionList = experimentDescriptionList;
    }

    /**
     * Gets a List of Organizational Contacts.
     *
     * @return List of Strings.
     */
    public List getOrganizationContactList() {
        return organizationContactList;
    }

    /**
     * Sets a List of Organizational Contacts.
     *
     * @param organizationContactList List of Strings.
     */
    public void setOrganizationContactList(List organizationContactList) {
        this.organizationContactList = organizationContactList;
    }

    /**
     * Gets a List of all External Files.
     *
     * @return List of Strings.
     */
    public List getFileList() {
        return fileList;
    }

    /**
     * Sets a List of all External Files.
     *
     * @param fileList List of Strings.
     */
    public void setFileList(List fileList) {
        this.fileList = fileList;
    }

    /**
     * Gets the MAGE-ML File.
     *
     * @return MAGE-ML File.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the MAGE-ML File.
     *
     * @param file MAGE-ML File.
     */
    public void setFile(File file) {
        this.file = file;
    }
}
