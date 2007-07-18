package cytoscape.actions;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import cytoscape.Cytoscape;
import cytoscape.view.InternalFrameComponent;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

// imports for PDF writing
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Action for exporting a network view to bitmap or vector graphics.
 * @author Samad Lotia
 */
public class ExportAsGraphicsAction extends CytoscapeAction
{
	private static ExportFilter BMP_FILTER = new ExportFilter("BMP", "bmp", new BitmapExporter("bmp"));
	private static ExportFilter JPG_FILTER = new ExportFilter("JPEG", "jpg", new BitmapExporter("jpg"));
	private static ExportFilter PDF_FILTER = new ExportFilter("PDF", "pdf", new PDFExporter());
	private static ExportFilter PNG_FILTER = new ExportFilter("PNG", "png", new BitmapExporter("png"));
	private static ExportFilter[] FILTERS = { BMP_FILTER, JPG_FILTER, PDF_FILTER, PNG_FILTER };

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
							"Save Cytoscape Session To",
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

		final ExportFilter filter = (ExportFilter) fileChooser.getFileFilter();
		final File file = checkExtension(fileChooser.getSelectedFile(), filter.getExtension());

		// Create the Task
		Task task = new Task()
		{
			public String getTitle()
			{
				return TITLE;
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				monitor.setStatus("Saving " + TITLE + " to " + file.getName());
			}

			public void halt()
			{
			}

			public void run()
			{
				CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
				InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(networkView);
				filter.getExporter().export(ifc, file);
			}
		};
		
		// Execute the task
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}

	private File checkExtension(File file, String extension)
	{
		if (!file.getName().endsWith(extension))
			file = new File(file.getPath() + extension);
		return file;
	}
}

class ExportFilter extends FileFilter
{
	private String description, extension;
	private Exporter exporter;
	
	public ExportFilter(String description, String extension, Exporter exporter)
	{
		this.description = description;
		this.extension = extension;
		this.exporter = exporter;
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

	public Exporter getExporter()
	{
		return exporter;
	}

	public String getExtension()
	{
		return "." + extension;
	}
}

interface Exporter
{
	public void export(InternalFrameComponent ifc, File file);
}

class PDFExporter implements Exporter
{
	public void export(InternalFrameComponent ifc, File file)
	{
		Rectangle pageSize = PageSize.LETTER;
		Document document = new Document(pageSize);
		try
		{
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
			PdfContentByte cb = writer.getDirectContent();
			Graphics2D g = cb.createGraphicsShapes((int) pageSize.getWidth(), (int) pageSize.getHeight());
			double imageScale = Math.min(pageSize.getWidth()  / ((double) ifc.getWidth()),
			                             pageSize.getHeight() / ((double) ifc.getHeight()));
			g.scale(imageScale, imageScale);
			ifc.print(g);
			g.dispose();
		}
		catch (Exception exp)
		{
			JOptionPane.showMessageDialog(
				Cytoscape.getDesktop(),
				"Could not export to PDF.\n\nError: " + exp.getMessage(),
				"Export to PDF", JOptionPane.ERROR_MESSAGE);
		}

		document.close();
	}
}

class BitmapExporter implements Exporter
{
	private String extension;

	public BitmapExporter(String extension)
	{
		this.extension = extension;
	}

	public void export(InternalFrameComponent ifc, File file)
	{
		int width = ifc.getWidth();
		int height = ifc.getHeight();

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D) image.getGraphics();
		ifc.print(g);
		g.dispose();
		
		try
		{
			FileOutputStream stream = new FileOutputStream(file);
			ImageIO.write(image, extension, stream);
			stream.close();
		}
		catch (Exception exp)
		{
			JOptionPane.showMessageDialog(
				Cytoscape.getDesktop(),
				"Could not export to bitmap graphics.\n\nError: " + exp.getMessage(),
				"Export to Bitmap Graphics", JOptionPane.ERROR_MESSAGE);
		}
	}
}
