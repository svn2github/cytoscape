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
package csplugins.dataviewer.task;

import csplugins.task.BaseTask;
import org.mskcc.dataservices.live.interaction.ReadSifFromFileOrWeb;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Task to Import SIF Data.
 *
 * @author Robert Sheridan
 */
public class LoadSifTask extends BaseTask {
    private URL url;
    private File file;
    private StringBuffer msg = new StringBuffer();
    private static final String TASK_TITLE = "Loading SIF File";

    /**
     * Constructor.
     *
     * @param url URL Location of SIF File.
     */
    public LoadSifTask(URL url) {
        super(TASK_TITLE);
        this.url = url;
    }

    /**
     * Constructor.
     *
     * @param file File Location of SIF File.
     */
    public LoadSifTask(File file) {
        super(TASK_TITLE);
        this.file = file;
    }

    /**
     * Import SIF Data.
     *
     * @throws Exception All Exceptions.
     */
    public void executeTask() throws Exception {
        this.setTaskTitle(TASK_TITLE);
        ReadSifFromFileOrWeb reader = new ReadSifFromFileOrWeb();
        ArrayList interactions = null;
        if (file != null) {
            interactions = reader.getInteractionsFromUrl(file.toString());
        } else {
            interactions = reader.getInteractionsFromUrl(url.toString());
        }
//        MapInteractionsToGraph mapper =
//                new MapInteractionsToGraph(interactions,
//                        cWindow.getGraph(),
//                        cWindow.getNodeAttributes(),
//                        cWindow.getEdgeAttributes());
//        mapper.doMapping();
//
//        cWindow.redrawGraph(true);

        this.setProgressMessage("Total Number of Interactions Retrieved:  "
                + interactions.size());
    }
}
