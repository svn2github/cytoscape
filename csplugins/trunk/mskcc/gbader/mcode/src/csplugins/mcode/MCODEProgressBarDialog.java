package csplugins.mcode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * * Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center
 * *
 * * Code written by: Gary Bader
 * * Authors: Gary Bader, Ethan Cerami, Chris Sander
 * *
 * * This library is free software; you can redistribute it and/or modify it
 * * under the terms of the GNU Lesser General Public License as published
 * * by the Free Software Foundation; either version 2.1 of the License, or
 * * any later version.
 * *
 * * This library is distributed in the hope that it will be useful, but
 * * WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 * * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 * * documentation provided hereunder is on an "as is" basis, and
 * * Memorial Sloan-Kettering Cancer Center
 * * has no obligations to provide maintenance, support,
 * * updates, enhancements or modifications.  In no event shall the
 * * Memorial Sloan-Kettering Cancer Center
 * * be liable to any party for direct, indirect, special,
 * * incidental or consequential damages, including lost profits, arising
 * * out of the use of this software and its documentation, even if
 * * Memorial Sloan-Kettering Cancer Center
 * * has been advised of the possibility of such damage.  See
 * * the GNU Lesser General Public License for more details.
 * *
 * * You should have received a copy of the GNU Lesser General Public License
 * * along with this library; if not, write to the Free Software Foundation,
 * * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 * *
 * * User: Gary Bader
 * * Date: Jul 7, 2004
 * * Time: 3:35:11 PM
 * * Description: Progress bar for long MCODE tasks (more than 0.5 seconds)
 */

/**
 * Progress bar for long MCODE tasks (more than 0.5 seconds)
 */
public class MCODEProgressBarDialog extends JDialog {
    private JProgressBar progressBar;
    private JButton cancelButton;
    private boolean cancelled;

    /**
     * Progress bar for MCODE - determinate with a cancel button
     * @param parentFrame Parent of this dialog
     */
    public MCODEProgressBarDialog(Frame parentFrame) {
        super(parentFrame, "MCODE Progress", false);
        setResizable(false);

        cancelled=false;

        //Create the demo's UI.
        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(200, 20));
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new MCODEProgressBarDialog.cancelAction());

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(progressBar);
        panel.add(cancelButton);

        panel.setOpaque(true);
        setContentPane(panel);
    }

    /**
     * Set the maximum value for the progress bar
     * @param lengthOfTask The length of the task (maximum of the progress bar)
     */
    public void setLengthOfTask(int lengthOfTask) {
        progressBar.setMaximum(lengthOfTask);
    }

    /**
     * Set the value for the progress bar
     * @param n Value to set for the progress bar - should be larger than the current value
     */
    public void setValue(int n) {
        progressBar.setValue(n);
    }

    /**
     * Sets the progress bar to be indeterminate
     * @param indeterminate
     */
    public void setIndeterminate(boolean indeterminate) {
        progressBar.setIndeterminate(indeterminate);
    }

    /**
     * Sets the message string of the progress bar
     * @param s String to be displayed on the progress bar
     */
    public void setString(String s) {
        progressBar.setString(s);
    }

    /**
     * Returns true if user has pressed the cancel button.  Task should periodically check this and
     * cancel itself at the next safe opportunity.
     * @return True if cancelled, false if not
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Handles the cancel button press for the progress bar
     */
    private class cancelAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            progressBar.setString("Cancelling. Please wait...");
            cancelled=true;
            /*doesn't actually do anything here, the calling code has the responsibility to cancel itself
              if it sees that the cancel button has been pressed, by a call to isCancelled()*/
        }
    }
}
