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
package csplugins.test.task;

import csplugins.task.Task;
import csplugins.task.SampleTask;
import csplugins.task.TaskMonitorUI;

/**
 * Tests the TaskMonitorUI from the Command Line.
 * This is not a JUnit Test.
 *
 * @author Ethan Cerami.
 */
public class TestUI {

    /**
     * Main Method, used to testing purposes only.
     *
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        String arg1 = args[0];
        if (arg1.equals("1")) {
            //  Uses Default TaskMonitorUI
            Task task = new SampleTask(100, 2000, 2000, 50);
            TaskMonitorUI monitor = new TaskMonitorUI(task);
            monitor.show();
            task.start();
        } else if (arg1.equals("2")) {
            //  Customizes TaskMonitorUI
            //  Hide Time Fields
            //  Hide User Buttons
            //  Automatically Dispose when Task is Complete.
            Task task = new SampleTask(100, 2000, 2000, 50);
            TaskMonitorUI monitor = new TaskMonitorUI(task, false, false, true);
            monitor.show();
            task.start();
        }
    }
}
