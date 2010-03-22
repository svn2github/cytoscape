/*
  Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

  The Cytoscape Consortium is:
  - Institute for Systems Biology
  - University of California San Diego
  - Memorial Sloan-Kettering Cancer Center
  - Institut Pasteur
  - Agilent Technologies

  This library is free software; you can redistribute it and/or modify it
  under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation; either version 2.1 of the License, or
  any later version.

  This library is distributed in the hope that it will be useful, but
  WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
  documentation provided hereunder is on an "as is" basis, and the
  Institute for Systems Biology and the Whitehead Institute
  have no obligations to provide maintenance, support,
  updates, enhancements or modifications.  In no event shall the
  Institute for Systems Biology and the Whitehead Institute
  be liable to any party for direct, indirect, special,
  incidental or consequential damages, including lost profits, arising
  out of the use of this software and its documentation, even if the
  Institute for Systems Biology and the Whitehead Institute
  have been advised of the possibility of such damage.  See
  the GNU Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License
  along with this library; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package org.cytoscape.coreplugin.cpath.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Error Display Widget.
 *
 * @author Ethan Cerami
 */
public class ErrorDisplay {
    private Frame parent;
    private static final int WIDTH = 50;
    private static final char SPACE = ' ';
    private static final String NEW_LINE = "\n";

    /**
     * Constructor.
     *
     * @param frame Frame object.
     */
    public ErrorDisplay (Frame frame) {
        this.parent = frame;
    }

    /**
     * Displays the Error Dialog Box.
     *
     * @param e Exception.
     */
    public void displayError (Throwable e) {
        showError(e);
    }

    /**
     * Displays the Error Dialog Box.
     *
     * @param e      Exception.
     * @param cPanel ConsolePanel Object.
     */
    public void displayError (Throwable e, ConsolePanel cPanel) {
        String msg = showError(e);
        cPanel.logMessage(msg);
    }

    /**
     * Displays the Error Dialog Box.
     *
     * @param errorMsg Error Message String.
     */
    public void displayError (String errorMsg) {
        String msg = wrapText(errorMsg);
        JOptionPane.showMessageDialog(parent, msg,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays the Error Dialog Box.
     *
     * @param e Exception.
     */
    private String showError (Throwable e) {
        e.printStackTrace();
        String msg = null;
//        if (e instanceof DataServiceException) {
//            DataServiceException dse = (DataServiceException) e;
//            msg = new String("An Error Has Occurred:  "
//                    + dse.getHumanReadableErrorMessage());
//        } else if (e instanceof MapperException) {
//            MapperException me = (MapperException) e;
//            msg = new String("An Error Has Occurred:  "
//                    + me.getHumanReadableErrorMessage());
//        } else {
//            Throwable t = e.getCause();
//            if (t == null) {
//                t = e;
//            }
//            msg = new String("An Error Has Occurred:  " + e.toString());
//        }
        msg = wrapText(msg);
        JOptionPane.showMessageDialog(parent, msg,
                "Error", JOptionPane.ERROR_MESSAGE);
        return msg;
    }

    /**
     * Wraps text to WIDTH characters long.
     *
     * @param msg Message.
     * @return Wrapped Text message.
     */
    public String wrapText (String msg) {
        StringBuffer newMessage = new StringBuffer();
        if (msg != null) {
            String strs[] = msg.split("\\s+");
            int lineIndex = 0;
            for (int i = 0; i < strs.length; i++) {
                newMessage.append(strs[i] + SPACE);
                lineIndex += strs[i].length();
                if (lineIndex > WIDTH) {
                    newMessage.append(NEW_LINE);
                    lineIndex = 0;
                }
            }
            if (newMessage.length() > 500) {
                return newMessage.substring(0, 500) + "...";
            }
            return newMessage.toString();
        } else {
            return msg;
        }
    }
}