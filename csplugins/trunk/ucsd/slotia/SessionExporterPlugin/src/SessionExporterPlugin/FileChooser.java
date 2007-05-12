package SessionExporterPlugin;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cytoscape.Cytoscape;

public class FileChooser
{
	/**
	 * Creates a file chooser dialog.
	 * This produces the correct dialog, prompts the user if the chosen
	 * file exists, and produces error dialogs if necessary.
	 * @return a File for the file or directory chosen, null if nothing was selected
	 */
	public static File chooseFile()
	{
		JFileChooser fileChooser = new JFileChooser()
		{
			// When the user clicks "Save"
			public void approveSelection()
			{
				// If the file exists, we should make sure that it is OK
				// to replace it
				if (getSelectedFile().exists())
				{
					Object options[] = { "Yes", "No" };
					int choice = JOptionPane.showOptionDialog(Cytoscape.getDesktop(),
							"\"" + getSelectedFile() + "\" already exists.\n\n" +
							"Do you want to replace it?",
							"Save Cytoscape Session To",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null,
							options,
							options[1]);
					
					if (choice == 0)
						super.approveSelection();
					else
						super.cancelSelection();
				}
				else
					super.approveSelection();
			}
		};

		fileChooser.setDialogTitle("Save Cytoscape Session To");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setAcceptAllFileFilterUsed(false);
		if (fileChooser.showSaveDialog(Cytoscape.getDesktop()) == JFileChooser.APPROVE_OPTION)
		{
			// Attempt to create the necessary directories
			try
			{
				if (!fileChooser.getSelectedFile().exists() && !fileChooser.getSelectedFile().mkdirs())
				{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Failed to create \"" + fileChooser.getSelectedFile() + "\".",
						"Save Cytoscape Session To",
						JOptionPane.ERROR_MESSAGE);
					return null;
				}
			}
			catch (SecurityException e)
			{
				JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
					"Failed to create \"" + fileChooser.getSelectedFile() + "\" because of " +
					"insufficient security permissions.\n\n" +
					"Security error: " + e.getMessage(),
					"Save Cytoscape Session To",
					JOptionPane.ERROR_MESSAGE);
				return null;
			}

			return fileChooser.getSelectedFile();
		}
		else
			return null;
	}
}
