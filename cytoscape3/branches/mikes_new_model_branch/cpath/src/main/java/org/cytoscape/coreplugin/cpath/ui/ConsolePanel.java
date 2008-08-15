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
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import java.awt.*;

/**
 * Console Panel Component.
 *
 * @author Ethan Cerami
 */
public class ConsolePanel extends JPanel implements Console {
    private JTextPane ta;
    private JScrollPane scrollPane;

    /**
     * No Arg Constructor.
     */
    public ConsolePanel () {
        this.setLayout(new BorderLayout());
        ta = new JTextPane();
        ta.setMargin(new Insets(5, 7, 5, 7));
        ta.setEditable(false);
        ta.setBackground(Color.WHITE);
        ta.setForeground(Color.BLACK);
        addStylesToDocument(ta.getStyledDocument());
        Font font = new Font("Courier", Font.PLAIN, 12);
        ta.setFont(font);
        scrollPane = new JScrollPane(ta);

        StyledDocument doc = ta.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), "Note:  As of January 1, 2008, the " +
                "cPath plugin will continue to operate, but will no longer be maintained.  "
                + "Users are advised to use the Pathway Commons plugin instead. "
                + "\n\nTo access the Pathway Commons plugin, select File -> Import -> "
                + "Network from Web Services, and select the Pathway Commons Web Service."
                + "\n\n", doc.getStyle("red-bold"));            
        } catch (BadLocationException e) {
        }
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Sets the Main Status Message.
     *
     * @param msg Status Message.
     */
    public void logMessage (String msg) {
        logMessage(msg, "regular");
    }

    /**
     * Sets the Main Status Message in Bold.
     *
     * @param msg Status Message.
     * @param style Style.
     */
    public void logMessage (String msg, String style) {
        logMessageWithStyle(msg, style);
    }

    /**
     * Clears the Console.
     */
    public void clear () {
        ta.setText("");
    }

    /**
     * Sets the Main Status Message in the Specified Style.
     *
     * @param msg   Status Message.
     * @param style Style, e.g. "regular", "bold".
     */
    private void logMessageWithStyle (String msg, String style) {
        StyledDocument doc = ta.getStyledDocument();
        try {
            doc.insertString(doc.getLength(), msg + "\n", doc.getStyle(style));
        } catch (BadLocationException e) {
        }

        //  Causes the ScrollPane to automatically scroll down.
        //  The line below used to use this code:  ta.getText().length()
        //  However, it resulted in error when running on Windows
        //  (see bug #509).  To fix the bug, use ta.getDocument().getLength().
        ta.setCaretPosition(ta.getDocument().getLength());
    }

    /**
     * Adds Styles to the JTextArea.
     *
     * @param doc StyledDocument Object.
     */
    protected void addStylesToDocument (StyledDocument doc) {
        Style def = StyleContext.getDefaultStyleContext().
                getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle("regular", def);
        StyleConstants.setFontFamily(def, "SansSerif");

        Style s = doc.addStyle("italic", regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle("bold", regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle("small", regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle("large", regular);
        StyleConstants.setFontSize(s, 16);

        s = doc.addStyle("red", regular);
        StyleConstants.setForeground(s, Color.RED);

        s = doc.addStyle("red-bold", regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.RED);        
    }

    /**
     * Gets te Preferred Size.
     *
     * @return Dimension Object.CP
     */
    public Dimension getPreferredSize () {
        return new Dimension(2, 150);
    }
}