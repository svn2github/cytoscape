/* -*-Java-*-
********************************************************************************
*
* File:         MiscGUI.java
* RCS:          $Header: /cvs/cvsroot/lstl-lsi/HyperEdge/src/cytoscape/hyperedge/impl/utils/MiscGUI.java,v 1.1 2007/07/04 01:11:35 creech Exp $
* Description:  
* Author:       Michael L. Creech
* Created:      Fri Jun 17 09:05:52 2005
* Modified:     Fri Jun 17 09:05:53 2005 (Michael L. Creech) creech@Dill
* Language:     Java
* Package:      
* Status:       Experimental (Do Not Distribute)
*
* (c) Copyright 2005, Agilent Technologies, all rights reserved.
*
********************************************************************************
*/


package cytoscape.hyperedge.impl.utils;

import java.awt.Component;
import java.awt.Cursor;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;



/**
 * Various GUI support routines.
 * @author Michael L. Creech
 * @version 1.0
 */
public class MiscGUI
{
    //~ Methods ////////////////////////////////////////////////////////////////

    /**
     * Presents a yes-no dialog with a Yes, No, and Cancel button.
     * @param app_abbr the String abbreviation of this application to use
     *                 in constructing the title line of this dialog.
     *                 If null, no abbreviation is used. If non-null,
     * a title of the form '&lt;abbr>: &lt;dialog_title>' is used.
     * @param dialog_title the title string to show within the title line
     * of this dialog.
     * @param yes_no_message the actual message to show in the dialog.
     * @param parent_comp the Component to have as a parent of this dialog.
     * @param icon        the ImageIcon to present to the left of the
     *                    yes_no_message. If null, a default ImageIcon
     *                    is used.
     * @return 0 when we have an answer of yes, non-zero for no or cancel.
     */
    static public int presentYesNoDialog (final String    app_abbr,
                                          final String    dialog_title,
                                          final String    yes_no_message,
                                          final Component parent_comp,
                                          final ImageIcon icon)
    {
        String title = "";
        if (app_abbr != null)
        {
            title = app_abbr + ": ";
        }
        title = title + dialog_title;
        if (icon == null)
        {
            return JOptionPane.showConfirmDialog (parent_comp, yes_no_message,
                                                  title,
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE);
        }
        else
        {
            return JOptionPane.showConfirmDialog (parent_comp, yes_no_message,
                                                  title,
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.QUESTION_MESSAGE,
                                                  icon);
        }
    }

    /**
     * Presents a message dialog with an OK button.
     * @param app_abbr the String abbreviation of this application to use
     *                 in constructing the title line of this dialog.
     *                 If null, no abbreviation is used. If non-null,
     * a title of the form '&lt;abbr>: &lt;dialog_title>' is used.
     * @param dialog_title the title string to show within the title line
     * of this dialog.
     * @param message the actual message to show in the dialog. This
     *                can be a String, Object or a nested array of Objects
     *                that include components to present.
     * @param parent_comp the Component to have as a parent of this dialog.
     */
    static public void presentMessageDialog (final String    app_abbr,
                                             final String    dialog_title,
                                             final Object    message,
                                             final Component parent_comp)
    {
        String title = "";
        if (app_abbr != null)
        {
            title = app_abbr + ':';
        }
        title = title + dialog_title;
        JOptionPane.showMessageDialog (parent_comp, message, title,
                                       JOptionPane.INFORMATION_MESSAGE, null);
    }

    /**
     * Ensure a given file path has a correct suffix.
     * @param file_path the path to check for a legal suffix.
     * @param correct_suffixes a String array of legal suffixes. If
     * file_path doesn't have one of these suffixes, suffix_to_add is
     * added to file_path (e.g, {"jpg", "jpeg"}).
     * @param suffix_to_add the suffix to add when file_path has no
     * suffix or an incorrect suffix (e.g., "jpg").
     * @return file_path if it has a correct suffix, otherwise
     * file_path with suffix_to_add appended to its end.
     */
    static public String ensureCorrectSuffix (String   file_path,
                                              String[] correct_suffixes,
                                              String   suffix_to_add)
    {
        String current_suffix = HEUtils.getSuffix (file_path, '.');
        if (current_suffix == null)
        {
            return file_path + '.' + suffix_to_add;
        }
        for (int i = 0; i < correct_suffixes.length; i++)
        {
            String legal_suffix = correct_suffixes[i];
            if (HEUtils.stringEqual (legal_suffix, current_suffix, false))
            {
                return file_path;
            }
        }
        return file_path + '.' + suffix_to_add;
    }

    /**
     * Check if a given file path exists and if it can be
     * overwritten. Present dialogs for user input as needed.
     * @param component_abbr the abbreviation for this component
     * (e.g., "ANV") used in presenting needed dialogs
     * @param file_path the String file path to check
     * @param component The Component in which to present needed dialogs.
     */
    static public boolean checkIfOverwriteOK (String    component_abbr,
                                              String    file_path,
                                              Component component)
    {
        // Now check if the save file exists. If so, ask if ok to overwrite:
        File local_file = new File(file_path);
        if (local_file.exists ())
        {
            if (MiscGUI.presentYesNoDialog (component_abbr,
                                            file_path + " exists!",
                                            file_path +
                                            " exists! Do you want to overwrite it?",
                                            component, null) != 0)
            {
                // cancel:
                return false;
            }

            // is the file read-only?
            if (!local_file.canWrite ())
            {
                MiscGUI.presentMessageDialog (component_abbr,
                                              file_path + " not writable!",
                                              "The file '" + file_path +
                                              "' is not writable. Please try again with another file.",
                                              component);
                return false;
            }
        }
        return true;
    }

    static public Cursor useWaitCursor (Component comp)
    {
        Cursor saved = comp.getCursor ();
        comp.setCursor (Cursor.getPredefinedCursor (Cursor.WAIT_CURSOR));
        return saved;
    }

    static public void resetCursor (Cursor    saved,
                                    Component comp)
    {
        comp.setCursor (saved);
    }
}
