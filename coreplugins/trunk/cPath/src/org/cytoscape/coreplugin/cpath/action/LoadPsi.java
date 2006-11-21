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

import org.cytoscape.coreplugin.cpath.task.ExportPsiTask;
import org.cytoscape.coreplugin.cpath.task.LoadPsiTask;
import csplugins.task.Task;
import csplugins.task.ui.TaskMonitorUI;
import cytoscape.Cytoscape;
import cytoscape.util.CyFileFilter;
import cytoscape.util.FileUtil;
import cytoscape.view.CytoscapeDesktop;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

/**
 * Loads PSI Files from Local File System or from the Web.
 *
 * @author Ethan Cerami
 */
public class LoadPsi extends BaseAction {

    /**
     * Action Command:  Load PSI from Web.
     */
    public static final String ACTION_IMPORT_PSI_WEB = "load_psi_web";

    /**
     * Action Command:  Load PSI from File.
     */
    public static final String ACTION_IMPORT_PSI_FILE = "load_psi_file";

    /**
     * Action Command:  Export to PSI-MI Format.
     */
    public static final String ACTION_EXPORT_PSI = "export_psi";

    /**
     * Cytoscape Desktop Object.
     */
    private CytoscapeDesktop desktop;

    /**
     * Constructor.
     */
    public LoadPsi() {
        this.desktop = Cytoscape.getDesktop();
    }

    /**
     * Receives Menu Selection Event.
     *
     * @param event Action Event
     */
    public void actionPerformed(ActionEvent event) {
        try {
            String command = event.getActionCommand();
            if (command.equals(LoadPsi.ACTION_IMPORT_PSI_FILE)) {
                importPsiFile();
            } else if (command.equals
                    (LoadPsi.ACTION_IMPORT_PSI_WEB)) {
                importPsiWeb();
            } else if (command.equals(LoadPsi.ACTION_EXPORT_PSI)) {
                exportPsiFile();
            }
        } catch (Exception e) {
            this.showError(e);
        }
    }

    /**
     * Imports a PSI Document from the Web.
     */
    public void importPsiWeb() {
        URL url = this.getURL("Enter URL for PSI-MI Document:  ");
        if (url != null) {
            LoadPsiTask task;
            if (url.getProtocol().equalsIgnoreCase("file")) {
                File file = new File(url.getFile());
                task = new LoadPsiTask(file);
            } else {
                task = new LoadPsiTask(url);
            }
            Component parentFrame = (Component) Cytoscape.getDesktop();
            task.setParentFrame(parentFrame);
            startTask(task);
        }
    }

    /**
     * Imports a PSI File.
     */
    public void importPsiFile() {
        String xml[] = new String[]{"xml"};
        String fileString = this.getFile(xml, "XML Files (*.xml)");
        if (fileString != null) {
            File file = new File(fileString);
            LoadPsiTask task = new LoadPsiTask(file);
            Component parentFrame = (Component) Cytoscape.getDesktop();
            task.setParentFrame(parentFrame);
            startTask(task);
        }
    }

    /**
     * Exports a PSI File.
     *
     * @throws ValidationException XML Validation Error.
     * @throws IOException         IO Error.
     * @throws MarshalException    XML Marshaling Error.
     */
    public void exportPsiFile() throws ValidationException,
            IOException, MarshalException {
        CyFileFilter filter = new CyFileFilter();
        filter.addExtension("xml");

        // Get the file name
        File file = FileUtil.getFile("Save to PSI-MI Format",
                FileUtil.SAVE, new CyFileFilter[]{filter});

        if (file != null) {
            file = this.conditionallyAddFileExtension(file);
            if (file.exists()) {
                String msg = "File:  " + file + " exists.  "
                        + "Do you want to overwrite it?";
                int confirmValue = JOptionPane.showConfirmDialog(desktop, msg,
                        "Overwrite Existing File?", JOptionPane.YES_NO_OPTION);
                if (confirmValue == JOptionPane.YES_OPTION) {
                    exportPsi(file);
                }
            } else {
                exportPsi(file);
            }
        }
    }

    /**
     * Bug Fix:  #0000055.  If the specified file does not have an
     * extension, add .xml extension.
     */
    private File conditionallyAddFileExtension(File file) {
        StringTokenizer tokenizer =
                new StringTokenizer(file.toString(), ".");
        String name = (String) tokenizer.nextElement();
        if (tokenizer.hasMoreElements()) {
            return file;
        } else {
            return new File(file + ".xml");
        }
    }

    /**
     * Exports PSI to Specified File.
     *
     * @param file File
     */
    private void exportPsi(File file) {
        ExportPsiTask task =
                new ExportPsiTask(Cytoscape.getCurrentNetwork(), file);
        Component parentFrame = (Component) Cytoscape.getDesktop();
        task.setParentFrame(parentFrame);
        startTask(task);
    }

    private void startTask(Task task) {
        Component parentFrame = (Component) Cytoscape.getDesktop();
        new TaskMonitorUI(task, true,
                true, true, 0, (Component) parentFrame);
        task.start();
    }
}