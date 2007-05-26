package SessionExporterPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.File;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.FileInputStream;
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

public class HTMLSessionExporter
{
	public void export(final SessionExporterSettings settings)
	{
		final Bundle bundle = BundleChooser.chooseBundle(settings);
		if (bundle == null)
			return;
		
		Task task = new Task()
		{
			TaskMonitor monitor = null;
			boolean needToHalt = false;

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
					writeSession();
					generateNetworkFiles(networkIDs);
					generateIndexHTML(networkIDs);
					bundle.close();
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

			private void writeSession() throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing Cytoscape session file...");
				setPercentCompleted(1);

				// Write session file to a temporary location
				File tempFile = File.createTempFile("session", "cys");
				CytoscapeSessionWriter sessionWriter = new CytoscapeSessionWriter(tempFile.toString());
				sessionWriter.writeSessionToDisk();

				// Copy the temp file to the bundle
				FileInputStream tempStream = new FileInputStream(tempFile);
				bundle.openEntry(Bundle.sessionFile());
				OutputStream bundleStream = bundle.entryOutputStream();
				byte[] buffer = new byte[1024*1024]; // 1MB buffer
				while (true)
				{
					int bytesRead = tempStream.read(buffer);
					if (bytesRead == -1)
						break;
					bundleStream.write(buffer, 0, bytesRead);
					bundleStream.flush();
					
				}
				bundle.closeEntry();
				tempStream.close();
			} // end writeSession()

			private void generateNetworkFiles(List<String> networkIDs) throws Exception
			{
				Map viewMap = Cytoscape.getNetworkViewMap();
				int currentNetwork = 0;
				int networkCount = networkIDs.size();
				for (String networkID : networkIDs)
				{
					String networkTitle = Cytoscape.getNetwork(networkID).getTitle();

					if (needToHalt)
						return;

					// export the sif file
					setStatus("Exporting SIF for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount);
					bundle.openEntry(Bundle.sifFile(networkTitle));
					CyNetwork network = Cytoscape.getNetwork(networkID);
					InteractionWriter.writeInteractions(network, bundle.entryWriter(), null);
					bundle.closeEntry();

					if (needToHalt)
						return;

					// render network image
					DGraphView view = (DGraphView) viewMap.get(networkID);
					setStatus("Rendering image for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 20 / networkCount);
					BufferedImage image = GraphViewToImage.convert(view, settings);
					if (image != null)
					{
						bundle.openEntry(Bundle.imageFile(networkTitle, settings.imageFormat));
						ImageIO.write(image, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					if (needToHalt)
						return;

					// generate thumbnail
					setStatus("Generating thumbnail for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 40 / networkCount);
					bundle.openEntry(Bundle.thumbnailFile(networkTitle, settings.imageFormat));
					BufferedImage thumbnail = Thumbnails.createThumbnail(image, settings);
					ImageIO.write(thumbnail, settings.imageFormat, bundle.entryOutputStream());
					bundle.closeEntry();
					
					if (needToHalt)
						return;

					// render legend
					setStatus("Rendering legend for: " + networkTitle);
					setPercentCompleted(10 + 80 * currentNetwork / networkCount + 60 / networkCount);
					VisualStyle visualStyle = Cytoscape.getNetworkView(networkID).getVisualStyle();
					BufferedImage legend = VisualStyleToImage.convert(visualStyle);
					if (legend != null)
					{
						bundle.openEntry(Bundle.legendFile(networkTitle, settings.imageFormat));
						ImageIO.write(legend, settings.imageFormat, bundle.entryOutputStream());
						bundle.closeEntry();
					}

					currentNetwork++;

				} // end for
				
			} // end generateImages()

			private void generateIndexHTML(List<String> networkIDs) throws Exception
			{
				if (needToHalt)
					return;

				setStatus("Writing HTML page");
				setPercentCompleted(90);
				bundle.openEntry(Bundle.indexHTMLFile());
				PrintWriter htmlWriter = new PrintWriter(bundle.entryWriter());
				htmlWriter.println("<html>");
				htmlWriter.println("<body>");
				htmlWriter.println("<a href=\"" + Bundle.sessionFile() + "\">Cytoscape Session File</a><br/>");
				htmlWriter.println("<table border=\"0\" cellspacing=\"10\" cellpadding=\"0\">");
				int i = 0;
				for (String networkID : networkIDs)
				{
					if (needToHalt)
						return;

					String networkTitle = Cytoscape.getNetwork(networkID).getTitle();

					if ((i % settings.numNetworksPerRow) == 0)
						htmlWriter.println("<tr valign=bottom>");
					htmlWriter.println("<td align=center valign=bottom>");

					boolean hasImageFile = bundle.hasEntry(Bundle.imageFile(networkTitle, settings.imageFormat));
					boolean hasLegendFile = bundle.hasEntry(Bundle.legendFile(networkTitle, settings.imageFormat));

					if (hasImageFile)
						htmlWriter.print("<a href=\"" + Bundle.imageFile(networkTitle, settings.imageFormat) + "\">");
					htmlWriter.print("<img border=0 src=\"" + Bundle.thumbnailFile(networkTitle, settings.imageFormat) + "\">");
					if (hasImageFile)
						htmlWriter.print("</a>");

					htmlWriter.print("<font size=-1>");
					htmlWriter.print("<br>" + networkTitle + " <br/>(");
					htmlWriter.print("<a href=\"" + Bundle.sifFile(networkTitle) + "\">sif</a>");
					
					if (hasImageFile)
						htmlWriter.print(" | <a href=\"" + Bundle.imageFile(networkTitle, settings.imageFormat) + "\">image</a>");
					if (hasLegendFile)
						htmlWriter.print(" | <a href=\"" + Bundle.legendFile(networkTitle, settings.imageFormat) + "\">legend</a>");
					
					htmlWriter.print(")</font>");
					htmlWriter.println("</td>");
					if ((i + 1) % settings.numNetworksPerRow == 0)
						htmlWriter.println("</tr>");
					i++;
				}
				if ((i + 1) % settings.numNetworksPerRow != 0)
					htmlWriter.println("</tr>");
				htmlWriter.println("</table>");
				htmlWriter.println("</body>");
				htmlWriter.println("</html>");
				bundle.closeEntry();
			} // end generateHTML
		}; // end new JTask

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
}
