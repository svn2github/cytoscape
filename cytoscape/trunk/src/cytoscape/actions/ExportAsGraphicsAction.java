package cytoscape.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import cytoscape.util.CytoscapeAction;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import cytoscape.util.export.Exporter;
import cytoscape.util.export.BitmapExporter;
import cytoscape.util.export.PDFExporter;
import cytoscape.util.export.SVGExporter;
import cytoscape.dialogs.ExportBitmapOptionsDialog;

/**
 * Action for exporting a network view to bitmap or vector graphics.
 * @author Samad Lotia
 */
public class ExportAsGraphicsAction extends CytoscapeAction
{
	private static ExportFilter BMP_FILTER = new BitmapExportFilter("BMP", "bmp");
	private static ExportFilter JPG_FILTER = new BitmapExportFilter("JPEG", "jpg");
	private static ExportFilter PDF_FILTER = new PDFExportFilter();
	private static ExportFilter PNG_FILTER = new BitmapExportFilter("PNG", "png");
	private static ExportFilter SVG_FILTER = new SVGExportFilter();
	private static ExportFilter[] FILTERS = { BMP_FILTER, JPG_FILTER, PDF_FILTER, PNG_FILTER, SVG_FILTER };

	private static String TITLE = "Network View as Graphics";

	public ExportAsGraphicsAction()
	{
		super(TITLE + "...");
		setPreferredMenu("File.Export");
		setAcceleratorCombo(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	public void actionPerformed(ActionEvent e)
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
							"Save " + TITLE,
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,
							null, options, options[1]);
					
					if (choice == 0)
						super.approveSelection();
				}
				else
					super.approveSelection();
			}
		};

		// Assign settings to the file chooser
		fileChooser.setDialogTitle("Save " + TITLE);
		fileChooser.setAcceptAllFileFilterUsed(false);
		for (int i = 0; i < FILTERS.length; i++)
			fileChooser.addChoosableFileFilter(FILTERS[i]);
		
		// Show the file chooser
		int result = fileChooser.showSaveDialog(Cytoscape.getDesktop());
		if (result != JFileChooser.APPROVE_OPTION)
			return;

		// Create the file stream
		ExportFilter filter = (ExportFilter) fileChooser.getFileFilter();
		File file = filter.checkExtension(fileChooser.getSelectedFile());
		FileOutputStream stream = null;
		try
		{
			stream = new FileOutputStream(file);
		}
		catch (Exception exp)
		{
			JOptionPane.showMessageDialog(	Cytoscape.getDesktop(),
							"Could not create file " + file.getName()
							+ "\n\nError: " + exp.getMessage());
			return;
		}

		// Export
		CyNetworkView view = Cytoscape.getCurrentNetworkView();
		filter.export(view, stream);
	}
}

class ExportTask
{
	public static void run(	final String title,
				final Exporter exporter,
				final CyNetworkView view,
				final FileOutputStream stream)
	{
		// Create the Task
		Task task = new Task()
		{
			TaskMonitor monitor;

			public String getTitle()
			{
				return title;
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				this.monitor = monitor;
			}

			public void halt()
			{
			}

			public void run()
			{
				try
				{
					exporter.export(view, stream);
				}
				catch (IOException e)
				{
					monitor.setException(e, "Could not complete export of network");
				}
			}
		};
		
		// Execute the task
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(false);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}
}

abstract class ExportFilter extends FileFilter
{
	protected String description, extension;
	
	public ExportFilter(String description, String extension)
	{
		this.description = description;
		this.extension = extension;
	}

	public String getDescription()
	{
		return description + " (" + getExtension() + ")";
	}

	public boolean accept(File f)
	{
		if (f.isDirectory())
			return true;
		return f.getName().endsWith(getExtension());
	}

	public String getExtension()
	{
		return "." + extension;
	}

	public File checkExtension(File file)
	{
		if (!file.getName().endsWith(getExtension()))
			file = new File(file.getPath() + getExtension());
		return file;
	}

	public abstract void export(CyNetworkView view, FileOutputStream stream);
}

class PDFExportFilter extends ExportFilter
{
	public PDFExportFilter()
	{
		super("PDF", "pdf");
	}
	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		PDFExporter exporter = new PDFExporter();
		ExportTask.run("Exporting to PDF", exporter, view, stream);
	}
}

class BitmapExportFilter extends ExportFilter
{
	public BitmapExportFilter(String description, String extension)
	{
		super(description, extension);
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		final InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		final ExportBitmapOptionsDialog dialog = new ExportBitmapOptionsDialog(ifc.getWidth(), ifc.getHeight());
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				BitmapExporter exporter = new BitmapExporter(extension, dialog.getZoom());
				dialog.dispose();
				ExportTask.run("Exporting to " + extension, exporter, view, stream);
			}
		};
		dialog.addActionListener(listener);
		dialog.setVisible(true);
	}
}

class SVGExportFilter extends ExportFilter
{
	public SVGExportFilter()
	{
		super("SVG", "svg");
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		SVGExporter exporter = new SVGExporter();
		ExportTask.run("Exporting to SVG", exporter, view, stream);
	}
}
