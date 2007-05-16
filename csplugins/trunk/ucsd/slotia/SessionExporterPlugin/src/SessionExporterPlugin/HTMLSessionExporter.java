package SessionExporterPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import javax.swing.JOptionPane;
import javax.imageio.ImageIO;

import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.view.CyNetworkView;
import ding.view.DGraphView;
import cytoscape.visual.VisualStyle;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.data.writers.InteractionWriter;
import cytoscape.data.writers.CytoscapeSessionWriter;

public class HTMLSessionExporter implements SessionExporter
{
	private static final int MAX_COLUMNS = 3;

	private static final String IMAGE_TYPE = "png";
	
	public void export(final File directory)
	{
		Task task = new Task()
		{
			TaskMonitor monitor = null;
			boolean needToHalt = false;
			Files files = new Files();

			public String getTitle()
			{
				return "Session for Web";
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				this.monitor = monitor;
			}

			public void halt()
			{
				needToHalt = true;
			}

			private void setStatus(String status)
			{
				if (monitor != null)
					monitor.setStatus(status);
			}

			private void setPercentCompleted(int percent)
			{
				if (monitor != null)
					monitor.setPercentCompleted(percent);
			}

			public void run()
			{
				try
				{
					List<String> networkIDs = networkIDs();
					writeSession(directory);
					generateImages(directory, networkIDs);
					generateHTML(directory, networkIDs);
				}
				catch (Exception e)
				{
					if (monitor != null)
						monitor.setException(e, "Could not complete session export");
					else
						e.printStackTrace();
				}
			}

			private List<String> networkIDs()
			{
				List<String> networkIDs = new ArrayList<String>();
				Iterator iterator = Cytoscape.getNetworkSet().iterator();
				while (iterator.hasNext())
				{
					CyNetwork network = (CyNetwork) iterator.next();
					String networkID = network.getIdentifier();
					networkIDs.add(networkID);
				}
				return networkIDs;
			}

			private void writeSession(File directory) throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing Cytoscape session file...");
				setPercentCompleted(1);
				File sessionFile = files.sessionFile(directory);
				CytoscapeSessionWriter sessionWriter = new CytoscapeSessionWriter(sessionFile.toString());
				sessionWriter.writeSessionToDisk();
			}

			private void generateImages(File directory, List<String> networkIDs) throws Exception
			{
				GraphViewToImage graphViewToImage = new GraphViewToImage(1.0, true, true);
				Map viewMap = Cytoscape.getNetworkViewMap();
				int currentNetwork = 0;
				int networkCount = networkIDs.size();
				for (String networkID : networkIDs)
				{
					String networkTitle = Cytoscape.getNetwork(networkID).getTitle();

					if (needToHalt)
						return;

					setStatus("Exporting SIF for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount);
					File sifFile = files.sifFile(directory, networkTitle);
					FileWriter sifWriter = new FileWriter(sifFile);
					CyNetwork network = Cytoscape.getNetwork(networkID);
					InteractionWriter.writeInteractions(network, sifWriter, null);
					sifWriter.close();

					if (needToHalt)
						return;

					DGraphView view = (DGraphView) viewMap.get(networkID);
					setStatus("Rendering image for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 20 / networkCount);
					File imageFile = files.imageFile(directory, networkTitle, IMAGE_TYPE);
					BufferedImage image = graphViewToImage.convert(view);
					if (image != null)
						ImageIO.write(image, IMAGE_TYPE, imageFile);

					if (needToHalt)
						return;

					setStatus("Generating thumbnail for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 40 / networkCount);
					File thumbnailFile = files.thumbnailFile(directory, networkTitle, IMAGE_TYPE);
					BufferedImage thumbnail = Thumbnails.createThumbnail(image);
					ImageIO.write(thumbnail, IMAGE_TYPE, thumbnailFile);
					
					if (needToHalt)
						return;

					setStatus("Rendering legend for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 60 / networkCount);
					File legendFile = files.legendFile(directory, networkTitle, IMAGE_TYPE);
					VisualStyle visualStyle = Cytoscape.getNetworkView(networkID).getVisualStyle();
					BufferedImage legend = VisualStyleToImage.convert(visualStyle);
					if (legend != null)
						ImageIO.write(legend, IMAGE_TYPE, legendFile);

					currentNetwork++;
				}
			}

			private void generateHTML(File directory, List<String> networkIDs) throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing HTML page");
				setPercentCompleted(90);
				File htmlFile = files.htmlFile(directory);
				PrintWriter htmlWriter = new PrintWriter(htmlFile);
				htmlWriter.println("<html>");
				htmlWriter.println("<body>");
				htmlWriter.println("<a href=\"" + files.sessionFile() + "\">Cytoscape Session File</a><br/>");
				htmlWriter.println("<table border=\"0\" cellspacing=\"10\" cellpadding=\"0\">");
				int i = 0;
				for (String networkID : networkIDs)
				{
					if (needToHalt)
						return;

					String networkTitle = Cytoscape.getNetwork(networkID).getTitle();

					if ((i % MAX_COLUMNS) == 0)
						htmlWriter.println("<tr valign=bottom>");
					htmlWriter.println("<td align=center valign=bottom>");

					File imageFile = files.imageFile(directory, networkTitle, IMAGE_TYPE);
					File legendFile = files.legendFile(directory, networkTitle, IMAGE_TYPE);

					if (imageFile.exists())
						htmlWriter.print("<a href=\"" + imageFile.getName() + "\">");
					htmlWriter.print("<img border=0 src=\"" + files.thumbnailFile(networkTitle, IMAGE_TYPE) + "\">");
					if (imageFile.exists())
						htmlWriter.print("</a>");

					htmlWriter.print("<font size=-1>");
					htmlWriter.print("<br>" + networkTitle + " <br/>(");
					htmlWriter.print("<a href=\"" + files.sifFile(networkTitle) + "\">sif</a>");
					
					if (imageFile.exists())
						htmlWriter.print(" | <a href=\"" + imageFile.getName() + "\">image</a>");
					if (legendFile.exists())
						htmlWriter.print(" | <a href=\"" + legendFile.getName() + "\">legend</a>");
					
					htmlWriter.print(")</font>");
					htmlWriter.println("</td>");
					if ((i + 1) % MAX_COLUMNS == 0)
						htmlWriter.println("</tr>");
					i++;
				}
				if ((i + 1) % MAX_COLUMNS != 0)
					htmlWriter.println("</tr>");
				htmlWriter.println("</table>");
				htmlWriter.println("</body>");
				htmlWriter.println("</html>");
				htmlWriter.close();
			}
		};

		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(true);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(true);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}
	

	private class Files
	{
		public String sessionFile()
		{
			return "session.cys";
		}

		public File sessionFile(File directory)
		{
			return new File(directory, sessionFile());
		}

		public String htmlFile()
		{
			return "index.html";
		}

		public File htmlFile(File directory)
		{
			return new File(directory, htmlFile());
		}

		public String sifFile(String network)
		{
			return network + ".sif";
		}

		public File sifFile(File directory, String network)
		{
			return new File(directory, sifFile(network));
		}

		public String imageFile(String network, String format)
		{
			return network + "_image." + format;
		}

		public File imageFile(File directory, String network, String format)
		{
			return new File(directory, imageFile(network, format));
		}

		public String thumbnailFile(String network, String format)
		{
			return network + "_thumbnail." + format;
		}

		public File thumbnailFile(File directory, String network, String format)
		{
			return new File(directory, thumbnailFile(network, format));
		}

		public String legendFile(String network, String format)
		{
			return network + "_legend." + format;
		}

		public File legendFile(File directory, String network, String format)
		{
			return new File(directory, legendFile(network, format));
		}
	}
}
