
/*
 Copyright (c) 2008, The Cytoscape Consortium (www.cytoscape.org)

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


package cytoscape.hyperedge.impl.utils;

import java.awt.Component;
import java.awt.Cursor;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Various GUI support routines.
 * 
 * @author Michael L. Creech
 * @version 1.0
 */
public final class MiscGUI {
    // Don't want people to manipulate the utility class constructor.
    private MiscGUI() {}
    
    // ~ Methods
    // ////////////////////////////////////////////////////////////////

    /**
     * Presents a yes-no dialog with a Yes, No, and Cancel button.
     * 
     * @param appAbbr
     *            the String abbreviation of this application to use in
     *            constructing the title line of this dialog. If null, no
     *            abbreviation is used. If non-null, a title of the form
     *            '&lt;appAbbr>: &lt;dialogTitle>' is used.
     * @param dialogTitle
     *            the title string to show within the title line of this dialog.
     * @param yesNoMessage
     *            the actual message to show in the dialog.
     * @param parentComp
     *            the Component to have as a parent of this dialog.
     * @param icon
     *            the ImageIcon to present to the left of the yes_no_message. If
     *            null, a default ImageIcon is used.
     * @return 0 when we have an answer of yes, non-zero for no or cancel.
     */
    public static int presentYesNoDialog(final String appAbbr,
	    final String dialogTitle, final String yesNoMessage,
	    final Component parentComp, final ImageIcon icon) {
	String title = "";
	if (appAbbr != null) {
	    title = appAbbr + ": ";
	}
	title = title + dialogTitle;
	if (icon == null) {
	    return JOptionPane.showConfirmDialog(parentComp, yesNoMessage,
		    title, JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE);
	} else {
	    return JOptionPane.showConfirmDialog(parentComp, yesNoMessage,
		    title, JOptionPane.YES_NO_OPTION,
		    JOptionPane.QUESTION_MESSAGE, icon);
	}
    }

    /**
     * Presents a message dialog with an OK button.
     * 
     * @param appAbbr
     *            the String abbreviation of this application to use in
     *            constructing the title line of this dialog. If null, no
     *            abbreviation is used. If non-null, a title of the form
     *            '&lt;abbr>: &lt;dialog_title>' is used.
     * @param dialogTitle
     *            the title string to show within the title line of this dialog.
     * @param message
     *            the actual message to show in the dialog. This can be a
     *            String, Object or a nested array of Objects that include
     *            components to present.
     * @param parentComp
     *            the Component to have as a parent of this dialog.
     */
    public static void presentMessageDialog(final String appAbbr,
	    final String dialogTitle, final Object message,
	    final Component parentComp) {
	String title = "";
	if (appAbbr != null) {
	    title = appAbbr + ':';
	}
	title = title + dialogTitle;
	JOptionPane.showMessageDialog(parentComp, message, title,
		JOptionPane.INFORMATION_MESSAGE, null);
    }

    /**
     * Ensure a given file path has a correct suffix.
     * 
     * @param filePath
     *            the path to check for a legal suffix.
     * @param correctSuffixes
     *            a String array of legal suffixes. If file_path doesn't have
     *            one of these suffixes, suffix_to_add is added to file_path
     *            (e.g, {"jpg", "jpeg"}).
     * @param suffixToAdd
     *            the suffix to add when file_path has no suffix or an incorrect
     *            suffix (e.g., "jpg").
     * @return file_path if it has a correct suffix, otherwise file_path with
     *         suffix_to_add appended to its end.
     */
    public static String ensureCorrectSuffix(final String filePath,
	    final String[] correctSuffixes, final String suffixToAdd) {
	final String currentSuffix = HEUtils.getSuffix(filePath, '.');
	if (currentSuffix == null) {
	    return filePath + '.' + suffixToAdd;
	}
	for (int i = 0; i < correctSuffixes.length; i++) {
	    final String legalSuffix = correctSuffixes[i];
	    if (HEUtils.stringEqual(legalSuffix, currentSuffix, false)) {
		return filePath;
	    }
	}
	return filePath + '.' + suffixToAdd;
    }

    /**
     * Check if a given file path exists and if it can be overwritten. Present
     * dialogs for user input as needed.
     * 
     * @param componentAbbr
     *            the abbreviation for this component (e.g., "ANV") used in
     *            presenting needed dialogs
     * @param filePath
     *            the String file path to check
     * @param component
     *            The Component in which to present needed dialogs.
     * @return true iff it is OK to overwrite an existing path,
     *         or if there is not existing path.
     */
    public static boolean checkIfOverwriteOK(final String componentAbbr,
	    final String filePath, final Component component) {
	// Now check if the save file exists. If so, ask if ok to overwrite:
	final File localFile = new File(filePath);
	if (localFile.exists()) {
	    if (MiscGUI.presentYesNoDialog(componentAbbr, filePath
		    + " exists!", filePath
		    + " exists! Do you want to overwrite it?", component, null) != 0) {
		// cancel:
		return false;
	    }

	    // is the file read-only?
	    if (!localFile.canWrite()) {
		MiscGUI
			.presentMessageDialog(
				componentAbbr,
				filePath + " not writable!",
				"The file '"
					+ filePath
					+ "' is not writable. Please try again with another file.",
				component);
		return false;
	    }
	}
	return true;
    }

    /**
     * Change a given component to use a wait cursor.
     * @param comp the Component for which to change the Cursor.
     * @return the previous Cursor.
     */
    public static Cursor useWaitCursor(final Component comp) {
	final Cursor saved = comp.getCursor();
	comp.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	return saved;
    }

    /**
     * Reset the cursor back to its saved value.
     * @param saved the previous saved Cursor.
     * @param comp the Component for which to change the Cursor.
     * @see MiscGUI#useWaitCursor
     */
    public static void resetCursor(final Cursor saved, final Component comp) {
	comp.setCursor(saved);
    }
}
