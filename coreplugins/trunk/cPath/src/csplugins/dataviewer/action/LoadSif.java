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
package csplugins.dataviewer.action;

import java.awt.event.ActionEvent;

/**
 * Loads SIF Files from Local File System or from the Web.
 *
 * @author Robert Sheridan
 */
public class LoadSif extends BaseAction {

    /**
     * Action Command:  Load SIF from Web.
     */
    public static final String ACTION_IMPORT_SIF_WEB = "load_sif_web";

    /**
     * Action Command:  Load SIF from File.
     */
    public static final String ACTION_IMPORT_SIF_FILE = "load_sif_file";

    /**
     * Action Command:  Export to SIF Format.
     */
    public static final String ACTION_EXPORT_SIF = "export_sif";

    /**
     * Constructor.
     */
    public LoadSif() {
    }

    /**
     * Receives Menu Selection Event.
     *
     * @param event Action Event
     */
    public void actionPerformed(ActionEvent event) {
        try {
            String command = event.getActionCommand();
            if (command.equals(LoadSif.ACTION_IMPORT_SIF_FILE)) {
                importSifFile();
            } else if (command.equals
                    (LoadSif.ACTION_IMPORT_SIF_WEB)) {
                importSifWeb();
            }
        } catch (Exception e) {
            this.showError(e);
        }
    }

    /**
     * Imports a SIF Document from the Web.
     */
    private void importSifWeb() {
//        URL url = this.getURL("Enter URL for SIF Document:  ");
//        if (url != null) {
//            ImportSif task;
//            if (url.getProtocol().equalsIgnoreCase("file")) {
//                File file = new File(url.getFile());
//                task = new ImportSif(cWindow, file);
//            } else {
//                task = new ImportSif(cWindow, url);
//            }
//            loadSif(task);
//        }
    }

    /**
     * Imports a SIF File.
     */
    private void importSifFile() {
//        String sif[] = new String[]{"sif"};
//        String fileString = this.getFile(sif, "Simpl. Int. Files (*.sif)");
//        if (fileString != null) {
//            File file = new File(fileString);
//            ImportSif task = new ImportSif(cWindow, file);
//            loadSif(task);
//        }
    }

    /**
     * Loads SIF in a separate thread.
     *
     * @param task Import SIF Task.
     */
    private void loadSif(csplugins.dataviewer.task.LoadSifTask task) {
//        RetrievingDataDisplay display = new RetrievingDataDisplay
//                (cWindow.getMainFrame(), "Retrieving interaction Data");
//
//        LongTermTask longTermTask = new LongTermTask(task,
//                cWindow.getMainFrame(), display);
//        longTermTask.start();
    }
}
