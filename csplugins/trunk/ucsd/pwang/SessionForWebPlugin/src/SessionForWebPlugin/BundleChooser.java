package SessionForWebPlugin;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import cytoscape.Cytoscape;

public class BundleChooser
{
	/**
	 * Creates a file chooser dialog, and returns a Bundle reflecting the user's choice.
	 * @return a Bundle for the file or directory chosen, null if nothing was selected
	 */
	public static Bundle chooseBundle(SessionExporterSettings settings)
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
							"Save Session To",
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

		if (settings.destination == SessionExporterSettings.DESTINATION_DIRECTORY)
		{
			fileChooser.setDialogTitle("Save Session To Directory");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setAcceptAllFileFilterUsed(false);
		}
		else if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE 
				|| settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS)
		{
			fileChooser.setDialogTitle("Save Session To Zip Archive");
			fileChooser.setSelectedFile(new File("bundle.zip"));
			FileFilter zipFilter = new FileFilter()
			{
				public boolean accept(File f)
				{
					if (f.isDirectory())
						return true;
					else
						return f.getName().endsWith(".zip");
				}

				public String getDescription()
				{
					return "Zip Archive";
				}
			};
			fileChooser.addChoosableFileFilter(zipFilter);
		}

		if (fileChooser.showSaveDialog(Cytoscape.getDesktop()) != JFileChooser.APPROVE_OPTION)
			return null;
			
		// Attempt to create the necessary directories;
		// display the appropriate error messages if an error has occured
		if (settings.destination == SessionExporterSettings.DESTINATION_DIRECTORY)
		{
			try
			{
				if (!fileChooser.getSelectedFile().exists() && !fileChooser.getSelectedFile().mkdirs())
				{
					JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
						"Failed to create \"" + fileChooser.getSelectedFile() + "\".",
						"Save Session To",
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
					"Save Session To",
					JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}

		File selectedFile = fileChooser.getSelectedFile();
		Bundle bundle = null;
		try
		{
			if (settings.destination == SessionExporterSettings.DESTINATION_DIRECTORY)
				bundle = new DirectoryBundle(selectedFile);
			else if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE)
				bundle = new ZipBundle(selectedFile);
			else if (settings.destination == SessionExporterSettings.DESTINATION_ZIP_ARCHIVE_4CELLCIRCUITS){
				settings.destinationDir =selectedFile;
				bundle = new ZipBundle2(selectedFile);
			}
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(Cytoscape.getDesktop(),
				"Failed to create the bundle \"" + selectedFile.toString() + "\".\n\n" +
				"Error message: " + e.getMessage(), "Session for Web", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return bundle;
	}
}
